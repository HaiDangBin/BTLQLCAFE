package GUI;
import DAO.ChiTietHoaDon_DAO;
import Entity.ChiTietHoaDon;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import DAO.ChiTietHoaDon_DAO;
import DAO.HoaDon_DAO;
import DAO.KhachHang_DAO;
import Entity.ChiTietHoaDon;
import Entity.HoaDon;
import Entity.KhachHang;
import Entity.NhanVien;
import STARTING.LoginForm;

public class ChiTietHoaDon_GUI extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTable tblChiTiet;
    private DefaultTableModel model;
    private JLabel lblTongTien;
    private JButton btnThanhToan;

    private String maBan;
    private String tenKH;
    private String sdtKH;

    private HoaDon_DAO hdDAO = new HoaDon_DAO();
    private ChiTietHoaDon_DAO cthdDAO = new ChiTietHoaDon_DAO();
    private KhachHang_DAO khDAO = new KhachHang_DAO();

    public ChiTietHoaDon_GUI(String maBan, String tenKH, String sdtKH) {
        this.maBan = maBan;
        this.tenKH = tenKH;
        this.sdtKH = sdtKH;

        setTitle("Chi tiết hóa đơn - Cafe CORNCORN");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ================= HEADER =================
        JLabel lblTitle = new JLabel("CHI TIẾT HÓA ĐƠN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(52, 152, 219));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setPreferredSize(new Dimension(800, 50));
        add(lblTitle, BorderLayout.NORTH);

        // ================= TABLE =================
        String[] cols = {"Mã SP", "Tên SP", "Số lượng", "Đơn giá", "Thành tiền"};
        model = new DefaultTableModel(cols, 0);
        tblChiTiet = new JTable(model);
        tblChiTiet.setRowHeight(26);
        add(new JScrollPane(tblChiTiet), BorderLayout.CENTER);

        // ================= FOOTER =================
        JPanel footer = new JPanel(new BorderLayout());
        lblTongTien = new JLabel("Tổng tiền: 0 VNĐ", SwingConstants.RIGHT);
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTongTien.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));
        footer.add(lblTongTien, BorderLayout.CENTER);

        btnThanhToan = new JButton("Thanh toán");
        btnThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnThanhToan.setBackground(new Color(46, 204, 113));
        btnThanhToan.setForeground(Color.WHITE);
        btnThanhToan.setFocusPainted(false);
        footer.add(btnThanhToan, BorderLayout.EAST);

        add(footer, BorderLayout.SOUTH);

        btnThanhToan.addActionListener(e -> xuLyThanhToan());
    }

    // ================== HÀM XỬ LÝ THANH TOÁN ==================
    private void xuLyThanhToan() {
        try {
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Không có sản phẩm nào để lập hóa đơn!",
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 1️⃣ Lấy mã NV từ tài khoản đăng nhập
            NhanVien nv = LoginForm.tkLogin.getNhanVien();
            if (nv == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin nhân viên đăng nhập!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2️⃣ Thêm khách hàng
            String maKH = khDAO.generateMaKH();
            KhachHang kh = new KhachHang(maKH, tenKH, sdtKH, null);
            khDAO.insert(kh);

            // 3️⃣ Sinh mã hóa đơn
            String maHD = "HD" + System.currentTimeMillis();
            String ngayLap = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            HoaDon hd = new HoaDon(maHD, nv.getMaNV(), maKH, null, maBan, ngayLap);
            boolean themHD = hdDAO.insert(hd);

            if (!themHD) {
                JOptionPane.showMessageDialog(this, "Không thể tạo hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 4️⃣ Lưu chi tiết hóa đơn
            double tongTien = 0;
            for (int i = 0; i < model.getRowCount(); i++) {
                String maSP = model.getValueAt(i, 0).toString();
                int soLuong = Integer.parseInt(model.getValueAt(i, 2).toString());
                double donGia = Double.parseDouble(model.getValueAt(i, 3).toString());

                ChiTietHoaDon cthd = new ChiTietHoaDon(maHD, maSP, soLuong, donGia);
                cthdDAO.createChiTietHoaDon(cthd);


                tongTien += soLuong * donGia;
            }

            // 5️⃣ Cập nhật tổng tiền và thông báo
            lblTongTien.setText(String.format("Tổng tiền: %,.0f VNĐ", tongTien));
            JOptionPane.showMessageDialog(this,
                    "Tạo hóa đơn thành công!\nTổng tiền: " + String.format("%,.0f VNĐ", tongTien),
                    "Hoàn tất", JOptionPane.INFORMATION_MESSAGE);

            dispose(); // đóng form sau khi thanh toán xong

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Đã xảy ra lỗi khi lập hóa đơn!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================== TEST ==================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChiTietHoaDon_GUI("B001", "Nguyễn Văn A", "0912345678").setVisible(true);
        });
    }
}
