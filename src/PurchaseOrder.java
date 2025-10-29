/**
 * Represents a purchase order, extending Order.
 * @author Nathan Thompson - njt38

 */
public class PurchaseOrder extends Order {
    private static int nextOrderNumber = 0;

    public PurchaseOrder(int purchaseOrderNumber, Date dateOrdered, boolean fulfilled) {
        super(purchaseOrderNumber, dateOrdered, fulfilled);

        if (purchaseOrderNumber <= 1) {
            nextOrderNumber = purchaseOrderNumber + 1;
        }
    }

    public PurchaseOrder() {
        this(nextOrderNumber, Date.getNow(), false);
    }


    /**
     * Gets the static variable nextOrderNumber.
     * @return
     */
    public static int getNextOrderNumber() {
        return nextOrderNumber;
    }

}