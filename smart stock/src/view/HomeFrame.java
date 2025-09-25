package view;

import model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.util.List;

public class HomeFrame extends JFrame {
    private JTable productTable;
    private User loggedInUser;
    private GradientPanel contentPanel;
    private Connection databaseConnection;

    // Declare buttons as instance variables
    private JButton addProductBtn, addStockBtn, deleteBtn, sellBtn, printBtn, backBtn;
    private JButton btnLoginHistory, btnProducts, btnCustomers, btnSales, btnReports, btnSettings, btnLogout;

    // Color scheme
    private final Color PRIMARY_COLOR = new Color(102, 0, 153);
    private final Color SECONDARY_COLOR = new Color(147, 112, 219);
    private final Color ACCENT_COLOR = new Color(255, 105, 180);
    private final Color BACKGROUND_COLOR = new Color(245, 245, 255);
    private final Color CARD_COLOR = Color.WHITE;

    public HomeFrame(User user, Connection connection) {
        this.loggedInUser = user;
        this.databaseConnection = connection;
        initializeUI();
        setupSidebar();
        setupMainContent();
        loadData();
        setupActions();

        // Record login when home frame opens
        LoginHistoryDAO.recordLogin(loggedInUser.getUsername());
    }

    private void initializeUI() {
        setTitle("Smart Stock - Home (" + loggedInUser.getRole() + ")");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Add window listener to record logout when window closes
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                LoginHistoryDAO.recordLogout(loggedInUser.getUsername());
            }
        });
    }

    private void setupSidebar() {
        JPanel sidebar = new GradientPanel(PRIMARY_COLOR, new Color(75, 0, 130));
        sidebar.setLayout(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setBorder(new EmptyBorder(20, 10, 20, 10));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BorderLayout());

        JLabel title = new JLabel("Smart Stock", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel userInfo = new JLabel(loggedInUser.getUsername() + " (" + loggedInUser.getRole() + ")", JLabel.CENTER);
        userInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userInfo.setForeground(new Color(200, 200, 200));

        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(userInfo, BorderLayout.SOUTH);

        // Navigation buttons
        JPanel navPanel = new JPanel();
        navPanel.setOpaque(false);
        navPanel.setLayout(new GridLayout(7, 1, 10, 10));
        navPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        // Create sidebar buttons
        btnLoginHistory = createNavButton("Login History", "📋");
        btnProducts = createNavButton("Manage Products", "📦");
        btnCustomers = createNavButton("Manage Customers", "👥");
        btnSales = createNavButton("Sales", "💰");
        btnReports = createNavButton("Reports", "📈");
        btnSettings = createNavButton("Settings", "⚙");
        btnLogout = createNavButton("Logout", "🚪");

        JButton[] navButtons = {btnLoginHistory, btnProducts, btnCustomers, btnSales, btnReports, btnSettings, btnLogout};

        for (JButton button : navButtons) {
            navPanel.add(button);

            // Disable certain features for cashier
            if ("cashier".equalsIgnoreCase(loggedInUser.getRole()) &&
                    (button.getText().contains("Manage Products") ||
                            button.getText().contains("Reports") ||
                            button.getText().contains("Settings"))) {
                button.setEnabled(false);
                button.setBackground(new Color(100, 100, 100));
                button.setToolTipText("Admin access required");
            }
        }

        sidebar.add(headerPanel, BorderLayout.NORTH);
        sidebar.add(navPanel, BorderLayout.CENTER);

        add(sidebar, BorderLayout.WEST);
    }

    private JButton createNavButton(String text, String icon) {
        JButton button = new JButton(icon + "  " + text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(SECONDARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(ACCENT_COLOR);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(SECONDARY_COLOR);
                }
            }
        });

        return button;
    }

    private void setupMainContent() {
        contentPanel = new GradientPanel(BACKGROUND_COLOR, new Color(230, 230, 255));
        contentPanel.setLayout(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = createHeaderPanel();
        contentPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel tablePanel = createTablePanel();
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        JPanel actionPanel = createActionPanel();
        contentPanel.add(actionPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Welcome back, " + loggedInUser.getUsername() + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(PRIMARY_COLOR);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        statsPanel.setOpaque(false);

        // Updated stats with real data
        try {
            int totalProducts = ProductDAO.getAllProducts().size();
            int lowStockCount = getLowStockCount();
            double todaySales = getTodaySales();

            String[][] statsData = {
                    {"Total Products", "📦", String.valueOf(totalProducts)},
                    {"Low Stock", "⚠", String.valueOf(lowStockCount)},
                    {"Today's Sales", "💰", String.format("%.2f", todaySales)}
            };

            for (String[] stat : statsData) {
                statsPanel.add(createStatCard(stat[0], stat[1], stat[2]));
            }
        } catch (Exception e) {
            // Fallback stats if there's an error
            String[][] statsData = {
                    {"Total Products", "📦", "0"},
                    {"Low Stock", "⚠", "0"},
                    {"Today's Sales", "💰", "0.00"}
            };

            for (String[] stat : statsData) {
                statsPanel.add(createStatCard(stat[0], stat[1], stat[2]));
            }
        }

        headerPanel.add(welcomeLabel, BorderLayout.NORTH);
        headerPanel.add(statsPanel, BorderLayout.SOUTH);

        return headerPanel;
    }

    private int getLowStockCount() throws Exception {
        List<Product> products = ProductDAO.getAllProducts();
        int count = 0;
        for (Product product : products) {
            if (product.getStock() > 0 && product.getStock() <= 10) {
                count++;
            }
        }
        return count;
    }

    private double getTodaySales() throws Exception {
        List<Sale> sales = SaleDAO.getAllSales();
        double total = 0;
        java.util.Date today = new java.util.Date();
        for (Sale sale : sales) {
            if (isToday(sale.getSaleDate())) {
                total += sale.getTotalAmount();
            }
        }
        return total;
    }

    private boolean isToday(java.sql.Timestamp timestamp) {
        java.util.Date date = new java.util.Date(timestamp.getTime());
        java.util.Date today = new java.util.Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        return sdf.format(date).equals(sdf.format(today));
    }

    private JPanel createStatCard(String title, String icon, String value) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(240, 240, 240), 1),
                new EmptyBorder(15, 20, 15, 20)
        ));
        card.setPreferredSize(new Dimension(180, 80));

        JLabel titleLabel = new JLabel(icon + " " + title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(Color.GRAY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(PRIMARY_COLOR);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                "Product Inventory",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 16),
                PRIMARY_COLOR
        ));

        productTable = new JTable(new DefaultTableModel(
                new Object[]{"ID", "Name", "Description", "Price", "Stock", "Status"}, 0
        )) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (isRowSelected(row)) {
                    c.setBackground(new Color(147, 112, 219, 100));
                } else {
                    c.setBackground(row % 2 == 0 ? new Color(250, 250, 255) : Color.WHITE);
                }
                return c;
            }
        };

        JTableHeader header = productTable.getTableHeader();
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));

        productTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        productTable.setRowHeight(30);
        productTable.setSelectionBackground(new Color(147, 112, 219, 100));
        productTable.setSelectionForeground(PRIMARY_COLOR);
        productTable.setGridColor(new Color(240, 240, 240));

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        addProductBtn = createActionButton("➕ Add Product", PRIMARY_COLOR);
        addStockBtn = createActionButton("📥 Add Stock", SECONDARY_COLOR);
        deleteBtn = createActionButton("🗑️ Delete", new Color(154, 8, 19));
        sellBtn = createActionButton("💰 Sell", new Color(40, 167, 69));
        printBtn = createActionButton("🖨️ Print", new Color(108, 117, 125));
        backBtn = createActionButton("⬅️ Back", new Color(253, 126, 20));

        JButton[] actionButtons = {addProductBtn, addStockBtn, deleteBtn, sellBtn, printBtn, backBtn};

        for (JButton button : actionButtons) {
            actionPanel.add(button);

            if ("cashier".equalsIgnoreCase(loggedInUser.getRole()) &&
                    (button == addProductBtn || button == addStockBtn || button == deleteBtn)) {
                button.setEnabled(false);
                button.setBackground(Color.GRAY);
                button.setToolTipText("Admin access required");
            }
        }

        return actionPanel;
    }

    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new RoundedBorder(20, color));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(color.darker());
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(color);
                }
            }
        });

        return button;
    }

    private void loadData() {
        refreshTable();
    }

    private void setupActions() {
        // Add Product Button
        addProductBtn.addActionListener(e -> openAddProductFrame());

        // Add Stock Button
        addStockBtn.addActionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a product first!");
                return;
            }

            try {
                int id = (int) productTable.getValueAt(row, 0);
                String qtyStr = JOptionPane.showInputDialog(this, "Enter quantity to add:");
                if (qtyStr == null) return;
                int qty = Integer.parseInt(qtyStr);

                if (qty <= 0) {
                    JOptionPane.showMessageDialog(this, "Quantity must be positive!");
                    return;
                }

                if (ProductDAO.addStock(id, qty)) {
                    JOptionPane.showMessageDialog(this, "Stock increased by " + qty);
                    refreshTable();
                    refreshStats();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add stock!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid quantity!");
            }
        });

        // Delete Product Button
        deleteBtn.addActionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a product first!");
                return;
            }

            int id = (int) productTable.getValueAt(row, 0);
            String productName = (String) productTable.getValueAt(row, 1);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete '" + productName + "'?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (ProductDAO.deleteProduct(id)) {
                    JOptionPane.showMessageDialog(this, "Product Deleted Successfully!");
                    refreshTable();
                    refreshStats();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete product!");
                }
            }
        });

        // Sell Product Button
        sellBtn.addActionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a product first!");
                return;
            }

            try {
                int id = (int) productTable.getValueAt(row, 0);
                String productName = (String) productTable.getValueAt(row, 1);
                int currentStock = (int) productTable.getValueAt(row, 4);

                String qtyStr = JOptionPane.showInputDialog(this,
                        "Sell '" + productName + "'\nCurrent stock: " + currentStock + "\nEnter quantity to sell:");
                if (qtyStr == null) return;

                int qty = Integer.parseInt(qtyStr);

                if (qty <= 0) {
                    JOptionPane.showMessageDialog(this, "Quantity must be positive!");
                    return;
                }

                if (qty > currentStock) {
                    JOptionPane.showMessageDialog(this, "Not enough stock! Available: " + currentStock);
                    return;
                }

                if (ProductDAO.recordSale(id, qty)) {
                    JOptionPane.showMessageDialog(this, "Sale Recorded Successfully!");
                    refreshTable();
                    refreshStats();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to record sale!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid quantity!");
            }
        });

        // Print Button
        printBtn.addActionListener(e -> {
            try {
                boolean complete = productTable.print();
                if (complete) {
                    JOptionPane.showMessageDialog(this, "Printing completed successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Printing was cancelled.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error while printing: " + ex.getMessage());
            }
        });

        // Back Button
        backBtn.addActionListener(e -> logout());

        // Navigation Buttons
        btnLoginHistory.addActionListener(e -> openLoginHistoryFrame());
        btnProducts.addActionListener(e -> openProductManagement());
        btnCustomers.addActionListener(e -> openCustomerFrame());
        btnSales.addActionListener(e -> openSalesFrame());
        btnReports.addActionListener(e -> openReportsFrame());

        // ✅ UPDATED: Settings Button now opens SettingsFrame
        btnSettings.addActionListener(e -> openSettingsFrame());

        btnLogout.addActionListener(e -> logout());
    }

    // ✅ NEW METHOD: Open Settings Frame
    private void openSettingsFrame() {
        if (!"ADMIN".equalsIgnoreCase(loggedInUser.getRole())) {
            JOptionPane.showMessageDialog(this,
                    "Access Denied!\nOnly administrators can access settings.",
                    "Admin Access Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            SettingsFrame settingsFrame = new SettingsFrame(databaseConnection, loggedInUser.getUsername());
            settingsFrame.setLocationRelativeTo(this);
            settingsFrame.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error opening settings: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            LoginHistoryDAO.recordLogout(loggedInUser.getUsername());
            this.dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private void openLoginHistoryFrame() {
        LoginHistoryFrame historyFrame = new LoginHistoryFrame();
        historyFrame.setLocationRelativeTo(this);
        historyFrame.setVisible(true);
    }

    private void openSalesFrame() {
        SalesFrame salesFrame = new SalesFrame();
        salesFrame.setLocationRelativeTo(this);
        salesFrame.setVisible(true);
    }

    private void openReportsFrame() {
        ReportsFrame reportsFrame = new ReportsFrame();
        reportsFrame.setLocationRelativeTo(this);
        reportsFrame.setVisible(true);
    }

    private void openProductManagement() {
        String[] options = {"➕ Add New Product", "📋 View All Products", "❌ Cancel"};

        int choice = JOptionPane.showOptionDialog(this,
                "Product Management Options",
                "Manage Products",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        switch (choice) {
            case 0:
                openAddProductFrame();
                break;
            case 1:
                refreshTable();
                JOptionPane.showMessageDialog(this, "Product list refreshed!");
                break;
        }
    }

    private void openCustomerFrame() {
        CustomerFrame customerFrame = new CustomerFrame();
        customerFrame.setLocationRelativeTo(this);
        customerFrame.setVisible(true);
    }

    private void openAddProductFrame() {
        AddProductFrame addProductFrame = new AddProductFrame();
        addProductFrame.setLocationRelativeTo(this);
        addProductFrame.setVisible(true);
        refreshTable();
        refreshStats();
    }

    private void refreshTable() {
        try {
            List<Product> products = ProductDAO.getAllProducts();
            DefaultTableModel model = (DefaultTableModel) productTable.getModel();
            model.setRowCount(0);
            for (Product p : products) {
                String status = p.getStock() > 10 ? "🟢 In Stock" : p.getStock() > 0 ? "🟡 Low Stock" : "🔴 Out of Stock";
                model.addRow(new Object[]{
                        p.getId(),
                        p.getName(),
                        p.getDescription(),
                        String.format("%.2f", p.getPrice()),
                        p.getStock(),
                        status
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage());
        }
    }

    private void refreshStats() {
        contentPanel.removeAll();
        setupMainContent();
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Custom gradient panel
    class GradientPanel extends JPanel {
        private Color startColor;
        private Color endColor;

        public GradientPanel(Color startColor, Color endColor) {
            this.startColor = startColor;
            this.endColor = endColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gp = new GradientPaint(0, 0, startColor, getWidth(), getHeight(), endColor);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    // Rounded border for buttons
    class RoundedBorder implements javax.swing.border.Border {
        private int radius;
        private Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.draw(new RoundRectangle2D.Float(x, y, width-1, height-1, radius, radius));
        }
    }
}
