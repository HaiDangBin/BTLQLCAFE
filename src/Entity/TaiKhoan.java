package Entity;

public class TaiKhoan {

    private String maTK;
    private String tenDN;
    private String matKhau;
    private String vaiTro;
    private boolean trangThai;
    private String maNV;

    // Thêm liên kết
    private NhanVien nhanVien;
    private ChucVu chucVu;

    public TaiKhoan(String maTK, String tenDN, String matKhau, 
                    String vaiTro, boolean trangThai, String maNV) {

        this.maTK = maTK;
        this.tenDN = tenDN;
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
        this.trangThai = trangThai;
        this.maNV = maNV;
    }

    public TaiKhoan() {}

    // setter để gán NV + ChucVu
    public void setNhanVien(NhanVien nv) { this.nhanVien = nv; }
    public void setChucVu(ChucVu cv) { this.chucVu = cv; }

    public NhanVien getNhanVien() { return nhanVien; }
    public ChucVu getChucVu() { return chucVu; }
}
