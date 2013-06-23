package ca.ott.al.starmap;

import java.util.SortedMap;
import java.util.TreeMap;

import ca.ott.al.starmap.console.ConsoleMenu;
import ca.ott.al.starmap.core.GameCore;
import ca.ott.al.starmap.core.map.StarMap;
import ca.ott.al.starmap.file.FactionBuilder;
import ca.ott.al.starmap.file.ForceBuilder;
import ca.ott.al.starmap.file.MapBuilder;

public class ConsoleRunner {

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Starting StarMap ConsoleRunner. \nCreating GameCore instance.");
        GameCore gameCore = GameCore.getGameCore();
        
        //Do the StarMap
        System.out.println("Creating the Starmap and loading its data.");
        StarMap starMap = gameCore.getStarMap();
        MapBuilder.execute(starMap);
        MapBuilder.executeReadOfPopulousWorlds(gameCore);        
        MapBuilder.populateProximityGraph(gameCore);

        //Do the Factions
        System.out.println("Creating the Factions.");
        FactionBuilder.execute(gameCore);
        
        //Do the Faction Maps
        System.out.println("Creating a map of the factions' territory.");
        MapBuilder.executeFactionMap(gameCore);
        
        //Create the Units
        System.out.println("Creating the units.");
        ForceBuilder.executeUnitBuild3039(gameCore);
        
        System.out.println("Running the home menu.");        
        runHomeMenu(gameCore);
    }

    private static void runHomeMenu(GameCore gameCore){
        SortedMap<String,String> menuItems = new TreeMap<String,String>();
        menuItems.put("F", "Select a Faction");
        menuItems.put("V", "View the Star Map");
        menuItems.put("X", "Exit the Game");
        
        ConsoleMenu homeMenu = new ConsoleMenu("Home Menu", menuItems,
                "Enter your selection: ");
        String selection = "empty"; 
        
        while(!selection.equalsIgnoreCase("x")){
            selection = homeMenu.deployMenu();
            
            if(selection.equalsIgnoreCase("x")){
                return;
            } else if (selection.equalsIgnoreCase("f")){
                runFactionSelectionMenu(gameCore);
            } else if (selection.equalsIgnoreCase("v")){
                runProduceViewableMap(gameCore);
            }
        }

    }
    
    private static void runFactionSelectionMenu(GameCore gameCore){
        
    }
    
    private static void runProduceViewableMap(GameCore gameCore){
        //TODO: write this in the weeks ahead.
    }
}
