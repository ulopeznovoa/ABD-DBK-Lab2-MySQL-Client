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

        // Open connection

        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://u000host.ehu.eus:8306",
                    "dummy", "foobar2020");
            conn.setAutoCommit(true);
            st = conn.createStatement();

        } catch (SQLException e) {

            e.printStackTrace();
        }

    }

    public Data(String serverAddress, String port, String username, String password) throws SQLException {

        conn = DriverManager.getConnection(
                "jdbc:mysql://" + serverAddress + ":" +port,
                username, password);
        conn.setAutoCommit(true);
        st = conn.createStatement();

    }

    /*
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        Data myData = new Data();
        System.out.println("MySQL is cute!!!");

    } */

}
