/**
 * A record representing a location in the warehouse.
 * @author Nathan Thompson - njt38
 * @param row
 * @param col
 */
public record Location(int row, int col) {
    /**
     * Returns a formatted string of a location.
     * @return
     */
    @Override
    public String toString() {
        return String.format("(%d, %d)", row, col);
    }
}
