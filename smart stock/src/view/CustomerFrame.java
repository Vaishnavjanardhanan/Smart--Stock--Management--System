package view;

import model.CustomerDAO;
import model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerFrame extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public CustomerFrame() {
        setTitle("Customer Management");
        setSize(700, 400);
        setLocationRelativeTo(null);

        model = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Phone", "Address"}, 0);
        table = new JTable(model);
        loadCustomers();

        JScrollPane scrollPane = new JScrollPane(table);

        JButton addBtn = new JButton("Add Customer");
        addBtn.addActionListener(e -> showCustomerForm(false));

        JButton editBtn = new JButton("Edit Customer");
        editBtn.addActionListener(e -> showCustomerForm(true));

        JButton deleteBtn = new JButton("Delete Customer");
        deleteBtn.addActionListener(e -> deleteCustomer());

        JPanel panel = new JPanel();
        panel.add(addBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);

        add(scrollPane, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);
    }

    private void loadCustomers() {
        model.setRowCount(0); // clear
        List<Customer> customers = CustomerDAO.getAllCustomers();
        for (Customer c : customers) {
            model.addRow(new Object[]{c.getId(), c.getName(), c.getEmail(), c.getPhone(), c.getAddress()});
        }
    }

    private void showCustomerForm(boolean isEdit) {
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField addressField = new JTextField();

        if (isEdit) {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a customer to edit.");
                return;
            }
            nameField.setText(model.getValueAt(row, 1).toString());
            emailField.setText(model.getValueAt(row, 2).toString());
            phoneField.setText(model.getValueAt(row, 3).toString());
            addressField.setText(model.getValueAt(row, 4).toString());
        }

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Name:")); panel.add(nameField);
        panel.add(new JLabel("Email:")); panel.add(emailField);
        panel.add(new JLabel("Phone:")); panel.add(phoneField);
        panel.add(new JLabel("Address:")); panel.add(addressField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                isEdit ? "Edit Customer" : "Add Customer", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();
            String address = addressField.getText();

            boolean success;
            if (isEdit) {
                int id = (int) model.getValueAt(table.getSelectedRow(), 0);
                success = CustomerDAO.updateCustomer(id, name, email, phone, address);
            } else {
                success = CustomerDAO.addCustomer(name, email, phone, address);
            }

            if (success) {
                JOptionPane.showMessageDialog(this, "Customer saved successfully!");
                loadCustomers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save customer.");
            }
        }
    }

    private void deleteCustomer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a customer to delete.");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        if (CustomerDAO.deleteCustomer(id)) {
            JOptionPane.showMessageDialog(this, "Customer deleted successfully!");
            loadCustomers();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete customer.");
        }
    }
                                      }
