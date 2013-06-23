package ca.ott.al.starmap.core.order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.spi.TriggeringEventEvaluator;

import ca.ott.al.starmap.core.order.unit.aerospace.AerospaceOrder;
import ca.ott.al.starmap.core.order.unit.combat.AttackOrder;
import ca.ott.al.starmap.core.order.unit.combat.CombatOrder;
import ca.ott.al.starmap.core.order.unit.combat.DefaultDefendOrder;
import ca.ott.al.starmap.core.order.unit.combat.DigInOrder;
import ca.ott.al.starmap.core.order.unit.combat.TrainingOrder;
import ca.ott.al.starmap.core.order.unit.move.AssaultOrder;
import ca.ott.al.starmap.core.order.unit.move.MovementOrder;
import ca.ott.al.starmap.core.order.unit.support.RepairOrder;
import ca.ott.al.starmap.core.order.unit.support.RestOrder;
import ca.ott.al.starmap.core.order.unit.support.ResupplyOrder;
import ca.ott.al.starmap.core.order.unit.support.SupportOrder;


public class OrderQueue implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -8626189099412783194L;

    private List<Order> ordersList; 
    private int maxNumberOrdersPerTurn = 4; //default
    
    public OrderQueue(boolean initializeDefault, int maxNumberOrdersPerTurn){
        ordersList = new ArrayList<Order>(maxNumberOrdersPerTurn);
        this.maxNumberOrdersPerTurn = maxNumberOrdersPerTurn;
        if(initializeDefault){
            add(new DefaultDefendOrder());
        }
    }
    
    /**
     * 
     * @param order
     * @return
     */
    public boolean add(Order order){
        //TODO: Lots of special checking here..........................
        //Check if this is the first order the unit has received
        if(!ordersList.isEmpty()){
            Order firstOrder = ordersList.get(0);
            if(firstOrder instanceof DefaultDefendOrder){
                ordersList.remove(0);
                ordersList.add(0, order);
            } else {
                //Check if there is room in the orders list
                if(ordersList.size() >= maxNumberOrdersPerTurn){
                    return false;
                }
                ordersList.add(order);
            }
        } else {
            //This case should only be hit when the queue is created
            ordersList.add(order);
        }
        return true;
    }
    
    public Order get(int index){
        if(index > ordersList.size()){
            return null;
        } else {
            return ordersList.get(index);
        }
    }
    
    public boolean remove(Order order){
        if (order != null){
            if(order instanceof MovementOrder){
                //Protect data integrity by removing the order and any subsequent orders
                int index = ordersList.indexOf(order);
                List<Order> frontEndList = ordersList.subList(0, index);
                ordersList = frontEndList;
                boolean success = true;
                
                //If the list is now empty, insert the default defend order
                if(ordersList.isEmpty()){
                    add(new DefaultDefendOrder());
                }
                return success;
            } else {
                //Just a regular order deletion
                boolean success = ordersList.remove(order);
                //If the list is now empty, insert the default defend order
                if(ordersList.isEmpty()){
                    add(new DefaultDefendOrder());
                }
                return success;
            }
        }
        return false;
    }
    
    /**
     * Convenience method for narrowing down which units may have real orders
     * @return
     */
    public boolean hasOrders(){
        if (ordersList.isEmpty()){
            return false;
        } else {
            Order firstOrder = ordersList.get(0);
            if(firstOrder instanceof DefaultDefendOrder){
                return false;
            }
        }
        return true;
    }
    
    public void clear(){
        ordersList.clear();
        ordersList.add(new DefaultDefendOrder());
    }
    
    /**
     * Warning! Do not modify this list.  Use the methods provided with this class.
     * @return The list of orders for this unit
     */
    public List<Order> getOrdersList(){
        return ordersList;
    }

    /**
     * 
     * @return true if the queue has at least one combat order other than a
     * defaultDefendOrder
     */
    public boolean containsCombatOrder() {
        for(Order order: ordersList){
            if(order instanceof CombatOrder){
                if(order instanceof DefaultDefendOrder){
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @return the first order in the list
     */
    public Order getNextOrder() {
        if (!ordersList.isEmpty()){
            return ordersList.get(0);
        } else {
            return new DefaultDefendOrder();
        }
    }

    /**
     * 
     * @return true if the queue has at least one aerospace order
     */
    public boolean containsAerospaceOrder() {
        for(Order order: ordersList){
            if(order instanceof AerospaceOrder){
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @return true if the orders queue is at capacity
     */
    public boolean queueIsFull() {
        if (ordersList.size() >= maxNumberOrdersPerTurn){
            return true;
        }
        return false;
    }

    /**
     * 
     * @return true if the queue contains an Assault Order
     */
    public boolean containsAssaultOrder() {
        for(Order order: ordersList){
            if(order instanceof AssaultOrder){
                return true;
            }
        }
        return false;
    }

    public boolean containsAttackOrder() {
        for(Order order: ordersList){
            if(order instanceof AttackOrder){
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @return true if there fewer than 2 support orders
     */
    public boolean hasFewerThanTwoSupportOrder() {
        int supportOrderCount = 0;
        for(Order order: ordersList){
            if(order instanceof SupportOrder){
                supportOrderCount ++;
            }
        }
        
        if(supportOrderCount < 2){
            return true;
        }
        return false;
    }

    /**
     * 
     * @return
     */
    public boolean canAcceptCombatOrder() {
        for(Order order: ordersList){
            if(order instanceof AssaultOrder){
                return false;
            }
            if(order instanceof CombatOrder){
                if(order instanceof DefaultDefendOrder){
                    return true;
                }
                return false;
            }
            if(order instanceof SupportOrder){
                return false;
            }
            if(order instanceof AerospaceOrder){
                return false;
            }
        }
        return true;
    }

    /**
     * 
     * @return true if there are no repair orders in the list
     */
    public boolean canRepair() {
        for(Order order: ordersList){
            if(order instanceof RepairOrder){
                return false;
            }
        }
        return true;
    }

    /**
     * 
     * @return true if there are no re-supply orders in the list
     */
    public boolean canResupply() {
        for(Order order: ordersList){
            if(order instanceof ResupplyOrder){
                return false;
            }
        }
        return true;
    }

    /**
     * 
     * @return true if the queue contains a rest or re-supply order
     */
    public boolean containsRestOrResupply() {
        for(Order order: ordersList){
            if(order instanceof RestOrder  || order instanceof ResupplyOrder){
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @return true if the queue contains a repair order
     */
    public boolean containsRepair() {
        for(Order order: ordersList){
            if(order instanceof RepairOrder){
                return true;
            }
        }
        return false;
    }

    /**
     * Clears the first order in the list.  Do this when an order's work has
     * been executed.  If this is the last order in the unit's queue, prepend 
     * a DefaultdefendOrder to the Queue 
     */
    public void clearNextOrder() {
        if(!ordersList.isEmpty()){
            ordersList.remove(0);
            if(ordersList.isEmpty()){
                addFirstOrder(new DefaultDefendOrder());
            }
        }
    }

    /**
     * Inserts an order at the head of the list. 
     * This is the only way there could be more than 6 orders in the queue.
     * Such a situation should be temporary.
     * @param order
     * @return
     */
    public boolean addFirstOrder(Order order) {
        ordersList.add(0, order);
        return true; 
    }

    public boolean containsDigIn() {
        for(Order order: ordersList){
            if(order instanceof DigInOrder){
                return true;
            }
        }
        return false;
    }

    public boolean containsTrainingOrder() {
        for(Order order: ordersList){
            if(order instanceof TrainingOrder){
                return true;
            }
        }
        return false;
    }

    public void clearTrainingOrder() {
        Iterator<Order> iterator = ordersList.iterator();
        while(iterator.hasNext()){
            Order order  = iterator.next();
            if(order instanceof TrainingOrder){
                iterator.remove();
            }
        }
        if(ordersList.isEmpty()){
            addFirstOrder(new DefaultDefendOrder());
        }
    }
}
