package ca.ott.al.starmap.core.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import ca.ott.al.starmap.core.faction.Faction;
import ca.ott.al.starmap.core.order.factory.ProductionLine;
import ca.ott.al.starmap.core.order.factory.ProductionOrder;
import ca.ott.al.starmap.core.unit.Reinforcement;
import ca.ott.al.starmap.core.unit.ReinforcementType;
import ca.ott.al.starmap.core.unit.Warship;

public class Factory extends StarMapObject {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    Faction controllingFaction;
    protected List<ProductionLine> productionLines;

    public int getFactorySize(){
        return productionLines.size();
    }
    
    public Factory(String name, Faction controllingFaction){
        super(name);
        productionLines = new ArrayList<ProductionLine>();
        this.controllingFaction = controllingFaction;
    }
    
    public void addProductionLine(ProductionLine productionLine){
        productionLines.add(productionLine);
    }
    
    
    
    public List<ProductionLine> getProductionLines(){
        return productionLines; 
    }

    public void clear() {
        productionLines.clear();
        
    }

    public ProductionLine getNextAvailableProductionLine() {
        for(ProductionLine line : productionLines){
            if(line.getItem().getName().equals("none")){
                return line;
            }
        }
        return null;
    }
    
//    Faction controllingFaction;
//    protected Map<String, ProductionLine> productionLines;
//    
//    public Factory(String name, Faction controllingFaction){
//        super(name);
//        productionLines = new TreeMap<String, ProductionLine>();
//        this.controllingFaction = controllingFaction;
//    }
//    
//    //METHODS==================================================================
//    public void addProductionLine(String productionModel){
//        productionLines.put(productionModel, new ProductionLine());
//    }
//    
//    public Set<String> getProductionLines(){
//        return productionLines.keySet(); 
//    }
//    
//    /**
//     * Allows for the destruction of a production line
//     * @param productionModel
//     * @return
//     */
//    public boolean destroyProductionLine(String productionModel){
//        if(productionLines.keySet().contains(productionModel)){
//            productionLines.remove(productionModel);
//            return true;
//        }else{
//            return false;
//        }
//    }
//    

//    
//    /**
//     * Processes the production orders and delivers the units for this game turn
//     * @param gameTurn the current game turn
//     */
//    public void processAllProductionOrders(int gameTurn){
//        for (ProductionLine line: productionLines.values()){
//            if(line.productionLineIsBusy()){
//                for(ProductionOrder order: line.getProductionOrders()){
//                    if(order.getDeliveryTurn() == gameTurn){
//                        processOrder(order);
//                        line.removeProductionOrder(order);
//                    }
//                }
//            }
//        }
//    }
//    
//    //Takes care of processing the order
//    private void processOrder(ProductionOrder order){
//        //We are producing a general purpose military unit
//        ReinforcementType companyType = order.getUnitType();
//        if(companyType != null){
//            //In case the order includes several companies...
//            for(int i= 0; i<order.getQuantity(); i++){
//                Reinforcement company = new Reinforcement(companyType, 
//                        companyType.getBaseCombatRating(), 1);
//                order.getBeneficiaryForce().addReinforcement(company);
//            }
//        }
//        
//        //We are producing a warship
//        if (order.getWarshipType() != null){
//            StarSystem capital = controllingFaction.getFactionCapital();
//            //StellarPosition position = capital.getPosition();
//            Warship warship = new Warship("New Warship",order.getWarshipType(),
//                    controllingFaction);
//
//            capital.addUnitInSystem(warship);
//        }
//    }    

}
