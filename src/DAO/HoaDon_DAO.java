package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import Entity.HoaDon;
import connectDB.DBconnection;

public class HoaDon_DAO {
    private Connection conn;

    public HoaDon_DAO() {
        conn = DBconnection.getConnection();
    }

    

//    public boolean addHoaDon(HoaDon hd) {
//        String sql = "INSERT INTO HoaDon VALUES (?, ?, ?, ?, ?)";
//        try (PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, hd.getMaHD());
//            ps.setDate(2, hd.getNgayLap());
//            ps.setString(3, hd.getMaNV());
//            ps.setString(4, hd.getMaKH());
//            ps.setDouble(5, hd.getTongTien());
//            return ps.executeUpdate() > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

    public boolean deleteHoaDon(String maHD) {
        String sql = "DELETE FROM HoaDon WHERE maHD=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHD);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean kiemTraTonTai(String maHD) {
        String sql = "SELECT 1 FROM HoaDon WHERE maHD = ?";
        Connection con = DBconnection.getConnection();
        
        if (con == null) {
            return false; 
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }
//    public boolean themHoaDon(HoaDon hd, DefaultTableModel modelChiTiet) {
//        PreparedStatement psHoaDon = null;
//        PreparedStatement psChiTiet = null;
//        boolean success = false;
//        
//        // SQL cho bảng HoaDon (5 cột: maHD, ngayLap, maNV, maKH, tongTien)
//        String sqlInsertHD = "INSERT INTO HoaDon (maHD, ngayLap, maNV, maKH, tongTien) VALUES (?, ?, ?, ?, ?)";
//        
//        // SQL cho bảng ChiTietHoaDon (Giả định: maHD, maMon, soLuong, donGia, thanhTien)
//        String sqlInsertCTHD = "INSERT INTO ChiTietHoaDon (maHD, maMon, soLuong, donGia, thanhTien) VALUES (?, ?, ?, ?, ?)";
//
//        try {
//            conn.setAutoCommit(false); // BẮT ĐẦU TRANSACTION
//
//            // 1. LƯU THÔNG TIN HÓA ĐƠN CHÍNH
//            psHoaDon = conn.prepareStatement(sqlInsertHD);
//            psHoaDon.setString(1, hd.getMaHD());
//            psHoaDon.setDate(2, hd.getNgayLap());
//            psHoaDon.setString(3, hd.getMaNV());
//            psHoaDon.setString(4, hd.getMaKH());
//            psHoaDon.setDouble(5, hd.getTongTien());
//            
//            psHoaDon.executeUpdate();
//
//            // 2. LƯU THÔNG TIN CHI TIẾT HÓA ĐƠN
//            psChiTiet = conn.prepareStatement(sqlInsertCTHD);
//            String maHD = hd.getMaHD();
//
//            // Lặp qua từng dòng trong modelChiTiet (modelMonAnGoc)
//            for (int i = 0; i < modelChiTiet.getRowCount(); i++) {
//                // Giả định modelMonAnGoc có cấu trúc: MaMon(0), Tên món(1), SL(2), Đơn giá(3), Thành tiền(4)
//                String maMon = modelChiTiet.getValueAt(i, 0).toString();
//                // Ép kiểu an toàn hơn khi lấy từ JTable
//                int soLuong = Integer.parseInt(modelChiTiet.getValueAt(i, 2).toString()); 
//                double donGia = (double) modelChiTiet.getValueAt(i, 3);
//                double thanhTien = (double) modelChiTiet.getValueAt(i, 4);
//
//                psChiTiet.setString(1, maHD);
//                psChiTiet.setString(2, maMon);
//                psChiTiet.setInt(3, soLuong);
//                psChiTiet.setDouble(4, donGia);
//                psChiTiet.setDouble(5, thanhTien);
//                
//                psChiTiet.addBatch(); // Thêm vào batch
//            }
//            
//            psChiTiet.executeBatch(); // Thực thi lưu chi tiết
//            
//            conn.commit(); // THÀNH CÔNG: Commit toàn bộ thay đổi
//            success = true;
//
//        } catch (SQLException e) {
//            try {
//                if (conn != null)
//                    conn.rollback(); // THẤT BẠI: Hủy bỏ tất cả thay đổi
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//            }
//            System.err.println("Lỗi CSDL khi thêm hóa đơn và chi tiết: " + e.getMessage());
//            e.printStackTrace();
//        } finally {
//            try {
//                if (psHoaDon != null) psHoaDon.close();
//                if (psChiTiet != null) psChiTiet.close();
//                if (conn != null) conn.setAutoCommit(true); // Trả lại chế độ mặc định
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//            }
//        }
//        return success;
//    }
    @SuppressWarnings("finally")
	public boolean themHoaDon(HoaDon hd, DefaultTableModel modelChiTiet) {
        PreparedStatement psHoaDon = null;
        PreparedStatement psChiTiet = null;
        boolean success = false;
        
        // SỬA LẠI SQL CHO KHỚP VỚI ENTITY HOADON.JAVA
        // (maHD, ngayLap, maNV, maKH, maDatBan, maKM, tongTien)
        String sqlInsertHD = "INSERT INTO HoaDon (maHD, ngayLap, maNV, maKH, maDatBan, maKM, tongTien) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        // SỬA LẠI SQL CHO KHỚP VỚI ENTITY CHITIETHOADON.JAVA
        // (maHD, maSP, soLuongSP, donGia)
        String sqlInsertCTHD = "INSERT INTO ChiTietHoaDon (maHD, maSP, soLuongSP, donGia) VALUES (?, ?, ?, ?)";

        try {
            conn.setAutoCommit(false); // BẮT ĐẦU TRANSACTION

            // 1. LƯU THÔNG TIN HÓA ĐƠN CHÍNH
            psHoaDon = conn.prepareStatement(sqlInsertHD);
            psHoaDon.setString(1, hd.getMaHD());
            psHoaDon.setDate(2, hd.getNgayLap());
            psHoaDon.setString(3, hd.getMaNV());
            psHoaDon.setString(4, hd.getMaKH());
            psHoaDon.setString(5, hd.getMaDatBan()); // THÊM
            psHoaDon.setString(6, hd.getMaKM());     // THÊM
            psHoaDon.setDouble(7, hd.getTongTien()); // VỊ TRÍ THAY ĐỔI
            
            psHoaDon.executeUpdate();

            // 2. LƯU THÔNG TIN CHI TIẾT HÓA ĐƠN
            psChiTiet = conn.prepareStatement(sqlInsertCTHD);
            String maHD = hd.getMaHD();

            for (int i = 0; i < modelChiTiet.getRowCount(); i++) {
                String maMon = modelChiTiet.getValueAt(i, 0).toString(); // (modelMonAnGoc cột 0 là MaMon/MaSP)
                int soLuong = Integer.parseInt(modelChiTiet.getValueAt(i, 2).toString()); 
                double donGia = (double) modelChiTiet.getValueAt(i, 3);
                
                psChiTiet.setString(1, maHD);
                psChiTiet.setString(2, maMon);    // Đảm bảo tên cột là 'maSP'
                psChiTiet.setInt(3, soLuong);     // Đảm bảo tên cột là 'soLuongSP'
                psChiTiet.setDouble(4, donGia);
                
                psChiTiet.addBatch(); 
            }
            
            psChiTiet.executeBatch(); 
            
            conn.commit(); 
            success = true;

        } catch (SQLException e) {
            try {
                if (conn != null)
                    conn.rollback(); 
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Lỗi CSDL khi thêm hóa đơn và chi tiết: " + e.getMessage());
            e.printStackTrace();
        } finally {
        	try {
              if (psHoaDon != null) psHoaDon.close();
              if (psChiTiet != null) psChiTiet.close();
              if (conn != null) conn.setAutoCommit(true); // Trả lại chế độ mặc định
          } catch (SQLException ex) {
              ex.printStackTrace();
        }
        return success;
    }
   }
    public int getSoDonHomNay() {
        int soDon = 0;

        String sql = """
            SELECT COUNT(*)
            FROM HoaDon
            WHERE CAST(ngayLap AS DATE) = CAST(GETDATE() AS DATE)
        """;

        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next())
                soDon = rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return soDon;
    }
    public List<String[]> getHoaDonGanNhat() {
        List<String[]> list = new ArrayList<>();

        String sql = """
            SELECT HoaDon.maHD, NhanVien.tenNV, HoaDon.ngayLap
            FROM HoaDon
            JOIN NhanVien ON HoaDon.maNV = NhanVien.maNV
            ORDER BY ngayLap DESC
        """;

        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("maHD"),
                    rs.getString("tenNV"),
                    rs.getString("ngayLap")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    public List<HoaDon> getAll() {
        List<HoaDon> list = new ArrayList<>();

        String sql = "SELECT * FROM HoaDon ORDER BY ngayLap DESC";

        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new HoaDon(
                    rs.getString("maHD"),
                    rs.getString("maNV"),
                    rs.getString("maKH"),
                    rs.getString("maKM"),
                    rs.getString("maDatBan"),
                    rs.getDate("ngayLap")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    public boolean insert(HoaDon hd) {
        String sql = """
            INSERT INTO HoaDon (maHD, ngayLap, maKH, maNV, maKM, maDatBan)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
        	System.out.println("=== DEBUG INSERT HOA DON ===");
        	System.out.println("maHD = " + hd.getMaHD());
        	System.out.println("ngayLap = " + hd.getNgayLap());
        	System.out.println("maKH = " + hd.getMaKH());
        	System.out.println("maNV = " + hd.getMaNV());
        	System.out.println("maKM = " + hd.getMaKM());
        	System.out.println("maDatBan = " + hd.getMaDatBan());

            ps.setString(1, hd.getMaHD());
            ps.setDate(2, hd.getNgayLap());
            ps.setString(3, hd.getMaKH());
            ps.setString(4, hd.getMaNV());
            ps.setString(5, hd.getMaKM());
            ps.setString(6, hd.getMaDatBan());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
        	e.printStackTrace();  // IN RA CONSOLE
            JOptionPane.showMessageDialog(null, 
                "SQL ERROR INSERT HOADON:\n" + e.getMessage());
        }

        return false;
    }
    public boolean update(HoaDon hd) {
        String sql = """
            UPDATE HoaDon SET
            ngayLap = ?, maKH = ?, maNV = ?, maKM = ?, maDatBan = ?
            WHERE maHD = ?
        """;

        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, hd.getNgayLap());
            ps.setString(2, hd.getMaKH());
            ps.setString(3, hd.getMaNV());
            ps.setString(4, hd.getMaKM());
            ps.setString(5, hd.getMaDatBan());
            ps.setString(6, hd.getMaHD());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    public boolean delete(String maHD) {
        String sql = "DELETE FROM HoaDon WHERE maHD = ?";

        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maHD);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    public HoaDon findById(String maHD) {
        String sql = "SELECT * FROM HoaDon WHERE maHD = ?";

        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maHD);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new HoaDon(
                    rs.getString("maHD"),
                    rs.getString("maNV"),
                    rs.getString("maKH"),
                    rs.getString("maKM"),
                    rs.getString("maDatBan"),
                    rs.getDate("ngayLap")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public String generateMaKH() {
        String sql = "SELECT TOP 1 maKH FROM HoaDon ORDER BY maKH DESC";

        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String last = rs.getString("maKH").trim(); // KH015
                int num = Integer.parseInt(last.substring(2)) + 1;
                return String.format("KH%03d", num);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "KH001";
    }
    public HoaDon getHoaDonByMa(String maHD) {
        String sql = "SELECT * FROM HoaDon WHERE maHD = ?";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maHD);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                HoaDon hd = new HoaDon(
                    rs.getString("maHD"),
                    rs.getString("maNV"),
                    rs.getString("maKH"),
                    rs.getString("maKM"),
                    rs.getString("maDatBan"),
                    rs.getDate("ngayLap")
                );
                return hd;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi lấy hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    public boolean insert1(HoaDon hd) {
        String sql = """
            INSERT INTO HoaDon (maHD, ngayLap, maKH, maNV, maKM, maDatBan)
            VALUES (?, ?, ?, ?, ?, ?)
        """; // Không có tongTien

        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hd.getMaHD());
            ps.setDate(2, hd.getNgayLap());
            ps.setString(3, hd.getMaKH());
            ps.setString(4, hd.getMaNV());
            ps.setString(5, hd.getMaKM());
            ps.setString(6, hd.getMaDatBan());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
