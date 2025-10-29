package webview.converter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class WriteOrders {
    private static final String header =
            """
                    <head>
                        <link rel="stylesheet" href="style.css">
                        <title>Orders</title>
                    </head>
           """;
    public static void write(DatabaseReader reader, String filename) {
        StringBuilder builder = new StringBuilder();
        builder.append(
                """
                        <!DOCTYPE html>
                        <html>
                        """);
        builder.append(header).append('\n');
        builder.append(Config.COMMON_HEADER);
        builder.append("<body>\n");
        builder.append("<table class='center three'>\n");
        List<Map<String, String>> contents = reader.getTable(
                """
                SELECT c.businessName,
                c.customerCode,
                o.customerCode,
                o.fulfilled,
                oi.orderNumber,
                oi.partCode,
                p.description,
                p.price,
                oi.quantity,
                p.price * oi.quantity AS total
                FROM customerOrderItems oi
                JOIN customerOrders o ON oi.orderNumber = o.orderNumber
                JOIN customers c ON o.customerCode = c.customerCode
                JOIN parts p ON oi.partCode = p.partCode
                WHERE o.fulfilled = 'N'
                ORDER BY oi.orderNumber, oi.partCode;
        """);

        double currentCustomerTotal = 0;
        String previousOrder = null;
        double totalAllOrders = 0;
        for(Map<String, String> row : contents) {
            String orderNumber = row.get("orderNumber");
            if(! orderNumber.equals(previousOrder)) {
                if(previousOrder != null) {
                    builder.append(String.format("<tr><td colspan='4'><strong>TOTAL:</strong></td><td colspan='2'><strong>%s%.2f</strong></td></tr>\n",
                            Config.CURRENCY_SYMBOL, currentCustomerTotal));
                    currentCustomerTotal = 0;
                }
                builder.append(String.format("<tr class='custom-tr'><td colspan='7'><strong>%s %s - Order number %s</strong></td></tr>\n",
                        row.get("customerCode"), row.get("businessName"), orderNumber));
                builder.append("<tr><th>Item</th><th>Description</th><th>Price</th><th>Quantity</th><th>Total</th></tr>\n");
                previousOrder = orderNumber;
            }
            builder.append("<tr>");
            for(String col : List.of("partCode", "description", "price", "quantity")) {
                builder.append(String.format("<td>%s</td>", row.get(col)));
            }
            builder.append(String.format("<td>%s%s</td>", Config.CURRENCY_SYMBOL, row.get("total")));
            builder.append("</tr>\n");
            double total = Double.parseDouble(row.get("total"));
            currentCustomerTotal += total;
            totalAllOrders += total;
        }
        if(previousOrder != null) {
            builder.append(String.format("<tr><td colspan='4'><strong>TOTAL:</strong></td><td><strong>%s%.2f</strong></td></tr>",
                    Config.CURRENCY_SYMBOL, currentCustomerTotal));
        }
        builder.append(String.format("<tr><td colspan='4'><strong>Total for All Orders:</strong></td><td><strong>%s%.2f</strong></td></tr>",
                Config.CURRENCY_SYMBOL, totalAllOrders));

        builder.append(
                """
                </table>
                </body>
                </html>
                """
        ).append('\n');

        try(FileWriter writer = new FileWriter(filename)) {
            writer.write(builder.toString());
        }
        catch(IOException ex) {
            System.err.println("Failed to write " + filename);
        }
    }
}
