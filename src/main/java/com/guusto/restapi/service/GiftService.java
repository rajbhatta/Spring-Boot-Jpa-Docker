package com.guusto.restapi.service;

import com.guusto.restapi.exception.GiftCardException;

public interface GiftService<T> {
   public void processGiftTransaction(T t) throws GiftCardException;
}
