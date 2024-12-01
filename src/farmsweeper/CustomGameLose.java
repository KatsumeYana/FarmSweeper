package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class CustomGameLose {

    public JPanel createLosePanel(CardLayout cardLayout, JPanel cardPanel) {
        
        FadingPanel panel = new FadingPanel(null);  // Pass null to use default layout
        panel.setLayout(null);
        
        // Home Button with image and hover effect
        JButton homeButton = createButton("Home Button.png", 100, 385, 70, 62, e -> {
            cardLayout.show(cardPanel, "Main Menu");
        });
        panel.add(homeButton);

        // Retry Button with image and hover effect
        JButton retryButton = createButton("Retry Button.png", 250, 385, 69, 63, e -> {
            // Get the selected difficulty from CustomMode
            String difficulty = CustomMode.getSelectedDifficulty();

            // Reset the gameboard state with the selected difficulty
            GameboardGameLogic gameboardLogic = new GameboardGameLogic(difficulty);
            JPanel newGameboardPanel = gameboardLogic.createGameboardPanel(cardLayout, cardPanel);

            // Replace or update the Gameboard panel
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

        // Start the fade-in animation
        panel.startFadeIn();

        return panel;
    }

    // Helper method to load images with fallback
    private ImageIcon loadImage(String path) {
        File imageFile = new File("resources/images/" + path);
        if (imageFile.exists()) {
            try {
                ImageIcon icon = new ImageIcon(imageFile.getPath());
                return icon;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error loading image: " + path, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.err.println("Image not found: " + path);
        }
        return null;
    }

    // Helper method to create a button with image icon and hover effect
    private JButton createButton(String iconPath, int x, int y, int z, int w, ActionListener action) {
        JButton button = new JButton();
        final ImageIcon buttonImageIcon = loadImage(iconPath);

        button.setIcon(buttonImageIcon);
        button.setText("");
        button.setBounds(x, y, z, w);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.addActionListener(action);

        // Apply the hover effect using Animations class
        Animations.applyHoverEffect(button, buttonImageIcon);
        return button;
    }
}
