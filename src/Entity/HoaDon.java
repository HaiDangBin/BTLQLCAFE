package Entity;

public class HoaDon {
    private String maHD;
    private String maNV;
    private String maKM;
    private String maDatBan;
    private String maKH;
    private String ngayLap;

    public HoaDon() {}

    public HoaDon(String maHD, String maNV, String maKH, String maKM, String maDatBan, String ngayLap) {
        this.maHD = maHD;
        this.maNV = maNV;
        this.maKM = maKM;
        this.maDatBan = maDatBan;
        this.maKH = maKH;
        this.ngayLap = ngayLap;
    }

    public String getMaHD() { return maHD; }
    public void setMaHD(String maHD) { this.maHD = maHD; }

    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }

    public String getMaKM() { return maKM; }
    public void setMaKM(String maKM) { this.maKM = maKM; }

    public String getMaDatBan() { return maDatBan; }
    public void setMaDatBan(String maDatBan) { this.maDatBan = maDatBan; }

    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { this.maKH = maKH; }

    public String getNgayLap() { return ngayLap; }
    public void setNgayLap(String ngayLap) { this.ngayLap = ngayLap; }
}
