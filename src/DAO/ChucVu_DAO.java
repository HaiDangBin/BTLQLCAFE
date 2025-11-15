package DAO;

import java.sql.*;
import java.util.ArrayList;

import connectDB.DBconnection;
import Entity.ChucVu;

public class ChucVu_DAO {

    public ArrayList<ChucVu> getAll() {
        ArrayList<ChucVu> list = new ArrayList<>();
        try {
            Connection con = DBconnection.getConnection();
            String sql = "SELECT * FROM LoaiChucVu";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new ChucVu(
                        rs.getString("maLoai"),
                        rs.getString("tenLoai")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean add(ChucVu cv) {
        try {
            Connection con = DBconnection.getConnection();
            String sql = "INSERT INTO LoaiChucVu VALUES (?,?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, cv.getMaLoai());
            ps.setString(2, cv.getTenLoai());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(ChucVu cv) {
        try {
            Connection con = DBconnection.getConnection();
            String sql = "UPDATE LoaiChucVu SET tenLoai=? WHERE maLoai=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, cv.getTenLoai());
            ps.setString(2, cv.getMaLoai());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String ma) {
        try {
            Connection con = DBconnection.getConnection();
            String sql = "DELETE FROM LoaiChucVu WHERE maLoai=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, ma);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
