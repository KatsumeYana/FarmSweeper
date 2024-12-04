package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class CustomGameLose {

    public JPanel createLosePanel(CardLayout cardLayout, JPanel cardPanel) {
    JPanel panel = new JPanel(null) {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Make sure the background is transparent
            setOpaque(false); // This makes the background transparent
        }
    };

    panel.setLayout(null); // Use null layout for custom positioning

    // Home Button with image and hover effect
    JButton homeButton = BaseGame.createButton("Home Button.png", 100, 385, 70, 62, e -> {
        cardLayout.show(cardPanel, "Main Menu");
    });
    panel.add(homeButton);

    // Retry Button with image and hover effect
    JButton retryButton = BaseGame.createButton("Retry Button.png", 250, 385, 69, 63, e -> {
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

        return panel;
    }

}