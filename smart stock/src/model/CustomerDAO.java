package model;

import main.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    // Add Customer
    public static boolean addCustomer(String name, String email, String phone, String address) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO Customers (name, email, phone, address, registration_date, is_active) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, TRUE)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, address);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all active Customers
    public static List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Customers WHERE is_active = TRUE ORDER BY name";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getTimestamp("registration_date")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Soft Delete Customer (mark as inactive)
    public static boolean deleteCustomer(int id) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE Customers SET is_active = FALSE WHERE customer_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Check if customer has sales records
    public static boolean hasSalesRecords(int customerId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM sales_log WHERE customer_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update Customer
    public static boolean updateCustomer(int id, String name, String email, String phone, String address) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE Customers SET name=?, email=?, phone=?, address=? WHERE customer_id=? AND is_active = TRUE";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, address);
            stmt.setInt(5, id);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get customer by ID
    public static Customer getCustomerById(int id) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Customers WHERE customer_id = ? AND is_active = TRUE";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getTimestamp("registration_date")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Search customers by name or email
    public static List<Customer> searchCustomers(String searchTerm) {
        List<Customer> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Customers WHERE is_active = TRUE AND (name LIKE ? OR email LIKE ?) ORDER BY name";
            PreparedStatement stmt = conn.prepareStatement(sql);
            String likeTerm = "%" + searchTerm + "%";
            stmt.setString(1, likeTerm);
            stmt.setString(2, likeTerm);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getTimestamp("registration_date")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Get total number of active customers
    public static int getTotalActiveCustomers() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM Customers WHERE is_active = TRUE";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Check if email already exists (for validation)
    public static boolean emailExists(String email, int excludeCustomerId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM Customers WHERE email = ? AND customer_id != ? AND is_active = TRUE";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setInt(2, excludeCustomerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Overloaded method for new customers (excludeCustomerId = -1)
    public static boolean emailExists(String email) {
        return emailExists(email, -1);
    }

    // Restore a soft-deleted customer (optional feature)
    public static boolean restoreCustomer(int id) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE Customers SET is_active = TRUE WHERE customer_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all customers including inactive (for admin purposes)
    public static List<Customer> getAllCustomersWithInactive() {
        List<Customer> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Customers ORDER BY is_active DESC, name";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Customer customer = new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getTimestamp("registration_date")
                );
                // You might want to add an isActive field to your Customer model
                // customer.setActive(rs.getBoolean("is_active"));
                list.add(customer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
