package ca.ott.al.starmap.file;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.ott.al.starmap.core.GameCore;
import ca.ott.al.starmap.core.faction.ComstarEconomy;
import ca.ott.al.starmap.core.faction.Economy;
import ca.ott.al.starmap.core.faction.Faction;
import ca.ott.al.starmap.core.faction.FactionRelationMatrix;
import ca.ott.al.starmap.core.faction.TechnologyTable;
import ca.ott.al.starmap.core.map.InhabitedWorld;
import ca.ott.al.starmap.core.map.InhabitedWorld.PlanetLevel;
import ca.ott.al.starmap.core.map.Factory;
import ca.ott.al.starmap.core.map.Star;
import ca.ott.al.starmap.core.map.StarMap;
import ca.ott.al.starmap.core.map.StarSystem;
import ca.ott.al.starmap.core.order.faction.BuildFactoryOrder;
import ca.ott.al.starmap.core.order.faction.BuildFortificationOrder;
import ca.ott.al.starmap.core.order.faction.FactionOrder;
import ca.ott.al.starmap.core.order.factory.ProductionItem;
import ca.ott.al.starmap.core.order.factory.ProductionItemSingleton;
import ca.ott.al.starmap.core.order.factory.ProductionLine;
import ca.ott.al.starmap.core.unit.GarrisonForce;
import ca.ott.al.starmap.core.unit.MercenaryForce;
import ca.ott.al.starmap.core.unit.MilitaryForce;
import ca.ott.al.starmap.core.unit.SpaceCraft.SpaceCraftStatus;
import ca.ott.al.starmap.core.unit.Warship;
import ca.ott.al.starmap.core.util.StellarPosition;
import ca.ott.al.starmap.ui.ProductionOrdersPanel.ProductionTableRow;

public class CSVWriter {
    
    /**
     * 
     * Completely write the game to a file. Sequence should be leaders,
     * factions with their economy, then star map, followed by units.
     * But others things may need to be included like transport pools, 
     * command circuits, political interests, the technology tables and 
     * warships.
     * 
     * Production orders, unit orders and faction orders are not planned
     * for inclusion in the CSVs at this time. 2011/06/09
     * 
     * @param file
     * @return true if the writing operation was successful
     */
    public static boolean writeGameCore(GameCore gameCore,File file) {

        char[] endOfLine = new char[2];
        endOfLine[0] = 13;
        endOfLine[1] = 10;
        
        try {
            Map<String, Faction> factions = gameCore.getFactions();
            StarMap starMap = gameCore.getStarMap();
            
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            // Critical assumption: only one game can be open at any one time

            //Step 1: Write any leaders
            String line = CSVConstants.headingKey + ",leader name,leadership,popularity,recovery time";
            writer.write(line);
            writer.newLine();
            for (Map.Entry<String, Faction> factionEntry : factions.entrySet()) {
                // Personality attributes
                String leaderName = factionEntry.getValue().getFactionLeader().getName();
                int leadership = factionEntry.getValue().getFactionLeader().getLeadership();
                int popularity = factionEntry.getValue().getFactionLeader().getPopularity();
                int recoveryTime = factionEntry.getValue().getFactionLeader().getRecoveryTime();
                line = CSVConstants.personKey + "," + leaderName + "," + leadership + "," + popularity 
                        + "," + recoveryTime;
                writer.write(line);
                writer.newLine();
            }

            //Step 2: Write the faction economic data
            // "Faction name"#"Personality"#"Capital"#"Color"#"StartEcono"#"MinEcono"
            // #"MaxEcono"#"FactionEconoMod"#"HPGnetSize"
            line = CSVConstants.headingKey + ",faction name,faction leader,capital,playable,color red,"
                    + "color green,color blue,current econo,min econo,max econo,HPG net size,"
                    + "transport pool,econo modifier,trade modifier,cash";
            writer.write(line);
            writer.newLine();
            for (Map.Entry<String, Faction> factionEntry : factions.entrySet()) {
                // Faction attributes
                String factionName = factionEntry.getKey();
                String leaderName = factionEntry.getValue().getFactionLeader().getName();
                
                String factionCapital = "none";
                if(factionEntry.getValue().getFactionCapital() != null){
                    factionCapital = factionEntry.getValue().getFactionCapital().getName();
                }
                Color factionColor = factionEntry.getValue().getFactionColor();
                boolean playable = factionEntry.getValue().isPlayable();
                
                Economy economy = factionEntry.getValue().getEconomy();
                //HPG network size will default to 0 unless this is a Comstar faction
                int hpgNetSize = 0;
                if(economy instanceof ComstarEconomy){
                    ComstarEconomy comstarEconomy = (ComstarEconomy)economy;
                    hpgNetSize = comstarEconomy.getComstarNetworkSize();
                }
                int transportPoolSize = factionEntry.getValue().getTransportPool().getTotalTransportPoints();
                line = CSVConstants.factionKey + "," + factionName + "," + leaderName + "," 
                        + factionCapital + "," + playable + "," + factionColor.getRed() + "," 
                        + factionColor.getGreen() + "," + factionColor.getBlue() + "," 
                        + economy.getCurrentEconomicStrength() + "," 
                        + economy.getEconomicMinimumPercentile() + ","
                        + economy.getEconomicMaximumPercentile() + ","
                        + hpgNetSize + "," + transportPoolSize + ","
                        + economy.getFactionSpecificEconomicModifier() + ","
                        + economy.getTradeModifier() + ","
                        + economy.getResourceBank();
                writer.write(line);
                writer.newLine();
            }

            //Step 3: Write the technology table
            line = CSVConstants.headingKey + ",faction name,battle tech,communication tech,industry tech";
            writer.write(line);
            writer.newLine();
            for (Map.Entry<String, Faction> factionEntry : factions.entrySet()) {
                String factionName = factionEntry.getKey();
                TechnologyTable tech = factionEntry.getValue().getTechnology();
                line = CSVConstants.techKey + ","+ factionName +","
                        + tech.getBattleTechRating() + "," 
                        + tech.getComTechRating() + "," 
                        + tech.getIndustryTechRating();
                writer.write(line);
                writer.newLine();
            }
            
            //Step 3.1: Write the diplomacy table
            line = CSVConstants.headingKey + ",faction 1,faction 2,relationship";
            writer.write(line);
            writer.newLine();
            
            FactionRelationMatrix matrix = gameCore.getFactionRelations();
            String[][] tableForCSV = matrix.getMatrixForCSV();
            
            for (String [] data : tableForCSV ){
                line = CSVConstants.diplomacyKey + "," + data[0] + "," + data[1] +
                        "," + data[2];
                writer.write(line);
                writer.newLine();
            }
            
            //Step 4: Write command circuits if any
            //Skip for now...
                        
            //Step 5: Write the star systems, including factories.  Assume 1 planet per system for now.
            line = CSVConstants.headingKey + ",system owner,system name,position x,position y,resource value,"
                    +"depot value,fortification,planet level,factory size,star type,star subtype";
            writer.write(line);
            writer.newLine();
            for (StarSystem system : starMap.getAllStarSystems()) {
                InhabitedWorld systemMainPlanet = system.getPrimaryPlanet();
                String systemOwner = systemMainPlanet.getControllingFaction().getFactionName();
                String systemName = system.getName();
                StellarPosition systemPosition = system.getPosition();
                Double resourceValue = system.getResourceValue();
                
                double resourceDepot = systemMainPlanet.getResourceDepot();
                int fortificationLevel = systemMainPlanet.getFortificationLevel();
                PlanetLevel planetLevel = systemMainPlanet.getPlanetLevel();
                
                int factory = systemMainPlanet.getFactorySize();
                
                Star star = system.getStars().get(new Integer(1));
                
                line = CSVConstants.systemKey + "," + systemOwner + "," + systemName + "," 
                        + systemPosition.getX() + "," + systemPosition.getY() + "," + resourceValue + ","
                        + resourceDepot + "," + fortificationLevel + "," + planetLevel.toString()
                        + "," + factory + "," + star.getType() + "," + star.getSubtype();
                writer.write(line);
                writer.newLine();
            }
            
            //Step 6: Write the warships 
            line = CSVConstants.headingKey + ",warship owner,warship name,warship class name,battle value,"
                    + "position x,position y,cost,force transport capacity,carrier capacity,damage level,"
                    + "functional";
            writer.write(line);
            writer.write(endOfLine);
            
            for (Map.Entry<String, Faction> factionEntry : factions.entrySet()) {
                String factionName = factionEntry.getKey();

                for(Warship warship : factionEntry.getValue().getWarships()){
                    String warshipName = warship.getName();
                    String warshipClassName = warship.getWarshipClass().getWarshipClassName();
                    double battleValue = warship.getBattleValue();
                    long cost = warship.getWarshipClass().getCost();
                    int transportCapacity = warship.getWarshipClass().getForceTransportCapacity();
                    int carrierCapacity = warship.getWarshipClass().getCarrierCapacity();
                    double damage = warship.getDamageLevel();
                    SpaceCraftStatus functional = warship.getStatus();
                    
                    line = CSVConstants.warshipKey + ","+ factionName +","
                    + warshipName + "," 
                    + warshipClassName + "," 
                    + battleValue + "," 
                    + "," 
                    + "," 
                    + cost + "," 
                    + transportCapacity + "," 
                    + carrierCapacity + "," 
                    + damage + "," 
                    + functional.toString();
                    writer.write(line);
                    writer.newLine();
                }
            }
                
            //Step 7: Write the military units
            //Algorithm: Go through the map to place the units
            //At this point this implies that no units can begin the game aboard a warship.
            
            line = CSVConstants.headingKey + ",force owner,force name,commander name,loyalty,"
            + "star system,air rating,ground rating,supply requirement,supply stock,experience,"
            + "force type";
            writer.write(line);
            writer.newLine();
            
            for (StarSystem system : starMap.getAllStarSystems()) {
                InhabitedWorld systemMainPlanet = system.getPrimaryPlanet();
                String systemName = system.getName();
                
                for(MilitaryForce militaryForce  : systemMainPlanet.getMilitaryForces()){
                    String forceOwner = militaryForce.getOwner().getFactionName();
                    String forceName = militaryForce.getName();
                    String commander = militaryForce.getCommandingOfficer();
                    String loyalty = militaryForce.getLoyalty().toString();
                    
                    double airRating = militaryForce.getAirRating();
                    double groundRating = militaryForce.getGroundRating();
                    int supplyRequirement = militaryForce.getSupplyRequirement();
                    int supplyStock = militaryForce.getSupplyPointsInStock();
                    double experience = militaryForce.getExperiencePoints();
                    
                    String forceType = CSVConstants.regularForce;
                    if(militaryForce instanceof GarrisonForce){
                        forceType = CSVConstants.garrisonForce;
                    } else if (militaryForce instanceof MercenaryForce){
                        forceType = CSVConstants.mercenaryForce;
                    }
                    
                    line = CSVConstants.militaryUnitKey + "," 
                            + forceOwner + "," 
                            + forceName + "," 
                            + commander + "," 
                            + loyalty + "," 
                            + systemName + ","
                            + airRating + "," 
                            + groundRating + "," 
                            + supplyRequirement + "," 
                            + supplyStock + "," 
                            + experience + ","
                            + forceType;
                    writer.write(line);
                    writer.newLine();
                }
            }
            writer.flush();
            
            //Step 8: write the production items on the market 
            for(ProductionItem item : ProductionItemSingleton.getProductionItemSingleton().getAvailableItems()){
                line = CSVConstants.productionItemKey + ","
                        + item.getName() + ","
                        + item.getAir() + ","
                        + item.getGround() + ","
                        + item.getCost() + ","
                        + item.getTime();
                writer.write(line);
                writer.newLine();
            }
            
            //Step 9: write the production orders 
            for (Map.Entry<String, Faction> factionEntry : factions.entrySet()) {
                // Faction attributes
                String factionName = factionEntry.getKey();
                List<StarSystem> factorySystems = factionEntry.getValue().getFactorySystems();
                
                for(StarSystem system : factorySystems){
                    Factory factory= system.getPrimaryPlanet().getFactory();
                    for (ProductionLine productionLine : factory.getProductionLines()){
                        line = CSVConstants.productionOrderKey + ","
                                + factionName + ","
                                + system.getName() + ","
                                + productionLine.getItem().getName() + ","
                                + productionLine.getTurnsRemaining();
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }
            
            //Step 10: write the faction orders with a long duration
            for (Map.Entry<String, Faction> factionEntry : factions.entrySet()) {
                // Faction attributes
                String factionName = factionEntry.getKey();
                Set<FactionOrder> factionOrders = factionEntry.getValue().getFactionOrders();
                
                for(FactionOrder order : factionOrders){
                    if(order instanceof BuildFortificationOrder){
                        line = CSVConstants.factionOrderKey + ","
                                + CSVConstants.buildFortification + ","
                                + factionName + ","
                                + ((BuildFortificationOrder)order).getSystemName() + ","
                                + ((BuildFortificationOrder)order).getDuration();
                        writer.write(line);
                        writer.newLine();
                    } else if (order instanceof BuildFactoryOrder){
                        line = CSVConstants.factionOrderKey + ","
                                + CSVConstants.buildFactory + ","
                                + factionName + ","
                                + ((BuildFactoryOrder)order).getSystemName() + ","
                                + ((BuildFactoryOrder)order).getDuration();
                        writer.write(line);
                        writer.newLine();
                    }
                    
                }
            }
            
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
