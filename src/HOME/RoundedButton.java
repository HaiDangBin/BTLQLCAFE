package HOME;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundedButton extends JButton {
    private Color defaultColor;
    private Color hoverColor;
    private Color pressColor;
    private int cornerRadius = 15; // Độ bo tròn góc

    public RoundedButton(String text, Color bgColor, Color fgColor) {
        super(text);
        this.defaultColor = bgColor;
        this.setForeground(fgColor);
        
        // Thiết lập màu sắc phản hồi (dựa trên màu nền)
        this.hoverColor = defaultColor.brighter().brighter();
        this.pressColor = defaultColor.darker();
        
        // Cần tắt opaque để vẽ nền tùy chỉnh
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(defaultColor);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                setBackground(pressColor);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Đảm bảo trở lại màu hover hoặc default sau khi nhả chuột
                if (e.getPoint().getX() >= 0 && e.getPoint().getX() <= getWidth() &&
                    e.getPoint().getY() >= 0 && e.getPoint().getY() <= getHeight()) {
                    setBackground(hoverColor);
                } else {
                    setBackground(defaultColor);
                }
            }
        });
        
        // Đặt màu nền ban đầu
        setBackground(defaultColor);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Vẽ nền bo tròn
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        
        g2.dispose();
        super.paintComponent(g); // Vẽ chữ lên trên
    }
}