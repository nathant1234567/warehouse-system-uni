/**
 * Customer class to represent a customer in the system.
 * @author Nathan Thompson - njt38
 * @param customerCode
 * @param businessName
 * @param vatNumber
 * @param email
 * @param phoneNumber
 * @param address
 */
public record Customer(int customerCode, String businessName, String vatNumber, String email, String phoneNumber, String address) implements Comparable<Customer> {

    /**
     * Returns a formatted string of a customer.
     * @return
     */
    @Override
    public String toString() {
        return String.format("%s, %s", businessName, address);
    }

    /**
     * Compares the customer code to another customer code.
     * @param other the object to be compared.
     * @return
     */
    @Override
    public int compareTo(Customer other) {
        return customerCode - other.customerCode();
    }
}

