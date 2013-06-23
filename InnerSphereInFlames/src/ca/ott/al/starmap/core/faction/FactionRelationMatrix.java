package ca.ott.al.starmap.core.faction;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A single instance of this class will encapsulate the relationships between the factions.
 * A Map entry is to hold the status of the relationship if it is other than neutral.
 * Faction names are unique string so they must be comparable.
 * The key to the map is a String of the form factionName1,factionName2 where the
 * faction name appear in compareTo order.  Thus the key for any two factions should 
 * always be the same and only one entry should ever exist in this table.
 * @author al
 * 
 */
public class FactionRelationMatrix implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 7648984968408335871L;


    public enum Relationship {
        neutral, allied, hostile, self
    }
    
    private Map<String, Relationship> table;
    
    public FactionRelationMatrix(){
        table = new HashMap<String, Relationship>();
    }
    
    /**
     * Returns the status of relations between two factions.  If the table does
     * not contain an entry for those factions, the system assumes neutrality.
     * @param faction1
     * @param faction2
     * @return
     */
    public Relationship getFactionRelationshipStatus(Faction faction1, Faction faction2){
        //first check to see if you have the same faction
        if(faction1 == faction2){
            return Relationship.self;
        }
        
        String key = composeKey(faction1, faction2);
        Relationship result = table.get(key);
        if(result == null){
            result = Relationship.neutral;
        }
        return result;
    }
    
    /**
     * 
     * @param faction1
     * @param faction2
     * @param status
     */
    public void setFactionRelationship(Faction faction1, Faction faction2, Relationship status){
        String key = composeKey(faction1, faction2);
        table.put(key, status);
    }
    
    /**
     * 
     * @return
     */
    public String[][] getMatrixForCSV(){
        String[][] tableAsArray = new String[table.size()][3];
        
        int i = 0;
        for(Map.Entry<String, Relationship> entry : table.entrySet()){
            String[] factions = entry.getKey().split(",");
            tableAsArray[i][0] = factions[0];
            tableAsArray[i][1] = factions[1];
            tableAsArray[i][2] = entry.getValue().name();
            i++;
        }
        
        return tableAsArray;
    }
    
    
    // ------------------------------------------------------------------------
    private String composeKey(Faction faction1, Faction faction2){
        String factionName1 = faction1.getFactionName();
        String factionName2 = faction2.getFactionName();
        
        String key = "not a correct key";
        if(factionName1.compareTo(factionName2) < 0){
            key = factionName1+ ","+ factionName2;
        } else if (factionName1.compareTo(factionName2) > 0){
            key = factionName2+ ","+ factionName1;
        }
        
        return key;
    }
}
