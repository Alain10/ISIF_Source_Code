package ca.ott.al.starmap.core.order.unit.move;

import ca.ott.al.starmap.core.map.StarSystem;
import ca.ott.al.starmap.core.transport.TransportFleet;

public class MoveOrder extends MovementOrder {

    /**
     * 
     */
    private static final long serialVersionUID = 1153176771036633862L;

    private StarSystem origin, destination;
    private TransportFleet fleet;
    
    
    public MoveOrder(StarSystem origin, StarSystem destination, TransportFleet tempFleet){
        this.origin = origin;
        this.destination = destination;
        this.fleet = tempFleet;
    }
    
    
    @Override
    public String toString() {
        return "Move ["+destination.getName()+"]";
    }

    @Override
    public StarSystem getOrigin() {
        return origin;
    }

    @Override
    public StarSystem getDestination() {
        return destination;
    }


    public TransportFleet getFleet() {
        return fleet;
    }    
}
