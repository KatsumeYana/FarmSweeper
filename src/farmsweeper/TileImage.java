package farmsweeper;

import javax.swing.*;
import java.awt.*;

public class TileImage {

    private JLabel label;
    private String currentImagePath;

     public TileImage(String theme) {
        label = new JLabel();
        setImageForTheme(theme);  // Set the initial theme-based tile image
    }

    public void setImageForTheme(String theme) {
        String imagePath;

        imagePath = switch (theme) {
            case "Spring" -> "resources/images/springTile.gif";
            case "Summer" -> "resources/images/summerTile.gif";
            case "Autumn" -> "resources/images/autumnTile.gif";
            default -> "resources/images/defaultTile.gif";
        };

        currentImagePath = imagePath;
        updateImage(imagePath);
    }

    public void updateImage(String imagePath) {
        ImageIcon icon = new ImageIcon(imagePath);
        label.setIcon(icon);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
    }

    public void setSoilImage(int adjacentMines) {
        String soilImagePath;

        if (adjacentMines == 0) {
            soilImagePath = "resources/images/soil1.png";
        } else if (adjacentMines >= 1 && adjacentMines <= 3) {
            soilImagePath = "resources/images/soil2.png";
        } else if (adjacentMines >= 4 && adjacentMines <= 6) {
            soilImagePath = "resources/images/soil3.png";
        } else if (adjacentMines >= 7 && adjacentMines <= 8) {
            soilImagePath = "resources/images/soil4.png";
        } else {
            // If no adjacent mines, keep the default or specific image
            soilImagePath = currentImagePath;
        }

        updateImage(soilImagePath);
    }
    
    public void setCrop(String theme){
        String cropImagePath;
        
        cropImagePath = switch (theme) {
            case "Spring" -> "resources/images/potao.png";
            case "Summer" -> "resources/images/melon.png";
            case "Autumn" -> "resources/images/pumpkin.png";
            default -> "resources/images/beet.png";
        };
    }

    public void setText(String text) {
        label.setText(text);
    }

    public JLabel getLabel() {
        return label;
    }
}
