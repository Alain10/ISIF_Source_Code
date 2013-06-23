package ca.ott.al.starmap.ui.dialog;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import ca.ott.al.starmap.core.GameCore;
import ca.ott.al.starmap.core.faction.Faction;
import ca.ott.al.starmap.core.map.StarSystem;
import ca.ott.al.starmap.core.order.faction.BuildDepotOrder;
import ca.ott.al.starmap.ui.FactionOrdersPanel;
import ca.ott.al.starmap.ui.UnitOrdersDetailPanel;

public class RaiseUnitDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    JLabel instructionLabel, nameInstructionLabel, unitTypeLabel;
    JComboBox<String> starSystemComboBox;
    JComboBox<String> unitTypeComboBox;
    JButton doneButton;
    JButton cancelButton;
    JTextArea unitNameTextArea;
        
    public RaiseUnitDialog(final Faction faction, StarSystem selectedSystem){
        
        setTitle("New Unit");
        
        instructionLabel = new JLabel(
                "Select the star system");
        Set<StarSystem> keySet = faction.getFactionTerritory().keySet();
        Vector<String> starSystems = new Vector<String>();
        for(StarSystem system: keySet){
            starSystems.add(system.getName());
        }
        starSystemComboBox = new JComboBox<String>(starSystems);
        //starSystemComboBox.setMaximumSize(new Dimension(200, 50));
        starSystemComboBox.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        starSystemComboBox.setSelectedIndex(0);
        if(selectedSystem != null && keySet.contains(selectedSystem)){
            starSystemComboBox.setSelectedIndex(starSystems.indexOf(selectedSystem.getName()));
        }
        
        unitTypeLabel = new JLabel("Select unit type");
        
        Vector<String> unitTypes = new Vector<String>();
        unitTypes.add("Ground Unit");
        unitTypes.add("Aerospace Only Unit");
        unitTypeComboBox = new JComboBox<String>(unitTypes);
        unitTypeComboBox.setSelectedIndex(0);
        
        nameInstructionLabel = new JLabel("Specify unit name");
        
        unitNameTextArea = new JTextArea("", 1, 50);
        
        doneButton = new JButton("Done");
        doneButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                String selectedSystem = (String)starSystemComboBox.getSelectedItem();
                
                if(selectedSystem != null && !unitNameTextArea.getText().equals("")){
                    String unitName = unitNameTextArea.getText();
                    GameCore gameCore = GameCore.getGameCore();
                    gameCore.raiseUnit(unitName, (String)unitTypeComboBox.getSelectedItem(), selectedSystem, faction);
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
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        add(instructionLabel, c);
        
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(starSystemComboBox, c);
        
        c.gridy = 1;
        c.gridx = 0;
        c.fill = GridBagConstraints.NONE;
        add(unitTypeLabel, c);
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(unitTypeComboBox, c);
        
        c.gridy = 2;
        c.gridx = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        add(nameInstructionLabel, c);
        
        c.gridx = 1;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        unitNameTextArea.setColumns(60);
        unitNameTextArea.setBorder(BorderFactory.createEmptyBorder(1, 15, 1, 15));
        
        add(unitNameTextArea, c);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(doneButton);
        buttonPanel.add(cancelButton);
        
        c.gridy = 3;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        add(buttonPanel, c);
        
        setModalityType(ModalityType.APPLICATION_MODAL);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(600, 200);
    }
}
