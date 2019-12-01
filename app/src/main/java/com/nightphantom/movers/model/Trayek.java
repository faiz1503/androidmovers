package com.nightphantom.movers.model;

public class Trayek {
    private String name;
    private String kode_bus;

    public Trayek() {
    }

    public Trayek(String name, String kode_bus) {
        this.name = name;
        this.kode_bus = kode_bus;
    }

    public String getName() {
        return name;
    }

    public String getKode_bus() {
        return kode_bus;
    }
}
