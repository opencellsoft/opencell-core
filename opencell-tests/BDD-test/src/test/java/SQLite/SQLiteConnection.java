package SQLite;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Stream;

public class SQLiteConnection {

    private static final String JSON_DATABASE = "Test_automation.db";
    private static final String JSON_TABLE = "json_data_table";
    private static final char SINGLE_QUOTE = '\'';

    public static void createNewDatabase() {
        String url = "jdbc:sqlite:C://sqlite/db/" + JSON_DATABASE;

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createJsonTable() {
        // SQLite connection string
        String url = "jdbc:sqlite:C://sqlite/db/" + JSON_DATABASE;

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS " + JSON_TABLE + "(\n"
                + "	key1 text NOT NULL,\n"
                + "	key2 text NOT NULL,\n"
                + "	key3 text NOT NULL,\n"
                + "	jsonObject JSON NOT NULL,\n"
                + "	jsonExpectedResult JSON NOT NULL,\n"
                + " PRIMARY KEY (key1, key2, key3)"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
             // create a new table
             stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:C://sqlite/db/" + JSON_DATABASE;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void insertJsonTable(String key1, String key2, String key3,
                                       JSONObject jsonObject, String jsonExpectedResult) {
        String sql = "INSERT INTO " + JSON_TABLE + "(key1, key2, key3, jsonObject, jsonExpectedResult) VALUES(?, ?, ?, json(?), ?)";

        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, key1);
            pstmt.setString(2, key2);
            pstmt.setString(3, key3);
            pstmt.setObject(4, jsonObject);
            pstmt.setObject(5, jsonExpectedResult);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static String selectJsonTable(String key1, String key2, String key3) {
        String sqlToSearchForTableName = "SELECT jsonObject FROM " + JSON_TABLE + " WHERE key1 = " + SINGLE_QUOTE + key1 + SINGLE_QUOTE
                        + " AND key2 = " + SINGLE_QUOTE + key2 + SINGLE_QUOTE
                        + " AND key3 = " + SINGLE_QUOTE + key3 + SINGLE_QUOTE;

        try (Connection conn = connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sqlToSearchForTableName)){

            // loop through the result set
            while (rs.next()) {
//System.out.println("rs.getString(\"jsonObject\") : " + rs.getString("jsonObject") );
                return rs.getString("jsonObject");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) throws IOException, ParseException {
        createNewDatabase();
        createJsonTable();

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(
                "C:\\IdeaProjects\\opencell-tests\\BDD-test\\src\\test\\resources\\CRUD\\seller\\UpdateSeller.json"));
//        JSONObject jsonExpectedResult = (JSONObject) parser.parse(new FileReader(
//                "C:\\IdeaProjects\\opencell-tests\\BDD-test\\src\\test\\resources\\CRUD\\seller\\ExpectedResult.txt"));

        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(
                Paths.get("C:\\IdeaProjects\\opencell-tests\\BDD-test\\src\\test\\resources\\CRUD\\seller\\ExpectedResult.txt"),
                StandardCharsets.UTF_8))
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        // insert three new rows
        insertJsonTable("Seller","UpdateSeller","TableUpdateSeller", jsonObject, contentBuilder.toString());

        // select a name of Json table
        selectJsonTable("Seller", "UpdateSeller", "TableUpdateSeller");

    }
}
