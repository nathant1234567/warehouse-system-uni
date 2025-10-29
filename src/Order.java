/**
 * Order superclass for the order system. Used to represent a generic order for purchase and customer order.
 * @author Nathan Thompson - njt38
 */
public class Order implements Comparable<Order>{

    protected final int orderNumber;
    protected final Date dateOrdered;
    protected boolean fulfilled;
    protected final AllBatches allBatches;

    public Order(int orderNumber, Date dateOrdered, boolean fulfilled) {
        this.orderNumber = orderNumber;
        this.dateOrdered = dateOrdered;
        this.fulfilled = fulfilled;
        this.allBatches = new AllBatches();
    }

    /**
     * Gets the order number.
     * @return
     */
    public int getOrderNumber() {
        return orderNumber;
    }

    /**
     * Gets the date ordered.
     * @return
     */
    public Date getDateOrdered() {
        return dateOrdered;
    }

    /**
     * Gets whether the order is fulfilled.
     * @return
     */
    public boolean isFulfilled() {
        return fulfilled;
    }

    /**
     * Sets the order to fulfilled.
     */
    public void setFulfilled() {
        this.fulfilled = true;
    }

    /**
     * Gets all the batches in the order.
     * @return
     */
    public AllBatches getAllBatches() {
        return allBatches;

    }

    /**
     * Adds a batch to the order.
     * @param batch the batch to be added.
     */
    public void addBatch(Batch batch) {
        allBatches.addBatch(batch);
    }

    /**
     * Hashcode for the order.
     * @return
     */
    @Override
    public int hashCode() {
        return orderNumber;
    }

    /**
     * @param other the object to be compared.
     * @return
     */
    @Override
    public int compareTo(Order other) {
        return orderNumber - other.orderNumber;
    }

    /**
     * To string method for the order.
     * @return
     */
    @Override
    public String toString() {
        return String.format("Order %d %s ordered on %s for batches %s", orderNumber, fulfilled ? "fulfilled" : "not fulfilled", dateOrdered, allBatches);
    }

    /**
     * Compares the order to another order.
     * @param other the object to be compared.
     * @return
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;

        if (other == null || getClass() != other.getClass()) return false;
        Order order = (Order) other;
        return orderNumber == order.orderNumber;
    }
}
