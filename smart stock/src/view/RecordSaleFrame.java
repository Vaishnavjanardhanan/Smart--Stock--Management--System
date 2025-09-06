package view;

import javax.swing.*;
import java.awt.*;
import model.ProductDAO;

public class RecordSaleFrame extends JFrame {
    public RecordSaleFrame() {
        setTitle("Record Sale");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        JTextField productIdField = new JTextField();
        JTextField qtyField = new JTextField();
        JButton recordBtn = new JButton("Record Sale");

        panel.add(new JLabel("Product ID:"));
        panel.add(productIdField);
        panel.add(new JLabel("Quantity Sold:"));
        panel.add(qtyField);
        panel.add(new JLabel(""));
        panel.add(recordBtn);

        add(panel);

        // ✅ Action Listener
        recordBtn.addActionListener(e -> {
            try {
                int productId = Integer.parseInt(productIdField.getText().trim());
                int qty = Integer.parseInt(qtyField.getText().trim());

                boolean success = ProductDAO.recordSale(productId, qty);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Sale recorded successfully! Stock updated.");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to record sale. Not enough stock or invalid Product ID.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers.");
            }
        });
    }
}
