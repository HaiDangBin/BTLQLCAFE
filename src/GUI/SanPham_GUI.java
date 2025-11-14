package GUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import DAO.SanPham_DAO;
import Entity.LoaiSanPham;
import Entity.SanPham;
import connectDB.DBconnection;

public class SanPham_GUI extends JPanel implements ActionListener {

    
    private JPanel pCen;
    private Box bx1;
    private Box pnA;
   
    private Box pnB;
   
    private DefaultTableModel model;
    private JTable table;
    private JButton btnLamMoi;
    private SanPham_DAO dao = new SanPham_DAO();
    private DecimalFormat df = new DecimalFormat("#,### VND");
    private boolean isUpdatingFields = false;
	private RoundedButton btnThemSP;
	private RoundedButton btnSuaSP;
	private RoundedButton btnXoaSP;
	private RoundedButton btnCapNhatSP;
	private JTextField txtMaSP;
	private JTextField txtTenSP;
	private RoundedButton btnThemLoai; 

    public SanPham_GUI() {
        DBconnection.getInstance().connect();  
        // ===== Setup North Panel =====
        setLayout(new BorderLayout());
        JPanel pNorth = new JPanel();
        pNorth.setBackground(new Color(255, 128, 0));
        JLabel lbltitle = new JLabel("QUẢN LÝ SẢN PHẨM", SwingConstants.CENTER);
        lbltitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lbltitle.setForeground(Color.WHITE);
        pNorth.add(lbltitle);
        add(pNorth, BorderLayout.NORTH);

        // ===== Setup West Panel (Chức năng) =====
        JPanel pWest = new JPanel();
        Dimension btnSize = new Dimension(200, 60);

        btnThemSP = new RoundedButton("Thêm sản phẩm", new Color(153, 76, 0));
        btnThemLoai = new RoundedButton("Thêm loại sản phẩm", new Color(0,153,153));
        btnSuaSP = new RoundedButton("Sửa sản phẩm", new Color(153, 153, 0)); 
        btnXoaSP = new RoundedButton("Xóa sản phẩm", new Color(204,0,0));
        btnCapNhatSP = new RoundedButton("Cập nhật chi tiết", new Color(0, 153, 76)); 

       
        btnThemSP.setMaximumSize(btnSize);
        btnThemLoai.setMaximumSize(btnSize);
        btnSuaSP.setMaximumSize(btnSize);
        btnXoaSP.setMaximumSize(btnSize);
        btnCapNhatSP.setMaximumSize(btnSize);
        
        pWest.setLayout(new BorderLayout());
        JPanel bx = new JPanel();
        bx.setLayout(new BoxLayout(bx, BoxLayout.Y_AXIS));
        bx.add(Box.createVerticalStrut(20));
        bx.add(Box.createRigidArea(new Dimension(0, 30)));
        bx.add(btnThemSP);
        bx.add(Box.createRigidArea(new Dimension(0, 30)));
        bx.add(btnThemLoai);
        bx.add(Box.createRigidArea(new Dimension(0,30)));
        bx.add(btnSuaSP);
        bx.add(Box.createRigidArea(new Dimension(0, 30)));
        bx.add(btnXoaSP);
        bx.add(Box.createRigidArea(new Dimension(0, 30)));
        bx.add(btnCapNhatSP);
        bx.add(Box.createRigidArea(new Dimension(0, 20)));
        bx.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bx.setBackground(new Color(255, 255, 255));
        pWest.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pWest.add(bx);
        add(pWest, BorderLayout.WEST);

        // ===== Setup Center Panel (Tìm kiếm và Bảng) =====
        pCen = new JPanel(new BorderLayout());
        pCen.add(bx1 = new Box(BoxLayout.Y_AXIS), BorderLayout.NORTH);
        pCen.setBackground(new Color(255, 255, 255));
        
        pnA = new Box(BoxLayout.X_AXIS);
        JLabel maMon = new JLabel("Mã sản phẩm:");
        maMon.setFont(new Font("Segoe UI", Font.BOLD, 18));
        maMon.setPreferredSize(new Dimension(180, 30));
        txtMaSP = new JTextField();
        txtMaSP.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        
        pnA.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        pnA.add(maMon);
        pnA.add(txtMaSP);
        pnA.add(Box.createHorizontalStrut(60));
        bx1.add(pnA);

        pnB = new Box(BoxLayout.X_AXIS);
        JLabel tenMon = new JLabel("Tên sản phẩm:");
        tenMon.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tenMon.setPreferredSize(new Dimension(180, 30));
        txtTenSP = new JTextField();
        txtTenSP.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        btnLamMoi = new RoundedButton("Làm mới (F7)", Color.black);
        btnLamMoi.setPreferredSize(new Dimension(140,30));
        
        pnB.setBorder(BorderFactory.createEmptyBorder(10, 50, 30, 50));
        pnB.add(tenMon);
        pnB.add(txtTenSP);
        pnB.add(Box.createHorizontalStrut(80));
        pnB.add(btnLamMoi);
        bx1.add(pnB);
        pCen.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(pCen);

        // Bảng dữ liệu
        model = new DefaultTableModel(new Object[]{"Mã sản phẩm", "Tên sản phẩm", "Mô tả", "Đơn giá","Số lượng", "Loại sản phẩm", "Hình ảnh"}, 0) {
            private static final long serialVersionUID = 1L;
            
            // Phương thức quan trọng: Báo cho JTable biết kiểu dữ liệu của mỗi cột
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 6) { // Cột "Hình ảnh" là cột thứ 5 (index 0 là cột 1)
                    return ImageIcon.class; // Trả về ImageIcon
                }
                return super.getColumnClass(columnIndex);
            }
            
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    isUpdatingFields = true; 
                    
                    txtMaSP.setText(model.getValueAt(row, 0).toString());
                    txtTenSP.setText(model.getValueAt(row, 1).toString()); 
                    txtMaSP.setEditable(false); 
                    
                    isUpdatingFields = false;
                }
                if (e.getClickCount() == 1 && table.rowAtPoint(e.getPoint()) == -1) {
                    table.clearSelection();
                    //clearInputFields();
                }
            }
        });
        
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
        JScrollPane scrollPane = new JScrollPane(table);
        table.setRowHeight(60);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(150, 235, 215));
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setPreferredSize(new Dimension(header.getWidth(), 30));
        
        SelectedRowRenderer renderer = new SelectedRowRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
            // CHỈ ÁP DỤNG RENDERER TÙY CHỈNH CHO CÁC CỘT KHÔNG PHẢI ẢNH
            if (i != 6) { // Cột index 5 là cột "Hình ảnh"
                table.getColumnModel().getColumn(i).setCellRenderer(renderer);
            } 
            // CỘT ẢNH (INDEX 5) SẼ TỰ ĐỘNG SỬ DỤNG DEFAULT RENDERER CHO ImageIcon
            // Do bạn đã định nghĩa getColumnClass(5) là ImageIcon.class
        }
        
        table.getColumnModel().getColumn(2).setPreferredWidth(350);
        table.getColumnModel().getColumn(6).setPreferredWidth(20);
        pCen.add(scrollPane, BorderLayout.CENTER);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));
        wrap.add(pCen, BorderLayout.CENTER);
        add(wrap, BorderLayout.CENTER);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int) (screenSize.getWidth() * 0.8), (int) (screenSize.getHeight() * 0.8));

        // Đăng ký sự kiện
        btnThemSP.addActionListener(this);
        btnXoaSP.addActionListener(this);
        btnCapNhatSP.addActionListener(this); 
        btnSuaSP.addActionListener(this); 
        btnLamMoi.addActionListener(this);

        addSearchListener();
        loadAllDataToTable(); 
    }

    private void loadAllDataToTable() {
        model.setRowCount(0);
        for (SanPham sp : dao.getAllSanPham()) {
            String tenLoai = (sp.getMaLoai() != null) ? sp.getMaLoai().getTenLoai() : "Chưa phân loại";
            
            // ********** PHẦN CẦN SỬA ĐỔI **********
            ImageIcon iconAnh = null;
            try {
                String imagePath = sp.getHinhAnh();
                
                
                String fileNameOnly = imagePath.replace("images/", ""); 
                
                Image image = new ImageIcon("images/" + fileNameOnly).getImage(); 
                
                Image scaledImage = image.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                iconAnh = new ImageIcon(scaledImage);
                
            } catch (Exception e) {
                iconAnh = null; 
            }
            // ***************************************
            
            model.addRow(new Object[]{
                sp.getMaSP(),
                sp.getTenSP(),
                sp.getMoTa(),
                df.format(sp.getGia()),
                sp.getSoLuong(),
                tenLoai,
                iconAnh 
            });
        }
    }
//
    @Override
    public void actionPerformed(ActionEvent e) {
        // [C]REATE - Thêm món ăn mới (HIỂN THỊ FORM NHẬP LIỆU)
        if (e.getSource() == btnThemSP) {
            SanPhamCRUD dialog = new SanPhamCRUD(this, null, false, "Thêm sản phẩm mới");
            dialog.setVisible(true); 
            // loadDataToTable() được gọi trong dialog sau khi thành công
            
        // [D]ELETE - Xóa món ăn
        } else if (e.getSource() == btnXoaSP) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String maSP = model.getValueAt(selectedRow, 0).toString();
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa món ăn '" + maSP + "'? (Điều này sẽ xóa vĩnh viễn dữ liệu liên quan)", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        boolean success = dao.xoaSP(maSP);
                        if (success) {
                            JOptionPane.showMessageDialog(this, "✅ Xóa món ăn '" + maSP + "' thành công!");
                            
                            clearInputFields();
                            loadAllDataToTable();
                        } else {
                            JOptionPane.showMessageDialog(this, "❌ Không thể xóa món ăn '" + maSP + "'. Có thể có lỗi Khóa ngoại chưa được xử lý triệt để!");
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "⚠️ Vui lòng chọn một món ăn từ bảng để xóa!");
            }
            
        // [U]PDATE (Chi tiết)
        } else if (e.getSource() == btnCapNhatSP) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String maSP = model.getValueAt(selectedRow, 0).toString();
                SanPham sp = dao.getSanPhamByMaSP(maSP); 
                if (sp != null) {
                    SanPhamCRUD dialog = new SanPhamCRUD(this, sp, true, "Cập nhật chi tiết món ăn");
                    
                    isUpdatingFields = true;
                    
                    dialog.txtMaSP.setText(sp.getMaSP());
                    dialog.txtTenSP.setText(sp.getTenSP());
                    dialog.txtMoTa.setText(sp.getMoTa());
                    
                    // Loại bỏ định dạng VND và dấu phẩy để hiển thị giá trị Double
                    dialog.txtDonGia.setText(String.valueOf(sp.getGia()));
                    dialog.txtSoLuong.setText(""+sp.getSoLuong());
                    
                    // Set ComboBox
                    String loaiMonHienTai = mapCodeToLoaiSP(sp.getMaLoai().getMaLoai());
                    String[] loaiOptions = {"Caffe", "Trà sữa", "Sinh tố", "Bánh ngọt & Ăn kèm", "Đá xay", "Nước ép", "Soda", "Trà trái cây"};
                    for (int i = 0; i < loaiOptions.length; i++) {
                        if (loaiMonHienTai.equals(loaiOptions[i])) {
                            dialog.cmbLoaiMon.setSelectedIndex(i);
                            break;
                        }
                    }
                    
                    isUpdatingFields = false;
                    
                    dialog.setVisible(true);
                    clearInputFields();
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Không tìm thấy món ăn để cập nhật!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "⚠️ Vui lòng chọn một món ăn từ bảng để cập nhật chi tiết!");
            }
            
        // [U]PDATE (Sửa tên nhanh)
        } else if (e.getSource() == btnSuaSP) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String maMonHienTai = model.getValueAt(selectedRow, 0).toString();
                String tenMonNew = txtTenSP.getText().trim();
                
                if (!maMonHienTai.isEmpty() && !tenMonNew.isEmpty() && !tenMonNew.equals(model.getValueAt(selectedRow, 1).toString())) {
                    int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn sửa tên sản phẩm '" + maMonHienTai + "' thành '" + tenMonNew + "'?", "Xác nhận sửa", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            SanPham originalMon = dao.getSanPhamByMaSP(maMonHienTai);
                            if (originalMon != null) {
                                // Sử dụng lại các thông tin cũ, chỉ thay đổi tên
                                SanPham updatedMon = new SanPham(originalMon.getMaSP(), tenMonNew, originalMon.getMoTa(), originalMon.getGia(), originalMon.getSoLuong(), originalMon.getMaLoai(), originalMon.getHinhAnh());
                                boolean success = dao.capNhat(updatedMon); 
                                
                                if (success) {
                                    JOptionPane.showMessageDialog(this, "✅ Sửa tên món ăn thành công!");
                                    loadAllDataToTable();
                                    clearInputFields();
                                } else {
                                    JOptionPane.showMessageDialog(this, "❌ Không thể sửa tên món ăn (lỗi hệ thống)!");
                                }
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "⚠️ Vui lòng sửa tên món ăn trong ô nhập liệu trước khi nhấn Sửa!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "⚠️ Vui lòng chọn một món ăn từ bảng để sửa tên!");
            }
        }
        // Làm mới
        else if(e.getSource() == btnLamMoi) {
        	clearInputFields();
        	loadAllDataToTable();
        }
       
    }
    private String mapCodeToLoaiSP(String maLoai) {
    	switch (maLoai) {
    		case "Caffe": return "LSP01";
    		case "Trà sữa": return "LSP02";
    		case "Sinh tố": return "LSP03";
    		case "Bánh Ngọt & Ăn Kèm": return "LSP04";
    		case "Đá xay": return "LSP05";
    		case "Nước ép": return "LSP06";
    		case "Soda": return "LSP07";
    		case "Trà trái cây": return "LSP08";
    		default: return "";
	}
    }


	//
    private void clearInputFields() {
        txtMaSP.setText("");
        txtTenSP.setText("");
        table.clearSelection();
        txtMaSP.setEditable(true);
   }
    private void addSearchListener() {
        txtMaSP.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { timKiemTheoMa(); }
            @Override
            public void removeUpdate(DocumentEvent e) { timKiemTheoMa(); }
            @Override
            public void changedUpdate(DocumentEvent e) { timKiemTheoMa(); }
        });
        txtTenSP.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { timKiemTheoTen(); }
            @Override
            public void removeUpdate(DocumentEvent e) { timKiemTheoTen(); }
            @Override
            public void changedUpdate(DocumentEvent e) { timKiemTheoTen(); }
        });
    }
    private void timKiemTheoMa() {
        if (isUpdatingFields) return; 
        String maTimKiem = txtMaSP.getText().trim().toLowerCase();
        if (maTimKiem.isEmpty() && txtTenSP.getText().trim().isEmpty()) {
            loadAllDataToTable(); 
            return;
        }
        if (!maTimKiem.isEmpty()) {
             locDuLieu(maTimKiem, true);
        }
    }
    
    private void timKiemTheoTen() {
        if (isUpdatingFields) return; 
        String tenTimKiem = txtTenSP.getText().trim().toLowerCase();
        if (tenTimKiem.isEmpty() && txtMaSP.getText().trim().isEmpty()) {
            loadAllDataToTable(); 
            return;
        }
        if (!tenTimKiem.isEmpty() && txtMaSP.getText().trim().isEmpty()) {
             locDuLieu(tenTimKiem, false);
        }
    }
    
    private void locDuLieu(String chuoiTimKiem, boolean timTheoMa) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        
        ArrayList<SanPham> dsAll = dao.getAllSanPham();
        for (SanPham sp : dsAll) {
        	ImageIcon iconAnh = null;
            try {
                String imagePath = sp.getHinhAnh();
                
                
                String fileNameOnly = imagePath.replace("images/", ""); 
                
                Image image = new ImageIcon("images/" + fileNameOnly).getImage(); 
                
                Image scaledImage = image.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                iconAnh = new ImageIcon(scaledImage);
                
            } catch (Exception e) {
                iconAnh = null; 
            }
            String target = timTheoMa ? sp.getMaSP().toLowerCase() : sp.getTenSP().toLowerCase();
            
            if (target.contains(chuoiTimKiem)) {
                model.addRow(new Object[] {
                    sp.getMaSP(),
                    sp.getTenSP(),
                    sp.getMoTa(),
                    df.format(sp.getGia()),
                    sp.getSoLuong(),
                    sp.getMaLoai().getTenLoai(),
                    iconAnh
                });
            }
        }
    }
   
//    // Custom Renderer để tô màu dòng được chọn
  class SelectedRowRenderer extends DefaultTableCellRenderer {
       private static final long serialVersionUID = 1L;

       @Override
       public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {            
           Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
           Color selectedColor = new Color(135, 206, 250); 
           
           if (isSelected) {
               c.setBackground(selectedColor); 
               c.setForeground(Color.BLACK);
           } else {
               c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240)); 
               c.setForeground(Color.BLACK);
            }
            return c;
        }
    }
    
//    // Inner class RoundedButton
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
            setPreferredSize(new Dimension(200, 25));
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
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        public void updateUI() {
            setUI(new javax.swing.plaf.basic.BasicButtonUI());
        }
    }
    
//    // Inner class RoundedTextField
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
//    }
//    
        
        
    }
    class SanPhamCRUD extends JDialog {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private SanPham_DAO dao = new SanPham_DAO();
        public JTextField txtMoTa;
        public JTextField txtDonGia;
        private JButton btnXoaTrang;
        private JButton btnThoat;
        public JComboBox<String> cmbLoaiMon;
        private JButton btnAction;
        private boolean isUpdateMode;
        private JButton btnChonAnh;
        private JLabel lblPreview;
        private String hinhAnhPath ="";
		private JTextField txtMaSP;
		private JTextField txtTenSP;
		private JComboBox cmbLoaiSP;
		private JTextField txtSoLuong;
		private String tenLoai;
		private SanPham sanPhamCu;
		
		private SanPham_GUI parentGUI;
        
        public SanPhamCRUD(SanPham_GUI parentGUI, SanPham sp,  boolean isUpdate, String title) {
            this.isUpdateMode = isUpdate;
            this.parentGUI = parentGUI; // Phải lưu lại parentGUI để gọi loadAllDataToTable()
            this.sanPhamCu = sp; // Lưu đối tượng Sản phẩm cũ
            this.isUpdateMode = isUpdate;
            
            setupUI();
            setupEvents();
           
            if (isUpdateMode) {
                loadSanPhamCu(sp); // GỌI HÀM LOAD DỮ LIỆU CŨ
                btnAction.setText("Cập nhật");
                ((RoundedButton)btnAction).setBackground(new Color(0, 153, 76)); 
                txtMaSP.setEditable(false); 
            } else {
                btnAction.setText("Thêm món");
                ((RoundedButton)btnAction).setBackground(new Color(0, 102, 0));
            }
        }

        private void setupUI() {
            setLayout(new BorderLayout(10, 10));
            setSize(700, 450);
            setLocationRelativeTo(getParent());
            setResizable(false);

            JPanel pInput = new JPanel(new BorderLayout());
            JPanel bx = new JPanel();
            bx.setLayout(new BoxLayout(bx, BoxLayout.Y_AXIS));
            pInput.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

            Dimension sizebtn = new Dimension(150, 40);
            Font font = new Font("Segoe UI", Font.PLAIN, 18);
            Font fontlbl = new Font("Segoe UI", Font.BOLD, 18);
            
            String[] loaiMonOptions = {"Caffe", "Trà sữa", "Sinh tố", "Bánh ngọt & Ăn kèm", "Đá xay", "Nước ép", "Soda", "Trà trái cây"};
            
            txtMaSP = new JTextField();
            txtTenSP = new JTextField();
            txtMoTa = new JTextField();
            txtDonGia = new JTextField();
            txtSoLuong = new JTextField();
            cmbLoaiSP = new JComboBox<>(loaiMonOptions);
            
            addField(bx, "Mã món ăn:", txtMaSP, sizebtn, fontlbl, font);
            addField(bx, "Tên món ăn:", txtTenSP, sizebtn, fontlbl, font);
            addField(bx, "Mô tả:", txtMoTa, sizebtn, fontlbl, font);
            addField(bx, "Đơn giá:", txtDonGia, sizebtn, fontlbl, font);
            addField(bx, "Số lượng:", txtSoLuong, sizebtn, fontlbl, font);
            addField(bx, "Loại món:", cmbLoaiSP, sizebtn, fontlbl, font);
            
            JPanel panelAnh = new JPanel();
            panelAnh.setLayout(new BoxLayout(panelAnh, BoxLayout.X_AXIS));
            JLabel lblAnh = new JLabel("Hình ảnh:");
            lblAnh.setPreferredSize(new Dimension(150,40));
            lblAnh.setFont(new Font("Segoe UI", Font.BOLD, 18));
            btnChonAnh = new RoundedButton("Chọn ảnh", new Color(70, 130, 180));
            lblPreview = new JLabel("Chưa chọn", SwingConstants.CENTER);
            lblPreview.setPreferredSize(new Dimension(150, 30));
            lblPreview.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            lblPreview.setForeground(Color.GRAY);
            panelAnh.add(lblAnh);
            panelAnh.add(Box.createHorizontalStrut(40));
            panelAnh.add(btnChonAnh);
            bx.add(panelAnh);

            pInput.add(bx, BorderLayout.CENTER);
            add(pInput, BorderLayout.CENTER);

            JPanel pButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
            btnAction = new RoundedButton("Action", Color.GRAY); 
            btnXoaTrang = new RoundedButton("Xóa trắng", new Color(102, 102, 0));
            btnThoat = new RoundedButton("Thoát", new Color(102, 0, 0));
            
            Font btnFont = new Font("Segoe UI", Font.BOLD, 16);
            btnAction.setFont(btnFont);
            btnXoaTrang.setFont(btnFont);
            btnThoat.setFont(btnFont);
            
            btnXoaTrang.setBackground(new Color(255, 204, 0));
            btnThoat.setForeground(Color.WHITE);
            
            pButtons.add(btnAction);
            pButtons.add(btnXoaTrang);
            pButtons.add(btnThoat);
            add(pButtons, BorderLayout.SOUTH);
        }
        
        private void addField(JPanel parentBox, String labelText, JComponent component, Dimension size, Font lblFont, Font compFont) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            JLabel label = new JLabel(labelText);
            label.setPreferredSize(size);
            label.setFont(lblFont);
            
            if (component instanceof JTextField) {
                ((JTextField) component).setFont(compFont);
            } else if (component instanceof JComboBox) {
                ((JComboBox) component).setFont(compFont);
            }
            
            panel.add(label);
            panel.add(component);
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            parentBox.add(panel);
        }

        private void setupEvents() {
            btnThoat.addActionListener(e -> dispose());       
            btnXoaTrang.addActionListener(e -> clearFields()); 
            btnChonAnh.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Chọn hình ảnh sản phẩm");
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Ảnh (*.jpg, *.png)", "jpg", "png"));
                int result = fileChooser.showOpenDialog(this);
                
                if (result == JFileChooser.APPROVE_OPTION) {
                    
                    File selectedFile = fileChooser.getSelectedFile();
                    String absolutePath = selectedFile.getAbsolutePath();
                    String fileName = selectedFile.getName(); // 1. Lấy tên file

                    // 2. Định nghĩa đường dẫn đích (tương đối)
                    // Đường dẫn tương đối sẽ là "images/TênFile.png"
                    String destinationPath = "images" + File.separator + fileName;
                    
                    // Tạo đối tượng File cho đường dẫn đích
                    File destinationFile = new File(destinationPath);
                    
                    try {
                        // 3. THỰC HIỆN COPY FILE: Sao chép ảnh từ vị trí gốc vào thư mục "images"
                        // Sử dụng Files.copy (cần import java.nio.file.*)
                        java.nio.file.Files.copy(
                            selectedFile.toPath(), 
                            destinationFile.toPath(), 
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING // Ghi đè nếu đã tồn tại
                        );
                        
                        this.hinhAnhPath = "images/" + fileName; 
                        
                        lblPreview.setText(fileName);
                        JOptionPane.showMessageDialog(this, "✅ Đã chọn ảnh thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);

                    } catch (java.io.IOException ex) {
                        JOptionPane.showMessageDialog(this, "❌ Lỗi sao chép file ảnh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            });
            btnAction.addActionListener(e -> {
                if (isUpdateMode) {
                    capNhatSanPham();
                } else {
                    themSanPham();
                }
            });
        }
        
        private void clearFields() {
            if (!isUpdateMode) txtMaSP.setText("");
            txtTenSP.setText("");
            txtMoTa.setText("");
            txtDonGia.setText("");
            txtSoLuong.setText("");
            cmbLoaiSP.setSelectedIndex(0);
            txtMaSP.requestFocus();
        }
        private void loadSanPhamCu(SanPham sp) {
            if (sp == null) return;
            
            txtMaSP.setText(sp.getMaSP());
            txtTenSP.setText(sp.getTenSP());
            txtMoTa.setText(sp.getMoTa());
            txtDonGia.setText(String.valueOf(sp.getGia()));
            txtSoLuong.setText(String.valueOf(sp.getSoLuong()));
            
            // ✅ BƯỚC QUAN TRỌNG NHẤT: LƯU ĐƯỜNG DẪN ẢNH CŨ
            this.hinhAnhPath = sp.getHinhAnh() != null ? sp.getHinhAnh() : "";
            
            // Cập nhật hiển thị (ví dụ: hiển thị tên file ảnh hoặc ảnh)
            if (!this.hinhAnhPath.isEmpty()) {
                // Bạn có thể hiển thị tên file:
                lblPreview.setText(new File(this.hinhAnhPath).getName());
                // Hoặc load ảnh nếu bạn đã có logic đó:
                // lblPreview.setIcon(new ImageIcon(this.hinhAnhPath));
            }

            // Set ComboBox (cần một hàm ánh xạ ngược, giả định bạn có hàm này)
            String tenLoaiCu = sp.getMaLoai().getTenLoai();
            cmbLoaiSP.setSelectedItem(tenLoaiCu);
        }

        private void themSanPham() {
            try {
                 String ma = txtMaSP.getText().trim();
                 String ten = txtTenSP.getText().trim();
                 String moTa = txtMoTa.getText().trim();
                 String tenLoaiMon = (String) cmbLoaiSP.getSelectedItem();
                 String maLoai = mapLoaiMonToCode(cmbLoaiSP.getSelectedItem().toString()); 
                 String hinhAnh = hinhAnhPath;
        
                 if (ma.isEmpty() || ten.isEmpty() || moTa.isEmpty() || maLoai.isEmpty()) {
                     JOptionPane.showMessageDialog(this, "⚠️ Vui lòng nhập đầy đủ thông tin!");
                     return;
                 }
                 double donGia;
                 String giaStr = txtDonGia.getText().trim().replace(",", "").replace(" VND", "");
                 try {
                     donGia = Double.parseDouble(giaStr);
                     if (donGia <= 0) {
                         JOptionPane.showMessageDialog(this, "⚠️ Đơn giá phải là số dương hợp lệ!");
                         return;
                     }
                 } catch (NumberFormatException ex) {
                     JOptionPane.showMessageDialog(this, "⚠️ Đơn giá không hợp lệ!");
                     return;
                 }
                 int soLuong = 0;
                 String slStr = txtSoLuong.getText().trim();
                 try {
					soLuong = Integer.parseInt(slStr);
					if(soLuong <= 0) {
						JOptionPane.showMessageDialog(this, "⚠️ Số lượng phải là số dương hợp lệ!");
						return;
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(this, "⚠️ Số lượng không hợp lệ");
				}
                 
                 SanPham sp = new SanPham(ma, ten, moTa, donGia, soLuong, new LoaiSanPham(maLoai, tenLoaiMon), hinhAnh);
                 boolean success = dao.themSanPham(sp);
        
                 if (success) {
                     JOptionPane.showMessageDialog(this, "✅ Thêm món ăn thành công!");
                     dispose();
                     SanPham_GUI.this.loadAllDataToTable();
                 } else {
                     JOptionPane.showMessageDialog(this, "❌ Không thể thêm món ăn (mã món có thể đã tồn tại hoặc lỗi SQL)!");
                 }
        
             } catch (Exception ex) {
                 JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
                 ex.printStackTrace();
             }
        }

        private void capNhatSanPham() {
            try {
                 String ma = txtMaSP.getText().trim();
                 String ten = txtTenSP.getText().trim();
                 String moTa = txtMoTa.getText().trim();
                 String tenLoaiMon = (String) cmbLoaiSP.getSelectedItem();
                 String maLoai = mapLoaiMonToCode(cmbLoaiSP.getSelectedItem().toString());
                 
                 if (ma.isEmpty() || ten.isEmpty() || moTa.isEmpty() || maLoai.isEmpty()) {
                     JOptionPane.showMessageDialog(this, "⚠️ Vui lòng nhập đầy đủ thông tin!");
                     return;
                 }
                 double donGia;
                 String giaStr = txtDonGia.getText().trim().replace(",", "").replace(" VND", "");
                 try {
                     donGia = Double.parseDouble(giaStr);
                     if (donGia <= 0) {
                         JOptionPane.showMessageDialog(this, "⚠️ Đơn giá phải là số dương hợp lệ!");
                         return;
                     }
                 } catch (NumberFormatException ex) {
                     JOptionPane.showMessageDialog(this, "⚠️ Đơn giá không hợp lệ!");
                     return;
                 }
                 int soLuong = 0; // Khởi tạo
                 try {
					String slStr = txtSoLuong.getText().trim();
                    // 1. CHUYỂN ĐỔI CHUỖI SANG SỐ NGUYÊN và GÁN GIÁ TRỊ
                    soLuong = Integer.parseInt(slStr); 
                    
                    // 2. KIỂM TRA GIÁ TRỊ ĐÃ GÁN
					if( soLuong <= 0) { 
						JOptionPane.showMessageDialog(this, "⚠️ Số lượng phải là số dương hợp lệ!");
						return;
					}
				} catch (NumberFormatException e) { // SỬ DỤNG NumberFormatException
					JOptionPane.showMessageDialog(this, "⚠️ Số lượng không hợp lệ (phải là số nguyên)!");
                    return; // Đảm bảo thoát khi có lỗi format
				}

                 SanPham sp = new SanPham(ma, ten, moTa, donGia, soLuong, new LoaiSanPham(maLoai, tenLoaiMon), hinhAnhPath);
                 boolean success = dao.capNhat(sp);
        
                 if (success) {
                     JOptionPane.showMessageDialog(this, "✅ Cập nhật sản phẩm thành công!");
                     dispose();
                     SanPham_GUI.this.loadAllDataToTable();
                 } else {
                     JOptionPane.showMessageDialog(this, "❌ Không thể cập nhật sản phẩm (mã không tồn tại hoặc lỗi khác)!");
                 }
        
             } catch (Exception ex) {
                 JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
                 ex.printStackTrace();
             }
        }
        
        private String mapLoaiMonToCode(String loaiMon) {
            switch (loaiMon) {
                case "Caffe": return "LSP01";
                case "Trà sữa": return "LSP02";
                case "Sinh tố": return "LSP03";
                case "Bánh Ngọt & Ăn Kèm": return "LSP04";
                case "Đá xay": return "LSP05";
                case "Nước ép": return "LSP06";
                case "Soda": return "LSP07";
                case "Trà trái cây": return "LSP08";
                default: return "";
            }
        }
    }

}