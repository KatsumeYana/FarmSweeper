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
        Font textFont = BaseGame.loadCustomFont("PressStart2P-Regular.ttf", 18);

        // Theme Label
        themeLabel = new JLabel(themes[currentThemeIndex], SwingConstants.CENTER);
        themeLabel.setFont(textFont);
        themeLabel.setBounds(560, 330, 150, 30);

        // Left button to change theme
        String themeLeftIconPath = "Previous Button.png";
        JButton themeLeftBtn = BaseGame.createButton(themeLeftIconPath, 500, 320, 63, 45, (ActionEvent e) -> {
            currentThemeIndex = (currentThemeIndex - 1 + themes.length) % themes.length;
            themeLabel.setText(themes[currentThemeIndex]);
        });

        // Right button to change theme
        String themeRightIconPath = "Next Button.png";
        JButton themeRightBtn = BaseGame.createButton(themeRightIconPath, 705, 320, 63, 43, (ActionEvent e) -> {
            currentThemeIndex = (currentThemeIndex + 1) % themes.length;
            themeLabel.setText(themes[currentThemeIndex]);
        });

        // Difficulty Label
        difficultyLabel = new JLabel(difficulties[currentDifficultyIndex], SwingConstants.CENTER);
        difficultyLabel.setFont(textFont);
        difficultyLabel.setBounds(565, 430, 150, 30);

        // Left button to change difficulty
        String difficultyLeftIconPath = "Previous Button.png";
        JButton difficultyLeftBtn = BaseGame.createButton(difficultyLeftIconPath, 500, 425, 63, 45, (ActionEvent e) -> {
            currentDifficultyIndex = (currentDifficultyIndex - 1 + difficulties.length) % difficulties.length;
            difficultyLabel.setText(difficulties[currentDifficultyIndex]);
        });

        // Right button to change difficulty
        String difficultyRightIconPath = "Next Button.png";
        JButton difficultyRightBtn = BaseGame.createButton(difficultyRightIconPath, 705, 425, 63, 43, (ActionEvent e) -> {
            currentDifficultyIndex = (currentDifficultyIndex + 1) % difficulties.length;
            difficultyLabel.setText(difficulties[currentDifficultyIndex]);
        });

        // Start Game Button
    String okRightIconPath = "Level Selection Ok Button.png";
    JButton okBtn = BaseGame.createButton(okRightIconPath, 700, 600, 128, 50, (ActionEvent e) -> {
        setSelectedTheme(themes[currentThemeIndex]); // Save the selected theme
        setSelectedDifficulty(difficulties[currentDifficultyIndex]); // Save the selected difficulty
        System.out.println("Selected theme: " + selectedTheme); // Debugging log
        System.out.println("Selected difficulty: " + selectedDifficulty); // Debugging log

        // Create the gameboard logic and panel
        GameboardGameLogic gameboard = new GameboardGameLogic(selectedDifficulty);

        // Create the new gameboard panel, passing the cardLayout and cardPanel for navigation
        JPanel newGameboardPanel = gameboard.createGameboardPanel(cardLayout, cardPanel);

        // Switch to the gameboard panel using CardLayout
        cardPanel.add(newGameboardPanel, "Gameboard");
        cardLayout.show(cardPanel, "Gameboard");
    });


        // Cancel Button
        String backIconPath = "Level Selection Cancel Button.png";
        JButton backButton = BaseGame.createButton(backIconPath, 500, 600, 128, 48, (ActionEvent e) -> {
            cardLayout.show(cardPanel, "Main Menu");
        });

        // Custom Mode Background 
        JLabel backgroundLabel = new JLabel();
        BaseGame.setBackground("resources/images/custommodebg.png", backgroundLabel, 1000, 700);
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

}