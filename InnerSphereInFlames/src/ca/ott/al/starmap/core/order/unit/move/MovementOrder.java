package ca.ott.al.starmap.core.order.unit.move;

import ca.ott.al.starmap.core.map.StarSystem;
import ca.ott.al.starmap.core.order.Order;

public abstract class MovementOrder extends Order {

    /**
     * 
     */
    private static final long serialVersionUID = 3908412438824972648L;

    
    public abstract StarSystem getOrigin(); 

    public abstract StarSystem getDestination(); 
}
