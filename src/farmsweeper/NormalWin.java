package farmsweeper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NormalWin {
    private final Connection conn;

    public NormalWin(Connection conn) {
        this.conn = conn;
    }

    public void onLevelWin(int level, int difficulty, int stars, int time, int turns) {
        try {
            // Insert or update the level information in the database
            int currentLevel = (difficulty - 1) * 3 + level;
            
            // Check if the current level exists
            PreparedStatement checkStmt = conn.prepareStatement(
                "SELECT level FROM normalgame WHERE level = ?"
            );
            checkStmt.setInt(1, currentLevel);
            boolean levelExists = checkStmt.executeQuery().next();

            if (levelExists) {
                // Update the existing level entry
                PreparedStatement updateStmt = conn.prepareStatement(
                    "UPDATE normalgame SET stars = ?, time = ?, turn = ? WHERE level = ?"
                );
                updateStmt.setInt(1, stars);
                updateStmt.setInt(2, time);
                updateStmt.setInt(3, turns);
                updateStmt.setInt(4, currentLevel);
                updateStmt.executeUpdate();
            } else {
                // Insert a new level entry
                PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO normalgame (level, stars, time, turn) VALUES (?, ?, ?, ?)"
                );
                insertStmt.setInt(1, currentLevel);
                insertStmt.setInt(2, stars);
                insertStmt.setInt(3, time);
                insertStmt.setInt(4, turns);
                insertStmt.executeUpdate();
            }

            // Unlock the next level
            int nextLevel = currentLevel + 1;
            PreparedStatement nextLevelStmt = conn.prepareStatement(
                "INSERT IGNORE INTO normalgame (level, stars, time, turn) VALUES (?, 0, 0, 0)"
            );
            nextLevelStmt.setInt(1, nextLevel);
            nextLevelStmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
