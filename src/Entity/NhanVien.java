package Entity;

import java.sql.Date;

public class NhanVien {
    private String maNV;
    private String tenNV;
    private String sdt;
    private String diaChi;
    private Date ngaySinh;
    private ChucVu chucVu;

    public NhanVien(String maNV, String tenNV, String sdt, String diaChi, Date ngaySinh, ChucVu chucVu) {
        this.maNV = maNV;
        this.tenNV = tenNV;
        this.sdt = sdt;
        this.diaChi = diaChi;
        this.ngaySinh = ngaySinh;
        this.chucVu = chucVu;
    }
    

    public NhanVien(String maNV) {
		super();
		this.maNV = maNV;
	}


	public NhanVien() {}

    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }

    public String getTenNV() { return tenNV; }
    public void setTenNV(String tenNV) { this.tenNV = tenNV; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public Date getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(Date ngaySinh) { this.ngaySinh = ngaySinh; }

    public ChucVu getChucVu() { return chucVu; }
    public void setChucVu(ChucVu chucVu) { this.chucVu = chucVu; }
}
