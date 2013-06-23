package ca.ott.al.starmap.core.order.factory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProductionItem implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    String name;
    double cost;
    double air;
    double ground;
    double time;

    public ProductionItem(String name, double cost, double air, double ground, double time) {
        super();
        this.name = name;
        this.cost = cost;
        this.air = air;
        this.ground = ground;
        this.time = time;
    }
    
    public String toString(){
        return name;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getCost() {
        return cost;
    }
    public void setCost(double cost) {
        this.cost = cost;
    }
    public double getAir() {
        return air;
    }
    public void setAir(double air) {
        this.air = air;
    }
    public double getGround() {
        return ground;
    }
    public void setGround(double ground) {
        this.ground = ground;
    }
    public double getTime() {
        return time;
    }
    public void setTime(double time) {
        this.time = time;
    }
}
