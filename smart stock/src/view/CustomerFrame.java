package view;

import model.CustomerDAO;
import model.Customer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class CustomerFrame extends JFrame {
    private JTable table;
    private DefaultTableModel model;
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

    public CustomerFrame() {
        initializeUI();
        setupMainContent();
        loadCustomers();
    }

    private void initializeUI() {
        setTitle("Customer Management - Smart Stock");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    private void setupMainContent() {
        contentPanel = new GradientPanel(BACKGROUND_COLOR, new Color(230, 230, 255));
        contentPanel.setLayout(new BorderLayout(15, 15));
        contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        headerPanel = createHeaderPanel();
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

        // Enhanced title with icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);

        JLabel iconLabel = new JLabel("👥");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setBorder(new EmptyBorder(0, 0, 0, 10));

        JLabel titleLabel = new JLabel("Customer Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(PRIMARY_COLOR);

        titlePanel.add(iconLabel);
        titlePanel.add(titleLabel);
        titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Initialize statsPanel first, then update it
        statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        statsPanel.setOpaque(false);
        updateStats(); // Now statsPanel is initialized

        headerPanel.add(titlePanel, BorderLayout.NORTH);
        headerPanel.add(statsPanel, BorderLayout.CENTER);

        return headerPanel;
    }

    private void updateStats() {
        if (statsPanel == null) {
            return; // Safety check
        }

        statsPanel.removeAll();

        try {
            int totalCustomers = CustomerDAO.getAllCustomers().size();
            int activeCustomers = totalCustomers;

            JPanel[] statsCards = {
                    createStatCard("Total Customers", "👥", String.valueOf(totalCustomers), PRIMARY_COLOR),
                    createStatCard("Active Customers", "✅", String.valueOf(activeCustomers), SUCCESS_COLOR),
                    createStatCard("Database Status", "🟢", "Connected", INFO_COLOR)
            };

            for (JPanel card : statsCards) {
                statsPanel.add(card);
            }
        } catch (Exception e) {
            JPanel[] statsCards = {
                    createStatCard("Total Customers", "👥", "0", PRIMARY_COLOR),
                    createStatCard("Active Customers", "✅", "0", SUCCESS_COLOR),
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
                new EmptyBorder(20, 25, 20, 25)
        ));
        card.setPreferredSize(new Dimension(200, 100));

        // Add hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(245, 245, 245));
                card.setBorder(BorderFactory.createCompoundBorder(
                        new RoundBorder(15, color, 2),
                        new EmptyBorder(20, 25, 20, 25)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(CARD_COLOR);
                card.setBorder(BorderFactory.createCompoundBorder(
                        new RoundBorder(15, new Color(220, 220, 220), 2),
                        new EmptyBorder(20, 25, 20, 25)
                ));
            }
        });

        JLabel titleLabel = new JLabel(icon + " " + title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(Color.GRAY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(10, PRIMARY_COLOR, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Enhanced table header
        JPanel tableHeaderPanel = new JPanel(new BorderLayout());
        tableHeaderPanel.setOpaque(false);
        tableHeaderPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel tableTitle = new JLabel("Customer Database");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(PRIMARY_COLOR);

        tableHeaderPanel.add(tableTitle, BorderLayout.WEST);

        model = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Phone", "Address"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model) {
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
                return c;
            }
        };

        // Enhanced table styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(35);
        table.setSelectionBackground(new Color(147, 112, 219, 80));
        table.setSelectionForeground(PRIMARY_COLOR.darker());
        table.setGridColor(new Color(220, 220, 220));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(new RoundBorder(5, new Color(200, 200, 200), 1));

        tablePanel.add(tableHeaderPanel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton addBtn = createEnhancedButton("➕ Add Customer", PRIMARY_COLOR);
        JButton editBtn = createEnhancedButton("✏️ Edit Customer", SECONDARY_COLOR);
        JButton deleteBtn = createEnhancedButton("🗑️ Delete Customer", WARNING_COLOR);
        JButton refreshBtn = createEnhancedButton("🔄 Refresh", INFO_COLOR);
        JButton printBtn = createEnhancedButton("🖨️ Print", new Color(108, 117, 125));

        addBtn.addActionListener(e -> showCustomerForm(false));
        editBtn.addActionListener(e -> showCustomerForm(true));
        deleteBtn.addActionListener(e -> deleteCustomer());
        refreshBtn.addActionListener(e -> refreshData());
        printBtn.addActionListener(e -> printTable());

        actionPanel.add(addBtn);
        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);
        actionPanel.add(refreshBtn);
        actionPanel.add(printBtn);

        return actionPanel;
    }

    private JButton createEnhancedButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 20, 12, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new RoundedBorder(25, color));

        // Hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
                button.setBorder(new RoundedBorder(25, color.brighter()));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
                button.setBorder(new RoundedBorder(25, color));
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

    private void loadCustomers() {
        model.setRowCount(0);
        try {
            List<Customer> customers = CustomerDAO.getAllCustomers();
            if (customers.isEmpty()) {
                showMessage("No customers found in the database.", "Information", JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (Customer c : customers) {
                    model.addRow(new Object[]{c.getId(), c.getName(), c.getEmail(), c.getPhone(), c.getAddress()});
                }
            }
        } catch (Exception e) {
            showMessage("Error loading customers: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showCustomerForm(boolean isEdit) {
        JTextField nameField = new JTextField(25);
        JTextField emailField = new JTextField(25);
        JTextField phoneField = new JTextField(25);
        JTextField addressField = new JTextField(25);

        // Enhanced field styling
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);
        Color fieldBg = new Color(250, 250, 255);
        Color fieldBorder = new Color(200, 200, 220);

        for (JTextField field : new JTextField[]{nameField, emailField, phoneField, addressField}) {
            field.setFont(fieldFont);
            field.setBackground(fieldBg);
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(fieldBorder, 1),
                    new EmptyBorder(8, 10, 8, 10)
            ));
        }

        if (isEdit) {
            int row = table.getSelectedRow();
            if (row == -1) {
                showMessage("Please select a customer to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            nameField.setText(model.getValueAt(row, 1).toString());
            emailField.setText(model.getValueAt(row, 2).toString());
            phoneField.setText(model.getValueAt(row, 3).toString());
            addressField.setText(model.getValueAt(row, 4).toString());
        }

        JPanel panel = new JPanel(new GridLayout(4, 2, 15, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(250, 250, 255));

        panel.add(createFormLabel("Name:"));
        panel.add(nameField);
        panel.add(createFormLabel("Email:"));
        panel.add(emailField);
        panel.add(createFormLabel("Phone:"));
        panel.add(phoneField);
        panel.add(createFormLabel("Address:"));
        panel.add(addressField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                isEdit ? "Edit Customer" : "Add New Customer",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();

            if (name.isEmpty() || email.isEmpty()) {
                showMessage("Name and Email are required fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success;
            if (isEdit) {
                int id = (int) model.getValueAt(table.getSelectedRow(), 0);
                success = CustomerDAO.updateCustomer(id, name, email, phone, address);
            } else {
                success = CustomerDAO.addCustomer(name, email, phone, address);
            }

            if (success) {
                showSuccessMessage("Customer " + (isEdit ? "updated" : "added") + " successfully!");
                refreshData();
            } else {
                showMessage("Failed to save customer.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(80, 80, 80));
        return label;
    }

    private void deleteCustomer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            showMessage("Please select a customer to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        String customerName = model.getValueAt(row, 1).toString();

        // Enhanced confirmation dialog
        JPanel confirmPanel = new JPanel(new BorderLayout(10, 10));
        confirmPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        confirmPanel.setBackground(new Color(255, 245, 245));

        JLabel warningIcon = new JLabel("⚠️");
        warningIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        warningIcon.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel messageLabel = new JLabel("<html><b>Delete Customer:</b> " + customerName + "<br><br>This action cannot be undone.</html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        confirmPanel.add(warningIcon, BorderLayout.WEST);
        confirmPanel.add(messageLabel, BorderLayout.CENTER);

        int confirm = JOptionPane.showConfirmDialog(this, confirmPanel,
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (CustomerDAO.deleteCustomer(id)) {
                showSuccessMessage("Customer deleted successfully!");
                refreshData();
            } else {
                showMessage("Failed to delete customer. They may have existing sales records.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshData() {
        loadCustomers();
        updateStats(); // Fixed: Only update stats, don't recreate entire UI
    }

    private void printTable() {
        try {
            boolean complete = table.print();
            if (complete) {
                showSuccessMessage("Printing completed successfully!");
            } else {
                showMessage("Printing was cancelled.", "Print Cancelled", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            showMessage("Error while printing: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
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
