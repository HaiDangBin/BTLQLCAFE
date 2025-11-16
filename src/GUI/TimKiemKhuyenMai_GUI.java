package GUI;

import DAO.KhuyenMai_DAO;
import Entity.KhuyenMai;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** TÌM KIẾM KHUYẾN MÃI – ngày dùng JDateChooser + style nút/ô giống Tìm Kiếm Thuế */
public class TimKiemKhuyenMai_GUI extends JPanel {

    /* ==== DAO ==== */
    private final KhuyenMai_DAO dao = new KhuyenMai_DAO();

    /* ==== Filters (giống TimKiemThue) ==== */
    private final JTextField tfMaKM   = new JTextField();
    private final JTextField tfTenMoTa= new JTextField();
    private final JComboBox<String> cbTrangThai =
            new JComboBox<>(new String[]{"Trống", "Hoạt động", "Ngưng áp dụng"});

    // Ngày dùng JDateChooser (yyyy-MM-dd)
    private final JDateChooser dcNgayBD = makeDateChooser();
    private final JDateChooser dcNgayKT = makeDateChooser();

    private final JButton btnTim = makeBtn("Tìm", 0x0D6EFD);
    private final JButton btnMoi = makeBtn("Làm mới", 0x6C757D);

    /* ==== Table ==== */
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{
            "Mã KM", "Tên", "Mô tả", "Điều kiện", "Ngày áp dụng", "Ngày kết thúc"
    }, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = styledTable(model);

    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TimKiemKhuyenMai_GUI() {
        setLayout(new BorderLayout());
        setOpaque(false);

        add(buildHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(8, 8));
        center.setBorder(new EmptyBorder(8, 8, 8, 8));
        add(center, BorderLayout.CENTER);

        center.add(buildTopBar(), BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        hookActions();
        reload();
    }

    /* ================= UI ================= */

    private JComponent buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(0xD07F00)); // cam giống TimKiemThue
        JLabel lb = new JLabel("TÌM KIẾM KHUYẾN MÃI", SwingConstants.CENTER);
        lb.setForeground(Color.WHITE);
        lb.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lb.setBorder(new EmptyBorder(10, 0, 10, 0));
        p.add(lb, BorderLayout.CENTER);
        return p;
    }

    private JComponent buildTopBar() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(8, 8, 8, 8));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        int r = 0, c = 0;

        // Dòng 1
        g.gridy = r; g.gridx = c++; grid.add(new JLabel("Mã KM:"), g);
        g.gridx = c++; g.weightx = 1; grid.add(tfMaKM, g); g.weightx = 0;

        g.gridx = c++; grid.add(new JLabel("Tên/Mô tả:"), g);
        g.gridx = c++; g.weightx = 1; grid.add(tfTenMoTa, g); g.weightx = 0;

        g.gridx = c++; grid.add(new JLabel("Loại/Trạng thái:"), g);
        g.gridx = c++; g.weightx = 1; grid.add(cbTrangThai, g); g.weightx = 0;

        // Dòng 2
        r++; c = 0;
        g.gridy = r; g.gridx = c++; grid.add(new JLabel("Ngày áp dụng:"), g);
        g.gridx = c++; g.weightx = 1; grid.add(dcNgayBD, g); g.weightx = 0;

        g.gridx = c++; grid.add(new JLabel("Ngày kết thúc:"), g);
        g.gridx = c++; g.weightx = 1; grid.add(dcNgayKT, g); g.weightx = 0;

        // Nút bên phải
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);
        actions.add(btnTim);
        actions.add(btnMoi);

        g.gridx = c; g.gridwidth = 2; grid.add(actions, g);
        g.gridwidth = 1;

        wrap.add(grid, BorderLayout.CENTER);
        return wrap;
    }

    private static JButton makeBtn(String text, int rgb) {
        JButton b = new JButton(text);
        b.setBackground(new Color(rgb));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        return b;
    }

    private static JDateChooser makeDateChooser() {
        JDateChooser dc = new JDateChooser();
        dc.setDateFormatString("yyyy-MM-dd");
        dc.setPreferredSize(new java.awt.Dimension(160, 26));
        return dc;
    }

    private static JTable styledTable(DefaultTableModel m) {
        JTable t = new JTable(m) {
            @Override public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row))
                    c.setBackground(row % 2 == 0 ? new Color(250, 250, 250) : new Color(240, 240, 240));
                else c.setBackground(new Color(220, 235, 255));
                return c;
            }
        };
        t.setRowHeight(26);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTableHeader h = t.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 14));
        h.setBackground(new Color(190, 236, 223));
        h.setForeground(Color.DARK_GRAY);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        t.getColumnModel().getColumn(4).setCellRenderer(center);
        t.getColumnModel().getColumn(5).setCellRenderer(center);
        return t;
    }

    /* ================= Actions ================= */

    private void hookActions() {
        btnTim.addActionListener(e -> doSearch());
        btnMoi.addActionListener(e -> { clearFilters(); reload(); });

        // Enter trên các ô -> Tìm
        tfMaKM.addActionListener(e -> btnTim.doClick());
        tfTenMoTa.addActionListener(e -> btnTim.doClick());
        cbTrangThai.addActionListener(e -> btnTim.doClick());
        dcNgayBD.addPropertyChangeListener(evt -> { if ("date".equals(evt.getPropertyName())) btnTim.doClick(); });
        dcNgayKT.addPropertyChangeListener(evt -> { if ("date".equals(evt.getPropertyName())) btnTim.doClick(); });
    }

    private void reload() {
        try { fillTable(dao.findAll()); }
        catch (Exception ex) { error(ex); }
    }

    private void doSearch() {
        try {
            String qMa   = tfMaKM.getText().trim().toLowerCase();
            String qTen  = tfTenMoTa.getText().trim().toLowerCase();
            String qTrang= String.valueOf(cbTrangThai.getSelectedItem());

            LocalDate from = dcNgayBD.getDate()==null ? null :
                    dcNgayBD.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate to   = dcNgayKT.getDate()==null ? null :
                    dcNgayKT.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            List<KhuyenMai> list = dao.findAll();
            list.removeIf(k -> {
                if (!qMa.isEmpty() && (k.getMaKM()==null || !k.getMaKM().toLowerCase().contains(qMa))) return true;

                String tenMo = ((k.getTenKM()==null?"":k.getTenKM()) + " " + (k.getMoTa()==null?"":k.getMoTa())).toLowerCase();
                if (!qTen.isEmpty() && !tenMo.contains(qTen)) return true;

                if (!"Trống".equals(qTrang) && k.getDieuKienApDung()!=null) {
                    String dk = k.getDieuKienApDung().toLowerCase();
                    if ("Hoạt động".equals(qTrang) && !(dk.contains("hoạt") || dk.contains("active"))) return true;
                    if ("Ngưng áp dụng".equals(qTrang) && !(dk.contains("ngưng") || dk.contains("hết hạn") || dk.contains("inactive"))) return true;
                }

                if (from==null && to==null) return false;
                LocalDate left  = k.getNgayBD()==null ? LocalDate.MIN : k.getNgayBD();
                LocalDate right = k.getNgayKT()==null ? LocalDate.MAX : k.getNgayKT();
                if (from!=null && right.isBefore(from)) return true;
                if (to!=null   && left.isAfter(to))    return true;
                return false;
            });

            fillTable(list);
        } catch (Exception ex) { error(ex); }
    }

    private void fillTable(List<KhuyenMai> list) {
        model.setRowCount(0);
        for (KhuyenMai k : list) {
            model.addRow(new Object[]{
                    n(k.getMaKM()),
                    n(k.getTenKM()!=null ? k.getTenKM() : k.getMoTa()),
                    n(k.getMoTa()),
                    n(k.getDieuKienApDung()),
                    k.getNgayBD()==null ? "" : F.format(k.getNgayBD()),
                    k.getNgayKT()==null ? "" : F.format(k.getNgayKT())
            });
        }
        table.clearSelection();
    }

    private void clearFilters() {
        tfMaKM.setText("");
        tfTenMoTa.setText("");
        cbTrangThai.setSelectedIndex(0);
        dcNgayBD.setDate(null);
        dcNgayKT.setDate(null);
        table.clearSelection();
    }

    /* ================= Utils ================= */

    private static String n(Object o) { return o==null? "" : String.valueOf(o); }

    private void error(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
