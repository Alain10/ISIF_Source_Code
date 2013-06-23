package ca.ott.al.starmap.core.faction;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import ca.ott.al.starmap.core.map.InhabitedWorld;
import ca.ott.al.starmap.core.map.StarSystem;
import ca.ott.al.starmap.core.order.faction.FactionOrder;
import ca.ott.al.starmap.core.transport.TransportPool;
import ca.ott.al.starmap.core.unit.MilitaryForce;
import ca.ott.al.starmap.core.unit.Warship;
import ca.ott.al.starmap.exception.InvalidParameterValueException;

public class Faction implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1791888714378837343L;
    
    private String factionName;
    private Personality factionLeader;
    private Color factionColor;
    private boolean playable;

    private Map<StarSystem, Double> factionTerritory;
    private StarSystem factionCapital;
    
    private Economy economy;
    private TechnologyTable technology;
    private TransportPool transportPool;
    private ReinforcementPool reinforcementPool;
    
    private Set<MilitaryForce> militaryForces;
    private Set<Warship> warships;
    
    private SortedSet<FactionOrder> factionOrders;
    
    public Faction(String factionName, Personality factionLeader,
            StarSystem factionCapital, Economy economy, TechnologyTable techTable,
            Color factionColor, boolean playable){
        super();
        this.factionName = factionName;
        this.factionLeader = factionLeader;
        this.factionCapital = factionCapital;
        this.factionColor = factionColor;
        this.playable = playable;
        
        this.economy = economy;
        economy.setFaction(this);
        technology = techTable;
        factionTerritory = new TreeMap<StarSystem, Double>();
        transportPool = new TransportPool(this, 0);
        reinforcementPool = new ReinforcementPool();
        
        militaryForces = new TreeSet<MilitaryForce>();
        warships = new TreeSet<Warship>();
        
        factionOrders = new TreeSet<FactionOrder>();
    }
    
    //Methods==================================================================
    /**
     * Add a StarSystem to this faction's territory
     * 
     * @param starSystem
     * @param ownership
     *            The percentage of the planet controlled by this faction
     *            expressed as a double between 0 and 1
     */
    public void addStarSystem(StarSystem starSystem, Double ownership)
            throws InvalidParameterValueException {
        if (ownership.doubleValue() > 1 || ownership.doubleValue() < 0) {
            throw new InvalidParameterValueException(
                    "Value of ownership must be between 0 and 1");
        }
        factionTerritory.put(starSystem, ownership);
    }

    public void addMilitaryForce(MilitaryForce militaryForce){
        if(!militaryForces.contains(militaryForce))
            militaryForces.add(militaryForce);
    }
    
    public void removeMilitaryForce(MilitaryForce militaryForce){
        if(militaryForces.contains(militaryForce))
            militaryForces.remove(militaryForce);
    }
    
    public void addWarship(Warship warship){
        if(!warships.contains(warship))
            warships.add(warship);
    }
    
    public void removeWarship(Warship warship){
        if(warships.contains(warship))
            warships.remove(warship);
    }
    
    public void addFactionOrder(FactionOrder order) {
        factionOrders.add(order);
    }

    public void removeFactionOrder(FactionOrder order){
        factionOrders.remove(order);
    }
    
    public final SortedSet<FactionOrder> getFactionOrders(){
        return factionOrders;
    }
    
    /**
     * Gets all the Systems in this Faction's Territory that have a factory
     * @return
     */
    public List<StarSystem> getFactorySystems() {
        List<StarSystem> factorySystems = new ArrayList<StarSystem>();
        
        for(StarSystem system: getFactionTerritory().keySet()){
            InhabitedWorld world = system.getPrimaryPlanet();
            if(world != null){
                if(world.getFactorySize() > 0){
                    factorySystems.add(system);
                }
            }
        }
        return factorySystems;
    }

    //Access Methods===========================================================
    public final Set<MilitaryForce> getMilitaryForces() {
        return militaryForces;
    }

    public final Set<Warship> getWarships() {
        return warships;
    }

    public String getFactionName() {
        return factionName;
    }

    public void setFactionName(String factionName) {
        this.factionName = factionName;
    }

    public Personality getFactionLeader() {
        return factionLeader;
    }

    public void setFactionLeader(Personality factionLeader) {
        this.factionLeader = factionLeader;
    }

    public void setFactionCapital(StarSystem factionCapital){
        this.factionCapital = factionCapital;
    }
    
    public StarSystem getFactionCapital() {
        return factionCapital;
    }

    public Color getFactionColor() {
        return factionColor;
    }

    public Economy getEconomy() {
        return economy;
    }

    public final Map<StarSystem, Double> getFactionTerritory() {
        return factionTerritory;
    }

    public TechnologyTable getTechnology() {
        return technology;
    }

    public TransportPool getTransportPool() {
        return transportPool;
    }
    
    public void setTransportPool(TransportPool pool){
        this.transportPool = pool;
    }

	public boolean isPlayable() {
		return playable;
	}

	public void setPlayable(boolean playable) {
		this.playable = playable;
	}

    public ReinforcementPool getReinforcementPool() {
        return reinforcementPool;
    }

    public void setReinforcementPool(ReinforcementPool reinforcementPool) {
        this.reinforcementPool = reinforcementPool;
    }
    
	
}
