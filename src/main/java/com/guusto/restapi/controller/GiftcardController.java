package com.guusto.restapi.controller;

import com.guusto.restapi.modal.Buygift;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:8085")
@RestController
@RequestMapping("/api/v1/guusto-service")
public class GiftcardController {

    @PostMapping("/buy-gift")
    public ResponseEntity<Buygift> buyGift(@RequestBody Buygift buygift){
        return new ResponseEntity<>(buygift, HttpStatus.CREATED);
    }



}
