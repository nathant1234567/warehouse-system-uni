package webview.converter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class WriteDeliveries {
    private static final String header =
            """
                    <head>
                        <link rel="stylesheet" href="style.css">
                        <title>Deliveries</title>
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
        builder.append("<h2 class = \"centre\">Deliveries</h2>\n");
        builder.append("<table class='center two'>\n");

        List<Map<String, String>> contents = reader.getTable(
                """
                                SELECT oi.deliveryNumber,
                                           oi.partCode,
                                           o.fulfilled,
                                           p.description,
                                           p.price,
                                           oi.quantity,
                                           p.type,
                                           t.typeDescription,
                                           p.price * oi.quantity AS total
                                    FROM deliveryItems oi
                                    JOIN deliveries o ON oi.deliveryNumber = o.deliveryNumber
                                    JOIN parts p ON oi.partCode = p.partCode
                                    JOIN partTypes t ON p.type = t.type
                                    WHERE o.fulfilled = 'N'
                                    ORDER BY p.type, oi.partCode;
                        """);
        double currentProductTotal = 0;
        int currentProductQty = 0;
        String previousProduct = null;
        String previousType = null;
        double totalAllOrders = 0;
        for (Map<String, String> row : contents) {
            String partCode = row.get("partCode");
            if (!partCode.equals(previousProduct)) {
                if (previousProduct != null) {
                    builder.append(String.format("<tr><td><strong>TOTAL:</td><td><strong>%d</strong></td><td><strong>%s%.2f</strong></td></tr>\n",
                            currentProductQty, Config.CURRENCY_SYMBOL, currentProductTotal));
                    currentProductTotal = 0;
                    currentProductQty = 0;
                }
            }
            String type = row.get("type");
            if (!type.equals(previousType)) {
                builder.append(String.format("<tr class='custom-tr2'><td colspan='3'><strong>%s Products</strong></td></tr>\n",
                        row.get("typeDescription")));
                previousType = type;
            }

            if (!partCode.equals(previousProduct)) {
                builder.append(String.format("<tr class='custom-tr'><td colspan='3'><strong>Item %s: %s %s%.2f each</strong></td></tr>\n",
                        row.get("partCode"), row.get("description"), Config.CURRENCY_SYMBOL, Double.parseDouble(row.get("price"))));
                builder.append("<tr><th>Delivery</th><th>Quantity</th><th>Total</th></tr>\n");
                previousProduct = partCode;
            }
            builder.append("<tr>");
            for(String col : List.of("deliveryNumber", "quantity")) {
                builder.append(String.format("<td>%s</td>", row.get(col)));
            }
            double total = Double.parseDouble(row.get("total"));
            builder.append(String.format("<td>%s%.2f</td>", Config.CURRENCY_SYMBOL, total));
            builder.append("</tr>\n");
            // Update total
            currentProductTotal += total;
            currentProductQty += Integer.parseInt(row.get("quantity"));

            // Update total for all orders
            totalAllOrders += total;
        }
        if(previousProduct != null) {
            builder.append(String.format("<tr><td><strong>TOTAL:</strong></td><td><strong>%d</strong></td><td><strong>%s%.2f</strong></td></tr>",
                    currentProductQty, Config.CURRENCY_SYMBOL, currentProductTotal));
        }
        builder.append(String.format("<tr><td colspan='2'><strong>Total for All Deliveries:</strong></td><td colspan='2'><strong>%s%.2f</strong></td></tr>",
                Config.CURRENCY_SYMBOL, totalAllOrders ));
        builder.append(
                """
                </table>
                </body>
                </html>\n
                """
        );

        try(FileWriter writer = new FileWriter(filename)) {
            writer.write(builder.toString());
        }
        catch(IOException ex) {
            System.err.println("Failed to write " + filename);
        }
    }
}
