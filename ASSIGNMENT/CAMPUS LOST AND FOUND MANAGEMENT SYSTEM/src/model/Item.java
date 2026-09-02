package model;

import java.sql.Date;

/**
 * Item represents a lost or found item record in the system.
 */
public class Item {
    private int itemId;
    private String itemName;
    private String description;
    private String category;
    private String status; // "Lost", "Found", "Returned"
    private String location;
    private Date dateReported;
    private String reportedBy;
    private String contact;

    // Default constructor
    public Item() {}

    // Constructor without itemId (for insertion)
    public Item(String itemName, String description, String category, String status, 
                String location, Date dateReported, String reportedBy, String contact) {
        this.itemName = itemName;
        this.description = description;
        this.category = category;
        this.status = status;
        this.location = location;
        this.dateReported = dateReported;
        this.reportedBy = reportedBy;
        this.contact = contact;
    }

    // Full constructor with itemId (for reading/updating)
    public Item(int itemId, String itemName, String description, String category, String status, 
                String location, Date dateReported, String reportedBy, String contact) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.description = description;
        this.category = category;
        this.status = status;
        this.location = location;
        this.dateReported = dateReported;
        this.reportedBy = reportedBy;
        this.contact = contact;
    }

    // Getters and Setters
    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDateReported() {
        return dateReported;
    }

    public void setDateReported(Date dateReported) {
        this.dateReported = dateReported;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return "Item{" +
                "itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                ", category='" + category + '\'' +
                ", status='" + status + '\'' +
                ", location='" + location + '\'' +
                ", dateReported=" + dateReported +
                ", reportedBy='" + reportedBy + '\'' +
                '}';
    }
}
