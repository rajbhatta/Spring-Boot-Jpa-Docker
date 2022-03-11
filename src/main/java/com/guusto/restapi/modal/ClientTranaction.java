package com.guusto.restapi.modal;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "tbl_client_transaction")
public class ClientTranaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int clientTransactionId;

    @Column(name = "quantity")
    private int quantity;

    @Column(name="amount")
    private double amount;

    @Column(name="remindBalance")
    private double remindBalance;

    @Column(name="gift")
    private int gift;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id", nullable = false)
    private Client client;

    public ClientTranaction() {
    }

    public ClientTranaction(int quantity, double amount, double remindBalance, int gift, Client client) {
        this.quantity = quantity;
        this.amount = amount;
        this.remindBalance = remindBalance;
        this.gift = gift;
        this.client = client;
    }

    public int getClientTransactionId() {
        return clientTransactionId;
    }

    public void setClientTransactionId(int clientTransactionId) {
        this.clientTransactionId = clientTransactionId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getRemindBalance() {
        return remindBalance;
    }

    public void setRemindBalance(double remindBalance) {
        this.remindBalance = remindBalance;
    }

    public int getGift() {
        return gift;
    }

    public void setGift(int gift) {
        this.gift = gift;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
