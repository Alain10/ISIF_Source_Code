package ca.ott.al.starmap.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import ca.ott.al.starmap.core.GameCore;
import ca.ott.al.starmap.core.faction.Faction;
import ca.ott.al.starmap.core.map.Factory;
import ca.ott.al.starmap.core.map.StarSystem;
import ca.ott.al.starmap.core.order.factory.ProductionItem;
import ca.ott.al.starmap.core.order.factory.ProductionItemSingleton;
import ca.ott.al.starmap.core.order.factory.ProductionLine;

public class ProductionOrdersPanel extends JPanel implements FactionAwareJPanel{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    //JButton createUnitButton;
    JTable productionTable;
    
    ProductionTableModel tableModel;
    JScrollPane scrollPane;
    
    public ProductionOrdersPanel(){
        setLayout(new GridBagLayout());
        
        //createUnitButton = new JButton("Raise New Unit");
        
        GridBagConstraints c = new GridBagConstraints();
//        c.gridx=0;
//        c.gridy=0;
//        c.weighty = 1.0;
//        c.weightx = 1.0;
//        c.fill = GridBagConstraints.HORIZONTAL;
//        c.anchor = GridBagConstraints.FIRST_LINE_START;
//        add(createUnitButton, c);
        
        tableModel = new ProductionTableModel();
        productionTable = new JTable(tableModel );
        
        c = new GridBagConstraints();
        c.gridx=1;
        c.gridy=0;
        c.weighty = 100;
        c.weightx = 1000;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        
        scrollPane = new JScrollPane(productionTable);
        productionTable.setFillsViewportHeight(true);
        add(scrollPane, c);
    }
    
    @Override
    public void switchFaction(Faction faction) {
        if(tableModel != null){
            tableModel.clearTable();
            List<StarSystem> factorySystems = faction.getFactorySystems();
            
            for(StarSystem system : factorySystems){
                Factory factory= system.getPrimaryPlanet().getFactory();
                for (ProductionLine productionLine : factory.getProductionLines()){
                    ProductionTableRow row = new ProductionTableRow(system, factory, productionLine);
                    tableModel.addTableRow(row);
                }
            }
            
            TableColumn itemColumn = productionTable.getColumnModel().getColumn(2);
            
            List<ProductionItem> availableItems = ProductionItemSingleton.getProductionItemSingleton().getAvailableItems();
            JComboBox<ProductionItem> comboBox = new JComboBox<ProductionItem>();
            
            for(ProductionItem item : availableItems){
                comboBox.addItem(item);
            }
            
            comboBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent event) {
                    if (event.getStateChange() == ItemEvent.SELECTED) {
                        JComboBox combo = (JComboBox) event.getSource();
                        
                        ProductionItem selectedItem = (ProductionItem)combo.getSelectedItem();
                        //System.out.println(selectedItem.getName());
                        //System.out.println(combo.getParent().getClass());
                        
                        JTable table = (JTable)combo.getParent();
                        int rowIndex = table.getSelectedRow();
                        //System.out.println(rowIndex);
                        table.getModel().setValueAt(selectedItem, rowIndex, 2);
                        
                    }
                }
            });
            
            itemColumn.setCellEditor(new DefaultCellEditor(comboBox));
            
            scrollPane.repaint();
        }
    }

    /**
     * 
     * @author Al
     *
     */
    public class ProductionTableModel extends AbstractTableModel{
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        
        private String[] columnNames = {"Star System", "Manufacturer", 
                "Producing", "Cost/Unit", "Time Remaining"};
        private List<ProductionTableRow> tableRows = new ArrayList<ProductionTableRow>();
        
        public void clearTable(){
            tableRows.clear();
        }
        
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public int getRowCount() {
            return tableRows.size();
        }

        @Override
        public Object getValueAt(int arg0, int arg1) {
            ProductionTableRow row = tableRows.get(arg0);
            return row.getValueAt(arg1);
        }
        
        public String getColumnName(int col) {
            return columnNames[col];
        }
        
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
        
        public boolean isCellEditable(int row, int col){
            if(col ==2)
                return true; 
            else 
                return false;
        }
        
        public void setValueAt(Object value, int row, int col) {
            if(col == 2){
                ProductionItem item = (ProductionItem)value;
                ProductionTableRow tableRow = tableRows.get(row);
                tableRow.productionLine.setItem(item);
                tableRow.productionLine.setTurnsRemaining(item.getTime());
                fireTableCellUpdated(row, col);
                fireTableCellUpdated(row, col+1);
                fireTableCellUpdated(row, col+2);
                
                InnerSphereInFlamesGui ancestor = (InnerSphereInFlamesGui)getTopLevelAncestor();
                ancestor.refreshAvailableResources();
            }
        }

        public void addTableRow(ProductionTableRow row){
            tableRows.add(row);
        }
    }
    
    /**
     * 
     * @author Al
     *
     */
    public class ProductionTableRow{
        
        public ProductionTableRow(StarSystem system, Factory factory, ProductionLine productionLine) {
            super();
            this.system = system;
            this.factory = factory;
            this.productionLine = productionLine;
        }

        StarSystem system;
        Factory factory;
        ProductionLine productionLine;
        
        public String getValueAt(int column){
            switch(column){
                case 0: return system.getName();
                case 1: return factory.getName();
                case 2: return productionLine.getItem().getName();
                case 3: return String.valueOf(productionLine.getItem().getCost());
                case 4: return String.valueOf(productionLine.getTurnsRemaining());
            }
            
            return "unknown";
        }
        
    }
}
