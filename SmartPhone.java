

public class SmartPhone extends Product {
    private String brand;
    private String model;
    private int batteryCapacity;

    public SmartPhone(int itemNumber, String productName, int quantityAvailable,
                      double productPrice, String brand, String model, int batteryCapacity) {
        super(itemNumber, productName, quantityAvailable, productPrice);
        this.brand = brand;
        this.model = model;
        this.batteryCapacity = batteryCapacity;
    }

    @Override
    public String toString() {
        return String.format(
            "Item number         : %04d%n" +
            "Product name        : %s%n" +
            "Brand               : %s%n" +
            "Model               : %s%n" +
            "Battery(mAh)        : %d%n" +
            "Quantity available  : %d%n" +
            "Price (RM)          : %.2f%n" +
            "Inventory value     : %.2f%n" +
            "Status              : %s",
            getItemNumber(), getProductName(), brand, model, batteryCapacity,
            getQuantityAvailable(), getProductPrice(), getInventoryValue(),
            isProductStatus() ? "Active" : "Discontinued"
        );
    }
}
