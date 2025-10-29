/**
 * Part class to represent a part that the company sells in the system.
 * @author Nathan Thompson - njt38
 * @param partCode
 * @param type
 * @param manufacturer
 * @param description
 * @param price
 */
public record Part(int partCode, String type, String manufacturer, String description, double price) implements Comparable<Part> {
    @Override
    public String toString() {
        return String.format("%s: %s, %s costs £%.2f", type, manufacturer, description, price);
    }

    /**
     * Compares this part to another part.
     * @param other the object to be compared.
     * @return
     */
    @Override
    public int compareTo(Part other) {
        return partCode - other.partCode();
    }

}
