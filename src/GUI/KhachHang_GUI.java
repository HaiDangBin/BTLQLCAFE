package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import DAO.KhachHang_DAO;
import Entity.KhachHang;

public class KhachHang_GUI extends JPanel {
    private JTextField txtMa, txtTen, txtSDT, txtEmail;
    private DefaultTableModel model;
    private JTable table;
    private KhachHang_DAO dao;

    public KhachHang_GUI() {
        
        setLayout(new BorderLayout());

        dao = new KhachHang_DAO();

        // Input
        JPanel pnlTop = new JPanel(new GridLayout(2, 4, 5, 5));
        txtMa = new JTextField();
        txtTen = new JTextField();
        txtSDT = new JTextField();
        txtEmail = new JTextField();

        pnlTop.add(new JLabel("Mã KH:")); pnlTop.add(txtMa);
        pnlTop.add(new JLabel("Tên KH:")); pnlTop.add(txtTen);
        pnlTop.add(new JLabel("SĐT:")); pnlTop.add(txtSDT);
        pnlTop.add(new JLabel("Email:")); pnlTop.add(txtEmail);

        add(pnlTop, BorderLayout.NORTH);

        // Table
        String[] cols = {"Mã KH", "Tên KH", "SĐT", "Email"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons
        JPanel pnlBtn = new JPanel();
        JButton btnLoad = new JButton("Tải lại");
        JButton btnAdd = new JButton("Thêm KH");
        JButton btnDel = new JButton("Xóa KH");
        pnlBtn.add(btnLoad);
        pnlBtn.add(btnAdd);
        pnlBtn.add(btnDel);
        add(pnlBtn, BorderLayout.SOUTH);

        // Events
        btnLoad.addActionListener(e -> loadData());
        btnAdd.addActionListener(e -> addKH());
        btnDel.addActionListener(e -> delKH());

        setVisible(true);
        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        ArrayList<KhachHang> list = dao.getAllKH();
        for (KhachHang kh : list) {
            model.addRow(new Object[]{kh.getMaKH(), kh.getTenKH(), kh.getsDT(), kh.getEmail()});
        }
    }

    private void addKH() {
        KhachHang kh = new KhachHang(
                txtMa.getText().trim(),
                txtTen.getText().trim(),
                txtSDT.getText().trim(),
                txtEmail.getText().trim()
        );
        if (dao.addKH(kh)) {
            JOptionPane.showMessageDialog(this, "✅ Thêm khách hàng thành công!");
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Thêm thất bại!");
        }
    }

    private void delKH() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần xóa!");
            return;
        }
        String maKH = model.getValueAt(row, 0).toString();
        if (dao.deleteKH(maKH)) {
            JOptionPane.showMessageDialog(this, "🗑️ Xóa thành công!");
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Xóa thất bại!");
        }
    }
}
