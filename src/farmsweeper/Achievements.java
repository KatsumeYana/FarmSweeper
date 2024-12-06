package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.sql.*;

public class Achievements {
    
    private JLabel backgroundLabel;
    private JLabel weedWhacker;
    private JLabel cropGuardian;
    private JLabel speedFarmer;
    private JLabel masterHarvester;
    private Connection dbConnection;

    public Achievements() {
        // Initialize database connection
        try {
            dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/farmsweeper", "root", "Juliana");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public JPanel createAchievementsPanel(CardLayout cardLayout, JPanel cardPanel) {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);

        // Weed Whacker Achievement
        weedWhacker = createAchievementLabel("Weed Whacker",
                "resources/images/Weed Whacker Locked.png",
                "resources/images/Weed Whacker Unlocked.png",
                216, 190, cardPanel, this::checkWeedWhacker);
        
        // Crop Guardian Achievement (Locked)
        cropGuardian = new JLabel();
        try {
            BufferedImage labelIcon = ImageIO.read(new File("resources/images/Crop Guardian Locked.png"));
            ImageIcon labelImageIcon = new ImageIcon(labelIcon);
            cropGuardian.setIcon(labelImageIcon);
            cropGuardian.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(cardPanel, "Error loading image for Crop Guardian achievement!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        cropGuardian.setBounds(600, 190, 200, 200);
        cropGuardian.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(cardPanel, "This achievement is locked.");
            }
        });

        // Speed Farmer Achievement
        speedFarmer = createAchievementLabel("Speed Farmer",
                "resources/images/Speed Farmer Locked.png",
                "resources/images/Speed Farmer Unlocked.png",
                216, 430, cardPanel, this::checkSpeedFarmer);

        // Master Harvester Achievement
        masterHarvester = createAchievementLabel("Master Harvester",
                "resources/images/Master Harvester Locked.png",
                "resources/images/Master Harvester Unlocked.png",
                600, 430, cardPanel, this::checkMasterHarvester);

        // Home Button
        JButton backButton = BaseGame.createButton("Home Button.png", 10, 10, 70, 62, e -> {
            cardLayout.show(cardPanel, "Main Menu");
        });

        // Add components to layeredPane
        layeredPane.add(weedWhacker, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(speedFarmer, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(masterHarvester, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(backButton, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(cropGuardian, JLayeredPane.PALETTE_LAYER);
        
        // Background
        try {
            ImageIcon backgroundIcon = new ImageIcon("resources/images/achieveBackground.png");
            backgroundLabel = new JLabel(backgroundIcon);
            Image image = backgroundIcon.getImage();
            Image scaledImage = image.getScaledInstance(1000, 700, Image.SCALE_SMOOTH);
            backgroundLabel.setIcon(new ImageIcon(scaledImage));
            backgroundLabel.setBounds(0, 0, 1000, 700);
            layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(cardPanel, "Error loading background image!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Panel Setup
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(layeredPane, BorderLayout.CENTER);
       
        return panel;
    }

    private JLabel createAchievementLabel(String name, String lockedPath, String unlockedPath, int x, int y, JPanel cardPanel, AchievementCheck check) {
        JLabel label = new JLabel();
        try {
            // Check if the achievement is unlocked
            boolean isUnlocked = check.isAchieved();

            // Load the correct image based on the achievement status
            BufferedImage labelIcon = ImageIO.read(new File(isUnlocked ? unlockedPath : lockedPath));
            ImageIcon labelImageIcon = new ImageIcon(labelIcon);
            label.setIcon(labelImageIcon);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(cardPanel, "Error loading image for " + name + " achievement!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        label.setBounds(x, y, 200, 200);
        label.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(cardPanel, check.isAchieved() ? name + " is unlocked!" : "This achievement is locked.");
            }
        });
        return label;
    }

    private boolean checkWeedWhacker() {
        try (Statement stmt = dbConnection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT goal_level FROM achievements WHERE achievement_id = 1");
            if (rs.next() && rs.getInt("goal_level") == 9) {
                return true; // Weed Whacker is unlocked if 9 levels are completed
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean checkSpeedFarmer() {
        try (Statement stmt = dbConnection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT goal_time_level6, goal_time_level9 FROM achievements WHERE achievement_id = 1");
            if (rs.next()) {
                Time level6Time = rs.getTime("goal_time_level6");
                Time level9Time = rs.getTime("goal_time_level9");
                // Speed Farmer is unlocked if both conditions are satisfied
                return level6Time.compareTo(Time.valueOf("180")) < 0 && level9Time.compareTo(Time.valueOf("600")) < 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean checkMasterHarvester() {
        try (Statement stmt = dbConnection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT goal_turn_level6, goal_turn_level9 FROM achievements WHERE achievement_id = 1");
            if (rs.next()) {
                int level6Turns = rs.getInt("goal_turn_level6");
                int level9Turns = rs.getInt("goal_turn_level9");
                // Master Harvester is unlocked if both conditions are satisfied
                return level6Turns < 50 && level9Turns < 100;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @FunctionalInterface
    interface AchievementCheck {
        boolean isAchieved();
    }
}
