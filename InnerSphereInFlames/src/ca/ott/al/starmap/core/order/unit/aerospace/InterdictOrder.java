package ca.ott.al.starmap.core.order.unit.aerospace;

import ca.ott.al.starmap.core.map.StarSystem;

public class InterdictOrder extends AerospaceOrder {

    /**
     * 
     */
    private static final long serialVersionUID = -658564609434271338L;

    StarSystem interdictedSystem;
    
    public StarSystem getInterdictedSystem() {
        return interdictedSystem;
    }

    public InterdictOrder(StarSystem destinationSystem) {
        interdictedSystem = destinationSystem;
    }

    public String toString(){
        return "Interdict";
    }

}
