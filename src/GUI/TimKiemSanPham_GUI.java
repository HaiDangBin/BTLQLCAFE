package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
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


import DAO.SanPham_DAO; 
import Entity.SanPham;
import GUI.SanPham_GUI.SelectedRowRenderer;
import connectDB.DBconnection; 

public class TimKiemSanPham_GUI extends JPanel implements ActionListener {
    private JPanel pCen;
    private Box bx1;
    private Box pnA;
    private JTextField txtMaMon;
    private JTextField txtTenMon;
    private RoundedButton btnTim; 
    private DefaultTableModel model;
    private JTable table;
    private SanPham_DAO dao = new SanPham_DAO();
    private DecimalFormat df = new DecimalFormat("#,### VND");
	private RoundedButton btnQuayLai;
	private RoundedButton btnLamMoi;
	private RoundedButton btnXemMon;
	private RoundedButton btnDatMon;
	private JComboBox<String> comboBoxLoai;

    public TimKiemSanPham_GUI() {
 
        DBconnection.getInstance().connect();

        setLayout(new BorderLayout());
        JPanel pNorth = new JPanel();
        pNorth.setBackground(new Color(204, 102, 0));
        JLabel lbltitle = new JLabel("TÌM KIẾM SẢN PHẨM", SwingConstants.CENTER);
        lbltitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lbltitle.setForeground(Color.WHITE);
        pNorth.add(lbltitle);
        add(pNorth, BorderLayout.NORTH);

        add(pCen = new JPanel(new BorderLayout()));
        pCen.add(bx1 = new Box(BoxLayout.Y_AXIS), BorderLayout.NORTH);
        pCen.setBackground(new Color(255, 255, 255));
        pnA = new Box(BoxLayout.X_AXIS);
        JLabel maMon = new JLabel("Mã sản phẩm:");
        maMon.setPreferredSize(new Dimension(130, 30));
        maMon.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtMaMon = new JTextField();
        JLabel tenMon = new JLabel("Tên sản phẩm:");
        tenMon.setPreferredSize(new Dimension(130, 30));
        tenMon.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtTenMon = new JTextField();

        btnTim = new RoundedButton("Tìm", new Color(0, 0, 204)); 
        
        JLabel loaiMon = new JLabel("Loại:");
        loaiMon.setFont(new Font("Segoe UI", Font.BOLD, 18));
        loaiMon.setPreferredSize(new Dimension(90, 30));
        String[] loai = {"Trống","Caffe", "Trà sữa", "Sinh tố", "Bánh ngọt & ăn kèm", "Đá xay", "Nước ép", "Soda", "Trà trái cây"};
        comboBoxLoai = new JComboBox<>(loai);
        comboBoxLoai.setSelectedIndex(0);
        comboBoxLoai.setPreferredSize(new Dimension(140, 30));

        pnA.setBorder(BorderFactory.createEmptyBorder(20, 5, 20, 5));
        pnA.add(maMon);
        pnA.add(txtMaMon);
        pnA.add(Box.createHorizontalStrut(20));
        pnA.add(tenMon);
        pnA.add(txtTenMon);
        pnA.add(Box.createHorizontalStrut(15));
        pnA.add(btnTim);
        pnA.add(Box.createHorizontalStrut(15));
        pnA.add(loaiMon);
        pnA.add(comboBoxLoai);
        bx1.add(pnA);
        
        // Bảng
        model = new DefaultTableModel(new Object[]{"Mã sản phẩm", "Tên sản phẩm", "Mô tả", "Đơn giá", "Số lượng", "Loại sản phẩm", "Hình ảnh"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 6) { 
                    return ImageIcon.class; 
                }
                return super.getColumnClass(columnIndex);
            }
            
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setPreferredSize(new Dimension(header.getWidth(), 30));
        SelectedRowRenderer renderer = new SelectedRowRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
 
            if (i != 6) {
                table.getColumnModel().getColumn(i).setCellRenderer(renderer);
            } 
        }
        table.getColumnModel().getColumn(2).setPreferredWidth(350);
        table.getColumnModel().getColumn(5).setPreferredWidth(20);
        table.setRowHeight(60);
        pCen.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 5));
        pCen.add(scrollPane, BorderLayout.CENTER);

        JPanel pSouth = new JPanel();
        pSouth.setLayout(new BorderLayout());
        JPanel pnB = new JPanel();
        pnB.setLayout(new BoxLayout(pnB, BoxLayout.X_AXIS));
        btnLamMoi = new RoundedButton("Làm mới", new Color(96, 96, 96));
        pnB.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        pnB.add(Box.createHorizontalStrut(20));
        pnB.add(btnLamMoi);
        pSouth.add(pnB);
        pnB.setBackground(new Color(255, 255, 255));
        pSouth.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(pSouth, BorderLayout.SOUTH);
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        wrap.add(pCen, BorderLayout.CENTER);
        add(wrap, BorderLayout.CENTER);    
        setSize(1000, 500);    
        setVisible(true);

        
        btnTim.addActionListener(this);
        btnLamMoi.addActionListener(this);
        comboBoxLoai.addActionListener(this);
        
        loadAllDataToTable(); 
    }

    private void loadAllDataToTable() {
        model.setRowCount(0);
        for (SanPham sp : dao.getAllSanPham()) {
            String tenLoai = (sp.getMaLoai() != null) ? sp.getMaLoai().getTenLoai() : "Chưa phân loại";
           
            ImageIcon iconAnh = getMonAnImage(sp);
            
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
    private ImageIcon getMonAnImage(SanPham sp) {
        ImageIcon iconAnh = null;
        try {
            String imagePath = sp.getHinhAnh();
            String fileNameOnly = imagePath.contains("/") ? imagePath.substring(imagePath.lastIndexOf('/') + 1) : imagePath;
            Image image = new ImageIcon("images/" + fileNameOnly).getImage(); 
            Image scaledImage = image.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            iconAnh = new ImageIcon(scaledImage);
            
        } catch (Exception e) {
            System.err.println("Lỗi tải ảnh cho sản phẩm " + sp.getMaSP() + ": " + e.getMessage());
            iconAnh = null; 
        }
        return iconAnh;
    }
    
 // Phương thức tìm kiếm
    private void timKiemMonAn() {
        String maTim = txtMaMon.getText().trim();
        String tenTim = txtTenMon.getText().trim();
        String tenLoai = (String) comboBoxLoai.getSelectedItem(); 
        String maLoaiTim = mapCodeToLoaiSP(tenLoai);
        model.setRowCount(0);
        ArrayList<SanPham> dsKetQuaTheoLoai;
        if (maLoaiTim.isEmpty() || tenLoai.equals("Tất cả")) {
            dsKetQuaTheoLoai = dao.getAllSanPham(); 
        } else {
            dsKetQuaTheoLoai = dao.getMonAnByLoaiMon(maLoaiTim); 
        }
        ArrayList<SanPham> dsLocCuoi = new ArrayList<>();        
        for (SanPham mon : dsKetQuaTheoLoai) {
            boolean matchMa = maTim.isEmpty() || mon.getMaSP().toLowerCase().contains(maTim.toLowerCase());
            boolean matchTen = tenTim.isEmpty() || mon.getTenSP().toLowerCase().contains(tenTim.toLowerCase());
            if (matchMa && matchTen) {
                dsLocCuoi.add(mon);
            }
        }
        for (SanPham sp : dsLocCuoi) {
        	ImageIcon icon = getMonAnImage(sp);
            model.addRow(new Object[]{
                sp.getMaSP(),
                sp.getTenSP(),
                sp.getMoTa(),
                df.format(sp.getGia()),
                sp.getSoLuong(),
                sp.getMaLoai().getTenLoai(),
                icon
            });
        }

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "⚠️ Không tìm thấy món ăn phù hợp với tiêu chí tìm kiếm!");
        }
    }
    private void lamMoi() {
    	txtMaMon.setText("");
    	txtTenMon.setText("");
    	table.clearSelection();
    	comboBoxLoai.setSelectedIndex(0);
    	loadAllDataToTable();
    }
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnTim || e.getSource() == comboBoxLoai) {
            timKiemMonAn();
        } else if (e.getSource() == btnLamMoi) {
        	lamMoi();
        }
    }
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
            setPreferredSize(new Dimension(text.equals("Tìm") ? 90 : 120, 30)); 
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