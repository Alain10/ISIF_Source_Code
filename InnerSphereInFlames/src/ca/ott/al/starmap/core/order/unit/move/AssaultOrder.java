package ca.ott.al.starmap.core.order.unit.move;

import ca.ott.al.starmap.core.map.StarSystem;
import ca.ott.al.starmap.core.transport.TransportFleet;

public class AssaultOrder extends MovementOrder {

    /**
     * 
     */
    private static final long serialVersionUID = 8955814736155368615L;
    
    private StarSystem origin, destination;
    private TransportFleet fleet;
    
    public AssaultOrder(StarSystem origin, StarSystem destination, TransportFleet tempFleet){
        this.origin = origin;
        this.destination = destination;
        this.fleet = tempFleet;
    }
    
    @Override
    public String toString() {
        return "Assault ["+destination.getName()+"]";
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
