package ca.ott.al.starmap.core;

import java.beans.DesignMode;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.log4j.Logger;

import ca.ott.al.starmap.core.faction.Faction;
import ca.ott.al.starmap.core.faction.FactionRelationMatrix;
import ca.ott.al.starmap.core.faction.FactionRelationMatrix.Relationship;
import ca.ott.al.starmap.core.faction.ReinforcementPool;
import ca.ott.al.starmap.core.map.Factory;
import ca.ott.al.starmap.core.map.InhabitedWorld;
import ca.ott.al.starmap.core.map.StarMap;
import ca.ott.al.starmap.core.map.StarSystem;
import ca.ott.al.starmap.core.order.Order;
import ca.ott.al.starmap.core.order.faction.FactionOrder;
import ca.ott.al.starmap.core.order.factory.ProductionItem;
import ca.ott.al.starmap.core.order.factory.ProductionLine;
import ca.ott.al.starmap.core.order.unit.combat.AssaultLandingOrder;
import ca.ott.al.starmap.core.order.unit.combat.AttackOrder;
import ca.ott.al.starmap.core.order.unit.combat.LandingOrder;
import ca.ott.al.starmap.core.order.unit.move.AssaultOrder;
import ca.ott.al.starmap.core.order.unit.move.MoveOrder;
import ca.ott.al.starmap.core.order.unit.move.MovementOrder;
import ca.ott.al.starmap.core.order.unit.support.RepairOrder;
import ca.ott.al.starmap.core.order.unit.support.RestOrder;
import ca.ott.al.starmap.core.order.unit.support.ResupplyOrder;
import ca.ott.al.starmap.core.unit.GarrisonForce;
import ca.ott.al.starmap.core.unit.MilitaryForce;
import ca.ott.al.starmap.core.unit.MilitaryForce.UnitLoyalty;
import ca.ott.al.starmap.core.unit.Reinforcement;
import ca.ott.al.starmap.core.unit.StarMapUnit;
import ca.ott.al.starmap.core.unit.Warship;
import ca.ott.al.starmap.dice.Dice;
import ca.ott.al.starmap.file.CSVReader;
import ca.ott.al.starmap.file.CSVWriter;

public class GameCore implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1098973037830684268L;

    private static transient Logger logger = Logger.getLogger(GameCore.class.getName());

    private StarMap starMap;
    private Map<String, Faction> factions;
    private FactionRelationMatrix factionRelations;
    private int turnNumber;

    private transient File saveFile;

    private static GameCore gameCore;

    /**
     * Constructor is private to enforce singleton pattern
     */
    private GameCore() {
        starMap = new StarMap();
        factions = new TreeMap<String, Faction>();
        // Faction noFaction = new Faction("None", new Personality("None", 1,
        // 100), new StarSystem(
        // new StellarPosition(0, 0), "Unnamed", new Star(Type.M, 1)), new
        // Economy(100, 50,
        // 150, 0), new TechnologyTable(0, 0, 0),Color.GRAY, false);
        // factions.put("None", noFaction);
        factionRelations = new FactionRelationMatrix();
        turnNumber = 0;
    }

    /**
     * Singleton Pattern
     * 
     * @return The only instance of the GameCore present in the VM
     */
    public static GameCore getGameCore() {
        if (gameCore == null) {
            gameCore = new GameCore();
        }
        return gameCore;
    }

    // Functional methods=======================================================
    /**
     * undo the serialization of the GameCore
     * 
     * @param file
     * @return
     */
    public boolean loadGame(File file) {
        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);
            objectInputStream = new ObjectInputStream(fileInputStream);
            GameCore localGameCore = (GameCore) objectInputStream.readObject();
            if (localGameCore != null) {
                gameCore = localGameCore;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Serialize the entire Game core and save it to a file.
     * 
     * @param file
     * @return
     */
    public boolean saveGame(File file) {
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(gameCore);
            objectOutputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return true;
    }

    /**
     * Write the core to a CSV file
     * 
     * @return true if the operation was successful
     */
    public boolean writeGameCoreCSV(File file) {
        return CSVWriter.writeGameCore(this, file);
    }

    /**
     * Restore the core from a CSV file
     * 
     * @return true if the read was successful
     */
    public boolean readGameCoreCSV(File file) {
        return CSVReader.readGameCore(file);
    }

    /**
     * Clears all of the Game Core's data by creating a new instance of the core
     */
    public void clearAllCoreData() {
        gameCore = new GameCore();
    }

    /**
     * 
     * @return An array with four numbers: The minimum x value found on the
     *         starmap at index 0 The maximum x value at index 1 The minimum y
     *         at index 2 the maximum Y at index 3
     */
    public int[] getStarMapExtremities() {
        return starMap.getStarMapExtremities();
    }

    // Main Game Logic Method -------------------------------------------------
    /**
     * This method is meant to be called only by the End of Turn button This is
     * where we will execute the core game logic.
     * 
     * @return TODO Complete this method...
     */
    public synchronized void executeEndOfTurn() {

        turnNumber++;
        GameLogTool.getGameLogTool().changeTurn();
        String message = "===== Commencing turn: " + turnNumber +" =====";
        GameLogTool.getGameLogTool().appendLog(message);
        logger.debug(message);
        
        // Reset the combat flags of units
        resetUnitCombatFlags();
        resetSystemCombatFlag();

        // Go through all the StarSystems in search of units with move orders
        executePreCombatMovement();

        // Go through all the star systems and do the aerospace combat.
        //executeAerospaceCombat();
        executeTwoSidedAerospaceCombat();

        // Go through all the systems and do the ground combat
        executeGroundCombat();

        // Removed destroyed units.  Check for Surrenders
        removeDestroyedUnits();

        // Handle units that must retreat
        handleRetreatingUnits();

        // Go through all the systems and do the training if applicable
        executeTrainingOrders();
        
        // Do support orders
        executeSupportOrders();

        //Clean up Attack orders that no longer apply
        clearExpiredOrders();

        //determine system ownership
        determineSystemOwnership();
        
        // Do post-turn cleanup
        // reset individual unit flags
        resetUnitMovementFlags();

        // regenerate garrisons
        executeGarrisonRegeneration();
        
        //Update the economic status of the different factions.
        //The critical thing here is to calculate how much money each faction has.
        //Do the calculation here before the faction orders because when those orders are processed,
        //they are destroyed.
        executeEndOfTurnEconomicAdmin();
        
        // do production
        executeProductionOrders();

        // do faction orders
        executeFactionOrders();
        
        //Release Transport Assets
        releaseTransportAssets();
    }
    
    /**
     * 
     */
    private void executeRepairOrders(){
        for(StarSystem system: starMap.getAllStarSystems()){
            InhabitedWorld world = system.getPrimaryPlanet();
            if (world != null) {
                Set<StarMapUnit> forcesOnPlanet = new HashSet<StarMapUnit>(); 
                forcesOnPlanet.addAll(world.getMilitaryForces());
                for(StarMapUnit unit : forcesOnPlanet){
                    if(unit.getOrders().containsRepair()){
                        
                    }
                }
            }
        }
    }
    
    /**
     * 
     */
    private void executeGarrisonRegeneration(){
        for(StarSystem system: starMap.getAllStarSystems()){
            InhabitedWorld world = system.getPrimaryPlanet();
            if (world != null) {
                Set<StarMapUnit> forcesOnPlanet = new HashSet<StarMapUnit>(); 
                forcesOnPlanet.addAll(world.getMilitaryForces());
                for(StarMapUnit unit : forcesOnPlanet){
                    if(unit instanceof GarrisonForce && unit.getName().indexOf(system.getName()) != -1 ){
                        ((GarrisonForce) unit).regenerate();
                    }
                }
            }
        }
    }
    
    /**
     * Generates reinforcement units and places them in each faction's reinforcement pool
     * @param ProductionItem 
     */
    private void executeProductionOrders(){
        for( Map.Entry<String, Faction> entry: factions.entrySet() ){
            Faction faction = entry.getValue();
            ReinforcementPool pool = faction.getReinforcementPool();
            List<StarSystem> factorySystems = faction.getFactorySystems();
            for(StarSystem factorySystem : factorySystems){
                InhabitedWorld world = factorySystem.getPrimaryPlanet();
                Factory factory = world.getFactory();
                for (ProductionLine line : factory.getProductionLines()){
                    ProductionItem item = line.getItem();
                    double time = line.getTurnsRemaining();
                    if(time <= 1 && !item.getName().equals("none")){
                        pool.add(new Reinforcement(item));
                        line.setTurnsRemaining(item.getTime());
                    } else if (!item.getName().equals("none")){
                        line.setTurnsRemaining(time-1);
                    }
                }
            }
        }
    }
    
    /**
     * Releases the transport resources at the end of the turn
     */
    private void releaseTransportAssets() {
        for(Faction faction: factions.values()){
            faction.getTransportPool().resetPoolEndOfTurn();
        }
    }

    private void resetSystemCombatFlag(){
        for(StarSystem system: starMap.getAllStarSystems()){
            system.setConflictInSystem(false);
        }
    }
    
    /**
     * Units that are out-gunned by 3 to 1 at the end of a combat round must retreat.  
     */
    private void handleRetreatingUnits(){
        for(StarSystem system: starMap.getAllStarSystems()){
            InhabitedWorld world = system.getPrimaryPlanet();
            if (world != null) {
                Set<StarMapUnit> forcesOnPlanet = new HashSet<StarMapUnit>(); 
                forcesOnPlanet.addAll(world.getMilitaryForces());
                for(StarMapUnit unit : forcesOnPlanet){
                    if(unit.isUnitMustRetreat()){
                        StarSystem destination = findNearestFriendlyPlanetWithoutConflict(unit, system);
                        if(destination != null){
                            world.removeMilitaryForce((MilitaryForce)unit);
                            destination.getPrimaryPlanet().addMilitaryForce((MilitaryForce)unit);
                            GameLogTool gameLogTool = GameLogTool.getGameLogTool();
                            gameLogTool.appendLog("        "+ unit.getName() + " has retreated from "
                                    + system.getName() + " to " + destination.getName());
                            //unit.setUnitMustRetreat(false);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 
     * @param unit
     * @return
     */
    private StarSystem findNearestFriendlyPlanetWithoutConflict(StarMapUnit unit, StarSystem system){
        Faction faction = unit.getOwner();
        double nearestDistance = Double.MAX_VALUE;
        StarSystem nearestSystem = null;
        for (Map.Entry<StarSystem, Double> entry: faction.getFactionTerritory().entrySet()){
            StarSystem potentialDestination = entry.getKey();
            double distance = StarMap.calculateDistance(system, potentialDestination);
            if(distance < nearestDistance){
                InhabitedWorld world = potentialDestination.getPrimaryPlanet();
                if(world != null){
                    //Verify that this system has no enemy presence
                    Set<StarMapUnit> forcesOnPlanet = new HashSet<StarMapUnit>(); 
                    forcesOnPlanet.addAll(world.getMilitaryForces());
                    Map<Faction,Set<StarMapUnit>> unitsByFaction = parseUnitsByFaction(forcesOnPlanet);
                    resolveAlliances(unitsByFaction);
                    if(unitsByFaction.size() == 1){
                        //Verify that the units present are friendly
                        Map.Entry<Faction, Set<StarMapUnit>> unitEntry = unitsByFaction.entrySet().iterator().next();
                        if(unitEntry.getKey() == faction){
                            nearestDistance = distance;
                            nearestSystem = potentialDestination;
                        }
                    } else if(unitsByFaction.size() == 0){
                        //No units present
                        nearestDistance = distance;
                        nearestSystem = potentialDestination;
                    }
                }
            }
        }
        return nearestSystem;
    }
    
    /**
     * 
     */
    private void executeEndOfTurnEconomicAdmin(){
        for(Map.Entry<String, Faction> factionEntry: factions.entrySet()){
            Faction faction = factionEntry.getValue();
            faction.getEconomy().executeEndOfTurn();
        }
    }
    
    /**
     * 
     */
    private void executeFactionOrders(){
       for(Map.Entry<String, Faction> factionEntry: factions.entrySet()){
           Faction faction = factionEntry.getValue();
           Iterator<FactionOrder> iterator = faction.getFactionOrders().iterator();
           while(iterator.hasNext()){
               FactionOrder order = iterator.next();
               boolean done = order.execute();
               if(done)
                   iterator.remove();
           }
       } 
    }
    
    /**
     * 
     */
    private void executeTrainingOrders(){
        for(StarSystem system: starMap.getAllStarSystems()){
            InhabitedWorld world = system.getPrimaryPlanet();
            
            if (world != null) {
                Set<StarMapUnit> forcesOnPlanet = new HashSet<StarMapUnit>(); 
                forcesOnPlanet.addAll(world.getMilitaryForces());
                for(StarMapUnit unit : forcesOnPlanet){
                    if(unit.getOrders().containsTrainingOrder()){
                        if(!unit.wasInvolvedInAerospaceCombatThisTurn() 
                                && !unit.wasInvolvedInGroundCombatThisTurn()){
                            unit.doTraining();
                        }
                        unit.getOrders().clearTrainingOrder();
                    }
                }
            }
        }
    }

    /**
     * Execute support orders for units on the ground and in space 
     */
    private void executeSupportOrders(){
        for(StarSystem system: starMap.getAllStarSystems()){
            
            Set<StarMapUnit> forcesInSystem = new HashSet<StarMapUnit>();
            forcesInSystem.addAll(system.getUnitsInSystem());
            
            InhabitedWorld world = system.getPrimaryPlanet();
            if (world != null) {
                forcesInSystem.addAll(world.getMilitaryForces());
            }
            
            for(StarMapUnit unit : forcesInSystem){
                if(unit.getOrders().containsRepair() || unit.getOrders().containsRestOrResupply()){
                    if(!unit.wasInvolvedInAerospaceCombatThisTurn() && !unit.wasInvolvedInGroundCombatThisTurn()){
                        Iterator<Order> orderIterator = unit.getOrders().getOrdersList().listIterator();
                        while(orderIterator.hasNext()){
                            Order order = (Order)orderIterator.next();
                            if(order instanceof RestOrder){
                                unit.modifyFatigue(-1);
                                orderIterator.remove();
                            } else
                                if(order instanceof ResupplyOrder){
                                    resupplyUnit(unit, system);
                                    orderIterator.remove();
                                } else
                                    if(order instanceof RepairOrder){
                                        repairUnit(unit, (RepairOrder)order);
                                        orderIterator.remove();
                                    } 
                        }
                    } else {
                        //No support orders can be executed if the unit was involved in combat
                        Iterator<Order> orderIterator = unit.getOrders().getOrdersList().listIterator();
                        while(orderIterator.hasNext()){
                            Order order = (Order)orderIterator.next();
                            if(order instanceof RestOrder || order instanceof ResupplyOrder 
                                    || order instanceof RepairOrder){
                                orderIterator.remove();
                            } 
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 
     * @param unit
     */
    private void resupplyUnit(StarMapUnit unit, StarSystem system){
        Faction faction = unit.getOwner();
        //figure out how many supply points the unit needs
        double supplyNeed = unit.getSupplyRequirement()*6 - unit.getSupplyPointsInStock();
        
        //Find the nearest faction supply depot with enough points 
        Set<StarSystem> territory = faction.getFactionTerritory().keySet();
        StarSystem nearestCapableDepot = null;
        double depotDistance = Double.MAX_VALUE;
        for(StarSystem searchedSystem: territory){
            InhabitedWorld world = searchedSystem.getPrimaryPlanet();
            if(world != null){
                if(world.getResourceDepot() >= supplyNeed){
                    if(nearestCapableDepot == null){
                        nearestCapableDepot = searchedSystem;
                        depotDistance = StarMap.calculateDistanceUsePythagore(system.getPosition().getX(), 
                                system.getPosition().getY(), nearestCapableDepot.getPosition().getX(), 
                                nearestCapableDepot.getPosition().getY());
                    } else {
                        double searchedSystemDistance = StarMap.calculateDistanceUsePythagore(
                                system.getPosition().getX(), system.getPosition().getY(), 
                                searchedSystem.getPosition().getX(), searchedSystem.getPosition().getY());
                        if(searchedSystemDistance < depotDistance){
                            nearestCapableDepot = searchedSystem;
                            depotDistance = searchedSystemDistance;
                        }
                    }
                }
            }
        }
        if(nearestCapableDepot != null){
            InhabitedWorld depotWorld = nearestCapableDepot.getPrimaryPlanet();
            depotWorld.setResourceDepot(depotWorld.getResourceDepot() - supplyNeed);
            unit.resupply(supplyNeed);
        }
    }
    
    /**
     * TODO This method will require integration with the production system.
     * 
     * @param unit
     */
    private void repairUnit(StarMapUnit unit, RepairOrder order){
        
        if(unit instanceof MilitaryForce){
            Vector<Reinforcement> reinforcements = order.getReinforcements();
            MilitaryForce force = (MilitaryForce)unit;
            for(Reinforcement reinforcement : reinforcements){
                force.addReinforcement(reinforcement);
            }
        } else {
            //TODO Handle warships
        }
        
    }
    
    /**
     * Traverse all Star Systems and get rid of unwanted 'Attack orders'
     */
    private void clearExpiredOrders(){
        for(StarSystem system: starMap.getAllStarSystems()){
            InhabitedWorld world = system.getPrimaryPlanet();
            if (world != null) {
                Set<StarMapUnit> forcesOnPlanet = new HashSet<StarMapUnit>(); 
                forcesOnPlanet.addAll(world.getMilitaryForces());
                Map<Faction,Set<StarMapUnit>> unitsByFaction = parseUnitsByFaction(forcesOnPlanet);
                resolveAlliances(unitsByFaction);
                if(unitsByFaction.size() == 1){
                    for(StarMapUnit unit: forcesOnPlanet){
                        if(unit.getOrders().containsAttackOrder()){
                            unit.getOrders().clear();
                        }
                    }
                }
                
            }
        }
    }
    
    /**
     * Determines if a starSystem has been conquered.
     */
    private void determineSystemOwnership(){
        
        String message = "    ===== Checking Faction Territories =====";
        GameLogTool.getGameLogTool().appendLog(message);
        logger.info(message);
        
        for (StarSystem system : starMap.getAllStarSystems()) {
            InhabitedWorld world = system.getPrimaryPlanet();
            if (world != null) {
                Set<StarMapUnit> forcesOnPlanet = new HashSet<StarMapUnit>(); 
                forcesOnPlanet.addAll(world.getMilitaryForces());
                
                Map<Faction,Set<StarMapUnit>> unitsByFaction = parseUnitsByFaction(forcesOnPlanet);
                resolveAlliances(unitsByFaction);
                //Test that is only one alliance or faction in the region
                if (unitsByFaction.size() == 1){
                    Faction winner = unitsByFaction.entrySet().iterator().next().getKey();
                    //Get the Faction and find out if they own the system
                    Faction worldOwner = world.getControllingFaction();
                    if(winner != worldOwner){
                        //Test to see if the two factions are allies
                        Relationship relationShip = factionRelations.getFactionRelationshipStatus(winner, worldOwner);
                        if((relationShip != Relationship.allied) ){
                            winner.getFactionTerritory().put(system, (double) 1);
                            worldOwner.getFactionTerritory().remove(system);
                            world.setControllingFaction(winner);

                            message = "        The " + system.getName() + " star system"  
                                    + " has been conquered by the " + winner.getFactionName();
                            GameLogTool.getGameLogTool().appendLog(message);
                            logger.info(message);
                        }
                    }
                    
                    //Now, automatically clear the attack orders of units that no longer need these
                    
                }
            }
        } 
    }
    
    /**
     * Remove destroyed units from the game
     */
    private void removeDestroyedUnits() {
        for (StarSystem system : starMap.getAllStarSystems()) {
            InhabitedWorld world = system.getPrimaryPlanet();
            if (world != null) {
                Set<MilitaryForce> forcesOnPlanet = world.getMilitaryForces();

                for (MilitaryForce force : forcesOnPlanet) {
                    if (force.getGroundRating() <= 0 && force.getAirRating() <= 0){
                        String message = "        " + force.getName() + " has been destroyed in the " 
                                + system.getName() + " star system!";
                        GameLogTool.getGameLogTool().appendLog(message);
                        logger.info(message);
                        world.removeMilitaryForce(force);
                        Faction faction = force.getOwner();
                        faction.removeMilitaryForce(force);
                    }
                }
            }

            Set<StarMapUnit> unitsInSystem = system.getUnitsInSystem();

            for (StarMapUnit unit : unitsInSystem) {
                unit.resetMovedThisTurn();
                //Set the space-based unit's place of origin this turn
                unit.setPlaceOfOriginThisTurn(system);
            }
        }
        
    }

    /**
     * Resets the movement flags for all the units in system
     */
    private void resetUnitMovementFlags() {
        for (StarSystem system : starMap.getAllStarSystems()) {
            InhabitedWorld world = system.getPrimaryPlanet();
            if (world != null) {
                Set<MilitaryForce> forcesOnPlanet = world.getMilitaryForces();

                for (MilitaryForce force : forcesOnPlanet) {
                    force.resetMovedThisTurn();
                    //Set the force's place of origin this turn
                    force.setPlaceOfOriginThisTurn(system);
                    //Clear the retreat flag
                    force.setUnitMustRetreat(false);
                }
            }

            Set<StarMapUnit> unitsInSystem = system.getUnitsInSystem();

            for (StarMapUnit unit : unitsInSystem) {
                unit.resetMovedThisTurn();
                //Set the space-based unit's place of origin this turn
                unit.setPlaceOfOriginThisTurn(system);
                //Clear the retreat flag
                unit.setUnitMustRetreat(false);
            }
        }
    }

    /**
     * Resets the combat flags for all the units in system
     */
    private void resetUnitCombatFlags() {
        for (StarSystem system : starMap.getAllStarSystems()) {
            InhabitedWorld world = system.getPrimaryPlanet();
            if (world != null) {
                Set<MilitaryForce> forcesOnPlanet = world.getMilitaryForces();

                for (MilitaryForce force : forcesOnPlanet) {
                    force.resetInvolvedInCombatThisTurn();
                    force.setUnitMustRetreat(false);
                }
            }

            Set<StarMapUnit> unitsInSystem = system.getUnitsInSystem();

            for (StarMapUnit unit : unitsInSystem) {
                unit.resetInvolvedInCombatThisTurn();
                unit.setUnitMustRetreat(false);
            }
        }
    }

    /**
     * 
     * @param system
     * @param logs
     * @return
     */
    private void executePreCombatMovement() {

        String message = "    ===== Commencing Unit Launch Sub Phase =====";
        GameLogTool.getGameLogTool().appendLog(message);
        logger.info(message);

        for (StarSystem system : starMap.getAllStarSystems()) {
            // First, move the units that are on inhabited worlds into space
            // above their planet of origin. If the units have more movement 
            // orders, these will be handled when the units/warships are 
            // moved in the next loop.
            InhabitedWorld world = system.getPrimaryPlanet();
            if (world != null) {
                Set<MilitaryForce> forcesOnPlanet = world.getMilitaryForces();
                
                Iterator<MilitaryForce> forceIterator = forcesOnPlanet.iterator();
                while (forceIterator.hasNext()) {
                    MilitaryForce force = forceIterator.next();
                    
                    if (force.hasOrders()) {
                        // Need to launch the unit if it has move orders.
                        Order nextOrder = force.getNextOrder();
                        if (nextOrder instanceof MovementOrder) {
                            // Do the launch
                            StarSystem origin = ((MovementOrder) nextOrder).getOrigin();

                            // Verify that this world and the origin planet
                            // match then launch the unit
                            InhabitedWorld originPlanet = origin.getPrimaryPlanet();
                            if (world.equals(originPlanet)) {
                                // world.removeMilitaryForce(force);
                                originPlanet.removeMilitaryForce(force);
                                system.addUnitInSystem(force);
                                message = "        " + force.getName() + " has launched from " + origin.getName();
                                GameLogTool.getGameLogTool().appendLog(message);
                                logger.info(message);
                            }
                        } else {
                            // The unit does not have a move order. Do nothing.
                        }
                    }
                }// end of processing for the forces on planet
            }
        }// End: for each starSystem

        // Now that all the units have launched, do the first phase of stellar
        // movement.
        message = "    ===== Commencing Interstellar Movement Sub Phase =====";
        GameLogTool.getGameLogTool().appendLog(message);
        logger.info(message);
        
        for (StarSystem system : starMap.getAllStarSystems()) {
            Set<StarMapUnit> unitsInSystem = system.getUnitsInSystem();

            Iterator<StarMapUnit> unitIterator = unitsInSystem.iterator();
            while (unitIterator.hasNext()) {
                StarMapUnit unit = (StarMapUnit) unitIterator.next();
                
                boolean moveFlag = true;
                while (unit.hasOrders() && moveFlag) {
                    // Need to move the unit if it has move orders.
                    // If the unit has an assault order, move the unit also.
                    Order order = unit.getNextOrder();
                    if (order instanceof MovementOrder) {
                        // Do the move
                        StarSystem origin = ((MovementOrder) order).getOrigin();
                        StarSystem destination = ((MovementOrder) order).getDestination();

                        // Remove the unit form the current system
                        origin.removeUnitFromSystem(unit);

                        // Add the unit to its destination
                        destination.addUnitInSystem(unit);

                        message = "        " + unit.getName() + " have moved from the " + origin.getName() 
                                + " star system to the " + destination.getName() + " star system.";
                        GameLogTool.getGameLogTool().appendLog(message);
                        logger.info(message);

                        // Consume the move order
                        unit.clearNextOrder();

                        // Peak ahead to see if the next order is a move order
                        Order nextOrder = unit.getNextOrder();

                        // If the order was an assault order, insert an assault
                        // landing order in its place so that we know we must 
                        // carry out an assault landing and ground battle. The 
                        // AssaultLandingOrder will become an Attack order
                        // during the ground combat phase.
                        if (order instanceof AssaultOrder) {
                            unit.addFirstOrder(new AssaultLandingOrder());
                            moveFlag = false;
                        } else if (!(nextOrder instanceof MoveOrder) && !(nextOrder instanceof AssaultOrder)
                                && (unit instanceof MilitaryForce)) {
                            // If the unit is a MilitaryForce and moved this
                            // turn, it is required to land by game rules 
                            // unless this system is not inhabited.
                            // Such forces will land and assume a defensive
                            // posture during combat phase.
                            // Warships and any forces aboard that have orders
                            // to land, will do so during the combat phase
                            InhabitedWorld world = system.getPrimaryPlanet();
                            if ((world != null)) {
                                unit.addFirstOrder(new LandingOrder());
                            }
                        }

                        // We know the unit has moved at least once.
                        unit.flagMovedThisTurn();
                    } else {
                        // We have finished the initial movement and need to
                        // stop trying to move the unit. That or the unit did 
                        // not move.
                        moveFlag = false;
                    }
                }
            }// end of processing the units in system
        }
    }

    /**
     * 
     */
    private void executeTwoSidedAerospaceCombat(){
        String message = "     ===== Commencing Aerospace Combat Sub Phase =====";
        GameLogTool.getGameLogTool().appendLog(message);
        logger.info(message);

        for (StarSystem system : starMap.getAllStarSystems()) {
            try {
                //Set the default combat flags for this system
                //CombatFlags combatFlags = new CombatFlags();
                //combatFlags.combatTakingPlaceInUninhabitedSpace = false;

                Set<StarMapUnit> unitsInSystem = system.getUnitsInSystem();

                Set<StarMapUnit> allUnitsPresent = new HashSet<StarMapUnit>();
                //Units in space automatically participate
                allUnitsPresent.addAll(unitsInSystem);

                // If there is a planet, combat takes place in the space above
                // it and the space component of the forces on the ground participate.
                InhabitedWorld world = system.getPrimaryPlanet();
                if ((world != null)) {
                    Set<StarMapUnit> planetUnits = new HashSet<StarMapUnit>(world.getMilitaryForces());
                    // Only include planet units that have Aerospace strength > 0
                    Iterator<StarMapUnit> iterator = planetUnits.iterator();
                    while (iterator.hasNext()) {
                        StarMapUnit planetUnit = (StarMapUnit) iterator.next();
                        //Ground units that have no aerospace strength do not participate
                        if (planetUnit.calculateRawAirStrength() <= 0) {
                            iterator.remove();
                        }
                    }
                    allUnitsPresent.addAll(planetUnits);
                }

                Map<Faction, Set<StarMapUnit>> allUnitsByFaction = parseUnitsByFaction(allUnitsPresent);

                if(allUnitsByFaction.size() > 0){
                    Faction defendingFaction;
                    if(world != null){
                        defendingFaction = world.getControllingFaction();
                    } else {
                        //randomly choose the defender and flag the combat as space-only
                        defendingFaction = allUnitsByFaction.keySet().iterator().next();
                        //combatFlags.combatTakingPlaceInUninhabitedSpace = true;
                    }

                    Set<StarMapUnit> defenders = new HashSet<StarMapUnit>();
                    Set<StarMapUnit> intruders = new HashSet<StarMapUnit>();

                    Set<StarMapUnit> ownerDefenders = allUnitsByFaction.get(defendingFaction);
                    if(ownerDefenders != null && !ownerDefenders.isEmpty()){
                        defenders.addAll(ownerDefenders);
                    }
                    
                    for (Map.Entry<Faction, Set<StarMapUnit>> entry : allUnitsByFaction.entrySet()){
                        Faction faction = entry.getKey();
                        Relationship relation = factionRelations.getFactionRelationshipStatus(defendingFaction, faction);
                        if (relation.equals(Relationship.allied) || relation.equals(Relationship.self)) {
                            defenders.addAll(entry.getValue());
                        } else {
                            intruders.addAll(entry.getValue());
                        }
                    }

                    // Rule Modifications: aerospace forces are not affected by
                    // fortifications, shielding units, dig in orders, etc...
                    // Units with Repair orders have their effective strength
                    // halved.
                    // Units with Supply and Rest order are unaffected.
                    // Aerospace combat will only count towards fatigue, supply
                    // expenditures and cancel support orders if the force is 
                    // more than 30% aerospace.

                    if(intruders.isEmpty() || defenders.isEmpty()){
                        continue;
                    }
                    
                    message = "        ***** Aerospace Combat in the "+ system.getName() +" Star System! ***** ";
                    GameLogTool.getGameLogTool().appendLog(message);
                    logger.info(message);
                    system.setConflictInSystem(true);
                    
                    // Step 1: Determine the leadership strength of each faction
                    int defenderLeader = findBestLeader(defenders);
                    int intruderLeader = findBestLeader(intruders);
                    Faction intruderFaction = findFactionOfBestLeader(intruders);

                    // Step 2: Determine the Aerospace strength of each faction
                    double defenderAirStrength = calculateEffectiveAirStrength(defenders);
                    double intruderAirStrength = calculateEffectiveAirStrength(intruders);

                    logger.debug("Defender: Leader: " + defenderLeader + " Air Strength: " + defenderAirStrength);
                    logger.debug("Intruder: Leader: " + intruderLeader + " Air Strength: " + intruderAirStrength);

                    // Step 3: Now have the intruder force fire
                    int intruderLeaderBonus = intruderLeader - defenderLeader;
                    // Add attack role bonus for tech
                    int intruderTechBonus = intruderFaction.getTechnology().getAttackRollBonus();
                    // Include a -2 penalty if uncoordinated
                    int intruderCoordinationPenalty = 0;
                    if (!verifyForceCoordination(intruders)){
                        intruderCoordinationPenalty = -2;
                    }
                    int intruderRollModifier = intruderLeaderBonus + intruderTechBonus + intruderCoordinationPenalty;
                    double intruderDamage = rollAttackRoll(intruderAirStrength, intruderRollModifier);

                    //Step 4: Now the defending force fires
                    int defenderLeaderBonus = -1 * intruderLeaderBonus;
                    int defenderTechBonus = defendingFaction.getTechnology().getAttackRollBonus();

                    int defenderRollModifier = defenderLeaderBonus + defenderTechBonus;
                    double defenderDamage = rollAttackRoll(defenderAirStrength, defenderRollModifier);

                    //Allocate damage to units of each side
                    double defenderRawAirStrength = calculateRawAirStrength(defenders);
                    for (StarMapUnit unit : defenders) {
                        if (defenderRawAirStrength > 0) {
                            unit.absorbAirDamage(intruderDamage * 
                                    (unit.calculateRawAirStrength() / defenderRawAirStrength));
                        } else {
                            unit.absorbAirDamage(intruderDamage / defenders.size());
                        }
                    }

                    double intruderRawAirStrength = calculateRawAirStrength(intruders);
                    for (StarMapUnit unit : intruders) {
                        if (intruderRawAirStrength > 0) {
                            unit.absorbAirDamage(defenderDamage * 
                                    (unit.calculateRawAirStrength() / intruderRawAirStrength));
                        } else {
                            unit.absorbAirDamage(defenderDamage / intruders.size());
                        }
                    }

                    //Finally, flag the units as having participated in aerospace combat
                    flagInvolvedInAerospaceCombat(intruders);
                    flagInvolvedInAerospaceCombat(defenders);
                    
                    //Any warships need to expend supplies at this point
                    Set<StarMapUnit> intruderWarships = selectWarships(intruders);
                    Set<StarMapUnit> defenderWarships = selectWarships(defenders);
                    expendSupplies(intruderWarships);
                    expendSupplies(defenderWarships);
                }
            } 
            catch(Exception e){
                logger.error("Exception while processing star system: "+ system.getName(), e);
            }
        }
    }
    
    private Set<StarMapUnit> selectWarships(Set<StarMapUnit> units){
        Set<StarMapUnit> warships = new HashSet<StarMapUnit>();
        for (StarMapUnit unit : units){
            if(unit instanceof Warship){
                warships.add(unit);
            }
        }
        return warships;
    }
    
    private void flagInvolvedInAerospaceCombat(Set<StarMapUnit> units){
        for(StarMapUnit unit : units){
            unit.flagInvolvedInAerospaceCombatThisTurn();
        }
    }
    
    private void flagInvolvedInGroundCombat(Set<StarMapUnit> units){
        for(StarMapUnit unit : units){
            unit.flagInvolvedInGroundCombatThisTurn();
        }        
    }
    
    private double calculateRawAirStrength(Set<StarMapUnit> units){
        double rawAirStrength = 0;
        for (StarMapUnit unit : units) {
            rawAirStrength += unit.calculateRawAirStrength();
        }
        return rawAirStrength;
    }

    private double calculateRawGroundStrength(Set<StarMapUnit> units){
        double rawStrength = 0;
        for (StarMapUnit unit : units) {
            rawStrength += unit.calculateRawGroundStrength();
        }
        return rawStrength;
    }
    
    private boolean verifyForceCoordination(Set<StarMapUnit> units) {
        StarSystem placeOfOrigin = null;
        for(StarMapUnit unit : units){
            if(placeOfOrigin == null){
                placeOfOrigin = unit.getPlaceOfOriginThisTurn();
            }
//            if(!placeOfOrigin.equals(unit.getPlaceOfOriginThisTurn())){
//                return false;
//            }
        }        
        return true;
    }
    
    private Faction findFactionOfBestLeader(Set<StarMapUnit> units) {
        int bestLeader = 0;
        Faction bestLeaderFaction= null;
        for (StarMapUnit unit : units) {
            int unitLeader = unit.getLeadershipLevel();
            if (unitLeader > bestLeader) {
                bestLeader = unitLeader;
                bestLeaderFaction = unit.getOwner();
            }
        }
        return bestLeaderFaction;
    }

    private double calculateEffectiveAirStrength(Set<StarMapUnit> units){
        double airStrength = 0;
        for (StarMapUnit unit : units) {
            airStrength += unit.calculateEffectiveAirStrength();
        }
        return airStrength;
    }
    
    private double calculateEffectiveGroundStrength(Set<StarMapUnit> units){
        double strength = 0;
        for (StarMapUnit unit : units) {
            strength += unit.calculateEffectiveGroundStrength();
        }
        return strength;
    }
    
    /**
     * Finds the best Leader in a set of units
     * @param units
     * @return
     */
    private int findBestLeader(Set<StarMapUnit> units){
        int bestLeader = 0;
        for (StarMapUnit unit : units) {
            int unitLeader = unit.getLeadershipLevel();
            if (unitLeader > bestLeader) {
                bestLeader = unitLeader;
            }
        }
        return bestLeader;
    }
    
    /**
     * Selects only the units of type MilitaryForce
     * @param units
     * @return
     */
    private Set<StarMapUnit> selectMilitaryForceUnits(Set<StarMapUnit> units){
        Set<StarMapUnit> militaryForces = new HashSet<StarMapUnit>();
        for(StarMapUnit unit : units){
            if(unit instanceof MilitaryForce){
                militaryForces.add(unit);
            }
        }
        return militaryForces;
    }
    
    /**
     * Because Ground Combat must take into account whether a unit is landing, the landing of units takes place
     * during the ground combat phase
     * 
     * TODO: air strikes
     * TODO: shielding units
     * TODO: total war
     */
    private void executeGroundCombat() {
        
        String message = "     ===== Commencing Ground Combat Sub Phase =====";
        GameLogTool.getGameLogTool().appendLog(message);
        logger.info(message);

        for (StarSystem system : starMap.getAllStarSystems()) {
            
            InhabitedWorld world = system.getPrimaryPlanet();
            if ((world != null)) {
                
                Set<StarMapUnit> planetUnits = new HashSet<StarMapUnit>(world.getMilitaryForces());
                Map<Faction, Set<StarMapUnit>> planetUnitsByFaction = parseUnitsByFaction(planetUnits);
                
                Set<StarMapUnit> spacebasedUnits = new HashSet<StarMapUnit>(system.getUnitsInSystem());
                Set<StarMapUnit> landingUnits = selectMilitaryForceUnits(spacebasedUnits);
                Map<Faction, Set<StarMapUnit>> spaceborneUnitsByFaction = parseUnitsByFaction(landingUnits);
                
                int allUnitsCount = planetUnitsByFaction.size() + spaceborneUnitsByFaction.size();
                if(allUnitsCount > 0){
                    Faction defendingFaction;
                    defendingFaction = world.getControllingFaction();
                    
                    Set<StarMapUnit> defenders = new HashSet<StarMapUnit>();
                    Set<StarMapUnit> intruders = new HashSet<StarMapUnit>();

                    Set<StarMapUnit> ownerDefenders = new HashSet<StarMapUnit>();
                    if(planetUnitsByFaction.get(defendingFaction) != null) {
                        ownerDefenders.addAll(planetUnitsByFaction.get(defendingFaction));
                    }
                    if(spaceborneUnitsByFaction.get(defendingFaction) != null) {
                        ownerDefenders.addAll(spaceborneUnitsByFaction.get(defendingFaction));
                    }
                    if(ownerDefenders != null && !ownerDefenders.isEmpty()) {
                        defenders.addAll(ownerDefenders);
                    }
                    
                    for (Map.Entry<Faction, Set<StarMapUnit>> entry : planetUnitsByFaction.entrySet()){
                        Faction faction = entry.getKey();
                        Relationship relation = factionRelations.getFactionRelationshipStatus(defendingFaction, faction);
                        if (relation.equals(Relationship.allied) || relation.equals(Relationship.self)) {
                            defenders.addAll(entry.getValue());
                        } else {
                            intruders.addAll(entry.getValue());
                        }
                    }
                    
                    //Determine if any intruders are already on planet.  
                    //If not, set flag to true to signify that an assault landing is taking place
                    boolean landingFlag = false;
                    if(intruders.isEmpty()){
                        landingFlag = true;
                    }
                    
                    for (Map.Entry<Faction, Set<StarMapUnit>> entry : spaceborneUnitsByFaction.entrySet()){
                        Faction faction = entry.getKey();
                        Relationship relation = factionRelations.getFactionRelationshipStatus(defendingFaction, faction);
                        if (relation.equals(Relationship.allied) || relation.equals(Relationship.self)) {
                            defenders.addAll(entry.getValue());
                        } else {
                            intruders.addAll(entry.getValue());
                        }
                    }
                    
                    //Land the moving units now.  Note that the landing flag must be set prior to our arrival here 
                    Iterator<StarMapUnit> iterator = system.getUnitsInSystem().iterator();
                    while(iterator.hasNext()){
                        StarMapUnit landingUnit = iterator.next();
                        Order landingOrder = landingUnit.getNextOrder();
                        if(landingOrder instanceof LandingOrder){
                            system.removeUnitFromSystem(landingUnit);
                            landingUnit.clearNextOrder();
                            world.addMilitaryForce((MilitaryForce)landingUnit);
                        } else if(landingOrder instanceof AssaultLandingOrder){
                            system.removeUnitFromSystem(landingUnit);
                            landingUnit.clearOrders();
                            landingUnit.addOrder(new AttackOrder());
                            world.addMilitaryForce((MilitaryForce)landingUnit);
                        }
                    }
                    
                    //Verify that there are intruders and defenders
                    if(intruders.isEmpty() || defenders.isEmpty()){
                        continue;
                    }
                    
                    //Now, confirm that at least one of the units on planet has an attack order
                    boolean attackOrderPresenceConfirmed = false;
                    for (MilitaryForce force: world.getMilitaryForces()){
                        if(force.getNextOrder() instanceof AttackOrder){
                            attackOrderPresenceConfirmed = true;
                        }
                    }
                    if(!attackOrderPresenceConfirmed){
                        continue;
                    }
                    
                    //TODO Now, handle the case for units that are being shielded
                    //If a unit has support order, it may be shielded if at least 2 other units are shielding
                    //Note: this is a rule modification to make things simpler
                    //Not implemented yet
                    
                    
                    //Proclaim ground combat
                    message = "        ***** Ground Combat in the "+ system.getName() +" Star System! ***** ";
                    GameLogTool.getGameLogTool().appendLog(message);
                    logger.info(message);
                    system.setConflictInSystem(true);
                    
                    // Step 1: Determine the leadership strength of each faction
                    int defenderLeader = findBestLeader(defenders);
                    int intruderLeader = findBestLeader(intruders);
                    Faction intruderFaction = findFactionOfBestLeader(intruders);

                    // Step 2: Determine the Aerospace strength of each faction
                    double defenderGroundStrength = calculateEffectiveGroundStrength(defenders);
                    double intruderGroundStrength = calculateEffectiveGroundStrength(intruders);

                    logger.debug("Defender: Leader: " + defenderLeader + " Ground Strength: " + defenderGroundStrength);
                    logger.debug("Intruder: Leader: " + intruderLeader + " Ground Strength: " + intruderGroundStrength);

                    // Step 3: Now have the intruder force fire
                    int intruderLeaderBonus = intruderLeader - defenderLeader;
                    // Add attack role bonus for tech
                    int intruderTechBonus = intruderFaction.getTechnology().getAttackRollBonus();
                    // Include a -2 penalty if uncoordinated
                    int intruderCoordinationPenalty = 0;
                    if (!verifyForceCoordination(intruders)){
                        intruderCoordinationPenalty = -2;
                    }
                    int intruderRollModifier = intruderLeaderBonus + intruderTechBonus + intruderCoordinationPenalty;
                    double intruderDamage = rollAttackRoll(intruderGroundStrength, intruderRollModifier);
                    //Reduce damage to defenders from the world's fortifications
                    intruderDamage *= (1-(0.2*world.getFortificationLevel()));
                    
                    //Step 4: Now the defending force fires
                    int defenderLeaderBonus = -1 * intruderLeaderBonus;
                    int defenderTechBonus = defendingFaction.getTechnology().getAttackRollBonus();

                    int defenderContestingLandingBonus = 0;
                    if(landingFlag = true){
                        defenderContestingLandingBonus = 2;
                    }
                    int defenderRollModifier = defenderLeaderBonus + defenderTechBonus + defenderContestingLandingBonus;
                    double defenderDamage = rollAttackRoll(defenderGroundStrength, defenderRollModifier);

                    //Allocate damage to units of each side
                    double defenderRawGroundStrength = calculateRawGroundStrength(defenders);
                    for (StarMapUnit unit : defenders) {
                        if (defenderRawGroundStrength > 0) {
                            unit.absorbGroundDamage(intruderDamage * 
                                    (unit.calculateRawGroundStrength() / defenderRawGroundStrength));
                        } else {
                            unit.absorbGroundDamage(intruderDamage / defenders.size());
                        }
                    }

                    double intruderRawGroundStrength = calculateRawGroundStrength(intruders);
                    for (StarMapUnit unit : intruders) {
                        if (intruderRawGroundStrength > 0) {
                            unit.absorbGroundDamage(defenderDamage * 
                                    (unit.calculateRawGroundStrength() / intruderRawGroundStrength));
                        } else {
                            unit.absorbGroundDamage(defenderDamage / intruders.size());
                        }
                    }

                    //Finally, flag the units as having participated in ground combat
                    flagInvolvedInGroundCombat(intruders);
                    flagInvolvedInGroundCombat(defenders);
                    
                    //Expend supplies
                    expendSupplies(intruders);
                    expendSupplies(defenders);
                    
                    //Add fatigue
                    addCombatFatigue(intruders);
                    addCombatFatigue(defenders);
                    
                    //Now recalculate the remaining strength and flag units for retreat if necessary
                    defenderGroundStrength = calculateEffectiveGroundStrength(defenders);
                    intruderGroundStrength = calculateEffectiveGroundStrength(intruders);
                    if(intruderGroundStrength > 0 && defenderGroundStrength / intruderGroundStrength < 0.333){
                        setRetreatFlag(defenders);
                    } else if( defenderGroundStrength > 0 && intruderGroundStrength/defenderGroundStrength < 0.333){
                        setRetreatFlag(intruders);
                    }
                    
                    stopExhautedUnitsFromAttacking(intruders);
                    stopExhautedUnitsFromAttacking(defenders);

                    //Add experience for the units engaged
                    gainExperience(intruders);
                    gainExperience(defenders);
                }
            }
        }
    }
    
    private void gainExperience(Set<StarMapUnit> units){
        for(StarMapUnit unit : units){
            unit.setExperiencePoints(unit.getExperiencePoints() + 1);
        }
    }
    
    
    /**
     * This method method may be superfluous with the retreat code and all but I want to make sure
     * the units stop attacking if the effective ground strength is zero
     */
    private void stopExhautedUnitsFromAttacking(Set<StarMapUnit> units){
        for(StarMapUnit unit : units){
            if(unit instanceof MilitaryForce){
                MilitaryForce force = (MilitaryForce)unit;
                if(force.calculateEffectiveGroundStrength() <= 0 && force.getOrders().containsAttackOrder()){
                    force.getOrders().clearNextOrder();
                }
            }
        }
    }
    
    private void setRetreatFlag(Set<StarMapUnit> units){
        for(StarMapUnit unit : units){
            unit.setUnitMustRetreat(true);
        }
    }
    
    private void addCombatFatigue(Set<StarMapUnit> units){
        for(StarMapUnit unit : units){
            unit.setFatiguePoints(unit.getFatiguePoints() + 1);
        }
    }

    private void expendSupplies(Set<StarMapUnit> units){
        for(StarMapUnit unit : units){
            unit.expendSupplies();
        }
    }
    
    /**
     * 
     * @param input
     * @return
     */
    public Map<Faction, Set<StarMapUnit>> parseUnitsByFaction(Set<StarMapUnit> input) {
        Map<Faction, Set<StarMapUnit>> result = new HashMap<Faction, Set<StarMapUnit>>();

        for (StarMapUnit unit : input) {
            Faction owner = unit.getOwner();
            Set<StarMapUnit> unitsOfThisFaction = result.get(owner);
            if (unitsOfThisFaction == null) {
                unitsOfThisFaction = new TreeSet<StarMapUnit>();
                unitsOfThisFaction.add(unit);
                result.put(owner, unitsOfThisFaction);
            } else {
                unitsOfThisFaction.add(unit);
            }
        }
        return result;
    }

    /**
     * Calculates the amount of damage generated by a force
     * 
     * @param forceStrength
     * @param rollModifier
     * @return
     */
    private double rollAttackRoll(double forceStrength, int rollModifier) {
        int roll = Dice.getDice().get2D6();
        roll += rollModifier;

        if (roll <= 0) {
            return 2.5 / 100;
        }
        double percentage = 0;
        switch (roll) {
        case 1:
            percentage = 5;
            break;
        case 2:
            percentage = 7.5;
            break;
        case 3:
            percentage = 10;
            break;
        case 4:
            percentage = 12.5;
            break;
        case 5:
            percentage = 15;
            break;
        case 6:
            percentage = 17.5;
            break;
        case 7:
            percentage = 20;
            break;
        case 8:
            percentage = 22.5;
            break;
        case 9:
            percentage = 25;
            break;
        case 10:
            percentage = 27.5;
            break;
        case 11:
            percentage = 30;
            break;
        case 12:
            percentage = 32.5;
            break;
        case 13:
            percentage = 35;
            break;
        case 14:
            percentage = 37.5;
            break;
        case 15:
            percentage = 40;
            break;
        default:
            percentage = 40;
        }
        return (percentage / 100) * forceStrength;
    }

    /**
     * PostCondition: Modifies the Map given to this method such that the units from allied factions are stored
     * in a common Map Entry 
     * 
     * @param units
     */
    public void resolveAlliances(Map<Faction, Set<StarMapUnit>> units){
        List<Faction> removeTheseEntries = new ArrayList<Faction>();
        
        if (units.size() > 1) {
            Iterator<Map.Entry<Faction, Set<StarMapUnit>>> firstAllyIterator = units.entrySet().iterator();
            while (firstAllyIterator.hasNext()) {
                Map.Entry<Faction, Set<StarMapUnit>> firstAllyEntry = firstAllyIterator.next();
                Faction firstAlly = firstAllyEntry.getKey();
                
                Iterator<Map.Entry<Faction, Set<StarMapUnit>>> secondAllyIterator = units.entrySet().iterator();
                while (secondAllyIterator.hasNext()) {
                    Map.Entry<Faction, Set<StarMapUnit>> secondAllyEntry = secondAllyIterator.next();
                    Faction secondAlly = secondAllyEntry.getKey();
                    
                    if (firstAlly != secondAlly  && !removeTheseEntries.contains(firstAlly)){
                        Relationship relation = factionRelations.getFactionRelationshipStatus(firstAlly, secondAlly);
                        if (relation.equals(Relationship.allied)) {
                            Set<StarMapUnit> firstAllySet = firstAllyEntry.getValue();
                            Set<StarMapUnit> secondAllySet = secondAllyEntry.getValue();
                            
                            Set<StarMapUnit> joinedSet = new TreeSet<StarMapUnit>();
                            joinedSet.addAll(firstAllySet);
                            joinedSet.addAll(secondAllySet);
                            
                            firstAllyEntry.setValue(joinedSet);
                            //secondAllyIterator.remove();
                            //Cannot remove the Ally here for fear of a ConcurrentModificationException.
                            //Solution: store the key in a list and remove those entries after the loops are done
                            removeTheseEntries.add(secondAlly);
                        }
                    }
                }
            }
        }
        //With both Iterators no longer in scope, I can safely remove the entries that have been merged from the list
        for(Faction faction: removeTheseEntries){
            units.remove(faction);
        }
    }

    /**
     * 
     * @param unitName
     * @param selectedItem
     * @param selectedSystem
     */
    public void raiseUnit(String unitName, String unitType, String selectedSystem, Faction faction) {
        StarSystem starSystem = starMap.getStarSystemByName(selectedSystem);
        String commandingOfficer = "unknown";
        double airRating, groundRating;
        if (unitType.equals("Ground Unit")){
            airRating = 0;
            groundRating = 5;
        } else {
            airRating = 5;
            groundRating = 0;
        }
        
        MilitaryForce force = new MilitaryForce(faction, unitName, commandingOfficer, airRating, groundRating, 1, 0, 
                0, UnitLoyalty.reliable);
        starSystem.getPrimaryPlanet().addMilitaryForce(force);
        faction.addMilitaryForce(force);
    }

    // Access methods----------------------------------------------------------
    public StarMap getStarMap() {
        return starMap;
    }

    public Map<String, Faction> getFactions() {
        return factions;
    }

    public Faction getFactionByName(String factionName) {
        return factions.get(factionName);
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public void setSaveFile(File saveFile) {
        this.saveFile = saveFile;
    }

    public File getSavefile() {
        return saveFile;
    }

    public FactionRelationMatrix getFactionRelations() {
        return factionRelations;
    }
    
    // Inner class for flags -------------------------------------------------
//    private class CombatFlags{
//        boolean combatTakingPlaceInUninhabitedSpace = false;
//        boolean defenderUncoordinated = false;
//        boolean intrudersUncoordinated = false;
//        
//    }


    
    // deprecated methods -----kept for historical ---------------------------
    /**
     * @deprecated
     * Aerospace combat will take place between groups of space forces present
     * in a star system which are hostile to each other.
     * 
     * The first step is to find out if there are units in system. If yes,
     * assume they are about to land. Warships are to be handled separately.
     * Then determine the opposing forces. Gather all the forces in space in a
     * Set. Gather all the units on the ground in a Set. Gather all units
     * involved in a Map of Set keyed on Faction. Resolve alliances by grouping
     * allied units in each category together.
     * 
     * If some of the defenders have Shield orders, the units with support
     * orders of the same faction will not participate provided they are less
     * than 1/3 of the shielding force.
     * 
     * Total the aerospace strength for each group and category. If one side has
     * zero aerospace strength, the other sides get to do ground attacks. If a
     * warship has bombardment orders, do the bombardment. Do the aerospace
     * combat rolls. Assign casualties to aerospace forces. Assign casualties to
     * landing ground forces if applicable.
     * 
     * Move surviving landing units to the ground. Remove any annihilated units
     * from play. (if applicable)
     * 
     * The things that will trigger a fight at this point are: -> units from an
     * unfriendly power that are landing. -> units from any group that have
     * attack orders -> warships with attack or bombardment orders
     * 
     * 
     * Note: flaw in this code: air combat only happens if units are present in
     * space.  Other problem is failure to give defender +1 advantage.
     * Yet another is difficulty with determining if unit was involved in 
     * combat.
     * 
     * @param system
     * @param logs
     * @return
     */
    private void executeAerospaceCombat() {
        for (StarSystem system : starMap.getAllStarSystems()) {
            Set<StarMapUnit> unitsInSystem = system.getUnitsInSystem();

            if (!unitsInSystem.isEmpty()) {
                Set<StarMapUnit> allUnitsPresent = new HashSet<StarMapUnit>();
                allUnitsPresent.addAll(unitsInSystem);

                // If there is a planet, combat takes place in the space above
                // it and the space component of the forces on the ground participate.
                InhabitedWorld world = system.getPrimaryPlanet();
                if ((world != null)) {
                    Set<StarMapUnit> planetUnits = new HashSet<StarMapUnit>(world.getMilitaryForces());
                    // Only include planet units that have Aerospace strength > 0
                    Iterator<StarMapUnit> iterator = planetUnits.iterator();
                    while (iterator.hasNext()) {
                        StarMapUnit planetUnit = (StarMapUnit) iterator.next();
                        if (planetUnit.calculateRawAirStrength() <= 0) {
                            iterator.remove();
                        }
                    }
                    allUnitsPresent.addAll(planetUnits);
                }

                Map<Faction, Set<StarMapUnit>> allUnitsByFaction = parseUnitsByFaction(allUnitsPresent);

                resolveAlliances(allUnitsByFaction);
                logger.debug("Units by faction: " + allUnitsByFaction.toString());

                if (allUnitsByFaction.size() > 1) {
                    // There is more than one faction present and they probably
                    // don't like each other.
                    String message = "Aerospace Combat in the " + system.getName() + " star system!";
                    GameLogTool.getGameLogTool().appendLog(message);

                    // Rule Modifications: aerospace forces are not affected by
                    // fortifications, shielding units, dig in orders, etc...
                    // Units with Repair orders have their effective strength
                    // halved.
                    // Units with Supply and Rest order are unaffected.
                    // Aerospace combat will only count towards fatigue, supply
                    // expenditures and cancel support orders if the force is 
                    // more than 30% aerospace.

                    // Step 1: Determine the leadership strength of each faction
                    // Step 2: Determine the Aerospace strength of each faction
                    Map<Faction, Integer> leaders = new HashMap<Faction, Integer>();
                    Map<Faction, Double> aerospaceStrength = new HashMap<Faction, Double>();
                    for (Map.Entry<Faction, Set<StarMapUnit>> factionEntry : allUnitsByFaction.entrySet()) {
                        Faction faction = factionEntry.getKey();
                        Set<StarMapUnit> units = factionEntry.getValue();
                        int bestLeader = 0;
                        double airStrength = 0;
                        for (StarMapUnit unit : units) {
                            int unitLeader = unit.getLeadershipLevel();
                            if (unitLeader > bestLeader) {
                                bestLeader = unitLeader;
                            }
                            airStrength += unit.calculateEffectiveAirStrength();
                            // flag the unit as having participated in aerospace combat
                            unit.flagInvolvedInAerospaceCombatThisTurn();
                        }
                        leaders.put(faction, bestLeader);
                        aerospaceStrength.put(faction, airStrength);
                        logger.debug(faction.getFactionName() + " Leader: " + bestLeader + " Air Strength:"
                                + airStrength);
                    }

                    // Now have each force fire
                    Map<Faction, Double> damageDealtByAlliance = new HashMap<Faction, Double>();
                    for (Map.Entry<Faction, Double> strengthEntry : aerospaceStrength.entrySet()) {
                        Faction faction = strengthEntry.getKey();
                        int leader = leaders.get(faction);
                        int enemyLeader = isolateBestEnemyLeader(faction, leaders);
                        int leaderDifferential = leader - enemyLeader;

                        // Add attack role bonus for tech
                        int techBonus = faction.getTechnology().getAttackRollBonus();
                        int rollModifier = leaderDifferential + techBonus;
                        // TODO Implement penalty to rollModifier for
                        // uncoordinated

                        // roll the attack
                        double damage = rollAttackRoll(strengthEntry.getValue(), rollModifier);
                        damageDealtByAlliance.put(faction, damage);
                        logger.debug(faction.getFactionName() + " Damage dealt: " + damage);
                    }

                    // Now, inflict the damage to the participants by alliance
                    // Assume all alliances take damage proportional to their
                    // air strength
                    Map<Faction, Double> damageAbsorbedByAlliance = new HashMap<Faction, Double>();
                    for (Map.Entry<Faction, Double> damageEntry : damageDealtByAlliance.entrySet()) {
                        double totalOpponentStrength = 0;
                        // Calculate total strength of the opponents
                        for (Map.Entry<Faction, Double> strengthEntry : aerospaceStrength.entrySet()) {
                            if (damageEntry.getKey() != strengthEntry.getKey()) {
                                totalOpponentStrength += strengthEntry.getValue();
                            }
                        }
                        // Now divide the damage between the opponents
                        // Be careful to avoid divide by zero
                        for (Map.Entry<Faction, Double> strengthEntry : aerospaceStrength.entrySet()) {
                            if (damageEntry.getKey() != strengthEntry.getKey()) {
                                if (totalOpponentStrength > 0) {
                                    double share = damageEntry.getValue()
                                            * (strengthEntry.getValue() / totalOpponentStrength);
                                    damageAbsorbedByAlliance.put(strengthEntry.getKey(), share);
                                } else {
                                    double share = damageEntry.getValue() * (1 / (damageDealtByAlliance.size() - 1));
                                    damageAbsorbedByAlliance.put(strengthEntry.getKey(), share);
                                }
                            }
                        }
                    }
                    // Finish allocating the damage to the individual units of
                    // each alliance
                    for (Map.Entry<Faction, Double> damageEntry : damageAbsorbedByAlliance.entrySet()) {

                        Set<StarMapUnit> allianceUnits = allUnitsByFaction.get(damageEntry.getKey());
                        double factionRawAirStrength = 0;
                        for (StarMapUnit unit : allianceUnits) {
                            factionRawAirStrength += unit.calculateRawAirStrength();
                        }
                        for (StarMapUnit unit : allianceUnits) {
                            if (factionRawAirStrength > 0) {
                                unit.absorbAirDamage(damageEntry.getValue()
                                        * (unit.calculateRawAirStrength() / factionRawAirStrength));
                            } else {
                                unit.absorbAirDamage(damageEntry.getValue() / allianceUnits.size());
                            }
                        }
                    }
                }
            }
        }
    }
    
    
    /**
     * @deprecated
     * Handles the special case where there is more than one enemy leader on
     * site. This would only happen in a fight with 3 or more sides. It's likely
     * to be rare, but the case must be handled. Rule modification: Returns the
     * best of the enemy leaders. Minimum rating of 2.
     * 
     * @param faction
     * @param leaders
     * @return
     */
    private int isolateBestEnemyLeader(Faction faction, Map<Faction, Integer> leaders) {
        Map<Faction, Integer> copy = new HashMap<Faction, Integer>(leaders);
        // Get rid of the leader for the current faction
        copy.remove(faction);
        int bestEnemy = 2;
        for (Map.Entry<Faction, Integer> entry : copy.entrySet()) {
            int enemy = entry.getValue();
            if (enemy > bestEnemy) {
                bestEnemy = enemy;
            }
        }
        return bestEnemy;
    }


}
