package DAO;

import java.sql.*;
import java.util.ArrayList;
import connectDB.DBconnection;

public class ThongKe_DAO {

    private Connection conn;

    public ThongKe_DAO() {
        conn = DBconnection.getInstance().getConnection();
    }

    // ==========================================================
    // 0. LẤY NGÀY NHỎ NHẤT & LỚN NHẤT TRONG HÓA ĐƠN
    // ==========================================================
    public Date getMinDate() {
        String sql = "SELECT MIN(ngayLap) FROM HoaDon";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getDate(1) != null)
                return rs.getDate(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return Date.valueOf("2000-01-01");
    }

    public Date getMaxDate() {
        String sql = "SELECT MAX(ngayLap) FROM HoaDon";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getDate(1) != null)
                return rs.getDate(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return new Date(System.currentTimeMillis());
    }

    // ==========================================================
    // 1. DOANH THU THEO NGÀY
    // ==========================================================
    public ArrayList<Object[]> getDoanhThuTheoNgay(Date from, Date to) {
        ArrayList<Object[]> list = new ArrayList<>();

        String sql = """
            SELECT 
                CONVERT(date, hd.ngayLap) AS ngay,
                COUNT(DISTINCT hd.maHD) AS soDon,
                SUM(ct.soLuongSP * ct.donGia) AS doanhThu,
                CASE 
                    WHEN COUNT(DISTINCT hd.maHD) = 0 THEN 0 
                    ELSE SUM(ct.soLuongSP * ct.donGia) * 1.0 /
                         COUNT(DISTINCT hd.maHD)
                END AS avgDon
            FROM HoaDon hd
            JOIN ChiTietHD ct ON hd.maHD = ct.maHD
            WHERE hd.ngayLap BETWEEN ? AND ?
            GROUP BY CONVERT(date, hd.ngayLap)
            ORDER BY ngay ASC
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, from);
            ps.setDate(2, to);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getDate("ngay"),
                        rs.getInt("soDon"),
                        rs.getDouble("doanhThu"),
                        rs.getDouble("avgDon")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ==========================================================
    // 2. TOP SẢN PHẨM BÁN CHẠY
    // ==========================================================
    public ArrayList<Object[]> getTopSanPham(Date from, Date to) {
        ArrayList<Object[]> list = new ArrayList<>();

        String sql = """
            SELECT 
                sp.maSP, 
                sp.tenSP,
                SUM(ct.soLuongSP) AS slBan,
                SUM(ct.soLuongSP * ct.donGia) AS doanhThu
            FROM HoaDon hd
            JOIN ChiTietHD ct ON hd.maHD = ct.maHD
            JOIN SanPham sp ON sp.maSP = ct.maSP
            WHERE hd.ngayLap BETWEEN ? AND ?
            GROUP BY sp.maSP, sp.tenSP
            HAVING SUM(ct.soLuongSP) > 0
            ORDER BY slBan DESC, doanhThu DESC
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, from);
            ps.setDate(2, to);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getString("maSP"),
                        rs.getString("tenSP"),
                        rs.getInt("slBan"),
                        rs.getDouble("doanhThu")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ==========================================================
    // 3. TỒN KHO
    // ==========================================================
    public ArrayList<Object[]> getTonKho() {
        ArrayList<Object[]> list = new ArrayList<>();

        String sql = """
            SELECT maSP, tenSP, soLuong 
            FROM SanPham 
            ORDER BY soLuong ASC
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int sl = rs.getInt("soLuong");
                String warn;

                if (sl == 0) warn = "Hết hàng!";
                else if (sl < 10) warn = "Sắp hết!";
                else warn = "";

                list.add(new Object[]{
                        rs.getString("maSP"),
                        rs.getString("tenSP"),
                        sl,
                        warn
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
