package view;

import model.User;
import model.UserDAO;
import model.LoginHistoryDAO;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Smart Stock - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Background (optional)
        try {
            JLabel background = new JLabel(new ImageIcon("Smart--Stock--Management--System-main/resources/background.png"));
            setContentPane(background);
            background.setLayout(new GridBagLayout());
        } catch (Exception e) {
            // Fallback to gradient background if image not found
            setContentPane(new GradientPanel(new Color(102, 0, 153), new Color(147, 112, 219)));
            ((JPanel)getContentPane()).setLayout(new GridBagLayout());
        }

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setOpaque(false);

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        // Buttons
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> login());

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> openRegisterForm());

        panel.add(loginButton);
        panel.add(registerButton);

        // Add panel to background with proper constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;

        if (getContentPane() instanceof JLabel) {
            ((JLabel)getContentPane()).add(panel, gbc);
        } else {
            ((JPanel)getContentPane()).add(panel, gbc);
        }

        // Add enter key listener for login
        usernameField.addActionListener(e -> login());
        passwordField.addActionListener(e -> login());
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password!");
            return;
        }

        try {
            UserDAO dao = new UserDAO();
            User user = dao.authenticate(username, password);

            if (user != null) {
                // Record login in history
                LoginHistoryDAO.recordLogin(user.getUsername());

                JOptionPane.showMessageDialog(this, "Welcome " + user.getUsername());
                new HomeFrame(user).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!");
                passwordField.setText("");
                passwordField.requestFocus();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // ✅ Registration form
    private void openRegisterForm() {
        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();
        JPasswordField confirmPassword = new JPasswordField();
        String[] roles = {"admin", "cashier"};
        JComboBox<String> roleBox = new JComboBox<>(roles);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.add(new JLabel("Username:"));
        panel.add(username);
        panel.add(new JLabel("Password:"));
        panel.add(password);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmPassword);
        panel.add(new JLabel("Role:"));
        panel.add(roleBox);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Register New User", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String uname = username.getText().trim();
            String pass = new String(password.getPassword());
            String confirmPass = new String(confirmPassword.getPassword());
            String role = (String) roleBox.getSelectedItem();

            if (!uname.isEmpty() && !pass.isEmpty() && !confirmPass.isEmpty()) {
                if (!pass.equals(confirmPass)) {
                    JOptionPane.showMessageDialog(this, "Passwords do not match!");
                    return;
                }

                if (UserDAO.addUser(uname, pass, role)) {
                    JOptionPane.showMessageDialog(this, "User registered successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Error: User could not be added (maybe username exists).");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
            }
        }
    }

    // Gradient background panel for fallback
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
}
