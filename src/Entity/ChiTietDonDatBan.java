package Entity;

public class ChiTietDonDatBan {
	private DonDatBan maDatBan;
	private Ban maBan;
	private SanPham maSP;
	private int soLuongSP;
	private double donGia;
	private String ghiChu;
	private String trangThai;
	public ChiTietDonDatBan(DonDatBan maDatBan, Ban maBan, SanPham maSP, int soLuongSP, double donGia, String ghiChu,
			String trangThai) {
		super();
		this.maDatBan = maDatBan;
		this.maBan = maBan;
		this.maSP = maSP;
		this.soLuongSP = soLuongSP;
		this.donGia = donGia;
		this.ghiChu = ghiChu;
		this.trangThai = trangThai;
	}
	public DonDatBan getMaDatBan() {
		return maDatBan;
	}
	public void setMaDatBan(DonDatBan maDatBan) {
		this.maDatBan = maDatBan;
	}
	public Ban getMaBan() {
		return maBan;
	}
	public void setMaBan(Ban maBan) {
		this.maBan = maBan;
	}
	public SanPham getMaSP() {
		return maSP;
	}
	public void setMaSP(SanPham maSP) {
		this.maSP = maSP;
	}
	public int getSoLuongSP() {
		return soLuongSP;
	}
	public void setSoLuongSP(int soLuongSP) {
		this.soLuongSP = soLuongSP;
	}
	public double getDonGia() {
		return donGia;
	}
	public void setDonGia(double donGia) {
		this.donGia = donGia;
	}
	public String getGhiChu() {
		return ghiChu;
	}
	public void setGhiChu(String ghiChu) {
		this.ghiChu = ghiChu;
	}
	public String getTrangThai() {
		return trangThai;
	}
	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}
	
	
}
