package com.guusto.restapi.repository;

import com.guusto.restapi.modal.ClientTranaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientTransactionRepository extends JpaRepository<ClientTranaction, Integer> {
}
