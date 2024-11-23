package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class CustomMode {

    private JLabel themeLabel;
    private String[] themes = {"Spring", "Summer", "Autumn"};
    private int currentThemeIndex = 0;
    private static String selectedTheme = "Default";

    public JPanel createCustomModePanel(CardLayout cardLayout, JPanel cardPanel) {
        JPanel panel = new JPanel(null);

        // Theme Selection
        themeLabel = new JLabel(themes[currentThemeIndex], SwingConstants.CENTER);
        themeLabel.setBounds(520, 290, 100, 30);
        JButton themeLeftBtn = new JButton("<");
        themeLeftBtn.setBounds(485, 270, 50, 70);
        JButton themeRightBtn = new JButton(">");
        themeRightBtn.setBounds(600, 270, 50, 70);

        themeLeftBtn.addActionListener(e -> {
            currentThemeIndex = (currentThemeIndex - 1 + themes.length) % themes.length;
            themeLabel.setText(themes[currentThemeIndex]);
        });

        themeRightBtn.addActionListener(e -> {
            currentThemeIndex = (currentThemeIndex + 1) % themes.length;
            themeLabel.setText(themes[currentThemeIndex]);
        });

        panel.add(themeLabel);
        panel.add(themeLeftBtn);
        panel.add(themeRightBtn);

        // Start Game Button
        JButton startButton = new JButton("Start Game");
        startButton.setBounds(650, 550, 100, 50);
        startButton.addActionListener(e -> {
            setSelectedTheme(themes[currentThemeIndex]); // Save the selected theme
            System.out.println("Selected theme: " + selectedTheme); // Debugging log

            // Dynamically rebuild the Gameboard panel
            cardPanel.add(new Gameboard().createGameboardPanel(cardLayout, cardPanel), "Gameboard");

            cardLayout.show(cardPanel, "Gameboard");
        });
        panel.add(startButton);
        
        //Cancel Button
        JButton backButton = new JButton("Cancel");
        backButton.setBounds(450, 550, 100, 50);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Main Menu");
            }
        });
        panel.add(backButton);

        // Set Custom Mode Background
        File backgroundFile = new File("custommodebg.png");
        JLabel backgroundLabel;
        if (backgroundFile.exists()) {
            ImageIcon originalIcon = new ImageIcon(backgroundFile.getPath());
            Image scaledImage = originalIcon.getImage().getScaledInstance(815, 620, Image.SCALE_SMOOTH);
            backgroundLabel = new JLabel(new ImageIcon(scaledImage));
        } else {
            backgroundLabel = new JLabel("Background image not found", SwingConstants.CENTER);
            backgroundLabel.setForeground(Color.RED);
        }
        backgroundLabel.setBounds(0, 0, 815, 620);
        panel.add(backgroundLabel);

        return panel;
    }

    public static String getSelectedTheme() {
        return selectedTheme;
    }

    public static void setSelectedTheme(String theme) {
        selectedTheme = theme;
    }
}
