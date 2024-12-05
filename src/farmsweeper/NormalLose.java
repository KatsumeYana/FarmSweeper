package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class NormalLose {

    public JPanel createLosePanel(CardLayout cardLayout, JPanel cardPanel) {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);

        // Home Button
        JButton homeButton = new JButton("Home");
        homeButton.setBounds(100, 385, 70, 50);
        homeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Main Menu");
            }
        });
        panel.add(homeButton);

        // Retry Button
        JButton retryButton = new JButton("Retry");
        retryButton.setBounds(250, 385, 100, 50);
        retryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Get the selected difficulty from CustomMode
                String difficulty = CustomMode.getSelectedDifficulty();

                // Reset the gameboard state with the selected difficulty
                GameboardGameLogic gameboardLogic = new GameboardGameLogic(difficulty);
                JPanel newGameboardPanel = gameboardLogic.createGameboardPanel(cardLayout, cardPanel);

                // Replace or update the Gameboard panel
                cardPanel.add(newGameboardPanel, "Gameboard");
                cardLayout.show(cardPanel, "Gameboard");
            }
        });
        panel.add(retryButton);
        
        // Custom Lose Image
        JLabel loseImageLabel = new JLabel();
        File loseImageFile = new File("resources/images/customlose.png");
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
