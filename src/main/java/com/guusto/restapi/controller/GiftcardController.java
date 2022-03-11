package com.guusto.restapi.controller;

import com.guusto.restapi.modal.Gift;
import com.guusto.restapi.modal.Purchase;
import com.guusto.restapi.service.GiftPurchaseValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:8085")
@RestController
@RequestMapping("/api/v1/guusto-service")
public class GiftcardController {

    @Autowired
    GiftPurchaseValidationService giftPurchaseValidationService;

    @PostMapping("/buy-gift")
    public ResponseEntity<Gift> buyGift(@RequestBody Gift buygift) {
        /*
            case 1: when balance is greater than quantity*amount
            case 2: when balance is greater than amount
         */
        /*
        if (giftPurchaseValidationService.checkBalance(buygift.) && giftPurchaseValidationService.checkBalanceWithQuantity(buygift)) {
            return new ResponseEntity<>(buygift, HttpStatus.CREATED);
        }*/
        return null;
    }

}
