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
import Entity.ChucVu;
import Entity.DonDatBan;
import Entity.KhachHang;
import Entity.NhanVien;
import connectDB.DBconnection;

public class DonDatBan_DAO {
	
	private ChucVu_DAO chucVuDAO;
	private KhachHang_DAO khachHangDAO;
	
	
	public DonDatBan_DAO() {
        this.chucVuDAO = new ChucVu_DAO(); 
        this.khachHangDAO = new KhachHang_DAO(); 
    }
	
	public List<Object[]> getAllDonDatBanDetails() {
        List<Object[]> danhSachDatBan = new ArrayList<>();
        Connection con = null; 
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT " +
                     "    DDB.maDatBan, KH.tenKH, DDB.soLuongKhach, DDB.ngayDat, NV.tenNV, DDB.trangThai " +
                     "FROM " +
                     "    DonDatBan DDB " +
                     "JOIN " +
                     "    KhachHang KH ON DDB.maKH = KH.maKH " +
                     "JOIN " +
                     "    NhanVien NV ON DDB.maNV = NV.maNV";
        try {
            con = DBconnection.getConnection(); 
            if (con == null) return danhSachDatBan; 
            
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();

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
            System.err.println("Lỗi SQL khi tải đơn đặt bàn: " + e.getMessage());
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
	public List<Object[]> findDonDatBan(String maDDB, String tenKH, String sdt, Date ngayTimKiem, String thoiGian) {
        List<Object[]> danhSachDatBan = new ArrayList<>();
        Connection con = null; 
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT " +
                     "    DDB.maDatBan, KH.tenKH, DDB.soLuongKhach, DDB.ngayDat, NV.tenNV, DDB.trangThai " +
                     "FROM " +
                     "    DonDatBan DDB " +
                     "JOIN " +
                     "    KhachHang KH ON DDB.maKH = KH.maKH " +
                     "JOIN " +
                     "    NhanVien NV ON DDB.maNV = NV.maNV " +
                     "WHERE 1=1"; 

        if (!maDDB.isEmpty()) {
            sql += " AND DDB.maDatBan LIKE ?";
        }
        if (!tenKH.isEmpty()) {
            sql += " AND KH.tenKH LIKE ?";
        }
        if (!sdt.isEmpty()) {
            sql += " AND KH.sDT LIKE ?";
        }

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
        }

        try {
            con = DBconnection.getConnection(); 
            if (con == null) return danhSachDatBan;
            
            stmt = con.prepareStatement(sql);
            int i = 1;

            if (!maDDB.isEmpty()) {
                stmt.setString(i++, "%" + maDDB + "%");
            }
            if (!tenKH.isEmpty()) {
                stmt.setString(i++, "%" + tenKH + "%");
            }
            if (!sdt.isEmpty()) {
                stmt.setString(i++, "%" + sdt + "%");
            }
            
            if (ngayTimKiem != null) {
                stmt.setDate(i++, ngayTimKiem);
            }

            rs = stmt.executeQuery();

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
	    
	    for (int i = 0; i < 10; i++) {
	        int randomNumber = random.nextInt(1000); 
	        String randomSuffix = String.format("%03d", randomNumber); 
	        newMaDDB = "DDB" + randomSuffix; 
	        String sql = "SELECT COUNT(*) FROM DonDatBan WHERE maDatBan = ?";
	        try {
	            stmt = con.prepareStatement(sql);
	            stmt.setString(1, newMaDDB);
	            rs = stmt.executeQuery();
	            
	            if (rs.next() && rs.getInt(1) == 0) {
	                return newMaDDB; 
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	             try {
	                 if (rs != null) rs.close();
	                 if (stmt != null) stmt.close();
	             } catch (SQLException e) {
	                 e.printStackTrace();
	             }
	        }
	    }

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
	public boolean capNhatTrangThai(String maDatBan, String trangThaiMoi) {
	    String sql = "UPDATE DonDatBan SET trangThai = ? WHERE maDatBan = ?";

	    try (Connection con = DBconnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setString(1, trangThaiMoi);
	        ps.setString(2, maDatBan);

	        return ps.executeUpdate() > 0;

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return false;
	}
	public DonDatBan getDonDatBanByMaBan1(String maBan) {
	    String sql = "SELECT * FROM DonDatBan WHERE maBan = ? AND trangThai = 'Đã đặt'";
	    try (Connection con = DBconnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {
	        ps.setString(1, maBan);
	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) {
	            DonDatBan ddb = new DonDatBan();
	            ddb.setMaDatBan(rs.getString("maDatBan"));
	            ddb.setNgayDat(rs.getDate("ngayDat"));
	            ddb.setSoLuongKhach(rs.getInt("soLuongKhach"));
	            ddb.setTrangThai(rs.getString("trangThai"));

	            KhachHang_DAO khDAO = new KhachHang_DAO();
	            KhachHang kh = khDAO.getKhachHangByMa(rs.getString("maKH"));
	            ddb.setMaKH(kh);

	            return ddb;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	public DonDatBan getDonDatBanByMaBan11(String maBan) {
	    String sql = """
	        SELECT TOP 1
	            ddb.maDatBan, ddb.ngayDat, ddb.soLuongKhach, ddb.trangThai,
	            ddb.maKH, ddb.maNV, ddb.maBan,
	            b.viTri, b.sucChua AS banSucChua, b.trangThai AS banTrangThai
	        FROM DonDatBan ddb
	        LEFT JOIN Ban b ON ddb.maBan = b.maBan
	        WHERE ddb.maBan = ? AND ddb.trangThai = 'Đang phục vụ'
	        ORDER BY ddb.ngayDat DESC
	        """;

	    try (Connection con = DBconnection.getConnection();
	         PreparedStatement stmt = con.prepareStatement(sql)) {

	        stmt.setString(1, maBan);
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                DonDatBan ddb = new DonDatBan();

	                // === CƠ BẢN ===
	                ddb.setMaDatBan(rs.getString("maDatBan"));
	                ddb.setNgayDat(rs.getDate("ngayDat"));
	                ddb.setSoLuongKhach(rs.getInt("soLuongKhach"));
	                ddb.setTrangThai(rs.getString("trangThai"));

	                // === KHÁCH HÀNG (Object) ===
	                String maKHStr = rs.getString("maKH");
	                KhachHang kh = null;
	                if (maKHStr != null && !maKHStr.trim().isEmpty()) {
	                    kh = new KhachHang_DAO().findKhachHangByMa(maKHStr);
	                    if (kh == null) {
	                        kh = new KhachHang(maKHStr, "Khách lẻ", "");
	                    }
	                } else {
	                    kh = new KhachHang("KH000", "Khách lẻ", "");
	                }
	                ddb.setMaKH(kh);  // Giữ nguyên Entity: setMaKH(KhachHang)

	                // === NHÂN VIÊN (Object) ===
	                String maNVStr = rs.getString("maNV");
	                NhanVien nv = null;
	                if (maNVStr != null && !maNVStr.trim().isEmpty()) {
	                    nv = new NhanVien_DAO().findNhanVienByMa(maNVStr);
	                    if (nv == null) {
	                        ChucVu cv = new ChucVu("L01", "Nhân viên");
	                        nv = new NhanVien(maNVStr, "Nhân viên", "", "", null, cv);
	                    }
	                } else {
	                    ChucVu cv = new ChucVu("L01", "Admin");
	                    nv = new NhanVien("NV01", "Admin", "", "", null, cv);
	                }
	                ddb.setMaNV(nv);  // Giữ nguyên Entity

	                // === BÀN (Object) ===
	                String maBanDB = rs.getString("maBan");
	                if (maBanDB != null) {
	                    Ban ban = new Ban();
	                    ban.setMaBan(maBanDB);
	                    ban.setViTri(rs.getString("viTri"));
	                    ban.setSucChua(rs.getInt("banSucChua"));
	                    ban.setTrangThai(rs.getString("banTrangThai"));
	                    ddb.setBan(ban);  // Giữ nguyên Entity
	                }

	                return ddb;
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Lỗi SQL khi tìm DonDatBan theo maBan: " + e.getMessage());
	        e.printStackTrace();
	    }
	    return null;
	}

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
	    Connection con = DBconnection.getConnection(); 
	    if (con == null) return null;
	    try (PreparedStatement stmt = con.prepareStatement(sql)) { 
	        
	        stmt.setString(1, maDDB);
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                // LẤY DỮ LIỆU CƠ BẢN
	                String maDatBan = rs.getString("maDatBan");
	                java.sql.Date ngayDat = rs.getDate("ngayDat");
	                int soLuongKhach = rs.getInt("soLuongKhach");
	                String trangThai = rs.getString("trangThai");

	                String maKH = rs.getString("maKH");
	                String maNV = rs.getString("maNV");
	                String maBanDB = rs.getString("maBan");

	                // TẠO KHÁCH HÀNG (Gọi DAO phụ)
	                KhachHang khachHang = null;
	                if (maKH != null && !maKH.trim().isEmpty()) {
	                    khachHang = khachHangDAO.getKhachHangByMa(maKH); 
	                    if (khachHang == null) {
	                        khachHang = new KhachHang(maKH, "Khách lẻ (Error)", "");
	                    }
	                } else {
	                    khachHang = new KhachHang("KH000", "Khách lẻ", "");
	                }

	                // TẠO NHÂN VIÊN
	                ChucVu chucVuCuaNV = chucVuDAO.findById("L01");
	                NhanVien nhanVien = null;
	                if (maNV != null && !maNV.trim().isEmpty()) {
	                    nhanVien = new NhanVien(maNV, "Nhân viên", "", "",null,chucVuCuaNV);
	                } else {
	                    nhanVien = new NhanVien("NV01", "Admin", "", "",null,chucVuCuaNV);
	                }

	                // TẠO BAN 
	                Ban ban = null;
	                if (maBanDB != null && !maBanDB.trim().isEmpty()) {
	                    String viTri = rs.getString("viTri");
	                    int sucChua = rs.getInt("banSucChua");
	                    String trangThaiBan = rs.getString("banTrangThai");
	                    ban = new Ban(maBanDB, viTri, sucChua, trangThaiBan);
	                }

	                // ... (Logic tạo DonDatBan) ...
	                ddb = new DonDatBan(maDatBan, ngayDat, soLuongKhach, trangThai, khachHang, nhanVien);
	                ddb.setBan(ban);
	            }
	        } // rs.close()
	    } catch (SQLException e) {
	        System.err.println("Lỗi SQL khi tìm kiếm DonDatBan theo mã: " + e.getMessage());
	        e.printStackTrace();
	    } 
	    return ddb;
	}
	public DefaultTableModel getMonAnByMaDDB(String maDDB) {
        String[] cols = {"Tên món", "SL", "Đơn giá", "Thành tiền"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        Connection con = DBconnection.getInstance().getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        DecimalFormat df = new DecimalFormat("#,###");
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


                model.addRow(new Object[]{
                    tenMon, 
                    soLuong, 
                    df.format(donGia) + " VND", 
                    df.format(thanhTien) + " VND" 
                });
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy chi tiết món ăn theo mã DDB: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Đóng tài nguyên
            try { if (rs != null) rs.close(); } catch (SQLException e) {  }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) {  }
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
	public DonDatBan getLatestActiveDonDatBanByMaBan(String maBan) {
	    DonDatBan ddb = null;
	    String sql = "SELECT TOP 1 "
	               + "    ddb.maDatBan, ddb.ngayDat, ddb.soLuongKhach, ddb.trangThai, ddb.maNV, "
	               + "    kh.maKH, kh.tenKH, kh.sDT, kh.eMail "
	               + "FROM "
	               + "    DonDatBan ddb "
	               + "JOIN "
	               + "    KhachHang kh ON ddb.maKH = kh.maKH "
	               + "WHERE "
	               + "    ddb.maBan = ? " 
	               + "    AND ddb.trangThai IN ('Đã đặt', 'Đang phục vụ','Đã cọc') " 
	               + "ORDER BY "
	               + "    ddb.ngayDat DESC, ddb.maDatBan DESC"; 

	    // Sử dụng try-with-resources để tự động đóng PreparedStatement và ResultSet
	    try (Connection con = DBconnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {
	        
	        // Kiểm tra kết nối (Dù đã được quản lý trong DBconnection, nhưng kiểm tra thêm cũng không thừa)
	        if (con == null) return null; 
	        
	        ps.setString(1, maBan);
	        
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                
	                // Lấy dữ liệu Khách hàng
	                String maKHStr = rs.getString("maKH");
	                String tenKH = rs.getString("tenKH");
	                String sdtKH = rs.getString("sDT"); 
	                String emailKH = rs.getString("eMail"); // ⭐ Lấy thêm email
	                
	                // Khởi tạo KhachHang (Cần đảm bảo Constructor có 4 tham số: ma, ten, sdt, email)
	                KhachHang khachHang = new KhachHang(maKHStr, tenKH, sdtKH, emailKH); 

	                // Lấy dữ liệu Đơn đặt bàn
	                String maDDB = rs.getString("maDatBan");
	                String trangThai = rs.getString("trangThai");
	                java.sql.Date ngayDat = rs.getDate("ngayDat"); 
	                int soLuongKhach = rs.getInt("soLuongKhach");
	                
	                // Lấy thông tin Nhân viên
	                String maNVStr = rs.getString("maNV");
	                NhanVien nhanVien = new NhanVien(maNVStr); 
	                
	                // 3. Khởi tạo DonDatBan
	                ddb = new DonDatBan(maDDB, ngayDat, soLuongKhach, trangThai, khachHang, nhanVien); 
	                Ban ban = new Ban(maBan); 
	                ddb.setBan(ban); 
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Lỗi CSDL khi truy vấn đơn đặt bàn hoạt động: " + e.getMessage());
	        e.printStackTrace();
	    }
	    return ddb;
	}
	public DonDatBan getDonDatBanActiveByMaBan(String maBan) {
	    DonDatBan ddb = null;
	    Connection con = DBconnection.getInstance().getConnection(); 
	    PreparedStatement stmt = null;
	    ResultSet rs = null;

	    try {
	        String sql = "SELECT DDB.MaDatBan, DDB.maKH, DDB.maBan, DDB.soLuongKhach, DDB.trangThai, DDB.ngayDat, " +
	                     "KH.tenKH, KH.sdt " + 
	                     "FROM DonDatBan DDB " +
	                     "JOIN KhachHang KH ON DDB.maKH = KH.maKH " +
	                     "WHERE DDB.maBan = ? AND UPPER(TRIM(DDB.trangThai)) IN (N'ĐẶT TRƯỚC', N'ĐANG PHỤC VỤ')";
	        
	        stmt = con.prepareStatement(sql);
	        stmt.setString(1, maBan);
	        rs = stmt.executeQuery();

	        if (rs.next()) {
	            System.out.println("Đã tìm thấy Đơn Đặt Bàn đang hoạt động cho Mã Bàn: " + maBan);
	            

	            KhachHang kh = new KhachHang();
	            kh.setMaKH(rs.getString("maKH"));
	            kh.setTenKH(rs.getString("tenKH"));
	            kh.setsDT(rs.getString("sdt")); 

	            // 2. Tạo Entity Bàn 
	            Ban ban = new Ban();
	            ban.setMaBan(rs.getString("maBan"));

	            // 3. Tạo Entity Đơn Đặt Bàn
	            ddb = new DonDatBan();
	            ddb.setMaDatBan(rs.getString("MaDatBan")); 
	            
	            ddb.setMaKH(kh);       
	            ddb.setBan(ban);            
	            ddb.setSoLuongKhach(rs.getInt("soLuongKhach"));
	            ddb.setTrangThai(rs.getString("trangThai"));
	            ddb.setNgayDat(rs.getDate("ngayDat")); 
	        } else {
	             System.out.println("Không tìm thấy Đơn Đặt Bàn đang hoạt động. Dùng Mã KH mặc định: KH000");
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
	        try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
	    }
	    return ddb;
	}
	public boolean updateTrangThaiDonDatBan(String maDDB, String newTrangThai) {
        Connection con = DBconnection.getConnection(); 
        if (con == null) return false;

        String sql = "UPDATE DonDatBan SET TrangThai = ? WHERE maDatBan = ?";
        int rowsAffected = 0;
        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newTrangThai);
            ps.setString(2, maDDB);

            rowsAffected = ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi cập nhật trạng thái Đơn Đặt Bàn (" + maDDB + ") sang " + newTrangThai + ":");
            e.printStackTrace();
        } 

        return rowsAffected > 0;
    }
	public DonDatBan getDonDatBanByMaBan(String maBan) {
	    DonDatBan ddb = null;
	    String sql = "SELECT * FROM DonDatBan WHERE maBan = ? AND trangThai IN ('Đã đặt', 'Đang phục vụ') ORDER BY ngayDat DESC LIMIT 1";  // Lấy đơn mới nhất đang hoạt động

	    try (Connection conn = DBconnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        
	        ps.setString(1, maBan);
	        ResultSet rs = ps.executeQuery();
	        
	        if (rs.next()) {
	            ddb = new DonDatBan();
	            
	            // Set các field cơ bản (String/Date/Int)
	            ddb.setMaDatBan(rs.getString("maDatBan"));
	            ddb.setNgayDat(rs.getDate("ngayDat"));
	            ddb.setSoLuongKhach(rs.getInt("soLuongKhach"));
	            ddb.setTrangThai(rs.getString("trangThai"));
	            
	            // Set maKH: Tạo object KhachHang tạm thời từ String
	            String maKHStr = rs.getString("maKH");
	            if (maKHStr != null && !maKHStr.trim().isEmpty()) {
	                KhachHang khTemp = new KhachHang(maKHStr, null, null);  // Constructor: maKH, tenKH=null, sDT=null (chỉ cần maKH để FK)
	                ddb.setMaKH(khTemp);
	            }
	            
	            // Set maNV: Tạo object NhanVien tạm thời từ String
	            String maNVStr = rs.getString("maNV");
	            if (maNVStr != null && !maNVStr.trim().isEmpty()) {
	                NhanVien nvTemp = new NhanVien(maNVStr, null, null, null, null, null);  // Constructor: maNV, tenNV=null, sDT=null, diaChi=null, ngaySinh=null, maLoai=null (dựa trên schema NhanVien)
	                ddb.setMaNV(nvTemp);  // Giả sử có setMaNV(NhanVien)
	            }
	            
	            // Set maBan (nếu Entity có field riêng)
	            ddb.setBan(new Ban(maBan));  // Giả sử có setBan(Ban) hoặc setMaBan(String maBan)
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	        System.err.println("Lỗi khi lấy DonDatBan bằng maBan: " + e.getMessage());
	        // Có thể throw new Exception nếu cần
	    }
	    
	    return ddb;
	}

}