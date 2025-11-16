package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Entity.Ban;
import connectDB.DBconnection; // Dùng DBconnection của bạn

public class Ban_DAO {


    // Phương thức lấy tất cả bàn từ DB
    public List<Ban> getAllBan() {
        List<Ban> danhSachBan = new ArrayList<>();
        Connection con = DBconnection.getInstance().getConnection();
        
        try {
            String sql = "SELECT MaBan, viTri, SucChua, trangThai FROM Ban"; 
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            
            while (rs.next()) {
                String maBan = rs.getString("MaBan");
                String viTri = rs.getString("viTri");
                int sucChua = rs.getInt("SucChua");
                String trangThai = rs.getString("TrangThai");
                
                // THÊM ĐIỀU KIỆN XỬ LÝ NULL NGAY TẠI ĐÂY

             // Đảm bảo logic này được thêm:
             if (trangThai == null || trangThai.trim().isEmpty()) {
                 trangThai = "Lỗi dữ liệu"; // Gán giá trị chuỗi hợp lệ (không null)
             }

             Ban ban = new Ban(maBan, viTri ,sucChua, trangThai);
                danhSachBan.add(ban);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return danhSachBan;
    }
 // Trong DAO.Ban_DAO.java, thay thế phương thức getBansByCondition()

    public List<Ban> getBansByCondition(String maBan, String trangThai, int sucChua) {
        List<Ban> danhSachBan = new ArrayList<>();
        Connection con = DBconnection.getInstance().getConnection();
        
        try {
            // [SỬA] Đảm bảo tên cột chính xác và viết hoa (MaBan, SucChua, TrangThai)
            StringBuilder sql = new StringBuilder("SELECT MaBan, viTri ,SucChua, TrangThai FROM Ban WHERE 1=1");

            // 1. Lọc theo Mã bàn
            if (maBan != null && !maBan.trim().isEmpty()) {
                sql.append(" AND MaBan LIKE N'%" + maBan + "%'");
            }

            // 2. Lọc theo Trạng thái
            if (trangThai != null && !trangThai.equalsIgnoreCase("Tất cả")) {
                sql.append(" AND TrangThai = N'" + trangThai + "'");
            }
            
            // 3. Lọc theo Sức chứa (Số người)
            if (sucChua > 0) {
                sql.append(" AND SucChua = " + sucChua);
            }

            Statement statement = con.createStatement();
            System.out.println("SQL truy vấn bàn: " + sql.toString()); // DEBUG
            ResultSet rs = statement.executeQuery(sql.toString());
            
            while (rs.next()) {
                // [SỬA] Đảm bảo tên cột khớp với câu lệnh SELECT
                String bMaBan = rs.getString("MaBan");
                String bViTri = rs.getString("viTri");
                int bSucChua = rs.getInt("SucChua");
                String bTrangThai = rs.getString("TrangThai");
                
                // Xử lý null/empty

             // Đảm bảo logic này được thêm:
             if (trangThai == null || trangThai.trim().isEmpty()) {
                 trangThai = "Lỗi dữ liệu"; // Gán giá trị chuỗi hợp lệ (không null)
             }

             Ban ban = new Ban(bMaBan, bViTri ,bSucChua, bTrangThai);
                danhSachBan.add(ban);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return danhSachBan;
    }
    public boolean updateTrangThaiBan(String maBan, String trangThaiMoi) {
        Connection con = DBconnection.getInstance().getConnection();
        PreparedStatement stmt = null;
        String sql = "UPDATE Ban SET trangThai = ? WHERE maBan = ?";
        
        try {
            stmt = con.prepareStatement(sql);
            stmt.setString(1, trangThaiMoi);
            stmt.setString(2, maBan);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public Ban layBanTheoMa(String maBan) { 
        Ban ban = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        // ⭐ BƯỚC 1: LẤY KẾT NỐI MỚI (Đồng bộ với các hàm khác)
        Connection con = DBconnection.getInstance().getConnection(); 
        
        if (con == null) {
            // Xử lý lỗi nếu không lấy được kết nối
            System.err.println("Lỗi: Không lấy được kết nối CSDL từ DBconnection.");
            return null;
        }
        
        try {
            String sql = "SELECT maBan, viTri, sucChua, trangThai FROM Ban WHERE maBan = ?";
            
            // ⭐ BƯỚC 2: SỬ DỤNG 'con' (biến cục bộ) thay vì 'this.connection'
            ps = con.prepareStatement(sql);
            ps.setString(1, maBan);
            rs = ps.executeQuery();

            if (rs.next()) {
                String viTri = rs.getString("viTri");
                int sucChua = rs.getInt("sucChua");
                String trangThai = rs.getString("trangThai");
                
                ban = new Ban(maBan, viTri, sucChua, trangThai); 
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace(); 
        } finally {
            // Không cần đóng 'con' vì nó là Singleton
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (java.sql.SQLException ex) {
                ex.printStackTrace();
            }
        }
        return ban;
    }
    public Ban getBanByMaDDB(String maBan) {
        Ban ban = null;
        Connection con = DBconnection.getInstance().getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        // Cần chỉnh sửa SQL theo cấu trúc bảng 'Ban' thực tế của bạn
        String sql = "SELECT maBan, viTri ,sucChua, trangThai FROM Ban WHERE maBan = ?"; 

        try {
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maBan);
            rs = stmt.executeQuery();

            if (rs.next()) {
                ban = new Ban();
                ban.setMaBan(rs.getString("maBan"));
                ban.setViTri(rs.getString("viTri"));
                ban.setSucChua(rs.getInt("sucChua")); // Giả định có cột sucChua
                ban.setTrangThai(rs.getString("trangThai"));
                
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thông tin Bàn theo mã: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Đóng tài nguyên
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignored */ }
        }
        return ban;
    }
}