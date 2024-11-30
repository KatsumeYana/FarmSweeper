package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class GameboardGameLogic {
    
    private class MineTile extends JButton {
        int r;
        int c;
        boolean isFlagged; 

        public MineTile(int r, int c) {
            this.r = r;
            this.c = c;
            this.isFlagged = false; 
        }
    }

    private int numRows;
    private int numCols;
    private int mineCount;
    private int[] boardBounds; 
    private MineTile[][] board;
    private ArrayList<MineTile> mineList;
    private Random random = new Random();
    private boolean gameOver = false;
    private boolean firstClick = true;
    private int tilesClicked = 0;
    
    Font tileFont;
    Font flagFont;
    
    JLabel timerLabel;
    JLabel turnLabel;
    
    private Timer gameTimer;
    private int timeElapsed = 0;
    private int turnCounter = 0;
    private String selectedDifficulty;
    

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
                boardBounds = new int[]{235, 150, 350, 350}; 
                break;
            case "Normal":
                numRows = 16;
                numCols = 16;
                mineCount = 40;
                boardBounds = new int[]{180, 150, 450, 450}; 
                break;
            case "Hard":
                numRows = 16;
                numCols = 30;
                mineCount = 99;
                boardBounds = new int[]{65, 150, 850, 450};
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

        Font textFont = loadCustomFont("PressStart2P-Regular.ttf", 16);
        
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
            System.err.println("Background image not found for theme: " + theme);
            backgroundLabel.setText("No theme image found for: " + theme);
            backgroundLabel.setForeground(Color.RED);
            backgroundLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
        layeredPane.add(backgroundLabel, Integer.valueOf(0));

        //Timer
        timerLabel = new JLabel("Time: 0s");
        layeredPane.add(timerLabel, Integer.valueOf(1));
        timerLabel.setBounds(10, 10, 150, 30);
        timerLabel.setFont(textFont);

        //Turn
        turnLabel = new JLabel("Turns: 0");
        layeredPane.add(turnLabel, Integer.valueOf(1));
        turnLabel.setBounds(10, 50, 150, 30);
        turnLabel.setFont(textFont);
        
        // Retry Button
        JButton retryButton = new JButton("Retry");
        retryButton.setBounds(240, 385, 100, 50);
        retryButton.addActionListener(e -> {
            GameboardGameLogic gameboardLogic = new GameboardGameLogic(selectedDifficulty);
            JPanel newGameboardPanel = gameboardLogic.createGameboardPanel(cardLayout, cardPanel);

            cardPanel.add(newGameboardPanel, "Gameboard");
            cardLayout.show(cardPanel, "Gameboard");
        });
        layeredPane.add(retryButton, Integer.valueOf(1));
        
        // Home button
        JButton homeButton = new JButton("Home");
        homeButton.setBounds(690, 30, 70, 50);
        homeButton.addActionListener(e -> cardLayout.show(cardPanel, "Main Menu"));
        layeredPane.add(homeButton, Integer.valueOf(1));
        
        // Gameboard Layer
        JPanel boardPanel = createGameBoard(() -> {
            homeButton.setEnabled(false);
            System.out.println("Win condition triggered for theme: " + theme);
            stopTimer();
            Leaderboard leaderboard = new Leaderboard();
            JPanel winPanel = new CustomGameWin(new Leaderboard()).createWinPanel(cardLayout, cardPanel, timeElapsed, turnCounter, selectedDifficulty);
            winPanel.setBounds(185, 0, 815, 620);
            layeredPane.add(winPanel, Integer.valueOf(3));
            layeredPane.revalidate();
            layeredPane.repaint();
        }, () -> {
            homeButton.setEnabled(false);
            System.out.println("Lose condition triggered!");
            stopTimer();
            JPanel losePanel = new CustomGameLose().createLosePanel(cardLayout, cardPanel);
            losePanel.setBounds(185, 0, 815, 620);
            layeredPane.add(losePanel, Integer.valueOf(3));
            layeredPane.revalidate();
            layeredPane.repaint();
        });

        // Set the bounds dynamically based on difficulty
        boardPanel.setBounds(boardBounds[0], boardBounds[1], boardBounds[2], boardBounds[3]);
        boardPanel.setOpaque(false);
        layeredPane.add(boardPanel, Integer.valueOf(2));

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
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;
                
                tileFont = loadCustomFont("PressStart2P-Regular.ttf", 12);
                flagFont = new Font("Segoe UI Emoji", Font.PLAIN, 12);
                
                tile.setMargin(new Insets(0, 0, 0, 0)); 
                tile.setContentAreaFilled(true); 
                tile.setFocusPainted(false); 
                tile.setFocusable(false);
                tile.setFont(tileFont);

                tile.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (gameOver) return;

                    MineTile clickedTile = (MineTile) e.getSource();

                    // Right-click logic (flagging)
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        if (!clickedTile.isFlagged && clickedTile.isEnabled()) {
                            // Flag the tile
                            clickedTile.setFont(flagFont); 
                            clickedTile.setMargin(new Insets(0, 0, 0, 0));
                            clickedTile.setContentAreaFilled(true); 
                            clickedTile.setFocusPainted(false);
                            clickedTile.setFocusable(false);
                            clickedTile.setText("🚩");
                            clickedTile.isFlagged = true;
                        } else if (clickedTile.isFlagged) {
                            // Unflag the tile
                            clickedTile.setText("");
                            clickedTile.isFlagged = false; 
                        }
                        return;
                    }

                    // Prevent left-click on flagged tiles
                    if (clickedTile.isFlagged) {
                        return;
                    }

                    // Left-click logic
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        
                         // Only increment turn counter if the tile has not been revealed (enabled)
                        if (!clickedTile.isEnabled()) {
                            return; // Skip turn increment for already revealed tiles
                        }
                        
                         if (firstClick) {
                                firstClick = false;
                                generateMines(clickedTile.r, clickedTile.c);
                                startTimer();
                            }
                         
                         incrementTurnCounter();
                         
                     if (mineList.contains(clickedTile)) {
                                revealMines();
                                stopTimer();
                                onLose.run();
                            } else {
                                checkTile(clickedTile.r, clickedTile.c);

                                if (tilesClicked == numRows * numCols - mineCount) {
                                    gameOver = true;
                                    stopTimer();
                                    disableAllTiles();
                                    onWin.run();
                                }
                        }
                    }
                }
            });


                boardPanel.add(tile);
            }
        }

        return boardPanel;
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
    
     private void startTimer() {
        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeElapsed++;
                timerLabel.setText("Time: " + timeElapsed + "s");
            }
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

        // Skip if it's the safe tile or one of its adjacent tiles
        if (Math.abs(r - safeRow) <= 1 && Math.abs(c - safeCol) <= 1) {
            continue;
        }

        MineTile tile = board[r][c];
        if (!mineList.contains(tile)) {
            mineList.add(tile);
            System.out.println("Mine placed at: (" + r + ", " + c + ")");
            minesLeft--;
        }
    }
}


    private void revealMines() {
        for (MineTile tile : mineList) {
            tile.setFont(flagFont);             
            tile.setText("💣");
            tile.setBackground(Color.RED);
        }
        gameOver = true;
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
        System.out.println("All tiles disabled.");
    }

    private void checkTile(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) return;

        MineTile tile = board[r][c];
        if (!tile.isEnabled()){
            
            return;
        } // Skip already revealed tiles

        tile.setEnabled(false);
        
        tilesClicked++;

        int adjacentMines = countAdjacentMines(r, c);
        if (adjacentMines > 0) {
            tile.setFont(tileFont); 
            tile.setText(String.valueOf(adjacentMines));
        } else {
            checkAdjacentTiles(r, c); // Reveal adjacent tiles
        }
    }

    private void checkAdjacentTiles(int r, int c) {
        checkTile(r - 1, c - 1);
        checkTile(r - 1, c);
        checkTile(r - 1, c + 1);
        checkTile(r, c - 1);
        checkTile(r, c + 1);
        checkTile(r + 1, c - 1);
        checkTile(r + 1, c);
        checkTile(r + 1, c + 1);
    }

    private int countAdjacentMines(int r, int c) {
        int count = 0;
        count += isMine(r - 1, c - 1);
        count += isMine(r - 1, c);
        count += isMine(r - 1, c + 1);
        count += isMine(r, c - 1);
        count += isMine(r, c + 1);
        count += isMine(r + 1, c - 1);
        count += isMine(r + 1, c);
        count += isMine(r + 1, c + 1);
        return count;
    }

    private int isMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) return 0;
        return mineList.contains(board[r][c]) ? 1 : 0;
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
