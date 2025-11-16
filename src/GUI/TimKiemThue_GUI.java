package GUI;

import DAO.Thue_DAO;
import DAO.Thue_DAO.Thue;
import connectDB.DBconnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import com.toedter.calendar.JDateChooser;

public class TimKiemThue_GUI extends JPanel {

    private final Thue_DAO dao;

    private final DefaultTableModel model;
    private final JTable table;

    private final JTextField tfMaFilter  = new JTextField();
    private final JTextField tfTenFilter = new JTextField();
    private final JComboBox<String> cbLoai = new JComboBox<>();

    private final JDateChooser dcApDung  = new JDateChooser();
    private final JDateChooser dcKetThuc = new JDateChooser();

    private final JButton btnTim = makeBtn("Tìm", 0x0D6EFD);
    private final JButton btnMoi = makeBtn("Làm mới", 0x6C757D);

    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // MAP mã loại → tên loại (để hiển thị)
    private Map<String,String> cacheLoai = Map.of();

    public TimKiemThue_GUI() {
        this.dao = new Thue_DAO(DBconnection.getConnection());

        dcApDung.setDateFormatString("yyyy-MM-dd");
        dcKetThuc.setDateFormatString("yyyy-MM-dd");

        setLayout(new BorderLayout());
        setOpaque(false);

        add(buildHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(8, 8));
        center.setBorder(new EmptyBorder(8, 8, 8, 8));
        add(center, BorderLayout.CENTER);

        center.add(buildTopBar(), BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{
                "Mã thuế", "Tên thuế", "Đối tượng áp dụng", "Tên loại thuế", "Ngày áp dụng", "Ngày kết thúc"
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = styledTable(model);
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        hookActions();
        reload();
        setKeyStrokes();
    }

    private JComponent buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(0xCC7A00));
        JLabel lb = new JLabel("TÌM KIẾM THUẾ", SwingConstants.CENTER);
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

        g.gridy = r; g.gridx = c++; grid.add(lbl("Mã thuế:"), g);
        g.gridx = c++; g.weightx = 1; grid.add(tfMaFilter, g); g.weightx = 0;

        g.gridx = c++; grid.add(lbl("Tên thuế:"), g);
        g.gridx = c++; g.weightx = 1; grid.add(tfTenFilter, g); g.weightx = 0;

        g.gridx = c++; grid.add(lbl("Loại:"), g);
        g.gridx = c++; g.weightx = 1; grid.add(cbLoai, g); g.weightx = 0;

        r++; c = 0;

        g.gridy = r; g.gridx = c++; grid.add(lbl("Ngày áp dụng:"), g);
        g.gridx = c++; g.weightx = 1; grid.add(dcApDung, g); g.weightx = 0;

        g.gridx = c++; grid.add(lbl("Ngày kết thúc:"), g);
        g.gridx = c++; g.weightx = 1; grid.add(dcKetThuc, g); g.weightx = 0;

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);
        actions.add(btnTim);
        actions.add(btnMoi);

        g.gridx = c; g.gridwidth = 2; grid.add(actions, g);

        wrap.add(grid, BorderLayout.CENTER);
        return wrap;
    }

    private JLabel lbl(String s) {
        JLabel l = new JLabel(s);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return l;
    }

    private JTable styledTable(DefaultTableModel m) {
        JTable t = new JTable(m){
            @Override public Component prepareRenderer(javax.swing.table.TableCellRenderer r,int row,int col){
                Component c = super.prepareRenderer(r,row,col);
                if(!isRowSelected(row))
                    c.setBackground(row%2==0?new Color(250,250,250):new Color(240,240,240));
                else c.setBackground(new Color(220,235,255));
                return c;
            }
        };
        t.setRowHeight(28);
        JTableHeader h=t.getTableHeader();
        h.setFont(new Font("Segoe UI",Font.BOLD,14));
        DefaultTableCellRenderer center=new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        t.getColumnModel().getColumn(4).setCellRenderer(center);
        t.getColumnModel().getColumn(5).setCellRenderer(center);
        return t;
    }

    private static JButton makeBtn(String text,int rgb){
        JButton b=new JButton(text);
        b.setBackground(new Color(rgb));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI",Font.BOLD,13));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6,14,6,14));
        return b;
    }

    private void hookActions(){
        btnTim.addActionListener(e -> doSearch());
        btnMoi.addActionListener(e -> { clearFilters(); reload(); });
    }

    private void reload(){
        try{
            List<Thue> list = dao.findAll();
            cacheLoai = dao.loadLoaiMap(); // Mã → Tên
            fillLoaiCombo(list);
            fillTable(list);
        }catch(Exception ex){ error(ex); }
    }

    private void fillLoaiCombo(List<Thue> list){
        cbLoai.removeAllItems();
        cbLoai.addItem("Trống");
        cacheLoai.values().forEach(cbLoai::addItem);
        cbLoai.setSelectedIndex(0);
    }

    private void doSearch(){
        try{
            String ma = tfMaFilter.getText().trim().toLowerCase();
            String ten = tfTenFilter.getText().trim().toLowerCase();

            String loai = Objects.equals(cbLoai.getSelectedItem(),"Trống") ?
                    "" : cbLoai.getSelectedItem().toString().trim().toLowerCase();

            LocalDate ap = dcApDung.getDate()==null?null:
                    dcApDung.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate kt = dcKetThuc.getDate()==null?null:
                    dcKetThuc.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            List<Thue> list = dao.findAll();
            list.removeIf(t ->{
                if(!ma.isEmpty() && !t.getMaThue().toLowerCase().contains(ma)) return true;
                if(!ten.isEmpty() && !t.getTenThue().toLowerCase().contains(ten)) return true;

                String tenLoai = cacheLoai.getOrDefault(t.getMaLoaiThue(),"").toLowerCase();
                if(!loai.isEmpty() && !tenLoai.contains(loai)) return true;

                LocalDate left=t.getNgayApDung();
                LocalDate right=t.getNgayKetThuc()==null?LocalDate.MAX:t.getNgayKetThuc();
                if(ap!=null && right.isBefore(ap)) return true;
                if(kt!=null && left.isAfter(kt)) return true;
                return false;
            });

            fillTable(list);
        }catch(Exception ex){ error(ex); }
    }

    private void fillTable(List<Thue> list){
        model.setRowCount(0);
        for(Thue t:list){
            String tenLoai = cacheLoai.getOrDefault(t.getMaLoaiThue(), t.getMaLoaiThue());
            model.addRow(new Object[]{
                    t.getMaThue(),
                    t.getTenThue(),
                    n(t.getDoiTuongApDung()),
                    tenLoai,
                    t.getNgayApDung()==null?"":F.format(t.getNgayApDung()),
                    t.getNgayKetThuc()==null?"":F.format(t.getNgayKetThuc())
            });
        }
        table.clearSelection();
    }

    private void clearFilters(){
        tfMaFilter.setText("");
        tfTenFilter.setText("");
        dcApDung.setDate(null);
        dcKetThuc.setDate(null);
        cbLoai.setSelectedIndex(0);
        table.clearSelection();
    }

    private void setKeyStrokes(){
        register("ENTER",()->btnTim.doClick());
        register("F7",()->btnMoi.doClick());
    }

    private void register(String key,Runnable run){
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(key),key);
        getActionMap().put(key,new AbstractAction(){
            @Override public void actionPerformed(java.awt.event.ActionEvent e){ run.run(); }
        });
    }

    private static String n(Object o){ return o==null?"":String.valueOf(o); }
    private void error(Exception ex){
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(),"Lỗi",JOptionPane.ERROR_MESSAGE);
    }
}
