package STARTING;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import DAO.TaiKhoan_DAO;
import Entity.TaiKhoan;
import STARTING.Main;

public class LoginForm extends JFrame {

    private static final long serialVersionUID = 1L;

    //  THÊM DÒNG NÀY: Lưu tài khoản đăng nhập toàn cục
    public static TaiKhoan tkLogin = null;

    private JTextField txtTenDN;
    private JPasswordField txtMatKhau;
    private TaiKhoan_DAO tkDAO = new TaiKhoan_DAO();

    public LoginForm() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        setTitle("Đăng nhập hệ thống");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ================= HEADER =================
        JPanel header = new JPanel();
        header.setBackground(new Color(0, 153, 51));
        JLabel lblTitle = new JLabel("ĐĂNG NHẬP HỆ THỐNG");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.add(lblTitle);
        add(header, BorderLayout.NORTH);

        // ================= FORM =================
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUser = new JLabel("Tên đăng nhập:");
        JLabel lblPass = new JLabel("Mật khẩu:");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        txtTenDN = new JTextField(20);
        txtMatKhau = new JPasswordField(20);
        JCheckBox chkShow = new JCheckBox("Hiện mật khẩu");
        chkShow.setBackground(Color.WHITE);
        chkShow.addActionListener(e -> txtMatKhau.setEchoChar(chkShow.isSelected() ? 0 : '•'));

        gbc.gridx = 0; gbc.gridy = 0; form.add(lblUser, gbc);
        gbc.gridx = 1; gbc.gridy = 0; form.add(txtTenDN, gbc);
        gbc.gridx = 0; gbc.gridy = 1; form.add(lblPass, gbc);
        gbc.gridx = 1; gbc.gridy = 1; form.add(txtMatKhau, gbc);
        gbc.gridx = 1; gbc.gridy = 2; form.add(chkShow, gbc);

        add(form, BorderLayout.CENTER);

        // ================= BUTTONS =================
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        JButton btnLogin = new JButton("Đăng nhập");
        JButton btnExit = new JButton("Thoát");

        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogin.setBackground(new Color(0, 153, 51));
        btnLogin.setForeground(Color.WHITE);

        btnExit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnExit.setBackground(new Color(204, 0, 0));
        btnExit.setForeground(Color.WHITE);

        btnLogin.addActionListener(e -> dangNhap());
        btnExit.addActionListener(e -> System.exit(0));

        btnPanel.add(btnLogin);
        btnPanel.add(btnExit);
        add(btnPanel, BorderLayout.SOUTH);

        txtMatKhau.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    dangNhap();
            }
        });
    }

    // ================= XỬ LÝ ĐĂNG NHẬP =================
    private void dangNhap() {
        String user = txtTenDN.getText().trim();
        String pass = new String(txtMatKhau.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không được để trống tên đăng nhập hoặc mật khẩu!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        TaiKhoan tk = tkDAO.dangNhap(user, pass);
        if (tk == null) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc mật khẩu sai!",
                    "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Lưu tài khoản đăng nhập lại để GUI khác dùng
        tkLogin = tk;

        String tenNV = (tk.getNhanVien() != null) ? tk.getNhanVien().getTenNV() : "bạn";
        JOptionPane.showMessageDialog(this,
                "Đăng nhập thành công!\nXin chào, " + tenNV + " 😊",
                "Chào mừng", JOptionPane.INFORMATION_MESSAGE);

        dispose();
        new Main().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
