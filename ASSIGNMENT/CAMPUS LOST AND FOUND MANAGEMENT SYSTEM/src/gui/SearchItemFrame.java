package gui;

import dao.ItemDAO;
import model.Item;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * SearchItemFrame allows multi-criteria searching of items in the MySQL database.
 */
public class SearchItemFrame extends JFrame {

    private final ItemDAO itemDAO = new ItemDAO();

    private JTextField txtKeyword;
    private JComboBox<String> comboCategory;
    private JComboBox<String> comboStatus;
    private JTextField txtLocation;

    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblStatusMessage;

    public SearchItemFrame(DashboardFrame dashboard) {
        setTitle("Campus Lost & Found - Search Items");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(960, 660);
        setLocationRelativeTo(dashboard);

        initComponents();
        performSearch(); // Default search shows all
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIUtils.COLOR_BG);

        // Header Panel
        JPanel headerPanel = UIUtils.createHeaderPanel(
                "Search & Filter Items",
                "Locate lost or found items using name, category, status, or location"
        );
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Top Filter Controls Panel
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBackground(UIUtils.COLOR_SURFACE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UIUtils.COLOR_BORDER, 1),
                new EmptyBorder(16, 20, 16, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Keyword Field
        gbc.gridx = 0; gbc.gridy = 0;
        filterPanel.add(UIUtils.createLabel("Keyword (Name/Desc)", UIUtils.FONT_BOLD), gbc);
        gbc.gridx = 1; gbc.weightx = 0.25;
        txtKeyword = new JTextField();
        UIUtils.styleTextField(txtKeyword);
        filterPanel.add(txtKeyword, gbc);

        // Category Combo
        gbc.gridx = 2; gbc.weightx = 0.0;
        filterPanel.add(UIUtils.createLabel("Category", UIUtils.FONT_BOLD), gbc);
        gbc.gridx = 3; gbc.weightx = 0.25;
        String[] catOptions = new String[UIUtils.CATEGORIES.length + 1];
        catOptions[0] = "All Categories";
        System.arraycopy(UIUtils.CATEGORIES, 0, catOptions, 1, UIUtils.CATEGORIES.length);
        comboCategory = new JComboBox<>(catOptions);
        UIUtils.styleComboBox(comboCategory);
        filterPanel.add(comboCategory, gbc);

        // Status Combo
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        filterPanel.add(UIUtils.createLabel("Status", UIUtils.FONT_BOLD), gbc);
        gbc.gridx = 1; gbc.weightx = 0.25;
        comboStatus = new JComboBox<>(new String[]{"All Statuses", "Lost", "Found", "Returned"});
        UIUtils.styleComboBox(comboStatus);
        filterPanel.add(comboStatus, gbc);

        // Location Field
        gbc.gridx = 2; gbc.weightx = 0.0;
        filterPanel.add(UIUtils.createLabel("Location", UIUtils.FONT_BOLD), gbc);
        gbc.gridx = 3; gbc.weightx = 0.25;
        txtLocation = new JTextField();
        UIUtils.styleTextField(txtLocation);
        filterPanel.add(txtLocation, gbc);

        // Buttons Row
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4; gbc.insets = new Insets(12, 8, 4, 8);
        JPanel btnFilterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnFilterPanel.setBackground(UIUtils.COLOR_SURFACE);

        JButton btnClear = UIUtils.createStyledButton("Clear Filters", new Color(203, 213, 225), UIUtils.COLOR_TEXT_MAIN);
        JButton btnSearch = UIUtils.createStyledButton("Search Items", UIUtils.COLOR_ACCENT, Color.WHITE);

        btnFilterPanel.add(btnClear);
        btnFilterPanel.add(btnSearch);
        filterPanel.add(btnFilterPanel, gbc);

        mainPanel.add(filterPanel, BorderLayout.NORTH);

        // Results Section
        JPanel resultsPanel = new JPanel(new BorderLayout(0, 10));
        resultsPanel.setBackground(UIUtils.COLOR_BG);
        resultsPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        String[] columns = {
            "Item ID", "Item Name", "Description", "Category", 
            "Status", "Location", "Date Reported", "Reported By", "Contact"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        UIUtils.styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.COLOR_BORDER, 1));
        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Status Bar
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(UIUtils.COLOR_BG);

        lblStatusMessage = new JLabel("Enter search filters above to query records.");
        lblStatusMessage.setFont(UIUtils.FONT_BOLD);
        lblStatusMessage.setForeground(UIUtils.COLOR_TEXT_MUTED);

        JButton btnBack = UIUtils.createStyledButton("Back to Dashboard", new Color(100, 116, 139), Color.WHITE);

        bottomPanel.add(lblStatusMessage, BorderLayout.WEST);
        bottomPanel.add(btnBack, BorderLayout.EAST);

        resultsPanel.add(bottomPanel, BorderLayout.SOUTH);
        mainPanel.add(resultsPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Event Handlers
        btnSearch.addActionListener(e -> performSearch());
        btnClear.addActionListener(e -> clearFilters());
        btnBack.addActionListener(e -> this.dispose());

        // Press Enter in text fields to search
        txtKeyword.addActionListener(e -> performSearch());
        txtLocation.addActionListener(e -> performSearch());
    }

    private void performSearch() {
        String keyword = txtKeyword.getText().trim();
        String category = (String) comboCategory.getSelectedItem();
        String status = (String) comboStatus.getSelectedItem();
        String location = txtLocation.getText().trim();

        List<Item> items = itemDAO.searchItems(keyword, category, status, location);
        tableModel.setRowCount(0);

        if (items.isEmpty()) {
            lblStatusMessage.setText("No matching items found.");
            lblStatusMessage.setForeground(UIUtils.COLOR_STATUS_LOST);
        } else {
            for (Item item : items) {
                tableModel.addRow(new Object[]{
                    item.getItemId(),
                    item.getItemName(),
                    item.getDescription(),
                    item.getCategory(),
                    item.getStatus(),
                    item.getLocation(),
                    item.getDateReported() != null ? item.getDateReported().toString() : "",
                    item.getReportedBy(),
                    item.getContact()
                });
            }
            lblStatusMessage.setText("Found " + items.size() + " matching item(s).");
            lblStatusMessage.setForeground(UIUtils.COLOR_STATUS_RETURNED);
        }
    }

    private void clearFilters() {
        txtKeyword.setText("");
        comboCategory.setSelectedIndex(0);
        comboStatus.setSelectedIndex(0);
        txtLocation.setText("");
        performSearch();
    }
}
