package ca.ott.al.starmap.core.map;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import ca.ott.al.starmap.core.unit.StarMapUnit;
import ca.ott.al.starmap.core.unit.Warship;
import ca.ott.al.starmap.core.util.StellarPosition;

public class StarSystem extends StarMapObject{

    /**
     * 
     */
    private static final long serialVersionUID = -940341976546930240L;

    private StellarPosition position;

    /**
     * Usually 1 star for inhabited systems, but 1/3 of stars in
     * milky way are binary or multiple-star so... don't know.
     * convention: the star with index 1 is the most massive and it
     * defines the game characteristics of the system
     */
    Map<Integer, Star> stars;

    /**
     *  Probably only the inhabited planets are of interest.
     *  Note this can include asteroids and other special places.
     *  For ISIF, only create the valuable planets. 
     *  Moons are treated as a planet.
     */
    Map<Integer, InhabitedWorld> planets;
    
    /**
     * Once populated, this Map holds references to all inhabited star systems
     * within 30 light years.  This data is essential for plotting unit 
     * movements.
     */
    Map<StarSystem, Double> systemsWithin30LY;

    /**
     * Once populated, this Set holds references to all inhabited star systems
     * within 60 light years.  This data is useful for plotting unit 
     * movements as well as implementing convoy warfare. 
     */
    Map<StarSystem, Double> systemsWithin60LY;
    
    /**
     * This attribute added for ISIF.
     */
    SortedSet<StarMapUnit> unitsInSystem;
    
    boolean conflictInSystem = false;
    
    /**
     * The full constructor
     * Rule: the first star in the list should be the most massive one.
     * @param position
     * @param starList
     * @param planetList
     */
    public StarSystem(StellarPosition position, String name, List<Star> starList,
            List<InhabitedWorld> planetList) {
        this(name);
        this.position = position;
        
        //Try using auto boxing...
        this.stars = new TreeMap<Integer,Star>();
        for(Star star: starList){
            int i=1;
            stars.put(i, star);
            i++;
        }
        
        this.planets = new TreeMap<Integer,InhabitedWorld>();
        for(InhabitedWorld planet: planetList){
            int i=1;
            planets.put(i, planet);
            i++;
        }
    }

    /**
     * Construct a simple star system with one star and one usable planet
     * 
     * @param position
     * @param star
     * @param planet
     */
    public StarSystem(StellarPosition position, String name, Star star,
            InhabitedWorld planet) {
        this(name);
        this.position = position;
        this.stars = new TreeMap<Integer,Star>();
        this.stars.put(1,star);
        this.planets = new TreeMap<Integer,InhabitedWorld>();
        this.planets.put(1,planet);
    }
    
    /**
     * Convenience constructor for a simple star system with one star
     * 
     * @param position
     * @param star
     */
    public StarSystem(StellarPosition position, String name, Star star) {
        this(name);
        this.position = position;
        this.stars = new TreeMap<Integer,Star>();
        this.stars.put(1,star);
        this.planets = new TreeMap<Integer,InhabitedWorld>();
    }
    
    private StarSystem(String name){
        super(name);
        systemsWithin30LY = new TreeMap<StarSystem, Double>();
        systemsWithin60LY = new TreeMap<StarSystem, Double>();
        unitsInSystem = new TreeSet<StarMapUnit>();
    }


    //Convenience methods======================================================
    public double getResourceValue(){
        double resourceValue=0;
        for(InhabitedWorld planet: planets.values()){
            resourceValue += planet.getResourceValue();
        }
        return resourceValue;
    }
    
    /**
     * Unit arrives to a star system.  It can be a MillitaryForce or a Warship.
     * @param warship
     */
    public void addUnitInSystem(StarMapUnit unit){
        unitsInSystem.add(unit);
    }
    
    /**
     * When a unit leaves a star system, a military force lands on a planet or is removed 
     * from play. 
     */
    public void removeUnitFromSystem(StarMapUnit unit){
        unitsInSystem.remove(unit);
    }
    
    /**
     * Convenience method that returns the first planet in the system.
     * By convention this would be the inhabited planet in the system.
     * @return
     */
    public InhabitedWorld getPrimaryPlanet(){
        if(!planets.isEmpty()){
            return planets.get(1);
        }
        else {
            return null;
        }
    }
    
    /**
     * This method is meant to be called by the StarMap generator
     * @param system
     * @param distance
     */
    public void addStarSystemWithin30LY(StarSystem system, double distance){
        systemsWithin30LY.put(system, distance);
    }
    
    /**
     * This method is meant to be called by the StarMap generator
     * @param system
     * @param distance
     */
    public void addStarSystemWithin60LY(StarSystem system, double distance){
        systemsWithin60LY.put(system, distance);
    }
    
    //Access methods===========================================================
    public StellarPosition getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Integer, Star> getStars() {
        return stars;
    }

    public Map<Integer, InhabitedWorld> getPlanets() {
        return planets;
    }
    
    /**
     * Returns a clone of the list of units in the system
     * @return
     */
    public Set<StarMapUnit> getUnitsInSystem() {
        return new TreeSet<StarMapUnit>(unitsInSystem);
    }

    public Map<StarSystem, Double> getSystemsWithin30LY() {
        return systemsWithin30LY;
    }

    public Map<StarSystem, Double> getSystemsWithin60LY() {
        return systemsWithin60LY;
    }

    public void setConflictInSystem(boolean b) {
        conflictInSystem = b;
    }
    
    public boolean isConflictInSystem() {
        return conflictInSystem;
    }

}
