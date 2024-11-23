package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class CustomGameWin {

    public JPanel createWinPanel(CardLayout cardLayout, JPanel cardPanel) {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);

        // Custom Win Image
        JLabel winImageLabel = new JLabel();
         File winImageFile = new File("customwin.png");
            ImageIcon originalIcon = new ImageIcon(winImageFile.getPath());
            Image scaledImage = originalIcon.getImage().getScaledInstance(450, 342, Image.SCALE_SMOOTH);
            winImageLabel.setIcon(new ImageIcon(scaledImage));
        winImageLabel.setBounds(0, 0, 815, 620);
        panel.add(winImageLabel);

        return panel;
    }
}
