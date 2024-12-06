package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MineTile extends JButton {
    int r, c;
    boolean isFlagged;
    boolean isRevealed;
    boolean isMine;
    private JLabel tileLabel;
    private final String theme;

    public MineTile(int r, int c, String theme) {
        this.r = r;
        this.c = c;
        this.isFlagged = false;
        this.isRevealed = false;
        this.isMine = false;
        this.theme = theme;

        // Create the label for the tile's image
        tileLabel = new JLabel();
        setLayout(new BorderLayout());
        add(tileLabel, BorderLayout.CENTER);  // Add TileImage label to the center of the button

        // Set initial tile appearance
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);

        // Set the initial tile image based on the selected theme
        setTileImage(theme, -1);  // Default tile image before it's revealed
    }

    // Method to set the appropriate soil image based on the number of adjacent mines
    public void setTileImage(String theme, int adjacentMines) {
        String tileImagePath = "";

        // If the tile is not revealed yet, set the initial image based on the selected theme
        if (adjacentMines == -1) {
            // Initial theme images before reveal
            switch (theme) {
                case "Spring":
                    tileImagePath = "resources/images/springTile.gif";
                    break;
                case "Summer":
                    tileImagePath = "resources/images/summerTile.gif";
                    break;
                case "Autumn":
                    tileImagePath = "resources/images/autumnTile.gif";
                    break;
                default:
                    tileImagePath = "resources/images/defaultTile.gif";
                    break;
            }
        }
        // Otherwise, show soil images based on adjacent mines
        else {
            if (adjacentMines == 0) {
                tileImagePath = "resources/images/soil.png";  // Soil image for no adjacent mines
            } else {
                tileImagePath = "resources/images/soil" + adjacentMines + ".png";  // Soil image with corresponding number
            }
        }

        // Set the image icon for the tile
        tileLabel.setIcon(loadImage(tileImagePath));  // Load image with fallback
        tileLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tileLabel.setVerticalAlignment(SwingConstants.CENTER);
    }

    // Method to reveal the tile
    public void reveal(String theme, int adjacentMines) {
        if (isRevealed) return;

        isRevealed = true;
        setTileImage(theme, adjacentMines);  // Update the image based on adjacent mines

        // If it's a mine, display a mine image
        if (isMine) {
            revealCrop(theme); // Show a mine image if it's a mine
        }
    }

    // Method to reveal the mine (bomb)
    public void revealCrop(String theme) {
        isRevealed = true;  
        String cropImagePath = switch (theme) {
            case "Spring" -> "resources/images/potao.png";
            case "Summer" -> "resources/images/melon.png";
            case "Autumn" -> "resources/images/pumpkin.png";
            default -> "none";
        };
        // Set the flagged image
        tileLabel.setIcon(loadImage(cropImagePath));
        tileLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tileLabel.setVerticalAlignment(SwingConstants.CENTER);
    }

    // Method to set the flag
    public void setFlag(String theme) {
        isFlagged = true;
        String flaggedImagePath = getFlaggedImagePath(theme);
        tileLabel.setIcon(loadImage(flaggedImagePath));
        tileLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tileLabel.setVerticalAlignment(SwingConstants.CENTER);
    }

    // Method to clear the flag
    public void clearFlag(String theme) {
        isFlagged = false;
        setTileImage(theme, -1);  // Reset to the default theme image (non-flagged)
    }

    // Helper method to get the flagged image path based on the theme
    private String getFlaggedImagePath(String theme) {
        return switch (theme) {
            case "Spring" -> "resources/images/springTileFlagged.gif";
            case "Summer" -> "resources/images/summerTileFlagged.gif";
            case "Autumn" -> "resources/images/autumnTileFlagged.gif";
            default -> "resources/images/springTileFlagged.gif";
        };
    }

    // Helper method to load images with fallback
    private ImageIcon loadImage(String imagePath) {
        File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            return new ImageIcon(imagePath);
        } else {
            System.err.println("Image not found: " + imagePath);
            return new ImageIcon("resources/images/defaultTile.gif");  // Fallback image
        }
    }
}
