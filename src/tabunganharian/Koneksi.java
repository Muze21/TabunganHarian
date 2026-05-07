package tabunganharian;
//  src\Koneksi.java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Koneksi {

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/db_tabungan",
            "root",
            ""
        );
    }
}
