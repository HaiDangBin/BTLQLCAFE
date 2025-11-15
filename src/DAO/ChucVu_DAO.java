package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import Entity.ChucVu; // Bắt buộc phải import Entity tương ứng
import connectDB.DBconnection;

public class ChucVu_DAO { // Đã đổi tên lớp để phản ánh đúng chức năng

	public ChucVu findById(String maLoai) {
        ChucVu loaiCV = null;
        
        String sql = "SELECT maLoai, tenLoai FROM LoaiChucVu WHERE maLoai = ?"; 
        
        // ⭐ Lấy Connection ra khỏi try-with-resources
        Connection con = DBconnection.getConnection(); 

        // ⭐ Chỉ sử dụng try-with-resources cho PreparedStatement và ResultSet
        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maLoai);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Lấy dữ liệu
                    String ma = rs.getString("maLoai");
                    String ten = rs.getString("tenLoai");
                    
                    // Tạo đối tượng Entity ChucVu
                    loaiCV = new ChucVu(ma, ten); 
                }
            } // rs.close() tự động
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        // KHÔNG CÓ con.close() ở đây.
        
        return loaiCV;
    }
}