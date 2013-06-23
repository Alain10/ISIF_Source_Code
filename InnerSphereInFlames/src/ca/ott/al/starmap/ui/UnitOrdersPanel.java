package ca.ott.al.starmap.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import org.apache.log4j.Logger;

import ca.ott.al.starmap.core.UnitOrderExecutor;
import ca.ott.al.starmap.core.faction.Faction;
import ca.ott.al.starmap.core.map.StarSystem;
import ca.ott.al.starmap.core.order.Order;
import ca.ott.al.starmap.core.order.OrderQueue;
import ca.ott.al.starmap.core.order.unit.aerospace.CommerceRaidOrder;
import ca.ott.al.starmap.core.order.unit.aerospace.InterdictOrder;
import ca.ott.al.starmap.core.order.unit.aerospace.PatrolOrder;
import ca.ott.al.starmap.core.order.unit.combat.AttackOrder;
import ca.ott.al.starmap.core.order.unit.combat.EmbarkationOrder;
import ca.ott.al.starmap.core.order.unit.combat.TrainingOrder;
import ca.ott.al.starmap.core.order.unit.combat.DefendOrder;
import ca.ott.al.starmap.core.order.unit.combat.DigInOrder;
import ca.ott.al.starmap.core.order.unit.combat.ShieldOrder;
import ca.ott.al.starmap.core.order.unit.move.AssaultOrder;
import ca.ott.al.starmap.core.order.unit.move.MoveOrder;
import ca.ott.al.starmap.core.order.unit.support.RepairOrder;
import ca.ott.al.starmap.core.order.unit.support.RestOrder;
import ca.ott.al.starmap.core.order.unit.support.ResupplyOrder;
import ca.ott.al.starmap.core.unit.GarrisonForce;
import ca.ott.al.starmap.core.unit.MilitaryForce;
import ca.ott.al.starmap.core.unit.StarMapUnit;
import ca.ott.al.starmap.ui.dialog.RepairUnitDialog;

public class UnitOrdersPanel extends JPanel implements FactionAwareJPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -750644830338573950L;

    Logger logger = Logger.getLogger(UnitOrdersPanel.class);
    
    
    private Faction selectedFaction;
    private final int starMapHeight = 2000;
    private final int starMapWidth = 2000;
    
    JLabel movementLabel = new JLabel("Movement Orders");
    JButton moveButton = new JButton("Move");
    JButton assaultButton = new JButton("Assault");
    JButton interdictButton = new JButton("Interdict");
    JButton commerceRaidButton = new JButton("Commerce Raid");
    JButton patrolButton = new JButton("Patrol");
    
    JLabel combatLabel = new JLabel("Combat Orders");
    JButton defendButton = new JButton("Defend");
    JButton digInButton = new JButton("Dig In");
    JButton shieldButton = new JButton("Shield");
    JButton attackButton = new JButton("Attack");
    JButton trainingButton = new JButton("Training");
    JButton embarkButton = new JButton("Embark");

    JLabel supportLabel = new JLabel("Support Orders");
    JButton resupplyButton = new JButton("Resupply");
    JButton restButton = new JButton("Rest");
    JButton repairButton = new JButton("Repair");

    StarMapPanel starMapPanel;
    JScrollBar verticalBar;
    JScrollBar horizontalBar;
    JComboBox<String> zoomComboBox;
    JLabel transportPoolSize;
    JLabel transportPoolAvailable;  
    JLabel commercialTransport;
    UnitOrdersDetailPanel unitOrdersDetailPanel;
    
    /**
     * Switches the Faction.  Also, this method is responsible for refreshing
     * this panel
     */
    @Override
    public void switchFaction(Faction faction) {
        selectedFaction = faction;
        updateTransportDetails();
        
        //TODO, implement behavior on this panel that limits what the user can
        //do based on Faction.  For development, leave the game board open.
        
        //Update all of the content of the order details panel
        unitOrdersDetailPanel.updateSystemDetails();
        
        //Inform the starMapPanel that the selected faction has changed
        starMapPanel.switchFaction(faction);
        //Have the StarMap refresh itself
        starMapPanel.repaint();
    }
    
    /**
     * Precondition: the selectedFaction variable needs to have been set 
     */
    public void updateTransportDetails(){
        //Update the labels for the transport pool data
        transportPoolSize.setText(new Integer(selectedFaction.getTransportPool()
                .getTotalTransportPoints()).toString());
        transportPoolAvailable.setText(new Integer(selectedFaction.getTransportPool()
                .getTransportPointsAvailable()).toString());
        commercialTransport.setText(new Double(selectedFaction.getTransportPool()
                .getTransportSpendingThisTurn()).toString());
        //This is a change that affects the state of the Faction's economy
        
    }
    
    public UnitOrdersPanel(){
        setLayout(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 1; c.weightx = 1.0; c.weighty = 1.0; 
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(2,2,2,2);
        
        add(movementLabel, c); c.gridy = 2;
        add(moveButton, c); c.gridy = 3;
        moveButton.setMnemonic(KeyEvent.VK_M);
        add(assaultButton, c); c.gridy = 4;
        add(interdictButton, c); c.gridy = 5;
        add(commerceRaidButton, c); c.gridy = 6;
        add(patrolButton, c); c.gridy = 7;
        
        add(combatLabel, c); c.gridy = 8;
        add(defendButton, c); c.gridy = 9;
        add(digInButton, c); c.gridy = 10;
        add(shieldButton, c); c.gridy = 11;
        add(attackButton, c); c.gridy = 12;
        add(trainingButton, c); c.gridy = 13;
        add(embarkButton, c); c.gridy = 14;
        
        add(supportLabel, c); c.gridy = 15;
        add(resupplyButton, c); c.gridy = 16;
        add(restButton, c); c.gridy = 17;
        add(repairButton, c); c.gridy = 18;
        
        //Disable all the buttons by default
        moveButton.setEnabled(false);
        assaultButton.setEnabled(false);
        interdictButton.setEnabled(false);
        commerceRaidButton.setEnabled(false);
        patrolButton.setEnabled(false);
        
        defendButton.setEnabled(false);
        digInButton.setEnabled(false);
        shieldButton.setEnabled(false);
        attackButton.setEnabled(false);
        trainingButton.setEnabled(false);
        embarkButton.setEnabled(false);
        
        resupplyButton.setEnabled(false);
        restButton.setEnabled(false);
        repairButton.setEnabled(false);
        
        starMapPanel = new StarMapPanel(this);
        c = new GridBagConstraints();
        c.gridy = 1; c.gridx = 2; c.weightx = 1000; c.weighty = 1000; 
        c.gridheight = 17;
        c.gridwidth = 6;
        c.fill = GridBagConstraints.BOTH;
        add(starMapPanel, c);

        verticalBar = new JScrollBar(JScrollBar.VERTICAL, 10, 0, 
                -(starMapHeight/2), (starMapHeight/2));
//        c = new GridBagConstraints();
//        c.gridy = 1; c.gridx = 7; c.weightx = 1.0; c.weighty = 1000; 
//        c.gridheight = 17;
//        c.gridwidth = 1;
//        c.fill = GridBagConstraints.VERTICAL;
//        add(verticalBar, c);

        horizontalBar = new JScrollBar(JScrollBar.HORIZONTAL, -10, 0, 
                -(starMapWidth/2), (starMapWidth/2));
//        c = new GridBagConstraints();
//        c.gridy = 18; c.gridx = 2; c.weightx = 1000; c.weighty = 1.0; 
//        c.gridheight = 1;
//        c.gridwidth = 6;
//        c.fill = GridBagConstraints.HORIZONTAL;
//        add(horizontalBar, c);
        
        createTransportPoolPanel();
        
//      JPanel emptyPanel = new JPanel();
//      c = new GridBagConstraints();
//      c.gridy = 0; c.gridx = 3; c.weightx = 100; c.weighty = 1.0;
//      c.gridheight = 1;
//      c.gridwidth = 3;
//      c.fill = GridBagConstraints.HORIZONTAL;
//      add(emptyPanel, c);
        
        createZoomComboBox();
        c = new GridBagConstraints();
        c.gridy = 0; c.gridx = 7; c.weightx = 1.0; c.weighty = 1.0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(zoomComboBox, c);
        
        unitOrdersDetailPanel = new UnitOrdersDetailPanel(this);
        c = new GridBagConstraints();
        c.gridy = 19; c.gridx = 0;
        c.gridheight = 8;
        c.gridwidth = 8;
        c.weightx = 1000; c.weighty = 200; 
        c.fill = GridBagConstraints.BOTH;
        add(unitOrdersDetailPanel, c);

//        createScrollBarListeners();
        createMoveOrderButtonListener();
        createAssaultOrderButtonListener();
        
        createAerospaceOrderButtonListeners();
        createCombatOrderButtonListeners();
        createSupportOrderButtonListeners();
        
    }
    
    //methods =================================================================
    
    private void createTransportPoolPanel(){
        JLabel transportPoolSizeTitle = new JLabel("Transport Pool: ");
        JLabel transportPoolAvailableTitle = new JLabel("Available Transports: ");
        JLabel commercialTransportTitle = new JLabel("Commercial Transports: ");
        
        transportPoolSize = new JLabel("0");
        transportPoolAvailable = new JLabel("0");  
        commercialTransport = new JLabel("0");
        
        JPanel transportPoolSizePanel = new JPanel();
        transportPoolSizePanel.setLayout(new BoxLayout(transportPoolSizePanel, BoxLayout.X_AXIS));
        transportPoolSizePanel.add(transportPoolSizeTitle);
        transportPoolSizePanel.add(transportPoolSize);
        
        JPanel transportPoolAvailablePanel = new JPanel();
        transportPoolAvailablePanel.setLayout(new BoxLayout(transportPoolAvailablePanel, BoxLayout.X_AXIS));
        transportPoolAvailablePanel.add(transportPoolAvailableTitle);
        transportPoolAvailablePanel.add(transportPoolAvailable);
        
        JPanel commercialPanel = new JPanel();
        commercialPanel.setLayout(new BoxLayout(commercialPanel, BoxLayout.X_AXIS));
        commercialPanel.add(commercialTransportTitle);
        commercialPanel.add(commercialTransport);
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0; c.gridx = 3; c.weightx = 100; c.weighty = 1.0;
        c.gridheight = 1; c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        add(transportPoolSizePanel, c);
        
        c = new GridBagConstraints();
        c.gridy = 0; c.gridx = 4; c.weightx = 100; c.weighty = 1.0;
        c.gridheight = 1; c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        add(transportPoolAvailablePanel, c);
        
        c = new GridBagConstraints();
        c.gridy = 0; c.gridx = 5; c.weightx = 100; c.weighty = 1.0;
        c.gridheight = 1; c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        add(commercialPanel, c);
        
        JPanel emptyPanel = new JPanel();
        c = new GridBagConstraints();
        c.gridy = 0; c.gridx = 6; c.weightx = 2000; c.weighty = 1.0;
        c.gridheight = 1; c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(emptyPanel, c);
   }
    
    /**
     * Adjusts which buttons are available to the user based on the unit type
     * @param selectedUnit
     */
    public void updateOrderButtonsContext(StarMapUnit selectedUnit){
        
        //For all units, if they already have a combat or assault order, the 
        //only other order they can receive is a move order.
        OrderQueue orderQueue = selectedUnit.getOrders();
        //Rules for combat orders
        if(orderQueue.containsCombatOrder() 
                || orderQueue.containsAerospaceOrder()
                || orderQueue.containsAssaultOrder()
                || orderQueue.queueIsFull()){
            //Disable everything but the move button
            //Aerospace orders
            interdictButton.setEnabled(false);
            commerceRaidButton.setEnabled(false);
            patrolButton.setEnabled(false);
            
            //MovementOrder
            assaultButton.setEnabled(false);
            if(selectedUnit instanceof MilitaryForce){
                //We have a regular military unit
                MilitaryForce unit = (MilitaryForce)selectedUnit;
                
                //If the unit a garrison, do not allow it to move
                if(unit instanceof GarrisonForce){
                    moveButton.setEnabled(false);
                } else {
                    //Rule simplification: no move orders for units with orders
                    moveButton.setEnabled(false);
                }
            }
            
            //Combat orders
            defendButton.setEnabled(false);
            digInButton.setEnabled(false);
            shieldButton.setEnabled(false);
            attackButton.setEnabled(false);
            trainingButton.setEnabled(false);
            embarkButton.setEnabled(false);
            //Support orders
            resupplyButton.setEnabled(false);
            restButton.setEnabled(false);
            repairButton.setEnabled(false);
        } else {
            if(selectedUnit instanceof MilitaryForce){
                //We have a regular military unit
                MilitaryForce unit = (MilitaryForce)selectedUnit;
                
                //If the unit a garrison, do not allow it to move
                if(unit instanceof GarrisonForce){
                    moveButton.setEnabled(false);
                    assaultButton.setEnabled(false);
                } else {
                    //Unit can move, provided it does not have a re-supply or 
                    //rest order
                    if(orderQueue.containsRestOrResupply()){
                        moveButton.setEnabled(false);
                    } else {
                        moveButton.setEnabled(true);
                        //Assault ought to be incompatible with repair
                        if(orderQueue.containsRepair() || orderQueue.containsRestOrResupply()){
                            assaultButton.setEnabled(false);
                        } else {
                            assaultButton.setEnabled(true);
                        }
                    }
                }
                
                //if the unit has a rest or resupply order it can't be
                //made to move unless attacked
                
                //Support orders can be accepted if there are no combat orders
                //and only one support order is in effect.
                if(orderQueue.hasFewerThanTwoSupportOrder()){
                    if(orderQueue.canRepair()){
                        repairButton.setEnabled(true);
                    } else {
                        repairButton.setEnabled(false);
                    }
                    if(orderQueue.canResupply()){
                        resupplyButton.setEnabled(true);
                    } else {
                        resupplyButton.setEnabled(false);
                    }
                    restButton.setEnabled(true);
                } else {
                    resupplyButton.setEnabled(false);
                    restButton.setEnabled(false);
                    repairButton.setEnabled(false);
                }
                
                //Combat orders can be accepted if no support or assault orders
                //are in the list 
                if(orderQueue.canAcceptCombatOrder()){
                    defendButton.setEnabled(true);
                    //TODO: temporary: set the shield button offline 
                    //shieldButton.setEnabled(true);
                    attackButton.setEnabled(true);
                    trainingButton.setEnabled(true);
                    //TODO: temporary: set the embark button offline 
                    //embarkButton.setEnabled(true);
                    
                    //If it in not an aerospace-only force, disable the aerospace-only buttons
                    //Otherwise, enable those same
                    if(unit.getGroundRating()>0){
                        interdictButton.setEnabled(false);
                        commerceRaidButton.setEnabled(false);
                        patrolButton.setEnabled(false);
                        digInButton.setEnabled(true);
                    } else {
                        interdictButton.setEnabled(true);
                        commerceRaidButton.setEnabled(true);
                        patrolButton.setEnabled(true);
                        digInButton.setEnabled(false);
                    }
                } else {
                    assaultButton.setEnabled(false);
                    defendButton.setEnabled(false);
                    digInButton.setEnabled(false);
                    shieldButton.setEnabled(false);
                    attackButton.setEnabled(false);
                    trainingButton.setEnabled(false);
                    embarkButton.setEnabled(false);
                }
            } else {
                //TODO handle warships
                
            }
        }
    }
    
    //private methods
    private void createZoomComboBox() {
        String[] zoomLevels = { "50%", "75%", "100%", "200%", "300%",
                "400%", "500%", "600%" , "750%", "1000%"};

        zoomComboBox = new JComboBox(zoomLevels);
        zoomComboBox.setMaximumSize(new Dimension(200, 50));
        zoomComboBox.setBorder(BorderFactory.createEmptyBorder(5, 3, 5, 20));
        zoomComboBox.setSelectedIndex(1);
        zoomComboBox.setMaximumRowCount(10);
        
        zoomComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    JComboBox<String> combo = (JComboBox<String>) event.getSource();
                    String selected = (String) combo.getSelectedItem();
                    alterZoomLevel(selected);
                }
            }
        });
    }

    private void alterZoomLevel(String zoomLevel) {
        String numeral = zoomLevel.substring(0, zoomLevel.length()-1);
        starMapPanel.setZoom(Integer.parseInt(numeral));
    }
    
//    private void createScrollBarListeners(){
//        
//        verticalBar.setUnitIncrement(20);
//        verticalBar.setBlockIncrement(50);
//        verticalBar.addAdjustmentListener(new AdjustmentListener() {
//            
//            @Override
//            public void adjustmentValueChanged(AdjustmentEvent e) {
//                int position = e.getValue();
//                logger.debug("map scrollbar Y: " + position*-1);
//                starMapPanel.setMapCoordinateY(position*-1);
//                starMapPanel.repaint();
//            }
//        });
//    
//
//        horizontalBar.setUnitIncrement(20);
//        horizontalBar.setBlockIncrement(50);
//        horizontalBar.addAdjustmentListener(new AdjustmentListener() {
//            
//            @Override
//            public void adjustmentValueChanged(AdjustmentEvent e) {
//                int position = e.getValue();
//                logger.debug("map scrollbar X: " + position);
//                starMapPanel.setMapCoordinateX(position);
//                starMapPanel.repaint();
//            }
//        });
//    }
    
//    public void adjustStarMapScrollBars(double mapCoordinateX, double mapCoordinateY){
//        horizontalBar.setValue((int)Math.round(mapCoordinateX));
//        verticalBar.setValue((int)Math.round(mapCoordinateY));
//    }
    
    private void createMoveOrderButtonListener(){
        
        moveButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                //Set the starmap panel into movement mode.  The user must 
                //click on the starmap or else cancel what he is doing
                starMapPanel.setMoveMode(true);
                
                //Determine the Unit's candidate destinations from the star 
                //system where it is located or the StarSystem that is its
                //last programmed location
                StarSystem selectedSystem = unitOrdersDetailPanel.getSelectedSystem();
                
                //The real location is not the selected system.  It is the last 
                //location on the unit's movement list.
                StarMapUnit selectedUnit = unitOrdersDetailPanel.getSelectedUnit();
                StarSystem sourceSystem = resolveUnitDestination(selectedUnit, selectedSystem);
                
                Map<StarSystem, Double> proximityMap = sourceSystem.getSystemsWithin30LY();
                starMapPanel.setSourceSystem(sourceSystem);
                starMapPanel.setCadidateDestinations(proximityMap.keySet());
                
                starMapPanel.repaint();
            }
        });
        
        
    }
    
    private void createAssaultOrderButtonListener() {
        assaultButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                //Set the starmap panel into movement mode.  The user must 
                //click on the starmap or else cancel what he is doing
                starMapPanel.setAssaultMode(true);
                
                //Determine the Unit's candidate destinations from the star 
                //system where it is located or the StarSystem that is its
                //last programmed location
                StarSystem selectedSystem = unitOrdersDetailPanel.getSelectedSystem();
                
                //The real location is not the selected system.  It is the last 
                //location on the unit's list.
                StarMapUnit selectedUnit = unitOrdersDetailPanel.getSelectedUnit();
                
                StarSystem sourceSystem = resolveUnitDestination(selectedUnit, selectedSystem);
                Map<StarSystem, Double> proximityMap = sourceSystem.getSystemsWithin30LY();
                
                starMapPanel.setSourceSystem(sourceSystem);
                starMapPanel.setCadidateDestinations(proximityMap.keySet());
                
                starMapPanel.repaint();
            }
        });
        
    }

    private void createAerospaceOrderButtonListeners() {
        commerceRaidButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //Get a handle on the selected unit
                StarMapUnit selectedUnit = unitOrdersDetailPanel.getSelectedUnit();
                
                //Determine the unit's location or future location if it will be moving this turn. 
                StarSystem selectedSystem = unitOrdersDetailPanel.getSelectedSystem();
                StarSystem destinationSystem = resolveUnitDestination(selectedUnit, selectedSystem);
                
                //Create the commerce raid order and put it at the end of the unit's queue
                CommerceRaidOrder order = new CommerceRaidOrder(destinationSystem);
                selectedUnit.addOrder(order);
                getUnitOrdersDetailPanel().updateUnitOrders(selectedUnit);
                updateOrderButtonsContext(selectedUnit);
            }
            
        });
        
        interdictButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                //Get a handle on the selected unit
                StarMapUnit selectedUnit = unitOrdersDetailPanel.getSelectedUnit();
                
                //Determine the unit's location or future location if it will be moving this turn. 
                StarSystem selectedSystem = unitOrdersDetailPanel.getSelectedSystem();
                StarSystem destinationSystem = resolveUnitDestination(selectedUnit, selectedSystem);
                
                //Create the interdiction order and put it at the end of the unit's queue
                InterdictOrder order = new InterdictOrder(destinationSystem);
                selectedUnit.addOrder(order);
                getUnitOrdersDetailPanel().updateUnitOrders(selectedUnit);
                updateOrderButtonsContext(selectedUnit);
            }
            
        });
        
        patrolButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                //Get a handle on the selected unit
                StarMapUnit selectedUnit = unitOrdersDetailPanel.getSelectedUnit();
                
                //Determine the unit's location or future location if it will be moving this turn. 
                StarSystem selectedSystem = unitOrdersDetailPanel.getSelectedSystem();
                StarSystem destinationSystem = resolveUnitDestination(selectedUnit, selectedSystem);
                
                //Create the patrol order and put it at the end of the unit's queue
                PatrolOrder order = new PatrolOrder(destinationSystem);
                selectedUnit.addOrder(order);
                getUnitOrdersDetailPanel().updateUnitOrders(selectedUnit);
                updateOrderButtonsContext(selectedUnit);
            }
            
        });
    }
    
    private void createCombatOrderButtonListeners() {
        attackButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                StarMapUnit selectedUnit = unitOrdersDetailPanel.getSelectedUnit();
                AttackOrder order = new AttackOrder();
                selectedUnit.addOrder(order);
                getUnitOrdersDetailPanel().updateUnitOrders(selectedUnit);
                updateOrderButtonsContext(selectedUnit);
            }
        });
        
        trainingButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                StarMapUnit selectedUnit = unitOrdersDetailPanel.getSelectedUnit();
                TrainingOrder order = new TrainingOrder();
                selectedUnit.addOrder(order);
                getUnitOrdersDetailPanel().updateUnitOrders(selectedUnit);
                updateOrderButtonsContext(selectedUnit);
            }
        });
        
        defendButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                StarMapUnit selectedUnit = unitOrdersDetailPanel.getSelectedUnit();
                DefendOrder order = new DefendOrder();
                selectedUnit.addOrder(order);
                getUnitOrdersDetailPanel().updateUnitOrders(selectedUnit);
                updateOrderButtonsContext(selectedUnit);
            }
        });
        
        embarkButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                StarMapUnit selectedUnit = unitOrdersDetailPanel.getSelectedUnit();
                
                //TODO create a dialog for selecting the warship
                EmbarkationOrder order = new EmbarkationOrder();
                selectedUnit.addOrder(order);
                getUnitOrdersDetailPanel().updateUnitOrders(selectedUnit);
                updateOrderButtonsContext(selectedUnit);
            }
        });
        
        digInButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                StarMapUnit selectedUnit = unitOrdersDetailPanel.getSelectedUnit();
                DigInOrder order = new DigInOrder();
                selectedUnit.addOrder(order);
                getUnitOrdersDetailPanel().updateUnitOrders(selectedUnit);
                updateOrderButtonsContext(selectedUnit);
                
                //The following line should force a refresh of the faction's economic data
                InnerSphereInFlamesGui gui = InnerSphereInFlamesGui.getGui();
                gui.switchFaction(selectedUnit.getOwner().getFactionName());
            }
        });
        
        shieldButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                StarMapUnit selectedUnit = unitOrdersDetailPanel.getSelectedUnit();
                ShieldOrder order = new ShieldOrder();
                selectedUnit.addOrder(order);
                getUnitOrdersDetailPanel().updateUnitOrders(selectedUnit);
                updateOrderButtonsContext(selectedUnit);
            }
        });
    }
    
    private void createSupportOrderButtonListeners() {
        restButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                StarMapUnit selectedUnit = unitOrdersDetailPanel.getSelectedUnit();
                RestOrder order = new RestOrder();
                selectedUnit.addOrder(order);
                getUnitOrdersDetailPanel().updateUnitOrders(selectedUnit);
                updateOrderButtonsContext(selectedUnit);
            }
        });
        
        resupplyButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                StarMapUnit selectedUnit = unitOrdersDetailPanel.getSelectedUnit();
                ResupplyOrder order = new ResupplyOrder();
                selectedUnit.addOrder(order);
                getUnitOrdersDetailPanel().updateUnitOrders(selectedUnit);
                updateOrderButtonsContext(selectedUnit);
            }
        });
        
        repairButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                StarMapUnit selectedUnit = unitOrdersDetailPanel.getSelectedUnit();
                RepairUnitDialog dialog = new RepairUnitDialog(selectedUnit.getOwner(),
                        (MilitaryForce)selectedUnit);
                //dialog.pack();
                dialog.setVisible(true);
                
                //RepairOrder order = new RepairOrder();
                //selectedUnit.addOrder(order);
                getUnitOrdersDetailPanel().updateUnitOrders(selectedUnit);
                updateOrderButtonsContext(selectedUnit);
            }
        });
    }


    
    //Methods -----------------------------------------------------------------
    public void setScrollingArea(int [] mapExtremities){
        
        int minX = mapExtremities[0];
        int maxX = mapExtremities[1];
        int minY = mapExtremities[2];
        int maxY = mapExtremities[3];
        
        if(maxX < minX || maxY < minY){
            //Gros bobo..
            return;
        }
        minX -= -10;
        maxX += 10;
        minY -= 10;
        maxY += 10;
        horizontalBar.setMinimum(minX);
        horizontalBar.setMaximum(maxX);
        verticalBar.setMinimum(minY);
        verticalBar.setMaximum(maxY);
        horizontalBar.setValue((minX+maxX)/2);
        verticalBar.setValue((minY+maxY)/2);
    }
    
    /**
     * Attempts to resolve the star system at the end of the order queue.
     * I should have used polymorphism here...
     * @param unit
     * @param start
     * @return
     */
    protected StarSystem resolveUnitDestination(UnitOrderExecutor unit, StarSystem start){
        //TODO In the future, add support for command circuits
        StarSystem anticipatedLocation = start;
        
        OrderQueue queue = unit.getOrders();
        List<Order> orders = queue.getOrdersList();
        for(Order order : orders){
            if(order instanceof MoveOrder){
                StarSystem destination = ((MoveOrder)order).getDestination();
                anticipatedLocation = destination;
            } else if(order instanceof AssaultOrder){
                StarSystem destination = ((AssaultOrder)order).getDestination();
                anticipatedLocation = destination;
            }
        }
        return anticipatedLocation;
    }
    
    //Access Methods ----------------------------------------------------------
    public UnitOrdersDetailPanel getUnitOrdersDetailPanel(){
        return unitOrdersDetailPanel;
    }
    
    public StarMapPanel getStarMpaPanel(){
        return starMapPanel;
    }

    public Faction getSelectedFaction() {
        return selectedFaction;
    }
    
    
    
}
