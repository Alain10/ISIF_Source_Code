package ca.ott.al.starmap.core.map;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import ca.ott.al.starmap.core.util.StellarPosition;

public class StarMap implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 6654773968358025993L;
    
    //map that is indexed by name:
    //Assumes all system names are unique Strings
    private Map<String, StarSystem> starSystemNameIndex;
    private Map<StellarPosition, StarSystem> starSystemLocationIndex;
    
    public StarMap() {
        starSystemNameIndex = new TreeMap<String, StarSystem>();
        starSystemLocationIndex = new TreeMap<StellarPosition, StarSystem>();
    }

    public void addStarSystem(StellarPosition position, StarSystem system) {
        starSystemNameIndex.put(system.getName(), system);
        starSystemLocationIndex.put(position, system);
    }

    public StarSystem getStarSystemByName(String systemName){
        return starSystemNameIndex.get(systemName);
    }
    
    public Collection<StarSystem> getAllStarSystems(){
        return starSystemNameIndex.values();
    }
    
    public StarSystem getStarSysmemByPosition(StellarPosition position) {
        return starSystemLocationIndex.get(position);
    }

    public String toISIFString() {
        StringBuffer buf = new StringBuffer();
        for(StarSystem system: starSystemNameIndex.values()){
            buf.append("---------------------------------------------------------------------\n");
            buf.append(system.getName());
            buf.append("; ");
            buf.append(system.getPosition().getX());
            buf.append("; ");
            buf.append(system.getPosition().getY());
            buf.append("\n");
            
            if(!system.getSystemsWithin30LY().isEmpty()){
                buf.append("-----");
                buf.append("Systems within 30 LY:----------------------------------------------");
                buf.append("\n");
                for(Map.Entry<StarSystem, Double> entry : system.
                        getSystemsWithin30LY().entrySet()){
                    StarSystem destinationSystem = entry.getKey();
                    buf.append("\t");
                    buf.append(destinationSystem.getName());
                    buf.append("\tX: ");
                    buf.append(destinationSystem.getPosition().getX());
                    buf.append("\tY: ");
                    buf.append(destinationSystem.getPosition().getY());
                    buf.append("\tDistance: ");
                    buf.append(entry.getValue());
                    buf.append("\n");
                }
            }
            
            if(!system.getSystemsWithin60LY().isEmpty()){
                buf.append("-----");
                buf.append("Systems within 60 LY:----------------------------------------------");
                buf.append("\n");
                for(Map.Entry<StarSystem, Double> entry : system.
                        getSystemsWithin60LY().entrySet()){
                    StarSystem destinationSystem = entry.getKey();
                    buf.append("\t");
                    buf.append(destinationSystem.getName());
                    buf.append("\tX: ");
                    buf.append(destinationSystem.getPosition().getX());
                    buf.append("\tY: ");
                    buf.append(destinationSystem.getPosition().getY());
                    buf.append("\tDistance: ");
                    buf.append(entry.getValue());
                    buf.append("\n");
                }
            }            
        }
        return buf.toString();
    }
    
    /**
     * Tasks the starMap to calculate which systems are within 30 light years
     * and 60 light years for all the systems on the map.
     * This operation populates two data structures for each system.
     * This method is expected to be slow since the processing is brute-force.
     * It should be a roughly O(2000)^2 complexity.
     * @param gameCore
     */
    public void populateStarSystemProximityMaps(){
        for(Map.Entry<String, StarSystem> baseEntry : starSystemNameIndex.entrySet()){
            StarSystem baseSystem = baseEntry.getValue();
            
            dest: for(Map.Entry<String, StarSystem> destEntry : starSystemNameIndex.entrySet()){
                StarSystem destinationSystem = destEntry.getValue();
                if(destinationSystem == baseSystem){
                    //These two are the same object, skip to the next 
                    continue dest;
                }
                //Apply Pythagore's theorem to calculate the distance.
                double xDifference = baseSystem.getPosition().getX()
                        - destinationSystem.getPosition().getX();
                double yDifference = baseSystem.getPosition().getY()
                        - destinationSystem.getPosition().getY();
                double sqaredSum = xDifference * xDifference + yDifference * yDifference;
                double distance = Math.sqrt(sqaredSum);
                if(distance < 30){
                    baseSystem.addStarSystemWithin30LY(destinationSystem, distance);
                }
                if(distance < 60){
                    baseSystem.addStarSystemWithin60LY(destinationSystem, distance);
                }
            }
        }
    }

    /**
     * 
     * @param clickMapCoordX
     * @param clickMapCoordY
     * @return The starSystem nearest to a map coordinate
     */
    public StarSystem getNearestStarSystem(double clickMapCoordX,
            double clickMapCoordY) {
        //I wish I could use a logarithm search but I do not have the right 
        //kind of indexing structure.  So will do sequential search since the
        //Inner Sphere should only have about ~2500 systems
        StarSystem selectedSystem = null;
        double distance;
        
        selectedSystem = starSystemNameIndex.values().iterator().next();
        distance = calculateDistanceUsePythagore(clickMapCoordX, 
                clickMapCoordY, selectedSystem.getPosition().getX(),
                selectedSystem.getPosition().getY());

        for(StarSystem searchSystem: starSystemNameIndex.values()){
            if(searchSystem != selectedSystem){
                double searchX = searchSystem.getPosition().getX();
                double searchY = searchSystem.getPosition().getY();

                double searchDistance = calculateDistanceUsePythagore(
                        clickMapCoordX, clickMapCoordY, searchX, searchY);
                if(searchDistance < distance){
                    selectedSystem = searchSystem;
                    distance = searchDistance;
                }
            }
        }
        return selectedSystem;
    }
    
    /**
     * Calculates the distance between two points 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static double calculateDistanceUsePythagore(double x1, double y1, double x2, double y2){
        double xDifference = x1 - x2;
        double yDifference = y1 - y2;
        double sqaredSum = xDifference * xDifference + yDifference * yDifference;
        double distance = Math.sqrt(sqaredSum);
        return distance;
    }

    public static double calculateDistance(StarSystem system1, StarSystem system2){
        return calculateDistanceUsePythagore(system1.getPosition().getX(), system1.getPosition().getY(), 
                system2.getPosition().getX(), system2.getPosition().getY());
    }
    
    /**
     * @return An array with four numbers:
     * The minimum x value found on the starmap at index 0
     * The maximum x value at index 1
     * The minimum y at index 2
     * the maximum Y at index 3
     */
    public int[] getStarMapExtremities() {
        int[] ext = new int[4];
        ext[0] = 0; ext[1] = 0; ext[2] = 0; ext[3] = 0;
        Set<StellarPosition> positionSet = starSystemLocationIndex.keySet();
        for(StellarPosition position : positionSet){
            if(position.getX() < ext[0]){
                ext[0] = (int)position.getX();
            }
            else if(position.getX() > ext[1]){
                ext[1] = (int)position.getX();
            }
            
            if(position.getY() < ext[2]){
                ext[2] = (int)position.getY();
            }
            else if(position.getY() > ext[3]){
                ext[3] = (int)position.getY();
            }
        }
        return ext;
    }
}
