package com.vosiievska.dbscanclustering;

import com.vosiievska.dbscanclustering.entity.Cluster;
import com.vosiievska.dbscanclustering.entity.Vehicle;
import com.vosiievska.dbscanclustering.service.ClusterService;
import com.vosiievska.dbscanclustering.service.NetworkGraphService;
import com.vosiievska.dbscanclustering.service.impl.ClusterServiceImpl;
import com.vosiievska.dbscanclustering.service.impl.NetworkGraphServiceImpl;
import com.vosiievska.dbscanclustering.service.response.DBSCANResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;

@Slf4j
public class Test {

    /**
     * 1 граф -- кластеризация путём DBSCAN
     * 2 граф -- вычисление мнимых кластерхедов + формирование кластеров по наибольшей вероятности
     * 3 граф -- формирование кластер хедов и кластеров путём оптимизации
     */

    /**
     * 1. генерация датасета транспортных средств 2. вычисление оптимального колл-ва кластеров при помощи DBSCAN 3.
     * вычисление мнимых CH и кластеров по вероятности принадележности вершин к ним 4. выбор CH (функционал Vehicle) 4.1
     * подсчёт своего Fit Factor 4.2 бродкаст 4.3 голосование 4.4 бродкаст кластер id (для CH)
     */

    private static NetworkGraphService networkService = new NetworkGraphServiceImpl();
    private static ClusterService clusterService = new ClusterServiceImpl();

    public static void main(String[] args) {
        List<Vehicle> vehicles = networkService.generateVehicles(10, 60);
        DBSCANResponse dbscanResponse = networkService.computeClusters(vehicles, 5.0, 1);
        List<Cluster<Vehicle>> clusters = networkService.computeClusterHeads(dbscanResponse, 100);

        clusters.forEach(c -> clusterService.calculateFitFactor(c));
        clusters.forEach(c -> clusterService.performElections(c));

        DefaultGraph graph = new DefaultGraph("Result graph");
        graph.display();

        log.info("Print cluster members.");
        for (Cluster cluster : clusters) {
            for (Vehicle vehicle : (List<Vehicle>) cluster.getVehicles()) {
                Node node = graph.addNode(Integer.toString(vehicle.getId()));
                node.addAttribute("ui.frozen");
                node.setAttribute("ui.style", cluster.getNodeStylistic());
                node.setAttribute("x", vehicle.getX());
                node.setAttribute("y", vehicle.getY());

                if (((Vehicle) cluster.getClusterHead()).getId() == vehicle.getId()) {
                    node.setAttribute("ui.label", "CH-" + vehicle.getId());
                } else {
                    node.setAttribute("ui.label", vehicle.getId());
                }
            }
        }
    }
}
