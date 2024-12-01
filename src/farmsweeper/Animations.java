package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Animations {

    // Hover effect for a JButton (resizing the icon when mouse enters and exits)
    public static void applyHoverEffect(JButton button, ImageIcon initialIcon) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                ImageIcon newIcon = new ImageIcon(initialIcon.getImage().getScaledInstance(
                        initialIcon.getIconWidth() + 5, initialIcon.getIconHeight() + 5, Image.SCALE_SMOOTH
                ));
                button.setIcon(newIcon);
                button.revalidate();
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                ImageIcon newIcon = new ImageIcon(initialIcon.getImage().getScaledInstance(
                        initialIcon.getIconWidth() - 5, initialIcon.getIconHeight() - 5, Image.SCALE_SMOOTH
                ));
                button.setIcon(newIcon);
                button.revalidate();
                button.repaint();
            }
        });
    }

    // Fade-in animation for any JComponent (e.g., JPanel, JLabel)
    public static void fadeIn(JComponent component, int durationMillis) {
        component.setOpaque(false);  // Start with fully transparent

        final float[] opacity = {0f}; // Initial opacity is 0 (fully transparent)

        Timer fadeTimer = new Timer(30, e -> {
            if (opacity[0] < 1f) {
                opacity[0] += 0.05f;  // Gradually increase opacity (speed can be adjusted)
                component.repaint();   // Repaint to update opacity
            } else {
                ((Timer) e.getSource()).stop(); // Stop the timer once fully opaque
            }
        });

        fadeTimer.start();  // Start the fade-in animation

        component.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void paintComponent(Graphics g) {

                Graphics2D g2d = (Graphics2D) g;
                // Apply opacity effect (alpha composite)
                AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity[0]);
                g2d.setComposite(alphaComposite);

                
            }
        });
    }
}
