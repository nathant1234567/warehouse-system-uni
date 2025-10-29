package webview.converter;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


/**
 * Write the tables of the Warehouse database to separate HTML files.
 */
public class DatabaseReader {
    //    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    // The name of the database.
    private final String database;
    // SQLite connection string
    private final String url;


    public DatabaseReader(String database) {
        this.database = database;
        url = "jdbc:sqlite:" + database;
    }

    /**
     * Connect to the database
     *
     * @param database the name of the database
     * @return the Connection object
     */
    private Connection connect(String database) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }


    /**
     * Use the given query to retrieve data as a list of maps from column name to value.
     * @param sql The query
     * @return The table as a list of maps.
     */
    public List<Map<String, String>> getTable(String sql) {
        List<Map<String, String>> contents = new ArrayList<>();
        try (Connection conn = this.connect(database);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
             ResultSetMetaData metaData = rs.getMetaData();
             int columnCount = metaData.getColumnCount();
             while (rs.next()) {
                 Map<String, String> row = new HashMap<>();
                 for (int columnNumber = 1; columnNumber <= columnCount; columnNumber++) {
                    String name = metaData.getColumnName(columnNumber);
                    row.put(name, rs.getString(columnNumber));
                }
                 contents.add(row);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return contents;
    }

    public void selectAll(String tableName) {
        String sql = "SELECT * FROM " + tableName;

        try (Connection conn = this.connect(database);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
             ResultSetMetaData metaData = rs.getMetaData();
             int columnCount = metaData.getColumnCount();
             while (rs.next()) {
                for (int columnNumber = 1; columnNumber <= columnCount; columnNumber++) {
                    String name = metaData.getColumnName(columnNumber);
                    int type = metaData.getColumnType(columnNumber);
                    switch (type) {
                        case Types.INTEGER:
                            System.out.printf("%s %d\t", name, rs.getInt(name));
                            break;
                        case Types.REAL:
                            System.out.printf("%s %f\t", name, rs.getDouble(name));
                            break;
                        case Types.VARCHAR:
                            System.out.printf("%s %s\t", name, rs.getString(name));
                            break;
                        default:
                            System.out.printf("%s %s\t", name, rs.getString(name));
                            break;
                    }
                }
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}