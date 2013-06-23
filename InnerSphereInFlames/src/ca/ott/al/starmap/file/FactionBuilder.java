package ca.ott.al.starmap.file;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import ca.ott.al.starmap.core.GameCore;
import ca.ott.al.starmap.core.faction.ComstarEconomy;
import ca.ott.al.starmap.core.faction.Economy;
import ca.ott.al.starmap.core.faction.Faction;
import ca.ott.al.starmap.core.faction.Personality;
import ca.ott.al.starmap.core.faction.TechnologyTable;
import ca.ott.al.starmap.core.map.InhabitedWorld.PlanetLevel;
import ca.ott.al.starmap.core.map.StarSystem;


public class FactionBuilder {
    
    public static void execute(GameCore gameCore){
        //File file = new File(
        //        "C:/dev/workspaceInnerSphere/InnerSphereInFlames/resources/FactionData.csv");
        File file = new File(
                "/home/al/isifworkspace/InnerSphereInFlames/resources/FactionData.csv");
        
        String factionName, leaderName, capitalName ,colorName;
        String currentEcono, econoMin, econoMax, econoMod, hpgNetSize;
        
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            bufferedReader.readLine();
            while(bufferedReader.ready()){
                String line = bufferedReader.readLine();
                String lineNoQuotes = line.replaceAll("\"", "");
                String[] splitLine= lineNoQuotes.split("#");
                
                factionName = splitLine[0];
                leaderName = splitLine[1];
                capitalName = splitLine[2];
                colorName = splitLine[3];
                currentEcono = splitLine[4];
                econoMin = splitLine[5];
                econoMax = splitLine[6];
                econoMod = splitLine[7];
                hpgNetSize = splitLine[8];
                
                Personality leader = new Personality(leaderName, 5, 100);
                StarSystem capital = gameCore.getStarMap().getStarSystemByName(capitalName);
                if(capital != null){
                    capital.getPrimaryPlanet().setResourceValue((double)10);
                    capital.getPrimaryPlanet().setPlanetLevel(PlanetLevel.capital);
                }
                //TODO: Fix the Colors later
                Color color = convertToColor(colorName);
                
                Economy economy = new Economy(Integer.parseInt(currentEcono), 
                        Integer.parseInt(econoMin), Integer.parseInt(econoMax),
                        Double.parseDouble(econoMod), 4, 50);
                
                //If this faction is a Comstar faction, it needs a Comstar Economy
                if(Integer.parseInt(hpgNetSize) > 0 ){
                    economy = new ComstarEconomy(Integer.parseInt(currentEcono), 
                        Integer.parseInt(econoMin), Integer.parseInt(econoMax),
                        Double.parseDouble(econoMod),4,50,Integer.parseInt(hpgNetSize));
                }
                
                TechnologyTable techTable = new TechnologyTable(0, 0, 0);
                Faction faction = new Faction(factionName, leader, capital, economy,
                        techTable, color, true);
                gameCore.getFactions().put(factionName, faction);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public static void initializeEconomicDataForTurnOne(GameCore gameCore) {
        for(Faction faction: gameCore.getFactions().values()){
            faction.getEconomy().calculateEconomicStrength();
            double bank = faction.getEconomy().calculateTotalResourcePoints(
                    faction.getFactionTerritory());
            faction.getEconomy().setResourceBank(bank);
        }
    }
    
    private static Color convertToColor(String colorString){
        if(colorString.equalsIgnoreCase("red")){
            return Color.red;
        } else if(colorString.equalsIgnoreCase("yellow")){
            return Color.yellow;
        } else if(colorString.equalsIgnoreCase("pink")){
            return Color.pink;
        } else if(colorString.equalsIgnoreCase("green")){
            return Color.green;
        } else if(colorString.equalsIgnoreCase("blue")){
            return Color.blue;
        } else if(colorString.equalsIgnoreCase("magenta")){
            return Color.magenta;
        } else {
            return Color.gray;
        }
        
    }
}
