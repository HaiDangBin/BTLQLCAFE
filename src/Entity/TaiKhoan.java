package Entity;

public class TaiKhoan {
    private String tenDN;
    private String matKhau;
    private String vaiTro;

    // Constructor mặc định
    public TaiKhoan() {}

    // Constructor đầy đủ — cái này cực kỳ quan trọng cho DAO
    public TaiKhoan(String tenDN, String matKhau, String vaiTro) {
        this.tenDN = tenDN;
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
    }

    // Getter và Setter
    public String getTenDN() {
        return tenDN;
    }

    public void setTenDN(String tenDN) {
        this.tenDN = tenDN;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    @Override
    public String toString() {
        return "TaiKhoan [tenDN=" + tenDN + ", vaiTro=" + vaiTro + "]";
    }
}
