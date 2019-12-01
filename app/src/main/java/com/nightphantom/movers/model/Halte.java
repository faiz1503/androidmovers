package com.nightphantom.movers.model;

import com.google.firebase.firestore.GeoPoint;

public class Halte {
    private String name;
    private String address;
    private GeoPoint location;

    public Halte() {
    }

    public Halte(String name, String address, GeoPoint location) {
        this.name = name;
        this.address = address;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public GeoPoint getLocation() {
        return location;
    }
}
