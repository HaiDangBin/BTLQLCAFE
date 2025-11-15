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
    private String sdtTuForm;
    private KhachHang_DAO khachHangDAO = new KhachHang_DAO();
    private HoaDon_DAO hdDAO = new HoaDon_DAO();
    private String maHoaDonHienTai;
	private JLabel lblTienCoc;
	private ChucVu_DAO chucVuDAO;

    public ChiTietDonDatBan_GUI(JFrame parent, DefaultTableModel modelMonAn, boolean isChuyenKhoan, Ban ban, String tenKH, String sdt) {
        super(parent, "Chi tiết đơn đặt bàn", true);
        DBconnection.getInstance().connect();
        this.isChuyenKhoanMode = isChuyenKhoan;
        this.banDAO = new Ban_DAO();
        this.ddbDAO = new DonDatBan_DAO();
        this.maDDBHienTai = null;
        this.banHienTai = ban;
        this.khachHangDAO = new KhachHang_DAO();
        this.isChiThanhToanTienCoc = true; // Luôn là thanh toán tiền cọc
        this.tenKHTuForm = tenKH != null ? tenKH : "Khách lẻ";
        this.sdtTuForm = sdt != null ? sdt : "";
        this.chucVuDAO = new ChucVu_DAO(); // KHẮC PHỤC LỖI NPE
        

        setSize(350, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(0, 0));

        // Header
        JPanel pnlHeader = new JPanel(new GridLayout(5, 1));
        JLabel lblTenNH = new JLabel("Quán Caffe CornCorn", SwingConstants.CENTER);
        lblTenNH.setFont(new Font("Segoe UI", Font.BOLD, 19));
        JLabel lblDiaChi = new JLabel("ĐỊA CHỈ: 24 Lê Đức Thọ, P.5, Gò Vấp", SwingConstants.CENTER);
        lblDiaChi.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JLabel lblSDT = new JLabel("SĐT: 0242 545 567", SwingConstants.CENTER);
        lblSDT.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JLabel lblHD = new JLabel("HÓA ĐƠN ĐẶT BÀN", SwingConstants.CENTER);
        lblHD.setFont(new Font("Segoe UI", Font.BOLD, 19));
        pnlHeader.add(lblTenNH); pnlHeader.add(lblDiaChi); pnlHeader.add(lblSDT); pnlHeader.add(Box.createVerticalStrut(1)); pnlHeader.add(lblHD);
        add(pnlHeader, BorderLayout.NORTH);

        // Tính tiền cọc (loại bỏ phần món ăn)
        double tienCocMotNguoi = 10000;
        this.rawTienDatCoc = this.banHienTai.getSucChua() * tienCocMotNguoi;
        this.tongTienThuc = this.rawTienDatCoc;
        this.vatThuc = 0; // Không VAT cho cọc
        this.thanhTienThuc = this.rawTienDatCoc;

  
        

        // Footer with totals and payment
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
        pnlButtons.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20)); // Thêm padding
        btnIn = new RoundedButton("IN", new Color(0, 102, 102));
        pnlButtons.add(btnIn);
        
        // ⭐ BẮT ĐẦU PHẦN KHẮC PHỤC LỖI HIỂN THỊ ⭐
        // 1. Tạo Container chứa cả pnlFooter và pnlButtons
        JPanel pnlBottom = new JPanel();
        // 2. Thiết lập BoxLayout để xếp chồng theo chiều dọc (Footer rồi đến Buttons)
        pnlBottom.setLayout(new BoxLayout(pnlBottom, BoxLayout.Y_AXIS)); 
        pnlBottom.setBackground(Color.WHITE);
        
        pnlBottom.add(pnlFooter); // Thêm Panel thông tin thanh toán
        pnlBottom.add(pnlButtons); // Thêm Panel nút bấm
        
        // 3. Chỉ gọi add(..., BorderLayout.SOUTH) MỘT LẦN DUY NHẤT cho container chung
        add(pnlBottom, BorderLayout.SOUTH);
        // ⭐ KẾT THÚC PHẦN KHẮC PHỤC LỖI HIỂN THỊ ⭐

        // ⭐ THÊM ACTION CHO NÚT IN: Nhập tiền khách trả, tính thừa, in, lưu DB
        btnIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double khachTra = Double.parseDouble(txtTienKhachTra.getText().trim().replace(",", "").replace("VND", ""));
                    double tienThua = khachTra - thanhTienThuc;
                    lblTienThua.setText(df.format(tienThua));

                    if (tienThua < 0) {
                        JOptionPane.showMessageDialog(ChiTietDonDatBan_GUI.this, "Tiền khách trả không đủ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                 // Lưu vào DB (nếu tiền cọc, tạo DDB mới hoặc update)
                    saveDonDatBan();

                    // In hóa đơn (sửa để không dùng bảng)
                    showHoaDonInDialog(df.format(rawTienDatCoc), df.format(vatThuc), df.format(thanhTienThuc), df.format(khachTra), df.format(tienThua), tenKHTuForm);

                     

                    // Update trạng thái bàn
                    banDAO.updateTrangThaiBan(banHienTai.getMaBan(), "Đã đặt");

                    JOptionPane.showMessageDialog(ChiTietDonDatBan_GUI.this, "Hóa đơn đã được in và lưu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // Đóng dialog

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(ChiTietDonDatBan_GUI.this, "Vui lòng nhập số tiền hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Thêm listener cho txtTienKhachTra để tính tiền thừa tự động
        txtTienKhachTra.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                try {
                    String text = txtTienKhachTra.getText().trim().replace(",", "").replace("VND", "");
                    if (text.isEmpty()) {
                        lblTienThua.setText("0 VND");
                        return;
                    }
                    double khachTra = Double.parseDouble(text);
                    double tienThua = khachTra - thanhTienThuc;
                    lblTienThua.setText(df.format(tienThua));
                } catch (NumberFormatException ex) {
                    // Bỏ qua nếu người dùng đang nhập dở
                }
            }
        });
    }

    private void showHoaDonInDialog(String tienCoc, String vat, String thanhTien, String tienKhach, String tienThua, String tenKhachHang) {
        JDialog dialog = new JDialog(this, "In hóa đơn", true);
        dialog.setSize(350, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(0, 0));

        JPanel header = new JPanel(new GridLayout(6, 1));
        JLabel title = new JLabel("Quán Caffe CornCorn", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 19));
        header.add(title);
        JLabel dChi = new JLabel("ĐỊA CHỈ: 24 Lê Đức Thọ, p5, Gò Vấp", SwingConstants.CENTER);
        dChi.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JLabel sdt = new JLabel("SĐT: 0242 545 567", SwingConstants.CENTER);
        sdt.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JLabel hd = new JLabel("HÓA ĐƠN ĐẶT BÀN: " + (maDDBHienTai!=null?maDDBHienTai:""), SwingConstants.CENTER);
        hd.setFont(new Font("Segoe UI", Font.BOLD, 19));
        JLabel kh = new JLabel("Khách hàng: "+ tenKhachHang, SwingConstants.LEFT);
        kh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.add(dChi); header.add(sdt); header.add(Box.createVerticalStrut(1)); header.add(hd); header.add(kh);
        dialog.add(header, BorderLayout.NORTH);

        

        JPanel footer = new JPanel(new GridLayout(7, 2, 5, 5));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        footer.add(new JLabel("Tiền cọc:"));
        footer.add(new JLabel(tienCoc, SwingConstants.RIGHT));
        footer.add(new JLabel("VAT (5%):"));
        footer.add(new JLabel(vat, SwingConstants.RIGHT));
        footer.add(new JLabel("Thành tiền:"));
        footer.add(new JLabel(thanhTien, SwingConstants.RIGHT));
        footer.add(new JLabel("Tiền khách trả:"));
        footer.add(new JLabel(tienKhach, SwingConstants.RIGHT));
        footer.add(new JLabel("Tiền thừa:"));
        footer.add(new JLabel(tienThua, SwingConstants.RIGHT));

        dialog.add(footer, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void saveDonDatBan() {
        try {
            // 1. Tạo mã đơn đặt bàn mới
        	String maDDBMoi = ddbDAO.phatSinhMaDatBan(); // Cần có hàm này trong DAO

            // 2. Tìm hoặc tạo khách hàng
            KhachHang kh = null;
            if (sdtTuForm != null && !sdtTuForm.trim().isEmpty()) {
                kh = khachHangDAO.findKhachHangBySDT(sdtTuForm);
                if (kh == null) {
                    String maKHMoi = khachHangDAO.phatSinhMaKH();
                    kh = new KhachHang(maKHMoi, tenKHTuForm, sdtTuForm);
                    khachHangDAO.createKhachHang(kh); // Lưu KH mới
                }
            } else {
                kh = new KhachHang("KH000", "Khách lẻ", "");
            }
            ChucVu chucVuCuaNV = this.chucVuDAO.findById("L01");
            // 3. Nhân viên (mặc định)
            NhanVien nv = new NhanVien("NV01", "Admin", " ", " "," ",chucVuCuaNV);

            // 4. Tạo đối tượng DonDatBan
            java.sql.Date sqlNgayDat = java.sql.Date.valueOf(LocalDate.now());
            int soLuongKhach = banHienTai.getSucChua();
            String trangThai = "Đã cọc"; // hoặc "Đã đặt"

            DonDatBan newDDB = new DonDatBan(
                maDDBMoi,
                sqlNgayDat,
                soLuongKhach,
                trangThai,
                kh,
                nv
            );

            // BẮT BUỘC: GÁN MÃ BÀN
            newDDB.setBan(banHienTai);

            // 5. Lưu vào CSDL
            if (!ddbDAO.createDonDatBan(newDDB)) {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu đơn đặt bàn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 6. Cập nhật trạng thái bàn
            if (!banDAO.updateTrangThaiBan(banHienTai.getMaBan(), "Đã đặt")) {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật trạng thái bàn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 7. Gán mã đơn hiện tại
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

    // Minimal placeholder RoundedButton
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