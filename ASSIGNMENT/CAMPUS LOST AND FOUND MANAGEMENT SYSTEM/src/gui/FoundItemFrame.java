package gui;

import dao.ItemDAO;
import model.Item;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;

/**
 * FoundItemFrame provides the form interface to report a found item on campus.
 */
public class FoundItemFrame extends JFrame {

    private final DashboardFrame parentDashboard;
    private final ItemDAO itemDAO = new ItemDAO();

    private JTextField txtItemName;
    private JTextArea txtDescription;
    private JComboBox<String> comboCategory;
    private JTextField txtLocation;
    private JTextField txtDateReported;
    private JTextField txtReportedBy;
    private JTextField txtContact;

    public FoundItemFrame(DashboardFrame dashboard) {
        this.parentDashboard = dashboard;

        setTitle("Campus Lost & Found - Report Found Item");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(620, 660);
        setLocationRelativeTo(dashboard);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIUtils.COLOR_BG);

        // Header Panel
        JPanel headerPanel = UIUtils.createHeaderPanel(
                "Report Found Item",
                "Fill in the details of an item found on campus"
        );
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form Container
        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBackground(UIUtils.COLOR_BG);
        bodyPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(UIUtils.COLOR_SURFACE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UIUtils.COLOR_BORDER, 1, true),
                new EmptyBorder(20, 24, 20, 24)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Item Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formCard.add(UIUtils.createLabel("Item Name *", UIUtils.FONT_BOLD), gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtItemName = new JTextField();
        UIUtils.styleTextField(txtItemName);
        formCard.add(txtItemName, gbc);

        // Row 1: Category
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        formCard.add(UIUtils.createLabel("Category *", UIUtils.FONT_BOLD), gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        comboCategory = new JComboBox<>(UIUtils.CATEGORIES);
        UIUtils.styleComboBox(comboCategory);
        formCard.add(comboCategory, gbc);

        // Row 2: Description
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        formCard.add(UIUtils.createLabel("Description *", UIUtils.FONT_BOLD), gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtDescription = new JTextArea(3, 20);
        UIUtils.styleTextArea(txtDescription);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescription);
        scrollDesc.setBorder(new LineBorder(UIUtils.COLOR_BORDER, 1, true));
        formCard.add(scrollDesc, gbc);

        // Row 3: Location Found
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        formCard.add(UIUtils.createLabel("Location Found *", UIUtils.FONT_BOLD), gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtLocation = new JTextField();
        UIUtils.styleTextField(txtLocation);
        formCard.add(txtLocation, gbc);

        // Row 4: Date Reported
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        formCard.add(UIUtils.createLabel("Date (YYYY-MM-DD) *", UIUtils.FONT_BOLD), gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtDateReported = new JTextField(LocalDate.now().toString());
        UIUtils.styleTextField(txtDateReported);
        formCard.add(txtDateReported, gbc);

        // Row 5: Reported By
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.3;
        formCard.add(UIUtils.createLabel("Reported By *", UIUtils.FONT_BOLD), gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtReportedBy = new JTextField();
        UIUtils.styleTextField(txtReportedBy);
        formCard.add(txtReportedBy, gbc);

        // Row 6: Contact Number
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0.3;
        formCard.add(UIUtils.createLabel("Contact Number *", UIUtils.FONT_BOLD), gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtContact = new JTextField();
        UIUtils.styleTextField(txtContact);
        formCard.add(txtContact, gbc);

        // Row 7: Buttons
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; gbc.insets = new Insets(16, 8, 8, 8);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(UIUtils.COLOR_SURFACE);

        JButton btnBack = UIUtils.createStyledButton("Back", new Color(100, 116, 139), Color.WHITE);
        JButton btnClear = UIUtils.createStyledButton("Clear", new Color(203, 213, 225), UIUtils.COLOR_TEXT_MAIN);
        JButton btnSubmit = UIUtils.createStyledButton("Submit Report", UIUtils.COLOR_STATUS_FOUND, Color.WHITE);

        btnPanel.add(btnBack);
        btnPanel.add(btnClear);
        btnPanel.add(btnSubmit);
        formCard.add(btnPanel, gbc);

        bodyPanel.add(formCard);
        mainPanel.add(bodyPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Action Listeners
        btnSubmit.addActionListener(e -> submitForm());
        btnClear.addActionListener(e -> clearForm());
        btnBack.addActionListener(e -> this.dispose());
    }

    private void submitForm() {
        String name = txtItemName.getText().trim();
        String desc = txtDescription.getText().trim();
        String category = (String) comboCategory.getSelectedItem();
        String location = txtLocation.getText().trim();
        String dateStr = txtDateReported.getText().trim();
        String reportedBy = txtReportedBy.getText().trim();
        String contact = txtContact.getText().trim();

        if (name.isEmpty() || desc.isEmpty() || location.isEmpty() || dateStr.isEmpty() || reportedBy.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "All fields marked with * are required!",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date sqlDate;
        try {
            sqlDate = Date.valueOf(dateStr);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid Date format! Please use YYYY-MM-DD (e.g. 2026-09-02).",
                    "Format Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create Item with status="Found"
        Item item = new Item(name, desc, category, "Found", location, sqlDate, reportedBy, contact);

        boolean success = itemDAO.addItem(item);
        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Found Item reported successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            if (parentDashboard != null) {
                parentDashboard.refreshStats();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to save record to MySQL database.\nPlease check your DB connection.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtItemName.setText("");
        txtDescription.setText("");
        comboCategory.setSelectedIndex(0);
        txtLocation.setText("");
        txtDateReported.setText(LocalDate.now().toString());
        txtReportedBy.setText("");
        txtContact.setText("");
    }
}
