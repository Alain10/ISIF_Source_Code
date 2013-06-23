package ca.ott.al.starmap.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.SortedMap;

public class ConsoleMenu {

    private String menuTitle, menuPrompt;
    private SortedMap<String,String> menuMap;
    
    
    public ConsoleMenu(String menuTitle, SortedMap<String,String> menuMap,
            String menuPrompt){
        this.menuMap = menuMap;
        this.menuTitle = menuTitle;
        this.menuPrompt = menuPrompt;
    }
    
    public String deployMenu(){
        //Print the menu
        System.out.println("\n\t"+menuTitle+"\n");
        for(Map.Entry<String, String> entry: menuMap.entrySet()){
            System.out.println("\t"+entry.getKey()+"\t"+entry.getValue());
        }
        System.out.print("\t"+menuPrompt);
        
        //Get the user input
        InputStreamReader inputReader = new InputStreamReader(System.in);
        BufferedReader bufferedReader = new BufferedReader(inputReader);
        String userInput = null;
        try {
            userInput = bufferedReader.readLine();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return userInput;
    }
    
}
