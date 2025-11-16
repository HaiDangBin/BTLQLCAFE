package HOME;

import javax.swing.*;
import java.awt.*;

public class TitlePanel extends JPanel {
    private JLabel titleLabel;
    private Color separatorColor = new Color(0, 150, 255); // Màu accent
    
    public TitlePanel(String title, Color bgColor, Color fgColor, Font font) {
        setLayout(new BorderLayout());
        setBackground(bgColor);
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        titleLabel = new JLabel(title);
        titleLabel.setForeground(fgColor);
        titleLabel.setFont(font);
        
        add(titleLabel, BorderLayout.WEST);
        
        // Thêm một panel nhỏ để tạo đường gạch chân (separator)
        JPanel separator = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(getWidth(), 3); // Độ dày 3px
            }
        };
        separator.setBackground(separatorColor);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0)); // Khoảng cách
        bottomPanel.add(separator, BorderLayout.WEST);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    public void setTitle(String title) {
        titleLabel.setText(title);
    }
}