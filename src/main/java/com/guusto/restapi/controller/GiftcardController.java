package com.guusto.restapi.controller;

import com.guusto.restapi.modal.Gift;
import com.guusto.restapi.modal.Purchase;
import com.guusto.restapi.service.ClientGiftTransactionService;
import com.guusto.restapi.service.GiftPurchaseValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api/v1/guusto-service")
public class GiftcardController {

    @Autowired
    GiftPurchaseValidationService giftPurchaseValidationService;

    @Autowired
    ClientGiftTransactionService clientGiftTransactionService;

    @PostMapping("/buy-gift")
    public ResponseEntity<Gift> buyGift(@RequestBody Gift gift) {
        try {
            clientGiftTransactionService.processGiftTransaction(gift);
            return new ResponseEntity<>(gift, HttpStatus.OK);
        }catch (Exception ee){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

    }

}
