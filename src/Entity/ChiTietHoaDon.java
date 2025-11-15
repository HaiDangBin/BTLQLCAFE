package Entity;

public class ChiTietHoaDon {

    private String maHD;
    private String maSP;
    private int soLuongSP;
    private double donGia;

    public ChiTietHoaDon() {}

    public ChiTietHoaDon(String maHD, String maSP, int soLuongSP, double donGia) {
        this.maHD = maHD;
        this.maSP = maSP;
        this.soLuongSP = soLuongSP;
        this.donGia = donGia;
    }

    public String getMaHD() { return maHD; }
    public void setMaHD(String maHD) { this.maHD = maHD; }

    public String getMaSP() { return maSP; }
    public void setMaSP(String maSP) { this.maSP = maSP; }

    public int getSoLuongSP() { return soLuongSP; }
    public void setSoLuongSP(int soLuongSP) { this.soLuongSP = soLuongSP; }

    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }
}
