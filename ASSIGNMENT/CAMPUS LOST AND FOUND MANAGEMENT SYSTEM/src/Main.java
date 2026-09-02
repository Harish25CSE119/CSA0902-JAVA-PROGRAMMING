import com.formdev.flatlaf.FlatLightLaf;
import database.DatabaseConnection;
import gui.LoginFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Main application entry point for Campus Lost-and-Found Management System.
 * Configured with FlatLaf modern UI theme engine.
 */
public class Main {

    public static void main(String[] args) {
        // Configure FlatLaf Modern Flat Look & Feel
        try {
            FlatLightLaf.setup();
            
            // Configure UI defaults for modern rounded controls
            UIManager.put("Button.arc", 14);
            UIManager.put("Component.arc", 12);
            UIManager.put("ProgressBar.arc", 12);
            UIManager.put("TextComponent.arc", 12);
            UIManager.put("ScrollBar.showButtons", true);
            UIManager.put("Table.alternateRowColor", new Color(248, 250, 252));
            UIManager.put("Table.rowHeight", 36);
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Could not set Look & Feel.");
            }
        }

        // Launch Login UI on Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            // Test MySQL connection silently on startup
            boolean connected = DatabaseConnection.testConnection();
            if (!connected) {
                System.out.println("Notice: MySQL database is currently unreachable. Configure DatabaseConnection.java or start MySQL service.");
            }

            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
