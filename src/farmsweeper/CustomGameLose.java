package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class CustomGameLose {

    private float alpha = 0f; // Start with fully transparent

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
        JButton homeButton = BaseGame.createButton("Home Button.png", 100, 385, 70, 62, e -> {
            cardLayout.show(cardPanel, "Main Menu");
        });
        panel.add(homeButton);

        // Retry Button with image and hover effect
        JButton retryButton = BaseGame.createButton("Retry Button.png", 250, 385, 69, 63, e -> {
            // Get the selected difficulty from CustomMode
            String difficulty = CustomMode.getSelectedDifficulty();
            CustomGameboardGameLogic gameboardLogic = new CustomGameboardGameLogic(difficulty);
            JPanel newGameboardPanel = gameboardLogic.createGameboardPanel(cardLayout, cardPanel);
            cardPanel.add(newGameboardPanel, "Gameboard");
            cardLayout.show(cardPanel, "Gameboard");
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
        loseImageLabel.setBounds(0, 0, 815, 620);
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
