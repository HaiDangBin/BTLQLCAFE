package Entity;

public class NhanVien {
    private String maNV;
    private String tenNV;
    private String sDT;
    private String maLoai;

    public NhanVien() {}

    public NhanVien(String maNV, String tenNV, String sDT, String maLoai) {
        this.maNV = maNV;
        this.tenNV = tenNV;
        this.sDT = sDT;
        this.maLoai = maLoai;
    }

    public String getMaNV() { return maNV; }
    public String getTenNV() { return tenNV; }
    public String getSdt() { return sDT; }
    public String getMaLoai() { return maLoai; }

    public void setMaNV(String maNV) { this.maNV = maNV; }
    public void setTenNV(String tenNV) { this.tenNV = tenNV; }
    public void setSdt(String sDT) { this.sDT = sDT; }
    public void setMaLoai(String maLoai) { this.maLoai = maLoai; }
}
