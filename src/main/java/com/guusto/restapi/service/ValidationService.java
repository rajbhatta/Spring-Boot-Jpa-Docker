package com.guusto.restapi.service;

public interface ValidationService<T> {
    public boolean checkBalance(T t);
    public boolean checkBalanceWithQuantity(T t);
}
