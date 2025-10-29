/**
 * OrderItem class represents an item in an order.
 * @param orderNumber
 * @param partCode
 * @param quantity
 */
public record OrderItem(int orderNumber, int partCode, int quantity) {
}
