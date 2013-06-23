package ca.ott.al.starmap.core.order.unit.aerospace;

import ca.ott.al.starmap.core.map.StarSystem;

public class PatrolOrder extends AerospaceOrder {

    /**
     * 
     */
    private static final long serialVersionUID = -8536608524690471597L;

    StarSystem patrolBase;
    
    public StarSystem getPatrolBase() {
        return patrolBase;
    }

    public PatrolOrder(StarSystem destinationSystem) {
        patrolBase = destinationSystem;
    }

    
    
    public String toString(){
        return "Patrol";
    }
}
