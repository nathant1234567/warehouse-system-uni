import java.util.*;

/**
 * A collection of deliveries stored in a tree map.
 * @author Nathan Thompson - njt38
 */
public class AllCustomers {
    private final Map<Integer, Customer> customers = new TreeMap<>();

    /**
     * Add a customer to the customer tree map.
     * @return
     */
    public void addCustomer(Customer customer) {
        customers.put(customer.customerCode(), customer);
    }

    /**
     * Print out all the customers in the tree map.
     */
    public void printCustomers() {
        for (Customer customer : customers.values()) {
            System.out.println(customer);
        }
    }
}
