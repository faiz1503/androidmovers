package com.nightphantom.movers.model;

public class History {
    private int amount;
    private String date,type,user_id;

    public History() {
    }

    public History(int amount, String date, String type, String user_id) {
        this.amount = amount;
        this.date = date;
        this.type = type;
        this.user_id = user_id;
    }

    public int getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getUser_id() {
        return user_id;
    }
}
