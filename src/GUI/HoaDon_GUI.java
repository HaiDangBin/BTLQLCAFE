package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

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
import java.util.List;
import java.util.regex.Pattern;

public class HoaDon_GUI extends JPanel {

    private TaiKhoan tkLogin; // để quay lại home & lấy mã NV

    private JTable tableHD;
    private DefaultTableModel modelHD;

    private JTable tableCT;
    private DefaultTableModel modelCT;

    private JTextField txtSearch;

    HoaDon_DAO hdDAO = new HoaDon_DAO();
    ChiTietHoaDon_DAO ctDAO = new ChiTietHoaDon_DAO();

    // ===================== CONSTRUCTOR =====================
    public HoaDon_GUI(TaiKhoan tk) {
        this.tkLogin = tk;

       
        setLayout(new BorderLayout());

        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildMainPanel(), BorderLayout.CENTER);

        loadHoaDon();
    }

    // ===================== TOP PANEL =====================
    private JPanel buildTopPanel() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // nút quay lại
        JButton btnBack = new JButton("← Quay lại");
        btnBack.addActionListener(e -> {
           // dispose();
            //if (tkLogin != null) new Home_GUI(tkLogin).setVisible(true);
        });
        top.add(btnBack, BorderLayout.WEST);

        // tiêu đề
        JLabel lbl = new JLabel("QUẢN LÝ HÓA ĐƠN", JLabel.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        top.add(lbl, BorderLayout.CENTER);

        return top;
    }

    // ===================== MAIN PANEL =====================
    private JPanel buildMainPanel() {
        JPanel pn = new JPanel(new BorderLayout());

        pn.add(buildFunctionPanel(), BorderLayout.NORTH);
        pn.add(buildContentPanel(), BorderLayout.CENTER);

        return pn;
    }

    // ===================== FUNCTION PANEL =====================
    private JPanel buildFunctionPanel() {
        JPanel fn = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JButton btnNewHD   = new JButton("Lập hóa đơn");
        JButton btnEditHD  = new JButton("Sửa");
        JButton btnDeleteHD= new JButton("Xóa");
        JButton btnReload  = new JButton("Tải lại");

        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Tìm");

        fn.add(btnNewHD);
        fn.add(btnEditHD);
        fn.add(btnDeleteHD);
        fn.add(btnReload);
        fn.add(new JLabel("Tìm:"));
        fn.add(txtSearch);
        fn.add(btnSearch);

        // sự kiện
        btnNewHD.addActionListener(e -> openNewHoaDonDialog());
        btnEditHD.addActionListener(e -> openEditHoaDonDialog());
        btnDeleteHD.addActionListener(e -> deleteHoaDon());
        btnReload.addActionListener(e -> loadHoaDon());
        btnSearch.addActionListener(e -> searchHoaDon());

        return fn;
    }

    // ===================== CONTENT PANEL =====================
    private JPanel buildContentPanel() {
        JPanel pn = new JPanel(new GridLayout(2, 1));

        // bảng hóa đơn
        String[] colsHD = {"Mã HD", "Mã NV", "Mã KH", "Mã KM", "Mã Đặt Bàn", "Ngày lập", "Tổng tiền"};
        modelHD = new DefaultTableModel(colsHD, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tableHD = new JTable(modelHD);
        tableHD.setRowHeight(22);

        // click để xem chi tiết
        tableHD.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int r = tableHD.getSelectedRow();
                if (r >= 0) {
                    String maHD = modelHD.getValueAt(r, 0).toString();
                    loadChiTietHD(maHD);
                }
            }
        });

        pn.add(new JScrollPane(tableHD));

        // bảng chi tiết
        String[] colsCT = {"Mã SP", "Số lượng", "Đơn giá", "Thành tiền"};
        modelCT = new DefaultTableModel(colsCT, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tableCT = new JTable(modelCT);
        tableCT.setRowHeight(22);

        pn.add(new JScrollPane(tableCT));

        return pn;
    }

    // ===================== LOAD HÓA ĐƠN =====================
    private void loadHoaDon() {
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

        modelCT.setRowCount(0); // clear chi tiết
    }

    // ===================== LOAD CHI TIẾT =====================
    private void loadChiTietHD(String maHD) {
        modelCT.setRowCount(0);

        String sql = "SELECT maSP, soLuongSP, donGia FROM ChiTietHD WHERE maHD = ?";

        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maHD);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int sl = rs.getInt("soLuongSP");
                double dg = rs.getDouble("donGia");
                modelCT.addRow(new Object[]{
                        rs.getString("maSP"),
                        sl,
                        dg,
                        sl * dg
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===================== SEARCH =====================
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

    // ===================== VALIDATION =====================
    private String validateHoaDon(String maHD, String ngay, String maNV,
                                  String maKH, String maKM, String maDatBan,
                                  boolean isNew) {

        if (maHD.isEmpty() || ngay.isEmpty() || maNV.isEmpty()) {
            return "Mã hóa đơn, ngày lập và mã nhân viên không được để trống!";
        }

        if (!Pattern.matches("^HD\\d{3,}$", maHD)) {
            return "Mã hóa đơn phải dạng HDxxx (ví dụ: HD001, HD020...).";
        }

        if (!Pattern.matches("^\\d{4}-\\d{2}-\\d{2}$", ngay)) {
            return "Ngày lập phải theo định dạng yyyy-MM-dd.";
        }

        if (!Pattern.matches("^NV\\d{2,}$", maNV)) {
            return "Mã nhân viên phải dạng NVxx.";
        }

        if (!maKH.isEmpty() && !Pattern.matches("^KH\\d{2,}$", maKH)) {
            return "Mã khách hàng phải dạng KHxx (hoặc để trống).";
        }

        if (!maKM.isEmpty() && !Pattern.matches("^KM\\d{2,}$", maKM)) {
            return "Mã khuyến mãi phải dạng KMxx (hoặc để trống).";
        }

        if (!maDatBan.isEmpty() && !Pattern.matches("^DDB\\d{2,}$", maDatBan)) {
            return "Mã đặt bàn phải dạng DDBxx (hoặc để trống).";
        }

        // kiểm tra trùng mã khi thêm mới
        if (isNew && hdDAO.findById(maHD) != null) {
            return "Mã hóa đơn đã tồn tại!";
        }

        return null; // hợp lệ
    }

    // ===================== LẬP HÓA ĐƠN (POPUP THÊM) =====================
    private void openNewHoaDonDialog() {
        JDialog dialog = new JDialog();
        dialog.setSize(420, 360);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(7, 2, 10, 10));
        dialog.setResizable(false);

        JTextField txtMaHD = new JTextField();
        JTextField txtNgay = new JTextField(java.time.LocalDate.now().toString());

        String defaultMaNV = "";
        if (tkLogin != null && tkLogin.getNhanVien() != null) {
            defaultMaNV = tkLogin.getNhanVien().getMaNV();
        }
        JTextField txtMaNV = new JTextField(defaultMaNV);
        if (tkLogin != null) {
            txtMaNV.setEditable(false); // đăng nhập rồi thì không cho sửa
        }

        JTextField txtMaKH = new JTextField();
        JTextField txtMaKM = new JTextField();
        JTextField txtMaDatBan = new JTextField();

        JButton btnCreate = new JButton("Tạo hóa đơn");

        dialog.add(new JLabel("Mã hóa đơn:"));
        dialog.add(txtMaHD);

        dialog.add(new JLabel("Ngày lập (yyyy-MM-dd):"));
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

            HoaDon hd = new HoaDon(maHD, maNV, maKH, maKM, maDB, ngay);

            if (hdDAO.insert(hd)) {
                JOptionPane.showMessageDialog(this, "Tạo hóa đơn thành công!");
                loadHoaDon();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Tạo hóa đơn thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    // ===================== POPUP SỬA HÓA ĐƠN =====================
    private void openEditHoaDonDialog(){
        int row = tableHD.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một hóa đơn cần sửa!");
            return;
        }

        String maHD = modelHD.getValueAt(row, 0).toString();
        HoaDon hdOld = hdDAO.findById(maHD);
        if (hdOld == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn trong CSDL!");
            return;
        }

        JDialog dialog = new JDialog();
        dialog.setSize(420, 360);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(7, 2, 10, 10));
        dialog.setResizable(false);

        JTextField txtMaHD = new JTextField(hdOld.getMaHD());
        txtMaHD.setEditable(false);

        JTextField txtNgay = new JTextField(hdOld.getNgayLap());
        JTextField txtMaNV = new JTextField(hdOld.getMaNV());
        JTextField txtMaKH = new JTextField(hdOld.getMaKH());
        JTextField txtMaKM = new JTextField(hdOld.getMaKM());
        JTextField txtMaDatBan = new JTextField(hdOld.getMaDatBan());

        JButton btnSave = new JButton("Lưu thay đổi");

        dialog.add(new JLabel("Mã hóa đơn:"));
        dialog.add(txtMaHD);

        dialog.add(new JLabel("Ngày lập (yyyy-MM-dd):"));
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

            HoaDon hd = new HoaDon(maHD, maNV, maKH, maKM, maDB, ngay);

            if (hdDAO.update(hd)) {
                JOptionPane.showMessageDialog(this, "Cập nhật hóa đơn thành công!");
                loadHoaDon();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    // ===================== XÓA HÓA ĐƠN =====================
    private void deleteHoaDon() {
        int row = tableHD.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn hóa đơn cần xóa!");
            return;
        }

        String maHD = modelHD.getValueAt(row, 0).toString();

        int opt = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa hóa đơn " + maHD + " ?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (opt != JOptionPane.YES_OPTION) return;

        if (hdDAO.delete(maHD)) {
            JOptionPane.showMessageDialog(this, "Xóa hóa đơn thành công!");
            loadHoaDon();
        } else {
            JOptionPane.showMessageDialog(this, "Xóa thất bại! (kiểm tra ràng buộc khóa ngoại ChiTietHD,...)",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

}
