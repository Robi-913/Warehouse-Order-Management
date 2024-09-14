package presentation.windows;

import bll.BillBLL;
import bll.ClientBLL;
import bll.OrderBLL;
import bll.ProductBLL;
import bll.validators.ValidationException;
import model.Bill;
import model.Client;
import model.Order;
import model.Product;
import presentation.RoundedButton;
import presentation.TableReflectionPopulation;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class OrderOperationsView extends JFrame {
    private final OrderBLL orderBLL;
    private final ProductBLL productBLL;
    private final BillBLL billBLL;
    private final JTable orderTable;
    private final DefaultTableModel tableModel;
    private int billId;

    public OrderOperationsView() {
        billId=0;
        orderBLL = new OrderBLL();
        ClientBLL clientBLL = new ClientBLL();
        productBLL = new ProductBLL();
        billBLL = new BillBLL();

        setTitle("Order Operations");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create form for placing orders
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        JComboBox<Client> clientComboBox = createCustomComboBox(clientBLL.findAllClients().toArray(new Client[0]));
        JComboBox<Product> productComboBox = createCustomComboBox(productBLL.findAllProducts().toArray(new Product[0]));
        JTextField quantityField = new JTextField();

        formPanel.add(new JLabel("Select Client:"));
        formPanel.add(clientComboBox);
        formPanel.add(new JLabel("Select Product:"));
        formPanel.add(productComboBox);
        formPanel.add(new JLabel("Enter Quantity:"));
        formPanel.add(quantityField);

        // Initialize table and model
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (getTableHeader().getResizingColumn() == null) {
                    c.setBackground(getBackground());
                }
                if (row % 2 == 0) {
                    c.setBackground(new Color(240, 248, 255));
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        };

        // Customize table appearance
        orderTable.setShowGrid(false);
        orderTable.setShowVerticalLines(false);
        orderTable.setShowHorizontalLines(false);
        orderTable.setIntercellSpacing(new Dimension(0, 0));
        orderTable.setGridColor(new Color(173, 216, 230)); // Light blue color

        // Customize table header appearance
        JTableHeader header = orderTable.getTableHeader();
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
        orderTable.setDefaultRenderer(Object.class, centerRenderer);

        JScrollPane scrollPane = new JScrollPane(orderTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create buttons for operations
        JPanel buttonPanel = getButtonPanel(clientComboBox, productComboBox, quantityField);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(buttonPanel, BorderLayout.SOUTH);

        add(formPanel, BorderLayout.NORTH);

        loadOrders();
    }

    private JPanel getButtonPanel(JComboBox<Client> clientComboBox, JComboBox<Product> productComboBox, JTextField quantityField) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(null);

        JButton placeOrderButton = new RoundedButton("Place Order");
        JButton updateButton = new RoundedButton("Update");
        JButton deleteButton = new RoundedButton("Delete");
        JButton closeButton = new RoundedButton("Close");
        JButton viewBillsButton = new RoundedButton("View Bills");

        Dimension buttonSize = new Dimension(150, 50);
        placeOrderButton.setPreferredSize(buttonSize);
        updateButton.setPreferredSize(buttonSize);
        deleteButton.setPreferredSize(buttonSize);
        closeButton.setPreferredSize(buttonSize);
        viewBillsButton.setPreferredSize(buttonSize);

        placeOrderButton.addActionListener(e -> placeOrder(clientComboBox, productComboBox, quantityField));
        updateButton.addActionListener(e -> updateOrder());
        deleteButton.addActionListener(e -> deleteOrder());
        viewBillsButton.addActionListener(e -> viewBills());
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(placeOrderButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewBillsButton);
        buttonPanel.add(closeButton);
        return buttonPanel;
    }



    private void placeOrder(JComboBox<Client> clientComboBox, JComboBox<Product> productComboBox, JTextField quantityField) {
        try {
            Client selectedClient = (Client) clientComboBox.getSelectedItem();
            Product selectedProduct = (Product) productComboBox.getSelectedItem();
            int quantity = Integer.parseInt(quantityField.getText());

            if (selectedClient == null) {
                JOptionPane.showMessageDialog(this, "Please select a valid client.");
                return;
            }

            if (selectedProduct == null) {
                JOptionPane.showMessageDialog(this, "Please select a valid product.");
                return;
            }

            if (selectedProduct.getStock() < quantity) {
                JOptionPane.showMessageDialog(this, "Not enough stock available. Please reduce the quantity.");
                return;
            }

            String orderDate = new java.sql.Timestamp(System.currentTimeMillis()).toString();
            Order order = new Order(selectedClient.getClient_id(), selectedProduct.getProduct_id(), quantity, orderDate);
            orderBLL.addOrder(order);

            BigDecimal totalPrice = BigDecimal.valueOf(selectedProduct.getPrice() * quantity);
            Bill bill = new Bill(billId, order.getOrder_id(), selectedClient.getClient_id(), selectedProduct.getProduct_id(), quantity, totalPrice, LocalDateTime.now());
            billId=billId+1;
            billBLL.crateBill(bill);

            selectedProduct.setStock(selectedProduct.getStock() - quantity);
            productBLL.updateProduct(selectedProduct);

            showBillDialog(bill);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity format. Please enter a valid number.");
        } finally {
            loadOrders();
        }
    }


    private void updateOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to update.");
            return;
        }

        try {
            // Get current values
            int orderId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
            int clientId = Integer.parseInt(tableModel.getValueAt(selectedRow, 1).toString());
            int productId = Integer.parseInt(tableModel.getValueAt(selectedRow, 2).toString());
            int quantity = Integer.parseInt(tableModel.getValueAt(selectedRow, 3).toString());
            String orderDate = tableModel.getValueAt(selectedRow, 4).toString();

            // Get new values using pop-up dialogs
            clientId = Integer.parseInt(getInput("Enter Client ID:", String.valueOf(clientId)));
            productId = Integer.parseInt(getInput("Enter Product ID:", String.valueOf(productId)));
            quantity = Integer.parseInt(getInput("Enter Quantity:", String.valueOf(quantity)));
            orderDate = getInput("Enter Order Date:", orderDate);

            // Create a new Order object with the updated values
            Order order = new Order(orderId, clientId, productId, quantity, orderDate);
            orderBLL.updateOrder(order);

        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid data format. Please check your inputs.");
        } finally {
            loadOrders();
        }
    }

    private void deleteOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to delete.");
            return;
        }

        try {
            int orderId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
            Order order = orderBLL.findOrderById(orderId);
            orderBLL.deleteOrder(order);
        } catch (ValidationException | SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } finally {
            loadOrders();
        }
    }

    private void loadOrders() {
        List<Order> orders = orderBLL.findAllOrders();
        TableReflectionPopulation.populateTableWithData(tableModel, orders);
    }

    private String getInput(String message, String initialValue) {
        String input = (String) JOptionPane.showInputDialog(this, message, "Input", JOptionPane.INFORMATION_MESSAGE, null, null, initialValue);
        if (input == null || input.trim().isEmpty()) {
            throw new ValidationException("Input cannot be empty!");
        }
        return input.trim();
    }

    private <T> JComboBox<T> createCustomComboBox(T[] items) {
        JComboBox<T> comboBox = new JComboBox<>(items);
        comboBox.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                return new JButton() {
                    @Override
                    public int getWidth() {
                        return 0;
                    }
                };
            }
        });
        return comboBox;
    }

    private void showBillDialog(Bill bill) {
        // Create a panel to hold the bill details and the OK button
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel detailsPanel = new JPanel(new GridLayout(0, 1));
        String[] billDetails = {
                "Bill ID: " + bill.bill_id(),
                "Order ID: " + bill.order_id(),
                "Client ID: " + bill.client_id(),
                "Product ID: " + bill.product_id(),
                "Quantity: " + bill.quantity(),
                "Total Price: " + bill.total_price(),
                "Date: " + bill.created_at().toString()
        };
        for (String detail : billDetails) {
            detailsPanel.add(new JLabel(detail));
        }
        panel.add(detailsPanel, BorderLayout.CENTER);

        // Create the OK button
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(okButton);
            if (window != null) {
                window.dispose();
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        Icon customIcon = new ImageIcon("src/main/resources/bill_icon.png");

        // Create and show the dialog
        JOptionPane.showOptionDialog(
                this,
                panel,
                "Bill",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                customIcon,
                new Object[]{},
                null
        );
    }

    private void viewBills() {
        new BillView().setVisible(true)  ;
    }

}
