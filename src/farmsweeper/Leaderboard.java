package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.*;
import java.sql.*;
import java.util.ArrayList;
public class Leaderboard {

    private JLabel backgroundLabel;
    private final JTable leaderboardTable;
    private final DefaultTableModel model;  // DefaultTableModel declaration
    
    private final String[] difficulties = {"Easy", "Normal", "Hard"};
    private int currentDifficultyIndex = 1; // Default to Normal
    private static String selectedDifficulty = "normal";
    // Store the leaderboard data in a list
    private final ArrayList<GameRecord> leaderboardData;

    private Connection conn;

    // Constructor initializes the table model
    public Leaderboard() {
        model = new DefaultTableModel(new Object[]{"Rank", "Name", "Time", "Turn"}, 0);
        leaderboardTable = new JTable(model);
        leaderboardTable.setFillsViewportHeight(true);
        leaderboardTable.setEnabled(false);
        
        
        // Initialize the leaderboard data
        leaderboardData = new ArrayList<>();
        
        // Connect to the database and load data
        connectToDatabase();
        loadLeaderboardDataFromDatabase();
    }

    // Method to connect to the database
    private void connectToDatabase() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/farmsweeper", "root", "Juliana");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error connecting to the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to load leaderboard data from the database
    private void loadLeaderboardDataFromDatabase() {
        try {
            // Adjust the query to use the selectedDifficulty directly in the WHERE clause
            String query = "SELECT player_name, difficulty, time_taken, turns_taken " +
                           "FROM customgamerecords WHERE difficulty = ? ORDER BY time_taken ASC";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, selectedDifficulty); // Use selectedDifficulty for filtering
                ResultSet rs = pstmt.executeQuery();

                // Clear previous data
                leaderboardData.clear();

                // Process each record from the database
                while (rs.next()) {
                    String name = rs.getString("player_name");
                    String difficulty = rs.getString("difficulty");
                    int timeTaken = rs.getInt("time_taken");
                    int turnsTaken = rs.getInt("turns_taken");

                    // Convert time to mm:ss format
                    String timeFormatted = formatTime(timeTaken);

                    // Add to leaderboardData
                    leaderboardData.add(new GameRecord(name, difficulty, timeFormatted, turnsTaken));
                }

                // Update table with the latest data
                updateTableFromData();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading leaderboard data.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    // Method to add a new game record to the database
    public void addGameRecord(String name, String difficulty, String time, int turnsTaken) {
        System.out.println("Adding new game record: " + name + ", " + difficulty + ", " + time + ", " + turnsTaken);

        // Create a new GameRecord object and insert it into the database
        String query = "INSERT INTO customgamerecords (player_name, difficulty, time_taken, turns_taken) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            // Convert time (mm:ss) to total seconds
            int timeInSeconds = convertTimeToSeconds(time);

            pstmt.setString(1, name);
            pstmt.setString(2, difficulty);
            pstmt.setInt(3, timeInSeconds);
            pstmt.setInt(4, turnsTaken);
            pstmt.executeUpdate();

            // Load the updated leaderboard data from the database
            loadLeaderboardDataFromDatabase();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error saving game record.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to sort the leaderboard data by time (ascending)
    public void sortLeaderboardData() {
        leaderboardData.sort((recordA, recordB) -> {
            long timeA = convertTimeToSeconds(recordA.getTime());
            long timeB = convertTimeToSeconds(recordB.getTime());
            return Long.compare(timeA, timeB); // Sorting by time (ascending)
        });
    }

    // Method to update the table from the leaderboard data
    private void updateTableFromData() {
        // Clear the existing rows in the table
        model.setRowCount(0);

        // Add sorted records to the table
        for (int i = 0; i < leaderboardData.size(); i++) {
            GameRecord record = leaderboardData.get(i);
            model.addRow(new Object[]{i + 1, record.getName(), record.getTime(), record.getTurnsTaken(), record.getDifficulty()});
        }

        // Notify the table that data has changed
        model.fireTableDataChanged();
    }

    // Method to convert time string (mm:ss) to seconds
    private int convertTimeToSeconds(String time) {
        String[] parts = time.split(":");
        int minutes = Integer.parseInt(parts[0]);
        int seconds = Integer.parseInt(parts[1]);
        return minutes * 60 + seconds;  // Return total time in seconds
    }

    // Format time (in seconds) to mm:ss
    private String formatTime(int timeTaken) {
        int minutes = timeTaken / 60;
        int seconds = timeTaken % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // Method to create the leaderboard panel
    public JPanel createLeaderboardPanel(CardLayout cardLayout, JPanel cardPanel) {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        
        // JTable and fonts for columns
        Font headerFont = BaseGame.loadCustomFont("PressStart2P-Regular.ttf", 12);
        Font textFont = BaseGame.loadCustomFont("PressStart2P-Regular.ttf", 10);
        Font filterFont = BaseGame.loadCustomFont("PressStart2P-Regular.ttf", 16);
        
        // Difficulty Selection
        JLabel difficultyLabel = new JLabel(difficulties[currentDifficultyIndex], SwingConstants.CENTER);
        difficultyLabel.setFont(filterFont);
        difficultyLabel.setBounds(370, 178, 100, 30);
        // Left button to change difficulty
        String difficultyLeftIconPath = "Leaderboard Difficulty Previous Button.png";
        JButton difficultyLeftBtn = BaseGame.createButton(difficultyLeftIconPath, 270, 160, 63, 64,(ActionEvent e) -> {
            currentDifficultyIndex = (currentDifficultyIndex - 1 + difficulties.length) % difficulties.length;
            difficultyLabel.setText(difficulties[currentDifficultyIndex]);
        });

        // Right button to change difficulty
        String difficultyRightIconPath = "Leaderboard Difficulty Next Button.png";
        JButton difficultyRightBtn = BaseGame.createButton(difficultyRightIconPath, 510, 160, 63, 64,(ActionEvent e) -> {
            currentDifficultyIndex = (currentDifficultyIndex + 1) % difficulties.length;
            difficultyLabel.setText(difficulties[currentDifficultyIndex]);
        });

        layeredPane.add(difficultyLabel);
        layeredPane.add(difficultyLeftBtn);
        layeredPane.add(difficultyRightBtn);
        
        //OK Button
        String okRightIconPath = "Leaderboard  Difficulty Ok Button.png";
        JButton okBtn = BaseGame.createButton(okRightIconPath, 650, 170, 92, 41, (ActionEvent e) -> {
            // Filter the leaderboard based on the selected difficulty
            selectedDifficulty = difficulties[currentDifficultyIndex].toLowerCase();  // Ensure it matches the database
            filterLeaderboardByDifficulty(selectedDifficulty);
        });
        layeredPane.add(okBtn);
        
        // Home Button
        String backIconPath = "back.png"; 
        JButton backButton = BaseGame.createButton(backIconPath, 20, 10, 70, 38, (ActionEvent e) -> {
            cardLayout.show(cardPanel, "Main Menu");
            System.out.println("You pressed home button");

        });
        layeredPane.add(backButton);
        

        //Table
        JTableHeader tableHeader = leaderboardTable.getTableHeader();
        tableHeader.setEnabled(false);
        tableHeader.setFont(headerFont);

        leaderboardTable.getColumnModel().getColumn(0).setCellRenderer(new CustomCellRenderer(textFont));
        leaderboardTable.getColumnModel().getColumn(1).setCellRenderer(new CustomCellRenderer(textFont));
        leaderboardTable.getColumnModel().getColumn(2).setCellRenderer(new CustomCellRenderer(textFont));
        leaderboardTable.getColumnModel().getColumn(3).setCellRenderer(new CustomCellRenderer(textFont));
        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        scrollPane.setBounds(155, 250, 700, 300);
        layeredPane.add(scrollPane);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(layeredPane, BorderLayout.CENTER);

        // Background setup
        ImageIcon backgroundIcon = BaseGame.loadImage("Leaderboard Background.png");
        if (backgroundIcon != null) {
            backgroundLabel = new JLabel(backgroundIcon);
            Image image = backgroundIcon.getImage();
            Image scaledImage = image.getScaledInstance(1000, 700, Image.SCALE_SMOOTH);
            backgroundLabel.setIcon(new ImageIcon(scaledImage));
            backgroundLabel.setBounds(0, 0, 1000, 700);
            layeredPane.add(backgroundLabel);
        } else {
            System.err.println("Error: Background image not found.");
        }
        return panel;
        
        
    }

    // Custom cell renderer to apply different fonts to columns
    private static class CustomCellRenderer extends DefaultTableCellRenderer {
        private final Font font;

        public CustomCellRenderer(Font font) {
            this.font = font;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            component.setFont(font);
            return component;
        }
    }

    // Inner class to represent a game record
    private static class GameRecord {
        private final String name;
        private final String difficulty;
        private final String time;
        private final int turnsTaken;

        public GameRecord(String name, String difficulty, String time, int turnsTaken) {
            this.name = name;
            this.difficulty = difficulty;
            this.time = time;
            this.turnsTaken = turnsTaken;
        }

        public String getName() {
            return name;
        }

        public String getDifficulty() {
            return difficulty;
        }

        public String getTime() {
            return time;
        }

        public int getTurnsTaken() {
            return turnsTaken;
        }
    }
    
    // Method to filter leaderboard data by difficulty
    private void filterLeaderboardByDifficulty(String difficulty) {
        // Update the selected difficulty before querying the database
        selectedDifficulty = difficulty.toLowerCase(); // Ensure case match

        // Reload the leaderboard data from the database
        loadLeaderboardDataFromDatabase();
    }


   

    
}