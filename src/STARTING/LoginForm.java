package STARTING;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import DAO.TaiKhoan_DAO;
import Entity.TaiKhoan;
import STARTING.Main;

public class LoginForm extends JFrame {

    public static TaiKhoan tkLogin;
	private JTextField txtTenDN;
    private JPasswordField txtMatKhau;
    private TaiKhoan_DAO tkDAO = new TaiKhoan_DAO();

    public LoginForm() {

        // (Có thể bỏ nếu cậu không thích đổi LookAndFeel)
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        setTitle("Đăng nhập hệ thống");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===================== HEADER =====================
        JPanel header = new JPanel();
        header.setBackground(new Color(0, 153, 51));
        JLabel lblTitle = new JLabel("ĐĂNG NHẬP HỆ THỐNG");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.add(lblTitle);
        add(header, BorderLayout.NORTH);

        // ===================== FORM PANEL =====================
        JPanel form = new JPanel();
        form.setLayout(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUser = new JLabel("Tên đăng nhập:");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JLabel lblPass = new JLabel("Mật khẩu:");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        txtTenDN = new JTextField(20);
        txtTenDN.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtTenDN.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        txtMatKhau = new JPasswordField(20);
        txtMatKhau.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtMatKhau.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JCheckBox chkShow = new JCheckBox("Hiện mật khẩu");
        chkShow.setBackground(Color.WHITE);
        chkShow.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chkShow.addActionListener(e -> {
            txtMatKhau.setEchoChar(chkShow.isSelected() ? 0 : '•');
        });

        // vị trí
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(lblUser, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        form.add(txtTenDN, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(lblPass, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        form.add(txtMatKhau, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        form.add(chkShow, gbc);

        add(form, BorderLayout.CENTER);

        // ===================== BUTTON PANEL =====================
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setPreferredSize(new Dimension(100, 70)); // đảm bảo luôn thấy nút

        JButton btnLogin = new JButton("Đăng nhập");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogin.setBackground(new Color(0, 153, 51));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setOpaque(true);
        btnLogin.setBorderPainted(true);
        btnLogin.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton btnExit = new JButton("Thoát");
        btnExit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnExit.setBackground(new Color(204, 0, 0));
        btnExit.setForeground(Color.WHITE);
        btnExit.setFocusPainted(false);
        btnExit.setOpaque(true);
        btnExit.setBorderPainted(true);
        btnExit.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        btnLogin.addActionListener(e -> dangNhap());
        btnExit.addActionListener(e -> System.exit(0));

        btnPanel.add(btnLogin);
        btnPanel.add(btnExit);

        add(btnPanel, BorderLayout.SOUTH);

        // Enter để đăng nhập
        txtMatKhau.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    dangNhap();
            }
        });
    }

    // ===================== XỬ LÝ ĐĂNG NHẬP =====================
    private void dangNhap() {

        String user = txtTenDN.getText().trim();
        String pass = new String(txtMatKhau.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Không được để trống tên đăng nhập hoặc mật khẩu!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        TaiKhoan tk = tkDAO.dangNhap(user, pass);

        if (tk == null) {
            JOptionPane.showMessageDialog(this,
                    "Tên đăng nhập hoặc mật khẩu sai!",
                    "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
            return;
        }
        tkLogin = tk;
        // 👉 CHỈ DÙNG GETTER ĐANG CÓ: getNhanVien().getTenNV()
        String tenNV = "bạn";
        if (tk.getNhanVien() != null) {
            // giả định NhanVien có getTenNV() (DAO đã tạo NhanVien từ tenNV)
            tenNV = tk.getNhanVien().getTenNV();
        }

        JOptionPane.showMessageDialog(this,
                "Đăng nhập thành công!\nXin chào, " + tenNV + " 😊",
                "Chào mừng",
                JOptionPane.INFORMATION_MESSAGE);

        dispose();
        new Main().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
