package ca.ott.al.starmap.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import ca.ott.al.starmap.core.GameCore;
import ca.ott.al.starmap.core.faction.Faction;
import ca.ott.al.starmap.core.map.StarMap;
import ca.ott.al.starmap.file.FactionBuilder;
import ca.ott.al.starmap.file.ForceBuilder;
import ca.ott.al.starmap.file.MapBuilder;

public class InnerSphereInFlamesGui extends JFrame {

    /**
     * Generated by Eclipse
     */
    private static final long serialVersionUID = -2521623386396084393L;

    private static Logger logger; 
    
    private static final int MAX_WIDTH = 5000;

    JLabel turnNumber, leaderName, availableResourceUnits;
    JComboBox factionComboBox;
    JPanel factionOrdersPanel, productionOrdersPanel, unitOrdersPanel, starMapPanel ;
    TurnLogPanel turnLogPanel; 
    JButton endTurnButton;
    
    //Make the Gui a singleton so that components can access it if necessary
    private static InnerSphereInFlamesGui gui;
    
    public static InnerSphereInFlamesGui getGui(){
        if(gui == null){
            gui = new InnerSphereInFlamesGui();
        }
        return gui;
    }
    
    /**
     * Main constructor
     */
    private InnerSphereInFlamesGui(){
        setTitle("The Inner Sphere In Flames");
        
        createMenuBar();

        JPanel background = new JPanel();
        //background.setLayout(new BoxLayout(background, BoxLayout.Y_AXIS));
        background.setLayout(new GridBagLayout());
        
        JPanel topPanel = createTopPanel();
        GridBagConstraints c = new GridBagConstraints();
        c.gridx=0;
        c.gridy=0;
        c.weighty = 1.0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        background.add(topPanel, c);

        //This will be the panel to hold the tab pane
        JTabbedPane mainPane = createMainPane();
        c = new GridBagConstraints();
        c.gridx=0;
        c.gridy=1;
        c.weighty = 10000;
        c.weightx = 10000;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.LINE_START;
        background.add(mainPane, c);
        
        JPanel bottomPanel = createBottomPanel();
        c = new GridBagConstraints();
        c.gridx=0;
        c.gridy=2;
        c.weighty = 1.0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.LAST_LINE_START;
        background.add(bottomPanel, c);
        
        add(background);
        setSize(1000, 800);
        
        // Final step of game preparation, set the default faction 
        if (factionComboBox.getSelectedIndex() == -1 
                && factionComboBox.getModel().getSize() != 0){
            factionComboBox.setSelectedIndex(0);
        }
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }
    
    /**
     * Creates the menu bar
     */
    private void createMenuBar(){
        JMenuBar menubar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        JMenuItem loadGameMenuItem = new JMenuItem("Load Game");
        loadGameMenuItem.setMnemonic(KeyEvent.VK_L);
        loadGameMenuItem.setToolTipText("Load a game from file");
        loadGameMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	JFileChooser fileChooser = new JFileChooser();
                int returnVal = fileChooser.showOpenDialog(InnerSphereInFlamesGui.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    GameCore gameCore = GameCore.getGameCore();
                    boolean success = gameCore.loadGame(file);
                    if(success){
                        //This is adjusting the GUI components, or at least it would start the process
                        Vector<String> factionNames = new Vector<String>(gameCore.getFactions()
                    			.keySet());
                        DefaultComboBoxModel model = new DefaultComboBoxModel(factionNames);
                        factionComboBox.setModel(model);
                        factionComboBox.setSelectedIndex(0);
                        
                        int[] mapExtremities = gameCore.getStarMapExtremities();
                        UnitOrdersPanel panelToUpdate =(UnitOrdersPanel)unitOrdersPanel;
                        panelToUpdate.setScrollingArea(mapExtremities);
                    }
                } 
            }
        });
        fileMenu.add(loadGameMenuItem);
        
        JMenuItem saveGameMenuItem = new JMenuItem("Save Game");
        saveGameMenuItem.setMnemonic(KeyEvent.VK_S);
        saveGameMenuItem.setToolTipText("Save the game to file");
        saveGameMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	JFileChooser fileChooser = new JFileChooser();
                int returnVal = fileChooser.showSaveDialog(InnerSphereInFlamesGui.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    GameCore gameCore = GameCore.getGameCore();
                    gameCore.saveGame(file);
                }
            }
        });
        fileMenu.add(saveGameMenuItem);
        
        JMenuItem saveGameToSheetMenuItem = new JMenuItem("Save Game to Spreadsheet");
        saveGameToSheetMenuItem.setMnemonic(KeyEvent.VK_P);
        saveGameToSheetMenuItem.setToolTipText("Save Game to a .csv file");
        saveGameToSheetMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                JFileChooser fileChooser = new JFileChooser();
                int returnVal = fileChooser.showSaveDialog(InnerSphereInFlamesGui.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    GameCore gameCore = GameCore.getGameCore();
                    gameCore.writeGameCoreCSV(file);
                }
            }
        });
        fileMenu.add(saveGameToSheetMenuItem);
        
        JMenuItem loadGameFromSheetMenuItem = new JMenuItem("Load Game from Spreadsheet");
        loadGameFromSheetMenuItem.setMnemonic(KeyEvent.VK_O);
        loadGameFromSheetMenuItem.setToolTipText("Load Game from a .csv file");
        loadGameFromSheetMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                JFileChooser fileChooser = new JFileChooser();
                int returnVal = fileChooser.showOpenDialog(InnerSphereInFlamesGui.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    GameCore gameCore = GameCore.getGameCore();
                    boolean success = gameCore.readGameCoreCSV(file);
                    if(success){
                        //This is adjusting the GUI components, or at least it would start the process
                        Vector<String> factionNames = new Vector<String>(gameCore.getFactions()
                                .keySet());
                        DefaultComboBoxModel model = new DefaultComboBoxModel(factionNames);
                        factionComboBox.setModel(model);
                        factionComboBox.setSelectedIndex(0);

                        int[] mapExtremities = gameCore.getStarMapExtremities();
                        UnitOrdersPanel panelToUpdate =(UnitOrdersPanel)unitOrdersPanel;
                        panelToUpdate.setScrollingArea(mapExtremities);
                        
                        //populate the proximity maps, essential for movement.
                        gameCore = GameCore.getGameCore();
                        StarMap starMap = gameCore.getStarMap();
                        starMap.populateStarSystemProximityMaps();
                    }
                } 
            }
        });
        fileMenu.add(loadGameFromSheetMenuItem);

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setMnemonic(KeyEvent.VK_X);
        exitMenuItem.setToolTipText("Exit Game");
        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);
        
        menubar.add(fileMenu);
        setJMenuBar(menubar);
    }
    
    /**
     * Creates the top panel that hold essential faction data
     */
    private JPanel createTopPanel(){
        
        JPanel topPanel = new JPanel(new BorderLayout());
        
        JPanel innerTopPanel = new JPanel();
        innerTopPanel.setLayout(new BoxLayout(innerTopPanel, BoxLayout.X_AXIS));
        innerTopPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        
        JLabel turnNumberLabel = new JLabel("Turn:");
        turnNumberLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        innerTopPanel.add(turnNumberLabel);

        turnNumber = new JLabel("0");
        turnNumber.setMinimumSize(new Dimension(30, 20));
        turnNumber.setMaximumSize(new Dimension(30, 20));
        innerTopPanel.add(turnNumber);
        
        JLabel factionNameLabel = new JLabel("Faction:");
        factionNameLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
        innerTopPanel.add(factionNameLabel);
        
        //Set up the choice of faction
        GameCore gameCore = GameCore.getGameCore();
        Vector<String> factionNames = new Vector<String>(gameCore.getFactions().keySet());
        
        factionComboBox = new JComboBox(factionNames);
        factionComboBox.setMaximumSize(new Dimension(250, 50));
        factionComboBox.setBorder(BorderFactory.createEmptyBorder(5, 3, 5, 20));
        factionComboBox.setSelectedIndex(-1);
        innerTopPanel.add(factionComboBox);

        factionComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    JComboBox combo = (JComboBox) event.getSource();
                    String selectedFactionName = (String)combo.getSelectedItem();
                    switchFaction(selectedFactionName);
                }
            }
        });

        JLabel leaderNameLabel = new JLabel("Leader:");
        leaderNameLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        innerTopPanel.add(leaderNameLabel);

        leaderName = new JLabel("Unknown");
        leaderName.setMinimumSize(new Dimension(120, 20));
        leaderName.setMaximumSize(new Dimension(200, 20));
        innerTopPanel.add(leaderName);
        
        JLabel resourceUnitLabel = new JLabel("Available Resource Units: ");
        resourceUnitLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        innerTopPanel.add(resourceUnitLabel);

        availableResourceUnits = new JLabel("0");
        availableResourceUnits.setMinimumSize(new Dimension(40, 20));
        availableResourceUnits.setMaximumSize(new Dimension(40, 20));
        innerTopPanel.add(availableResourceUnits);
        
        JSeparator separator = new JSeparator();
        separator.setForeground(Color.gray);

        topPanel.setMaximumSize(new Dimension(MAX_WIDTH, 50));
        topPanel.add(innerTopPanel, BorderLayout.CENTER);
        topPanel.add(separator, BorderLayout.SOUTH);
        return topPanel;
    }
    

    private JTabbedPane createMainPane(){
        //mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        unitOrdersPanel = new UnitOrdersPanel();
        tabbedPane.addTab("Star Map", null, unitOrdersPanel,"unitOrders");
        factionOrdersPanel = new FactionOrdersPanel();
        tabbedPane.addTab("Faction Orders", null, factionOrdersPanel, "faction");
        productionOrdersPanel = new ProductionOrdersPanel();
        tabbedPane.addTab("Production Orders", null, productionOrdersPanel, "production");
        turnLogPanel = new TurnLogPanel();
        tabbedPane.addTab("End of Turn Log", null, turnLogPanel, "turnLog");
        
        return tabbedPane;
    }
    

    private JPanel createBottomPanel(){
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        JPanel innerPanel = new JPanel();
        innerPanel.setAlignmentX(1f);
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));
        bottomPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        
        endTurnButton = new JButton("End Turn");
        endTurnButton.setMinimumSize(new Dimension(150,25));
        endTurnButton.setPreferredSize(new Dimension(150,25));
        
        endTurnButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        //innerPanel.add(endTurnButton);
        
        JSeparator separator = new JSeparator();
        separator.setForeground(Color.gray);

        bottomPanel.setMaximumSize(new Dimension(MAX_WIDTH, 50));
        
        bottomPanel.add(innerPanel, BorderLayout.WEST);
        bottomPanel.add(endTurnButton, BorderLayout.EAST);
        bottomPanel.add(separator, BorderLayout.NORTH);
        
        createEndOfTurnButtonListener();
        
        return bottomPanel;
    }
    
    private void createEndOfTurnButtonListener(){
        endTurnButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                
                GameCore gameCore = GameCore.getGameCore();
                
                gameCore.executeEndOfTurn();
                
                //Force a refreshing of all the GUI components
                turnLogPanel.refresh();
                switchFaction(factionComboBox.getSelectedItem().toString());
            }
        });
    }
    
    //Methods------------------------------------------------------------------
    public void refreshAvailableResources(){
        String selectedFactionName = (String)factionComboBox.getSelectedItem();
        if(selectedFactionName != null && !selectedFactionName.isEmpty()){
            GameCore gameCore = GameCore.getGameCore();
            Faction faction = gameCore.getFactionByName(selectedFactionName);
            if(faction != null){
                double availableRUs = faction.getEconomy()
                        .calculateAvailableResourcePoints();
                availableRUs = Math.round(availableRUs* 10)/10;
                availableResourceUnits.setText(String.valueOf(availableRUs));
            }
        }
    }
    
    /**
     * Method called by a change in the Faction ComboBox
     * @param selectedFactionName
     */
    protected void switchFaction(String selectedFactionName) {
        GameCore gameCore = GameCore.getGameCore(); 
        turnNumber.setText(String.valueOf(gameCore.getTurnNumber()));
        Faction faction = gameCore.getFactionByName(selectedFactionName);
        if (faction != null){
            leaderName.setText(faction.getFactionLeader().getName());
            
            double availableRUs = faction.getEconomy().calculateAvailableResourcePoints();
            availableRUs = Math.floor(availableRUs);
            availableResourceUnits.setText(String.valueOf(availableRUs));
            
            //Need to contact the other panels and have them update themselves
            //That, or use some sort of Listener
            FactionAwareJPanel factionOrders = (FactionAwareJPanel)factionOrdersPanel;
            factionOrders.switchFaction(faction);
            FactionAwareJPanel unitOrders = (FactionAwareJPanel)unitOrdersPanel;
            unitOrders.switchFaction(faction);
            FactionAwareJPanel productionOrders = (FactionAwareJPanel)productionOrdersPanel;
            productionOrders.switchFaction(faction);
        }
    }
    
    
    //Static Methods-----------------------------------------------------------
    private static void initializeDevelopmentGameCore(){
        System.out.println("Starting The Inner Sphere In Flames. \nCreating GameCore instance.");
        GameCore gameCore = GameCore.getGameCore();
        
        //Do the StarMap
        System.out.println("Creating the Starmap and loading its data.");
        StarMap starMap = gameCore.getStarMap();
        MapBuilder.execute(starMap);
        MapBuilder.executeReadOfPopulousWorlds(gameCore);        
        MapBuilder.populateProximityGraph(gameCore);

        //Do the Factions
        System.out.println("Creating the Factions.");
        FactionBuilder.execute(gameCore);
        
        //Do the Faction Maps
        System.out.println("Creating a map of the factions' territory.");
        MapBuilder.executeFactionMap(gameCore);
        
        //Create the Units
        System.out.println("Creating the units.");
        ForceBuilder.executeUnitBuild3039(gameCore);
        
        FactionBuilder.initializeEconomicDataForTurnOne(gameCore);
    }
    
    private static void initializeEmptyGameCore(){
        System.out.println("Starting The Inner Sphere In Flames. \nCreating GameCore instance.");
        GameCore gameCore = GameCore.getGameCore();
        
        //TODO: Flesh out an empty game environment
    }
    
    private static void initializeDefaultGameCoreFromCSV(){
        
        Map<String, String> env = System.getenv();
        //Initialize Log4j
        String log4jConfigFile = env.get("ISIFLOG")+"/config/Log4j.properties";
        PropertyConfigurator.configure(log4jConfigFile);
        
        logger = Logger.getLogger(InnerSphereInFlamesGui.class.getName());
        
        logger.debug("Starting The Inner Sphere In Flames. Creating GameCore instance.");
        GameCore gameCore = GameCore.getGameCore();
        
        //String filename = "/home/al/isifworkspace/InnerSphereInFlames/resources/3039_dev_base2.csv";
        
        String filename = env.get("ISIFDEFAULT");
        
        logger.debug("Initializing from "+filename);
        
        File file = new File(filename);
        boolean success = gameCore.readGameCoreCSV(file);
        if(success){
            logger.debug("Gamecore successfully Initialized");
                        
            //populate the proximity maps, essential for movement
            //Note that the read from the CSV wipes out the old core
            //instance and it needs to be gotten again, hence the next
            //line is not superfluous.
            gameCore = GameCore.getGameCore();
            StarMap starMap = gameCore.getStarMap();
            starMap.populateStarSystemProximityMaps();
            
            //Final preparations for turn 1
            //FactionBuilder.initializeEconomicDataForTurnOne(gameCore);
        }
    }
    
    public static void main(String[] args) {
        //initializeDevelopmentGameCore();
        initializeDefaultGameCoreFromCSV();
        gui = getGui();
    }

    public void updateFactionData(Faction faction) {
        if (faction != null){
            leaderName.setText(faction.getFactionLeader().getName());
            
            double availableRUs = faction.getEconomy().calculateAvailableResourcePoints();
            availableRUs = Math.floor(availableRUs);
            availableResourceUnits.setText(String.valueOf(availableRUs));
        }
    }
}
