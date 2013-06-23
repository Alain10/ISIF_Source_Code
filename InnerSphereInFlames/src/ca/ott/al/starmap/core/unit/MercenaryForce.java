package ca.ott.al.starmap.core.unit;

import java.io.Serializable;

import ca.ott.al.starmap.core.faction.Faction;

public class MercenaryForce extends MilitaryForce implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 6089779913640977387L;

    public MercenaryForce(Faction forceOwner, String forceName,
            String commandingOfficer, double airRating, double groundRating,
            int supplyRequirement, int supplyPointsInStock,
            double experiencePoints, UnitLoyalty loyalty) {
        super(forceOwner, forceName, commandingOfficer, airRating, groundRating,
                supplyRequirement, supplyPointsInStock, experiencePoints, loyalty);
    }

}
