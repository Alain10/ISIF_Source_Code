package ca.ott.al.starmap.core;

import java.util.Map;
import java.util.TreeMap;

public class GameLogTool {

    private static GameLogTool gameLogTool;
    
    private final Map<Integer, GameTurnLog> gameTurnLog;
    private int currentTurn;
    
    private GameLogTool(){
        gameTurnLog = new TreeMap<Integer, GameTurnLog>();
        currentTurn = 0;
        
        //maybe use turnLogZero to provide an introduction to a scenario
        GameTurnLog turnZeroLog = new GameTurnLog();
        gameTurnLog.put(currentTurn, turnZeroLog);
    }
    
    public static GameLogTool getGameLogTool(){
        if(gameLogTool == null){
            gameLogTool = new GameLogTool();
        }
        return gameLogTool;
    }
    
    /**
     * Appends a message to the log of the current turn
     * @param message
     */
    public synchronized void appendLog(String message){
        GameTurnLog currentLog = gameTurnLog.get(currentTurn);
        currentLog.appendMessage(message);
    }
    
    /**
     * Change turn
     */
    public synchronized void changeTurn(){
        currentTurn++;
        GameTurnLog turnLog = new GameTurnLog();
        gameTurnLog.put(currentTurn, turnLog);
    }
    
    /**
     * Return the logs from the last turn
     */
    public synchronized String[] getLastTurnLog(){
        GameTurnLog turnLog = gameTurnLog.get(currentTurn);
        return turnLog.getTurnLogMessages();
    }
    
    private class GameTurnLog{
        private final Map<Integer,String> gameLogMessagesMap;
        int logCount = 0;
        
        public GameTurnLog(){
            gameLogMessagesMap = new TreeMap<Integer, String>();
        }
        
        public void appendMessage(String message){
            gameLogMessagesMap.put(logCount, message);
            logCount++;
        }
        
        public String[] getTurnLogMessages(){
            String[] messageArray = new String[gameLogMessagesMap.size()];
            for(Map.Entry<Integer, String> entry : gameLogMessagesMap.entrySet()){
                messageArray[entry.getKey()] = entry.getValue();
            }
            return messageArray;
        }
    }
    
}
