package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class CustomGameLose {

    public JPanel createLosePanel(CardLayout cardLayout, JPanel cardPanel) {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);

        // Custom Lose Image
        JLabel loseImageLabel = new JLabel();
        File loseImageFile = new File("customlose.png");
            ImageIcon originalIcon = new ImageIcon(loseImageFile.getPath());
            Image scaledImage = originalIcon.getImage().getScaledInstance(450, 342, Image.SCALE_SMOOTH);
            loseImageLabel.setIcon(new ImageIcon(scaledImage));
        
        loseImageLabel.setBounds(0, 0, 815, 620);
        panel.add(loseImageLabel);
        
        JButton homeButton = new JButton("Home");
        homeButton.setBounds(100, 385, 70, 50);
        homeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Main Menu");
            }
        });
        
        // Retry Button
        JButton retryButton = new JButton("Retry");
        retryButton.setBounds(450, 485, 100, 50);
        retryButton.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                // Reset the gameboard state
                GameboardGameLogic gameboardLogic = new GameboardGameLogic();
                JPanel newGameboardPanel = gameboardLogic.createGameboardPanel(cardLayout, cardPanel);

                // Replace the old Gameboard panel
                cardPanel.add(newGameboardPanel, "Gameboard");
                cardLayout.show(cardPanel, "Gameboard");
            }
        });
        panel.add(retryButton);

        return panel;
    }
}
