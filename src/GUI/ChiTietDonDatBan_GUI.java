package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import DAO.Ban_DAO;
import DAO.ChucVu_DAO;
import DAO.DonDatBan_DAO;
import DAO.HoaDon_DAO;
import DAO.KhachHang_DAO;
import Entity.Ban;
import Entity.ChucVu;
import Entity.DonDatBan;
import Entity.HoaDon;
import Entity.KhachHang;
import Entity.NhanVien;
import connectDB.DBconnection;
import DAO.HoaDon_DAO; 

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Random;

public class ChiTietDonDatBan_GUI extends JDialog {
    private JLabel lblTongTien, lblThanhTien, lblTienThua;
    private JTextField txtTienKhachTra;
    private DecimalFormat df = new DecimalFormat("#,### VND");
    private double rawTienDatCoc;
    private String tenKhachHangHienTai = "Khách lẻ";
    private DonDatBan_DAO ddbDAO = new DonDatBan_DAO();
    private KhachHang_DAO khDAO = new KhachHang_DAO();
    private String maDDBHienTai;
    private double tongTienThuc;
    private double vatThuc;
    private double thanhTienThuc;
    private JLabel lblTienKhachTraStatic;
    private JLabel lblTienThuaStatic;
    private RoundedButton btnIn;
    private final boolean isChuyenKhoanMode;
    private JLabel lblTienKhachTra;
    private Ban_DAO banDAO;
    private Ban banHienTai;
    private boolean isChiThanhToanTienCoc;
    private String tenKHTuForm;
    private String ngayStr;
    private String gioStr;
    private String phutStr;
    private String sdtTuForm;
    private KhachHang_DAO khachHangDAO = new KhachHang_DAO();
    private HoaDon_DAO hdDAO = new HoaDon_DAO();
    private String maHoaDonHienTai;
	private JLabel lblTienCoc;
	private ChucVu_DAO chucVuDAO;
    private JLabel lblThoiGianDat;
	

    public ChiTietDonDatBan_GUI(JFrame parent, DefaultTableModel modelMonAn, boolean isChuyenKhoan, Ban ban, String tenKH, String sdt, String ngay, String gio, String phut) {
        super(parent, "Chi tiết đơn đặt bàn", true);
        DBconnection.getInstance().connect();
        this.isChuyenKhoanMode = isChuyenKhoan;
        this.banDAO = new Ban_DAO();
        this.ddbDAO = new DonDatBan_DAO();
        // Giữ nguyên maDDBHienTai = null để phân biệt chế độ
        this.banHienTai = ban;
        this.khachHangDAO = new KhachHang_DAO();
        this.isChiThanhToanTienCoc = true; 
        this.tenKHTuForm = tenKH != null ? tenKH : "Khách lẻ";
        this.sdtTuForm = sdt != null ? sdt : "";
        this.chucVuDAO = new ChucVu_DAO();
        
        // ⭐ SỬA LỖI ROBUSTNESS: Gán và xử lý Null/Empty input
        this.ngayStr = (ngay != null && !ngay.trim().isEmpty()) ? ngay.trim() : "??-??-????";
        this.gioStr = (gio != null && !gio.trim().isEmpty()) ? gio.trim() : "??";
        this.phutStr = (phut != null && !phut.trim().isEmpty()) ? phut.trim() : "??";
    
        setSize(350, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(0, 0));
        
        // Tính tiền cọc
        double tienCocMotNguoi = 10000;
        this.rawTienDatCoc = this.banHienTai.getSucChua() * tienCocMotNguoi;
        this.tongTienThuc = this.rawTienDatCoc;
        this.vatThuc = 0; 
        this.thanhTienThuc = this.rawTienDatCoc;

        // Header
        JPanel pnlHeader = new JPanel(new GridLayout(5, 1));
        JLabel lblTenNH = new JLabel("Quán Caffe CornCorn", SwingConstants.CENTER);
        lblTenNH.setFont(new Font("Segoe UI", Font.BOLD, 19));
        JLabel lblDiaChi = new JLabel("ĐỊA CHỈ: 24 Lê Đức Thọ, P.5, Gò Vấp", SwingConstants.CENTER);
        lblDiaChi.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        
 
        JLabel lblSDTQuan = new JLabel("SĐT: 0242 545 567", SwingConstants.CENTER);
        lblSDTQuan.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        
        JLabel lblHD = new JLabel("HÓA ĐƠN ĐẶT BÀN", SwingConstants.CENTER);
        lblHD.setFont(new Font("Segoe UI", Font.BOLD, 19));
        
        pnlHeader.add(lblTenNH); 
        pnlHeader.add(lblDiaChi); 
        pnlHeader.add(lblSDTQuan); 
        pnlHeader.add(Box.createVerticalStrut(1)); 
        pnlHeader.add(lblHD);
        add(pnlHeader, BorderLayout.NORTH);

  
        JPanel pnlInfo = new JPanel(new GridLayout(4, 1, 5, 5));
        pnlInfo.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

     
        JLabel lblTenKH = new JLabel("Khách hàng: " + this.tenKHTuForm);
        JLabel lblSDT = new JLabel("SĐT: " + this.sdtTuForm); 

  
        JLabel lblBan = new JLabel("Bàn: " + banHienTai.getMaBan() + " - Sức chứa: " + banHienTai.getSucChua());

  
        String thoiGian = this.ngayStr + " lúc " + this.gioStr + ":" + this.phutStr;
        lblThoiGianDat = new JLabel("Thời gian đặt: " + thoiGian); 
        lblThoiGianDat.setFont(new Font("Segoe UI", Font.BOLD, 14)); 

        pnlInfo.add(lblTenKH);
        pnlInfo.add(lblSDT);
        pnlInfo.add(lblBan);
        pnlInfo.add(lblThoiGianDat); 

        add(pnlInfo, BorderLayout.CENTER);
  

        
        JPanel pnlFooter = new JPanel(new GridLayout(7, 2, 5, 5));
        pnlFooter.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        lblTongTien = new JLabel(df.format(tongTienThuc), SwingConstants.RIGHT);
        JLabel lblVat = new JLabel(df.format(vatThuc), SwingConstants.RIGHT);
        lblThanhTien = new JLabel(df.format(thanhTienThuc), SwingConstants.RIGHT);
        lblTienCoc = new JLabel(df.format(rawTienDatCoc), SwingConstants.RIGHT);
        
        
        lblTienKhachTraStatic = new JLabel("Tiền khách trả:");
        txtTienKhachTra = new JTextField();
        lblTienThuaStatic = new JLabel("Tiền thừa:");
        lblTienThua = new JLabel("0 VND", SwingConstants.RIGHT);
        
        pnlFooter.add(new JLabel("Tiền cọc:"));
        pnlFooter.add(lblTienCoc);
        pnlFooter.add(new JLabel("Tổng tiền:"));
        pnlFooter.add(lblTongTien);
        pnlFooter.add(new JLabel("VAT (5%):"));
        pnlFooter.add(lblVat);
        pnlFooter.add(new JLabel("Thành tiền:"));
        pnlFooter.add(lblThanhTien);
        pnlFooter.add(lblTienKhachTraStatic);
        pnlFooter.add(txtTienKhachTra);
        pnlFooter.add(lblTienThuaStatic);
        pnlFooter.add(lblTienThua);

        // Buttons
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlButtons.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20)); 
        btnIn = new RoundedButton("IN", new Color(0, 102, 102));
        pnlButtons.add(btnIn);
        

        JPanel pnlBottom = new JPanel();
        pnlBottom.setLayout(new BoxLayout(pnlBottom, BoxLayout.Y_AXIS)); 
        pnlBottom.setBackground(Color.WHITE);
        
        pnlBottom.add(pnlFooter); 
        pnlBottom.add(pnlButtons); 
        
        add(pnlBottom, BorderLayout.SOUTH);


        if (this.maDDBHienTai != null && !this.maDDBHienTai.isEmpty()) {
            this.setTitle("Xem Chi Tiết Đơn Đặt Bàn");
  
            txtTienKhachTra.setVisible(false);
            lblTienKhachTraStatic.setVisible(false);
            lblTienThuaStatic.setVisible(false);
            lblTienThua.setVisible(false);
            btnIn.setVisible(false);
            
   
            try {
                DonDatBan donDatBanHienTai = ddbDAO.getDonDatBanByMa(this.maDDBHienTai);
                
                if (donDatBanHienTai != null) {
                    this.tenKHTuForm = donDatBanHienTai.getMaKH().getTenKH();
                    this.rawTienDatCoc = donDatBanHienTai.getSoLuongKhach() * tienCocMotNguoi;
                    this.tongTienThuc = this.rawTienDatCoc;
                    this.thanhTienThuc = this.rawTienDatCoc;

                    String thoiGianDatTrenHoaDon;
                    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm");
                    java.util.Date ngayGioDat = new java.util.Date(donDatBanHienTai.getNgayDat().getTime());
                    thoiGianDatTrenHoaDon = formatter.format(ngayGioDat);
                    
                    showHoaDonInDialog(
                        df.format(rawTienDatCoc), 
                        df.format(vatThuc), 
                        df.format(thanhTienThuc), 
                        df.format(thanhTienThuc), 
                        df.format(0.0), 
                        tenKHTuForm,
                        thoiGianDatTrenHoaDon
                    );
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu đơn đặt bàn.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi tải chi tiết đơn đặt bàn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    dispose();
                }
            });
            SwingUtilities.invokeLater(() -> dispose()); 
            return; 
        }

        btnIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String text = txtTienKhachTra.getText().trim().replace(",", "").replace("VND", "").replace(".", "");
                    double khachTra = Double.parseDouble(text.isEmpty() ? "0" : text);
                    double tienThua = khachTra - thanhTienThuc;
                    lblTienThua.setText(df.format(tienThua));

                    if (tienThua < 0) {
                        JOptionPane.showMessageDialog(ChiTietDonDatBan_GUI.this, "Tiền khách trả không đủ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // 1. Lưu đơn đặt bàn
                    saveDonDatBan();
                    
                    // Kiểm tra maDDBHienTai sau khi lưu
                    if (maDDBHienTai == null || maDDBHienTai.isEmpty()) {
                        JOptionPane.showMessageDialog(ChiTietDonDatBan_GUI.this, 
                            "Lỗi hệ thống: Không tìm thấy Mã đơn đặt bàn sau khi lưu.", 
                            "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                        return; 
                    }

                    DonDatBan donDatBanHienTai = ddbDAO.getDonDatBanByMa(maDDBHienTai);
                    
                    // 2. TẠO CHUỖI THỜI GIAN ĐẶT TỪ DB
                    String thoiGianDatTrenHoaDon;
                    if (donDatBanHienTai != null && donDatBanHienTai.getNgayDat() != null) {
 
                        // SỬA LỖI ĐỊNH DẠNG: THÊM HH:mm VÀO CHUỖI FORMAT
                        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm");
                        java.util.Date ngayGioDat = new java.util.Date(donDatBanHienTai.getNgayDat().getTime()); 
                        
                        thoiGianDatTrenHoaDon = formatter.format(ngayGioDat);
                    } else {
                        thoiGianDatTrenHoaDon = "Lỗi dữ liệu thời gian đặt.";
                    }
                    
                    // 3. In hóa đơn 
                    showHoaDonInDialog(
                        df.format(rawTienDatCoc), 
                        df.format(vatThuc), 
                        df.format(thanhTienThuc), 
                        df.format(khachTra), 
                        df.format(tienThua), 
                        tenKHTuForm,
                        thoiGianDatTrenHoaDon 
                    );

                    banDAO.updateTrangThaiBan(banHienTai.getMaBan(), "Đã đặt");

                    JOptionPane.showMessageDialog(ChiTietDonDatBan_GUI.this, "Hóa đơn đã được in và lưu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); 

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(ChiTietDonDatBan_GUI.this, "Vui lòng nhập số tiền hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        txtTienKhachTra.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                try {
                    String text = txtTienKhachTra.getText().trim().replace(",", "").replace("VND", "").replace(".", "");
                    if (text.isEmpty()) {
                        lblTienThua.setText("0 VND");
                        return;
                    }
                    double khachTra = Double.parseDouble(text);
                    double tienThua = khachTra - thanhTienThuc;
                    lblTienThua.setText(df.format(tienThua));
                } catch (NumberFormatException ex) {
                }
            }
        });
    }

    private void showHoaDonInDialog(String tienCoc, String vat, String thanhTien, String tienKhach, String tienThua, String tenKhachHang, String thoigiandat) {
        JDialog dialog = new JDialog(this, "In hóa đơn", true);
        dialog.setSize(350, 450); 
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(0, 0));


        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Color.WHITE);

        JLabel title = new JLabel("Quán Caffe CornCorn", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 19));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel dChi = new JLabel("<html><div style='text-align: center;'>ĐỊA CHỈ: 24 Lê Đức Thọ, p5, Gò Vấp</div></html>", SwingConstants.CENTER);
        dChi.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dChi.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sdt = new JLabel("SĐT: 0242 545 567", SwingConstants.CENTER);
        sdt.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sdt.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel hd = new JLabel("HÓA ĐƠN ĐẶT BÀN", SwingConstants.CENTER);
        hd.setFont(new Font("Segoe UI", Font.BOLD, 19));
        hd.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        header.add(title); 
        header.add(dChi); 
        header.add(sdt); 
        header.add(Box.createVerticalStrut(5)); 
        header.add(hd); 
        header.add(Box.createVerticalStrut(10)); 

        JPanel pnlMaDon = new JPanel(new BorderLayout());
        JLabel maDDBLbl = new JLabel("Mã đơn: " + (maDDBHienTai != null ? maDDBHienTai : "Đang tạo..."), SwingConstants.LEFT);
        maDDBLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pnlMaDon.add(maDDBLbl, BorderLayout.WEST);

        // Khách hàng
        JPanel pnlKH = new JPanel(new BorderLayout());
        JLabel kh = new JLabel("Khách hàng: "+ tenKhachHang, SwingConstants.LEFT);
        kh.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pnlKH.add(kh, BorderLayout.WEST);

        // Bàn đặt
        JPanel pnlBan = new JPanel(new BorderLayout());
        JLabel banLbl = new JLabel("Bàn đặt: " + banHienTai.getMaBan() + " - Số khách: " + banHienTai.getSucChua(), SwingConstants.LEFT);
        banLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pnlBan.add(banLbl, BorderLayout.WEST);

        // Thời gian đặt
        JPanel pnlTG = new JPanel(new BorderLayout());
        JLabel tg = new JLabel("Thời gian đặt: " + thoigiandat, SwingConstants.LEFT);
        tg.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pnlTG.add(tg, BorderLayout.WEST);


        header.add(pnlMaDon);
        header.add(pnlKH); 
        header.add(pnlBan);
        header.add(pnlTG);
        
        header.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        dialog.add(header, BorderLayout.NORTH);

        JPanel footer = new JPanel(new GridLayout(7, 2, 5, 5));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        footer.add(new JLabel("Tiền cọc:"));
        footer.add(new JLabel(tienCoc, SwingConstants.RIGHT));
        footer.add(new JLabel("VAT (0%):")); 
        footer.add(new JLabel(vat, SwingConstants.RIGHT));
        footer.add(new JLabel("Thành tiền:"));
        footer.add(new JLabel(thanhTien, SwingConstants.RIGHT));
        footer.add(new JLabel("Tiền khách trả:"));
        footer.add(new JLabel(tienKhach, SwingConstants.RIGHT));
        footer.add(new JLabel("Tiền thừa:"));
        footer.add(new JLabel(tienThua, SwingConstants.RIGHT));

        JPanel pnlBottom = new JPanel(new BorderLayout());
        JLabel lblCamOn = new JLabel("<html><div style='text-align: center;'>Vui lòng giữ hóa đơn này<br>Hẹn gặp lại quý khách!</div></html>", SwingConstants.CENTER);
        lblCamOn.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblCamOn.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        pnlBottom.add(footer, BorderLayout.NORTH);
        pnlBottom.add(lblCamOn, BorderLayout.SOUTH);
        
        dialog.add(pnlBottom, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void saveDonDatBan() {
        try {
            // 1. Tạo mã đơn đặt bàn mới
            String maDDBMoi = ddbDAO.phatSinhMaDatBan(); 
            
            String dateTimeString = ngayStr + " " + gioStr + ":" + phutStr + ":00";
            java.sql.Timestamp sqlNgayGioDat;
            
            try {
                java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                java.util.Date parsedDate = format.parse(dateTimeString);
                sqlNgayGioDat = new java.sql.Timestamp(parsedDate.getTime());

            } catch (Exception e) {

                System.err.println("Lỗi phân tích thời gian đặt bàn. Sử dụng thời gian hiện tại.");
                sqlNgayGioDat = new java.sql.Timestamp(System.currentTimeMillis()); 
            }

            KhachHang kh = null;
            if (sdtTuForm != null && !sdtTuForm.trim().isEmpty()) {
                kh = khachHangDAO.findKhachHangBySDT(sdtTuForm);
                if (kh == null) {
                    String maKHMoi = khachHangDAO.phatSinhMaKH();
                    kh = new KhachHang(maKHMoi, tenKHTuForm, sdtTuForm);
                    khachHangDAO.createKhachHang(kh); 
                }
            } else {
                kh = new KhachHang("KH000", "Khách lẻ", "");
            }
            ChucVu chucVuCuaNV = this.chucVuDAO.findById("L01");
      
            NhanVien nv = new NhanVien("NV01", "Admin", " ", " ",null,chucVuCuaNV);
            int soLuongKhach = banHienTai.getSucChua();
            String trangThai = "Đã cọc"; 

            DonDatBan newDDB = new DonDatBan(
                maDDBMoi,
                sqlNgayGioDat, 
                soLuongKhach,
                trangThai,
                kh,
                nv
            );
            newDDB.setBan(banHienTai);

            if (!ddbDAO.createDonDatBan(newDDB)) {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu đơn đặt bàn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!banDAO.updateTrangThaiBan(banHienTai.getMaBan(), "Đã đặt")) {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật trạng thái bàn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            this.maDDBHienTai = maDDBMoi;

            System.out.println("Đặt bàn thành công! Mã đơn: " + maDDBMoi + " | Bàn: " + banHienTai.getMaBan());

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void setMaDDBHienTai(String maDDB) {
        this.maDDBHienTai = maDDB;
    }

    class RoundedButton extends JButton {
        public RoundedButton(String text, Color bg) { super(text); setBackground(bg); setForeground(Color.WHITE); }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        public void updateUI() {
            setUI(new javax.swing.plaf.basic.BasicButtonUI());
        }
    }
}