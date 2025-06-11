

public class Refrigerator extends Product {
    private String doorDesign;
    private String color;
    private double capacity;

    public Refrigerator(int itemNumber, String productName, int quantityAvailable, double productPrice,
                        String doorDesign, String color, double capacity) {
        super(itemNumber, productName, quantityAvailable, productPrice);
        this.doorDesign = doorDesign;
        this.color = color;
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return String.format(
            "Item number     : %04d%n" +
            "Product name        : %s%n" +
            "Door design         : %s%n" +
            "Color               : %s%n" +
            "Capacity (Litres)   : %.1f%n" +
            "Quantity available  : %d%n" +
            "Price (RM)          : %.2f%n" +
            "Inventory value     : %.2f%n" +
            "Status              : %s",
            getItemNumber(), getProductName(), doorDesign, color, capacity,
            getQuantityAvailable(), getProductPrice(), getInventoryValue(),
            isProductStatus() ? "Active" : "Discontinued"
        );
    }
}
