package ca.ott.al.starmap.ui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import ca.ott.al.starmap.core.GameCore;
import ca.ott.al.starmap.core.faction.Faction;
import ca.ott.al.starmap.core.map.StarSystem;
import ca.ott.al.starmap.core.order.faction.BuildFactoryOrder;
import ca.ott.al.starmap.ui.FactionOrdersPanel;

public class FactoryConstructionDialog extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    JLabel instructionLabel;
    JComboBox starSystemComboBox;
    JButton doneButton;
    JButton cancelButton;
    
    private Faction selectedfaction;
    private FactionOrdersPanel callerPanel;
    
    public FactoryConstructionDialog(Faction faction, FactionOrdersPanel caller){
        this.selectedfaction = faction;
        this.callerPanel = caller;
        
        setTitle("New Construction");
        
        instructionLabel = new JLabel(
                "Select the star system where this faction will build a production line");
        Set<StarSystem> keySet = faction.getFactionTerritory().keySet();
        Vector<String> starSystems = new Vector<String>();
        for(StarSystem system: keySet){
            starSystems.add(system.getName());
        }
        starSystemComboBox = new JComboBox<String>(starSystems);
        //starSystemComboBox.setMaximumSize(new Dimension(200, 50));
        starSystemComboBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        starSystemComboBox.setSelectedIndex(0);
        
        doneButton = new JButton("Done");
        doneButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                String selectedSystem = (String)starSystemComboBox.getSelectedItem();
                StarSystem starSystem = GameCore.getGameCore().getStarMap()
                        .getStarSystemByName(selectedSystem);
                
                if(selectedSystem != null){
                    int cost = 1000;
                    double availableRUs = selectedfaction.getEconomy().
                            calculateAvailableResourcePoints();
                    if(availableRUs > cost){
                        BuildFactoryOrder order = new BuildFactoryOrder(starSystem);
                        selectedfaction.addFactionOrder(order);
                        callerPanel.refreshOrderList();
                    }
                    dispose();
                }
            }
        });
        
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                dispose();
            }
        });
        
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0;
        c.weightx = 1.0; c.weighty = 1.0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.NONE;
        add(instructionLabel, c);
        
        c.gridy = 1;
        add(starSystemComboBox, c);
        
        c.gridy =2;
        c.gridwidth = 1;
        add(doneButton, c);
        
        c.gridx = 1;
        add(cancelButton, c);
        
        setModalityType(ModalityType.APPLICATION_MODAL);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(500, 300);
    }
}
