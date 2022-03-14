package com.guusto.restapi.service;

import com.guusto.restapi.exception.ClientBalanceException;
import com.guusto.restapi.exception.ClientException;
import com.guusto.restapi.exception.GiftCardException;
import com.guusto.restapi.modal.*;
import com.guusto.restapi.repository.ClientTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class ClientGiftTransactionServiceV2 implements GiftService<Gift> {

    private AtomicInteger atomicInteger;

    public ClientGiftTransactionServiceV2() {
        atomicInteger = new AtomicInteger(1);
    }

    @Autowired
    GiftPurchaseValidationService giftPurchaseValidationService;

    @Autowired
    ClientBalanceService clientBalanceService;


    @Autowired
    ClientTransactionRepository clientTransactionRepository;

    @Autowired
    ClientService clientService;

    private static int generatedId = 0;

    @Override
    public void processGiftTransaction(Gift gift) throws GiftCardException {
        try {
            int transactionCounter = atomicInteger.incrementAndGet();
            HashMap<String, ClientTranaction> linkedHashMap = new LinkedHashMap<>();
            Client client = clientService.getClientById(gift.getClientId());

            double clientBalance = getLatestClientBalance(client);

            gift.getTotalPurchase().stream().forEach((purchase) -> {
                try {
                    processPurchase(purchase, gift, transactionCounter, linkedHashMap, clientBalance, client);
                } catch (ClientBalanceException | ClientException e) {
                    e.printStackTrace();
                }
            });

            //write map to database as a batch
            //writeTransactionLedgerBatch(linkedHashMap);

        } catch (Exception ex) {
            throw new GiftCardException("UNABLE TO PROCESS GIFT CARD FOR [" + gift.getClientId() + "]");
        }


    }

    private void processPurchase(Purchase purchase,
                                 Gift gift,
                                 int transactionCounter,
                                 HashMap<String, ClientTranaction> mapClient,
                                 double clientBalance,
                                 Client client) throws ClientBalanceException, ClientException {

        ClientTranaction processedClientTransaction = getLastRecordFromHashMap(mapClient);
        if (processedClientTransaction == null) {
            //Validate balance
            if (giftPurchaseValidationService.checkBalanceWithAmount(clientBalance, purchase.getAmount() * purchase.getQuantity())) {
                double remindBalance = clientBalance - purchase.getQuantity() * purchase.getAmount();
                ClientTranaction clientTransaction = new ClientTranaction(purchase.getQuantity(), purchase.getAmount(), remindBalance, purchase.getQuantity(), client);
                mapClient.put("transaction"+transactionCounter, new ClientTranaction(purchase.getQuantity(), purchase.getAmount(), remindBalance, purchase.getQuantity(), client));
                writeTransactionLedger(clientTransaction);
            }
        } else {
            double remindBalance = processedClientTransaction.getRemindBalance() - purchase.getQuantity() * purchase.getAmount();
            //validate balance
            if (giftPurchaseValidationService.checkBalanceWithAmount(remindBalance, purchase.getAmount() * purchase.getQuantity())) {
                ClientTranaction clientTransaction = new ClientTranaction(purchase.getQuantity(), purchase.getAmount(), remindBalance, purchase.getQuantity(), client);
                mapClient.put("transaction"+transactionCounter, new ClientTranaction(purchase.getQuantity(), purchase.getAmount(), remindBalance, purchase.getQuantity(), client));
                writeTransactionLedger(clientTransaction);
            }
        }
    }

    private ClientTranaction getLastRecordFromHashMap(HashMap<String, ClientTranaction> clientTransactionMap) {
        if (clientTransactionMap != null) {
            ClientTranaction clientTranaction = null;
            for (Map.Entry<String, ClientTranaction> entry : clientTransactionMap.entrySet()) {
                clientTranaction = entry.getValue();
            }
            return clientTranaction;
        }
        return null;
    }

    public ClientTranaction getLastTransactionRecordByClient(Client client){
        List<ClientTranaction> clientTransactionsList = clientTransactionRepository.findByClient(client);
        if (clientTransactionsList.size() >=1){
            //Last record contains the latest transaction value
            return clientTransactionsList.get(clientTransactionsList.size()-1);
        }
        return null;
    }

    private Double getLatestClientBalance(Client client) throws ClientBalanceException {

        //If previous transaction is detected then get value from transaction table.
        ClientTranaction clientTransactionRecord = getLastTransactionRecordByClient(client);

        if (clientTransactionRecord == null){
            //If no transaction then return from client balance table
            ClientBalance clientBalance =  clientBalanceService.getBalanceById(client.getId());
            return  clientBalance.getBalance();
        }else {
            return clientTransactionRecord.getRemindBalance();
        }

    }

    private int writeTransactionLedger(ClientTranaction clientTranaction) {
        clientTransactionRepository.save(clientTranaction);
        return clientTranaction.getClientTransactionId();
    }

    private void writeTransactionLedgerBatch(HashMap<String, ClientTranaction> linkedHashMap){
        for (var entry : linkedHashMap.entrySet()) {
            ClientTranaction clientTranaction = entry.getValue();
            clientTransactionRepository.save(clientTranaction);
        }
    }

}
