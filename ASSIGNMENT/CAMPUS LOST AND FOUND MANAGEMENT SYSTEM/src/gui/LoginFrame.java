package gui;

import database.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * LoginFrame provides the initial authentication screen for the system.
 * Predefined Admin Credentials: admin / admin123
 */
public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public LoginFrame() {
        setTitle("Campus Lost & Found - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 440);
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIUtils.COLOR_BG);

        // Header Banner
        JPanel headerPanel = UIUtils.createHeaderPanel(
                "Campus Lost & Found",
                "Administrator Authentication Portal");
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Card Form Body
        JPanel bodyContainer = new JPanel(new GridBagLayout());
        bodyContainer.setBackground(UIUtils.COLOR_BG);
        bodyContainer.setBorder(new EmptyBorder(20, 30, 20, 30));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(UIUtils.COLOR_SURFACE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UIUtils.COLOR_BORDER, 1, true),
                new EmptyBorder(24, 24, 24, 24)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username Label & Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel lblUser = UIUtils.createLabel("Username", UIUtils.FONT_BOLD);
        formCard.add(lblUser, gbc);

        gbc.gridy = 1;
        txtUsername = new JTextField(18);
        UIUtils.styleTextField(txtUsername);
        txtUsername.setText("admin"); // Pre-fill convenience
        formCard.add(txtUsername, gbc);

        // Password Label & Field
        gbc.gridy = 2;
        JLabel lblPass = UIUtils.createLabel("Password", UIUtils.FONT_BOLD);
        formCard.add(lblPass, gbc);

        gbc.gridy = 3;
        txtPassword = new JPasswordField(18);
        txtPassword.setFont(UIUtils.FONT_BODY);
        txtPassword.setForeground(UIUtils.COLOR_TEXT_MAIN);
        txtPassword.setBackground(UIUtils.COLOR_SURFACE);
        txtPassword.setCaretColor(UIUtils.COLOR_TEXT_MAIN);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UIUtils.COLOR_BORDER, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        txtPassword.setText("admin123"); // Pre-fill convenience
        formCard.add(txtPassword, gbc);

        // Buttons Panel
        gbc.gridy = 4;
        gbc.insets = new Insets(16, 8, 8, 8);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(UIUtils.COLOR_SURFACE);

        JButton btnExit = UIUtils.createStyledButton("Exit", new Color(100, 116, 139), Color.WHITE);
        JButton btnLogin = UIUtils.createStyledButton("Login", UIUtils.COLOR_ACCENT, Color.WHITE);

        buttonPanel.add(btnExit);
        buttonPanel.add(btnLogin);
        formCard.add(buttonPanel, gbc);

        bodyContainer.add(formCard);
        mainPanel.add(bodyContainer, BorderLayout.CENTER);

        // Footer Info
        JLabel lblFooter = new JLabel("Default Login: admin / admin123  |  Web App: http://localhost:5000", SwingConstants.CENTER);
        lblFooter.setFont(UIUtils.FONT_SMALL);
        lblFooter.setForeground(UIUtils.COLOR_TEXT_MUTED);
        lblFooter.setBorder(new EmptyBorder(0, 0, 15, 0));
        mainPanel.add(lblFooter, BorderLayout.SOUTH);

        add(mainPanel);

        // Action Listeners
        btnLogin.addActionListener(e -> performLogin());
        btnExit.addActionListener(e -> System.exit(0));

        // Submit on Enter key press
        txtPassword.addActionListener(e -> performLogin());
        txtUsername.addActionListener(e -> performLogin());
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both Username and Password.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate Predefined Credentials
        if ("admin".equalsIgnoreCase(username) && "admin123".equals(password)) {
            // Check Database Connectivity
            boolean dbConnected = DatabaseConnection.testConnection();
            if (!dbConnected) {
                DatabaseConnection.showConnectionErrorDialog(this);
            }

            // Launch Dashboard
            DashboardFrame dashboard = new DashboardFrame();
            dashboard.setVisible(true);
            this.dispose(); // Close login window
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid Username or Password!\nHint: Use admin / admin123",
                    "Authentication Failed",
                    JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }
}
