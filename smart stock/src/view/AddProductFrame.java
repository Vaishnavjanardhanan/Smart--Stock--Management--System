package view;

import javax.swing.*;
import java.awt.*;
import model.ProductDAO;

public class AddProductFrame extends JFrame {
    public AddProductFrame() {
        setTitle("➕ Add New Product");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with padding
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Form panel (labels + fields)
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        JLabel nameLbl = new JLabel("Product Name:");
        JTextField nameField = new JTextField();

        JLabel descLbl = new JLabel("Description:");
        JTextField descField = new JTextField();

        JLabel priceLbl = new JLabel("Price:");
        JTextField priceField = new JTextField();

        JLabel stockLbl = new JLabel("Stock:");
        JTextField stockField = new JTextField();

        formPanel.add(nameLbl);
        formPanel.add(nameField);
        formPanel.add(descLbl);
        formPanel.add(descField);
        formPanel.add(priceLbl);
        formPanel.add(priceField);
        formPanel.add(stockLbl);
        formPanel.add(stockField);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("✅ Save");
        JButton cancelBtn = new JButton("❌ Cancel");

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        // Add to main panel
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        // Button Actions
        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String desc = descField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                int stock = Integer.parseInt(stockField.getText().trim());

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Product name cannot be empty.");
                    return;
                }

                boolean success = ProductDAO.addProduct(name, desc, price, stock);
                if (success) {
                    JOptionPane.showMessageDialog(this, "✅ Product added successfully!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Failed to add product.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "⚠ Please enter valid numbers for price and stock.");
            }
        });

        cancelBtn.addActionListener(e -> dispose());
    }
}
