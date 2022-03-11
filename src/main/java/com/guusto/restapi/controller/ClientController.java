package com.guusto.restapi.controller;

import com.guusto.restapi.exception.ClientException;
import com.guusto.restapi.modal.Client;
import com.guusto.restapi.repository.ClientRepository;
import com.guusto.restapi.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api/v1/guusto-service")
public class ClientController {

    @Autowired
    ClientService clientService;

    @GetMapping("/clients")
    public ResponseEntity<List<Client>> getClients(){
        if(clientService.get() == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(clientService.get(), HttpStatus.OK);
    }

    @GetMapping("/clients/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable("id") int id) {
        try {
            Client client = clientService.getClientById(id);
            return new ResponseEntity<>(client, HttpStatus.OK);
        } catch (ClientException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
