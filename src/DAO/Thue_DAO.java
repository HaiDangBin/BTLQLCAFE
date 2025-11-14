package DAO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Thue_DAO {

    private final Connection conn;

    public Thue_DAO(Connection conn) {
        this.conn = conn;
    }

    public static class Thue {
        private String maThue;
        private String tenThue;
        private LocalDate ngayApDung;
        private LocalDate ngayKetThuc;
        private String doiTuongApDung;
        private String loaiThue;

        public Thue() { }

        public Thue(String maThue, String tenThue, LocalDate ngayApDung, LocalDate ngayKetThuc,
                    String doiTuongApDung, String loaiThue) {
            this.maThue = maThue;
            this.tenThue = tenThue;
            this.ngayApDung = ngayApDung;
            this.ngayKetThuc = ngayKetThuc;
            this.doiTuongApDung = doiTuongApDung;
            this.loaiThue = loaiThue;
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
        public String getLoaiThue() { return loaiThue; }
        public void setLoaiThue(String loaiThue) { this.loaiThue = loaiThue; }
    }

    public boolean create(Thue t) throws SQLException {
        String sql = "INSERT INTO Thue(maThue, tenThue, ngayApDung, ngayKetThuc, doiTuongApDung, loaiThue) VALUES (?,?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, t.getMaThue());
        ps.setString(2, t.getTenThue());
        ps.setDate(3, Date.valueOf(t.getNgayApDung()));
        if (t.getNgayKetThuc() != null) ps.setDate(4, Date.valueOf(t.getNgayKetThuc()));
        else ps.setNull(4, Types.DATE);
        ps.setString(5, t.getDoiTuongApDung());
        ps.setString(6, t.getLoaiThue());
        return ps.executeUpdate() > 0;
    }

    public boolean update(Thue t) throws SQLException {
        String sql = "UPDATE Thue SET tenThue=?, ngayApDung=?, ngayKetThuc=?, doiTuongApDung=?, loaiThue=? WHERE maThue=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, t.getTenThue());
        ps.setDate(2, Date.valueOf(t.getNgayApDung()));
        if (t.getNgayKetThuc() != null) ps.setDate(3, Date.valueOf(t.getNgayKetThuc()));
        else ps.setNull(3, Types.DATE);
        ps.setString(4, t.getDoiTuongApDung());
        ps.setString(5, t.getLoaiThue());
        ps.setString(6, t.getMaThue());
        return ps.executeUpdate() > 0;
    }

    public boolean delete(String maThue) throws SQLException {
        String sql = "DELETE FROM Thue WHERE maThue=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, maThue);
        return ps.executeUpdate() > 0;
    }

    public Thue findById(String maThue) throws SQLException {
        String sql = "SELECT * FROM Thue WHERE maThue=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, maThue);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return map(rs);
        return null;
    }

    public List<Thue> findAll() throws SQLException {
        String sql = "SELECT * FROM Thue ORDER BY ngayApDung DESC";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<Thue> list = new ArrayList<>();
        while (rs.next()) list.add(map(rs));
        return list;
    }

    private Thue map(ResultSet rs) throws SQLException {
        Thue t = new Thue();
        t.setMaThue(rs.getString("maThue"));
        t.setTenThue(rs.getString("tenThue"));
        t.setNgayApDung(rs.getDate("ngayApDung").toLocalDate());
        Date kt = rs.getDate("ngayKetThuc");
        t.setNgayKetThuc(kt != null ? kt.toLocalDate() : null);
        t.setDoiTuongApDung(rs.getString("doiTuongApDung"));
        t.setLoaiThue(rs.getString("loaiThue"));
        return t;
    }
}
