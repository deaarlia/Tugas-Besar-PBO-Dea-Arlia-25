import java.sql.*;

public class KoneksiDatabase {
    private static final String URL = "jdbc:mysql://localhost:3306/tbpbo_dea"; // Mapping database
    private static final String USER = "root"; // Username database
    private static final String PASSWORD = ""; // Password database, kosong karena tidak ada
    static { // Static block untuk load driver
        try { // Load MySQL JDBC Driver
           Class.forName("com.mysql.cj.jdbc.Driver"); // Driver MySQL terbaru
       } catch (ClassNotFoundException e) { // Tangani jika driver tidak ditemukan
           System.out.println("MySQL JDBC Driver not found in classpath: " + e.getMessage()); // Pesan error
       } 
   } 
   public static Connection getConnection() throws SQLException { // Method untuk mendapatkan koneksi
       return DriverManager.getConnection(URL, USER, PASSWORD); // Kembalikan koneksi
   } 
   public static void checkConnection() { // Method untuk cek koneksi
       try (Connection conn = getConnection()) { // Coba mengambil koneksi
           System.out.println("Koneksi MySQL berhasil."); // Pesan sukses
       } catch (SQLException e) { // Tangani error koneksi
           System.out.println("Koneksi ke database gagal: " + e.getMessage()); // Pesan error
       } 
   } 
} 