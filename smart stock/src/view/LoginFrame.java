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

        JLabel background = new JLabel(new ImageIcon("resources/background.png"));
        setContentPane(background);
        background.setLayout(new GridBagLayout());

        JPanel panel = new JPanel(new GridLayout(3,2,10,10));
        panel.setOpaque(false);
        usernameField = new JTextField();
        passwordField = new JPasswordField();

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> login());
        panel.add(new JLabel());
        panel.add(loginButton);

        background.add(panel, new GridBagConstraints());
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        UserDAO dao = new UserDAO();
        User user = dao.authenticate(username, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Welcome " + user.getUsername());
            new HomeFrame(user).setVisible(true);  // ✅ Pass User to HomeFrame
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials!");
        }
    }
}
