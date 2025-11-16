package DAO;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

/** DAO cho bảng Thue; kèm tiện ích load tên loại thuế. */
public class Thue_DAO {

    private final Connection conn;

    public Thue_DAO(Connection conn) {
        this.conn = conn;
    }

    /** POJO đơn giản dùng cho GUI. */
    public static class Thue {
        private String maThue;
        private String tenThue;
        private LocalDate ngayApDung;
        private LocalDate ngayKetThuc;
        private String doiTuongApDung;
        private String maLoaiThue;

        public Thue() { }

        public Thue(String maThue, String tenThue, LocalDate ngayApDung, LocalDate ngayKetThuc,
                    String doiTuongApDung, String maLoaiThue) {
            this.maThue = maThue;
            this.tenThue = tenThue;
            this.ngayApDung = ngayApDung;
            this.ngayKetThuc = ngayKetThuc;
            this.doiTuongApDung = doiTuongApDung;
            this.maLoaiThue = maLoaiThue;
        }

        public String getMaThue() { return maThue; }
        public void setMaThue(String maThue) { this.maThue = maThue; }

        public String getTenThue() { return tenThue; }
        public void setTenThue(String tenThue) { this.tenThue = tenThue; }

        public LocalDate getNgayApDung() { return ngayApDung; }
        public void setNgayApDung(LocalDate ngayApDung) { this.ngayApDung = ngayApDung; }

        public LocalDate getNgayKetThuc() { return ngayKetThuc; }
        public void setNgayKetThuc(LocalDate ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }

        public String getDoiTuongApDung() { return doiTuongApDung; }
        public void setDoiTuongApDung(String doiTuongApDung) { this.doiTuongApDung = doiTuongApDung; }

        public String getMaLoaiThue() { return maLoaiThue; }
        public void setMaLoaiThue(String maLoaiThue) { this.maLoaiThue = maLoaiThue; }
    }

    /* ================= CRUD ================= */

    public boolean create(Thue t) throws SQLException {
        String sql = "INSERT INTO Thue(maThue, tenThue, ngayApDung, ngayKetThuc, doiTuongApDung, maLoaiThue) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getMaThue());
            ps.setString(2, t.getTenThue());
            ps.setDate(3, Date.valueOf(t.getNgayApDung()));
            if (t.getNgayKetThuc() != null) ps.setDate(4, Date.valueOf(t.getNgayKetThuc()));
            else ps.setNull(4, Types.DATE);
            ps.setString(5, t.getDoiTuongApDung());
            ps.setString(6, t.getMaLoaiThue());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Thue t) throws SQLException {
        String sql = "UPDATE Thue SET tenThue=?, ngayApDung=?, ngayKetThuc=?, doiTuongApDung=?, maLoaiThue=? WHERE maThue=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getTenThue());
            ps.setDate(2, Date.valueOf(t.getNgayApDung()));
            if (t.getNgayKetThuc() != null) ps.setDate(3, Date.valueOf(t.getNgayKetThuc()));
            else ps.setNull(3, Types.DATE);
            ps.setString(4, t.getDoiTuongApDung());
            ps.setString(5, t.getMaLoaiThue());
            ps.setString(6, t.getMaThue());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(String maThue) throws SQLException {
        String sql = "DELETE FROM Thue WHERE maThue=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maThue);
            return ps.executeUpdate() > 0;
        }
    }

    public Thue findById(String maThue) throws SQLException {
        String sql = "SELECT * FROM Thue WHERE maThue=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maThue);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Thue> findAll() throws SQLException {
        String sql = "SELECT * FROM Thue ORDER BY ngayApDung DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Thue> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        }
    }

    /* =============== Tiện ích =============== */

    /** Lấy map {maLoaiThue -> tên loại} từ bảng LoaiThue.
     * Theo schema của bạn, cột tên là 'tenThue' (không phải 'tenLoaiThue'). */
    public Map<String, String> loadLoaiMap() throws SQLException {
        Map<String, String> map = new HashMap<>();
        String sql = "SELECT maLoaiThue, tenThue FROM LoaiThue"; // <= dùng tenThue như bạn nói
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString(1), rs.getString(2));
            }
        }
        return map;
    }

    /* =============== Helper =============== */

    private Thue map(ResultSet rs) throws SQLException {
        Thue t = new Thue();
        t.setMaThue(rs.getString("maThue"));
        t.setTenThue(rs.getString("tenThue"));
        t.setNgayApDung(rs.getDate("ngayApDung").toLocalDate());
        Date kt = rs.getDate("ngayKetThuc");
        t.setNgayKetThuc(kt != null ? kt.toLocalDate() : null);
        t.setDoiTuongApDung(rs.getString("doiTuongApDung"));
        t.setMaLoaiThue(rs.getString("maLoaiThue"));
        return t;
    }
    
}
