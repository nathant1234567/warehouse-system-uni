import java.util.*;

/**
 * A collection of customer orders in a tree map.
 * @author Nathan Thompson - njt38
 */
public class AllCustomerOrders {
    private final Map<Integer, CustomerOrder> customerOrders = new TreeMap<>();

    /**
     * Gets all the order numbers in the tree map as a list.
     * @return
     */
    public ArrayList<Integer> getOrderNumbers() {
        return new ArrayList<>(customerOrders.keySet());
    }

    /**
     * Returns all the orders in the tree map as a collection.
     * @return
     */
    public Collection<CustomerOrder> getOrders() {
        return customerOrders.values();
    }

    /**
     * Adds an order to the tree map.
     * @param order
     */
    public void addOrder(CustomerOrder order) {
        customerOrders.put(order.getOrderNumber(), order);
    }

    /**
     * Gets an order from the tree map by order number.
     * @param orderNumber
     * @return
     */
    public CustomerOrder getOrder(int orderNumber) {
        return customerOrders.get(orderNumber);
    }

    /**
     * Prints all the orders in the tree map.
     */
    public void printOrders() {
        for (CustomerOrder order : customerOrders.values()) {
            System.out.println(order);
        }
    }
}
