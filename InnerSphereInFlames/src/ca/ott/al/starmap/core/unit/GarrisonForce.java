package ca.ott.al.starmap.core.unit;

import java.io.Serializable;

import ca.ott.al.starmap.core.faction.Faction;

public class GarrisonForce extends MilitaryForce implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -7447777185197469019L;
    
    private double maxAirRating, maxGroundRating;
    
    public GarrisonForce(Faction forceOwner, String forceName,
            String commandingOfficer, double airRating, double groundRating,
            int supplyRequirement, int supplyPointsInStock,
            double experiencePoints, UnitLoyalty loyalty, boolean unitAtHalfStrength) {
        super(forceOwner, forceName, commandingOfficer, airRating, groundRating,
                    supplyRequirement, supplyPointsInStock, experiencePoints, loyalty);
        if(unitAtHalfStrength){
            maxAirRating = airRating*2;
            maxGroundRating = groundRating*2;
        } else{
            maxAirRating = airRating;
            maxGroundRating = groundRating;
        }
    }

    /**
     * Regenerate the garrison if it has not been in combat this turn.
     * It is possible for the garrison to exceed max strength by 5%.
     * This is design intent.
     */
    public void regenerate(){
        //if damaged and not involved in combat, regenerate 5%
        if(!wasInvolvedInGroundCombatThisTurn()){
            double airRating = getAirRating();
            if(airRating < maxAirRating){
                airRating += (maxAirRating/20);
                setAirRating(airRating);
            }
            double groundRating = getGroundRating();
            if(groundRating < maxGroundRating){
                groundRating += (maxGroundRating/20);
                setGroundRating(groundRating);
            }
        }
    }
}
