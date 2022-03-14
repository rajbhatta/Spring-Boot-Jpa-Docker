package com.guusto.restapi.repository;

import com.guusto.restapi.modal.Client;
import com.guusto.restapi.modal.ClientTranaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientTransactionRepository extends JpaRepository<ClientTranaction, Integer> {

    List<ClientTranaction> findByClient(Client client);

}
