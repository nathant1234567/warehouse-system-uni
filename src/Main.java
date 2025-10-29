import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.*;

/**
 * The main method connects to the database and retrieves
 * objects that store all the data from the database.
 */
public class Main {
    // A random number generator.
    private final static Random rand = new Random();
    // Access to the database.
    private static DatabaseHandler databaseHandler = null;

    /**
     * Read tables from the database and support some operations on the data.
     *
     * @param args Not used.
     * @throws SQLException If there are problems accessing the database.
     */
    public static void main(String[] args) throws SQLException {
        setup();

        AllParts allParts = databaseHandler.readParts();
        Warehouse theWarehouse = databaseHandler.readWarehouse();
//        allParts.printParts();
//        theWarehouse.printOccupiedLocations();

        AllCustomers allCustomers = databaseHandler.readCustomers();
        System.out.println(" ===== All Customers");
//        allCustomers.printCustomers();

        AllCustomerOrders allCustomerOrders = databaseHandler.readCustomerOrders();
//        allCustomerOrders.printOrders();

        AllPurchaseOrders allPurchaseOrders = databaseHandler.readPurchaseOrders();
//        allPurchaseOrders.printOrders();

        part1(theWarehouse);
        part2(databaseHandler);
        part3(databaseHandler, allParts, allCustomerOrders);
        part4(theWarehouse, allCustomerOrders);
        part5(databaseHandler, theWarehouse, allParts, allPurchaseOrders);
        part6(theWarehouse, allCustomerOrders, allPurchaseOrders);
        part7(theWarehouse, allCustomerOrders);

        AllDeliveries allDeliveries = databaseHandler.readDeliveries();
        part8(theWarehouse, allDeliveries);
    }

    /**
     * Test completion of part 1 of the assignment.
     */
    public static void part1(Warehouse theWarehouse) {
        System.out.println("Part 1 tests");
        // TODO: Complete this part.
        // Print locations that contain at least one part.
        List<Location> partLocations = theWarehouse.getPartLocations();
        System.out.println("Part1a: Results of running getPartLocations " + partLocations);

        // Get details of a random location
        System.out.println("Part1b: Results of running getBatchAt: ");
        if (!partLocations.isEmpty()) {
            Location theLocation = partLocations.get(rand.nextInt(partLocations.size()));
            Batch theBatch = theWarehouse.getBatchAt(theLocation);
            System.out.printf("At location %s is %s%n", theLocation, theBatch);
        } else {
            System.out.println("No parts available.");
        }

        // Print the count of a random part that is in the warehouse
        List<Integer> availableParts = theWarehouse.getAvailablePartCodes();
        System.out.println("Part1c: Results of running getAvailableParts: ");
        System.out.println(availableParts);

        // Select a random part code
        int index = rand.nextInt(availableParts.size());
        int partCode = availableParts.get(index);
        int count = theWarehouse.getPartCount(partCode);
        System.out.printf("Part1c: getPartCount: There are %d boxes of part %d%n", count, partCode);

        // Print the locations of a random part.
        index = rand.nextInt(availableParts.size());
        partCode = availableParts.get(index);
        List<Location> locationsOfPart = theWarehouse.findPart(partCode);
        System.out.printf("Part1d: findPart: Part %s is at location(s): %s%n", partCode, locationsOfPart);


        System.out.println("=== End of part 1");
        System.out.println();
    }

    /**
     * Get the contents of the partTypes table and print the pairs.
     * @throws SQLException If there is an error accessing the database.
     */
    private static void part2(DatabaseHandler databaseHandler)  throws SQLException {
        System.out.println("Part 2 tests");
        // TODO: Complete this part.
        Map<String, String> partTypes = databaseHandler.readPartTypes();

        for (Map.Entry<String, String> entry : partTypes.entrySet()) {
            System.out.printf("%s: %s%n", entry.getKey(), entry.getValue().trim());
        }

        System.out.println("=== End of part 2");
        System.out.println();
    }

    /**
     * Calculate the cost of each customer order.
     */
    private static void part3(DatabaseHandler databaseHandler, AllParts allParts, AllCustomerOrders allCustomerOrders) throws SQLException  {
        System.out.println("Part 3 tests");
        // TODO: Complete this part.
        AllOrderItems allOrderItems = databaseHandler.readOrderItems("customerOrderItems");

        for (OrderItem order : allOrderItems.getOrderItems()) { // problem
            Batch aBatch = new Batch(order.partCode(), order.quantity());
            CustomerOrder customerOrder = allCustomerOrders.getOrder(order.orderNumber());
            customerOrder.addBatch(aBatch);
        }

        //allCustomerOrders.printOrders();
        for (CustomerOrder customerOrder : allCustomerOrders.getOrders()) {
            double cost = allParts.getCost(customerOrder);
            System.out.printf("Customer order %4d costs £ %.2f%n", customerOrder.getOrderNumber(), cost); // should it be formatted in same way as doc?
        }

        System.out.println("=== End of part 3");
        System.out.println();
    }

    /**
     * Check which unfilled orders can be filled.
     */
    public static void part4(Warehouse theWarehouse, AllCustomerOrders allCustomerOrders) {
        System.out.println("Part 4 tests");
        // TODO: Complete this part.

        for (CustomerOrder customerOrder : allCustomerOrders.getOrders()) {
            if (theWarehouse.canBeFilled(customerOrder)) {
                System.out.printf("Customer order %d: is in stock.%n", customerOrder.getOrderNumber());
            } else {
                System.out.printf("Customer order %d: needs a purchase order.%n", customerOrder.getOrderNumber());
            }
        }

        System.out.println("=== End of part 4");
        System.out.println();
    }

    /**
     * Create a purchase order for any parts not currently in stock.
     *
     * @param databaseHandler   The database handler.
     * @param theWarehouse
     * @param allParts
     * @param allPurchaseOrders
     */
    private static void part5(DatabaseHandler databaseHandler, Warehouse theWarehouse, AllParts allParts, AllPurchaseOrders allPurchaseOrders)
            throws SQLException {
        System.out.println("Part 5 tests");
        // TODO: Complete this part.
        AllOrderItems allOrderItems = databaseHandler.readOrderItems("purchaseOrderItems");

        for (OrderItem order : allOrderItems.getOrderItems()) {
            Batch aBatch = new Batch(order.partCode(), order.quantity());
            PurchaseOrder purchaseOrder = allPurchaseOrders.getOrder(order.orderNumber());
            purchaseOrder.addBatch(aBatch);
        }
//        allPurchaseOrders.printOrders();

        PurchaseOrder restockOrder = theWarehouse.createRestockOrder(allParts);
        if (restockOrder != null) {
            System.out.println(restockOrder);
        }

        System.out.println("=== End of part 5");
        System.out.println();
    }

    /**
     * Generate a purchase order for an unfilled order that cannot be filled.
     * Only order parts from the customer order for which there are not
     * enough in the warehouse.
     */
    public static void part6(Warehouse theWarehouse, AllCustomerOrders allCustomerOrders, AllPurchaseOrders allPurchaseOrders) {
        System.out.println("Part 6 tests");
        // TODO: Complete this part.

        List<CustomerOrder> unfulfilledOrders = new ArrayList<>();
        for (CustomerOrder customerOrder : allCustomerOrders.getOrders()) {
            if (!theWarehouse.canBeFilled(customerOrder)) {
                unfulfilledOrders.add(customerOrder);
            }
        }

        if (!unfulfilledOrders.isEmpty()) {
            CustomerOrder randomUnfulfilledOrder = unfulfilledOrders.get(rand.nextInt(unfulfilledOrders.size()));

            PurchaseOrder purchaseOrder = theWarehouse.createPurchaseOrder(randomUnfulfilledOrder);

            System.out.printf("Customer order %d triggered Purchase order: %d ordered on %s for items: %s%n", randomUnfulfilledOrder.getOrderNumber(), purchaseOrder.getOrderNumber(), purchaseOrder.getDateOrdered(), purchaseOrder.getAllBatches());
        }



        System.out.println("=== End of part 6");
        System.out.println();
    }

    /**
     * Generate a pick list for at least one customer order that
     * can be fulfilled.
     */
    public static void part7(Warehouse theWarehouse, AllCustomerOrders allCustomerOrders) {
        System.out.println("Part 7 tests");
        // TODO: Complete this part.

        List<CustomerOrder> unfulfilledOrders = new ArrayList<>();
        for (CustomerOrder customerOrder : allCustomerOrders.getOrders()) {
            if (!theWarehouse.canBeFilled(customerOrder)) {
                unfulfilledOrders.add(customerOrder);
            }
        }

        if (!unfulfilledOrders.isEmpty()) {
            CustomerOrder randomUnfulfilledOrder = unfulfilledOrders.get(rand.nextInt(unfulfilledOrders.size()));

            List<PickListItem> pickList = theWarehouse.createAPickList(randomUnfulfilledOrder);
            randomUnfulfilledOrder.setFulfilled();

            System.out.println("Results of running createAPickList:");
            System.out.printf("Customer order %d fulfilled for customer %d%nPick list is %s%n", randomUnfulfilledOrder.getOrderNumber(), randomUnfulfilledOrder.getCustomerCode(), pickList);

            // TODO: Update the database.
            //databaseHandler.updateWarehouseFromCustomerOrder(theWarehouse, randomUnfulfilledOrder.getOrderNumber());
        }
        System.out.println("=== End of part 7");
        System.out.println();
    }

    /**
     * Store the contents of a random delivery in the warehouse.
     */
    public static void part8(Warehouse theWarehouse, AllDeliveries allDeliveries)
            throws SQLException {
        System.out.println("Part 8 tests");
        // TODO: Complete this part.

        AllOrderItems allOrderItems = databaseHandler.readOrderItems("deliveryItems");

        for (OrderItem order: allOrderItems.getOrderItems()) {
            Batch aBatch = new Batch(order.partCode(), order.quantity());
            Delivery aDelivery = allDeliveries.getDelivery(order.orderNumber());
            aDelivery.addBatch(aBatch);
        }

        List<Delivery> unfulfilledDeliveries = allDeliveries.getOutstandingDeliveryNumbers();

        if (!unfulfilledDeliveries.isEmpty()) {
            Delivery randomDelivery = unfulfilledDeliveries.get(rand.nextInt(unfulfilledDeliveries.size()));

            System.out.println(randomDelivery);
            List<Location> storedLocations = theWarehouse.storeDelivery(randomDelivery);
            randomDelivery.setFulfilled();

            for (Batch batch : randomDelivery.getAllBatches().getBatches()) {
                for (Location location : storedLocations) {
                    Batch warehouseBatch = theWarehouse.getBatchAt(location);
                    if (warehouseBatch.getPartCode() == batch.getPartCode()) {
                        System.out.printf("Part %d was stored at: (%d,%d) which now contains %d boxes.%n", batch.getPartCode(), location.row(), location.col(), warehouseBatch.getQuantity());

                        // TODO: Update the database.
                        //databaseHandler.updateWarehouseFromDelivery(theWarehouse, randomDelivery.getDeliveryNumber());
                    }
                }
            }
        }
        System.out.println("=== End of part 8");
        System.out.println();
    }

    /**
     * Set up the connection to the database.
     * If the connection fails, the program will exit.
     */
    private static void setup() {
        try {
            databaseHandler = new DatabaseHandler();
        } catch (SQLTimeoutException e) {
            System.err.println("Failed to connect to the database.");
            System.exit(1);
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Failed to access the database: " + e);
            System.exit(1);
        }
    }

}
