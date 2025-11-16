package DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.table.DefaultTableModel;

import Entity.Ban;
import Entity.DonDatBan;
import Entity.KhachHang;
import Entity.NhanVien;
import connectDB.DBconnection;

public class DonDatBan_DAO {
	public List<Object[]> getAllDonDatBanDetails() {
        List<Object[]> danhSachDatBan = new ArrayList<>();
        Connection con = null; 
        PreparedStatement stmt = null;
        ResultSet rs = null;

        // Cập nhật SQL: Chọn các cột mới: soLuongKhach, trangThai
        String sql = "SELECT " +
                     "    DDB.maDatBan, KH.tenKH, DDB.soLuongKhach, DDB.ngayDat, NV.tenNV, DDB.trangThai " +
                     "FROM " +
                     "    DonDatBan DDB " +
                     "JOIN " +
                     "    KhachHang KH ON DDB.maKH = KH.maKH " +
                     "JOIN " +
                     "    NhanVien NV ON DDB.maNV = NV.maNV";
                     // Bỏ các JOIN không cần thiết (KM, Ban) vì đã loại bỏ khỏi bảng GUI

        try {
            con = DBconnection.getConnection(); 
            if (con == null) return danhSachDatBan; // Thoát nếu không kết nối được
            
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                // Đảm bảo thứ tự cột KHỚP CHÍNH XÁC với DefaultTableModel mới trong GUI
                Object[] rowData = {
                    rs.getString("maDatBan"),    // 1. Mã đơn đặt bàn
                    rs.getString("tenKH"),       // 2. Tên khách hàng
                    rs.getInt("soLuongKhach"),   // 3. Số lượng khách (Lấy từ DDB)
                    rs.getDate("ngayDat").toString(), // 4. Ngày lập
                    rs.getString("tenNV"),       // 5. Nhân viên
                    rs.getString("trangThai")    // 6. Trạng thái (Lấy từ DDB)
                };
                danhSachDatBan.add(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi SQL khi tải đơn đặt bàn: " + e.getMessage());
        } finally {
            // Đóng tài nguyên an toàn
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (con != null && !con.isClosed()) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return danhSachDatBan;
    }
	public List<Object[]> findDonDatBan(String maDDB, String tenKH, String sdt, Date ngayTimKiem, String thoiGian) {
        List<Object[]> danhSachDatBan = new ArrayList<>();
        Connection con = null; 
        PreparedStatement stmt = null;
        ResultSet rs = null;

        // Bắt đầu truy vấn cơ bản (giống như getAll)
        String sql = "SELECT " +
                     "    DDB.maDatBan, KH.tenKH, DDB.soLuongKhach, DDB.ngayDat, NV.tenNV, DDB.trangThai " +
                     "FROM " +
                     "    DonDatBan DDB " +
                     "JOIN " +
                     "    KhachHang KH ON DDB.maKH = KH.maKH " +
                     "JOIN " +
                     "    NhanVien NV ON DDB.maNV = NV.maNV " +
                     "WHERE 1=1"; // Điều kiện ban đầu luôn đúng

        // 1. Xây dựng điều kiện tìm kiếm động
        if (!maDDB.isEmpty()) {
            sql += " AND DDB.maDatBan LIKE ?";
        }
        if (!tenKH.isEmpty()) {
            sql += " AND KH.tenKH LIKE ?";
        }
        if (!sdt.isEmpty()) {
            sql += " AND KH.sDT LIKE ?";
        }

        // 2. Xử lý lọc theo Ngày/Thời gian
        if (ngayTimKiem != null) {
            sql += " AND DDB.ngayDat = ?";
        } else {
            LocalDate today = LocalDate.now();
            if (thoiGian.equals("Hôm nay")) {
                sql += " AND DDB.ngayDat = ?";
                ngayTimKiem = Date.valueOf(today);
            } else if (thoiGian.equals("Hôm qua")) {
                sql += " AND DDB.ngayDat = ?";
                ngayTimKiem = Date.valueOf(today.minusDays(1));
            }
            // Nếu là "Tất cả", không thêm điều kiện ngày tháng
        }

        try {
            con = DBconnection.getConnection(); 
            if (con == null) return danhSachDatBan;
            
            stmt = con.prepareStatement(sql);
            int i = 1; // Chỉ số tham số

            // 3. Gán giá trị cho các tham số
            if (!maDDB.isEmpty()) {
                stmt.setString(i++, "%" + maDDB + "%");
            }
            if (!tenKH.isEmpty()) {
                stmt.setString(i++, "%" + tenKH + "%");
            }
            if (!sdt.isEmpty()) {
                stmt.setString(i++, "%" + sdt + "%");
            }
            
            // Xử lý tham số ngày (Nếu có)
            if (ngayTimKiem != null) {
                stmt.setDate(i++, ngayTimKiem);
            }

            rs = stmt.executeQuery();

            // 4. Đọc dữ liệu (giống như getAll)
            while (rs.next()) {
                Object[] rowData = {
                    rs.getString("maDatBan"),    
                    rs.getString("tenKH"),       
                    rs.getInt("soLuongKhach"),   
                    rs.getDate("ngayDat").toString(), 
                    rs.getString("tenNV"),       
                    rs.getString("trangThai")    
                };
                danhSachDatBan.add(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi SQL khi tìm kiếm đơn đặt bàn: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (con != null && !con.isClosed()) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return danhSachDatBan;
    }
	public String phatSinhMaDatBan() {
	    Connection con = DBconnection.getInstance().getConnection();
	    String newMaDDB = "";
	    Random random = new Random();
	    PreparedStatement stmt = null;
	    ResultSet rs = null;
	    
	    // Thử tối đa 10 lần để tránh lặp vô hạn nếu CSDL đầy mã
	    for (int i = 0; i < 10; i++) {
	        // Tạo 3 chữ số ngẫu nhiên (từ 000 đến 999)
	        int randomNumber = random.nextInt(1000); 
	        // Định dạng thành chuỗi 3 chữ số, ví dụ: 5 -> "005", 123 -> "123"
	        String randomSuffix = String.format("%03d", randomNumber); 
	        
	        // Gộp lại: DDB + 3 chữ số ngẫu nhiên
	        newMaDDB = "DDB" + randomSuffix; 
	        
	        // Kiểm tra xem mã này đã tồn tại trong CSDL chưa
	        String sql = "SELECT COUNT(*) FROM DonDatBan WHERE maDatBan = ?";
	        try {
	            stmt = con.prepareStatement(sql);
	            stmt.setString(1, newMaDDB);
	            rs = stmt.executeQuery();
	            
	            if (rs.next() && rs.getInt(1) == 0) {
	                // Nếu COUNT là 0, mã chưa tồn tại, trả về mã này
	                return newMaDDB; 
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            // Nếu có lỗi, vẫn cố gắng thử lại lần tiếp theo
	        } finally {
	             // Đóng ResultSet và Statement ở đây
	             try {
	                 if (rs != null) rs.close();
	                 if (stmt != null) stmt.close();
	             } catch (SQLException e) {
	                 e.printStackTrace();
	             }
	        }
	    }
	    
	    // Nếu sau 10 lần thử vẫn không tạo được mã duy nhất, trả về mã lỗi hoặc throw exception
	    return "DDB_ERROR"; 
	}
	public boolean createDonDatBan(DonDatBan ddb) {
	    String sql = """
	        INSERT INTO DonDatBan 
	        (maDatBan, ngayDat, soLuongKhach, trangThai, maKH, maNV, maBan) 
	        VALUES (?, ?, ?, ?, ?, ?, ?)
	        """;

	    try (Connection con = DBconnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setString(1, ddb.getMaDatBan());
	        ps.setDate(2, ddb.getNgayDat());
	        ps.setInt(3, ddb.getSoLuongKhach());
	        ps.setString(4, ddb.getTrangThai());
	        ps.setString(5, ddb.getMaKH().getMaKH());
	        ps.setString(6, ddb.getMaNV().getMaNV());

	        // LẤY MÃ BÀN TỪ ĐỐI TƯỢNG BAN
	        if (ddb.getBan() != null) {
	            ps.setString(7, ddb.getBan().getMaBan());
	        } else {
	            ps.setNull(7, java.sql.Types.VARCHAR);
	        }

	        return ps.executeUpdate() > 0;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	// Trong DonDatBan_DAO.java

	public DonDatBan getDonDatBanByMa(String maDDB) {
	    DonDatBan ddb = null;
	    String sql = """
	        SELECT 
	            ddb.maDatBan, ddb.ngayDat, ddb.soLuongKhach, ddb.trangThai,
	            ddb.maKH, ddb.maNV, ddb.maBan,
	            b.viTri, b.sucChua AS banSucChua, b.trangThai AS banTrangThai
	        FROM DonDatBan ddb
	        LEFT JOIN Ban b ON ddb.maBan = b.maBan
	        WHERE ddb.maDatBan = ?
	        """;

	    try (Connection con = DBconnection.getConnection();
	         PreparedStatement stmt = con.prepareStatement(sql)) {

	        if (con == null) return null;

	        stmt.setString(1, maDDB);
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                // LẤY DỮ LIỆU
	                String maDatBan = rs.getString("maDatBan");
	                java.sql.Date ngayDat = rs.getDate("ngayDat"); // ĐÚNG: java.sql.Date
	                int soLuongKhach = rs.getInt("soLuongKhach");
	                String trangThai = rs.getString("trangThai");

	                String maKH = rs.getString("maKH");
	                String maNV = rs.getString("maNV");
	                String maBanDB = rs.getString("maBan");

	                // TẠO KHÁCH HÀNG
	                KhachHang khachHang = null;
	                if (maKH != null && !maKH.trim().isEmpty()) {
	                    khachHang = new KhachHang_DAO().getKhachHangByMa(maKH);
	                    if (khachHang == null) {
	                        khachHang = new KhachHang(maKH, "Khách lẻ", "");
	                    }
	                } else {
	                    khachHang = new KhachHang("KH000", "Khách lẻ", "");
	                }

	                // TẠO NHÂN VIÊN
	                NhanVien nhanVien = null;
	                if (maNV != null && !maNV.trim().isEmpty()) {
	                    nhanVien = new NhanVien(maNV, "Nhân viên", "", "");
	                } else {
	                    nhanVien = new NhanVien("NV01", "Admin", "", "");
	                }

	                // TẠO BAN
	                Ban ban = null;
	                if (maBanDB != null && !maBanDB.trim().isEmpty()) {
	                    String viTri = rs.getString("viTri");
	                    int sucChua = rs.getInt("banSucChua");
	                    String trangThaiBan = rs.getString("banTrangThai");
	                    ban = new Ban(maBanDB, viTri, sucChua, trangThaiBan);
	                }

	                // DÙNG CONSTRUCTOR ĐÚNG TRONG DonDatBan
	                ddb = new DonDatBan(
	                    maDatBan,
	                    ngayDat,           // java.sql.Date
	                    soLuongKhach,      // int
	                    trangThai,         // String
	                    khachHang,         // KhachHang
	                    nhanVien           // NhanVien
	                );

	                // GÁN BAN
	                ddb.setBan(ban);
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Lỗi SQL khi tìm kiếm DonDatBan theo mã: " + e.getMessage());
	        e.printStackTrace();
	    }
	    return ddb;
	}
	public DefaultTableModel getMonAnByMaDDB(String maDDB) {
        // Thiết lập cấu trúc cột cho bảng hiển thị
        String[] cols = {"Tên món", "SL", "Đơn giá", "Thành tiền"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        Connection con = DBconnection.getInstance().getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        DecimalFormat df = new DecimalFormat("#,###"); // Định dạng tiền
        
        // Truy vấn CSDL để lấy Chi tiết đơn đặt bàn
        // Giả sử có bảng ChiTietDonDatBan (CTDDB) và SanPham (SP)
        // Cần JOIN 2 bảng này
        String sql = "SELECT SP.tenSP, CTDDB.soLuong, SP.giaBan " +
                     "FROM ChiTietDonDatBan CTDDB " +
                     "JOIN SanPham SP ON CTDDB.maSP = SP.maSP " +
                     "WHERE CTDDB.maDDB = ?";

        try {
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maDDB);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String tenMon = rs.getString("tenSP");
                int soLuong = rs.getInt("soLuong");
                double donGia = rs.getDouble("giaBan");
                double thanhTien = soLuong * donGia;

                // Thêm hàng vào DefaultTableModel
                model.addRow(new Object[]{
                    tenMon, 
                    soLuong, 
                    df.format(donGia) + " VND", // Định dạng tiền để hiển thị
                    df.format(thanhTien) + " VND" // Định dạng tiền để hiển thị
                });
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy chi tiết món ăn theo mã DDB: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Đóng tài nguyên
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignored */ }
        }
        return model;
    }
	public String phatSinhMaDDB() {
	    String prefix = "DDB";
	    Random rand = new Random();
	    String ma;
	    do {
	        ma = prefix + String.format("%03d", rand.nextInt(1000));
	    } while (getDonDatBanByMa(ma) != null); // Đảm bảo không trùng
	    return ma;
	}

}