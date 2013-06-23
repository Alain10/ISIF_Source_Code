package ca.ott.al.starmap.core.unit;

import java.io.Serializable;
import java.text.DecimalFormat;

import org.apache.log4j.Logger;

import ca.ott.al.starmap.core.GameLogTool;
import ca.ott.al.starmap.core.faction.Faction;
import ca.ott.al.starmap.core.order.Order;
import ca.ott.al.starmap.core.order.OrderQueue;
import ca.ott.al.starmap.core.order.unit.combat.AssaultLandingOrder;
import ca.ott.al.starmap.core.order.unit.combat.LandingOrder;
import ca.ott.al.starmap.core.unit.ReinforcementType.Environment;
import ca.ott.al.starmap.ui.StarMapPanel;

/**
 * In ISIF, a military force is nominally a mech regiment with its associated 
 * units.  It can also be an all aerospace unit.  If such is the case, the unit
 * need not be attached to a warship but it could be.  Planetary garrisons are
 * also instances of MilitaryForce. 
 * @author Alain
 *
 */
public class MilitaryForce extends StarMapUnit implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = -1539658866880887123L;

    private static Logger logger = Logger.getLogger(MilitaryForce.class.getName());
    
    public enum UnitLoyalty {fanatical, reliable, questionable}
    
    private double airRating;
    private double groundRating;
    
    

    public MilitaryForce(Faction forceOwner, String forceName, 
            String commandingOfficer, double airRating, double groundRating,
            int supplyRequirement, int supplyPointsInStock, 
            double experiencePoints, UnitLoyalty loyalty) {
        super(forceName, forceOwner);
        this.owner = forceOwner;
        this.commandingOfficer = commandingOfficer;
        this.airRating = airRating;
        this.groundRating = groundRating;
        this.supplyRequirement = supplyRequirement;
        this.supplyPointsInStock = supplyPointsInStock;
        this.experiencePoints = experiencePoints;
        this.setLoyalty(loyalty);
        
        this.setFatiguePoints(0);
        involvedInGroundCombatThisTurn = false;
        determineLeadershipLevel();
    }

    //Force Methods============================================================
    /**
     * return the effective aerospace strength of this unit modified for fatigue,
     * supply and orders if applicable
     */
    @Override
    public double calculateEffectiveAirStrength() {
        double fatiguePenalty = calculateFatiguePenalty();
        double supplyPenalty = calculateSupplyPenalty();
        double totalPenalty = fatiguePenalty + supplyPenalty;
        if(totalPenalty > 1){
            totalPenalty = 1;
        }
        //Reduce the unit strength by supply and fatigue penalty
        double effectiveStrength = airRating - (airRating * totalPenalty);
        //Modify unit strength by experience multiplier
        effectiveStrength = effectiveStrength * calculateExperienceMultiplier();
        
        //modify effective air strength for technology 
        double techMultiplier = owner.getTechnology().getAerospaceModifier();
        effectiveStrength *= techMultiplier;
        
        //If the unit is repairing, halve its strength 
        if(orderQueue.containsRepair()){
            effectiveStrength = effectiveStrength * 0.5;
        }
        
        return effectiveStrength;
    }
    
    @Override
    public void absorbAirDamage(double damage) {
        if(orderQueue.containsRepair()){
            setAirRating(getAirRating() - (damage*2));
        } else {
            setAirRating(getAirRating() - damage);
        }
        
        if((getAirRating() < 0)){
            if (unitIsLanding()){
                //At this point, the air strength is in the negative.
                //This has a consequence if the unit is landing.
                //Rule modification: can't do 4% for every 10 points since I don't have the info!
                //Besides, the rule does not make sense for huge fights.
                //If the overall effect is that 100 point of Aerospace strength should cause 
                //40% casualties to a 500 point ground force... A possible compromise rule would 
                //be the ground force looses 2*EnemyEffectiveAerospaceStrength.
                //For now, allocate twice the remaining damage to ground forces
                //Note : at this point the air rating is a negative integer that contains the 
                //amount of damage to be transferred
                absorbGroundDamage(getAirRating()*-2);
            }
            setAirRating(0);
        }
        
        DecimalFormat df = new DecimalFormat("##.#");
        String message = "            "+getName()+" has lost "+df.format(damage)+" aerospace points.";
        GameLogTool.getGameLogTool().appendLog(message);
        logger.info(message);
    }

    @Override
    public double calculateRawAirStrength() {
        return airRating;
    }
    
    public void absorbGroundDamage(double damage) {
        double adjustedDamage;
        //Check if the unit has a repair order
        if(orderQueue.containsRepair()){
            adjustedDamage = damage*2;
        } else {
            adjustedDamage = damage;
        }
        
        //check if the unit is dug in 
        if(getOrders().containsDigIn()){
            adjustedDamage *= 0.75;
        }
        
        setGroundRating(getGroundRating() - adjustedDamage);
        
        DecimalFormat df = new DecimalFormat("##.#");
        String message = "            "+getName()+" has lost "+df.format(adjustedDamage)+" ground points.";
        GameLogTool.getGameLogTool().appendLog(message);
        logger.info(message);
        
        if(getGroundRating() < 0){
            setGroundRating(0);
        }
    }
    
    @Override
    public double calculateEffectiveGroundStrength() {
        double fatiguePenalty = calculateFatiguePenalty();
        double supplyPenalty = calculateSupplyPenalty();
        double totalPenalty = fatiguePenalty + supplyPenalty;
        if(totalPenalty > 1){
            totalPenalty = 1;
        }
        //Reduce the unit strength by supply and fatigue penalty
        double effectiveStrength = groundRating - (groundRating * totalPenalty);
        //Modify unit strength by experience multiplier
        effectiveStrength = effectiveStrength * calculateExperienceMultiplier();
        
        //modify effective air strength for technology 
        double techMultiplier = owner.getTechnology().getGroundModifier();
        effectiveStrength *= techMultiplier;
        
        //If the unit is repairing, halve its strength 
        if(orderQueue.containsRepair()){
            effectiveStrength = effectiveStrength * 0.5;
        }
        return effectiveStrength;
    }

    @Override
    public double calculateRawGroundStrength() {
        return groundRating;
    }
    

    
    /**
     * Add a company of reinforcements to the force
     * @param company
     */
    public void addReinforcement(Reinforcement company) {
        //First prepare to calculate the experience level of the merged force
        double reinforcementFactor = 0;
        double veteranFactor = (getAirRating() + getGroundRating()) * getExperiencePoints();
        
        //Next, add the new forces 
        airRating += company.getItem().getAir();
        groundRating += company.getItem().getGround();
        
        //Finish calculating the experience level of the merged force
        double mergedXP = (reinforcementFactor + veteranFactor) / (airRating + groundRating);
        setExperiencePoints(mergedXP);
        determineLeadershipLevel();
    }
    

    public String toISIFString() {
        // Compile a String to display this force
        StringBuffer buf = new StringBuffer();
        buf.append("\t");
        buf.append(getName());
        buf.append("\tAIR: ");
        buf.append(getAirRating());
        buf.append("\tGND: ");
        buf.append(getGroundRating());
        buf.append("\tSP: ");
        buf.append(getSupplyRequirement());
        buf.append("/");
        buf.append(getSupplyPointsInStock());
        buf.append("\tLD: ");
        buf.append(getLeadershipLevel());
        buf.append("\tXP: ");
        buf.append(getExperiencePoints());
        return buf.toString();
    }
    
    /**
     * Determine if the unit is in the midst of a landing
     * @return
     */
    private boolean unitIsLanding(){
        Order order = orderQueue.getNextOrder();
        if(order instanceof LandingOrder || order instanceof AssaultLandingOrder){
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Determines the supply requirement of a force dynamically
     */
    @Override
    public int getSupplyRequirement() {
        double forceTotal = getAirRating() + getGroundRating();
        return (int)Math.ceil(forceTotal/1000);
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
    

    public double getAirRating() {
        return airRating;
    }

    public double getGroundRating() {
        return groundRating;
    }

    public void setAirRating(double airRating) {
        this.airRating = airRating;
    }

    public void setGroundRating(double groundRating) {
        this.groundRating = groundRating;
    }

}
