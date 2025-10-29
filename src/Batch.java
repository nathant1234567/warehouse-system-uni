/**
 * A part and how many of them.
 * @author Nathan Thompson - njt38
 */
public class Batch implements Comparable<Batch>
{
    // The part code.
    private final int partCode;
    // The number of this part.
    private int quantity;

    /**
     * A part and a number of items.
     * @param partCode The part code.
     * @param quantity The number of items of this part code.
     */
    public Batch(int partCode, int quantity)
    {
        this.partCode = partCode;
        this.quantity = quantity;
    }

    /**
     * Increases the quantity of the batch by the specified amount.
     * @param amount
     */
    public void increaseQuantity(int amount)
    {
        quantity += amount;
    }

    /**
     * Reduces the quantity of the batch by the specified amount.
     * @param amount
     */
    public void reduceQuantity(int amount)
    {
        quantity -= amount;
    }

    /**
     * Gets the part code.
     * @return
     */
    public int getPartCode()
    {
        return partCode;
    }

    /**
     * Gets the quantity.
     * @return
     */
    public int getQuantity()
    {
        return quantity;
    }

    /**
     * Returns a string representation of the batch.
     * @return
     */
    @Override
    public String toString()
    {
        return String.format("Part code: %d, quantity %d", partCode, quantity);
    }

    /**
     * Compares the part code of the batch to another part code.
     * @param other the object to be compared.
     * @return
     */
    @Override
    public int compareTo(Batch other)
    {
        return partCode - other.partCode;
    }
}
