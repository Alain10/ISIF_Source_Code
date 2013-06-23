package ca.ott.al.starmap.core.order.unit.combat;

import ca.ott.al.starmap.core.unit.Warship;

public class EmbarkationOrder extends CombatOrder {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Warship warship;
    
    public EmbarkationOrder(){}
    
    private EmbarkationOrder(Warship warship){
        this.warship = warship;
    }
    
    public Warship getWarship(){
        return warship;
    }
    
    public String toString(){
        return "Embark";
    }
}
