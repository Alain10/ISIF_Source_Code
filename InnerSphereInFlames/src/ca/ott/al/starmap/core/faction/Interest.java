package ca.ott.al.starmap.core.faction;

import java.io.Serializable;

import ca.ott.al.starmap.core.map.StarSystem;

public class Interest implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 5572338985707120302L;
    
    private String interestName;
    private Personality interestLeader;
    
    private int popularity;

    private StarSystem interestHomeworld;    

    public Interest(String interestName, Personality interestLeader) {
        super();
        this.interestName = interestName;
        this.interestLeader = interestLeader;
    }

    public Personality getInterestLeader() {
        return interestLeader;
    }

    public void setInterestLeader(Personality interestLeader) {
        this.interestLeader = interestLeader;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public String getInterestName() {
        return interestName;
    }

    public void setInterestHomeworld(StarSystem interestHomeworld) {
        this.interestHomeworld = interestHomeworld;
    }

    public StarSystem getInterestHomeworld() {
        return interestHomeworld;
    }
    
    
    
}
