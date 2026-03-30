package com.spaaas.powerplant.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.PositiveOrZero;

public class Fuels {

    @JsonProperty("gas(euro/MWh)")
    @PositiveOrZero
    private double gas;

    @JsonProperty("kerosine(euro/MWh)")
    @PositiveOrZero
    private double kerosine;

    @JsonProperty("co2(euro/ton)")
    @PositiveOrZero
    private double co2;

    @JsonProperty("wind(%)")
    @PositiveOrZero
    private double wind;

    public double getGas() { return gas; }
    public void setGas(double gas) { this.gas = gas; }

    public double getKerosine() { return kerosine; }
    public void setKerosine(double kerosine) { this.kerosine = kerosine; }

    public double getCo2() { return co2; }
    public void setCo2(double co2) { this.co2 = co2; }

    public double getWind() { return wind; }
    public void setWind(double wind) { this.wind = wind; }
}
