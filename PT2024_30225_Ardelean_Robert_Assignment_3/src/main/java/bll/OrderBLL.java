package bll;

import bll.validators.ValidationException;
import bll.validators.Validator;
import bll.validators.order.OrderQuantityValidator;
import dao.OrderDAO;
import model.Order;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class OrderBLL {
    private final OrderDAO orderDAO;
    private final List<Validator<Order>> validators;

    public OrderBLL() {
        orderDAO = new OrderDAO();
        validators = new ArrayList<>();
        validators.add(new OrderQuantityValidator());
    }

    public Order findOrderById(int id) {
        return orderDAO.findById(id);
    }

    public List<Order> findAllOrders() {
        return orderDAO.findAll();
    }

    public void addOrder(Order order) {
        for (Validator<Order> validator : validators) {
            validator.validate(order);
        }
        orderDAO.insert(order);
    }

    public void updateOrder(Order order) {
        for (Validator<Order> validator : validators) {
            validator.validate(order);
        }
        orderDAO.update(order);
    }

    public void deleteOrder(Order order) throws ValidationException, SQLException {
        orderDAO.delete(order);
    }
}
