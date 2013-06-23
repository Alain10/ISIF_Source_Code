package ca.ott.al.starmap.ui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import ca.ott.al.starmap.core.GameCore;
import ca.ott.al.starmap.core.faction.Faction;
import ca.ott.al.starmap.core.map.StarSystem;
import ca.ott.al.starmap.core.order.faction.BuildDepotOrder;
import ca.ott.al.starmap.ui.FactionOrdersPanel;

public class DepotConstructionDialog extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 9082430296392233085L;
    
    JLabel instructionLabel, ruInstructionLabel;
    JComboBox starSystemComboBox;
    JButton doneButton;
    JButton cancelButton;
    JSpinner depotValue;
    
    private Faction selectedfaction;
    private FactionOrdersPanel callerPanel;
    
    public DepotConstructionDialog(Faction faction, FactionOrdersPanel caller){
        this.selectedfaction = faction;
        this.callerPanel = caller;
        
        setTitle("New Construction");
        
        instructionLabel = new JLabel(
                "Select the star system where this faction will build a depot");
        Set<StarSystem> keySet = faction.getFactionTerritory().keySet();
        Vector<String> starSystems = new Vector<String>();
        for(StarSystem system: keySet){
            starSystems.add(system.getName());
        }
        starSystemComboBox = new JComboBox<String>(starSystems);
        //starSystemComboBox.setMaximumSize(new Dimension(200, 50));
        starSystemComboBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        starSystemComboBox.setSelectedIndex(0);
        
        ruInstructionLabel = new JLabel(
                "Specify the number of resource units to store in the depot");
        SpinnerModel depotModel = new SpinnerNumberModel(10, 10, 500, 10);
        depotValue = new JSpinner(depotModel);
        
        doneButton = new JButton("Done");
        doneButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                String selectedSystem = (String)starSystemComboBox.getSelectedItem();
                StarSystem starSystem = GameCore.getGameCore().getStarMap()
                        .getStarSystemByName(selectedSystem);
                
                if(selectedSystem != null){
                    int value = (Integer)depotValue.getModel().getValue();
                    double availableRUs = selectedfaction.getEconomy().
                            calculateAvailableResourcePoints();
                    if(availableRUs > value){
                        BuildDepotOrder order = new BuildDepotOrder(starSystem, value);
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
        
        c.gridy = 2;
        add(ruInstructionLabel, c);
        
        c.gridy = 3;
        add(depotValue, c);
        
        c.gridy = 4;
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
