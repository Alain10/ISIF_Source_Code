package ca.ott.al.starmap.console;

import java.awt.Point;

import ca.ott.al.starmap.dice.Dice;

public class RandomGarrisonGenerator {

    public RandomGarrisonGenerator(){
        
    }
    
    public static Point generateGarrison(int modifier){
        int diceRoll = Dice.getDice().get1D6();
        diceRoll += modifier;
        
        int groundStrength = 0;
        int airStrength = 0;
        
        /*
         Modified Roll  Infantry    Armor   Mech
        2 or less       2           1       0
        3               2           2       0
        4               3           2       0
        5               3           3       0
        6               4           3       0
        7               4           3       0
        8               5           4       1
        9+              6           5       2
        */
        
        switch(diceRoll){
            case 1: groundStrength = 2*(27) + 30; break;
            case 2: groundStrength = (27 + 41) + 60; break;
            case 3: groundStrength = (27 + 41) + 30 + 60; break;
            case 4: groundStrength = (2* 27 + 48) + 30 + 60; break;
            case 5: groundStrength = (27 + 41 +48) + 30 + 60 + 90; break;
            case 6: groundStrength = (2 * 27 + 41 +48) + 30 + 60 + 90; break;
            case 7: groundStrength = (27 + 41*2 + 48) + 60*2 + 90; break;
            case 8: groundStrength = (27* 2 + 41*2 + 48) + 30 + 60*2 + 90 + 120; 
                    airStrength = 30;
                    break;
            case 9: case 10:
            case 11: case 12:
                    groundStrength = (27* 2 + 41*2 + 48*2) + 30 + 60*2 + 90*2 + 120 + 180; 
                    airStrength = 30 + 40;
                    break;
            
        }
        
        return new Point(airStrength,groundStrength);
    } 
    
}
