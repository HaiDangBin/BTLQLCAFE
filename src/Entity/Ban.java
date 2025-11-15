package Entity;

import java.awt.Color;

public class Ban {
	private String maBan;
	private String viTri;
	private int sucChua;
	private String trangThai;
	public Ban(String maBan, String viTri, int sucChua, String trangThai) {
		super();
		this.maBan = maBan;
		this.viTri = viTri;
		this.sucChua = sucChua;
		this.trangThai = trangThai;
	}
	
	public Ban() {
		super();
	}
	

	public Ban(String maBan) {
		super();
		this.maBan = maBan;
	}

	public String getMaBan() {
		return maBan;
	}
	public void setMaBan(String maBan) {
		this.maBan = maBan;
	}
	public String getViTri() {
		return viTri;
	}
	public void setViTri(String viTri) {
		this.viTri = viTri;
	}
	public int getSucChua() {
		return sucChua;
	}
	public void setSucChua(int sucChua) {
		this.sucChua = sucChua;
	}
	public String getTrangThai() {
		return trangThai;
	}
	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}
	
	public Color getMauTheoTrangThai() {
	    // BƯỚC 1: KIỂM TRA NULL AN TOÀN TRƯỚC KHI DÙNG SWITCH
	    if (this.trangThai == null || this.trangThai.trim().isEmpty()) {
	        return Color.GRAY; // Trả về màu Xám mặc định cho bàn có trạng thái không hợp lệ/null
	    }

	    // BƯỚC 2: CHỈ CHẠY SWITCH KHI ĐÃ CHẮC CHẮN GIÁ TRỊ LÀ CHUỖI KHÔNG NULL
	    switch (this.trangThai) {
	        case "Trống":
	            return new Color(150, 220, 255); // Màu xanh dương nhạt
	        case "Có khách":
	            return new Color(102, 0, 0); // Màu vàng
	        case "Đặt trước":
	        	return Color.YELLOW; // Màu hồng nhạt/cam nhạt
	        default:
	            return Color.LIGHT_GRAY; // Màu cho các trạng thái không xác định
	    }
	}
}
