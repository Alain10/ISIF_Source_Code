package ca.ott.al.starmap.core.map;

import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import ca.ott.al.starmap.core.MilitaryForceHolder;
import ca.ott.al.starmap.core.faction.Faction;
import ca.ott.al.starmap.core.unit.GarrisonForce;
import ca.ott.al.starmap.core.unit.MilitaryForce;

public class InhabitedWorld extends PlanetaryBody implements MilitaryForceHolder{

    /**
     * 
     */
    private static final long serialVersionUID = -1107630093164496288L;

    //Attributes that are specific to the Inner Sphere in Flames Game
    public enum PlanetLevel {
        ordinary, minorIndustry, majorIndustry, regionalCapital, capital
    };
    
    public enum PlanetTraffic {
        normal, commerceRaided, blockaded, isolated, disrupted 
    };
   
    private Double resourceValue;
    private Factory factory;
    private SortedSet<MilitaryForce> militaryForces;
    private int fortificationLevel;
    private double resourceDepot;
    private PlanetLevel planetLevel;
    private PlanetTraffic planetTraffic;
    
    Faction controllingFaction;
    
    /**
     * Basic constructor for Inner Sphere in Flames planet
     */
    public InhabitedWorld(String name, Double resourceValue) {
        super(name);
        this.resourceValue = resourceValue;
        militaryForces = new TreeSet<MilitaryForce>();
        setFortificationLevel(0);
        resourceDepot = 0;
        planetLevel = PlanetLevel.ordinary;
        planetTraffic = PlanetTraffic.normal;
    }
    
    //Methods==================================================================
    /**
     * Returns a clone of the list of military forces in the system
     */
    public Set<MilitaryForce> getMilitaryForces() {
        return new TreeSet<MilitaryForce>(militaryForces);
    }
    
    public boolean addMilitaryForce(MilitaryForce militaryForce){
        if(!militaryForces.contains(militaryForce))
            militaryForces.add(militaryForce);
        return true;
    }
    
    public boolean removeMilitaryForce(MilitaryForce force){
        return militaryForces.remove(force);
    }
    
    public String toISIFString(){
        StringBuffer buf = new StringBuffer();
        buf.append(getName());
        buf.append("\n");
        buf.append("\tRessource Value: ");
        buf.append(getResourceValue());
        buf.append("\n");
        buf.append("\tDescription: ");
        buf.append(getDescription());
        buf.append("\n");
        if(getFactory() != null){
            buf.append("\tFactory: ");
            buf.append(getFactory().getName());
            buf.append(" Lines: ");
            buf.append(getFactory().getFactorySize());
            buf.append("\n");
        }
        buf.append("\tMilitary Forces: \n");
        if(!getMilitaryForces().isEmpty()){
            for (MilitaryForce force : militaryForces){
                buf.append("\t\t");
                buf.append(force.toISIFString());
                buf.append("\n");
            }
        }
        return buf.toString(); 
    }
    
    public void addSuppliesToDepot(double supplies) {
        this.resourceDepot += supplies;
    }
    
    public void removeSuppliesFromDepot(double supplies) {
        this.resourceDepot -= supplies;
    }

    public String getPlanetLevelString() {
        //ordinary, minorIndustry, majorIndustry, regionalCapital, capital
        switch(planetLevel){
            case ordinary: return "Ordinary";
            case minorIndustry: return "Minor Industrial Center";
            case majorIndustry: return "Major Industrial Center";
            case regionalCapital: return "Regional Capital";
            case capital: return "National Capital";
        }
        return null;
    }
    
    public boolean hasNonGarrisonUnitsPresent() {
        for(MilitaryForce force: militaryForces){
            if(! (force instanceof GarrisonForce)){
                return true;
            }
        }
        return false;
    }
    
    //Access Methods ==========================================================
    public Factory getFactory() {
        return factory;
    }

    public void setFactory(Factory factory) {
        this.factory = factory;
    }
    
    /**
     * Special access method for the factory size.  If no factory is present, size is 0.
     * @return The number of production lines at the factory.
     */
    public int getFactorySize(){
        if(factory != null){
            return factory.getFactorySize();
        } else {
            return 0;
        }
    }
    
    public Double getResourceValue() {
        return resourceValue;
    }

    public void setResourceValue(Double resourceValue) {
        this.resourceValue = resourceValue;
    }

    public void setFortificationLevel(int fortificationLevel) {
        this.fortificationLevel = fortificationLevel;
    }

    public int getFortificationLevel() {
        return fortificationLevel;
    }

    public double getResourceDepot() {
        return resourceDepot;
    }
    
    public void setResourceDepot(double resourceDepot) {
        this.resourceDepot = resourceDepot;
    }

    public PlanetLevel getPlanetLevel() {
        return planetLevel;
    }

    public void setPlanetLevel(PlanetLevel planetLevel) {
        this.planetLevel = planetLevel;
    }

    public Faction getControllingFaction() {
        return controllingFaction;
    }

    public void setControllingFaction(Faction controllingFaction) {
        this.controllingFaction = controllingFaction;
    }

    public PlanetTraffic getPlanetTraffic() {
        return planetTraffic;
    }

    public void setPlanetTraffic(PlanetTraffic planetTraffic) {
        this.planetTraffic = planetTraffic;
    }



    
    
    
}
