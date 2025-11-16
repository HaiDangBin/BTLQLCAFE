package GUI;

import DAO.ThongKe_DAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;

public class ThongKeSanPham_GUI extends JPanel {

    private JTabbedPane tabs;

    private JTable tblDoanhThu, tblTopSP, tblTonKho;
    private DefaultTableModel modelDoanhThu, modelTopSP, modelTonKho;

    private JTextField txtFrom, txtTo;
    private JButton btnLoc, btnRefresh;

    private ThongKe_DAO tkDAO = new ThongKe_DAO();

    public ThongKeSanPham_GUI() {

        setLayout(new BorderLayout());
        applyTheme();

        // ==== HEADER ====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0x009970));

        JLabel lblTitle = new JLabel("BÁO CÁO QUẢN LÝ", SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        header.add(lblTitle, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // ==== TABS ====
        tabs = new JTabbedPane();

        Font f = tabs.getFont();
        tabs.setFont(f.deriveFont(f.getSize2D() * 1.1f));

        tabs.add("Doanh thu", createTabDoanhThu());
        tabs.add("Top sản phẩm", createTabTopSP());
        tabs.add("Tồn kho", createTabTonKho());
        add(tabs, BorderLayout.CENTER);

        // ==== FOOTER ====
        JPanel foot = new JPanel();

        btnRefresh = new JButton("Làm mới (F5)");
        btnRefresh.addActionListener(e -> reloadAll());

        foot.add(btnRefresh);
        add(foot, BorderLayout.SOUTH);

        // ==== AUTO DATE RANGE ====
        setDefaultDateRange();

        // ==== LOAD INITIAL DATA ====
        reloadAll();
    }


    // ===============================================================
    // TAB DOANH THU
    // ===============================================================
    private JPanel createTabDoanhThu() {
        JPanel pnl = new JPanel(new BorderLayout());

        JPanel filter = new JPanel();

        txtFrom = new JTextField(10);
        txtTo = new JTextField(10);
        btnLoc = new JButton("Lọc");

        btnLoc.addActionListener(e -> reloadAll());

        filter.add(new JLabel("Từ ngày:"));
        filter.add(txtFrom);
        filter.add(new JLabel("Đến ngày:"));
        filter.add(txtTo);
        filter.add(btnLoc);

        pnl.add(filter, BorderLayout.NORTH);

        modelDoanhThu = new DefaultTableModel(new String[]{
                "Ngày", "Số đơn", "Doanh thu", "Trung bình/đơn"
        }, 0);

        tblDoanhThu = new JTable(modelDoanhThu);
        pnl.add(new JScrollPane(tblDoanhThu), BorderLayout.CENTER);

        return pnl;
    }

    // ===============================================================
    // TAB TOP SẢN PHẨM
    // ===============================================================
    private JPanel createTabTopSP() {
        JPanel pnl = new JPanel(new BorderLayout());

        modelTopSP = new DefaultTableModel(new String[]{
                "Mã SP", "Tên SP", "SL bán", "Doanh thu"
        }, 0);

        tblTopSP = new JTable(modelTopSP);
        pnl.add(new JScrollPane(tblTopSP), BorderLayout.CENTER);

        return pnl;
    }

    // ===============================================================
    // TAB TỒN KHO
    // ===============================================================
    private JPanel createTabTonKho() {
        JPanel pnl = new JPanel(new BorderLayout());

        modelTonKho = new DefaultTableModel(new String[]{
                "Mã SP", "Tên SP", "Số lượng", "Cảnh báo"
        }, 0);

        tblTonKho = new JTable(modelTonKho);
        pnl.add(new JScrollPane(tblTonKho), BorderLayout.CENTER);

        return pnl;
    }

    // ===============================================================
    // LOAD DATA (CHUẨN)
    // ===============================================================
    private void loadDoanhThu() {
        try {
            modelDoanhThu.setRowCount(0);

            Date from = Date.valueOf(txtFrom.getText());
            Date to = Date.valueOf(txtTo.getText());

            for (Object[] r : tkDAO.getDoanhThuTheoNgay(from, to)) {
                modelDoanhThu.addRow(new Object[]{
                        r[0],
                        r[1],
                        money((double) r[2]),
                        money((double) r[3])
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ngày không hợp lệ! (yyyy-MM-dd)");
        }
    }

    private void loadTopSP() {
        try {
            modelTopSP.setRowCount(0);

            Date from = Date.valueOf(txtFrom.getText());
            Date to = Date.valueOf(txtTo.getText());

            for (Object[] r : tkDAO.getTopSanPham(from, to)) {
                modelTopSP.addRow(new Object[]{
                        r[0], r[1], r[2], money((double) r[3])
                });
            }

        } catch (Exception ignored) {}
    }

    private void loadTonKho() {
        modelTonKho.setRowCount(0);

        for (Object[] r : tkDAO.getTonKho()) {
            modelTonKho.addRow(r);
        }
    }

    private void reloadAll() {
        loadDoanhThu();
        loadTopSP();
        loadTonKho();
    }

    // ===============================================================
    // SET NGÀY MẶC ĐỊNH (AUTO lấy từ DB)
    // ===============================================================
    private void setDefaultDateRange() {
        try {
            Date min = tkDAO.getMinDate();
            Date max = tkDAO.getMaxDate();

            txtFrom.setText(min.toString());
            txtTo.setText(max.toString());
        } catch (Exception e) {
            txtFrom.setText("2024-01-01");
            txtTo.setText("2024-12-31");
        }
    }

    // ===============================================================
    // UTILS
    // ===============================================================
    private String money(double v) {
        return String.format("%,.0f VND", v);
    }

    private void applyTheme() {
        UIManager.put("Table.selectionBackground", new Color(200, 220, 255));
        UIManager.put("TabbedPane.tabInsets", new Insets(6, 18, 6, 18));
    }
}
