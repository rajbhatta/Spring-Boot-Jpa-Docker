package com.guusto.restapi.controller;

import com.guusto.restapi.modal.Client;
import com.guusto.restapi.modal.ClientBalance;
import com.guusto.restapi.repository.ClientRepository;
import com.guusto.restapi.service.ClientBalanceService;
import com.guusto.restapi.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/*
@CrossOrigin(origins = "http://localhost:8085")
@RestController
@RequestMapping("/api/v1/guusto-service")
public class ClientBalanceController {

    @Autowired
    ClientBalanceService clientBalanceService;

    @GetMapping("/balance")
    public ResponseEntity<List<ClientBalance>> getBalance(){
        if(clientBalanceService.get() == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(clientBalanceService.get(), HttpStatus.OK);
    }

    @GetMapping("/balance/{id}")
    public ResponseEntity<ClientBalance> getBalanceClientById(@PathVariable("id") int id) {
        ClientBalance clientBalance=clientBalanceService.getBalanceById(id);
        if (clientBalance ==null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(clientBalance, HttpStatus.OK);
    }
}
*/