package ca.ott.al.starmap.core.util;

import java.io.Serializable;

public class StellarPosition implements Comparable<StellarPosition>, Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -7311136455226344590L;
    
    private double x;
    private double y;
    private double z;

    public StellarPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public StellarPosition(int x, int y) {
        new StellarPosition(x, y, 0);
    }

    public StellarPosition(double x, double y) {
        this.x = x;
        this.y = y;
        this.z = 0;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    @Override
    public int compareTo(StellarPosition o) {
        if (o.x > x) {
            return 1;
        } else if (o.x < x) {
            return -1;
        } else if (o.y > y) {
            return 1;
        } else if (o.y < y) {
            return -1;
        } else if (o.z > z) {
            return 1;
        } else if (o.z < z) {
            return -1;
        } else {
            return 0;
        }
    }
}
