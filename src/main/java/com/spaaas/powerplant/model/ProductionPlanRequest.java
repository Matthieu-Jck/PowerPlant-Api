package com.spaaas.powerplant.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public class ProductionPlanRequest {

    @Positive
    private double load;

    @NotNull
    @Valid
    private Fuels fuels;

    @NotEmpty
    @Valid
    private List<Powerplant> powerplants;

    public double getLoad() { return load; }
    public void setLoad(double load) { this.load = load; }

    public Fuels getFuels() { return fuels; }
    public void setFuels(Fuels fuels) { this.fuels = fuels; }

    public List<Powerplant> getPowerplants() { return powerplants; }
    public void setPowerplants(List<Powerplant> powerplants) { this.powerplants = powerplants; }
}
