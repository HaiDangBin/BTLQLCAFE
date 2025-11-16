package Entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import connectDB.DBconnection;

public class KhachHang {
    private String maKH;
    private String tenKH;
    private String sDT;
    private String email;

    public KhachHang() {}

    public KhachHang(String maKH, String tenKH, String sDT, String email) {
        this.maKH = maKH;
        this.tenKH = tenKH;
        this.sDT = sDT;
        this.email = email;
    }
    public KhachHang(String maKH, String tenKH, String sDT) {
        this.maKH = maKH;
        this.tenKH = tenKH;
        this.sDT = sDT;
        this.email = null; 
    }

    public String getMaKH() { return maKH; }
    public String getTenKH() { return tenKH; }
    public String getsDT() { return sDT; }
    public String getEmail() { return email; }

    public void setMaKH(String maKH) { this.maKH = maKH; }
    public void setTenKH(String tenKH) { this.tenKH = tenKH; }
    public void setsDT(String sDT) { this.sDT = sDT; }
    public void setEmail(String email) { this.email = email; }
    public boolean createKhachHang(KhachHang kh) {
        String sql = """
            INSERT INTO KhachHang (maKH, tenKH, sDT)
            VALUES (?, ?, ?)
        """;

        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, kh.getMaKH());
            ps.setString(2, kh.getTenKH());
            ps.setString(3, kh.getsDT());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}

