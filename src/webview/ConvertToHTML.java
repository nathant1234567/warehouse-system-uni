package webview;

import webview.converter.*;

/**
 * Class to write static versions of the Warehouse HTML pages.
 * This needs to be run whenever the database is changed.
 */
public class ConvertToHTML {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException
    {
        // Make sure the driver is available.
        Class.forName("org.sqlite.JDBC");
        DatabaseReader reader = new DatabaseReader(Config.SQLDatabase);
        WriteCustomers.write(reader, Config.SITE_ADDRESS + Config.CUSTOMERS);
        WriteOrders.write(reader, Config.SITE_ADDRESS + Config.ORDERS);
        WritePurchaseOrders.write(reader, Config.SITE_ADDRESS + Config.PURCHASE_ORDERS);
        WriteDeliveries.write(reader, Config.SITE_ADDRESS + Config.DELIVERIES);
        WriteWarehouse.write(reader, Config.SITE_ADDRESS + Config.WAREHOUSE);
        WriteAllParts.write(reader, Config.SITE_ADDRESS + Config.ALL_PARTS);
        System.out.println("HTML files written to " + Config.SITE_ADDRESS + Config.WAREHOUSE);
        System.out.println("View the HTML files by running the ViewHTML config in IntelliJ.");
    }
}
