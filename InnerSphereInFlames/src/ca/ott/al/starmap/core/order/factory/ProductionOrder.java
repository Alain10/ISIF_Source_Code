package ca.ott.al.starmap.core.order.factory;

import java.io.Serializable;

import ca.ott.al.starmap.core.unit.MilitaryForce;
import ca.ott.al.starmap.core.unit.ReinforcementType;
import ca.ott.al.starmap.core.unit.WarshipClass;

/**
 * Type to keep track of production orders on one production line
 */
public class ProductionOrder implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = -262051004571564446L;
    
    private int deliveryTurn; //Most types of units take 1 turn
    private int quantity;
    private ReinforcementType unitType = null;
    private MilitaryForce beneficiaryForce = null;
    private WarshipClass warshipType = null;

    /**
     * create production order for general unit
     * @param unitType
     * @param beneficiaryForce
     * @param deliveryTurn
     */
    public ProductionOrder(ReinforcementType unitType, int quantity, 
            MilitaryForce beneficiaryForce, int deliveryTurn){
        this.unitType = unitType;
        this.quantity = quantity;
        this.beneficiaryForce = beneficiaryForce;
        this.deliveryTurn = deliveryTurn;
    }

    /**
     * create a production order for a warship
     * @param warshipType
     * @param deliveryTurn
     */
    public ProductionOrder(WarshipClass warshipType, int deliveryTurn){
        this.warshipType = warshipType;
        this.deliveryTurn = deliveryTurn;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getDeliveryTurn() {
        return deliveryTurn;
    }

    public ReinforcementType getUnitType() {
        return unitType;
    }

    public MilitaryForce getBeneficiaryForce() {
        return beneficiaryForce;
    }

    public WarshipClass getWarshipType() {
        return warshipType;
    }
}
