package webview.converter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WriteWarehouse {
    private static final String header =
            """
                    <head>
                        <link rel="stylesheet" href="style.css">
                        <title>Warehouse</title>
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
        builder.append("<h2 class = \"centre\">Birds-eye View of the Warehouse</h2>\n");
        builder.append("<table class='center two'>\n");
        List<Map<String, String>> contents = reader.getTable("SELECT location, partCode, quantity FROM warehouse");
        Map<String, String> grid = new HashMap<>();
        for (Map<String, String> row : contents) {
            String location = row.get("location");
            grid.put(location, String.format("Part: %s<br />Qty: %s %s",
                                            row.get("partCode"), row.get("quantity"), location));
        }
        Map<String, String> contentDetails = new HashMap<>();
        builder.append("<div class='grid-container'>\n");
        for(int row = 0; row < Config.NUMBER_OF_ROWS; row++) {
            for(int col = 0; col < Config.NUMBER_OF_COLUMNS; col++) {
                String location = row + "," + col;
                String locationContents = grid.get(location);
                if(locationContents != null) {
                    // On click, bring up an alert with further details of the part in that location.
                    String locationDetails = getDetails(reader, location);
                    builder.append(String.format("<div class='grid-item with-content' data-coordinate='%s' onClick='showItemDetails(\"%s\")'>%s</div>\n",
                            location, locationDetails, locationContents));
                    contentDetails.put(location, locationDetails);
                }
                else {
                    builder.append(String.format("<div class='grid-item' data-coordinate='%s'></div>\n", location));
                }
            }
        }
        builder.append("</div>\n");
        builder.append(
                """
                <script>
                function showItemDetails(details) {
                    alert(details);
                }
                </script>
                """
        );
        builder.append(
                """
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

    /**
     * Retrieve details of the part at the given location.
     * @param reader The database reader.
     * @param location The location in row,col format.
     * @return A string that can be used in a JS alert window.
     */
    private static String getDetails(DatabaseReader reader, String location)
    {
        List<Map<String, String>> partDetails =
                reader.getTable(String.format(
                        "SELECT p.partCode, p.type, t.typeDescription, p.manufacturer, p.description, p.price, w.quantity FROM parts p JOIN warehouse w ON p.partCode = w.partCode JOIN partTypes t ON p.type = t.type WHERE w.location = '%s'",
                        location));
        StringBuilder builder = new StringBuilder();
        if(! partDetails.isEmpty()) {
            Map<String, String> response = partDetails.get(0);
            builder.append("Part code: ")
                    .append(response.get("partCode"))
                    .append(" - ")
                    .append(response.get("typeDescription"))
                    .append("\\nManufacturer: ")
                    .append(response.get("manufacturer"))
                    .append("\\nDescription: ")
                    .append(response.get("description"))
                    .append("\\nPrice: ")
                    .append(response.get("price"))
                    .append("\\nQuantity: ")
                    .append(response.get("quantity"))
                    .append("\\n\\nLocated in warehouse section: ")
                    .append(location);
        }
        else {
            System.out.println("No part in this location.");
//            builder.append("No part in this location.");
        }
        return builder.toString();
    }
}
