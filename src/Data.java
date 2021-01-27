import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Data {

    Connection conn;
    Statement st;
    String sentence;
    ResultSet rs;

    public Data() { 

    }

    public Data(String serverAddress, String port, String username, String password) throws SQLException {

        conn = DriverManager.getConnection(
                "jdbc:mysql://" + serverAddress + ":" +port,
                username, password);
        conn.setAutoCommit(true);
        st = conn.createStatement();

    } 

}
