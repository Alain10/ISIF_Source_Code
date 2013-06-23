package ca.ott.al.starmap.core.unit;

import java.io.Serializable;

import ca.ott.al.starmap.core.faction.Faction;

public abstract class SpaceCraft extends StarMapUnit implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 398741660145197660L;

    public enum SpaceCraftStatus {
        funtional, nonFunctional
    }

    public SpaceCraft(String name, Faction spaceCraftOwner){
        super(name, spaceCraftOwner);
    }
    
    /**
     * Expressed as fraction from 0 to 1.  0 is undamaged, 1 is destroyed
     */
    protected double damageLevel = 0;
    protected SpaceCraftStatus status = SpaceCraftStatus.funtional;
    

    public void setDamageLevel(double damageLevel) {
        this.damageLevel = damageLevel;
    }

    public double getDamageLevel() {
        return damageLevel;
    }

    public void setStatus(SpaceCraftStatus status) {
        this.status = status;
    }

    public SpaceCraftStatus getStatus() {
        return status;
    }
}
