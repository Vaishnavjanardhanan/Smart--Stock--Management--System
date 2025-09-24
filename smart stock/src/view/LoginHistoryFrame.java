package view;

import model.LoginHistoryDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LoginHistoryFrame extends JFrame {
    private JTable historyTable;
    
    public LoginHistoryFrame() {
        initializeUI();
        setupComponents();
        loadLoginHistory();
    }
    
    private void initializeUI() {
        setTitle("Smart Stock - Login History");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }
    
    private void setupComponents() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Login History");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(102, 0, 153));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.setBackground(new Color(102, 0, 153));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> loadLoginHistory());
        headerPanel.add(refreshBtn, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        String[] columns = {"Username", "Login Time", "Logout Time", "Session Duration"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        historyTable = new JTable(model);
        historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        historyTable.setRowHeight(30);
        historyTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        historyTable.getTableHeader().setBackground(new Color(102, 0, 153));
        historyTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(historyTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        add(tablePanel, BorderLayout.CENTER);
    }
    
    private void loadLoginHistory() {
        try {
            var history = LoginHistoryDAO.getLoginHistory();
            DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
            model.setRowCount(0);
            
            for (var record : history) {
                String duration = record.getLogoutTime() != null ? 
                    formatDuration(record.getSessionDuration()) : "Still logged in";
                
                model.addRow(new Object[]{
                    record.getUsername(),
                    record.getLoginTime(),
                    record.getLogoutTime(),
                    duration
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading login history: " + e.getMessage());
        }
    }
    
    private String formatDuration(int seconds) {
        if (seconds < 60) return seconds + " seconds";
        int minutes = seconds / 60;
        if (minutes < 60) return minutes + " minutes";
        int hours = minutes / 60;
        return hours + " hours " + (minutes % 60) + " minutes";
    }
}