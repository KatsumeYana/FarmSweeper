
package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;

public class MainMenu extends JFrame {

private JButton normalModeButton;
    private JButton customModeButton;
    private JButton achieveButton;
    private JButton leaderButton;
    private JButton exitButton;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    public MainMenu() {
        
        setTitle("FarmSweeper");
        setSize(810, 655);  
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JPanel mainMenuPanel = createMainMenuPanel();

        NormalMode normalModePanelClass = new NormalMode();
        JPanel normalModePanel = normalModePanelClass.createNormalModePanel(cardLayout, cardPanel);

        CustomMode customModePanelClass = new CustomMode();
        JPanel customModePanel = customModePanelClass.createCustomModePanel(cardLayout, cardPanel);

        Achievements achievementsPanelClass = new Achievements();
        JPanel achievementsModePanel = achievementsPanelClass.createAchievementsPanel(cardLayout, cardPanel);

        Leaderboard leaderboardPanelClass = new Leaderboard();
        JPanel leaderboardModePanel = leaderboardPanelClass.createLeaderboardPanel(cardLayout, cardPanel);

        cardPanel.add(mainMenuPanel, "Main Menu");
        cardPanel.add(normalModePanel, "Normal Mode");
        cardPanel.add(customModePanel, "Custom Mode");
        cardPanel.add(achievementsModePanel, "Achievements");
        cardPanel.add(leaderboardModePanel, "Leaderboard");

        setContentPane(cardPanel);
        cardLayout.show(cardPanel, "Main Menu");
        setVisible(true);
    }

    private JPanel createMainMenuPanel() {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);

        ImageIcon backgroundIcon = new ImageIcon("main.png");
        JLabel backgroundLabel = new JLabel(backgroundIcon);

        Image image = backgroundIcon.getImage();
        Image scaledImage = image.getScaledInstance(815, 620, Image.SCALE_SMOOTH);
        backgroundLabel.setIcon(new ImageIcon(scaledImage));

        backgroundLabel.setBounds(0, 0, 815, 620);
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);

        normalModeButton = new JButton();
        try {
            BufferedImage buttonIcon = ImageIO.read(new File("normalMode.png"));
            ImageIcon buttonImageIcon = new ImageIcon(buttonIcon);
            normalModeButton.setIcon(buttonImageIcon);
            normalModeButton.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading image for Normal Mode button!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        normalModeButton.setBounds(243, 225, 323, 85);
        normalModeButton.setBorderPainted(false);
        normalModeButton.setFocusPainted(false);
        normalModeButton.setContentAreaFilled(false);

        normalModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Normal Mode");
            }
        });

        customModeButton = new JButton();
        try {
            BufferedImage buttonIcon = ImageIO.read(new File("customMode.png"));
            ImageIcon buttonImageIcon = new ImageIcon(buttonIcon);
            customModeButton.setIcon(buttonImageIcon);
            customModeButton.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading image for Custom Mode button!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        customModeButton.setBounds(243, 297, 323, 85);
        customModeButton.setBorderPainted(false);
        customModeButton.setFocusPainted(false);
        customModeButton.setContentAreaFilled(false);

        customModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Custom Mode");
            }
        });

        achieveButton = new JButton();
        try {
            BufferedImage buttonIcon = ImageIO.read(new File("achieveMode.png"));
            ImageIcon buttonImageIcon = new ImageIcon(buttonIcon);
            achieveButton.setIcon(buttonImageIcon);
            achieveButton.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading image for Achievements button!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        achieveButton.setBounds(243, 369, 323, 85);
        achieveButton.setBorderPainted(false);
        achieveButton.setFocusPainted(false);
        achieveButton.setContentAreaFilled(false);

        achieveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Achievements");
            }
        });

        leaderButton = new JButton();
        try {
            BufferedImage buttonIcon = ImageIO.read(new File("leaderMode.png"));
            ImageIcon buttonImageIcon = new ImageIcon(buttonIcon);
            leaderButton.setIcon(buttonImageIcon);
            leaderButton.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading image for Leaderboard button!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        leaderButton.setBounds(243, 441, 323, 85);
        leaderButton.setBorderPainted(false);
        leaderButton.setFocusPainted(false);
        leaderButton.setContentAreaFilled(false);

        leaderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Leaderboard");
            }
        });

        exitButton = new JButton();
        try {
            BufferedImage buttonIcon = ImageIO.read(new File("exitMode.png"));
            ImageIcon buttonImageIcon = new ImageIcon(buttonIcon);
            exitButton.setIcon(buttonImageIcon);
            exitButton.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading image for Exit button!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        exitButton.setBounds(243, 513, 323, 85);
        exitButton.setBorderPainted(false);
        exitButton.setFocusPainted(false);
        exitButton.setContentAreaFilled(false);

        exitButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
        System.out.println("Exit button clicked");

        int choice = JOptionPane.showConfirmDialog(
            MainMenu.this,  
            "Do you want to exit the game?",  
            "Exit Game",  
            JOptionPane.YES_NO_OPTION,  
            JOptionPane.QUESTION_MESSAGE  
        );

        if (choice == JOptionPane.YES_OPTION) {
            System.out.println("Exiting game...");
            System.exit(0);  
        } else {
            System.out.println("Exit cancelled.");
        }
    }
});

        layeredPane.add(normalModeButton, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(customModeButton, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(achieveButton, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(leaderButton, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(exitButton, JLayeredPane.PALETTE_LAYER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(layeredPane, BorderLayout.CENTER);  
        return panel;
    }
}
