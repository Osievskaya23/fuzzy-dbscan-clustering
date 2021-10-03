package com.vosiievska.dbscanclustering.service.impl;

import static com.vosiievska.dbscanclustering.utils.Utils.getDecimalFormater;

import com.vosiievska.dbscanclustering.entity.Cluster;
import com.vosiievska.dbscanclustering.entity.Vehicle;
import com.vosiievska.dbscanclustering.entity.VehicleFunction;
import com.vosiievska.dbscanclustering.service.VehicleService;
import java.text.DecimalFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class VehicleServiceImpl implements VehicleService {

    private static double THRESHOLD_CONSTANT = 1.5;

    @Override
    public double getRelativeAverageSpeed(Vehicle vehicle, Cluster<Vehicle> cluster) {
        log.debug("Vehicle {}. Calculate relative average speed.", vehicle.getId());
        return cluster.getStandardDeviation() == 0 ? 0
            : (vehicle.getSpeed() - cluster.getMean()) / cluster.getStandardDeviation();
    }

    @Override
    public double getNeighborhoodDegree(Vehicle vehicle, Cluster<Vehicle> cluster) {
        log.debug("Vehicle {}. Calculate neighborhood degree.", vehicle.getId());
        double threshold = THRESHOLD_CONSTANT * cluster.getStandardDeviation();
        long count = vehicle.getNeighbors().stream()
            .filter(n -> Math.abs(vehicle.getSpeed() - n.getSpeed()) < threshold)
            .count();
        return count / (double) cluster.getVehicles().size();
    }

    @Override
    public double getLinkQuality(Vehicle vehicle, Cluster<Vehicle> cluster) {
        log.debug("Vehicle {}. Calculate RSU link quality.", vehicle.getId());
        double vehicleMessagePower = vehicle.getP_th() + vehicle.getG() - vehicle.getL();
        double rlq =
            vehicleMessagePower / (cluster.getMaxReceivedMessagePower() - cluster.getMinReceivedMessagePower());
        return Math.min(rlq, 1);
    }

    @Override
    public double getBestAgentLocation(Vehicle vehicle, Cluster<Vehicle> cluster) {
        log.debug("Vehicle {}. Calculate best agent location.", vehicle.getId());
        return Math.sqrt(Math.pow(cluster.getClusterCenter()[1] - vehicle.getY(), 2) + Math
            .pow(cluster.getClusterCenter()[0] - vehicle.getX(), 2));
    }

    @Override
    public void getFitFactor(Vehicle vehicle, Cluster<Vehicle> cluster) {
        double ras = getRelativeAverageSpeed(vehicle, cluster);
        double nd = getNeighborhoodDegree(vehicle, cluster);
        double rlq = getLinkQuality(vehicle, cluster);
        double bal = getBestAgentLocation(vehicle, cluster);

        vehicle.setFunction(new VehicleFunction(ras, nd, rlq, bal));

        DecimalFormat df = getDecimalFormater();
        String fitFactor = String.format("%s%s%s%s%s%s", StringUtils.center("Vehicle " + vehicle.getId() + "->", 20),
            StringUtils.center("relative average speed = " + df.format(ras) + ";", 40),
            StringUtils.center("neighborhood degree = " + df.format(nd) + ";", 30),
            StringUtils.center("RSU link quality = " + df.format(rlq) + ";", 40),
            StringUtils.center("best agent location = " + df.format(bal) + ";", 30),
            StringUtils.center("Fit Function = " + df.format(vehicle.getFunction().getFitFactor()) + ";", 30));

        log.info(fitFactor);
    }
}
