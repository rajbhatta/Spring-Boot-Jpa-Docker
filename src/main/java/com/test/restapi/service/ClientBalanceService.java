package com.test.restapi.service;

import com.test.restapi.exception.ClientBalanceException;
import com.test.restapi.modal.ClientBalance;
import com.test.restapi.repository.ClientBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClientBalanceService {

    @Autowired
    ClientBalanceRepository clientBalanceRepository;

    public List<ClientBalance> get(){
        List<ClientBalance> clientBalanceList = new ArrayList<ClientBalance>();
        clientBalanceRepository.findAll().forEach(clientBalance -> clientBalanceList.add(clientBalance));
        return clientBalanceList;
    }

    public ClientBalance getBalanceById(int clientId) throws ClientBalanceException {
        Optional<ClientBalance> clientOptional= clientBalanceRepository.findById(clientId);
        if (clientOptional.isPresent()){
            return clientOptional.get();
        }
       throw new ClientBalanceException("UNABLE TO GET BALANCE ID for ["+clientId+"]");
    }

}
