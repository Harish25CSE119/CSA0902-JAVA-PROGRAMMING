package gui;

import dao.ItemDAO;
import model.Item;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * ViewItemsFrame displays all lost/found records from the MySQL database in a styled JTable.
 */
public class ViewItemsFrame extends JFrame {

    private final DashboardFrame parentDashboard;
    private final ItemDAO itemDAO = new ItemDAO();

    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblCount;

    public ViewItemsFrame(DashboardFrame dashboard) {
        this.parentDashboard = dashboard;

        setTitle("Campus Lost & Found - View All Items");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(980, 600);
        setLocationRelativeTo(dashboard);

        initComponents();
        loadTableData();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIUtils.COLOR_BG);

        // Header Panel
        JPanel headerPanel = UIUtils.createHeaderPanel(
                "All Reported Items",
                "Complete listing of lost, found, and returned items in campus database"
        );
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Table Panel Container
        JPanel bodyPanel = new JPanel(new BorderLayout(0, 10));
        bodyPanel.setBackground(UIUtils.COLOR_BG);
        bodyPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Column Header Names
        String[] columns = {
            "Item ID", "Item Name", "Description", "Category", 
            "Status", "Location", "Date Reported", "Reported By", "Contact"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only JTable
            }
        };

        table = new JTable(tableModel);
        UIUtils.styleTable(table);

        // Column Width Allocations
        table.getColumnModel().getColumn(0).setPreferredWidth(60);  // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(130); // Name
        table.getColumnModel().getColumn(2).setPreferredWidth(180); // Description
        table.getColumnModel().getColumn(3).setPreferredWidth(110); // Category
        table.getColumnModel().getColumn(4).setPreferredWidth(80);  // Status
        table.getColumnModel().getColumn(5).setPreferredWidth(120); // Location
        table.getColumnModel().getColumn(6).setPreferredWidth(90);  // Date
        table.getColumnModel().getColumn(7).setPreferredWidth(110); // Reported By
        table.getColumnModel().getColumn(8).setPreferredWidth(90);  // Contact

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.COLOR_BORDER, 1));
        bodyPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Action Bar
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(UIUtils.COLOR_BG);

        lblCount = new JLabel("Total Items: 0");
        lblCount.setFont(UIUtils.FONT_BOLD);
        lblCount.setForeground(UIUtils.COLOR_TEXT_MAIN);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(UIUtils.COLOR_BG);

        JButton btnBack = UIUtils.createStyledButton("Back", new Color(100, 116, 139), Color.WHITE);
        JButton btnRefresh = UIUtils.createStyledButton("Refresh List", UIUtils.COLOR_ACCENT, Color.WHITE);

        btnPanel.add(btnBack);
        btnPanel.add(btnRefresh);

        bottomBar.add(lblCount, BorderLayout.WEST);
        bottomBar.add(btnPanel, BorderLayout.EAST);

        bodyPanel.add(bottomBar, BorderLayout.SOUTH);
        mainPanel.add(bodyPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Action Listeners
        btnRefresh.addActionListener(e -> {
            loadTableData();
            if (parentDashboard != null) parentDashboard.refreshStats();
        });
        btnBack.addActionListener(e -> this.dispose());
    }

    /**
     * Fetches all records from MySQL via ItemDAO and populates the JTable.
     */
    public void loadTableData() {
        tableModel.setRowCount(0); // Clear existing rows
        List<Item> items = itemDAO.getAllItems();

        for (Item item : items) {
            Object[] row = {
                item.getItemId(),
                item.getItemName(),
                item.getDescription(),
                item.getCategory(),
                item.getStatus(),
                item.getLocation(),
                item.getDateReported() != null ? item.getDateReported().toString() : "",
                item.getReportedBy(),
                item.getContact()
            };
            tableModel.addRow(row);
        }

        lblCount.setText("Total Records Displayed: " + items.size());
    }
}
