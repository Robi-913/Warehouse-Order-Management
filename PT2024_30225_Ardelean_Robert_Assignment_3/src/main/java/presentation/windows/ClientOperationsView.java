package presentation.windows;

import bll.ClientBLL;
import bll.validators.ValidationException;
import model.Client;
import presentation.RoundedButton;
import presentation.TableReflectionPopulation;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ClientOperationsView extends JFrame {
    private final ClientBLL clientBLL;
    private final JTable clientTable;
    private final DefaultTableModel tableModel;
    private final Icon customIcon;

    public ClientOperationsView() {
        clientBLL = new ClientBLL();
        customIcon = new ImageIcon("src/main/resources/icon_bd.png"); // Change to your icon path

        setTitle("Client Operations");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize table and model
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        clientTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (getTableHeader().getResizingColumn() == null) {
                    if (row % 2 == 0) {
                        c.setBackground(new Color(240, 248, 255)); // Light blue background for even rows
                    } else {
                        c.setBackground(Color.WHITE); // White background for odd rows
                    }
                }
                return c;
            }
        };

        // Customize table appearance
        clientTable.setShowGrid(false);
        clientTable.setShowVerticalLines(false);
        clientTable.setShowHorizontalLines(false);
        clientTable.setIntercellSpacing(new Dimension(0, 0));
        clientTable.setGridColor(new Color(173, 216, 230)); // Light blue color

        // Customize table header appearance
        JTableHeader header = clientTable.getTableHeader();
        header.setDefaultRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JLabel label = new JLabel(value.toString());
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(true);
            label.setBackground(new Color(173, 216, 230)); // Light blue color
            label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
            label.setFont(new Font("Arial", Font.BOLD, 12));
            return label;
        });

        // Center align cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        clientTable.setDefaultRenderer(Object.class, centerRenderer);

        JScrollPane scrollPane = new JScrollPane(clientTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create buttons for operations
        JPanel buttonPanel = getButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        // Load clients at startup
        loadClients();
    }

    private JPanel getButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(null);

        JButton addButton = new RoundedButton("Add");
        JButton updateButton = new RoundedButton("Update");
        JButton deleteButton = new RoundedButton("Delete");
        JButton closeButton = new RoundedButton("Close");

        Dimension buttonSize = new Dimension(150, 50);
        addButton.setPreferredSize(buttonSize);
        updateButton.setPreferredSize(buttonSize);
        deleteButton.setPreferredSize(buttonSize);
        closeButton.setPreferredSize(buttonSize);

        addButton.addActionListener(e -> addClient());
        updateButton.addActionListener(e -> updateClient());
        deleteButton.addActionListener(e -> deleteClient());
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);
        return buttonPanel;
    }

    private void loadClients() {
        List<Client> clients = clientBLL.findAllClients();
        TableReflectionPopulation.populateTableWithData(tableModel, clients);
    }

    private void addClient() {
        try {
            int id = Integer.parseInt(getInput("Enter Id:"));
            String name = getInput("Enter Name:");
            String email = getInput("Enter Email:");
            String phone = getInput("Enter Phone:");
            String address = getInput("Enter Address:");
            int age = Integer.parseInt(getInput("Enter Age:"));

            Client client = new Client(id, name, email, phone, address, age);
            clientBLL.addClient(client);
        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid data format. Please check your inputs.");
        } finally {
            loadClients();
        }
    }

    private void updateClient() {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a client to update.");
            return;
        }

        try {
            // Get current values
            int clientId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
            String currentName = tableModel.getValueAt(selectedRow, 1).toString();
            String currentEmail = tableModel.getValueAt(selectedRow, 2).toString();
            String currentPhone = tableModel.getValueAt(selectedRow, 3).toString();
            String currentAddress = tableModel.getValueAt(selectedRow, 4).toString();
            int currentAge = Integer.parseInt(tableModel.getValueAt(selectedRow, 5).toString());

            // Get new values using pop-up dialogs
            String name = getInput("Enter Name:", currentName);
            String email = getInput("Enter Email:", currentEmail);
            String phone = getInput("Enter Phone:", currentPhone);
            String address = getInput("Enter Address:", currentAddress);
            int age = Integer.parseInt(getInput("Enter Age:", String.valueOf(currentAge)));

            // Create a new Client object with the updated values
            Client client = new Client(clientId, name, email, phone, address, age);
            clientBLL.updateClient(client);
        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid data format. Please check your inputs.");
        } finally {
            loadClients();
        }
    }

    private void deleteClient() {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a client to delete.");
            return;
        }

        try {
            int clientId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
            Client client = clientBLL.findClientById(clientId);
            clientBLL.deleteClient(client);
        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (SQLException e) {
            if (e.getMessage().contains("Cannot delete or update a parent row")) {
                JOptionPane.showMessageDialog(this, "Cannot delete client with existing orders. Please delete associated orders first.");
            } else {
                JOptionPane.showMessageDialog(this, "An unexpected error occurred. Please try again.");
            }
        } finally {
            loadClients();
        }
    }

    private String getInput(String message, String initialValue) {
        String input = (String) JOptionPane.showInputDialog(this, message, "Input", JOptionPane.INFORMATION_MESSAGE, customIcon, null, initialValue);
        if (input == null || input.trim().isEmpty()) {
            throw new ValidationException("Input cannot be empty!");
        }
        return input.trim();
    }

    private String getInput(String message) {
        String input = (String) JOptionPane.showInputDialog(this, message, "Input", JOptionPane.INFORMATION_MESSAGE, customIcon, null, "");
        if (input == null || input.trim().isEmpty()) {
            throw new ValidationException("Input cannot be empty!");
        }
        return input.trim();
    }
}
