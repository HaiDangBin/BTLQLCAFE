package connectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBconnection {
    private static Connection con = null;
    private static DBconnection instance = new DBconnection();

    // Singleton pattern
    public static DBconnection getInstance() {
        return instance;
    }

    // Hàm kết nối SQL Server
    public static void connect() {
        try {
            // 🔧 Kết nối bằng SQL Authentication (user + password)
            String url = "jdbc:sqlserver://localhost\\SQLEXPRESS:1433;"
                       + "databaseName=CAFFE;"
                       + "encrypt=true;"
                       + "trustServerCertificate=true;";
            String user = "sa";          
            String password = "123456789";  

            // Nạp driver SQL Server
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Thực hiện kết nối
            con = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Kết nối SQL Server thành công (SQL Authentication)!");

        } catch (SQLException e) {
            System.err.println("❌ Kết nối SQL Server thất bại:");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Không tìm thấy JDBC Driver cho SQL Server!");
            e.printStackTrace();
        }
    }

    // Ngắt kết nối
    public static void disconnect() {
        if (con != null) {
            try {
                con.close();
                System.out.println("🔌 Đã ngắt kết nối SQL Server.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Lấy kết nối hiện tại
    public static Connection getConnection() {
        try {
            if (con == null || con.isClosed()) {
                connect(); // ✅ Gọi kết nối tự động nếu chưa có
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

	

}
