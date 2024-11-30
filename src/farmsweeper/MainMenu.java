package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;
import java.sql.*;

public class MainMenu extends JFrame {

    private JButton normalModeButton;
    private JButton customModeButton;
    private JButton achieveButton;
    private JButton leaderboardButton;
    private JButton exitButton;
    private JPanel cardPanel;
    private CardLayout cardLayout;    

    private int timeElapsed = 0;
    private int turnCounter = 0;
    private String selectedDifficulty = "Normal";

    // Constructor
    public MainMenu() {
        setTitle("FarmSweeper");
        setSize(1000, 700);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Test database connection on start
        if (isDatabaseConnected()) {
            JOptionPane.showMessageDialog(this, "Database connected successfully!", "Connection Status", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to connect to the database.", "Connection Status", JOptionPane.ERROR_MESSAGE);
        }

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Panels for different modes
        JPanel mainMenuPanel = createMainMenuPanel();
        NormalMode normalModePanelClass = new NormalMode();
        JPanel normalModePanel = normalModePanelClass.createNormalModePanel(cardLayout, cardPanel);

        CustomMode customModePanelClass = new CustomMode();
        JPanel customModePanel = customModePanelClass.createCustomModePanel(cardLayout, cardPanel);

        GameboardGameLogic gameboardLogic = new GameboardGameLogic("Normal");
        JPanel gameboardPanel = gameboardLogic.createGameboardPanel(cardLayout, cardPanel);

        // Leaderboard Panel
        Leaderboard leaderboardPanel = new Leaderboard();
        JPanel leaderboardPanelInstance = leaderboardPanel.createLeaderboardPanel(cardLayout, cardPanel);

        // Other Panels
        CustomGameWin customGameWin = new CustomGameWin(leaderboardPanel);
        JPanel winPanel = customGameWin.createWinPanel(cardLayout, cardPanel, timeElapsed, turnCounter, selectedDifficulty);
        
        CustomGameLose customGameLosePanelClass = new CustomGameLose();
        JPanel customgamelosePanel = customGameLosePanelClass.createLosePanel(cardLayout, cardPanel);

        Achievements achievementsPanelClass = new Achievements();
        JPanel achievementsModePanel = achievementsPanelClass.createAchievementsPanel(cardLayout, cardPanel);

        // Add panels to CardLayout
        cardPanel.add(mainMenuPanel, "Main Menu");
        cardPanel.add(normalModePanel, "Normal Mode");
        cardPanel.add(customModePanel, "Custom Mode");
        cardPanel.add(gameboardPanel, "Gameboard");
        cardPanel.add(winPanel, "Custom Game Win");
        cardPanel.add(customgamelosePanel, "Custom Game Lose");
        cardPanel.add(achievementsModePanel, "Achievements");
        cardPanel.add(leaderboardPanelInstance, "Leaderboard");

        // Set the content pane and start with the main menu
        setContentPane(cardPanel);
        cardLayout.show(cardPanel, "Main Menu");
        setVisible(true);
    }

    // Create the main menu panel with buttons for various modes
    private JPanel createMainMenuPanel() {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);

        // Background image for the main menu
        ImageIcon backgroundIcon = new ImageIcon("main.png");
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        Image image = backgroundIcon.getImage();
        Image scaledImage = image.getScaledInstance(815, 620, Image.SCALE_SMOOTH);
        backgroundLabel.setIcon(new ImageIcon(scaledImage));
        backgroundLabel.setBounds(0, 0, 815, 620);
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);

        // Buttons for normal mode, custom mode, achievements, leaderboard, and exit
        normalModeButton = createButton("normalMode.png", 243, 225, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Normal Mode");
            }
        });

        customModeButton = createButton("customMode.png", 243, 297, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Custom Mode");
            }
        });

        achieveButton = createButton("achieveMode.png", 243, 369, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Achievements");
            }
        });

        // Leaderboard button now connected to the Leaderboard panel
        leaderboardButton = createButton("leaderMode.png", 243, 441, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Leaderboard");
            }
        });

        exitButton = createButton("exitMode.png", 243, 513, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exitGame();
            }
        });

        // Add buttons to the layered pane
        layeredPane.add(normalModeButton, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(customModeButton, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(achieveButton, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(leaderboardButton, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(exitButton, JLayeredPane.PALETTE_LAYER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(layeredPane, BorderLayout.CENTER);
        return panel;
    }

    // Helper method to create a button with an image icon
    private JButton createButton(String iconPath, int x, int y, ActionListener action) {
        JButton button = new JButton();
        try {
            BufferedImage buttonIcon = ImageIO.read(new File(iconPath));
            ImageIcon buttonImageIcon = new ImageIcon(buttonIcon);
            button.setIcon(buttonImageIcon);
            button.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading image for button: " + iconPath, "Error", JOptionPane.ERROR_MESSAGE);
        }
        button.setBounds(x, y, 323, 85);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.addActionListener(action);
        return button;
    }

    // Method to handle game exit
    private void exitGame() {
        int choice = JOptionPane.showConfirmDialog(this, "Do you want to exit the game?", "Exit Game",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    // Check if the database connection is successful
    private boolean isDatabaseConnected() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/farmsweeper", "root", "Juliana")) {
            if (conn != null) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
