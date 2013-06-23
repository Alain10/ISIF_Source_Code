package ca.ott.al.starmap.core.unit;

import java.io.Serializable;

import ca.ott.al.starmap.exception.InvalidParameterValueException;

public class WarshipClass extends JumpshipClass implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -3620148048700782557L;
    
    private String warshipClassName;
    private int battleValue;
    private long cost;
    
    //New feature, this is to enable the warship to transport troops.
    //The idea is that large transport like the Potemkin class or Aegis
    //class should be able to transport about 1000 ground points for
    //every 50000 tons of cargo space aboard.  Clan ships ought to be
    //slightly more efficient on the assumption that they are transporting
    //clan troops.  The details are left to the warship designer.
    private int forceTransportCapacity;
    
    //New feature, some warship are designed as carriers such as the Thera and
    //Conqueror classes.  These ships differ from others in that if they are
    //transporting a force with a lot of aerospace fighters, they can deploy
    //a substantial proportion of it from the warship in aerospace combat.
    //The carrierCapacity sets the limit.  Any aerospace assets beyond the
    //limit are considered to be cargo like ground forces and cannot be used
    //in aerospace combat until the ground force lands.
    //carrierCapacity overlaps with forceTransportCapacity and hence
    //forceTransportCapacity is always greater than carrierCapacity
    private int carrierCapacity;

    public WarshipClass(String warshipClassName, int battleValue, long cost, 
            int forceTransportCapacity, int carrierCapacity) 
            throws InvalidParameterValueException{
        this.warshipClassName = warshipClassName;
        this.battleValue = battleValue;
        this.cost = cost;
        this.forceTransportCapacity = forceTransportCapacity;
        if(carrierCapacity <= forceTransportCapacity){
            this.carrierCapacity = carrierCapacity;
        }else
            throw new InvalidParameterValueException("carrierCapacity must be"
                    +" smaller than or equal to forceTransportCapacity");
    }
    
    //ACCESSORS================================================================
    
    public int getBattleValue() {
        return battleValue;
    }

    public String getWarshipClassName() {
        return warshipClassName;
    }

    public long getCost() {
        return cost;
    }

    public int getForceTransportCapacity() {
        return forceTransportCapacity;
    }
    
    public int getCarrierCapacity(){
        return carrierCapacity;
    }

}
