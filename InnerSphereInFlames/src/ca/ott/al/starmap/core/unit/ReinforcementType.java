package ca.ott.al.starmap.core.unit;

import java.io.Serializable;

public class ReinforcementType implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = -8553776478670571179L;

    public enum Environment{aerospace, ground};
    
    protected String typeDescription;
    protected Environment environment;
    protected double resourceCost;
    protected int baseCombatRating;
    
    public ReinforcementType(String typeDescription, Environment environment,
            double resourceCost, int baseCombatRating) {
        super();
        this.typeDescription = typeDescription;
        this.environment = environment;
        this.resourceCost = resourceCost;
        this.baseCombatRating = baseCombatRating;
    }

    public String getTypeDescription() {
        return typeDescription;
    }

    public void setTypeDescription(String typeDescription) {
        this.typeDescription = typeDescription;
    }

    public double getResourceCost() {
        return resourceCost;
    }

    public void setResourceCost(double resourceCost) {
        this.resourceCost = resourceCost;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public int getBaseCombatRating() {
        return baseCombatRating;
    }

    public void setBaseCombatRating(int baseCombatRating) {
        this.baseCombatRating = baseCombatRating;
    }


    
    
}
