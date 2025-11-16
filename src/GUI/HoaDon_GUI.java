package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;

import DAO.ChiTietHoaDon_DAO;
import DAO.HoaDon_DAO;
import Entity.HoaDon;
import Entity.TaiKhoan;
import connectDB.DBconnection;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

public class HoaDon_GUI extends JPanel {

    private TaiKhoan tkLogin;

    private JTable tableHD;
    private DefaultTableModel modelHD;

    private JTable tableCT;
    private DefaultTableModel modelCT;

    private JTextField txtSearch;

    HoaDon_DAO hdDAO = new HoaDon_DAO();
    ChiTietHoaDon_DAO ctDAO = new ChiTietHoaDon_DAO();

    public HoaDon_GUI() {
        setLayout(new BorderLayout());

        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildMainPanel(), BorderLayout.CENTER);

        loadHoaDon();
    }

    // ====================== HEADER ======================
    private JPanel buildTopPanel() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(0,153,76));
        top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lbl = new JLabel("QUẢN LÝ HÓA ĐƠN", JLabel.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lbl.setForeground(Color.WHITE);
        top.add(lbl, BorderLayout.CENTER);

        return top;
    }

    // ====================== MAIN PANEL ======================
    private JPanel buildMainPanel() {
        JPanel pn = new JPanel(new BorderLayout());

        pn.add(buildFunctionPanel(), BorderLayout.NORTH);
        pn.add(buildContentPanel(), BorderLayout.CENTER);

        return pn;
    }

    // ====================== FUNCTION PANEL ======================
    private JPanel buildFunctionPanel() {
        JPanel fn = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        fn.setBackground(Color.WHITE);

        RoundedButton btnNewHD   = new RoundedButton("Lập hóa đơn", new Color(0,102,0));
        RoundedButton btnEditHD  = new RoundedButton("Sửa", new Color(0,102,102));
        RoundedButton btnDeleteHD= new RoundedButton("Xóa", new Color(153,0,0));
        RoundedButton btnReload  = new RoundedButton("Tải lại", new Color(102,102,102));
        RoundedButton btnSearch  = new RoundedButton("Tìm", new Color(0,102,204));

        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtSearch.setPreferredSize(new Dimension(220, 32));

        fn.add(btnNewHD);
        fn.add(btnEditHD);
        fn.add(btnDeleteHD);
        fn.add(btnReload);
        fn.add(new JLabel("Tìm:"));
        fn.add(txtSearch);
        fn.add(btnSearch);

        btnNewHD.addActionListener(e -> openNewHoaDonDialog());
        btnEditHD.addActionListener(e -> openEditHoaDonDialog());
        btnDeleteHD.addActionListener(e -> deleteHoaDon());
        btnReload.addActionListener(e -> loadHoaDon());
        btnSearch.addActionListener(e -> searchHoaDon());

        return fn;
    }

    // ====================== CONTENT PANEL ======================
    private JPanel buildContentPanel() {
        JPanel pn = new JPanel(new GridLayout(2, 1));
        pn.setBackground(Color.WHITE);

        // ----------- BẢNG HÓA ĐƠN -----------
        String[] colsHD = {
            "Mã HD", "Mã NV", "Mã KH",
            "Mã KM", "Mã Đặt Bàn", "Ngày lập", "Tổng tiền"
        };

        modelHD = new DefaultTableModel(colsHD, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tableHD = new JTable(modelHD);
        decorateTable(tableHD);

        tableHD.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int r = tableHD.getSelectedRow();
                if (r >= 0) {
                    loadChiTietHD(modelHD.getValueAt(r, 0).toString());
                }
            }
        });

        pn.add(new JScrollPane(tableHD));

        // ----------- BẢNG CHI TIẾT -----------
        String[] colsCT = {
            "Mã SP", "Tên SP", "Số lượng",
            "Đơn giá", "Thành tiền",
            "Nhân viên", "Khách hàng"
        };

        modelCT = new DefaultTableModel(colsCT, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tableCT = new JTable(modelCT);
        decorateTable(tableCT);

        pn.add(new JScrollPane(tableCT));

        return pn;
    }

    // ====================== DECORATE TABLE ======================
    private void decorateTable(JTable table) {
        table.setRowHeight(24);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setGridColor(new Color(220, 220, 220));

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0,153,76));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setCellRenderer(center);
    }

    // ====================== LOAD HÓA ĐƠN ======================
    public void loadHoaDon() {
        modelHD.setRowCount(0);
        List<HoaDon> ds = hdDAO.getAll();

        for (HoaDon hd : ds) {
            double total = ctDAO.getTongTien(hd.getMaHD());
            modelHD.addRow(new Object[]{
                hd.getMaHD(),
                hd.getMaNV(),
                hd.getMaKH(),
                hd.getMaKM(),
                hd.getMaDatBan(),
                hd.getNgayLap(),
                String.format("%,.0f VNĐ", total)
            });
        }

        modelCT.setRowCount(0);
    }

    // ====================== LOAD CHI TIẾT ======================
    private void loadChiTietHD(String maHD) {
        modelCT.setRowCount(0);

        String sql = """
            SELECT 
                ChiTietHD.maSP,
                SanPham.tenSP,
                ChiTietHD.soLuongSP,
                ChiTietHD.donGia,
                (ChiTietHD.soLuongSP * ChiTietHD.donGia) AS thanhTien,
                NhanVien.tenNV,
                KhachHang.tenKH
            FROM ChiTietHD
            JOIN SanPham ON ChiTietHD.maSP = SanPham.maSP
            JOIN HoaDon ON ChiTietHD.maHD = HoaDon.maHD
            LEFT JOIN NhanVien ON HoaDon.maNV = NhanVien.maNV
            LEFT JOIN KhachHang ON HoaDon.maKH = KhachHang.maKH
            WHERE ChiTietHD.maHD = ?
        """;

        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maHD);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                modelCT.addRow(new Object[]{
                    rs.getString("maSP"),
                    rs.getString("tenSP"),
                    rs.getInt("soLuongSP"),
                    String.format("%,.0f VNĐ", rs.getDouble("donGia")),
                    String.format("%,.0f VNĐ", rs.getDouble("thanhTien")),
                    rs.getString("tenNV"),
                    rs.getString("tenKH")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ====================== SEARCH ======================
    private void searchHoaDon() {
        String key = txtSearch.getText().trim().toLowerCase();
        modelHD.setRowCount(0);

        for (HoaDon hd : hdDAO.getAll()) {
            if (hd.getMaHD().toLowerCase().contains(key)
                    || hd.getMaNV().toLowerCase().contains(key)
                    || (hd.getMaDatBan() != null && hd.getMaDatBan().toLowerCase().contains(key))) {

                double total = ctDAO.getTongTien(hd.getMaHD());
                modelHD.addRow(new Object[]{
                    hd.getMaHD(),
                    hd.getMaNV(),
                    hd.getMaKH(),
                    hd.getMaKM(),
                    hd.getMaDatBan(),
                    hd.getNgayLap(),
                    String.format("%,.0f VNĐ", total)
                });
            }
        }

        modelCT.setRowCount(0);
    }

    // ====================== VALIDATE ======================
    private String validateHoaDon(String maHD, String ngay, String maNV,
                                  String maKH, String maKM, String maDatBan,
                                  boolean isNew) {

        if (maHD.isEmpty() || ngay.isEmpty() || maNV.isEmpty()) {
            return "Mã hóa đơn, ngày lập và mã nhân viên không được để trống!";
        }

        if (!Pattern.matches("^HD\\d{3,}$", maHD)) {
            return "Mã hóa đơn phải dạng HDxxx.";
        }

        if (!Pattern.matches("^\\d{4}-\\d{2}-\\d{2}$", ngay)) {
            return "Ngày lập phải yyyy-MM-dd.";
        }

        if (!Pattern.matches("^NV\\d{2,}$", maNV)) {
            return "Mã NV phải dạng NVxx.";
        }

        if (!maKH.isEmpty() && !Pattern.matches("^KH\\d{2,}$", maKH)) {
            return "Mã KH phải dạng KHxx.";
        }

        if (!maKM.isEmpty() && !Pattern.matches("^KM\\d{2,}$", maKM)) {
            return "Mã KM phải dạng KMxx.";
        }

        if (!maDatBan.isEmpty() && !Pattern.matches("^DDB\\d{2,}$", maDatBan)) {
            return "Mã đặt bàn phải dạng DDBxx.";
        }

        if (isNew && hdDAO.findById(maHD) != null) {
            return "Mã hóa đơn đã tồn tại!";
        }

        return null;
    }

    // ====================== POPUP THÊM ======================
    private void openNewHoaDonDialog() {
        JDialog dialog = new JDialog((Frame)null, "Lập hóa đơn mới", true);
        dialog.setSize(420, 360);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(new GridLayout(7, 2, 10, 10));
        dialog.setResizable(false);

        JTextField txtMaHD = new JTextField();
        JTextField txtNgay = new JTextField(java.time.LocalDate.now().toString());

        JTextField txtMaNV = new JTextField();
        JTextField txtMaKH = new JTextField();
        JTextField txtMaKM = new JTextField();
        JTextField txtMaDatBan = new JTextField();

        JButton btnCreate = new JButton("Tạo hóa đơn");

        dialog.add(new JLabel("Mã hóa đơn:"));
        dialog.add(txtMaHD);

        dialog.add(new JLabel("Ngày lập:"));
        dialog.add(txtNgay);

        dialog.add(new JLabel("Mã NV:"));
        dialog.add(txtMaNV);

        dialog.add(new JLabel("Mã KH:"));
        dialog.add(txtMaKH);

        dialog.add(new JLabel("Mã KM:"));
        dialog.add(txtMaKM);

        dialog.add(new JLabel("Mã Đặt Bàn:"));
        dialog.add(txtMaDatBan);

        dialog.add(new JLabel());
        dialog.add(btnCreate);

        btnCreate.addActionListener(e -> {
            String maHD = txtMaHD.getText().trim();
            String ngay = txtNgay.getText().trim();
            String maNV = txtMaNV.getText().trim();
            String maKH = txtMaKH.getText().trim();
            String maKM = txtMaKM.getText().trim();
            String maDB = txtMaDatBan.getText().trim();

            String err = validateHoaDon(maHD, ngay, maNV, maKH, maKM, maDB, true);
            if (err != null) {
                JOptionPane.showMessageDialog(dialog, err, "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            java.sql.Date sqlDate = java.sql.Date.valueOf(LocalDate.parse(ngay));

            HoaDon hd = new HoaDon(maHD, maNV, maKH, maKM, maDB, sqlDate);

            if (hdDAO.insert(hd)) {
                JOptionPane.showMessageDialog(dialog, "Tạo hóa đơn thành công!");
                loadHoaDon();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Tạo hóa đơn thất bại!");
            }
        });

        dialog.setVisible(true);
    }

    // ====================== POPUP SỬA ======================
    private void openEditHoaDonDialog() {
        int row = tableHD.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn hóa đơn cần sửa!");
            return;
        }

        String maHD = modelHD.getValueAt(row, 0).toString();
        HoaDon hdOld = hdDAO.findById(maHD);
        if (hdOld == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn!");
            return;
        }

        JDialog dialog = new JDialog((Frame)null, "Sửa hóa đơn", true);
        dialog.setSize(420, 360);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(new GridLayout(7, 2, 10, 10));
        dialog.setResizable(false);

        JTextField txtMaHD = new JTextField(hdOld.getMaHD());
        txtMaHD.setEditable(false);

        JTextField txtNgay = new JTextField(hdOld.getNgayLap().toString());
        JTextField txtMaNV = new JTextField(hdOld.getMaNV());
        JTextField txtMaKH = new JTextField(hdOld.getMaKH());
        JTextField txtMaKM = new JTextField(hdOld.getMaKM());
        JTextField txtMaDatBan = new JTextField(hdOld.getMaDatBan());

        JButton btnSave = new JButton("Lưu thay đổi");

        dialog.add(new JLabel("Mã hóa đơn:"));
        dialog.add(txtMaHD);

        dialog.add(new JLabel("Ngày lập:"));
        dialog.add(txtNgay);

        dialog.add(new JLabel("Mã NV:"));
        dialog.add(txtMaNV);

        dialog.add(new JLabel("Mã KH:"));
        dialog.add(txtMaKH);

        dialog.add(new JLabel("Mã KM:"));
        dialog.add(txtMaKM);

        dialog.add(new JLabel("Mã Đặt Bàn:"));
        dialog.add(txtMaDatBan);

        dialog.add(new JLabel());
        dialog.add(btnSave);

        btnSave.addActionListener(e -> {
            String ngay = txtNgay.getText().trim();
            String maNV = txtMaNV.getText().trim();
            String maKH = txtMaKH.getText().trim();
            String maKM = txtMaKM.getText().trim();
            String maDB = txtMaDatBan.getText().trim();

            String err = validateHoaDon(maHD, ngay, maNV, maKH, maKM, maDB, false);
            if (err != null) {
                JOptionPane.showMessageDialog(dialog, err, "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            java.sql.Date sqlDate = java.sql.Date.valueOf(LocalDate.parse(ngay));
            HoaDon hd = new HoaDon(maHD, maNV, maKH, maKM, maDB, sqlDate);

            if (hdDAO.update(hd)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                loadHoaDon();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
            }
        });

        dialog.setVisible(true);
    }

    // ====================== XÓA ======================
    private void deleteHoaDon() {
        int row = tableHD.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn hóa đơn cần xóa!");
            return;
        }

        String maHD = modelHD.getValueAt(row, 0).toString();

        int opt = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa hóa đơn " + maHD + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (opt != JOptionPane.YES_OPTION) return;

        if (hdDAO.delete(maHD)) {
            JOptionPane.showMessageDialog(this, "Xóa thành công!");
            loadHoaDon();
        } else {
            JOptionPane.showMessageDialog(this, "Xóa thất bại!");
        }
    }

    // ====================== BUTTON DECORATION ======================
    class RoundedButton extends JButton {
        private Color backgroundColor;
        private Color hoverColor;

        public RoundedButton(String text, Color bgColor) {
            super(text);
            this.backgroundColor = bgColor;
            this.hoverColor = bgColor.darker();

            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 15));
            setPreferredSize(new Dimension(130, 35));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent evt) {
                    setBackground(hoverColor);
                }
                @Override
                public void mouseExited(MouseEvent evt) {
                    setBackground(backgroundColor);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        public void updateUI() {
            setUI(new javax.swing.plaf.basic.BasicButtonUI());
        }
    }

}
