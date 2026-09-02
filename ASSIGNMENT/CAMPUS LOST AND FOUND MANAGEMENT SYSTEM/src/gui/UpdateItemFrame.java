package gui;

import dao.ItemDAO;
import model.Item;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.Date;

/**
 * UpdateItemFrame allows fetching an existing item by Item ID and modifying its fields.
 */
public class UpdateItemFrame extends JFrame {

    private final DashboardFrame parentDashboard;
    private final ItemDAO itemDAO = new ItemDAO();

    private JTextField txtSearchId;
    private JButton btnLoad;

    private JTextField txtItemName;
    private JTextArea txtDescription;
    private JComboBox<String> comboCategory;
    private JComboBox<String> comboStatus;
    private JTextField txtLocation;
    private JTextField txtDateReported;
    private JTextField txtReportedBy;
    private JTextField txtContact;

    private Item loadedItem = null;

    public UpdateItemFrame(DashboardFrame dashboard) {
        this.parentDashboard = dashboard;

        setTitle("Campus Lost & Found - Update Item");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(650, 720);
        setLocationRelativeTo(dashboard);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIUtils.COLOR_BG);

        // Header Panel
        JPanel headerPanel = UIUtils.createHeaderPanel(
                "Update Item Record",
                "Enter an Item ID to load and edit its details in the database"
        );
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel bodyPanel = new JPanel(new BorderLayout(0, 16));
        bodyPanel.setBackground(UIUtils.COLOR_BG);
        bodyPanel.setBorder(new EmptyBorder(16, 24, 16, 24));

        // 1. Top ID Lookup Bar
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

        btnLoad = UIUtils.createStyledButton("Load Item Details", UIUtils.COLOR_ACCENT, Color.WHITE);
        lookupCard.add(btnLoad);

        bodyPanel.add(lookupCard, BorderLayout.NORTH);

        // 2. Form Card
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(UIUtils.COLOR_SURFACE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UIUtils.COLOR_BORDER, 1, true),
                new EmptyBorder(16, 20, 16, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Item Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formCard.add(UIUtils.createLabel("Item Name *", UIUtils.FONT_BOLD), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtItemName = new JTextField();
        UIUtils.styleTextField(txtItemName);
        formCard.add(txtItemName, gbc);

        // Category
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        formCard.add(UIUtils.createLabel("Category *", UIUtils.FONT_BOLD), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        comboCategory = new JComboBox<>(UIUtils.CATEGORIES);
        UIUtils.styleComboBox(comboCategory);
        formCard.add(comboCategory, gbc);

        // Status
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        formCard.add(UIUtils.createLabel("Status *", UIUtils.FONT_BOLD), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        comboStatus = new JComboBox<>(new String[]{"Lost", "Found", "Returned"});
        UIUtils.styleComboBox(comboStatus);
        formCard.add(comboStatus, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        formCard.add(UIUtils.createLabel("Description *", UIUtils.FONT_BOLD), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtDescription = new JTextArea(3, 20);
        UIUtils.styleTextArea(txtDescription);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescription);
        scrollDesc.setBorder(new LineBorder(UIUtils.COLOR_BORDER, 1, true));
        formCard.add(scrollDesc, gbc);

        // Location
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        formCard.add(UIUtils.createLabel("Location *", UIUtils.FONT_BOLD), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtLocation = new JTextField();
        UIUtils.styleTextField(txtLocation);
        formCard.add(txtLocation, gbc);

        // Date Reported
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.3;
        formCard.add(UIUtils.createLabel("Date (YYYY-MM-DD) *", UIUtils.FONT_BOLD), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtDateReported = new JTextField();
        UIUtils.styleTextField(txtDateReported);
        formCard.add(txtDateReported, gbc);

        // Reported By
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0.3;
        formCard.add(UIUtils.createLabel("Reported By *", UIUtils.FONT_BOLD), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtReportedBy = new JTextField();
        UIUtils.styleTextField(txtReportedBy);
        formCard.add(txtReportedBy, gbc);

        // Contact
        gbc.gridx = 0; gbc.gridy = 7; gbc.weightx = 0.3;
        formCard.add(UIUtils.createLabel("Contact Number *", UIUtils.FONT_BOLD), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtContact = new JTextField();
        UIUtils.styleTextField(txtContact);
        formCard.add(txtContact, gbc);

        // Form Buttons
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2; gbc.insets = new Insets(14, 8, 8, 8);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(UIUtils.COLOR_SURFACE);

        JButton btnBack = UIUtils.createStyledButton("Back", new Color(100, 116, 139), Color.WHITE);
        JButton btnClear = UIUtils.createStyledButton("Clear", new Color(203, 213, 225), UIUtils.COLOR_TEXT_MAIN);
        JButton btnUpdate = UIUtils.createStyledButton("Update Record", new Color(109, 40, 217), Color.WHITE); // Purple

        btnPanel.add(btnBack);
        btnPanel.add(btnClear);
        btnPanel.add(btnUpdate);
        formCard.add(btnPanel, gbc);

        bodyPanel.add(formCard, BorderLayout.CENTER);
        mainPanel.add(bodyPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Disable form fields until loaded
        setFormEnabled(false);

        // Action Listeners
        btnLoad.addActionListener(e -> loadItemDetails());
        txtSearchId.addActionListener(e -> loadItemDetails());
        btnUpdate.addActionListener(e -> updateItemDetails());
        btnClear.addActionListener(e -> clearForm());
        btnBack.addActionListener(e -> this.dispose());
    }

    private void loadItemDetails() {
        String idText = txtSearchId.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an Item ID!", "Validation Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int itemId;
        try {
            itemId = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Item ID must be a numeric integer!", "Format Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        loadedItem = itemDAO.getItemById(itemId);
        if (loadedItem == null) {
            JOptionPane.showMessageDialog(this, "No record found with Item ID: " + itemId, "Item Not Found", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            setFormEnabled(false);
        } else {
            // Populate form fields
            txtItemName.setText(loadedItem.getItemName());
            txtDescription.setText(loadedItem.getDescription());
            comboCategory.setSelectedItem(loadedItem.getCategory());
            comboStatus.setSelectedItem(loadedItem.getStatus());
            txtLocation.setText(loadedItem.getLocation());
            txtDateReported.setText(loadedItem.getDateReported() != null ? loadedItem.getDateReported().toString() : "");
            txtReportedBy.setText(loadedItem.getReportedBy());
            txtContact.setText(loadedItem.getContact());

            setFormEnabled(true);
            JOptionPane.showMessageDialog(this, "Loaded record for '" + loadedItem.getItemName() + "'", "Record Loaded", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateItemDetails() {
        if (loadedItem == null) {
            JOptionPane.showMessageDialog(this, "Please load an item record first!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = txtItemName.getText().trim();
        String desc = txtDescription.getText().trim();
        String category = (String) comboCategory.getSelectedItem();
        String status = (String) comboStatus.getSelectedItem();
        String location = txtLocation.getText().trim();
        String dateStr = txtDateReported.getText().trim();
        String reportedBy = txtReportedBy.getText().trim();
        String contact = txtContact.getText().trim();

        if (name.isEmpty() || desc.isEmpty() || location.isEmpty() || dateStr.isEmpty() || reportedBy.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Validation Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date sqlDate;
        try {
            sqlDate = Date.valueOf(dateStr);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Date format! Use YYYY-MM-DD.", "Format Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update fields of loadedItem
        loadedItem.setItemName(name);
        loadedItem.setDescription(desc);
        loadedItem.setCategory(category);
        loadedItem.setStatus(status);
        loadedItem.setLocation(location);
        loadedItem.setDateReported(sqlDate);
        loadedItem.setReportedBy(reportedBy);
        loadedItem.setContact(contact);

        boolean success = itemDAO.updateItem(loadedItem);
        if (success) {
            JOptionPane.showMessageDialog(this, "Item ID " + loadedItem.getItemId() + " updated successfully!", "Update Success", JOptionPane.INFORMATION_MESSAGE);
            if (parentDashboard != null) parentDashboard.refreshStats();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update item in MySQL database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setFormEnabled(boolean enabled) {
        txtItemName.setEnabled(enabled);
        txtDescription.setEnabled(enabled);
        comboCategory.setEnabled(enabled);
        comboStatus.setEnabled(enabled);
        txtLocation.setEnabled(enabled);
        txtDateReported.setEnabled(enabled);
        txtReportedBy.setEnabled(enabled);
        txtContact.setEnabled(enabled);

        // Maintain high contrast even when disabled
        Color fgColor = enabled ? UIUtils.COLOR_TEXT_MAIN : UIUtils.COLOR_TEXT_MUTED;
        txtItemName.setForeground(fgColor);
        txtDescription.setForeground(fgColor);
        txtLocation.setForeground(fgColor);
        txtDateReported.setForeground(fgColor);
        txtReportedBy.setForeground(fgColor);
        txtContact.setForeground(fgColor);
    }

    private void clearForm() {
        txtSearchId.setText("");
        txtItemName.setText("");
        txtDescription.setText("");
        comboCategory.setSelectedIndex(0);
        comboStatus.setSelectedIndex(0);
        txtLocation.setText("");
        txtDateReported.setText("");
        txtReportedBy.setText("");
        txtContact.setText("");
        loadedItem = null;
        setFormEnabled(false);
    }
}
