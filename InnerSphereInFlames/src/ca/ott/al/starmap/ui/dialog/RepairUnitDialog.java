package ca.ott.al.starmap.ui.dialog;

import java.awt.GridBagConstraints;
import java.awt.Dialog.ModalityType;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import ca.ott.al.starmap.core.faction.Faction;
import ca.ott.al.starmap.core.order.factory.ProductionItem;
import ca.ott.al.starmap.core.order.unit.support.RepairOrder;
import ca.ott.al.starmap.core.unit.MilitaryForce;
import ca.ott.al.starmap.core.unit.Reinforcement;
import ca.ott.al.starmap.core.unit.StarMapUnit;

public class RepairUnitDialog extends JDialog {

    final Faction faction;
    final MilitaryForce force;
    
    JLabel unitName;
    
    JList<Reinforcement> availableList;
    JList<Reinforcement> selectedList;
    
    JButton addButton;
    JButton removeButton;
    JButton clearButton;
    
    JLabel unitAirRating;
    JLabel unitGroundRating;
    JLabel unitExperience;
    
    JLabel reinforcementAir;
    JLabel reinforcementGround;
    
    JButton okButton;
    JButton cancelButton;
    
    Vector<Reinforcement> poolVector;
    Vector<Reinforcement> selectedVector;
    
    public RepairUnitDialog(Faction factionParam, final MilitaryForce force) {
        super();
        this.faction = factionParam;
        this.force = force;
        
        setTitle("Repair Unit");
        setLayout(new GridBagLayout());
        
        String[] availableListItems = new String[1];
        availableList = new JList(availableListItems);
        availableList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        availableList.setLayoutOrientation(JList.VERTICAL);
        
        poolVector = new Vector<Reinforcement>();
        poolVector.addAll(factionParam.getReinforcementPool().getPool());
        availableList.setListData(poolVector);
        if(availableList.getModel().getSize()> 0){
            availableList.setSelectedIndex(0);
        }
        
        String[] selectedListItems = new String[1];
        selectedList = new JList(selectedListItems);
        selectedList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        selectedList.setLayoutOrientation(JList.VERTICAL);
        
        selectedVector = new Vector<Reinforcement>();
        if(selectedList.getModel().getSize()> 0){
            selectedList.setSelectedIndex(0);
        }
        
        addButton = new JButton(">>");
        addButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                List<Reinforcement> list = availableList.getSelectedValuesList();
                if(!list.isEmpty()){
                    for(Reinforcement ref : list){
                        poolVector.remove(ref);
                        selectedVector.add(ref);
                    }
                    availableList.setListData(poolVector);
                    selectedList.setListData(selectedVector);
                }
            }
        });
        
        removeButton = new JButton("<<");
        removeButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                List<Reinforcement> list = selectedList.getSelectedValuesList();
                if(!list.isEmpty()){
                    for(Reinforcement ref : list){
                        selectedVector.remove(ref);
                        poolVector.add(ref);
                    }
                    availableList.setListData(poolVector);
                    selectedList.setListData(selectedVector);
                }
            }
        });
        
        clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                poolVector.addAll(selectedVector);
                selectedVector.clear();
                selectedList.setListData(selectedVector);
                availableList.setListData(poolVector);
                
            }
        });
        
        JLabel availableLabel = new JLabel("Reinforcement Pool");
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0;
        c.weightx = 100; c.weighty = 1.0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(3, 3, 3, 3);
        add(availableLabel, c);
        
        c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 1;
        c.weightx = 100; c.weighty = 100;
        c.gridwidth = 2;
        c.gridheight = 10;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(3, 3, 3, 3);
        
        JScrollPane availableScrollPane = new JScrollPane(availableList);
        add(availableScrollPane, c);
        
        c = new GridBagConstraints();
        c.gridx = 2; c.gridy = 1;
        c.weightx = 1.0; c.weighty = 1.0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(3, 3, 3, 3);
        add(addButton, c);
        
        c = new GridBagConstraints();
        c.gridx = 2; c.gridy = 2;
        c.weightx = 1.0; c.weighty = 1.0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(3, 3, 3, 3);
        add(removeButton, c);
        
        c = new GridBagConstraints();
        c.gridx = 2; c.gridy = 3;
        c.weightx = 1.0; c.weighty = 1.0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(3, 3, 3, 3);
        add(clearButton, c);
        
        JPanel emptyPanel = new JPanel();
        c = new GridBagConstraints();
        c.gridx = 2; c.gridy = 4;
        c.weightx = 1; c.weighty = 100;
        c.gridwidth = 1;
        c.gridheight = 7;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(3, 3, 3, 3);
        add(emptyPanel, c);
        
        unitName = new JLabel(force.getName());
        
        c = new GridBagConstraints();
        c.gridx = 3; c.gridy = 0;
        c.weightx = 100; c.weighty = 1.0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(3, 3, 3, 3);
        add(unitName, c);
        
        c = new GridBagConstraints();
        c.gridx = 3; c.gridy = 1;
        c.weightx = 100; c.weighty = 100;
        c.gridwidth = 2;
        c.gridheight = 10;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(3, 3, 3, 3);
        
        JScrollPane selectedScrollPane = new JScrollPane(selectedList);
        add(selectedScrollPane, c);
        
        // Now add the labels in the bottom section
        JLabel title1 = new JLabel("Air:");
        JLabel title2 = new JLabel("Ground:");
        JLabel title3 = new JLabel("Air:");
        JLabel title4 = new JLabel("Ground:");
        JLabel title5 = new JLabel("Experience:");
        
        unitAirRating = new JLabel(String.valueOf(force.calculateRawAirStrength()));
        unitGroundRating = new JLabel(String.valueOf(force.calculateRawGroundStrength()));
        unitExperience = new JLabel(String.valueOf(force.getExperiencePoints()));
        
        reinforcementAir = new JLabel("");
        reinforcementGround = new JLabel("");
                
        okButton = new JButton("Ok");
        okButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                force.addOrder(new RepairOrder(selectedVector));
                for(Reinforcement ref : selectedVector){
                    faction.getReinforcementPool().remove(ref);
                }
                dispose();
            }
        });

        c = new GridBagConstraints();
        c.gridx = 3; c.gridy = 14;
        c.weightx = 50; c.weighty = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(3, 3, 3, 3);
        add(okButton, c);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                dispose();
            }
        });
        
        c = new GridBagConstraints();
        c.gridx = 4; c.gridy = 14;
        c.weightx = 50; c.weighty = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(3, 3, 3, 3);
        add(cancelButton, c);
        
        setModalityType(ModalityType.APPLICATION_MODAL);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(800, 400);
    }

}
