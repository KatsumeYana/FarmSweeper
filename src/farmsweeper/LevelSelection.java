package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LevelSelection extends JPanel {
    private final Connection conn;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private Image backgroundImage;

    public LevelSelection(Connection conn, CardLayout cardLayout, JPanel cardPanel) {
        this.conn = conn;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;

        // Load background image
        backgroundImage = new ImageIcon("Level Selection.png").getImage();

        setLayout(new BorderLayout()); // Use BorderLayout for flexibility
        setOpaque(false); // Make the panel transparent to show the background image

        JPanel difficultyPanel = new JPanel(new GridBagLayout());
        difficultyPanel.setOpaque(false); // Transparent to show the background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 0, 0); // Spacing between sections
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Easy Section
        gbc.gridy = 0;
        difficultyPanel.add(createDifficultySection("Easy", 1), gbc);

        // Medium Section
        gbc.gridy = 1;
        difficultyPanel.add(createDifficultySection("Medium", 2), gbc);

        // Hard Section
        gbc.gridy = 2;
        difficultyPanel.add(createDifficultySection("Hard", 3), gbc);

        add(difficultyPanel, BorderLayout.CENTER);

        // Back Button
        JButton backButton = createBackButton();
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backPanel.setOpaque(false);
        backPanel.add(backButton);
        add(backPanel, BorderLayout.SOUTH);
    }

    private JPanel createDifficultySection(String difficultyName, int difficulty) {
        JPanel sectionPanel = new JPanel(new BorderLayout());
        sectionPanel.setOpaque(false);

        // Label for the difficulty
        JLabel difficultyLabel = new JLabel(difficultyName, JLabel.CENTER);
        difficultyLabel.setFont(new Font("Arial", Font.BOLD, 24));
        difficultyLabel.setForeground(Color.WHITE);
        sectionPanel.add(difficultyLabel, BorderLayout.NORTH);

        // Panel for the level buttons
        JPanel levelsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        levelsPanel.setOpaque(false);

        // Add level buttons for the given difficulty
        for (int level = 1; level <= 3; level++) {
            JButton levelButton = createLevelButton(difficulty, level);
            levelsPanel.add(levelButton);
        }

        sectionPanel.add(levelsPanel, BorderLayout.CENTER);
        return sectionPanel;
    }

    private JButton createLevelButton(int difficulty, int level) {
        JButton levelButton = new JButton();
        levelButton.setEnabled(isLevelUnlocked(difficulty, level));
        levelButton.setContentAreaFilled(false);
        levelButton.setBorderPainted(false);
        levelButton.setFocusPainted(false);

        // Set button image
        String iconPath = "Level " + difficulty + "-" + level + ".png";
        ImageIcon buttonIcon = new ImageIcon(iconPath);
        levelButton.setIcon(buttonIcon);

        // Add action listener
        levelButton.addActionListener(e -> startGame(difficulty, level));
        return levelButton;
    }

    private JButton createBackButton() {
        JButton backButton = new JButton();
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);

        // Set back button image
        ImageIcon backIcon = new ImageIcon("Home Button.png"); // Adjust the path if needed
        backButton.setIcon(backIcon);

        // Add action listener to navigate back to MainMenu
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "MainMenu"));
        return backButton;
    }

    private boolean isLevelUnlocked(int difficulty, int level) {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT MAX(level) AS max_level FROM normalgame");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int maxLevel = rs.getInt("max_level");
                return (difficulty - 1) * 3 + level <= maxLevel;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void startGame(int difficulty, int level) {
        NormalGameboardGameLogic gameLogic = new NormalGameboardGameLogic(level, difficulty, cardLayout, cardPanel);
        cardPanel.add(gameLogic, "GameBoard");
        cardLayout.show(cardPanel, "GameBoard");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // Draw background image
    }
}
    