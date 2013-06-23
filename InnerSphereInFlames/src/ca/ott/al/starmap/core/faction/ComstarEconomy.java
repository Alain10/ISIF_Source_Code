package ca.ott.al.starmap.core.faction;

import java.util.Map;

import ca.ott.al.starmap.core.map.StarSystem;

public class ComstarEconomy extends Economy {

    /**
     * 
     */
    private static final long serialVersionUID = 3501313846404790828L;
    
    protected int comstarNetworkSize;
    
    public ComstarEconomy(int startEconomicStrength, 
            int economicMinimumPercentile, int economicMaximumPercentile, 
            double factionSpecificEconomicModifier, double tradeModifier, 
            double cash, int hpgNetSize) {
        super(startEconomicStrength, economicMinimumPercentile, 
                economicMaximumPercentile, factionSpecificEconomicModifier,
                tradeModifier, cash);
        comstarNetworkSize = hpgNetSize;
    }
    
    @Override
    public double calculateRawResourcePoints(Map<StarSystem, Double> factionTerritory){
        double resourcePointTotal = 0;
        
        for(StarSystem system: factionTerritory.keySet()){
            double systemFraction = factionTerritory.get(system);
            double fullSystemValue = system.getResourceValue();
            double systemValue = fullSystemValue * systemFraction;
            resourcePointTotal += systemValue;
        }
        resourcePointTotal += comstarNetworkSize/100;
        return resourcePointTotal;
    }

    public int getComstarNetworkSize() {
        return comstarNetworkSize;
    }

    public void setComstarNetworkSize(int comstarNetworkSize) {
        this.comstarNetworkSize = comstarNetworkSize;
    }    
}
