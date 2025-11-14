package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
    // ✅ Thêm JComboBox vào thuộc tính lớp
    private JComboBox<String> comboBoxThoiGian;
	private RoundedButton btnThanhToan;
	private DonDatBan_DAO ddbDAO; 

    public DonDatBan_GUI() {
        setLayout(new BorderLayout());

        // ======= HEADER (Giữ nguyên) =======
        JPanel pNorth = new JPanel();
        pNorth.setBackground(new Color(0,153,76));
        JLabel lbltitle = new JLabel("QUẢN LÝ ĐƠN ĐẶT BÀN", SwingConstants.CENTER);
        lbltitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lbltitle.setForeground(Color.WHITE);
        pNorth.add(lbltitle);
        add(pNorth, BorderLayout.NORTH);

        // ======= CENTER (Giữ nguyên) =======
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
        thoiGian.setPreferredSize(new Dimension(130,30));

        String [] ngay = {"Tất cả", "Hôm nay", "Hôm qua"}; // ✅ Thêm 'Tất cả' để hiển thị không lọc
        // ✅ Khởi tạo thuộc tính lớp
        comboBoxThoiGian = new JComboBox<>(ngay);
        comboBoxThoiGian.setSelectedIndex(0);
        comboBoxThoiGian.setPreferredSize(new Dimension(150,30));

        JLabel ngayLap = new JLabel("Ngày lập:");
        ngayLap.setPreferredSize(new Dimension(130,30));
        ngayLap.setFont(new Font("Segoe UI", Font.BOLD, 16));

        // ✅ Khởi tạo thuộc tính lớp
        dateChooser = new JDateChooser(); 
        dateChooser.setDate(null); 
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setPreferredSize(new Dimension(130, 25));
        dateChooser.setMaximumSize(new Dimension(130, 25));

        pnA.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        pnA.add(tenKH); pnA.add(txtTenKH);
        pnA.add(Box.createHorizontalStrut(30));
        pnA.add(sdt); pnA.add(txtSDT);
        pnA.add(Box.createHorizontalStrut(30));
        pnA.add(thoiGian); pnA.add(comboBoxThoiGian); // ✅ Sử dụng thuộc tính lớp
        pnA.add(Box.createHorizontalStrut(30));
        pnA.add(ngayLap); pnA.add(dateChooser);       // ✅ Sử dụng thuộc tính lớp
        bx1.add(pnA);

        // --- Hàng 2 (Giữ nguyên) ---
        pnB = new Box(BoxLayout.X_AXIS);
        JLabel maHD = new JLabel("Mã đơn đặt bàn:");
        maHD.setFont(new Font("Segoe UI", Font.BOLD, 16));
        maHD.setPreferredSize(new Dimension(150,30));
        txtMaHD = new JTextField();
        btnTimHD = new RoundedButton("Tìm kiếm", new Color(128,128,128));
        btnLamMoi = new RoundedButton("Làm mới", new Color(128,128,128));
        btnXem = new RoundedButton("Xem", new Color(153,0,0));
        btnThanhToan = new RoundedButton("Thanh toán", new Color(0,102,0));

        Dimension btnSize = new Dimension(350,30);
        pnB.setBorder(BorderFactory.createEmptyBorder(10,10,20,10));
        pnB.add(maHD); pnB.add(txtMaHD);
        pnB.add(Box.createHorizontalStrut(30));
        pnB.add(btnTimHD); btnTimHD.setMaximumSize(btnSize);
        pnB.add(Box.createHorizontalStrut(30));
        pnB.add(btnLamMoi); btnLamMoi.setMaximumSize(btnSize);
        pnB.add(Box.createHorizontalStrut(30));
        pnB.add(btnXem); btnXem.setMaximumSize(btnSize);
        pnB.add(Box.createHorizontalStrut(30));
        pnB.add(btnThanhToan); btnThanhToan.setMaximumSize(btnSize);
        bx1.add(pnB);

        // ======= BẢNG HÓA ĐƠN (Giữ nguyên) =======
        model = new DefaultTableModel(new Object[]{
            "Mã đơn đặt bàn","Tên khách hàng","Số lượng khách", "Ngày lập",
            "Nhân viên", "Trạng thái"
        }, 0) {
        	/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false; // **QUAN TRỌNG: Trả về false để vô hiệu hóa chỉnh sửa**
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

        // ======= NÚT HÀNH ĐỘNG (Cập nhật) =======
      
        // ======= KHỞI TẠO (Giữ nguyên) =======
     
        setVisible(true);
        btnTimHD.addActionListener(this);
        btnLamMoi.addActionListener(this);
        btnXem.addActionListener(this);
        btnThanhToan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // ⭐ FIX: Gọi hàm xử lý Thanh toán để mở ChiTietDonDatBan_GUI
                handleThanhToanTuBang(); 
            }
        });
        // Load dữ liệu ban đầu
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
            handleXemChiTiet();
        }
    }
   

    // ✅ Phương thức xử lý nút Làm mới
    private void handleLamMoi() {
        txtMaHD.setText("");
        txtSDT.setText("");
        txtTenKH.setText("");
        dateChooser.setDate(null);
        comboBoxThoiGian.setSelectedIndex(0);
        loadDonDatBanToTable(); // Tải lại toàn bộ danh sách
    }

    // ✅ Phương thức xử lý nút Xem (cần logic chi tiết hơn)
    private void handleXemChiTiet() {
        int row = table.getSelectedRow();
        if (row != -1) {
            String maDatBan = (String) model.getValueAt(row, 0);
            JOptionPane.showMessageDialog(this, "Xem chi tiết cho Đơn Đặt Bàn: " + maDatBan + "\n(Hãy mở cửa sổ chi tiết tại đây)", "Xem Chi Tiết", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn đặt bàn để xem chi tiết.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void loadDonDatBanToTable() {
        model.setRowCount(0); 
        
        // Gọi DAO để lấy danh sách đơn đặt bàn chi tiết (đã JOIN)
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

        // Chuyển java.util.Date sang java.sql.Date để truyền vào DAO
        java.sql.Date sqlNgayLap = null;
        if (utilNgayLap != null) {
            sqlNgayLap = new java.sql.Date(utilNgayLap.getTime());
        }

        // Kiểm tra nếu tất cả các trường rỗng, thì load lại tất cả
        if (maDDB.isEmpty() && tenKH.isEmpty() && sdt.isEmpty() && utilNgayLap == null && thoiGian.equals("Tất cả")) {
            loadDonDatBanToTable();
            return;
        }
        
        // 1. Gọi DAO để tìm kiếm
        List<Object[]> ketQuaTimKiem = DonDatBanDAO.findDonDatBan(maDDB, tenKH, sdt, sqlNgayLap, thoiGian);
        
        // 2. Cập nhật bảng
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
 // Trong DonDatBan_GUI.java

 // Trong DonDatBan_GUI.java

    private void handleThanhToanTuBang() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 đơn đặt bàn để thanh toán.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String maDDB = (String) model.getValueAt(row, 0);
        String trangThai = (String) model.getValueAt(row, 5); // Giả định trạng thái ở cột 5
        
        if (trangThai.equals("Đã thanh toán")) {
            JOptionPane.showMessageDialog(this, "Đơn đặt bàn này đã được thanh toán.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 1. Lấy đối tượng DonDatBan
        // DonDatBanDAO là ddbDAO trong các hướng dẫn trước
        DonDatBan ddb = DonDatBanDAO.getDonDatBanByMa(maDDB); 
        
        // 2. Kiểm tra DDB
        if (ddb == null || ddb.getMaDatBan() == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin đơn đặt bàn hoặc bàn liên quan.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 3. Lấy thông tin KH và Bàn
        String tenKH = "Khách lẻ";
        String sdt = "";
        Ban banHienTai = null;

        try {
            // Cần KhachHang_DAO đã khai báo ở đầu class (giả định tồn tại)
            KhachHang_DAO khachHangDAO = new KhachHang_DAO();
            if (ddb.getMaKH() != null) {
                // Giả định getKhachHangByMa tồn tại
                KhachHang kh = khachHangDAO.getKhachHangByMa(ddb.getMaKH().getMaKH()); 
                if (kh != null) {
                    tenKH = kh.getTenKH();
                    sdt = kh.getsDT();
                }
            }
            
            // Cần Ban_DAO đã khai báo ở đầu class (giả định tồn tại)
            Ban_DAO banDAO = new Ban_DAO();
            String maBanDDB = ddb.getMaDatBan();
            // ⭐ Giả định DonDatBan có phương thức getMaBan() trả về đối tượng Ban có getMaBan()
            banHienTai = banDAO.getBanByMaDDB(maBanDDB);
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy thông tin Khách hàng hoặc Bàn: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Lỗi truy vấn dữ liệu đơn đặt bàn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (banHienTai == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin bàn liên quan.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 4. Lấy danh sách món ăn (Giả định DonDatBanDAO có getMonAnByMaDDB)
        DefaultTableModel modelMonAn = DonDatBanDAO.getMonAnByMaDDB(maDDB); 
        
        if (modelMonAn == null) {
             // Tạo model rỗng nếu không có món ăn (ví dụ: chỉ thanh toán tiền cọc)
             modelMonAn = new DefaultTableModel(new String[]{"Tên món", "Mã món", "SL", "Đơn giá", "Thành tiền"}, 0);
        }

        // 5. Mở ChiTietDonDatBan_GUI (THAY THẾ GoiMon_GUI)
        ChiTietDonDatBan_GUI chiTiet = new ChiTietDonDatBan_GUI(
            (JFrame) SwingUtilities.getWindowAncestor(this), 
            modelMonAn, 
            false, // isChuyenKhoan = false (Tiền mặt mặc định)
            banHienTai,
            tenKH,
            sdt // Truyền tenKH và sdt
        );
        // ⭐ Truyền mã đơn đặt bàn hiện tại để nó không tự sinh mã mới khi thanh toán
        chiTiet.setMaDDBHienTai(maDDB); 
        chiTiet.setVisible(true);
        
        // Cập nhật lại giao diện sau khi dialog thanh toán đóng
        loadDonDatBanToTable(); // ⭐ FIX: Gọi hàm tải lại dữ liệu bảng DDB
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