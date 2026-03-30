package com.spaaas.powerplant.model;

public class PowerplantResult {

    private String name;
    private double p;

    public PowerplantResult(String name, double p) {
        this.name = name;
        this.p = p;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getP() { return p; }
    public void setP(double p) { this.p = p; }
}
