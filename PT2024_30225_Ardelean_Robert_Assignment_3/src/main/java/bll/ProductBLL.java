package bll;

import bll.validators.ValidationException;
import bll.validators.Validator;

import bll.validators.product.ProductPriceValidator;
import bll.validators.product.ProductStockValidator;
import dao.ProductDAO;
import model.Product;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ProductBLL {

    private final ProductDAO productDAO;
    private final List<Validator<Product>> validators;

    public ProductBLL() {
        productDAO = new ProductDAO();
        validators = new ArrayList<>();
        validators.add(new ProductPriceValidator());
        validators.add(new ProductStockValidator());
    }

    public Product findProductById(int id) {
        return productDAO.findById(id);
    }

    public List<Product> findAllProducts() {
        return productDAO.findAll();
    }

    public void addProduct(Product product) {
        for (Validator<Product> validator : validators) {
            validator.validate(product);
        }
        productDAO.insert(product);
    }

    public void updateProduct(Product product) {
        for (Validator<Product> validator : validators) {
            validator.validate(product);
        }
        productDAO.update(product);
    }

    public void deleteProduct(Product product) throws ValidationException, SQLException {
        productDAO.delete(product);
    }
}
