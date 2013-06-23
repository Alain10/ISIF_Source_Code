package ca.ott.al.starmap.core.order.faction;

import ca.ott.al.starmap.core.faction.Faction;
import ca.ott.al.starmap.core.map.StarSystem;
import ca.ott.al.starmap.core.unit.GarrisonForce;
import ca.ott.al.starmap.core.unit.MilitaryForce;

public class EstablishGarrisonOrder extends FactionOrder {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private StarSystem starSystem;
    
    public EstablishGarrisonOrder(StarSystem starSystem) {
        super();
        this.starSystem = starSystem;
    }

    @Override
    public int compareTo(Object arg0) {
        String display = this.toString();
        String argument = arg0.toString();
        return display.compareToIgnoreCase(argument);
    }

    @Override
    public double getCost() {
        return 3;
    }

    @Override
    public boolean execute() {
        Faction faction = starSystem.getPrimaryPlanet().getControllingFaction();
        MilitaryForce force = new GarrisonForce(faction, starSystem.getPrimaryPlanet().getName() 
                + " Planetary Garrison", "unknown", 25, 125, 1, 6, 1,  MilitaryForce.UnitLoyalty.reliable, true);
        starSystem.getPrimaryPlanet().addMilitaryForce(force);
        return true;
    }

    @Override
    public String toString() {
        String display = "Establish a Garrison int the "+starSystem.getName()+ " Star System";
        return display;
    }

    @Override
    public int getDuration() {
        return 0;
    }

}
