package view;

import model.Product;
import model.ProductDAO;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HomeFrame extends JFrame {
    private JTable productTable;
    private User loggedInUser;  

    public HomeFrame(User user) {
        this.loggedInUser = user;

        setTitle("Smart Stock - Home (" + user.getRole() + ")");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        // Table
        productTable = new JTable(new DefaultTableModel(
                new Object[]{"ID", "Name", "Description", "Price", "Stock"}, 0
        ));
        JScrollPane scrollPane = new JScrollPane(productTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add Product");
        JButton updateBtn = new JButton("Update Stock");
        JButton deleteBtn = new JButton("Delete Product");
        JButton sellBtn = new JButton("Sell Product");

      
        if ("cashier".equalsIgnoreCase(user.getRole())) {
            addBtn.setEnabled(false);
            updateBtn.setEnabled(false);
            deleteBtn.setEnabled(false);
        }

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(sellBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        add(panel);

        // Load products
        refreshTable();

        // Button Actions
        addBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Enter Product Name:");
            String desc = JOptionPane.showInputDialog(this, "Enter Description:");
            double price = Double.parseDouble(JOptionPane.showInputDialog(this, "Enter Price:"));
            int stock = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Stock:"));

            if (ProductDAO.addProduct(name, desc, price, stock)) {
                JOptionPane.showMessageDialog(this, "Product Added!");
                refreshTable();
            }
        });

        updateBtn.addActionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row >= 0) {
                int id = (int) productTable.getValueAt(row, 0);
                int newStock = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter New Stock:"));
                if (ProductDAO.updateStock(id, newStock)) {
                    JOptionPane.showMessageDialog(this, "Stock Updated!");
                    refreshTable();
                }
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row >= 0) {
                int id = (int) productTable.getValueAt(row, 0);
                if (ProductDAO.deleteProduct(id)) {
                    JOptionPane.showMessageDialog(this, "Product Deleted!");
                    refreshTable();
                }
            }
        });

        sellBtn.addActionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row >= 0) {
                int id = (int) productTable.getValueAt(row, 0);
                int qty = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Quantity Sold:"));
                if (ProductDAO.recordSale(id, qty)) {
                    JOptionPane.showMessageDialog(this, "Sale Recorded!");
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Not enough stock!");
                }
            }
        });
    }

    private void refreshTable() {
        List<Product> products = ProductDAO.getAllProducts();
        DefaultTableModel model = (DefaultTableModel) productTable.getModel();
        model.setRowCount(0);
        for (Product p : products) {
            model.addRow(new Object[]{p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getStock()});
        }
    }
}

