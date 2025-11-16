package DAO;

import java.sql.*;

import Entity.ChiTietHoaDon;
import connectDB.DBconnection;

public class ChiTietHoaDon_DAO {

    // Tính tổng tiền của hoá đơn
    public double getTongTien(String maHD) {

        double total = 0;

        String sql = """
            SELECT SUM(soLuongSP * donGia)
            FROM ChiTietHD
            WHERE maHD = ?
        """;

        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maHD);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
                total = rs.getDouble(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return total;
    }
 // ===================== THÊM MỚI CHI TIẾT HÓA ĐƠN =====================
    public boolean createChiTietHoaDon(ChiTietHoaDon ct) {
        String sql = """
            INSERT INTO ChiTietHD (maHD, maSP, soLuongSP, donGia)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, ct.getMaHD());
            ps.setString(2, ct.getMaSP());
            ps.setInt(3, ct.getSoLuongSP());
            ps.setDouble(4, ct.getDonGia());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    

}
