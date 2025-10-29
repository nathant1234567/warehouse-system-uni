package webview.converter;

public interface Config {
    String SQLDatabase = "warehousedata.sqlite";
    int NUMBER_OF_ROWS = 20;
    int NUMBER_OF_COLUMNS = 30;
    String CURRENCY_SYMBOL = "&pound;";
    // The header common to all pages.
    // Contains links to each page.
    String SITE_ADDRESS = "html/";
    String WAREHOUSE = "warehouse.html";
    String ALL_PARTS = "allParts.html";
    String CUSTOMERS = "customers.html";
    String ORDERS = "orders.html";
    String PURCHASE_ORDERS = "purchaseOrders.html";
    String DELIVERIES = "deliveries.html";
    String COMMON_HEADER = String.format(
            """
                            	  	<div class = "centre"> \s
                            	   		<h1>
                            	      		<i class="fas fa-laptop"></i> Team DB PC Logistics <i class="fas fa-laptop"></i>
                            	   		</h1>
                            		</div>
                            		<div class="topnav center">
                            			  <a href=%s>Warehouse</a>
                            			  <a href=%s>Parts</a>
                            			  <a href=%s>Customers</a>
                            			  <a href=%s>Customer Orders</a>
                            			  <a href=%s>Purchase Orders</a>
                            			  <a href=%s>Deliveries</a>
                            		</div>
                    """, WAREHOUSE, ALL_PARTS, CUSTOMERS, ORDERS, PURCHASE_ORDERS, DELIVERIES);
}
