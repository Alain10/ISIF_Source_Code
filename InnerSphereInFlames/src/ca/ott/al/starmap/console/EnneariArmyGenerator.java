package ca.ott.al.starmap.console;

import java.util.Random;

public class EnneariArmyGenerator {

    public static String[] greekLetter = {"Alpha", "Beta", "Gamma", "Delta", "Epsilon", 
        "Zeta", "Eta", "Theta", "Iota", "Kappa", "Lambda", "Mu", "Nu", "Xi", "Omicron",
        "Pi", "Rho", "Sigma", "Tau", "Upsilon", "Phi", "Chi", "Psi", "Omega"};
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        //generateBasicArmy();
        generateRandomCoord();
    }
    
    public static void generateRandomCoord(){
        Random random = new Random();
        for(int i=0; i <19; i++ ){
            int randomX = 3000 + random.nextInt(250);
            int randomY = -150 + random.nextInt(250);
            System.out.println(randomX + " "+randomY);
        }
    }
    
    public static void generateBasicArmy(){

        String heading = "military unit";
        String factionName= "Enneari Federation";
        String commander = "unknown";
        String starSystem = "Hivrannee";
        String loyalty = "fanatical";
        String unitType = "regular";
        
        Random random = new Random();

        for(int i=0; i <41; i++ ){
            String name = (i+1)+"th Armored Cavalry";
            String ground = "1185";
            String air = "220";
            String supplyReq = "2";
            String supplyStock ="12";
            int randomExp = 5 + random.nextInt(6);
            String exp = String.valueOf(randomExp);
            System.out.println(heading+","+factionName+","+name+","+commander+","+loyalty+","+starSystem+","+
            air+","+ground+","+supplyReq+","+supplyStock+","+exp+","+unitType);
        }
        
        for(int i=0; i <21; i++ ){
            String name = (i+1)+"th Federation Guards";
            String ground = "1420";
            String air = "270";
            String supplyReq = "2";
            String supplyStock ="12";
            int randomExp = 5 + random.nextInt(6);
            String exp = String.valueOf(randomExp);
            System.out.println(heading+","+factionName+","+name+","+commander+","+loyalty+","+starSystem+","+
            air+","+ground+","+supplyReq+","+supplyStock+","+exp+","+unitType);
        }

        for(int i=0; i <2; i++ ){
            String name = (i+1)+"th Marineri";
            String ground = "945";
            String air = "200";
            String supplyReq = "2";
            String supplyStock ="12";
            String exp = "10";
            System.out.println(heading+","+factionName+","+name+","+commander+","+loyalty+","+starSystem+","+
            air+","+ground+","+supplyReq+","+supplyStock+","+exp+","+unitType);
        }
        for(int i=0; i <7; i++ ){
            String name = (i+1)+"th Pursuit Cavalry";
            String ground = "995";
            String air = "180";
            String supplyReq = "2";
            String supplyStock ="12";
            int randomExp = 3 + random.nextInt(6);
            String exp = String.valueOf(randomExp);
            System.out.println(heading+","+factionName+","+name+","+commander+","+loyalty+","+starSystem+","+
            air+","+ground+","+supplyReq+","+supplyStock+","+exp+","+unitType);
        }
        for(int i=0; i <21; i++ ){
            String name = greekLetter[i]+" Exoplanetary";
            String ground = "1070";
            String air = "220";
            String supplyReq = "2";
            String supplyStock ="12";
            int randomExp = 5 + random.nextInt(6);
            String exp = String.valueOf(randomExp);
            System.out.println(heading+","+factionName+","+name+","+commander+","+loyalty+","+starSystem+","+
            air+","+ground+","+supplyReq+","+supplyStock+","+exp+","+unitType);
        }
    }    
    
    
    public static void generateAdvancedArmy(){
        //heading   force owner force name  commander name  loyalty star system air rating  ground rating   supply requirement  supply stock    experience  force type
        //military unit Comstar Defenders of the Faithful   unknown fanatical   Terra   520 2700    4   24  16  regular
        
        String heading = "military unit";
        String factionName= "Enneari Federation";
        String commander = "unknown";
        String starSystem = "Hivrannee";
        String loyalty = "fanatical";
        String unitType = "regular";
/*        
        Light   Medium  Heavy   Assault Ground  Air
        60  95  120 140     
                            
Armored Cavalry 41      1   8   2   1   1200    225
Guards  14      1   3   5   3   1365    270
Assault Guards  7       0   2   5   5   1490    270
Marineri    2       0   6   3   0   930 135
Pursuit Cavalry 7       5   7   0   0   965 135
Exoplanetary    21      2   6   3   1   1190    225
Training Battalions (9) 3       5   3   1   0   705 135
*/
        
        for(int i=0; i <41; i++ ){
            String name = (i+1)+"th Armored Cavalry";
            String ground = "1200";
            String air = "225";
            String supplyReq = "2";
            String supplyStock ="12";
            String exp = "10";
            System.out.println(heading+","+factionName+","+name+","+commander+","+loyalty+","+starSystem+","+
            air+","+ground+","+supplyReq+","+supplyStock+","+exp+","+unitType);
        }
        
        for(int i=0; i <14; i++ ){
            String name = (i+1)+"th Federation Guards";
            String ground = "1365";
            String air = "270";
            String supplyReq = "2";
            String supplyStock ="12";
            String exp = "10";
            System.out.println(heading+","+factionName+","+name+","+commander+","+loyalty+","+starSystem+","+
            air+","+ground+","+supplyReq+","+supplyStock+","+exp+","+unitType);
        }
        
        for(int i=0; i <7; i++ ){
            String name = (i+1)+"th Assault Guards";
            String ground = "1490";
            String air = "270";
            String supplyReq = "2";
            String supplyStock ="12";
            String exp = "10";
            System.out.println(heading+","+factionName+","+name+","+commander+","+loyalty+","+starSystem+","+
            air+","+ground+","+supplyReq+","+supplyStock+","+exp+","+unitType);
        }

        for(int i=0; i <2; i++ ){
            String name = (i+1)+"th Marineri";
            String ground = "930";
            String air = "135";
            String supplyReq = "2";
            String supplyStock ="12";
            String exp = "10";
            System.out.println(heading+","+factionName+","+name+","+commander+","+loyalty+","+starSystem+","+
            air+","+ground+","+supplyReq+","+supplyStock+","+exp+","+unitType);
        }
        for(int i=0; i <7; i++ ){
            String name = (i+1)+"th Pursuit Cavalry";
            String ground = "965";
            String air = "135";
            String supplyReq = "2";
            String supplyStock ="12";
            String exp = "10";
            System.out.println(heading+","+factionName+","+name+","+commander+","+loyalty+","+starSystem+","+
            air+","+ground+","+supplyReq+","+supplyStock+","+exp+","+unitType);
        }
        for(int i=0; i <21; i++ ){
            String name = (i+1)+"th Exoplanetary";
            String ground = "1190";
            String air = "225";
            String supplyReq = "2";
            String supplyStock ="12";
            String exp = "10";
            System.out.println(heading+","+factionName+","+name+","+commander+","+loyalty+","+starSystem+","+
            air+","+ground+","+supplyReq+","+supplyStock+","+exp+","+unitType);
        }
        
    }

}
