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
        // ===== Button Actions =====
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        btnOverview.addActionListener(e -> cl.show(contentPanel, "DASHBOARD"));
        btnProducts.addActionListener(e -> cl.show(contentPanel, "PRODUCTS"));
        btnCustomers.addActionListener(e -> cl.show(contentPanel, "CUSTOMERS"));
        btnSales.addActionListener(e -> cl.show(contentPanel, "SALES"));
        btnReports.addActionListener(e -> cl.show(contentPanel, "REPORTS"));
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(58, 95, 127));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        return btn;
    }

    private JPanel createContentPanel(String message) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(message, JLabel.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    // ✅ Dashboard panel with product counts
    private JPanel createDashboardPanel(String username, String role) {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel welcome = new JLabel("Welcome, " + username + " (" + role + ")", JLabel.CENTER);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 20));
        panel.add(welcome);

        JLabel totalProducts = new JLabel("📦 Total Products: " + ProductDAO.countProducts(), JLabel.CENTER);
        totalProducts.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        panel.add(totalProducts);

        JLabel lowStock = new JLabel("⚠ Low Stock: " + ProductDAO.countLowStock(), JLabel.CENTER);
        lowStock.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        panel.add(lowStock);

        JLabel reports = new JLabel("📑 Reports Overview", JLabel.CENTER);
        reports.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        panel.add(reports);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DashboardFrame("admin", "ADMIN").setVisible(true));
    }
}
