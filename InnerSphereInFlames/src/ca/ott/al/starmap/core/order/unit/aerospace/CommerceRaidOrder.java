package ca.ott.al.starmap.core.order.unit.aerospace;

import ca.ott.al.starmap.core.map.StarSystem;

public class CommerceRaidOrder extends AerospaceOrder {

    /**
     * 
     */
    private static final long serialVersionUID = -3119140600299558208L;

    StarSystem raidingBase;
    
    public StarSystem getRaidingBase() {
        return raidingBase;
    }

    public CommerceRaidOrder(StarSystem destinationSystem) {
        this.raidingBase = destinationSystem;
    }

    
    public String toString(){
        return "Commerce Raid";
    }

}
