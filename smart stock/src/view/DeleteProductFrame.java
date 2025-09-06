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
     
      
