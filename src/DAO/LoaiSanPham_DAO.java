package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import Entity.LoaiSanPham;
import connectDB.DBconnection; // ⭐ Đảm bảo đã import lớp kết nối

public class LoaiSanPham_DAO {

    private Connection con;

    public LoaiSanPham_DAO() {
        con = DBconnection.getInstance().getConnection();
    }
    public String phatSinhMaLoai() {
        String maLoai = "LSP01"; 
        String sql = "SELECT MAX(maLoai) FROM LoaiSanPham";
        
        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                String maxMa = rs.getString(1);
                if (maxMa != null) {
                    // Lấy phần số từ mã lớn nhất (ví dụ: "08" từ "LSP08")
                    int num = Integer.parseInt(maxMa.substring(3)) + 1; 
                    
                    // ⭐ KIỂM TRA GIỚI HẠN (2 ký tự số)
                    if (num > 99) {
                        JOptionPane.showMessageDialog(null, 
                            "Lỗi phát sinh mã: Đã vượt quá giới hạn 99 loại sản phẩm (LSP99).", 
                            "Lỗi Mã Loại", JOptionPane.ERROR_MESSAGE);
                        return null; 
                    }
                    
                    // ⭐ ĐỊNH DẠNG MỚI: sử dụng %02d (2 ký tự số)
                    maLoai = String.format("LSP%02d", num); 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Lỗi định dạng mã loại sản phẩm: " + e.getMessage());
            e.printStackTrace();
        }
        return maLoai;
    }


    public boolean themLoaiSanPham(LoaiSanPham loaiSP) {
        String sql = "INSERT INTO LoaiSanPham (maLoai, tenLoai) VALUES (?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, loaiSP.getMaLoai());
            stmt.setNString(2, loaiSP.getTenLoai()); 
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            if (e.getMessage().contains("PRIMARY KEY")) {
                System.err.println("Lỗi: Mã loại đã tồn tại.");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }
    public ArrayList<LoaiSanPham> getAllLoaiSanPham() {
        ArrayList<LoaiSanPham> dsLoai = new ArrayList<>();
        
        String sql = "SELECT maLoai, tenLoai FROM LoaiSanPham ORDER BY tenLoai"; 
        
        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                String maLoai = rs.getString("maLoai");
                String tenLoai = rs.getString("tenLoai");
                
                LoaiSanPham loaiSP = new LoaiSanPham(maLoai, tenLoai);
                dsLoai.add(loaiSP);
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi CSDL khi lấy danh sách Loại Sản Phẩm:");
            e.printStackTrace();
        }
        return dsLoai;
    }
}