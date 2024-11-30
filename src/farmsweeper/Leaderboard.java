package farmsweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Leaderboard {

    private JLabel backgroundLabel;
    private JLabel leaderBoard;
    private JButton backButton;
    private final JTable leaderboardTable;
    private final DefaultTableModel model;  // DefaultTableModel declaration
    
    private String[] difficulties = {"Easy", "Normal", "Hard"};
    private int currentDifficultyIndex = 1; // Default to Normal
    private static String selectedDifficulty = "normal";
    
    // Store the leaderboard data in a list
    private ArrayList<GameRecord> leaderboardData;

    private Connection conn;

    // Constructor initializes the table model
    public Leaderboard() {
        model = new DefaultTableModel(new Object[]{"Rank", "Name", "Time", "Turn", "Difficulty"}, 0);
        leaderboardTable = new JTable(model);
        leaderboardTable.setFillsViewportHeight(true);
        
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
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error connecting to the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to load leaderboard data from the database
    private void loadLeaderboardDataFromDatabase() {
        try (Statement stmt = conn.createStatement()) {
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
            e.printStackTrace();
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
            e.printStackTrace();
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

        // Title for Leaderboard
        leaderBoard = new JLabel();
        try {
            BufferedImage titleIcon = ImageIO.read(new File("leaderboardTitle.png"));
            ImageIcon titleImageIcon = new ImageIcon(titleIcon);
            leaderBoard.setIcon(titleImageIcon);
            leaderBoard.setText("");
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(cardPanel, "Error loading image for Leaderboard title!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        leaderBoard.setBounds(15, 40, 700, 105);
        layeredPane.add(leaderBoard);
        
        // Difficulty Selection
        JLabel difficultyLabel = new JLabel(difficulties[currentDifficultyIndex], SwingConstants.CENTER);
        difficultyLabel.setBounds(300, 150, 100, 30);
        JButton difficultyLeftBtn = new JButton("<");
        difficultyLeftBtn.setBounds(260, 150, 50, 70);
        JButton difficultyRightBtn = new JButton(">");
        difficultyRightBtn.setBounds(400, 150, 50, 70);

        difficultyLeftBtn.addActionListener(e -> {
            currentDifficultyIndex = (currentDifficultyIndex - 1 + difficulties.length) % difficulties.length;
            selectedDifficulty = difficulties[currentDifficultyIndex]; // Update selected difficulty
            difficultyLabel.setText(selectedDifficulty);
        });

        difficultyRightBtn.addActionListener(e -> {
            currentDifficultyIndex = (currentDifficultyIndex + 1) % difficulties.length;
            selectedDifficulty = difficulties[currentDifficultyIndex]; // Update selected difficulty
            difficultyLabel.setText(selectedDifficulty);
        });

        layeredPane.add(difficultyLabel);
        layeredPane.add(difficultyLeftBtn);
        layeredPane.add(difficultyRightBtn);
        
        //Filter Button
        JButton filterBtn = new JButton("Filter");
        filterBtn.setBounds(490, 150, 100, 70);
        filterBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Filter the leaderboard based on the selected difficulty
                filterLeaderboardByDifficulty(selectedDifficulty);
            }
        });
        layeredPane.add(filterBtn);
        
        
        
        // Back Button
        backButton = new JButton("");
        try {
            BufferedImage buttonIcon = ImageIO.read(new File("backButton.png"));
            ImageIcon buttonImageIcon = new ImageIcon(buttonIcon);
            backButton.setIcon(buttonImageIcon);
            backButton.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(cardPanel, "Error loading image for Back button!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        backButton.setBounds(300, 490, 200, 105);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setContentAreaFilled(false);

        backButton.addActionListener(e -> cardLayout.show(cardPanel, "Main Menu"));
        layeredPane.add(backButton);
        
        

        // JTable and fonts for columns
        Font headerFont = loadCustomFont("PressStart2P-Regular.ttf", 12);
        Font rankFont = loadCustomFont("PressStart2P-Regular.ttf", 10);
        Font nameFont = loadCustomFont("PressStart2P-Regular.ttf", 10);
        Font timeFont = loadCustomFont("PressStart2P-Regular.ttf", 10);
        Font turnFont = loadCustomFont("PressStart2P-Regular.ttf", 10);
        Font difficultyFont = loadCustomFont("PressStart2P-Regular.ttf", 10);

        JTableHeader tableHeader = leaderboardTable.getTableHeader();
        tableHeader.setFont(headerFont);

        leaderboardTable.getColumnModel().getColumn(0).setCellRenderer(new CustomCellRenderer(rankFont));
        leaderboardTable.getColumnModel().getColumn(1).setCellRenderer(new CustomCellRenderer(nameFont));
        leaderboardTable.getColumnModel().getColumn(2).setCellRenderer(new CustomCellRenderer(timeFont));
        leaderboardTable.getColumnModel().getColumn(3).setCellRenderer(new CustomCellRenderer(turnFont));
        leaderboardTable.getColumnModel().getColumn(4).setCellRenderer(new CustomCellRenderer(difficultyFont));

        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        scrollPane.setBounds(50, 250, 700, 200);
        layeredPane.add(scrollPane);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(layeredPane, BorderLayout.CENTER);

        // Background setup
        ImageIcon backgroundIcon = loadImage("backgroundGame.png");
        if (backgroundIcon != null) {
            backgroundLabel = new JLabel(backgroundIcon);
            Image image = backgroundIcon.getImage();
            Image scaledImage = image.getScaledInstance(815, 620, Image.SCALE_SMOOTH);
            backgroundLabel.setIcon(new ImageIcon(scaledImage));
            backgroundLabel.setBounds(0, 0, 815, 620);
            layeredPane.add(backgroundLabel);
        } else {
            System.err.println("Error: Background image not found.");
        }
        return panel;
    }

    // Method to load images and handle null cases
    private ImageIcon loadImage(String fileName) {
        File imageFile = new File(fileName);
        if (imageFile.exists()) {
            return new ImageIcon(imageFile.getPath());
        } else {
            System.err.println("Error: Image not found: " + fileName);
            return null;
        }
    }
    
    // Load custom font for table headers and cells
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

    // Custom cell renderer to apply different fonts to columns
    private static class CustomCellRenderer extends DefaultTableCellRenderer {
        private Font font;

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
        private String name;
        private String difficulty;
        private String time;
        private int turnsTaken;

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
        // Reload the leaderboard data from the database, filtering by the selected difficulty
        selectedDifficulty = difficulty; // Update selected difficulty
        loadLeaderboardDataFromDatabase(); // This method now takes care of filtering the data from the database
    }


    // Method to update the table with filtered data
    private void updateTableFromFilteredData(ArrayList<GameRecord> filteredData) {
        // Clear the existing rows in the table
        model.setRowCount(0);

        // Add filtered records to the table
        for (int i = 0; i < filteredData.size(); i++) {
            GameRecord record = filteredData.get(i);
            model.addRow(new Object[]{i + 1, record.getName(), record.getTime(), record.getTurnsTaken(), record.getDifficulty()});
        }

        // Notify the table that data has changed
        model.fireTableDataChanged();
    }

}
