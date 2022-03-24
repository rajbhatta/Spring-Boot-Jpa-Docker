package com.test.restapi.service;

import com.test.restapi.exception.ClientBalanceException;
import com.test.restapi.modal.ClientBalance;
import com.test.restapi.modal.Purchase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GiftPurchaseValidationService implements ValidationService<Purchase, Integer> {

    @Autowired
    ClientBalanceService clientBalanceService;

    @Override
    public boolean checkBalance(Purchase purchase, Integer clientId) throws ClientBalanceException {

        ClientBalance clientBalance = clientBalanceService.getBalanceById(clientId);
        if (Double.compare(clientBalance.getBalance(), purchase.getAmount()) == 0) {
            return false;
        } else if (Double.compare(clientBalance.getBalance(), purchase.getAmount()) < 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean checkBalanceWithQuantity(Purchase purchase, Integer clientId) throws ClientBalanceException {

        ClientBalance clientBalance = clientBalanceService.getBalanceById(clientId);
        double totalCost = purchase.getAmount() * purchase.getQuantity();

        if (Double.compare(clientBalance.getBalance(), totalCost) == 0) {
            return false;
        } else if (Double.compare(clientBalance.getBalance(), totalCost) < 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean checkBalanceWithAmount(double totalBalance, double purchaseAmount) {
        if (Double.compare(totalBalance, purchaseAmount) == 0) {
            return false;
        } else if (Double.compare(totalBalance, purchaseAmount) < 0) {
            return false;
        } else {
            return true;
        }
    }

}
