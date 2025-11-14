package Entity;

import java.sql.Date;

public class HoaDon {
    private String maHD;
    private Date ngayLap;
    private String maNV;
    private String maKH;
    private double tongTien;

    public HoaDon() {}

    public HoaDon(String maHD, Date ngayLap, String maNV, String maKH, double tongTien) {
        this.maHD = maHD;
        this.ngayLap = ngayLap;
        this.maNV = maNV;
        this.maKH = maKH;
        this.tongTien = tongTien;
    }

    public String getMaHD() { return maHD; }
    public Date getNgayLap() { return ngayLap; }
    public String getMaNV() { return maNV; }
    public String getMaKH() { return maKH; }
    public double getTongTien() { return tongTien; }

    public void setMaHD(String maHD) { this.maHD = maHD; }
    public void setNgayLap(Date ngayLap) { this.ngayLap = ngayLap; }
    public void setMaNV(String maNV) { this.maNV = maNV; }
    public void setMaKH(String maKH) { this.maKH = maKH; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }

    @Override
    public String toString() {
        return maHD + " - " + ngayLap + " - " + tongTien;
    }
}
