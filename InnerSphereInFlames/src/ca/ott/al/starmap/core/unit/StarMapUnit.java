package ca.ott.al.starmap.core.unit;

import ca.ott.al.starmap.core.UnitOrderExecutor;
import ca.ott.al.starmap.core.faction.Faction;
import ca.ott.al.starmap.core.map.StarMapObject;
import ca.ott.al.starmap.core.map.StarSystem;
import ca.ott.al.starmap.core.order.OrderQueue;
import ca.ott.al.starmap.core.unit.MilitaryForce.UnitLoyalty;
import ca.ott.al.starmap.ui.InnerSphereInFlamesGui;

public abstract class StarMapUnit extends StarMapObject implements UnitOrderExecutor{

    /**
     * 
     */
    private static final long serialVersionUID = 6890695503593997536L;
    
    protected OrderQueue orderQueue;
    
    protected Faction owner;
    protected String commandingOfficer;
    protected int leadershipLevel;
    protected UnitLoyalty loyalty;
    
    protected int supplyRequirement;
    protected int supplyPointsInStock;
    protected int turnsWithoutSupply;
    
    protected double experiencePoints;
    
    protected double fatiguePoints;
    
    protected boolean involvedInGroundCombatThisTurn;
    protected boolean involvedInAerospaceCombatThisTurn;
    protected boolean movedThisTurn;
    protected boolean unitMustRetreat;
    protected StarSystem placeOfOriginThisTurn;
    
    public StarMapUnit(String name, Faction unitOwner){
        super(name);
        int maxOrderCount = unitOwner.getTechnology().getMaxNumberOfOrders();
        orderQueue = new OrderQueue(true, maxOrderCount);
        involvedInGroundCombatThisTurn = false;
        involvedInAerospaceCombatThisTurn = false;
        movedThisTurn = false;
        unitMustRetreat = false;
        turnsWithoutSupply = 0;
    }

    //Methods------------------------------------------------------------------
    abstract public double calculateEffectiveAirStrength();
    abstract public double calculateRawAirStrength();
    abstract public void absorbAirDamage(double damage);
    abstract public double calculateEffectiveGroundStrength();
    abstract public double calculateRawGroundStrength();
    abstract public void absorbGroundDamage(double damage);
    
    public void expendSupplies(){
        supplyPointsInStock -= supplyRequirement;
        if(supplyPointsInStock <= 0){
            supplyPointsInStock = 0;
            turnsWithoutSupply++;
        }
    }
    
    public void resupply(double supplyNeed){
        supplyPointsInStock += supplyNeed;
        turnsWithoutSupply = 0;
    }
    
    /**
     * @return the supply penalty multiplier
     */
    protected double calculateSupplyPenalty(){
        return turnsWithoutSupply*0.1;
    }
    
    /**
     * Adds the specified amount of fatigue to the unit
     */
    public void modifyFatigue(double fatigue){
        this.fatiguePoints += fatigue;
        if(fatiguePoints < 0){
            fatiguePoints = 0;
        }
        if(fatiguePoints >=8){
            fatiguePoints = 8;
        }
    }
    
    /**
     * 
     * @return the fatigue penalty multiplier
     */
    protected double calculateFatiguePenalty(){
        if (fatiguePoints < 3){
            return 0;
        } else if(fatiguePoints >= 3 && fatiguePoints < 4){
            return 0.05;
        } else if (fatiguePoints >= 4 && fatiguePoints < 5){
            return 0.15;
        } else if (fatiguePoints >= 5 && fatiguePoints < 6){
            return 0.30;
        } else if (fatiguePoints >= 6 && fatiguePoints < 7){
            return 0.50;
        } else if (fatiguePoints >= 7 && fatiguePoints < 8){
            return 0.75;
        } else if (fatiguePoints >= 8){
            return 1;
        }
        return 0;
    }
    
    
    protected double calculateExperienceMultiplier(){
        if(experiencePoints <= 5){
            return 0.8; //green
        } else if (experiencePoints > 5 && experiencePoints <=15 ){
            return 1;  //regular
        } else if (experiencePoints > 15 && experiencePoints <=30){
            return 1.5; //veteran
        } else if (experiencePoints > 30){
            return 2 + (0.1 * (Math.floor(experiencePoints-30)/5) );
        }
        return 1;
    }
    
    protected void determineLeadershipLevel(){
        if(experiencePoints <= 5){
            leadershipLevel = 2;
        } else if((experiencePoints > 5) && (experiencePoints <= 15)){
            leadershipLevel = 3;
        } else if((experiencePoints > 15) && (experiencePoints <= 30)){
            leadershipLevel = 4;
        } else if((experiencePoints > 30)){
            leadershipLevel = 5;
        }
    }
    
    public void doTraining() {
        if(fatiguePoints < 8 && supplyPointsInStock >= supplyRequirement && turnsWithoutSupply == 0){
            modifyFatigue(1);
            expendSupplies();
            if(experiencePoints < 10  ){
                experiencePoints += 0.5;
            }
        }
    }
    
    @Override
    public String toString(){
        if(this.hasOrders()){
            return this.getName() + " *";
        }
        return this.getName();
    }
    
    // ------------------------------------------------------------------------
    /**
     * @return Indicates whether the unit was involved in ground combat this turn
     */
    public boolean wasInvolvedInGroundCombatThisTurn() {
        return involvedInGroundCombatThisTurn;
    }

    public void flagInvolvedInGroundCombatThisTurn() {
        involvedInGroundCombatThisTurn = true;
    }
    
    public boolean wasInvolvedInAerospaceCombatThisTurn() {
        return involvedInAerospaceCombatThisTurn;
    }

    public void flagInvolvedInAerospaceCombatThisTurn() {
        involvedInAerospaceCombatThisTurn = true;
    }
    
    public void resetInvolvedInCombatThisTurn() {
        involvedInGroundCombatThisTurn = false;
        involvedInAerospaceCombatThisTurn = false;
    }
    
    public StarSystem getPlaceOfOriginThisTurn() {
        return placeOfOriginThisTurn;
    }

    public void setPlaceOfOriginThisTurn(StarSystem placeOfOriginThisTurn) {
        this.placeOfOriginThisTurn = placeOfOriginThisTurn;
    }

    public boolean isUnitMustRetreat() {
        return unitMustRetreat;
    }

    public void setUnitMustRetreat(boolean unitMustRetreat) {
        this.unitMustRetreat = unitMustRetreat;
    }

    //-------------------------------------------------------------------------
    public boolean isMovedThisTurn(){
        return movedThisTurn;
    }
    
    public void resetMovedThisTurn(){
        movedThisTurn = false;
    }
    
    public void flagMovedThisTurn(){
        movedThisTurn = true;
    }
    
    public int getSupplyRequirement() {
        return supplyRequirement;
    }

    public int getSupplyPointsInStock() {
        return supplyPointsInStock;
    }

    //-------------------------------------------------------------------------
    public void setLoyalty(UnitLoyalty loyalty) {
        this.loyalty = loyalty;
    }

    public UnitLoyalty getLoyalty() {
        return loyalty;
    }

    public Faction getOwner() {
        return owner;
    }

    public void setOwner(Faction owner) {
        this.owner = owner;
    }
    
    public String getCommandingOfficer() {
        return commandingOfficer;
    }

    public void setCommandingOfficer(String commandingOfficer) {
        this.commandingOfficer = commandingOfficer;
    }
    
    public int getLeadershipLevel() {
        return leadershipLevel;
    }
    
    public double getExperiencePoints() {
        return experiencePoints;
    }

    public void setExperiencePoints(double experiencePoints) {
        this.experiencePoints = experiencePoints;
    }
    
    public void setFatiguePoints(double fatiguePoints) {
        this.fatiguePoints = fatiguePoints;
    }

    public double getFatiguePoints() {
        return fatiguePoints;
    }


}
