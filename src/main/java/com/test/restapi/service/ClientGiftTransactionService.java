package com.test.restapi.service;

import com.test.restapi.exception.ClientBalanceException;
import com.test.restapi.exception.ClientException;
import com.test.restapi.exception.GiftCardException;
import com.guusto.restapi.modal.*;
import com.test.restapi.repository.ClientTransactionRepository;
import com.test.restapi.modal.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientGiftTransactionService implements GiftService<Gift> {

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
        try{
            gift.getTotalPurchase().stream().forEach((purchase) -> {
                try {
                    processPurchase(purchase, gift);
                } catch (ClientBalanceException | ClientException e) {
                    e.printStackTrace();
                }
            });
        }catch (Exception ex){
            throw new GiftCardException("UNABLE TO PROCESS GIFT CARD FOR ["+gift.getClientId()+"]");
        }

    }

    private void processPurchase(Purchase purchase, Gift gift) throws ClientBalanceException, ClientException {

        ClientBalance clientBalance = clientBalanceService.getBalanceById(gift.getClientId());
        Client client = clientService.getClientById(gift.getClientId());

        if (clientBalance != null) {
            /**
             * First round generatedId will be 0. Thus, client balance is checked with amount*quantity.
             */
            if (generatedId == 0) {
                if (giftPurchaseValidationService.checkBalanceWithAmount(clientBalance.getBalance(), purchase.getAmount() * purchase.getQuantity())) {
                    double dueAmount = (clientBalance.getBalance() - purchase.getAmount() * purchase.getQuantity());
                    ClientTranaction clientTranaction = new ClientTranaction(purchase.getQuantity(), purchase.getAmount(), dueAmount, purchase.getQuantity(), client);
                    generatedId = writeTransactionLedger(clientTranaction);
                }
            } else {
                /**
                 *
                 */
                double remainingClientBalance = provideLastInsertedClientTransactionRecord(generatedId).getRemindBalance();
                double newDueAmount = (remainingClientBalance - purchase.getAmount() * purchase.getQuantity());
                if (giftPurchaseValidationService.checkBalanceWithAmount(remainingClientBalance, purchase.getAmount() * purchase.getQuantity())) {
                    ClientTranaction clientTranaction = new ClientTranaction(purchase.getQuantity(), purchase.getAmount(), newDueAmount, purchase.getQuantity(), client);
                    generatedId = writeTransactionLedger(clientTranaction);
                }
            }
        }
    }

    private ClientTranaction provideLastInsertedClientTransactionRecord(int clientTransactionId) {
        Optional<ClientTranaction> clientTranactionOptional = clientTransactionRepository.findById(clientTransactionId);
        if (clientTranactionOptional.isPresent()) {
            return clientTranactionOptional.get();
        }
        return null;
    }

    private int writeTransactionLedger(ClientTranaction clientTranaction) {
        clientTransactionRepository.save(clientTranaction);
        return clientTranaction.getClientTransactionId();
    }

}
