package webview.converter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Write the Customers table as an HTML page.
 */
public class WriteCustomers {
    private static final String header =
            """
                    <head>
                        <link rel="stylesheet" href="style.css">
                        <title>Customers</title>
                    </head>
           """;

    public static void write(DatabaseReader reader, String filename)
    {
         StringBuilder builder = new StringBuilder();
         builder.append(
                """
                <!DOCTYPE html>
                <html>
                """);
         builder.append(header).append('\n');
         builder.append(Config.COMMON_HEADER);
         builder.append("<body>\n");
         builder.append(
                """
                 <h2 class = "centre">Customers</h1>
                <table class = "center">
                <th>Code</th>
                <th>Name</th>
                <th>Email Address</th>
                <th>Phone Number</th>
                <th>Address</th>
                """
        ).append('\n');

        List<Map<String, String>> contents = reader.getTable(
                "SELECT customerCode, businessName, email, phoneNumber, address FROM customers ORDER BY businessName");
        for(Map<String, String> row : contents) {
            builder.append("<tr>");
            for(String col : List.of("customerCode", "businessName", "email", "phoneNumber", "address")) {
                builder.append("<td>");
                builder.append(row.get(col));
                builder.append("</td>");
            }
            builder.append("</tr>").append('\n');
        }
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
