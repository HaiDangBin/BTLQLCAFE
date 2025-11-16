package GUI;

import java.awt.BorderLayout;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Nha_GUI extends JPanel {
	public Nha_GUI() {
		setLayout(new BorderLayout());
        JLabel lblBackground = new JLabel(new ImageIcon("/image/trangchu.jpg")) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                g.drawImage(((ImageIcon)getIcon()).getImage(), 
                            0, 0, getWidth(), getHeight(), this);
            }
        };
        add(lblBackground, BorderLayout.CENTER);
	}
}