package gui;

import dao.ItemDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Map;

/**
 * DashboardFrame acts as the primary hub of the Campus Lost & Found System.
 * Displays system metrics summary cards and provides access to all CRUD
 * functions.
 */
public class DashboardFrame extends JFrame {

    private final ItemDAO itemDAO = new ItemDAO();

    private JLabel lblTotalCount;
    private JLabel lblLostCount;
    private JLabel lblFoundCount;
    private JLabel lblReturnedCount;

    public DashboardFrame() {
        setTitle("Campus Lost-and-Found Management System - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 640);
        setLocationRelativeTo(null); // Center screen

        initComponents();
        refreshStats();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIUtils.COLOR_BG);

        // Header Panel
        JPanel headerPanel = UIUtils.createHeaderPanel(
                "Campus Lost-and-Found Management System",
                "Main Control Dashboard & Navigation Hub");
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center Content Container
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBackground(UIUtils.COLOR_BG);
        contentPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        // 1. Top Section: Metric Cards
        JPanel metricsGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        metricsGrid.setBackground(UIUtils.COLOR_BG);

        lblTotalCount = new JLabel("0", SwingConstants.CENTER);
        lblLostCount = new JLabel("0", SwingConstants.CENTER);
        lblFoundCount = new JLabel("0", SwingConstants.CENTER);
        lblReturnedCount = new JLabel("0", SwingConstants.CENTER);

        metricsGrid.add(createMetricCard("Total Items Reported", lblTotalCount, UIUtils.COLOR_PRIMARY));
        metricsGrid.add(createMetricCard("Lost Items", lblLostCount, UIUtils.COLOR_STATUS_LOST));
        metricsGrid.add(createMetricCard("Found Items", lblFoundCount, UIUtils.COLOR_STATUS_FOUND));
        metricsGrid.add(createMetricCard("Returned Items", lblReturnedCount, UIUtils.COLOR_STATUS_RETURNED));

        contentPanel.add(metricsGrid, BorderLayout.NORTH);

        // 2. Center Section: Navigation Buttons Grid
        JPanel navGrid = new JPanel(new GridLayout(2, 4, 16, 16));
        navGrid.setBackground(UIUtils.COLOR_BG);

        JButton btnReportLost = createNavButton("Report Lost Item", "Record a lost item report",
                UIUtils.COLOR_STATUS_LOST);
        JButton btnReportFound = createNavButton("Report Found Item", "Record a found item report",
                UIUtils.COLOR_STATUS_FOUND);
        JButton btnViewItems = createNavButton("View All Items", "Browse complete directory", UIUtils.COLOR_PRIMARY);
        JButton btnSearch = createNavButton("Search Items", "Filter by keyword/category", UIUtils.COLOR_ACCENT);

        JButton btnUpdate = createNavButton("Update Item", "Modify existing record", new Color(109, 40, 217)); // Purple
        JButton btnDelete = createNavButton("Delete Item", "Remove an item record", new Color(225, 29, 72)); // Crimson
        JButton btnReturn = createNavButton("Mark as Returned", "Claim/Return item to owner",
                UIUtils.COLOR_STATUS_RETURNED);
        JButton btnRefresh = createNavButton("Refresh Stats", "Reload database counts", new Color(71, 85, 105)); // Slate

        navGrid.add(btnReportLost);
        navGrid.add(btnReportFound);
        navGrid.add(btnViewItems);
        navGrid.add(btnSearch);

        navGrid.add(btnUpdate);
        navGrid.add(btnDelete);
        navGrid.add(btnReturn);
        navGrid.add(btnRefresh);

        contentPanel.add(navGrid, BorderLayout.CENTER);

        // 3. Bottom Section: Action Bar & Logout
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(UIUtils.COLOR_BG);
        bottomBar.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel lblSystemInfo = new JLabel("Logged in as: Admin  |  Localhost Web Server: http://localhost:5000");
        lblSystemInfo.setFont(UIUtils.FONT_SMALL);
        lblSystemInfo.setForeground(UIUtils.COLOR_TEXT_MUTED);

        JButton btnLogout = UIUtils.createStyledButton("Logout", new Color(220, 38, 38), Color.WHITE);

        bottomBar.add(lblSystemInfo, BorderLayout.WEST);
        bottomBar.add(btnLogout, BorderLayout.EAST);

        contentPanel.add(bottomBar, BorderLayout.SOUTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);

        // --- Action Listeners ---
        btnReportLost.addActionListener(e -> new LostItemFrame(this).setVisible(true));
        btnReportFound.addActionListener(e -> new FoundItemFrame(this).setVisible(true));
        btnViewItems.addActionListener(e -> new ViewItemsFrame(this).setVisible(true));
        btnSearch.addActionListener(e -> new SearchItemFrame(this).setVisible(true));
        btnUpdate.addActionListener(e -> new UpdateItemFrame(this).setVisible(true));
        btnDelete.addActionListener(e -> new DeleteItemFrame(this).setVisible(true));
        btnReturn.addActionListener(e -> new ReturnItemFrame(this).setVisible(true));

        btnRefresh.addActionListener(e -> {
            refreshStats();
            JOptionPane.showMessageDialog(this, "Dashboard metrics updated!", "Refreshed",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to log out?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                this.dispose();
            }
        });
    }

    /**
     * Refreshes metric summary numbers from the database.
     */
    public void refreshStats() {
        Map<String, Integer> stats = itemDAO.getStats();
        lblTotalCount.setText(String.valueOf(stats.getOrDefault("total", 0)));
        lblLostCount.setText(String.valueOf(stats.getOrDefault("lost", 0)));
        lblFoundCount.setText(String.valueOf(stats.getOrDefault("found", 0)));
        lblReturnedCount.setText(String.valueOf(stats.getOrDefault("returned", 0)));
    }

    private JPanel createMetricCard(String title, JLabel countLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(UIUtils.COLOR_SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UIUtils.COLOR_BORDER, 1, true),
                new EmptyBorder(14, 16, 14, 16)));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UIUtils.FONT_SMALL);
        titleLbl.setForeground(UIUtils.COLOR_TEXT_MUTED);

        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        countLabel.setForeground(accentColor);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(countLabel, BorderLayout.CENTER);

        // Accent line indicator at bottom
        JPanel line = new JPanel();
        line.setPreferredSize(new Dimension(0, 4));
        line.setBackground(accentColor);
        card.add(line, BorderLayout.SOUTH);

        return card;
    }

    private JButton createNavButton(String title, String subtitle, Color themeColor) {
        JButton button = new JButton();
        button.setLayout(new GridBagLayout());
        button.setBackground(UIUtils.COLOR_SURFACE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UIUtils.COLOR_BORDER, 1, true),
                new EmptyBorder(14, 14, 14, 14)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UIUtils.FONT_HEADER);
        lblTitle.setForeground(themeColor);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(UIUtils.FONT_SMALL);
        lblSub.setForeground(UIUtils.COLOR_TEXT_MUTED);

        button.add(lblTitle, gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(4, 0, 0, 0);
        button.add(lblSub, gbc);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(241, 245, 249));
                button.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(themeColor, 2, true),
                        new EmptyBorder(14, 14, 14, 14)));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(UIUtils.COLOR_SURFACE);
                button.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(UIUtils.COLOR_BORDER, 1, true),
                        new EmptyBorder(14, 14, 14, 14)));
            }
        });

        return button;
    }
}
