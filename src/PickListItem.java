/**
 * Implementing a record class to represent a pick list item.
 * @author Nathan Thompson - njt38
 * @param aLocation
 * @param aBatch
 */
public record PickListItem(Location theLocation, Batch theBatch) {

    /**
     * Returns a formatted string of a pick list item.
     * @return
     */
    @Override
    public String toString() {
        return String.format("Location: (%d,%d) Batch: Part code: %d, quantity %d",
                theLocation.row(), theLocation.col(), theBatch.getPartCode(), theBatch.getQuantity());
    }
}
