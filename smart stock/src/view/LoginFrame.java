package view;

import model.User;
import model.UserDAO;

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
        JLabel background = new JLabel(new ImageIcon("resources/background.png"));
        setContentPane(background);
        background.setLayout(new GridBagLayout());

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

        background.add(panel, new GridBagConstraints());
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        UserDAO dao = new UserDAO();
        User user = dao.authenticate(username, password);

        if (user != null) {
            JOptionPane.showMessageDialog(this, "Welcome " + user.getUsername());
            new HomeFrame(user).setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials!");
        }
    }

    // ✅ Registration form
    private void openRegisterForm() {
        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();
        String[] roles = {"admin", "cashier"};
        JComboBox<String> roleBox = new JComboBox<>(roles);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.add(new JLabel("Username:"));
        panel.add(username);
        panel.add(new JLabel("Password:"));
        panel.add(password);
        panel.add(new JLabel("Role:"));
        panel.add(roleBox);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Register New User", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String uname = username.getText().trim();
            String pass = new String(password.getPassword());
            String role = (String) roleBox.getSelectedItem();

            if (!uname.isEmpty() && !pass.isEmpty()) {
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
}
