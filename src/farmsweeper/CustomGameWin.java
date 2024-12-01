package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.border.Border;

public class CustomGameWin {

    private Leaderboard leaderboard;
    private String playerName;
    private String difficulty;
    private int timeTaken;
    private int turnsTaken;

    public CustomGameWin(Leaderboard leaderboard) {
        this.leaderboard = leaderboard;
    }

    public JPanel createWinPanel(CardLayout cardLayout, JPanel cardPanel, int timeTaken, int turnsTaken, String difficulty) {
        setTimeTaken(timeTaken);
        setTurnsTaken(turnsTaken);
        setDifficulty(difficulty);

        FadingPanel panel = new FadingPanel(null);
        panel.setOpaque(false);

        // Font
        Font textfont = CustomFont.loadCustomFont("PressStart2P-Regular.ttf", 12);

        // Input Name Field with Placeholder
        JTextField inputName = new JTextField("Enter name");
        inputName.setBounds(370, 340, 180, 30);
        inputName.setFont(textfont);
        inputName.setForeground(Color.GRAY);
        inputName.setBorder(null);  // Remove custom border
        inputName.setBackground(new Color(255, 202, 99));  // Yellow Gold color

        inputName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (inputName.getText().equals("Enter name")) {
                    inputName.setText("");  // Clear placeholder text
                    inputName.setForeground(Color.BLACK);  // Set text color to black when typing
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (inputName.getText().isEmpty()) {
                    inputName.setText("Enter name");
                    inputName.setForeground(Color.GRAY);  // Restore placeholder color
                }
            }
        });
        panel.add(inputName, Integer.valueOf(2));

        // Home Button
        JButton homeButton = createButton("Home Button.png", 200, 420, 70, 62, e -> {
            cardLayout.show(cardPanel, "Main Menu");
            System.out.println("Home button pressed");
        });
        panel.add(homeButton, Integer.valueOf(1));

        // Retry Button
        JButton retryButton = createButton("Retry Button.png", 300, 420, 69, 63, e -> {
            GameboardGameLogic gameboardLogic = new GameboardGameLogic(difficulty);
            JPanel newGameboardPanel = gameboardLogic.createGameboardPanel(cardLayout, cardPanel);
            cardPanel.add(newGameboardPanel, "Gameboard");
            cardLayout.show(cardPanel, "Gameboard");
            System.out.println("Retry button pressed");
        });
        panel.add(retryButton);

        // Change Game Button
        JButton changeGameButton = createButton("changegame.png", 400, 420, 137, 58, e -> {
            cardLayout.show(cardPanel, "Custom Mode");
        });
        panel.add(changeGameButton, Integer.valueOf(1));

        // Save Button to save the record
        JButton addButton = createButton("addleaderboard.png", 550, 420, 186, 59, e -> {
            String namePlayer = inputName.getText().trim();
            if (!namePlayer.isEmpty() && !namePlayer.equals("Enter name")) {
                setPlayerName(namePlayer);
                leaderboard.addGameRecord(namePlayer, difficulty, formatTime(timeTaken), turnsTaken);

                // Show leaderboard panel
                JPanel leaderboardPanel = leaderboard.createLeaderboardPanel(cardLayout, cardPanel);
                cardPanel.add(leaderboardPanel, "Leaderboard");
                cardLayout.show(cardPanel, "Leaderboard");

                JOptionPane.showMessageDialog(panel, "Record saved successfully!");
            } else {
                JOptionPane.showMessageDialog(panel, "Please enter a valid name!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(addButton, Integer.valueOf(1));

        // Custom Win Image
        JLabel winImageLabel = new JLabel();
        File winImageFile = new File("resources/images/Custom Mode Win Screen.png");
        if (winImageFile.exists()) {
            ImageIcon originalIcon = new ImageIcon(winImageFile.getPath());
            Image scaledImage = originalIcon.getImage().getScaledInstance(900, 700, Image.SCALE_SMOOTH);
            winImageLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            winImageLabel.setText("Image not found");
            winImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
        winImageLabel.setBounds(0, 0, 815, 620);
        panel.add(winImageLabel, Integer.valueOf(0));

        // Apply fade-in animation
        Animations.fadeIn(panel, 10000);  // Fade in the win panel over 2 seconds

        return panel;
    }

    private JButton createButton(String iconPath, int x, int y, int z, int w, ActionListener action) {
        JButton button = new JButton();
        final ImageIcon buttonImageIcon = loadImage(iconPath);

        button.setIcon(buttonImageIcon);
        button.setText("");
        button.setBounds(x, y, z, w);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.addActionListener(action);

        // Apply the hover effect using Animations class
        Animations.applyHoverEffect(button, buttonImageIcon);
        
        return button;
    }

    private ImageIcon loadImage(String path) {
        File imageFile = new File("resources/images/" + path);
        if (imageFile.exists()) {
            try {
                BufferedImage bufferedImage = ImageIO.read(imageFile);
                return new ImageIcon(bufferedImage);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error loading image: " + path, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.err.println("Image not found: " + path);
        }
        return null;
    }

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
