
package view;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;

public class ProductFrame extends JFrame {
    private JTextField idField, nameField, categoryField, priceField, quantityField;
    private JButton saveButton;

    public ProductFrame() {
        setTitle("Add Product");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel idLabel = new JLabel("Product ID:");
        idLabel.setBounds(30, 20, 100, 25);
        add(idLabel);

        idField = new JTextField();
        idField.setBounds(150, 20, 200, 25);
        add(idField);

        JLabel nameLabel = new JLabel("Product Name:");
        nameLabel.setBounds(30, 60, 100, 25);
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(150, 60, 200, 25);
        add(nameField);

        JLabel catLabel = new JLabel("Category:");
        catLabel.setBounds(30, 100, 100, 25);
        add(catLabel);

        categoryField = new JTextField();
        categoryField.setBounds(150, 100, 200, 25);
        add(categoryField);

        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setBounds(30, 140, 100, 25);
        add(priceLabel);

        priceField = new JTextField();
        priceField.setBounds(150, 140, 200, 25);
        add(priceField);

        JLabel qtyLabel = new JLabel("Quantity:");
        qtyLabel.setBounds(30, 180, 100, 25);
        add(qtyLabel);

        quantityField = new JTextField();
        quantityField.setBounds(150, 180, 200, 25);
        add(quantityField);

        saveButton = new JButton("Save Product");
        saveButton.setBounds(150, 220, 200, 25);
        add(saveButton);

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveProduct();
            }
        });

        setVisible(true);
    }

    private void saveProduct() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/products.txt", true))) {
            String product = idField.getText() + "," +
                             nameField.getText() + "," +
                             categoryField.getText() + "," +
                             priceField.getText() + "," +
                             quantityField.getText();
            writer.write(product);
            writer.newLine();
            JOptionPane.showMessageDialog(null, "Product saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
