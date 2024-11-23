package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class CustomGameLose {

    public JPanel createLosePanel(CardLayout cardLayout, JPanel cardPanel) {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);

        // Custom Lose Image
        JLabel loseImageLabel = new JLabel();
        File loseImageFile = new File("customlose.png");
            ImageIcon originalIcon = new ImageIcon(loseImageFile.getPath());
            Image scaledImage = originalIcon.getImage().getScaledInstance(450, 342, Image.SCALE_SMOOTH);
            loseImageLabel.setIcon(new ImageIcon(scaledImage));
        
        loseImageLabel.setBounds(0, 0, 815, 620);
        panel.add(loseImageLabel);

        return panel;
    }
}
