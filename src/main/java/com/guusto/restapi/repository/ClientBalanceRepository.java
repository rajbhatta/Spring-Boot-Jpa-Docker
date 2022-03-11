package com.guusto.restapi.repository;

import com.guusto.restapi.modal.ClientBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientBalanceRepository extends JpaRepository<ClientBalance, Integer> {
}
