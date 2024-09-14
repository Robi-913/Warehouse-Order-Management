package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @param bill_id
 * @param order_id
 * @param client_id
 * @param product_id
 * @param quantity
 * @param total_price
 * @param created_at
 */
public record Bill(int bill_id, int order_id, int client_id, int product_id, int quantity, BigDecimal total_price, LocalDateTime created_at) {
}
