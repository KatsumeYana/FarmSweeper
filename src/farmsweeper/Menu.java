package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Menu extends JFrame {

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
    private final int level=1;
    
    private Connection conn;

    // Constructor
    public Menu() {
        setTitle("FarmSweeper");
        setSize(1010, 735);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        this.cardLayout = new CardLayout();
        this.cardPanel = new JPanel(cardLayout);

        // Initialize the connection here
        conn = DatabaseConnection.getConnection();  // Get the connection from DatabaseConnection

        if (conn == null) {
            JOptionPane.showMessageDialog(this, "Failed to connect to the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        // Panels for different modes
        JPanel mainMenuPanel = createMainMenuPanel();
        
        
        NormalMode normalMode = new NormalMode(cardLayout, cardPanel, conn);  // Pass the connection here
        JPanel normalModePanel = normalMode.createNormalModePanel();  // Create the panel
        
         // Create and pass the connection to NormalWin
        NormalWin normalWin = new NormalWin(level, timeElapsed, turnCounter, cardLayout, cardPanel);  // Pass conn here
        JPanel normalWinPanel = normalWin.createWinPanel();  // Pass cardLayout and cardPanel for navigation
            
        // Add the win panel to the card panel
        cardPanel.add(normalWinPanel, "Normal Win");


        CustomMode customModePanelClass = new CustomMode();
        JPanel customModePanel = customModePanelClass.createCustomModePanel(cardLayout, cardPanel);

        CustomGameboardGameLogic gameboardLogic = new CustomGameboardGameLogic("Normal");
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
        ImageIcon backgroundIcon = BaseGame.loadImage("menu.png");  // Relative path to the image

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
        normalModeButton = BaseGame.createButton("Normal Mode Button.png", 340, 250, 335, 65, (ActionEvent e) -> {
            cardLayout.show(cardPanel, "Normal Mode");
        });

        customModeButton = BaseGame.createButton("Custom Mode Button.png", 340, 330,335, 65,  (ActionEvent e) -> {
            cardLayout.show(cardPanel, "Custom Mode");
        });

        achieveButton = BaseGame.createButton("Achievements Button.png", 340, 410, 335, 65, (ActionEvent e) -> {
            cardLayout.show(cardPanel, "Achievements");
        });

        leaderboardButton = BaseGame.createButton("Leaderboard Button.png", 340, 490, 335, 65, (ActionEvent e) -> {
            cardLayout.show(cardPanel, "Leaderboard");
        });

        exitButton = BaseGame.createButton("Exit Button.png", 340, 570, 335, 65, (ActionEvent e) -> {
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

}