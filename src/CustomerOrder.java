/**
 * Represents a customer order, extending Order.
 * @author Nathan Thompson - njt38
 */
public class CustomerOrder extends Order {
    private final int customerCode;

    public CustomerOrder(int orderNumber, int customerCode, Date dateOrdered, boolean fulfilled) {
        super(orderNumber, dateOrdered, fulfilled);
        this.customerCode = customerCode;

    }

    /**
     * Gets the customer code.
     * @return
     */
    public int getCustomerCode() {
        return customerCode;
    }

    /**
     * Returns a string representation of the customer order.
     * @return
     */
    @Override
    public String toString() {
        if (fulfilled) {
            return String.format("For customer %d order %d fulfilled ordered on %s for batches %s", customerCode, orderNumber, dateOrdered, allBatches);
        } else {
            return String.format("For customer %d order %d not fulfilled ordered on %s for batches %s", customerCode, orderNumber, dateOrdered, allBatches);
        }

    }
}
