package view;

import javax.swing.*;
import java.awt.*;
import model.ProductDAO;

public class DeleteProductFrame extends JFrame {
    public DeleteProductFrame() {
        setTitle("Delete Product");
           setSize(300, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField productIdField = new JTextField();
        JButton deleteBtn = new JButton("Delete");

        panel.add(new JLabel("Product ID:"));
        panel.add(productIdField);
        panel.add(new JLabel(""));
     panel.add(deleteBtn);

        add(panel);

        deleteBtn.addActionListener(e -> {
            try {
                int productId = Integer.parseInt(productIdField.getText().trim());
                boolean success = ProductDAO.deleteProduct(productId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Product deleted successfully!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete product. Check Product ID.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid Product ID.");
            }
        });
    }
}
      
