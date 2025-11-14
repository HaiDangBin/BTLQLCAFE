package Entity;

public class SanPham {
    private String maSP;
    private String tenSP;
    private String moTa;
    private double gia;
    private int soLuong;
    private LoaiSanPham maLoai;
    private String hinhAnh;

    
	
	public SanPham(String maSP, String tenSP, String moTa, double gia, int soLuong, LoaiSanPham maLoai,
			String hinhAnh) {
		super();
		this.maSP = maSP;
		this.tenSP = tenSP;
		this.moTa = moTa;
		this.gia = gia;
		this.soLuong = soLuong;
		this.maLoai = maLoai;
		this.hinhAnh = hinhAnh;
	}
	
	


	public String getMaSP() {
		return maSP;
	}




	public void setMaSP(String maSP) {
		this.maSP = maSP;
	}




	public String getTenSP() {
		return tenSP;
	}




	public void setTenSP(String tenSP) {
		this.tenSP = tenSP;
	}




	public String getMoTa() {
		return moTa;
	}




	public void setMoTa(String moTa) {
		this.moTa = moTa;
	}




	public double getGia() {
		return gia;
	}




	public void setGia(double gia) {
		this.gia = gia;
	}




	public int getSoLuong() {
		return soLuong;
	}




	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}




	public LoaiSanPham getMaLoai() {
		return maLoai;
	}




	public void setMaLoai(LoaiSanPham maLoai) {
		this.maLoai = maLoai;
	}




	public String getHinhAnh() {
		return hinhAnh;
	}




	public void setHinhAnh(String hinhAnh) {
		this.hinhAnh = hinhAnh;
	}




	// Phương thức tiện ích để hiển thị lên JTable
    public Object[] toArray() {
        return new Object[]{maSP, tenSP, moTa, gia, soLuong, maLoai, hinhAnh};
    }
}
