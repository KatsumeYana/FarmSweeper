package farmsweeper;

import java.awt.*;
import java.io.*;

public class CustomFont {

    // Load custom font from file
    public static Font loadCustomFont(String fontPath, float size) {
        try {
            File fontFile = new File(fontPath);
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            return font.deriveFont(size);
        } catch (FontFormatException | IOException e) {
            System.out.println("Error loading font: " + e.getMessage());
            return new Font("Arial", Font.PLAIN, 14); // Fallback to Arial if loading fails
        }
    }
}
