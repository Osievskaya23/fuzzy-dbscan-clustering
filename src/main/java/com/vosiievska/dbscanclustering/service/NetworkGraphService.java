package com.vosiievska.dbscanclustering.service;

import com.vosiievska.dbscanclustering.entity.Cluster;
import com.vosiievska.dbscanclustering.entity.Vehicle;
import com.vosiievska.dbscanclustering.service.response.DBSCANResponse;
import java.util.List;

public interface NetworkGraphService {

    List<Vehicle> generateVehicles(int clustersAmount, int vehiclesNumber);

    DBSCANResponse computeClusters(List<Vehicle> vehicles, double eps, int minPts);

    List<Cluster<Vehicle>> computeClusterHeads(DBSCANResponse dbscanResponse, int iterations);
}
