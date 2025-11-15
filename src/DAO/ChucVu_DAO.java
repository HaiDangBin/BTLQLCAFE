package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import Entity.ChucVu; 
import connectDB.DBconnection;

public class ChucVu_DAO { 

	public ChucVu findById(String maLoai) {
        ChucVu loaiCV = null;       
        String sql = "SELECT maLoai, tenLoai FROM LoaiChucVu WHERE maLoai = ?"; 
        Connection con = DBconnection.getConnection(); 
        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maLoai);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                   
                    String ma = rs.getString("maLoai");
                    String ten = rs.getString("tenLoai");
                    loaiCV = new ChucVu(ma, ten); 
                }
            } 
            
        } catch (Exception e) {
            e.printStackTrace();
        }
      
        
        return loaiCV;
    }
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