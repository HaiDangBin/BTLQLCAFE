package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import DAO.HoaDon_DAO;
import Entity.Ban;
import Entity.HoaDon;
import Entity.KhachHang;
import Entity.KhuyenMai;
import Entity.NhanVien;
import connectDB.DBconnection;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

public class ChiTietHoaDonSanPham_GUI extends JPanel {
	private JTable tblChiTiet;
	private JLabel lblTongTien, lblVAT, lblThanhTien, lblTienThua;
	private JTextField txtTienKhachTra;
	private DecimalFormat df = new DecimalFormat("#,### VND");
	private DefaultTableModel modelMonAnGoc;
	private String maBanHienTai;
    private String maNVHienTai;
    private String maKHHienTai;

	private double tongTienThuc;
	private double vatThuc;
	private double thanhTienThuc;

	private JLabel lblTienKhachTraStatic;
	private JLabel lblTienThuaStatic;
	private RoundedButton btnIn;
	private final boolean isChuyenKhoanMode;
	private JLabel lblTienKhachTra;

	private GoiMon_GUI parentPanel;


	public ChiTietHoaDonSanPham_GUI(GoiMon_GUI hoaDonPanel, DefaultTableModel modelMonAn, boolean isChuyenKhoan, String maBan, String maNV, String maKH ) {
	

		this.parentPanel = hoaDonPanel; 
		this.isChuyenKhoanMode = isChuyenKhoan;
		this.maBanHienTai = maBan;
        this.maNVHienTai = maNV;
        this.maKHHienTai = maKH;

	
		setLayout(new BorderLayout(10, 10));
		this.modelMonAnGoc = modelMonAn;

		// ===== HEADER =====
		JPanel pnlHeader = new JPanel(new GridLayout(5, 1));
		JLabel lblTenNH = new JLabel("Quán Caffe CornCorn", SwingConstants.CENTER);
		lblTenNH.setFont(new Font("Segoe UI", Font.BOLD, 19));
		JLabel lblDiaChi = new JLabel("ĐỊA CHỈ: 24 Lê Đức Thọ, p5, Gò Vấp", SwingConstants.CENTER);
		lblDiaChi.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		JLabel lblSDT = new JLabel("SĐT: 0242 545 567", SwingConstants.CENTER);
		lblSDT.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		JLabel lblHD = new JLabel("HÓA ĐƠN THANH TOÁN", SwingConstants.CENTER);
		lblHD.setFont(new Font("Segoe UI", Font.BOLD, 19));

		pnlHeader.add(lblTenNH);
		pnlHeader.add(lblDiaChi);
		pnlHeader.add(lblSDT);
		pnlHeader.add(Box.createVerticalStrut(1));
		pnlHeader.add(lblHD);
		add(pnlHeader, BorderLayout.NORTH);

		// ===== BẢNG MÓN ĂN =====
		String[] cols = { "Tên món", "Số lượng", "Đơn giá", "Thành tiền" };
		DefaultTableModel tblModel = new DefaultTableModel(cols, 0);
		tblChiTiet = new JTable(tblModel);
		JScrollPane scroll = new JScrollPane(tblChiTiet);

		scroll.setPreferredSize(new Dimension(500, 250)); 

		add(scroll, BorderLayout.CENTER);
		this.modelMonAnGoc = new DefaultTableModel(new Object[] { "MaMon", "Tên món", "SL", "Đơn giá", "Thành tiền" }, 0);

		this.tongTienThuc = 0;
		for (int i = 0; i < modelMonAn.getRowCount(); i++) {
			// Lấy dữ liệu từ model gốc (model trong table chính)
			Object maMon = modelMonAn.getValueAt(i, 0);
			String tenMon = modelMonAn.getValueAt(i, 1).toString();
			Object slObject = modelMonAn.getValueAt(i, 2);
			int sl;
			if (slObject instanceof Integer) {
				sl = (int) slObject;
			} else {
				sl = Integer.parseInt(slObject.toString().trim());
			}
			String donGiaStr = modelMonAn.getValueAt(i, 3).toString().replace(",", "").replace("VND", "").trim();
			double donGia = Double.parseDouble(donGiaStr);
			double thanhTien = sl * donGia;
			this.tongTienThuc += thanhTien;

			// Thêm vào model chi tiết (để lưu CSDL)
			this.modelMonAnGoc.addRow(new Object[] { maMon, tenMon, sl, donGia, thanhTien });

			// Thêm vào model hiển thị (trong dialog này)
			tblModel.addRow(new Object[] { tenMon, sl, df.format(donGia), df.format(thanhTien) });
		}

		// ===== FOOTER =====
		JPanel pnlInfo = new JPanel(new GridLayout(5, 2, 5, 5));
		pnlInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

		this.vatThuc = this.tongTienThuc * 0.05;
		this.thanhTienThuc = this.tongTienThuc + this.vatThuc;

		pnlInfo.add(new JLabel("Tổng cộng:"));
		pnlInfo.add(lblTongTien = new JLabel(df.format(this.tongTienThuc), SwingConstants.RIGHT));

		pnlInfo.add(new JLabel("VAT (10%):"));
		pnlInfo.add(lblVAT = new JLabel(df.format(this.vatThuc), SwingConstants.RIGHT));

		pnlInfo.add(new JLabel("Thành tiền:"));
		pnlInfo.add(lblThanhTien = new JLabel(df.format(this.thanhTienThuc), SwingConstants.RIGHT));

		pnlInfo.add(lblTienKhachTraStatic = new JLabel("Tiền khách trả:"));
		txtTienKhachTra = new JTextField();
		txtTienKhachTra.setHorizontalAlignment(SwingConstants.RIGHT);
		pnlInfo.add(txtTienKhachTra);

		pnlInfo.add(lblTienThuaStatic = new JLabel("Tiền thừa:"));
		pnlInfo.add(lblTienThua = new JLabel("0 VND", SwingConstants.RIGHT));

		txtTienKhachTra.addActionListener(e -> {
			try {
				double khachTra = Double
						.parseDouble(txtTienKhachTra.getText().replace(",", "").replace("VND", "").trim());
				double thua = khachTra - this.thanhTienThuc;
				txtTienKhachTra.setText(df.format(khachTra));
				lblTienThua.setText(df.format(thua >= 0 ? thua : 0));
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền hợp lệ!");
			}
		});

		JPanel pnlAction = new JPanel(new BorderLayout());
		pnlAction.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JLabel lblCamOn = new JLabel("CẢM ƠN QUÝ KHÁCH!", SwingConstants.CENTER);
		lblCamOn.setFont(new Font("Segoe UI", Font.ITALIC, 18));
		pnlAction.add(lblCamOn, BorderLayout.NORTH);

		JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
		RoundedButton btnDong = new RoundedButton("ĐÓNG", new Color(153, 0, 0));
		this.btnIn = new RoundedButton("IN", new Color(70, 70, 70));

		if (!this.isChuyenKhoanMode) {
			pnlButtons.add(btnDong);
			pnlButtons.add(this.btnIn);
		} else {
			pnlButtons.setVisible(false);
		}

		pnlAction.add(pnlButtons, BorderLayout.CENTER);

		this.btnIn
				.addActionListener(e -> handleInHoaDon(tblModel, this.tongTienThuc, this.vatThuc, this.thanhTienThuc));

	
		btnDong.addActionListener(e -> {
			Window window = SwingUtilities.getWindowAncestor(this);
			if (window != null) {
				window.dispose();
			}
		});

		JPanel pnlFooterContainer = new JPanel();
		pnlFooterContainer.setLayout(new BoxLayout(pnlFooterContainer, BoxLayout.Y_AXIS));
		pnlFooterContainer.add(pnlInfo);
		pnlFooterContainer.add(pnlAction);

		if (this.isChuyenKhoanMode) {
			txtTienKhachTra.setVisible(false);
			int txtIndex = getComponentIndex(pnlInfo, txtTienKhachTra);

			if (txtIndex != -1) {
				pnlInfo.remove(txtTienKhachTra);
				JLabel lblKhachTraChuyenKhoan = new JLabel(df.format(this.thanhTienThuc), SwingConstants.RIGHT);
				pnlInfo.add(lblKhachTraChuyenKhoan, txtIndex);
			}

			lblTienThua.setText("0 VND");
			lblTienThua.setFont(new Font("Segoe UI", Font.BOLD, 14));

			for (ActionListener al : this.btnIn.getActionListeners()) {
				this.btnIn.removeActionListener(al);
			}
			this.btnIn.setText("IN & HOÀN TẤT");
			this.btnIn.addActionListener(e -> {
				handleThanhToanChuyenKhoan();
			});

			pnlInfo.revalidate();
			pnlInfo.repaint();
		}
		add(pnlFooterContainer, BorderLayout.SOUTH);
	}

	/** ==================== XỬ LÝ IN HÓA ĐƠN ==================== */
	private void handleInHoaDon(DefaultTableModel tblModel, double tongTien, double vat, double thanhTien) {
		try {
			double khachTra = Double.parseDouble(txtTienKhachTra.getText().replace(",", "").replace("VND", "").trim());
			double tienThua = khachTra - thanhTien;

			if (khachTra < thanhTien) {
				JOptionPane.showMessageDialog(this, "Tiền khách trả chưa đủ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
				return;
			}

			if (JOptionPane.showConfirmDialog(this, "Xác nhận thanh toán và in hóa đơn?", "Xác nhận",
					JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
				return;

			String maHD = taoMaHoaDonNgauNhien();
            String maKH = this.maKHHienTai; 
            String maNV = this.maNVHienTai; 
            String maKM = null;
            String maBan = this.maBanHienTai;

			KhachHang khachHangEntity = new KhachHang();
			khachHangEntity.setMaKH(maKH);

			NhanVien nhanVienEntity = new NhanVien();
			nhanVienEntity.setMaNV(maNV);

			Ban banEntity = new Ban();
			banEntity.setMaBan(maBan);

			KhuyenMai khuyenMaiEntity = null;
			if (maKM != null) {
				khuyenMaiEntity = new KhuyenMai();
				khuyenMaiEntity.setMaKM(maKM);
			}

			java.sql.Date sqlNgayLap = java.sql.Date.valueOf(LocalDate.now());

			HoaDon hd = new HoaDon(
					maHD,      
				    maNV,       
				    maKH,      
				    maKM,       
				    maBan,   
				    sqlNgayLap      
		        );

			HoaDon_DAO dao = new HoaDon_DAO();

			if (dao.insert(hd)) {
				JOptionPane.showMessageDialog(this, "✅ Lưu hóa đơn thành công! Mã HD: " + maHD);

		
				if (parentPanel != null) {
					parentPanel.clearOrderTable1();
				}

				showHoaDonInDialog(tblModel,
						df.format(tongTien), df.format(vat), df.format(thanhTien), df.format(khachTra),
						df.format(tienThua >= 0 ? tienThua : 0));

				
				Window window = SwingUtilities.getWindowAncestor(this);
				if (window != null)
					window.dispose();

			} else {
				JOptionPane.showMessageDialog(this, "❌ Lưu hóa đơn thất bại!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
			}
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập Tiền khách trả hợp lệ!", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace(); 
		}
	}

	public void handleThanhToanChuyenKhoan() {
		double tongTien = this.tongTienThuc;
		double vat = this.vatThuc;
		double thanhTien = this.thanhTienThuc;
		double khachTra = thanhTien;
		double tienThua = 0;

		try {

			if (JOptionPane.showConfirmDialog(this, "Xác nhận thanh toán CHUYỂN KHOẢN và in hóa đơn?", "Xác nhận",
					JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
				return;

			String maHD = taoMaHoaDonNgauNhien();
            String maKH = this.maKHHienTai;
            String maNV = this.maNVHienTai;
            String maKM = null;
            String maBan = this.maBanHienTai;

			KhachHang khachHangEntity = new KhachHang();
			khachHangEntity.setMaKH(maKH);

			NhanVien nhanVienEntity = new NhanVien();
			nhanVienEntity.setMaNV(maNV);

			Ban banEntity = new Ban();
			banEntity.setMaBan(maBan);

			KhuyenMai khuyenMaiEntity = null;
			if (maKM != null) {
				khuyenMaiEntity = new KhuyenMai();
				khuyenMaiEntity.setMaKM(maKM);
			}

			java.sql.Date sqlNgayLap = java.sql.Date.valueOf(LocalDate.now());

			HoaDon hd = new HoaDon(
					maHD,       
				    maNV,     
				    maKH,      
				    maKM,     
				    maBan,   
				    sqlNgayLap     
		        );

			HoaDon_DAO dao = new HoaDon_DAO();

			if (dao.insert(hd)) {
				JOptionPane.showMessageDialog(this, "✅ Lưu hóa đơn thành công! Mã HD: " + maHD);
				if (parentPanel != null) {
					parentPanel.clearOrderTable1();
				}

				// THAY ĐỔI: Dùng modelMonAnGoc để in (vì tblModel có thể chưa được tạo)
				showHoaDonInDialog(this.modelMonAnGoc, df.format(tongTien), df.format(vat), df.format(thanhTien),
						df.format(khachTra), df.format(tienThua));

				Window window = SwingUtilities.getWindowAncestor(this);
				if (window != null)
					window.dispose();

			} else {
				JOptionPane.showMessageDialog(this, "❌ Lưu hóa đơn thất bại!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "❌ Lỗi khi thanh toán chuyển khoản: " + ex.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace(); 
		}
	}

	/** ==================== HIỂN THỊ POPUP IN ==================== */
	private void showHoaDonInDialog(DefaultTableModel model, String tong, String vat, String thanhTien,
			String tienKhach, String tienThua) {
		Window owner = SwingUtilities.getWindowAncestor(this); 

		// THAY ĐỔI: Tạo JDialog mới
		JDialog dialog = new JDialog(owner, "In hóa đơn");
		dialog.setSize(550, 700);
		dialog.setLocationRelativeTo(owner);
		dialog.setLayout(new BorderLayout(10, 10));

		// Header
		JPanel header = new JPanel(new GridLayout(5, 1));
		JLabel title = new JLabel("Quán Caffe CornCornP", SwingConstants.CENTER);
		title.setFont(new Font("Segoe UI", Font.BOLD, 19));
		header.add(title);
		JLabel dChi = new JLabel("ĐỊA CHỈ: 24 Lê Đức Thọ, p5, Gò Vấp", SwingConstants.CENTER);
		dChi.setFont(new Font("Segoe UI", Font.BOLD, 16));
		JLabel sdt = new JLabel("SĐT: 0242 545 567", SwingConstants.CENTER);
		sdt.setFont(new Font("Segoe UI", Font.BOLD, 16));
		JLabel hd = new JLabel("HÓA ĐƠN THANH TOÁN", SwingConstants.CENTER);
		hd.setFont(new Font("Segoe UI", Font.BOLD, 19));
		header.add(dChi);
		header.add(sdt);
		header.add(Box.createVerticalStrut(1));
		header.add(hd);
		dialog.add(header, BorderLayout.NORTH);

		// Table
		String[] cols = { "Tên món", "SL", "Đơn giá", "Thành tiền" };
		DefaultTableModel tblModel = new DefaultTableModel(cols, 0);
		JTable tblIn = new JTable(tblModel);

		for (int i = 0; i < model.getRowCount(); i++) {
			tblModel.addRow(new Object[] { model.getValueAt(i, 1), 
					model.getValueAt(i, 2), 
					df.format(model.getValueAt(i, 3)), 
					df.format(model.getValueAt(i, 4)) 
			});
		}
		JScrollPane scroll = new JScrollPane(tblIn);

		// Footer
		JPanel footer = new JPanel(new GridLayout(5, 2, 5, 5));
		footer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		footer.add(new JLabel("Tổng cộng:"));
		footer.add(new JLabel(tong, SwingConstants.RIGHT));
		footer.add(new JLabel("VAT (5%):"));
		footer.add(new JLabel(vat, SwingConstants.RIGHT));
		footer.add(new JLabel("Thành tiền:"));
		footer.add(new JLabel(thanhTien, SwingConstants.RIGHT));
		footer.add(new JLabel("Tiền khách trả:"));
		footer.add(new JLabel(tienKhach, SwingConstants.RIGHT));
		footer.add(new JLabel("Tiền thừa:"));
		footer.add(new JLabel(tienThua, SwingConstants.RIGHT));

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(scroll, BorderLayout.CENTER);
		centerPanel.add(footer, BorderLayout.SOUTH);
		dialog.add(centerPanel, BorderLayout.CENTER);

		JLabel lblCamOn = new JLabel("CẢM ƠN QUÝ KHÁCH!", SwingConstants.CENTER);
		lblCamOn.setFont(new Font("Segoe UI", Font.ITALIC, 16));
		lblCamOn.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		dialog.add(lblCamOn, BorderLayout.SOUTH);

		dialog.setVisible(true);
	}

	private String taoMaHoaDonNgauNhien() {
		HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
		String maHD;
		Random rand = new Random();
		do {
			int soNgauNhien = rand.nextInt(900) + 100; 
			maHD = "HD" + soNgauNhien;
		} while (hoaDonDAO.kiemTraTonTai(maHD));
		return maHD;
	}

	// ================== NÚT BO TRÒN ===================
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
			setForeground(Color.WHITE);
			setBackground(backgroundColor);
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
			g2.setColor(getBackground() == null ? backgroundColor : getBackground());
			g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
			super.paintComponent(g2);
			g2.dispose();
		}

		@Override
		public void updateUI() {
			setUI(new javax.swing.plaf.basic.BasicButtonUI());
		}
	}

	private int getComponentIndex(Container container, Component component) {
		for (int i = 0; i < container.getComponentCount(); i++) {
			if (container.getComponent(i) == component) {
				return i;
			}
		}
		return -1;
	}
}