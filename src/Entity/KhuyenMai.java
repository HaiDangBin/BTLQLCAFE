package Entity;

import java.time.LocalDate;

public class KhuyenMai {
    private String maKM;
    private String tenKM;
    private String moTa;
    private LocalDate ngayBD;
    private LocalDate ngayKT;
    private String dieuKienApDung;

    public KhuyenMai() {
    }

    public KhuyenMai(String maKM, String tenKM, String moTa, LocalDate ngayBD, LocalDate ngayKT, String dieuKienApDung) {
        this.maKM = maKM;
        this.tenKM = tenKM;
        this.moTa = moTa;
        this.ngayBD = ngayBD;
        this.ngayKT = ngayKT;
        this.dieuKienApDung = dieuKienApDung;
    }

    public String getMaKM() {
        return maKM;
    }

    public void setMaKM(String maKM) {
        this.maKM = maKM;
    }

    public String getTenKM() {
        return tenKM;
    }

    public void setTenKM(String tenKM) {
        this.tenKM = tenKM;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public LocalDate getNgayBD() {
        return ngayBD;
    }

    public void setNgayBD(LocalDate ngayBD) {
        this.ngayBD = ngayBD;
    }

    public LocalDate getNgayKT() {
        return ngayKT;
    }

    public void setNgayKT(LocalDate ngayKT) {
        this.ngayKT = ngayKT;
    }

    public String getDieuKienApDung() {
        return dieuKienApDung;
    }

    public void setDieuKienApDung(String dieuKienApDung) {
        this.dieuKienApDung = dieuKienApDung;
    }

    @Override
    public String toString() {
        return maKM + " - " + tenKM;
    }
}
