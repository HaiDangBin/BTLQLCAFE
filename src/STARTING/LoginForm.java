package STARTING;

import javax.swing.*;
import java.awt.*;
import DAO.TaiKhoan_DAO;
import Entity.TaiKhoan;
import GUI.Home_GUI;

public class LoginForm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnExit;

    public LoginForm() {
        setTitle("Đăng nhập hệ thống");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== Nền gradient =====
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(131, 167, 255),
                        getWidth(), getHeight(), new Color(204, 180, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());

        // ===== Form đăng nhập =====
        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(new Color(255, 255, 255, 230));
        loginPanel.setPreferredSize(new Dimension(380, 280));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        loginPanel.setOpaque(true);
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("ĐĂNG NHẬP HỆ THỐNG", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // username
        JPanel userPanel = new JPanel(new BorderLayout(10, 10));
        userPanel.setOpaque(false);
        JLabel userIcon = new JLabel(new ImageIcon("src/icons/user.png"));
        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setBorder(BorderFactory.createTitledBorder("Tài khoản"));
        userPanel.add(userIcon, BorderLayout.WEST);
        userPanel.add(txtUsername, BorderLayout.CENTER);

        // password
        JPanel passPanel = new JPanel(new BorderLayout(10, 10));
        passPanel.setOpaque(false);
        JLabel passIcon = new JLabel(new ImageIcon("src/icons/lock.png"));
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createTitledBorder("Mật khẩu"));
        passPanel.add(passIcon, BorderLayout.WEST);
        passPanel.add(txtPassword, BorderLayout.CENTER);

        // buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        btnLogin = new JButton("Đăng nhập");
        btnLogin.setBackground(new Color(93, 123, 255));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnExit = new JButton("Thoát");
        btnExit.setBackground(new Color(200, 200, 200));
        btnExit.setFont(new Font("Segoe UI", Font.BOLD, 14));

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnExit);

        // add to loginPanel
        loginPanel.add(lblTitle);
        loginPanel.add(userPanel);
        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(passPanel);
        loginPanel.add(Box.createVerticalStrut(20));
        loginPanel.add(buttonPanel);

        // Footer
        JLabel lblFooter = new JLabel("Thiết kế bởi nhóm bạn", JLabel.CENTER);
        lblFooter.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblFooter.setForeground(Color.DARK_GRAY);
        lblFooter.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        backgroundPanel.add(loginPanel);
        add(backgroundPanel, BorderLayout.CENTER);
        add(lblFooter, BorderLayout.SOUTH);

        // ==== SỰ KIỆN ====
        btnLogin.addActionListener(e -> doLogin());
        txtPassword.addActionListener(e -> doLogin());
        btnExit.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tài khoản và mật khẩu!");
            return;
        }

        TaiKhoan tk = new TaiKhoan_DAO().dangNhap(username, password);

        if (tk == null) {
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Đăng nhập thành công!\nXin chào, " + tk.getNhanVien().getTenNV());

        new Home_GUI(tk).setVisible(true);
        this.dispose();
    }
}
