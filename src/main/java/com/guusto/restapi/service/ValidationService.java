package com.guusto.restapi.service;

/**
 *
 * @param <T> generic object
 *  Validate balance and balance with quantity
 */
public interface ValidationService<T,U> {
    public boolean checkBalance(T t, U u);
    public boolean checkBalanceWithQuantity(T t, U u);
}
