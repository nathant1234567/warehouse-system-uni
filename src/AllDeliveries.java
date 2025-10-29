import java.util.*;

/**
 * All deliveries that .
 * @author Nathan Thompson - njt38
 */
public class AllDeliveries extends AllEntities<Delivery> {
    /**
     * Adds a delivery to the tree map.
     * @param delivery
     */
    public void addDelivery(Delivery delivery) {
        orders.put(delivery.getOrderNumber(), delivery);
    }

    /**
     * Returns a delivery with the given delivery number.
     * @param deliveryNumber
     * @return
     */
    public Delivery getDelivery(int deliveryNumber) {
        return orders.get(deliveryNumber);
    }

    /**
     * Returns all deliveries.
     * @return
     */
    public Collection<Delivery> getDeliveries() {
        return orders.values();
    }

    /**
     * Prints all the deliveries that are stored in the tree map.
     */
    public void printDeliveries() {
        for (Delivery delivery : orders.values()) {
            System.out.println(delivery);
        }
    }

    public List<Delivery> getOutstandingDeliveryNumbers() {
        List<Delivery> outstandingDeliveryNumbers = new ArrayList<>();
        for (Delivery delivery : orders.values()) {
            if (!delivery.isFulfilled()) {
                outstandingDeliveryNumbers.add(delivery);
            }
        }
        return outstandingDeliveryNumbers;
    }
}
