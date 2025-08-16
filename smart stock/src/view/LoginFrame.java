
package view;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoginFrame extends JFrame {
    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("Login");
        setSize(300, 180);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(20, 20, 80, 25);
        add(userLabel);

        userField = new JTextField();
        userField.setBounds(100, 20, 150, 25);
        add(userField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(20, 60, 80, 25);
        add(passLabel);

        passField = new JPasswordField();
        passField.setBounds(100, 60, 150, 25);
        add(passField);

        loginButton = new JButton("Login");
        loginButton.setBounds(100, 100, 150, 25);
        add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String user = userField.getText();
                String pass = new String(passField.getPassword());

                if (!user.isEmpty() && !pass.isEmpty()) {
                    logLogin(user);
                    dispose();
                    new ProductFrame();
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter all fields.");
                }
            }
        });

        setVisible(true);
    }

    private void logLogin(String username) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/login_logs.txt", true))) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            writer.write(username + " logged in at " + dtf.format(LocalDateTime.now()));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
