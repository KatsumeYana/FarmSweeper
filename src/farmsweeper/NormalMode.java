package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class NormalMode {

    private JButton newGameButton;
    private JButton resumeGameButton;
    private JButton backButton;
    private JLabel backgroundLabel;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private Connection conn;

    public NormalMode(CardLayout cardLayout, JPanel cardPanel) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        
        // Initialize the connection here if it's null
        if (conn == null) {
            conn = DatabaseConnection.getConnection();  // Initialize the connection
        }
    }

    public JPanel createNormalModePanel() {
        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Database connection is not initialized!", "Error", JOptionPane.ERROR_MESSAGE);
            return null;  // Exit if the connection is not valid
        }

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);

        // Set background image
        ImageIcon backgroundIcon = new ImageIcon("resources/images/backgroundGame.png");
        backgroundLabel = new JLabel(backgroundIcon);
        Image image = backgroundIcon.getImage();
        Image scaledImage = image.getScaledInstance(1000, 700, Image.SCALE_SMOOTH);
        backgroundLabel.setIcon(new ImageIcon(scaledImage));
        backgroundLabel.setBounds(0, 0, 1000, 700);
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);

        // Create New Game Button using BaseGame.createButton
        newGameButton = BaseGame.createButton("New Game Button.png", 200, -10, 276, 371, (ActionEvent e) -> {
            if (isGameDataExists(1)) {  // Check if there is existing game data
                int response = JOptionPane.showConfirmDialog(cardPanel,
                        "Game data already exists. Do you want to reset it?",
                        "Reset Game",
                        JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    resetGameData();
                    insertNewGameData();
                    JOptionPane.showMessageDialog(cardPanel,
                            "Game has been reset!",
                            "Game Reset",
                            JOptionPane.INFORMATION_MESSAGE);
                    // Proceed to Level Selection Panel after setting up the database
                    navigateToLevelSelection();  // Navigate to level selection
                } else {
                    // If the user selects "No", go back to Normal Mode
                    cardLayout.show(cardPanel, "Normal Mode");
                }
            } else {
                insertNewGameData();
                navigateToLevelSelection();  // Start a new game and navigate
            }
        });

        // Create Resume Game Button using BaseGame.createButton
        resumeGameButton = BaseGame.createButton("Resume Game Button.png", 550, -10, 276, 371, (ActionEvent e) -> {
            int levelToResume = getLevelToResume();  // Get the level to resume from the database
            if (isGameDataExists(levelToResume)) {  // Check if valid data exists for the level
                JOptionPane.showMessageDialog(cardPanel,
                        "Game progress found. Proceeding to Level " + levelToResume,
                        "Resume Game",
                        JOptionPane.INFORMATION_MESSAGE);

                // Navigate to Level Selection Panel with the level to resume
                navigateToLevelSelection(levelToResume);
            } else {
                JOptionPane.showMessageDialog(cardPanel,
                        "No progress found for Level " + levelToResume + ". Starting a new game.",
                        "No Progress",
                        JOptionPane.INFORMATION_MESSAGE);

                // Start a new game from Level 1 or the initial level
                navigateToLevelSelection();  // Start a new game and navigate
            }
        });

        // Create Back Button using BaseGame.createButton
        backButton = BaseGame.createButton("Normal Mode Back to Menu Button.png", 450, 500, 126, 50, (ActionEvent e) -> {
            cardLayout.show(cardPanel, "Main Menu");
        });

        // Add buttons to layered pane
        layeredPane.add(newGameButton, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(resumeGameButton, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(backButton, JLayeredPane.PALETTE_LAYER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(layeredPane, BorderLayout.CENTER);
        return panel;
    }

    // Navigate to Level Selection Panel
    private void navigateToLevelSelection() {
        String selectedTheme = CustomMode.getSelectedTheme();  // Retrieve the selected theme
        LevelSelection levelSelection = new LevelSelection(cardLayout, cardPanel, conn, selectedTheme, 1);  // Default level is 1
        cardPanel.add(levelSelection, "LevelSelection");
        cardLayout.show(cardPanel, "LevelSelection");
    }

    // Navigate to Level Selection Panel with the level to resume
    private void navigateToLevelSelection(int levelToResume) {
        String selectedTheme = CustomMode.getSelectedTheme();  // Retrieve the selected theme
        LevelSelection levelSelection = new LevelSelection(cardLayout, cardPanel, conn, selectedTheme, levelToResume);
        cardPanel.add(levelSelection, "LevelSelection");
        cardLayout.show(cardPanel, "LevelSelection");
    }

    // Get the level to resume from the database
    private int getLevelToResume() {
        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Database connection is not initialized!", "Error", JOptionPane.ERROR_MESSAGE);
            return 1; // Default to Level 1 if no connection is found
        }

        // Query to get the highest level that has progress saved
        try {
            String query = "SELECT MAX(level) FROM normalgamerecords WHERE time IS NOT NULL AND turn IS NOT NULL";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getInt(1);  // Return the highest level found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error checking game data.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return 1;  // Default to Level 1 if no progress found
    }

    // Check if the game data exists for the given level
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
                return rs.getInt(1) > 0;  // If there is data for the level, return true
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error checking game data.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;  // No valid data for the level
    }

    // Reset the game data in the database
    private void resetGameData() {
        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Database connection is not initialized!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String deleteQuery = "DELETE FROM normalgamerecords";  // Corrected table name
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(deleteQuery);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error resetting game data.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Insert new game data into the database
    private void insertNewGameData() {
        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Database connection is not initialized!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String insertQuery = "INSERT INTO normalgamerecords (level, time, turn) VALUES (1, '00:00:00', 0)"; // Set initial values
            PreparedStatement stmt = conn.prepareStatement(insertQuery);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error creating new game data.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
