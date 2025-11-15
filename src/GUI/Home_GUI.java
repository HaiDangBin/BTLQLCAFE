package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import DAO.HoaDon_DAO;
import DAO.ChiTietHoaDon_DAO;
import Entity.TaiKhoan;

import java.awt.*;
import java.util.List;

public class Home_GUI extends JFrame {

    private TaiKhoan tkLogin;

    private JLabel lblTenNV, lblChucVu, lblSoDon, lblDoanhThu;
    private JTable tableHD;
    private DefaultTableModel model;

    private HoaDon_DAO hdDAO = new HoaDon_DAO();
    private ChiTietHoaDon_DAO cthdDAO = new ChiTietHoaDon_DAO();

    // ========================== CONSTRUCTOR ================================
    public Home_GUI(TaiKhoan tk) {
        this.tkLogin = tk;

        setTitle("Trang chủ");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildLeftMenu(), BorderLayout.WEST);
        add(buildMainPanel(), BorderLayout.CENTER);

        loadThongTinNhanVien();
        loadThongKe();
        loadLichSuHoaDon();
    }

    // ======================== MENU TRÁI ====================================
    private JPanel buildLeftMenu() {
        JPanel pn = new JPanel();
        pn.setPreferredSize(new Dimension(220, 750));
        pn.setLayout(new GridLayout(14, 1, 0, 5));  // 14 mục
        pn.setBackground(new Color(220, 230, 245));

        pn.add(makeMenuButton("Trang chủ"));
        pn.add(makeMenuButton("Bán hàng / Gọi món"));
        pn.add(makeMenuButton("Đặt bàn"));
        pn.add(makeMenuButton("Quản lý bàn"));
        pn.add(makeMenuButton("Sản phẩm"));
        pn.add(makeMenuButton("Loại sản phẩm"));
        pn.add(makeMenuButton("Khuyến mãi"));
        pn.add(makeMenuButton("Khách hàng"));
        pn.add(makeMenuButton("Hóa đơn"));
        pn.add(makeMenuButton("Thống kê doanh thu"));
        pn.add(makeMenuButton("Thống kê sản phẩm"));
        pn.add(makeMenuButton("Nhân viên"));
        pn.add(makeMenuButton("Chức vụ"));
        pn.add(makeMenuButton("Tài khoản"));

        return pn;
    }


    private JButton makeMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        return btn;
    }

    // ======================== MAIN PANEL ===================================
    private JPanel buildMainPanel() {
        JPanel pn = new JPanel(new BorderLayout());

        pn.add(buildTopInfo(), BorderLayout.NORTH);
        pn.add(buildCenterContent(), BorderLayout.CENTER);

        return pn;
    }

    // ======================== THÔNG TIN NHÂN VIÊN ============================
    private JPanel buildTopInfo() {

        JPanel pn = new JPanel(new GridLayout(1, 3));

        // Nhân viên
        JPanel pnNV = new JPanel(new GridLayout(2, 1));
        pnNV.setBorder(BorderFactory.createTitledBorder("Nhân viên"));
        lblTenNV = new JLabel("Tên: ...");
        lblChucVu = new JLabel("Chức vụ: ...");
        pnNV.add(lblTenNV);
        pnNV.add(lblChucVu);
     


        // Số đơn
        JPanel pnDon = new JPanel(new GridLayout(1, 1));
        pnDon.setBorder(BorderFactory.createTitledBorder("Số đơn hôm nay"));
        lblSoDon = new JLabel("0", SwingConstants.CENTER);
        lblSoDon.setFont(new Font("Segoe UI", Font.BOLD, 22));
        pnDon.add(lblSoDon);

        // Doanh thu
        JPanel pnDT = new JPanel(new GridLayout(1, 1));
        pnDT.setBorder(BorderFactory.createTitledBorder("Doanh thu hôm nay"));
        lblDoanhThu = new JLabel("0 VNĐ", SwingConstants.CENTER);
        lblDoanhThu.setFont(new Font("Segoe UI", Font.BOLD, 22));
        pnDT.add(lblDoanhThu);

        pn.add(pnNV);
        pn.add(pnDon);
        pn.add(pnDT);

        return pn;
    }

    // ========================= BẢNG LỊCH SỬ HD ===============================
    private JPanel buildCenterContent() {
        JPanel pn = new JPanel(new BorderLayout());

        JButton btnNewOrder = new JButton("Đơn hàng mới");
        btnNewOrder.setPreferredSize(new Dimension(160, 40));
        btnNewOrder.addActionListener(e -> openOrderGUI());

        pn.add(btnNewOrder, BorderLayout.NORTH);

        // Table
        String[] cols = {"Mã hóa đơn", "Tên nhân viên", "Tổng tiền", "Ngày lập"};
        model = new DefaultTableModel(cols, 0);
        tableHD = new JTable(model);

        JScrollPane sp = new JScrollPane(tableHD);
        pn.add(sp, BorderLayout.CENTER);

        return pn;
    }

    // ========================== LOAD DỮ LIỆU ================================

    private void loadThongTinNhanVien() {
        lblTenNV.setText("Tên: " + tkLogin.getNhanVien().getTenNV());
        lblChucVu.setText("Chức vụ: " + tkLogin.getChucVu().getTenLoai());
    }

    private void loadThongKe() {

        // Số đơn hôm nay
        int soDon = hdDAO.getSoDonHomNay();
        lblSoDon.setText(String.valueOf(soDon));

        // Doanh thu
        double tong = 0;
        List<String[]> list = hdDAO.getHoaDonGanNhat();

        for (String[] row : list) {
            String maHD = row[0];
            tong += cthdDAO.getTongTien(maHD);
        }

        lblDoanhThu.setText(String.format("%,.0f VNĐ", tong));
    }

    private void loadLichSuHoaDon() {

        model.setRowCount(0);

        List<String[]> ds = hdDAO.getHoaDonGanNhat();

        for (String[] row : ds) {
            String maHD = row[0];
            double total = cthdDAO.getTongTien(maHD);

            model.addRow(new Object[]{
                    maHD,
                    row[1],
                    String.format("%,.0f VNĐ", total),
                    row[2]
            });
        }
    }

    // ======================= CHUYỂN SANG BÁN HÀNG ============================
    private void openOrderGUI() {
        JOptionPane.showMessageDialog(this,
                "👉 Ở đây mở form Bán Hàng / Gọi Món của bạn.\nChỉ cần gọi new GoiMon_GUI().setVisible(true)");
    }

    

}
