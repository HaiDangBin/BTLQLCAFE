package STARTING;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import GUI.*;

public class Main extends JFrame implements ActionListener {
    private static final String BACKGROUND_IMAGE_PATH = "/image/trangchu.jpg"; 

    private JMenuItem mnuTrangChu, mnuKhachHang, mnuNhanVien, mnuHoaDon, mnuKhuyenMai, mnuSanPham, 
            mnuTroGiup, mnuThoat, mnuThongKeDoanhThu, mnuThongKeHoaDon, mnuDonDatBan, mnuDatBan,
            mnuTkKhachHang, mnuTkNhanVien, mnuTaiKhoan,
            mnuTkKhuyenMai, mnuThue, mnuTkThue;

    private JPanel contentWrapperPanel;

	private JMenuItem mnuTkSanPham;

    public Main() {
        setTitle("Quản lý Caffe CornCorn");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        createAllMenuItems();

        JMenuBar menuBar = new JMenuBar();
        String[][] menuItems = {
            {"Hệ Thống", "gear.png"},
            {"Danh Mục", "market-segment.png"},
            {"Xử Lý", "bpm.png"},
            {"Tìm Kiếm", "loupe.png"},
            {"Thống Kê", "analysis.png"}
        };

        for (String[] item : menuItems) {
            JMenu menu = createMenuWithIcon(item[0], item[1]);
            addMenuItemsToMenu(menu, item[0]);
            menuBar.add(menu);
        }

        setJMenuBar(menuBar);

        // ✅ Chỉ thêm MỘT panel nền duy nhất (contentWrapperPanel)
        contentWrapperPanel = new ImagePanel(BACKGROUND_IMAGE_PATH);
        contentWrapperPanel.setLayout(new BorderLayout());
        add(contentWrapperPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // ----------------------- MENU -------------------------------------
    private void addMenuItemsToMenu(JMenu menu, String menuText) {
        switch (menuText) {
            case "Hệ Thống":
                menu.add(mnuTrangChu);
                menu.addSeparator();
                menu.add(mnuTaiKhoan);
                menu.addSeparator();
                menu.add(mnuThoat);
                break;
            case "Danh Mục":
                menu.add(mnuKhachHang);
                menu.add(mnuNhanVien);
                menu.add(mnuSanPham);
                menu.add(mnuKhuyenMai);
                menu.add(mnuThue);
                menu.add(mnuDonDatBan);
                menu.add(mnuHoaDon);
                break;
            case "Xử Lý":
                menu.add(mnuDatBan);
                break;
            case "Tìm Kiếm":
                menu.add(mnuTkSanPham);
                menu.add(mnuTkKhuyenMai);
                menu.add(mnuTkThue);
                break;
            case "Thống Kê":
                menu.add(mnuThongKeDoanhThu);
                break;
        }
    }

    private void setupMenuItem(JMenuItem item) {
        item.setPreferredSize(new Dimension(250, 30));
        item.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        item.setHorizontalAlignment(SwingConstants.LEFT);
    }

    private void createAllMenuItems() {
        mnuTrangChu = new JMenuItem("Trang chủ");
        mnuTroGiup = new JMenuItem("Trợ giúp");
        mnuThoat = new JMenuItem("Thoát");
        mnuTaiKhoan = new JMenuItem("Quản lý Tài khoản");
        mnuKhachHang = new JMenuItem("Quản lý khách hàng");
        mnuNhanVien = new JMenuItem("Quản lý nhân viên");
        mnuKhuyenMai = new JMenuItem("Quản lý khuyến mãi");
        mnuSanPham = new JMenuItem("Quản lý sản phẩm");
        mnuDonDatBan = new JMenuItem("Quản lý đơn đặt bàn");
        mnuHoaDon = new JMenuItem("Quản lý hóa đơn");
        mnuDatBan = new JMenuItem("Đặt bàn");
        mnuThongKeDoanhThu = new JMenuItem("Báo cáo quản lý");
        mnuTkSanPham = new JMenuItem("Tìm kiếm sản phẩm");
        mnuTkKhuyenMai = new JMenuItem("Tìm kiếm khuyến mãi");
        mnuThue = new JMenuItem("Quản lý thuế");
        mnuTkThue = new JMenuItem("Tìm kiếm thuế");

        JMenuItem[] allItems = {
            mnuTrangChu, mnuTroGiup, mnuThoat, mnuTaiKhoan, mnuKhachHang, mnuNhanVien, mnuKhuyenMai, mnuSanPham,
            mnuDonDatBan, mnuDatBan, mnuHoaDon, mnuThongKeDoanhThu,
            mnuTkSanPham, mnuTkKhuyenMai, mnuThue, mnuTkThue
        };

        for (JMenuItem item : allItems) {
            setupMenuItem(item);
            item.addActionListener(this);
        }
    }

    // ----------------------- IMAGE PANEL -------------------------------
    class ImagePanel extends JPanel {
        private Image image;

        public ImagePanel(String path) {
            try {
                java.net.URL imageUrl = getClass().getResource(path);
                if (imageUrl != null) {
                    image = new ImageIcon(imageUrl).getImage();
                } else {
                    System.err.println("❌ Không tìm thấy ảnh nền: " + path);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null)
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private JMenu createMenuWithIcon(String text, String iconPath) {
        JMenu menu = new JMenu(text);
        menu.setFont(new Font("Segoe UI", Font.BOLD, 20));
        menu.setPreferredSize(new Dimension(150, 40));

        try {
            String fullPath = "/image/" + iconPath;
            java.net.URL imageUrl = getClass().getResource(fullPath);
            if (imageUrl != null) {
                ImageIcon originalIcon = new ImageIcon(imageUrl);
                Image scaled = originalIcon.getImage().getScaledInstance(26, 26, Image.SCALE_SMOOTH);
                menu.setIcon(new ImageIcon(scaled));
            } else {
                System.err.println("⚠️ Không tìm thấy icon: " + fullPath);
            }
        } catch (Exception ignored) {}

        return menu;
    }

    // ----------------------- CHUYỂN PANEL -------------------------------
    private void setPanel(JPanel panel) {
        contentWrapperPanel.removeAll();
        contentWrapperPanel.add(panel, BorderLayout.CENTER);
        contentWrapperPanel.revalidate();
        contentWrapperPanel.repaint();
    }

    // ----------------------- XỬ LÝ MENU -------------------------------
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        try {
            if (o == mnuTrangChu) {
            	setPanel(new ImagePanel(BACKGROUND_IMAGE_PATH));
            } else if (o == mnuSanPham) {
                setPanel(new SanPham_GUI());
            } else if( o == mnuTkSanPham) {
            	setPanel(new TimKiemSanPham_GUI());
            } else if( o ==mnuDatBan) {
            	setPanel(new DatBan_GUI());
            } else if( o == mnuKhuyenMai) {
            	setPanel( new KhuyenMai_GUI());
            } else if( o ==mnuThue) {
            	setPanel( new Thue_GUI());
            } else if( o == mnuKhachHang) {
            	setPanel(new KhachHang_GUI());
            } else if( o == mnuHoaDon) {
            	setPanel(new HoaDon_GUI());
            } else if( o ==mnuNhanVien) {
            	setPanel(new NhanVien_GUI());
            } else if( o ==mnuDonDatBan) {
            	setPanel(new DonDatBan_GUI());
            } else if (o ==mnuTaiKhoan) {
            	setPanel(new TaiKhoan_GUI());
            } else if (o ==mnuNhanVien) {
            	setPanel(new NhanVien_GUI());
            } else if(o ==mnuThongKeDoanhThu) {
            	setPanel(new ThongKeSanPham_GUI());
            } else if(o == mnuTkKhuyenMai) {
            	setPanel(new TimKiemKhuyenMai_GUI());
            } else if(o ==mnuTkThue) {
            	setPanel(new TimKiemThue_GUI());
            }
            	
            	
       
            
            
            else if (o == mnuThoat) {
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn thoát?", "Xác nhận",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION)
                    System.exit(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ----------------------- MAIN -------------------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
