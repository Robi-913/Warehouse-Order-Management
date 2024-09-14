package model;

/**
 *
 */
public class Order {
    private int order_id;
    private int client_id;
    private int product_id;
    private int quantity;
    private String order_date;

    public Order() {
    }

    public Order(int client_id, int product_id, int quantity, String order_date) {
        this.client_id = client_id;
        this.product_id = product_id;
        this.quantity = quantity;
        this.order_date = order_date;
    }

    public Order(int order_id, int client_id, int product_id, int quantity, String order_date) {
        this.order_id=order_id;
        this.client_id = client_id;
        this.product_id = product_id;
        this.quantity = quantity;
        this.order_date = order_date;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    @Override
    public String toString() {
        return "Order{" +
                "client_id=" + client_id +
                ", product_id=" + product_id +
                ", quantity=" + quantity +
                ", order_date='" + order_date + '\'' +
                '}';
    }

}
