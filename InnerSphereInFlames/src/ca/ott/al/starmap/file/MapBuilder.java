package ca.ott.al.starmap.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

import ca.ott.al.starmap.core.GameCore;
import ca.ott.al.starmap.core.faction.Faction;
import ca.ott.al.starmap.core.map.InhabitedWorld;
import ca.ott.al.starmap.core.map.Star;
import ca.ott.al.starmap.core.map.StarMap;
import ca.ott.al.starmap.core.map.StarSystem;
import ca.ott.al.starmap.core.unit.GarrisonForce;
import ca.ott.al.starmap.core.unit.MilitaryForce.UnitLoyalty;
import ca.ott.al.starmap.core.util.StellarPosition;
import ca.ott.al.starmap.exception.InvalidParameterValueException;

public class MapBuilder {

    /**
     * Produces the basic map from csv data
     * @param starMap
     */
    public static void execute(StarMap starMap) {
        //File file = new File("C:/dev/workspaceInnerSphere/InnerSphereInFlames/resources/MassagedMapData.csv");
        File file = new File("/home/al/isifworkspace/InnerSphereInFlames/resources/MassagedMapData.csv");
        
        String factionName, systemName, xCoordinate, yCoordinate;
        
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            bufferedReader.readLine();
            while(bufferedReader.ready()){
                String line = bufferedReader.readLine();
                String lineNoQuotes = line.replaceAll("\"", "");
                String[] splitLine= lineNoQuotes.split("#");
                
                if(splitLine[0].compareTo("")!=0){
                    factionName = splitLine[0];
                }
                systemName = splitLine[1];
                xCoordinate = splitLine[2];
                yCoordinate = splitLine[3];
                
                xCoordinate = xCoordinate.replace(",", ".");
                yCoordinate = yCoordinate.replace(",", ".");
                
                Double xAsDouble = Double.valueOf(xCoordinate);
                Double yAsDouble = Double.valueOf(yCoordinate);
                
                StellarPosition position = new StellarPosition(xAsDouble, yAsDouble);
                Star star = new Star(Star.Type.G, 2);
                StarSystem system = new StarSystem(position, systemName, star);
                InhabitedWorld world = new InhabitedWorld(systemName, 0.5);
                system.getPlanets().put(1, world);
                starMap.addStarSystem(position, system);
            }
            
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     * Produces the maps for the territories of the factions
     * These Map draw references into the main starMap
     * @param gameCore
     */
    public static void executeFactionMap(GameCore gameCore) {
        //File file = new File(
        //        "C:/dev/workspaceInnerSphere/InnerSphereInFlames/resources/MassagedMapData.csv");
        File file = new File(
                "/home/al/isifworkspace/InnerSphereInFlames/resources/MassagedMapData.csv");
        
        
        
        String factionName, systemName;
        
        Map<String, Faction> factions = gameCore.getFactions();
        
        StarMap starMap = gameCore.getStarMap();
        if(starMap == null){
            return;
        }
        
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            bufferedReader.readLine();
            while(bufferedReader.ready()){
                String line = bufferedReader.readLine();
                String lineNoQuotes = line.replaceAll("\"", "");
                String[] splitLine= lineNoQuotes.split("#");
                
                if(splitLine[0].compareTo("")!=0){
                    factionName = splitLine[0];
                }else{
                    return;
                }
                
                Faction currentFaction = factions.get(factionName);
                if(currentFaction == null){
                    return;
                }
                
                systemName = splitLine[1];
                StarSystem starSystem = gameCore.getStarMap().getStarSystemByName(systemName);
                if (starSystem != null){
                    currentFaction.addStarSystem(starSystem, (double)1);
                    starSystem.getPrimaryPlanet().setControllingFaction(currentFaction);
                    
                    Random random = new Random();
                    double airRating = Math.round(40 + random.nextInt(20));
                    double groundRating = Math.round(200 + random.nextInt(100));
                    
                    //Add a garrison 
                    InhabitedWorld world = starSystem.getPrimaryPlanet();
                    GarrisonForce garrison = new GarrisonForce(currentFaction,
                            starSystem.getName()+" Planetary Garrison", 
                            "unknown commander", airRating, groundRating, 1, 6,
                            0, UnitLoyalty.reliable, false);
                    world.addMilitaryForce(garrison);
                }
            }
            
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidParameterValueException e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     * Add some extra detail to the starMap from the populous worlds file
     * @param gameCore
     */
    public static void executeReadOfPopulousWorlds(GameCore gameCore) {
        //File file = new File(
        //        "C:/dev/workspaceInnerSphere/InnerSphereInFlames/resources/PopulousWorlds.csv");
        File file = new File(
                "/home/al/isifworkspace/InnerSphereInFlames/resources/PopulousWorlds.csv");
        
        String houseName, systemName, climateName, population, description;
        StarMap starMap = gameCore.getStarMap();
        if(starMap == null){
            return;
        }
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            bufferedReader.readLine();
            while(bufferedReader.ready()){
                String line = bufferedReader.readLine();
                String lineNoQuotes = line.replaceAll("\"", "");
                String[] splitLine= lineNoQuotes.split("#");
                
                if(splitLine[0].compareTo("")!=0){
                    houseName = splitLine[0];
                }
                systemName = splitLine[1].trim();
                climateName = splitLine[2].trim();
                population = splitLine[3].trim();
                description = splitLine[4].trim();
                
                StarSystem system = gameCore.getStarMap().getStarSystemByName(systemName);
                if(system != null){
                    InhabitedWorld world = system.getPrimaryPlanet();
                    world.setDescription(description);
                    world.setPopulation(Long.parseLong(population));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    /**
     * Tasks the starMap to calculate which systems are within 30 light years
     * and 60 light years for all the systems on the map.
     * This operation populates two data structures for each ssytem.
     * @param gameCore
     */
    public static void populateProximityGraph(GameCore gameCore) {
        StarMap starMap = gameCore.getStarMap();
        starMap.populateStarSystemProximityMaps();
    }

}
