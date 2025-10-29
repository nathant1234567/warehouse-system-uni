import java.util.*;

/**
 * The warehouse.
 * It is divided into a rectangular grid for storing batches.
 * Each batch consists of a part number and a quantity
 * Only one type of part is stored in any location of the grid.
 * @author Nathan Thompson - njt38
 */
public class Warehouse {
    // The maximum quantity in any location of the grid.
    private static final int MAX_AMOUNT = 500;
    // The number of rows and columns.
    private final int numRows, numCols;
    // The grid.
    // Empty locations must be stored as null values.
    private final Batch[][] grid;

    /**
     * Create an empty warehouse of the given number of rows and columns.
     * @param numRows The number of rows.
     * @param numCols The number of columns.
     */
    public Warehouse(int numRows, int numCols){
        this.numRows = numRows;
        this.numCols = numCols;
        grid = new Batch[this.numRows][this.numCols];
    }

    /**
     * Add a batch to the warehouse using the 2d array location
     * @param location
     * @param batch
     */
    public void addToWarehouse(Location location, Batch batch) {
        grid[location.row()][location.col()] = batch;
    }

    /**
     * Print all the occupied locations with their batches in the warehouse.
     */
    public void printOccupiedLocations() {
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                if (grid[row][col] != null) {
                    System.out.printf("%d,%d: %s%n", row, col, grid[row][col]);
                }
            }
        }
    }

    /**
     * Returns an array list of all the occupied locations in the warehouse.
     * @return
     */
    public ArrayList<Location> getPartLocations() {
        ArrayList<Location> locations = new ArrayList<>();
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                if (grid[row][col] != null) {
                    locations.add(new Location(row, col));
                }
            }
        }
        return locations;
    }

    /**
     * Returns the batch at the given location.
     * @param location
     * @return
     */
    public Batch getBatchAt(Location location) {
        return grid[location.row()][location.col()];
    }

    /**
     * Returns a list of all the part codes in the warehouse.
     * @return
     */
    public List<Integer> getAvailablePartCodes() {
        List<Integer> partCodes = new ArrayList<>();
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                if (grid[row][col] != null) {
                     partCodes.add(grid[row][col].getPartCode());
                }
            }
        }
        return partCodes;
    }

    /**
     * Returns the count of the given part code in the warehouse.
     * @param partCode
     * @return
     */
    public int getPartCount (int partCode) {
        int count = 0;
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                if (grid[row][col] != null && grid[row][col].getPartCode() == partCode) {
                    count += grid[row][col].getQuantity();
                }
            }
        }
        return count;
    }

    /**
     * Returns a list of all the locations that contain the given part code.
     * @param partCode
     * @return
     */
    public List<Location> findPart(int partCode) {
        List<Location> locations = new ArrayList<>();
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                if (grid[row][col] != null && grid[row][col].getPartCode() == partCode) {
                    locations.add(new Location(row, col));
                }
            }
        }
        return locations;
    }

    /**
     * Checks if the warehouse can fill the customer order.
     * @param customerOrder
     * @return
     */
    public boolean canBeFilled(CustomerOrder customerOrder) {
        AllBatches allBatches = customerOrder.getAllBatches();
        for (Batch aBatch : allBatches.getBatches()) {
            if (getPartCount(aBatch.getPartCode()) < aBatch.getQuantity()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a purchase order to restock the warehouse.
     * @param allParts
     * @return
     */
    public PurchaseOrder createRestockOrder(AllParts allParts) {
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        for (Part part : allParts.getParts()) {
            if (getPartCount(part.partCode()) == 0) {
                Batch batch = new Batch(part.partCode(), 50);
                purchaseOrder.addBatch(batch);
            }
        }
        if (purchaseOrder.getAllBatches().getBatches().isEmpty()) {
            return null;
        }
        return purchaseOrder;
    }

    /**
     * Creates a purchase order for the parts that are not in stock for the customer order, with the quantity that it needs.
     * @param customerOrder
     * @return
     */
    public PurchaseOrder createPurchaseOrder(CustomerOrder customerOrder) {
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        for (Batch batch : customerOrder.getAllBatches().getBatches()) {
            int quantityInWarehouse = getPartCount(batch.getPartCode());
            if (quantityInWarehouse < batch.getQuantity()) {
                int quantityNeeded = batch.getQuantity() - quantityInWarehouse;
                purchaseOrder.addBatch(new Batch(batch.getPartCode(), quantityNeeded));
            }
        }
        return purchaseOrder;
    }

    /**
     * Creates a pick list for the customer order. It loops through the batches that are required then loops through the locations that contain the part code.
     * If the quantity needed is greater than the quantity available, it picks the quantity available and reduces the quantity needed. Adds it to the pick list.
     * @param order
     * @return
     */
    public List<PickListItem> createAPickList(CustomerOrder order) {
        List<PickListItem> pickListItems = new ArrayList<>();

        for (Batch orderBatch : order.getAllBatches().getBatches()) {
            int partCode = orderBatch.getPartCode();
            int quantityNeeded = orderBatch.getQuantity();
            List<Location> partLocations = findPart(partCode);

            for (Location location : partLocations) {
                if (quantityNeeded <= 0) {
                    break;
                }
                Batch warehouseBatch = getBatchAt(location);
                int quantityAvailable = warehouseBatch.getQuantity();

                if (quantityAvailable > 0) {
                    int quantityToPick = Math.min(quantityNeeded, quantityAvailable);
                    pickListItems.add(new PickListItem(location, new Batch(orderBatch.getPartCode(), quantityToPick)));
                    quantityNeeded -= quantityToPick;
                    warehouseBatch.reduceQuantity(quantityToPick);

                    if (warehouseBatch.getQuantity() == 0) {
                        grid[location.row()][location.col()] = null;
                    }
                }
            }
        }
        return pickListItems;
    }

    /**
     * Stores the delivery in the warehouse. It loops through the batches in the delivery and stores them in the existing locations, or finds new locations if needed.
     * @param delivery
     * @return
     */
    public List<Location> storeDelivery(Delivery delivery) {
        List<Location> updatedLocations = new ArrayList<>();

        for (Batch batch : delivery.getAllBatches().getBatches()) {
            int partCode = batch.getPartCode();
            int quantityToStore = batch.getQuantity();

            // Find already existing locations with the part
            List<Location> existingLocations = findPart(partCode);

            // Store the delivery in the existing locations
            for (Location location : existingLocations) {
                if (quantityToStore <= 0) {
                    break;
                }
                Batch existingBatch = getBatchAt(location);
                int availableSpace = MAX_AMOUNT - existingBatch.getQuantity();
                if (availableSpace > 0) {
                    int quantityToAdd = Math.min(quantityToStore, availableSpace);
                    existingBatch.increaseQuantity(quantityToAdd);
                    quantityToStore -= quantityToAdd;
                    updatedLocations.add(location);
                }
            }

            // Store the delivery in new locations, if needed
            for (int row = 0; row < numRows && quantityToStore > 0; row++) {
                for (int col = 0; col < numCols && quantityToStore > 0; col++) {
                    if (grid[row][col] == null) {
                        int quantityToAdd = Math.min(quantityToStore, MAX_AMOUNT);
                        grid[row][col] = new Batch(partCode, quantityToAdd);
                        quantityToStore -= quantityToAdd;
                        updatedLocations.add(new Location(row, col));
                    }
                }
            }
        }
        return updatedLocations;
    }
}
