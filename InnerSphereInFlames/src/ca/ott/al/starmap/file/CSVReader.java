package ca.ott.al.starmap.file;

import java.awt.Color;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ca.ott.al.starmap.console.RandomGarrisonGenerator;
import ca.ott.al.starmap.core.GameCore;
import ca.ott.al.starmap.core.faction.ComstarEconomy;
import ca.ott.al.starmap.core.faction.Economy;
import ca.ott.al.starmap.core.faction.Faction;
import ca.ott.al.starmap.core.faction.FactionRelationMatrix.Relationship;
import ca.ott.al.starmap.core.faction.Personality;
import ca.ott.al.starmap.core.faction.TechnologyTable;
import ca.ott.al.starmap.core.map.Factory;
import ca.ott.al.starmap.core.map.InhabitedWorld;
import ca.ott.al.starmap.core.map.InhabitedWorld.PlanetLevel;
import ca.ott.al.starmap.core.map.Star;
import ca.ott.al.starmap.core.map.StarMap;
import ca.ott.al.starmap.core.map.StarSystem;
import ca.ott.al.starmap.core.order.faction.BuildFactoryOrder;
import ca.ott.al.starmap.core.order.faction.BuildFortificationOrder;
import ca.ott.al.starmap.core.order.factory.ProductionItem;
import ca.ott.al.starmap.core.order.factory.ProductionItemSingleton;
import ca.ott.al.starmap.core.order.factory.ProductionLine;
import ca.ott.al.starmap.core.transport.TransportPool;
import ca.ott.al.starmap.core.unit.GarrisonForce;
import ca.ott.al.starmap.core.unit.MercenaryForce;
import ca.ott.al.starmap.core.unit.MilitaryForce;
import ca.ott.al.starmap.core.unit.MilitaryForce.UnitLoyalty;
import ca.ott.al.starmap.core.unit.SpaceCraft;
import ca.ott.al.starmap.core.unit.SpaceCraft.SpaceCraftStatus;
import ca.ott.al.starmap.core.unit.Warship;
import ca.ott.al.starmap.core.unit.WarshipClass;
import ca.ott.al.starmap.core.util.StellarPosition;
import ca.ott.al.starmap.exception.InvalidParameterValueException;
import ca.ott.al.starmap.ui.ProductionOrdersPanel.ProductionTableRow;

public class CSVReader {


    private static Map<String,Personality> leaderCache;
    private static Map<String,TechnologyTable> techTableCache;
    private static Map<String, String> factionCapitalNameCache;
    
    public static boolean readGameCore(File file){
        //Clear the caches and the core
        clearCaches();
        GameCore gameCore = GameCore.getGameCore();
        gameCore.clearAllCoreData();
        
        gameCore = GameCore.getGameCore();
        
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            bufferedReader.mark(5000000);
            
            String line;
            
            //Step 1 and 2 in the sequence, is to cache the personalities and technologies
            //ignore all others
            line = bufferedReader.readLine();
            while(line != null){
                String[] lineParts = line.split(",");
                cleanLineParts(lineParts);
                
                if(lineParts[0].equals(CSVConstants.personKey)){
                    parsePerson(lineParts);
                }else if(lineParts[0].equals(CSVConstants.techKey)){
                    parseTech(lineParts);
                }
                line = bufferedReader.readLine();
            }
            bufferedReader.reset();
            
            //Step 3: Next in the sequence is to do the factions
            line = bufferedReader.readLine();
            while(line != null){
                String[] lineParts = line.split(",");
                cleanLineParts(lineParts);
                if(lineParts[0].equals(CSVConstants.factionKey)){
                    parseFaction(lineParts);
                }
                line = bufferedReader.readLine();
            }
            bufferedReader.reset();
            
            //Step 4: Step 4 is to reconstruct the star map
            line = bufferedReader.readLine();
            while(line != null){
                String[] lineParts = line.split(",");
                cleanLineParts(lineParts);
                if(lineParts[0].equals(CSVConstants.systemKey)){
                    parseStarSystem(lineParts);
                }
                line = bufferedReader.readLine();
            }
            bufferedReader.reset();
            
            //Step 5: reconcile capitals
            reconcileFactionCapitals();
            
            //Step 6 & 7: reconstruct the warships and reconcile them
            line = bufferedReader.readLine();
            while(line != null){
                String[] lineParts = line.split(",");
                cleanLineParts(lineParts);
                if(lineParts[0].equals(CSVConstants.warshipKey)){
                    parseWarship(lineParts);
                }
                line = bufferedReader.readLine();
            }
            bufferedReader.reset();
            
            //Step 8 & 9: rebuild the military units and reconcile them
            line = bufferedReader.readLine();
            while(line != null){
                String[] lineParts = line.split(",");
                cleanLineParts(lineParts);
                if(lineParts[0].equals(CSVConstants.militaryUnitKey)){
                    parseMilitaryUnit(lineParts);
                }
                line = bufferedReader.readLine();
            }
            bufferedReader.reset();
            
            //Step 10: read the faction diplomacy entries if there are any
            line = bufferedReader.readLine();
            while(line != null){
                String[] lineParts = line.split(",");
                cleanLineParts(lineParts);
                if(lineParts[0].equals(CSVConstants.diplomacyKey)){
                    parseDiplomacy(lineParts);
                }
                line = bufferedReader.readLine();
            }
            bufferedReader.reset();
            
            //Step 11: optional, carry out corrections of the system owners
            line = bufferedReader.readLine();
            while(line != null){
                String[] lineParts = line.split(",");
                cleanLineParts(lineParts);
                if(lineParts[0].equals(CSVConstants.correctionKey)){
                    parseCorrection(lineParts);
                }
                line = bufferedReader.readLine();
            }
            bufferedReader.reset();
            
            //Step 12: optional, enter the correct number of factories for the era  
            line = bufferedReader.readLine();
            while(line != null){
                String[] lineParts = line.split(",");
                cleanLineParts(lineParts);
                if(lineParts[0].equals(CSVConstants.factoryKey)){
                    parseFactory(lineParts);
                }
                line = bufferedReader.readLine();
            }
            bufferedReader.reset();
            
            
            
            //Step 13: parse production items  
//            line = bufferedReader.readLine();
//            while(line != null){
//                String[] lineParts = line.split(",");
//                cleanLineParts(lineParts);
//                if(lineParts[0].equals(CSVConstants.productionItemKey)){
//                    parseProductionItem(lineParts);
//                }
//                line = bufferedReader.readLine();
//            }
//            bufferedReader.reset();
            
            //Step 14: parse production lines that are active  
            line = bufferedReader.readLine();
            while(line != null){
                String[] lineParts = line.split(",");
                cleanLineParts(lineParts);
                if(lineParts[0].equals(CSVConstants.productionOrderKey)){
                    parseProductionOrder(lineParts);
                }
                line = bufferedReader.readLine();
            }
            bufferedReader.reset();
            
            //Step 15: parse faction orders that are outstanding
            line = bufferedReader.readLine();
            while(line != null){
                String[] lineParts = line.split(",");
                cleanLineParts(lineParts);
                if(lineParts[0].equals(CSVConstants.factionOrderKey)){
                    parseFactionOrder(lineParts);
                }
                line = bufferedReader.readLine();
            }
            bufferedReader.reset();
            
            bufferedReader.close();
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static void cleanLineParts(String[] lineParts) {
        for (int i = 0; i< lineParts.length; i++){
            if(lineParts[i].startsWith("\"") && lineParts[i].endsWith("\"")){
                lineParts[i] = lineParts[i].substring(1, lineParts[i].length()-1);
            }
        }
    }

    //Parsing methods==========================================================
    /**
     * build fortification  Federated Suns  Adelson 2
     * @param lineParts
     */
    private static void parseFactionOrder(String[] lineParts){
        String orderType = lineParts[1];
        String factionName = lineParts[2];
        String systemName = lineParts[3];
        int time = Integer.parseInt(lineParts[4]);
        
        if(orderType.equals(CSVConstants.buildFortification)){
            StarSystem system = GameCore.getGameCore().getStarMap().getStarSystemByName(systemName);
            BuildFortificationOrder order = new BuildFortificationOrder(system, time);
            Faction faction = GameCore.getGameCore().getFactionByName(factionName);
            faction.addFactionOrder(order);
        } else if(orderType.equals(CSVConstants.buildFactory)){
            StarSystem system = GameCore.getGameCore().getStarMap().getStarSystemByName(systemName);
            BuildFactoryOrder order = new BuildFactoryOrder(system, time);
            Faction faction = GameCore.getGameCore().getFactionByName(factionName);
            faction.addFactionOrder(order);
        }

    }
    
    /**
     * production item  IS Light Mech Company (3025)    0   40  0.5 1
     * @param lineParts
     */
    private static void parseProductionItem(String[] lineParts){
        String name = lineParts[1];
        double ground = Double.parseDouble(lineParts[2]);
        double air = Double.parseDouble(lineParts[3]);
        double cost = Double.parseDouble(lineParts[4]);
        double time = Double.parseDouble(lineParts[5]);
        
        ProductionItem item = new ProductionItem(name, cost, air, ground, time);
        //ProductionItemSingleton.getProductionItemSingleton().addItem(item);
    }
    
    /**
     * production order Lyran Commonwealth systemName none    0
     * @param lineParts
     */
    private static void parseProductionOrder(String[] lineParts){
        String factionName = lineParts[1];
        String systemName = lineParts[2];
        String itemName = lineParts[3];
        double time = Double.parseDouble(lineParts[4]);
        StarSystem system = GameCore.getGameCore().getStarMap().getStarSystemByName(systemName);
        Factory factory = system.getPrimaryPlanet().getFactory();
        
        ProductionItem item = ProductionItemSingleton.getProductionItemSingleton().getProductionItemByName(itemName);
        
//        factory.addProductionLine(new ProductionLine(
//                        ProductionItemSingleton.getProductionItemSingleton().getAvailableItems().get(0)));
        ProductionLine prodLine = factory.getNextAvailableProductionLine();
        prodLine.setItem(item);
        prodLine.setTurnsRemaining(time);
    }
    
    private static void parseDiplomacy(String[] lineParts){
        String factionName1 = lineParts[1];
        String factionName2 = lineParts[2];
        String relationship = lineParts[3];
        
        GameCore gameCore = GameCore.getGameCore();
        Faction faction1 = gameCore.getFactionByName(factionName1);
        Faction faction2 = gameCore.getFactionByName(factionName2);
        
        Relationship status = Relationship.valueOf(relationship);
        if(status != null){
            gameCore.getFactionRelations().setFactionRelationship(faction1, faction2, status);
        }
    }
    
    
    private static void parseCorrection(String[] lineParts) {
        String factionName = lineParts[1];
        String systemName = lineParts[2];
        
        GameCore gameCore = GameCore.getGameCore();
        Faction faction = gameCore.getFactions().get(factionName);
        
        StarSystem starSystem = gameCore.getStarMap().getStarSystemByName(systemName);
        if(starSystem != null){
            InhabitedWorld world = starSystem.getPrimaryPlanet();
            if(world != null){
                Faction oldOwner = world.getControllingFaction();
                oldOwner.getFactionTerritory().remove(starSystem);
                world.setControllingFaction(faction);
                faction.getFactionTerritory().put(starSystem, new Double(1));
                Set<MilitaryForce> forces = world.getMilitaryForces();
                
                for(MilitaryForce garrison : forces){
                    if(garrison instanceof GarrisonForce){
                        oldOwner.removeMilitaryForce(garrison);
                        garrison.setOwner(faction);
                        faction.addMilitaryForce(garrison);
                    }
                }
            }
        }
        
    }
    
    /**
     * postcondition: a new instance of Personality is inserted in the leaderCache
     */
    private static void parsePerson(String[] lineParts){
        String leaderName = lineParts[1];
        int leadership = Integer.parseInt(lineParts[2]);
        int popullarity = Integer.parseInt(lineParts[3]);
        Personality person = new Personality(leaderName, leadership, popullarity);
        leaderCache.put(leaderName, person);
    }
    
    /**
     * one faction's technology table will be in the cache
     * @param lineParts
     */
    private static void parseTech(String[] lineParts){
        String factionName = lineParts[1];
        double battletech = Double.parseDouble(lineParts[2]);
        double comTech = Double.parseDouble(lineParts[3]);
        double industryTech = Double.parseDouble(lineParts[4]);
        TechnologyTable techTable = new TechnologyTable(battletech, comTech, industryTech);
        techTableCache.put(factionName, techTable);
    }
    
    /**
     * create a faction in the game core.
     * @param lineParts
     */
    private static void parseFaction(String[] lineParts){
        String factionName = lineParts[1];
        String leaderName = lineParts[2];
        Personality leader = leaderCache.get(leaderName);
        String capitalName = lineParts[3];
        
        factionCapitalNameCache.put(factionName, capitalName);
        
        boolean playable = Boolean.parseBoolean(lineParts[4]);
        
        int colorRed = Integer.parseInt(lineParts[5]);
        int colorGreen = Integer.parseInt(lineParts[6]);
        int colorBlue = Integer.parseInt(lineParts[7]);
        Color color = new Color(colorRed, colorGreen, colorBlue);
        
        double econoStrength = Double.parseDouble(lineParts[8]);
        int econoStrengthInt = (int)econoStrength;
        int econoMin = Integer.parseInt(lineParts[9]);
        int econoMax = Integer.parseInt(lineParts[10]);
        int hpgNetSize = Integer.parseInt(lineParts[11]);
        int transportPool = Integer.parseInt(lineParts[12]);
        double econoMod = Double.parseDouble(lineParts[13]);
        double tradeMod = Double.parseDouble(lineParts[14]);
        double cash = Double.parseDouble(lineParts[15]);
        
        Economy economy;
        if(hpgNetSize > 0){
            economy = new ComstarEconomy(econoStrengthInt, econoMin, econoMax, econoMod, 
                    tradeMod, cash, hpgNetSize);
        } else {
            economy = new Economy(econoStrengthInt, econoMin, econoMax, econoMod, tradeMod, cash);
        }
        TechnologyTable techTable = techTableCache.get(factionName);
        if(techTable == null){
            techTable = new TechnologyTable(0, 0, 0);
        }
        
        Faction faction = new Faction(factionName, leader, null, economy, techTable, color, playable);
        TransportPool pool = new TransportPool(faction, transportPool);
        faction.setTransportPool(pool);
        GameCore gameCore = GameCore.getGameCore();
        gameCore.getFactions().put(factionName, faction);
    }
    
    /**
     * create a star system in the game core
     * @param lineParts
     */
    private static void parseStarSystem(String[] lineParts){
        String factionOwner = lineParts[1];
        String systemName = lineParts[2];
        double positionX = Double.parseDouble(lineParts[3]);
        double positionY = Double.parseDouble(lineParts[4]);
        double resource = Double.parseDouble(lineParts[5]);
        double depot = Double.parseDouble(lineParts[6]);
        int fortification = Integer.parseInt(lineParts[7]);
        String planetLevel = lineParts[8];
        int factorySize = Integer.parseInt(lineParts[9]);
        Star.Type starType = Star.Type.valueOf(lineParts[10]);
        int starSubType = Integer.parseInt(lineParts[11]);
        
        StellarPosition position = new StellarPosition(positionX,positionY);
        InhabitedWorld world = new InhabitedWorld(systemName,resource);
        world.setFortificationLevel(fortification);
        world.setResourceDepot(depot);
        world.setPlanetLevel(PlanetLevel.valueOf(planetLevel));
        
        Faction controllingFaction = GameCore.getGameCore().getFactionByName(factionOwner);
        if(factorySize > 0){
            Factory factory = new Factory(systemName, controllingFaction);
            for(int i=0; i < factorySize; i++){
                factory.addProductionLine(new ProductionLine(
                        ProductionItemSingleton.getProductionItemSingleton().getAvailableItems().get(0)));
            }
            world.setFactory(factory);
        }
        Star principalStar = new Star(starType,starSubType);
        StarSystem starSystem = new StarSystem(position, systemName, principalStar);
        starSystem.getPlanets().put(1, world);
        
        GameCore gameCore = GameCore.getGameCore();
        gameCore.getStarMap().addStarSystem(position, starSystem);
        try {
            //Link the Star System to the faction in both directions
            controllingFaction.addStarSystem(starSystem, new Double(1));
            world.setControllingFaction(controllingFaction);
        } catch (InvalidParameterValueException e) {
            e.printStackTrace();
        }
    }

    /**
     * create all warships
     * @param lineParts
     */
    private static void parseWarship(String[] lineParts) {
        String factionName = lineParts[1];
        String warshipName = lineParts[2];
        String warshipClassName = lineParts[3];
        double battleValueDouble = Double.parseDouble(lineParts[4]);
        int battleValue = (int)Math.floor(battleValueDouble);
        //double positionX = Double.parseDouble(lineParts[5]);
        //double positionY = Double.parseDouble(lineParts[6]);
        //StellarPosition position = new StellarPosition(positionX, positionY);
        long cost = Long.parseLong(lineParts[7]);
        int transportCapacity = Integer.parseInt(lineParts[8]);
        int carrierCapacity = Integer.parseInt(lineParts[9]);
        double damage = Double.parseDouble(lineParts[10]);
        SpaceCraftStatus functional = SpaceCraft.SpaceCraftStatus.valueOf(lineParts[11]);
        
        try {
            Faction warshipOwner = GameCore.getGameCore().getFactionByName(factionName);
            WarshipClass warshipClass = new WarshipClass(warshipClassName, battleValue, cost, 
                    transportCapacity, carrierCapacity);
            Warship warship = new Warship(warshipName, warshipClass, warshipOwner);
            warship.setDamageLevel(damage);
            warship.setStatus(functional);
            
            //TODO Fix warship location
            //StarSystem location = GameCore.getGameCore().getStarMap().getStarSysmemByPosition(position);
            //if(location != null){
            //    location.addWarshipInSystem(warship);
            //}//Otherwise the ship is located in deep space
        } catch (InvalidParameterValueException e) {
            e.printStackTrace();
        }
    }

    /**
     * parses all the military units
     * @param lineParts
     */
    private static void parseMilitaryUnit(String[] lineParts) {
        String forceOwner = lineParts[1];
        String forceName = lineParts[2];
        String commander = lineParts[3];
        UnitLoyalty loyalty = MilitaryForce.UnitLoyalty.valueOf(lineParts[4]);
        String systemName = lineParts[5];
        
        double airRating = Double.parseDouble(lineParts[6]);
        double groundRating = Double.parseDouble(lineParts[7]);
        int supplyRequirement = Integer.parseInt(lineParts[8]);
        int supplyStock = Integer.parseInt(lineParts[9]);
        double experience = Double.parseDouble(lineParts[10]);
        
        String forceType = lineParts[11];
        
        Faction ownerFaction = GameCore.getGameCore().getFactionByName(forceOwner);
        
        MilitaryForce force;
        if(forceType.equals(CSVConstants.garrisonForce)){
            //Special code for 3039 garrisons
//            if(ownerFaction.getFactionName().equals("Draconis Combine")){
//                force.setGroundRating(675);
//                force.setAirRating(155);
//            } else if (ownerFaction.getFactionName().equals("Federated Suns")){
//                force.setGroundRating(646);
//                force.setAirRating(150);
//            } else if (ownerFaction.getFactionName().equals("Lyran Commonwealth")){
//                force.setGroundRating(728);
//                force.setAirRating(190);
//            } 
            //Settings For 3025
            //force.setGroundRating(500);
            //force.setAirRating(100);
            
            //Special code for initial generation of random forces
            
            Point strength = RandomGarrisonGenerator.generateGarrison(1);
//            airRating = (strength.x);
//            groundRating = (strength.y);
            
            force = new GarrisonForce(ownerFaction, forceName, commander, airRating, 
                    groundRating, supplyRequirement, supplyStock, experience, loyalty, false);
            
            
        }else if(forceType.equals(CSVConstants.mercenaryForce)){
            force = new MercenaryForce(ownerFaction, forceName, commander, airRating, 
                    groundRating, supplyRequirement, supplyStock, experience, loyalty);
        }else{
            force = new MilitaryForce(ownerFaction, forceName, commander, airRating, 
                    groundRating, supplyRequirement, supplyStock, experience, loyalty);
        }
        
        ownerFaction.addMilitaryForce(force);
        StarSystem starSystem = GameCore.getGameCore().getStarMap().getStarSystemByName(systemName);
        starSystem.getPrimaryPlanet().addMilitaryForce(force);
    }

    /**
     * Corrects the number of production lines on a world
     * @param lineParts
     */
    private static void parseFactory(String[] lineParts){
        String planetName = lineParts[1];
        int factorySize = Integer.parseInt(lineParts[2]);
        
        StarSystem starSystem = GameCore.getGameCore().getStarMap().getStarSystemByName(planetName);
        if(starSystem != null){
            InhabitedWorld world = starSystem.getPrimaryPlanet();
            Faction controllingFaction = world.getControllingFaction();
            //world.setFortificationLevel(fortification);
            //world.setResourceDepot(depot);
            //world.setPlanetLevel(PlanetLevel.valueOf(planetLevel));

            if(factorySize > 0){
                Factory factory = new Factory(planetName, controllingFaction);
                for(int i=0; i < factorySize; i++){
                    factory.addProductionLine(new ProductionLine(
                            ProductionItemSingleton.getProductionItemSingleton().getAvailableItems().get(0)));
                }
                world.setFactory(factory);
            }
        }
    }

    
    //Other methods============================================================
    private static void clearCaches(){
        leaderCache = new HashMap<String, Personality>();
        techTableCache = new HashMap<String, TechnologyTable>();
        factionCapitalNameCache = new HashMap<String, String>();
    }
    
    /**
     * postCondition: factions have been reconciled with their capitals
     */
    private static void reconcileFactionCapitals() {
        GameCore gameCore = GameCore.getGameCore();
        StarMap starMap = gameCore.getStarMap();
        Map<String, Faction> factions = gameCore.getFactions();
        
        for(Map.Entry<String, Faction> factionEntry :factions.entrySet()){
            String factionName = factionEntry.getValue().getFactionName();
            String capitalName = factionCapitalNameCache.get(factionName);
            StarSystem capital = starMap.getStarSystemByName(capitalName);
            if(capital != null){
                factionEntry.getValue().setFactionCapital(capital);
            }
        }
    }
}
