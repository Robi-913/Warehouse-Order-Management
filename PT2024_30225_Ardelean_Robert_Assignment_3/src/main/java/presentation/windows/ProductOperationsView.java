package presentation.windows;

import bll.ProductBLL;
import bll.validators.ValidationException;
import model.Product;
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

public class ProductOperationsView extends JFrame {
    private final ProductBLL productBLL;
    private final JTable productTable;
    private final DefaultTableModel tableModel;
    private final Icon customIcon;

    public ProductOperationsView() {
        productBLL = new ProductBLL();
        customIcon = new ImageIcon("src/main/resources/icon_bd.png"); // Change to your icon path

        setTitle("Product Operations");
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
        productTable = new JTable(tableModel) {
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
        productTable.setShowGrid(false);
        productTable.setShowVerticalLines(false);
        productTable.setShowHorizontalLines(false);
        productTable.setIntercellSpacing(new Dimension(0, 0));
        productTable.setGridColor(new Color(173, 216, 230)); // Light blue color

        // Customize table header appearance
        JTableHeader header = productTable.getTableHeader();
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
        productTable.setDefaultRenderer(Object.class, centerRenderer);

        JScrollPane scrollPane = new JScrollPane(productTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create buttons for operations
        JPanel buttonPanel = getButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        loadProducts();
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

        addButton.addActionListener(e -> addProduct());
        updateButton.addActionListener(e -> updateProduct());
        deleteButton.addActionListener(e -> deleteProduct());
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);
        return buttonPanel;
    }


    private void loadProducts() {
        List<Product> products = productBLL.findAllProducts();
        TableReflectionPopulation.populateTableWithData(tableModel, products);
    }

    private void addProduct() {
        try {
            int id = Integer.parseInt(getInput("Enter Id:"));
            String name = getInput("Enter Name:");
            double price = Double.parseDouble(getInput("Enter Price:"));
            int stock = Integer.parseInt(getInput("Enter Stock:"));

            Product product = new Product(id, name, price, stock);
            productBLL.addProduct(product);
        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid data format. Please check your inputs.");
        } finally {
            loadProducts();
        }
    }

    private void updateProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to update.");
            return;
        }

        try {
            // Get current values
            int productId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
            String currentName = tableModel.getValueAt(selectedRow, 1).toString();
            double currentPrice = Double.parseDouble(tableModel.getValueAt(selectedRow, 2).toString());
            int currentStock = Integer.parseInt(tableModel.getValueAt(selectedRow, 3).toString());

            // Get new values using pop-up dialogs
            String name = getInput("Enter Name:", currentName);
            double price = Double.parseDouble(getInput("Enter Price:", String.valueOf(currentPrice)));
            int stock = Integer.parseInt(getInput("Enter Stock:", String.valueOf(currentStock)));

            // Create a new Product object with the updated values
            Product product = new Product(productId, name, price, stock);
            productBLL.updateProduct(product);
        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid data format. Please check your inputs.");
        } finally {
            loadProducts();
        }
    }

    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.");
            return;
        }

        try {
            int productId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
            Product product = productBLL.findProductById(productId);
            productBLL.deleteProduct(product);
        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (SQLException e) {
            if (e.getMessage().contains("Cannot delete or update a parent row")) {
                JOptionPane.showMessageDialog(this, "Cannot delete product with existing orders. Please delete associated orders first.");
            } else {
                JOptionPane.showMessageDialog(this, "An unexpected error occurred. Please try again.");
            }
        } finally {
            loadProducts();
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
