package GUI;

import DAO.Thue_DAO;
import DAO.Thue_DAO.Thue;
import connectDB.DBconnection;
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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** QUẢN LÝ THUẾ – JDateChooser + CRUD giống KhuyenMai_GUI, fix List ambiguous, map TÊN LOẠI -> MÃ LOẠI. */
public class Thue_GUI extends JPanel {

    private final Thue_DAO dao;

    private final DefaultTableModel model;
    private final JTable table;

    /* FILTER */
    private final JTextField tfMaFilter    = new JTextField();
    private final JTextField tfTenFilter   = new JTextField();
    private final JTextField tfDoiTuongF   = new JTextField();
    private final JTextField tfLoaiF       = new JTextField(); // gõ tên hoặc mã đều được
    private final JDateChooser dcApDungFilter  = makeDateChooser();
    private final JDateChooser dcKetThucFilter = makeDateChooser();

    /* NÚT */
    private final JButton btnMoi     = makeBtn("Làm mới",  0x0D6EFD);
    private final JButton btnCapNhat = makeBtn("Cập nhật", 0x20C997);
    private final JButton btnTaoMoi  = makeBtn("Tạo mới",  0x6F42C1);
    private final JButton btnXoa     = makeBtn("Xóa",      0xDC3545);

    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /* CACHE & STATE */
    private boolean isFiltering = false;
    private List<Thue> cacheAll = new ArrayList<>();
    /** map: maLoai -> tenLoai (dùng để hiển thị & map ngược) */
    private Map<String,String> cacheLoai = Map.of();

    public Thue_GUI() {
        this.dao = new Thue_DAO(DBconnection.getConnection());

        setLayout(new BorderLayout());
        setOpaque(false);

        add(buildHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(8,8));
        center.setBorder(new EmptyBorder(8,8,8,8));
        add(center, BorderLayout.CENTER);

        center.add(buildTopBar(), BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{
                "Mã thuế","Tên thuế","Đối tượng","Tên loại thuế","Ngày áp dụng","Ngày kết thúc"
        },0){
            @Override public boolean isCellEditable(int r,int c){ return false; }
        };

        table = styledTable(model);
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        hookActions();
        reload();
        setKeyStrokes();
    }

    /* ================= Header ================= */

    private JComponent buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(0x009970));
        JLabel lb = new JLabel("QUẢN LÝ THUẾ", SwingConstants.CENTER);
        lb.setForeground(Color.WHITE);
        lb.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lb.setBorder(new EmptyBorder(10,0,10,0));
        p.add(lb, BorderLayout.CENTER);
        return p;
    }

    /* ================= Filters bar ================= */

    private JComponent buildTopBar() {
        JPanel wrap = new JPanel(new BorderLayout());
        JPanel left = new JPanel(new GridBagLayout());
        left.setOpaque(false);
        left.setBorder(new EmptyBorder(4,4,4,4));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4,6,4,6);
        g.fill = GridBagConstraints.HORIZONTAL;

        int c=0,r=0;

        g.gridy=r; g.gridx=c++; left.add(new JLabel("Mã thuế:"),g);
        g.gridx=c++; g.weightx=1; left.add(tfMaFilter,g); g.weightx=0;

        g.gridx=c++; left.add(new JLabel("Tên thuế:"),g);
        g.gridx=c++; g.weightx=1; left.add(tfTenFilter,g); g.weightx=0;

        g.gridx=c++; left.add(new JLabel("Đối tượng:"),g);
        g.gridx=c++; g.weightx=1; left.add(tfDoiTuongF,g); g.weightx=0;

        r++; c=0;
        g.gridy=r; g.gridx=c++; left.add(new JLabel("Ngày áp dụng:"),g);
        g.gridx=c++; g.weightx=1; left.add(dcApDungFilter,g); g.weightx=0;

        g.gridx=c++; left.add(new JLabel("Ngày kết thúc:"),g);
        g.gridx=c++; g.weightx=1; left.add(dcKetThucFilter,g); g.weightx=0;

        g.gridx=c++; left.add(new JLabel("Loại thuế:"),g);
        g.gridx=c++; g.weightx=1; left.add(tfLoaiF,g); g.weightx=0;

        wrap.add(left,BorderLayout.CENTER);

        JPanel right=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));
        right.setOpaque(false);
        right.add(btnMoi); right.add(btnCapNhat); right.add(btnTaoMoi); right.add(btnXoa);
        wrap.add(right,BorderLayout.EAST);

        return wrap;
    }

    /* ================= Table ================= */

    private JTable styledTable(DefaultTableModel m){
        JTable t = new JTable(m){
            @Override public Component prepareRenderer(javax.swing.table.TableCellRenderer r,int row,int col){
                Component c=super.prepareRenderer(r,row,col);
                if(!isRowSelected(row))
                    c.setBackground(row%2==0?new Color(250,250,250):new Color(240,240,240));
                else c.setBackground(new Color(220,235,255));
                return c;
            }
        };
        t.setRowHeight(26);
        t.setFont(new Font("Segoe UI",Font.PLAIN,14));
        JTableHeader h=t.getTableHeader();
        h.setFont(new Font("Segoe UI",Font.BOLD,14));
        h.setBackground(new Color(190,236,223));
        h.setForeground(Color.DARK_GRAY);

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

    private static JDateChooser makeDateChooser(){
        JDateChooser dc = new JDateChooser();
        dc.setDateFormatString("yyyy-MM-dd");
        dc.setPreferredSize(new java.awt.Dimension(150, 26));
        return dc;
    }

    /* ================= Actions ================= */

    private void hookActions(){
        // Lọc realtime (chạy trên EDT bằng invokeLater, tránh re-entrancy)
        DocumentListener auto = new DocumentListener() {
            private void safe() { SwingUtilities.invokeLater(() -> { if(!isFiltering) applyFilters(); }); }
            @Override public void insertUpdate(DocumentEvent e) { safe(); }
            @Override public void removeUpdate(DocumentEvent e) { safe(); }
            @Override public void changedUpdate(DocumentEvent e) { safe(); }
        };
        tfMaFilter.getDocument().addDocumentListener(auto);
        tfTenFilter.getDocument().addDocumentListener(auto);
        tfDoiTuongF.getDocument().addDocumentListener(auto);
        tfLoaiF.getDocument().addDocumentListener(auto);

        dcApDungFilter.addPropertyChangeListener(evt -> { if ("date".equals(evt.getPropertyName())) applyFilters(); });
        dcKetThucFilter.addPropertyChangeListener(evt -> { if ("date".equals(evt.getPropertyName())) applyFilters(); });

        btnMoi.addActionListener(e -> { clearFilters(); reload(); });

        btnTaoMoi.addActionListener(e -> onAdd());
        btnCapNhat.addActionListener(e -> onEdit());
        btnXoa.addActionListener(e -> onDelete());

        
    }

    /* ================= Data ================= */

    private void reload(){
        try{
            cacheAll  = dao.findAll();
            cacheLoai = dao.loadLoaiMap(); // ma -> ten
            fillTable(cacheAll, cacheLoai);
        }catch(Exception ex){ error(ex); }
    }

    /** Lọc theo tất cả các ô, hỗ trợ gõ tên hoặc mã loại. */
    private void applyFilters(){
        if(isFiltering) return;
        isFiltering = true;
        try{
            String ma = val(tfMaFilter);
            String ten = val(tfTenFilter);
            String dtg = val(tfDoiTuongF);
            String loai = val(tfLoaiF);

            LocalDate ap = toLocal(dcApDungFilter.getDate());
            LocalDate kt = toLocal(dcKetThucFilter.getDate());

            List<Thue> list = new ArrayList<>(cacheAll);
            Map<String,String> loaiMap = cacheLoai;

            list.removeIf(t -> {
                if(!ma.isEmpty()  && (t.getMaThue()==null || !t.getMaThue().toLowerCase().contains(ma))) return true;
                if(!ten.isEmpty() && (t.getTenThue()==null || !t.getTenThue().toLowerCase().contains(ten))) return true;
                if(!dtg.isEmpty()){
                    String v=t.getDoiTuongApDung();
                    if(v==null || !v.toLowerCase().contains(dtg)) return true;
                }

                if(!loai.isEmpty()){
                    String tenLoai = loaiMap.getOrDefault(t.getMaLoaiThue(), "");
                    boolean byName = tenLoai!=null && tenLoai.toLowerCase().contains(loai);
                    boolean byCode = t.getMaLoaiThue()!=null && t.getMaLoaiThue().toLowerCase().contains(loai);
                    if(!byName && !byCode) return true;
                }

                if(ap==null && kt==null) return false;
                LocalDate left  = t.getNgayApDung();
                LocalDate right = t.getNgayKetThuc()==null?LocalDate.MAX:t.getNgayKetThuc();
                if(ap!=null && right.isBefore(ap)) return true;
                if(kt!=null && left.isAfter(kt)) return true;
                return false;
            });

            fillTable(list, loaiMap);
            if(model.getRowCount()>0){
                SwingUtilities.invokeLater(() -> table.getSelectionModel().setSelectionInterval(0,0));
            }
        }catch(Exception ex){ error(ex); }
        finally { isFiltering=false; }
    }

    private void fillTable(List<Thue> list, Map<String,String> loaiMap){
        model.setRowCount(0);
        for(Thue t:list){
            String tenLoai = loaiMap.getOrDefault(t.getMaLoaiThue(), t.getMaLoaiThue());
            model.addRow(new Object[]{
                    t.getMaThue(),
                    t.getTenThue(),
                    t.getDoiTuongApDung(),
                    tenLoai,
                    t.getNgayApDung()==null?"":F.format(t.getNgayApDung()),
                    t.getNgayKetThuc()==null?"":F.format(t.getNgayKetThuc())
            });
        }
        table.clearSelection();
    }

    /* ================= CRUD  ================= */

    private void onAdd(){
        Thue t = inputDialog(null);
        if(t==null) return;
        try{
            if(dao.create(t)){ reload(); info("Đã thêm thuế."); }
            else warn("Thêm không thành công.");
        }catch(Exception ex){ error(ex); }
    }

    private void onEdit(){
        int r = table.getSelectedRow();
        if(r<0){ warn("Chọn dòng cần sửa."); return; }

        // Lấy dữ liệu hiện tại từ bảng để đổ form
        Thue cur = new Thue();
        cur.setMaThue(String.valueOf(model.getValueAt(r,0)));
        cur.setTenThue(String.valueOf(model.getValueAt(r,1)));
        cur.setDoiTuongApDung(n(model.getValueAt(r,2)));
        cur.setMaLoaiThue(resolveCodeFromDisplay(n(model.getValueAt(r,3)))); // tên -> mã nếu có
        cur.setNgayApDung(s2d(model.getValueAt(r,4)));
        cur.setNgayKetThuc(s2d(model.getValueAt(r,5)));

        Thue edited = inputDialog(cur);
        if(edited==null) return;

        try{
            if(dao.update(edited)){ reload(); info("Đã cập nhật."); }
            else warn("Cập nhật không thành công.");
        }catch(Exception ex){ error(ex); }
    }

    private void onDelete(){
        int r = table.getSelectedRow();
        if(r<0){ warn("Chọn dòng cần xóa."); return; }
        String id = String.valueOf(model.getValueAt(r,0));
        if(JOptionPane.showConfirmDialog(this,"Xóa thuế: "+id+" ?","Xác nhận",
                JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION) return;
        try{
            if(dao.delete(id)){ reload(); info("Đã xóa."); }
            else warn("Xóa không thành công.");
        }catch(Exception ex){ error(ex); }
    }

    /** Dialog nhập/sửa giống KhuyenMai – có map TÊN LOẠI -> MÃ LOẠI để tránh lỗi FK. */
    private Thue inputDialog(Thue initial){
        JTextField tfId   = new JTextField(initial==null?"":n(initial.getMaThue()));
        JTextField tfTen  = new JTextField(initial==null?"":n(initial.getTenThue()));
        JTextField tfDT   = new JTextField(initial==null?"":n(initial.getDoiTuongApDung()));
        JTextField tfLoai = new JTextField(initial==null?"": displayLoai(initial.getMaLoaiThue())); // hiển thị tên nếu có

        JDateChooser dcAp  = makeDateChooser();
        JDateChooser dcKt  = makeDateChooser();
        if(initial!=null){
            dcAp.setDate(toUtil(initial.getNgayApDung()));
            dcKt.setDate(toUtil(initial.getNgayKetThuc()));
        }

        JPanel p = new JPanel(new java.awt.GridLayout(0,2,6,6));
        p.add(new JLabel("Mã thuế:")); p.add(tfId);
        p.add(new JLabel("Tên thuế:")); p.add(tfTen);
        p.add(new JLabel("Đối tượng:")); p.add(tfDT);
        p.add(new JLabel("Loại thuế (gõ TÊN hoặc MÃ):")); p.add(tfLoai);
        p.add(new JLabel("Ngày áp dụng:")); p.add(dcAp);
        p.add(new JLabel("Ngày kết thúc:")); p.add(dcKt);

        String title = initial==null? "Tạo mới": "Cập nhật";
        if(JOptionPane.showConfirmDialog(this,p,title,JOptionPane.OK_CANCEL_OPTION)!=JOptionPane.OK_OPTION) return null;

        String id   = tfId.getText().trim();
        String name = tfTen.getText().trim();
        if(id.isEmpty() && initial==null) throw new IllegalArgumentException("Mã thuế không được trống.");
        if(name.isEmpty()) throw new IllegalArgumentException("Tên thuế không được trống.");

        LocalDate ap = toLocal(dcAp.getDate());
        LocalDate kt = toLocal(dcKt.getDate());

        // Người dùng có thể nhập TÊN hoặc MÃ — ta chuẩn hóa thành MÃ để lưu DB:
        String userLoai = tfLoai.getText().trim();
        String maLoai = normalizeLoai(userLoai);

        Thue t = new Thue(
                id,
                name,
                ap,
                kt,
                tfDT.getText().trim(),
                maLoai
        );
        return t;
    }

    /* ================= Helpers ================= */

    private static String val(JTextField tf){ return tf.getText().trim().toLowerCase(); }
    private static String n(Object o){ return o==null?"":String.valueOf(o); }

    private static LocalDate s2d(Object o){
        if(o==null) return null;
        String s=String.valueOf(o).trim();
        if(s.isEmpty()) return null;
        return LocalDate.parse(s, F);
    }

    private static LocalDate toLocal(java.util.Date d){
        return d==null?null: d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    private static java.util.Date toUtil(LocalDate d){
        if(d==null) return null;
        return java.util.Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /** Trả về tên loại từ mã nếu có; nếu không có, trả lại đầu vào. */
    private String displayLoai(String ma){
        if(ma==null) return "";
        String ten = cacheLoai.get(ma);
        return ten!=null?ten:ma;
    }

    /** Người dùng có thể gõ tên hoặc mã. Chuẩn hóa về MÃ để ghi DB. */
    private String normalizeLoai(String userInput){
        if(userInput==null || userInput.isBlank()) return ""; // để DAO tự xử/hoặc DB cho phép null
        String s = userInput.trim();
        // Nếu trùng trực tiếp một MÃ trong cache -> dùng luôn
        if(cacheLoai.containsKey(s)) return s;

        // Thử map theo TÊN (so sánh không phân biệt hoa thường)
        String lower = s.toLowerCase();
        for(Map.Entry<String,String> e : cacheLoai.entrySet()){
            if(Objects.equals(e.getValue(), null)) continue;
            if(e.getValue().trim().toLowerCase().equals(lower)){
                return e.getKey(); // MÃ
            }
        }

        // Không khớp tên nào -> ném lỗi để tránh FK lỗi
        throw new IllegalArgumentException("Loại thuế không hợp lệ, hãy nhập đúng TÊN loại (đã có) hoặc MÃ loại.");
    }

    /** Nếu hiển thị tên trong bảng, khi sửa cần suy ra lại mã. */
    private String resolveCodeFromDisplay(String display){
        if(display==null || display.isBlank()) return "";
        // Nếu trùng mã thì trả mã
        if(cacheLoai.containsKey(display)) return display;
        // Nếu là tên -> tìm mã
        String lower = display.trim().toLowerCase();
        for(Map.Entry<String,String> e: cacheLoai.entrySet()){
            String ten = e.getValue();
            if(ten!=null && ten.trim().toLowerCase().equals(lower)) return e.getKey();
        }
        return display; // fallback
    }

    private void clearFilters(){
        tfMaFilter.setText(""); tfTenFilter.setText("");
        tfDoiTuongF.setText(""); tfLoaiF.setText("");
        dcApDungFilter.setDate(null); dcKetThucFilter.setDate(null);
        table.clearSelection();
    }

    private void setKeyStrokes(){
        register("F3",()->btnTaoMoi.doClick());
        register("F4",()->btnCapNhat.doClick());
        register("F5",()->btnXoa.doClick());
        register("F7",()->btnMoi.doClick());
    }

    private void register(String key,Runnable run){
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(key),key);
        getActionMap().put(key,new AbstractAction(){
            @Override public void actionPerformed(java.awt.event.ActionEvent e){ run.run(); }
        });
    }

    private void info(String m){ JOptionPane.showMessageDialog(this,m,"Thông báo",JOptionPane.INFORMATION_MESSAGE); }
    private void warn(String m){ JOptionPane.showMessageDialog(this,m,"Cảnh báo",JOptionPane.WARNING_MESSAGE); }
    private void error(Exception ex){
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,ex.getMessage(),"Lỗi",JOptionPane.ERROR_MESSAGE);
    }
}
