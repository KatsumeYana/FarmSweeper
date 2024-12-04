package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.sql.*;

public class BaseGame {

    // Hover effect for a JButton (resizing the icon when mouse enters and exits)
    public static void applyHoverEffect(JButton button, ImageIcon initialIcon) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                ImageIcon newIcon = new ImageIcon(initialIcon.getImage().getScaledInstance(
                        initialIcon.getIconWidth() + 5, initialIcon.getIconHeight() + 5, Image.SCALE_SMOOTH
                ));
                button.setIcon(newIcon);
                button.revalidate();
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                ImageIcon newIcon = new ImageIcon(initialIcon.getImage().getScaledInstance(
                        initialIcon.getIconWidth() - 5, initialIcon.getIconHeight() - 5, Image.SCALE_SMOOTH
                ));
                button.setIcon(newIcon);
                button.revalidate();
                button.repaint();
            }
        });
    }
    
    // Load custom font from file
    public static Font loadCustomFont(String fontPath, float size) {
        try {
            File fontFile = new File(fontPath);
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            return font.deriveFont(size);
        } catch (FontFormatException | IOException e) {
            System.out.println("Error loading font: " + e.getMessage());
            return new Font("Arial", Font.PLAIN, 14); // Fallback to Arial if loading fails
        }
    }
    
    // Helper method to create a button with image icon and hover effect
    public static JButton createButton(String iconPath, int x, int y, int width, int height, ActionListener action) {
        JButton button = new JButton();
        final ImageIcon buttonImageIcon = loadImage(iconPath); // Use loadImage from BaseGame

        button.setIcon(buttonImageIcon);
        button.setText("");
        button.setBounds(x, y, width, height);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.addActionListener(action);

        // Apply the hover effect using Animations class
        applyHoverEffect(button, buttonImageIcon);

        return button;
    }
    
    
    
    // Method to connect to the database
    public static Connection connectToDatabase() {
        String url = "jdbc:mysql://localhost:3306/farmsweeper";  // Update with your actual database details
        String user = "root";  // Update with your actual username
        String password = "Juliana";  // Update with your actual password
        
        Connection conn = null;
        try {
            // Make sure to load the JDBC driver if needed (for older versions of MySQL JDBC driver)
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish connection
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database.");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
        }
        return conn;
    }
    
    static String getImagePathForTheme(String theme) {
        return switch (theme) {
            case "Spring" -> "resources/images/Spring.png";
            case "Summer" -> "resources/images/Summer.png";
            case "Autumn" -> "resources/images/Autumn.png";
            default -> "No chosen image";
        };
    }
    
    //Setting of BG THEME
    public static void setBackgroundForTheme(String theme, JLabel label, int width, int height) {
        label.setBounds(0, 0, width, height);  // Set the bounds for the label
        String imagePath = getImagePathForTheme(theme);
        File imageFile = new File(imagePath);

        if (imageFile.exists()) {
            ImageIcon originalIcon = new ImageIcon(imagePath);
            Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaledImage));
        } else {
            System.err.println("Background image not found for theme: " + theme);
            label.setText("No theme image found for: " + theme);
            label.setHorizontalAlignment(SwingConstants.CENTER);
        }
    }
    //settin bg
    public static void setBackground(String filePath, JLabel label, int width, int height) {
        File backgroundFile = new File(filePath);
        if (backgroundFile.exists()) {
            ImageIcon originalIcon = new ImageIcon(backgroundFile.getPath());
            Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaledImage));
        } else {
            label.setText("Background image not found");
            label.setForeground(Color.RED);
        }
        label.setBounds(0, 0, width, height);
    }
    
    // Method to load images with a fallback
    public static ImageIcon loadImage(String path) {
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
    // Method to create an image label for a given image file path
    public static JLabel createImageLabel(String imagePath, int width, int height) {
        JLabel imageLabel = new JLabel();
        File imageFile = new File(imagePath);
        
        if (imageFile.exists()) {
            ImageIcon originalIcon = new ImageIcon(imageFile.getPath());
            Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            imageLabel.setText("Image not found!");
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imageLabel.setForeground(Color.RED);
        }
        
        imageLabel.setBounds(0, 0, width, height);  // Adjust size as needed
        return imageLabel;
    }
}
