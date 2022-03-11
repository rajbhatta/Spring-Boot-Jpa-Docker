package com.guusto.restapi.modal;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class Gift implements Serializable {

    @JsonProperty(value="client_id")
    private int clientId;

    @JsonProperty(value="purchase")
    private List<Purchase> totalPurchase;

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public List<Purchase> getTotalPurchase() {
        return totalPurchase;
    }

    public void setTotalPurchase(List<Purchase> totalPurchase) {
        this.totalPurchase = totalPurchase;
    }
}
