package com.vosiievska.dbscanclustering.service.fuzzy.c.means.clustering;

import com.vosiievska.dbscanclustering.entity.Vehicle;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * https://sites.google.com/site/dataclusteringalgorithms/fuzzy-c-means-clustering-algorithm
 */
public class FuzzyClustering {

    private static final int MIN_VEHICLE_SPEED_RANGE = 22;
    private static final int MAX_VEHICLE_SPEED_RANGE = 30;
    private static final int MEAN_POWER_RANGE = -50;
    private static final int STANDARD_DEVIATION_POWER_RANGE = -30;
    private static final int MIN_POWER_RANGE = 0;
    private static final int MAX_POWER_RANGE = 100;

    public List<Vehicle> vehicles;
    public ArrayList<ArrayList<Float>> clusterCenters; // 0 -> x; 1 -> y
    private float[][] u;  // u ^ (k + 1)
    private float[][] u_pre; // u ^ k
    private int clusterCount;
    private int iteration;
    private int dimension;
    private int fuzziness; // 1 <= m < Infinity
    private double epsilon;
    public double finalError;

    public FuzzyClustering() {
        this.vehicles = new ArrayList<>();
        this.clusterCenters = new ArrayList<>();
        this.fuzziness = 2;
        this.epsilon = 0.01;
        this.dimension = 2;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public ArrayList<ArrayList<Float>> getClusterCenters() {
        return clusterCenters;
    }

    public float[][] getU() {
        return u;
    }

    public void run(int clusterNumber, int iteration, List<Vehicle> data) {
        this.clusterCount = clusterNumber;
        this.iteration = iteration;
        this.vehicles = data;

        // Step 1: Randomly select ‘c’ cluster centers.

        //start algorithm
        //1 assign initial membership values
        assignInitialMembership();

        for (int i = 0; i < this.iteration; i++) {
            //2 calculate cluster centers
            calculateClusterCenters();

            //3
            updateMembershipValues();

            //4
            finalError = checkConvergence();
            if (finalError <= epsilon) {
                break;
            }
        }
    }

    /**
     * this function get dataset size, min and max range, number of clusters and generate random number with gaussian
     * distribution
     */
    public List<Vehicle> createRandomData(int vehiclesAmount, int dimension, int minRange, int maxRange,
        int clusterAmount) {
        ArrayList<ArrayList<Integer>> centroids = new ArrayList<>();
        centroids.add(new ArrayList<>());

        int[] numberOfDataInEachArea = new int[clusterAmount];

        int range = maxRange - minRange + 1;
        int step = range / (clusterAmount + 1);
        for (int i = 1; i <= clusterAmount; i++) {
            centroids.get(0).add(minRange + i * step);
        }

        for (int i = 0; i < dimension - 1; i++) {
            centroids.add((ArrayList<Integer>) centroids.get(0).clone());
        }

        double variance = (centroids.get(0).get(1) - centroids.get(0).get(0)) / 2.5;
        for (int i = 0; i < dimension; i++) {
            Collections.shuffle(centroids.get(i));
        }

        Random r = new Random();
        int sum = 0;
        for (int i = 0; i < clusterAmount; i++) {
            int rg = r.nextInt(50) + 10;
            numberOfDataInEachArea[i] = (rg);
            sum += rg;
        }

        for (int i = 0; i < clusterAmount; i++) {
            numberOfDataInEachArea[i] = (int) ((((double) numberOfDataInEachArea[i]) / sum) * vehiclesAmount);
        }

        int idCounter = 1;
        Random fRandom = new Random();
        for (int i = 0; i < clusterAmount; i++) {
            for (int j = 0; j < numberOfDataInEachArea[i]; j++) {
                Vehicle vehicle = Vehicle.builder()
                    .id(idCounter++)
                    .x((float) (centroids.get(0).get(i) + fRandom.nextGaussian() * variance))
                    .y((float) (centroids.get(1).get(i) + fRandom.nextGaussian() * variance))
                    .speed(new Random().nextInt(MAX_VEHICLE_SPEED_RANGE - MIN_VEHICLE_SPEED_RANGE)
                        + MIN_VEHICLE_SPEED_RANGE)
                    .sensitivity(new Random().nextInt(MAX_POWER_RANGE - MIN_POWER_RANGE) + MIN_POWER_RANGE)
                    .p_th(new Random().nextInt(MAX_POWER_RANGE - MIN_POWER_RANGE) + MIN_POWER_RANGE)
                    .g(new Random().nextInt(MAX_POWER_RANGE - MIN_POWER_RANGE) + MIN_POWER_RANGE)
                    .l(new Random().nextInt(MAX_POWER_RANGE - MIN_POWER_RANGE) + MIN_POWER_RANGE)
                    .build();
                vehicles.add(vehicle);
            }
        }
        return vehicles;
    }

    /**
     * this function generate membership value for each data
     */
    private void assignInitialMembership() {
        u = new float[vehicles.size()][clusterCount];
        u_pre = new float[vehicles.size()][clusterCount];

        Random r = new Random();
        for (int i = 0; i < vehicles.size(); i++) {
            float sum = 0;
            for (int j = 0; j < clusterCount; j++) {
                u[i][j] = r.nextFloat() * 10 + 1;
                sum += u[i][j];
            }
            for (int j = 0; j < clusterCount; j++) {
                u[i][j] = u[i][j] / sum;
            }

//            float sum = 0;
//            for (int j = 0; j < clusterCount; j++) {
//                float tmp = r.nextFloat() * 10 + 1;
//                sum += u[i][j];
//                u[i][j] = tmp / sum;
//            } todo: do like this
        }
    }

    /**
     * in this function we calculate value of each cluster
     */
    private void calculateClusterCenters() {
        clusterCenters.clear();
        for (int i = 0; i < clusterCount; i++) {
            ArrayList<Float> tmp = new ArrayList<>(); // 0 -> x; 1 -> y
            // calculate for x

            float x_cluster_ij;
            float x_sum1 = 0;
            float x_sum2 = 0;
            for (int k = 0; k < vehicles.size(); k++) {
                double tt = Math.pow(u[k][i], fuzziness);
                x_sum1 += tt * vehicles.get(k).getX();
                x_sum2 += tt;
            }
            x_cluster_ij = x_sum1 / x_sum2;
            tmp.add(x_cluster_ij);

            // calculate for y
            float y_cluster_ij;
            float y_sum1 = 0;
            float y_sum2 = 0;
            for (int k = 0; k < vehicles.size(); k++) {
                double tt = Math.pow(u[k][i], fuzziness);
                y_sum1 += tt * vehicles.get(k).getY();
                y_sum2 += tt;
            }
            y_cluster_ij = y_sum1 / y_sum2;
            tmp.add(y_cluster_ij);

            // out ot for
            clusterCenters.add(tmp);
        }
    }

    /**
     * in this function we will update membership value
     */
    private void updateMembershipValues() {
        for (int i = 0; i < vehicles.size(); i++) {
            for (int j = 0; j < clusterCount; j++) {
                u_pre[i][j] = u[i][j];
                float sum = 0;
                float upper = Distance(vehicles.get(i), clusterCenters.get(j));
                for (int k = 0; k < clusterCount; k++) {
                    float lower = Distance(vehicles.get(i), clusterCenters.get(k));
                    sum += Math.pow((upper / lower), 2 / (fuzziness - 1));
                }
                u[i][j] = 1 / sum;
            }
        }
    }

    /**
     * get norm 2 of two point
     */
    private float Distance(Vehicle p1, ArrayList<Float> p2) {
        float sum = 0;

        sum += Math.pow(p1.getX() - p2.get(0), 2);
        sum += Math.pow(p1.getY() - p2.get(1), 2);

        sum = (float) Math.sqrt(sum);
        return sum;
    }

    private float Distance(ArrayList<Float> p1, ArrayList<Float> p2) {
        float sum = 0;
        for (int i = 0; i < p1.size(); i++) {
            sum += Math.pow(p1.get(i) - p2.get(i), 2);
        }
        sum = (float) Math.sqrt(sum);
        return sum;
    }

    /**
     * we calculate norm 2 of ||U - U_pre||
     */
    private double checkConvergence() {
        double sum = 0;
        for (int i = 0; i < vehicles.size(); i++) {
            for (int j = 0; j < clusterCount; j++) {
                sum += Math.pow(u[i][j] - u_pre[i][j], 2);
            }
        }
        return Math.sqrt(sum);
    }

    /**
     * write random generated data to file for visualizing
     */
    public void writeDataToFile(ArrayList<ArrayList<Float>> inpData, String fileName) throws IOException {

        FileWriter fileWriter = new FileWriter("./" + fileName + ".csv");
        PrintWriter printWriter = new PrintWriter(fileWriter);

        for (int i = 0; i < inpData.size(); i++) {
            String res = "";
            for (int j = 0; j < inpData.get(i).size(); j++) {
                if (j == inpData.get(i).size() - 1) {
                    res += inpData.get(i).get(j);
                } else {
                    res += inpData.get(i).get(j) + ",";
                }
            }
            printWriter.println(res);
        }
        printWriter.close();
    }

    public void writeDataToConsole(ArrayList<ArrayList<Float>> inpData) throws IOException {
        for (int i = 0; i < inpData.size(); i++) {
            String res = "";
            for (int j = 0; j < inpData.get(i).size(); j++) {
                if (j == inpData.get(i).size() - 1) {
                    res += inpData.get(i).get(j);
                } else {
                    res += inpData.get(i).get(j) + ",";
                }
            }
            System.out.println(res);
        }
    }
}
