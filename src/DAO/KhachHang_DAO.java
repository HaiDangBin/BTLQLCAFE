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

    // Lấy danh sách khách hàng
    public ArrayList<KhachHang> getAllKH() {
        ArrayList<KhachHang> list = new ArrayList<>();
        String sql = "SELECT maKH, tenKH, sDT, eMail FROM KhachHang";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                KhachHang kh = new KhachHang(
                        rs.getString("maKH"),
                        rs.getString("tenKH"),
                        rs.getString("sDT"),
                        rs.getString("eMail")
                );
                list.add(kh);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thêm khách hàng
    public boolean addKH(KhachHang kh) {
        String sql = "INSERT INTO KhachHang(maKH, tenKH, sDT, eMail) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kh.getMaKH());
            ps.setString(2, kh.getTenKH());
            ps.setString(3, kh.getsDT());
            ps.setString(4, kh.getEmail());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa khách hàng
    public boolean deleteKH(String maKH) {
        String sql = "DELETE FROM KhachHang WHERE maKH = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKH);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật khách hàng
    public boolean updateKH(KhachHang kh) {
        String sql = "UPDATE KhachHang SET tenKH=?, sDT=?, eMail=? WHERE maKH=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kh.getTenKH());
            ps.setString(2, kh.getsDT());
            ps.setString(3, kh.getEmail());
            ps.setString(4, kh.getMaKH());
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public String phatSinhMaKH() {
        String sql = "SELECT MAX(maKH) AS maxMa FROM KhachHang";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String max = rs.getString("maxMa");
                if (max == null) return "KH001";

                int num = Integer.parseInt(max.substring(2)) + 1;
                return String.format("KH%03d", num);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "KH001";
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
                // KHÔNG set email -> nullable
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kh;
    }
 // ===================== PHÁT SINH MÃ KHÁCH HÀNG =====================
    public String generateMaKH() {
        String sql = "SELECT TOP 1 maKH FROM KhachHang ORDER BY maKH DESC";
        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String last = rs.getString("maKH").trim(); // ví dụ: KH015
                int num = Integer.parseInt(last.substring(2)) + 1;
                return String.format("KH%03d", num);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "KH001";
    }
    public boolean insert(KhachHang kh) {
        String sql = """
            INSERT INTO KhachHang (maKH, tenKH, sDT, email)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, kh.getMaKH());
            ps.setString(2, kh.getTenKH());
            ps.setString(3, kh.getsDT());
            ps.setString(4, kh.getEmail());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public KhachHang getKhachHangByMa(String maKH) {
        String sql = "SELECT * FROM KhachHang WHERE maKH = ?";
        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKH);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new KhachHang(
                    rs.getString("maKH"),
                    rs.getString("tenKH"),
                    rs.getString("sDT"),
                    rs.getString("email")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
