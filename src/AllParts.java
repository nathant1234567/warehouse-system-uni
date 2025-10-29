import java.util.Collection;
import java.util.TreeMap;

/**
 * A collection class to store all the parts in a tree map, and to modify that tree map.
 * @author Nathan Thompson - njt38
 */
public class AllParts {
    private TreeMap<Integer, Part> partsMap = new TreeMap<>();

    /**
     * Get all the parts in the tree map.
     * @return
     */
    public Collection<Part> getParts() {
        return partsMap.values();
    }

    /**
     * Adds a part to the tree map.
     * @param part
     */
    public void addPart(Part part) {
        partsMap.put(part.partCode(), part);
    }

    /**
     * Removes a part from the tree map.
     */
    public void printParts() {
        for (Part part : partsMap.values()) {
            System.out.println(part);
        }
    }

    /**
     * Gets the cost of a customer order by looping through the batches in the order and multiplying the quantity by the price of the part.
     * @param customerOrder
     * @return
     */
    public double getCost(CustomerOrder customerOrder) {
        AllBatches allBatches = customerOrder.getAllBatches();
        double cost = 0;

        for (Batch batch : allBatches.getBatches()) {
            int quantity = batch.getQuantity();
            int partCode = batch.getPartCode();
            Part part = partsMap.get(partCode);
            cost += part.price() * quantity;
        }
        return cost;
    }
}
