
package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;

public class Achievements {

    private JLabel backgroundLabel;
    private JLabel weedWhacker;
    private JLabel cropGuardian;
    private JLabel speedFarmer;
    private JLabel masterHarvester;
    private JButton backButton;

    public JPanel createAchievementsPanel(CardLayout cardLayout, JPanel cardPanel) {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);

        ImageIcon backgroundIcon = new ImageIcon("achieveBackground.png");
        backgroundLabel = new JLabel(backgroundIcon);
        
        Image image = backgroundIcon.getImage();
        Image scaledImage = image.getScaledInstance(815, 620, Image.SCALE_SMOOTH);
        backgroundLabel.setIcon(new ImageIcon(scaledImage));
        
        backgroundLabel.setBounds(0, 0, 815, 620);
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);

        weedWhacker = new JLabel();
        try {
            BufferedImage labelIcon = ImageIO.read(new File("weedWhackerLocked.png"));
            ImageIcon labelImageIcon = new ImageIcon(labelIcon);
            weedWhacker.setIcon(labelImageIcon);
            weedWhacker.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(cardPanel, "Error loading image for Weed Whacker achievement!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        weedWhacker.setBounds(-161, 95, 500, 320);
        weedWhacker.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(cardPanel, "This achievement is locked.");
            }
        });

        cropGuardian = new JLabel();
        try {
            BufferedImage labelIcon = ImageIO.read(new File("cropGuardianLocked.png"));
            ImageIcon labelImageIcon = new ImageIcon(labelIcon);
            cropGuardian.setIcon(labelImageIcon);
            cropGuardian.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(cardPanel, "Error loading image for Crop Guardian achievement!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        cropGuardian.setBounds(180, 95, 500, 320); 
        cropGuardian.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(cardPanel, "This achievement is locked.");
            }
        });

        speedFarmer = new JLabel();
        try {
            BufferedImage labelIcon = ImageIO.read(new File("speedFarmerLocked.png"));
            ImageIcon labelImageIcon = new ImageIcon(labelIcon);
            speedFarmer.setIcon(labelImageIcon);
            speedFarmer.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(cardPanel, "Error loading image for Speed Farmer achievement!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        speedFarmer.setBounds(-161, 318, 500, 320); 
        speedFarmer.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(cardPanel, "This achievement is locked.");
            }
        });

        masterHarvester = new JLabel();
        try {
            BufferedImage labelIcon = ImageIO.read(new File("masterHarvesterLocked.png"));
            ImageIcon labelImageIcon = new ImageIcon(labelIcon);
            masterHarvester.setIcon(labelImageIcon);
            masterHarvester.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(cardPanel, "Error loading image for Master Harvester achievement!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        masterHarvester.setBounds(180, 318, 500, 320); 
        masterHarvester.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(cardPanel, "This achievement is locked.");
            }
        });

        backButton = new JButton("");
        try {
            BufferedImage buttonIcon = ImageIO.read(new File("back.png"));
            ImageIcon buttonImageIcon = new ImageIcon(buttonIcon);
            backButton.setIcon(buttonImageIcon);
            backButton.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(cardPanel, "Error loading image for Back button!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        backButton.setBounds(10, 10, 75, 50);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setContentAreaFilled(false);
        
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Main Menu");
            }
        });

        layeredPane.add(weedWhacker, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(cropGuardian, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(speedFarmer, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(masterHarvester, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(backButton, JLayeredPane.PALETTE_LAYER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(layeredPane, BorderLayout.CENTER);
        return panel;
    }
}
