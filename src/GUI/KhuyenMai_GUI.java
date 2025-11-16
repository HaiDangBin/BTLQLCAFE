 package GUI;

import DAO.KhuyenMai_DAO;
import Entity.KhuyenMai;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.ZoneId;

import java.util.Date;
import java.util.List;

/** Quản Lý Khuyến Mãi – layout & buttons theo Quản Lý Thuế, lọc realtime, JDateChooser cho ngày. */
public class KhuyenMai_GUI extends JPanel {

    private final KhuyenMai_DAO dao;

    /* ===== Filters (giống Thuế) ===== */
    private final JTextField tfMaFilter   = new JTextField();
    private final JTextField tfTenFilter  = new JTextField();
    private final JTextField tfMoTaFilter = new JTextField();
    private final JTextField tfDkFilter   = new JTextField();
    private final JDateChooser dcApDungF  = makeDateChooser();
    private final JDateChooser dcKetThucF = makeDateChooser();

    /* ===== Bảng ===== */
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Mã KM", "Tên", "Mô tả", "Điều kiện", "Ngày áp dụng", "Ngày kết thúc"}, 0
    ){
        @Override public boolean isCellEditable(int r,int c){ return false; }
    };
    private final JTable table = styledTable(model);

    /* ===== Form CRUD (đồng bộ với filters về style) ===== */
    private final JTextField tfMa   = new JTextField();
    private final JTextField tfTen  = new JTextField();
    private final JTextField tfMoTa = new JTextField();
    private final JTextField tfDieuKien = new JTextField();
    private final JDateChooser dcApDung  = makeDateChooser();
    private final JDateChooser dcKetThuc = makeDateChooser();

    /* ===== Nút giống Thuế ===== */
    private final JButton btnMoi     = makeBtn("Làm mới",  0x0D6EFD);
    private final JButton btnCapNhat = makeBtn("Cập nhật", 0x20C997);
    private final JButton btnTaoMoi  = makeBtn("Tạo mới",  0x6F42C1);
    private final JButton btnXoa     = makeBtn("Xóa",      0xDC3545);

    public KhuyenMai_GUI() {
        this.dao = new KhuyenMai_DAO();

        setLayout(new BorderLayout());
        setOpaque(false);

        add(buildHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(8,8));
        center.setBorder(new EmptyBorder(8,8,8,8));
        add(center, BorderLayout.CENTER);

        center.add(buildTopBar(), BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        hookActions();
        loadData();
    }

    /* ================= Header ================= */

    private JComponent buildHeader(){
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(0x009970)); // xanh lá giống Thuế
        JLabel lb = new JLabel("QUẢN LÝ KHUYẾN MÃI", SwingConstants.CENTER);
        lb.setForeground(Color.WHITE);
        lb.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lb.setBorder(new EmptyBorder(10,0,10,0));
        p.add(lb, BorderLayout.CENTER);
        return p;
    }

    /* ================= Filters + Buttons (style Thuế) ================= */

    private JComponent buildTopBar(){
        JPanel wrap = new JPanel(new BorderLayout());
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(4,4,4,4));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4,6,4,6);
        g.fill = GridBagConstraints.HORIZONTAL;

        int r=0,c=0;

        // Dòng 1: Mã | Tên | Mô tả
        g.gridy=r; g.gridx=c++; grid.add(lbl("Mã KM:"), g);
        g.gridx=c++; g.weightx=1; grid.add(tfMaFilter, g); g.weightx=0;

        g.gridx=c++; grid.add(lbl("Tên:"), g);
        g.gridx=c++; g.weightx=1; grid.add(tfTenFilter, g); g.weightx=0;

        g.gridx=c++; grid.add(lbl("Mô tả:"), g);
        g.gridx=c++; g.weightx=1; grid.add(tfMoTaFilter, g); g.weightx=0;

        // Dòng 2: Điều kiện | Ngày áp dụng | Ngày kết thúc | Buttons
        r++; c=0;
        g.gridy=r; g.gridx=c++; grid.add(lbl("Điều kiện:"), g);
        g.gridx=c++; g.weightx=1; grid.add(tfDkFilter, g); g.weightx=0;

        g.gridx=c++; grid.add(lbl("Ngày áp dụng:"), g);
        g.gridx=c++; g.weightx=1; grid.add(dcApDungF, g); g.weightx=0;

        g.gridx=c++; grid.add(lbl("Ngày kết thúc:"), g);
        g.gridx=c++; g.weightx=1; grid.add(dcKetThucF, g); g.weightx=0;

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(btnMoi); right.add(btnCapNhat); right.add(btnTaoMoi); right.add(btnXoa);

        wrap.add(grid, BorderLayout.CENTER);
        wrap.add(right, BorderLayout.EAST);
        return wrap;
    }

    private JLabel lbl(String s){
        JLabel l = new JLabel(s);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return l;
    }

    /* ================= Table ================= */

    private static JTable styledTable(DefaultTableModel m){
        JTable t = new JTable(m){
            @Override public Component prepareRenderer(javax.swing.table.TableCellRenderer r,int row,int col){
                Component c = super.prepareRenderer(r,row,col);
                if(!isRowSelected(row))
                    c.setBackground(row%2==0?new Color(250,250,250):new Color(240,240,240));
                else c.setBackground(new Color(220,235,255));
                return c;
            }
        };
        t.setRowHeight(26);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTableHeader h = t.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 14));
        h.setBackground(new Color(190,236,223));
        h.setForeground(Color.DARK_GRAY);
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        t.getColumnModel().getColumn(4).setCellRenderer(center);
        t.getColumnModel().getColumn(5).setCellRenderer(center);
        // width gợi ý
        int[] w = {110,220,260,260,120,120};
        for(int i=0;i<w.length;i++) t.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        return t;
    }

    private static JButton makeBtn(String text, int rgb){
        JButton b = new JButton(text);
        b.setBackground(new Color(rgb));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6,14,6,14));
        return b;
    }

    private static JDateChooser makeDateChooser(){
        JDateChooser dc = new JDateChooser();
        dc.setDateFormatString("yyyy-MM-dd");
        dc.setPreferredSize(new Dimension(160, 26));
        return dc;
    }

    private static String n(Object o){ return o==null? "": String.valueOf(o); }
    private static Date toUtilDate(LocalDate d){
        return d==null? null: Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    private static LocalDate fromUtilDate(Date d){
        return d==null? null: d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /* ================= Data & Actions ================= */

    private void hookActions(){
        // lọc realtime
        DocumentListener live = new DocumentListener(){
            private void go(){ SwingUtilities.invokeLater(KhuyenMai_GUI.this::applyFilters); }
            @Override public void insertUpdate(DocumentEvent e){ go(); }
            @Override public void removeUpdate(DocumentEvent e){ go(); }
            @Override public void changedUpdate(DocumentEvent e){ go(); }
        };
        tfMaFilter.getDocument().addDocumentListener(live);
        tfTenFilter.getDocument().addDocumentListener(live);
        tfMoTaFilter.getDocument().addDocumentListener(live);
        tfDkFilter.getDocument().addDocumentListener(live);

        PropertyChangeListener dateLive = evt -> {
            if("date".equals(evt.getPropertyName())) applyFilters();
        };
        dcApDungF.addPropertyChangeListener(dateLive);
        dcKetThucF.addPropertyChangeListener(dateLive);

        // CRUD
        btnMoi.addActionListener(e -> { clearFilters(); loadData(); });

        btnTaoMoi.addActionListener(e -> onAdd());
        btnCapNhat.addActionListener(e -> onEdit());
        btnXoa.addActionListener(e -> onDelete());

        // click bảng -> đổ form
        table.getSelectionModel().addListSelectionListener(e -> {
            if(e.getValueIsAdjusting()) return;
            int r = table.getSelectedRow(); if(r<0) return;
            tfMa.setText(n(model.getValueAt(r,0)));
            tfTen.setText(n(model.getValueAt(r,1)));
            tfMoTa.setText(n(model.getValueAt(r,2)));
            tfDieuKien.setText(n(model.getValueAt(r,3)));
            dcApDung.setDate(toUtilDate(s2d(model.getValueAt(r,4))));
            dcKetThuc.setDate(toUtilDate(s2d(model.getValueAt(r,5))));
        });
    }

    private static LocalDate s2d(Object o){
        if(o==null) return null;
        String s = String.valueOf(o).trim();
        if(s.isEmpty()) return null;
        return LocalDate.parse(s);
    }

    private void loadData(){
        try{
            List<KhuyenMai> list = dao.findAll();
            fillTable(list);
        }catch(Exception ex){ error(ex); }
    }

    private void fillTable(List<KhuyenMai> list){
        model.setRowCount(0);
        for(KhuyenMai k : list){
            model.addRow(new Object[]{
                    n(k.getMaKM()),
                    n(k.getTenKM()),
                    n(k.getMoTa()),
                    n(k.getDieuKienApDung()),
                    k.getNgayBD()==null? "": k.getNgayBD().toString(),
                    k.getNgayKT()==null? "": k.getNgayKT().toString()
            });
        }
        table.clearSelection();
    }

    private void applyFilters(){
        String qMa   = tfMaFilter.getText().trim().toLowerCase();
        String qTen  = tfTenFilter.getText().trim().toLowerCase();
        String qMoTa = tfMoTaFilter.getText().trim().toLowerCase();
        String qDk   = tfDkFilter.getText().trim().toLowerCase();
        LocalDate f  = fromUtilDate(dcApDungF.getDate());
        LocalDate t  = fromUtilDate(dcKetThucF.getDate());

        try{
            List<KhuyenMai> list = dao.findAll();
            list.removeIf(k -> {
                if(!qMa.isEmpty()   && (k.getMaKM()==null   || !k.getMaKM().toLowerCase().contains(qMa))) return true;
                if(!qTen.isEmpty()  && (k.getTenKM()==null  || !k.getTenKM().toLowerCase().contains(qTen))) return true;
                if(!qMoTa.isEmpty() && (k.getMoTa()==null   || !k.getMoTa().toLowerCase().contains(qMoTa))) return true;
                if(!qDk.isEmpty()   && (k.getDieuKienApDung()==null || !k.getDieuKienApDung().toLowerCase().contains(qDk))) return true;

                if(f==null && t==null) return false;
                LocalDate bd = k.getNgayBD()==null? LocalDate.MIN: k.getNgayBD();
                LocalDate kt = k.getNgayKT()==null? LocalDate.MAX: k.getNgayKT();
                if(f!=null && kt.isBefore(f)) return true;
                if(t!=null && bd.isAfter(t))  return true;
                return false;
            });
            fillTable(list);
        }catch(Exception ex){ error(ex); }
    }

    /* ================= CRUD ================= */

    private void onAdd(){
        KhuyenMai km = inputDialog(null);
        if(km==null) return;
        try{
            if(dao.insert(km)){ loadData(); info("Đã thêm khuyến mãi."); }
            else warn("Thêm không thành công.");
        }catch(Exception ex){ error(ex); }
    }

    private void onEdit(){
        int r = table.getSelectedRow();
        if(r<0){ warn("Chọn dòng cần sửa."); return; }

        KhuyenMai old = new KhuyenMai();
        old.setMaKM(n(model.getValueAt(r,0)));
        old.setTenKM(n(model.getValueAt(r,1)));
        old.setMoTa(n(model.getValueAt(r,2)));
        old.setDieuKienApDung(n(model.getValueAt(r,3)));
        old.setNgayBD(s2d(model.getValueAt(r,4)));
        old.setNgayKT(s2d(model.getValueAt(r,5)));

        KhuyenMai km = inputDialog(old);
        if(km==null) return;

        try{
            if(dao.update(km)){ loadData(); info("Đã cập nhật."); }
            else warn("Cập nhật không thành công.");
        }catch(Exception ex){ error(ex); }
    }

    private void onDelete(){
        int r = table.getSelectedRow();
        if(r<0){ warn("Chọn dòng cần xóa."); return; }
        String ma = String.valueOf(model.getValueAt(r,0));
        if(JOptionPane.showConfirmDialog(this,"Xóa khuyến mãi: "+ma+" ?","Xác nhận",
                JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION) return;
        try{
            if(dao.delete(ma)){ loadData(); info("Đã xóa."); }
            else warn("Xóa không thành công.");
        }catch(Exception ex){ error(ex); }
    }

    private KhuyenMai inputDialog(KhuyenMai initial){
        JTextField tfId  = new JTextField(initial==null? "": n(initial.getMaKM()));
        JTextField tfTen = new JTextField(initial==null? "": n(initial.getTenKM()));
        JTextField tfMo  = new JTextField(initial==null? "": n(initial.getMoTa()));
        JTextField tfDk  = new JTextField(initial==null? "": n(initial.getDieuKienApDung()));

        JDateChooser dcBd = makeDateChooser();
        JDateChooser dcKt = makeDateChooser();
        if(initial!=null){
            dcBd.setDate(toUtilDate(initial.getNgayBD()));
            dcKt.setDate(toUtilDate(initial.getNgayKT()));
        }

        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6,6,6,6);
        g.fill = GridBagConstraints.HORIZONTAL;
        int r=0,c=0;

        g.gridy=r; g.gridx=c++; p.add(new JLabel("Mã KM:"), g);
        g.gridx=c++; g.weightx=1; p.add(tfId, g); g.weightx=0;

        g.gridx=c++; p.add(new JLabel("Tên:"), g);
        g.gridx=c++; g.weightx=1; p.add(tfTen, g); g.weightx=0;

        r++; c=0;
        g.gridy=r; g.gridx=c++; p.add(new JLabel("Mô tả:"), g);
        g.gridx=c++; g.gridwidth=3; g.weightx=1; p.add(tfMo, g); g.gridwidth=1; g.weightx=0;

        r++; c=0;
        g.gridy=r; g.gridx=c++; p.add(new JLabel("Điều kiện:"), g);
        g.gridx=c++; g.gridwidth=3; g.weightx=1; p.add(tfDk, g); g.gridwidth=1; g.weightx=0;

        r++; c=0;
        g.gridy=r; g.gridx=c++; p.add(new JLabel("Ngày áp dụng:"), g);
        g.gridx=c++; p.add(dcBd, g);
        g.gridx=c++; p.add(new JLabel("Ngày kết thúc:"), g);
        g.gridx=c++; p.add(dcKt, g);

        if(JOptionPane.showConfirmDialog(this, p, initial==null? "Tạo mới":"Cập nhật",
                JOptionPane.OK_CANCEL_OPTION)!=JOptionPane.OK_OPTION) return null;

        KhuyenMai k = new KhuyenMai();
        k.setMaKM(tfId.getText().trim());
        k.setTenKM(tfTen.getText().trim());
        k.setMoTa(tfMo.getText().trim());
        k.setDieuKienApDung(tfDk.getText().trim());
        k.setNgayBD(fromUtilDate(dcBd.getDate()));
        k.setNgayKT(fromUtilDate(dcKt.getDate()));
        return k;
    }

    /* ================= Utils ================= */

    private void clearFilters(){
        tfMaFilter.setText("");
        tfTenFilter.setText("");
        tfMoTaFilter.setText("");
        tfDkFilter.setText("");
        dcApDungF.setDate(null);
        dcKetThucF.setDate(null);
        table.clearSelection();
    }

    private void info(String m){ JOptionPane.showMessageDialog(this,m,"Thông báo",JOptionPane.INFORMATION_MESSAGE); }
    private void warn(String m){ JOptionPane.showMessageDialog(this,m,"Cảnh báo",JOptionPane.WARNING_MESSAGE); }
    private void error(Exception e){
        e.printStackTrace();
        JOptionPane.showMessageDialog(this,e.getMessage(),"Lỗi",JOptionPane.ERROR_MESSAGE);
    }
}
