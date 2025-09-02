package model;

import main.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public static List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM products")) {

            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getInt("stock")
                );
                products.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    public static boolean addProduct(String name, String description, double price, int stock) {
        String sql = "INSERT INTO products(name, description, price, stock) VALUES(?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setDouble(3, price);
            ps.setInt(4, stock);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateStock(int productId, int newStock) {
        String sql = "UPDATE products SET stock=? WHERE product_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newStock);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE product_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean recordSale(int productId, int quantity) {
        String logSql = "INSERT INTO sales_log(product_id, quantity_sold) VALUES(?, ?)";
        String stockSql = "UPDATE products SET stock = stock - ? WHERE product_id=? AND stock >= ?";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Insert into sales log
            try (PreparedStatement psLog = conn.prepareStatement(logSql)) {
                psLog.setInt(1, productId);
                psLog.setInt(2, quantity);
                psLog.executeUpdate();
            }

            // Decrease stock
            try (PreparedStatement psStock = conn.prepareStatement(stockSql)) {
                psStock.setInt(1, quantity);
                psStock.setInt(2, productId);
                psStock.setInt(3, quantity);
                int rows = psStock.executeUpdate();
                if (rows == 0) {
                    conn.rollback();
                    return false; // Not enough stock
                }
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

