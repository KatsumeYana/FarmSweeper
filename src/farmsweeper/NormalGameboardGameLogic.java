package farmsweeper;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class NormalGameboardGameLogic extends JPanel {
    private class MineTile extends JButton {
        int r, c;

        public MineTile(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    private int tileSize = 50; // Default tile size
    private int numRows, numCols, mineCount;
    private int[] boardBounds;
    private JLabel textLabel = new JLabel();
    private JLabel timerLabel = new JLabel();
    private JLabel turnLabel = new JLabel();
    private JPanel boardPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    };
    private MineTile[][] board;
    private ArrayList<MineTile> mineList;
    private Random random = new Random();
    private int tilesClicked = 0;
    private boolean gameOver = false;
    private boolean firstClick = true;
    private int turnCounter = 0;
    private Timer timer;
    private int elapsedTime = 0;
    private Image backgroundImage;

    public NormalGameboardGameLogic(int difficulty, int level, CardLayout cardLayout, JPanel cardPanel) {
    configureLevel(level, difficulty);
    setBackgroundForDifficulty(difficulty);
    setLayout(new BorderLayout());

    // Create and add the info panel (existing code)
    JPanel infoPanel = new JPanel(new GridLayout(1, 3));
    textLabel.setFont(new Font("Arial", Font.BOLD, 20));
    textLabel.setHorizontalAlignment(JLabel.CENTER);
    textLabel.setText("Minesweeper: " + mineCount + " mines");
    timerLabel.setFont(new Font("Arial", Font.BOLD, 20));
    timerLabel.setHorizontalAlignment(JLabel.CENTER);
    timerLabel.setText("Time: 0s");
    turnLabel.setFont(new Font("Arial", Font.BOLD, 20));
    turnLabel.setHorizontalAlignment(JLabel.CENTER);
    turnLabel.setText("Turns: 0");
    infoPanel.add(textLabel);
    infoPanel.add(timerLabel);
    infoPanel.add(turnLabel);
    add(infoPanel, BorderLayout.NORTH);

    // Create and add the board panel
    boardPanel.setLayout(new GridLayout(numRows, numCols));
    add(boardPanel, BorderLayout.CENTER);

    initializeBoard();
    setMines();

    // Create and add the back button
    JButton backButton = new JButton("Back to Level Selection");
    backButton.addActionListener(e -> {
        if (timer != null) {
            timer.stop();
        }
        cardLayout.show(cardPanel, "LevelSelection");
    });
    add(backButton, BorderLayout.SOUTH);

    setPreferredSize(new Dimension(numCols * tileSize + 50, numRows * tileSize + 150));
}


    private void configureLevel(int level, int difficulty) {
        switch (difficulty) {
            case 1 -> { // Easy
                numRows = 8;
                numCols = 8;
                mineCount = switch (level) {
                    case 1 -> 5;
                    case 2 -> 10;
                    case 3 -> 15;
                    default -> 5;
                };
                boardBounds = new int[]{200, 160, 350, 350};
            }
            case 2 -> { // Normal
                numRows = 16;
                numCols = 16;
                mineCount = switch (level) {
                    case 1 -> 24;
                    case 2 -> 40;
                    case 3 -> 56;
                    default -> 24;
                };
                boardBounds = new int[]{210, 160, 500, 500};
            }
            case 3 -> { // Hard
                numRows = 30;
                numCols = 16;
                mineCount = switch (level) {
                    case 1 -> 64;
                    case 2 -> 80;
                    case 3 -> 99;
                    default -> 64;
                };
                boardBounds = new int[]{45, 160, 900, 500};
            }
        }

        // Dynamically adjust tile size
        tileSize = Math.min(50, 500 / Math.max(numRows, numCols));
    }

    private void setBackgroundForDifficulty(int difficulty) {
        String imagePath = switch (difficulty) {
            case 1 -> "/resources/Spring.png";
            case 2 -> "/resources/Summer.png";
            case 3 -> "/resources/Autumn.png";
            default -> null;
        };
        if (imagePath != null) {
            try {
                backgroundImage = new ImageIcon(getClass().getResource(imagePath)).getImage();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Background image not found: " + imagePath);
            }
        }
    }

    private void initializeBoard() {
        board = new MineTile[numRows][numCols];
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 18));
                tile.setPreferredSize(new Dimension(tileSize, tileSize));
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) return;
                        MineTile t = (MineTile) e.getSource();
                        turnCounter++;
                        turnLabel.setText("Turns: " + turnCounter);

                        if (e.getButton() == MouseEvent.BUTTON1) { // Left-click
                            if (firstClick) {
                                ensureSafeFirstClick(t.r, t.c);
                                startTimer();
                                firstClick = false;
                            }
                            if (mineList.contains(t)) {
                                revealMines();
                            } else {
                                checkMine(t.r, t.c);
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON3) { // Right-click
                            if (t.getText().isEmpty()) {
                                t.setText("🚩");
                            } else if (t.getText().equals("🚩")) {
                                t.setText("");
                            }
                        }
                    }
                });
                boardPanel.add(tile);
            }
        }
    }

    private void setMines() {
        mineList = new ArrayList<>();
        while (mineList.size() < mineCount) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);
            MineTile tile = board[r][c];
            if (!mineList.contains(tile)) {
                mineList.add(tile);
            }
        }
    }

    private void ensureSafeFirstClick(int r, int c) {
        MineTile firstTile = board[r][c];
        if (mineList.contains(firstTile)) {
            mineList.remove(firstTile);
            MineTile newMine;
            do {
                int newR = random.nextInt(numRows);
                int newC = random.nextInt(numCols);
                newMine = board[newR][newC];
            } while (mineList.contains(newMine) || (newMine.r == r && newMine.c == c));
            mineList.add(newMine);
        }
    }

    private void startTimer() {
        timer = new Timer(1000, e -> {
            elapsedTime++;
            timerLabel.setText("Time: " + elapsedTime + "s");
        });
        timer.start();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    private void revealMines() {
        for (MineTile mine : mineList) {
            mine.setText("💣");
        }
        gameOver = true;
        textLabel.setText("Game Over!");
        stopTimer();
    }

    private void checkMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) return;
        MineTile tile = board[r][c];
        if (!tile.isEnabled()) return;

        tile.setEnabled(false);
        tilesClicked++;
        int minesFound = 0;

        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr != 0 || dc != 0) minesFound += countMine(r + dr, c + dc);
            }
        }

        if (minesFound > 0) {
            tile.setText(String.valueOf(minesFound));
        } else {
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (dr != 0 || dc != 0) checkMine(r + dr, c + dc);
                }
            }
        }

        if (tilesClicked == numRows * numCols - mineCount) {
            textLabel.setText("You Win!");
            gameOver = true;
            stopTimer();
        }
    }

    private int countMine(int r, int c) {
        return (r >= 0 && r < numRows && c >= 0 && c < numCols && mineList.contains(board[r][c])) ? 1 : 0;
    }
}
