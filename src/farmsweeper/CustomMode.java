package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;


public class CustomMode {

    private JLabel themeLabel;
    private JLabel difficultyLabel;
    private final String[] themes = {"Spring", "Summer", "Autumn"};
    private final String[] difficulties = {"Easy", "Normal", "Hard"};
    private int currentThemeIndex = 0;
    private int currentDifficultyIndex = 1; // Default to Normal
    private static String selectedTheme = "Spring"; // Default theme
    private static String selectedDifficulty = "Normal"; // Default difficulty
    
    public JPanel createCustomModePanel(CardLayout cardLayout, JPanel cardPanel) {
        JPanel layeredPanel = new JPanel(null);

        // Load custom font for text using CustomFont class
        Font textFont = CustomFont.loadCustomFont("PressStart2P-Regular.ttf", 18);

        // Theme Label
        themeLabel = new JLabel(themes[currentThemeIndex], SwingConstants.CENTER);
        themeLabel.setFont(textFont);
        themeLabel.setBounds(560, 330, 150, 30);

        // Left button to change theme
        String themeLeftIconPath = "Previous Button.png";
        JButton themeLeftBtn = createButton(themeLeftIconPath, 450, 305, (ActionEvent e) -> {
            currentThemeIndex = (currentThemeIndex - 1 + themes.length) % themes.length;
            themeLabel.setText(themes[currentThemeIndex]);
        });

        // Right button to change theme
        String themeRightIconPath = "Next Button.png";
        JButton themeRightBtn = createButton(themeRightIconPath, 670, 305, (ActionEvent e) -> {
            currentThemeIndex = (currentThemeIndex + 1) % themes.length;
            themeLabel.setText(themes[currentThemeIndex]);
        });

        // Difficulty Label
        difficultyLabel = new JLabel(difficulties[currentDifficultyIndex], SwingConstants.CENTER);
        difficultyLabel.setFont(textFont);
        difficultyLabel.setBounds(565, 430, 150, 30);
        
        // Left button to change difficulty
        String difficultyLeftIconPath = "Previous Button.png";
        JButton difficultyLeftBtn = createButton(difficultyLeftIconPath, 450, 400, (ActionEvent e) -> {
            currentDifficultyIndex = (currentDifficultyIndex - 1 + difficulties.length) % difficulties.length;
            difficultyLabel.setText(difficulties[currentDifficultyIndex]);
        });

        // Right button to change difficulty
        String difficultyRightIconPath = "Next Button.png";
        JButton difficultyRightBtn = createButton(difficultyRightIconPath, 670, 400, (ActionEvent e) -> {
            currentDifficultyIndex = (currentDifficultyIndex + 1) % difficulties.length;
            difficultyLabel.setText(difficulties[currentDifficultyIndex]);
        });

        // Start Game Button
        String okRightIconPath = "Level Selection Ok Button.png";
        JButton okBtn = createButton(okRightIconPath, 700, 600, (ActionEvent e) -> {
            setSelectedTheme(themes[currentThemeIndex]); // Save the selected theme
            setSelectedDifficulty(difficulties[currentDifficultyIndex]); // Save the selected difficulty
            System.out.println("Selected theme: " + selectedTheme); // Debugging log
            System.out.println("Selected difficulty: " + selectedDifficulty); // Debugging log

            GameboardGameLogic gameboard = new GameboardGameLogic(selectedDifficulty);
            // Dynamically rebuild the GameboardGameLogic panel
            cardPanel.add(gameboard.createGameboardPanel(cardLayout, cardPanel), "Gameboard");
            cardLayout.show(cardPanel, "Gameboard");
        });

        // Cancel Button
        String backIconPath = "Level Selection Cancel Button.png";
        JButton backButton = createButton(backIconPath, 500, 600, (ActionEvent e) -> {
            cardLayout.show(cardPanel, "Main Menu");
        });

        // Custom Mode Background
        File backgroundFile = new File("resources/images/custommodebg.png");
        JLabel backgroundLabel;
        if (backgroundFile.exists()) {
            ImageIcon originalIcon = new ImageIcon(backgroundFile.getPath());
            Image scaledImage = originalIcon.getImage().getScaledInstance(1000, 700, Image.SCALE_SMOOTH);
            backgroundLabel = new JLabel(new ImageIcon(scaledImage));
        } else {
            backgroundLabel = new JLabel("Background image not found", SwingConstants.CENTER);
            backgroundLabel.setForeground(Color.RED);
        }
        backgroundLabel.setBounds(0, 0, 1000, 700);
        
        // Adding components to the panel
        layeredPanel.add(themeLabel);
        layeredPanel.add(themeLeftBtn);
        layeredPanel.add(themeRightBtn);

        layeredPanel.add(difficultyLabel);
        layeredPanel.add(difficultyLeftBtn);
        layeredPanel.add(difficultyRightBtn);

        layeredPanel.add(okBtn);
        layeredPanel.add(backButton);

        layeredPanel.add(backgroundLabel);

        return layeredPanel;
    }

    //---MODEL--- 
    public static String getSelectedTheme() {
        return selectedTheme;
    }

    public static void setSelectedTheme(String theme) {
        selectedTheme = theme;
    }

    public static String getSelectedDifficulty() {
        return selectedDifficulty;
    }

    public static void setSelectedDifficulty(String difficulty) {
        selectedDifficulty = difficulty;
    }

    // Helper method to load images with a fallback
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
        return null; // Return null if image is not found or can't be loaded
    }

    // Helper method to create a button with an image icon and hover animation
    private JButton createButton(String iconPath, int x, int y, ActionListener action) {
        JButton button = new JButton();
        final ImageIcon buttonImageIcon = loadImage(iconPath);
        
        button.setIcon(buttonImageIcon);
        button.setText("");
        button.setBounds(x, y, 150, 85); // size of hover 
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.addActionListener(action);

        // Apply the hover effect using Animations class
        Animations.applyHoverEffect(button, buttonImageIcon);

        return button;
    }
}
