
package view;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;

public class CustomerFrame extends JFrame {
    private JTextField idField, nameField, contactField;
    private JTextArea addressArea;
    private JButton saveButton;

    public CustomerFrame() {
        setTitle("Add Customer");
        setSize(400, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel idLabel = new JLabel("Customer ID:");
        idLabel.setBounds(30, 20, 100, 25);
        add(idLabel);

        idField = new JTextField();
        idField.setBounds(150, 20, 200, 25);
        add(idField);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(30, 60, 100, 25);
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(150, 60, 200, 25);
        add(nameField);

        JLabel contactLabel = new JLabel("Contact:");
        contactLabel.setBounds(30, 100, 100, 25);
        add(contactLabel);

        contactField = new JTextField();
        contactField.setBounds(150, 100, 200, 25);
        add(contactField);

        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setBounds(30, 140, 100, 25);
        add(addressLabel);

        addressArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(addressArea);
        scrollPane.setBounds(150, 140, 200, 80);
        add(scrollPane);

        saveButton = new JButton("Save Customer");
        saveButton.setBounds(150, 240, 200, 30);
        add(saveButton);

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveCustomer();
            }
        });

        setVisible(true);
    }

    private void saveCustomer() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/customers.txt", true))) {
            String customer = idField.getText() + "," +
                              nameField.getText() + "," +
                              contactField.getText() + "," +
                              addressArea.getText().replace("\n", " ");
            writer.write(customer);
            writer.newLine();
            JOptionPane.showMessageDialog(null, "Customer saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}