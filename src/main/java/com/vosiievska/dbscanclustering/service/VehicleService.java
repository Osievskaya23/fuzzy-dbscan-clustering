package com.vosiievska.dbscanclustering.service;

import com.vosiievska.dbscanclustering.entity.Cluster;
import com.vosiievska.dbscanclustering.entity.Vehicle;

public interface VehicleService {

    double getRelativeAverageSpeed(Vehicle vehicle,
        Cluster<Vehicle> cluster);

    double getNeighborhoodDegree(Vehicle vehicle,
        Cluster<Vehicle> cluster);

    double getLinkQuality(Vehicle vehicle,
        Cluster<Vehicle> cluster);

    double getBestAgentLocation(Vehicle vehicle, Cluster<Vehicle> clusterCenter);

    void getFitFactor(Vehicle vehicle, Cluster<Vehicle> cluster);
}
