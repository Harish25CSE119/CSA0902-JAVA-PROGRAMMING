package gui;

import dao.ItemDAO;
import model.Item;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * DeleteItemFrame allows searching for an item by ID, previewing its details,
 * and deleting it permanently from the MySQL database with user confirmation.
 */
public class DeleteItemFrame extends JFrame {

    private final DashboardFrame parentDashboard;
    private final ItemDAO itemDAO = new ItemDAO();

    private JTextField txtSearchId;
    private JButton btnLoad;

    private JLabel lblPreviewName;
    private JLabel lblPreviewCategory;
    private JLabel lblPreviewStatus;
    private JLabel lblPreviewLocation;
    private JLabel lblPreviewReportedBy;

    private JButton btnDelete;
    private Item loadedItem = null;

    public DeleteItemFrame(DashboardFrame dashboard) {
        this.parentDashboard = dashboard;

        setTitle("Campus Lost & Found - Delete Item Record");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 540);
        setLocationRelativeTo(dashboard);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIUtils.COLOR_BG);

        // Header Panel
        JPanel headerPanel = UIUtils.createHeaderPanel(
                "Delete Item Record",
                "Permanently remove an item entry from the campus database"
        );
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel bodyPanel = new JPanel(new BorderLayout(0, 16));
        bodyPanel.setBackground(UIUtils.COLOR_BG);
        bodyPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        // 1. Top Lookup Bar
        JPanel lookupCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        lookupCard.setBackground(UIUtils.COLOR_SURFACE);
        lookupCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UIUtils.COLOR_BORDER, 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));

        JLabel lblId = UIUtils.createLabel("Enter Item ID:", UIUtils.FONT_BOLD);
        lookupCard.add(lblId);

        txtSearchId = new JTextField(8);
        UIUtils.styleTextField(txtSearchId);
        lookupCard.add(txtSearchId);

        btnLoad = UIUtils.createStyledButton("Load Details", UIUtils.COLOR_ACCENT, Color.WHITE);
        lookupCard.add(btnLoad);

        bodyPanel.add(lookupCard, BorderLayout.NORTH);

        // 2. Item Preview Card
        JPanel previewCard = new JPanel(new GridLayout(5, 2, 8, 8));
        previewCard.setBackground(UIUtils.COLOR_SURFACE);
        previewCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        new LineBorder(UIUtils.COLOR_BORDER, 1, true),
                        " Item Summary Preview ",
                        0, 0, UIUtils.FONT_BOLD, UIUtils.COLOR_PRIMARY
                ),
                new EmptyBorder(16, 20, 16, 20)
        ));

        lblPreviewName = UIUtils.createLabel("-", UIUtils.FONT_BOLD);
        lblPreviewCategory = UIUtils.createLabel("-", UIUtils.FONT_BODY);
        lblPreviewStatus = UIUtils.createLabel("-", UIUtils.FONT_BOLD);
        lblPreviewLocation = UIUtils.createLabel("-", UIUtils.FONT_BODY);
        lblPreviewReportedBy = UIUtils.createLabel("-", UIUtils.FONT_BODY);

        previewCard.add(UIUtils.createLabel("Item Name:", UIUtils.FONT_BOLD));
        previewCard.add(lblPreviewName);

        previewCard.add(UIUtils.createLabel("Category:", UIUtils.FONT_BOLD));
        previewCard.add(lblPreviewCategory);

        previewCard.add(UIUtils.createLabel("Status:", UIUtils.FONT_BOLD));
        previewCard.add(lblPreviewStatus);

        previewCard.add(UIUtils.createLabel("Location:", UIUtils.FONT_BOLD));
        previewCard.add(lblPreviewLocation);

        previewCard.add(UIUtils.createLabel("Reported By:", UIUtils.FONT_BOLD));
        previewCard.add(lblPreviewReportedBy);

        bodyPanel.add(previewCard, BorderLayout.CENTER);

        // 3. Action Buttons Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(UIUtils.COLOR_BG);

        JButton btnBack = UIUtils.createStyledButton("Back", new Color(100, 116, 139), Color.WHITE);
        JButton btnCancel = UIUtils.createStyledButton("Cancel", new Color(203, 213, 225), UIUtils.COLOR_TEXT_MAIN);
        btnDelete = UIUtils.createStyledButton("Delete Item", new Color(220, 38, 38), Color.WHITE); // Crimson Red
        btnDelete.setEnabled(false);

        btnPanel.add(btnBack);
        btnPanel.add(btnCancel);
        btnPanel.add(btnDelete);

        bodyPanel.add(btnPanel, BorderLayout.SOUTH);
        mainPanel.add(bodyPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Event Handlers
        btnLoad.addActionListener(e -> loadItem());
        txtSearchId.addActionListener(e -> loadItem());
        btnDelete.addActionListener(e -> deleteItem());
        btnCancel.addActionListener(e -> clearPreview());
        btnBack.addActionListener(e -> this.dispose());
    }

    private void loadItem() {
        String idText = txtSearchId.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an Item ID!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int itemId;
        try {
            itemId = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Item ID must be an integer!", "Format Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        loadedItem = itemDAO.getItemById(itemId);
        if (loadedItem == null) {
            JOptionPane.showMessageDialog(this, "No item found with ID: " + itemId, "Not Found", JOptionPane.INFORMATION_MESSAGE);
            clearPreview();
        } else {
            lblPreviewName.setText(loadedItem.getItemName());
            lblPreviewCategory.setText(loadedItem.getCategory());
            lblPreviewStatus.setText(loadedItem.getStatus());
            
            if ("Lost".equalsIgnoreCase(loadedItem.getStatus())) {
                lblPreviewStatus.setForeground(UIUtils.COLOR_STATUS_LOST);
            } else if ("Found".equalsIgnoreCase(loadedItem.getStatus())) {
                lblPreviewStatus.setForeground(UIUtils.COLOR_STATUS_FOUND);
            } else {
                lblPreviewStatus.setForeground(UIUtils.COLOR_STATUS_RETURNED);
            }

            lblPreviewLocation.setText(loadedItem.getLocation());
            lblPreviewReportedBy.setText(loadedItem.getReportedBy() + " (" + loadedItem.getContact() + ")");

            btnDelete.setEnabled(true);
        }
    }

    private void deleteItem() {
        if (loadedItem == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this item?\n\n"
                        + "Item ID: " + loadedItem.getItemId() + "\n"
                        + "Name: " + loadedItem.getItemName() + "\n"
                        + "Status: " + loadedItem.getStatus() + "\n\n"
                        + "This action cannot be undone!",
                "Confirm Delete Record",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = itemDAO.deleteItem(loadedItem.getItemId());
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Item record deleted successfully!",
                        "Deletion Completed",
                        JOptionPane.INFORMATION_MESSAGE);
                clearPreview();
                txtSearchId.setText("");
                if (parentDashboard != null) parentDashboard.refreshStats();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete item from database.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearPreview() {
        lblPreviewName.setText("-");
        lblPreviewCategory.setText("-");
        lblPreviewStatus.setText("-");
        lblPreviewStatus.setForeground(UIUtils.COLOR_TEXT_MAIN);
        lblPreviewLocation.setText("-");
        lblPreviewReportedBy.setText("-");
        loadedItem = null;
        btnDelete.setEnabled(false);
    }
}
