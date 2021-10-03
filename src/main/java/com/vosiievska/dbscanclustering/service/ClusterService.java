package com.vosiievska.dbscanclustering.service;

import com.vosiievska.dbscanclustering.entity.Cluster;
import com.vosiievska.dbscanclustering.entity.Vehicle;

public interface ClusterService {

    void calculateFitFactor(Cluster<Vehicle> cluster);

    void performElections(Cluster<Vehicle> cluster);
}
