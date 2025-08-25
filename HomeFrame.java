import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import java.util.List;

public class HomeFrame extends JFrame {
    private User currentUser;
    private JTabbedPane tabbedPane;
    
    public HomeFrame(User user) {
        this.currentUser = user;
        
        setTitle("Smart Stock Inventory - Home");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        
        // View menu
        JMenu viewMenu = new JMenu("View");
        menuBar.add(viewMenu);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "Smart Stock Inventory App\nVersion 1.0\n\n© 2024 All rights reserved", 
                "About", JOptionPane.INFORMATION_MESSAGE);
        });
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Add tabs based on user role
        if (currentUser.isAdmin()) {
            tabbedPane.addTab("Dashboard", new DashboardPanel());
            tabbedPane.addTab("Products", new ProductsPanel());
            tabbedPane.addTab("Stock", new StockPanel());
            tabbedPane.addTab("Reports", new ReportsPanel());
            tabbedPane.addTab("Administration", new AdminPanel());
        } else {
            tabbedPane.addTab("Dashboard", new DashboardPanel());
            tabbedPane.addTab("Products", new ProductsPanel());
            tabbedPane.addTab("Stock", new StockPanel());
        }
        
        add(tabbedPane);
        
        // Status bar
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        JLabel statusLabel = new JLabel("Logged in as: " + currentUser.getUsername() + 
                                      " | Role: " + (currentUser.isAdmin() ? "Administrator" : "User"));
        statusPanel.add(statusLabel, BorderLayout.WEST);
        
        JLabel dateLabel = new JLabel(new Date().toString());
        statusPanel.add(dateLabel, BorderLayout.EAST);
        
        add(statusPanel, BorderLayout.SOUTH);
    }
}

// Dashboard Panel
class DashboardPanel extends JPanel {
    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Inventory Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        add(titleLabel, BorderLayout.NORTH);
        
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Stat cards
        statsPanel.add(createStatCard("Total Products", String.valueOf(Database.getProducts().size()), Color.BLUE));
        statsPanel.add(createStatCard("Low Stock Items", "3", Color.RED));
        statsPanel.add(createStatCard("Total Value", "$12,548.75", Color.GREEN));
        statsPanel.add(createStatCard("Categories", "4", Color.ORANGE));
        
        add(statsPanel, BorderLayout.CENTER);
        
        // Recent activities
        JTextArea activityArea = new JTextArea();
        activityArea.setEditable(false);
        activityArea.setText("Recent Activities:\n- Product P002 updated\n- New stock added for P001\n- User login detected");
        activityArea.setBorder(BorderFactory.createTitledBorder("Recent Activities"));
        
        add(activityArea, BorderLayout.SOUTH);
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        card.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(Color.DARK_GRAY);
        
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(color);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
}

// Products Panel
class ProductsPanel extends JPanel {
    private JTable productsTable;
    private ProductTableModel tableModel;
    
    public ProductsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("Product Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 204));
        add(titleLabel, BorderLayout.NORTH);
        
        // Table
        tableModel = new ProductTableModel();
        productsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(productsTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton addButton = new JButton("Add Product");
        addButton.setBackground(new Color(0, 153, 0));
        addButton.setForeground(Color.WHITE);
        
        JButton editButton = new JButton("Edit Product");
        editButton.setBackground(new Color(0, 102, 204));
        editButton.setForeground(Color.WHITE);
        
        JButton deleteButton = new JButton("Delete Product");
        deleteButton.setBackground(new Color(204, 0, 0));
        deleteButton.setForeground(Color.WHITE);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(102, 102, 102));
        refreshButton.setForeground(Color.WHITE);
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Button actions
        addButton.addActionListener(e -> showAddProductDialog());
        editButton.addActionListener(e -> showEditProductDialog());
        deleteButton.addActionListener(e -> deleteProduct());
        refreshButton.addActionListener(e -> refreshTable());
    }
    
    private void showAddProductDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Add New Product");
        dialog.setSize(400, 350);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel idLabel = new JLabel("Product ID:");
        JTextField idField = new JTextField();
        
        JLabel nameLabel = new JLabel("Product Name:");
        JTextField nameField = new JTextField();
        
        JLabel categoryLabel = new JLabel("Category:");
        JTextField categoryField = new JTextField();
        
        JLabel priceLabel = new JLabel("Price:");
        JTextField priceField = new JTextField();
        
        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField();
        
        JLabel minStockLabel = new JLabel("Min Stock Level:");
        JTextField minStockField = new JTextField();
        
        panel.add(idLabel);
        panel.add(idField);
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(categoryLabel);
        panel.add(categoryField);
        panel.add(priceLabel);
        panel.add(priceField);
        panel.add(quantityLabel);
        panel.add(quantityField);
        panel.add(minStockLabel);
        panel.add(minStockField);
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                String id = idField.getText();
                String name = nameField.getText();
                String category = categoryField.getText();
                double price = Double.parseDouble(priceField.getText());
                int quantity = Integer.parseInt(quantityField.getText());
                int minStock = Integer.parseInt(minStockField.getText());
                
                Product product = new Product(id, name, category, price, quantity, minStock);
                Database.addProduct(product);
                refreshTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Product added successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input! Please check your values.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void showEditProductDialog() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String productId = (String) tableModel.getValueAt(selectedRow, 0);
        Product product = Database.getProductById(productId);
        
        if (product == null) {
            JOptionPane.showMessageDialog(this, "Product not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog();
        dialog.setTitle("Edit Product");
        dialog.setSize(400, 350);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel idLabel = new JLabel("Product ID:");
        JTextField idField = new JTextField(product.getId());
        idField.setEditable(false);
        
        JLabel nameLabel = new JLabel("Product Name:");
        JTextField nameField = new JTextField(product.getName());
        
        JLabel categoryLabel = new JLabel("Category:");
        JTextField categoryField = new JTextField(product.getCategory());
        
        JLabel priceLabel = new JLabel("Price:");
        JTextField priceField = new JTextField(String.valueOf(product.getPrice()));
        
        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField(String.valueOf(product.getQuantity()));
        
        JLabel minStockLabel = new JLabel("Min Stock Level:");
        JTextField minStockField = new JTextField(String.valueOf(product.getMinStockLevel()));
        
        panel.add(idLabel);
        panel.add(idField);
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(categoryLabel);
        panel.add(categoryField);
        panel.add(priceLabel);
        panel.add(priceField);
        panel.add(quantityLabel);
        panel.add(quantityField);
        panel.add(minStockLabel);
        panel.add(minStockField);
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String category = categoryField.getText();
                double price = Double.parseDouble(priceField.getText());
                int quantity = Integer.parseInt(quantityField.getText());
                int minStock = Integer.parseInt(minStockField.getText());
                
                product.setName(name);
                product.setCategory(category);
                product.setPrice(price);
                product.setQuantity(quantity);
                product.setMinStockLevel(minStock);
                
                Database.updateProduct(product);
                refreshTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Product updated successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input! Please check your values.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void deleteProduct() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String productId = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete product " + productId + "?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            Database.deleteProduct(productId);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Product deleted successfully!");
        }
    }
    
    private void refreshTable() {
        tableModel.fireTableDataChanged();
    }
}

// Stock Panel
class StockPanel extends JPanel {
    private JTable productsTable;
    private ProductTableModel tableModel;
    private JComboBox<String> productComboBox;
    
    public StockPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("Stock Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 204));
        add(titleLabel, BorderLayout.NORTH);
        
        // Table
        tableModel = new ProductTableModel();
        productsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(productsTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Stock control panel
        JPanel stockControlPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        stockControlPanel.setBorder(BorderFactory.createTitledBorder("Stock Control"));
        
        // Product selection
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel productLabel = new JLabel("Select Product:");
        productComboBox = new JComboBox<>();
        refreshProductComboBox();
        
        JTextField searchField = new JTextField(15);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterProducts(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterProducts(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterProducts(); }
            
            private void filterProducts() {
                String query = searchField.getText();
                java.util.List<String> suggestions = Database.getProductSuggestions(query);
                productComboBox.removeAllItems();
                for (String suggestion : suggestions) {
                    productComboBox.addItem(suggestion);
                }
            }
        });
        
        selectionPanel.add(productLabel);
        selectionPanel.add(searchField);
        selectionPanel.add(productComboBox);
        
        // Quantity control
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel quantityLabel = new JLabel("Quantity to Add:");
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        
        JButton addStockButton = new JButton("Add Stock");
        addStockButton.setBackground(new Color(0, 153, 0));
        addStockButton.setForeground(Color.WHITE);
        
        JButton removeStockButton = new JButton("Remove Stock");
        removeStockButton.setBackground(new Color(204, 0, 0));
        removeStockButton.setForeground(Color.WHITE);
        
        quantityPanel.add(quantityLabel);
        quantityPanel.add(quantitySpinner);
        quantityPanel.add(addStockButton);
        quantityPanel.add(removeStockButton);
        
        stockControlPanel.add(selectionPanel);
        stockControlPanel.add(quantityPanel);
        
        add(stockControlPanel, BorderLayout.SOUTH);
        
        // Button actions
        addStockButton.addActionListener(e -> {
            adjustStock((Integer) quantitySpinner.getValue(), true);
        });
        
        removeStockButton.addActionListener(e -> {
            adjustStock((Integer) quantitySpinner.getValue(), false);
        });
    }
    
    private void refreshProductComboBox() {
        productComboBox.removeAllItems();
        for (Product product : Database.getProducts()) {
            productComboBox.addItem(product.getId() + " - " + product.getName());
        }
    }
    
    private void adjustStock(int quantity, boolean isAdd) {
        String selected = (String) productComboBox.getSelectedItem();
        if (selected == null || selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a product.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String productId = selected.split(" - ")[0];
        Product product = Database.getProductById(productId);
        
        if (product == null) {
            JOptionPane.showMessageDialog(this, "Product not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (isAdd) {
            product.setQuantity(product.getQuantity() + quantity);
            JOptionPane.showMessageDialog(this, "Added " + quantity + " units to " + product.getName());
        } else {
            if (product.getQuantity() < quantity) {
                JOptionPane.showMessageDialog(this, "Not enough stock to remove!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            product.setQuantity(product.getQuantity() - quantity);
            JOptionPane.showMessageDialog(this, "Removed " + quantity + " units from " + product.getName());
        }
        
        Database.updateProduct(product);
        tableModel.fireTableDataChanged();
    }
}

// Reports Panel
class ReportsPanel extends JPanel {
    public ReportsPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Reports", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 204));
        add(titleLabel, BorderLayout.NORTH);
        
        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setText(generateReport());
        reportArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(reportArea);
        add(scrollPane, BorderLayout.CENTER);
        
        JButton refreshButton = new JButton("Refresh Report");
        refreshButton.addActionListener(e -> reportArea.setText(generateReport()));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("INVENTORY REPORT\n");
        report.append("Generated on: ").append(new Date()).append("\n\n");
        
        report.append("PRODUCT SUMMARY:\n");
        report.append("================\n");
        
        List<Product> products = Database.getProducts();
        double totalValue = 0;
        
        for (Product product : products) {
            double productValue = product.getPrice() * product.getQuantity();
            totalValue += productValue;
            
            report.append(String.format("%s (%s): %d units, $%.2f each, Total: $%.2f\n",
                product.getName(), product.getId(), product.getQuantity(), 
                product.getPrice(), productValue));
        }
        
        report.append("\nTOTAL INVENTORY VALUE: $").append(String.format("%.2f", totalValue)).append("\n\n");
        
        report.append("LOW STOCK ALERTS:\n");
        report.append("=================\n");
        
        boolean hasLowStock = false;
        for (Product product : products) {
            if (product.getQuantity() <= product.getMinStockLevel()) {
                hasLowStock = true;
                report.append(String.format("WARNING: %s (%s) is low on stock. Current: %d, Minimum: %d\n",
                    product.getName(), product.getId(), product.getQuantity(), product.getMinStockLevel()));
            }
        }
        
        if (!hasLowStock) {
            report.append("No low stock items.\n");
        }
        
        return report.toString();
    }
}

// Admin Panel
class AdminPanel extends JPanel {
    public AdminPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Administration", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 204));
        add(titleLabel, BorderLayout.NORTH);
        
        JTextArea adminArea = new JTextArea();
        adminArea.setEditable(false);
        adminArea.setText("Administrative Functions:\n\n- User Management\n- System Configuration\n- Database Backup/Restore\n- Audit Logs\n- Permission Settings");
        adminArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(adminArea, BorderLayout.CENTER);
    }
}

// Table Model for Products
class ProductTableModel extends javax.swing.table.AbstractTableModel {
    private String[] columnNames = {"ID", "Name", "Category", "Price", "Quantity", "Min Stock"};
    
    @Override
    public int getRowCount() {
        return Database.getProducts().size();
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Product product = Database.getProducts().get(rowIndex);
        switch (columnIndex) {
            case 0: return product.getId();
            case 1: return product.getName();
            case 2: return product.getCategory();
            case 3: return product.getPrice();
            case 4: return product.getQuantity();
            case 5: return product.getMinStockLevel();
            default: return null;
        }
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 3) return Double.class;
        if (columnIndex == 4 || columnIndex == 5) return Integer.class;
        return String.class;
    }
}