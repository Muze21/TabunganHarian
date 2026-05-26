package tabunganharian;
//  src\Koneksi.java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Koneksi {

    public static Connection getConnection() throws SQLException {
        // Alamat database: jdbc:mysql://[host]:[port]/[nama_database]
        String url = "jdbc:mysql://localhost:3306/db_tabungan";
        String user = "root"; // username bawaan Laragon
        String password = ""; // password bawaan Laragon (kosong)

        return DriverManager.getConnection(url, user, password);
    }

    // Method main untuk mengetes apakah koneksi berhasil
    public static void main(String[] args) {
        try {
            Connection c = getConnection();
            System.out.println("HORE! Koneksi ke database berhasil terhubung.");
        } catch (SQLException e) {
            System.out.println("YAH! Koneksi gagal. Pastikan Laragon sudah menyala dan database db_tabungan sudah dibuat.");
            System.out.println("Detail Error: " + e.getMessage());
        }
    }
}
