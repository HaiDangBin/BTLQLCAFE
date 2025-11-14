package DAO;

import Entity.KhuyenMai;
import connectDB.DBconnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO cho bảng KhuyenMai
 * Cột: maKM (PK), tenKM, moTa, ngayBD, ngayKT, dieuKienApDung
 */
public class KhuyenMai_DAO {

    private final Connection conn;

    public KhuyenMai_DAO(Connection conn) {
        this.conn = conn;
    }
    public KhuyenMai_DAO() {
        // Giả sử bạn có một class/method để lấy Connection (ví dụ: ConnectDB.getConnection())
        this.conn = DBconnection.getConnection(); // 👈 Thay ConnectDB bằng lớp kết nối CSDL của bạn
    }

    /* ===================== CRUD ===================== */

    public List<KhuyenMai> findAll() throws SQLException {
        String sql = "SELECT maKM, tenKM, moTa, ngayBD, ngayKT, dieuKienApDung FROM KhuyenMai";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<KhuyenMai> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        }
    }

    public Optional<KhuyenMai> findById(String maKM) throws SQLException {
        String sql = "SELECT maKM, tenKM, moTa, ngayBD, ngayKT, dieuKienApDung FROM KhuyenMai WHERE maKM = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKM);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public boolean insert(KhuyenMai km) throws SQLException {
        String sql = "INSERT INTO KhuyenMai(maKM, tenKM, moTa, ngayBD, ngayKT, dieuKienApDung) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            fill(ps, km);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(KhuyenMai km) throws SQLException {
        String sql = "UPDATE KhuyenMai SET tenKM = ?, moTa = ?, ngayBD = ?, ngayKT = ?, dieuKienApDung = ? WHERE maKM = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, km.getTenKM());
            ps.setString(2, km.getMoTa());
            ps.setDate(3, toSqlDate(km.getNgayBD()));
            ps.setDate(4, toSqlDate(km.getNgayKT()));
            ps.setString(5, km.getDieuKienApDung());
            ps.setString(6, km.getMaKM());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(String maKM) throws SQLException {
        String sql = "DELETE FROM KhuyenMai WHERE maKM = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKM);
            return ps.executeUpdate() > 0;
        }
    }

    /* ===================== Queries tiện ích ===================== */

    /** Tìm theo từ khóa xuất hiện ở tên hoặc mô tả. */
    public List<KhuyenMai> search(String keyword) throws SQLException {
        String like = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        String sql =
                "SELECT maKM, tenKM, moTa, ngayBD, ngayKT, dieuKienApDung " +
                "FROM KhuyenMai WHERE tenKM LIKE ? OR moTa LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                List<KhuyenMai> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        }
    }

    /** Lấy danh sách KM đang hiệu lực tại ngày chỉ định (mặc định: hôm nay). */
    public List<KhuyenMai> getActive(LocalDate at) throws SQLException {
        if (at == null) at = LocalDate.now();
        // ORDER BY tương thích SQL Server để đưa NULL lên trước
        String sql =
            "SELECT maKM, tenKM, moTa, ngayBD, ngayKT, dieuKienApDung " +
            "FROM KhuyenMai " +
            "WHERE (ngayBD IS NULL OR ngayBD <= ?) AND (ngayKT IS NULL OR ngayKT >= ?) " +
            "ORDER BY CASE WHEN ngayBD IS NULL THEN 0 ELSE 1 END, ngayBD";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            Date d = Date.valueOf(at);
            ps.setDate(1, d);
            ps.setDate(2, d);
            try (ResultSet rs = ps.executeQuery()) {
                List<KhuyenMai> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        }
    }

    /* ===================== Helpers ===================== */

    private KhuyenMai map(ResultSet rs) throws SQLException {
        KhuyenMai km = new KhuyenMai();
        km.setMaKM(rs.getString("maKM"));
        km.setTenKM(rs.getString("tenKM"));
        km.setMoTa(rs.getString("moTa"));
        km.setNgayBD(fromSqlDate(rs.getDate("ngayBD")));
        km.setNgayKT(fromSqlDate(rs.getDate("ngayKT")));
        km.setDieuKienApDung(rs.getString("dieuKienApDung"));
        return km;
    }

    private void fill(PreparedStatement ps, KhuyenMai km) throws SQLException {
        ps.setString(1, km.getMaKM());
        ps.setString(2, km.getTenKM());
        ps.setString(3, km.getMoTa());
        ps.setDate(4, toSqlDate(km.getNgayBD()));
        ps.setDate(5, toSqlDate(km.getNgayKT()));
        ps.setString(6, km.getDieuKienApDung());
    }

    private static Date toSqlDate(LocalDate d) { return d == null ? null : Date.valueOf(d); }
    private static LocalDate fromSqlDate(Date d) { return d == null ? null : d.toLocalDate(); }
}
