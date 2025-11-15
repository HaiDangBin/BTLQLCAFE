package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.ImageIcon;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import DAO.Ban_DAO;
import DAO.DonDatBan_DAO;
import DAO.KhachHang_DAO;
import Entity.Ban;
import Entity.DonDatBan;
import Entity.KhachHang;
import Entity.NhanVien;
import connectDB.DBconnection;

public class DatBan_GUI extends JPanel{
	private JLabel timeLabel;
	private JLabel dateLabel;
	private Timer clockTimer;
	private final Color frameEdgeColor = new Color(255, 255, 255);
	private final Color secondaryAccentColor = new Color(0, 0, 0); 
	private final Color primaryAccentColor = new Color(192, 57, 43);
	private JComboBox cmbSoNguoi; 
	private JTextField txtMaBan, txtMaDatBan;
	private Ban_DAO ban_DAO; 
	private JPanel pDanhSachBan;
	private Ban selectedBan = null; 
    private BanAnPanel selectedPanel = null;
    private RoundedButton btnDatBan;
    protected DonDatBan_DAO donDatBanDAO;
    protected KhachHang_DAO khachHangDAO;
    private RoundedButton btnThemBan;
	public DatBan_GUI() {
	    DBconnection.getInstance().connect();
	    ban_DAO = new Ban_DAO();
	    donDatBanDAO = new DonDatBan_DAO();
        khachHangDAO = new KhachHang_DAO();
	    
	    
	    setBackground(Color.WHITE); 
	    
	    // ===== Setup North Panel =====
	    setLayout(new BorderLayout());
	    JPanel pNorth = new JPanel();
	    // Giữ pNorth màu xanh đậm để tiêu đề nổi bật
	    pNorth.setBackground(new Color(0, 51, 51)); 
	    JLabel lbltitle = new JLabel("DANH SÁCH BÀN", SwingConstants.CENTER);
	    lbltitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
	    lbltitle.setForeground(Color.WHITE);
	    pNorth.add(lbltitle);
	    add(pNorth, BorderLayout.NORTH);
	    
	    // ===== Setup West Panel (Chứa đồng hồ) =====
	    JPanel pWest = new JPanel();
	    pWest.setLayout(new BoxLayout(pWest, BoxLayout.Y_AXIS));
	    pWest.setPreferredSize(new Dimension(250, getHeight())); 
	    // Đặt màu nền của pWest thành trắng
	    
	    JPanel bx = new JPanel();
	    bx.setLayout(new BoxLayout(bx, BoxLayout.Y_AXIS));
	    // Thêm đồng hồ vào bx
	    JPanel clock = createClockPanel(); 
	    
	    bx.add(clock);
	     

	    pWest.add(bx, BorderLayout.NORTH); 
	    
	    JPanel bxx = new JPanel();
	    bxx.setLayout(new BoxLayout(bxx, BoxLayout.Y_AXIS));
	    bxx.setBackground(Color.WHITE);
	    // Thêm padding cho bxx
	    bxx.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	    bxx.setAlignmentX(Component.CENTER_ALIGNMENT); // Căn giữa bxx

	    RoundedButton btnLocBanTrong = new RoundedButton("Bàn trống", new Color(0,153,76));
	    RoundedButton btnLocBanDatTruoc = new RoundedButton("Bàn đã đặt", new Color(0,153,76));
	    RoundedButton btnLocBanDangDung = new RoundedButton("Bàn có khách", new Color(0,153,76));
	    
	    btnDatBan = new RoundedButton("Đặt bàn", new Color(0,102,102));
	    RoundedButton btnHuyBan = new RoundedButton("Hủy bàn đặt", new Color(102,0,0));
	    RoundedButton btnXemCT = new RoundedButton("Xem chi tiết", new Color(102,102,0));
	    RoundedButton btnGoiMon = new RoundedButton("Gọi món", new Color(153,75,0));
	    RoundedButton btnThanhToan = new RoundedButton("Thanh toán", new Color(0,76,153));
	    btnThemBan = new RoundedButton("Thêm bàn", new Color(0,153,76));
	    

	    // Căn giữa các nút và thêm khoảng cách giữa chúng
	    btnLocBanTrong.setAlignmentX(Component.CENTER_ALIGNMENT);
	    btnLocBanTrong.setMaximumSize(new Dimension(220,40));
	    btnLocBanDatTruoc.setAlignmentX(Component.CENTER_ALIGNMENT);
	    btnLocBanDatTruoc.setMaximumSize(new Dimension(220,40));
	    btnLocBanDangDung.setAlignmentX(Component.CENTER_ALIGNMENT);
	    btnLocBanDangDung.setMaximumSize(new Dimension(220,40));
	    btnDatBan.setAlignmentX(Component.CENTER_ALIGNMENT);
	    btnDatBan.setMaximumSize(new Dimension(220,40));
	    btnDatBan.setEnabled(false);
	    btnHuyBan.setAlignmentX(Component.CENTER_ALIGNMENT);
	    btnHuyBan.setMaximumSize(new Dimension(220,40));
	    btnXemCT.setAlignmentX(Component.CENTER_ALIGNMENT);
	    btnXemCT.setMaximumSize(new Dimension(220,40));
	    btnGoiMon.setAlignmentX(Component.CENTER_ALIGNMENT);
	    btnGoiMon.setMaximumSize(new Dimension(220,40));
	    btnThanhToan.setAlignmentX(Component.CENTER_ALIGNMENT);
	    btnThanhToan.setMaximumSize(new Dimension(220,40));
	    btnThemBan.setAlignmentX(Component.CENTER_ALIGNMENT);
	    btnThemBan.setMaximumSize(new Dimension(220,40));
	    
	    bxx.add(btnLocBanTrong);
	    bxx.add(Box.createVerticalStrut(20)); // Khoảng cách giữa các nút
	    bxx.add(btnLocBanDatTruoc);
	    bxx.add(Box.createVerticalStrut(20));
	    bxx.add(btnLocBanDangDung);
	    bxx.add(Box.createVerticalStrut(20));
	    bxx.add(btnDatBan);
	    bxx.add(Box.createVerticalStrut(20));
	    bxx.add(btnHuyBan);
	    bxx.add(Box.createVerticalStrut(20));
	    bxx.add(btnXemCT);
	    bxx.add(Box.createVerticalStrut(20));
	    bxx.add(btnGoiMon);
	    bxx.add(Box.createVerticalStrut(20));
	    bxx.add(btnThanhToan);
	    bxx.add(Box.createVerticalStrut(20));
	    bxx.add(btnThemBan);

	    pWest.add(bx); 
	    pWest.add(Box.createVerticalStrut(10)); 
	    pWest.add(bxx); 
	    
	    
	    pWest.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
	    add(pWest, BorderLayout.WEST);
	    
	    JPanel pCen = new JPanel();
	    pCen.setLayout(new BorderLayout());
	    
	    JPanel cenbx = new JPanel();
	    cenbx.setLayout(new BoxLayout(cenbx, BoxLayout.Y_AXIS));
	    cenbx.setBackground(Color.white);
	    cenbx.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	    JPanel pnA = new JPanel();
	    pnA.setLayout(new BoxLayout(pnA, BoxLayout.X_AXIS));
	    JLabel lblMaBan = new JLabel("Mã bàn:");
	    lblMaBan.setFont(new Font("Segoe UI", Font.BOLD, 18));
	    txtMaBan = new JTextField();
	    
	    lblMaBan.setPreferredSize(new Dimension(120, 30));
	    lblMaBan.setHorizontalAlignment(SwingConstants.LEFT); 

	    txtMaBan = new JTextField(); 
	    txtMaBan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30)); 
	    
	    pnA.setBackground(Color.white);
	    pnA.add(lblMaBan);
	    pnA.add(Box.createHorizontalStrut(5));
	    pnA.add(txtMaBan);
	    pnA.add(Box.createHorizontalGlue());
	    
	    JPanel pnB = new JPanel();
	    pnB.setLayout(new BoxLayout(pnB, BoxLayout.X_AXIS));
	    pnB.setBackground(Color.WHITE);
	    
	    JLabel lblMaDatban = new JLabel("Mã đặt bàn:");
	    lblMaDatban.setFont(new Font("Segoe UI", Font.BOLD, 18));
	    txtMaDatBan = new JTextField();
	    lblMaDatban.setPreferredSize(new Dimension(120, 30));
	    lblMaDatban.setHorizontalAlignment(SwingConstants.LEFT); 

	    txtMaDatBan = new JTextField();
	    txtMaDatBan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30)); 

	    pnB.add(lblMaDatban);
	    pnB.add(Box.createHorizontalStrut(5)); 
	    pnB.add(txtMaDatBan);
	    pnB.add(Box.createHorizontalGlue());
        
        JPanel pnC = new JPanel();
        pnC.setLayout(new BoxLayout(pnC, BoxLayout.X_AXIS));
        
        RoundedButton btnTim = new RoundedButton("Tìm kiếm", new Color(0,102,0));
        btnTim.setPreferredSize(new Dimension(50,30));
        RoundedButton btnLamMoi = new RoundedButton("Làm mới", new Color(20,20,20));
        btnLamMoi.setPreferredSize(new Dimension(50,30));
        JLabel lblsoNguoi = new JLabel("Số người:");
        lblsoNguoi.setPreferredSize(new Dimension(120, 20));
        lblsoNguoi.setHorizontalAlignment(SwingConstants.LEFT);
        lblsoNguoi.setFont(new Font("Segoe UI", Font.BOLD, 18));
	    String[] soNguoi = {"Tất cả","2 Người", "4 Người", "6 Người", "8 Người", "10 Người"};
	    cmbSoNguoi = new JComboBox<>(soNguoi);
	    cmbSoNguoi.setSelectedIndex(0);
        cmbSoNguoi.setPreferredSize(new Dimension(80, 30));
        cmbSoNguoi.setMaximumSize(new Dimension(80, 30));
        lblsoNguoi.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        pnC.add(lblsoNguoi);
        pnC.add(Box.createHorizontalStrut(40));
        pnC.add(cmbSoNguoi);
        pnC.add(Box.createHorizontalStrut(20));
        pnC.add(btnTim);
        pnC.add(Box.createHorizontalStrut(20));
        pnC.add(btnLamMoi);
        pnC.setBackground(Color.white);
        
        
        pnA.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnB.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnC.setAlignmentX(Component.LEFT_ALIGNMENT);
	    cenbx.add(pnA);
	    cenbx.add(Box.createVerticalStrut(10));
	    cenbx.add(pnB);
	    cenbx.add(Box.createVerticalStrut(10));
	    cenbx.add(pnC);
	    
	    cenbx.add(Box.createVerticalStrut(20));
	    pDanhSachBan = new JPanel(); 
		pDanhSachBan.setLayout(new GridLayout(0, 6, 15, 15)); 
		pDanhSachBan.setBackground(Color.WHITE);
		pDanhSachBan.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); 
		loadBanVaoGUI((String)null, "Tất cả", 0);
	    pCen.add(cenbx, BorderLayout.NORTH);
	    pCen.add(pDanhSachBan, BorderLayout.CENTER);
	    pCen.setBorder(BorderFactory.createEmptyBorder(10,0,10,10));
	    add(pCen, BorderLayout.CENTER);
	    
	    btnDatBan.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            if (selectedBan == null || !"Trống".equals(selectedBan.getTrangThai().trim())) {
	                JOptionPane.showMessageDialog(DatBan_GUI.this, "Vui lòng chọn bàn trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
	                return;
	            }

	            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(DatBan_GUI.this);

	            // MỞ DatBanMoi_Dialog
	            DatBanMoi_Dialog dialog = new DatBanMoi_Dialog(parentFrame, selectedBan.getMaBan(), selectedBan.getSucChua());
	            dialog.setVisible(true);
	        }
	    });
	    btnTim.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            String maDatBan = txtMaDatBan.getText().trim();
	            if (!maDatBan.isEmpty()) {
	                handleTimKiemBanTheoMaDDB(maDatBan);
	                return;
	            }

	            String maBan = txtMaBan.getText().trim();
	            
	            // Xử lý Số người (SucChua)
	            int sucChua = 0;
	            String selectedItem = (String) cmbSoNguoi.getSelectedItem();
	            
	            if (selectedItem != null && !selectedItem.equalsIgnoreCase("Tất cả")) {
	                try {
	                    String soNguoiStr = selectedItem.split(" ")[0]; 
	                    sucChua = Integer.parseInt(soNguoiStr); 
	                } catch (NumberFormatException ex) {
	                    sucChua = 0; 
	                }
	            }

	            String trangThaiLoc = "Tất cả"; 
	            if (maBan.isEmpty()) maBan = null; 
	            
	            loadBanVaoGUI(maBan, trangThaiLoc, sucChua);

	            programmaticallySelectBan(null); 
	            txtMaBan.setText(maBan != null ? maBan : "");
	        }
	    });
	    btnThemBan.addActionListener(new ActionListener() {
	        
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor((Component) e.getSource()); // Lấy Frame chứa nút
	            
	            // ⭐ SỬA LỖI: Sử dụng "TênClass.this" để tham chiếu đến DatBan_GUI
	            ThemBan_Dialog dialog = new ThemBan_Dialog(parentFrame, DatBan_GUI.this); 
	            
	            dialog.setVisible(true);
	        }
	    });
	    
	    btnLocBanTrong.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            loadBanVaoGUI(null, "Trống", 0);
	        }
	    });
	    btnLocBanDangDung.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            loadBanVaoGUI((String)null, "Có khách", 0);
	        }
	    });
	    btnLocBanDatTruoc.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            loadBanVaoGUI((String)null, "Đặt trước", 0);
	        }
	    });
	    btnGoiMon.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            handleGoiMonAction();
	        }
	    });
	    
	    
	    // Bắt đầu đồng hồ
	    startClock();	}
	private JPanel createClockPanel() {
		JPanel clockPanel = new JPanel();
		clockPanel.setLayout(new BoxLayout(clockPanel, BoxLayout.Y_AXIS));
		clockPanel.setBackground(frameEdgeColor); 
		clockPanel.setBorder(BorderFactory.createEmptyBorder(20, 34, 20, 34));
		
		timeLabel = new JLabel("00:00:00", SwingConstants.CENTER);
		timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 40)); 
		timeLabel.setForeground(secondaryAccentColor); 
		timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		dateLabel = new JLabel("Ngày, DD/MM/YYYY", SwingConstants.CENTER);
		dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		dateLabel.setForeground(primaryAccentColor); 
		dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		clockPanel.add(timeLabel);
		clockPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		clockPanel.add(dateLabel);
		clockPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		
		return clockPanel;
	}
	private void startClock() {
		clockTimer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateClock();
			}
		});
		clockTimer.start();
		updateClock();
	}
	private void updateClock() {
		LocalDateTime now = LocalDateTime.now();
		
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		timeLabel.setText(now.format(timeFormatter));
		
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy");
		String dateText = now.format(dateFormatter);
		
		dateText = dateText.replace("Monday", "Thứ Hai")
						   .replace("Tuesday", "Thứ Ba")
						   .replace("Wednesday", "Thứ Tư")
						   .replace("Thursday", "Thứ Năm")
						   .replace("Friday", "Thứ Sáu")
						   .replace("Saturday", "Thứ Bảy")
						   .replace("Sunday", "Chủ Nhật");
						   
		dateLabel.setText(dateText);
	}
	
	private void handleGoiMonAction() {
	    // 1. Kiểm tra xem bàn đã được chọn chưa
	    if (selectedBan == null) {
	        JOptionPane.showMessageDialog(this, "Vui lòng chọn một bàn để gọi món.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
	        return;
	    }
	    
	    String maBan = selectedBan.getMaBan();
	    
	    // 2. TÌM ĐƠN ĐẶT BÀN ĐANG HOẠT ĐỘNG (Đã đặt hoặc Đang phục vụ)
	    // ⭐ LƯU Ý: Phải đảm bảo DonDatBan_DAO có phương thức này và TẢI thông tin KhachHang
	    // (Giả sử bạn đã triển khai phương thức getLatestActiveDonDatBanByMaBan(String maBan))
	    DonDatBan ddbHienTai = donDatBanDAO.getLatestActiveDonDatBanByMaBan(maBan);

	    String tenKH = "Khách lẻ"; // Giá trị mặc định nếu bàn trống hoặc không có đơn đặt
	    String sdtKH = ""; // Giá trị mặc định

	    // 3. Lấy thông tin khách hàng từ Đơn đặt bàn nếu tìm thấy
	    if (ddbHienTai != null && ddbHienTai.getMaKH() != null) {
	        KhachHang khachHang = ddbHienTai.getMaKH();
	        tenKH = khachHang.getTenKH(); // Lấy Tên KH từ Đơn Đặt Bàn
	        sdtKH = khachHang.getsDT();   // Lấy SĐT KH từ Đơn Đặt Bàn
	    }
	    
	    // ⭐ 4. MỞ GIAO DIỆN GỌI MÓN
	    try {
	        // Lấy JFrame cha để truyền vào constructor của GoiMon_GUI
	        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
	        
	        GoiMon_GUI goiMonUI = new GoiMon_GUI(
	            parentFrame, 
	            maBan, 
	            tenKH, 
	            sdtKH
	        );
	        goiMonUI.setVisible(true);
	        
	        // Sau khi gọi món, có thể cần cập nhật lại trạng thái bàn (Ví dụ: từ "Đặt trước" -> "Đang phục vụ")
	        loadBanVaoGUI(); 

	    } catch (Exception ex) {
	        ex.printStackTrace();
	        JOptionPane.showMessageDialog(this, "Lỗi khi mở giao diện Gọi món: " + ex.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
	    }
	}
	
	public void moHoaDonDatCoc(Ban banDuocChon, String tenKH, String sdt) {
	    // 1. Kiểm tra trạng thái
	    if (!banDuocChon.getTrangThai().equalsIgnoreCase("Trống")) {
	         JOptionPane.showMessageDialog(this, "Bàn đang " + banDuocChon.getTrangThai() + ", không thể đặt cọc.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
	        return;
	    }

	    // 2. Chọn phương thức thanh toán tiền đặt cọc
	    String[] options = {"Tiền mặt", "Chuyển khoản"};
	    int choice = JOptionPane.showOptionDialog(this, 
	        "Chọn phương thức thanh toán tiền đặt cọc:", 
	        "Thanh toán Đặt Cọc", 
	        JOptionPane.YES_NO_OPTION, 
	        JOptionPane.QUESTION_MESSAGE, 
	        null, 
	        options, 
	        options[0]);

	    if (choice == JOptionPane.CLOSED_OPTION) {
	        return; 
	    }
	    
	    boolean isChuyenKhoan = (choice == 1);

	    // 3. Tạo EMPTY DefaultTableModel (model rỗng báo hiệu chỉ thanh toán tiền cọc)
	    String[] cols = {"Tên món", "SL", "Đơn giá", "Thành tiền"};
	    DefaultTableModel emptyModel = new DefaultTableModel(cols, 0);

	    // 4. Mở ChiTietDonDatBan_GUI
	    ChiTietDonDatBan_GUI chiTiet = new ChiTietDonDatBan_GUI(
	        (JFrame) SwingUtilities.getWindowAncestor(this), // Parent Frame
	        emptyModel, // Model rỗng
	        isChuyenKhoan, 
	        banDuocChon, 
	        tenKH, 
	        sdt
	    );
	    chiTiet.setVisible(true);
	    
	    // 5. Cập nhật lại danh sách bàn sau khi dialog thanh toán đóng
	    loadBanVaoGUI();; // Cần đảm bảo hàm này tồn tại để refresh trạng thái bàn
	}
	
	
	
	class DatBanMoi_Dialog extends JDialog {
		private DatBan_GUI parentGUI;
	    private JTextField txtTenKhachHang, txtSoDienThoai;
	    private JLabel lblSoNguoi, lblNgayGio, lblMaBanHienTai; 
	    private RoundedButton btnXacNhan, btnHuy;
	    private String maBanDuocChon;
	    private int sucChuaBan; 
	    private JLabel lblMaNV; 
	    private JComboBox<String> cmbTrangThai;
		private RoundedButton btnGoiSP;
		private DatBan_GUI parentPanel;
		private Ban selectedBan;
	    public DatBanMoi_Dialog(JFrame parent, String maBan, int sucChua) {
	    	super(parent, "Đặt Bàn Mới (" + maBan + ")", true);
	        // Lưu thông tin bàn được chọn
	        this.maBanDuocChon = maBan;
	        this.sucChuaBan = sucChua;
	        this.parentPanel = parentPanel;
	        // Setup giao diện
	        setupUI();
	        
	        // Thiết lập sự kiện
	        setupEvents();
	        
	        // Cập nhật giá trị hiển thị ban đầu
	        lblMaBanHienTai.setText(maBan);
	        lblSoNguoi.setText(String.valueOf(sucChua) + " người");
	    }

	    private void setupUI() {
	        setLayout(new BorderLayout(10, 10));
	        setSize(500, 450); 
	        setLocationRelativeTo(getParent());
	        setResizable(false);
	        
	        
	        JPanel pMain = new JPanel();
	        pMain.setLayout(new BoxLayout(pMain, BoxLayout.Y_AXIS));
	        pMain.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
	        pMain.setBackground(Color.WHITE);
	        
	        // Các Font và Dimension
	        Dimension sizeField = new Dimension(200, 35);
	        Font fontInput = new Font("Segoe UI", Font.PLAIN, 16);
	        Font fontLabel = new Font("Segoe UI", Font.BOLD, 16);
	        Font fontValue = new Font("Segoe UI", Font.BOLD, 16);

	        
	        lblMaBanHienTai = new JLabel();
	        lblMaBanHienTai.setFont(fontValue);
	        lblMaBanHienTai.setForeground(new Color(0, 102, 102));
	        pMain.add(createFieldPanel("Mã Bàn:", lblMaBanHienTai, fontLabel));
	        pMain.add(Box.createVerticalStrut(15));
	        
	     
	        lblMaNV = new JLabel("NV01"); 
	        lblMaNV.setFont(fontValue);
	        lblMaNV.setForeground(new Color(153, 51, 0));
	        pMain.add(createFieldPanel("Nhân viên:", lblMaNV, fontLabel));
	        pMain.add(Box.createVerticalStrut(15));
	        
	        
	        lblSoNguoi = new JLabel();
	        lblSoNguoi.setFont(fontValue);
	        pMain.add(createFieldPanel("Sức chứa bàn:", lblSoNguoi, fontLabel));
	        pMain.add(Box.createVerticalStrut(15));
	        
	        String[] trangThaiOptions = {"Đặt trước", "Đã đến"};
	        cmbTrangThai = new JComboBox<>(trangThaiOptions);
	        cmbTrangThai.setFont(fontInput);
	        cmbTrangThai.setSelectedIndex(0); 
	        pMain.add(createFieldPanel("Trạng thái:", cmbTrangThai, fontLabel));
	        pMain.add(Box.createVerticalStrut(15));

	        
	        lblNgayGio = new JLabel();
	        lblNgayGio.setFont(fontValue);
	        lblNgayGio.setForeground(new Color(192, 57, 43)); 
	        updateNgayGio(); 
	        pMain.add(createFieldPanel("Thời gian đặt:", lblNgayGio, fontLabel));
	        pMain.add(Box.createVerticalStrut(15));

	        
	        txtTenKhachHang = new JTextField(20);
	        txtTenKhachHang.setFont(fontInput);
	        pMain.add(createFieldPanel("Tên Khách hàng:", txtTenKhachHang, fontLabel));
	        pMain.add(Box.createVerticalStrut(15));

	       
	        txtSoDienThoai = new JTextField(20);
	        txtSoDienThoai.setFont(fontInput);
	        pMain.add(createFieldPanel("Số điện thoại:", txtSoDienThoai, fontLabel));
	        pMain.add(Box.createVerticalStrut(30)); 

	        
	        JPanel pButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
	        pButtons.setBackground(Color.WHITE);
	        
	        btnXacNhan = new RoundedButton("Xác nhận Đặt", new Color(0, 102, 0));
	        btnHuy = new RoundedButton("Hủy bỏ", new Color(102, 0, 0));
	        
	        Font btnFont = new Font("Segoe UI", Font.BOLD, 13);
	        btnXacNhan.setFont(btnFont);
	        btnHuy.setFont(btnFont);
	        
	        Dimension buttonSize = new Dimension(160, 45);
	        btnXacNhan.setPreferredSize(buttonSize);
	        
	        btnHuy.setPreferredSize(buttonSize);

	        pButtons.add(btnXacNhan);
	        
	        pButtons.add(btnHuy);

	        add(pMain, BorderLayout.CENTER);
	        add(pButtons, BorderLayout.SOUTH);
	    }
	    private boolean validateInputs() {
	        String tenKH = txtTenKhachHang.getText().trim();
	        String sdt = txtSoDienThoai.getText().trim();

	        if (tenKH.isEmpty()) {
	            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
	            txtTenKhachHang.requestFocus();
	            return false;
	        }

	        if (!sdt.isEmpty() && !sdt.matches("\\d{10}")) {
	            JOptionPane.showMessageDialog(this, "Số điện thoại phải có 10 chữ số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
	            txtSoDienThoai.requestFocus();
	            return false;
	        }

	        return true;
	    }
	    
	    private void setupEvents() {
	        // Nút Hủy
	        btnHuy.addActionListener(e -> dispose()); // Đóng Dialog

//	        // Nút Xác nhận
	        btnXacNhan.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {

	                String tenKH = txtTenKhachHang.getText().trim();
	                String sdt = txtSoDienThoai.getText().trim();

	                if (tenKH.isEmpty()) {
	                    JOptionPane.showMessageDialog(null, "Vui lòng nhập tên khách hàng!");
	                    return;
	                }

	                if (sdt.isEmpty() || !sdt.matches("0\\d{9}")) {
	                    JOptionPane.showMessageDialog(null, "Số điện thoại không hợp lệ (10 số, bắt đầu bằng 0)");
	                    return;
	                }

	                // ⭐ LẤY BÀN TỪ DATABASE — KHÔNG DÙNG selectedBan (sửa lỗi)
	                Ban banDuocChon = ban_DAO.layBanTheoMa(maBanDuocChon);

	                if (banDuocChon == null) {
	                    JOptionPane.showMessageDialog(null, "Không tìm thấy bàn: " + maBanDuocChon);
	                    return;
	                }

	                // ⭐ KIỂM TRA BÀN CÓ TRỐNG KHÔNG
	                if (!banDuocChon.getTrangThai().equalsIgnoreCase("Trống")) {
	                    JOptionPane.showMessageDialog(null, 
	                        "Bàn đang " + banDuocChon.getTrangThai() + ", không thể đặt!");
	                    return;
	                }

	                // ⭐ MỞ CHI TIẾT ĐẶT BÀN (Giao diện thanh toán tiền cọc)
	                ChiTietDonDatBan_GUI dialog = new ChiTietDonDatBan_GUI(
	                    (JFrame) getParent(), 
	                    null,          
	                    false,         
	                    banDuocChon,  
	                    tenKH, 
	                    sdt
	                );

	                dialog.setVisible(true);

	                // Đóng dialog đặt mới sau khi mở màn hình thanh toán
	                dispose();
	            }
	        });

	    }
	    private void handleXacNhanDat() {
	        String tenKH = txtTenKhachHang.getText().trim();
	        String sdt = txtSoDienThoai.getText().trim();

	        // 1. Validate dữ liệu
	        if (tenKH.isEmpty() || sdt.isEmpty()) {
	            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Tên và SĐT khách hàng.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
	            return;
	        }

	        if (!sdt.matches("0\\d{9}")) {
	            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ (phải là 10 số, bắt đầu bằng 0).", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
	            return;
	        }

	        try {
	            // LẤY đối tượng Ban từ DB theo mã bàn đã truyền vào constructor
	            Ban banDuocChon = ban_DAO.layBanTheoMa(this.maBanDuocChon);
	            if (banDuocChon == null) {
	                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin bàn: " + this.maBanDuocChon, "Lỗi", JOptionPane.ERROR_MESSAGE);
	                return;
	            }

	            // MỞ ChiTietDonDatBan_GUI để nhân viên thanh toán đặt cọc ngay
	            ChiTietDonDatBan_GUI chiTiet = new ChiTietDonDatBan_GUI(
	                (JFrame) this.getParent(),
	                new javax.swing.table.DefaultTableModel(new String[] {"Tên món","SL","Đơn giá","Thành tiền"}, 0), // model rỗng => thanh toán cọc
	                false,   // isChuyenKhoan (mặc định false; nếu bạn muốn lấy lựa chọn từ form thì truyền giá trị đó)
	                banDuocChon,
	                tenKH,
	                sdt
	            );
	            chiTiet.setVisible(true);

	            // Sau khi đóng dialog chi tiết, có thể refresh giao diện chính (nếu cần)
	            // Ví dụ: ((DatBan_GUI)SwingUtilities.getWindowAncestor(this)).loadBanVaoGUI(); 
	            // nhưng chỉ gọi nếu bạn truyền/biết parent DatBan_GUI.
	        } catch (Exception e) {
	            e.printStackTrace();
	            JOptionPane.showMessageDialog(this, "Lỗi khi xử lý đặt bàn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
	        }
	    }

	    // Hàm xử lý việc đặt bàn
	    private boolean handleDatBanAction() {
	        // 1. Lấy dữ liệu từ form
	        String tenKH = txtTenKhachHang.getText().trim();
	        String sdt = txtSoDienThoai.getText().trim();

	        // 2. Validate dữ liệu
	        if (tenKH.isEmpty() || sdt.isEmpty()) {
	            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Tên khách hàng và Số điện thoại.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
	            return false; // Dừng lại nếu lỗi
	        }
            // Kiểm tra SĐT (ví dụ đơn giản)
            if (!sdt.matches("0\\d{9}")) {
                JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ (phải là 10 số, bắt đầu bằng 0).", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
	            return false;
            }

	        try {
	            // 3. XỬ LÝ KHÁCH HÀNG
	            // Vì DatBanMoi_Dialog là inner class, nó có thể truy cập khachHangDAO của DatBan_GUI
	            KhachHang kh = khachHangDAO.findKhachHangBySDT(sdt);

	            if (kh == null) {
	                // Nếu KH không tồn tại -> Tạo mới
	                String maKH_moi = khachHangDAO.phatSinhMaKH(); // Cần hàm này trong DAO
	                kh = new KhachHang(maKH_moi, tenKH, sdt);
	                
	                boolean taoKHSuccess = khachHangDAO.createKhachHang(kh); // Cần hàm này trong DAO
	                if (!taoKHSuccess) {
	                    JOptionPane.showMessageDialog(this, "Không thể tạo khách hàng mới.", "Lỗi Database", JOptionPane.ERROR_MESSAGE);
	                    return false;
	                }
	            }

	            // 4. XỬ LÝ NHÂN VIÊN (Lấy mã 'NV01' từ label)
	            NhanVien nv = new NhanVien(); // Giả định bạn có Entity NhanVien
	            nv.setMaNV(lblMaNV.getText()); // Lấy mã NV từ label (đang hardcode là NV01)

	            // 5. TẠO ĐƠN ĐẶT BÀN
	            String maDDB_moi = donDatBanDAO.phatSinhMaDatBan(); // Cần hàm này trong DAO
	            java.sql.Date ngayDat = new java.sql.Date(System.currentTimeMillis()); // Ngày hiện tại
	            int soKhach = sucChuaBan; // Lấy sức chứa của bàn
	            String trangThaiDatBan = (String) cmbTrangThai.getSelectedItem();
	            String trangThaiBanMoi = trangThaiDatBan.equals("Đã đến") ? "Đang phục vụ" : "Đặt trước";

	            DonDatBan ddbMoi = new DonDatBan(maDDB_moi, ngayDat, soKhach, trangThaiBanMoi, kh, nv);

	            // 6. LƯU VÀO DATABASE VÀ CẬP NHẬT BÀN
	            Ban banMoi = new Ban(maBanDuocChon, "", soKhach, trangThaiBanMoi);
	            ddbMoi.setBan(banMoi);
                // Truy cập trực tiếp các DAO của lớp cha DatBan_GUI
	            boolean taoDDBSuccess = donDatBanDAO.createDonDatBan(ddbMoi); // Cần hàm này trong DAO
	            boolean capNhatBanSuccess = ban_DAO.updateTrangThaiBan(maBanDuocChon, trangThaiBanMoi); // Cần hàm này trong DAO

	            // 7. HOÀN TẤT
	            if (taoDDBSuccess && capNhatBanSuccess) {
	                JOptionPane.showMessageDialog(this, "Đã đặt bàn " + maBanDuocChon + " thành công!\nMã Đơn: " + maDDB_moi, "Thành công", JOptionPane.INFORMATION_MESSAGE);
	                return true; // Trả về true để đóng dialog và tải lại bàn
	            } else {
	            	JOptionPane.showMessageDialog(this, "Đã đặt bàn " + maBanDuocChon + " thành công!\nMã Đơn: " + maDDB_moi + ". Trạng thái: " + trangThaiDatBan, "Thành công", JOptionPane.INFORMATION_MESSAGE);
	                return false;
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi hệ thống: " + e.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
	            return false;
	        }
	    }
	 // Trong DatBan_GUI.java

	    private void handleThanhToanDatCoc() {
	        // 1. Kiểm tra trạng thái và thông tin khách hàng
	        if (selectedBan == null || selectedBan.getMaBan().trim().isEmpty()) {
	            JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn để đặt.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
	            return;
	        }
	        
	        // Lấy thông tin khách hàng từ các JTextField
	        String tenKH = txtTenKhachHang.getText().trim();
	        String sdt = txtSoDienThoai.getText().trim();
	        
	        if (tenKH.isEmpty() || sdt.isEmpty()) {
	            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Tên và SĐT khách hàng.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
	            return;
	        }
	        
	        if (!selectedBan.getTrangThai().equalsIgnoreCase("Trống")) {
	             JOptionPane.showMessageDialog(this, "Bàn đang " + selectedBan.getTrangThai() + ", không thể đặt cọc.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
	            return;
	        }

	        // 2. Chọn phương thức thanh toán tiền đặt cọc
	        String[] options = {"Tiền mặt", "Chuyển khoản"};
	        int choice = JOptionPane.showOptionDialog(this, 
	            "Chọn phương thức thanh toán tiền đặt cọc:", 
	            "Thanh toán Đặt Cọc", 
	            JOptionPane.YES_NO_OPTION, 
	            JOptionPane.QUESTION_MESSAGE, 
	            null, 
	            options, 
	            options[0]);

	        if (choice == JOptionPane.CLOSED_OPTION) {
	            return; // Người dùng hủy bỏ
	        }
	        
	        boolean isChuyenKhoan = (choice == 1);

	        // 3. Tạo EMPTY DefaultTableModel
	        // Việc này báo cho ChiTietDonDatBan_GUI biết đây là thanh toán "chỉ tiền cọc"
	        String[] cols = {"Tên món", "SL", "Đơn giá", "Thành tiền"};
	        DefaultTableModel emptyModel = new DefaultTableModel(cols, 0);

	        // 4. Mở ChiTietDonDatBan_GUI
	        ChiTietDonDatBan_GUI chiTiet = new ChiTietDonDatBan_GUI(
	            (JFrame) SwingUtilities.getWindowAncestor(this), // Parent Frame
	            emptyModel, // Model rỗng -> Thanh toán tiền cọc
	            isChuyenKhoan, 
	            selectedBan, 
	            tenKH, 
	            sdt
	        );
	        chiTiet.setVisible(true);
	        
	        // 5. Cập nhật lại danh sách bàn sau khi dialog đóng
	        // (Bởi vì ChiTietDonDatBan_GUI đã lưu DDB và đổi trạng thái bàn sang "Đã đặt")
	        loadBanVaoGUI(); // Cần đảm bảo bạn có hàm loadBan() để refresh màn hình
	    }
	    private JPanel createFieldPanel(String labelText, JComponent component, Font labelFont) {
	        JPanel panel = new JPanel(new BorderLayout(10, 0));
	        panel.setBackground(Color.WHITE);
	        JLabel label = new JLabel(labelText);
	        label.setFont(labelFont);
	        label.setPreferredSize(new Dimension(150, 35)); 
	        
	        
	        label.setHorizontalAlignment(SwingConstants.LEFT);
	        panel.add(label, BorderLayout.WEST);
	       
	        if (component instanceof JTextField) {
	            component.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
	        }
	        
	        panel.add(component, BorderLayout.CENTER);
	        return panel;
	    }
	    
	    // Hàm cập nhật ngày giờ tự động
	    private void updateNgayGio() {
	        LocalDateTime now = LocalDateTime.now();
	        // Định dạng
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy"); 
	        lblNgayGio.setText(now.format(formatter));
	    }
	}
	public void loadBanVaoGUI() {
	    pDanhSachBan.removeAll(); // Xóa tất cả component hiện có
	    pDanhSachBan.revalidate(); // Cập nhật Layout
	    pDanhSachBan.repaint(); // Vẽ lại

	    // 1. Lấy danh sách bàn từ Database
	    List<Ban> danhSachBan = ban_DAO.getAllBan();

	    // 2. Thêm từng bàn vào Panel
	    for (Ban ban : danhSachBan) {
	        // Tạo và thêm BanAnPanel cho mỗi đối tượng Ban
	        pDanhSachBan.add(new BanAnPanel(ban)); 
	    }
	    
	    pDanhSachBan.revalidate();
	    pDanhSachBan.repaint();
	}
	// Thêm vào lớp DatBan_GUI (cùng cấp với loadBanVaoGUI)

	// Trong DatBan_GUI.java
private void programmaticallySelectBan(String maBan) {
	    
	    // 1. Reset trạng thái chọn của TẤT CẢ các bàn về trạng thái mặc định
	    for (Component comp : pDanhSachBan.getComponents()) {
	        if (comp instanceof BanAnPanel) {
	            BanAnPanel panel = (BanAnPanel) comp;
	            // Đặt lại border mặc định cho tất cả
	            panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY.darker(), 1));
	        }
	    }
	    
	    // Reset biến lưu trạng thái chọn
	    selectedPanel = null;
	    selectedBan = null;
	    txtMaBan.setText("");
	    btnDatBan.setEnabled(false);
	    btnDatBan.setBackground(Color.GRAY);
	    
	    // Nếu mã bàn tìm kiếm là null, kết thúc hàm
	    if (maBan == null || maBan.isEmpty()) {
	        pDanhSachBan.revalidate();
	        pDanhSachBan.repaint();
	        return;
	    }

	    // 2. Lặp lại để tìm và chọn bàn cần tìm
	    for (Component comp : pDanhSachBan.getComponents()) {
	        if (comp instanceof BanAnPanel) {
	            BanAnPanel panel = (BanAnPanel) comp;
	            if (panel.ban.getMaBan().equalsIgnoreCase(maBan)) { 
	                // Gán biến chọn
	                selectedPanel = panel;
	                selectedBan = panel.ban;
	                
	                // Làm nổi bật border (Màu đỏ 3px)
	                panel.setBorder(BorderFactory.createLineBorder(Color.RED, 3)); 
	                
	                // Cập nhật ô Mã bàn
	                txtMaBan.setText(maBan);
	                
	                // Cập nhật trạng thái nút Đặt Bàn
	                if ("Trống".equals(selectedBan.getTrangThai().trim())) {
	                    btnDatBan.setEnabled(true);
	                    btnDatBan.setBackground(new Color(0,102,102)); 
	                } else {
	                    btnDatBan.setEnabled(false);
	                    btnDatBan.setBackground(Color.GRAY); 
	                }
	                
	                // Cuộn đến vị trí bàn
	                pDanhSachBan.scrollRectToVisible(panel.getBounds());
	                break;
	            }
	        }
	    }

	    pDanhSachBan.revalidate();
	    pDanhSachBan.repaint();
	}
	// Thêm vào lớp DatBan_GUI (cùng cấp với loadBanVaoGUI)

	// Trong DatBan_GUI.java, thay thế hàm handleTimKiemBanTheoMaDDB bằng đoạn code sau:

	// Trong DatBan_GUI.java
private void handleTimKiemBanTheoMaDDB(String maDDB) {
    maDDB = maDDB.trim();
    if (maDDB.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã Đơn Đặt Bàn.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    DonDatBan ddb = donDatBanDAO.getDonDatBanByMa(maDDB);

    if (ddb == null) {
        JOptionPane.showMessageDialog(this, "Không tìm thấy Đơn Đặt Bàn có mã: " + maDDB, "Lỗi", JOptionPane.ERROR_MESSAGE);
        // Xóa chọn bàn nếu không tìm thấy
        programmaticallySelectBan(null); 
        return;
    }

    String maBanCanTim = null;
    
    // ⭐ Đã Fix: Xử lý NullPointerException an toàn
    if (ddb.getBan() != null) { 
        maBanCanTim = ddb.getBan().getMaBan(); 
    }
    
    if (maBanCanTim == null || maBanCanTim.isEmpty()) {
         JOptionPane.showMessageDialog(this, 
             "Đã tìm thấy Đơn Đặt Bàn: " + maDDB + ".\n" +
             "NHƯNG: Đơn này không có thông tin bàn liên kết (maBan = NULL).", 
             "Thông báo", JOptionPane.WARNING_MESSAGE);
         
         programmaticallySelectBan(null); 
         return;
    }

    // ⭐ DÒNG GỌI HÀM CHỌN BÀN
    programmaticallySelectBan(maBanCanTim);
    
    JOptionPane.showMessageDialog(this, 
        "Tìm thấy Đơn Đặt Bàn: " + maDDB + ".\n" +
        "Đang hiển thị Bàn: " + maBanCanTim + ".\n" +
        "Trạng thái Đơn: " + ddb.getTrangThai(), 
        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
}
	public void loadBanVaoGUI(String maBan, String trangThai, int sucChua) {
	    pDanhSachBan.removeAll();
	    pDanhSachBan.revalidate();
	    pDanhSachBan.repaint();

	    List<Ban> danhSachBan;
	    // "Tất cả" là chuỗi mặc định cho lọc không hoạt động
	    if ((maBan == null || maBan.trim().isEmpty()) && trangThai.equals("Tất cả") && sucChua == 0) {
	        danhSachBan = ban_DAO.getAllBan();
	    } else {
	        danhSachBan = ban_DAO.getBansByCondition(maBan, trangThai, sucChua);
	    }

	    // Thêm từng bàn vào Panel
	    for (Ban ban : danhSachBan) {
	        pDanhSachBan.add(new BanAnPanel(ban));
	    }

	    pDanhSachBan.revalidate();
	    pDanhSachBan.repaint();
	}
	public class ThemBan_Dialog extends JDialog implements ActionListener {
	    
	    private DatBan_GUI parentGUI; // Tham chiếu đến giao diện chính để refresh
	    private JTextField txtMaBan, txtViTri, txtSucChua;
	    private JComboBox<String> cmbTrangThai;
	    private JButton btnLuu, btnHuy;
	    private Ban_DAO banDAO;

	    public ThemBan_Dialog(JFrame parent, DatBan_GUI parentGUI) {
	        super(parent, "Thêm Bàn Mới", true); // true cho modal dialog
	        this.parentGUI = parentGUI;
	        this.banDAO = new Ban_DAO();
	        
	        setLayout(new BorderLayout());
	        setSize(400, 300);
	        setLocationRelativeTo(parent);
	        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	        
	        // Cấu hình giao diện
	        setupForm();
	    }

	    private void setupForm() {
	        JPanel pCenter = new JPanel(new GridLayout(4, 2, 10, 10));
	        pCenter.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
	        
	        // 1. Mã Bàn (Tự động tạo)
	        txtMaBan = new JTextField(generateNewMaBan()); // ⭐ Phương thức giả định tạo mã
	        txtMaBan.setEditable(false);
	        pCenter.add(new JLabel("Mã Bàn:"));
	        pCenter.add(txtMaBan);
	        
	        // 2. Vị Trí
	        txtViTri = new JTextField();
	        pCenter.add(new JLabel("Vị Trí:"));
	        pCenter.add(txtViTri);

	        // 3. Sức Chứa
	        txtSucChua = new JTextField();
	        pCenter.add(new JLabel("Sức Chứa (Số người):"));
	        pCenter.add(txtSucChua);
	        
	        // 4. Trạng Thái (Thường là mặc định "Trống" khi thêm mới)
	        cmbTrangThai = new JComboBox<>(new String[]{"Trống", "Đặt trước", "Đang phục vụ"}); // Chỉ cho phép 2 trạng thái khởi tạo
	        cmbTrangThai.setSelectedItem("Trống");
	        pCenter.add(new JLabel("Trạng Thái Mặc Định:"));
	        pCenter.add(cmbTrangThai);
	        
	        add(pCenter, BorderLayout.CENTER);

	        // Nút Lưu và Hủy
	        JPanel pSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
	        btnLuu = new JButton("Lưu");
	        btnHuy = new JButton("Hủy");

	        btnLuu.addActionListener(this);
	        btnHuy.addActionListener(this);
	        btnLuu.setActionCommand("Luu");
	        btnHuy.setActionCommand("Huy");
	        
	        pSouth.add(btnLuu);
	        pSouth.add(btnHuy);
	        add(pSouth, BorderLayout.SOUTH);
	    }

	    private String generateNewMaBan() {
	        // ⭐ Cần thay thế bằng logic tạo mã bàn thực tế của bạn
	        // Ví dụ: dựa trên số lượng bàn hiện có trong CSDL
	        List<Ban> listBan = banDAO.getAllBan();
	        int soLuongHienTai = listBan.size();
	        return String.format("B%03d", soLuongHienTai + 1);
	    }
	    
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        if (e.getActionCommand().equals("Luu")) {
	            themBanMoi();
	        } else if (e.getActionCommand().equals("Huy")) {
	            dispose(); // Đóng dialog
	        }
	    }

	    private void themBanMoi() {
	        String maBan = txtMaBan.getText().trim();
	        String viTri = txtViTri.getText().trim();
	        String trangThai = cmbTrangThai.getSelectedItem().toString();
	        
	        int sucChua;
	        try {
	            sucChua = Integer.parseInt(txtSucChua.getText().trim());
	            if (sucChua <= 0) {
	                 JOptionPane.showMessageDialog(this, "Sức chứa phải là số nguyên dương.", "Lỗi", JOptionPane.ERROR_MESSAGE);
	                 return;
	            }
	        } catch (NumberFormatException ex) {
	            JOptionPane.showMessageDialog(this, "Sức chứa không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
	            return;
	        }

	        if (maBan.isEmpty() || viTri.isEmpty()) {
	             JOptionPane.showMessageDialog(this, "Mã bàn và Vị trí không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
	             return;
	        }
	        
	        // 1. Tạo đối tượng Ban
	        Ban banMoi = new Ban(maBan, viTri, sucChua, trangThai);

	        // 2. Thêm vào CSDL
	        if (banDAO.createBan(banMoi)) {
	            JOptionPane.showMessageDialog(this, "Thêm bàn " + maBan + " thành công!");
	            
	            // ⭐ 3. CẬP NHẬT GIAO DIỆN CHÍNH
	            // Gọi phương thức load lại danh sách bàn của DatBan_GUI
	            parentGUI.loadBanVaoGUI(null, "Tất cả", 0); 
	            
	            dispose(); // Đóng dialog
	        } else {
	            JOptionPane.showMessageDialog(this, "Lỗi khi thêm bàn. Có thể Mã bàn đã tồn tại hoặc lỗi CSDL.", "Lỗi", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	}
	class BanAnPanel extends JPanel {
	    private Ban ban; 
	    private Color mauNen;
	    private final String ICON_TRONG = "/image/table.PNG";
	    private final String ICON_DANG_PHUC_VU = "/image/ban_phuc_vu.PNG";
	    private final String ICON_DA_DAT = "/image/ban_dat_truoc.PNG";
	    private final String ICON_DEFAULT = "/image/table.PNG";

	    public BanAnPanel(Ban ban) { 
	        this.ban = ban;
	        final BanAnPanel currentPanel = this;
	        this.mauNen = ban.getMauTheoTrangThai(); 
	        
	        setLayout(new BorderLayout());
	        setBackground(mauNen.darker()); 
	        setPreferredSize(new Dimension(150, 100)); 
	        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY.darker(), 1));
	        
	        java.net.URL imageUrl = null;
	        
	        String iconPath = ICON_DEFAULT;
	        switch (ban.getTrangThai()) {
            case "Trống":
                iconPath = ICON_TRONG;
                break;
            case "Đang phục vụ":
                iconPath = ICON_DANG_PHUC_VU;
                break;
            case "Đã đặt":
                iconPath = ICON_DA_DAT;
                break;
            case "Lỗi dữ liệu": 
                iconPath = ICON_DEFAULT;
                break;
        }
	        imageUrl = getClass().getResource(iconPath);
	        
	        JLabel lblIcon = new JLabel(new ImageIcon(iconPath)); 
	        if (imageUrl != null) {
	            ImageIcon originalIcon = new ImageIcon(imageUrl);
	            java.awt.Image image = originalIcon.getImage();
	            java.awt.Image scaledImage = image.getScaledInstance(120, 60, java.awt.Image.SCALE_SMOOTH); 
	            
	            lblIcon = new JLabel(new ImageIcon(scaledImage));
	        } else {
	            
	            lblIcon = new JLabel("Icon Lỗi!", SwingConstants.CENTER);
	            lblIcon.setForeground(Color.RED);
	        }
	        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
	        
	        // Thêm icon vào phần trung tâm 
	        add(lblIcon, BorderLayout.CENTER);
	        
	        // Panel để chứa thông tin chi tiết 
	        JPanel pInfo = new JPanel(new BorderLayout());
	        pInfo.setBackground(Color.WHITE);
	        pInfo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

	        JLabel lblTenBan = new JLabel("Bàn: " + ban.getMaBan(), SwingConstants.CENTER); 
	        lblTenBan.setFont(new Font("Segoe UI", Font.BOLD, 10));
	        lblTenBan.setForeground(Color.BLACK);
	        
	        JLabel lblSoNguoi = new JLabel(ban.getSucChua() + " người", SwingConstants.CENTER); 
	        lblSoNguoi.setFont(new Font("Segoe UI", Font.PLAIN, 10));
	        lblSoNguoi.setForeground(Color.DARK_GRAY);
	        
	        
	        JLabel lblTrangThai = new JLabel(ban.getTrangThai(), SwingConstants.CENTER);
	        lblTrangThai.setFont(new Font("Segoe UI", Font.BOLD, 8));
	        
	        String trangThaiBan = ban.getTrangThai();
	        
	     
	        if ("Đang phục vụ".equals(trangThaiBan)) { 
	             lblTrangThai.setForeground(new Color(192, 57, 43)); 
	        } else if ("Đã đặt".equals(trangThaiBan)) { 
	             lblTrangThai.setForeground(new Color(153, 153, 0)); 
	        } else if ("Trống".equals(trangThaiBan)) { 
	             lblTrangThai.setForeground(new Color(0,102,0)); 
	        } else {
	             
	             lblTrangThai.setForeground(Color.BLACK);
	        }
	        
	      
	        JPanel pText = new JPanel(new GridLayout(3, 1));
	        pText.setBackground(Color.WHITE);
	        pText.add(lblSoNguoi);
	        pText.add(lblTenBan);
	        pText.add(lblTrangThai); 
	        
	        pInfo.add(pText, BorderLayout.CENTER);
	        
	        add(pInfo, BorderLayout.SOUTH); 
	        
	        // Thêm MouseListener để có hiệu ứng khi click chọn bàn
	        addMouseListener(new MouseAdapter() {
	        	@Override
	        	public void mouseClicked(MouseEvent e) {
	        	
	        	if (selectedPanel != null && selectedPanel != currentPanel) {
	        		selectedPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY.darker(), 1));
	        	}
	        	selectedPanel = currentPanel; 
	        	selectedBan = ban; // Gán đối tượng Ban vào biến của DatBan_GUI
	        	currentPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 3)); 
	        	if ("Trống".equals(ban.getTrangThai().trim())) {
	        	btnDatBan.setEnabled(true);
	        	btnDatBan.setBackground(new Color(0,102,102)); 
	        	} else {
	        	btnDatBan.setEnabled(false);
	        	btnDatBan.setBackground(Color.GRAY); 
	        	}
	        	System.out.println("Đã chọn bàn: " + ban.getMaBan() + " - Trạng thái: " + ban.getTrangThai());
	        	}
	            
	        	@Override
	            public void mouseEntered(MouseEvent e) {
	                // Chỉ hiển thị hiệu ứng hover nếu bàn này KHÔNG phải là bàn đang được chọn
	                if (currentPanel != selectedPanel) {
	            	    setBorder(BorderFactory.createLineBorder(new Color(192, 57, 43), 2)); 
	                }
	            }
	            
	        	@Override
	            public void mouseExited(MouseEvent e) {
	                // Chỉ reset border về mặc định nếu bàn này KHÔNG phải là bàn đang được chọn
	                if (currentPanel != selectedPanel) {
	            	    setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY.darker(), 1));
	                }
	            }
	        });
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
	            setFont(new Font("Segoe UI", Font.BOLD, 18));
	            setPreferredSize(new Dimension(300, 45));
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
	            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
	            super.paintComponent(g2);
	            g2.dispose();
	        }

	        @Override
	        public void updateUI() {
	            setUI(new javax.swing.plaf.basic.BasicButtonUI());
	        }
	    }
	    
//	    // Inner class RoundedTextField
	    class RoundedTextField extends JTextField {
	        public RoundedTextField(String placeholder) {
	            super(placeholder);
	            setFont(new Font("Segoe UI", Font.PLAIN, 16));
	            setForeground(Color.GRAY);
	            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
	        }

	        @Override
	        protected void paintComponent(Graphics g) {
	            Graphics2D g2 = (Graphics2D) g.create();
	            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	            g2.setColor(Color.WHITE);
	            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
	            g2.setColor(new Color(200, 200, 200));
	            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
	            super.paintComponent(g2);
	            g2.dispose();
	        }
	    }
}
