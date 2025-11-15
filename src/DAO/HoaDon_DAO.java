package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Entity.HoaDon;
import connectDB.DBconnection;

public class HoaDon_DAO {

    // Lấy số đơn trong ngày
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

    // Lấy hóa đơn mới nhất + nhân viên
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

    // Lấy tất cả hóa đơn
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
                    rs.getString("ngayLap")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // Thêm hóa đơn
    public boolean insert(HoaDon hd) {
        String sql = """
            INSERT INTO HoaDon (maHD, ngayLap, maKH, maNV, maKM, maDatBan)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, hd.getMaHD());
            ps.setString(2, hd.getNgayLap());
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

    // Sửa hóa đơn
    public boolean update(HoaDon hd) {
        String sql = """
            UPDATE HoaDon SET
            ngayLap = ?, maKH = ?, maNV = ?, maKM = ?, maDatBan = ?
            WHERE maHD = ?
        """;

        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, hd.getNgayLap());
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

    // Xóa hóa đơn
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

    // Tìm hóa đơn theo mã
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
                    rs.getString("ngayLap")
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

}
