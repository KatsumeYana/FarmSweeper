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

    private Connection conn;

    public NormalMode() {
        connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/farmsweeper", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error connecting to the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isGameDataExists() {
        try {
            String query = "SELECT COUNT(*) FROM normalgame";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getInt(1) > 0; // Check if there's any data
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,  "Error checking game data.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private void resetGameData() {
        try {
            String deleteQuery = "DELETE FROM normalgame";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(deleteQuery);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error resetting game data.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void insertNewGameData() {
        try {
            String insertQuery = "INSERT INTO normalgame (level, stars, time, turn) VALUES (1, 0, 0, 0)";
            PreparedStatement stmt = conn.prepareStatement(insertQuery);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error creating new game data.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public JPanel createNormalModePanel(CardLayout cardLayout, JPanel cardPanel) {
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

        // New Game Button
        String newGameIconPath = "New Game Button.png";
        JButton newGameButton = BaseGame.createButton(newGameIconPath, 200, -10, 276, 371, (ActionEvent e) -> {
                    if (isGameDataExists()) {
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
                        } else {
                            return; // Do nothing if the user chooses not to reset
                        }
                    } else {
                        insertNewGameData();
                    }

                    // Proceed to Level Selection
                    LevelSelection levelSelection = new LevelSelection(conn, cardLayout, cardPanel);
                    cardPanel.add(levelSelection, "LevelSelection");  // Add the LevelSelection panel directly
                    cardLayout.show(cardPanel, "LevelSelection");
                });


        // Resume Game Button
        String resumeGameIconPath = "Resume Game Button.png";
        JButton resumeGameButton = BaseGame.createButton(resumeGameIconPath, 550, -10, 276, 371, (ActionEvent e) -> {
            if (isGameDataExists()) {
                JOptionPane.showMessageDialog(cardPanel,
                        "Game progress found. Proceeding to Level Selection.",
                        "Resume Game",
                        JOptionPane.INFORMATION_MESSAGE);

                                    // Proceed to Level Selection
                       LevelSelection levelSelection = new LevelSelection(conn, cardLayout, cardPanel);
                       cardPanel.add(levelSelection, "LevelSelection");  // Add the LevelSelection panel directly
                       cardLayout.show(cardPanel, "LevelSelection");
                } else {
                JOptionPane.showMessageDialog(cardPanel,
                        "No progress found. Start a new game.",
                        "No Progress",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Back Button
        String backIconPath = "Normal Mode Back to Menu Button.png";
        JButton backButton = BaseGame.createButton(backIconPath, 400, 450, 126, 50, (ActionEvent e) -> {
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
}
