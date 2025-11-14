package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.ArrayList;
import DAO.HoaDon_DAO;
import Entity.HoaDon;

public class HoaDon_GUI extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private HoaDon_DAO hdDAO;

    private JTextField txtMaHD, txtNgayLap, txtMaNV, txtMaKH, txtTongTien;

    public HoaDon_GUI() {
        setLayout(new BorderLayout());

        hdDAO = new HoaDon_DAO();

        // ===== INPUT PANEL =====
        JPanel inputPanel = new JPanel(new GridLayout(2, 5, 5, 5));
        txtMaHD = new JTextField();
        txtNgayLap = new JTextField();
        txtMaNV = new JTextField();
        txtMaKH = new JTextField();
        txtTongTien = new JTextField();

        inputPanel.add(new JLabel("Mã HD:")); inputPanel.add(txtMaHD);
        inputPanel.add(new JLabel("Ngày lập (yyyy-mm-dd):")); inputPanel.add(txtNgayLap);
        inputPanel.add(new JLabel("Mã NV:")); inputPanel.add(txtMaNV);
        inputPanel.add(new JLabel("Mã KH:")); inputPanel.add(txtMaKH);
        inputPanel.add(new JLabel("Tổng tiền:")); inputPanel.add(txtTongTien);

        add(inputPanel, BorderLayout.NORTH);

        // ===== TABLE =====
        String[] cols = {"Mã HD", "Ngày lập", "Mã NV", "Mã KH", "Tổng tiền"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== BUTTON PANEL =====
        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("Thêm");
        JButton btnDelete = new JButton("Xóa");
        JButton btnReload = new JButton("Tải lại");
        btnPanel.add(btnAdd);
        btnPanel.add(btnDelete);
        btnPanel.add(btnReload);
        add(btnPanel, BorderLayout.SOUTH);

        // ===== EVENTS =====
        btnAdd.addActionListener(e -> addHoaDon());
        btnDelete.addActionListener(e -> deleteHoaDon());
        btnReload.addActionListener(e -> loadData());

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        ArrayList<HoaDon> ds = hdDAO.getAllHoaDon();
        for (HoaDon hd : ds) {
            model.addRow(new Object[]{
                hd.getMaHD(), hd.getNgayLap(), hd.getMaNV(), hd.getMaKH(), hd.getTongTien()
            });
        }
    }

    private void addHoaDon() {
        try {
            HoaDon hd = new HoaDon(
                txtMaHD.getText(),
                Date.valueOf(txtNgayLap.getText()),
                txtMaNV.getText(),
                txtMaKH.getText(),
                Double.parseDouble(txtTongTien.getText())
            );
            if (hdDAO.addHoaDon(hd)) {
                JOptionPane.showMessageDialog(this, "✅ Thêm thành công!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Thêm thất bại!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "⚠️ Lỗi nhập liệu!");
        }
    }

    private void deleteHoaDon() {
        String ma = txtMaHD.getText();
        if (hdDAO.deleteHoaDon(ma)) {
            JOptionPane.showMessageDialog(this, "🗑️ Xóa thành công!");
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Xóa thất bại!");
        }
    }

}
