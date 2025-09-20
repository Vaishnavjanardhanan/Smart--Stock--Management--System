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
    private Image backgroundImage;

    public HomeFrame(User user) {
        this.loggedInUser = user;

        setTitle("Smart Stock - Home (" + user.getRole() + ")");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ===== Sidebar =====
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(128, 0, 128)); // purple
        sidebar.setLayout(new GridLayout(10, 1, 5, 5));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));

        JLabel title = new JLabel("Smart Stock", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        sidebar.add(title);

        JButton btnDashboard = new JButton("Dashboard");
        JButton btnProducts = new JButton("Manage Products");
        JButton btnCustomers = new JButton("Manage Customers");
        JButton btnSales = new JButton("Sales");
        JButton btnReports = new JButton("Reports");
        JButton btnLogout = new JButton("Logout");

        JButton[] buttons = {btnDashboard, btnProducts, btnCustomers, btnSales, btnReports, btnLogout};
        for (JButton b : buttons) {
            b.setBackground(Color.WHITE);
            b.setForeground(new Color(128, 0, 128));
            b.setFocusPainted(false);
            sidebar.add(b);
        }

        add(sidebar, BorderLayout.WEST);

        // ===== Main Content Panel =====
        JPanel contentPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        // Product Table
        productTable = new JTable(new DefaultTableModel(
                new Object[]{"ID", "Name", "Description", "Price", "Stock"}, 0
        ));
        JScrollPane scrollPane = new JScrollPane(productTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        JButton addBtn = new JButton("Add Product");
        JButton updateBtn = new JButton("Add Stock");
        JButton deleteBtn = new JButton("Delete Product");
        JButton sellBtn = new JButton("Sell Product");
        JButton printBtn = new JButton("🖨 Print Products");
        JButton backBtn = new JButton("⬅ Back");

        if ("cashier".equalsIgnoreCase(user.getRole())) {
            addBtn.setEnabled(false);
            updateBtn.setEnabled(false);
            deleteBtn.setEnabled(false);
            btnProducts.setEnabled(false);
        }

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(sellBtn);
        buttonPanel.add(printBtn);
        buttonPanel.add(backBtn);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(contentPanel, BorderLayout.CENTER);

        // ===== Load Data =====
        refreshTable();

        // ===== Actions =====
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

        //  Increment stock instead of overwrite
        updateBtn.addActionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row >= 0) {
                int id = (int) productTable.getValueAt(row, 0);
                int qty = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter quantity to add:"));
                if (ProductDAO.addStock(id, qty)) {
                    JOptionPane.showMessageDialog(this, "Stock increased by " + qty);
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

        //  Print products directly
        printBtn.addActionListener(e -> {
            try {
                boolean complete = productTable.print();
                if (complete) {
                    JOptionPane.showMessageDialog(this, "Printing complete!");
                } else {
                    JOptionPane.showMessageDialog(this, "Printing canceled.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error while printing: " + ex.getMessage());
            }
        });

        btnProducts.addActionListener(e -> new ProductFrame().setVisible(true));
        btnCustomers.addActionListener(e -> new CustomerFrame().setVisible(true));
        backBtn.addActionListener(e -> {
            this.dispose();
            new LoginFrame().setVisible(true);
        });
        btnLogout.addActionListener(e -> {
            this.dispose();
            new LoginFrame().setVisible(true);
        });
    }

    private void refreshTable() {
        List<Product> products = ProductDAO.getAllProducts();
        DefaultTableModel model = (DefaultTableModel) productTable.getModel();
        model.setRowCount(0);
        for (Product p : products) {
            model.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getDescription(),
                    p.getPrice(),
                    p.getStock()
            });
        }
    }
}

