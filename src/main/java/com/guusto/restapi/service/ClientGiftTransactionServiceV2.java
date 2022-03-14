package com.guusto.restapi.service;

import com.guusto.restapi.exception.ClientBalanceException;
import com.guusto.restapi.exception.ClientException;
import com.guusto.restapi.exception.GiftCardException;
import com.guusto.restapi.modal.*;
import com.guusto.restapi.repository.ClientTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
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

            ClientBalance clientBalance = clientBalanceService.getBalanceById(gift.getClientId());

            gift.getTotalPurchase().stream().forEach((purchase) -> {
                try {
                    processPurchase(purchase, gift, transactionCounter, linkedHashMap, clientBalance, client);
                } catch (ClientBalanceException | ClientException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception ex) {
            throw new GiftCardException("UNABLE TO PROCESS GIFT CARD FOR [" + gift.getClientId() + "]");
        }

    }

    private void processPurchase(Purchase purchase,
                                 Gift gift,
                                 int transactionCounter,
                                 HashMap<String, ClientTranaction> mapClient,
                                 ClientBalance clientBalance,
                                 Client client) throws ClientBalanceException, ClientException {

        ClientTranaction processedClientTransaction = getLastRecordFromHashMap(mapClient);
        if (processedClientTransaction == null) {
            //Validate balance
            if (giftPurchaseValidationService.checkBalanceWithAmount(clientBalance.getBalance(), purchase.getAmount() * purchase.getQuantity())) {
                double remindBalance = clientBalance.getBalance() - purchase.getQuantity() * purchase.getAmount();
                ClientTranaction clientTranaction = new ClientTranaction(purchase.getQuantity(), purchase.getAmount(), remindBalance, purchase.getQuantity(), client);
                writeTransactionLedger(clientTranaction);
                mapClient.put("tranasction"+transactionCounter, new ClientTranaction(purchase.getQuantity(), purchase.getAmount(), remindBalance, purchase.getQuantity(), client));

            }
        } else {
            double remindBalance = processedClientTransaction.getRemindBalance() - purchase.getQuantity() * purchase.getAmount();
            //validate balance
            if (giftPurchaseValidationService.checkBalanceWithAmount(remindBalance, purchase.getAmount() * purchase.getQuantity())) {
                ClientTranaction clientTranaction = new ClientTranaction(purchase.getQuantity(), purchase.getAmount(), remindBalance, purchase.getQuantity(), client);
                writeTransactionLedger(clientTranaction);
                mapClient.put("tranasction"+transactionCounter, new ClientTranaction(purchase.getQuantity(), purchase.getAmount(), remindBalance, purchase.getQuantity(), client));

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

    public ClientTranaction getLastTransactionRecordById(int clientId){
      return null;
    }

    private int writeTransactionLedger(ClientTranaction clientTranaction) {
        clientTransactionRepository.save(clientTranaction);
        return clientTranaction.getClientTransactionId();
    }

}
