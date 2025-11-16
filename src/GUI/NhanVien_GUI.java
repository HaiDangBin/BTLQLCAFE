package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import DAO.NhanVien_DAO;
import DAO.ChucVu_DAO;
import Entity.ChucVu;
import Entity.NhanVien;
import Entity.TaiKhoan;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Pattern;

public class NhanVien_GUI extends JPanel {

    private TaiKhoan tkLogin;

    private JTextField txtMaNV, txtTenNV, txtSDT, txtDiaChi, txtNgaySinh, txtTim;
    private JComboBox<ChucVu> cboChucVu;

    private JButton btnThem, btnSua, btnXoa, btnClear, btnTim, btnLoad;

    private JTable table;
    private DefaultTableModel model;

    private NhanVien_DAO nvDAO = new NhanVien_DAO();
    private ChucVu_DAO cvDAO = new ChucVu_DAO();

    public NhanVien_GUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel content = new JPanel(new BorderLayout());
        content.add(buildFormPanel(), BorderLayout.NORTH);
        content.add(buildTablePanel(), BorderLayout.CENTER);

        this.add(buildTitlePanel(), BorderLayout.NORTH);
        this.add(content, BorderLayout.CENTER);

        loadChucVu();
        loadNhanVien();
        addEventHandlers();
    }

    // ===================== TIÊU ĐỀ =========================
    private JPanel buildTitlePanel() {
        JPanel pn = new JPanel(new BorderLayout());
        pn.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        pn.setBackground(new Color(0, 153, 153)); // xanh ngọc

        JLabel lbl = new JLabel("QUẢN LÝ NHÂN VIÊN", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl.setForeground(Color.WHITE);
        pn.add(lbl, BorderLayout.CENTER);

        return pn;
    }

    // ===================== FORM INPUT =========================
    private JPanel buildFormPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        wrapper.setBackground(Color.WHITE);
        wrapper.setOpaque(true);

        JPanel form = new JPanel(new GridLayout(3, 4, 15, 15));
        form.setBackground(new Color(245, 245, 245));
        form.setOpaque(true);

        form.add(new JLabel("Mã nhân viên:"));
        txtMaNV = new JTextField();
        form.add(txtMaNV);

        form.add(new JLabel("Tên nhân viên:"));
        txtTenNV = new JTextField();
        form.add(txtTenNV);

        form.add(new JLabel("Số điện thoại:"));
        txtSDT = new JTextField();
        form.add(txtSDT);

        form.add(new JLabel("Địa chỉ:"));
        txtDiaChi = new JTextField();
        form.add(txtDiaChi);

        form.add(new JLabel("Ngày sinh (yyyy-MM-dd):"));
        txtNgaySinh = new JTextField();
        form.add(txtNgaySinh);

        form.add(new JLabel("Chức vụ:"));
        cboChucVu = new JComboBox<>();

        cboChucVu.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ChucVu cv) {
                    setText(cv.getTenLoai());
                }
                return this;
            }
        });

        form.add(cboChucVu);

        wrapper.add(form, BorderLayout.CENTER);

        // ===================== BUTTON PANEL =========================
        JPanel pnBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        pnBtn.setBackground(new Color(245, 245, 245));
        pnBtn.setOpaque(true);

        btnThem = new JButton("Thêm");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");
        btnClear = new JButton("Xóa trắng");
        btnTim = new JButton("Tìm");
        btnLoad = new JButton("Tải lại");
        txtTim = new JTextField(15);

        // Style cho button
        Color btnColor = new Color(0, 153, 153);
        Color btnText = Color.WHITE;
        JButton[] buttons = {btnThem, btnSua, btnXoa, btnClear, btnTim, btnLoad};

        for (JButton b : buttons) {
            b.setBackground(btnColor);
            b.setForeground(btnText);
            b.setFocusPainted(false);
            b.setOpaque(true);
            b.setBorder(BorderFactory.createLineBorder(new Color(0, 120, 120)));
        }

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

    // ===================== BẢNG NHÂN VIÊN =========================
    private JPanel buildTablePanel() {
        JPanel pn = new JPanel(new BorderLayout());
        pn.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        pn.setBackground(Color.WHITE);

        String[] cols = {"Mã NV", "Tên NV", "SĐT", "Địa chỉ", "Ngày sinh", "Chức vụ"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // HEADER MÀU XANH
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0, 153, 153));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // TÔ MÀU DÒNG XEN KẼ
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    c.setBackground(new Color(0, 153, 153));
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        pn.add(new JScrollPane(table), BorderLayout.CENTER);

        return pn;
    }

    // ===================== LOAD & CRUD =========================
    private void loadChucVu() {
        cboChucVu.removeAllItems();
        List<ChucVu> ds = cvDAO.getAll();
        for (ChucVu cv : ds) {
            cboChucVu.addItem(cv);
        }
        if (cboChucVu.getItemCount() > 0) {
            cboChucVu.setSelectedIndex(0);
        }
    }

    private void loadNhanVien() {
        model.setRowCount(0);
        List<NhanVien> ds = nvDAO.getAll();
        for (NhanVien nv : ds) {
            model.addRow(new Object[]{
                    nv.getMaNV(),
                    nv.getTenNV(),
                    nv.getSdt(),
                    nv.getDiaChi(),
                    nv.getNgaySinh(),
                    nv.getChucVu().getTenLoai()
            });
        }
    }

    private void addEventHandlers() {

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMaNV.setText(model.getValueAt(row, 0).toString());
                    txtTenNV.setText(model.getValueAt(row, 1).toString());
                    txtSDT.setText(model.getValueAt(row, 2).toString());
                    txtDiaChi.setText(model.getValueAt(row, 3).toString());
                    txtNgaySinh.setText(model.getValueAt(row, 4).toString());

                    String tenCV = model.getValueAt(row, 5).toString();
                    for (int i = 0; i < cboChucVu.getItemCount(); i++) {
                        ChucVu cv = cboChucVu.getItemAt(i);
                        if (cv.getTenLoai().equals(tenCV)) {
                            cboChucVu.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        });

        btnClear.addActionListener(e -> clearForm());

        btnThem.addActionListener(e -> {
            if (!validateForm()) return;
            NhanVien nv = getNhanVienFromForm();
            if (nvDAO.add(nv)) {
                JOptionPane.showMessageDialog(this, "Thêm thành công!");
                loadNhanVien();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnSua.addActionListener(e -> {
            if (!validateForm()) return;
            NhanVien nv = getNhanVienFromForm();
            if (nvDAO.update(nv)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                loadNhanVien();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnXoa.addActionListener(e -> {
            String ma = txtMaNV.getText().trim();
            if (ma.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nhập mã nhân viên cần xóa!");
                return;
            }
            if (nvDAO.delete(ma)) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                loadNhanVien();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnLoad.addActionListener(e -> {
            txtTim.setText("");
            loadNhanVien();
        });

        btnTim.addActionListener(e -> searchNhanVien());
    }

    private NhanVien getNhanVienFromForm() {
        ChucVu cv = (ChucVu) cboChucVu.getSelectedItem();
        
        // Lấy chuỗi ngày sinh từ TextField
        String ngaySinhStr = txtNgaySinh.getText().trim();
        java.sql.Date ngaySinhSQL = null;

        try {
            // 1. Định nghĩa format của chuỗi ngày tháng đang nhập (Ví dụ: "yyyy-MM-dd" hoặc "dd/MM/yyyy")
            // Giả sử chuỗi ngày sinh có format là "yyyy-MM-dd" (phổ biến)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            // 2. Phân tích chuỗi String thành LocalDate
            LocalDate localDate = LocalDate.parse(ngaySinhStr, formatter);
            
            // 3. Chuyển đổi LocalDate thành java.sql.Date
            ngaySinhSQL = java.sql.Date.valueOf(localDate);

        } catch (DateTimeParseException e) {
            // Xử lý khi chuỗi ngày tháng không đúng format
            JOptionPane.showMessageDialog(null, "Lỗi định dạng ngày sinh. Vui lòng nhập đúng format (ví dụ: 2000-12-31).", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null; // Trả về null hoặc ném ngoại lệ nếu dữ liệu không hợp lệ
        }

        return new NhanVien(
                txtMaNV.getText().trim(),
                txtTenNV.getText().trim(),
                txtSDT.getText().trim(),
                txtDiaChi.getText().trim(),
                ngaySinhSQL, // Đã chuyển thành kiểu Date
                cv
        );
    }

    private void clearForm() {
        txtMaNV.setText("");
        txtTenNV.setText("");
        txtSDT.setText("");
        txtDiaChi.setText("");
        txtNgaySinh.setText("");
        txtTim.setText("");
        if (cboChucVu.getItemCount() > 0) {
            cboChucVu.setSelectedIndex(0);
        }
        table.clearSelection();
    }

    private boolean validateForm() {
        String ma = txtMaNV.getText().trim();
        String ten = txtTenNV.getText().trim();
        String sdt = txtSDT.getText().trim();
        String ns = txtNgaySinh.getText().trim();

        if (ma.isEmpty() || ten.isEmpty() || sdt.isEmpty() || ns.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!Pattern.matches("^NV\\d{2,}$", ma)) {
            JOptionPane.showMessageDialog(this, "Mã NV phải dạng NVxx (vd: NV01)", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!Pattern.matches("^0\\d{9}$", sdt)) {
            JOptionPane.showMessageDialog(this, "SĐT phải 10 số và bắt đầu bằng 0", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!Pattern.matches("^\\d{4}-\\d{2}-\\d{2}$", ns)) {
            JOptionPane.showMessageDialog(this, "Ngày sinh phải dạng yyyy-MM-dd", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void searchNhanVien() {
        String key = txtTim.getText().trim().toLowerCase();
        model.setRowCount(0);

        for (NhanVien nv : nvDAO.getAll()) {
            if (nv.getMaNV().toLowerCase().contains(key)
                    || nv.getTenNV().toLowerCase().contains(key)
                    || nv.getSdt().toLowerCase().contains(key)) {

                model.addRow(new Object[]{
                        nv.getMaNV(),
                        nv.getTenNV(),
                        nv.getSdt(),
                        nv.getDiaChi(),
                        nv.getNgaySinh(),
                        nv.getChucVu().getTenLoai()
                });
            }
        }
    }
}
