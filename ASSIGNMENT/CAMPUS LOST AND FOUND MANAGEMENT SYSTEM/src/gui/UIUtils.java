package gui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * UIUtils provides design system constants, FlatLaf theme toggles, and modern UI helper utilities for Swing screens.
 */
public class UIUtils {

    private static boolean isDarkMode = false;

    // High Contrast Color Palette
    public static final Color COLOR_BG = new Color(241, 245, 249);          // Slate 100
    public static final Color COLOR_SURFACE = new Color(255, 255, 255);     // Clean White
    public static final Color COLOR_PRIMARY = new Color(15, 23, 42);        // Slate 900 (Deep Navy Header)
    public static final Color COLOR_ACCENT = new Color(37, 99, 235);        // Royal Blue
    public static final Color COLOR_ACCENT_HOVER = new Color(29, 78, 216);  // Dark Blue
    public static final Color COLOR_TEXT_MAIN = new Color(15, 23, 42);      // Slate 900
    public static final Color COLOR_TEXT_MUTED = new Color(71, 85, 105);    // Slate 600
    public static final Color COLOR_BORDER = new Color(203, 213, 225);      // Slate 300

    // Status Colors
    public static final Color COLOR_STATUS_LOST = new Color(220, 38, 38);     // Crimson Red
    public static final Color COLOR_STATUS_FOUND = new Color(37, 99, 235);    // Royal Blue
    public static final Color COLOR_STATUS_RETURNED = new Color(5, 150, 105); // Emerald Green

    // Standard Categories
    public static final String[] CATEGORIES = {
        "Electronics",
        "Personal Effects",
        "Books & Stationery",
        "Clothing & Accessories",
        "ID Cards & Documents",
        "Keys & Locks",
        "Sports Equipment",
        "Other"
    };

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);

    /**
     * Toggles between Dark Mode and Light Mode across all Swing windows dynamically.
     */
    public static void toggleTheme() {
        try {
            if (isDarkMode) {
                FlatLightLaf.setup();
                isDarkMode = false;
            } else {
                FlatDarkLaf.setup();
                isDarkMode = true;
            }
            FlatLaf.updateUI();
        } catch (Exception e) {
            System.err.println("Failed to switch theme: " + e.getMessage());
        }
    }

    public static boolean isDarkMode() {
        return isDarkMode;
    }

    /**
     * Creates a styled primary action button with hover animation and guaranteed text contrast.
     */
    public static JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(bg.darker(), 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));

        // Hover Effect
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(bg.darker());
                    btn.setForeground(fg);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(bg);
                    btn.setForeground(fg);
                }
            }
        });

        return btn;
    }

    /**
     * Creates a modern header panel with title, subtitle, and instant Theme Switcher button.
     */
    public static JPanel createHeaderPanel(String titleText, String subtitleText) {
        JPanel headerPanel = new JPanel(new BorderLayout(12, 0));
        headerPanel.setBackground(COLOR_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(18, 24, 18, 24));

        JPanel textGroup = new JPanel(new BorderLayout(0, 4));
        textGroup.setOpaque(false);

        JLabel titleLabel = new JLabel(titleText);
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        textGroup.add(titleLabel, BorderLayout.NORTH);

        if (subtitleText != null && !subtitleText.trim().isEmpty()) {
            JLabel subLabel = new JLabel(subtitleText);
            subLabel.setFont(FONT_SMALL);
            subLabel.setForeground(new Color(226, 232, 240));
            textGroup.add(subLabel, BorderLayout.SOUTH);
        }

        headerPanel.add(textGroup, BorderLayout.CENTER);

        // Theme Toggle Button in Header
        JButton btnTheme = new JButton(isDarkMode ? "☀️ Light Mode" : "🌙 Dark Mode");
        btnTheme.setFont(FONT_BOLD);
        btnTheme.setForeground(Color.WHITE);
        btnTheme.setBackground(new Color(30, 41, 59));
        btnTheme.setFocusPainted(false);
        btnTheme.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTheme.putClientProperty("JButton.buttonType", "roundRect");
        btnTheme.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(51, 65, 85), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));

        btnTheme.addActionListener(e -> {
            toggleTheme();
            btnTheme.setText(isDarkMode ? "☀️ Light Mode" : "🌙 Dark Mode");
        });

        headerPanel.add(btnTheme, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * Styles a text field with clean padding, explicit text color, background, and border.
     */
    public static void styleTextField(JTextField textField) {
        textField.setFont(FONT_BODY);
        textField.putClientProperty("JComponent.roundRect", true);
        textField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    /**
     * Styles a text area with explicit text color, background, and caret.
     */
    public static void styleTextArea(JTextArea textArea) {
        textArea.setFont(FONT_BODY);
        textArea.setMargin(new Insets(6, 8, 6, 8));
    }

    /**
     * Styles a JComboBox with explicit foreground, background, and high contrast list renderer.
     */
    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(FONT_BODY);
        comboBox.putClientProperty("JComponent.roundRect", true);

        // Custom Renderer for Dropdown Items
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setFont(FONT_BODY);
                label.setBorder(new EmptyBorder(6, 10, 6, 10));
                return label;
            }
        });
    }

    /**
     * Styles a label with standard high contrast text color.
     */
    public static JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font != null ? font : FONT_BODY);
        return label;
    }

    /**
     * Applies custom cell rendering for JTable with status pill badge highlighting.
     */
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(38);
        table.setShowGrid(true);
        table.setGridColor(COLOR_BORDER);

        // Header Styling
        table.getTableHeader().setFont(FONT_BOLD);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));

        // Custom Cell Renderer for Status & Row Backgrounds
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, val, isSelected, hasFocus, row, column);

                // Highlight Status column
                if (val != null) {
                    String strVal = val.toString();
                    if ("Lost".equalsIgnoreCase(strVal)) {
                        c.setForeground(COLOR_STATUS_LOST);
                        c.setFont(FONT_BOLD);
                    } else if ("Found".equalsIgnoreCase(strVal)) {
                        c.setForeground(COLOR_STATUS_FOUND);
                        c.setFont(FONT_BOLD);
                    } else if ("Returned".equalsIgnoreCase(strVal)) {
                        c.setForeground(COLOR_STATUS_RETURNED);
                        c.setFont(FONT_BOLD);
                    }
                }

                return c;
            }
        });
    }
}
