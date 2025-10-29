package webview.converter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class WriteAllParts {
    private static final String header =
            """
                    <head>
                        <link rel="stylesheet" href="style.css">
                        <title>All Parts</title>
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
        builder.append("<h2 class = \"centre\">All Parts by Type</h2>\n");
        builder.append("<table class='center two'>\n");
        List<Map<String, String>> contents = reader.getTable(
                """
                SELECT manufacturer, description, price, partCode, typeDescription FROM parts, partTypes 
                WHERE parts.type = partTypes.type ORDER BY typeDescription
                """);
        String previousType = null;
        for (Map<String, String> row : contents) {
            String typeDescription = row.get("typeDescription");
            if(! typeDescription.equals(previousType)) {
                builder.append(
                        String.format("<tr class='custom-tr'><td colspan='6'><strong>%s Parts:</strong></td></tr>\n",
                                typeDescription));
                builder.append("<th>Part</th><th>Manufacturer</th><th>Description</th><th>Price</th><th>Stock</th><th>Search</th>");
                previousType = typeDescription;
            }
            builder.append("<tr>");
            for(String col : List.of("partCode", "manufacturer", "description")) {
                builder.append(String.format("<td>%s</td>", row.get(col)));
            }
            double price = Double.parseDouble(row.get("price"));
            builder.append(String.format("<td>%s%.2f</td>", Config.CURRENCY_SYMBOL, price));
            int totalQuantity = 0;
            String partCode = row.get("partCode");
            List<Map<String, String>> partRows =
                    reader.getTable(String.format("SELECT location, quantity FROM warehouse WHERE partCode = '%s' ",
                                                partCode));
            StringBuilder locationList = new StringBuilder();
            locationList.append("All locations of part ").append(partCode).append("\\n");
            if(! partRows.isEmpty()) {
                for (Map<String, String> partRow : partRows) {
                    int quantity = Integer.parseInt(partRow.get("quantity"));
                    totalQuantity += quantity;
                    locationList.append(String.format("Location: %5s Quantity: %3d\\n",
                                                      partRow.get("location"), quantity));
                }
            }
            else {
                locationList.append("There are none in the warehouse.");
            }
            builder.append(String.format("<td>%d</td>", totalQuantity));
            builder.append(String.format("<td><button onClick='search(\"%s\")' class=\"button smallButton\"> Search </button></td>",
                    locationList.toString()));

            builder.append("</tr>\n");
        }
        builder.append(
                """
                </table>
                <script>
                function search(partCode) {
                    alert(partCode);
                }
                </script>
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
