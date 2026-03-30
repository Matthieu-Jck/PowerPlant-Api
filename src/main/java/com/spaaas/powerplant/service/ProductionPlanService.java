package com.spaaas.powerplant.service;

import com.spaaas.powerplant.exception.ProductionPlanException;
import com.spaaas.powerplant.model.*;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Optimized and deterministic production planner. Non-optimal.
 *
 * Strategy:
 * 1. Compute costs (merit order)
 * 2. Use greedy
 * 3. Apply controlled adjustments
 *
 */
@Service
public class ProductionPlanService {

    private static final double CO2_EMISSION_FACTOR = 0.3;
    private static final double PRECISION = 0.1;

    public List<PowerplantResult> computeProductionPlan(ProductionPlanRequest request) {

        double load = request.getLoad();
        List<Plant> plants = enrich(request);

        Map<String, Double> production = new LinkedHashMap<>();
        plants.forEach(p -> production.put(p.name, 0.0));

        // Handle wind first
        for (Plant p : plants) {
            if (p.type == PowerplantType.WINDTURBINE) {
                double prod = Math.min(p.pmax, load);
                prod = round(prod);
                production.put(p.name, prod);
                load -= prod;
            }
        }

        if (load < -PRECISION) {
            throw new ProductionPlanException("Wind exceeds load");
        }

        // Sort dispatchable by cost (merit order)
        List<Plant> dispatchable = plants.stream()
                .filter(p -> p.type != PowerplantType.WINDTURBINE)
                .sorted(Comparator.comparingDouble(p -> p.cost))
                .toList();

        List<Plant> active = new ArrayList<>();

        // Greedy
        for (Plant p : dispatchable) {
            if (load <= 0) break;

            if (p.pmax <= 0) continue;

            double prod = Math.min(p.pmax, load);

            if (prod < p.pmin) continue;

            prod = Math.max(prod, p.pmin);
            prod = round(prod);

            production.put(p.name, prod);
            active.add(p);
            load -= prod;
        }

        // Adjust when too low
        if (load > PRECISION) {
            for (int i = active.size() - 1; i >= 0 && load > PRECISION; i--) {
                Plant p = active.get(i);
                double current = production.get(p.name);
                double headroom = p.pmax - current;

                if (headroom <= 0) continue;

                double add = Math.min(headroom, load);
                add = round(add);

                production.put(p.name, round(current + add));
                load -= add;
            }
        }

        // Adjust when too high
        if (load < -PRECISION) {
            for (int i = active.size() - 1; i >= 0 && load < -PRECISION; i--) {
                Plant p = active.get(i);
                double current = production.get(p.name);
                double reducible = current - p.pmin;

                if (reducible <= 0) continue;

                double reduce = Math.min(reducible, -load);
                reduce = round(reduce);

                production.put(p.name, round(current - reduce));
                load += reduce;
            }
        }

        if (Math.abs(load) > PRECISION) {
            throw new ProductionPlanException("Unable to match load precisely");
        }

        return production.entrySet().stream()
                .map(e -> new PowerplantResult(e.getKey(), round(e.getValue())))
                .toList();
    }

    private List<Plant> enrich(ProductionPlanRequest request) {
        List<Plant> result = new ArrayList<>();

        for (Powerplant p : request.getPowerplants()) {
            double cost = 0;
            double pmax = p.getPmax();

            switch (p.getType()) {
                case WINDTURBINE -> {
                    pmax = p.getPmax() * request.getFuels().getWind() / 100.0;
                }
                case GASFIRED -> {
                    cost = request.getFuels().getGas() / p.getEfficiency()
                            + request.getFuels().getCo2() * CO2_EMISSION_FACTOR;
                }
                case TURBOJET -> {
                    cost = request.getFuels().getKerosine() / p.getEfficiency();
                }
            }

            result.add(new Plant(
                    p.getName(),
                    p.getType(),
                    p.getPmin(),
                    pmax,
                    cost
            ));
        }

        return result;
    }

    private double round(double v) {
        return Math.round(v * 10.0) / 10.0;
    }

    static class Plant {
        String name;
        PowerplantType type;
        double pmin;
        double pmax;
        double cost;

        Plant(String name, PowerplantType type, double pmin, double pmax, double cost) {
            this.name = name;
            this.type = type;
            this.pmin = pmin;
            this.pmax = pmax;
            this.cost = cost;
        }
    }
}