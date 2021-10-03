package com.vosiievska.dbscanclustering.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class VehicleFunction {

    private static double a = 0.31;
    private static double b = 0.31;
    private static double c = 0.31;
    private static double d = 0.07;

    private double relativeAverageSpeed;
    private double neighborDegree;
    private double linkQuality;
    private double bestAgentLocation;
    private double fitFactor;

    public VehicleFunction(double relativeAverageSpeed, double neighborDegree, double linkQuality,
        double bestAgentLocation) {
        this.relativeAverageSpeed = relativeAverageSpeed;
        this.neighborDegree = neighborDegree;
        this.linkQuality = linkQuality;
        this.bestAgentLocation = bestAgentLocation;
        this.fitFactor = a * Math.pow(relativeAverageSpeed, 2) + b * Math.pow(1 - neighborDegree, 2)
            + c * Math.pow(1 - linkQuality, 2) + d * Math.pow(bestAgentLocation, 2);

        log.info(a + " * (" + Math.pow(relativeAverageSpeed, 2) + ") + "
            + b + " * (" + Math.pow(1 - neighborDegree, 2) + ") + "
            + c + " * (" + Math.pow(1 - linkQuality, 2) + ") + "
            + d + " * (" + Math.pow(bestAgentLocation, 2) + ") = "
            + a * Math.pow(relativeAverageSpeed, 2) + " + "
            + b * Math.pow(1 - neighborDegree, 2) + " + "
            + c * Math.pow(1 - linkQuality, 2) + " + "
            + d * Math.pow(bestAgentLocation, 2));
    }
}
