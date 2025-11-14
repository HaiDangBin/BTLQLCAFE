package STARTING;

import javax.swing.*;
import DAO.TaiKhoan_DAO;
import Entity.TaiKhoan;
import java.awt.*;

public class Login extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin, btnExit;

    public Login() {
        setTitle("Đăng nhập hệ thống");
        setSize(400, 250);                        // ✅ Quan trọng: thêm lại kích thước cửa sổ
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);              // Căn giữa màn hình
        setLayout(null);                          // Dùng layout tuyệt đối cho dễ căn

        JLabel lblUser = new JLabel("Tên đăng nhập:");
        lblUser.setBounds(50, 50, 100, 25);
        add(lblUser);

        txtUser = new JTextField();
        txtUser.setBounds(160, 50, 150, 25);
        add(txtUser);

        JLabel lblPass = new JLabel("Mật khẩu:");
        lblPass.setBounds(50, 90, 100, 25);
        add(lblPass);

        txtPass = new JPasswordField();
        txtPass.setBounds(160, 90, 150, 25);
        add(txtPass);

        btnLogin = new JButton("Đăng nhập");
        btnLogin.setBounds(80, 150, 100, 30);
        add(btnLogin);

        btnExit = new JButton("Thoát");
        btnExit.setBounds(210, 150, 100, 30);
        add(btnExit);

        // ====== Sự kiện ======
        btnLogin.addActionListener(e -> handleLogin());
        btnExit.addActionListener(e -> System.exit(0));
    }

    private void handleLogin() {
        String user = txtUser.getText().trim();
        String pass = String.valueOf(txtPass.getPassword()).trim();

        TaiKhoan_DAO dao = new TaiKhoan_DAO();
        TaiKhoan tk = dao.checkLogin(user, pass);

        if (tk != null) {
            JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
            new Main().setVisible(true);   // ✅ Gọi giao diện chính sau đăng nhập
            dispose();                          // ✅ Đóng cửa sổ đăng nhập
        } else {
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!");
        }
    }

    public static void main(String[] args) {
        new Login().setVisible(true);        // ✅ Gọi cửa sổ đăng nhập
    }
}
