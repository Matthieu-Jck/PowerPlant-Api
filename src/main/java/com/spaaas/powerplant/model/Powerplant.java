package com.spaaas.powerplant.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class Powerplant {

    @NotBlank
    private String name;

    @NotNull
    private PowerplantType type;

    @PositiveOrZero
    private double efficiency;

    @PositiveOrZero
    private double pmin;

    @PositiveOrZero
    private double pmax;

    private double effectivePmax;
    private double costPerMwh;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public PowerplantType getType() { return type; }
    public void setType(PowerplantType type) { this.type = type; }

    public double getEfficiency() { return efficiency; }
    public void setEfficiency(double efficiency) { this.efficiency = efficiency; }

    public double getPmin() { return pmin; }
    public void setPmin(double pmin) { this.pmin = pmin; }

    public double getPmax() { return pmax; }
    public void setPmax(double pmax) { this.pmax = pmax; }

    public double getEffectivePmax() { return effectivePmax; }
    public void setEffectivePmax(double effectivePmax) { this.effectivePmax = effectivePmax; }

    public double getCostPerMwh() { return costPerMwh; }
    public void setCostPerMwh(double costPerMwh) { this.costPerMwh = costPerMwh; }

    @Override
    public String toString() {
        return String.format("Powerplant{name='%s', type=%s, pmin=%.1f, pmax=%.1f, effectivePmax=%.1f, cost=%.4f}",
                name, type, pmin, pmax, effectivePmax, costPerMwh);
    }
}
