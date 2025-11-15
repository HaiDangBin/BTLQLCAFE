package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import DAO.NhanVien_DAO;
import Entity.NhanVien;

public class NhanVien_GUI extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private NhanVien_DAO nvDAO;

    public NhanVien_GUI() {
        
        setLayout(new BorderLayout());

        nvDAO = new NhanVien_DAO();

        // ======== Table =========
        String[] cols = {"Mã NV", "Tên NV", "SĐT", "Mã Loại"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ======== Button refresh =========
        JButton btnRefresh = new JButton("Tải lại dữ liệu");
        btnRefresh.addActionListener(e -> loadData());
        add(btnRefresh, BorderLayout.SOUTH);

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        ArrayList<NhanVien> ds = nvDAO.getAllNhanVien();
        for (NhanVien nv : ds) {
            model.addRow(new Object[]{
                nv.getMaNV(),
                nv.getTenNV(),
                nv.getSdt(),
                nv.getChucVu()
            });
        }
    }

}
