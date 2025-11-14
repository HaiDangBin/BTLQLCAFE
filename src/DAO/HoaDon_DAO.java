package DAO;

import java.sql.*;
import java.util.ArrayList;
import Entity.HoaDon;
import connectDB.DBconnection;

public class HoaDon_DAO {
    private Connection conn;

    public HoaDon_DAO() {
        conn = DBconnection.getConnection();
    }

    public ArrayList<HoaDon> getAllHoaDon() {
        ArrayList<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                HoaDon hd = new HoaDon(
                    rs.getString("maHD"),
                    rs.getDate("ngayLap"),
                    rs.getString("maNV"),
                    rs.getString("maKH"),
                    rs.getDouble("tongTien")
                );
                list.add(hd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addHoaDon(HoaDon hd) {
        String sql = "INSERT INTO HoaDon VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hd.getMaHD());
            ps.setDate(2, hd.getNgayLap());
            ps.setString(3, hd.getMaNV());
            ps.setString(4, hd.getMaKH());
            ps.setDouble(5, hd.getTongTien());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

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
}
