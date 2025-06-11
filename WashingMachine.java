

public class WashingMachine extends Product {
    private int drumSize;
    private String type;
    private boolean hasDryer;

    public WashingMachine(int itemNumber, String productName, int quantityAvailable,
                          double productPrice, int drumSize, String type, boolean hasDryer) {
        super(itemNumber, productName, quantityAvailable, productPrice);
        this.drumSize = drumSize;
        this.type = type;
        this.hasDryer = hasDryer;
    }

    @Override
    public String toString() {
        return String.format(
            "Item number     : %04d%n" +
            "Product name        : %s%n" +
            "Drum size           : %d L%n" +
            "Type                : %s%n" +
            "Has Dryer           : %s%n" +
            "Quantity available  : %d%n" +
            "Price (RM)          : %.2f%n" +
            "Inventory value     : %.2f%n" +
            "Status              : %s",
            getItemNumber(), getProductName(), drumSize, type,
            (hasDryer ? "Yes" : "No"), getQuantityAvailable(),
            getProductPrice(), getInventoryValue(),
            isProductStatus() ? "Active" : "Discontinued"
        );
    }
}
