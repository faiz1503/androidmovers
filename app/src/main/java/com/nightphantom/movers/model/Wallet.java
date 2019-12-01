package com.nightphantom.movers.model;

public class Wallet {

    String user_id;
    Number amount;


    private Wallet(){

    }

    public Wallet(String user_id, Number amount) {
        this.user_id = user_id;
        this.amount = amount;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Number getAmount() {
        return amount;
    }

    public void setAmount(Number amount) {
        this.amount = amount;
    }
}
