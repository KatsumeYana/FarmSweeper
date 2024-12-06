package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

public class NormalGameboardGameLogic extends JPanel {

    private int tileSize = 50;
    private int numRows, numCols, mineCount;
    private JPanel boardPanel = new JPanel();
    private MineTile[][] board;
    private ArrayList<MineTile> mineList;
    private int[] boardBounds;
    private Random random = new Random();
    private boolean gameOver = false;
    private boolean firstClick = true;
    private int tilesClicked = 0;
    private JLabel timerLabel;
    private JLabel turnLabel;
    private JLabel levelLabel;  // Label to display the current level
    private int turnCounter = 0;
    private Timer gameTimer;
    private int timeElapsed = 0;
    private int level;
    private String theme;  // Theme passed from LevelSelection
    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    // Constructor to initialize the game logic with level and theme
    public NormalGameboardGameLogic(int level, CardLayout cardLayout, JPanel cardPanel, String theme) {
        this.level = level;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.theme = theme;

        configureLevel(level);  // Configure the board size and mine count based on the level
        setLayout(new BorderLayout());
    }

    // Configure the level based on level number (rows, cols, mines, etc.)
    private void configureLevel(int level) {
        if (level >= 1 && level <= 3) {
            numRows = 8;
            numCols = 8;
            mineCount = (level == 1) ? 5 : (level == 2) ? 10 : 15;
            boardBounds = new int[]{350, 160, 350, 350};  // Adjusted based on level
        } else if (level >= 4 && level <= 6) {
            numRows = 16;
            numCols = 16;
            mineCount = (level == 4) ? 24 : (level == 5) ? 40 : 56;
            boardBounds = new int[]{260, 160, 500, 500};  // Adjusted for larger board
        } else if (level >= 7 && level <= 9) {
            numRows = 30;
            numCols = 16;
            mineCount = (level == 7) ? 64 : (level == 8) ? 80 : 99;
            boardBounds = new int[]{45, 160, 900, 500};  // Adjusted for maximum board size
        }
    }

    // Create the gameboard panel with CardLayout and JPanel for navigation
    public JPanel createNormalGameboardPanel() {
        JPanel gameboardPanel = new JPanel(new BorderLayout());

        // Get the database connection
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Database connection is not initialized!", "Error", JOptionPane.ERROR_MESSAGE);
            return gameboardPanel;  // Return an empty panel in case of an error
        }

        // Background setup based on theme (Spring, Summer, Autumn)
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(815, 620));

        JLabel backgroundLabel = new JLabel();
        backgroundLabel.setBounds(0, 0, 1000, 700);

        Font textFont = BaseGame.loadCustomFont("PressStart2P-Regular.ttf", 16);

        try {
            // Attempt to load the image using getResource (for project classpath)
            ImageIcon backgroundIcon = new ImageIcon("resources/images/" + theme + ".png");
            if (backgroundIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                Image scaledImage = backgroundIcon.getImage().getScaledInstance(1000, 700, Image.SCALE_SMOOTH);
                backgroundLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                System.err.println("Error: Background image not found for theme: " + theme);
                backgroundLabel.setText("No theme image found.");
                backgroundLabel.setHorizontalAlignment(SwingConstants.CENTER);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading image: " + theme);
            backgroundLabel.setText("Error loading image.");
            backgroundLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }

        // Add background to the layered pane
        layeredPane.add(backgroundLabel, Integer.valueOf(0));

        // TimeTurnBoard Layer (to display time and turns)
        String timeIconPath = "timeturnboard.png";
        ImageIcon timeIcon = BaseGame.loadImage(timeIconPath);
        if (timeIcon != null) {
            JLabel timeTurnBoardLabel = new JLabel(timeIcon);
            timeTurnBoardLabel.setBounds(225, -100, 600, 400); // Adjust bounds as per your design
            layeredPane.add(timeTurnBoardLabel, Integer.valueOf(1));  // TimeTurnBoard at layer 1
        }

        // Add timer and turn labels
        timerLabel = new JLabel("Time: 0");
        timerLabel.setFont(textFont);
        timerLabel.setBounds(350, 85, 200, 30);
        turnLabel = new JLabel("Turns: 0");
        turnLabel.setFont(textFont);
        turnLabel.setBounds(540, 85, 150, 30);

        // Add the current level label
         String levelIconPath = "Custom Mode Level  Display Button.png";
        ImageIcon leveldisplay = BaseGame.loadImage(levelIconPath);
        if (leveldisplay != null) {
            JLabel levelbg = new JLabel(leveldisplay);
            levelbg.setBounds(0, 50, 143, 77);
            layeredPane.add(levelbg, Integer.valueOf(1));  // TimeTurnBoard at layer 1
        }
        levelLabel = new JLabel("Level:" + level);
        levelLabel.setFont(textFont);
        levelLabel.setBounds(0, 70, 150, 30);  // Position the level label on the right
        layeredPane.add(levelLabel, Integer.valueOf(2));

        layeredPane.add(timerLabel, Integer.valueOf(2));
        layeredPane.add(turnLabel, Integer.valueOf(2));

        // Retry Button
        JButton retryButton = BaseGame.createButton("Retry Button.png", 700, 10, 70, 63, (ActionEvent e) -> {
            NormalGameboardGameLogic gameboardLogic = new NormalGameboardGameLogic(level, cardLayout, cardPanel, theme);
            JPanel newGameboardPanel = gameboardLogic.createNormalGameboardPanel();
            cardPanel.add(newGameboardPanel, "Normal Gameboard");
            cardLayout.show(cardPanel, "Normal Gameboard");
        });
        layeredPane.add(retryButton, Integer.valueOf(7));

        // Home Button
        JButton homeButton = BaseGame.createButton("Home Button.png", 800, 10, 69, 62, (ActionEvent e) -> {
            cardLayout.show(cardPanel, "Main Menu");
        });
        layeredPane.add(homeButton, Integer.valueOf(7));

        // Gameboard Layer
        boardPanel = createGameBoard(() -> {
            // Win condition
            gameOver = true;
            stopTimer();
        }, () -> {
            // Lose condition
            gameOver = true;
            stopTimer();
            showLosePanel();  // Trigger the lose panel to display
        });

        boardPanel.setBounds(boardBounds[0], boardBounds[1], boardBounds[2], boardBounds[3]);
        layeredPane.add(boardPanel, Integer.valueOf(7));

        gameboardPanel.add(layeredPane, BorderLayout.CENTER);

        return gameboardPanel;
    }

    // Create the gameboard (Main Gameplay Area)
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

                                // Trigger NormalWin and show win panel
                                NormalWin normalWin = new NormalWin(level, timeElapsed, turnCounter, cardLayout, cardPanel);
                                JPanel winPanel = normalWin.createWinPanel();
                                cardPanel.add(winPanel, "Normal Win");
                                cardLayout.show(cardPanel, "Normal Win");
                            }
                        }
                    }
                });

                boardPanel.add(tile);
            }
        }

        return boardPanel;
    }
    
      // Show the lose panel
    private void showLosePanel() {
        // Create and show the lose panel after a loss
        NormalLose losePanel = new NormalLose(level);
        JPanel losePanelInstance = losePanel.createLosePanel(cardLayout, cardPanel);  // Pass the cardLayout and cardPanel
        cardPanel.add(losePanelInstance, "Normal Lose");
        cardLayout.show(cardPanel, "Normal Lose");  // Show the lose panel
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
            gameOver = true;
            stopTimer();

            // Ensure conn is passed correctly to NormalWin
            NormalWin normalWin = new NormalWin(level, timeElapsed, turnCounter, cardLayout, cardPanel);  // Pass conn here
            JPanel winPanel = normalWin.createWinPanel();  // Pass cardLayout and cardPanel for navigation
            cardPanel.add(winPanel, "Normal Win");
            cardLayout.show(cardPanel, "Normal Win");
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
