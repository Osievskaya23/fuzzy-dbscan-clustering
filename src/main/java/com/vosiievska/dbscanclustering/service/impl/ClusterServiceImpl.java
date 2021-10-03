package com.vosiievska.dbscanclustering.service.impl;

import com.vosiievska.dbscanclustering.entity.Cluster;
import com.vosiievska.dbscanclustering.entity.Vehicle;
import com.vosiievska.dbscanclustering.service.ClusterService;
import com.vosiievska.dbscanclustering.service.VehicleService;
import java.util.Comparator;
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
            .format("Cluster %s -> %s%s%s%s", cluster.getId(), StringUtils.center("mean = " + cluster.getMean(), 20),
                StringUtils.center("standard deviation = " + cluster.getStandardDeviation() + ";", 40),
                StringUtils.center("max received message power = " + cluster.getMaxReceivedMessagePower() + ";", 40),
                StringUtils.center("min received message power = " + cluster.getMinReceivedMessagePower() + ";", 40));

        log.info(clusterInitResult);

        cluster.getVehicles().forEach(v -> vehicleService.getFitFactor(v, cluster));
    }

    @Override
    public void performElections(Cluster<Vehicle> cluster) {
        log.info("Cluster {}. Perform cluster head election.", cluster.getId());
        Vehicle clusterHead = cluster.getVehicles().stream()
            .min(Comparator.comparing(v -> v.getFunction().getFitFactor()))
            .orElseThrow();
        cluster.setClusterHead(clusterHead);
    }
}
