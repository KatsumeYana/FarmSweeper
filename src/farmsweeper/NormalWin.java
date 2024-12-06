package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;

public class NormalWin {

    private int timeElapsed;
    private int turnCounter;
    private int level;
    private float alpha = 0f; // Start with fully transparent
    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    public NormalWin(int level, int timeElapsed, int turnCounter, CardLayout cardLayout, JPanel cardPanel) {
        this.level = level;
        this.timeElapsed = timeElapsed;
        this.turnCounter = turnCounter;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
    }

    // Method to create the win panel and save game data
    public JPanel createWinPanel() {
        JPanel panel = new JPanel(null){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Ensure the panel has a transparent background
                setOpaque(false);

                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Set alpha for fading effect, ensure it's within the range [0.0f, 1.0f]
                AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(1.0f, Math.max(0.0f, alpha)));
                g2d.setComposite(alphaComposite);

                super.paintComponent(g);  // Draw the content
            }
        };

        
        panel.setLayout(null);  // Use null layout for custom positioning
        startFadeIn(panel);     // Start fade-in effect
        
        // Get the connection from the DatabaseConnection class
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Database connection is not initialized!", "Error", JOptionPane.ERROR_MESSAGE);
            return panel;  // Return empty panel in case of an error
        }

        // Save the game data (time, turn, and level) into the database
        saveWinData(conn); // Pass the connection to the saveWinData method

        panel.setLayout(null);  // Use null layout for custom positioning
        panel.setOpaque(false); // Set the panel to be transparent

        Font textFont = BaseGame.loadCustomFont("PressStart2P-Regular.ttf", 12);

        // Add buttons for "Home", "Retry", and "Next"
        String homeButtonIconPath = "Home Button.png"; 
        JButton homeButton = BaseGame.createButton(homeButtonIconPath, 290, 420, 70, 62, e -> {
            cardLayout.show(cardPanel, "Main Menu");
            System.out.println("You pressed home button");
        });
        panel.add(homeButton);

        String retryButtonIconPath = "Retry Button.png"; 
        JButton retryButton = BaseGame.createButton(retryButtonIconPath, 400, 420, 69, 63, e -> {
            // Reset the gameboard state with the selected difficulty
            NormalGameboardGameLogic gameboardLogic = new NormalGameboardGameLogic(level, cardLayout, cardPanel, "Spring");  
            JPanel gameboardPanel = gameboardLogic.createNormalGameboardPanel();
            cardPanel.add(gameboardPanel, "Normal Gameboard");
            cardLayout.show(cardPanel, "Normal Gameboard");
            System.out.println("You pressed retry button");
        });
        panel.add(retryButton);

        String nextIconPath = "Normal Mode Next Button.png";
        JButton nextButton  = BaseGame.createButton(nextIconPath, 500, 420, 127, 48, e -> {
            goToNextLevel();
        });
        panel.add(nextButton);

        // Custom Win Image
        JLabel winImageLabel = new JLabel();
        File winImageFile = new File("resources/images/Normal Mode Win.png");  
        if (winImageFile.exists()) {
            ImageIcon originalIcon = new ImageIcon(winImageFile.getPath());
            Image scaledImage = originalIcon.getImage().getScaledInstance(1000, 700, Image.SCALE_SMOOTH);
            winImageLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            winImageLabel.setText("Image not found");
            winImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
        winImageLabel.setBounds(0, 0, 815, 620);
        panel.add(winImageLabel, Integer.valueOf(5));

        // Start the fade-in effect after the panel is created
        startFadeIn(panel);

        return panel;
    }

    // Save win data to the database
    private void saveWinData(Connection conn) {
        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Database connection is not initialized!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Check if the record already exists for the given level
            String checkQuery = "SELECT COUNT(*) FROM normalgamerecords WHERE level = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, level);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                // If a record exists, update the existing record
                String updateQuery = "UPDATE normalgamerecords SET time = ?, turn = ? WHERE level = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setInt(1, timeElapsed);  // Update time
                updateStmt.setInt(2, turnCounter);  // Update turn count
                updateStmt.setInt(3, level);  // Set the level
                updateStmt.executeUpdate();
                System.out.println("Win data updated successfully!");
            } else {
                // If no record exists, insert a new one
                String insertQuery = "INSERT INTO normalgamerecords (level, time, turn) VALUES (?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setInt(1, level);
                insertStmt.setInt(2, timeElapsed);  // Save the time in seconds or a formatted string
                insertStmt.setInt(3, turnCounter);
                insertStmt.executeUpdate();
                System.out.println("Win data saved successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saving win data.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Go to the next level (you can implement level progression here)
    private void goToNextLevel() {
        // Increment the current level
        level++;

        // Get the theme based on the new level
        String theme = getThemeForLevel(level);

        // Pass the updated level and theme to the game logic
        NormalGameboardGameLogic gameboardLogic = new NormalGameboardGameLogic(level, cardLayout, cardPanel, theme);  
        JPanel gameboardPanel = gameboardLogic.createNormalGameboardPanel();
        cardPanel.add(gameboardPanel, "Normal Gameboard");
        cardLayout.show(cardPanel, "Normal Gameboard");  // Show the updated gameboard panel

        System.out.println("You pressed next button, going to level " + level);
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

    // Fade-in effect for the panel to appear gradually
    private void startFadeIn(JPanel panel) {
        Timer fadeInTimer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (alpha < 1f) {
                    alpha += 0.05f;  // Increase opacity
                    panel.repaint();  // Repaint the panel with updated opacity
                } else {
                    ((Timer) e.getSource()).stop();  // Stop the fade-in effect when fully opaque
                }
            }
        });
        fadeInTimer.start();
    }
}
