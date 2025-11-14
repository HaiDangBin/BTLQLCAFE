package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import connectDB.DBconnection;
import Entity.LoaiSanPham;
import Entity.SanPham;

public class SanPham_DAO {
	private final Connection conn;
	public SanPham_DAO() {
        conn = DBconnection.getInstance().getConnection();
    }
	public ArrayList<SanPham> getAllSanPham() {
        ArrayList<SanPham> ds = new ArrayList<>();
        // SỬA: Không gọi DBconnection.getConnection() nữa, dùng biến conn
        String sql = "SELECT m.maSP, m.tenSP, m.moTa, m.gia, m.soLuong, m.hinhAnh, "
                + "l.maLoai AS maLoai, l.tenLoai AS tenLoai "
                + "FROM SanPham m "
                + "JOIN LoaiSanPham l ON m.maLoai = l.maLoai";
        if (conn == null) return ds; // Kiểm tra kết nối

        try (PreparedStatement stmt = conn.prepareStatement(sql); // SỬA: Dùng biến conn
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                LoaiSanPham loai = new LoaiSanPham(
                    rs.getString("maLoai"),
                    rs.getString("tenLoai")
                );
                SanPham sp = new SanPham(
                    rs.getString("maSP"),
                    rs.getString("tenSP"),
                    rs.getString("moTa"),
                    rs.getDouble("gia"),
                    rs.getInt("soLuong"),
                    loai,
                    rs.getString("hinhAnh")
                );
                ds.add(sp);
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi SQL khi lấy danh sách sản phẩm: " + e.getMessage());
            e.printStackTrace();
        }
        return ds;
    }
	public SanPham getSanPhamByMaSP(String maMon) {
        String sql = """
            SELECT m.maSP, m.tenSP, m.moTa, m.gia,m.soLuong, m.hinhAnh,
                   l.maLoai, l.tenLoai
            FROM SanPham m
            JOIN LoaiSanPham l ON m.maLoai = l.maLoai
            WHERE m.maSP = ?
        """;
        // SỬA: Bỏ DriverManager, dùng biến conn
        if (conn == null) {
            System.out.println("❌ Lỗi kết nối CSDL khi tìm sản phẩm.");
            return null;
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maMon);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LoaiSanPham loaiSP = new LoaiSanPham(
                        rs.getString("maLoai"),
                        rs.getString("tenLoai")
                    );
                    return new SanPham(
                        rs.getString("maSP"),
                        rs.getString("tenSP"),
                        rs.getString("moTa"),
                        rs.getDouble("gia"),
                        rs.getInt("soLuong"),
                        loaiSP,
                        rs.getString("hinhAnh")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi SQL khi tìm sản phẩm theo mã: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
	public ArrayList<SanPham> getMonAnByLoaiMon(String maLoai) {
        ArrayList<SanPham> ds = new ArrayList<>();
        String sql = """
            SELECT m.maSP, m.tenSP, m.moTa, m.gia, m.soLuong, m.hinhAnh,
                   l.maLoai, l.tenLoai
            FROM SanPham m
            JOIN LoaiSanPham l ON m.maLoai = l.maLoai
            WHERE m.maLoai = ?
        """;
        
        if (conn == null) return ds; // SỬA: Dùng biến conn

        try (PreparedStatement stmt = conn.prepareStatement(sql)) { // SỬA: Dùng biến conn
            stmt.setString(1, maLoai);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LoaiSanPham loai = new LoaiSanPham(
                        rs.getString("maLoai"),
                        rs.getString("tenLoai")
                    );
                    SanPham sp = new SanPham(
                        rs.getString("maSP"),
                        rs.getString("tenSP"),
                        rs.getString("moTa"),
                        rs.getDouble("gia"),
                        rs.getInt("soLuong"),
                        loai,
                        rs.getString("hinhAnh")
                    );
                    ds.add(sp);
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi SQL khi tìm món ăn theo loại: " + e.getMessage());
            e.printStackTrace();
        }
        return ds;
    }
	public boolean themSanPham(SanPham sp) {
        // SỬA: Xóa bỏ các lệnh gọi DBconnection
        if (conn == null) {
            System.out.println("❌ Lỗi kết nối CSDL khi thêm sản phẩm.");
            return false;
        }

        String sql = "INSERT INTO SanPham (maSP, tenSP, moTa, gia, soLuong,  maLoai, hinhAnh) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) { // SỬA: Dùng biến conn
            stmt.setString(1, sp.getMaSP());
            stmt.setString(2, sp.getTenSP());
            stmt.setString(3, sp.getMoTa());
            stmt.setDouble(4, sp.getGia());
            stmt.setInt(5, sp.getSoLuong());
            stmt.setString(6, sp.getMaLoai().getMaLoai());
            stmt.setString(7, sp.getHinhAnh());

            int n = stmt.executeUpdate();
            return n > 0;
        } catch (Exception e) {
            System.out.println("❌ Lỗi SQL khi thêm món ăn: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
	public boolean capNhat(SanPham sp) {
        String sql = "UPDATE SanPham SET tenSP = ?, moTa = ?, gia = ?, soLuong = ?, maLoai = ?, hinhAnh = ? WHERE maSP = ?";
        // SỬA: Bỏ DriverManager, dùng biến conn
        if (conn == null) {
            System.out.println("❌ Lỗi kết nối CSDL khi cập nhật món ăn.");
            return false;
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sp.getTenSP());
            stmt.setString(2, sp.getMoTa());
            stmt.setDouble(3, sp.getGia());
            stmt.setInt(4, sp.getSoLuong());
            stmt.setString(5, sp.getMaLoai().getMaLoai());
            stmt.setString(6, sp.getHinhAnh());
            stmt.setString(7, sp.getMaSP());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("❌ Lỗi SQL khi cập nhật món ăn: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
	public boolean xoaSP(String maSP) {
        String sql = "DELETE FROM SanPham WHERE maSP = ?";
        // SỬA: Bỏ DriverManager, dùng biến conn
        if (conn == null) {
            System.out.println("❌ Lỗi kết nối CSDL khi xóa món ăn.");
            return false;
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maSP);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("❌ Lỗi SQL khi xóa sản phẩm: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
