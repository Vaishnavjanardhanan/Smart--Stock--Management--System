package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SaleDAO {

    public static boolean recordSale(int productId, int quantity, Integer customerId) {
        String sql = "INSERT INTO sales_log (product_id, quantity_sold, customer_id, sale_amount) VALUES (?, ?, ?, ?)";

        try (Connection conn = main.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Calculate sale amount
            double unitPrice = getProductPrice(productId);
            double totalAmount = unitPrice * quantity;

            pstmt.setInt(1, productId);
            pstmt.setInt(2, quantity);

            if (customerId != null) {
                pstmt.setInt(3, customerId);
            } else {
                // Use the walk-in customer
                pstmt.setInt(3, getWalkInCustomerId());
            }

            pstmt.setDouble(4, totalAmount);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Update product stock
                return model.ProductDAO.recordSale(productId, quantity);
            }

        } catch (SQLException e) {
            System.err.println("Error recording sale: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private static double getProductPrice(int productId) throws SQLException {
        String sql = "SELECT price FROM products WHERE product_id = ?";
        try (Connection conn = main.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("price");
            }
        }
        return 0.0;
    }

    private static int getWalkInCustomerId() throws SQLException {
        String sql = "SELECT customer_id FROM customers WHERE name = 'Walk-in Customer'";
        try (Connection conn = main.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("customer_id");
            }
        }
        // If walk-in customer doesn't exist, create one
        return createWalkInCustomer();
    }

    private static int createWalkInCustomer() throws SQLException {
        String sql = "INSERT INTO customers (name, email, phone, address) VALUES ('Walk-in Customer', 'walkin@store.com', '000-0000', 'Store Location')";
        try (Connection conn = main.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException("Failed to create walk-in customer");
    }

    public static List<Sale> getAllSales() throws SQLException {
        List<Sale> sales = new ArrayList<>();
        String sql = """
            SELECT s.sale_id, p.name as product_name, p.product_id,
                   c.name as customer_name, c.customer_id,
                   s.quantity_sold, p.price as unit_price, s.sale_amount, s.sale_date
            FROM sales_log s
            JOIN products p ON s.product_id = p.product_id
            LEFT JOIN customers c ON s.customer_id = c.customer_id
            ORDER BY s.sale_date DESC
            """;

        try (Connection conn = main.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Sale sale = new Sale();
                sale.setSaleId(rs.getInt("sale_id"));
                sale.setProductId(rs.getInt("product_id"));
                sale.setProductName(rs.getString("product_name"));
                sale.setCustomerId(rs.getInt("customer_id"));
                sale.setCustomerName(rs.getString("customer_name"));
                sale.setQuantitySold(rs.getInt("quantity_sold"));
                sale.setUnitPrice(rs.getDouble("unit_price"));
                sale.setTotalAmount(rs.getDouble("sale_amount"));
                sale.setSaleDate(rs.getTimestamp("sale_date"));
                sales.add(sale);
            }
        }
        return sales;
    }

    public static double getTotalSalesToday() throws SQLException {
        String sql = "SELECT COALESCE(SUM(sale_amount), 0) as total FROM sales_log WHERE DATE(sale_date) = CURDATE()";
        try (Connection conn = main.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0.0;
    }

    public static int getTotalSalesCount() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM sales_log";
        try (Connection conn = main.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }
}