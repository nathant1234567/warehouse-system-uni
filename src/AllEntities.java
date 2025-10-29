import java.util.Map;
import java.util.TreeMap;

/**
 * AllOrders superclass for the order system. Used to represent a generic container to store a list of customer, purchase or delivery entities.
 * It provides the basic functionality for these entities.
 * Uses <t> to allow for generic types.
 * @author Nathan Thompson - njt38
 */
public class AllEntities<T extends Order> {
    protected Map<Integer, T> orders = new TreeMap<>();

}
