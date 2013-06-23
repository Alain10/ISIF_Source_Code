package ca.ott.al.starmap.core.order.faction;

import ca.ott.al.starmap.core.map.InhabitedWorld;
import ca.ott.al.starmap.core.map.StarSystem;

public class BuildDepotOrder extends FactionOrder {

    /**
     * 
     */
    private static final long serialVersionUID = -291535354596787185L;
    
    private double depotCost;
    private StarSystem starSystem;
    
    public BuildDepotOrder(StarSystem starSystem, int value){
        depotCost = value;
        this.starSystem = starSystem;
    }
    
    @Override
    public double getCost() {
        return depotCost;
    }

    @Override
    public boolean execute() {
        InhabitedWorld world = starSystem.getPrimaryPlanet();
        world.addSuppliesToDepot(depotCost);
        return true;
    }

    @Override
    public String toString() {
        String display = "Build up Depot in "+starSystem.getName()+ " Star System by "
                + depotCost +" supply points";
        return display;
    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int compareTo(Object arg0) {
        String display = this.toString();
        String argument = arg0.toString();
        return display.compareToIgnoreCase(argument);
    }

}
