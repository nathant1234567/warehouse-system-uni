import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WarehouseTest
{
    // Warehouse details.
    private Warehouse theWarehouse;
    // Work with a smaller warehouse as there is no particular need for
    // a large one.
    private final int numRows = 5, numColumns = 6;
    // Part details.
    // A part code to find.
    private int partCodeToFind;
    // Locations for partCodeToFind.
    private List<Location> partLocationsToFind;
    // Quantities for partCodeToFind.
    private List<Integer> partQuantitiesToFind;
    // A location with an Item we won't be searching for.
    private final Location locationToNotFind = new Location(0, 0);
    // The maximum amount in any location.
    private static final int MAX_AMOUNT = 500;
    // The restocking quantity
    private static final int RESTOCK_QUANTITY = 50;

    // Item details of those parts to be found.
    private List<Batch> itemsToFind;
    // Customer details.
    private final int customerCode = 1;
    // Customer order date.
    private final Date orderDate = Date.getNow();
    // Support generation of random part numbers, etc.
    private static final Random rand = new Random();

    /**
     * Set up the fixture before each test.
     */
    @org.junit.jupiter.api.BeforeEach
    void setUp()
    {
        theWarehouse = new Warehouse(numRows, numColumns);

        partCodeToFind = 1 + rand.nextInt(100);
        int partCodeToNotFind = partCodeToFind * 7;

        // Where partCodeToFind will be located.
        partLocationsToFind = List.of(
                new Location(1, 1),
                new Location(2, 4),
                new Location(3, 5),
                new Location(numRows - 1, numColumns - 1));
        // Quantities of partCodeToFind in various locations.
        partQuantitiesToFind = List.of(1, 2, 3, 4);

        // Create the Items that will be stored in the warehouse.
        itemsToFind = new ArrayList<>();
        for(int quantity : partQuantitiesToFind) {
            itemsToFind.add(new Batch(partCodeToFind, quantity));
        }

        // Put the items in the warehouse at known locations.
        for(int index = 0; index < itemsToFind.size(); index++) {
            theWarehouse.addToWarehouse(partLocationsToFind.get(index), itemsToFind.get(index));
        }

        // An item for the part to not find.
        Batch batchToNotFind = new Batch(partCodeToNotFind, MAX_AMOUNT);
        theWarehouse.addToWarehouse(locationToNotFind, batchToNotFind);

        // Make sure that we have the same number of quantities as locations.
        assert partLocationsToFind.size() == partQuantitiesToFind.size();

        // Make sure we aren't storing different parts in the same location.
        assert ! partLocationsToFind.contains(locationToNotFind);
    }

    /**
     * Test finding part locations in an empty warehouse.
     */
    @org.junit.jupiter.api.Test
    @org.junit.jupiter.api.Order(1)
    void testGetPartLocationsNone()
    {
        theWarehouse = new Warehouse(numRows, numColumns);
        List<Location> actual = theWarehouse.getPartLocations();
        assertEquals(0, actual.size());
    }

    /**
     * Test finding part locations when there is only one location.
     */
    @org.junit.jupiter.api.Test
    @org.junit.jupiter.api.Order(2)
    void testGetPartLocationsOne()
    {
        Location aLocation = partLocationsToFind.get(0);
        Batch aBatch = itemsToFind.get(0);
        theWarehouse = new Warehouse(numRows, numColumns);
        theWarehouse.addToWarehouse(aLocation, aBatch);

        List<Location> actual = theWarehouse.getPartLocations();
        assertEquals(1, actual.size());
        assertEquals(aLocation, actual.get(0));
    }

    /**
     * Test finding part locations when there are multiple.
     */
    @org.junit.jupiter.api.Test
    @org.junit.jupiter.api.Order(3)
    void testGetPartLocationsMultiple()
    {
        List<Location> expected = new ArrayList<>(partLocationsToFind);
        expected.add(locationToNotFind);
        List<Location> actual = theWarehouse.getPartLocations();
        assertEquals(expected.size(), actual.size());
        for(Location aLocation : expected) {
            assertTrue(expected.contains(aLocation));
        }
    }

    /**
     * Test getItemAt where there are none.
     */
    @org.junit.jupiter.api.Test
    @org.junit.jupiter.api.Order(4)
    void testGetBatchAtNone()
    {
        theWarehouse = new Warehouse(numRows, numColumns);
        for (Location aLocation : partLocationsToFind) {
            Batch actual = theWarehouse.getBatchAt(aLocation);
            assertNull(actual);
        }
    }

    /**
     * Test getItemAt at all of its locations.
     */
    @org.junit.jupiter.api.Test
    @org.junit.jupiter.api.Order(5)
    void testGetBatchAt()
    {
        for(int index = 0; index < partLocationsToFind.size(); index++) {
            Location aLocation = partLocationsToFind.get(index);
            Batch expected = itemsToFind.get(index);
            Batch actual = theWarehouse.getBatchAt(aLocation);
            assertEquals(expected.getPartCode(), actual.getPartCode());
            assertEquals(expected.getQuantity(), actual.getQuantity());
        }
    }

    /**
     * Test getPartCount when there are no parts.
     */
    @org.junit.jupiter.api.Test
    @org.junit.jupiter.api.Order(6)
    void testGetPartCountNone()
    {
        theWarehouse = new Warehouse(numRows, numColumns);
        int expectedCount = 0;
        int actualCount = theWarehouse.getPartCount(partCodeToFind);
        assertEquals(expectedCount, actualCount);
    }

    /**
     * Test getPartCount when there is only one item in the warehouse.
     */
    @org.junit.jupiter.api.Test
    @org.junit.jupiter.api.Order(7)
    void testGetPartCountOneLocation()
    {
        int expectedCount = 3;
        int anotherPartCode = partCodeToFind + 1;
        Location aLocation = new Location(4, 4);
        Batch aBatch = new Batch(anotherPartCode, expectedCount);
        theWarehouse = new Warehouse(numRows, numColumns);
        theWarehouse.addToWarehouse(aLocation, aBatch);
        int actualCount = theWarehouse.getPartCount(anotherPartCode);
        assertEquals(expectedCount, actualCount);
    }

    /**
     * Test getPartCount for multiple locations.
     */
    @org.junit.jupiter.api.Test
    @org.junit.jupiter.api.Order(8)
    void testGetPartCountMultipleLocations()
    {
        int expectedCount = 0;
        for(int quantity : partQuantitiesToFind) {
            expectedCount += quantity;
        }
        int actualCount = theWarehouse.getPartCount(partCodeToFind);
        assertEquals(expectedCount, actualCount);
    }

    /**
     * Test findPart when it is in one location.
     */
    @org.junit.jupiter.api.Test
    @org.junit.jupiter.api.Order(9)
    void testFindPartOne()
    {
        Location aLocation = new Location(4, 4);
        Batch aBatch = new Batch(partCodeToFind, 1);
        List<Location> expected = new ArrayList<>();
        expected.add(aLocation);

        theWarehouse = new Warehouse(numRows, numColumns);
        theWarehouse.addToWarehouse(aLocation, aBatch);

        List<Location> actual = theWarehouse.findPart(partCodeToFind);
        assertEquals(expected.size(), actual.size());
        assertEquals(aLocation, actual.get(0));
    }

    /**
     * Test findPart when the part is in multiple locations.
     */
    @org.junit.jupiter.api.Test
    @org.junit.jupiter.api.Order(10)
    void testFindPartMultiple()
    {
        List<Location> expected = new ArrayList<>(partLocationsToFind);
        List<Location> actual = theWarehouse.findPart(partCodeToFind);
        assertEquals(expected.size(), actual.size());
        for(Location aLocation : expected) {
            assertTrue(actual.contains(aLocation));
        }
    }

    /**
     * Test canBeFilled when the warehouse is empty.
     */
    @Test
    @org.junit.jupiter.api.Order(11)
    void testCanBeFilledEmptyWarehouse()
    {
        theWarehouse = new Warehouse(numRows, numColumns);
        // The client's inventory of items.
        CustomerOrder order = new CustomerOrder(1, customerCode, orderDate, false);
        Batch theBatch = new Batch(partCodeToFind, 1);
        order.getAllBatches().addBatch(theBatch);
        boolean actual = theWarehouse.canBeFilled(order);
        assertFalse(actual);
    }

    /**
     * Test canBeFilled when the part does not exist.
     */
    @Test
    @org.junit.jupiter.api.Order(12)
    void testCanBeFilledNoPart()
    {
        // The client's inventory of items.
        CustomerOrder order = new CustomerOrder(1, customerCode, orderDate, false);
        Batch theBatch = new Batch(partCodeToFind + 1, 1);
        order.getAllBatches().addBatch(theBatch);
        boolean actual = theWarehouse.canBeFilled(order);
        assertFalse(actual);
    }

    /**
     * Test canBeFilled for an order of a single part.
     */
    @Test
    @org.junit.jupiter.api.Order(13)
    void testCanBeFilledOnePart()
    {
        // The client's inventory of items.
        CustomerOrder order = new CustomerOrder(1, customerCode, orderDate, false);
        Batch theBatch = new Batch(partCodeToFind, 1);
        order.getAllBatches().addBatch(theBatch);
        boolean actual = theWarehouse.canBeFilled(order);
        assertTrue(actual, "canBeFilled should return true if there are parts");
    }

    /**
     * Test can be filled when the parts are in multiple locations.
     */
    @Test
    @org.junit.jupiter.api.Order(14)
    void testCanBeFilledMultipleLocations()
    {
        CustomerOrder order = new CustomerOrder(1, customerCode, orderDate, false);
        int quantity = partQuantitiesToFind.get(partQuantitiesToFind.size() - 1) + 1;
        Batch theBatch = new Batch(partCodeToFind, quantity);
        order.getAllBatches().addBatch(theBatch);
        boolean actual = theWarehouse.canBeFilled(order);
        assertTrue(actual, "canBeFilled should return true if there are parts");
    }

    /**
     * Test canBeFilled when all the available items are required.
     */
    @Test
    @org.junit.jupiter.api.Order(15)
    void testCanBeFilledAllLocations()
    {
        // The client's inventory of items.
        CustomerOrder order = new CustomerOrder(1, customerCode, orderDate, false);
        int maxQuantity = 0;
        for(int quantity : partQuantitiesToFind) {
            maxQuantity += quantity;
        }
        Batch theBatch = new Batch(partCodeToFind, maxQuantity);
        order.getAllBatches().addBatch(theBatch);
        boolean actual = theWarehouse.canBeFilled(order);
        assertTrue(actual, "canBeFilled should return true if there are parts");
    }

    /**
     * Test can be filled when there are insufficient parts.
     */
    @Test
    @org.junit.jupiter.api.Order(16)
    void testCanBeFilledTooMany()
    {
        // The client's inventory of items.
        CustomerOrder order = new CustomerOrder(1, customerCode, orderDate, false);
        int maxQuantity = 0;
        for(int quantity : partQuantitiesToFind) {
            maxQuantity += quantity;
        }
        Batch theBatch = new Batch(partCodeToFind, maxQuantity + 1);
        order.getAllBatches().addBatch(theBatch);
        boolean actual = theWarehouse.canBeFilled(order);
        assertFalse(actual, "canBeFilled should return false if there aren't enough parts");
    }

    /**
     * Test restocking with an empty inventory.
     */
    @Test
    @org.junit.jupiter.api.Order(17)
    void testRestockEmptyInventory()
    {
        AllParts inventory = new AllParts();
        PurchaseOrder order = theWarehouse.createRestockOrder(inventory);
        assertNull(order, "No order should be created when there are no parts in the inventory.");
    }

    /**
     * Test restocking with nothing needed restocking.
     */
    @Test
    @org.junit.jupiter.api.Order(18)
    void testRestockNothingNeeded()
    {
        theWarehouse = new Warehouse(numRows, numColumns);
        AllParts inventory = new AllParts();
        for (Location location : partLocationsToFind) {
            int partCode = rand.nextInt(1000);
            Batch batch = new Batch(partCode, MAX_AMOUNT);
            inventory.addPart(new Part(partCode, "Part " + partCode,
                    "Manufacturer: " + partCode, "Description: " + partCode, 9.99));
            theWarehouse.addToWarehouse(location, batch);
        }
        PurchaseOrder order = theWarehouse.createRestockOrder(inventory);
        assertNull(order, "No order should be created when no parts are needed.");
    }

    /**
     * Test restocking with nothing needed restocking.
     */
    @Test
    @org.junit.jupiter.api.Order(19)
    void testRestockOneNeeded()
    {
        theWarehouse = new Warehouse(numRows, numColumns);
        AllParts inventory = new AllParts();
        int partCode = rand.nextInt(1000);
        inventory.addPart(new Part(partCode, "Part " + partCode,
                "Manufacturer: " + partCode, "Description: " + partCode, 9.99));

        PurchaseOrder order = theWarehouse.createRestockOrder(inventory);
        assertNotNull(order, "An order should be created when a part is needed.");
        assertEquals(1, order.getAllBatches().getBatches().size());
        assertEquals(RESTOCK_QUANTITY, order.getAllBatches().getBatches().iterator().next().getQuantity());
        assertEquals(partCode, order.getAllBatches().getBatches().iterator().next().getPartCode());
    }

    /**
     * Test restocking with multiple needing restocking.
     */
    @Test
    @org.junit.jupiter.api.Order(20)
    void testRestockMultipleNeeded()
    {
        theWarehouse = new Warehouse(numRows, numColumns);
        AllParts inventory = new AllParts();
        for(int index = 0; index < partLocationsToFind.size(); index++) {
            int partCode = rand.nextInt(1000);
            inventory.addPart(new Part(partCode, "Part " + partCode,
                    "Manufacturer: " + partCode, "Description: " + partCode, 9.99));
        }
        PurchaseOrder order = theWarehouse.createRestockOrder(inventory);
        assertNotNull(order, "An order should be created when multiple parts are needed.");
        assertEquals(partLocationsToFind.size(), order.getAllBatches().getBatches().size());
        for(Batch aBatch : order.getAllBatches().getBatches()) {
            assertEquals(RESTOCK_QUANTITY, aBatch.getQuantity());
        }
    }

    /**
     * Test creating a purchase order when there is one item missing
     * from a client's order.
     */
    @Test
    @org.junit.jupiter.api.Order(21)
    void testCreatePurchaseOrderOneItem()
    {
        int numberMissing = 1;
        theWarehouse = new Warehouse(numRows, numColumns);
        // The client's inventory of items.
        CustomerOrder order = new CustomerOrder(1, customerCode, orderDate, false);
        Batch theBatch = new Batch(partCodeToFind, 1);
        order.getAllBatches().addBatch(theBatch);
        PurchaseOrder purchaseOrder = theWarehouse.createPurchaseOrder(order);
        assertNotNull(purchaseOrder, "A purchase order should be created.");
        assertEquals(numberMissing, purchaseOrder.getAllBatches().getBatches().size());
        assertEquals(partCodeToFind, purchaseOrder.getAllBatches().getBatches().iterator().next().getPartCode());
        assertEquals(numberMissing, purchaseOrder.getAllBatches().getBatches().iterator().next().getQuantity());
    }

    /**
     * Test creating a purchase order when there are multiple items missing
     * a single part from a client's order.
     */
    @Test
    @org.junit.jupiter.api.Order(22)
    void testCreatePurchaseOrderMultipleItemsOneMissing()
    {
        theWarehouse = new Warehouse(numRows, numColumns);
        int numberMissing = 1;
        CustomerOrder order = new CustomerOrder(1, customerCode, orderDate, false);
        for(int index = 0; index < partQuantitiesToFind.size(); index++) {
            int partCode = index + 10;
            theWarehouse.addToWarehouse(partLocationsToFind.get(index), new Batch(partCode, partQuantitiesToFind.get(index)));
            Batch theBatch = new Batch(partCode, partQuantitiesToFind.get(index) + numberMissing);
            order.getAllBatches().addBatch(theBatch);
        }
        PurchaseOrder purchaseOrder = theWarehouse.createPurchaseOrder(order);
        assertNotNull(purchaseOrder, "A purchase order should be created.");
        Collection<Batch> batches = purchaseOrder.getAllBatches().getBatches();
        assertEquals(partQuantitiesToFind.size(), batches.size());
        for(Batch aBatch : batches) {
            assertEquals(numberMissing, aBatch.getQuantity());
        }
    }

    /**
     * Test creating a purchase order when there are multiple items missing
     * the maximum amount from a client's order.
     */
    @Test
    @org.junit.jupiter.api.Order(23)
    void testCreatePurchaseOrderMultipleItemsMaximumMissing()
    {
        theWarehouse = new Warehouse(numRows, numColumns);
        int numberMissing = MAX_AMOUNT;
        CustomerOrder order = new CustomerOrder(1, customerCode, orderDate, false);
        for(int index = 0; index < partQuantitiesToFind.size(); index++) {
            int partCode = index + 10;
            theWarehouse.addToWarehouse(partLocationsToFind.get(index), new Batch(partCode, partQuantitiesToFind.get(index)));
            Batch theBatch = new Batch(partCode, partQuantitiesToFind.get(index) + numberMissing);
            order.getAllBatches().addBatch(theBatch);
        }
        PurchaseOrder purchaseOrder = theWarehouse.createPurchaseOrder(order);
        assertNotNull(purchaseOrder, "A purchase order should be created.");
        Collection<Batch> batches = purchaseOrder.getAllBatches().getBatches();
        assertEquals(partQuantitiesToFind.size(), batches.size());
        for(Batch aBatch : batches) {
            assertEquals(numberMissing, aBatch.getQuantity());
        }
    }

    /**
     * Create a pick list that leaves one part left.
     */
    @Test
    @org.junit.jupiter.api.Order(24)
    void testCreatePickListOneItem()
    {
        theWarehouse = new Warehouse(numRows, numColumns);
        int numberLeft = 1;
        CustomerOrder order = new CustomerOrder(1, customerCode, orderDate, false);
        Location theLocation = partLocationsToFind.get(0);
        int theQuantity = 2 + rand.nextInt(10);
        Batch theWarehouseBatch = new Batch(partCodeToFind, theQuantity);
        Batch theClientBatch = new Batch(partCodeToFind, theQuantity - numberLeft);
        theWarehouse.addToWarehouse(theLocation, theWarehouseBatch);
        order.getAllBatches().addBatch(theClientBatch);
        List<PickListItem> pickList = theWarehouse.createAPickList(order);
        assertEquals(1, pickList.size());
        PickListItem thePickItem = pickList.get(0);
        assertEquals(partCodeToFind, thePickItem.theBatch().getPartCode());
        assertEquals(theQuantity - numberLeft, thePickItem.theBatch().getQuantity());
        assertEquals(theLocation, thePickItem.theLocation());
        Batch remainingInWarehouse = theWarehouse.getBatchAt(theLocation);
        assertNotNull(remainingInWarehouse);
        assertEquals(numberLeft, remainingInWarehouse.getQuantity());
    }


    /**
     * Create a pick list that takes all of a single part.
     */
    @Test
    @org.junit.jupiter.api.Order(25)
    void testCreatePickListOneItemNoneLeft()
    {
        theWarehouse = new Warehouse(numRows, numColumns);
        int numberLeft = 0;

        CustomerOrder order = new CustomerOrder(1, customerCode, orderDate, false);
        Location theLocation = partLocationsToFind.get(0);
        int theQuantity = 2 + rand.nextInt(10);
        Batch theWarehouseBatch = new Batch(partCodeToFind, theQuantity);
        Batch theClientBatch = new Batch(partCodeToFind, theQuantity - numberLeft);
        theWarehouse.addToWarehouse(theLocation, theWarehouseBatch);
        order.getAllBatches().addBatch(theClientBatch);
        List<PickListItem> pickList = theWarehouse.createAPickList(order);
        assertEquals(1, pickList.size());
        PickListItem thePickItem = pickList.get(0);
        assertEquals(partCodeToFind, thePickItem.theBatch().getPartCode());
        assertEquals(theQuantity - numberLeft, thePickItem.theBatch().getQuantity());
        assertEquals(theLocation, thePickItem.theLocation());
        Batch remainingInWarehouse = theWarehouse.getBatchAt(theLocation);
        assertNull(remainingInWarehouse, "Empty locations in the warehouse must be null.");
    }

    /**
     * Create a pick list from multiple locations.
     */
    @Test
    @org.junit.jupiter.api.Order(26)
    void testCreatePickListMultipleLocations()
    {
        int theQuantity = MAX_AMOUNT + MAX_AMOUNT/2;
        theWarehouse = new Warehouse(numRows, numColumns);
        theWarehouse.addToWarehouse(partLocationsToFind.get(0), new Batch(partCodeToFind, MAX_AMOUNT));
        theWarehouse.addToWarehouse(partLocationsToFind.get(1), new Batch(partCodeToFind, theQuantity - MAX_AMOUNT));
        CustomerOrder order = new CustomerOrder(1, customerCode, orderDate, false);
        Batch theClientBatch = new Batch(partCodeToFind, theQuantity);
        order.getAllBatches().addBatch(theClientBatch);
        List<PickListItem> pickList = theWarehouse.createAPickList(order);
        assertEquals(2, pickList.size());
        int totalPicked = 0;
        for (PickListItem thePickItem : pickList) {
            int pickedQuantity = thePickItem.theBatch().getQuantity();
            assertEquals(partCodeToFind, thePickItem.theBatch().getPartCode());
            assertTrue(pickedQuantity == MAX_AMOUNT || pickedQuantity == theQuantity - MAX_AMOUNT);
            totalPicked += pickedQuantity;
            assertTrue(thePickItem.theLocation().equals(partLocationsToFind.get(0)) ||
                    thePickItem.theLocation().equals(partLocationsToFind.get(1)));
        }
        assertEquals(theQuantity, totalPicked);
        Batch remainingInWarehouse = theWarehouse.getBatchAt(partLocationsToFind.get(0));
        assertNull(remainingInWarehouse, "Empty locations in the warehouse must be null.");
        remainingInWarehouse = theWarehouse.getBatchAt(partLocationsToFind.get(1));
        assertNull(remainingInWarehouse, "Empty locations in the warehouse must be null.");
    }

    /**
     * Test storeDelivery adding 1 part to each existing location.
     */
    @Test
    @org.junit.jupiter.api.Order(27)
    void testStoreDeliveryOneMore()
    {
        int quantityDelivered = 1;
        theWarehouse = new Warehouse(numRows, numColumns);
        Delivery theDelivery = new Delivery(1, orderDate, false);
        for(int index = 0; index < partLocationsToFind.size(); index++) {
            int partCode = index + 10;
            theWarehouse.addToWarehouse(
                    partLocationsToFind.get(index),
                    new Batch(partCode, partQuantitiesToFind.get(index)));
            theDelivery.getAllBatches().addBatch(new Batch(partCode, quantityDelivered));
        }
        theWarehouse.storeDelivery(theDelivery);
        for(int index = 0; index < partLocationsToFind.size(); index++) {
            Batch theBatch = theWarehouse.getBatchAt(partLocationsToFind.get(index));
            assertEquals(partQuantitiesToFind.get(index) + quantityDelivered, theBatch.getQuantity());
        }
    }

    /**
     * Test storeDelivery adding to multiple locations.
     */
    @Test()
    @org.junit.jupiter.api.Order(28)
    void testStoreDeliveryInMultipleLocations()
    {
        int originalQuantity = 1;
        int quantityDelivered = 2 * MAX_AMOUNT + 1;
        theWarehouse = new Warehouse(numRows, numColumns);
        Delivery theDelivery = new Delivery(1, orderDate, false);
        int partCode = partCodeToFind;
        Location originalLocation = partLocationsToFind.get(0);
        theWarehouse.addToWarehouse(originalLocation, new Batch(partCode, originalQuantity));
        theDelivery.getAllBatches().addBatch(new Batch(partCode, quantityDelivered));
        theWarehouse.storeDelivery(theDelivery);
        List<Location> currentLocations = theWarehouse.findPart(partCode);
        assertEquals(3, currentLocations.size());
        Batch updatedBatch = theWarehouse.getBatchAt(originalLocation);
        assertNotNull(updatedBatch);
        assertEquals(MAX_AMOUNT, updatedBatch.getQuantity());
        int totalStored = 0;
        for(Location aLocation : currentLocations) {
            totalStored += theWarehouse.getBatchAt(aLocation).getQuantity();
        }
        assertEquals(originalQuantity + quantityDelivered, totalStored);
    }

    /**
     * Test the result of retrieving the product types from the database.
     */
    @Test
    @org.junit.jupiter.api.Order(29)
    void testGetProductTypes()
    {
        try {
            DatabaseHandler databaseHandler = new DatabaseHandler();
            Map<String, String> types = databaseHandler.readPartTypes();
            StringBuilder actualBuilder = new StringBuilder();
            for (String theType : types.keySet()) {
                actualBuilder.append(String.format("%s: %s%n", theType, types.get(theType)));
            }
            List<String> expectedList =List.of(
                    "CAB", "Cable",
                    "CARD", "Peripheral Card",
                    "CASE", "PC Case",
                    "COOL", "CPU Cooler",
                    "CPU", "Processor",
                    "FAN", "Case Fan",
                    "MOBO", "Motherboard",
                    "NET", "Network Card",
                    "PER", "Peripheral",
                    "PSU", "Power Supply",
                    "RAM", "Random Access Memory",
                    "SND", "Sound Card",
                    "STOR", "Storage",
                    "VID", "Video Card");
            StringBuilder expectedBuilder = new StringBuilder();
            for(int i = 0 ; i < expectedList.size() ; i += 2) {
                expectedBuilder.append(String.format("%s: %s%n", expectedList.get(i), expectedList.get(i+1)));
            }
            String actual = actualBuilder.toString().trim();
            String expected = expectedBuilder.toString().trim();
            assertEquals(expected, actual);
        } catch (SQLTimeoutException e) {
            fail("Failed to connect to the database. Make sure you are connected to the VPN if you are not on campus.");
        } catch (SQLException | ClassNotFoundException e) {
            fail("Failed to access the database: " + e);
        }
    }

    /**
     * Test getCost when there are no items.
     */
    @Test
    @org.junit.jupiter.api.Order(30)
    void testGetCostNoItems()
    {
        AllParts allParts = new AllParts();
        CustomerOrder order = new CustomerOrder(1, customerCode, orderDate, false);
        double expectedCost = 0;
        double actualCost = allParts.getCost(order);
        assertEquals(expectedCost, actualCost);

    }

    /**
     * Test getCost when there is one item.
     */
    @Test
    @org.junit.jupiter.api.Order(31)
    void testGetCostOneItem()
    {
        AllParts allParts = new AllParts();
        double thePrice = 10.0;
        int howMany = 1;
        Part thePart = new Part(partCodeToFind, "Part " + partCodeToFind,
                "Manufacturer: " + partCodeToFind, "Description: " + partCodeToFind, thePrice);
        allParts.addPart(thePart);

        Batch theBatch = new Batch(partCodeToFind, howMany);

        CustomerOrder order = new CustomerOrder(1, customerCode, orderDate, false);
        order.getAllBatches().addBatch(theBatch);
        double expectedCost = thePrice;
        double actualCost = allParts.getCost(order);
        assertEquals(expectedCost, actualCost);

    }

    /**
     * Test getCost when there are multiple items.
     */
    @Test
    @org.junit.jupiter.api.Order(32)
    void testGetCostMultipleItems()
    {
        AllParts allParts = new AllParts();
        double thePrice = 10.0;
        double expectedCost = 0;
        int partCode = partCodeToFind;
        CustomerOrder order = new CustomerOrder(1, customerCode, orderDate, false);
        AllBatches customerInventory = order.getAllBatches();
        for (Integer integer : partQuantitiesToFind) {
            Part thePart = new Part(partCode, "Part " + partCode,
                    "Manufacturer: " + partCode, "Description: " + partCode, thePrice);
            allParts.addPart(thePart);
            Batch aBatch = new Batch(partCode, integer);
            customerInventory.addBatch(aBatch);
            expectedCost += aBatch.getQuantity() * thePrice;
            partCode++;
        }

        double actualCost = allParts.getCost(order);
        assertEquals(expectedCost, actualCost);

    }

}