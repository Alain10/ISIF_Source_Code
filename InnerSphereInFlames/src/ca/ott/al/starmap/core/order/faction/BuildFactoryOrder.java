package ca.ott.al.starmap.core.order.faction;

import ca.ott.al.starmap.core.map.Factory;
import ca.ott.al.starmap.core.map.InhabitedWorld;
import ca.ott.al.starmap.core.map.StarSystem;
import ca.ott.al.starmap.core.order.factory.ProductionItem;
import ca.ott.al.starmap.core.order.factory.ProductionItemSingleton;
import ca.ott.al.starmap.core.order.factory.ProductionLine;
import ca.ott.al.starmap.dice.Dice;

public class BuildFactoryOrder extends FactionOrder {

    /**
     * 
     */
    private static final long serialVersionUID = -51008208344964318L;
    
    private int duration;
    private StarSystem starSystem;
    private boolean paidFor;
    
    public BuildFactoryOrder(StarSystem system){
        starSystem = system;
        duration = 12 + Dice.getDice().get2D6();
        paidFor = false;
    }
    
    public BuildFactoryOrder(StarSystem system, int time) {
        starSystem = system;
        duration = time;
        paidFor = true;
    }

    @Override
    public int compareTo(Object o) {
        String display = this.toString();
        String argument = o.toString();
        return display.compareToIgnoreCase(argument);
    }

    @Override
    public double getCost() {
        if(paidFor){
            return 0;
        }
        return 1000;
    }

    @Override
    public boolean execute() {
        if(duration <= 0){
            InhabitedWorld world = starSystem.getPrimaryPlanet();
            if(world.getFactorySize() > 0){
                world.getFactory().addProductionLine(new ProductionLine(
                        ProductionItemSingleton.getProductionItemSingleton().getAvailableItems().get(0)));
            } else {
                world.setFactory(new Factory("New Factory", world.getControllingFaction()));
                world.getFactory().addProductionLine(new ProductionLine(
                        ProductionItemSingleton.getProductionItemSingleton().getAvailableItems().get(0)));
            }
            return true;
        } else {
            duration--;
            paidFor = true;
            return false;
        }
    }

    @Override
    public String toString() {
        String display = "Build Factory in "+starSystem.getName()+ " Star System. ("
                + duration +" turns remaining)";
        return display;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    public String getSystemName() {
        return starSystem.getName();
    }

}
