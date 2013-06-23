package ca.ott.al.starmap.core.faction;

import java.io.Serializable;

public class TechnologyTable implements Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1511692765567007740L;
    
    private double battleTechRating;
    private double comTechRating;
    private double industryTechRating;
    
    public TechnologyTable(double startBattleTechRating, 
            double startComTechRating, double startIndustryTechRating){
        battleTechRating = startBattleTechRating;
        comTechRating = startComTechRating;
        industryTechRating = startIndustryTechRating;
    }

    //Technology effect methods================================================
    
    //Combat
    public double getAerospaceModifier(){
        if(battleTechRating < 50){
            return 1;
        } else if(battleTechRating >= 50 && battleTechRating < 250){
            return 1.1;
        } else if(battleTechRating >= 250 && battleTechRating < 400){
            return 1.2;
        } else if(battleTechRating >= 400 && battleTechRating < 550){
            return 1.3;
        } else if(battleTechRating >= 550){
            return 1.4;
        }
        return 1; //Should never reach this line
    }
    
    public double getGroundModifier(){
        if(battleTechRating < 100){
            return 1;
        } else if(battleTechRating >= 100 && battleTechRating < 200){
            return 1.1;
        } else if(battleTechRating >= 200 && battleTechRating < 350){
            return 1.2;
        } else if(battleTechRating >= 350 && battleTechRating < 500){
            return 1.3;
        } else if(battleTechRating >= 500){
            return 1.4;
        }
        return 1; //Should never reach this line
    }
    
    public int getAttackRollBonus(){
        if(battleTechRating < 150){
            return 0;
        } else if(battleTechRating >= 150 && battleTechRating < 300){
            return 1;
        } else if(battleTechRating >= 300 && battleTechRating < 450){
            return 2;
        } else if(battleTechRating >= 450){
            return 3;
        }
        return 0; //Should never reach this line
    }
    
    //Communications 
    public int getMaxNumberOfOrders(){
        if(comTechRating <100){
            return 4;
        } else if(comTechRating >= 100 && comTechRating < 200){
            return 5;
        } else if(comTechRating >= 200){
            return 6;
        }
        return 4;
    }
    
    public boolean hasHPGFaxComms(){
        if(comTechRating >= 300){
            return true;
        }
        return false;
    }
    
    public boolean hasOwnHPGComms(){
        if(comTechRating >= 450){
            return true;
        }
        return false;
    }
    
    public boolean hasChatterWebLeadershipBonus(){
        if(comTechRating >= 500){
            return true;
        }
        return false;
    }
    
    //Industry
    public int getIndustryTechEconomicModifier(){
        if(industryTechRating < 50){
            return 0;
        } else if(industryTechRating >= 50 && industryTechRating < 200){
            return 1;
        } else if(industryTechRating >= 200 && industryTechRating < 350){
            return 2;
        } else if(industryTechRating >= 350 && industryTechRating < 500){
            return 3;
        } else if(industryTechRating >= 500){
            return 4;
        }
        return 0; //Should never reach this line
    }
    
    public double getIndustryResourceMultiplier(){
        if(industryTechRating < 150){
            return 1;
        } else if(industryTechRating >= 150 && industryTechRating < 300){
            return 1.1;
        } else if(industryTechRating >= 300 && industryTechRating < 450){
            return 1.2;
        } else if(industryTechRating >= 450){
            return 1.3;
        }
        return 1; //Should never reach this line
    }
    
    //Access methods===========================================================
    public double getBattleTechRating() {
        return battleTechRating;
    }

    public double getComTechRating() {
        return comTechRating;
    }

    public double getIndustryTechRating() {
        return industryTechRating;
    }

    public void setBattleTechRating(double battleTechRating) {
        this.battleTechRating = battleTechRating;
    }

    public void setComTechRating(double comTechRating) {
        this.comTechRating = comTechRating;
    }

    public void setIndustryTechRating(double industryTechRating) {
        this.industryTechRating = industryTechRating;
    }
}
