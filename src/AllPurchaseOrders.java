import java.util.*;

/**
 * A collection of purchase orders in a tree map.
 * @author Nathan Thompson - njt38
 */
public class AllPurchaseOrders extends AllEntities<PurchaseOrder> {

    /**
     * Adds a purchase order to the tree map.
     * @param order
     */
    public void addOrder(PurchaseOrder order) {
        orders.put(order.getOrderNumber(), order);
    }

    /**
     * Gets a purchase order from the tree map by order number.
     * @param orderNumber
     * @return
     */
    public PurchaseOrder getOrder(int orderNumber) {
        return orders.get(orderNumber);
    }

    /**
     * Prints all the purchase orders in the tree map.
     */
    public void printOrders() {
        for (PurchaseOrder order : orders.values()) {
            System.out.println(order);
        }
    }
}
