package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;

public class GameboardGameLogic {
    
    //Constructor
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
    private final Random random = new Random();
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
                boardBounds = new int[]{345, 200, 350, 350}; 
                break;
            case "Normal":
                numRows = 16;
                numCols = 16;
                mineCount = 40;
                boardBounds = new int[]{300, 200, 450, 450}; 
                break;
            case "Hard":
                numRows = 16;
                numCols = 30;
                mineCount = 99;
                boardBounds = new int[]{80, 200, 850, 450};
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

        Font textFont = CustomFont.loadCustomFont("PressStart2P-Regular.ttf", 16);
        
        // JLayeredPane for background and game logic
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(815, 620));

        // Background Layer
        String theme = CustomMode.getSelectedTheme();
        JLabel backgroundLabel = new JLabel();
        backgroundLabel.setBounds(0, 0, 1000, 700);
        String imagePath = getImagePathForTheme(theme);
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
        
        // TimeTurnBoard Layer (Added on top of the background)
        String timeIconPath = "timeturnboard.png";
        ImageIcon timeIcon = loadImage(timeIconPath);
        if (timeIcon != null) {
            JLabel timeTurnBoardLabel = new JLabel(timeIcon);
            timeTurnBoardLabel.setBounds(225, -100, 600, 400); // Adjust bounds as per your design
            layeredPane.add(timeTurnBoardLabel, Integer.valueOf(1)); // TimeTurnBoard at layer 1
        }
        

        // Timer
        timerLabel = new JLabel("Time: 0");
        layeredPane.add(timerLabel, Integer.valueOf(2)); // Timer at layer 2
        timerLabel.setBounds(360, 85, 200, 30);
        timerLabel.setFont(textFont);

        // Turn
        turnLabel = new JLabel("Turns: 0");
        layeredPane.add(turnLabel, Integer.valueOf(2)); // Turn label at layer 2
        turnLabel.setBounds(550, 85, 150, 30);
        turnLabel.setFont(textFont);
        
        // Retry Button
        String retryButtonIconPath = "Retry Button.png"; 
        JButton retryButton = createButton(retryButtonIconPath, 800, 10, (ActionEvent e) -> {
            GameboardGameLogic gameboardLogic = new GameboardGameLogic(selectedDifficulty);
            JPanel newGameboardPanel = gameboardLogic.createGameboardPanel(cardLayout, cardPanel);
            cardPanel.add(newGameboardPanel, "Gameboard");
            cardLayout.show(cardPanel, "Gameboard");
            System.out.println("You pressed retry button");
        });
        layeredPane.add(retryButton, Integer.valueOf(7)); 

        // Home Button
        String homeButtonIconPath = "Home Button.png"; 
        JButton homeButton = createButton(homeButtonIconPath, 900, 10, (ActionEvent e) -> {
            cardLayout.show(cardPanel, "Main Menu");
            System.out.println("You pressed home button");

        });
        layeredPane.add(homeButton, Integer.valueOf(7)); 


        // Gameboard Layer
        JPanel boardPanel = createGameBoard(() -> {
            homeButton.setEnabled(false);
            retryButton.setEnabled(false);
            System.out.println("You Win! Theme of " + theme);
            stopTimer();
            Leaderboard leaderboard = new Leaderboard();
            JPanel winPanel = new CustomGameWin(new Leaderboard()).createWinPanel(cardLayout, cardPanel, timeElapsed, turnCounter, selectedDifficulty);
            winPanel.setBounds(60, 70, 815, 620);
            layeredPane.add(winPanel, Integer.valueOf(8)); 
            layeredPane.revalidate();
            layeredPane.repaint();
        }, () -> {
            homeButton.setEnabled(false);
            retryButton.setEnabled(false);
            System.out.println("You Lose!");
            stopTimer();
            JPanel losePanel = new CustomGameLose().createLosePanel(cardLayout, cardPanel);
            losePanel.setBounds(318, 40, 815, 620);
            layeredPane.add(losePanel, Integer.valueOf(8)); 
            layeredPane.revalidate();
            layeredPane.repaint(); 
       });

        
        // Set the bounds dynamically based on difficulty
        boardPanel.setBounds(boardBounds[0], boardBounds[1], boardBounds[2], boardBounds[3]);
        boardPanel.setOpaque(false);
        // Apply the fade-in effect after the game board is created
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
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;
                
                tileFont = CustomFont.loadCustomFont("PressStart2P-Regular.ttf", 12);
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
                                revealMines(clickedTile);
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
    private void revealMines(MineTile clickedTile) {
        // Only reveal the clicked mine and stop the game
        clickedTile.setFont(flagFont);             
        clickedTile.setText("💣");
        clickedTile.setBackground(Color.RED);
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
                return "resources/images/Spring.png";
            case "Summer":
                return "resources/images/Summer.png";
            case "Autumn":
                return "resources/images/Autumn.png";
            default:
                return "No chosen image";
        }
    }
    
    // Helper method to load images with a fallback
    private ImageIcon loadImage(String path) {
        File imageFile = new File("resources/images/" + path);
        if (imageFile.exists()) {
            try {
                BufferedImage bufferedImage = ImageIO.read(imageFile);
                return new ImageIcon(bufferedImage);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error loading image: " + path, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.err.println("Image not found: " + path);
        }
        return null; // Return null if image is not found or can't be loaded
    }

    // Helper method to create a button with an image icon
    private JButton createButton(String iconPath, int x, int y, ActionListener action) {
        JButton button = new JButton();
        final ImageIcon buttonImageIcon = loadImage(iconPath);
        
        button.setIcon(buttonImageIcon);
        button.setText("");
        button.setBounds(x, y, 100, 70);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.addActionListener(action);

        // Apply the hover effect using Animations class
        Animations.applyHoverEffect(button, buttonImageIcon);

        return button;
    }
}
