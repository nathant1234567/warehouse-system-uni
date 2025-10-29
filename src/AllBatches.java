import java.util.*;

/**
 * A collection of batches.
 * @author Nathan Thompson - njt38
 */
public class AllBatches
{
    private final Map<Integer, Batch> batches = new TreeMap<>();

    /**
     * Adds a batch to the batch tree map using the part code for the key.
     * @return
     */
    public void addBatch(Batch batch)
    {
        batches.put(batch.getPartCode(), batch);
    }

    /**
     * Returns a collection of all the batches in the tree map.
     * @return
     */
    public Collection<Batch> getBatches()
    {
        return batches.values();
    }

    /**
     * Prints all the batches in the tree map.
     */
    public void printBatches()
    {
        for (Batch batch : batches.values()) {
            System.out.println(batch);
        }
    }

    /**
     * Returns a formatted string of all the batches in the tree map.
     * @return
     */
    @Override
    public String toString() {
        return batches.values().toString();
    }
}

