package ca.ott.al.starmap.core.faction;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import ca.ott.al.starmap.core.map.Factory;
import ca.ott.al.starmap.core.map.InhabitedWorld;
import ca.ott.al.starmap.core.map.StarSystem;
import ca.ott.al.starmap.core.order.faction.FactionOrder;
import ca.ott.al.starmap.core.order.factory.ProductionItem;
import ca.ott.al.starmap.core.order.factory.ProductionLine;
import ca.ott.al.starmap.core.unit.MilitaryForce;
import ca.ott.al.starmap.core.unit.Reinforcement;
import ca.ott.al.starmap.core.unit.StarMapUnit;
import ca.ott.al.starmap.dice.Dice;

public class Economy implements Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = 4963782956241004899L;

    public static enum TradingPartnerStatus{noTrade, restrictedTrade, freeTrade, allyTrade};
    public static enum TradingPartnerSize {minorPower, majorPower};
    
    /**
     * A reference to the faction. This field must be populated because many use cases
     * require access to the faction's orders, units, territory etc...
     */
    protected Faction faction;
    /**
     * The total in raw resources that the faction has at the beginning of a turn
     */
    protected double resourceBank; 
    /**
     * The percentage of the economy allocated to civilian spending this turn
     * Expressed as an integer in the range 0 to 100 
     * Default starting value is 20
     */
    protected int civilianSpendingPercentile;

    //The following are attributes that affect the economic strength
    protected double currentEconomicStrength;
    protected int economicMinimumPercentile;
    protected int economicMaximumPercentile;
    protected int numberOfWorldsInConflict;
    private boolean underInterdiction = false;
    protected double factionSpecificEconomicModifier;
    protected double tradeModifier;
    protected Map<Faction, TradingPartner> tradingPartners;
    
    /**
     * Used only for display purposes, do not access for any other reason
     */
    protected double latestEconomicModifier;
    /**
     * Used only for display purposes, do not access for any other reason
     */
    protected double latestEconomicStrengthChange;
    

    /**
     * @param faction
     * @param startEconomicStrength
     * @param economicMinimumPercentile
     * @param economicMaximumPercentile
     * @param factionSpecificEconomicModifier
     * @param cash 
     */
    public Economy(int startEconomicStrength,
            int economicMinimumPercentile,
            int economicMaximumPercentile, double factionSpecificEconomicModifier,
            double tradeModifier, double cash) {
        this.currentEconomicStrength = startEconomicStrength; //set by scenario
        this.economicMinimumPercentile = economicMinimumPercentile;
        this.economicMaximumPercentile = economicMaximumPercentile;
        this.factionSpecificEconomicModifier = factionSpecificEconomicModifier;
        this.tradeModifier = tradeModifier;
        resourceBank = cash;
        numberOfWorldsInConflict=0;
        civilianSpendingPercentile=20;
        tradingPartners = new HashMap<Faction, Economy.TradingPartner>();
    }
    
    //METHODS==================================================================
    /**
     * @returns The amount of resources that are still available this turn 
     * after deduction of the cost of all the standing orders.
     * 
     * Note: This method floors its result for display purposes.
     */
    public double calculateAvailableResourcePoints(){
        double availableBank = calculateTotalResourcePoints(faction.getFactionTerritory());
        availableBank -= calculateExpenditures();
        return availableBank;
    }
    
    public double calculateExpenditures(){
        double expenditures = 0;
        //Deduct civilian spending
        double civilianSpending = (double)civilianSpendingPercentile/100;
        double civilianExpense = calculateRawResourcePoints(faction.getFactionTerritory()) *(civilianSpending);
        expenditures += civilianExpense;
        
        //TODO: all the deductions...
        //Deduct cost of construction (depot, fortifications, factories)
        //Deduct cost of research orders
        //Deduct cost of security spending
        SortedSet<FactionOrder> factionOrders = faction.getFactionOrders();
        for(FactionOrder order : factionOrders){
            expenditures += order.getCost();
        }
        //Deduct cost of unit orders such as Dig In(does not include transport pool or movement costs)
        Set<MilitaryForce> units = faction.getMilitaryForces();
        for(MilitaryForce force: units){
            if(force.getOrders().containsDigIn()){
                expenditures += 2.5;
            }
        }
        
        //Deduct cost of espionage orders
        
        //Deduct cost of production orders
        expenditures += calculateProductionExpenditures();
        
        //Deduct cost of command circuits construction and maintenance
        
        //Deduct cost of transport pool
        expenditures += faction.getTransportPool().getTransportSpendingThisTurn();
        
        //availableBank = Math.floor(availableBank);
        return expenditures;
    }
    
    public double calculateProductionExpenditures(){
        double total = 0;
        List<StarSystem> factorySystems = faction.getFactorySystems();
        for(StarSystem factorySystem : factorySystems){
            InhabitedWorld world = factorySystem.getPrimaryPlanet();
            Factory factory = world.getFactory();
            for (ProductionLine line : factory.getProductionLines()){
                ProductionItem item = line.getItem();
                
                //Only add this cost if the item is entering production this turn
                //Otherwise the item in the production line has already been paid for
                if(line.getTurnsRemaining() == item.getTime()){
                    total += item.getCost();
                }
            }
        }
        return total;
    }
    
    /**
     * Returns The resource points for a faction in a given game turn
     */
    public double calculateTotalResourcePoints(Map<StarSystem, Double> factionTerritory){
        double total = resourceBank + calculateIncome(factionTerritory);
        return total;
    }
    
    /**
     * Returns the income for the faction this turn
     * @param factionTerritory
     * @return
     */
    public double calculateIncome(Map<StarSystem, Double> factionTerritory){
        //Calculate the bonus from any resources saved up
        double interestBonus = Math.floor(resourceBank/50)*0.1; 
        //Calculate the resources from the StarMap
        double rawResourcePoints = calculateRawResourcePoints(factionTerritory);
        double subTotal = interestBonus + rawResourcePoints;
        //obtain the industrial technology modifier
        double techModifier = faction.getTechnology().getIndustryResourceMultiplier();
        
        double finishedTotal = subTotal * techModifier * (currentEconomicStrength/100);
        return finishedTotal;
    }
    
    /**
     * The Map contains references to StarSystem objects, these are the keys.
     * The double value in each pair is a the fraction of the planet controlled
     * by this faction.  It is expressed as a number between 0 and 1.
     */
    public double calculateRawResourcePoints(Map<StarSystem, Double> factionTerritory){
        double resourcePointTotal = 0;
        
        for(StarSystem system: factionTerritory.keySet()){
            Double systemFraction = factionTerritory.get(system);
            double fullSystemValue = system.getResourceValue();
            double systemValue = fullSystemValue * systemFraction;
            resourcePointTotal += systemValue;
        }
        return resourcePointTotal;
    }
    
    /**
     * Calculates the faction's economic strength based on it modifiers
     * and sets the current economic strength
     */
    public void calculateEconomicStrength(){
        //Start with faction specific modifier
        double modifier = factionSpecificEconomicModifier;
        //Add in the IndustryTech modifier
        modifier += faction.getTechnology().getIndustryTechEconomicModifier();
        //Add in the modifiers for the trading partners
        modifier += tradeModifier;
        //Subtract the penalties for worlds in conflict
            modifier -= 0.5 * numberOfWorldsInConflict/4;
        //Subtract the Interdiction Modifier if it applies
        if(underInterdiction){
            modifier -= 5;
        }
        //Store the modifier so that it can be displayed to the user
        latestEconomicModifier = modifier;
        
        //Now complete the adjustment
        adjustEconomicStrength(modifier);
    }
    
    private void adjustEconomicStrength(double modifier){
        double resultingEconomicStrength = currentEconomicStrength;
        int change = 0;
        if(modifier < 2){
            change = Dice.getDice().get3D6() * -1;
            resultingEconomicStrength += change;
        } else if(modifier >= 2 && modifier < 3){
            change = Dice.getDice().get2D6() * -1;
            resultingEconomicStrength += change;
        } else if(modifier >= 3 && modifier < 4){
            change = Dice.getDice().get1D6() * -1;
            resultingEconomicStrength += change;
        } else if(modifier >= 4 && modifier < 5){
            change = Dice.getDice().get1D6over2() * -1;
            resultingEconomicStrength += change;
        } else if(modifier >= 5 && modifier < 6){
            //no change
            change = 0;
        } else if(modifier >= 6 && modifier < 7){
            change = Dice.getDice().get1D6over2();
            resultingEconomicStrength += change;
        } else if(modifier >= 7 && modifier < 8){
            change = Dice.getDice().get1D6();
            resultingEconomicStrength += change;
        } else if(modifier >= 8 && modifier < 9){
            change = Dice.getDice().get2D6();
            resultingEconomicStrength += change;
        } else if(modifier >= 9){
            change = Dice.getDice().get3D6();
            resultingEconomicStrength += change;
        } 
        //Store the change in economic strength for display purposes
        latestEconomicStrengthChange = change;
        //complete setting and storing the economic strength
        this.setCurrentEconomicStrength(resultingEconomicStrength);
    }
    
    /**
     * Sets the economic strength within the faction's economic limits
     * @param economicStrength
     */
    public void setCurrentEconomicStrength(double economicStrength) {
        if(economicStrength < economicMinimumPercentile){
            currentEconomicStrength = economicMinimumPercentile; 
        } else if (economicStrength > economicMaximumPercentile){
            currentEconomicStrength = economicMaximumPercentile; 
        } else{
            currentEconomicStrength = economicStrength;
        }
    }
    
    /**
     * Runs the code to update a faction's economy at the end of a turn
     */
    public void executeEndOfTurn(){
        resourceBank = resourceBank + calculateIncome(faction.getFactionTerritory())
                - calculateExpenditures();
        calculateEconomicStrength();
    }
    
    //Accessors ===============================================================
    public double getCurrentEconomicStrength() {
        return currentEconomicStrength;
    }

    public int getEconomicMinimumPercentile() {
        return economicMinimumPercentile;
    }

    public int getEconomicMaximumPercentile() {
        return economicMaximumPercentile;
    }

    public double getFactionSpecificEconomicModifier() {
        return factionSpecificEconomicModifier;
    }

    public double getResourceBank() {
        return resourceBank;
    }

    public void setResourceBank(double resourceBank) {
        this.resourceBank = resourceBank;
    }
    
    public int getNumberOfWorldsInConflict() {
        return numberOfWorldsInConflict;
    }

    public void incrementNumberOfWorldsInConflict() {
        numberOfWorldsInConflict++;
    }
    
    public void decrementNumberOfWorldsInConflict() {
        if(numberOfWorldsInConflict>0)
            numberOfWorldsInConflict--;
    }
    
    public boolean isUnderInterdiction() {
        return underInterdiction;
    }

    public void setUnderInterdiction(boolean underInterdiction) {
        this.underInterdiction = underInterdiction;
    }
    
    
    public double getLatestEconomicModifier() {
        return latestEconomicModifier;
    }

    public double getLatestEconomicStrengthChange() {
        return latestEconomicStrengthChange;
    }

    public int getCivilianSpendingPercentile() {
        return civilianSpendingPercentile;
    }

    public void setCivilianSpendingPercentile(int civilianSpendingPercentile) {
        this.civilianSpendingPercentile = civilianSpendingPercentile;
    }

    /**
     * It is essential this method be called by the faction constructor
     * @param faction
     */
    protected void setFaction(Faction faction){
        this.faction = faction;
    }

    //Inner types =============================================================
    public class TradingPartner {
        private TradingPartnerStatus tradingPartnerStatus;
        private TradingPartnerSize tradingPartnerSize;
        private boolean isAdjacent;
        
        /**
         * Create a trading partner
         * @param tradingPartnerStatus
         * @param tradingPartnerSize
         * @param isAdjacent
         */
        public TradingPartner(TradingPartnerStatus tradingPartnerStatus,
                TradingPartnerSize tradingPartnerSize,
                boolean isAdjacent){
            this.tradingPartnerStatus = tradingPartnerStatus;
            this.tradingPartnerSize = tradingPartnerSize;
        }
        
        /**
         * Calculate the economic modifier for one faction
         * @return the modifier
         */
        public double calculateTradeModifier(){
            double modifier = 0;
            if(tradingPartnerStatus== TradingPartnerStatus.noTrade){
                //nothing
            } else if (tradingPartnerStatus== TradingPartnerStatus.restrictedTrade){
                modifier += 0.5;
            } else if (tradingPartnerStatus== TradingPartnerStatus.freeTrade){
                modifier += 1;
            } else if (tradingPartnerStatus== TradingPartnerStatus.allyTrade){
                modifier += 1.5;
            } 
            
            if(tradingPartnerSize == TradingPartnerSize.minorPower){
                modifier = modifier/2;
            }
            if(!isAdjacent){
                modifier -= 0.5;
            }
            return modifier;
        }
        
        public void setTradingPartnerStatus(
                TradingPartnerStatus tradingPartnerStatus) {
            this.tradingPartnerStatus = tradingPartnerStatus;
        }
        
        public TradingPartnerStatus getTradingPartnerStatus(){
            return tradingPartnerStatus;
        }
        
        public boolean isLessThan50LightYearsDistant(){
            return isAdjacent;
        }
        
        public void setDistance(boolean lessThan50LightYears){
            isAdjacent= lessThan50LightYears;
        }
    }

    public String getTradeModifier() {
        return new Double(tradeModifier).toString();
    }
    
}
