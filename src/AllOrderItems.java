import java.util.*;

/**
 * A collection of order items.
 * @author Nathan Thompson - njt38
 */
public class AllOrderItems {
    private final List<OrderItem> orderItems = new ArrayList<>();

    /**
     * Add an order item to the list.
     * @param orderItem
     */
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
    }

    /**
     * Returns all the order items in the list.
     * @return
     */
    public Collection<OrderItem> getOrderItems() {
        return orderItems;
    }
}
