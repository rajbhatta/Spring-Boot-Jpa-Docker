package com.guusto.restapi.modal;

import java.io.Serializable;

public class Purchase implements Serializable {
    private double amount;
    private int quantity;

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
