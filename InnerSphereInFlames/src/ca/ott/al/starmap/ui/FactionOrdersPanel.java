package ca.ott.al.starmap.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ca.ott.al.starmap.core.faction.Faction;
import ca.ott.al.starmap.core.order.faction.FactionOrder;
import ca.ott.al.starmap.ui.dialog.DepotConstructionDialog;
import ca.ott.al.starmap.ui.dialog.EstablishGarrisonDialog;
import ca.ott.al.starmap.ui.dialog.FactoryConstructionDialog;
import ca.ott.al.starmap.ui.dialog.FortificationConstructionDialog;

public class FactionOrdersPanel extends JPanel implements FactionAwareJPanel{

    private static final long serialVersionUID = 756952992643013247L;

    private Faction selectedFaction;
    private FactionOrdersPanel thisPanel;
    
    //Active fields of the Economic Report 
    JLabel ruCountLabel, economicRatingCurrentLabel, economicModifierCurrentLabel;
    JLabel economicRatingChangeCurrentLabel, governmentPopCurrentLabel;
    JSpinner spinner;
    
    //Active fields of the Faction Orders Section
    JComboBox orderComboBox;
    JButton issueOrderButton, deleteOrderButton;
    JList ordersList;
    

    /**
     * Constructs the Panel components
     */
    public FactionOrdersPanel(){
        thisPanel = this;
        setLayout(new GridBagLayout());
        
        JPanel economicReportPanel = createEconomicReportPanel();
        JPanel factionOrdersPanel = createFactionOrdersPanel();
        
        JSeparator separator1 = new JSeparator();
        separator1.setForeground(Color.gray);
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridx=0;
        c.gridy=0;
        c.weighty = 1.0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        add(economicReportPanel, c);
        
        c = new GridBagConstraints();
        c.gridx=0;
        c.gridy=1;
        c.weighty = 1.0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.LINE_START;
        add(separator1, c);
        
        c = new GridBagConstraints();
        c.gridx=0;
        c.gridy=2;
        c.weightx = 1.0;
        c.weighty = 10000;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.LAST_LINE_START;
        add(factionOrdersPanel, c);
    }

    private JPanel createFactionOrdersPanel() {
        JPanel ordersPanel = new JPanel();
        ordersPanel.setLayout(new GridBagLayout());
        ordersPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        
        JLabel reportTitle = new JLabel("Faction Orders");
        reportTitle.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        ordersPanel.add(reportTitle, c);
        
        String[] orders = {"Build Depot", "Build Factory", "Build Fortification", "Establish Garrison"
//                "Factory Security", "Hire Mercenaries", "Personnal Security", 
//                , "Sponsor Insurgency", "Supply Security", 
//                "Technology Security", "Warship Security"
                };
        orderComboBox = new JComboBox(orders);
        orderComboBox.setMaximumSize(new Dimension(200, 50));
        orderComboBox.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 10));
        orderComboBox.setSelectedIndex(0);
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.gridheight=1;
        c.gridwidth=1;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        ordersPanel.add(orderComboBox, c);

        issueOrderButton = new JButton("Issue Order");
        attachIssueOrderListener();
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1;
        c.weighty = 1;
        c.gridheight=1;
        c.gridwidth=1;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        issueOrderButton.setBorder(BorderFactory.createLineBorder(Color.black));
        ordersPanel.add(issueOrderButton, c);
        
        deleteOrderButton = new JButton("Delete Order");
        attachDeleteOrderListener();
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 1;
        c.weighty = 1;
        c.gridheight=1;
        c.gridwidth=1;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        deleteOrderButton.setBorder(BorderFactory.createLineBorder(Color.black));
        ordersPanel.add(deleteOrderButton, c);
        
        JPanel emptyPanel = new JPanel();
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 4;
        c.weightx = 1;
        c.weighty = 100;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.VERTICAL;
        ordersPanel.add(emptyPanel, c);
        
        //For now, since no faction is selected by default, fill the list with empty Strings
        String [] standingFactionOrders = new String[1];
        
        ordersList = new JList(standingFactionOrders);
        ordersList.setSelectionMode(JList.VERTICAL);
//        ordersList.addListSelectionListener(new ListSelectionListener() {
//            public void valueChanged(ListSelectionEvent e) {
//                if (!e.getValueIsAdjusting()) {
//                    String name = (String) list.getSelectedValue();
//                    Font font = new Font(name, Font.PLAIN, 12);
//                    label.setFont(font);
//                }
//            }
//        });

        JScrollPane pane = new JScrollPane();
        pane.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        pane.getViewport().add(ordersList);
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 10000;
        c.weighty = 10000;
        c.gridheight=10;
        c.gridwidth=8;
        c.fill = GridBagConstraints.BOTH;
        ordersPanel.add(pane, c);
        
        return ordersPanel;
    }
    
    private void attachIssueOrderListener(){
        issueOrderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String order = (String)orderComboBox.getSelectedItem();
                /*
                "Build Depot", "Build Factory", "Build Fortification", 
                "Factory Security", "Hire Mercenaries", "Personnal Security", 
                "Reestablish Garrison", "Sponsor Insurgency", "Supply Security", 
                "Technology Security", "Warship Security"
                */
                if(order.equalsIgnoreCase("Build Depot")){
                    DepotConstructionDialog dialog = new DepotConstructionDialog(
                            selectedFaction, thisPanel);
                    dialog.setVisible(true);
                } else if(order.equalsIgnoreCase("Build Factory")){
                    FactoryConstructionDialog dialog = new FactoryConstructionDialog(selectedFaction, thisPanel);
                    dialog.setVisible(true);
                } else if(order.equalsIgnoreCase("Build Fortification")){
                    FortificationConstructionDialog dialog = new FortificationConstructionDialog(
                            selectedFaction, thisPanel);
                    dialog.setVisible(true);
                } else if(order.equalsIgnoreCase("Establish Garrison")){
                    EstablishGarrisonDialog dialog = new EstablishGarrisonDialog(selectedFaction, thisPanel);
                    dialog.setVisible(true);
                }
            }
        });
    }
    
    private void attachDeleteOrderListener(){
        deleteOrderButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent arg0) {
                FactionOrder selectedOrder = (FactionOrder)ordersList.getSelectedValue();
                if(selectedOrder != null){
                    selectedFaction.removeFactionOrder(selectedOrder);
                    refreshOrderList();
                }
            }
        });
    }
    
    

    private JPanel createEconomicReportPanel(){
        JPanel economicReportPanel = new JPanel();
        economicReportPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        economicReportPanel.setLayout(new GridBagLayout());
        
        JPanel panelRow1 = new JPanel(); 
        JPanel panelRow2 = new JPanel(); 
        JPanel panelRow3 = new JPanel(); 
        JPanel panelRow4 = new JPanel(); 
        JPanel panelRow5 = new JPanel(); 
        JPanel panelRow6 = new JPanel(); 
        
        JLabel economicReportTitle = new JLabel("Economic Report");
        JLabel ruLabel = new JLabel("Raw resource units available:");
        ruCountLabel = new JLabel("0");
        JLabel econoRatingLabel = new JLabel("Economic rating:");
        economicRatingCurrentLabel = new JLabel("100");
        economicRatingChangeCurrentLabel = new JLabel("+0%");
        JLabel econoModifierLabel = new JLabel("Economic modifier:");
        economicModifierCurrentLabel = new JLabel("6");
        JLabel governmentPopLabel = new JLabel("Government popularity:");
        governmentPopCurrentLabel = new JLabel("100%");
        JLabel civilianSpendingLabel = new JLabel("Civilian sector spending (%):");
        
        int civilianSpending = 20;
        SpinnerModel percentageModel = new SpinnerNumberModel(civilianSpending,
                civilianSpending - 20,
                civilianSpending + 80,
                1);
        spinner = new JSpinner(percentageModel);
        spinner.setEditor(new JSpinner.NumberEditor(spinner, "#"));
        spinner.setBounds(0, 80, 0, 0);
        
        spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
                //The argument should be the JSpinner
                JSpinner localSpinner = (JSpinner)arg0.getSource();
                int value = (Integer)localSpinner.getValue();
                //Capture the previous value
                int oldCivilianSpending = selectedFaction.getEconomy()
                        .getCivilianSpendingPercentile();
                selectedFaction.getEconomy().setCivilianSpendingPercentile(value);
                double availableResources = selectedFaction.getEconomy()
                        .calculateAvailableResourcePoints();
                if(availableResources <= 0){
                    //Refuse the change if this spending would bring RUs below zero
                    //Reverse the change in the model
                    selectedFaction.getEconomy().setCivilianSpendingPercentile(
                            oldCivilianSpending);
                    //Reverse the change in the spinner
                    localSpinner.setValue(String.valueOf(oldCivilianSpending));
                }
                //Container ancestor = getTopLevelAncestor();
                //System.out.println(ancestor);
                InnerSphereInFlamesGui ancestor = (InnerSphereInFlamesGui)getTopLevelAncestor();
                ancestor.refreshAvailableResources();
            }
        });
        
        panelRow1.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelRow1.add(economicReportTitle);
        
        panelRow2.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelRow2.add(ruLabel);
        panelRow2.add(ruCountLabel);
        
        panelRow3.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelRow3.add(econoRatingLabel);
        panelRow3.add(economicRatingCurrentLabel);
        panelRow3.add(economicRatingChangeCurrentLabel);
        
        panelRow4.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelRow4.add(econoModifierLabel);
        panelRow4.add(economicModifierCurrentLabel);
        
        panelRow5.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelRow5.add(governmentPopLabel);
        panelRow5.add(governmentPopCurrentLabel);
        
        panelRow6.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelRow6.add(civilianSpendingLabel);
        panelRow6.add(spinner);
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        
        economicReportPanel.add(panelRow1, c);
        
        c.gridy = 1;
        economicReportPanel.add(panelRow2, c);
        c.gridx = 1;
        economicReportPanel.add(panelRow3, c);
        c.gridx = 2;
        economicReportPanel.add(panelRow4, c);
        
        c.gridy = 2;
        c.gridx = 0;
        economicReportPanel.add(panelRow6, c);
        c.gridx = 1;
        economicReportPanel.add(panelRow5, c);
        
        return economicReportPanel;
    }
    
    
    //Methods------------------------------------------------------------------
    @Override
    public void switchFaction(Faction faction) {
        selectedFaction = faction;
        
        refreshModelData();
    }
    
    public void refreshOrderList(){
        Set<FactionOrder> orders = selectedFaction.getFactionOrders();
        Vector<FactionOrder> ordersVector = new Vector<FactionOrder>(orders);
        ordersList.setListData(ordersVector);
        InnerSphereInFlamesGui ancestor = (InnerSphereInFlamesGui)getTopLevelAncestor();
        ancestor.refreshAvailableResources();
    }
    
    //Private Methods----------------------------------------------------------
    private void refreshModelData(){
//      JLabel ruCountLabel, economicRatingCurrentLabel, economicModifierCurrentLabel;
//      JLabel economicRatingChangeCurrentLabel, governmentPopCurrentLabel;
//      JSpinner spinner;
//      JList ordersList;
        double bank = Math.round(selectedFaction.getEconomy().getResourceBank());
        ruCountLabel.setText(String.valueOf(bank));

        economicRatingCurrentLabel.setText(String.valueOf(selectedFaction.getEconomy()
                .getCurrentEconomicStrength()));
        double modifier = Math.round(selectedFaction.getEconomy().getLatestEconomicModifier());
        economicModifierCurrentLabel.setText(String.valueOf(modifier));

        economicRatingChangeCurrentLabel.setText(String.valueOf(selectedFaction.getEconomy()
                .getLatestEconomicStrengthChange()));

        governmentPopCurrentLabel.setText(String.valueOf(selectedFaction.getFactionLeader()
                .getPopularity()));
        spinner.setValue(selectedFaction.getEconomy().getCivilianSpendingPercentile());

        //Populate the Faction's orders list
        refreshOrderList();
    }
    
}
