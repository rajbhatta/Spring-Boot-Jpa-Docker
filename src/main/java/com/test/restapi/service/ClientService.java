package com.test.restapi.service;

import com.test.restapi.exception.ClientException;
import com.test.restapi.modal.Client;
import com.test.restapi.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    ClientRepository clientRepository;

    public List<Client> get(){
        List<Client> clientsList = new ArrayList<Client>();
        clientRepository.findAll().forEach(client -> clientsList.add(client));
        return clientsList;
    }

    public Client getClientById(int clientId) throws ClientException {
        Optional<Client> clientOptional= clientRepository.findById(clientId);
        if (clientOptional.isPresent()){
            return clientOptional.get();
        }
        throw new ClientException("UNABLE TO GET CLIENT BY ["+clientId+"]");
    }

}
