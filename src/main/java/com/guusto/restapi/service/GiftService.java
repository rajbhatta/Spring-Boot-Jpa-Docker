package com.guusto.restapi.service;

public interface GiftService<T,U> {
   public void processGiftTransaction(T t, U u);
}
