package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class CustomGameWin {

    public JPanel createWinPanel(CardLayout cardLayout, JPanel cardPanel) {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);

        // Custom Win Image
        JLabel winImageLabel = new JLabel();
         File winImageFile = new File("customwin.png");
            ImageIcon originalIcon = new ImageIcon(winImageFile.getPath());
            Image scaledImage = originalIcon.getImage().getScaledInstance(450, 342, Image.SCALE_SMOOTH);
            winImageLabel.setIcon(new ImageIcon(scaledImage));
        winImageLabel.setBounds(0, 0, 815, 620);
        
        JTextField inputName = new JTextField("Enter name");
        inputName.setBounds(180, 320, 100, 30);
        inputName.setFont(new Font("Arial", Font.PLAIN, 16));
        
        
        JButton homeButton = new JButton("Home");
        homeButton.setBounds(100, 385, 70, 50);
        homeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Main Menu");
            }
        });
        
        
        JButton leaderboardButton = new JButton("Add Leaderboard");
        leaderboardButton.setBounds(200, 385, 150, 50);
        leaderboardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Leaderboard");
            }
        });
        panel.add(leaderboardButton);
        panel.add(inputName);
        panel.add(homeButton);
        panel.add(winImageLabel);
        return panel;
    }
}
