package presentation;

import presentation.windows.ClientOperationsView;
import presentation.windows.OrderOperationsView;
import presentation.windows.ProductOperationsView;

import javax.swing.*;
import java.awt.*;

/**
 *         // Set border
 *         // Set icon
 *         // Create and add icon label
 */
public class View extends JFrame {

    public View() {
        setTitle("Warehouse");
        setSize(720, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(50, 36, 178), 7));

        ImageIcon imageIcon = new ImageIcon("src/main/resources/icon.png");
        ImageIcon image = new ImageIcon("src/main/resources/icon_128.png");
        setIconImage(imageIcon.getImage());

        JLabel iconLabel = new JLabel(image);
        iconLabel.setBounds(289, 50, 128, 128); // Position centrally above the title
        add(iconLabel);

        // Create and add title
        JLabel titleLabel = new JLabel("Warehouse", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 100));
        titleLabel.setForeground(new Color(50, 36, 178));
        titleLabel.setBounds(10, 210, 680, 100); // Positioned centrally below the icon
        add(titleLabel);

        // Create buttons
        RoundedButton clientOperationsButton = new RoundedButton("Client Operations");
        RoundedButton productOperationsButton = new RoundedButton("Product Operations");
        RoundedButton createOrderButton = new RoundedButton("Create Order");

        // Set bounds for buttons
        clientOperationsButton.setBounds(54, 400, 163, 40);
        productOperationsButton.setBounds(271, 400, 163, 40);
        createOrderButton.setBounds(488, 400, 163, 40);

        // Add action listeners to buttons
        clientOperationsButton.addActionListener(e -> new ClientOperationsView().setVisible(true));
        productOperationsButton.addActionListener(e -> new ProductOperationsView().setVisible(true));
        createOrderButton.addActionListener(e -> new OrderOperationsView().setVisible(true));

        // Add buttons to the frame
        add(clientOperationsButton);
        add(productOperationsButton);
        add(createOrderButton);
    }

}
