package dao;

import database.DatabaseConnection;
import model.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object (DAO) for handling all CRUD operations on the 'items' table in MySQL.
 */
public class ItemDAO {

    /**
     * Inserts a new lost or found item record into the database.
     * @param item Item object containing record details
     * @return true if insertion succeeded, false otherwise.
     */
    public boolean addItem(Item item) {
        String sql = "INSERT INTO items (item_name, description, category, status, location, date_reported, reported_by, contact) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, item.getItemName());
            pstmt.setString(2, item.getDescription());
            pstmt.setString(3, item.getCategory());
            pstmt.setString(4, item.getStatus());
            pstmt.setString(5, item.getLocation());
            pstmt.setDate(6, item.getDateReported());
            pstmt.setString(7, item.getReportedBy());
            pstmt.setString(8, item.getContact());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all item records from the database ordered by item_id descending.
     * @return List of Item objects
     */
    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items ORDER BY item_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Item item = extractItemFromResultSet(rs);
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * Retrieves a single item record by its Primary Key (item_id).
     * @param itemId Unique ID of the item
     * @return Item object if found, null otherwise.
     */
    public Item getItemById(int itemId) {
        String sql = "SELECT * FROM items WHERE item_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, itemId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractItemFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates an existing item record in MySQL.
     * @param item Item object containing updated fields
     * @return true if update succeeded, false otherwise.
     */
    public boolean updateItem(Item item) {
        String sql = "UPDATE items SET item_name = ?, description = ?, category = ?, status = ?, "
                   + "location = ?, date_reported = ?, reported_by = ?, contact = ? WHERE item_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, item.getItemName());
            pstmt.setString(2, item.getDescription());
            pstmt.setString(3, item.getCategory());
            pstmt.setString(4, item.getStatus());
            pstmt.setString(5, item.getLocation());
            pstmt.setDate(6, item.getDateReported());
            pstmt.setString(7, item.getReportedBy());
            pstmt.setString(8, item.getContact());
            pstmt.setInt(9, item.getItemId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes an item record from MySQL by its item_id.
     * @param itemId ID of the item to remove
     * @return true if deletion succeeded, false otherwise.
     */
    public boolean deleteItem(int itemId) {
        String sql = "DELETE FROM items WHERE item_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, itemId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates the status of an item to 'Returned'.
     * @param itemId ID of the item
     * @return true if status was updated, false otherwise.
     */
    public boolean markAsReturned(int itemId) {
        String sql = "UPDATE items SET status = 'Returned' WHERE item_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, itemId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Dynamically searches for items matching multiple criteria.
     * @param keyword Partial match for item_name or description
     * @param category Exact category match or "All Categories"
     * @param status Exact status match or "All Statuses"
     * @param location Partial match for location
     * @return List of matching Item objects
     */
    public List<Item> searchItems(String keyword, String category, String status, String location) {
        List<Item> items = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM items WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (LOWER(item_name) LIKE ? OR LOWER(description) LIKE ?)");
            String kwParam = "%" + keyword.trim().toLowerCase() + "%";
            params.add(kwParam);
            params.add(kwParam);
        }

        if (category != null && !category.trim().isEmpty() && !"All Categories".equalsIgnoreCase(category)) {
            sql.append(" AND category = ?");
            params.add(category.trim());
        }

        if (status != null && !status.trim().isEmpty() && !"All Statuses".equalsIgnoreCase(status)) {
            sql.append(" AND status = ?");
            params.add(status.trim());
        }

        if (location != null && !location.trim().isEmpty()) {
            sql.append(" AND LOWER(location) LIKE ?");
            params.add("%" + location.trim().toLowerCase() + "%");
        }

        sql.append(" ORDER BY item_id DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(extractItemFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    /**
     * Returns a Map containing counts for Total, Lost, Found, and Returned items for Dashboard metrics.
     * @return Map with keys "total", "lost", "found", "returned"
     */
    public Map<String, Integer> getStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total", 0);
        stats.put("lost", 0);
        stats.put("found", 0);
        stats.put("returned", 0);

        String sql = "SELECT status, COUNT(*) AS cnt FROM items GROUP BY status";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int total = 0;
            while (rs.next()) {
                String st = rs.getString("status");
                int count = rs.getInt("cnt");
                total += count;

                if ("Lost".equalsIgnoreCase(st)) {
                    stats.put("lost", count);
                } else if ("Found".equalsIgnoreCase(st)) {
                    stats.put("found", count);
                } else if ("Returned".equalsIgnoreCase(st)) {
                    stats.put("returned", count);
                }
            }
            stats.put("total", total);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    /**
     * Helper method to convert a ResultSet row into an Item object.
     */
    private Item extractItemFromResultSet(ResultSet rs) throws SQLException {
        return new Item(
                rs.getInt("item_id"),
                rs.getString("item_name"),
                rs.getString("description"),
                rs.getString("category"),
                rs.getString("status"),
                rs.getString("location"),
                rs.getDate("date_reported"),
                rs.getString("reported_by"),
                rs.getString("contact")
        );
    }
}
