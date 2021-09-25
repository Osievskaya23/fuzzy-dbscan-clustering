package com.vosiievska.dbscanclustering.service.impl;

import com.vosiievska.dbscanclustering.entity.Cluster;
import com.vosiievska.dbscanclustering.utils.ClusterColorsUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.math3.util.MathUtils;

public class DBSCANClustererImpl<T extends Clusterable> extends VANETClusterer<T> {

    private final double eps;
    private final int minPts;
    private ClusterColorsUtils clusterColorsUtils = new ClusterColorsUtils();

    public DBSCANClustererImpl(double eps, int minPts) throws NotPositiveException {
        this(eps, minPts, new EuclideanDistance());
    }

    public DBSCANClustererImpl(double eps, int minPts, DistanceMeasure measure) throws NotPositiveException {
        super(measure);
        if (eps < 0.0D) {
            throw new NotPositiveException(eps);
        } else if (minPts < 0) {
            throw new NotPositiveException(minPts);
        } else {
            this.eps = eps;
            this.minPts = minPts;
        }
    }

    public double getEps() {
        return this.eps;
    }

    public int getMinPts() {
        return this.minPts;
    }

    public List<Cluster<T>> cluster(Collection<T> points) throws NullArgumentException {
        MathUtils.checkNotNull(points);
        List<Cluster<T>> clusters = new ArrayList();
        Map<Clusterable, DBSCANClustererImpl.PointStatus> visited = new HashMap();
        Iterator i$ = points.iterator();

        int counter = 1;
        while (i$.hasNext()) {
            T point = (T) i$.next();
            if (visited.get(point) == null) {
                List<T> neighbors = this.getNeighbors(point, points);
                if (neighbors.size() >= this.minPts) {
                    Cluster<T> cluster = new Cluster(counter, clusterColorsUtils.getNextColor());
                    clusters.add(this.expandCluster(cluster, point, neighbors, points, visited));
                    counter++;
                } else {
                    visited.put(point, DBSCANClustererImpl.PointStatus.NOISE);
                }
            }
        }

        return clusters;
    }

    private Cluster<T> expandCluster(Cluster<T> cluster, T point, List<T> neighbors, Collection<T> points,
        Map<Clusterable, DBSCANClustererImpl.PointStatus> visited) {
        cluster.addPoint(point);
        visited.put(point, PointStatus.PART_OF_CLUSTER);
        List<T> seeds = new ArrayList(neighbors);

        for (int index = 0; index < seeds.size(); ++index) {
            T current = (T) ((List) seeds).get(index);
            PointStatus pStatus = visited.get(current);
            if (pStatus == null) {
                List<T> currentNeighbors = this.getNeighbors(current, points);
                if (currentNeighbors.size() >= this.minPts) {
                    seeds = this.merge((List) seeds, currentNeighbors);
                }
            }

            if (pStatus != PointStatus.PART_OF_CLUSTER) {
                visited.put(current, PointStatus.PART_OF_CLUSTER);
                cluster.addPoint(current);
            }
        }

        return cluster;
    }

    private List<T> getNeighbors(T point, Collection<T> points) {
        List<T> neighbors = new ArrayList();
        Iterator i$ = points.iterator(); // todo replace with for

        while (i$.hasNext()) {
            T neighbor = (T) i$.next();
            if (point != neighbor && this.distance(neighbor, point) <= this.eps) {
                neighbors.add(neighbor);
            }
        }

        return neighbors;
    }

    private List<T> merge(List<T> one, List<T> two) {
        Set<T> oneSet = new HashSet(one);
        Iterator i$ = two.iterator();

        while (i$.hasNext()) {
            T item = (T) i$.next();
            if (!oneSet.contains(item)) {
                one.add(item);
            }
        }

        return one;
    }

    private enum PointStatus {
        NOISE,
        PART_OF_CLUSTER;

        PointStatus() {
        }
    }
}
