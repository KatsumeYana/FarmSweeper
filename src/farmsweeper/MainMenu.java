package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;

public class MainMenu extends JFrame {

    private JButton normalModeButton;
    private JButton customModeButton;
    private JButton achieveButton;
    private JButton leaderboardButton;
    private JButton exitButton;
    private final JPanel cardPanel;
    private final CardLayout cardLayout;

    private final int timeElapsed = 0;
    private final int turnCounter = 0;
    private final String selectedDifficulty = "Normal";

    // Constructor
    public MainMenu() {
        setTitle("FarmSweeper");
        setSize(1010, 735);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Test database connection on start
        if (isDatabaseConnected()) {
            JOptionPane.showMessageDialog(null, "Database connected successfully!", "Connection Status", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Failed to connect to the database.", "Connection Status", JOptionPane.ERROR_MESSAGE);
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
        ImageIcon backgroundIcon = loadImage("menu.png");  // Relative path to the image

        if (backgroundIcon == null) {
            // Fallback to a plain background color if the image is not found
            backgroundIcon = new ImageIcon();
        }

        JLabel backgroundLabel = new JLabel(backgroundIcon);
        Image image = backgroundIcon.getImage();
        Image scaledImage = image.getScaledInstance(1000, 700, Image.SCALE_SMOOTH);
        backgroundLabel.setIcon(new ImageIcon(scaledImage));
        backgroundLabel.setBounds(0, 0, 1000, 700);
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);

        // Buttons for normal mode, custom mode, achievements, leaderboard, and exit
        normalModeButton = createButton("Normal Mode Button.png", 340, 250, (ActionEvent e) -> {
            cardLayout.show(cardPanel, "Normal Mode");
        });

        customModeButton = createButton("Custom Mode Button.png", 340, 330, (ActionEvent e) -> {
            cardLayout.show(cardPanel, "Custom Mode");
        });

        achieveButton = createButton("Achievements Button.png", 340, 410, (ActionEvent e) -> {
            cardLayout.show(cardPanel, "Achievements");
        });

        leaderboardButton = createButton("Leaderboard Button.png", 340, 490, (ActionEvent e) -> {
            cardLayout.show(cardPanel, "Leaderboard");
        });

        exitButton = createButton("Exit Button.png", 340, 570, (ActionEvent e) -> {
            exitGame();
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
        // Load initial image icon
        final ImageIcon buttonImageIcon = loadImage(iconPath);

        // Initial icon size
        final int initialWidth = buttonImageIcon.getIconWidth();
        final int initialHeight = buttonImageIcon.getIconHeight();

        // Set up the button
        JButton button = new JButton();
        button.setIcon(buttonImageIcon);
        button.setText("");
        button.setBounds(x, y, initialWidth, initialHeight);  // initial size based on the image
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.addActionListener(action);

        // Apply the hover effect using Animations class
        Animations.applyHoverEffect(button, buttonImageIcon);

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
            return conn != null;
        } catch (SQLException e) {
            return false;
        }
    }

    // Helper method to load images with a fallback
    private ImageIcon loadImage(String path) {
        // Directly load image from the file system (relative to project folder)
        File imageFile = new File("resources/images/" + path);
        if (imageFile.exists()) {
            try {
                BufferedImage bufferedImage = ImageIO.read(imageFile);
                return new ImageIcon(bufferedImage);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading image: " + path, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.err.println("Image not found: " + path);
        }
        return null; // Return null if image is not found or can't be loaded
    }
}
