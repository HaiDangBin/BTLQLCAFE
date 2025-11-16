package DAO;

import java.sql.*;
import connectDB.DBconnection;
import Entity.*;

public class TaiKhoan_DAO {

    public TaiKhoan dangNhap(String tenDN, String matKhau) {

        String sql = """
            SELECT tk.*, 
                   nv.tenNV, nv.sDT, nv.diaChi, nv.ngaySinh, nv.maLoai,
                   lcv.tenLoai
            FROM TaiKhoan tk
            JOIN NhanVien nv ON tk.maNV = nv.maNV
            JOIN LoaiChucVu lcv ON nv.maLoai = lcv.maLoai
            WHERE tk.tenDN = ? AND tk.matKhau = ?
        """;

        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tenDN);
            ps.setString(2, matKhau);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                // ===================== TÀI KHOẢN ======================
                TaiKhoan tk = new TaiKhoan(
                        rs.getString("maTK"),
                        rs.getString("tenDN"),
                        rs.getString("matKhau"),
                        rs.getString("vaiTro"),
                        rs.getBoolean("trangThai"),
                        rs.getString("maNV")
                );

                // ===================== CHỨC VỤ ======================
                ChucVu cv = new ChucVu(
                        rs.getString("maLoai"),
                        rs.getString("tenLoai")
                );

                // ===================== NHÂN VIÊN ======================
                NhanVien nv = new NhanVien(
                        rs.getString("maNV"),
                        rs.getString("tenNV"),
                        rs.getString("sDT"),
                        rs.getString("diaChi"),
                        rs.getString("ngaySinh"),
                        cv
                );

                tk.setNhanVien(nv);
                tk.setChucVu(cv);

                return tk;
            }

        } catch (Exception e) {
            System.out.println("❌ Lỗi đăng nhập:");
            e.printStackTrace();
        }
        return null;
    }
}
