package com.vosiievska.dbscanclustering.service.impl;

import com.vosiievska.dbscanclustering.entity.Cluster;
import com.vosiievska.dbscanclustering.entity.Vehicle;
import com.vosiievska.dbscanclustering.service.VehicleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class VehicleServiceImpl implements VehicleService {

    private static int THRESHOLD_CONSTANT = 1;

    @Override
    public double getRelativeAverageSpeed(Vehicle vehicle, Cluster<Vehicle> cluster) {
        log.debug("Vehicle {}. Calculate relative average speed.", vehicle.getId());
        return (vehicle.getSpeed() - cluster.getMean()) / cluster.getStandardDeviation();
    }

    @Override
    public double getNeighborhoodDegree(Vehicle vehicle,
        Cluster<Vehicle> cluster) {
        log.debug("Vehicle {}. Calculate neighborhood degree.", vehicle.getId());
        double threshold = THRESHOLD_CONSTANT * cluster.getStandardDeviation();
        return vehicle.getNeighbors().stream().filter(n -> (vehicle.getSpeed() - n.getSpeed()) < threshold).count();
    }

    @Override
    public double getLinkQuality(Vehicle vehicle,
        Cluster<Vehicle> cluster) {
        log.debug("Vehicle {}. Calculate RSU link quality.", vehicle.getId());
        double vehicleMessagePower = vehicle.getP_th() + vehicle.getG() - vehicle.getL();
        return vehicleMessagePower / cluster.getMaxReceivedMessagePower() - cluster.getMinReceivedMessagePower();
    }

    @Override
    public double getBestAgentLocation(Vehicle vehicle, Cluster<Vehicle> cluster) {
        log.debug("Vehicle {}. Calculate best agent location.", vehicle.getId());
        return Math
            .sqrt(Math.pow(cluster.getClusterCenter()[1] - vehicle.getY(), 2) + Math
                .pow(cluster.getClusterCenter()[0] - vehicle.getX(), 2));
    }

    @Override
    public void getFitFactor(Vehicle vehicle, Cluster<Vehicle> cluster) {
        log.info("Vehicle {}", vehicle.getId());
        String fitFactor = String.format("%n%s%s%s%s%s", StringUtils.center("Vehicle " + vehicle.getId(), 20),
            StringUtils.center("relative average speed = " + getRelativeAverageSpeed(vehicle, cluster) + ";", 30),
            StringUtils.center("neighborhood degree = " + getNeighborhoodDegree(vehicle, cluster) + ";", 30),
            StringUtils.center("RSU link quality = " + getLinkQuality(vehicle, cluster) + ";", 30),
            StringUtils.center("best agent location = " + getBestAgentLocation(vehicle, cluster) + ";", 30));
        log.info("Fit Factor: {} ", fitFactor);
    }
}
