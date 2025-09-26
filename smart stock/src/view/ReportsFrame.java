package view;

import model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.print.PrinterException;
import java.text.SimpleDateFormat;
import java.text.MessageFormat;
import java.util.List;

public class ReportsFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private JTable todaySalesTable, salesHistoryTable, lowStockTable, customersTable;
    private GradientPanel contentPanel;
    private JPanel headerPanel;
    private JPanel statsPanel;

    // Enhanced color scheme
    private final Color PRIMARY_COLOR = new Color(102, 0, 153);
    private final Color SECONDARY_COLOR = new Color(147, 112, 219);
    private final Color ACCENT_COLOR = new Color(255, 105, 180);
    private final Color BACKGROUND_COLOR = new Color(245, 245, 255);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color WARNING_COLOR = new Color(220, 53, 69);
    private final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private final Color INFO_COLOR = new Color(23, 162, 184);
    private final Color SALES_COLOR = new Color(255, 140, 0);
    private final Color STOCK_COLOR = new Color(32, 201, 151);

    public ReportsFrame() {
        initializeUI();
        setupMainContent();
        loadData();
    }

    private void initializeUI() {
        setTitle("Smart Stock - Reports Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void setupMainContent() {
        contentPanel = new GradientPanel(BACKGROUND_COLOR, new Color(230, 230, 255));
        contentPanel.setLayout(new BorderLayout(15, 15));
        contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        headerPanel = createHeaderPanel();
        contentPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel tabsPanel = createTabsPanel();
        contentPanel.add(tabsPanel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        // Enhanced title with icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);

        JLabel iconLabel = new JLabel("📊");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setBorder(new EmptyBorder(0, 0, 0, 10));

        JLabel titleLabel = new JLabel("Reports Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(PRIMARY_COLOR);

        titlePanel.add(iconLabel);
        titlePanel.add(titleLabel);
        titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Stats panel
        statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        statsPanel.setOpaque(false);
        updateStats();

        headerPanel.add(titlePanel, BorderLayout.NORTH);
        headerPanel.add(statsPanel, BorderLayout.CENTER);

        return headerPanel;
    }

    private void updateStats() {
        if (statsPanel == null) return;

        statsPanel.removeAll();

        try {
            List<Sale> sales = SaleDAO.getAllSales();
            List<Product> products = ProductDAO.getAllProducts();
            List<Customer> customers = CustomerDAO.getAllCustomers();

            int todaySalesCount = 0;
            double todayRevenue = 0;
            int lowStockCount = 0;
            int outOfStockCount = 0;

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String today = dateFormat.format(new java.util.Date());

            for (Sale sale : sales) {
                String saleDate = dateFormat.format(sale.getSaleDate());
                if (saleDate.equals(today)) {
                    todaySalesCount++;
                    todayRevenue += sale.getTotalAmount();
                }
            }

            for (Product product : products) {
                if (product.getStock() == 0) {
                    outOfStockCount++;
                } else if (product.getStock() <= 10) {
                    lowStockCount++;
                }
            }

            JPanel[] statsCards = {
                    createStatCard("Today's Sales", "💰", String.valueOf(todaySalesCount), SALES_COLOR),
                    createStatCard("Today's Revenue", "💵", String.format("%.2f", todayRevenue), SUCCESS_COLOR),
                    createStatCard("Low Stock", "⚠️", String.valueOf(lowStockCount), WARNING_COLOR),
                    createStatCard("Out of Stock", "🔴", String.valueOf(outOfStockCount), WARNING_COLOR),
                    createStatCard("Total Customers", "👥", String.valueOf(customers.size()), INFO_COLOR)
            };

            for (JPanel card : statsCards) {
                statsPanel.add(card);
            }
        } catch (Exception e) {
            JPanel[] statsCards = {
                    createStatCard("Today's Sales", "💰", "0", SALES_COLOR),
                    createStatCard("Today's Revenue", "💵", "0.00", SUCCESS_COLOR),
                    createStatCard("Database Status", "🔴", "Error", WARNING_COLOR)
            };

            for (JPanel card : statsCards) {
                statsPanel.add(card);
            }
        }

        statsPanel.revalidate();
        statsPanel.repaint();
    }

    private JPanel createStatCard(String title, String icon, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(15, new Color(220, 220, 220), 2),
                new EmptyBorder(15, 20, 15, 20)
        ));
        card.setPreferredSize(new Dimension(180, 80));

        // Add hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(245, 245, 245));
                card.setBorder(BorderFactory.createCompoundBorder(
                        new RoundBorder(15, color, 2),
                        new EmptyBorder(15, 20, 15, 20)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(CARD_COLOR);
                card.setBorder(BorderFactory.createCompoundBorder(
                        new RoundBorder(15, new Color(220, 220, 220), 2),
                        new EmptyBorder(15, 20, 15, 20)
                ));
            }
        });

        JLabel titleLabel = new JLabel(icon + " " + title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        titleLabel.setForeground(Color.GRAY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valueLabel.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createTabsPanel() {
        JPanel tabsPanel = new JPanel(new BorderLayout());
        tabsPanel.setOpaque(false);
        tabsPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(10, PRIMARY_COLOR, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Enhanced tabbed pane
        tabbedPane = new JTabbedPane(JTabbedPane.TOP) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BACKGROUND_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };

        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(BACKGROUND_COLOR);
        tabbedPane.setForeground(PRIMARY_COLOR);

        // Create enhanced tabs
        tabbedPane.addTab("💰 Today's Sales", createEnhancedTablePanel("Today's Sales",
                new String[]{"Sale ID", "Product Name", "Customer", "Quantity", "Unit Price", "Total Amount", "Date"}));

        tabbedPane.addTab("📈 Sales History", createEnhancedTablePanel("Sales History",
                new String[]{"Sale ID", "Product Name", "Customer", "Quantity", "Unit Price", "Total Amount", "Date"}));

        tabbedPane.addTab("⚠️ Low Stock", createEnhancedTablePanel("Low Stock Alerts",
                new String[]{"Product ID", "Product Name", "Current Stock", "Status"}));

        tabbedPane.addTab("👥 Customers", createEnhancedTablePanel("Customer Details",
                new String[]{"Customer ID", "Name", "Email", "Phone", "Address", "Registration Date"}));

        // Style the tabs
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            JLabel tabLabel = new JLabel(tabbedPane.getTitleAt(i), JLabel.CENTER);
            tabLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            tabLabel.setBorder(new EmptyBorder(8, 15, 8, 15));
            tabbedPane.setTabComponentAt(i, tabLabel);
        }

        tabsPanel.add(tabbedPane, BorderLayout.CENTER);
        return tabsPanel;
    }

    private JPanel createEnhancedTablePanel(String title, String[] columns) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Table header with title
        JPanel tableHeaderPanel = new JPanel(new BorderLayout());
        tableHeaderPanel.setOpaque(false);
        tableHeaderPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel tableTitle = new JLabel(title);
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(PRIMARY_COLOR);

        tableHeaderPanel.add(tableTitle, BorderLayout.WEST);
        tableHeaderPanel.add(createTableActions(), BorderLayout.EAST);

        // Create enhanced table
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (isRowSelected(row)) {
                    c.setBackground(new Color(147, 112, 219, 60));
                    c.setForeground(PRIMARY_COLOR.darker());
                } else {
                    c.setBackground(row % 2 == 0 ? new Color(250, 250, 255) : new Color(245, 245, 250));
                    c.setForeground(new Color(60, 60, 60));
                }

                // Color coding for status columns
                if (column == 3 && title.equals("Low Stock Alerts")) {
                    Object value = getValueAt(row, column);
                    if (value != null) {
                        String status = value.toString();
                        if (status.contains("Out of Stock")) {
                            c.setForeground(WARNING_COLOR);
                        } else if (status.contains("Critical")) {
                            c.setForeground(new Color(255, 140, 0));
                        } else if (status.contains("Low Stock")) {
                            c.setForeground(new Color(255, 193, 7));
                        }
                    }
                }

                return c;
            }
        };

        // Enhanced table styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(header.getWidth(), 35));

        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.setSelectionBackground(new Color(147, 112, 219, 80));
        table.setSelectionForeground(PRIMARY_COLOR.darker());
        table.setGridColor(new Color(220, 220, 220));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(5, new Color(200, 200, 200), 1),
                new EmptyBorder(5, 5, 5, 5)
        ));
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(tableHeaderPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Store table reference based on title
        switch (title) {
            case "Today's Sales": todaySalesTable = table; break;
            case "Sales History": salesHistoryTable = table; break;
            case "Low Stock Alerts": lowStockTable = table; break;
            case "Customer Details": customersTable = table; break;
        }

        return panel;
    }

    private JPanel createTableActions() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        JButton refreshBtn = createActionButton("🔄 Refresh", INFO_COLOR);
        JButton exportBtn = createActionButton("💾 Export", SUCCESS_COLOR);
        JButton printBtn = createActionButton("🖨️ Print", PRIMARY_COLOR);

        refreshBtn.addActionListener(e -> loadData());
        exportBtn.addActionListener(e -> exportTable(getCurrentTable(), getCurrentTabTitle()));
        printBtn.addActionListener(e -> printTable(getCurrentTable()));

        actionPanel.add(refreshBtn);
        actionPanel.add(exportBtn);
        actionPanel.add(printBtn);

        return actionPanel;
    }

    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new RoundedBorder(20, color));

        // Hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
                button.setBorder(new RoundedBorder(20, color.brighter()));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
                button.setBorder(new RoundedBorder(20, color));
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }
        });

        return button;
    }

    private JTable getCurrentTable() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        switch (selectedIndex) {
            case 0: return todaySalesTable;
            case 1: return salesHistoryTable;
            case 2: return lowStockTable;
            case 3: return customersTable;
            default: return todaySalesTable;
        }
    }

    private String getCurrentTabTitle() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        switch (selectedIndex) {
            case 0: return "Today's Sales";
            case 1: return "Sales History";
            case 2: return "Low Stock Alerts";
            case 3: return "Customer Details";
            default: return "Today's Sales";
        }
    }

    private void loadData() {
        loadTodaySales();
        loadSalesHistory();
        loadLowStockAlerts();
        loadCustomerDetails();
        updateStats();
    }

    private void loadTodaySales() {
        try {
            DefaultTableModel model = (DefaultTableModel) todaySalesTable.getModel();
            model.setRowCount(0);

            List<Sale> allSales = SaleDAO.getAllSales();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String today = dateFormat.format(new java.util.Date());

            double totalSales = 0;
            int saleCount = 0;

            for (Sale sale : allSales) {
                String saleDate = dateFormat.format(sale.getSaleDate());
                if (saleDate.equals(today)) {
                    String customerName = sale.getCustomerName() != null ? sale.getCustomerName() : "Walk-in Customer";

                    model.addRow(new Object[]{
                            sale.getSaleId(),
                            sale.getProductName(),
                            customerName,
                            sale.getQuantitySold(),
                            String.format("%.2f", sale.getUnitPrice()),
                            String.format("%.2f", sale.getTotalAmount()),
                            new SimpleDateFormat("yyyy-MM-dd HH:mm").format(sale.getSaleDate())
                    });

                    totalSales += sale.getTotalAmount();
                    saleCount++;
                }
            }

            // Add summary row
            if (saleCount > 0) {
                model.addRow(new Object[]{
                        "SUMMARY",
                        "Total Sales: " + saleCount,
                        "",
                        "",
                        "TOTAL:",
                        String.format("%.2f", totalSales),
                        "Average: " + String.format("%.2f", totalSales / saleCount)
                });
            } else {
                model.addRow(new Object[]{"No sales recorded for today", "", "", "", "", "", ""});
            }

        } catch (Exception e) {
            showMessage("Error loading today's sales: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSalesHistory() {
        try {
            DefaultTableModel model = (DefaultTableModel) salesHistoryTable.getModel();
            model.setRowCount(0);

            List<Sale> sales = SaleDAO.getAllSales();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            double totalSales = 0;
            int totalQuantity = 0;

            for (Sale sale : sales) {
                String customerName = sale.getCustomerName() != null ? sale.getCustomerName() : "Walk-in Customer";

                model.addRow(new Object[]{
                        sale.getSaleId(),
                        sale.getProductName(),
                        customerName,
                        sale.getQuantitySold(),
                        String.format("%.2f", sale.getUnitPrice()),
                        String.format("%.2f", sale.getTotalAmount()),
                        dateFormat.format(sale.getSaleDate())
                });

                totalSales += sale.getTotalAmount();
                totalQuantity += sale.getQuantitySold();
            }

            // Add summary row
            if (!sales.isEmpty()) {
                model.addRow(new Object[]{
                        "SUMMARY",
                        "Total Records: " + sales.size(),
                        "Total Quantity: " + totalQuantity,
                        "",
                        "GRAND TOTAL:",
                        String.format("%.2f", totalSales),
                        "Average: " + String.format("%.2f", totalSales / sales.size())
                });
            } else {
                model.addRow(new Object[]{"No sales history available", "", "", "", "", "", ""});
            }

        } catch (Exception e) {
            showMessage("Error loading sales history: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadLowStockAlerts() {
        try {
            DefaultTableModel model = (DefaultTableModel) lowStockTable.getModel();
            model.setRowCount(0);

            List<Product> products = ProductDAO.getAllProducts();
            int alertCount = 0;
            int outOfStockCount = 0;

            for (Product product : products) {
                String status;
                if (product.getStock() == 0) {
                    status = "🔴 Out of Stock";
                    outOfStockCount++;
                } else if (product.getStock() <= 3) {
                    status = "🟠 Critical Stock";
                    alertCount++;
                } else if (product.getStock() <= 10) {
                    status = "🟡 Low Stock";
                    alertCount++;
                } else {
                    status = "🟢 In Stock";
                    // Skip products with sufficient stock
                    continue;
                }

                model.addRow(new Object[]{
                        product.getId(),
                        product.getName(),
                        product.getStock(),
                        status
                });
            }

            // Add summary row
            model.addRow(new Object[]{
                    "SUMMARY",
                    "Total Alerts: " + alertCount,
                    "Out of Stock: " + outOfStockCount,
                    "Total Products: " + products.size()
            });

        } catch (Exception e) {
            showMessage("Error loading low stock alerts: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCustomerDetails() {
        try {
            DefaultTableModel model = (DefaultTableModel) customersTable.getModel();
            model.setRowCount(0);

            List<Customer> customers = CustomerDAO.getAllCustomers();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            for (Customer customer : customers) {
                model.addRow(new Object[]{
                        customer.getId(),
                        customer.getName(),
                        customer.getEmail(),
                        customer.getPhone(),
                        customer.getAddress(),
                        customer.getRegistrationDate() != null ?
                                dateFormat.format(customer.getRegistrationDate()) : "N/A"
                });
            }

            // Add summary row
            model.addRow(new Object[]{
                    "SUMMARY",
                    "Total Customers: " + customers.size(),
                    "",
                    "",
                    "",
                    ""
            });

        } catch (Exception e) {
            showMessage("Error loading customer details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printTable(JTable table) {
        try {
            MessageFormat header = new MessageFormat("Smart Stock Report - " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date()));
            MessageFormat footer = new MessageFormat("Page {0}");

            boolean complete = table.print(JTable.PrintMode.FIT_WIDTH, header, footer);
            if (complete) {
                showSuccessMessage("Printing completed successfully!");
            } else {
                showMessage("Printing was cancelled.", "Print Cancelled", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (PrinterException ex) {
            showMessage("Error while printing: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportTable(JTable table, String reportType) {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Export " + reportType);
            fileChooser.setSelectedFile(new java.io.File(reportType.replace(" ", "_") + "_" +
                    new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".csv"));

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                java.io.PrintWriter pw = new java.io.PrintWriter(file);

                // Export headers
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                for (int i = 0; i < model.getColumnCount(); i++) {
                    pw.print(model.getColumnName(i));
                    if (i < model.getColumnCount() - 1) pw.print(",");
                }
                pw.println();

                // Export data
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        Object value = model.getValueAt(i, j);
                        pw.print(value != null ? value.toString().replace(",", ";") : "");
                        if (j < model.getColumnCount() - 1) pw.print(",");
                    }
                    pw.println();
                }

                pw.close();
                showSuccessMessage("Data exported successfully to: " + file.getAbsolutePath());
            }
        } catch (Exception ex) {
            showMessage("Error exporting data: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    // Enhanced Gradient Panel
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
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gp = new GradientPaint(0, 0, startColor, getWidth(), getHeight(), endColor);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    // Enhanced Rounded Border
    class RoundBorder implements javax.swing.border.Border {
        private int radius;
        private Color color;
        private int thickness;

        RoundBorder(int radius, Color color, int thickness) {
            this.radius = radius;
            this.color = color;
            this.thickness = thickness;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x + thickness/2, y + thickness/2, width - thickness, height - thickness, radius, radius);
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
