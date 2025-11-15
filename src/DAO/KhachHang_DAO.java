package DAO;

import java.sql.*;
import java.util.ArrayList;
import connectDB.DBconnection;
import Entity.KhachHang;

public class KhachHang_DAO {
    private Connection conn;

    public KhachHang_DAO() {
        conn = DBconnection.getConnection();
    }

    public ArrayList<KhachHang> getAllKH() {
        ArrayList<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new KhachHang(
                        rs.getString("maKH"),
                        rs.getString("tenKH"),
                        rs.getString("sDT"),
                        rs.getString("eMail")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addKH(KhachHang kh) {
        String sql = "INSERT INTO KhachHang VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kh.getMaKH());
            ps.setString(2, kh.getTenKH());
            ps.setString(3, kh.getsDT());
            ps.setString(4, kh.getEmail());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteKH(String maKH) {
        String sql = "DELETE FROM KhachHang WHERE maKH = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKH);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public KhachHang findKhachHangBySDT(String sdt) {
        KhachHang kh = null;
        Connection con = DBconnection.getInstance().getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM KhachHang WHERE sDT = ?";
        try {
            stmt = con.prepareStatement(sql);
            stmt.setString(1, sdt);
            rs = stmt.executeQuery();
            if (rs.next()) {
                kh = new KhachHang();
                kh.setMaKH(rs.getString("maKH"));
                kh.setTenKH(rs.getString("tenKH"));
                kh.setsDT(rs.getString("sDT"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } 
        // Không đóng connection ở đây nếu nó được quản lý bởi Singleton
        return kh;
    }

    // Tạo KH mới
    public boolean createKhachHang(KhachHang kh) {
        Connection con = DBconnection.getInstance().getConnection();
        PreparedStatement stmt = null;
        String sql = "INSERT INTO KhachHang(maKH, tenKH, sDT) VALUES (?, ?, ?)";
        try {
            stmt = con.prepareStatement(sql);
            stmt.setString(1, kh.getMaKH());
            stmt.setString(2, kh.getTenKH());
            stmt.setString(3, kh.getsDT());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Hàm tự động tạo mã KH (ví dụ)
    public String phatSinhMaKH() {
        // Logic tạo mã mới, ví dụ: "KH" + timestamp hoặc "KH" + số thứ tự
        return "KH" + System.currentTimeMillis() % 100000; // Mã ví dụ
    }
    public KhachHang getKhachHangByMa(String maKH) {
        KhachHang kh = null;
        
        // ⭐ Lấy Connection. KHÔNG đặt Connection trong try-with-resources
        // để tránh đóng kết nối khi nó là kết nối Singleton dùng chung.
        Connection con = DBconnection.getConnection(); 
        
        // ⭐ SQL: Truy vấn dựa trên maKH
        String sql = "SELECT maKH, tenKH, sDT, eMail FROM KhachHang WHERE maKH = ?";
        
        // Sử dụng try-with-resources cho PreparedStatement và ResultSet
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            
            // Bỏ kiểm tra con.isClosed() và lấy lại connection vì DBconnection phải đảm bảo điều này.

            stmt.setString(1, maKH);
            
            // Dùng try-with-resources lồng nhau cho ResultSet
            try (ResultSet rs = stmt.executeQuery()) { 
                if (rs.next()) {
                    // ⭐ Tạo đối tượng KhachHang đầy đủ từ ResultSet
                    kh = new KhachHang(
                        rs.getString("maKH"),
                        rs.getString("tenKH"),
                        rs.getString("sDT"),
                        rs.getString("eMail")
                    );
                }
            } // rs.close() được gọi tự động
            
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi tìm Khách hàng theo Mã: " + e.getMessage());
            e.printStackTrace();
        } 
        // KHÔNG CÓ finally và KHÔNG CÓ con.close()
        
        return kh;
    }
}
