package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class CustomGameWin {

    private Leaderboard leaderboard;
    private String playerName;
    private String difficulty;
    private int timeTaken;
    private int turnsTaken;
    private float alpha = 0f; // Start with fully transparent

    public CustomGameWin() {}

    public CustomGameWin(Leaderboard leaderboard) {
        this.leaderboard = leaderboard;
    }

    public JPanel createWinPanel(CardLayout cardLayout, JPanel cardPanel, int timeElapsed, int turnCounter, String selectedDifficulty) {
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Set alpha for fading effect (ensure it's between 0.0f and 1.0f)
                AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(1.0f, Math.max(0.0f, alpha)));
                g2d.setComposite(alphaComposite);

                super.paintComponent(g);  // Draw content (buttons, images, etc.)
            }
        };

        panel.setLayout(null);  // Use null layout for custom positioning
        panel.setOpaque(false); // Set the panel to be transparent
        startFadeIn(panel);     // Start fade-in effect

        Font textFont = BaseGame.loadCustomFont("PressStart2P-Regular.ttf", 12);
        
        // Input Name Field with Placeholder
        JTextField inputName = new JTextField("Enter name");
        inputName.setBounds(390, 340, 150, 30);  
        inputName.setFont(textFont);
        inputName.setForeground(Color.GRAY);
        inputName.setBorder(null);  // Remove custom border
        inputName.setBackground(new Color(255, 202, 99));  // Yellow Gold color

        // Focus listener to handle placeholder text
        inputName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (inputName.getText().equals("Enter name")) {
                    inputName.setText(""); // Clear placeholder text
                    inputName.setForeground(Color.BLACK); // Set text color to black
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (inputName.getText().isEmpty()) {
                    inputName.setText("Enter name"); // Restore placeholder text
                    inputName.setForeground(Color.GRAY); // Set text color to gray
                }
            }
        });
        panel.add(inputName);

        // Home Button
        String homeButtonIconPath = "Home Button.png"; 
        JButton homeButton = BaseGame.createButton(homeButtonIconPath, 185, 420, 70, 62, e -> {
            cardLayout.show(cardPanel, "Main Menu");
            System.out.println("You pressed home button");
        });
        panel.add(homeButton);

        // Retry Button
        String retryButtonIconPath = "Retry Button.png"; 
        JButton retryButton = BaseGame.createButton(retryButtonIconPath, 285, 420, 69, 63, e -> {
            // Reset the gameboard state with the selected difficulty
            GameboardGameLogic gameboardLogic = new GameboardGameLogic(selectedDifficulty);
            JPanel newGameboardPanel = gameboardLogic.createGameboardPanel(cardLayout, cardPanel);
            cardPanel.add(newGameboardPanel, "Gameboard");
            cardLayout.show(cardPanel, "Gameboard");
            System.out.println("You pressed retry button");
        });
        panel.add(retryButton, Integer.valueOf(8));

        // Change Game Button
        String changeGameIconPath = "changegame.png";
        JButton changeGameButton = BaseGame.createButton(changeGameIconPath, 385, 420, 137, 58, e -> {
            cardLayout.show(cardPanel, "Custom Mode");  // Go to custom mode to change game settings
        });
        panel.add(changeGameButton, Integer.valueOf(8));

        // Save Button to save the record
        String addButtonIconPath = "addleaderboard.png"; 
        JButton addButton = BaseGame.createButton(addButtonIconPath, 535, 420, 186, 59,(ActionEvent e) -> {
            String namePlayer = inputName.getText().trim(); // Use the local inputName field

            if (!namePlayer.isEmpty() && !namePlayer.equals("Enter name")) {
                setPlayerName(namePlayer); // Save player name to instance variable

                // Add to leaderboard and save record before showing leaderboard
                leaderboard.addGameRecord(namePlayer, selectedDifficulty, formatTime(timeElapsed), turnCounter);

                // Show the leaderboard screen
                JPanel leaderboardPanel = leaderboard.createLeaderboardPanel(cardLayout, cardPanel);
                cardPanel.add(leaderboardPanel, "Leaderboard");
                cardLayout.show(cardPanel, "Leaderboard");

                JOptionPane.showMessageDialog(panel, "Record saved successfully!");
            } else {
                JOptionPane.showMessageDialog(panel, "Please enter a valid name!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(addButton, Integer.valueOf(8));

        // Custom Win Image
        JLabel winImageLabel = new JLabel();
        File winImageFile = new File("resources/images/Custom Mode Win Screen.png");  // Ensure correct path
        if (winImageFile.exists()) {
            ImageIcon originalIcon = new ImageIcon(winImageFile.getPath());
            Image scaledImage = originalIcon.getImage().getScaledInstance(900, 700, Image.SCALE_SMOOTH);
            winImageLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            winImageLabel.setText("Image not found");
            winImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
        winImageLabel.setBounds(0, 0, 815, 620);
        panel.add(winImageLabel, Integer.valueOf(5));

        return panel;
    }

    // Fade-in effect (updates alpha to make panel fade in)
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

    // Utility method to format time as MM:SS
    private String formatTime(int timeTaken) {
        int minutes = timeTaken / 60;
        int seconds = timeTaken % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // Getters and setters
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(int timeTaken) {
        this.timeTaken = timeTaken;
    }

    public int getTurnsTaken() {
        return turnsTaken;
    }

    public void setTurnsTaken(int turnsTaken) {
        this.turnsTaken = turnsTaken;
    }
}
