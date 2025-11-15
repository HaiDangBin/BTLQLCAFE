package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import connectDB.DBconnection;

public class ChiTietHoaDon_DAO {
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
}
