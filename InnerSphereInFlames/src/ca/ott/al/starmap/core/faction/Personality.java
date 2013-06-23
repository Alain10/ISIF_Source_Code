package ca.ott.al.starmap.core.faction;

import java.io.Serializable;

import ca.ott.al.starmap.core.map.StarMapObject;

public class Personality extends StarMapObject implements Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = -7216527271297722662L;
    
    private int leadership;
    private int popularity;
    
    private int recoveryTime;
    
    public Personality(String name, int leadership, int popularity){
        super(name);
        this.leadership = leadership;
        this.popularity = popularity;
    }
    
    public int getLeadership() {
        return leadership;
    }
    public int getPopularity() {
        return popularity;
    }
    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }
    public void setRecoveryTime(int recoveryTime) {
        this.recoveryTime = recoveryTime;
    }
    public int getRecoveryTime() {
        return recoveryTime;
    }
}
