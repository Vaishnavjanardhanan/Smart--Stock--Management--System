package model;

import main.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    // Add Customer
    public static boolean addCustomer(String name, String email, String phone, String address) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO Customers (name, email, phone, address) VALUES (?, ?, ?, ?)";
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

    // Get all Customers
    public static List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Customers";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Delete Customer
    public static boolean deleteCustomer(int id) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM Customers WHERE customer_id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update Customer
    public static boolean updateCustomer(int id, String name, String email, String phone, String address) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE Customers SET name=?, email=?, phone=?, address=? WHERE customer_id=?";
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
}
