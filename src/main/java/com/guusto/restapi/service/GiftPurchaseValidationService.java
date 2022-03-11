package com.guusto.restapi.service;

import com.guusto.restapi.modal.Buygift;
import com.guusto.restapi.modal.ClientBalance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GiftPurchaseValidationService implements ValidationService<Buygift> {

    @Autowired
    ClientBalanceService clientBalanceService;

    @Override
    public boolean checkBalance(Buygift buygift) {
        ClientBalance clientBalance = clientBalanceService.getBalanceById(buygift.getClientId());
        if (Double.compare(clientBalance.getBalance(), buygift.getAmount()) == 0) {
            return false;
        } else if (Double.compare(clientBalance.getBalance(), buygift.getAmount()) < 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean checkBalanceWithQuantity(Buygift buygift) {
        ClientBalance clientBalance = clientBalanceService.getBalanceById(buygift.getClientId());
        double totalCost = buygift.getAmount() * buygift.getQuantity();

        if (Double.compare(clientBalance.getBalance(), totalCost) == 0) {
            return false;
        } else if (Double.compare(clientBalance.getBalance(), totalCost) < 0) {
            return false;
        } else {
            return true;
        }
    }


}
