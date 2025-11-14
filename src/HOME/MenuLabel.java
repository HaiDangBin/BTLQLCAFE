package HOME;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MenuLabel extends JLabel {
    private Color defaultBg;
    private Color highlightBg;
    private Color highlightBorder = new Color(0, 150, 255); // Màu xanh accent
    private boolean isHovered = false;

    public MenuLabel(String title, Color textColor, Font font, Color defaultBg, Color highlightBg) {
        super(title);
        this.defaultBg = defaultBg;
        this.highlightBg = highlightBg;
        
        setForeground(textColor);
        setFont(font);
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        setOpaque(true);
        setBackground(defaultBg);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Vẽ nền
        if (isHovered) {
            // Tạo Gradient nhẹ khi hover
            GradientPaint gp = new GradientPaint(0, 0, highlightBg, getWidth(), 0, defaultBg.brighter());
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Vẽ viền highlight bên trái
            g2.setColor(highlightBorder);
            g2.fillRect(0, 0, 5, getHeight()); // Dải màu dọc
        } else {
            g2.setColor(defaultBg);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        g2.dispose();
        super.paintComponent(g);
    }
    
    // Ghi đè để không vẽ nền mặc định của JLabel
    @Override
    public void setOpaque(boolean isOpaque) {
        // Luôn tắt opaque để paintComponent tự vẽ
        super.setOpaque(false); 
    }
}