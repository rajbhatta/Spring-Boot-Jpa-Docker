package com.guusto.restapi.modal;

import javax.persistence.*;

@Entity
@Table(name = "tbl_client_balance")
public class ClientBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long client_balance_id;

    @Column(name = "balance")
    private double balance;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id", nullable = false)
    private Client client;

    public long getClient_balance_id() {
        return client_balance_id;
    }

    public void setClient_balance_id(long client_balance_id) {
        this.client_balance_id = client_balance_id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
