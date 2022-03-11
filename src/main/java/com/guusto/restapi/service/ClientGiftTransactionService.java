package com.guusto.restapi.service;

import com.guusto.restapi.modal.Gift;
import com.guusto.restapi.modal.Purchase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientGiftTransactionService implements GiftService<Gift, Integer>  {

    @Autowired
    GiftPurchaseValidationService giftPurchaseValidationService;

    @Override
    public void processGiftTransaction(Gift gift, Integer clientId) {
        gift.getTotalPurchase().stream().forEach((purchase) -> {
            processPurchase(purchase,clientId);
        });
    }

    private void processPurchase(Purchase purchase, Integer clientId) {
         /*
            case 1: when balance is greater than quantity*amount
            case 2: when balance is greater than amount
         */
        if (giftPurchaseValidationService.checkBalance(purchase, clientId)
                && giftPurchaseValidationService.checkBalanceWithQuantity(purchase, clientId)) {
            //
        }
    }
}
