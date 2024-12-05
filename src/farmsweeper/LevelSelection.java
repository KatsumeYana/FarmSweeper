package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class LevelSelection extends JPanel {

    private Connection conn;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private String selectedTheme;  // Variable to store selected theme
    private int levelToResume;     // Variable to store the level to resume, can be 1 if it's a new game

    public LevelSelection(CardLayout cardLayout, JPanel cardPanel, Connection conn, String selectedTheme, int levelToResume) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.conn = conn;
        this.selectedTheme = selectedTheme;  
        this.levelToResume = levelToResume;
        setupUI();  // Set up the UI for the level selection
    }

    private void setupUI() {
        setLayout(new BorderLayout());  // Set BorderLayout for the panel

        // Set background image and other settings
        ImageIcon backgroundIcon = new ImageIcon("resources/images/LEVEL SELECTION BACKGROUND.png");
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        Image image = backgroundIcon.getImage();
        Image scaledImage = image.getScaledInstance(1100, 800, Image.SCALE_SMOOTH);
        backgroundLabel.setIcon(new ImageIcon(scaledImage));
        backgroundLabel.setBounds(0, 0, 1000, 700);

        // Create a layered panel to include the background
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1100, 800));
        layeredPane.add(backgroundLabel, Integer.valueOf(0));

        // Create the main panel for level selection
        JPanel levelSelectionPanel = new JPanel();
        levelSelectionPanel.setOpaque(false);  // Make it transparent to show the background
        levelSelectionPanel.setLayout(new BoxLayout(levelSelectionPanel, BoxLayout.Y_AXIS));  // Stack vertically

        // Add difficulty section labels and levels based on levelToResume
        levelSelectionPanel.add(createDifficultyLabel("Easy"));
        levelSelectionPanel.add(createLevelPanel(1, 3));  // Levels 1 to 3 for Easy
        levelSelectionPanel.add(Box.createVerticalStrut(5));  // Space between difficulty sections

        levelSelectionPanel.add(createDifficultyLabel("Normal"));
        levelSelectionPanel.add(createLevelPanel(4, 6));  // Levels 4 to 6 for Normal
        levelSelectionPanel.add(Box.createVerticalStrut(5));  // Space between difficulty sections

        levelSelectionPanel.add(createDifficultyLabel("Hard"));
        levelSelectionPanel.add(createLevelPanel(7, 9));  // Levels 7 to 9 for Hard

        // Add the level selection panel to the layered pane
        levelSelectionPanel.setBounds(215, 200, 600, 400);
        layeredPane.add(levelSelectionPanel, Integer.valueOf(1));  // Add above background

        // Cancel Button to go back to the main menu
        String backIconPath = "back.png";
        JButton backButton = BaseGame.createButton(backIconPath, 20, 10, 128, 70, (ActionEvent e) -> {
            cardLayout.show(cardPanel, "Main Menu");
        });
        layeredPane.add(backButton, Integer.valueOf(1));  // Add above background

        // Add layeredPane to the panel
        add(layeredPane, BorderLayout.CENTER);
    }

    // Method to create the difficulty label for each row
    private JLabel createDifficultyLabel(String difficultyName) {
        JLabel label = new JLabel(difficultyName, SwingConstants.CENTER);
        Font textFont = BaseGame.loadCustomFont("PressStart2P-Regular.ttf", 18);
        label.setFont(textFont);
        label.setForeground(new Color(91, 56, 34));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JPanel createLevelPanel(int startLevel, int endLevel) {
    JPanel levelPanel = new JPanel();
    levelPanel.setOpaque(false);  // Make it transparent to show the background
    levelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

    for (int level = startLevel; level <= endLevel; level++) {
        final int currentLevel = level;  // Make currentLevel final
        final String currentTheme = selectedTheme;  // Make currentTheme final

        // Create the level icon path for the button
        String levelIconPath = "Level" + level + ".png";
        JButton levelButton = BaseGame.createButton(levelIconPath, 100, 385, 50, 20, e -> {
            startGame(currentLevel, currentTheme);  // Pass both level and theme to the startGame method
        });

        // Check if the level is unlocked and disable the button if not
        if (!isLevelUnlocked(level)) {
            levelButton.setEnabled(false);  // Disable button if level is locked
        }

        // Add the level button to the panel
        levelPanel.add(levelButton);
    }

    return levelPanel;
}


    
    
    private void startGame(int currentLevel, String currentTheme) {
        // Pass the selected theme to the game logic when starting the game
        NormalGameboardGameLogic gameLogic = new NormalGameboardGameLogic(currentLevel, cardLayout, cardPanel, selectedTheme);
        cardPanel.add(gameLogic, "GameBoard");
        cardLayout.show(cardPanel, "GameBoard");
    }

    // Method to check if the level is unlocked based on the database
    private boolean isLevelUnlocked(int level) {
        if (level == 1) {
            // Level 1 is unlocked only if there is game data for it
            return isGameDataExists(1);  // Check if there is game data for Level 1
        }

        // For other levels, check if the level exists in the database and has valid data
        try {
            String query = "SELECT level FROM normalgamerecords WHERE level = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, level);
            ResultSet rs = stmt.executeQuery();
            return rs.next();  // If there's any result, the level is unlocked
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to check if the game data exists for the specified level
    private boolean isGameDataExists(int level) {
        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Database connection is not initialized!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            String query = "SELECT COUNT(*) FROM normalgamerecords WHERE level = ? AND time IS NOT NULL AND turn IS NOT NULL";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, level);  // Set the level to check
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;  // If there's any valid data for the level, return true
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error checking game data.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return false;  // No valid data for the level
    }
}
