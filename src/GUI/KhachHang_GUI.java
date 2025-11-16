package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import DAO.KhachHang_DAO;
import Entity.KhachHang;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class KhachHang_GUI extends JPanel {

    private JTextField txtMa, txtTen, txtSDT, txtEmail;
    private DefaultTableModel model;
    private JTable table;
    private KhachHang_DAO dao;

    private RoundedButton btnTimKiem;
    private RoundedButton btnLamMoi;
    private RoundedButton btnThem;
    private RoundedButton btnSua;
    private RoundedButton btnXoa;

    public KhachHang_GUI() {
        dao = new KhachHang_DAO();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ================= HEADER =================
        JPanel pNorth = new JPanel();
        pNorth.setBackground(new Color(0, 153, 76));
        JLabel lblTitle = new JLabel("QUẢN LÝ KHÁCH HÀNG", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        pNorth.add(lblTitle);
        add(pNorth, BorderLayout.NORTH);

        // =============== CENTER WRAPPER ===============
        JPanel centerWrap = new JPanel(new BorderLayout());
        centerWrap.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerWrap.setBackground(Color.WHITE);
        add(centerWrap, BorderLayout.CENTER);

        // =============== PANEL TRÊN: FORM + NÚT ===============
        JPanel pTop = new JPanel(new BorderLayout());
        pTop.setBackground(Color.WHITE);
        centerWrap.add(pTop, BorderLayout.NORTH);

        Box bxTop = new Box(BoxLayout.Y_AXIS);
        pTop.add(bxTop, BorderLayout.NORTH);

        // ---------- HÀNG 1: TÊN, SĐT, EMAIL ----------
        Box row1 = new Box(BoxLayout.X_AXIS);
        row1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel lblTen = new JLabel("Tên khách hàng:");
        lblTen.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTen.setPreferredSize(new Dimension(140, 30));
        txtTen = makeField(true);

        JLabel lblSDT = new JLabel("Số điện thoại:");
        lblSDT.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSDT.setPreferredSize(new Dimension(120, 30));
        txtSDT = makeField(true);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblEmail.setPreferredSize(new Dimension(80, 30));
        txtEmail = makeField(true);

        row1.add(lblTen);
        row1.add(txtTen);
        row1.add(Box.createHorizontalStrut(25));
        row1.add(lblSDT);
        row1.add(txtSDT);
        row1.add(Box.createHorizontalStrut(25));
        row1.add(lblEmail);
        row1.add(txtEmail);

        bxTop.add(row1);

        // ---------- HÀNG 2: MÃ + NÚT ----------
        Box row2 = new Box(BoxLayout.X_AXIS);
        row2.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));

        JLabel lblMa = new JLabel("Mã khách hàng:");
        lblMa.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblMa.setPreferredSize(new Dimension(140, 30));
        txtMa = makeField(false);

        btnTimKiem = new RoundedButton("Tìm kiếm", new Color(128, 128, 128));
        btnLamMoi  = new RoundedButton("Làm mới", new Color(128, 128, 128));
        btnThem    = new RoundedButton("Thêm", new Color(0, 102, 0));
        btnSua     = new RoundedButton("Sửa", new Color(0, 102, 204));
        btnXoa     = new RoundedButton("Xóa", new Color(153, 0, 0));

        Dimension btnSize = new Dimension(130, 32);

        row2.add(lblMa);
        row2.add(txtMa);

        row2.add(Box.createHorizontalStrut(25));
        btnTimKiem.setMaximumSize(btnSize);
        row2.add(btnTimKiem);

        row2.add(Box.createHorizontalStrut(15));
        btnLamMoi.setMaximumSize(btnSize);
        row2.add(btnLamMoi);

        row2.add(Box.createHorizontalStrut(15));
        btnThem.setMaximumSize(btnSize);
        row2.add(btnThem);

        row2.add(Box.createHorizontalStrut(15));
        btnSua.setMaximumSize(btnSize);
        row2.add(btnSua);

        row2.add(Box.createHorizontalStrut(15));
        btnXoa.setMaximumSize(btnSize);
        row2.add(btnXoa);

        bxTop.add(row2);

        // Giới hạn chiều cao vùng form + nút cho gọn, không phình to
        pTop.setPreferredSize(new Dimension(0, 130));

        // =============== TABLE DƯỚI ===============
        String[] cols = {"Mã KH", "Tên khách hàng", "Số điện thoại", "Email"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(24);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        header.setPreferredSize(new Dimension(header.getWidth(), 28));
        header.setBackground(new Color(220, 240, 255));

        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        renderer.setHorizontalAlignment(SwingConstants.LEFT);

        JScrollPane scroll = new JScrollPane(table);
        centerWrap.add(scroll, BorderLayout.CENTER);

        // =============== SỰ KIỆN ===============
        loadData();
        autoGenMa();

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fillForm();
            }
        });

        btnLamMoi.addActionListener(e -> {
            clearForm();
            autoGenMa();
            loadData();
        });

        btnThem.addActionListener(e -> addKH());
        btnSua.addActionListener(e -> updateKH());
        btnXoa.addActionListener(e -> deleteKH());
        btnTimKiem.addActionListener(e -> timKiem());
    }

    // ================== UI SUPPORT ==================
    private JTextField makeField(boolean editable) {
        JTextField txt = new JTextField();
        txt.setEditable(editable);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return txt;
    }

    private void loadData() {
        model.setRowCount(0);
        List<KhachHang> ds = dao.getAllKH();
        for (KhachHang kh : ds) {
            model.addRow(new Object[]{
                    kh.getMaKH(),
                    kh.getTenKH(),
                    kh.getsDT(),
                    kh.getEmail()
            });
        }
    }

    private void fillForm() {
        int r = table.getSelectedRow();
        if (r == -1) return;

        txtMa.setText(model.getValueAt(r, 0).toString());
        txtTen.setText(model.getValueAt(r, 1).toString());
        txtSDT.setText(model.getValueAt(r, 2).toString());
        Object emailObj = model.getValueAt(r, 3);
        txtEmail.setText(emailObj == null ? "" : emailObj.toString());
    }

    private void clearForm() {
        txtTen.setText("");
        txtSDT.setText("");
        txtEmail.setText("");
    }

    private void autoGenMa() {
        txtMa.setText(dao.phatSinhMaKH());
    }

    // ================== VALIDATION ==================
    private boolean validateForm() {
        String ten = txtTen.getText().trim();
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();

        if (ten.isEmpty() || sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Tên khách hàng và SĐT không được để trống!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!sdt.matches("^0\\d{8,10}$")) {
            JOptionPane.showMessageDialog(this,
                    "Số điện thoại không hợp lệ! (Bắt đầu bằng 0, dài 9–11 số)",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!email.isEmpty() && !email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,6}$")) {
            JOptionPane.showMessageDialog(this,
                    "Email không hợp lệ!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    // ================== CRUD ==================
    private void addKH() {
        if (!validateForm()) return;

        KhachHang kh = new KhachHang(
                txtMa.getText().trim(),
                txtTen.getText().trim(),
                txtSDT.getText().trim(),
                txtEmail.getText().trim().isEmpty() ? null : txtEmail.getText().trim()
        );

        if (dao.addKH(kh)) {
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!");
            loadData();
            clearForm();
            autoGenMa();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Thêm thất bại! (Có thể mã KH đã tồn tại)",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateKH() {
        int r = table.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this,
                    "Hãy chọn khách hàng cần sửa trên bảng!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (!validateForm()) return;

        KhachHang kh = new KhachHang(
                txtMa.getText().trim(),
                txtTen.getText().trim(),
                txtSDT.getText().trim(),
                txtEmail.getText().trim().isEmpty() ? null : txtEmail.getText().trim()
        );

        if (dao.updateKH(kh)) {
            JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thành công!");
            loadData();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Cập nhật thất bại!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteKH() {
        int r = table.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this,
                    "Hãy chọn khách hàng cần xóa trên bảng!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String maKH = model.getValueAt(r, 0).toString();

        int opt = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa khách hàng " + maKH + " ?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (opt != JOptionPane.YES_OPTION) return;

        if (dao.deleteKH(maKH)) {
            JOptionPane.showMessageDialog(this, "Xóa khách hàng thành công!");
            loadData();
            clearForm();
            autoGenMa();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Không thể xóa! (Có thể KH đã phát sinh hóa đơn)",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================== TÌM KIẾM (XỬ LÝ Ở GUI) ==================
    private void timKiem() {
        String ma = txtMa.getText().trim().toLowerCase();
        String ten = txtTen.getText().trim().toLowerCase();
        String sdt = txtSDT.getText().trim().toLowerCase();
        String email = txtEmail.getText().trim().toLowerCase();

        List<KhachHang> ds = dao.getAllKH();
        model.setRowCount(0);

        for (KhachHang kh : ds) {
            boolean match = true;

            if (!ma.isEmpty() && (kh.getMaKH() == null || !kh.getMaKH().toLowerCase().contains(ma)))
                match = false;

            if (!ten.isEmpty() && (kh.getTenKH() == null || !kh.getTenKH().toLowerCase().contains(ten)))
                match = false;

            if (!sdt.isEmpty() && (kh.getsDT() == null || !kh.getsDT().toLowerCase().contains(sdt)))
                match = false;

            String em = kh.getEmail() == null ? "" : kh.getEmail();
            if (!email.isEmpty() && !em.toLowerCase().contains(email))
                match = false;

            if (match) {
                model.addRow(new Object[]{
                        kh.getMaKH(),
                        kh.getTenKH(),
                        kh.getsDT(),
                        kh.getEmail()
                });
            }
        }

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy khách hàng phù hợp!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ================== ROUNDED BUTTON ==================
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
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setPreferredSize(new Dimension(120, 30));
            setBackground(bgColor);
            setForeground(Color.WHITE);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBackground(hoverColor);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(backgroundColor);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        public void updateUI() {
            setUI(new javax.swing.plaf.basic.BasicButtonUI());
        }
    }
}
