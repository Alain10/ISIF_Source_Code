package ca.ott.al.starmap.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import ca.ott.al.starmap.core.GameCore;
import ca.ott.al.starmap.core.faction.Faction;
import ca.ott.al.starmap.core.map.StarSystem;
import ca.ott.al.starmap.core.unit.MilitaryForce;
import ca.ott.al.starmap.core.unit.MilitaryForce.UnitLoyalty;

public class ForceBuilder {

    public static void executeUnitBuild3039(GameCore gameCore) {
        //Construct all the units from the resource csv
//        File file = new File(
//        "C:/dev/workspaceInnerSphere/InnerSphereInFlames/resources/unitsSetup3039_3.txt");
        File file = new File(
        "/home/al/isifworkspace/InnerSphereInFlames/resources/unitsSetup3039_3.txt");
        
        String factionName, forceName, systemName , airString, groundString;
        String supplyString, leadershipString, experienceString;

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            bufferedReader.readLine();
            traverse: while(bufferedReader.ready()){
                String line = bufferedReader.readLine();
                if(line.endsWith("#####")){
                    //Skip this line
                    continue traverse;
                }
                String lineNoQuotes = line.replaceAll("\"", "");
                String[] splitLine= lineNoQuotes.split("#");

                factionName = splitLine[0];
                forceName = splitLine[1];
                systemName = splitLine[2];
                airString = splitLine[3];
                groundString = splitLine[4];
                supplyString = splitLine[5];
                leadershipString = splitLine[6];
                experienceString = splitLine[7];
                
                Faction faction = gameCore.getFactionByName(factionName);
                if(faction == null){
                    System.out.println("Cannot find faction: "+ factionName
                            + "for force:" + forceName);
                    continue traverse;
                }
                StarSystem location = gameCore.getStarMap().getStarSystemByName(systemName);
                if(location == null){
                    System.out.println("Cannot find location: "+ systemName
                            + " for force: "+ forceName);
                    continue traverse;
                }
                Double airRating = Double.valueOf(airString);
                
                //Correct for a flaw in the commas of the 3039 data....
                if(groundString.contains(",")){
                    groundString = groundString.replace(",", ".");
                    
                }
                Double groundRating = Double.valueOf(groundString);
                if(groundRating <3){
                    groundRating = groundRating * 1000;
                }
                
                String supply[] = supplyString.split("/");
                Integer supplyReg = Integer.valueOf(supply[0]);
                Integer supplyStock = Integer.valueOf(supply[1]);
                
                Integer leadership = Integer.valueOf(leadershipString);
                String exp[] = experienceString.split("/");
                Integer expPoints = Integer.valueOf(exp[1]);
                
                MilitaryForce militaryForce = new MilitaryForce(faction, forceName,
                        "unknown", airRating, groundRating, supplyReg, supplyStock, 
                        expPoints, UnitLoyalty.reliable);
                //These two operation go hand-in-hand
                faction.addMilitaryForce(militaryForce);
                location.getPrimaryPlanet().addMilitaryForce(militaryForce);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
