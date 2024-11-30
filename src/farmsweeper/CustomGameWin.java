package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.File;

public class CustomGameWin {

    private Leaderboard leaderboard;  // Add leaderboard instance
    private String playerName;
    private String difficulty;
    private int timeTaken;  // Changed to int for easier handling
    private int turnsTaken;

    // Constructor to accept the leaderboard instance
    public CustomGameWin(Leaderboard leaderboard) {
        this.leaderboard = leaderboard;
    }
    
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

    // Create win panel
    public JPanel createWinPanel(CardLayout cardLayout, JPanel cardPanel, int timeTaken, int turnsTaken, String difficulty) {
        // Set instance variables for timeTaken and turnsTaken
        setTimeTaken(timeTaken);
        setTurnsTaken(turnsTaken);
        setDifficulty(difficulty);

        JPanel cardpanel = new JPanel(null);
        cardpanel.setOpaque(false);

        // Input Name Field with Placeholder
        JTextField inputName = new JTextField("Enter name");
        inputName.setBounds(180, 320, 200, 30);  // Adjusted size and position for better UI experience
        inputName.setFont(new Font("Arial", Font.PLAIN, 16));
        inputName.setForeground(Color.GRAY);

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
        cardpanel.add(inputName);

        // Home Button
        JButton homeButton = new JButton("Home");
        homeButton.setBounds(100, 385, 100, 50);
        homeButton.addActionListener(e -> cardLayout.show(cardPanel, "Main Menu"));
        cardpanel.add(homeButton);

        // Retry Button
        JButton retryButton = new JButton("Retry");
        retryButton.setBounds(240, 385, 100, 50);
        retryButton.addActionListener(e -> {
            GameboardGameLogic gameboardLogic = new GameboardGameLogic(difficulty);
            JPanel newGameboardPanel = gameboardLogic.createGameboardPanel(cardLayout, cardPanel);
            cardPanel.add(newGameboardPanel, "Gameboard");
            cardLayout.show(cardPanel, "Gameboard");
        });
        cardpanel.add(retryButton);

        // Save Button to save the record
        JButton saveButton = new JButton("Save");
        saveButton.setBounds(400, 385, 100, 50);
        saveButton.addActionListener(e -> {
            String playerName = inputName.getText().trim();
            if (!playerName.isEmpty() && !playerName.equals("Enter name")) {
                setPlayerName(playerName); // Save player name to instance variable

                // Add to leaderboard and save record before showing leaderboard
                leaderboard.addGameRecord(playerName, difficulty, formatTime(timeTaken), turnsTaken);
                
                // Show the leaderboard screen
                JPanel leaderboardPanel = leaderboard.createLeaderboardPanel(cardLayout, cardPanel);
                cardPanel.add(leaderboardPanel, "Leaderboard");
                cardLayout.show(cardPanel, "Leaderboard");
                
                JOptionPane.showMessageDialog(cardpanel, "Record saved successfully!");
            } else {
                JOptionPane.showMessageDialog(cardpanel, "Please enter a valid name!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        cardpanel.add(saveButton);
        
        
        // Change Game Button
        JButton changeGameButton = new JButton("Change Game");
        changeGameButton.setBounds(400, 320, 130, 50);
        changeGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Custom Mode");  // Go to custom mode to change game settings
            }
        });
        cardpanel.add(changeGameButton, Integer.valueOf(1));
            
        // Custom Win Image
        JLabel winImageLabel = new JLabel();
        File winImageFile = new File("customwin.png");
        if (winImageFile.exists()) {
            ImageIcon originalIcon = new ImageIcon(winImageFile.getPath());
            Image scaledImage = originalIcon.getImage().getScaledInstance(450, 342, Image.SCALE_SMOOTH);
            winImageLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            winImageLabel.setText("Image not found");
            winImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
        winImageLabel.setBounds(0, 0, 815, 620);
        cardpanel.add(winImageLabel);

        return cardpanel;
    }

    private String formatTime(int timeTaken) {
        int minutes = timeTaken / 60;
        int seconds = timeTaken % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}