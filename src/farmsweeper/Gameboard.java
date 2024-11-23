package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class Gameboard {

    private CustomGameLogic gameLogic;
    private int time = 0;
    private int turn = 0;
    private Timer timer;
    private boolean timerStarted = false;

    public JPanel createGameboardPanel(CardLayout cardLayout, JPanel cardPanel) {
        JPanel gameboardPanel = new JPanel(new BorderLayout());

        // JLayeredPane for background and game logic
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(815, 620));

        // Background Layer
        String theme = CustomMode.getSelectedTheme();
        JLabel backgroundLabel = new JLabel();
        backgroundLabel.setBounds(0, 0, 815, 620);
        String imagePath = getImagePathForTheme(theme);
        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
            ImageIcon originalIcon = new ImageIcon(imagePath);
            Image scaledImage = originalIcon.getImage().getScaledInstance(815, 620, Image.SCALE_SMOOTH);
            backgroundLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            backgroundLabel.setText("No theme image found for: " + theme);
            backgroundLabel.setForeground(Color.RED);
            backgroundLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
        layeredPane.add(backgroundLabel, Integer.valueOf(0));

        // Game Logic Layer
        gameLogic = new CustomGameLogic();
        JLabel timeLabel = new JLabel("Time: 0", SwingConstants.LEFT);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timeLabel.setBounds(290, 110, 100, 30);

        JLabel turnLabel = new JLabel("Turn: 0", SwingConstants.LEFT);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 16));
        turnLabel.setBounds(450, 110, 100, 30);

        JPanel gameBoard = gameLogic.createGameBoard(timeLabel, turnLabel, () -> {
            // Start the timer on the first click
            if (!timerStarted) {
                startTimer(timeLabel);
                timerStarted = true;
            }
        }, () -> { //onWin
            // Show win screen as a top layer
            JPanel winPanel = new CustomGameWin().createWinPanel(cardLayout, cardPanel);
            winPanel.setBounds(185, 0, 815, 620);
            layeredPane.add(winPanel, Integer.valueOf(3));
            layeredPane.revalidate();
            layeredPane.repaint();
            stopTimer();
        }, () -> { //onLose
            // Transition to lose screen
            JPanel losePanel = new CustomGameLose().createLosePanel(cardLayout, cardPanel);
            losePanel.setBounds(185, 0, 815, 620);
            layeredPane.add(losePanel, Integer.valueOf(3));
            layeredPane.revalidate();
            layeredPane.repaint();
            stopTimer();
        });
        

        gameBoard.setBounds(235, 150, 350, 350);
        gameBoard.setOpaque(false);

        layeredPane.add(timeLabel, Integer.valueOf(1));
        layeredPane.add(turnLabel, Integer.valueOf(1));
        layeredPane.add(gameBoard, Integer.valueOf(2));

        gameboardPanel.add(layeredPane, BorderLayout.CENTER);

        return gameboardPanel;
    }

    private void startTimer(JLabel timeLabel) {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                time++;
                timeLabel.setText("Time: " + time);
            }
        });
        timer.start();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    private String getImagePathForTheme(String theme) {
        switch (theme) {
            case "Spring":
                return "spring.png";
            case "Summer":
                return "summer.png";
            case "Autumn":
                return "autumn.png";
            default:
                return "default.png";
        }
    }
}
