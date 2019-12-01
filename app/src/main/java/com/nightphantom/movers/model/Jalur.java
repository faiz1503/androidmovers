package com.nightphantom.movers.model;

public class Jalur {
    private String namaTrayek;
    private String asal;
    private String tujuan;
    private double jam;

    public Jalur() {
    }

    public Jalur(String namaTrayek, String asal, String tujuan, double jam) {
        this.namaTrayek = namaTrayek;
        this.asal = asal;
        this.tujuan = tujuan;
        this.jam = jam;
    }

    public String getNamaTrayek() {
        return namaTrayek;
    }

    public String getAsal() {
        return asal;
    }

    public String getTujuan() {
        return tujuan;
    }

    public double getJam() {
        return jam;
    }
}
