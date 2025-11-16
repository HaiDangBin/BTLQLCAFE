package DAO;

import java.sql.*;
import java.util.ArrayList;

import connectDB.DBconnection;
import Entity.ChucVu;
import Entity.NhanVien;

public class NhanVien_DAO {

    public ArrayList<NhanVien> getAll() {
        ArrayList<NhanVien> list = new ArrayList<>();

        try {
            Connection con = DBconnection.getConnection();
            String sql = """
                    SELECT nv.*, cv.tenLoai 
                    FROM NhanVien nv 
                    JOIN LoaiChucVu cv ON nv.maLoai = cv.maLoai
                    """;

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ChucVu cv = new ChucVu(
                        rs.getString("maLoai"),
                        rs.getString("tenLoai")
                );

                NhanVien nv = new NhanVien(
                        rs.getString("maNV"),
                        rs.getString("tenNV"),
                        rs.getString("sDT"),
                        rs.getString("diaChi"),
                        rs.getString("ngaySinh"),
                        cv
                );

                list.add(nv);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean add(NhanVien nv) {
        try {
            Connection con = DBconnection.getConnection();
            String sql = "INSERT INTO NhanVien VALUES (?,?,?,?,?,?)";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nv.getMaNV());
            ps.setString(2, nv.getTenNV());
            ps.setString(3, nv.getSdt());
            ps.setString(4, nv.getDiaChi());
            ps.setString(5, nv.getNgaySinh());
            ps.setString(6, nv.getChucVu().getMaLoai());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(NhanVien nv) {
        try {
            Connection con = DBconnection.getConnection();
            String sql = """
                UPDATE NhanVien SET 
                    tenNV=?, sDT=?, diaChi=?, ngaySinh=?, maLoai=?
                WHERE maNV=?
                """;

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, nv.getTenNV());
            ps.setString(2, nv.getSdt());
            ps.setString(3, nv.getDiaChi());
            ps.setString(4, nv.getNgaySinh());
            ps.setString(5, nv.getChucVu().getMaLoai());
            ps.setString(6, nv.getMaNV());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String ma) {
        try {
            Connection con = DBconnection.getConnection();
            String sql = "DELETE FROM NhanVien WHERE maNV=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, ma);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
