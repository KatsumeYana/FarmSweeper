package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;

public class NormalMode {

    private JButton newGameButton;
    private JButton resumeGameButton;
    private JButton backButton;
    private JButton okButton;
    private JLabel backgroundLabel;

    public JPanel createNormalModePanel(CardLayout cardLayout, JPanel cardPanel) {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);

        ImageIcon backgroundIcon = new ImageIcon("backgroundGame.png");
        backgroundLabel = new JLabel(backgroundIcon);

        Image image = backgroundIcon.getImage();
        Image scaledImage = image.getScaledInstance(815, 620, Image.SCALE_SMOOTH);
        backgroundLabel.setIcon(new ImageIcon(scaledImage));

        backgroundLabel.setBounds(0, 0, 815, 620);
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);

        newGameButton = new JButton();
        try {
            BufferedImage buttonIcon = ImageIO.read(new File("newGame.png"));
            ImageIcon buttonImageIcon = new ImageIcon(buttonIcon);
            newGameButton.setIcon(buttonImageIcon);
            newGameButton.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(cardPanel, "Error loading image for Normal Mode button!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        newGameButton.setBounds(20, -50, 400, 700);
        newGameButton.setBorderPainted(false);
        newGameButton.setFocusPainted(false);
        newGameButton.setContentAreaFilled(false);

        newGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Normal Mode");
            }
        });
        
        resumeGameButton = new JButton();
        try {
            BufferedImage buttonIcon = ImageIO.read(new File("resumeGame.png"));
            ImageIcon buttonImageIcon = new ImageIcon(buttonIcon);
            resumeGameButton.setIcon(buttonImageIcon);
            resumeGameButton.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(cardPanel, "Error loading image for Normal Mode button!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        resumeGameButton.setBounds(380, -50, 400, 700);
        resumeGameButton.setBorderPainted(false);
        resumeGameButton.setFocusPainted(false);
        resumeGameButton.setContentAreaFilled(false);

        resumeGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Normal Mode");
            }
        });

        backButton = new JButton("");
        try {
            BufferedImage buttonIcon = ImageIO.read(new File("backButton.png"));
            ImageIcon buttonImageIcon = new ImageIcon(buttonIcon);
            backButton.setIcon(buttonImageIcon);
            backButton.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(cardPanel, "Error loading image for Normal Mode button!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        backButton.setBounds(400, 470, 200, 105);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setContentAreaFilled(false);
        
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Main Menu");
            }
        });
        
        okButton = new JButton("");
        try {
            BufferedImage buttonIcon = ImageIO.read(new File("okButton.png"));
            ImageIcon buttonImageIcon = new ImageIcon(buttonIcon);
            okButton.setIcon(buttonImageIcon);
            okButton.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(cardPanel, "Error loading image for Normal Mode button!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        okButton.setBounds(580, 470, 200, 105);
        okButton.setBorderPainted(false);
        okButton.setFocusPainted(false);
        okButton.setContentAreaFilled(false);
        
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Main Menu");
            }
        });

        layeredPane.add(backButton, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(newGameButton, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(resumeGameButton, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(okButton, JLayeredPane.PALETTE_LAYER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(layeredPane, BorderLayout.CENTER);
        return panel;
    }
}
