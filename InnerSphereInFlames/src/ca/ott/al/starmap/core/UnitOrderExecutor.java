package ca.ott.al.starmap.core;

import ca.ott.al.starmap.core.order.Order;
import ca.ott.al.starmap.core.order.OrderQueue;

public interface UnitOrderExecutor {


    public boolean hasOrders();
    
    public OrderQueue getOrders();
    
    public boolean addOrder(Order order);
    
    public boolean cancelOrder(Order order);
    
    public void clearOrders();
    
    public Order getNextOrder();
    
    public void clearNextOrder();
    
    public boolean addFirstOrder(Order order);
    
}
