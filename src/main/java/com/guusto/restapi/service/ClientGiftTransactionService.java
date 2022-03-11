package com.guusto.restapi.service;

import com.guusto.restapi.modal.*;
import com.guusto.restapi.repository.ClientTransactionRepository;
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

    public int writeTransactionLedger(ClientTranaction clientTranaction) {
        clientTransactionRepository.save(clientTranaction);
        return clientTranaction.getClientTransactionId();
    }

    public ClientTranaction provideLastInsertedClientTransactionRecord(int clientTransactionId) {
        Optional<ClientTranaction> clientTranactionOptional = clientTransactionRepository.findById(clientTransactionId);
        if (clientTranactionOptional.isPresent()) {
            return clientTranactionOptional.get();
        }
        return null;
    }


    @Override
    public void processGiftTransaction(Gift gift) {
        gift.getTotalPurchase().stream().forEach((purchase) -> {
            processPurchase(purchase, gift);
        });
    }

    private void processPurchase(Purchase purchase, Gift gift) {

        ClientBalance clientBalance = clientBalanceService.getBalanceById(gift.getClientId());
        Client client = clientService.getClientById(gift.getClientId());

        /**
         * First round generatedId will be 0. Thus, client balance is checked with amount*quantity.
         */
        if (generatedId == 0) {
            if (checkBalanceWithQuantity(clientBalance.getBalance(), purchase.getAmount() * purchase.getQuantity())) {
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
            if (checkBalanceWithQuantity(remainingClientBalance, purchase.getAmount() * purchase.getQuantity())) {
                ClientTranaction clientTranaction = new ClientTranaction(purchase.getQuantity(), purchase.getAmount(), newDueAmount, purchase.getQuantity(), client);
                generatedId = writeTransactionLedger(clientTranaction);
            }
        }

    }

    public boolean checkBalanceWithQuantity(double totalBalance, double purchaseAmount) {
        if (Double.compare(totalBalance, purchaseAmount) == 0) {
            return false;
        } else if (Double.compare(totalBalance, purchaseAmount) < 0) {
            return false;
        } else {
            return true;
        }
    }
}
