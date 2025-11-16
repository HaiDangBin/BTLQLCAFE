package Entity;

import java.time.LocalDate;

public class Thue {
    private String maThue;
    private String tenThue;
    private LocalDate ngayApDung;
    private LocalDate ngayKetThuc;
    private String doiTuongApDung;
    private String loaiThue;

    public Thue() {
    }

    public Thue(String maThue, String tenThue, LocalDate ngayApDung, LocalDate ngayKetThuc,
                String doiTuongApDung, String loaiThue) {
        this.maThue = maThue;
        this.tenThue = tenThue;
        this.ngayApDung = ngayApDung;
        this.ngayKetThuc = ngayKetThuc;
        this.doiTuongApDung = doiTuongApDung;
        this.loaiThue = loaiThue;
    }

    public String getMaThue() {
        return maThue;
    }

    public void setMaThue(String maThue) {
        this.maThue = maThue;
    }

    public String getTenThue() {
        return tenThue;
    }

    public void setTenThue(String tenThue) {
        this.tenThue = tenThue;
    }

    public LocalDate getNgayApDung() {
        return ngayApDung;
    }

    public void setNgayApDung(LocalDate ngayApDung) {
        this.ngayApDung = ngayApDung;
    }

    public LocalDate getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public String getDoiTuongApDung() {
        return doiTuongApDung;
    }

    public void setDoiTuongApDung(String doiTuongApDung) {
        this.doiTuongApDung = doiTuongApDung;
    }

    public String getLoaiThue() {
        return loaiThue;
    }

    public void setLoaiThue(String loaiThue) {
        this.loaiThue = loaiThue;
    }

    @Override
    public String toString() {
        return "Thue{" +
                "maThue='" + maThue + '\'' +
                ", tenThue='" + tenThue + '\'' +
                ", ngayApDung=" + ngayApDung +
                ", ngayKetThuc=" + ngayKetThuc +
                ", doiTuongApDung='" + doiTuongApDung + '\'' +
                ", loaiThue='" + loaiThue + '\'' +
                '}';
    }
}
