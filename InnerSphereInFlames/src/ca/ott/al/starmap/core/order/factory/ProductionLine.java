package ca.ott.al.starmap.core.order.factory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ca.ott.al.starmap.core.unit.MilitaryForce;
import ca.ott.al.starmap.core.unit.ReinforcementType;
import ca.ott.al.starmap.core.unit.WarshipClass;

public class ProductionLine implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static final int PER_TURN_RESOURCE_MAX =3;
    
    private ProductionItem item;
    private double turnsRemaining;
    
    public ProductionLine(ProductionItem item){
        this.item = item;
    }

    public ProductionItem getItem() {
        return item;
    }

    public void setItem(ProductionItem item) {
        this.item = item;
    }

    public double getTurnsRemaining() {
        return turnsRemaining;
    }

    public void setTurnsRemaining(double turnsRemaining) {
        this.turnsRemaining = turnsRemaining;
    }
    
    
}
    
    
    
    
//    //I was tempted to use a queue, but it is not so important...
//    private List<ProductionOrder> orders = new ArrayList<ProductionOrder>();
//    
//    /** 
//     * @deprecated
//     * Add a production order for any type of ground or aerospace unit other 
//     * than warship.
//     * Rule modification: orders that cost more than PER_TURN_RESOURCE_MAX (3 RP)
//     * are delivered cost/PER_TURN_RESOURCE_MAX turns later.
//     * Rule modification: all the output of one production line in one turn must
//     * go to the same MilitaryForce.
//     * @param unitType The type of unit
//     * @param quantity The number of units to be built, keep this number low
//     * for timely delivery
//     * @param beneficiaryForce The force that will benefit from new equipment
//     * @param orderGameTurn The turn on which the order was issued
//     * @return the resource cost 
//     */
//    public double addProductionOrder(ReinforcementType unitType, int quantity, 
//            MilitaryForce beneficiaryForce, int orderGameTurn){
//        int deliveryGameTurn = orderGameTurn + 1;
//        
//        if(unitType.getResourceCost()* quantity > PER_TURN_RESOURCE_MAX){
//            double delay =((unitType.getResourceCost()* quantity)
//                    /PER_TURN_RESOURCE_MAX);
//            Double adjustedDelay = Math.ceil(delay);
//            deliveryGameTurn = orderGameTurn + adjustedDelay.intValue();
//        }
//        ProductionOrder order = new ProductionOrder(unitType, quantity,
//                beneficiaryForce, deliveryGameTurn);
//        orders.add(order);
//        
//        return unitType.getResourceCost()* quantity;
//    }
//    
//    /**
//     * @deprecated
//     * Adds a production order for a warship
//     * Rule modification: warship RP cost is calculated from cost/100000000
//     * and not battle value/1000.
//     * Warships become available BV/10000 turns after they are ordered.
//     * @param warshipClass The type of warship
//     * @param orderGameTurn The turn on which the order was issued
//     * @return the cost in RPs to be subtracted from faction account
//     */
//    public double addProductionOrder(WarshipClass warshipClass, int orderGameTurn){
//        
//        int deliveryGameTurn = orderGameTurn + warshipClass.getBattleValue()/10000;
//        ProductionOrder order = new ProductionOrder(warshipClass, deliveryGameTurn);
//        orders.add(order);
//        
//        //I hope this auto boxing works
//        Long tempCost = warshipClass.getCost()/100000000;
//        int rpCost = tempCost.intValue();
//        return rpCost;
//    }
//    
//    public List<ProductionOrder> getProductionOrders(){
//        return orders;
//    }
//    
//    public void removeProductionOrder(ProductionOrder order){
//        orders.remove(order);
//    }
//    
//    /**
//     * A production line will be considered busy if it has an order.
//     * @return
//     */
//    public boolean productionLineIsBusy(){
//        if(orders.isEmpty()){
//            return false;
//        }else{
//            return true;
//        }
//    }

