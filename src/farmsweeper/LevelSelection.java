package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LevelSelection extends JPanel {

    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final Connection conn;

    // Constructor
    public LevelSelection(CardLayout cardLayout, JPanel cardPanel, Connection conn) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.conn = conn;
        createLevelSelectionPanel();  // Create the level selection UI
    }

    // Method to create the Level Selection Panel with levels and background
    public JPanel createLevelSelectionPanel() {
        JPanel panel = new JPanel(null);
        panel.setLayout(new BorderLayout());

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

        // Back Button to go back to the main menu
        String backIconPath = "back.png";
        JButton backButton = BaseGame.createButton(backIconPath, 20, 10, 128, 70, (ActionEvent e) -> {
            cardLayout.show(cardPanel, "Main Menu");
        });
        layeredPane.add(backButton, Integer.valueOf(1));  // Add above background

        // Add layeredPane to the panel
        panel.add(layeredPane, BorderLayout.CENTER);

        return panel;
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

    // Method to create the level panels dynamically (levels 1-3, 4-6, 7-9)
    private JPanel createLevelPanel(int startLevel, int endLevel) {
        JPanel levelPanel = new JPanel();
        levelPanel.setOpaque(false);  // Make it transparent to show the background
        levelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        for (int level = startLevel; level <= endLevel; level++) {
            final int currentLevel = level;  // Make currentLevel final

            // Create the level icon path for the button
            String levelIconPath = "Level" + level + ".png";
            JButton levelButton = BaseGame.createButton(levelIconPath, 100, 385, 50, 20, e -> {
                startGame(currentLevel);  // Pass level to the startGame method
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

    // Method to check if the game data exists for the specified level
    private boolean isGameDataExists(int level) {
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

    // Method to check if the level is unlocked based on the database
    private boolean isLevelUnlocked(int level) {
        // Always unlock Level 1
        if (level == 1) {
            return true;
        }

        // For other levels, check if the previous level has data, and if yes, unlock the current level
        try {
            // Check if the previous level exists and has valid data
            String query = "SELECT COUNT(*) FROM normalgamerecords WHERE level = ? AND time IS NOT NULL AND turn IS NOT NULL";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, level - 1);  // Check the previous level
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;  // Unlock the level if previous level has data
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Otherwise, the level is locked
    }

    // Method to start the game for the selected level
    private void startGame(int selectedLevel) {
        // Get the theme based on the level
        String theme = getThemeForLevel(selectedLevel);

        // Pass the theme to the game logic
        NormalGameboardGameLogic gameboardLogic = new NormalGameboardGameLogic(selectedLevel, cardLayout, cardPanel, theme);  // Pass theme to the game logic
        JPanel gameboardPanel = gameboardLogic.createNormalGameboardPanel();
        cardPanel.add(gameboardPanel, "Normal Gameboard");
        cardLayout.show(cardPanel, "Normal Gameboard");  // Show the gameboard panel
    }

    // Method to get the theme based on the selected level
    private String getThemeForLevel(int level) {
        if (level >= 1 && level <= 3) {
            return "Spring";  // Levels 1-3 are Spring
        } else if (level >= 4 && level <= 6) {
            return "Summer";  // Levels 4-6 are Summer
        } else {
            return "Autumn";  // Levels 7-9 are Autumn
        }
    }

}
