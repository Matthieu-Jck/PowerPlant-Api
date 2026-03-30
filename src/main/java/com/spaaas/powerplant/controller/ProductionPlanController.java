package com.spaaas.powerplant.controller;

import com.spaaas.powerplant.model.PowerplantResult;
import com.spaaas.powerplant.model.ProductionPlanRequest;
import com.spaaas.powerplant.service.ProductionPlanService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productionplan")
public class ProductionPlanController {

    private static final Logger log = LoggerFactory.getLogger(ProductionPlanController.class);

    private final ProductionPlanService productionPlanService;

    public ProductionPlanController(ProductionPlanService productionPlanService) {
        this.productionPlanService = productionPlanService;
    }

    /**
     * POST /productionplan
     *
     * Accepts a payload describing the load, fuel prices and available powerplants,
     * and returns the optimal production plan.
     */
    @PostMapping
    public ResponseEntity<List<PowerplantResult>> computeProductionPlan(
            @Valid @RequestBody ProductionPlanRequest request) {

        log.info("Received production plan request: load={}MWh, plants={}",
                request.getLoad(), request.getPowerplants().size());

        List<PowerplantResult> result = productionPlanService.computeProductionPlan(request);

        log.info("Production plan computed successfully with {} entries", result.size());
        return ResponseEntity.ok(result);
    }
}
