package ca.ott.al.starmap.core.unit;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import ca.ott.al.starmap.core.GameLogTool;
import ca.ott.al.starmap.core.MilitaryForceHolder;
import ca.ott.al.starmap.core.faction.Faction;
import ca.ott.al.starmap.core.order.Order;
import ca.ott.al.starmap.core.order.OrderQueue;

public class Warship extends SpaceCraft implements MilitaryForceHolder, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 15558358604666643L;
    
    private static Logger logger = Logger.getLogger(Warship.class.getName());

    private WarshipClass warshipClass;
    
    private int supplyRequirement;
    private int supplyPointsInStock;

    private double experiencePoints;
    private int leadershipLevel;

    /**
     * Rule modification: This feature is not in the rules but I judge it 
     * necessary for Clan operations where warships can carry troops
     * at low cost
     */
    protected Set<MilitaryForce> forcesEmbarked;
    
    /**
     * @param warshipClass
     * @param warshipOwner
     */
    public Warship(String name, WarshipClass warshipClass, Faction warshipOwner){
        super(name, warshipOwner);
        this.warshipClass = warshipClass;
        this.owner = warshipOwner;
        forcesEmbarked = new TreeSet<MilitaryForce>();
    }
    
    //METHODS==================================================================
    public String toString(){
        return name + " (" + warshipClass.getWarshipClassName() + ")";
    }
    
    /**
     * Embark the force if there is room aboard the Warship
     * return true if the force was embarked, false otherwise
     */
    @Override
    public boolean addMilitaryForce(MilitaryForce militaryForce) {
        double alreadyEmbarkedTotal  = 0;
        for(MilitaryForce alreadyAboard: forcesEmbarked){
            alreadyEmbarkedTotal += alreadyAboard.getAirRating();
            alreadyEmbarkedTotal += alreadyAboard.getGroundRating();
        }
        if(militaryForce.getAirRating() + militaryForce.getGroundRating() +
                alreadyEmbarkedTotal < warshipClass.getForceTransportCapacity()){
            forcesEmbarked.add(militaryForce);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeMilitaryForce(MilitaryForce militaryForce) {
        return forcesEmbarked.remove(militaryForce);
    }
    
    /**
     * Rule modification: Warship Aerospace rating is calculated from the 
     * battle value instead of the cost.
     * @return
     */
    public double getAirRating(){
        //Get value of the warship itself
        return (warshipClass.getBattleValue()/1000);
    }
    
    /**
     * Returns the forces embarked on this ship
     */
    @Override
    public Set<MilitaryForce> getMilitaryForces() {
        return forcesEmbarked;
    }
    
    /**
     * 
     */
    @Override
    public double calculateEffectiveAirStrength() {
        //Right now, this includes the ship and any forces carried. 
        //There is nothing in the rules about warships having experience or fatigue.
        //Since experience could really distort the value of the ship, it may be 
        //better to simply return the ship's rating for now.
        //
        //This method also adds the strength of embarked aerospace forces adjusted 
        //for fatigue, unit supply situation and warship's carrier capacity.
        double total = getAirRating();
        total = total * (1-calculateSupplyPenalty());
        
        total += calculateEffectiveEmbarkedFighterStrength();
        
        return total;
    }
    
    /**
     * This method calculates the strength of embarked aerospace forces adjusted 
     * for fatigue, unit supply situation and warship's carrier capacity.
     * @return
     */
    private double calculateEffectiveEmbarkedFighterStrength(){
        double total = 0;
        //Add the fighters from the embarked aerospace forces if they are aboard
        if(!forcesEmbarked.isEmpty()){
            double embarkedTotal = 0;
            for (MilitaryForce force: forcesEmbarked){
                embarkedTotal += force.calculateEffectiveAirStrength();
            }
            //control for the carrier's fighter capacity
            if(embarkedTotal > warshipClass.getCarrierCapacity()){
                total += warshipClass.getCarrierCapacity();
            } else {
                total += embarkedTotal;
            }
        }
        return total;
    }

    /**
     * Calculates the raw air strength.
     * Use this method for damage allocation purposes.
     * @return
     */
    private double calculateRawFighterStrength(){
        double total = 0;
        if(!forcesEmbarked.isEmpty()){
            for (MilitaryForce force: forcesEmbarked){
                total += force.getAirRating();
            }
        }
        return total;
    }
    
    /**
     * 
     */
    @Override
    public double calculateRawAirStrength(){
        double value = getAirRating()*(1-getDamageLevel());
        value += calculateRawFighterStrength();
        return value;
    }
    
    /**
     * 
     */
    @Override
    public void absorbAirDamage(double damage) {
        double warship = getAirRating()*(1-getDamageLevel());
        double embarkedFighters = calculateRawFighterStrength();
        double total = warship + embarkedFighters;
        String message = "";
        
        if(embarkedFighters > 0){
            double fighterDamage = damage*(embarkedFighters/total);
            for(MilitaryForce force: forcesEmbarked){
                double forceDamage = (force.getAirRating()/embarkedFighters)*fighterDamage;
                force.absorbAirDamage(forceDamage);
            }
        }
        
        if(warship > 0){
            double warshipDamage = damage*(warship/total);
            double remainingWarshipStrength = warship - warshipDamage;
            if(remainingWarshipStrength > 0){
                setDamageLevel(remainingWarshipStrength/getAirRating());
                message = "Warship: " + this.getName() + " has taken damage";
            }
            else {
                setDamageLevel(1);
                message = "Warship: " + this.getName() + " has been destroyed";
            }
        }
        GameLogTool.getGameLogTool().appendLog(message);
        logger.info(message);
    }
    
    /**
     * Stub
     */
    @Override
    public double calculateEffectiveGroundStrength() {
        return 0;
    }

    /**
     * Stub
     */
    @Override
    public double calculateRawGroundStrength() {
        return 0;
    }

    /**
     * Stub
     */
    @Override
    public void absorbGroundDamage(double damage) {
        //Stub
    }
    
    //Order Executor Methods===================================================
    @Override
    public boolean hasOrders() {
        return orderQueue.hasOrders();
    }

    @Override
    public OrderQueue getOrders() {
        return orderQueue;
    }

    @Override
    public boolean addOrder(Order order) {
        return orderQueue.add(order);
    }

    @Override
    public boolean cancelOrder(Order order) {
        return orderQueue.remove(order);
    }

    @Override
    public void clearOrders() {
        orderQueue.clear();
    }

    @Override
    public Order getNextOrder() {
        return orderQueue.getNextOrder();
    }
    
    @Override
    public void clearNextOrder() {
        orderQueue.clearNextOrder();
    }

    @Override
    public boolean addFirstOrder(Order order) {
        return orderQueue.addFirstOrder(order);
    }

    //Access methods===========================================================
    public void setWarshipOwner(Faction warshipOwner) {
        this.owner = warshipOwner;
    }

    public Faction getWarshipOwner() {
        return owner;
    }

    public double getBattleValue() {
        return warshipClass.getBattleValue() * (1 - damageLevel);
    }
    
    public WarshipClass getWarshipClass(){
        return warshipClass;
    }

    public int getSupplyPointsInStock() {
        return supplyPointsInStock;
    }

    public void setSupplyPointsInStock(int supplyPointsInStock) {
        this.supplyPointsInStock = supplyPointsInStock;
    }

    public double getExperiencePoints() {
        return experiencePoints;
    }

    public void setExperiencePoints(double experiencePoints) {
        this.experiencePoints = experiencePoints;
    }

    public int getLeadershipLevel() {
        return leadershipLevel;
    }

    public void setLeadershipLevel(int leadershipLevel) {
        this.leadershipLevel = leadershipLevel;
    }

    public int getSupplyRequirement() {
        return supplyRequirement;
    }

    public Set<MilitaryForce> getForcesEmbarked() {
        return forcesEmbarked;
    }

}
