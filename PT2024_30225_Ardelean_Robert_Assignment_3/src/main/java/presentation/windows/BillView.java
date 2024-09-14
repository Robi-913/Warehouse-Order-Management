package presentation.windows;

import bll.BillBLL;
import model.Bill;
import presentation.RoundedButton;
import presentation.TableReflectionPopulation;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 *
 */
public class BillView extends JFrame {
    private final BillBLL billBLL;
    private final JTable billTable;
    private final DefaultTableModel tableModel;

    public BillView() {
        billBLL = new BillBLL();

        setTitle("All Bills");
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
        billTable = new JTable(tableModel) {
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
        billTable.setShowGrid(false);
        billTable.setShowVerticalLines(false);
        billTable.setShowHorizontalLines(false);
        billTable.setIntercellSpacing(new Dimension(0, 0));
        billTable.setGridColor(new Color(173, 216, 230)); // Light blue color

        // Customize table header appearance
        JTableHeader header = billTable.getTableHeader();
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
        billTable.setDefaultRenderer(Object.class, centerRenderer);

        JScrollPane scrollPane = new JScrollPane(billTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create button for closing
        JButton closeButton = new RoundedButton("Close");

        // Set button size
        Dimension buttonSize = new Dimension(150, 50);
        closeButton.setPreferredSize(buttonSize);

        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Show information message
        showInfoMessage();

        // Load bills at startup
        loadBills();
    }

    private void loadBills() {
        List<Bill> bills = billBLL.findAllBills();
        TableReflectionPopulation.populateTableWithData(tableModel, bills);
    }

    private void showInfoMessage() {
        String message = "If the Order ID is 0, the order has been deleted/canceled.";
        ImageIcon icon = new ImageIcon("src/main/resources/bill_icon.png");
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE, icon);
    }

}
