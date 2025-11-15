package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import connectDB.DBconnection;
import DAO.NhanVien_DAO;
import Entity.NhanVien;
import Entity.TaiKhoan;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.List;
import java.util.regex.Pattern;

public class TaiKhoan_GUI extends JPanel {

    private TaiKhoan tkLogin;

    private JTextField txtMaTK, txtTenDN, txtMatKhau, txtVaiTro, txtTim;
    private JCheckBox chkTrangThai;
    private JComboBox<NhanVien> cboNhanVien;

    private JButton btnThem, btnSua, btnXoa, btnClear, btnTim, btnLoad;

    private JTable table;
    private DefaultTableModel model;

    private NhanVien_DAO nvDAO = new NhanVien_DAO();

    // ===================== CONSTRUCTOR =====================
    public TaiKhoan_GUI() {
        setLayout(new BorderLayout());

        JPanel content = new JPanel(new BorderLayout());
        content.add(buildFormPanel(), BorderLayout.NORTH);
        content.add(buildTablePanel(), BorderLayout.CENTER);

        add(buildTitlePanel(), BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);

        loadNhanVienCombo();
        loadTaiKhoan();
        addEventHandlers();
    }

    // ===================== TITLE =====================
    private JPanel buildTitlePanel() {
        JPanel pn = new JPanel(new BorderLayout());
        pn.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel lbl = new JLabel("QUẢN LÝ TÀI KHOẢN", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        pn.add(lbl, BorderLayout.CENTER);

        return pn;
    }

    // ===================== FORM PANEL =====================
    private JPanel buildFormPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel form = new JPanel(new GridLayout(3, 4, 15, 15));

        form.add(new JLabel("Mã tài khoản:"));
        txtMaTK = new JTextField();
        form.add(txtMaTK);

        form.add(new JLabel("Tên đăng nhập:"));
        txtTenDN = new JTextField();
        form.add(txtTenDN);

        form.add(new JLabel("Mật khẩu:"));
        txtMatKhau = new JTextField();
        form.add(txtMatKhau);

        form.add(new JLabel("Vai trò:"));
        txtVaiTro = new JTextField();
        form.add(txtVaiTro);

        form.add(new JLabel("Trạng thái:"));
        chkTrangThai = new JCheckBox("Đang hoạt động");
        chkTrangThai.setSelected(true);
        form.add(chkTrangThai);

        form.add(new JLabel("Nhân viên:"));
        cboNhanVien = new JComboBox<>();

        cboNhanVien.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof NhanVien nv) {
                    setText(nv.getMaNV() + " - " + nv.getTenNV());
                }
                return this;
            }
        });

        form.add(cboNhanVien);

        wrapper.add(form, BorderLayout.CENTER);

        JPanel pnBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));

        btnThem = new JButton("Thêm");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");
        btnClear = new JButton("Xóa trắng");
        btnTim = new JButton("Tìm");
        btnLoad = new JButton("Tải lại");
        txtTim = new JTextField(15);

        pnBtn.add(btnThem);
        pnBtn.add(btnSua);
        pnBtn.add(btnXoa);
        pnBtn.add(btnClear);
        pnBtn.add(new JLabel("Tìm:"));
        pnBtn.add(txtTim);
        pnBtn.add(btnTim);
        pnBtn.add(btnLoad);

        wrapper.add(pnBtn, BorderLayout.SOUTH);

        return wrapper;
    }

    // ===================== TABLE PANEL =====================
    private JPanel buildTablePanel() {
        JPanel pn = new JPanel(new BorderLayout());
        pn.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        String[] cols = {"Mã TK", "Tên đăng nhập", "Mật khẩu",
                "Vai trò", "Trạng thái", "Mã NV", "Tên nhân viên"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        pn.add(new JScrollPane(table), BorderLayout.CENTER);

        return pn;
    }

    // ===================== LOAD DATA =====================
    private void loadNhanVienCombo() {
        cboNhanVien.removeAllItems();
        List<NhanVien> ds = nvDAO.getAll();
        for (NhanVien nv : ds) {
            cboNhanVien.addItem(nv);
        }
        if (cboNhanVien.getItemCount() > 0) cboNhanVien.setSelectedIndex(0);
    }

    private void loadTaiKhoan() {
        model.setRowCount(0);
        String sql = """
                SELECT tk.maTK, tk.tenDN, tk.matKhau, tk.vaiTro, tk.trangThai,
                       tk.maNV, nv.tenNV
                FROM TaiKhoan tk
                JOIN NhanVien nv ON tk.maNV = nv.maNV
                """;

        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("maTK"),
                        rs.getString("tenDN"),
                        rs.getString("matKhau"),
                        rs.getString("vaiTro"),
                        rs.getBoolean("trangThai") ? "Đang hoạt động" : "Khóa",
                        rs.getString("maNV"),
                        rs.getString("tenNV")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Lỗi tải dữ liệu tài khoản!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===================== EVENTS =====================
    private void addEventHandlers() {

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int r = table.getSelectedRow();
                if (r >= 0) {
                    txtMaTK.setText(model.getValueAt(r, 0).toString());
                    txtTenDN.setText(model.getValueAt(r, 1).toString());
                    txtMatKhau.setText(model.getValueAt(r, 2).toString());
                    txtVaiTro.setText(model.getValueAt(r, 3).toString());
                    chkTrangThai.setSelected(
                            model.getValueAt(r, 4).toString().equalsIgnoreCase("Đang hoạt động")
                    );

                    String maNV = model.getValueAt(r, 5).toString();

                    for (int i = 0; i < cboNhanVien.getItemCount(); i++) {
                        if (cboNhanVien.getItemAt(i).getMaNV().equals(maNV)) {
                            cboNhanVien.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        });

        btnClear.addActionListener(e -> clearForm());

        btnThem.addActionListener(e -> {
            if (!validateForm()) return;

            String sql = """
                    INSERT INTO TaiKhoan(maTK, tenDN, matKhau, vaiTro, trangThai, maNV)
                    VALUES (?,?,?,?,?,?)
                    """;

            try (Connection con = DBconnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                NhanVien nv = (NhanVien) cboNhanVien.getSelectedItem();

                ps.setString(1, txtMaTK.getText().trim());
                ps.setString(2, txtTenDN.getText().trim());
                ps.setString(3, txtMatKhau.getText().trim());
                ps.setString(4, txtVaiTro.getText().trim());
                ps.setBoolean(5, chkTrangThai.isSelected());
                ps.setString(6, nv != null ? nv.getMaNV() : null);

                if (ps.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "Thêm tài khoản thành công!");
                    loadTaiKhoan();
                    clearForm();
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi thêm tài khoản!\n" + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnSua.addActionListener(e -> {
            if (!validateForm()) return;

            String sql = """
                    UPDATE TaiKhoan SET
                        tenDN=?, matKhau=?, vaiTro=?, trangThai=?, maNV=?
                    WHERE maTK=?
                    """;

            try (Connection con = DBconnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                NhanVien nv = (NhanVien) cboNhanVien.getSelectedItem();

                ps.setString(1, txtTenDN.getText().trim());
                ps.setString(2, txtMatKhau.getText().trim());
                ps.setString(3, txtVaiTro.getText().trim());
                ps.setBoolean(4, chkTrangThai.isSelected());
                ps.setString(5, nv != null ? nv.getMaNV() : null);
                ps.setString(6, txtMaTK.getText().trim());

                if (ps.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    loadTaiKhoan();
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi cập nhật tài khoản!\n" + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnXoa.addActionListener(e -> {
            String maTK = txtMaTK.getText().trim();
            if (maTK.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nhập mã tài khoản cần xóa!");
                return;
            }

            String sql = "DELETE FROM TaiKhoan WHERE maTK=?";

            try (Connection con = DBconnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, maTK);
                if (ps.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    loadTaiKhoan();
                    clearForm();
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi xóa tài khoản!\n" + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnLoad.addActionListener(e -> {
            txtTim.setText("");
            loadTaiKhoan();
        });

        btnTim.addActionListener(e -> searchTaiKhoan());
    }

    // ===================== SUPPORT FUNCTIONS =====================
    private void clearForm() {
        txtMaTK.setText("");
        txtTenDN.setText("");
        txtMatKhau.setText("");
        txtVaiTro.setText("");
        chkTrangThai.setSelected(true);
        txtTim.setText("");

        if (cboNhanVien.getItemCount() > 0)
            cboNhanVien.setSelectedIndex(0);

        table.clearSelection();
    }

    private boolean validateForm() {
        String ma = txtMaTK.getText().trim();
        String ten = txtTenDN.getText().trim();
        String mk = txtMatKhau.getText().trim();

        if (ma.isEmpty() || ten.isEmpty() || mk.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không được để trống thông tin bắt buộc!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!Pattern.matches("^TK\\d{2,}$", ma)) {
            JOptionPane.showMessageDialog(this, "Mã TK phải dạng TKxx",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (mk.length() < 3) {
            JOptionPane.showMessageDialog(this, "Mật khẩu tối thiểu 3 ký tự!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (cboNhanVien.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Chọn nhân viên!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void searchTaiKhoan() {
        String key = txtTim.getText().trim();
        if (key.isEmpty()) {
            loadTaiKhoan();
            return;
        }

        model.setRowCount(0);

        String sql = """
                SELECT tk.maTK, tk.tenDN, tk.matKhau, tk.vaiTro, tk.trangThai,
                       tk.maNV, nv.tenNV
                FROM TaiKhoan tk
                JOIN NhanVien nv ON tk.maNV = nv.maNV
                WHERE tk.maTK LIKE ? OR tk.tenDN LIKE ? OR tk.maNV LIKE ?
                """;

        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String like = "%" + key + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("maTK"),
                        rs.getString("tenDN"),
                        rs.getString("matKhau"),
                        rs.getString("vaiTro"),
                        rs.getBoolean("trangThai") ? "Đang hoạt động" : "Khóa",
                        rs.getString("maNV"),
                        rs.getString("tenNV")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Lỗi tìm kiếm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
