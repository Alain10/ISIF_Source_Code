package ca.ott.al.starmap.core.order.faction;

import java.io.Serializable;

public abstract class FactionOrder implements Comparable<Object>, Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -268620846250902109L;

    public abstract double getCost();
    
    public abstract boolean execute();
    
    public abstract String toString();
    
    public abstract int getDuration();
}
