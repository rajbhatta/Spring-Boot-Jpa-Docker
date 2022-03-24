package com.test.restapi.service;

import com.test.restapi.exception.ClientBalanceException;

/**
 *
 * @param <T> generic object
 *  Validate balance and balance with quantity
 */
public interface ValidationService<T,U> {
    public boolean checkBalance(T t, U u) throws ClientBalanceException;
    public boolean checkBalanceWithQuantity(T t, U u) throws ClientBalanceException;
    public boolean checkBalanceWithAmount(double totalBalance, double purchaseAmount);
}
