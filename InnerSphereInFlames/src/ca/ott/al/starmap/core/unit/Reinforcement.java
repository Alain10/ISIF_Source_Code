package ca.ott.al.starmap.core.unit;

import java.io.Serializable;

import ca.ott.al.starmap.core.order.factory.ProductionItem;

public class Reinforcement implements Serializable{

    ProductionItem item;
    
    /**
     * 
     */
    private static final long serialVersionUID = 3953150297796646246L;
    
    public Reinforcement(ProductionItem item) {
        this.item = item;
    }

    public ProductionItem getItem() {
        return item;
    }
    
    public String toString(){
        return item.getName();
    }
    

//    private ReinforcementType companyType;
//    
//    protected int combatRating;
//    protected double experiencePoints;
//    
//    public Reinforcement(ReinforcementType companyType, int combatRating, 
//            double experiencePoints){
//        this.companyType = companyType;
//        setCombatRating(combatRating);
//        setExperiencePoints(experiencePoints);
//    }
//
//    public int getCombatRating() {
//        return combatRating;
//    }
//
//    public void setCombatRating(int combatRating) {
//        this.combatRating = combatRating;
//    }
//
//    public void setCompanyType(ReinforcementType companyType) {
//        this.companyType = companyType;
//    }
//
//    public ReinforcementType getCompanyType() {
//        return companyType;
//    }
//
//    public double getExperiencePoints() {
//        return experiencePoints;
//    }
//
//    public void setExperiencePoints(double experiencePoints) {
//        this.experiencePoints = experiencePoints;
//    }
}
