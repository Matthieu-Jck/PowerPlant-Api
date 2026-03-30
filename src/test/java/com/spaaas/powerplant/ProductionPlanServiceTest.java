package com.spaaas.powerplant;

import com.spaaas.powerplant.exception.ProductionPlanException;
import com.spaaas.powerplant.model.*;
import com.spaaas.powerplant.service.ProductionPlanService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductionPlanServiceTest {

    private final ProductionPlanService service = new ProductionPlanService();

    // ---------- Helpers ----------

    private Fuels buildFuels(double wind) {
        Fuels fuels = new Fuels();
        fuels.setGas(13.4);
        fuels.setKerosine(50.8);
        fuels.setCo2(20.0);
        fuels.setWind(wind);
        return fuels;
    }

    private Powerplant plant(String name, PowerplantType type,
                             double efficiency, double pmin, double pmax) {
        Powerplant p = new Powerplant();
        p.setName(name);
        p.setType(type);
        p.setEfficiency(efficiency);
        p.setPmin(pmin);
        p.setPmax(pmax);
        return p;
    }

    private ProductionPlanRequest request(double load, double wind, List<Powerplant> plants) {
        ProductionPlanRequest req = new ProductionPlanRequest();
        req.setLoad(load);
        req.setFuels(buildFuels(wind));
        req.setPowerplants(plants);
        return req;
    }

    // ---------- Tests ----------

    @Test
    void shouldMatchLoadExactly() {
        ProductionPlanRequest req = request(
                200,
                0,
                List.of(
                        plant("gas1", PowerplantType.GASFIRED, 0.5, 50, 200)
                )
        );

        List<PowerplantResult> result = service.computeProductionPlan(req);

        double total = result.stream().mapToDouble(PowerplantResult::getP).sum();

        assertEquals(200.0, total, 0.1);
    }

    @Test
    void shouldUseWindFirst() {
        ProductionPlanRequest req = request(
                100,
                100, // full wind
                List.of(
                        plant("gas1", PowerplantType.GASFIRED, 0.5, 50, 200),
                        plant("wind1", PowerplantType.WINDTURBINE, 1.0, 0, 150)
                )
        );

        List<PowerplantResult> result = service.computeProductionPlan(req);

        PowerplantResult wind = result.stream()
                .filter(p -> p.getName().equals("wind1"))
                .findFirst()
                .orElseThrow();

        assertEquals(100.0, wind.getP(), 0.1);
    }

    @Test
    void shouldRespectPmin() {
        ProductionPlanRequest req = request(
                60,
                0,
                List.of(
                        plant("gas1", PowerplantType.GASFIRED, 0.5, 50, 100)
                )
        );

        List<PowerplantResult> result = service.computeProductionPlan(req);

        double output = result.get(0).getP();

        assertTrue(output == 0 || output >= 50);
    }

    @Test
    void shouldNotExceedPmax() {
        ProductionPlanRequest req = request(
                500,
                0,
                List.of(
                        plant("gas1", PowerplantType.GASFIRED, 0.5, 50, 100),
                        plant("gas2", PowerplantType.GASFIRED, 0.5, 50, 200)
                )
        );

        List<PowerplantResult> result = service.computeProductionPlan(req);

        for (PowerplantResult r : result) {
            Powerplant original = req.getPowerplants().stream()
                    .filter(p -> p.getName().equals(r.getName()))
                    .findFirst()
                    .orElseThrow();

            assertTrue(r.getP() <= original.getPmax() + 0.1);
        }
    }

    @Test
    void shouldThrowIfLoadTooHigh() {
        ProductionPlanRequest req = request(
                1000,
                0,
                List.of(
                        plant("small", PowerplantType.GASFIRED, 0.5, 50, 100)
                )
        );

        assertThrows(ProductionPlanException.class,
                () -> service.computeProductionPlan(req));
    }

    @Test
    void shouldHandleZeroWind() {
        ProductionPlanRequest req = request(
                200,
                0,
                List.of(
                        plant("wind1", PowerplantType.WINDTURBINE, 1.0, 0, 150),
                        plant("gas1", PowerplantType.GASFIRED, 0.5, 50, 200)
                )
        );

        List<PowerplantResult> result = service.computeProductionPlan(req);

        PowerplantResult wind = result.stream()
                .filter(p -> p.getName().equals("wind1"))
                .findFirst()
                .orElseThrow();

        assertEquals(0.0, wind.getP(), 0.1);
    }
}
