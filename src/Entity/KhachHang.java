package Entity;

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
}
