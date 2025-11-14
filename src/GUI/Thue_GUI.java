package GUI;

import DAO.Thue_DAO;
import DAO.Thue_DAO.Thue;
import connectDB.DBconnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** QUẢN LÝ THUẾ – bố cục: ô nhập bên trái, nút bên phải */
public class Thue_GUI extends JPanel {

    private final Thue_DAO dao;
    private final Connection conn;

    // ====== Model/Bảng ======
    private final DefaultTableModel model;
    private final JTable table;

    // ====== Ô nhập bộ lọc (trên) ======
    private final JTextField tfMaFilter    = new JTextField();
    private final JTextField tfTenFilter   = new JTextField();
    private final JTextField tfApDungF     = new JTextField(); // yyyy-MM-dd
    private final JTextField tfKetThucF    = new JTextField(); // yyyy-MM-dd
    private final JTextField tfDoiTuongF   = new JTextField();
    private final JTextField tfLoaiF       = new JTextField();

    // ====== Ô nhập CRUD (dùng chung với bộ lọc) ======
    private final JTextField tfMa    = tfMaFilter;
    private final JTextField tfTen   = tfTenFilter;
    private final JTextField tfApDung= tfApDungF;
    private final JTextField tfKetThuc=tfKetThucF;
    private final JTextField tfDoiTuong=tfDoiTuongF;
    private final JTextField tfLoai  = tfLoaiF;

    // ====== Nút ======
    private final JButton btnTim     = makeBtn("Tìm kiếm", 0x198754);
    private final JButton btnMoi     = makeBtn("Làm mới",  0x0D6EFD);
    private final JButton btnCapNhat = makeBtn("Cập nhật", 0x20C997);
    private final JButton btnTaoMoi  = makeBtn("Tạo mới",  0x6F42C1);
    private final JButton btnXoa     = makeBtn("Xóa",      0xDC3545);

    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Thue_GUI() {
    	Connection staticConn = DBconnection.getConnection();
    	this.conn = staticConn;// Lớp ConnectDB của bạn
        this.dao = new Thue_DAO(staticConn);

        setLayout(new BorderLayout());
        setOpaque(false);

        // ===== Header =====
        add(buildHeader(), BorderLayout.NORTH);

        // ===== Trung tâm: thanh nhập + bảng =====
        JPanel center = new JPanel(new BorderLayout(8,8));
        center.setBorder(new EmptyBorder(8,8,8,8));
        add(center, BorderLayout.CENTER);

        center.add(buildTopBar(), BorderLayout.NORTH);

        // ===== Bảng =====
        model = new DefaultTableModel(new Object[]{
                "Mã thuế", "Tên thuế", "Đối tượng", "Loại thuế",
                "Ngày áp dụng", "Ngày kết thúc"
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = styledTable(model);
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== Sự kiện =====
        hookActions();
        reload();
        setKeyStrokes();
    }

    /* ===================== UI PARTS ===================== */

    private JComponent buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(0x009970)); // xanh teal
        JLabel lb = new JLabel("QUẢN LÝ THUẾ", SwingConstants.CENTER);
        lb.setForeground(Color.WHITE);
        lb.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lb.setBorder(new EmptyBorder(10,0,10,0));
        p.add(lb, BorderLayout.CENTER);
        return p;
    }

    /** Thanh nhập liệu bên trái & cụm nút bên phải */
    private JComponent buildTopBar() {
        JPanel wrap = new JPanel(new BorderLayout());

        // Trái: 2 hàng × 6 cột (label-field)
        JPanel left = new JPanel(new GridBagLayout());
        left.setOpaque(false);
        left.setBorder(new EmptyBorder(4,4,4,4));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4,6,4,6);
        g.fill = GridBagConstraints.HORIZONTAL;

        int c = 0, r = 0;
        // Hàng 1
        g.gridy = r;
        g.gridx = c++; left.add(new JLabel("Mã thuế:"), g);
        g.gridx = c++; g.weightx = 1; left.add(tfMaFilter, g); g.weightx = 0;

        g.gridx = c++; left.add(new JLabel("Tên thuế:"), g);
        g.gridx = c++; g.weightx = 1; left.add(tfTenFilter, g); g.weightx = 0;

        g.gridx = c++; left.add(new JLabel("Đối tượng:"), g);
        g.gridx = c++; g.weightx = 1; left.add(tfDoiTuongF, g); g.weightx = 0;

        // Hàng 2
        r++; c = 0;
        g.gridy = r;
        g.gridx = c++; left.add(new JLabel("Ngày áp dụng (yyyy-MM-dd):"), g);
        g.gridx = c++; g.weightx = 1; left.add(tfApDungF, g); g.weightx = 0;

        g.gridx = c++; left.add(new JLabel("Ngày kết thúc:"), g);
        g.gridx = c++; g.weightx = 1; left.add(tfKetThucF, g); g.weightx = 0;

        g.gridx = c++; left.add(new JLabel("Loại thuế:"), g);
        g.gridx = c++; g.weightx = 1; left.add(tfLoaiF, g); g.weightx = 0;

        wrap.add(left, BorderLayout.CENTER);

        // Phải: các nút (căn phải)
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(btnTim);
        right.add(btnMoi);
        right.add(btnCapNhat);
        right.add(btnTaoMoi);
        right.add(btnXoa);

        wrap.add(right, BorderLayout.EAST);
        return wrap;
    }

    private JTable styledTable(DefaultTableModel m) {
        JTable t = new JTable(m) {
            @Override public Component prepareRenderer(
                    javax.swing.table.TableCellRenderer r, int row, int column) {
                Component c = super.prepareRenderer(r, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? new Color(250,250,250) : new Color(240,240,240));
                } else c.setBackground(new Color(220,235,255));
                return c;
            }
        };
        t.setRowHeight(26);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTableHeader h = t.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 14));
        h.setBackground(new Color(190,236,223));
        h.setForeground(Color.DARK_GRAY);

        // canh giữa cột ngày
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        t.getColumnModel().getColumn(4).setCellRenderer(center);
        t.getColumnModel().getColumn(5).setCellRenderer(center);

        setColW(t,0,120); setColW(t,1,200); setColW(t,2,200);
        setColW(t,3,140); setColW(t,4,140); setColW(t,5,140);
        return t;
    }

    private static void setColW(JTable t, int col, int w){
        TableColumn c = t.getColumnModel().getColumn(col);
        c.setPreferredWidth(w);
    }

    private static JButton makeBtn(String text, int rgb) {
        JButton b = new JButton(text);
        b.putClientProperty("JButton.buttonType", "roundRect");
        b.setBackground(new Color(rgb));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6,14,6,14));
        return b;
    }

    /* ===================== BEHAVIOR ===================== */

    private void hookActions() {
        // tìm kiếm = lọc client theo các field
        btnTim.addActionListener(e -> doSearch());

        // làm mới: xóa field, reload
        btnMoi.addActionListener(e -> {
            clearFields();
            reload();
        });

        // cập nhật: update theo field hiện tại (yêu cầu mã tồn tại)
        btnCapNhat.addActionListener(e -> {
            try {
                Thue t = readForm(false);
                if (dao.update(t)) {
                    info("Đã cập nhật.");
                    reload();
                } else warn("Không có thay đổi.");
            } catch (Exception ex) { error(ex); }
        });

        // tạo mới: insert (yêu cầu mã mới)
        btnTaoMoi.addActionListener(e -> {
            try {
                Thue t = readForm(true);
                if (dao.create(t)) {
                    info("Đã thêm mới.");
                    reload();
                }
            } catch (Exception ex) { error(ex); }
        });

        btnXoa.addActionListener(e -> {
            int r = table.getSelectedRow();
            String id = tfMa.getText().trim();
            if (r >= 0 && id.isEmpty()) id = String.valueOf(model.getValueAt(r,0));
            if (id.isEmpty()) { warn("Hãy chọn dòng hoặc nhập Mã thuế để xóa."); return; }
            if (JOptionPane.showConfirmDialog(this,"Xóa mã thuế "+id+" ?","Xác nhận",
                    JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
            try {
                if (dao.delete(id)) {
                    info("Đã xóa.");
                    reload();
                }
            } catch (Exception ex) { error(ex); }
        });

        // chọn bảng -> đổ vào field
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int r = table.getSelectedRow();
            if (r < 0) return;
            tfMa.setText(n(model.getValueAt(r,0)));
            tfTen.setText(n(model.getValueAt(r,1)));
            tfDoiTuong.setText(n(model.getValueAt(r,2)));
            tfLoai.setText(n(model.getValueAt(r,3)));
            tfApDung.setText(n(model.getValueAt(r,4)));
            tfKetThuc.setText(n(model.getValueAt(r,5)));
        });
    }

    private void reload() {
        try {
            List<Thue> list = dao.findAll();
            fillTable(list);
        } catch (Exception ex) { error(ex); }
    }

    private void doSearch() {
        try {
            String ma   = tfMaFilter.getText().trim().toLowerCase();
            String ten  = tfTenFilter.getText().trim().toLowerCase();
            String dtg  = tfDoiTuongF.getText().trim().toLowerCase();
            String loai = tfLoaiF.getText().trim().toLowerCase();
            String apS  = tfApDungF.getText().trim();
            String ktS  = tfKetThucF.getText().trim();

            LocalDate ap = apS.isEmpty() ? null : LocalDate.parse(apS, F);
            LocalDate kt = ktS.isEmpty() ? null : LocalDate.parse(ktS, F);

            List<Thue> list = dao.findAll();
            list.removeIf(t -> {
                // filter text
                if (!ma.isEmpty()   && (t.getMaThue()==null   || !t.getMaThue().toLowerCase().contains(ma))) return true;
                if (!ten.isEmpty()  && (t.getTenThue()==null  || !t.getTenThue().toLowerCase().contains(ten))) return true;
                if (!dtg.isEmpty()  && (t.getDoiTuongApDung()==null || !t.getDoiTuongApDung().toLowerCase().contains(dtg))) return true;
                if (!loai.isEmpty() && (t.getLoaiThue()==null || !t.getLoaiThue().toLowerCase().contains(loai))) return true;

                // filter date overlap if any date provided
                if (ap==null && kt==null) return false;
                LocalDate left  = t.getNgayApDung() == null ? LocalDate.MIN : t.getNgayApDung();
                LocalDate right = t.getNgayKetThuc()==null ? LocalDate.MAX : t.getNgayKetThuc();
                if (ap != null && right.isBefore(ap)) return true;
                if (kt != null && left.isAfter(kt))  return true;
                return false;
            });
            fillTable(list);
        } catch (Exception ex) { error(ex); }
    }

    private void fillTable(List<Thue> list) {
        model.setRowCount(0);
        for (Thue t : list) {
            model.addRow(new Object[]{
                    t.getMaThue(),
                    t.getTenThue(),
                    t.getDoiTuongApDung(),
                    t.getLoaiThue(),
                    t.getNgayApDung()==null ? "" : F.format(t.getNgayApDung()),
                    t.getNgayKetThuc()==null ? "" : F.format(t.getNgayKetThuc())
            });
        }
        table.clearSelection();
    }

    private Thue readForm(boolean requireNewId) {
        String ma   = tfMa.getText().trim();
        String ten  = tfTen.getText().trim();
        String apS  = tfApDung.getText().trim();
        String ktS  = tfKetThuc.getText().trim();
        String dtg  = tfDoiTuong.getText().trim();
        String loai = tfLoai.getText().trim();

        if (requireNewId && ma.isEmpty()) throw new IllegalArgumentException("Mã thuế không được trống.");
        if (ten.isEmpty()) throw new IllegalArgumentException("Tên thuế không được trống.");
        if (apS.isEmpty()) throw new IllegalArgumentException("Ngày áp dụng không được trống.");

        LocalDate ap = LocalDate.parse(apS, F);
        LocalDate kt = ktS.isEmpty() ? null : LocalDate.parse(ktS, F);
        return new Thue(ma, ten, ap, kt, dtg, loai);
    }

    private void clearFields() {
        tfMa.setText(""); tfTen.setText(""); tfDoiTuong.setText(""); tfLoai.setText("");
        tfApDung.setText(""); tfKetThuc.setText("");
        table.clearSelection();
    }

    /* ===================== Utils ===================== */

    private void setKeyStrokes() {
        register("F3", () -> btnTaoMoi.doClick());
        register("F4", () -> btnCapNhat.doClick());
        register("F5", () -> btnXoa.doClick());
        register("F7", () -> btnMoi.doClick());
        register("ENTER", () -> btnTim.doClick());
    }

    private void register(String key, Runnable run){
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(key), key);
        getActionMap().put(key, new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { run.run(); }
        });
    }

    private static String n(Object o){ return o==null ? "" : String.valueOf(o); }
    private void info(String m){ JOptionPane.showMessageDialog(this, m, "Thông báo", JOptionPane.INFORMATION_MESSAGE); }
    private void warn(String m){ JOptionPane.showMessageDialog(this, m, "Cảnh báo", JOptionPane.WARNING_MESSAGE); }
    private void error(Exception ex){
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
