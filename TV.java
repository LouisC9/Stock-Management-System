
public class TV extends Product {
    private String screenType;
    private String resolution;
    private double displaySize;

    public TV(int itemNumber, String productName, int quantityAvailable, double productPrice,
              String screenType, String resolution, double displaySize) {
        super(itemNumber, productName, quantityAvailable, productPrice);
        this.screenType = screenType;
        this.resolution = resolution;
        this.displaySize = displaySize;
    }

    @Override
    public String toString() {
        return String.format(
            "Item number     : %04d%n" +
            "Product name        : %s%n" +
            "Screen type         : %s%n" +
            "Resolution          : %s%n" +
            "Display size        : %.1f%n" +
            "Quantity available  : %d%n" +
            "Price (RM)          : %.2f%n" +
            "Inventory value     : %.2f%n" +
            "Status              : %s",
            getItemNumber(), getProductName(), screenType, resolution, displaySize,
            getQuantityAvailable(), getProductPrice(), getInventoryValue(),
            isProductStatus() ? "Active" : "Discontinued"
        );
    }
}
