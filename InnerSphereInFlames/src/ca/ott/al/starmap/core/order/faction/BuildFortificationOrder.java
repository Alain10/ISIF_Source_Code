package ca.ott.al.starmap.core.order.faction;

import ca.ott.al.starmap.core.map.InhabitedWorld;
import ca.ott.al.starmap.core.map.StarSystem;
import ca.ott.al.starmap.dice.Dice;

public class BuildFortificationOrder extends FactionOrder {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private int duration;
    private StarSystem starSystem;
    private boolean paidFor;
    
    public BuildFortificationOrder(StarSystem system){
        starSystem = system;
        duration = Dice.getDice().get1D6();
        paidFor = false;
    }
    
    public BuildFortificationOrder(StarSystem system, int duration){
        starSystem = system;
        this.duration = duration;
        paidFor = true;
    }
    
    @Override
    public int compareTo(Object arg0) {
        String display = this.toString();
        String argument = arg0.toString();
        return display.compareToIgnoreCase(argument);
    }

    @Override
    public double getCost() {
        if(paidFor){
            return 0;
        }
        return 10;
    }

    @Override
    public boolean execute() {
        if(duration <= 0){
            InhabitedWorld world = starSystem.getPrimaryPlanet();
            if(world.getFortificationLevel() < 4)
                world.setFortificationLevel(world.getFortificationLevel()+ 1);
            return true;
        } else {
            duration--;
            paidFor = true;
            return false;
        }
    }

    @Override
    public String toString() {
        String display = "Build Fortification in "+starSystem.getName()+ " Star System. ("
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
