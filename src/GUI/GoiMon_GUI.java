package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.HashMap; // Cần import HashMap
import java.util.Map;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import DAO.SanPham_DAO;
import Entity.SanPham;
import DAO.Ban_DAO; // <-- THÊM DÒNG NÀY
import Entity.Ban;
import DAO.DonDatBan_DAO;
import Entity.DonDatBan;

public class GoiMon_GUI extends JDialog {
	private SanPham_DAO sanPhamDAO;
    private final DecimalFormat df = new DecimalFormat("#,###");
    private Ban_DAO banDAO; 
	private Ban banHienTai;
	private DonDatBan_DAO ddbDAO; 

    private final Vector<Vector<Object>> orderData = new Vector<>();
	private JTextField txtTimKiem;
	private RoundedButton btnSoLuongKhach;
	private RoundedButton btnKhachOrder;
	private RoundedButton btnGioVao;
	private JTable table;
    private RoundedButton btnTienMat;
    private RoundedButton btnChuyenKhoan;
    private String phuongThucThanhToan = "Tiền mặt";
    private String maBan;
    private DefaultTableModel orderModel;
    private RoundedLabel lblTongTien;
    
    private final Dimension WIDE_BTN = new Dimension(220, 45);
    private final Color RED_ALERT = new Color(217, 83, 79);
    private final Color GREEN_SUCCESS = new Color(92, 184, 92);
    private final Color BLUE_INFO = new Color(91, 192, 222);
    
    private Map<String, SanPham> menuMap;
	private SanPham sanPham;
	private RoundedButton btnChuyenGhep;
	private RoundedButton btnLuu;
	private RoundedButton btnThanhToan;
	private String tenKhachHang;
	private String sdtKhachHang;

    public GoiMon_GUI(JFrame parent, String maBan, String tenKH, String sdt) {
    	super(parent, "Gọi Món Mới cho Bàn " + maBan, true);
        this.maBan = maBan;
        this.banDAO = new Ban_DAO();
        this.tenKhachHang = tenKH; 
        this.sdtKhachHang = sdt;
        this.ddbDAO = new DonDatBan_DAO();
        this.setUndecorated(true);
        try {
            this.banHienTai = banDAO.layBanTheoMa(this.maBan);

            if (this.banHienTai == null) {
               
                JOptionPane.showMessageDialog(parent, 
                    "Lỗi: Không tìm thấy Bàn " + maBan + " trong cơ sở dữ liệu. Không thể tiếp tục.", 
                    "Lỗi Dữ liệu Bàn", 
                    JOptionPane.ERROR_MESSAGE);
                this.dispose(); 
                return; 
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Lỗi CSDL khi lấy thông tin bàn: " + e.getMessage(), "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
            this.dispose();
            return;
        }
        int dialogWidth = 1525; 
        int dialogHeight = 800; 
        setSize(dialogWidth, dialogHeight); 
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - dialogWidth) / 2;
        int y = (screenSize.height - dialogHeight) / 2; 
        setLocation(x, y);
        this.sanPhamDAO = new SanPham_DAO(); 
        this.menuMap = getMonAnList(); 
        
        String[] columnNames = {"", "TÊN HÀNG HÓA", "SL", "ĐƠN GIÁ", "THÀNH TIỀN"};
        this.orderModel = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;
            @Override public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex == 2) ? Integer.class : String.class;
            }
            @Override public boolean isCellEditable(int row, int column) {
                return column == 2; 
            }
        };
        this.table = new JTable(orderModel);
    	table.getColumnModel().getColumn(0).setPreferredWidth(0);
    	table.getColumnModel().getColumn(0).setMinWidth(0);
    	table.getColumnModel().getColumn(0).setMaxWidth(0);

        txtTimKiem = new JTextField(30); 

        setupUIContent(); 

        setupEvents();
    }

    private void setupUIContent() {
        
        setLayout(new BorderLayout());
        
        // ---------------------- 1. PANEL NORTH (Thanh tìm kiếm) ----------------------
        JPanel pNorth = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pNorth.setBackground(new Color(0,51,102));
        // txtTimKiem đã được khởi tạo trong constructor
        txtTimKiem.setPreferredSize(new Dimension(300,30));
        pNorth.add(txtTimKiem);
        add(pNorth, BorderLayout.NORTH);
        
        // ---------------------- 2. PANEL WEST (Bảng Chi tiết đơn hàng) ----------------------
        JPanel wrap = new JPanel();
        wrap.setLayout(new BorderLayout());
        JPanel pWest = new JPanel();
        pWest.setLayout(new BorderLayout()); 
        pWest.setPreferredSize(new Dimension(800, 0));
        
        // -- 2a. Header pWest --
        JPanel pnA = new JPanel();
        pnA.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        pnA.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btnSoLuongKhach = new RoundedButton("👥 Số lượng khách [1]", new Color(64,64,64));
        btnKhachOrder = new RoundedButton("🔔 Khách gọi món [5]", new Color(64,64,64));
        btnGioVao = new RoundedButton("⏰ Giờ vào: ", new Color(152,0,0)); 

        pnA.add(btnSoLuongKhach);
        pnA.add(Box.createHorizontalStrut(10));
        pnA.add(btnKhachOrder);
        pnA.add(Box.createHorizontalStrut(10)); 
        pnA.add(btnGioVao);
        
        pWest.add(pnA, BorderLayout.NORTH); 
       
        table.setRowHeight(35);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
        JScrollPane scrollPane = new JScrollPane(table);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setPreferredSize(new Dimension(header.getWidth(), 30));
        table.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer()); 
        table.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(table));
        
        pWest.add(scrollPane, BorderLayout.CENTER);

        // -- 2c. Footer pWest  --
        JPanel pSouth = new JPanel(new BorderLayout());
        pSouth.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel pFooterTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        // 1. Nút thanh toán
        btnTienMat = new RoundedButton("Tiền mặt", new Color(224,224,224), new Dimension(250,50));
        btnChuyenKhoan = new RoundedButton("Chuyển khoản", new Color(224,224,224), new Dimension(250,50));
        btnTienMat.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnTienMat.setForeground(Color.BLACK);
        btnChuyenKhoan.setFont(new Font("Segoe UI", Font.BOLD,20));
        btnChuyenKhoan.setForeground(Color.BLACK);
        
        pFooterTop.add(btnTienMat);
        pFooterTop.add(Box.createHorizontalStrut(1));
        pFooterTop.add(btnChuyenKhoan);
        
        // 2. Tổng tiền
        pFooterTop.add(Box.createHorizontalStrut(1));        
        lblTongTien = new RoundedLabel("Tổng tiền: ", new Color(145,0,0)); 
        lblTongTien.setPreferredSize(new Dimension(220, 50)); 
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTongTien.setHorizontalAlignment(SwingConstants.LEFT);
        
        pFooterTop.add(lblTongTien); 

        pSouth.add(pFooterTop, BorderLayout.NORTH);

        // Các nút chức năng ở dưới
        JPanel pFooterBottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        btnChuyenGhep = new RoundedButton("CHUYỂN/GHÉP BÀN", BLUE_INFO, new Dimension(250,45));
        btnLuu = new RoundedButton("LƯU", new Color(230, 82, 135), new Dimension(250,45));
        btnThanhToan = new RoundedButton("THANH TOÁN", GREEN_SUCCESS, WIDE_BTN);

        pFooterBottom.add(btnChuyenGhep);
        pFooterBottom.add(Box.createHorizontalStrut(1));
        pFooterBottom.add(btnLuu);
        pFooterBottom.add(Box.createHorizontalStrut(1));
        pFooterBottom.add(btnThanhToan);
        
        pSouth.add(pFooterBottom, BorderLayout.CENTER);
        
        pWest.add(pSouth, BorderLayout.SOUTH);
        wrap.add(pWest);
        wrap.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        wrap.setBackground(Color.WHITE);
        
        add(wrap, BorderLayout.WEST); 

        // ---------------------- 3. PANEL CENTER (Bảng thực đơn) ----------------------
        JPanel pCenter = createMenuPanel(); 
        JScrollPane menuScrollPane = new JScrollPane(pCenter);
        menuScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
        menuScrollPane.setBorder(null);
        
        add(menuScrollPane, BorderLayout.CENTER); 
    }
    private JPanel createMenuPanel() {
   
		JPanel pCenter = new JPanel();
		pCenter.setLayout(new GridLayout(0, 5, 10, 10)); 
		pCenter.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		pCenter.setBackground(Color.WHITE);

        int ITEM_WIDTH = 120;
        int IMAGE_HEIGHT = 120;
        int TEXT_HEIGHT = 70;
        int ITEM_HEIGHT = IMAGE_HEIGHT + TEXT_HEIGHT; 

        for (SanPham sp : menuMap.values()) {
          
            JPanel itemPanel = new JPanel(new BorderLayout());
            itemPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
            itemPanel.setBackground(Color.WHITE);
            itemPanel.setPreferredSize(new Dimension(ITEM_WIDTH, ITEM_HEIGHT)); 

            // 1. Hình ảnh
            JLabel lblImage = new JLabel();
            lblImage.setHorizontalAlignment(SwingConstants.CENTER);
            lblImage.setPreferredSize(new Dimension(ITEM_WIDTH, IMAGE_HEIGHT)); 

            try {
         
                String hinhAnh = sp.getHinhAnh();
                
                if (hinhAnh != null && !hinhAnh.isEmpty()) {
                    ImageIcon icon = new ImageIcon(hinhAnh);
                    if (icon.getImageLoadStatus() == java.awt.MediaTracker.ERRORED) {
                         lblImage.setText("Load Error: " + hinhAnh);
                    } else {
           
                        Image image = icon.getImage().getScaledInstance(ITEM_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH);
                        lblImage.setIcon(new ImageIcon(image));
                    }
                } else {
                    lblImage.setText("No Image");
                }
            } catch (Exception ex) {
                lblImage.setText("Lỗi ảnh (Exception)");
                ex.printStackTrace();
            }
            itemPanel.add(lblImage, BorderLayout.CENTER);
            JLabel lblTenMon = new JLabel("<html><center><b>" + sp.getTenSP() + "</b><br>" + df.format(sp.getGia()) + "₫</center></html>");
            lblTenMon.setHorizontalAlignment(SwingConstants.CENTER);
            lblTenMon.setPreferredSize(new Dimension(ITEM_WIDTH, TEXT_HEIGHT)); 
            lblTenMon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            itemPanel.add(lblTenMon, BorderLayout.SOUTH);
            itemPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    addMonAnToTable(sp);
                }
              
                @Override
                public void mouseEntered(MouseEvent e) {
                    itemPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
                    itemPanel.repaint();
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    itemPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
                    itemPanel.repaint();
                }
            });

            pCenter.add(itemPanel);
        }
        return pCenter;
    }
    
    private Map<String, SanPham> getMonAnList() {
        java.util.HashMap<String, SanPham> map = new java.util.HashMap<>(); 
        for (SanPham sp : sanPhamDAO.getAllSanPham()) {
            map.put(sp.getMaSP(), sp);
        }
        return map;
    }

    private void addMonAnToTable(SanPham sp) {

        for (int i = 0; i < orderModel.getRowCount(); i++) {
            String maSPInTable = (String) orderModel.getValueAt(i, 0);
            if (maSPInTable.equals(sp.getMaSP())) {
                int currentSL = (int) orderModel.getValueAt(i, 2);
                int newSL = currentSL + 1;
                double newThanhTien = newSL * sp.getGia();
                
                orderModel.setValueAt(newSL, i, 2);
                orderModel.setValueAt(df.format(newThanhTien), i, 4); 
                recalculateTotal();
                return;
            }
        }
        Vector<Object> rowData = new Vector<>();
        rowData.add(sp.getMaSP()); 
        rowData.add(sp.getTenSP()); 
        rowData.add(1); 
        rowData.add(df.format(sp.getGia())); 
        rowData.add(df.format(sp.getGia())); 
        orderModel.addRow(rowData);
        recalculateTotal();
    }

    private void recalculateTotal() {
        double total = 0;
        for (int i = 0; i < orderModel.getRowCount(); i++) {
            String thanhTienStr = (String) orderModel.getValueAt(i, 4);
            try {
                double thanhTien = df.parse(thanhTienStr).doubleValue();
                total += thanhTien;
            } catch (java.text.ParseException e) {
                System.err.println("Lỗi parse thành tiền: " + thanhTienStr);
            }
        }
        lblTongTien.setText("Tổng tiền: " + df.format(total) + " ₫");
    }
    
    private void setupEvents() {
        btnTienMat.addActionListener(e -> {
            phuongThucThanhToan = "Tiền mặt";
            btnTienMat.setBackground(GREEN_SUCCESS);
            btnChuyenKhoan.setBackground(new Color(224,224,224));
        });
        
        btnChuyenKhoan.addActionListener(e -> {
            phuongThucThanhToan = "Chuyển khoản";
            btnChuyenKhoan.setBackground(GREEN_SUCCESS);
            btnTienMat.setBackground(new Color(224,224,224));
        });
        btnLuu.addActionListener(e -> {
            dispose();
        });
        btnThanhToan.addActionListener(e -> {
            hienThiChiTietDonDatBan(); 
        });

    }
    private void hienThiChiTietDonDatBan() {
        // 1. Kiểm tra đơn hàng rỗng
        if (orderModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món trước khi thanh toán.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int soLuongNguoiDatCoc = 0;
        if (this.banHienTai != null) {
          
            soLuongNguoiDatCoc = this.banHienTai.getSucChua(); 
        }
        
     
        if (soLuongNguoiDatCoc < 0) {
            soLuongNguoiDatCoc = 0;
        }
        String maKHHienTai = "KHL00"; 

        try {
            DonDatBan ddb = ddbDAO.getDonDatBanActiveByMaBan(this.maBan);
            
            if (ddb != null && ddb.getMaKH() != null) {
                maKHHienTai = ddb.getMaKH().getMaKH();
                this.tenKhachHang = ddb.getMaKH().getTenKH();
                this.sdtKhachHang = ddb.getMaKH().getsDT();
            
            } else {
                System.out.println("Không tìm thấy Đơn Đặt Bàn đang hoạt động. Dùng Mã KH mặc định: " + maKHHienTai);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi truy vấn Đơn Đặt Bàn: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JFrame ownerFrame = (JFrame) SwingUtilities.getWindowAncestor(this.getParent());
        JDialog thanhToanDialog = new JDialog(ownerFrame, "THANH TOÁN HÓA ĐƠN BÀN " + this.maBan, true);
        boolean isChuyenKhoan = phuongThucThanhToan.equals("Chuyển khoản");
        String maNVHienTai = "NV001";
        ChiTietHoaDonSanPham_GUI chiTietPanel = new ChiTietHoaDonSanPham_GUI(
                this,                         
                orderModel,                    
                isChuyenKhoan,                  
                this.banHienTai.getMaBan(),    
                maNVHienTai,                    
                maKHHienTai                     
            );
        thanhToanDialog.add(chiTietPanel);
        thanhToanDialog.pack();
        thanhToanDialog.setLocationRelativeTo(ownerFrame);
        thanhToanDialog.setVisible(true);
    }
    public void clearOrderTable1() {
		DefaultTableModel tblModel = (DefaultTableModel) table.getModel();
		tblModel.setRowCount(0);
		recalculateTotal();
	}
    
    class ButtonEditor extends javax.swing.AbstractCellEditor implements javax.swing.table.TableCellEditor, ActionListener {

        private final ButtonPanel editorComponent;
        private final JTable table;
        private int row, col;
        private Object originalValue;

        public ButtonEditor(JTable table) {
            this.table = table;
            this.editorComponent = new ButtonPanel();

            editorComponent.getPlusButton().addActionListener(this);
            editorComponent.getMinusButton().addActionListener(this);
        }

        @Override
        public java.awt.Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            this.col = column;
            this.originalValue = value; 
            int qty = 0;
            try {
                qty = Integer.parseInt(value.toString());
            } catch (Exception e) {
                qty = 0;
            }
            editorComponent.setQuantity(qty);
            editorComponent.setBackground(table.getSelectionBackground());
            return editorComponent;
        }

        @Override
        public Object getCellEditorValue() {
            return Integer.parseInt(editorComponent.lblSL.getText());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int currentSL = 0;
            try {
                currentSL = Integer.parseInt(originalValue.toString());
            } catch (Exception ex) {
                currentSL = 1; 
            }
            
            double donGia = 0;
            try {
                 donGia = df.parse(table.getValueAt(row, 3).toString()).doubleValue();
            } catch (Exception ex) {
                 donGia = 0;
            }


            if (e.getSource() == editorComponent.getPlusButton()) {
                currentSL++;
            } else if (e.getSource() == editorComponent.getMinusButton()) {
                currentSL--;
                if (currentSL <= 0) {
                    int confirm = JOptionPane.showConfirmDialog(table, 
                        "Bạn có muốn xóa món này khỏi hóa đơn không?", 
                        "Xác nhận", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        ((DefaultTableModel) table.getModel()).removeRow(row);
                        recalculateTotal(); 
                       
                        fireEditingCanceled(); 
                        return;
                    } else {
                        currentSL = 1;
                    }
                }
            }
            table.setValueAt(currentSL, row, 2);
            double newThanhTien = currentSL * donGia;
            table.setValueAt(df.format(newThanhTien), row, 4);
            recalculateTotal(); 
            editorComponent.setQuantity(currentSL);
            fireEditingStopped();
        }
    }
    
    
    private JButton createIconButton(String text, Color bgColor, Color fgColor, Dimension size) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setPreferredSize(size);
        button.setFont(new Font("Segoe UI", Font.BOLD, 20)); 
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        button.setFocusPainted(false);
        return button;
    }
    
    class RoundedButton extends JButton {
        private Color backgroundColor;
        private Color hoverColor;

        public RoundedButton(String text, Color bgColor, Dimension size) { 
            super(text);
            this.backgroundColor = bgColor;
            this.hoverColor = bgColor.darker();
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setPreferredSize(size); 
            setBackground(bgColor);
            setForeground(Color.WHITE);

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
        
        public RoundedButton(String text, Color bgColor) {
            this(text, bgColor, new Dimension(210, 25)); 
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        public void updateUI() {
            setUI(new javax.swing.plaf.basic.BasicButtonUI());
        }
    }
    
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
	    class RoundedLabel extends JLabel {
	        private Color backgroundColor;
	        private int cornerRadius = 20; 

	        public RoundedLabel(String text, Color bgColor, int horizontalAlignment) {
	            super(text, horizontalAlignment);
	            this.backgroundColor = bgColor;
	            setOpaque(false); 
	            setFont(new Font("Segoe UI", Font.BOLD, 21));
	            setForeground(Color.WHITE);
	            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); 
	        }

	        public RoundedLabel(String text, Color bgColor) {
	            this(text, bgColor, SwingConstants.LEFT);
	        }

	        @Override
	        protected void paintComponent(Graphics g) {
	            Graphics2D g2 = (Graphics2D) g.create();
	            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	            g2.setColor(backgroundColor);
	            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
	            super.paintComponent(g2);
	            
	            g2.dispose();
	        }
	        
	        public void setBackgroundColor(Color backgroundColor) {
	            this.backgroundColor = backgroundColor;
	            repaint();
	        }
	    }
	    class ButtonPanel extends JPanel {
	        private final JButton btnMinus;
	        private final JButton btnPlus;
	        private final JLabel lblSL;
	        
	        public ButtonPanel() {
	            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2)); 
	            setOpaque(true);
	            
	            // Nút giảm
	            btnMinus = new JButton("-");
	            btnMinus.setPreferredSize(new Dimension(30, 30));
	            btnMinus.setFont(new Font("Segoe UI", Font.BOLD, 14));
	            btnMinus.setBackground(new Color(240, 240, 240));
	            btnMinus.setFocusPainted(false);
	            btnMinus.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
	            
	            // Label hiển thị số lượng
	            lblSL = new JLabel("1");
	            lblSL.setPreferredSize(new Dimension(40, 30));
	            lblSL.setHorizontalAlignment(SwingConstants.CENTER);
	            lblSL.setFont(new Font("Segoe UI", Font.BOLD, 14));
	            
	            // Nút tăng
	            btnPlus = new JButton("+");
	            btnPlus.setPreferredSize(new Dimension(30, 30));
	            btnPlus.setFont(new Font("Segoe UI", Font.BOLD, 14));
	            btnPlus.setBackground(new Color(240, 240, 240));
	            btnPlus.setFocusPainted(false);
	            btnPlus.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
	            
	            add(btnMinus);
	            add(lblSL);
	            add(btnPlus);
	        }
	        
	        public JButton getMinusButton() {
	            return btnMinus;
	        }

	        public JButton getPlusButton() {
	            return btnPlus;
	        }
	        
	        public void setQuantity(int quantity) {
	            lblSL.setText(String.valueOf(quantity));
	        }
	    }
	    class ButtonRenderer extends ButtonPanel implements javax.swing.table.TableCellRenderer {
	        
	        public ButtonRenderer() {
	            setOpaque(true);
	        }

	        @Override
	        public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	            int quantity = 1;
	            if (value != null) {
	                try {
	                    quantity = Integer.parseInt(value.toString()); 
	                } catch (NumberFormatException e) {
	                    quantity = 1; 
	                }
	            }
	            setQuantity(quantity);
	            return this;
	        }
	    }
}