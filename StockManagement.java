

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class StockManagement {
    private final ArrayList<Product> products = new ArrayList<>();
    
    public StockManagement() {
        // nothing else
    }
    
    /** Exactly the console flow, moved out of static main */
    public void runConsole() {
        Scanner scanner = new Scanner(System.in);

        // ----- User Info -----
        String fn = getValidatedString(scanner, "First name: ");  
        String sn = getValidatedSurname(scanner, "Surname: ");
        UserInfo user = new UserInfo(fn, sn);
        System.out.println(user.getInfoString());

        // ----- Welcome -----
        System.out.printf("%nWelcome %s (UserID: %s) to the SMS%n%n",
            user.getCompleteName(), user.getUserId());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        System.out.println("Current date and time: " + dtf.format(LocalDateTime.now()));

        // ----- Group Members -----
        ArrayList<String> gm = new ArrayList<>();
        gm.add("Fun Yong Qi");
        gm.add("Chin Jian Ming");
        gm.add("Chia Yong Cheng");
        gm.add("Ng Jun Hao");
        Collections.sort(gm);
        System.out.println("Group members (alphabetical):");
        gm.forEach(System.out::println);

        // ----- Add Products -----
        int max = getNonNegativeInt(scanner, "\nHow many products do you want to add (0 to exit): ");
        if (max == 0) {
            System.out.println("No products added. Exiting...");
            goodBye(user);
            return;
        }
        for (int i = 0; i < max; i++) {
            addProductConsole(scanner);
        }

        // ----- Menu Loop -----
        int choice;
        do {
            choice = displayMenu(scanner);
            executeMenuChoice(choice, scanner);
        } while (choice != 0);

        goodBye(user);
    }

    public void goodBye(UserInfo user) {
        System.out.printf("Goodbye %s. Your user ID is: %s.%n",
            user.getCompleteName(), user.getUserId());
    }

    
    public static void main(String[] args) {
        new StockManagement().runConsole();
    }


    //=== Console Helpers ===

    private static int displayMenu(Scanner scanner) {
        System.out.println("\n--- Menu ---");
        System.out.println("1. View products");
        System.out.println("2. Add stock");
        System.out.println("3. Deduct stock");
        System.out.println("4. Discontinue product");
        System.out.println("0. Exit");
        return getMenuChoice(scanner, "Please enter a menu option: ", 0, 4);
    }

    private static int getMenuChoice(Scanner scanner, String prompt, int min, int max) {
        int val;
        do {
            System.out.print(prompt);
            while (!scanner.hasNextInt()) {
                System.out.print("Invalid input. Enter number between " + min + " and " + max + ": ");
                scanner.next();
            }
            val = scanner.nextInt();
        } while (val < min || val > max);
        scanner.nextLine();
        return val;
    }

    private void addProductConsole(Scanner scanner) {
        System.out.println("\nSelect product to add:");
        System.out.println("1. Refrigerator");
        System.out.println("2. TV");
        System.out.println("3. Washing Machine");
        System.out.println("4. SmartPhone");
        int c = getMenuChoice(scanner, "Enter your choice: ", 1, 4);
        switch (c) {
            case 1: addRefrigeratorConsole(scanner); break;
            case 2: addTVConsole(scanner);           break;
            case 3: addWashingMachineConsole(scanner); break;
            case 4: addSmartPhoneConsole(scanner);     break;
        }
    }

    public void executeMenuChoice(int choice, Scanner scanner) {
        switch (choice) {
            case 1: displayProducts();        break;
            case 2: addStockConsole(scanner); break;
            case 3: deductStockConsole(scanner); break;
            case 4: discontinueConsole(scanner);  break;
            case 0: System.out.println("Exiting the program."); break;
        }
    }

    // Exactly Console methods to avoid GUI clashes

    private void displayProducts() {
        System.out.println("\n--- Product List ---");
        products.sort(Comparator.comparingInt(Product::getItemNumber));
        for (int i = 0; i < products.size(); i++) {
            System.out.printf("[%02d]%s%n%n", i + 1, products.get(i));
        }
    }

    private void addStockConsole(Scanner scanner) {
        int idx = selectProduct(scanner);
        if (idx < 0) { System.out.println("Add stock canceled."); return; }
        Product p = products.get(idx);
        if (!p.isProductStatus()) {
            System.out.println("Cannot add stock. Product is discontinued.");
            return;
        }
        int q = getNonNegativeInt(scanner, "Enter quantity to add: ");
        System.out.println(p.addStock(q));
    }

    private void deductStockConsole(Scanner scanner) {
        int idx = selectProduct(scanner);
        if (idx < 0) { System.out.println("Deduct stock canceled."); return; }
        Product p = products.get(idx);
        if (!p.isProductStatus()) {
            System.out.println("Cannot deduct stock. Product is discontinued.");
            return;
        }
        int q;
        do {
            q = getNonNegativeInt(scanner, "Enter quantity to deduct: ");
            if (q > p.getQuantityAvailable()) {
                System.out.println("Quantity exceeds stock. Re-enter.");
            }
        } while (q > p.getQuantityAvailable());
        System.out.println(p.deductStock(q));
    }

    private void discontinueConsole(Scanner scanner) {
        int idx = selectProduct(scanner);
        if (idx < 0) { System.out.println("Discontinue canceled."); return; }
        Product p = products.get(idx);
        if (!p.isProductStatus()) {
            System.out.println("Product is already discontinued.");
            return;
        }
        p.setProductStatus(false);
        System.out.println("Product discontinued.");
    }

    private int selectProduct(Scanner scanner) {
        displayProducts();
        int idx;
        do {
            idx = getNonNegativeInt(scanner,
                "Select a product by index (1-" + products.size() + ", 0 to cancel): ") - 1;
            if (idx < 0) return -1;
        } while (idx < 0 || idx >= products.size());
        return idx;
    }

    public static String getValidatedString(Scanner scanner, String prompt) {
        String in;
        do {
            System.out.print(prompt);
            in = scanner.nextLine().trim();
            if (!in.matches("[A-Za-z ]+")) {
                System.out.println("Invalid input. Letters and spaces only.");
                in = "";
            }
        } while (in.isEmpty());
        return in;
    }

    public static String getValidatedSurname(Scanner scanner, String prompt) {
        String in;
        do {
            System.out.print(prompt);
            in = scanner.nextLine().trim();
            if (!in.matches("[A-Za-z '/]+")) {
                System.out.println("Invalid surname. Letters, spaces, / and ' only.");
                in = "";
            }
        } while (in.isEmpty());
        return in;
    }

    private static int getNonNegativeInt(Scanner scanner, String prompt) {
        int v;
        System.out.print(prompt);
        while (!scanner.hasNextInt() || (v = scanner.nextInt()) < 0) {
            System.out.print("Enter a non-negative integer: ");
            scanner.nextLine();
        }
        scanner.nextLine();
        return v;
    }

    // --- Consoleâ€�only Addâ€�Product methods ---
    public void addRefrigeratorConsole(Scanner scanner) {
        String name  = getValidatedString(scanner, "Enter product name: ");
        String des   = getValidatedString(scanner, "Enter door design: ");
        String col   = getValidatedString(scanner, "Enter color: ");
        double cap   = getStrictPositiveDouble(scanner, "Enter capacity: ");
        int qty      = getNonNegativeInt(scanner, "Enter quantity: ");
        double pr    = getStrictPositiveDouble(scanner, "Enter price: ");
        int id;
        do {
            id = getNonNegativeInt(scanner, "Enter item number: ");
            if (isItemNumberUsed(id)) 
                System.out.println("Item number already used. Enter a different one.");
        } while (isItemNumberUsed(id));
        products.add(new Refrigerator(id, name, qty, pr, des, col, cap));
    }

    public void addTVConsole(Scanner scanner) {
        String name = getValidatedString(scanner, "Enter product name: ");
        String st   = getValidatedString(scanner, "Enter screen type: ");
        String res  = getAnyString(scanner, "Enter resolution: ");
        double ds   = getStrictPositiveDouble(scanner, "Enter display size: ");
        int qty     = getNonNegativeInt(scanner, "Enter quantity: ");
        double pr   = getStrictPositiveDouble(scanner, "Enter price: ");
        int id;
        do {
            id = getNonNegativeInt(scanner, "Enter item number: ");
            if (isItemNumberUsed(id)) 
                System.out.println("Item number already used. Enter a different one.");
        } while (isItemNumberUsed(id));
        products.add(new TV(id, name, qty, pr, st, res, ds));
    }

    public void addWashingMachineConsole(Scanner scanner) {
        String name = getValidatedString(scanner, "Enter product name: ");
        int drum    = getNonNegativeInt(scanner, "Enter drum size (liters): ");
        String tp   = getValidatedString(scanner, "Enter type: ");
        // dryer
        String dIn;
        do {
            System.out.print("Has dryer? (y/n): ");
            dIn = scanner.nextLine().trim();
        } while (!dIn.equalsIgnoreCase("y") && !dIn.equalsIgnoreCase("n"));
        boolean hd = dIn.equalsIgnoreCase("y");
        int qty = getNonNegativeInt(scanner, "Enter quantity: ");
        double pr = getStrictPositiveDouble(scanner, "Enter price: ");
        int id;
        do {
            id = getNonNegativeInt(scanner, "Enter item number: ");
            if (isItemNumberUsed(id)) 
                System.out.println("Item number already used. Enter a different one.");
        } while (isItemNumberUsed(id));
        products.add(new WashingMachine(id, name, qty, pr, drum, tp, hd));
    }

    public void addSmartPhoneConsole(Scanner scanner) {
        String name = getValidatedString(scanner, "Enter product name: ");
        String bd   = getValidatedString(scanner, "Enter brand: ");
        String mo   = getAnyString(scanner, "Enter model: ");
        int bc      = getNonNegativeInt(scanner, "Enter battery capacity (mAh): ");
        int qty     = getNonNegativeInt(scanner, "Enter quantity: ");
        double pr   = getStrictPositiveDouble(scanner, "Enter price: ");
        int id;
        do {
            id = getNonNegativeInt(scanner, "Enter item number: ");
            if (isItemNumberUsed(id)) 
                System.out.println("Item number already used. Enter a different one.");
        } while (isItemNumberUsed(id));
        products.add(new SmartPhone(id, name, qty, pr, bd, mo, bc));
    }

    private static String getAnyString(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static double getStrictPositiveDouble(Scanner scanner, String prompt) {
        double v;
        System.out.print(prompt);
        while (!scanner.hasNextDouble() || (v = scanner.nextDouble()) <= 0) {
            System.out.print("Enter a positive number: ");
            scanner.nextLine();
        }
        scanner.nextLine();
        return v;
    }

    
    // Exactly GUI methods to avoid Console clashes
    public void addRefrigerator(int itemNumber,
                                String productName,
                                int quantityAvailable,
                                double productPrice,
                                String doorDesign,
                                String color,
                                double capacity) {
        products.add(new Refrigerator(
            itemNumber, productName, quantityAvailable, productPrice,
            doorDesign, color, capacity));
    }

    public void addTV(int itemNumber,
                      String productName,
                      int quantityAvailable,
                      double productPrice,
                      String screenType,
                      String resolution,
                      double displaySize) {
        products.add(new TV(
            itemNumber, productName, quantityAvailable, productPrice,
            screenType, resolution, displaySize));
    }

    public void addWashingMachine(int itemNumber,
                                  String productName,
                                  int quantityAvailable,
                                  double productPrice,
                                  int drumSize,
                                  String type,
                                  boolean hasDryer) {
        products.add(new WashingMachine(
            itemNumber, productName, quantityAvailable, productPrice,
            drumSize, type, hasDryer));
    }

    public void addSmartPhone(int itemNumber,
                              String productName,
                              int quantityAvailable,
                              double productPrice,
                              String brand,
                              String model,
                              int batteryCapacity) {
        products.add(new SmartPhone(
            itemNumber, productName, quantityAvailable, productPrice,
            brand, model, batteryCapacity));
    }

    
    // --- Engine helpers for GUI usage ---
    public boolean isItemNumberUsed(int itemNumber) {
        return products.stream().anyMatch(p -> p.getItemNumber() == itemNumber);
    }
    public ArrayList<Product> getProducts() {
        return products;
    }
    public String addStock(int idx, int qty) {
        return products.get(idx).addStock(qty);
    }
    public String deductStock(int idx, int qty) {
        return products.get(idx).deductStock(qty);
    }
    public String discontinueProduct(int idx) {
        Product p = products.get(idx);
        if (!p.isProductStatus()) return "Product is already discontinued.";
        p.setProductStatus(false);
        return "Product discontinued.";
    }
}
