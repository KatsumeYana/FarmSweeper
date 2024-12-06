package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class NormalLose {

    private float alpha = 0f; // Start with fully transparent
    private int level; // To store the current level

    // Constructor to pass the current level to the panel
    public NormalLose(int level) {
        this.level = level;
    }

    public JPanel createLosePanel(CardLayout cardLayout, JPanel cardPanel) {
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Ensure the panel has a transparent background
                setOpaque(false);

                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Set alpha for fading effect, ensure it's within the range [0.0f, 1.0f]
                AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(1.0f, Math.max(0.0f, alpha)));
                g2d.setComposite(alphaComposite);

                super.paintComponent(g);  // Draw the content
            }
        };

        panel.setLayout(null);  // Use null layout for custom positioning
        startFadeIn(panel);     // Start fade-in effect

        // Home Button with image and hover effect
        JButton homeButton = BaseGame.createButton("Home Button.png", 350, 430, 70, 62, e -> {
            cardLayout.show(cardPanel, "Main Menu");
        });
        panel.add(homeButton);

        // Retry Button with image and hover effect
        JButton retryButton = BaseGame.createButton("Retry Button.png", 550, 430, 69, 63, e -> {
            // Restart the game from the current level
            NormalGameboardGameLogic gameboardLogic = new NormalGameboardGameLogic(level, cardLayout, cardPanel, "Spring");
            JPanel gameboardPanel = gameboardLogic.createNormalGameboardPanel();
            cardPanel.add(gameboardPanel, "Normal Gameboard");
            cardLayout.show(cardPanel, "Normal Gameboard");  // Show the updated gameboard panel
        });
        panel.add(retryButton);

        // Custom Lose Image
        JLabel loseImageLabel = new JLabel();
        File loseImageFile = new File("resources/images/lose.png");
        if (loseImageFile.exists()) {
            ImageIcon originalIcon = new ImageIcon(loseImageFile.getPath());
            Image scaledImage = originalIcon.getImage().getScaledInstance(450, 342, Image.SCALE_SMOOTH);
            loseImageLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            loseImageLabel.setText("Image not found!");
            loseImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            loseImageLabel.setForeground(Color.RED);
        }
        loseImageLabel.setBounds(270, 40, 815, 620);
        panel.add(loseImageLabel);

        return panel;
    }

    // Fade-in effect
    private void startFadeIn(JPanel panel) {
        Timer fadeInTimer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (alpha < 1f) {
                    alpha += 0.05f;  // Increase opacity
                    panel.repaint();  // Repaint the panel with updated opacity
                } else {
                    ((Timer) e.getSource()).stop();  // Stop the fade-in effect when fully opaque
                }
            }
        });
        fadeInTimer.start();
    }
}
