import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionManager{

    private static DBConnectionManager ourInstance = new DBConnectionManager();

    private static String USERNAME = "root";
    private static String PASSWORD = "root";
    private static String CONNECTION_STRING = "jdbc:mysql://localhost:3306/gillette?useSSL=false";

    private static Connection connection = null;

    private DBConnectionManager() {
    }

    public static DBConnectionManager getInstance() {
        return ourInstance;
    }

    public Connection getConnection() {

        if (connection == null) connection = openConnection();
        return connection;
    }

    public void closeConnection(){

        if (connection != null){
            try {
                 connection.close();
                System.out.println("Connection successfully closed/");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static Connection openConnection(){

        try {
            connection = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
            System.out.println("Successfully connected to database!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

}
