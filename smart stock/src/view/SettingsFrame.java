package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SettingsFrame extends JFrame {
    private JPanel contentPanel;
    private Connection connection;
    private String currentUser;
    private JTable userTable;
    private DefaultTableModel tableModel;

    // Simple color scheme
    private final Color PRIMARY_COLOR = new Color(102, 0, 153);
    private final Color BACKGROUND_COLOR = new Color(245, 245, 255);

    public SettingsFrame(Connection connection, String currentUser) {
        this.connection = connection;
        this.currentUser = currentUser;
        initializeUI();
        setupMainContent();
        loadUsers(); // Load users when frame opens
    }

    private void initializeUI() {
        setTitle("Settings - Smart Stock");
        setSize(600, 700); // Increased size to accommodate user list
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(true);
    }

    private void setupMainContent() {
        contentPanel = new JPanel();
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = createHeaderPanel();
        contentPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel mainPanel = createMainPanel();

        // Use scroll pane for main content
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("⚙️ Settings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);

        panel.add(titleLabel);
        return panel;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);

        // Current User Info
        panel.add(createCurrentUserInfo());
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Change Password Section
        panel.add(createSectionTitle("Change Password"));
        panel.add(createPasswordForm());

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // User Management Section (Admin only)
        if (isAdminUser()) {
            panel.add(createSectionTitle("User Management (Admin)"));
            panel.add(createUserManagementPanel());
            panel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        // Action Buttons at the bottom
        panel.add(createActionButtons());

        return panel;
    }

    private JPanel createCurrentUserInfo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(220, 230, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                new EmptyBorder(10, 15, 10, 15)
        ));

        String userRole = getUserRole();
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());

        JLabel userInfo = new JLabel("👤 Logged in as: " + currentUser + " | Role: " + userRole + " | Time: " + currentTime);
        userInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userInfo.setForeground(Color.DARK_GRAY);

        panel.add(userInfo, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSectionTitle(String title) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(5, 0, 10, 0));

        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(PRIMARY_COLOR);

        panel.add(label);
        return panel;
    }

    private JPanel createPasswordForm() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel currentLabel = new JLabel("Current Password:");
        JPasswordField currentField = new JPasswordField(20);

        JLabel newLabel = new JLabel("New Password:");
        JPasswordField newField = new JPasswordField(20);

        JLabel confirmLabel = new JLabel("Confirm Password:");
        JPasswordField confirmField = new JPasswordField(20);

        JButton changeBtn = new JButton("Change Password");
        changeBtn.setBackground(PRIMARY_COLOR);
        changeBtn.setForeground(Color.WHITE);
        changeBtn.setFocusPainted(false);
        changeBtn.addActionListener(e -> changePassword(
                new String(currentField.getPassword()),
                new String(newField.getPassword()),
                new String(confirmField.getPassword())
        ));

        panel.add(currentLabel);
        panel.add(currentField);
        panel.add(newLabel);
        panel.add(newField);
        panel.add(confirmLabel);
        panel.add(confirmField);
        panel.add(new JLabel()); // Empty cell for spacing
        panel.add(changeBtn);

        return panel;
    }

    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create table model with columns
        String[] columns = {"User ID", "Username", "Role", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setRowHeight(30);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        userTable.getTableHeader().setBackground(PRIMARY_COLOR);
        userTable.getTableHeader().setForeground(Color.WHITE);

        // Add mouse listener for row selection
        userTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = userTable.rowAtPoint(evt.getPoint());
                if (row >= 0) {
                    String username = tableModel.getValueAt(row, 1).toString();
                    String role = tableModel.getValueAt(row, 2).toString();
                    showUserActions(username, role);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setPreferredSize(new Dimension(500, 200));
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR),
                "Existing Users (" + tableModel.getRowCount() + " users)"
        ));

        // Admin actions panel
        JPanel actionPanel = createAdminActionPanel();

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAdminActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionPanel.setBackground(BACKGROUND_COLOR);
        actionPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton addUserBtn = createStyledButton("Add New User", Color.GREEN);
        JButton resetPassBtn = createStyledButton("Reset Password", Color.ORANGE);
        JButton deleteUserBtn = createStyledButton("Delete User", Color.RED);
        JButton refreshBtn = createStyledButton("Refresh List", PRIMARY_COLOR);

        addUserBtn.addActionListener(e -> addNewUser());
        resetPassBtn.addActionListener(e -> resetUserPassword());
        deleteUserBtn.addActionListener(e -> deleteUser());
        refreshBtn.addActionListener(e -> loadUsers());

        actionPanel.add(addUserBtn);
        actionPanel.add(resetPassBtn);
        actionPanel.add(deleteUserBtn);
        actionPanel.add(refreshBtn);

        return actionPanel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 30));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return button;
    }

    private JPanel createActionButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton saveBtn = new JButton("💾 Save Settings");
        JButton cancelBtn = new JButton("❌ Close");
        JButton helpBtn = new JButton("❓ Help");

        for (JButton btn : new JButton[]{saveBtn, cancelBtn, helpBtn}) {
            btn.setBackground(PRIMARY_COLOR);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(120, 35));
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        }

        saveBtn.addActionListener(e -> saveSettings());
        cancelBtn.addActionListener(e -> dispose());
        helpBtn.addActionListener(e -> showHelp());

        panel.add(saveBtn);
        panel.add(cancelBtn);
        panel.add(helpBtn);

        return panel;
    }

    private void loadUsers() {
        try {
            tableModel.setRowCount(0); // Clear existing data

            List<User> users = getUsersFromDatabase();

            for (User user : users) {
                String status = user.getUsername().equals(currentUser) ? "Current User" : "Active";
                tableModel.addRow(new Object[]{
                        user.getUserId(),
                        user.getUsername(),
                        user.getRole(),
                        status
                });
            }

            // Update the border title with user count
            if (userTable != null) {
                Component parent = userTable.getParent();
                if (parent instanceof JScrollPane) {
                    ((JScrollPane) parent).setBorder(BorderFactory.createTitledBorder(
                            BorderFactory.createLineBorder(PRIMARY_COLOR),
                            "Existing Users (" + users.size() + " users)"
                    ));
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + ex.getMessage());
        }
    }

    private void showUserActions(String username, String role) {
        String[] options = {"Reset Password", "Change Role", "View Details", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this,
                "User: " + username + "\nRole: " + role + "\n\nChoose action:",
                "User Management",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        switch (choice) {
            case 0: resetUserPassword(username); break;
            case 1: changeUserRole(username, role); break;
            case 2: showUserDetails(username); break;
        }
    }

    // Database methods
    private boolean isAdminUser() {
        try {
            String sql = "SELECT role FROM users WHERE username = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, currentUser);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return "admin".equalsIgnoreCase(rs.getString("role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getUserRole() {
        try {
            String sql = "SELECT role FROM users WHERE username = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, currentUser);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "UNKNOWN";
    }

    private List<User> getUsersFromDatabase() {
        List<User> users = new ArrayList<>();
        try {
            String sql = "SELECT user_id, username, role FROM users ORDER BY user_id";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return users;
    }

    private void changePassword(String currentPass, String newPass, String confirmPass) {
        // ... your existing changePassword method ...
    }

    private void addNewUser() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"admin", "cashier"});

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Role:"));
        panel.add(roleCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New User", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String role = (String) roleCombo.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Check if user already exists
                String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";
                PreparedStatement checkStmt = connection.prepareStatement(checkSql);
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.setString(3, role);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "User added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadUsers(); // Refresh the list
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding user: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetUserPassword() {
        resetUserPassword(null);
    }

    private void resetUserPassword(String username) {
        if (username == null) {
            username = JOptionPane.showInputDialog(this, "Enter username to reset password:");
        }

        if (username != null && !username.trim().isEmpty()) {
            String newPassword = generateSimplePassword();

            try {
                String sql = "UPDATE users SET password = ? WHERE username = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, newPassword);
                stmt.setString(2, username);
                int affected = stmt.executeUpdate();

                if (affected > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Password reset for user: " + username + "\nNew password: " + newPassword,
                            "Password Reset",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "User not found!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error resetting password: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void changeUserRole(String username, String currentRole) {
        String newRole = currentRole.equals("admin") ? "cashier" : "admin";

        int confirm = JOptionPane.showConfirmDialog(this,
                "Change role for " + username + " from " + currentRole + " to " + newRole + "?",
                "Confirm Role Change",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "UPDATE users SET role = ? WHERE username = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, newRole);
                stmt.setString(2, username);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Role changed successfully!");
                loadUsers();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error changing role: " + e.getMessage());
            }
        }
    }

    private void showUserDetails(String username) {
        try {
            String sql = "SELECT user_id, username, role FROM users WHERE username = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this,
                        "User Details:\n\n" +
                                "ID: " + rs.getInt("user_id") + "\n" +
                                "Username: " + rs.getString("username") + "\n" +
                                "Role: " + rs.getString("role"),
                        "User Information",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading user details: " + e.getMessage());
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            String username = tableModel.getValueAt(selectedRow, 1).toString();

            if (username.equals(currentUser)) {
                JOptionPane.showMessageDialog(this, "You cannot delete your own account!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete user: " + username + "?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String sql = "DELETE FROM users WHERE username = ?";
                    PreparedStatement stmt = connection.prepareStatement(sql);
                    stmt.setString(1, username);
                    stmt.executeUpdate();

                    JOptionPane.showMessageDialog(this, "User deleted successfully!");
                    loadUsers();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to delete!");
        }
    }

    private void saveSettings() {
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        JOptionPane.showMessageDialog(this,
                "Settings saved successfully!\nSaved at: " + currentTime,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showHelp() {
        JOptionPane.showMessageDialog(this,
                "Settings Help:\n\n" +
                        "• Change Password: Update your login password\n" +
                        "• User Management: Admin functions for user accounts\n" +
                        "• Click on any user in the list to manage them\n" +
                        "• Add User: Create new system users\n" +
                        "• Reset Password: Reset passwords for other users\n\n" +
                        "Current User: " + currentUser + "\n" +
                        "User Role: " + getUserRole(),
                "Help",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private String generateSimplePassword() {
        String[] words = {"apple", "sun", "moon", "star", "cloud", "river"};
        String[] numbers = {"123", "456", "789", "100", "999"};

        String word = words[(int)(Math.random() * words.length)];
        String number = numbers[(int)(Math.random() * numbers.length)];

        return word + number;
    }

    // User model class
    private class User {
        private int userId;
        private String username;
        private String role;

        public User(int userId, String username, String role) {
            this.userId = userId;
            this.username = username;
            this.role = role;
        }

        public int getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getRole() { return role; }
    }
}