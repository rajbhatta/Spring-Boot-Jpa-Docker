package com.guusto.restapi.service;

import com.guusto.restapi.modal.Client;
import com.guusto.restapi.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClientService {

    @Autowired
    ClientRepository clientRepository;

    public List<Client> get(){
        List<Client> clientsList = new ArrayList<Client>();
        clientRepository.findAll().forEach(client -> clientsList.add(client));
        return clientsList;
    }

}
