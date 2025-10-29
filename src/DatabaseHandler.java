import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Handle interactions with the database.
 * @author Nathan Thompsom - njt38
 */
public class DatabaseHandler
{
    private static final int ROWS = 20, COLS = 30;
    private final Connection connection;
    private Warehouse warehouse;

    /**
     * Create a database reader and read the contents of
     * most of the tables.
     *
     * @throws ClassNotFoundException If there is no Database driver.
     * @throws SQLException           If there is a problem reading from the database.
     */
    public DatabaseHandler()
            throws ClassNotFoundException, SQLException
    {
        Class.forName("org.sqlite.JDBC");
        String database = "warehousedata.sqlite";
        String url = "jdbc:sqlite:" + database;

        // Set a timeout in case of connectivity issues.
        DriverManager.setLoginTimeout(1);
        connection = DriverManager.getConnection(url);
    }

    /**
     * Read all the parts that the company sells (which may or may not be in stock)
     *
     * @throws SQLException on SQL error.
     */
    public AllParts readParts() throws SQLException
    {
        AllParts allParts = new AllParts();

        // DO NOT CHANGE ANY OF THE CODE BELOW THIS LINE
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from parts");
        while (resultSet.next()) {
            int partCode = resultSet.getInt("partCode");
            String type = resultSet.getString("type").trim();
            String manufacturer = resultSet.getString("manufacturer").trim();
            String description = resultSet.getString("description").trim();
            double price = resultSet.getDouble("price");
            // DO NOT CHANGE ANY OF THE CODE ABOVE THIS LINE

            Part part = new Part(partCode, type, manufacturer, description, price);

            allParts.addPart(part);
        }
        resultSet.close();
        statement.close();

        return allParts;
    }

    /**
     * Get the part types from the database and return them
     * as a map with type as the key and type description as the value.
     *
     * @return the part types.
     */
    public Map<String, String> readPartTypes()
            throws SQLException
    {
        Map<String, String> types = new TreeMap<>();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from partTypes");

        while (resultSet.next()) {
            String type = resultSet.getString("type").trim();
            String description = resultSet.getString("typeDescription").trim();
            types.put(type, description);
        }
        resultSet.close();
        statement.close();
        return types;
    }

    /**
     * Read the contents of each warehouse section
     *
     * @throws SQLException On SQL error.
     */
    public Warehouse readWarehouse() throws SQLException
    {
        // DO NOT CHANGE ANY OF THE CODE BELOW THIS LINE
        Statement statement = connection.createStatement();
        Warehouse warehouse = new Warehouse(ROWS, COLS);
        ResultSet resultSet =
                statement.executeQuery("select * from warehouse");
        while (resultSet.next()) {
            String location = resultSet.getString("location").trim();
            int partCode = resultSet.getInt("partCode");
            int quantity = resultSet.getInt("quantity");
            String[] rowCol = location.split(",");
            int row = Integer.parseInt(rowCol[0]);
            int col = Integer.parseInt(rowCol[1]);
            // DO NOT CHANGE ANY OF THE CODE ABOVE THIS LINE

            Location theLocation = new Location(row, col);
            Batch theBatch = new Batch(partCode, quantity);

            warehouse.addToWarehouse(theLocation, theBatch);
        }
        resultSet.close();
        statement.close();
        return warehouse;
    }

    /**
     * Read the customer information; name, address etc and adds it to the customer object.
     *
     * @throws SQLException on SQL error.
     */
    public AllCustomers readCustomers() throws SQLException
    {
        AllCustomers allCustomers = new AllCustomers();
        // DO NOT CHANGE ANY OF THE CODE BELOW THIS LINE
        Statement statement = connection.createStatement();
        ResultSet resultSet =
                statement.executeQuery("select * from customers");
        while (resultSet.next()) {
            int customerCode = resultSet.getInt("customerCode");
            String businessName = resultSet.getString("businessName").trim();
            String vatNumber = resultSet.getString("vatNumber").trim();
            String email = resultSet.getString("email").trim();
            String phoneNumber = resultSet.getString("phoneNumber").trim();
            String address = resultSet.getString("address").trim();
            // DO NOT CHANGE ANY OF THE CODE ABOVE THIS LINE

            Customer customer = new Customer(customerCode, businessName, vatNumber, email, phoneNumber, address);
            allCustomers.addCustomer(customer);
        }
        resultSet.close();
        statement.close();

        return allCustomers;
    }

    /**
     * Read all outstanding customer orders that have not been picked yet
     *
     * @throws SQLException on SQL error.
     */
    public AllCustomerOrders readCustomerOrders()
            throws SQLException
    {
        AllCustomerOrders allCustomerOrders = new AllCustomerOrders();
        // DO NOT CHANGE ANY OF THE CODE BELOW THIS LINE
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from customerOrders");
        while (resultSet.next()) {
            int orderNumber = resultSet.getInt("orderNumber");
            int customerCode = resultSet.getInt("customerCode");
            Date dateOrdered = new Date(resultSet.getString("dateOrdered"));
            boolean fulfilled = resultSet.getString("fulfilled").equals("Y");
            // DO NOT CHANGE ANY OF THE CODE ABOVE THIS LINE

            CustomerOrder customerOrder = new CustomerOrder(orderNumber, customerCode, dateOrdered, fulfilled);
            allCustomerOrders.addOrder(customerOrder);
        }
        resultSet.close();
        statement.close();

        return allCustomerOrders;
    }

    /**
     * Read all outstanding purchase orders that have not been placed yet
     *
     * @throws SQLException on SQL error.
     */
    public AllPurchaseOrders readPurchaseOrders()
            throws SQLException
    {
        AllPurchaseOrders allPurchaseOrders = new AllPurchaseOrders();
        // DO NOT CHANGE ANY OF THE CODE BELOW THIS LINE
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery( "select * from purchaseOrders");
        while (resultSet.next()) {
            int orderNumber = resultSet.getInt("purchaseOrderNumber");
            Date dateOrdered = new Date(resultSet.getString("dateOrdered"));
            boolean fulfilled = resultSet.getString("fulfilled").equals("Y");
            // DO NOT CHANGE ANY OF THE CODE ABOVE THIS LINE

            PurchaseOrder purchaseOrder = new PurchaseOrder(orderNumber, dateOrdered, fulfilled);
            allPurchaseOrders.addOrder(purchaseOrder);
        }
        resultSet.close();
        statement.close();
        return allPurchaseOrders;
    }

    /**
     * Read all recent deliveries that have not been put into the warehouse yet
     *
     * @throws SQLException on SQL error.
     */
    public AllDeliveries readDeliveries() throws SQLException
    {
        AllDeliveries allDeliveries = new AllDeliveries();
        // DO NOT CHANGE ANY OF THE CODE BELOW THIS LINE
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from deliveries");
        while (resultSet.next()) {
            int deliveryNumber = resultSet.getInt("deliveryNumber");
            Date dateDelivered = new Date(resultSet.getString("dateDelivered"));
            boolean fulfilled = resultSet.getString("fulfilled").equals("Y");
            // DO NOT CHANGE ANY OF THE CODE ABOVE THIS LINE

            Delivery delivery = new Delivery(deliveryNumber, dateDelivered, fulfilled);
            allDeliveries.addDelivery(delivery);
        }
        resultSet.close();
        statement.close();

        return allDeliveries;
    }

    /**
     * Read all the order items or delivery items from the given  table.
     * @param tableName The table to read.
     * @return All the order items from the given table..
     * @throws SQLException
     */
    public AllOrderItems readOrderItems(String tableName) throws SQLException
    {
        AllOrderItems allOrderItems = new AllOrderItems();
        // DO NOT CHANGE ANY OF THE CODE BELOW THIS LINE
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from " + tableName);
        // Select the correct column name for the table.
        String orderNumberColumn = switch(tableName) {
            case "customerOrderItems" -> "orderNumber";
            case "purchaseOrderItems" -> "purchaseOrderNumber";
            case "deliveryItems" -> "deliveryNumber";
            default -> throw new IllegalStateException("Unexpected table name: " + tableName);
        };
        while (resultSet.next()) {
            int orderNumber = resultSet.getInt(orderNumberColumn);
            int partCode = resultSet.getInt("partCode");
            int quantity = resultSet.getInt("quantity");
            // DO NOT CHANGE ANY OF THE CODE ABOVE THIS LINE

            OrderItem orderItem = new OrderItem(orderNumber, partCode, quantity);
            allOrderItems.addOrderItem(orderItem);
        }
        resultSet.close();
        statement.close();

        return allOrderItems;
    }

    /**
     * Set the fulfilled flag n the orders table to 'Y'.
     *
     * @param orderNumber the customer's order number.
     * @throws SQLException
     */
    public void setCustomerOrderFulfilled(int orderNumber) throws SQLException
    {
        setFulfilled("customerOrders", "orderNumber", orderNumber);
    }

    /**
     * Set the fulfilled flag n the deliveries table to 'Y'.
     *
     * @param deliveryNumber the delivery number.
     * @throws SQLException
     */
    public void setDeliveryFulfilled(int deliveryNumber) throws SQLException
    {
        setFulfilled("deliveries", "deliveryNumber", deliveryNumber);
    }

    /**
     * Set the fulfilled flag in the purchases table to 'Y'.
     *
     * @param purchaseOrderNumber the purchase order number.
     * @throws SQLException
     */
    public void setPurchaseOrderFulfilled(int purchaseOrderNumber) throws SQLException
    {
        setFulfilled("purchases", "purchaseOrderNumber", purchaseOrderNumber);
    }

    /**
     * Update the warehouse and deliveries tables as a result of a delivery.
     * <b>DO NOT CHANGE THIS METHOD.</b>
     * @param theWarehouse The warehouse
     * @param deliveryNumber The delivery number
     */
    public void updateWarehouseFromDelivery(Warehouse theWarehouse, int deliveryNumber) {
        try {
            updateWarehouse(theWarehouse);
            setDeliveryFulfilled(deliveryNumber);
        } catch (SQLException e) {
            System.err.println("Failed to update the warehouse from a delivery: " + e);
        }
    }

    /**
     * Update the warehouse and customerOrders tables as a result of a delivery.
     * <b>DO NOT CHANGE THIS METHOD.</b>
     * @param theWarehouse The warehouse
     * @param orderNumber The customer order number.
     */
    public void updateWarehouseFromCustomerOrder(Warehouse theWarehouse, int orderNumber) {
        try {
            updateWarehouse(theWarehouse);
            setCustomerOrderFulfilled(orderNumber);
        } catch (SQLException e) {
            System.err.println("Failed to update the warehouse from a customer order: " + e);
        }
    }

    /**
     * Update the warehouse table from the current state.
     *
     * @param theWarehouse The warehouse.
     */
    private void updateWarehouse(Warehouse theWarehouse)
            throws SQLException
    {
        try(Statement statement = connection.createStatement()) {
            // Clear the existing contents of the warehouse table.
            statement.executeUpdate("delete from warehouse");
            // UNCOMMENT FROM HERE
            // Insert each batch into the warehouse at its location.
            List<Location> partLocations = theWarehouse.getPartLocations();
            for (Location theLocation : partLocations) {
                Batch aBatch = theWarehouse.getBatchAt(theLocation);
                if (statement.executeUpdate(
                        String.format("insert into warehouse values ('%d,%d', %d, %d)",
                                theLocation.row(), theLocation.col(),
                                aBatch.getPartCode(), aBatch.getQuantity())) != 1) {
                    System.err.println("Failed to insert " + aBatch + " into the warehouse.");
                }
            }
            // END OF CODE TO BE UNCOMMENTED
        }
    }


    /**
     * Set the fulfilled flag to Y for where the given column has the matching number.
     *
     * @param table      The table to update.
     * @param columnName The column to match.
     * @param number     The value in the column.
     * @throws SQLException
     */
    private void setFulfilled(String table, String columnName, int number)
            throws SQLException
    {
        Statement statement = connection.createStatement();
        String sql = String.format("update %s set fulfilled = 'Y' where %s = %d",
                table, columnName, number);
        int response = statement.executeUpdate(sql);
        statement.close();
        if(response != 1) {
            throw new SQLException(
                    String.format("Failed to update the fulfilled column of table %s where %s = %d.",
                            table,  columnName, number));
        }
    }

    /**
     * Get the warehouse.
     *
     * @return the warehouse.
     */
    public Warehouse getWarehouse() throws SQLException {
        if(warehouse == null) {
            warehouse = readWarehouse();
        }
        return warehouse;
    }
}
