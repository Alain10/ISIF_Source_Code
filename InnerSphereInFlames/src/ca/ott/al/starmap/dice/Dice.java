package ca.ott.al.starmap.dice;

import java.util.Random;

public class Dice {
    
    private static Dice dice;
    private Random randomGenerator;
    
    private Dice(){
        randomGenerator = new Random();
    }
    
    public static Dice getDice(){
        if(dice == null){
            dice = new Dice();
        }
        return dice;
    }
    
    public int get1D6(){
        int base = randomGenerator.nextInt(6);
        base++; //0 is inclusive, 6 excluded from the call above this line
        return base;
    }
    
    public int get2D6(){
        int base1 = randomGenerator.nextInt(6);
        int base2 = randomGenerator.nextInt(6);
        base1++;
        base2++;
        return base1 + base2;
    }
    
    public int get3D6(){
        int base1 = randomGenerator.nextInt(6);
        int base2 = randomGenerator.nextInt(6);
        int base3 = randomGenerator.nextInt(6);
        base1++;
        base2++;
        base3++;
        return base1 + base2 + base3;
    }
    
    public int get1D6over2(){
        int base = randomGenerator.nextInt(3);
        base++; //0 is inclusive, 3 excluded from the call above this line
        return base;
    }
    
    /**
     * @return a number between 0 and 99, inclusive
     */
    public int get2D10(){
        int base = randomGenerator.nextInt(100);
        return base;        
    }
}
