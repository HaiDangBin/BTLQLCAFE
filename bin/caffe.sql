-- ===================================
-- TẠO DATABASE
-- ===================================
CREATE DATABASE CAFFE;
GO
USE CAFFE;
GO

-- ===================================
-- BẢNG LOẠI CHỨC VỤ
-- ===================================
CREATE TABLE LoaiChucVu (
    maLoai VARCHAR(10) PRIMARY KEY,
    tenLoai NVARCHAR(100)
);
GO

-- ===================================
-- BẢNG NHÂN VIÊN
-- ===================================
CREATE TABLE NhanVien (
    maNV VARCHAR(10) PRIMARY KEY,
    tenNV NVARCHAR(100),
    sDT VARCHAR(15),
    diaChi NVARCHAR(200),
    ngaySinh DATE,
    maLoai VARCHAR(10),
    FOREIGN KEY (maLoai) REFERENCES LoaiChucVu(maLoai)
);
GO

-- ===================================
-- BẢNG TÀI KHOẢN
-- ===================================
CREATE TABLE TaiKhoan (
    maTK VARCHAR(10) PRIMARY KEY,
    tenDN VARCHAR(50) UNIQUE,
    matKhau VARCHAR(255),
    vaiTro NVARCHAR(50),
    trangThai BIT,
    maNV VARCHAR(10) NOT NULL,
    FOREIGN KEY (maNV) REFERENCES NhanVien(maNV)
);
GO



-- ===================================
-- BẢNG KHÁCH HÀNG
-- ===================================
CREATE TABLE KhachHang (
    maKH VARCHAR(10) PRIMARY KEY,
    tenKH NVARCHAR(100),
    sDT VARCHAR(15),
    eMail VARCHAR(100)
);
GO

-- ===================================
-- BẢNG BÀN
-- ===================================
CREATE TABLE Ban (
    maBan VARCHAR(10) PRIMARY KEY,
    viTri NVARCHAR(100),
    sucChua INT,
    trangThai NVARCHAR(20)
);
GO
-- ===================================
-- BẢNG LOẠI SẢN PHẨM 
-- ===================================
CREATE TABLE LoaiSanPham (
    maLoai VARCHAR(10) PRIMARY KEY,
    tenLoai NVARCHAR(100)
);
GO

-- ===================================
-- BẢNG SẢN PHẨM
-- ===================================
CREATE TABLE SanPham (
    maSP VARCHAR(10) PRIMARY KEY, 
    tenSP NVARCHAR(100),          
    moTa NVARCHAR(200),           
    gia DECIMAL(12,2),            
    soLuong INT,                  
    maLoai VARCHAR(10),
    hinhAnh NVARCHAR(255),
    FOREIGN KEY (maLoai) REFERENCES LoaiSanPham(maLoai)
);
Go

-- ===================================
-- BẢNG KHUYẾN MÃI
-- ===================================
CREATE TABLE KhuyenMai (
    maKM VARCHAR(10) PRIMARY KEY,
    tenKM NVARCHAR(100),
    moTa NVARCHAR(200),
    ngayBD DATE,
    ngayKT DATE,
    dieuKienApDung NVARCHAR(200)
);
GO

CREATE TABLE LoaiThue (
    maLoaiThue VARCHAR(10) PRIMARY KEY,
    tenThue NVARCHAR(100)
);

-- ===================================
-- BẢNG THUẾ
-- ===================================
CREATE TABLE Thue (
    maThue VARCHAR(10) PRIMARY KEY,
    tenThue NVARCHAR(100),
    mucThue DECIMAL(5,2),         
    ngayApDung DATE,
    ngayKetThuc DATE,
    doiTuongApDung NVARCHAR(100),
    maLoaiThue VARCHAR(10),
    FOREIGN KEY (maLoaiThue) REFERENCES LoaiThue(maLoaiThue)
);
GO

-- ===================================
-- BẢNG ĐƠN ĐẶT BÀN
-- ===================================
CREATE TABLE DonDatBan (
    maDatBan VARCHAR(10) PRIMARY KEY,
    ngayDat DATE,
    soLuongKhach INT,
    trangThai NVARCHAR(20),
    maKH VARCHAR(10),
    maNV VARCHAR(10),
    FOREIGN KEY (maKH) REFERENCES KhachHang(maKH),
    FOREIGN KEY (maNV) REFERENCES NhanVien(maNV)
);
GO

-- ===================================
-- BẢNG CHI TIẾT ĐƠN ĐẶT BÀN
-- ===================================
CREATE TABLE ChiTietDonDatBan (
    maDatBan VARCHAR(10),
    maBan VARCHAR(10),
    maSP VARCHAR(10),
    soLuongSP INT,
    donGia DECIMAL(12,2),
    ghiChu NVARCHAR(200),
    trangThai NVARCHAR(20),
    PRIMARY KEY (maDatBan,maBan, maSP),
    FOREIGN KEY (maDatBan) REFERENCES DonDatBan(maDatBan),
    FOREIGN KEY (maBan) REFERENCES Ban(maBan),
    FOREIGN KEY (maSP) REFERENCES SanPham(maSP)
);
GO

-- ===================================
-- BẢNG HÓA ĐƠN
-- ===================================
CREATE TABLE HoaDon (
    maHD VARCHAR(10) PRIMARY KEY,
    ngayLap DATE,
    trangThai NVARCHAR(20),
    maKH VARCHAR(10),
    maNV VARCHAR(10),
    maKM VARCHAR(10),
    -- maDatBan có thể NULL nếu hóa đơn không qua bước đặt bàn (ví dụ: mua mang về)
    maDatBan VARCHAR(10) NULL, 
    FOREIGN KEY (maKH) REFERENCES KhachHang(maKH),
    FOREIGN KEY (maNV) REFERENCES NhanVien(maNV),
    FOREIGN KEY (maKM) REFERENCES KhuyenMai(maKM),
    FOREIGN KEY (maDatBan) REFERENCES DonDatBan(maDatBan)
);
GO

-- ===================================
-- BẢNG CHI TIẾT HÓA ĐƠN
-- ===================================
CREATE TABLE ChiTietHD (
    maHD VARCHAR(10),
    maSP VARCHAR(10),
    soLuongSP INT,
    donGia DECIMAL(12,2),
    ghiChu NVARCHAR(200),
    maThue VARCHAR(10),
    PRIMARY KEY (maHD, maSP),
    FOREIGN KEY (maHD) REFERENCES HoaDon(maHD),
    FOREIGN KEY (maSP) REFERENCES SanPham(maSP),
    FOREIGN KEY (maThue) REFERENCES Thue(maThue)
);
GO



-- ===================================
-- DỮ LIỆU MẪU CHO LOẠI CHỨC VỤ
-- ===================================
INSERT INTO LoaiChucVu (maLoai, tenLoai) VALUES
('L01', N'Nhân viên quản lý'),
('L02', N'Nhân viên phục vụ'),
('L03', N'Nhân viên thu ngân'),
('L04', N'Nhân viên pha chế');
GO

-- ===================================
-- DỮ LIỆU MẪU CHO NHÂN VIÊN
-- ===================================
INSERT INTO NhanVien (maNV, tenNV, sDT, diaChi, ngaySinh, maLoai)
VALUES ('NV01', N'Trương Quốc Tuấn', '0903161320', N'123 Lê Lợi, Q1, TP.HCM', '1995-02-15', 'L01'),
('NV02', N'Trần Tấn Kiệt', '0803461320', N'45 Nguyễn Trãi, Q5, TP.HCM', '1995-02-15', 'L02'),
('NV03', N'Phạm Huỳnh Hương', '0301334457', N'23 Lê Văn Sỹ, Q3, TP.HCM', '1995-02-15', 'L02'),
('NV04', N'Nguyễn Hoàng Khang', '0981244567', N'67 Điện Biên Phủ, Q10, TP.HCM', '1995-02-15', 'L02'),
('NV05', N'Đoàn Ánh Minh', '0939234323', N'23 Trần Hưng Đạo, Q.5, TP.HCM', '1995-02-15', 'L03'),
('NV06', N'Huỳnh Thị Xuân Mai', '0831232565', N'78 Nguyễn Thị Minh Khai, Q.3, TP.HCM', '1995-02-15', 'L03'),
('NV07', N'Lý Tuấn Huy', '0993124567', N'90 Điện Biên Phủ, Q.Bình Thạnh, TP.HCM', '1995-02-15', 'L03'),
('NV08', N'Phạm Đình Tuấn', '0939546787', N'9 Hai Bà Trưng, Q.1, TP.HCM', '1995-02-15', 'L04'),
('NV09', N'Phạm Hồng Hạnh', '0143655324', N'14 Võ Văn Tần, Q.3, TP.HCM', '1995-02-15', 'L02'),
('NV10', N'Lê Minh Phúc', '0787098875', N'22 Lê Văn Sỹ, Q.Phú Nhuận, TP.HCM', '1995-02-15', 'L04');
GO

INSERT INTO LoaiSanPham (maLoai, tenLoai) VALUES
('LSP01', N'Cà Phê'),      
('LSP02', N'Trà sữa'),           
('LSP03', N'Sinh Tố'),        
('LSP04', N'Bánh Ngọt & Ăn Kèm'),       
('LSP05', N'Đá xay'),
('LSP06', N'Nước ép'),
('LSP07', N'Soda'),
('LSP08', N'Trà trái cây');
GO

-- ===================================
-- DỮ LIỆU MẪU CHO SẢN PHẨM
-- ===================================
INSERT INTO SanPham (maSP, tenSP, moTa, gia, soLuong, maLoai, hinhAnh)
VALUES
('SP01', N'Cà phê sữa đá', N'Cà phê pha phin truyền thống', 25000, 120, 'LSP01', N'images/caphe_suada.jpg'),
('SP02', N'Cà phê đen đá', N'Cà phê nguyên chất', 20000, 100, 'LSP01', N'images/caphe_den.jpg'),
('SP03', N'Cappuccino', N'Cà phê Ý bọt sữa', 45000, 80, 'LSP01', N'images/cappuccino.jpg'),
('SP04', N'Latte', N'Cà phê sữa tươi', 45000, 70, 'LSP01', N'images/latte.jpg'),
('SP05', N'Bạc xỉu', N'Cà phê pha sữa đậm', 30000, 90, 'LSP01', N'images/bacxiu.jpg'),
('SP06', N'Trà đào cam sả', N'Trà đào tươi kèm lát cam', 35000, 110, 'LSP08', N'images/tra_dao_cam_sa.jpg'),
('SP07', N'Trà vải hoa hồng', N'Trà hương hoa nhẹ nhàng', 38000, 100, 'LSP08', N'images/tra_vai_hoa_hong.jpg'),
('SP08', N'Trà chanh mật ong', N'Trà chanh mát lạnh', 30000, 90, 'LSP08', N'images/tra_chanh.jpg'),
('SP09', N'Sinh tố bơ', N'Sinh tố bơ Đà Lạt tươi', 40000, 85, 'LSP03', N'images/sinh_to_bo.jpg'),
('SP10', N'Sinh tố dâu', N'Sinh tố từ dâu tươi', 38000, 75, 'LSP03', N'images/sinh_to_dau.jpg'),
('SP11', N'Nước ép cam', N'Nước ép cam nguyên chất', 35000, 120, 'LSP06', N'images/nuoc_ep_cam.jpg'),
('SP12', N'Nước ép táo', N'Nước ép táo Mỹ tươi', 37000, 100, 'LSP06', N'images/nuoc_ep_tao.jpg'),
('SP13', N'Soda chanh', N'Nước soda hương chanh', 30000, 60, 'LSP07', N'images/soda_chanh.jpg'),
('SP14', N'Soda dâu', N'Soda vị dâu ngọt nhẹ', 32000, 50, 'LSP07', N'images/soda_dau.jpg'),
('SP15', N'Bánh tiramisu', N'Bánh Ý hương cà phê', 45000, 40, 'LSP04', N'images/tiramisu.jpg'),
('SP16', N'Bánh mousse dâu', N'Bánh lạnh vị dâu tươi', 42000, 35, 'LSP04', N'images/mousse_dau.jpg'),
('SP17', N'Bánh flan', N'Caramen trứng sữa', 20000, 100, 'LSP04', N'images/flan.jpg'),
('SP18', N'Bánh su kem', N'Bánh su nhân kem vani', 15000, 100, 'LSP04', N'images/su_kem.jpg'),
('SP19', N'Bánh mì bơ tỏi', N'Bánh mì nướng thơm bơ', 25000, 60, 'LSP04', N'images/bo_toi.jpg'),
('SP20', N'Pizza mini', N'Pizza cá nhân nhân phô mai', 50000, 40, 'LSP04', N'images/pizza_mini.jpg'),
('SP21', N'Matcha đá xay', N'Trà xanh matcha Nhật Bản pha cùng sữa và đá xay mịn', 55000, 80, 'LSP05', N'images/matcha_daxay.jpg'),
('SP22', N'Cookies đá xay', N'Sữa tươi kết hợp bánh cookies và kem tươi', 60000, 70, 'LSP05', N'images/cookies_daxay.jpg'),
('SP23', N'Sữa tươi trân châu đường đen', N'Sữa tươi thơm béo kèm trân châu dẻo và đường đen Đài Loan', 55000, 90, 'LSP02', N'images/suatruoi_tcdd.jpg'),
('SP24', N'Trà sữa matcha', N'Trà xanh matcha Nhật Bản kết hợp sữa tươi và trân châu', 50000, 85, 'LSP02', N'images/trasua_matcha.jpg'),
('SP25', N'Trà sữa socola', N'Trà đen và bột cacao, vị ngọt nhẹ', 48000, 100, 'LSP02', N'images/trasua_socola.jpg'),
('SP26', N'Bánh croissant bơ', N'Bánh sừng bò nướng giòn, nhân bơ tan chảy', 35000, 60, 'LSP04', N'images/croissant_bo.jpg'),
('SP31', N'Sinh tố xoài', N'Sinh tố từ xoài chín tự nhiên, hương vị nhiệt đới', 45000, 70, 'LSP03', N'images/sinh_to_xoai.jpg'),
('SP32', N'Sinh tố dưa hấu', N'Sinh tố dưa hấu tươi mát, giải nhiệt mùa hè', 42000, 80, 'LSP03', N'images/sinh_to_dua_hau.jpg'),
('SP33', N'Nước ép cà rốt', N'Nước ép cà rốt tươi nguyên chất, giàu vitamin A', 38000, 90, 'LSP06', N'images/nuoc_ep_carot.jpg'),
('SP34', N'Nước ép ổi', N'Nước ép ổi ép lạnh giữ nguyên vị tự nhiên', 36000, 85, 'LSP06', N'images/nuoc_ep_oi.jpg'),
('SP35', N'Trà hoa cúc mật ong', N'Trà hoa cúc khô pha với mật ong nguyên chất, giúp thư giãn', 40000, 75, 'LSP08', N'images/tra_hoa_cuc.jpg'),
('SP36', N'Trà bạc hà', N'Trà xanh kết hợp lá bạc hà tươi, vị the mát', 38000, 65, 'LSP08', N'images/tra_bac_ha.jpg'),
('SP37', N'Bánh donut socola', N'Bánh vòng phủ socola ngọt nhẹ, thích hợp ăn sáng', 30000, 50, 'LSP04', N'images/donut_socola.jpg'),
('SP38', N'Bánh muffin việt quất', N'Bánh nướng mềm xốp, nhân việt quất tươi', 45000, 45, 'LSP04', N'images/muffin_vietquat.jpg'),
('SP40', N'Yến mạch sữa chua trái cây', N'Món ăn sáng lành mạnh với yến mạch, sữa chua và trái cây tươi', 60000, 40, 'LSP04', N'images/yenmach_suachua.jpg');
GO

-- ===================================
-- DỮ LIỆU MẪU CHO KHÁCH HÀNG
-- ===================================
INSERT INTO KhachHang (maKH, tenKH, sDT, eMail)
VALUES
('KH000', N'Khách vãng lai', '0123456789', 'khachvlai@gmail.com'),
('KH001', N'Nguyễn Thị Mai', '0905123123', 'mainguyen@gmail.com'),
('KH002', N'Lê Văn Nam', '0918123456', 'namle95@gmail.com'),
('KH003', N'Trần Thị Thu', '0939456789', 'thutran2000@yahoo.com'),
('KH004', N'Phạm Hoàng Long', '0906789345', 'longpham94@gmail.com'),
('KH005', N'Đặng Ngọc Hân', '0978123987', 'han.dang01@gmail.com'),
('KH006', N'Võ Minh Tâm', '0912456789', 'minhtam99@gmail.com'),
('KH007', N'Lý Thanh Tú', '0909345123', 'tuly96@gmail.com'),
('KH008', 'Hoàng Thị Hồng', '0938123123', 'honghoang97@gmail.com'),
('KH009', N'Nguyễn Văn Hải', '0906123987', 'hainguyen93@gmail.com'),
('KH010', N'Bùi Thị Lan', '0919234567', 'lanbui02@gmail.com'),
('KH011', N'Trịnh Quốc Khánh', '0908567345', 'khanhtrinh90@gmail.com'),
('KH012', N'Phan Thị Mỹ Duyên', '0935123987', 'duyenmy98@gmail.com'),
('KH013', N'Tạ Quang Minh', '0901234789', 'minhta95@gmail.com'),
('KH014', N'Đoàn Thị Như', '0934345678', 'nhudoan00@gmail.com'),
('KH015', N'Nguyễn Văn Khôi', '0915456789', 'khoinguyen97@gmail.com'),
('KH016', N'Lâm Thị Thu Hằng', '0908567123', 'hanglam01@gmail.com'),
('KH017', N'Trương Đức Anh', '0919123123', 'anhtruong99@gmail.com'),
('KH018', N'Phạm Thị Hoa', '0937456789', 'hoapham94@gmail.com'),
('KH019', N'Lê Thanh Bình', '0908789345', 'binhle89@gmail.com'),
('KH020', N'Trần Mỹ Linh', '0917123987', 'linhtran03@gmail.com');
Go

-- ===================================
-- DỮ LIỆU MẪU CHO KHUYẾN MÃI
-- ===================================
SET DATEFORMAT DMY;

INSERT INTO KhuyenMai (maKM, tenKM, moTa, ngayBD, ngayKT, dieuKienApDung)
VALUES
('KM001', N'Giảm 10% cho khách hàng thân thiết', 
 N'Áp dụng cho tất cả hóa đơn của khách hàng có thẻ thành viên thân thiết.', 
 '01/01/2025', '31/03/2025', 
 N'Khách hàng có thẻ thành viên thân thiết'),

('KM002', N'Mua 2 tặng 1 cà phê', 
 N'Khi mua 2 ly cà phê bất kỳ sẽ được tặng 1 ly cà phê đen miễn phí.', 
 '01/02/2025', '30/04/2025', 
 N'Áp dụng cho đơn hàng có từ 2 ly cà phê trở lên'),

('KM003', N'Giảm 20% cho hóa đơn trên 200.000đ', 
 N'Áp dụng cho tổng hóa đơn có giá trị lớn hơn hoặc bằng 200.000 đồng.', 
 '01/03/2025', '30/06/2025', 
 N'Hóa đơn từ 200.000đ trở lên'),

('KM004', N'Tặng bánh ngọt khi mua combo sáng', 
 N'Khi gọi combo cà phê + đồ ăn sáng sẽ được tặng kèm 1 bánh ngọt mini.', 
 '01/04/2025', '30/06/2025', 
 N'Áp dụng khung giờ 6h00–10h00'),

('KM005', N'Happy Hour – Giảm 30% buổi chiều', 
 N'Giảm 30% cho tất cả đồ uống trong khung giờ 14h00–17h00 mỗi ngày.', 
 '01/01/2025', '31/12/2025', 
 N'Áp dụng trong khung giờ 14h00–17h00'),

('KM006', N'Giảm 15% khi thanh toán qua ví Momo', 
 N'Áp dụng khi thanh toán bằng ví điện tử Momo.', 
 '15/02/2025', '31/12/2025', 
 N'Thanh toán qua Momo'),

('KM007', N'Sinh nhật vui vẻ – Giảm 50%', 
 N'Giảm 50% giá trị hóa đơn cho khách hàng có sinh nhật trong tháng.', 
 '01/01/2025', '31/12/2025', 
 N'Khách hàng có sinh nhật trong tháng'),

('KM008', N'Combo bạn bè – Giảm 25%', 
 N'Giảm 25% cho nhóm từ 4 người trở lên.', 
 '15/03/2025', '31/08/2025', 
 N'Nhóm khách từ 4 người trở lên'),

('KM009', N'Thứ 4 vui vẻ – Đồng giá 29.000đ', 
 N'Tất cả đồ uống đồng giá 29.000đ vào thứ 4 hàng tuần.', 
 '01/01/2025', '31/12/2025', 
 N'Áp dụng vào thứ 4 hàng tuần'),

('KM010', N'Khuyến mãi tết – Giảm 25% toàn bộ menu', 
 N'Chương trình khuyến mãi đặc biệt nhân dịp Tết Nguyên Đán.', 
 '20/01/2025', '10/02/2025', 
 N'Áp dụng cho tất cả khách hàng trong dịp Tết');
GO
-- ===================================
-- DỮ LIỆU MẪU CHO Bàn
-- ===================================
INSERT INTO Ban (maBan, viTri, sucChua, trangThai)
VALUES
('B001', N'Tầng 1', 2, N'Trống'),
('B002', N'Tầng 1', 4, N'Có khách'),
('B003', N'Tầng 1', 2, N'Trống'),
('B004', N'Tầng 1', 4, N'Đặt trước'),
('B005', N'Tầng 1', 6, N'Có khách'),

('B006', N'Tầng 2', 2, N'Trống'),
('B007', N'Tầng 2', 4, N'Trống'),
('B008', N'Tầng 2', 6, N'Có khách'),
('B009', N'Tầng 2', 2, N'Trống'),
('B010', N'Tầng 2', 4, N'Trống'),

('B011', N'Sân vườn', 4, N'Có khách'),
('B012', N'Sân vườn', 2, N'Trống'),
('B013', N'Sân vườn', 6, N'Trống'),
('B014', N'Sân vườn', 4, N'Có khách'),
('B015', N'Sân vườn', 2, N'Đặt trước'),

('B016', N'Khu VIP - Phòng riêng', 6, N'Trống'),
('B017', N'Khu VIP - Phòng riêng', 8, N'Có khách'),
('B018', N'Khu VIP - Phòng riêng', 6, N'Trống'),
('B019', N'Khu VIP - Phòng riêng', 8, N'Trống'),
('B020', N'Khu VIP - Phòng riêng', 10, N'Đặt trước'),

('B021', N'Tầng 1', 4, N'Trống'),
('B022', N'Tầng 1', 6, N'Có khách'),
('B023', N'Tầng 2', 4, N'Trống'),
('B024', N'Tầng 2', 2, N'Có khách'),
('B025', N'Sân vườn', 4, N'Trống'),

('B026', N'Sân vườn', 6, N'Trống'),
('B027', N'Tầng 2', 4, N'Trống'),
('B028', N'Tầng 1', 2, N'Có khách'),
('B029', N'Tầng 1', 10, N'Trống'),
('B030', N'Tầng 1', 8, N'Trống'),

('B031', N'Tầng 1', 2, N'Trống'),
('B032', N'Tầng 1', 4, N'Trống'),
('B033', N'Tầng 1', 2, N'Trống'),
('B034', N'Tầng 1', 2, N'Trống'),
('B035', N'Tầng 1', 6, N'Trống'),
('B036', N'Tầng 1', 8, N'Trống'),
('B037', N'Tầng 1', 8, N'Trống'),
('B038', N'Tầng 1', 10, N'Trống'),
('B039', N'Tầng 1', 10, N'Trống'),
('B040', N'Tầng 1', 10, N'Trống');
Go
-- ===================================
-- DỮ LIỆU MẪU CHO ĐƠN ĐẶT BÀN
-- ===================================
SET DATEFORMAT dmy;
GO
INSERT INTO DonDatBan (maDatBan, ngayDat, soLuongKhach, trangThai, maKH, maNV)
VALUES
('DDB001', '01/09/2025', 2, N'Hoàn thành', 'KH001', 'NV01'),
('DDB002', '05/09/2025', 4, N'Hoàn thành', 'KH002', 'NV07'),
('DDB003', '05/09/2025', 3, N'Hủy', 'KH003', 'NV06'), 
('DDB004', '06/09/2025', 5, N'Hoàn thành', 'KH004', 'NV05'),
('DDB005', '10/09/2025', 2, N'Đang phục vụ', 'KH005', 'NV01'), 

('DDB006', '14/09/2025', 4, N'Hoàn thành', 'KH006', 'NV01'),
('DDB007', '15/09/2025', 3, N'Đặt trước', 'KH007', 'NV05'),
('DDB008', '20/09/2025', 6, N'Hoàn thành', 'KH008', 'NV06'),
('DDB009', '22/09/2025', 2, N'Hoàn thành', 'KH009', 'NV01'),
('DDB010', '24/09/2025', 4, N'Đặt trước', 'KH010', 'NV01'),

('DDB011', '02/10/2025', 5, N'Hủy', 'KH011', 'NV07'),
('DDB012', '04/10/2025', 3, N'Hoàn thành', 'KH012', 'NV06'),
('DDB013', '04/10/2025', 4, N'Hoàn thành', 'KH013', 'NV07'),
('DDB014', '05/10/2025', 6, N'Đặt trước', 'KH014', 'NV07'),
('DDB015', '07/10/2025', 2, N'Hoàn thành', 'KH015', 'NV07'),

('DDB016', '09/10/2025', 3, N'Hoàn thành', 'KH016', 'NV06'),
('DDB017', '11/10/2025', 5, N'Hủy', 'KH017', 'NV06'),
('DDB018', '12/10/2025', 6, N'Hoàn thành', 'KH018', 'NV05'),
('DDB019', '14/10/2025', 2, N'Đặt trước', 'KH019', 'NV06'),
('DDB020', '16/10/2025', 4, N'Hoàn thành', 'KH020', 'NV07'),

('DDB021', '18/10/2025', 3, N'Hoàn thành', 'KH001', 'NV01'),
('DDB022', '20/10/2025', 2, N'Đặt trước', 'KH002', 'NV05'),
('DDB023', '23/10/2025', 5, N'Hoàn thành', 'KH003', 'NV01'),
('DDB024', '24/10/2025', 6, N'Đang phục vụ', 'KH004', 'NV01'),
('DDB025', '25/10/2025', 4, N'Hủy', 'KH005', 'NV01'),

('DDB026', '26/10/2025', 3, N'Hoàn thành', 'KH006', 'NV05'),
('DDB027', '27/10/2025', 2, N'Hoàn thành', 'KH007', 'NV05'),
('DDB028', '30/10/2025', 5, N'Hoàn thành', 'KH008', 'NV05'),
('DDB029', '02/11/2025', 4, N'Hoàn thành', 'KH009', 'NV06'),
('DDB030', '04/11/2025', 6, N'Đang phục vụ', 'KH010', 'NV07');
Go
-- ===================================
-- DỮ LIỆU MẪU CHO Tài Khoản
-- ===================================
INSERT INTO TaiKhoan (maTK, tenDN, matKhau, vaiTro, trangThai, maNV)
VALUES
('TK001', 'admin01', '123456', N'Nhân viên quản lý', 1, 'NV01'),
('TK002', 'thungan01', '123456', N'Nhân viên thu ngân', 1, 'NV05'),
('TK003', 'thungan02', '123456', N'Nhân viên thu ngân', 1, 'NV06'),
('TK004', 'thungan03', '123456', N'Nhân viên thu ngân', 1, 'NV07');
Go
-- =====================================
-- DỮ LIỆU MẪU CHO ĐƠN CHI TIẾT ĐẶT BÀN (Sử dụng các maDatBan đã tồn tại)
-- =====================================
INSERT INTO ChiTietDonDatBan (maDatBan, maBan, maSP, soLuongSP, donGia, ghiChu, trangThai)
VALUES
('DDB001', 'B001', 'SP01', 2, 45000, N'Trung bình đá', N'Đang chuẩn bị'),
('DDB001', 'B001', 'SP10', 1, 60000, N'Ít ngọt', N'Đang chuẩn bị'),
('DDB002', 'B002', 'SP03', 3, 35000, N'Không đá', N'Đã phục vụ'),
('DDB002', 'B002', 'SP15', 2, 55000, N'Ly lớn', N'Đang chuẩn bị'),
('DDB003', 'B003', 'SP05', 4, 25000, N'Thêm kem', N'Đã phục vụ'),
('DDB003', 'B003', 'SP12', 1, 70000, N'Thêm đá', N'Đã phục vụ'),
('DDB004', 'B004', 'SP06', 1, 30000, N'Không đường', N'Đang chuẩn bị'),
('DDB004', 'B004', 'SP18', 2, 55000, N'Nóng', N'Đang chuẩn bị'),
('DDB005', 'B005', 'SP07', 3, 25000, N'Thêm topping trân châu', N'Đã phục vụ'),
('DDB006', 'B006', 'SP02', 2, 40000, N'Ly nhỏ', N'Đang chuẩn bị'),
('DDB006', 'B006', 'SP14', 1, 65000, N'Vừa đá', N'Đang chuẩn bị'),
('DDB007', 'B007', 'SP09', 2, 50000, N'Trung bình đá', N'Đã phục vụ'),
('DDB007', 'B007', 'SP17', 1, 55000, N'Không kem', N'Đã phục vụ'),
('DDB008', 'B008', 'SP04', 2, 38000, N'Ly lớn', N'Đang chuẩn bị'),
('DDB008', 'B008', 'SP11', 1, 70000, N'Thêm đá', N'Đang chuẩn bị'),
('DDB009', 'B009', 'SP08', 1, 28000, N'Không đá', N'Đã phục vụ'),
('DDB009', 'B009', 'SP19', 2, 60000, N'Nóng nhẹ', N'Đã phục vụ'),
('DDB010', 'B010', 'SP13', 3, 32000, N'Trung bình', N'Đang chuẩn bị'),
('DDB010', 'B010', 'SP20', 1, 75000, N'Thêm caramel', N'Đang chuẩn bị'),
('DDB010', 'B010', 'SP05', 1, 25000, N'Thêm kem', N'Đang chuẩn bị');
Go
-- =====================================
-- DỮ LIỆU MẪU CHO LOẠI THUẾ
-- =====================================
INSERT INTO LoaiThue (maLoaiThue, tenThue)
VALUES
('LT01', N'Thuế giá trị gia tăng (GTGT)'),
('LT02', N'Thuế tiêu thụ đặc biệt (TTĐB)'),
('LT03', N'Thuế dịch vụ (DV)'),
('LT04', N'Thuế môi trường'),
('LT05', N'Thuế nhập khẩu (NK)'),
('LT06', N'Thuế bảo vệ người tiêu dùng (BVNTD)'),
('LT07', N'Thuế mùa vụ / tạm thời');
GO
-- =====================================
-- DỮ LIỆU MẪU CHO THUẾ
-- =====================================
INSERT INTO Thue (maThue, tenThue, mucThue, ngayApDung, ngayKetThuc, doiTuongApDung, maLoaiThue)
VALUES
('T001', N'Thuế GTGT 10%', 10.00, '2024-01-01', NULL, N'Tất cả sản phẩm đồ uống', 'LT01'),
('T002', N'Thuế TTĐB 15%', 15.00, '2024-03-01', NULL, N'Đồ uống có cồn', 'LT02'),
('T003', N'Thuế dịch vụ 5%', 5.00, '2024-06-01', NULL, N'Dịch vụ đặt bàn, phục vụ', 'LT03'),
('T004', N'Thuế GTGT ưu đãi 5%', 5.00, '2024-10-01', '2025-03-31', N'Sản phẩm khuyến mãi', 'LT01'),
('T005', N'Thuế môi trường 2%', 2.00, '2024-02-15', NULL, N'Bao bì, ly nhựa dùng một lần', 'LT04'),
('T006', N'Thuế GTGT 8%', 8.00, '2023-07-01', '2024-12-31', N'Đồ ăn nhẹ', 'LT01'),
('T007', N'Thuế nhập khẩu 12%', 12.00, '2024-05-01', NULL, N'Sản phẩm nhập khẩu', 'LT05'),
('T008', N'Thuế bảo vệ người tiêu dùng 3%', 3.00, '2024-09-01', NULL, N'Sản phẩm đóng chai', 'LT06'),
('T009', N'Thuế hàng cao cấp 20%', 20.00, '2024-11-01', NULL, N'Sản phẩm đặc biệt (rượu vang, cà phê hạt nhập)', 'LT02'),
('T010', N'Thuế mùa lễ hội 7%', 7.00, '2024-12-01', '2025-01-15', N'Tất cả sản phẩm trong tháng lễ', 'LT07');
GO
-- ===================================
-- DỮ LIỆU MẪU CHO HÓA ĐƠN
-- ===================================

INSERT INTO HoaDon (maHD, ngayLap, trangThai, maKH, maNV, maKM, maDatBan)
VALUES
('HD001', '2025-01-05', N'Đã thanh toán', 'KH001', 'NV01', 'KM001', 'DDB001'), 
('HD002', '2025-01-07', N'Đã thanh toán', 'KH002', 'NV01', NULL, 'DDB002'), 
('HD003', '2025-01-10', N'Đã thanh toán', 'KH003', 'NV05', 'KM005', NULL),
('HD004', '2025-01-20', N'Đã thanh toán', 'KH004', 'NV05', 'KM010', 'DDB004'),
('HD005', '2025-02-02', N'Đã thanh toán', 'KH005', 'NV05', 'KM010', NULL), 
('HD006', '2025-02-15', N'Đã thanh toán', 'KH006', 'NV06', 'KM006', 'DDB006'), 
('HD007', '2025-02-22', N'Đã thanh toán', 'KH007', 'NV07', NULL, NULL), 
('HD008', '2025-03-05', N'Đã thanh toán', 'KH008', 'NV07', 'KM001', 'DDB008'), 
('HD009', '2025-03-10', N'Đã thanh toán', 'KH009', 'NV06', 'KM002', 'DDB009'),
('HD010', '2025-03-25', N'Đã thanh toán', 'KH010', 'NV06', NULL, NULL), 
('HD011', '2025-04-01', N'Đã thanh toán', 'KH011', 'NV06', 'KM004', 'DDB012'), 
('HD012', '2025-04-15', N'Đã thanh toán', 'KH012', 'NV05', 'KM003', 'DDB013'), 
('HD013', '2025-04-20', N'Đã thanh toán', 'KH013', 'NV05', NULL, NULL), 
('HD014', '2025-05-05', N'Đã thanh toán', 'KH014', 'NV01', 'KM005', NULL), 
('HD015', '2025-05-10', N'Đã thanh toán', 'KH015', 'NV05', 'KM003', 'DDB015'), 
('HD016', '2025-06-01', N'Đã thanh toán', 'KH016', 'NV01', NULL, 'DDB016'),
('HD017', '2025-06-15', N'Đã thanh toán', 'KH017', 'NV01', 'KM008', NULL), 
('HD018', '2025-06-30', N'Đã thanh toán', 'KH018', 'NV01', 'KM009', NULL), 
('HD019', '2025-07-05', N'Đã thanh toán', 'KH019', 'NV01', NULL, NULL), 
('HD020', '2025-07-20', N'Đã thanh toán', 'KH020', 'NV05', 'KM007', NULL); 
Go

-- =====================================
-- DỮ LIỆU MẪU CHO CHI TIẾT HÓA ĐƠN
-- =====================================
INSERT INTO ChiTietHD (maHD, maSP, soLuongSP, donGia, ghiChu, maThue) VALUES
('HD001', 'SP01', 2, 45000, 'Ít đường', 'T001'),
('HD001', 'SP05', 1, 55000, 'Thêm topping trứng', 'T001'),
('HD002', 'SP03', 3, 40000, 'Mang về', 'T002'),
('HD002', 'SP09', 2, 60000, NULL, 'T002'),
('HD003', 'SP02', 1, 50000, 'Ít đá', 'T001'),
('HD003', 'SP04', 2, 70000, 'Uống tại bàn', 'T003'),
('HD004', 'SP06', 2, 65000, NULL, 'T004'),
('HD004', 'SP07', 1, 75000, 'Giảm muối', 'T003'),
('HD005', 'SP08', 3, 55000, 'Thêm', 'T001'),
('HD005', 'SP10', 2, 80000, NULL, 'T002'),
('HD006', 'SP12', 1, 90000, NULL, 'T003'),
('HD006', 'SP14', 2, 45000, NULL, 'T004'),
('HD007', 'SP15', 1, 120000, 'Combo đặc biệt', 'T001'),
('HD007', 'SP18', 2, 70000, NULL, 'T001'),
('HD008', 'SP11', 3, 60000, 'Phục vụ nhanh', 'T002'),
('HD008', 'SP17', 2, 85000, NULL, 'T002'),
('HD009', 'SP16', 1, 95000, NULL, 'T003'),
('HD009', 'SP13', 1, 55000, NULL, 'T003'),
('HD010', 'SP19', 2, 65000, 'Khách đặt online', 'T004'),
('HD010', 'SP20', 1, 50000, NULL, 'T004'),
('HD010', 'SP02', 2, 50000, 'Không có', 'T001'),
('HD011', 'SP04', 1, 70000, 'Không có', 'T002'),
('HD011', 'SP07', 2, 75000, 'Ít đường', 'T003'),
('HD012', 'SP08', 3, 55000, NULL, 'T001'),
('HD012', 'SP10', 1, 80000, 'Mang về', 'T002'),
('HD013', 'SP11', 2, 60000, 'Khách đặt qua app', 'T004'),
('HD014', 'SP13', 1, 55000, 'Thêm đá', 'T003'),
('HD015', 'SP15', 2, 120000, 'Combo trưa', 'T001'),
('HD016', 'SP17', 1, 85000, 'Phục vụ nhanh', 'T002'),
('HD017', 'SP18', 2, 70000, NULL, 'T001'),
('HD018', 'SP19', 1, 65000, 'Khách quen', 'T004'),
('HD019', 'SP20', 3, 50000, 'Thêm topping', 'T001'),
('HD020', 'SP01', 2, 45000, 'Thêm đá', 'T003'),
('HD020', 'SP05', 1, 55000, 'Thêm đá', 'T003'),
('HD020', 'SP09', 2, 60000, 'Uống tại bàn', 'T002'),
('HD015', 'SP12', 1, 90000, NULL, 'T004'),
('HD016', 'SP14', 2, 45000, 'Giảm ngọt', 'T002'),
('HD017', 'SP16', 1, 95000, 'Món đặc biệt', 'T003'),
('HD018', 'SP03', 2, 40000, 'Mang về', 'T001'),
('HD019', 'SP06', 1, 65000, 'Thêm đá', 'T004');
GO
-- Lệnh SQL để thêm cột maBan vào bảng DonDatBan
ALTER TABLE DonDatBan
ADD maBan VARCHAR(10) NULL; 
-- ⚠️ Lưu ý: VARCHAR(10) là giả định, hãy dùng kiểu dữ liệu và độ dài 
--           chính xác của cột maBan trong bảng Ban (khoá chính).

-- Tùy chọn: Thêm Ràng buộc Khoá ngoại (Foreign Key)
ALTER TABLE DonDatBan
ADD CONSTRAINT FK_DonDatBan_Ban
FOREIGN KEY (maBan) REFERENCES Ban(maBan);

SELECT * FROM KhachHang WHERE maKH = 'KH000';

ALTER TABLE DonDatBan
ALTER COLUMN ngayDat DATETIME2;

