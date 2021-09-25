package com.vosiievska.dbscanclustering.utils;

import java.util.Random;

/**
 * Generate pseudo-random floating point values, with an approximately Gaussian (normal) distribution.
 *
 * Many physical measurements have an approximately Gaussian distribution; this provides a way of simulating such
 * values.
 */
public final class RandomGaussian {

    private Random fRandom = new Random();

    public static void main(String[] args) {
        RandomGaussian gaussian = new RandomGaussian();
        double MEAN = 100.0f;
        double VARIANCE = 5.0f;
        for (int idx = 1; idx <= 10; ++idx) {
            System.out.println("Generated : " + gaussian.getGaussian(MEAN, VARIANCE));
        }
    }

    private double getGaussian(double aMean, double aVariance) {
        return aMean + fRandom.nextGaussian() * aVariance;
    }

}
