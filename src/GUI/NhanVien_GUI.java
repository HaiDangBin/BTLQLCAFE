package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import DAO.NhanVien_DAO;
import DAO.ChucVu_DAO;
import Entity.ChucVu;
import Entity.NhanVien;
import Entity.TaiKhoan;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    public NhanVien_GUI(TaiKhoan tk) {
        this.tkLogin = tk;

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

    private JPanel buildTitlePanel() {
        JPanel pn = new JPanel(new BorderLayout());
        pn.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JButton btnBack = new JButton("← Quay lại");
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pn.add(btnBack, BorderLayout.WEST);

        JLabel lbl = new JLabel("QUẢN LÝ NHÂN VIÊN", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        pn.add(lbl, BorderLayout.CENTER);

        // sự kiện quay lại, chỉ callback
        btnBack.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(this).dispose();
        });

        return pn;
    }

    private JPanel buildFormPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel form = new JPanel(new GridLayout(3, 4, 15, 15));

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

    private JPanel buildTablePanel() {
        JPanel pn = new JPanel(new BorderLayout());
        pn.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

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

        pn.add(new JScrollPane(table), BorderLayout.CENTER);

        return pn;
    }

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
        return new NhanVien(
                txtMaNV.getText().trim(),
                txtTenNV.getText().trim(),
                txtSDT.getText().trim(),
                txtDiaChi.getText().trim(),
                txtNgaySinh.getText().trim(),
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
