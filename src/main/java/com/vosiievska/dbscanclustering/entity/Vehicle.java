package com.vosiievska.dbscanclustering.entity;

import static com.vosiievska.dbscanclustering.utils.Utils.getDecimalFormater;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.ml.clustering.Clusterable;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Vehicle implements Clusterable {

    private static final long serialVersionUID = 3946024775784901369L;

    private int id;
    private double x;
    private double y;
    private double speed; // [22 -  30 m/s] could be negative due to movement direction
    private double sensitivity; // [0-100 dB] Sv: sensitivity of the vehicle
    private double p_th; // [0-100 dB] transmitter output power
    private double g; // [0-100 dB] total transmitter and receiver antenna gain
    private double l; // [0-100 dB] total transmitter;
    private VehicleFunction function;
    private Set<Vehicle> neighbors;

    public void setNeighbors(Set<Vehicle> neighbors) {
        this.neighbors = neighbors;
    }

    @Override
    public double[] getPoint() {
        return new double[]{this.x, this.y};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Vehicle)) {
            return false;
        }
        Vehicle vehicle = (Vehicle) o;
        return getId() == vehicle.getId() &&
            Double.compare(vehicle.getX(), getX()) == 0 &&
            Double.compare(vehicle.getY(), getY()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getX(), getY());
    }

    @Override
    public String toString() {
        String format = "\n%s%s%s%s%s%s%s%s";
        DecimalFormat df = getDecimalFormater();
        return String.format(format, StringUtils.center("Vehicle " + id, 20),
            StringUtils.center("x = " + df.format(this.x) + ";", 20),
            StringUtils.center("y = " + df.format(this.y) + ";", 20),
            StringUtils.center("speed = " + df.format(this.speed) + ";", 20),
            StringUtils.center("sensitivity = " + df.format(this.sensitivity) + ";", 20),
            StringUtils.center("p_th = " + df.format(this.p_th) + ";", 20),
            StringUtils.center("g = " + df.format(this.g) + ";", 20),
            StringUtils.center("l = " + df.format(this.l) + ";", 20));
    }
}
