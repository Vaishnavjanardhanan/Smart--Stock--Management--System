package view;

import javax.swing.*;

public class ProductFormDialog extends JDialog {
    public ProductFormDialog(JFrame parent) {
        super(parent, "Add Product", true);
        setSize(300,200);
        setLocationRelativeTo(parent);
    }
}
