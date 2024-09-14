package bll.validators.order;

import bll.validators.ValidationException;
import bll.validators.Validator;
import model.Order;

public class OrderQuantityValidator implements Validator<Order> {

    private static final int MAX_QUANTITY = 1000;

    @Override
    public void validate(Order order) {
        if (order.getQuantity() <= 0) {
            throw new ValidationException("The quantity must be greater than 0.");
        }
        if (order.getQuantity() > MAX_QUANTITY) {
            throw new ValidationException("The quantity must be less than or equal to " + MAX_QUANTITY + ".");
        }
    }
}
