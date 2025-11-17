package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.toedter.calendar.JDateChooser;

import DAO.Ban_DAO;
import DAO.DonDatBan_DAO;
import DAO.KhachHang_DAO;
import Entity.Ban;
import Entity.DonDatBan;
import Entity.KhachHang;


public class DonDatBan_GUI extends JPanel implements ActionListener{
    private JPanel pCen;
    private Box bx1;
    private Box pnA;
    private JTextField txtTenKH;
    private JTextField txtSDT;
    private Box pnB;
    private JTextField txtMaHD;
    private DefaultTableModel model;
    private JTable table;
    private DonDatBan_DAO DonDatBanDAO = new DonDatBan_DAO();
    private JDateChooser dateChooser;
    private DecimalFormat df = new DecimalFormat("#,### VND");
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private RoundedButton btnTimHD;
    private RoundedButton btnLamMoi;
    private RoundedButton btnXem;
    private JComboBox<String> comboBoxThoiGian;
    private String maDDBHienTai;
    private Ban banHienTai;
    private Ban_DAO banDAO = new Ban_DAO();
    private final double TIEN_COC_MOT_NGUOI = 10000.0;

	private DonDatBan_DAO ddbDAO; 

    public DonDatBan_GUI() {
    	ddbDAO = new DonDatBan_DAO();
        setLayout(new BorderLayout());

        // ======= HEADER =======
        JPanel pNorth = new JPanel();
        pNorth.setBackground(new Color(0,153,76));
        JLabel lbltitle = new JLabel("QUẢN LÝ ĐƠN ĐẶT BÀN", SwingConstants.CENTER);
        lbltitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lbltitle.setForeground(Color.WHITE);
        pNorth.add(lbltitle);
        add(pNorth, BorderLayout.NORTH);

        // ======= CENTER =======
        add(pCen = new JPanel(new BorderLayout()));
        pCen.setBackground(Color.WHITE);
        pCen.add(bx1 = new Box(BoxLayout.Y_AXIS), BorderLayout.NORTH);

        // --- Hàng 1 ---
        pnA = new Box(BoxLayout.X_AXIS);
        JLabel tenKH = new JLabel("Tên khách hàng:");
        tenKH.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tenKH.setPreferredSize(new Dimension(150,30));
        txtTenKH = new JTextField();

        JLabel sdt = new JLabel("Số điện thoại");
        sdt.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sdt.setPreferredSize(new Dimension(120,30));
        txtSDT = new JTextField();

        JLabel thoiGian = new JLabel("Thời gian");
        thoiGian.setFont(new Font("Segoe UI", Font.BOLD, 16));
        thoiGian.setPreferredSize(new Dimension(100,30));

        String [] ngay = {"Tất cả", "Hôm nay", "Hôm qua"}; 
        comboBoxThoiGian = new JComboBox<>(ngay);
        comboBoxThoiGian.setSelectedIndex(0);
        comboBoxThoiGian.setPreferredSize(new Dimension(110,30));

        JLabel ngayLap = new JLabel("Ngày lập:");
        ngayLap.setPreferredSize(new Dimension(90,30));
        ngayLap.setFont(new Font("Segoe UI", Font.BOLD, 16));

        dateChooser = new JDateChooser(); 
        dateChooser.setDate(null); 
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setPreferredSize(new Dimension(110, 25));
        dateChooser.setMaximumSize(new Dimension(110, 25));

        pnA.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        pnA.add(tenKH); pnA.add(txtTenKH);
        pnA.add(Box.createHorizontalStrut(30));
        pnA.add(sdt); pnA.add(txtSDT);
        pnA.add(Box.createHorizontalStrut(20));
        pnA.add(thoiGian); pnA.add(comboBoxThoiGian); 
        pnA.add(Box.createHorizontalStrut(20));
        pnA.add(ngayLap); pnA.add(dateChooser);       
        bx1.add(pnA);

        pnB = new Box(BoxLayout.X_AXIS);
        JLabel maHD = new JLabel("Mã đơn đặt bàn:");
        maHD.setFont(new Font("Segoe UI", Font.BOLD, 16));
        maHD.setPreferredSize(new Dimension(150,30));
        txtMaHD = new JTextField();
        btnTimHD = new RoundedButton("Tìm kiếm", new Color(128,128,128));
        btnLamMoi = new RoundedButton("Làm mới", new Color(128,128,128));
        btnXem = new RoundedButton("Xem", new Color(153,0,0));

        Dimension btnSize = new Dimension(350,30);
        pnB.setBorder(BorderFactory.createEmptyBorder(10,10,20,10));
        pnB.add(maHD); pnB.add(txtMaHD);
        pnB.add(Box.createHorizontalStrut(30));
        pnB.add(btnTimHD); btnTimHD.setMaximumSize(btnSize);
        pnB.add(Box.createHorizontalStrut(30));
        pnB.add(btnLamMoi); btnLamMoi.setMaximumSize(btnSize);
        pnB.add(Box.createHorizontalStrut(30));
        pnB.add(btnXem); btnXem.setMaximumSize(btnSize);
        bx1.add(pnB);

        // ======= BẢNG HÓA ĐƠN  =======
        model = new DefaultTableModel(new Object[]{
            "Mã đơn đặt bàn","Tên khách hàng","Số lượng khách", "Ngày lập",
            "Nhân viên", "Trạng thái"
        }, 0) {        	
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        table = new JTable(model);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        header.setPreferredSize(new Dimension(header.getWidth(), 25));
        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        renderer.setHorizontalAlignment(SwingConstants.LEFT);

        JScrollPane scrollPane = new JScrollPane(table);
        pCen.setBorder(BorderFactory.createEmptyBorder(0,5,10,5));
        pCen.add(scrollPane, BorderLayout.CENTER);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        wrap.add(pCen, BorderLayout.CENTER);
        add(wrap, BorderLayout.CENTER);

        setVisible(true);
        btnTimHD.addActionListener(this);
        btnLamMoi.addActionListener(this);
        btnXem.addActionListener(this);
       
        loadDonDatBanToTable();       
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(btnTimHD)) {
            handleTimKiem();
        } else if (o.equals(btnLamMoi)) {
            handleLamMoi();
        } else if (o.equals(btnXem)) {
        	handleXem();
        }
    }
   
    private void handleLamMoi() {
        txtMaHD.setText("");
        txtSDT.setText("");
        txtTenKH.setText("");
        dateChooser.setDate(null);
        comboBoxThoiGian.setSelectedIndex(0);
        loadDonDatBanToTable(); 
    }
    private void handleXem() {

        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn để xem!");
            return;
        }

        // Lấy mã đơn đặt bàn
        String maDDB = table.getValueAt(selectedRow, 0).toString();
        DonDatBan ddb = ddbDAO.getDonDatBanByMa(maDDB);

        if (ddb == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy đơn đặt bàn!");
            return;
        }
        Ban banDat = ddb.getBan();
        String tenKH = ddb.getMaKH().getTenKH(); 
        int soLuongKhach = ddb.getSoLuongKhach();
        
        double rawTienCoc = soLuongKhach * TIEN_COC_MOT_NGUOI;
        double rawThanhTien = rawTienCoc;
        double rawTienThua = 0.0; 

        String tienCoc = df.format(rawTienCoc);
        String vat = df.format(0.0); 
        String thanhTien = df.format(rawThanhTien);
        String tienKhach = df.format(rawThanhTien); 
        String tienThua = df.format(rawTienThua);

        String thoigiandat = new SimpleDateFormat("dd-MM-yyyy HH:mm")
                .format(ddb.getNgayDat());
        showHoaDonInDialog(
            tienCoc,
            vat,
            thanhTien,
            tienKhach,
            tienThua,
            tenKH,
            thoigiandat,
            banDat
        );
    }
 
    private void showHoaDonInDialog(String tienCoc, String vat, String thanhTien, String tienKhach, String tienThua, String tenKhachHang, String thoigiandat, Ban banHienTai) {
         JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "In hóa đơn", true);
         dialog.setSize(350, 450); 
         dialog.setLocationRelativeTo(this);
         dialog.setLayout(new BorderLayout(0, 0));
         JPanel header = new JPanel();
         header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
         header.setBackground(Color.WHITE); 

    
         JLabel title = new JLabel("Quán Caffe CornCorn", SwingConstants.CENTER);
         title.setFont(new Font("Segoe UI", Font.BOLD, 19));
         title.setAlignmentX(Component.CENTER_ALIGNMENT);
         
    
         JLabel dChi = new JLabel("<html><div style='text-align: center;'>ĐỊA CHỈ: 24 Lê Đức Thọ, p5, Gò Vấp</div></html>", SwingConstants.CENTER);
         dChi.setFont(new Font("Segoe UI", Font.BOLD, 16));
         dChi.setAlignmentX(Component.CENTER_ALIGNMENT);

         JLabel sdt = new JLabel("SĐT: 0242 545 567", SwingConstants.CENTER);
         sdt.setFont(new Font("Segoe UI", Font.BOLD, 16));
         sdt.setAlignmentX(Component.CENTER_ALIGNMENT);

         JLabel hd = new JLabel("HÓA ĐƠN ĐẶT BÀN", SwingConstants.CENTER);
         hd.setFont(new Font("Segoe UI", Font.BOLD, 19));
         hd.setAlignmentX(Component.CENTER_ALIGNMENT);
         
         header.add(title); 
         header.add(dChi); 
         header.add(sdt); 
         header.add(Box.createVerticalStrut(5)); 
         header.add(hd); 
         header.add(Box.createVerticalStrut(10)); // Tách dòng

         JPanel pnlMaDon = new JPanel(new BorderLayout());
         JLabel maDDBLbl = new JLabel("Mã đơn: " + (maDDBHienTai != null ? maDDBHienTai : "Đang tạo..."), SwingConstants.LEFT);
         maDDBLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
         pnlMaDon.add(maDDBLbl, BorderLayout.WEST);

         // Khách hàng
         JPanel pnlKH = new JPanel(new BorderLayout());
         JLabel kh = new JLabel("Khách hàng: "+ tenKhachHang, SwingConstants.LEFT);
         kh.setFont(new Font("Segoe UI", Font.BOLD, 13));
         pnlKH.add(kh, BorderLayout.WEST);

         // Bàn đặt
         JPanel pnlBan = new JPanel(new BorderLayout());
         JLabel banLbl = new JLabel("Bàn đặt: " + banHienTai.getMaBan() + " - Số khách: " + banHienTai.getSucChua(), SwingConstants.LEFT);
         banLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
         pnlBan.add(banLbl, BorderLayout.WEST);

         // Thời gian đặt
         JPanel pnlTG = new JPanel(new BorderLayout());
         JLabel tg = new JLabel("Thời gian đặt: " + thoigiandat, SwingConstants.LEFT);
         tg.setFont(new Font("Segoe UI", Font.BOLD, 13));
         pnlTG.add(tg, BorderLayout.WEST);

         header.add(pnlMaDon);
         header.add(pnlKH); 
         header.add(pnlBan);
         header.add(pnlTG);
         
         header.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
         dialog.add(header, BorderLayout.NORTH);

         JPanel footer = new JPanel(new GridLayout(7, 2, 5, 5));
         footer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
         footer.add(new JLabel("Tiền cọc:"));
         footer.add(new JLabel(tienCoc, SwingConstants.RIGHT));
         footer.add(new JLabel("VAT (0%):")); 
         footer.add(new JLabel(vat, SwingConstants.RIGHT));
         footer.add(new JLabel("Thành tiền:"));
         footer.add(new JLabel(thanhTien, SwingConstants.RIGHT));
         footer.add(new JLabel("Tiền khách trả:"));
         footer.add(new JLabel(tienKhach, SwingConstants.RIGHT));
         footer.add(new JLabel("Tiền thừa:"));
         footer.add(new JLabel(tienThua, SwingConstants.RIGHT));

         // Thêm một dòng chữ ký/cảm ơn
         JPanel pnlBottom = new JPanel(new BorderLayout());
         JLabel lblCamOn = new JLabel("<html><div style='text-align: center;'>Vui lòng giữ hóa đơn này<br>Hẹn gặp lại quý khách!</div></html>", SwingConstants.CENTER);
         lblCamOn.setFont(new Font("Segoe UI", Font.ITALIC, 14));
         lblCamOn.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
         pnlBottom.add(footer, BorderLayout.NORTH);
         pnlBottom.add(lblCamOn, BorderLayout.SOUTH);
         
         dialog.add(pnlBottom, BorderLayout.SOUTH);
         
         dialog.setVisible(true);
    }

    
    private void loadDonDatBanToTable() {
        model.setRowCount(0); 
        List<Object[]> danhSachDonDatBan = DonDatBanDAO.getAllDonDatBanDetails(); 
        
        for (Object[] rowData : danhSachDonDatBan) {
            model.addRow(rowData);
        }
    }
    private void handleTimKiem() {
        String maDDB = txtMaHD.getText().trim();
        String tenKH = txtTenKH.getText().trim();
        String sdt = txtSDT.getText().trim();
        Date utilNgayLap = dateChooser.getDate();
        String thoiGian = (String) comboBoxThoiGian.getSelectedItem();
        java.sql.Date sqlNgayLap = null;
        if (utilNgayLap != null) {
            sqlNgayLap = new java.sql.Date(utilNgayLap.getTime());
        }
        if (maDDB.isEmpty() && tenKH.isEmpty() && sdt.isEmpty() && utilNgayLap == null && thoiGian.equals("Tất cả")) {
            loadDonDatBanToTable();
            return;
        }
        List<Object[]> ketQuaTimKiem = DonDatBanDAO.findDonDatBan(maDDB, tenKH, sdt, sqlNgayLap, thoiGian);

        model.setRowCount(0);
        for (Object[] rowData : ketQuaTimKiem) {
            model.addRow(rowData);
        }

        if (ketQuaTimKiem.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy đơn đặt bàn nào khớp với tiêu chí.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Tìm thấy " + ketQuaTimKiem.size() + " đơn đặt bàn.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    class RoundedButton extends JButton {
        private Color backgroundColor;
        private Color hoverColor;

        public RoundedButton(String text, Color bgColor) {
            super(text);
            this.backgroundColor = bgColor;
            this.hoverColor = bgColor.darker();
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(new Font("Segoe UI", Font.BOLD, 16));
            setPreferredSize(new Dimension(120, 30));
            setBackground(bgColor);
            setForeground(bgColor.equals(Color.WHITE) ? Color.BLACK : Color.WHITE);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBackground(hoverColor);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(backgroundColor);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        public void updateUI() {
            setUI(new javax.swing.plaf.basic.BasicButtonUI());
        }
    }
	

}