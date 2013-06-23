package ca.ott.al.starmap.core.order.factory;

import java.util.ArrayList;
import java.util.List;

public class ProductionItemSingleton {

    private ProductionItemSingleton(){}
    
    private static ProductionItemSingleton singleton;
    
    public static ProductionItemSingleton getProductionItemSingleton(){
        if(singleton == null){
            singleton = new ProductionItemSingleton();
            singleton.initializeProductionItems();
        }
        return singleton;
    }
    
    /**
     * This is for a generic list of production items.
     * Later, this functionality should be moved to the individual factions.
     */
    private List<ProductionItem> availableItems = new ArrayList<ProductionItem>();
    
    /**
     * This Method MUST be called before the game gets started
     */
    public void initializeProductionItems(){
        ProductionItem none = new ProductionItem("none", 0, 0, 0, 1);
        availableItems.add(none);
        
        initializeDefault3039Items();

    }
    
    private void initializeDefault3039Items(){
        ProductionItem lightMech = new ProductionItem("Light Mech Btn.", 1.8, 30, 120, 1);
        ProductionItem mediumMech = new ProductionItem("Medium Mech Btn.", 3.6, 40, 180, 2);
        ProductionItem heavyMech = new ProductionItem("Heavy Mech Btn.", 6.6, 40, 210, 3);
        ProductionItem assaultMech = new ProductionItem("Assault Mech Btn.", 7.5, 50, 230, 3);
        
        availableItems.add(lightMech);
        availableItems.add(mediumMech);
        availableItems.add(heavyMech);
        availableItems.add(assaultMech);
        
        ProductionItem lightVehicle = new ProductionItem("Light Armor Btn.", 0.6, 0, 30, 1);
        ProductionItem mediumVehicle = new ProductionItem("Medium Armor Btn.", 1.2, 0, 60, 1);
        ProductionItem heavyVehicle = new ProductionItem("Heavy Armor Btn.", 2.1, 0, 90, 1);
        ProductionItem assaultVehicle = new ProductionItem("Assault Armor Btn.", 3.6, 0, 120, 2);
        
        availableItems.add(lightVehicle);
        availableItems.add(mediumVehicle);
        availableItems.add(heavyVehicle);
        availableItems.add(assaultVehicle);
        
        ProductionItem footInfantry = new ProductionItem("Foot Infantry Reg.", 0.9, 0, 27, 1);
        ProductionItem mechInfantry = new ProductionItem("Mech. Infantry Reg.", 1.2, 0, 48, 1);
        ProductionItem motorInfantry = new ProductionItem("Motor Infantry Reg.", 1.1, 0, 41, 1);
        ProductionItem jumpInfantry = new ProductionItem("Jump Infantry Reg.", 1.5, 60, 27, 1);
        
        availableItems.add(footInfantry);
        availableItems.add(mechInfantry);
        availableItems.add(motorInfantry);
        availableItems.add(jumpInfantry);
        
        ProductionItem artillery = new ProductionItem("Artillery Comp.", 0.5, 0, 23, 1);
        
        availableItems.add(artillery);
        
        ProductionItem airWing = new ProductionItem("Air Wing", 1.9, 120, 0, 1);
        
        availableItems.add(airWing);
    }
    
    private void initializeDefaultItems(){
        ProductionItem lightMech = new ProductionItem("Light Mech Company (3025)", 0.5, 0, 40, 1);
        availableItems.add(lightMech);
        ProductionItem mediumMech = new ProductionItem("Medium Mech Company (3025)", 1, 0, 60, 1);
        availableItems.add(mediumMech);
        ProductionItem heavyMech = new ProductionItem("Heavy Mech Company (3025)", 2, 0, 70, 1);
        availableItems.add(heavyMech);
        ProductionItem assaultMech = new ProductionItem("Assault Mech Company (3025)", 2.5, 0, 90, 1);
        availableItems.add(assaultMech);
        
        ProductionItem lightVehicle = new ProductionItem("Light Armor Company (3025)", 0.2, 0, 10, 1);
        availableItems.add(lightVehicle);
        ProductionItem mediumVehicle = new ProductionItem("Medium Armor Company (3025)", 0.4, 0, 20, 1);
        availableItems.add(mediumVehicle);
        ProductionItem heavyVehicle = new ProductionItem("Heavy Armor Company (3025)", 0.7, 0, 30, 1);
        availableItems.add(heavyVehicle);
        ProductionItem assaultVehicle = new ProductionItem("Assault Armor Company (3025)", 1.2, 0, 40, 1);
        availableItems.add(assaultVehicle);
        
        ProductionItem infantryRegiment = new ProductionItem("Infantry Regiment", 0.9, 0, 27, 1);
        availableItems.add(infantryRegiment);
        
        ProductionItem convFighter = new ProductionItem("Conventional Fighter Squadron", 0.5, 40, 0, 1);
        availableItems.add(convFighter);
        
        ProductionItem lightFighter = new ProductionItem("IS Light Fighter Squadron (3025)", 0.3, 30, 0, 1);
        availableItems.add(lightFighter);
        ProductionItem mediumFighter = new ProductionItem("IS Medium Fighter Squadron (3025)", 0.6, 40, 0, 1);
        availableItems.add(mediumFighter);
        ProductionItem heavyFighter = new ProductionItem("IS Heavy Fighter Squadron (3025)", 1, 50, 0, 1);
        availableItems.add(heavyFighter);
        
    }
    
    private void initializeEnneariItems(){
        ProductionItem eMediumMech = new ProductionItem("Enneari Medium Mech Company", 2, 0, 95, 1);
        availableItems.add(eMediumMech);
        ProductionItem eHeavyMech = new ProductionItem("Enneari Heavy Mech Company", 3, 0, 120, 1);
        availableItems.add(eHeavyMech);
        ProductionItem eAssaultMech = new ProductionItem("Enneari Assault Mech Company", 4.5, 0, 140, 2);
        availableItems.add(eAssaultMech);

        ProductionItem elightFighter = new ProductionItem("Enneari Light Fighter Squadron", 0.5, 45, 0, 1);
        availableItems.add(elightFighter);
        ProductionItem emediumFighter = new ProductionItem("Enneari Medium Fighter Squadron", 1, 75, 0, 1);
        availableItems.add(emediumFighter);
        ProductionItem eheavyFighter = new ProductionItem("Enneari Heavy Fighter Squadron", 1.5, 90, 0, 1);
        availableItems.add(eheavyFighter);
        
    }
    
    public List<ProductionItem> getAvailableItems(){
        return availableItems;
    }

    public void addItem(ProductionItem item) {
        availableItems.add(item);
    }
    
    public void clear(){
        availableItems.clear();
    }

    public ProductionItem getProductionItemByName(String itemName) {
        for(ProductionItem item : availableItems){
            if(item.getName().equals(itemName)){
                return item;
            }
        }
        return null;
    }
}
