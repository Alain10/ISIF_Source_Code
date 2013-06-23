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

import ca.ott.al.starmap.core.GameCore;
import ca.ott.al.starmap.core.faction.Faction;
import ca.ott.al.starmap.core.map.StarSystem;
import ca.ott.al.starmap.core.order.faction.EstablishGarrisonOrder;
import ca.ott.al.starmap.core.unit.GarrisonForce;
import ca.ott.al.starmap.core.unit.MilitaryForce;
import ca.ott.al.starmap.ui.FactionOrdersPanel;


public class EstablishGarrisonDialog extends JDialog {
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
    
    public EstablishGarrisonDialog(Faction faction, FactionOrdersPanel caller){
        this.selectedfaction = faction;
        this.callerPanel = caller;
        
        setTitle("Establish Garrison");
        
        instructionLabel = new JLabel(
                "Select the star system where this faction will Establish a new Garrison.  "
                        +"If the combo box is empty, this faction may not establish any garrisons at this time.");
        Set<StarSystem> keySet = faction.getFactionTerritory().keySet();
        Vector<String> starSystems = new Vector<String>();
        for(StarSystem system: keySet){
            boolean noGarrison = true;
            Set<MilitaryForce> forces = system.getPrimaryPlanet().getMilitaryForces();
            for(MilitaryForce force: forces){
                if(force instanceof GarrisonForce){
                    noGarrison = false;
                }
            }
            if(noGarrison)
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
                    int cost = 3;
                    double availableRUs = selectedfaction.getEconomy().
                            calculateAvailableResourcePoints();
                    if(availableRUs > cost){
                        EstablishGarrisonOrder order = new EstablishGarrisonOrder(starSystem);
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