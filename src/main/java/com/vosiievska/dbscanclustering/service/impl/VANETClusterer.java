package com.vosiievska.dbscanclustering.service.impl;

import com.vosiievska.dbscanclustering.entity.Cluster;
import java.util.Collection;
import java.util.List;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

public abstract class VANETClusterer<T extends Clusterable> {

    private DistanceMeasure measure;

    protected VANETClusterer(DistanceMeasure measure) {
        this.measure = measure;
    }

    public abstract List<? extends Cluster<T>> cluster(Collection<T> var1)
        throws MathIllegalArgumentException, ConvergenceException;

    public DistanceMeasure getDistanceMeasure() {
        return this.measure;
    }

    protected double distance(Clusterable p1, Clusterable p2) {
        return this.measure.compute(p1.getPoint(), p2.getPoint());
    }
}
