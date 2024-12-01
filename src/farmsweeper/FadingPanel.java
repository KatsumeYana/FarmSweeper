package farmsweeper;

import javax.swing.*;
import java.awt.*;

public class FadingPanel extends JPanel {
    private float opacity = 0f;  // Initial opacity is 0 (fully transparent)

    public FadingPanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);  // Start with fully transparent
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        // Apply opacity effect (alpha composite)
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity);
        g2d.setComposite(alphaComposite);

        // Paint the component normally using the parent's paint mechanism
        super.paintComponent(g);
    }

    // Method to start the fade-in effect
    public void startFadeIn() {
        Timer timer = new Timer(30, e -> {
            if (opacity < 1f) {
                opacity += 0.05f;  // Gradually increase opacity (speed can be adjusted)
                opacity = Math.min(opacity, 1.0f);  // Ensure opacity doesn't exceed 1
                repaint();  // Repaint the component with new opacity
            } else {
                ((Timer) e.getSource()).stop();  // Stop the timer once fully opaque
            }
        });
        timer.start();  // Start the fade-in animation
    }
}
