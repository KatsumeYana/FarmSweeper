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
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final Connection conn;

    public NormalMode(CardLayout cardLayout, JPanel cardPanel, Connection conn) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.conn = conn;
        
        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Database connection is not initialized!", "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("Connection initialized in NormalMode");

        // Pass the connection to LevelSelection
        LevelSelection levelSelection = new LevelSelection(cardLayout, cardPanel, conn);
        JPanel levelSelectionPanel = levelSelection.createLevelSelectionPanel();
        cardPanel.add(levelSelectionPanel, "Level Selection");
    }

    public JPanel createNormalModePanel() {
        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Database connection is not initialized!", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
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

        // Create New Game Button with a confirmation dialog for resetting data
        newGameButton = BaseGame.createButton("New Game Button.png", 200, -10, 276, 371, (ActionEvent e) -> {
            int response = JOptionPane.showConfirmDialog(cardPanel, "Are you sure you want to start a new game? All saved data will be erased.", "New Game Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                resetGameData(); // Reset data for a new game
                navigateToLevelSelection(1); // Start at level 1
            } else {
                System.out.println("User canceled new game.");
            }
        });

        // Create Resume Game Button
        resumeGameButton = BaseGame.createButton("Resume Game Button.png", 550, -10, 276, 371, (ActionEvent e) -> {
            int levelToResume = getLevelToResume();  // Retrieve the level to resume
            if (isGameDataExists(levelToResume)) {
                JOptionPane.showMessageDialog(cardPanel, "Saved Data on Level " + levelToResume, "Resume Game", JOptionPane.INFORMATION_MESSAGE);
                navigateToLevelSelection(levelToResume); // Resume from the saved level
            } else {
                JOptionPane.showMessageDialog(cardPanel, "No saved game data found. Starting a new game.", "No Progress", JOptionPane.WARNING_MESSAGE);
                navigateToLevelSelection(1); // Start a new game
            }
        });

        // Create Back Button
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
    private void navigateToLevelSelection(int levelToResume) {
        LevelSelection levelSelection = new LevelSelection(cardLayout, cardPanel, conn);
        JPanel levelSelectionPanel = levelSelection.createLevelSelectionPanel();  // Correct method call
        cardPanel.add(levelSelectionPanel, "Level Selection");
        cardLayout.show(cardPanel, "Level Selection");
    }

    // Reset the game data in the database
    private void resetGameData() {
        try {
            String deleteQuery = "DELETE FROM normalgamerecords";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(deleteQuery);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error resetting game data.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Check if the game data exists for the given level
    private boolean isGameDataExists(int level) {
        try {
            String query = "SELECT COUNT(*) FROM normalgamerecords WHERE level = ? AND time IS NOT NULL AND turn IS NOT NULL";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, level);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Retrieve the highest level with saved progress
    private int getLevelToResume() {
        try {
            String query = "SELECT MAX(level) FROM normalgamerecords WHERE time IS NOT NULL AND turn IS NOT NULL";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;  // Default to level 1 if no progress exists
    }
}
