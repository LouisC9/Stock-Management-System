

public abstract class Product {
    private String productName;
    private int itemNumber;
    private int quantityAvailable;
    private double productPrice;
    private boolean productStatus;

    public Product(int itemNumber, String productName, int quantityAvailable, double productPrice) {
        this.itemNumber = itemNumber;
        this.productName = productName;
        this.quantityAvailable = quantityAvailable;
        this.productPrice = productPrice;
        this.productStatus = true;
    }

    public String getProductName() { return productName; }
    public int getItemNumber()   { return itemNumber; }
    public int getQuantityAvailable() { return quantityAvailable; }
    public double getProductPrice(){ return productPrice; }
    public boolean isProductStatus(){ return productStatus; }

    public void setProductStatus(boolean status) { this.productStatus = status; }

    /**
     * Attempts to add stock. Returns a message.
     */
    public String addStock(int quantity) {
        if (!productStatus) {
            return "Cannot add stock. Product is discontinued.";
        }
        this.quantityAvailable += quantity;
        return "Stock added successfully.";
    }

    /**
     * Attempts to deduct stock. Returns a message.
     */
    public String deductStock(int quantity) {
        if (!productStatus) {
            return "Cannot deduct stock. Product is discontinued.";
        }
        if (quantity > quantityAvailable) {
            return "Deduct quantity should not exceed current stock.";
        }
        this.quantityAvailable -= quantity;
        return "Stock deducted successfully.";
    }

    public double getInventoryValue() {
        return productPrice * quantityAvailable;
    }

    @Override
    public abstract String toString();
}
