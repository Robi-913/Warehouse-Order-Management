package bll.validators.product;

import bll.validators.ValidationException;
import bll.validators.Validator;
import model.Product;

public class ProductPriceValidator implements Validator<Product> {
    private static final double MIN_PRICE = 50.0;
    private static final double MAX_PRICE = 2000.0;

    @Override
    public void validate(Product product) {
        double price = product.getPrice();
        if (price < MIN_PRICE || price > MAX_PRICE) {
            throw new ValidationException("The product price must be between " + MIN_PRICE + " and " + MAX_PRICE + ".");
        }
    }
}
