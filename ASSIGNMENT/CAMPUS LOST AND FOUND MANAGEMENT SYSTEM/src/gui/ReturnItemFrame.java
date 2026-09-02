package gui;

import dao.ItemDAO;
import model.Item;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * ReturnItemFrame allows marking a lost or found item as 'Returned' (claimed by rightful owner).
 */
public class ReturnItemFrame extends JFrame {

    private final DashboardFrame parentDashboard;
    private final ItemDAO itemDAO = new ItemDAO();

    private JTextField txtSearchId;
    private JButton btnLoad;

    private JLabel lblName;
    private JLabel lblCategory;
    private JLabel lblCurrentStatus;
    private JLabel lblLocation;
    private JLabel lblReportedBy;

    private JButton btnMarkReturned;
    private Item loadedItem = null;

    public ReturnItemFrame(DashboardFrame dashboard) {
        this.parentDashboard = dashboard;

        setTitle("Campus Lost & Found - Mark Item as Returned");
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
                "Mark Item as Returned / Claimed",
                "Update item status to 'Returned' after successful ownership verification"
        );
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel bodyPanel = new JPanel(new BorderLayout(0, 16));
        bodyPanel.setBackground(UIUtils.COLOR_BG);
        bodyPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        // 1. Top Search Bar
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

        // 2. Details Preview Card
        JPanel card = new JPanel(new GridLayout(5, 2, 8, 8));
        card.setBackground(UIUtils.COLOR_SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        new LineBorder(UIUtils.COLOR_BORDER, 1, true),
                        " Item Verification Details ",
                        0, 0, UIUtils.FONT_BOLD, UIUtils.COLOR_PRIMARY
                ),
                new EmptyBorder(16, 20, 16, 20)
        ));

        lblName = UIUtils.createLabel("-", UIUtils.FONT_BOLD);
        lblCategory = UIUtils.createLabel("-", UIUtils.FONT_BODY);
        lblCurrentStatus = UIUtils.createLabel("-", UIUtils.FONT_BOLD);
        lblLocation = UIUtils.createLabel("-", UIUtils.FONT_BODY);
        lblReportedBy = UIUtils.createLabel("-", UIUtils.FONT_BODY);

        card.add(UIUtils.createLabel("Item Name:", UIUtils.FONT_BOLD));
        card.add(lblName);

        card.add(UIUtils.createLabel("Category:", UIUtils.FONT_BOLD));
        card.add(lblCategory);

        card.add(UIUtils.createLabel("Current Status:", UIUtils.FONT_BOLD));
        card.add(lblCurrentStatus);

        card.add(UIUtils.createLabel("Location:", UIUtils.FONT_BOLD));
        card.add(lblLocation);

        card.add(UIUtils.createLabel("Reported By:", UIUtils.FONT_BOLD));
        card.add(lblReportedBy);

        bodyPanel.add(card, BorderLayout.CENTER);

        // 3. Action Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(UIUtils.COLOR_BG);

        JButton btnBack = UIUtils.createStyledButton("Back", new Color(100, 116, 139), Color.WHITE);
        btnMarkReturned = UIUtils.createStyledButton("Mark as Returned", UIUtils.COLOR_STATUS_RETURNED, Color.WHITE);
        btnMarkReturned.setEnabled(false);

        btnPanel.add(btnBack);
        btnPanel.add(btnMarkReturned);

        bodyPanel.add(btnPanel, BorderLayout.SOUTH);
        mainPanel.add(bodyPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Event Handlers
        btnLoad.addActionListener(e -> loadItem());
        txtSearchId.addActionListener(e -> loadItem());
        btnMarkReturned.addActionListener(e -> processReturn());
        btnBack.addActionListener(e -> this.dispose());
    }

    private void loadItem() {
        String idText = txtSearchId.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an Item ID!", "Validation Warning", JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "No record found with Item ID: " + itemId, "Item Not Found", JOptionPane.INFORMATION_MESSAGE);
            clearPreview();
        } else {
            lblName.setText(loadedItem.getItemName());
            lblCategory.setText(loadedItem.getCategory());
            lblCurrentStatus.setText(loadedItem.getStatus());
            lblLocation.setText(loadedItem.getLocation());
            lblReportedBy.setText(loadedItem.getReportedBy() + " (" + loadedItem.getContact() + ")");

            if ("Returned".equalsIgnoreCase(loadedItem.getStatus())) {
                lblCurrentStatus.setForeground(UIUtils.COLOR_STATUS_RETURNED);
                btnMarkReturned.setEnabled(false);
                JOptionPane.showMessageDialog(this,
                        "Notice: This item has ALREADY been marked as Returned!",
                        "Already Returned",
                        JOptionPane.INFORMATION_MESSAGE);
            } else if ("Lost".equalsIgnoreCase(loadedItem.getStatus())) {
                lblCurrentStatus.setForeground(UIUtils.COLOR_STATUS_LOST);
                btnMarkReturned.setEnabled(true);
            } else {
                lblCurrentStatus.setForeground(UIUtils.COLOR_STATUS_FOUND);
                btnMarkReturned.setEnabled(true);
            }
        }
    }

    private void processReturn() {
        if (loadedItem == null) return;

        if ("Returned".equalsIgnoreCase(loadedItem.getStatus())) {
            JOptionPane.showMessageDialog(this, "This item is already marked as returned!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success = itemDAO.markAsReturned(loadedItem.getItemId());
        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Item ID " + loadedItem.getItemId() + " ('" + loadedItem.getItemName() + "') marked as RETURNED!",
                    "Status Updated",
                    JOptionPane.INFORMATION_MESSAGE);
            
            // Reload and refresh
            loadItem();
            if (parentDashboard != null) parentDashboard.refreshStats();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update status in database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearPreview() {
        lblName.setText("-");
        lblCategory.setText("-");
        lblCurrentStatus.setText("-");
        lblCurrentStatus.setForeground(UIUtils.COLOR_TEXT_MAIN);
        lblLocation.setText("-");
        lblReportedBy.setText("-");
        loadedItem = null;
        btnMarkReturned.setEnabled(false);
    }
}
