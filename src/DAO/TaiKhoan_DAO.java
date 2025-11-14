package DAO;

import java.sql.*;
import connectDB.DBconnection;
import Entity.TaiKhoan;

public class TaiKhoan_DAO {

    // Hàm kiểm tra đăng nhập
    public TaiKhoan checkLogin(String username, String password) {
        TaiKhoan tk = null;

        try {
            Connection conn = DBconnection.getConnection();
            String sql = "SELECT * FROM TaiKhoan WHERE tenDN = ? AND matKhau = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                tk = new TaiKhoan(
                    rs.getString("tenDN"),
                    rs.getString("matKhau"),
                    rs.getString("vaiTro")
                );
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tk; // nếu null => login sai
    }
}

