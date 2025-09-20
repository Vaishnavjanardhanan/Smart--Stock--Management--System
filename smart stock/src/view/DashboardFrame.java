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