package ca.ott.al.starmap.core.transport;

import java.io.Serializable;

import ca.ott.al.starmap.core.faction.Faction;
import ca.ott.al.starmap.core.unit.MilitaryForce;

/**
 * This class will represent a faction's transport pool.  The rough plan is to
 * use this class as a general portal for access to transport resources.
 * If transport pool points are available, those will be used first.  The class
 * will make the Faction pay for civilian transport it the transport pool is
 * exhausted.  Furthermore, upkeep costs for the transport pool will be 
 * calculated and paid during the economic phase.
 * 
 * @author Alain
 *
 */
public class TransportPool implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 702567378392858032L;

    private Faction poolOwner;
    
    private int totalTransportPoints = 0;
    private int transportPointsAvailable = 0;
    
    private double transportSpendingThisTurn = 0;
    
    public TransportPool(Faction owner, int initialTransportPoint){
        poolOwner = owner;
        totalTransportPoints = initialTransportPoint;
        transportPointsAvailable = initialTransportPoint;
    }
    
    /**
     * Obtains a transport fleet of appropriate size for a military force.
     * If RP costs must be paid, they are deducted from the Faction's RP bank.
     * @param militaryForce
     * @return A transport fleet of appropriate size. null if no transport
     * can be obtained.
     */
    public TransportFleet obtainTransportFleet(MilitaryForce militaryForce){
        double totalForceRating = militaryForce.getAirRating() 
                + militaryForce.getGroundRating();
        double requiredTransport = totalForceRating/1000;
        
        int pointsRequired = (int)Math.ceil(requiredTransport);
        
        if(transportPointsAvailable >= pointsRequired){
            transportPointsAvailable -= pointsRequired;
            TransportFleet fleet = new TransportFleet(pointsRequired);
            fleet.addMilitaryForce(militaryForce);
            return fleet;
        } else if(poolOwner.getEconomy().calculateAvailableResourcePoints()
                >= pointsRequired){
            //Record the cost of hiring civilian transport
            this.transportSpendingThisTurn += pointsRequired;
            TransportFleet fleet = new TransportFleet(pointsRequired);
            fleet.addMilitaryForce(militaryForce);
            return fleet;
        }   
        return null;
    }
    
    /**
     * 
     * @param transportFleet
     */
    public void releaseTransportFleet(TransportFleet transportFleet) {
        if(transportPointsAvailable + transportFleet.getTransportPoints() <= totalTransportPoints){
            transportPointsAvailable += transportFleet.getTransportPoints();
        } else {
            transportSpendingThisTurn -= transportFleet.getTransportPoints();
        }
        
    }
    
    /**
     * Get the monthly transport pool support cost
     * @return
     */
    private double getPoolSupportCost(){
        return totalTransportPoints/10;
    }
    
    /**
     * Adds a point to the transport pool
     * @return
     */
    public boolean createTransportPoint(){
        double bank = poolOwner.getEconomy().calculateAvailableResourcePoints();
        if(bank >= 5){
            totalTransportPoints++;
            transportPointsAvailable++;
            bank -= 5;
            transportSpendingThisTurn +=5;
            return true;
        }
        return false;
    }
    
    public boolean scrapTransportPoint(){
        if(totalTransportPoints > 0 && transportPointsAvailable >0){
            totalTransportPoints--;
            transportPointsAvailable--;
        }
        return false;
    }
    
    /**
     * This method must be called once at the end of each turn when all forces
     * are on a world or a warship.  It resets the transport points and
     * resets the transport pool costs.
     */
    public void resetPoolEndOfTurn(){
        transportPointsAvailable = totalTransportPoints;
        transportSpendingThisTurn = 0 + getPoolSupportCost();
    }
    
    public double getTransportSpendingThisTurn(){
        return transportSpendingThisTurn;
    }

    public int getTotalTransportPoints() {
        return totalTransportPoints;
    }
    
    public int getTransportPointsAvailable() {
        return transportPointsAvailable;
    }

}
