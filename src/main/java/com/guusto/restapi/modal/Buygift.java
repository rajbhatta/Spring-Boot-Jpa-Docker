package com.guusto.restapi.modal;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Buygift implements Serializable {

    @JsonProperty(value="client_id")
    private int clientId;
    private double amount;
    private int quantity;

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
