package com.guusto.restapi.service;

/**
 *
 * @param <T> generic object
 *  Validate balance and balance with quantity
 */
public interface ValidationService<T> {
    public boolean checkBalance(T t);
    public boolean checkBalanceWithQuantity(T t);
}
