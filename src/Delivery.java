/**
 * Delivery class to represent a delivery that can be fulfilled.
 * @author Nathan Thompson - njt38
 */
public class Delivery extends Order {

    /**
     * Initiates a delivery object.
     * @param orderNumber
     * @param dateDelivered
     * @param fulfilled
     */
    public Delivery(int orderNumber, Date dateDelivered, boolean fulfilled) {
        super(orderNumber, dateDelivered, fulfilled);
    }

    /**
     * Returns a string representation of the delivery.
     * @return
     */
    @Override
    public String toString() {
        if (fulfilled) {
            return String.format("Delivery: %d has been unloaded on %s for items %s", orderNumber, dateOrdered, allBatches);
        } else {
            return String.format("Delivery %d to be unloaded on %s for items %s", orderNumber, dateOrdered, allBatches);
        }

    }
}
