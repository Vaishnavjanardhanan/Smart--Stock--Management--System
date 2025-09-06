package view;

import javax.swing.*;
import java.awt.*;
import model.ProductDAO;

public class UpdateStockFrame extends JFrame {
    public UpdateStockFrame() {
        setTitle("Update Stock");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        JTextField productIdField = new JTextField();
        JTextField stockField = new JTextField();
        JButton updateBtn = new JButton("Update");

        panel.add(new JLabel("Product ID:"));
        panel.add(productIdField);
        panel.add(new JLabel("New Stock:"));
        panel.add(stockField);
        panel.add(new JLabel(""));
        panel.add(updateBtn);

        add(panel);

        updateBtn.addActionListener(e -> {
            try {
                int productId = Integer.parseInt(productIdField.getText().trim());
                int newStock = Integer.parseInt(stockField.getText().trim());

                boolean success = ProductDAO.updateStock(productId, newStock);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Stock updated successfully!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update stock. Check Product ID.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers.");
            }
        });
    }
}
