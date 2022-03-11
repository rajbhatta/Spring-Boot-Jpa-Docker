package com.guusto.restapi.service;

import com.guusto.restapi.modal.Client;
import com.guusto.restapi.modal.ClientBalance;
import com.guusto.restapi.repository.ClientBalanceRepository;
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

    public ClientBalance getBalanceById(int clientId){
        Optional<ClientBalance> clientOptional= clientBalanceRepository.findById(clientId);
        if (clientOptional.isPresent()){
            return clientOptional.get();
        }
        return null;
    }

}
