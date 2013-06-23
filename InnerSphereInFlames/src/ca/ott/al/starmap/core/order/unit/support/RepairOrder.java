package ca.ott.al.starmap.core.order.unit.support;

import java.util.List;
import java.util.Vector;

import ca.ott.al.starmap.core.unit.Reinforcement;

public class RepairOrder extends SupportOrder {

    /**
     * 
     */
    private static final long serialVersionUID = -117537678038485342L;

    private Vector<Reinforcement> reinforcements;
    
    public RepairOrder(Vector<Reinforcement> reinforcements){
        this.reinforcements = reinforcements;
    }
    
    public String toString(){
        return "Repair";
    }

    public Vector<Reinforcement> getReinforcements() {
        return reinforcements;
    }
    
}
