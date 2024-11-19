
package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.table.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Leaderboard {

    private JLabel backgroundLabel;
    private JLabel leaderBoard;
    private JButton backButton;

    public JPanel createLeaderboardPanel(CardLayout cardLayout, JPanel cardPanel) {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);

        ImageIcon backgroundIcon = new ImageIcon("backgroundGame.png");
        backgroundLabel = new JLabel(backgroundIcon);
        Image image = backgroundIcon.getImage();
        Image scaledImage = image.getScaledInstance(815, 620, Image.SCALE_SMOOTH);
        backgroundLabel.setIcon(new ImageIcon(scaledImage));
        backgroundLabel.setBounds(0, 0, 815, 620);
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);

        leaderBoard = new JLabel();
        try {
            BufferedImage titleIcon = ImageIO.read(new File("leaderboardTitle.png"));
            ImageIcon titleImageIcon = new ImageIcon(titleIcon);
            leaderBoard.setIcon(titleImageIcon);
            leaderBoard.setText("");
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(cardPanel, "Error loading image for Leaderboard title!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        leaderBoard.setBounds(15, 40, 700, 105);
        layeredPane.add(leaderBoard, JLayeredPane.PALETTE_LAYER);

        backButton = new JButton("");
        try {
            BufferedImage buttonIcon = ImageIO.read(new File("backButton.png"));
            ImageIcon buttonImageIcon = new ImageIcon(buttonIcon);
            backButton.setIcon(buttonImageIcon);
            backButton.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(cardPanel, "Error loading image for Back button!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        backButton.setBounds(300, 490, 200, 105);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setContentAreaFilled(false);
        
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Main Menu");
            }
        });

        String[] columnNames = {"Rank", "Name", "Time", "Turn", "Difficulty"};
        Object[][] data = {
            {1, "Player1", "00:02:30", 15, "Easy"},
            {2, "Player2", "00:03:05", 18, "Medium"},
            {3, "Player3", "00:04:00", 22, "Hard"},
        };

        JTable leaderboardTable = new JTable(data, columnNames);
        leaderboardTable.setFillsViewportHeight(true);

        Font headerFont = loadCustomFont("PressStart2P-Regular.ttf", 12);
        Font rankFont = loadCustomFont("PressStart2P-Regular.ttf", 10);
        Font nameFont = loadCustomFont("PressStart2P-Regular.ttf", 10);
        Font timeFont = loadCustomFont("PressStart2P-Regular.ttf", 10);
        Font turnFont = loadCustomFont("PressStart2P-Regular.ttf", 10);
        Font difficultyFont = loadCustomFont("PressStart2P-Regular.ttf", 10);

        JTableHeader tableHeader = leaderboardTable.getTableHeader();
        tableHeader.setFont(headerFont);

        leaderboardTable.getColumnModel().getColumn(0).setCellRenderer(new CustomCellRenderer(rankFont));
        leaderboardTable.getColumnModel().getColumn(1).setCellRenderer(new CustomCellRenderer(nameFont));
        leaderboardTable.getColumnModel().getColumn(2).setCellRenderer(new CustomCellRenderer(timeFont));
        leaderboardTable.getColumnModel().getColumn(3).setCellRenderer(new CustomCellRenderer(turnFont));
        leaderboardTable.getColumnModel().getColumn(4).setCellRenderer(new CustomCellRenderer(difficultyFont));

        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        scrollPane.setBounds(50, 150, 700, 350);
        
        layeredPane.add(scrollPane, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(backButton, JLayeredPane.PALETTE_LAYER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(layeredPane, BorderLayout.CENTER);
        return panel;
    }

    private Font loadCustomFont(String fontPath, float size) {
        try {
            File fontFile = new File(fontPath);
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            return font.deriveFont(size);
        } catch (Exception e) {
            System.out.println("Error loading font: " + e.getMessage());
            return new Font("Arial", Font.PLAIN, 14);
        }
    }

    private static class CustomCellRenderer extends DefaultTableCellRenderer {
        private Font font;

        public CustomCellRenderer(Font font) {
            this.font = font;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            component.setFont(font);
            return component;
        }
    }
}
