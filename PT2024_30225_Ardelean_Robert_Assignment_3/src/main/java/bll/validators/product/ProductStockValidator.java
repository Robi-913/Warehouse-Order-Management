package bll.validators.product;

import bll.validators.ValidationException;
import bll.validators.Validator;
import model.Product;

public class ProductStockValidator implements Validator<Product> {

    @Override
    public void validate(Product product) {
        if (product.getStock() < 0) {
            throw new ValidationException("The stock cannot be negative.");
        }
    }
}
