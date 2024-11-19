package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;

public class CustomMode {

    private JLabel backgroundLabel;
    private JLabel themeLabel;
    private JButton themeLeftBtn;
    private JButton themeRightBtn;
    private JLabel difficultyLabel;
    private JButton difficultyLeftBtn;
    private JButton difficultyRightBtn;
    
    private String[] themes = {"Spring", "Summer", "Autumn"};
    private String[] difficulties = {"Easy", "Normal", "Hard"};
    private int currentThemeIndex = 0; // Default index
    private int currentDifficultyIndex = 0; // Default index

    public JPanel createCustomModePanel(CardLayout cardLayout, JPanel cardPanel) {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);

        
        // Theme Selection
        themeLeftBtn = new JButton("<");
        themeRightBtn = new JButton(">");
        themeLabel = new JLabel("Spring"); 
        //themeLabel.setFont(new Font("PressStart2P-Regular.ttf", Font.BOLD, 20));
        themeLabel.setBounds(550, 290, 100, 30);
        themeLeftBtn.setBounds(485, 270, 50, 70);
        themeRightBtn.setBounds(600, 270, 50, 70);
        layeredPane.add(themeLabel);
        layeredPane.add(themeLeftBtn);
        layeredPane.add(themeRightBtn);

        // Difficulty Selection
        difficultyLeftBtn = new JButton("<");
        difficultyRightBtn = new JButton(">");
        difficultyLabel = new JLabel("Easy", SwingConstants.CENTER); 
        //difficultyLabel.setFont(new Font("PressStart2P-Regular.ttf", Font.BOLD, 20));
        difficultyLabel.setBounds(515, 385, 100, 30);
        difficultyLeftBtn.setBounds(485, 365, 50, 70);
        difficultyRightBtn.setBounds(600, 365, 50, 70);
        layeredPane.add(difficultyLabel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(difficultyLeftBtn, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(difficultyRightBtn, JLayeredPane.PALETTE_LAYER);

        // Add Action Listeners for Buttons
        themeLeftBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextThemeLeft();
                themeLabel.setText(getCurrentTheme());
            }
        });

        themeRightBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextThemeRight();
                themeLabel.setText(getCurrentTheme());
            }
        });

        difficultyLeftBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextDifficultyLeft();
                difficultyLabel.setText(getCurrentDifficulty());
            }
        });

        difficultyRightBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextDifficultyRight();
                difficultyLabel.setText(getCurrentDifficulty());
            }
        });
        
        JButton backButton = new JButton("Back to Main Menu");
        backButton.setBounds(440, 550, 150, 50);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Main Menu");
            }
        });
        
        JButton okButton = new JButton("Ok");
        okButton.setBounds(680, 550, 50, 50);
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Gameboard");
            }
        });
        
        layeredPane.add(backButton);
        layeredPane.add(okButton);

        ImageIcon backgroundIcon = new ImageIcon("custommodebg.png");
        backgroundLabel = new JLabel(backgroundIcon);
        
        Image image = backgroundIcon.getImage();
        Image scaledImage = image.getScaledInstance(815, 620, Image.SCALE_SMOOTH);
        backgroundLabel.setIcon(new ImageIcon(scaledImage));
        
        backgroundLabel.setBounds(0, 0, 815, 620);
        layeredPane.add(backgroundLabel);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(layeredPane, BorderLayout.CENTER);
        return panel;
    }
    
    public String getCurrentTheme() {
        return themes[currentThemeIndex];
    }

    // Getter for current difficulty
    public String getCurrentDifficulty() {
        return difficulties[currentDifficultyIndex];
    }

    // Navigate themes
    public void nextThemeLeft() {
        currentThemeIndex = (currentThemeIndex - 1 + themes.length) % themes.length;
    }

    public void nextThemeRight() {
        currentThemeIndex = (currentThemeIndex + 1) % themes.length;
    }

    // Navigate difficulties
    public void nextDifficultyLeft() {
        currentDifficultyIndex = (currentDifficultyIndex - 1 + difficulties.length) % difficulties.length;
    }

    public void nextDifficultyRight() {
        currentDifficultyIndex = (currentDifficultyIndex + 1) % difficulties.length;
    }
}
