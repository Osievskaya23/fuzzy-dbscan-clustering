package com.vosiievska.dbscanclustering.entity;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.ml.clustering.Clusterable;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Cluster<T extends Clusterable> implements Serializable {

    private static final long serialVersionUID = -3442297081515880464L;

    private int id;
    private List<T> points;
    private T clusterHead;
    private Double[] clusterCenter;
    private Color color;
    private double mean;
    private double standardDeviation;
    private double maxReceivedMessagePower;
    private double minReceivedMessagePower;

    public Cluster(int id, Color color) {
        this.points = new ArrayList<>();
        this.id = id;
        this.color = color;
    }

    public Cluster(int id, Double[] clusterCenter, Color color) {
        this.points = new ArrayList<>();
        this.id = id;
        this.clusterCenter = clusterCenter;
        this.color = color;
    }

    public void init() {
        this.mean = this.points.stream().mapToDouble(p -> ((Vehicle) p).getSpeed()).average().getAsDouble();
        double sum = this.points.stream().mapToDouble(p -> Math.pow((((Vehicle) p).getSpeed() - mean), 2)).sum();
        this.standardDeviation = Math.sqrt(sum / this.points.size());

        List<Double> neighborMessagePower = this.points.stream()
            .map(p -> (Vehicle) p)
            .map(v -> v.getP_th() + v.getG() - v.getL())
            .collect(Collectors.toList());
        this.maxReceivedMessagePower = Collections.max(neighborMessagePower);
        this.minReceivedMessagePower = Collections.min(neighborMessagePower);
    }

    public int getId() {
        return id;
    }

    public void addPoint(T point) {
        this.points.add(point);
    }

    public List<T> getVehicles() {
        return this.points;
    }

    public Color getColor() {
        return color;
    }

    public String getNodeStylistic() {
        return String
            .format("text-size: 20px; fill-color: rgb(%s, %s, %s);", this.color.getRed(), this.color.getGreen(),
                this.color.getBlue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cluster)) {
            return false;
        }
        Cluster<?> cluster = (Cluster<?>) o;
        return getId() == cluster.getId() &&
            getPoints().equals(cluster.getPoints()) &&
            getColor().equals(cluster.getColor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getPoints(), getColor());
    }

    @Override
    public String toString() {
        return String.format("%nCluster %s:%n %s", this.id, this.points.stream()
            .map(Object::toString).collect(Collectors.joining("")));
    }
}
