package ca.ott.al.starmap;

import java.util.Map;

import ca.ott.al.starmap.core.GameCore;
import ca.ott.al.starmap.core.faction.Faction;
import ca.ott.al.starmap.core.map.InhabitedWorld;
import ca.ott.al.starmap.core.map.StarMap;
import ca.ott.al.starmap.core.map.StarSystem;
import ca.ott.al.starmap.file.FactionBuilder;
import ca.ott.al.starmap.file.ForceBuilder;
import ca.ott.al.starmap.file.MapBuilder;


public class TestRunner {

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Starting StarMap TestRunner.  Creating GameCore instance");
        GameCore gameCore = GameCore.getGameCore();
        
        //Do the StarMap
        StarMap starMap = gameCore.getStarMap();
        MapBuilder.execute(starMap);
        MapBuilder.executeReadOfPopulousWorlds(gameCore);        
        MapBuilder.populateProximityGraph(gameCore);
        printStarMap(starMap);

        //Do the Factions
        FactionBuilder.execute(gameCore);
        
        //Do the Faction Maps
        MapBuilder.executeFactionMap(gameCore);
        
        //Create the Units
        ForceBuilder.executeUnitBuild3039(gameCore);
        
        //Print everything
        printAllModelData(gameCore);
    }

    private static void printStarMap(StarMap starMap){
        //Print everything from a fully populated star map 
        System.out.println(starMap.toString());
        System.out.print(starMap.toISIFString());
    }

    private static void printAllModelData(GameCore gameCore){
        for (Faction faction :gameCore.getFactions().values()){
            System.out.println(faction.getFactionName());
            System.out.println("Leader: "+ faction.getFactionLeader().getName());
            
            Map<StarSystem,Double> territory = faction.getFactionTerritory();
            for(Map.Entry<StarSystem, Double> entry: territory.entrySet()){
                InhabitedWorld world = entry.getKey().getPrimaryPlanet();
                System.out.print(world.toISIFString());
            }
        }
    }
    
}
