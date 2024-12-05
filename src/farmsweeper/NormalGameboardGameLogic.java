package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class NormalGameboardGameLogic extends JPanel {

    private int tileSize = 50;
    private int numRows, numCols, mineCount;
    private JPanel boardPanel = new JPanel();
    private NormalMineTile[][] board;
    private ArrayList<NormalMineTile> mineList;
    private Random random = new Random();
    private boolean gameOver = false;
    private boolean firstClick = true;
    private int tilesClicked = 0;
    private JLabel timerLabel;
    private JLabel turnLabel;
    private int turnCounter = 0;
    private Timer timer;
    private int elapsedTime = 0;
    private Image backgroundImage;
    private int level;
    private String theme;

    // Constructor for initializing the gameboard logic based on the level and theme
    public NormalGameboardGameLogic(int level, CardLayout cardLayout, JPanel cardPanel, String theme) {
        this.level = level;
        this.theme = theme;
        configureLevel(level);
        setBackgroundForLevel();
        setLayout(new BorderLayout());

        JPanel infoPanel = new JPanel(new GridLayout(1, 3));
        infoPanel.setOpaque(false);

        timerLabel = new JLabel("Time: 0");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        turnLabel = new JLabel("Turns: 0");
        turnLabel.setFont(new Font("Arial", Font.BOLD, 20));
        turnLabel.setHorizontalAlignment(SwingConstants.CENTER);

        infoPanel.add(timerLabel);
        infoPanel.add(turnLabel);

        add(infoPanel, BorderLayout.NORTH);

        // Set up the boardPanel with FlowLayout for centering tiles
        boardPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));  // Adjust spacing here
        add(boardPanel, BorderLayout.CENTER);

        initializeBoard();
        setMines();

        JButton backButton = new JButton("Back to Level Selection");
        backButton.addActionListener(e -> {
            if (timer != null) {
                timer.stop();
            }
            cardLayout.show(cardPanel, "LevelSelection");
        });
        add(backButton, BorderLayout.SOUTH);

        // Dynamically adjust the game size
        setPreferredSize(new Dimension(numCols * tileSize + 30, numRows * tileSize + 150));
    }

    private void configureLevel(int level) {
        if (level >= 1 && level <= 3) {
            numRows = 8;
            numCols = 8;
            mineCount = switch (level) {
                case 1 -> 5;
                case 2 -> 10;
                case 3 -> 15;
                default -> 5;
            };
        } else if (level >= 4 && level <= 6) {
            numRows = 16;
            numCols = 16;
            mineCount = switch (level) {
                case 4 -> 24;
                case 5 -> 40;
                case 6 -> 56;
                default -> 24;
            };
        } else if (level >= 7 && level <= 9) {
            numRows = 30;
            numCols = 16;
            mineCount = switch (level) {
                case 7 -> 64;
                case 8 -> 80;
                case 9 -> 99;
                default -> 64;
            };
        }
        tileSize = Math.min(50, 500 / Math.max(numRows, numCols));
    }

    private void setBackgroundForLevel() {
        String imagePath = switch (level) {
            case 1, 2, 3 -> "/images/Spring.png"; // Use path relative to resources folder
            case 4, 5, 6 -> "/images/Summer.png"; 
            case 7, 8, 9 -> "/images/Autumn.png";  
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
        board = new NormalMineTile[numRows][numCols];
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                NormalMineTile tile = new NormalMineTile(r, c, theme);
                board[r][c] = tile;
                tile.setPreferredSize(new Dimension(tileSize, tileSize));
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) return;

                        NormalMineTile t = (NormalMineTile) e.getSource();
                        turnCounter++;
                        turnLabel.setText("Turns: " + turnCounter);

                        if (e.getButton() == MouseEvent.BUTTON1) {
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
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
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
            NormalMineTile tile = board[r][c];
            if (!mineList.contains(tile)) {
                mineList.add(tile);
            }
        }
    }

    private void ensureSafeFirstClick(int r, int c) {
        NormalMineTile firstTile = board[r][c];
        if (mineList.contains(firstTile)) {
            mineList.remove(firstTile);
            NormalMineTile newMine;
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
        for (NormalMineTile mine : mineList) {
            mine.setText("💣");
        }
        gameOver = true;
        displayGameOverScreen("You Lose!");
        stopTimer();
    }

    private void displayGameOverScreen(String message) {
        JPanel gameOverPanel = new JPanel();
        gameOverPanel.setLayout(new BorderLayout());
        JLabel gameOverLabel = new JLabel(message, SwingConstants.CENTER);
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 30));
        gameOverPanel.add(gameOverLabel, BorderLayout.CENTER);
        add(gameOverPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void checkMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) return;

        NormalMineTile tile = board[r][c];
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
            gameOver = true;
            displayGameOverScreen("You Win!");
            stopTimer();
        }
    }

    private int countMine(int r, int c) {
        return (r >= 0 && r < numRows && c >= 0 && c < numCols && mineList.contains(board[r][c])) ? 1 : 0;
    }
}
