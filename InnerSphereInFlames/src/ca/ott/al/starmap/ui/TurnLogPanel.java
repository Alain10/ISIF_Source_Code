package ca.ott.al.starmap.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import ca.ott.al.starmap.core.GameLogTool;

public class TurnLogPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 7781732316169412610L;

    JScrollPane turnLogScrollPane;
    JTextArea turnLogTextArea;
    
    JTextPane textPane;
    
    public TurnLogPanel(){
        setLayout(new BorderLayout());
        
        turnLogTextArea = new JTextArea();
        turnLogScrollPane = new JScrollPane(turnLogTextArea);
        
        add(turnLogScrollPane,BorderLayout.CENTER);
    }
    
    /**
     * Call this method to refresh the log pane
     */
    public void refresh(){
        turnLogTextArea.setText("");
        String[] logs = GameLogTool.getGameLogTool().getLastTurnLog();
        for(int i=0; i< logs.length; i++){
            turnLogTextArea.append(logs[i]+ "\n");
        }
    }
    
}
