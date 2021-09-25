package com.vosiievska.dbscanclustering.service.impl;

import com.vosiievska.dbscanclustering.entity.Cluster;
import com.vosiievska.dbscanclustering.entity.Vehicle;
import com.vosiievska.dbscanclustering.service.ClusterService;
import com.vosiievska.dbscanclustering.service.VehicleService;
import java.util.OptionalDouble;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class ClusterServiceImpl implements ClusterService {

    private VehicleService vehicleService = new VehicleServiceImpl();

    @Override
    public void calculateFitFactor(Cluster<Vehicle> cluster) {
        log.info("Cluster {}. Each Vehicle calculates it's Fit Factor.", cluster.getId());

        cluster.init();
        String clusterInitResult = String
            .format("Cluster %s -> %s%s%s%s", cluster.getId(), StringUtils.center("mean = " + cluster.getMean(), 30),
                StringUtils.center("standard deviation = " + cluster.getStandardDeviation() + ";", 30),
                StringUtils.center("max received message power = " + cluster.getMaxReceivedMessagePower() + ";", 30),
                StringUtils.center("min received message power = " + cluster.getMinReceivedMessagePower() + ";", 30));

        log.info(clusterInitResult);

        cluster.getVehicles().forEach(v -> vehicleService.getFitFactor(v, cluster));
    }

    private double getMean(Set<Vehicle> neighbors) {
        log.debug("Calculate cluster mean.");
        OptionalDouble mean = neighbors.stream().mapToDouble(Vehicle::getSpeed).average();
        if (mean.isPresent()) {
            return mean.getAsDouble();
        } else {
            log.error("Mean calculation problems");
            throw new NullPointerException();
        }
    }

    private double getStandardDeviation(Set<Vehicle> neighbors) {
        log.debug("Calculate cluster standard deviation.");
        return getStandardDeviation(neighbors, getMean(neighbors));
    }

    private double getStandardDeviation(Set<Vehicle> neighbors, double mean) {
        log.debug("Calculate cluster standard deviation.");
        double sum = neighbors.stream().mapToDouble(n -> Math.pow((n.getSpeed() - mean), 2)).sum();
        return Math.sqrt(sum / (neighbors.size() - 1));
    }

    private double getReceivedMessagePower(Vehicle vehicle) {
        log.debug("Calculate received message power.");
        return vehicle.getP_th() + vehicle.getG() - vehicle.getL();
    }
}
