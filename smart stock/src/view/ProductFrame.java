
package view;

import model.ProductDAO;
import model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductFrame extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public ProductFrame() {
        setTitle("Product Management");
        setSize(750, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(new String[]{"ID", "Name", "Description", "Price", "Stock"}, 0);
        table = new JTable(model);
        loadProducts();

        JScrollPane scrollPane = new JScrollPane(table);

        JButton addBtn = new JButton("Add Product");
        addBtn.addActionListener(e -> {
            new AddProductFrame().setVisible(true);
            loadProducts();
        });

        JButton updateBtn = new JButton("Update Stock");
        updateBtn.addActionListener(e -> {
            new UpdateStockFrame().setVisible(true);
            loadProducts();
        });

        JButton deleteBtn = new JButton("Delete Product");
        deleteBtn.addActionListener(e -> {
            new DeleteProductFrame().setVisible(true);
            loadProducts();
        });

        JButton saleBtn = new JButton("Record Sale");
        saleBtn.addActionListener(e -> {
            new RecordSaleFrame().setVisible(true);
            loadProducts();
        });

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadProducts());

        JPanel panel = new JPanel();
        panel.add(addBtn);
        panel.add(updateBtn);
        panel.add(deleteBtn);
        panel.add(saleBtn);   // new button
        panel.add(refreshBtn);

        add(scrollPane, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);
    }

    private void loadProducts() {
        model.setRowCount(0);
        List<Product> products = ProductDAO.getAllProducts();
        for (Product p : products) {
            model.addRow(new Object[]{
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getStock()
            });
        }
    }
            }package view;

import model.ProductDAO;
import model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductFrame extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public ProductFrame() {
        setTitle("Product Management");
        setSize(750, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(new String[]{"ID", "Name", "Description", "Price", "Stock"}, 0);
        table = new JTable(model);
        loadProducts();

        JScrollPane scrollPane = new JScrollPane(table);

        JButton addBtn = new JButton("Add Product");
        addBtn.addActionListener(e -> {
            new AddProductFrame().setVisible(true);
            loadProducts();
        });

        JButton updateBtn = new JButton("Update Stock");
        updateBtn.addActionListener(e -> {
            new UpdateStockFrame().setVisible(true);
            loadProducts();
        });

        JButton deleteBtn = new JButton("Delete Product");
        deleteBtn.addActionListener(e -> {
            new DeleteProductFrame().setVisible(true);
            loadProducts();
        });

        JButton saleBtn = new JButton("Record Sale");
        saleBtn.addActionListener(e -> {
            new RecordSaleFrame().setVisible(true);
            loadProducts();
        });

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadProducts());

        JPanel panel = new JPanel();
        panel.add(addBtn);
        panel.add(updateBtn);
        panel.add(deleteBtn);
        panel.add(saleBtn);   // new button
        panel.add(refreshBtn);

        add(scrollPane, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);
    }

    private void loadProducts() {
        model.setRowCount(0);
        List<Product> products = ProductDAO.getAllProducts();
        for (Product p : products) {
            model.addRow(new Object[]{
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getStock()
            });
        }
    }
        }
