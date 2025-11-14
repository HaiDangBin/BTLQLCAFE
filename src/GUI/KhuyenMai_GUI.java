package GUI;

import DAO.KhuyenMai_DAO;
import Entity.KhuyenMai;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/** KhuyenMai_GUI – bố cục & màu sắc bám sát ảnh mẫu. */
public class KhuyenMai_GUI extends JPanel {

    private final KhuyenMai_DAO dao;

    /* Top filters (theo ảnh) */
    private final JTextField tfMa   = new JTextField();
    private final JTextField tfMoTa = new JTextField();
    private final JTextField tfNgayBD = new JTextField();   // yyyy-MM-dd
    private final JTextField tfNgayKT = new JTextField();   // yyyy-MM-dd
    private final JComboBox<String> cbTrangThai =
            new JComboBox<>(new String[]{"Hoạt động", "Ngưng áp dụng"});

    /* Buttons on the right of filters (theo ảnh) */
    private final JButton btnTimKiem = makeBtn("Tìm kiếm",  new Color(0x2E7D32), Color.WHITE);
    private final JButton btnCapNhat = makeBtnIcon(" Cập nhật", "/image/edit.png", new Color(0xFF8A65));
    private final JButton btnTaoMoi  = makeBtnIcon(" Tạo mới", "/image/add.png",  new Color(0x64B5F6));
    private final JButton btnLamMoi  = makeBtnIcon(" Làm mới", "/image/refresh.png", new Color(0x90CAF9));
    private final JButton btnTroVe   = makeBtnIcon(" Trở về", "/image/back.png", new Color(0xB0BEC5));

    /* Table */
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Mã giảm giá", "Mô tả", "Chiết khấu", "Ngày bắt đầu", "Ngày hết hạn", "Số lượng", "Đã sử dụng", "Trạng thái"},
            0
    ) { /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	@Override public boolean isCellEditable(int r, int c) { return false; } };
    private final JTable table = buildStyledTable(model);

    /* Pagination (giống ảnh) – demo */
    private final JLabel lbPage = new JLabel("1 / 1", SwingConstants.CENTER);
    private final JButton btnFirst = pageBtn("|<");
    private final JButton btnPrev  = pageBtn("<<");
    private final JButton btnNext  = pageBtn(">>");
    private final JButton btnLast  = pageBtn(">|");

    public KhuyenMai_GUI() {
    	this.dao = new KhuyenMai_DAO();
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel card = new JPanel(new BorderLayout(10,10));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(10,10,10,10));
        add(card, BorderLayout.CENTER);

        card.add(buildHeader(), BorderLayout.NORTH);
        card.add(buildCenter(), BorderLayout.CENTER);
        card.add(buildPager(), BorderLayout.SOUTH);

        hookActions();
        loadData();
    }

    /* ================= Header (màu teal + tiêu đề) ================= */

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0x26A69A)); // teal
        header.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel title = new JLabel("Quản Lý Khuyến Mãi", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));

        header.add(title, BorderLayout.CENTER);
        return header;
    }

    /* ================= Center (filters + table) ================= */

    private JComponent buildCenter() {
        JPanel center = new JPanel(new BorderLayout(8,8));
        center.setOpaque(false);

        center.add(buildFiltersBar(), BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        return center;
    }

    private JComponent buildFiltersBar() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6,6,6,6);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridy = 0;

        // Cột trái (mã, mô tả)
        int c = 0;
        g.gridx = c++; p.add(new JLabel("Mã giảm giá:"), g);
        g.gridx = c++; g.weightx = 1; p.add(tfMa, g); g.weightx = 0;

        g.gridx = c++; p.add(new JLabel("Mô tả:"), g);
        g.gridx = c++; g.weightx = 1; p.add(tfMoTa, g); g.weightx = 0;

        // Dòng 2
        g.gridy = 1; c = 0;

        JPanel dateStart = fieldWithIcon(tfNgayBD, "/image/calendar.png");
        JPanel dateEnd   = fieldWithIcon(tfNgayKT, "/image/calendar.png");

        g.gridx = c++; p.add(new JLabel("Ngày bắt đầu:"), g);
        g.gridx = c++; g.weightx = 1; p.add(dateStart, g); g.weightx = 0;

        g.gridx = c++; p.add(new JLabel("Trạng thái:"), g);
        g.gridx = c++; g.weightx = 1; p.add(cbTrangThai, g); g.weightx = 0;

        // Nút ở bên phải
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        buttons.add(btnTimKiem);
        buttons.add(btnLamMoi);
        buttons.add(btnCapNhat);
        buttons.add(btnTaoMoi);
        buttons.add(btnTroVe);

        g.gridx = c++; p.add(new JLabel(""), g); // spacer
        g.gridx = c++; g.weightx = 1; p.add(dateEnd, g); g.weightx = 0;
        g.gridx = c++; g.gridwidth = 2; p.add(buttons, g); g.gridwidth = 1;

        return p;
    }

    /* ================= Pagination (demo) ================= */

    private JComponent buildPager() {
        JPanel pager = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 4));
        pager.setOpaque(false);
        pager.add(btnFirst);
        pager.add(btnPrev);
        pager.add(lbPage);
        pager.add(btnNext);
        pager.add(btnLast);
        return pager;
    }

    /* ================= Actions & Data ================= */

    private void hookActions() {
        btnTimKiem.addActionListener(e -> applyFilters());
        btnLamMoi.addActionListener(e -> { tfMa.setText(""); tfMoTa.setText(""); tfNgayBD.setText(""); tfNgayKT.setText(""); cbTrangThai.setSelectedIndex(0); loadData(); });
        btnTaoMoi.addActionListener(e -> onAdd());
        btnCapNhat.addActionListener(e -> onEdit());
        // btnTroVe: đóng frame chứa nếu có
        btnTroVe.addActionListener(e -> {
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w instanceof JFrame) ((JFrame) w).dispose();
        });

        // Phân trang demo (chưa chia trang dữ liệu)
        btnFirst.addActionListener(e -> lbPage.setText("1 / 1"));
        btnPrev.addActionListener(e -> lbPage.setText("1 / 1"));
        btnNext.addActionListener(e -> lbPage.setText("1 / 1"));
        btnLast.addActionListener(e -> lbPage.setText("1 / 1"));
    }

    private void loadData() {
        try {
            List<KhuyenMai> list = dao.findAll();
            fillTable(list);
        } catch (Exception ex) {
            error(ex);
        }
    }

    private void fillTable(List<KhuyenMai> list) {
        model.setRowCount(0);
        for (KhuyenMai k : list) {
            model.addRow(new Object[]{
                    n(k.getMaKM()),
                    n(k.getTenKM() == null ? k.getMoTa() : k.getTenKM()),
                    "", // chưa có cột trong entity
                    k.getNgayBD() == null ? "" : k.getNgayBD().toString(),
                    k.getNgayKT() == null ? "" : k.getNgayKT().toString(),
                    "", // số lượng
                    "", // đã sử dụng
                    n(k.getDieuKienApDung()) // tạm hiển thị ở cột Trạng thái
            });
        }
    }

    private void applyFilters() {
        String qMa   = tfMa.getText().trim().toLowerCase();
        String qMoTa = tfMoTa.getText().trim().toLowerCase();
        String from  = tfNgayBD.getText().trim();
        String to    = tfNgayKT.getText().trim();
        String trangThai = String.valueOf(cbTrangThai.getSelectedItem()).toLowerCase();

        try {
            List<KhuyenMai> list = dao.findAll();
            list.removeIf(k -> {
                boolean ok =
                        (qMa.isEmpty()   || (k.getMaKM()!=null  && k.getMaKM().toLowerCase().contains(qMa))) &&
                        (qMoTa.isEmpty() || ((k.getTenKM()!=null && k.getTenKM().toLowerCase().contains(qMoTa))
                                           ||(k.getMoTa()!=null && k.getMoTa().toLowerCase().contains(qMoTa))));
                if (!ok) return true;

                // lọc trạng thái (dùng dieuKienApDung tạm thời)
                if (!trangThai.isBlank() && k.getDieuKienApDung()!=null) {
                    String dk = k.getDieuKienApDung().toLowerCase();
                    if (!dk.contains(trangThai.equals("hoạt động") ? "hoạt" : "ngưng")
                        && !dk.contains(trangThai)) return true;
                }

                if (from.isEmpty() && to.isEmpty()) return false;

                LocalDate f = from.isEmpty()? null : LocalDate.parse(from);
                LocalDate t = to.isEmpty()?   null : LocalDate.parse(to);

                LocalDate bd = k.getNgayBD()==null ? LocalDate.MIN : k.getNgayBD();
                LocalDate kt = k.getNgayKT()==null ? LocalDate.MAX : k.getNgayKT();

                if (f != null && kt.isBefore(f)) return true;
                if (t != null && bd.isAfter(t))  return true;
                return false;
            });
            fillTable(list);
        } catch (Exception ex) { error(ex); }
    }

    /* ================= CRUD (sử dụng dialog đơn giản) ================= */

    private void onAdd() {
        KhuyenMai km = inputDialog(null);
        if (km == null) return;
        try {
            if (dao.insert(km)) {
                loadData();
                info("Đã thêm khuyến mãi.");
            } else warn("Thêm không thành công.");
        } catch (Exception ex) { error(ex); }
    }

    private void onEdit() {
        int r = table.getSelectedRow();
        if (r < 0) { warn("Chọn dòng cần sửa."); return; }
        KhuyenMai old = new KhuyenMai();
        old.setMaKM(String.valueOf(model.getValueAt(r,0)));
        old.setTenKM(String.valueOf(model.getValueAt(r,1)));
        old.setNgayBD(s2d(model.getValueAt(r,3)));
        old.setNgayKT(s2d(model.getValueAt(r,4)));
        old.setMoTa(String.valueOf(model.getValueAt(r,1)));
        old.setDieuKienApDung(String.valueOf(model.getValueAt(r,7)));

        KhuyenMai km = inputDialog(old);
        if (km == null) return;

        try {
            if (dao.update(km)) {
                loadData();
                info("Đã cập nhật.");
            } else warn("Cập nhật không thành công.");
        } catch (Exception ex) { error(ex); }
    }

    private KhuyenMai inputDialog(KhuyenMai initial) {
        JTextField tfId  = new JTextField(initial==null?"":n(initial.getMaKM()));
        JTextField tfTen = new JTextField(initial==null?"":n(initial.getTenKM()));
        JTextField tfMo  = new JTextField(initial==null?"":n(initial.getMoTa()));
        JTextField tfBd  = new JTextField(initial==null||initial.getNgayBD()==null?"":initial.getNgayBD().toString());
        JTextField tfKt  = new JTextField(initial==null||initial.getNgayKT()==null?"":initial.getNgayKT().toString());
        JTextField tfDk  = new JTextField(initial==null?"":n(initial.getDieuKienApDung()));

        JPanel p = new JPanel(new GridLayout(0,2,6,6));
        p.add(new JLabel("Mã KM:")); p.add(tfId);
        p.add(new JLabel("Tên KM:")); p.add(tfTen);
        p.add(new JLabel("Mô tả:")); p.add(tfMo);
        p.add(new JLabel("Ngày bắt đầu yyyy-MM-dd:")); p.add(tfBd);
        p.add(new JLabel("Ngày hết hạn yyyy-MM-dd:")); p.add(tfKt);
        p.add(new JLabel("Trạng thái/Điều kiện:")); p.add(tfDk);

        if (JOptionPane.showConfirmDialog(this, p, initial==null?"Tạo mới":"Cập nhật",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return null;

        KhuyenMai k = new KhuyenMai();
        k.setMaKM(tfId.getText().trim());
        k.setTenKM(tfTen.getText().trim());
        k.setMoTa(tfMo.getText().trim());
        k.setNgayBD(tfBd.getText().isBlank()? null : LocalDate.parse(tfBd.getText().trim()));
        k.setNgayKT(tfKt.getText().isBlank()? null : LocalDate.parse(tfKt.getText().trim()));
        k.setDieuKienApDung(tfDk.getText().trim());
        return k;
    }

    /* ================= Helpers ================= */

    private static JTable buildStyledTable(DefaultTableModel m) {
        JTable t = new JTable(m) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int column) {
                Component c = super.prepareRenderer(r, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? new Color(245,245,245) : new Color(235,235,235));
                } else c.setBackground(new Color(220,235,255));
                return c;
            }
        };
        t.setRowHeight(26);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTableHeader h = t.getTableHeader();
        h.setPreferredSize(new Dimension(h.getPreferredSize().width, 28));
        h.setDefaultRenderer(new DefaultTableCellRenderer() {
            { setOpaque(true); setHorizontalAlignment(CENTER); setFont(new Font("Segoe UI", Font.BOLD, 14)); }
            @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setBackground(new Color(0x80CBC4)); // teal nhạt
                setForeground(Color.DARK_GRAY);
                return this;
            }
        });
        // width sơ bộ
        int[] w = {120, 320, 100, 130, 130, 90, 90, 120};
        for (int i = 0; i < w.length; i++) t.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        t.getColumnModel().getColumn(2).setCellRenderer(center);
        t.getColumnModel().getColumn(3).setCellRenderer(center);
        t.getColumnModel().getColumn(4).setCellRenderer(center);
        t.getColumnModel().getColumn(5).setCellRenderer(center);
        t.getColumnModel().getColumn(6).setCellRenderer(center);
        t.getColumnModel().getColumn(7).setCellRenderer(center);
        return t;
    }

    private static JPanel fieldWithIcon(JTextField tf, String iconPath) {
        JButton b = new JButton();
        b.setFocusable(false);
        b.setPreferredSize(new Dimension(34, tf.getPreferredSize().height));
        b.setIcon(loadIcon(iconPath, 16, 16));
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(tf, BorderLayout.CENTER);
        wrap.add(b, BorderLayout.EAST);
        return wrap;
    }

    private static JButton makeBtn(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBorder(new EmptyBorder(6,14,6,14));
        return b;
    }
    private static JButton makeBtnIcon(String text, String icon, Color bg) {
        JButton b = makeBtn(text, bg, Color.WHITE);
        b.setIcon(loadIcon(icon, 18, 18));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        return b;
    }
    private static JButton pageBtn(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(new Color(245,245,245));
        b.setBorder(new EmptyBorder(6,14,6,14));
        return b;
    }
    private static ImageIcon loadIcon(String path, int w, int h) {
        try {
            if (path == null) return null;
            java.net.URL u = KhuyenMai_GUI.class.getResource(path);
            if (u == null) return null;
            Image img = new ImageIcon(u).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception ignored) { return null; }
    }

    private static String n(Object o){ return o==null? "" : String.valueOf(o); }
    private static LocalDate s2d(Object o){ return (o==null||String.valueOf(o).isBlank())? null : LocalDate.parse(String.valueOf(o)); }

    private void info(String m){ JOptionPane.showMessageDialog(this, m, "Thông báo", JOptionPane.INFORMATION_MESSAGE); }
    private void warn(String m){ JOptionPane.showMessageDialog(this, m, "Cảnh báo", JOptionPane.WARNING_MESSAGE); }
    private void error(Exception e){ e.printStackTrace(); JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); }
}
