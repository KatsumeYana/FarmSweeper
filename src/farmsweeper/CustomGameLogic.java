package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class CustomGameLogic {

    private static class MineTile extends JButton {
        int r;
        int c;

        public MineTile(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    private int numRows = 8;
    private int numCols = 8;
    private int mineCount = 1;
    private MineTile[][] board;
    private ArrayList<MineTile> mineList;
    private Random random = new Random();
    private boolean gameOver = false;
    private int tilesClicked = 0;

    public JPanel createGameBoard(JLabel timeLabel, JLabel turnLabel, Runnable onFirstClick, Runnable onWin, Runnable onLose) {
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
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 8));

                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) return; // Block interaction if the game is over

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
                            if (tilesClicked == 0) {
                                onFirstClick.run(); // Start the timer
                            }

                            if (mineList.contains(tile)) {
                                revealMines();
                                onLose.run();
                            } else {
                                checkTile(tile.r, tile.c);
                                tilesClicked++;
                                turnLabel.setText("Turn: " + tilesClicked);

                                // Win condition
                                if (tilesClicked == numRows * numCols - mineCount) {
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

        placeMines();
        return boardPanel;
    }

    private void placeMines() {
        int minesLeft = mineCount;
        while (minesLeft > 0) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);
            MineTile tile = board[r][c];
            if (!mineList.contains(tile)) {
                mineList.add(tile);
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
                tile.setEnabled(false); // Disable the button
                for (MouseListener ml : tile.getMouseListeners()) {
                    tile.removeMouseListener(ml); // Remove all mouse listeners
                }
            }
        }
        gameOver = true; // Prevent further interaction
    }

    private void checkTile(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) return;

        MineTile tile = board[r][c];
        if (!tile.isEnabled()) return;

        tile.setEnabled(false);

        int adjacentMines = countAdjacentMines(r, c);
        if (adjacentMines > 0) {
            tile.setText(String.valueOf(adjacentMines));
        } else {
            checkAdjacentTiles(r, c);
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
}
