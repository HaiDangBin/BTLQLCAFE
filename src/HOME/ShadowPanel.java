package HOME;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ShadowPanel extends JPanel {
    private int shadowSize = 8; // Kích thước bóng đổ
    private Color shadowColor = new Color(0, 0, 0, 80); // Màu đen, độ trong suốt 80/255
    private int cornerRadius = 15; // Độ cong của góc

    public ShadowPanel(LayoutManager layout) {
        super(layout);
        // Quan trọng: Tắt opaque để JPanel có thể vẽ bóng đổ bên ngoài khu vực của nó
        setOpaque(false);
        // Thiết lập màu nền ban đầu để panel nội dung có màu sắc
        super.setBackground(new Color(48, 48, 48)); 
    }
    
    public ShadowPanel() {
        this(new BorderLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x = shadowSize;
        int y = shadowSize;
        int w = getWidth() - shadowSize * 2;
        int h = getHeight() - shadowSize * 2;

        // 1. Vẽ bóng đổ (Blur effect)
        // Đây là cách đơn giản để tạo hiệu ứng mờ dần (không phải blur thực sự, nhưng hiệu quả)
        for (int i = 0; i < shadowSize; i++) {
            // Tính toán độ trong suốt giảm dần
            int alpha = shadowColor.getAlpha() * (shadowSize - i) / shadowSize;
            g2.setColor(new Color(shadowColor.getRed(), shadowColor.getGreen(), shadowColor.getBlue(), alpha));
            
            // Vẽ bóng đổ xung quanh khu vực nội dung (offset nhẹ để trông giống bóng đổ)
            g2.fill(new RoundRectangle2D.Double(
                    x - i / 2.0 + 2, y - i / 2.0 + 2, // Dịch chuyển bóng nhẹ xuống và sang phải
                    w + i - 4, h + i - 4, 
                    cornerRadius + i, cornerRadius + i));
        }

        // 2. Vẽ nền panel chính
        g2.setColor(getBackground()); 
        g2.fill(new RoundRectangle2D.Double(x, y, w, h, cornerRadius, cornerRadius));

        g2.dispose();
        super.paintComponent(g); // Vẽ các component con lên trên
    }
    
    // Đảm bảo getBackground() trả về màu nền nội dung
    @Override
    public void setBackground(Color bg) {
        // Lưu màu nền cho việc vẽ nội dung
        super.setBackground(bg); 
    }
    
    // Ghi đè getPreferredSize để tính toán kích thước bóng đổ
    @Override
    public Dimension getPreferredSize() {
        if (getLayout() != null) {
            Dimension dim = super.getPreferredSize();
            return new Dimension(dim.width + shadowSize * 2, dim.height + shadowSize * 2);
        }
        return super.getPreferredSize();
    }
}