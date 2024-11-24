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

        public MineTile(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }
    
    private int numRows = 8;
    private int numCols = 8;
    private int mineCount = 2;
    private MineTile[][] board;
    private ArrayList<MineTile> mineList;
    private Random random = new Random();
    private boolean gameOver = false;
    private boolean firstClick = true;
    private int tilesClicked = 0;
    private CustomGameWin gamewin;

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
            System.err.println("Background image not found for theme: " + theme);
            backgroundLabel.setText("No theme image found for: " + theme);
            backgroundLabel.setForeground(Color.RED);
            backgroundLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
        layeredPane.add(backgroundLabel, Integer.valueOf(0));
        
        //home button
                JButton homeButton = new JButton("Home");
                homeButton.setBounds(690, 30, 70, 50);
                homeButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        cardLayout.show(cardPanel, "Main Menu");
                    }
                });
                layeredPane.add(homeButton, Integer.valueOf(1));
        
        // Gameboard Layer
        JPanel boardPanel = createGameBoard(() -> {
            homeButton.setEnabled(false);
            System.out.println("Win condition triggered for theme: " + theme);
            JPanel winPanel = new CustomGameWin().createWinPanel(cardLayout, cardPanel);
            winPanel.setBounds(185, 0, 815, 620); // Ensure full coverage
            layeredPane.add(winPanel, Integer.valueOf(3));
            layeredPane.revalidate();
            layeredPane.repaint();
        }, () -> {
            homeButton.setEnabled(false);
            System.out.println("Lose condition triggered!");
            JPanel losePanel = new CustomGameLose().createLosePanel(cardLayout, cardPanel);
            losePanel.setBounds(185, 0, 815, 620); // Ensure full coverage
            layeredPane.add(losePanel, Integer.valueOf(3));
            layeredPane.revalidate();
            layeredPane.repaint();
        });
        
        
        
        boardPanel.setBounds(235, 150, 350, 350);
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

                tile.setFocusable(false);
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 12));

                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) return;

                        // Right-click logic
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            if (tile.getText().isEmpty()) {
                                tile.setText("🚩");
                            } else if ("🚩".equals(tile.getText())) {
                                tile.setText("");
                            }
                            return;
                        }

                        // Prevent left-click on flagged tiles
                        if ("🚩".equals(tile.getText())) {
                            return;
                        }

                        // Left-click logic
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (firstClick) {
                                firstClick = false;
                                System.out.println("First click at: (" + tile.r + ", " + tile.c + ")");
                                generateMines(tile.r, tile.c); // Generate mines ensuring the first tile is safe
                            }

                            if (mineList.contains(tile)) {
                                System.out.println("Mine clicked at: (" + tile.r + ", " + tile.c + ")");
                                revealMines();
                                onLose.run();
                            } else {
                                checkTile(tile.r, tile.c);
                                System.out.println("Tiles clicked: " + tilesClicked);

                                // Win condition
                                if (tilesClicked == numRows * numCols - mineCount) {
                                    System.out.println("Win condition met. Tiles clicked: " + tilesClicked);
                                    gameOver = true;
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

    private void generateMines(int safeRow, int safeCol) {
        int minesLeft = mineCount;
        while (minesLeft > 0) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);
            MineTile tile = board[r][c];
            if (!mineList.contains(tile) && !(r == safeRow && c == safeCol)) {
                mineList.add(tile);
                System.out.println("Mine placed at: (" + r + ", " + c + ")");
                minesLeft--;
            }
        }
    }

    private void revealMines() {
        for (MineTile tile : mineList) {
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
        if (!tile.isEnabled()) return; // Skip already revealed tiles

        tile.setEnabled(false);
        tilesClicked++;
        System.out.println("Revealed tile at: (" + r + ", " + c + "). Adjacent mines: " + countAdjacentMines(r, c));

        int adjacentMines = countAdjacentMines(r, c);
        if (adjacentMines > 0) {
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
