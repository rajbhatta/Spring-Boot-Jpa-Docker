package com.test.restapi.service;

import com.test.restapi.exception.GiftCardException;

public interface GiftService<T> {
   public void processGiftTransaction(T t) throws GiftCardException;
}
