package DAO;

import java.sql.*;
import java.util.ArrayList;
import Entity.NhanVien;
import connectDB.DBconnection;

public class NhanVien_DAO {
    private Connection conn;

    public NhanVien_DAO() {
        conn = DBconnection.getConnection();
    }

    public ArrayList<NhanVien> getAllNhanVien() {
        ArrayList<NhanVien> list = new ArrayList<>();
        String sql = "SELECT maNV, tenNV, sDT, maLoai FROM NhanVien";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                NhanVien nv = new NhanVien(
                    rs.getString("maNV"),
                    rs.getString("tenNV"),
                    rs.getString("sDT"),
                    rs.getString("maLoai")
                );
                list.add(nv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public NhanVien getNhanVienByMaNV(String maNV) {
        NhanVien nv = null;
        Connection con = DBconnection.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Lấy các trường thông tin cần thiết của Nhân viên
            String sql = "SELECT maNV, tenNV, sDT, maLoai FROM NhanVien WHERE maNV = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maNV);
            
            rs = stmt.executeQuery();

            if (rs.next()) {
                // ⭐ KHỞI TẠO ĐỐI TƯỢNG NHANVIEN
                // Lưu ý: Cần đảm bảo constructor này phù hợp với Entity NhanVien của bạn
                nv = new NhanVien(
                    rs.getString("maNV"),
                    rs.getString("tenNV"),
                    rs.getString("sDT"),
                    rs.getString("maLoai")
                    // Thêm các trường khác nếu Entity NhanVien của bạn có nhiều hơn
                );
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thông tin Nhân viên: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return nv;
    }
}

