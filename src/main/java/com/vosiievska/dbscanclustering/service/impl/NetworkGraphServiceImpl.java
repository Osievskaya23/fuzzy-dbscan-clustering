package com.vosiievska.dbscanclustering.service.impl;

import com.vosiievska.dbscanclustering.entity.Cluster;
import com.vosiievska.dbscanclustering.entity.Vehicle;
import com.vosiievska.dbscanclustering.service.NetworkGraphService;
import com.vosiievska.dbscanclustering.service.fuzzy.c.means.clustering.FuzzyClustering;
import com.vosiievska.dbscanclustering.service.response.DBSCANResponse;
import com.vosiievska.dbscanclustering.utils.ClusterColorsUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;

@Slf4j
public class NetworkGraphServiceImpl implements NetworkGraphService {

    @Override
    public List<Vehicle> generateVehicles(int clustersAmount, int vehiclesNumber) {
        log.info("Generate network. Number of clusters: {}; Data set size: {}.", clustersAmount, vehiclesNumber);
        FuzzyClustering clusterer = new FuzzyClustering();
        List<Vehicle> vehicles = clusterer.createRandomData(vehiclesNumber, 2, 1, 100, clustersAmount);

        log.info("Finish network generation.");
        log.info("Display generated network graph.");
        DefaultGraph graph = new DefaultGraph("Fuzzy C-means");
        graph.display();

        for (int i = 0; i < vehicles.size(); i++) {
            Node node = graph.addNode(String.valueOf(vehicles.get(i).getId()));
            node.addAttribute("ui.frozen");
            node.setAttribute("ui.style", "text-size: 20px;");
            node.setAttribute("x", vehicles.get(i).getX());
            node.setAttribute("y", vehicles.get(i).getY());
            node.setAttribute("ui.label", i + 1);
        }
        return vehicles;
    }

    @Override
    public DBSCANResponse computeClusters(List<Vehicle> vehicles, double eps, int minPts) {
        log.info("Start DBSCAN clustering. Vehicles number: {}; eps: {}; minPts: {}.", vehicles.size(), eps, minPts);

        DBSCANClustererImpl<Vehicle> dbscanClusterer = new DBSCANClustererImpl<>(eps, minPts);
        List<Cluster<Vehicle>> clusters = dbscanClusterer.cluster(vehicles);

        // get noise
        List<Vehicle> partOfCluster = clusters.stream()
            .flatMap(c -> c.getVehicles().stream())
            .collect(Collectors.toList());

        List<Vehicle> noise = vehicles.stream()
            .filter(v -> !partOfCluster.contains(v))
            .collect(Collectors.toList());

        log.info("Finish DBSCAN clustering. Clusters number: {}; noise vehicles: {}; total clusters: {}.",
            clusters.size(), noise.size(), clusters.size() + noise.size());

        log.info("Clusters: {}", clusters.stream().map(Cluster::toString).collect(Collectors.joining()));
        log.info("Noise: {}", noise.stream().map(Vehicle::toString).collect(Collectors.joining("")));

        log.info("Display DBSCAN clustering result graph.");
        DefaultGraph graph = new DefaultGraph("DBSCAN");
        graph.display();

        for (Cluster<Vehicle> cluster : clusters) {
            for (Vehicle vehicle : cluster.getVehicles()) {
                Node node = graph.addNode(Integer.toString(vehicle.getId()));
                node.addAttribute("ui.frozen");
                node.setAttribute("ui.class", "cluster " + cluster.getId());
                node.setAttribute("ui.style", cluster.getNodeStylistic());
                node.setAttribute("x", vehicle.getX());
                node.setAttribute("y", vehicle.getY());
                node.setAttribute("ui.label", vehicle.getId());
            }
        }

        for (int i = 0; i < noise.size(); i++) {
            Node node = graph.addNode(Integer.toString(noise.get(i).getId()));
            node.addAttribute("ui.frozen");
            node.setAttribute("ui.style", "text-size: 20px;");
            node.setAttribute("x", noise.get(i).getX());
            node.setAttribute("y", noise.get(i).getY());
            node.setAttribute("ui.label", "NOISE-" + noise.get(i).getId());
        }

        return DBSCANResponse.builder()
            .clustersNumber(clusters.size())
            .partOfCluster(partOfCluster)
            .noise(noise)
            .build();
    }

    @Override
    public List<Cluster<Vehicle>> computeClusterHeads(DBSCANResponse dbscanResponse, int iterations) {
        log.info("Start Fuzzy clustering. Iterations {}.", iterations);

        FuzzyClustering clusterer = new FuzzyClustering();
        clusterer.run(dbscanResponse.getClustersNumber(), iterations, dbscanResponse.getPartOfCluster());
        ArrayList<ArrayList<Float>> clusterCenters = clusterer.getClusterCenters();

        log.info("ClusterHeads: {}.", clusterCentersToString(clusterCenters));

        log.info("Form clusters according to the membership point.");
        List<Cluster<Vehicle>> clusters = new ArrayList<>();
        ClusterColorsUtils colorsUtils = new ClusterColorsUtils();
        for (int i = 0; i < clusterCenters.size(); i++) {
            Cluster<Vehicle> cluster = new Cluster<>(i + 1, new Double[]{Double.valueOf(clusterCenters.get(i).get(0)),
                Double.valueOf(clusterCenters.get(i).get(1))}, colorsUtils.getNextColor());
            clusters.add(cluster);
        }

        for (int i = 0; i < dbscanResponse.getPartOfCluster().size(); i++) {
            log.debug("Get max probability for vehicle.");
            int max = 0;
            for (int j = 0; j < clusterer.getU()[i].length; j++) {
                max = clusterer.getU()[i][j] > clusterer.getU()[i][max] ? j : max;
            }

            log.debug("Set vehicle to cluster with max probability.");
            clusters.get(max).addPoint(dbscanResponse.getPartOfCluster().get(i));
        }

        log.debug("Create separate cluster for noise.");
        for (int i = 0; i < dbscanResponse.getNoise().size(); i++) {
            Cluster<Vehicle> cluster = new Cluster<>(clusters.size() + i,
                new Double[]{dbscanResponse.getNoise().get(i).getX(), dbscanResponse.getNoise().get(i).getY()},
                colorsUtils.getNextColor());
            cluster.addPoint(dbscanResponse.getNoise().get(i));
            clusters.add(cluster);
        }

        log.info("Finish fuzzy clustering. Expected clusters number: {}; cluster centers number: "
                + "{}; noise clusters: {}, total clusters: {}", dbscanResponse.getClustersNumber(), clusterCenters.size(),
            dbscanResponse.getNoise().size(), clusters.size());

        log.info("Clusters: {}", clusters.stream().map(Cluster::toString).collect(Collectors.joining()));

        log.info("Display Fuzzy c-means clustering result graph.");
        DefaultGraph graph = new DefaultGraph("Fuzzy C-means");
        graph.display();

        log.debug("Print cluster heads for parts of cluster.");
        for (int i = 0; i < clusterCenters.size(); i++) {
            Node node = graph.addNode(Integer.toString(i + 100));
            node.addAttribute("ui.frozen");
            node.setAttribute("ui.style", "fill-color: rgb(255, 0, 0);");
            node.setAttribute("x", clusterCenters.get(i).get(0));
            node.setAttribute("y", clusterCenters.get(i).get(1));
            node.setAttribute("ui.label", "CH-" + (i + 1));
        }

        log.debug("Print cluster heads for noise.");
        for (int i = 0; i < dbscanResponse.getNoise().size(); i++) {
            Node node = graph.addNode(Integer.toString(clusterCenters.size() + i + 100));
            node.addAttribute("ui.frozen");
            node.setAttribute("ui.style", "fill-color: rgb(255, 0, 0);");
            node.setAttribute("x", dbscanResponse.getNoise().get(i).getX());
            node.setAttribute("y", dbscanResponse.getNoise().get(i).getY());
            node.setAttribute("ui.label", "CH-" + (clusterCenters.size() + i + 1));
        }

        log.debug("Print cluster members.");
        for (Cluster cluster : clusters) {
            for (Vehicle vehicle1 : (List<Vehicle>) cluster.getVehicles()) {
                Node node = graph.addNode(Integer.toString(vehicle1.getId()));
                node.addAttribute("ui.frozen");
                node.setAttribute("ui.style", cluster.getNodeStylistic());
                node.setAttribute("x", vehicle1.getX());
                node.setAttribute("y", vehicle1.getY());

                if (dbscanResponse.getNoise().contains(vehicle1)) {
                    node.setAttribute("ui.label", "NOISE-" + vehicle1.getId());
                } else {
                    node.setAttribute("ui.label", vehicle1.getId());
                }
            }
        }

        log.debug("Set vehicle neightbors.");
        for (int i = 0; i < clusters.size(); i++) {
            for (int j = 0; j < clusters.get(i).getVehicles().size(); j++) {

                int finalI = i;
                int finalJ = j;
                clusters.get(i).getVehicles().get(j).setNeighbors(
                    clusters.get(i).getVehicles().stream()
                        .filter(v -> v.getId() != clusters.get(finalI).getVehicles().get(finalJ).getId())
                        .collect(Collectors.toSet())
                );
            }
        }
        return clusters;
    }

    public String clusterCentersToString(ArrayList<ArrayList<Float>> inpData) {
        String result = "";
        for (int i = 0; i < inpData.size(); i++) {
            String line = StringUtils.center("\n" + i + ": ", 3);
            for (int j = 0; j < inpData.get(i).size(); j++) {
                if (j == inpData.get(i).size() - 1) {
                    line += StringUtils.center(Float.toString(inpData.get(i).get(j)), 20);
                } else {
                    line += StringUtils.center(inpData.get(i).get(j) + ", ", 20);
                }
            }
            result += line;
        }
        return result;
    }
}
