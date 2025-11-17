package Entity;


import java.sql.Time;
import java.time.LocalDateTime;

public class DonDatBan {
	private String maDatBan;
	private java.sql.Timestamp ngayDat;
	private int soLuongKhach;
	private String trangThai;
	private KhachHang maKH;
	private NhanVien maNV;
	private Ban ban;
	public DonDatBan(String maDatBan, java.sql.Timestamp ngayDat, int soLuongKhach, String trangThai, KhachHang maKH, NhanVien maNV) {
		super();
		this.maDatBan = maDatBan;
		this.ngayDat = ngayDat;
		this.soLuongKhach = soLuongKhach;
		this.trangThai = trangThai;
		this.maKH = maKH;
		this.maNV = maNV;
	}
	
	public DonDatBan() {
		
	}
	public Ban getBan() {
        return ban;
    }
    public void setBan(Ban ban) {
        this.ban = ban;
    }

	public String getMaDatBan() {
		return maDatBan;
	}
	public void setMaDatBan(String maDatBan) {
		this.maDatBan = maDatBan;
	}
	public java.sql.Timestamp getNgayDat() {
		return ngayDat;
	}
	public void setNgayDat(java.sql.Timestamp ngayDat) {
		this.ngayDat = ngayDat;
	}
	public int getSoLuongKhach() {
		return soLuongKhach;
	}
	public void setSoLuongKhach(int soLuongKhach) {
		this.soLuongKhach = soLuongKhach;
	}
	public String getTrangThai() {
		return trangThai;
	}
	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}
	public KhachHang getMaKH() {
		return maKH;
	}
	public void setMaKH(KhachHang maKH) {
		this.maKH = maKH;
	}
	public NhanVien getMaNV() {
		return maNV;
	}
	public void setMaNV(NhanVien maNV) {
		this.maNV = maNV;
	}
	
	
}
