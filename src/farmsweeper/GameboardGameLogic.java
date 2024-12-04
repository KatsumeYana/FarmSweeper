package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class GameboardGameLogic {

    private int numRows;
    private int numCols;
    private int mineCount;
    private int[] boardBounds;
    private MineTile[][] board;
    private ArrayList<MineTile> mineList;
    private final Random random = new Random();
    private boolean gameOver = false;
    private boolean firstClick = true;
    private int tilesClicked = 0;

    JLabel timerLabel;
    JLabel turnLabel;

    private Timer gameTimer;
    private int timeElapsed = 0;
    private int turnCounter = 0;
    private final String selectedDifficulty;

    public GameboardGameLogic(String difficulty) {
        this.selectedDifficulty = difficulty;
        setDifficulty(difficulty);
    }

    private void setDifficulty(String difficulty) {
        switch (difficulty) {
            case "Easy":
                numRows = 8;
                numCols = 8;
                mineCount = 10;
                boardBounds = new int[]{350, 160, 350, 350};
                break;
            case "Normal":
                numRows = 16;
                numCols = 16;
                mineCount = 40;
                boardBounds = new int[]{260, 160, 500, 500};
                break;
            case "Hard":
                numRows = 16;
                numCols = 30;
                mineCount = 99;
                boardBounds = new int[]{45, 160, 900, 500};
                break;
            default:
                throw new IllegalArgumentException("Invalid difficulty: " + difficulty);
        }
    }

    public JPanel createGameboardPanel(CardLayout cardLayout, JPanel cardPanel) {
            if (numRows == 0 || numCols == 0) {
                throw new IllegalArgumentException("rows and cols cannot both be zero");
            }

            JPanel gameboardPanel = new JPanel(new BorderLayout());
            Font textFont = BaseGame.loadCustomFont("PressStart2P-Regular.ttf", 16);

            // JLayeredPane for background and game logic
            JLayeredPane layeredPane = new JLayeredPane();
            layeredPane.setPreferredSize(new Dimension(815, 620));

            // Background Layer
            String theme = CustomMode.getSelectedTheme();
            JLabel backgroundLabel = new JLabel();
            backgroundLabel.setBounds(0, 0, 1000, 700);
            String imagePath = BaseGame.getImagePathForTheme(theme);
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                ImageIcon originalIcon = new ImageIcon(imagePath);
                Image scaledImage = originalIcon.getImage().getScaledInstance(1000, 700, Image.SCALE_SMOOTH);
                backgroundLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                System.err.println("Background image not found for theme: " + theme);
                backgroundLabel.setText("No theme image found for: " + theme);
                backgroundLabel.setHorizontalAlignment(SwingConstants.CENTER);
            }
            layeredPane.add(backgroundLabel, Integer.valueOf(0));

            // TimeTurnBoard Layer (to display time and turns)
            String timeIconPath = "timeturnboard.png";
            ImageIcon timeIcon = BaseGame.loadImage(timeIconPath);
            if (timeIcon != null) {
                JLabel timeTurnBoardLabel = new JLabel(timeIcon);
                timeTurnBoardLabel.setBounds(225, -100, 600, 400); // Adjust bounds as per your design
                layeredPane.add(timeTurnBoardLabel, Integer.valueOf(1));  // TimeTurnBoard at layer 1
            }
            
            // Timer and Turn Counter labels
            timerLabel = new JLabel("Time: 0");
            layeredPane.add(timerLabel, Integer.valueOf(2)); // Timer at layer 2
            timerLabel.setBounds(350, 85, 200, 30);
            timerLabel.setFont(textFont);

            turnLabel = new JLabel("Turns: 0");
            layeredPane.add(turnLabel, Integer.valueOf(2)); // Turn label at layer 2
            turnLabel.setBounds(540, 85, 150, 30);
            turnLabel.setFont(textFont);
            

            // Retry Button
            JButton retryButton = BaseGame.createButton("Retry Button.png", 800, 10, 70, 63, (ActionEvent e) -> {
                GameboardGameLogic gameboardLogic = new GameboardGameLogic(selectedDifficulty);
                JPanel newGameboardPanel = gameboardLogic.createGameboardPanel(cardLayout, cardPanel);
                cardPanel.add(newGameboardPanel, "Gameboard");
                cardLayout.show(cardPanel, "Gameboard");
            });
            layeredPane.add(retryButton, Integer.valueOf(7));

            // Home Button
            JButton homeButton = BaseGame.createButton("Home Button.png", 900, 10, 69, 62, (ActionEvent e) -> {
                cardLayout.show(cardPanel, "Main Menu");
            });
            layeredPane.add(homeButton, Integer.valueOf(7));

            // Gameboard Layer
            JPanel boardPanel = createGameBoard(() -> {
                homeButton.setEnabled(false);
                retryButton.setEnabled(false);
                stopTimer();
                System.out.println("You Win");

                Leaderboard leaderboard = new Leaderboard();
                JPanel winPanel = new CustomGameWin(leaderboard).createWinPanel(cardLayout, cardPanel, timeElapsed, turnCounter, selectedDifficulty);
                winPanel.setBounds(60, 50, 815, 620);
                layeredPane.add(winPanel, Integer.valueOf(8));
                layeredPane.revalidate();
                layeredPane.repaint();
                disableAllTiles(); // Disable all tiles after the game ends
            }, () -> {
                homeButton.setEnabled(false);
                retryButton.setEnabled(false);
                stopTimer();
                System.out.println("You Lose");
                
                JPanel losePanel = new CustomGameLose().createLosePanel(cardLayout, cardPanel);
                losePanel.setBounds(300, 40, 815, 620);
                layeredPane.add(losePanel, Integer.valueOf(8));
                layeredPane.revalidate();
                layeredPane.repaint();
                disableAllTiles(); // Disable all tiles after the game ends
            });

            // Set bounds dynamically based on difficulty
            boardPanel.setBounds(boardBounds[0], boardBounds[1], boardBounds[2], boardBounds[3]);
            boardPanel.setOpaque(false);
            layeredPane.add(boardPanel, Integer.valueOf(7));  // Add boardPanel to layeredPane
            gameboardPanel.add(layeredPane, BorderLayout.CENTER);

            return gameboardPanel;
        }

private JPanel createGameBoard(Runnable onWin, Runnable onLose) {
    JPanel boardPanel = new JPanel(new GridLayout(numRows, numCols));
    boardPanel.setPreferredSize(new Dimension(400, 400));
    boardPanel.setOpaque(false);

    board = new MineTile[numRows][numCols];
    mineList = new ArrayList<>();

    for (int r = 0; r < numRows; r++) {
        for (int c = 0; c < numCols; c++) {
            MineTile tile = new MineTile(r, c, CustomMode.getSelectedTheme());
            board[r][c] = tile;

            tile.setMargin(new Insets(0, 0, 0, 0));
            tile.setContentAreaFilled(true);
            tile.setFocusPainted(false);
            tile.setFocusable(false);
            tile.setBackground(new Color(198, 149, 112));

            tile.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (gameOver) return;

                    MineTile clickedTile = (MineTile) e.getSource();

                    // Right-click logic (flagging)
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        if (!clickedTile.isFlagged && clickedTile.isEnabled()) {
                            clickedTile.setFlag(CustomMode.getSelectedTheme());  // Flag the tile
                        } else if (clickedTile.isFlagged) {
                            clickedTile.clearFlag(CustomMode.getSelectedTheme());  // Unflag the tile and reset to default image
                        }
                        return;
                    }

                    // Left-click logic (reveal the tile)
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        if (clickedTile.isFlagged || clickedTile.isRevealed) {
                            return;  // Skip if already flagged or revealed
                        }

                        if (firstClick) {
                            firstClick = false;
                            generateMines(clickedTile.r, clickedTile.c); // Place mines
                            startTimer(); // Start the timer after first click
                        }

                        incrementTurnCounter(); // Increment turn count

                        // Reveal the tile and check its surroundings for adjacent crops (formerly mines)
                        if (mineList.contains(clickedTile)) {
                            clickedTile.revealCrop(CustomMode.getSelectedTheme()); // Reveal crop (mine)
                            stopTimer(); // Stop the timer
                            onLose.run(); // Trigger lose condition
                        } else {
                            int adjacentMines = countAdjacentMines(clickedTile.r, clickedTile.c);
                            clickedTile.reveal(CustomMode.getSelectedTheme(), adjacentMines); // Reveal the tile based on adjacent crops count
                            revealTiles(clickedTile.r, clickedTile.c, onWin); // Reveal surrounding tiles
                        }

                        // If all non-mine tiles are revealed, the game is won
                        if (tilesClicked == numRows * numCols - mineCount) {
                            gameOver = true;
                            stopTimer();
                            disableAllTiles();
                            onWin.run(); // Trigger win condition
                        }
                    }
                }
            });

            boardPanel.add(tile);
        }
    }

    return boardPanel;
}
    // Timer
    private void startTimer() {
        gameTimer = new Timer(1000, (ActionEvent e) -> {
            timeElapsed++;
            timerLabel.setText("Time: " + timeElapsed);
        });
        gameTimer.start();
    }

    private void stopTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    private void incrementTurnCounter() {
        turnCounter++;
        turnLabel.setText("Turns: " + turnCounter);
    }

    private void generateMines(int safeRow, int safeCol) {
        int minesLeft = mineCount;
        while (minesLeft > 0) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);

            if (Math.abs(r - safeRow) <= 1 && Math.abs(c - safeCol) <= 1) {
                continue;
            }

            MineTile tile = board[r][c];
            if (!mineList.contains(tile)) {
                mineList.add(tile);
                tile.isMine = true;  // Mark the tile as a mine (formerly crop)
                minesLeft--;
            }
        }
    }

    private void disableAllTiles() {
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                MineTile tile = board[r][c];
                tile.setEnabled(false);
                for (MouseListener ml : tile.getMouseListeners()) {
                    tile.removeMouseListener(ml);
                }
        }
      }
    }

    private void revealTiles(int startRow, int startCol, Runnable onWin) {
    MineTile tile = board[startRow][startCol];
    int adjacentMines = countAdjacentMines(startRow, startCol); // Count adjacent mines for the clicked tile
    tile.reveal(CustomMode.getSelectedTheme(), adjacentMines);  // Reveal the tile based on adjacent mines
    tile.setEnabled(false); // Disable the tile after it is revealed

    tilesClicked++; // Increment tilesClicked when a tile is revealed

        // If the clicked tile has no adjacent mines, recursively reveal surrounding tiles
        if (adjacentMines == 0) {
            revealAdjacentTiles(startRow, startCol);  // Recursively reveal adjacent tiles if no adjacent mines
        }

        // Check if the win condition is met after revealing this tile
        if (tilesClicked == numRows * numCols - mineCount) {
            System.out.println("Win Condition Met!");
            gameOver = true;
            stopTimer();
            disableAllTiles();
            onWin.run(); // Trigger win condition
        }
    }

    private void revealAdjacentTiles(int r, int c) {
        // Check the surrounding tiles (top-left, top, top-right, left, right, bottom-left, bottom, bottom-right)
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = r + i;
                int newCol = c + j;

                // Skip out of bounds tiles
                if (newRow < 0 || newRow >= numRows || newCol < 0 || newCol >= numCols) continue;

                MineTile adjacentTile = board[newRow][newCol];

                // Skip already revealed or flagged tiles
                if (adjacentTile.isRevealed || adjacentTile.isFlagged) continue;

                int adjacentMines = countAdjacentMines(newRow, newCol);  // Count adjacent mines for the adjacent tile
                adjacentTile.reveal(CustomMode.getSelectedTheme(), adjacentMines);  // Reveal the adjacent tile
                adjacentTile.setEnabled(false);  // Disable the adjacent tile after it is revealed

                // If the adjacent tile has no mines around it, recursively reveal surrounding tiles
                if (adjacentMines == 0) {
                    revealAdjacentTiles(newRow, newCol);  // Recursively reveal adjacent tiles if no adjacent mines
                }

                tilesClicked++; // Increment the counter when an adjacent tile is revealed
            }
        }
    }

    private int countAdjacentMines(int r, int c) {
        int count = 0;

        // Check all adjacent tiles (top-left, top, top-right, left, right, bottom-left, bottom, bottom-right)
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = r + i;
                int newCol = c + j;

                // Skip out-of-bounds tiles
                if (newRow < 0 || newRow >= numRows || newCol < 0 || newCol >= numCols) continue;

                // Skip the tile itself
                if (i == 0 && j == 0) continue;

                // Count if the tile is a crop (was previously a mine)
                MineTile adjacentTile = board[newRow][newCol];
                if (adjacentTile.isMine) {
                    count++;
                }
            }
        }
        return count;
    }

}
