package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import DAO.Ban_DAO; // Import thêm Ban_DAO để cập nhật trạng thái bàn
import DAO.ChiTietHoaDon_DAO;
import DAO.DonDatBan_DAO;
import DAO.HoaDon_DAO;
import DAO.KhachHang_DAO;
import Entity.ChiTietHoaDon;
import Entity.DonDatBan;
import Entity.HoaDon;
import Entity.KhachHang;
import STARTING.LoginForm; // Đảm bảo import này đúng với cấu trúc project của bạn

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
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
    private String maHDHienTai;

	private double tongTienThuc;
	private double vatThuc;
	private double thanhTienThuc;
    
    private String tenKHHienTai = "Khách vãng lai"; 

	private JLabel lblTienKhachTraStatic;
	private JLabel lblTienThuaStatic;
	private RoundedButton btnIn;
	private final boolean isChuyenKhoanMode;
	private JLabel lblTienKhachTra;

	private GoiMon_GUI parentPanel;
	private JLabel txtThanhTien;


	public ChiTietHoaDonSanPham_GUI(GoiMon_GUI hoaDonPanel, DefaultTableModel modelMonAn, boolean isChuyenKhoan, String maBan, String maNV, String maKH ) {
	
		this.parentPanel = hoaDonPanel;	
		this.isChuyenKhoanMode = isChuyenKhoan;
		this.maBanHienTai = maBan;
        this.maNVHienTai = maNV;
        this.maKHHienTai = maKH;
	
		setLayout(new BorderLayout(10, 10));
		
        // ===== BẢNG MÓN ĂN =====
		String[] cols = { "Tên món", "Số lượng", "Đơn giá", "Thành tiền" };
		DefaultTableModel tblModel = new DefaultTableModel(cols, 0);
		tblChiTiet = new JTable(tblModel);
		JScrollPane scroll = new JScrollPane(tblChiTiet);

		scroll.setPreferredSize(new Dimension(500, 250));	
		add(scroll, BorderLayout.CENTER);

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

		this.modelMonAnGoc = new DefaultTableModel(new Object[] { "MaMon", "Tên món", "SL", "Đơn giá", "Thành tiền" }, 0); 

		this.tongTienThuc = 0;
        // Duyệt modelMonAn được truyền từ GoiMon_GUI
		for (int i = 0; i < modelMonAn.getRowCount(); i++) {
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
			this.modelMonAnGoc.addRow(new Object[] { maMon, tenMon, sl, donGia, thanhTien });
			tblModel.addRow(new Object[] { tenMon, sl, df.format(donGia), df.format(thanhTien) });
		}

		// ===== FOOTER =====
		JPanel pnlInfo = new JPanel(new GridLayout(5, 2, 5, 5));
		pnlInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

		this.vatThuc = this.tongTienThuc * 0.05;
		this.thanhTienThuc = this.tongTienThuc + this.vatThuc;

		pnlInfo.add(new JLabel("Tổng cộng:"));
		pnlInfo.add(lblTongTien = new JLabel(df.format(this.tongTienThuc), SwingConstants.RIGHT));

		pnlInfo.add(new JLabel("VAT (5%):")); 
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

		btnIn.addActionListener(e -> xuLyInHoaDon()); 
	
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
			lblTienThuaStatic.setVisible(false);
			lblTienThua.setVisible(false);
			
			int txtIndex = getComponentIndex(pnlInfo, txtTienKhachTra);

			if (txtIndex != -1) {
				pnlInfo.remove(txtTienKhachTra);
				JLabel lblKhachTraChuyenKhoan = new JLabel(df.format(this.thanhTienThuc), SwingConstants.RIGHT);
				pnlInfo.add(lblKhachTraChuyenKhoan, txtIndex);
				lblTienKhachTraStatic.setText("Đã nhận chuyển khoản:");
			}

			for (ActionListener al : this.btnIn.getActionListeners()) {
				this.btnIn.removeActionListener(al);
			}
			
			this.btnIn = new RoundedButton("IN & HOÀN TẤT", new Color(22, 160, 133)); 
			this.btnIn.addActionListener(e -> {
				handleThanhToanChuyenKhoan();
			});

			pnlButtons.setVisible(true);
			pnlButtons.removeAll();
			pnlButtons.add(btnDong);
			pnlButtons.add(this.btnIn);

			pnlInfo.revalidate();
			pnlInfo.repaint();
		}
		add(pnlFooterContainer, BorderLayout.SOUTH);
	}
	
	private void xuLyInHoaDon() {
	    try {
	        if (modelMonAnGoc.getRowCount() == 0) {
	            JOptionPane.showMessageDialog(this, "Chưa có món để in!");
	            return;
	        }
	        String text = txtTienKhachTra.getText().trim().replace(",", "").replace("VND", "");
	        if (text.isEmpty()) {
	            JOptionPane.showMessageDialog(this, "Vui lòng nhập tiền khách trả!", "Lỗi", JOptionPane.ERROR_MESSAGE);
	            return;
	        }
	        double khachTra = Double.parseDouble(text);
	        
	        if (khachTra < this.thanhTienThuc) {
	             JOptionPane.showMessageDialog(this, "Tiền khách trả không đủ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
	             return;
	        }
	        saveHoaDonTienMat();
	        handleInHoaDon(khachTra, thanhTienThuc);

	        capNhatHoaDonGUI();

	        Window window = SwingUtilities.getWindowAncestor(this);
	        if (window != null) window.dispose();

	    } catch (NumberFormatException ex) {
	        JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
	    } catch (Exception ex) {
	        ex.printStackTrace();
	        JOptionPane.showMessageDialog(this, "Lỗi khi xử lý in hóa đơn: " + ex.getMessage());
	    }
	}

	private void capNhatHoaDonGUI() {
        Window ancestorWindow = SwingUtilities.getWindowAncestor(this);
        if (ancestorWindow == null) return;
        Window owner = ancestorWindow.getOwner();
        if (owner instanceof JFrame) {
            JFrame mainFrame = (JFrame) owner;
            Component[] components = mainFrame.getContentPane().getComponents();
            for (Component comp : components) {
                if (comp instanceof JTabbedPane) {
                    JTabbedPane tabbedPane = (JTabbedPane) comp;
                    for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                        Component tabComponent = tabbedPane.getComponentAt(i);
                        if (tabComponent instanceof HoaDon_GUI) {
                            ((HoaDon_GUI) tabComponent).loadHoaDon();
                            return; 
                        }
                    }
                }
            }
        }
	}
	private void handleInHoaDon(double khachTra, double thanhTien) {
	    try {
	        double tienThua = khachTra - thanhTien;
	        showHoaDonInDialog(modelMonAnGoc, maHDHienTai, 
                    df.format(tongTienThuc), df.format(vatThuc), df.format(thanhTienThuc),
	                df.format(khachTra), df.format(tienThua), this.tenKHHienTai); 
	        
	        JOptionPane.showMessageDialog(this, "In hóa đơn thành công!");
	        if (parentPanel != null) {
	            parentPanel.clearOrderTable1();
	        }

	    } catch (NumberFormatException ex) {
	        JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
	    } catch (Exception ex) {
	        JOptionPane.showMessageDialog(this, "Lỗi hệ thống khi hiển thị hóa đơn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
	        ex.printStackTrace();
	    }
	}
	private void saveHoaDonTienMat() {
	    try {
	        DonDatBan_DAO ddbDAO = new DonDatBan_DAO();
	        DonDatBan ddb = ddbDAO.getLatestActiveDonDatBanByMaBan(this.maBanHienTai); 

	        String maDatBan = null;
	        String maKH = "KH000"; 
            this.tenKHHienTai = "Khách vãng lai"; 

	        if (ddb != null) {
	            maDatBan = ddb.getMaDatBan();
	            if (ddb.getMaKH() != null && ddb.getMaKH().getMaKH() != null) {
	                maKH = ddb.getMaKH().getMaKH();
	            }
	        }
	        if (!maKH.equals("KH000")) {
	            KhachHang kh = new KhachHang_DAO().findKhachHangByMa(maKH);
	            if (kh != null && kh.getTenKH() != null) {
	                this.tenKHHienTai = kh.getTenKH(); 
	            }
	        }
	        this.maHDHienTai = taoMaHoaDonNgauNhien();
	        String maNV = this.maNVHienTai;
            if (maNV == null || maNV.trim().isEmpty()) {
                maNV = LoginForm.tkLogin.getNhanVien().getMaNV();
            }
	        String maKM = null;
	        java.sql.Date sqlNgayLap = java.sql.Date.valueOf(LocalDate.now());

	        HoaDon hd = new HoaDon(
	                maHDHienTai,
	                maNV,
	                maKH,       
	                maKM,
	                maDatBan,   
	                sqlNgayLap
	        );
	        hd.setTongTien(this.thanhTienThuc); 

	        HoaDon_DAO hdDAO = new HoaDon_DAO();
	        if (!hdDAO.insert(hd)) {
	            JOptionPane.showMessageDialog(this,	
	                "❌ Lưu hóa đơn thất bại!", "SQL ERROR",
	                JOptionPane.ERROR_MESSAGE);
	            return;
	        }
	        ChiTietHoaDon_DAO ctDAO = new ChiTietHoaDon_DAO();
	        for (int i = 0; i < modelMonAnGoc.getRowCount(); i++) {
	            String maSP = modelMonAnGoc.getValueAt(i, 0).toString();
	            int soLuong = Integer.parseInt(modelMonAnGoc.getValueAt(i, 2).toString());
	            double donGia = Double.parseDouble(modelMonAnGoc.getValueAt(i, 3).toString()); 

	            ctDAO.createChiTietHoaDon(
	                new ChiTietHoaDon(maHDHienTai, maSP, soLuong, donGia)
	            );
	        }
	        if (maDatBan != null) {
	            ddbDAO.capNhatTrangThai(maDatBan, "Đã thanh toán"); 
	        }
	        new Ban_DAO().updateTrangThaiBan(this.maBanHienTai, "Trống");

	    } catch (Exception e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(this,
	                "Lỗi khi lưu hóa đơn: " + e.getMessage(), "Lỗi",
	                JOptionPane.ERROR_MESSAGE);
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
            DonDatBan_DAO ddbDAO = new DonDatBan_DAO();
            DonDatBan ddb = ddbDAO.getLatestActiveDonDatBanByMaBan(this.maBanHienTai); 

            String maDatBan = null;
            String maKH = "KH000"; 
            this.tenKHHienTai = "Khách vãng lai"; 
            if (ddb != null) {
                maDatBan = ddb.getMaDatBan();
                if (ddb.getMaKH() != null && ddb.getMaKH().getMaKH() != null) {
                    maKH = ddb.getMaKH().getMaKH();
                }
            }
            if (!maKH.equals("KH000")) {
	            KhachHang kh = new KhachHang_DAO().findKhachHangByMa(maKH);
	            if (kh != null && kh.getTenKH() != null) {
	                this.tenKHHienTai = kh.getTenKH();
	            }
	        }
			String maHD = taoMaHoaDonNgauNhien();
            this.maHDHienTai = maHD; 
            String maNV = this.maNVHienTai;
            if (maNV == null || maNV.trim().isEmpty()) {
                maNV = LoginForm.tkLogin.getNhanVien().getMaNV();
            }
            String maKM = null;
            
			java.sql.Date sqlNgayLap = java.sql.Date.valueOf(LocalDate.now());

			HoaDon hd = new HoaDon(
					maHD,	 	
				 	maNV,	 	
				 	maKH,	 	
				 	maKM,	 	
				 	maDatBan,   
				 	sqlNgayLap	 	
		 	 	);
            hd.setTongTien(this.thanhTienThuc); 

			HoaDon_DAO dao = new HoaDon_DAO();

			if (dao.insert(hd)) {
                ChiTietHoaDon_DAO ctDAO = new ChiTietHoaDon_DAO();
                for (int i = 0; i < modelMonAnGoc.getRowCount(); i++) {
                    String maSP = modelMonAnGoc.getValueAt(i, 0).toString();
                    int soLuong = Integer.parseInt(modelMonAnGoc.getValueAt(i, 2).toString());
                    double donGia = Double.parseDouble(modelMonAnGoc.getValueAt(i, 3).toString());

                    ctDAO.createChiTietHoaDon(
                        new ChiTietHoaDon(maHD, maSP, soLuong, donGia)
                    );
                }
                if (maDatBan != null) {
                    ddbDAO.capNhatTrangThai(maDatBan, "Đã thanh toán");
                }
                new Ban_DAO().updateTrangThaiBan(this.maBanHienTai, "Trống");

				JOptionPane.showMessageDialog(this, "✅ Lưu hóa đơn thành công! Mã HD: " + maHD);
				
				// 4. Hiển thị dialog in
                showHoaDonInDialog(this.modelMonAnGoc, maHD, 
                        df.format(tongTien), df.format(vat), df.format(thanhTien),
                        df.format(khachTra), df.format(tienThua), this.tenKHHienTai); 
                
				if (parentPanel != null) {
					parentPanel.clearOrderTable1();
				}
                capNhatHoaDonGUI();
                
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

	private void showHoaDonInDialog(DefaultTableModel model, String maHD,
            String tong, String vat, String thanhTien,
            String tienKhach, String tienThua, String tenKH) { 

        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(owner, "In hóa đơn");
        dialog.setSize(450, 600);
        dialog.setLocationRelativeTo(owner);
        dialog.setLayout(new BorderLayout(10, 10));

        // ================= HEADER =================
        JPanel header = new JPanel(new GridLayout(6, 1));
        header.add(new JLabel("Quán Caffe CornCorn", SwingConstants.CENTER));
        header.add(new JLabel("Địa chỉ: 24 Lê Đức Thọ, Gò Vấp", SwingConstants.CENTER));
        header.add(new JLabel("SĐT: 0242 545 567", SwingConstants.CENTER));
        header.add(new JLabel("HÓA ĐƠN THANH TOÁN", SwingConstants.CENTER));
        header.add(new JLabel("Khách hàng: " + tenKH, SwingConstants.CENTER)); 
        header.add(new JLabel("Mã HĐ: " + maHD, SwingConstants.CENTER));


        dialog.add(header, BorderLayout.NORTH);

        // ================= TABLE =================
        String[] cols = { "Tên món", "SL", "Đơn giá", "Thành tiền" };
        DefaultTableModel tblModel = new DefaultTableModel(cols, 0);
        JTable table = new JTable(tblModel);

        for (int i = 0; i < model.getRowCount(); i++) {
            tblModel.addRow(new Object[]{
                model.getValueAt(i, 1),
                model.getValueAt(i, 2),
                df.format(model.getValueAt(i, 3)),
                df.format(model.getValueAt(i, 4))
            });
        }

        dialog.add(new JScrollPane(table), BorderLayout.CENTER);

        // ================= FOOTER =================
        JPanel footer = new JPanel(new GridLayout(5, 2, 5, 5));
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

        dialog.add(footer, BorderLayout.SOUTH);

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

	// ================== HÀM TIỆN ÍCH (Giữ nguyên) ===================
	private int getComponentIndex(Container container, Component component) {
		for (int i = 0; i < container.getComponentCount(); i++) {
			if (container.getComponent(i) == component) {
				return i;
			}
		}
		return -1;
	}
}