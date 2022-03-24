package com.test.restapi.repository;

import com.test.restapi.modal.Client;
import com.test.restapi.modal.ClientTranaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientTransactionRepository extends JpaRepository<ClientTranaction, Integer> {

    List<ClientTranaction> findByClient(Client client);

}
