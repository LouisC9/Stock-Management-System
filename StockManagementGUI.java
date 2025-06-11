import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class StockManagementGUI extends Application {

    // --- Constants ---
    private static final String APP_TITLE = "Stock Management System";
    private static final String[] GROUP_MEMBERS = {"Fun Yong Qi", "Chin Jian Ming", "Chia Yong Cheng", "Ng Jun Hao"};
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // --- Data ---
    private final StockManagement stockManager = new StockManagement();
    private final ObservableList<Product> productList = FXCollections.observableArrayList();
    private UserInfo currentUser;
    private int maxProducts = 10;

    // --- UI Components ---
    private ListView<Product> productListView;
    private BorderPane rootLayout;
    private VBox rightPanel;
    private Label headerUserLabel, headerDateLabel;
    private Button addProductBtn;
    private Label maxProductsLabel;

    // --- Dynamic Add Form Fields ---
    private ComboBox<String> addTypeBox;
    private TextField addItemNumber, addName, addPrice, addQty, addF1, addF2, addF3;

    @Override
    public void start(Stage primaryStage) {
        showUserInfoDialog();
    }

    // --- Header ---
    private VBox createHeader() {
        VBox header = new VBox(5);
        header.setPadding(new Insets(18, 30, 18, 30));
        header.setStyle("-fx-background-color: linear-gradient(to right, #283e51, #485563);");
        
        Label title = new Label("Stock Management Dashboard");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        title.setTextFill(Color.WHITE);

        headerUserLabel = new Label("User: " + currentUser.getCompleteName() + " (ID: " + currentUser.getUserId() + ")");
        headerUserLabel.setTextFill(Color.LIGHTGRAY);
        headerUserLabel.setFont(Font.font("Segoe UI", 15));

        headerDateLabel = new Label("Current Time: " + DATE_TIME_FORMATTER.format(LocalDateTime.now()));
        headerDateLabel.setTextFill(Color.LIGHTGRAY);
        headerDateLabel.setFont(Font.font("Segoe UI", 13));

        maxProductsLabel = new Label(getMaxProductsInfo());
        maxProductsLabel.setTextFill(Color.LIGHTGRAY);
        maxProductsLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));

        Label group = new Label("Group: " + String.join(", ", GROUP_MEMBERS));
        group.setTextFill(Color.LIGHTGRAY);
        group.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));

        header.getChildren().addAll(title, headerUserLabel, headerDateLabel, maxProductsLabel, group);
        return header;
    }

    private String getMaxProductsInfo() {
        return "Max Products: " + maxProducts + " | Added: " + stockManager.getProducts().size() + 
               " | Remaining: " + (maxProducts - stockManager.getProducts().size());
    }

    private void updateMaxProductsLabel() {
        if (maxProductsLabel != null) {
            maxProductsLabel.setText(getMaxProductsInfo());
        }
    }

    // --- Main Content ---
    private BorderPane createMainContent() {
        BorderPane mainContent = new BorderPane();

        // Left: Product List
        VBox left = new VBox(10);
        left.setPadding(new Insets(20));
        left.setPrefWidth(350); // Set preferred width for left panel
        left.setStyle("-fx-background-color: #f7fafd;");
        
        addProductBtn = new Button("+ Add Product");
        addProductBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        addProductBtn.setOnAction(e -> showAddProductForm());

        productListView = new ListView<>(productList);
        productListView.setCellFactory(lv -> new ProductListCell());
        productListView.setOnMouseClicked(e -> {
            Product selected = productListView.getSelectionModel().getSelectedItem();
            if (selected != null) showProductDetails(selected);
        });

        left.getChildren().addAll(addProductBtn, productListView);
        VBox.setVgrow(productListView, Priority.ALWAYS);

        // Right: Details/Actions Panel
        rightPanel = new VBox();
        rightPanel.setPadding(new Insets(30));
        rightPanel.setAlignment(Pos.TOP_CENTER);
        rightPanel.setStyle("-fx-background-color: white;");
        showWelcomePanel();

        mainContent.setLeft(left);
        mainContent.setCenter(rightPanel);

        return mainContent;
    }

    // --- Welcome Panel ---
    private void showWelcomePanel() {
        rightPanel.getChildren().clear();
        Label welcome = new Label("Welcome to the Stock Management System!");
        welcome.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        welcome.setTextFill(Color.web("#283e51"));
        
        Label tip = new Label("Select a product from the list or click '+ Add Product' to begin.");
        tip.setFont(Font.font("Segoe UI", 15));
        tip.setTextFill(Color.web("#485563"));
        
        rightPanel.getChildren().addAll(welcome, tip);
    }

    // --- Product Details Panel ---
    private void showProductDetails(Product p) {
        rightPanel.getChildren().clear();
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: #fff; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, #b0b8c1, 10, 0.2, 0, 4);");
        box.setAlignment(Pos.TOP_LEFT);

        // Format ID with 4 digits and show it first
        Label id = new Label(String.format("Item Number: %04d", p.getItemNumber()));
        id.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        
        Label name = new Label("Name: " + p.getProductName());
        name.setFont(Font.font("Segoe UI", 15));
        
        Label type = new Label("Type: " + getTypeString(p));
        type.setFont(Font.font("Segoe UI", 15));
        
        Label status = new Label("Status: " + (p.isProductStatus() ? "Active" : "Discontinued"));
        status.setTextFill(p.isProductStatus() ? Color.GREEN : Color.RED);
        status.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        
        Label qty = new Label("Quantity: " + p.getQuantityAvailable());
        Label price = new Label(String.format("Price: RM %.2f", p.getProductPrice()));
        Label value = new Label(String.format("Inventory Value: RM %.2f", p.getInventoryValue()));
        qty.setFont(Font.font("Segoe UI", 14));
        price.setFont(Font.font("Segoe UI", 14));
        value.setFont(Font.font("Segoe UI", 14));

        // Product-specific details section
        VBox details = new VBox(5);
        Label label = new Label("Other Details:");
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        details.getChildren().add(label);


        // Convert the product's toString() result into separate lines
        String[] productInfo = p.toString().split("\n");

        // Go through each line of product information
        for (String line : productInfo) {
            // Check if the line contains a colon (separates property name from value)
            if (line.contains(":")) {
                // Split the line into property name and value
                String[] parts = line.split(":");
                
                // Get the property name without extra spaces
                String propertyName = parts[0].trim();
                String propertyValue = parts.length > 1 ? parts[1].trim() : "";
                
                // Skip basic information that's already shown above
                boolean isBasicInfo = propertyName.equals("Item number") || 
                                    propertyName.equals("Product name") || 
                                    propertyName.equals("Status") ||
                                    propertyName.equals("Quantity available") ||
                                    propertyName.equals("Price (RM)") ||
                                    propertyName.equals("Inventory value");
                
                // If it's not basic info, add it to the details
                if (!isBasicInfo) {
                    // Create a label with the property and its value, without space before colon
                    Label detailLabel = new Label(propertyName + ": " + propertyValue);
                    detailLabel.setFont(Font.font("Segoe UI", 14));
                    details.getChildren().add(detailLabel);
                }
            }
        }

        details.setPadding(new Insets(10, 0, 0, 0));

        // Stock management
        HBox stockBox = new HBox(10);
        TextField stockField = new TextField();
        stockField.setPromptText("Enter quantity");
        Button addStockBtn = new Button("Add Stock");
        Button deductStockBtn = new Button("Deduct Stock");
        
        // Disable stock management for discontinued products
        boolean isActive = p.isProductStatus();
        stockField.setDisable(!isActive);
        addStockBtn.setDisable(!isActive);
        deductStockBtn.setDisable(!isActive);
        
        // Add tooltip to explain why buttons are disabled
        if (!isActive) {
            Tooltip disabledTooltip = new Tooltip("Stock management is disabled for discontinued products");
            Tooltip.install(stockField, disabledTooltip);
            Tooltip.install(addStockBtn, disabledTooltip);
            Tooltip.install(deductStockBtn, disabledTooltip);
        }
        
        addStockBtn.setOnAction(e -> {
            try {
                int q = Integer.parseInt(stockField.getText());
                if (q < 0) {
                    showAlert("Please enter a non-negative quantity.");
                    return;
                }
                showAlert(p.addStock(q));
                refreshList();
                showProductDetails(p);
            } catch (Exception ex) { showAlert("Invalid quantity."); }
        });
        
        deductStockBtn.setOnAction(e -> {
            try {
                int q = Integer.parseInt(stockField.getText());
                if (q < 0) {
                    showAlert("Please enter a non-negative quantity.");
                    return;
                }
                showAlert(p.deductStock(q));
                refreshList();
                showProductDetails(p);
            } catch (Exception ex) { showAlert("Invalid quantity."); }
        });
        
        stockBox.getChildren().addAll(stockField, addStockBtn, deductStockBtn);

        // Discontinue button
        Button discontinueBtn = new Button("Discontinue Product");
        discontinueBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        discontinueBtn.setOnAction(e -> {
            p.setProductStatus(false);
            showAlert("Product discontinued.");
            refreshList();
            showProductDetails(p);
        });
        discontinueBtn.setDisable(!p.isProductStatus());

        box.getChildren().addAll(id, name, type, status, qty, price, value, details, stockBox, discontinueBtn);
        rightPanel.getChildren().add(box);
    }

    // --- Add Product Form ---
    private void showAddProductForm() {
        rightPanel.getChildren().clear();
        VBox form = new VBox(15);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #fff; -fx-background-radius: 12;");
        form.setAlignment(Pos.TOP_LEFT);
        form.setPrefWidth(400);

        Label title = new Label("Add New Product");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));

        // Type Selection
        Label typeLabel = new Label("Product Type:");
        typeLabel.setMinWidth(120);
        addTypeBox = new ComboBox<>();
        addTypeBox.setPromptText("Select product type");
        addTypeBox.getItems().addAll("Refrigerator", "TV", "Washing Machine", "SmartPhone");
        addTypeBox.setMaxWidth(Double.MAX_VALUE);
        HBox typeBox = new HBox(10);
        typeBox.getChildren().addAll(typeLabel, addTypeBox);
        HBox.setHgrow(addTypeBox, Priority.ALWAYS);

        // Basic Fields with HBox layout
        Label nameLabel = new Label("Product Name:");
        nameLabel.setMinWidth(120);
        addName = new TextField();
        addName.setPromptText("Enter letters and spaces only");
        HBox nameBox = new HBox(10);
        nameBox.getChildren().addAll(nameLabel, addName);
        HBox.setHgrow(addName, Priority.ALWAYS);

        Label itemNumberLabel = new Label("Item Number:");
        itemNumberLabel.setMinWidth(120);
        addItemNumber = new TextField();
        addItemNumber.setPromptText("Enter 4 digits (e.g., 0001)");
        // Add listener to format the number as 4 digits
        addItemNumber.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                addItemNumber.setText(newValue.replaceAll("[^\\d]", ""));
            } else if (!newValue.isEmpty()) {
                try {
                    int value = Integer.parseInt(newValue);
                    if (value > 0) {
                        addItemNumber.setText(String.format("%04d", value));
                    }
                } catch (NumberFormatException e) {
                    // Keep the old value if parsing fails
                    addItemNumber.setText(oldValue);
                }
            }
        });
        HBox itemNumberBox = new HBox(10);
        itemNumberBox.getChildren().addAll(itemNumberLabel, addItemNumber);
        HBox.setHgrow(addItemNumber, Priority.ALWAYS);

        Label priceLabel = new Label("Price (RM):");
        priceLabel.setMinWidth(120);
        addPrice = new TextField();
        addPrice.setPromptText("Enter positive number (e.g., 999.99)");
        HBox priceBox = new HBox(10);
        priceBox.getChildren().addAll(priceLabel, addPrice);
        HBox.setHgrow(addPrice, Priority.ALWAYS);

        Label qtyLabel = new Label("Quantity:");
        qtyLabel.setMinWidth(120);
        addQty = new TextField();
        addQty.setPromptText("Enter non-negative number (e.g., 10)");
        HBox qtyBox = new HBox(10);
        qtyBox.getChildren().addAll(qtyLabel, addQty);
        HBox.setHgrow(addQty, Priority.ALWAYS);

        // Dynamic Fields
        addF1 = new TextField();
        addF2 = new TextField();
        addF3 = new TextField();

        // Create radio buttons for Has Dryer
        RadioButton yesButton = new RadioButton("Yes");
        RadioButton noButton = new RadioButton("No");
        ToggleGroup hasDryerGroup = new ToggleGroup();
        yesButton.setToggleGroup(hasDryerGroup);
        noButton.setToggleGroup(hasDryerGroup);
        noButton.setSelected(true); // Default to No
        
        // Style the radio buttons
        HBox radioBox = new HBox(20, yesButton, noButton);
        radioBox.setAlignment(Pos.CENTER_LEFT);
        radioBox.setPadding(new Insets(5, 0, 5, 0));
        
        // Add tooltips to radio buttons
        yesButton.setTooltip(new Tooltip("Machine includes dryer functionality"));
        noButton.setTooltip(new Tooltip("Machine is washer only"));

        VBox fieldsBox = new VBox(10);
        fieldsBox.setVisible(false);

        addTypeBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            fieldsBox.getChildren().clear();
            if (newVal != null) {
                switch (newVal) {
                    case "Refrigerator":
                        addF1.setPromptText("Enter letters and spaces only");
                        addF2.setPromptText("Enter letters and spaces only");
                        addF3.setPromptText("Enter positive number (e.g., 200)");
                        
                        Label doorLabel = new Label("Door Design:");
                        doorLabel.setMinWidth(120);
                        HBox doorBox = new HBox(10, doorLabel, addF1);
                        HBox.setHgrow(addF1, Priority.ALWAYS);
                        
                        Label colorLabel = new Label("Color:");
                        colorLabel.setMinWidth(120);
                        HBox colorBox = new HBox(10, colorLabel, addF2);
                        HBox.setHgrow(addF2, Priority.ALWAYS);
                        
                        Label capacityLabel = new Label("Capacity (L):");
                        capacityLabel.setMinWidth(120);
                        HBox capacityBox = new HBox(10, capacityLabel, addF3);
                        HBox.setHgrow(addF3, Priority.ALWAYS);
                        
                        fieldsBox.getChildren().addAll(doorBox, colorBox, capacityBox);
                    break;
                case "TV":
                        addF1.setPromptText("Enter letters and spaces only");
                        addF2.setPromptText("Enter resolution (e.g., 1920x1080)");
                        addF3.setPromptText("Enter positive number (e.g., 55)");
                        
                        Label screenLabel = new Label("Screen Type:");
                        screenLabel.setMinWidth(120);
                        HBox screenBox = new HBox(10, screenLabel, addF1);
                        HBox.setHgrow(addF1, Priority.ALWAYS);
                        
                        Label resolutionLabel = new Label("Resolution:");
                        resolutionLabel.setMinWidth(120);
                        HBox resolutionBox = new HBox(10, resolutionLabel, addF2);
                        HBox.setHgrow(addF2, Priority.ALWAYS);
                        
                        Label sizeLabel = new Label("Display Size (inches):");
                        sizeLabel.setMinWidth(120);
                        HBox sizeBox = new HBox(10, sizeLabel, addF3);
                        HBox.setHgrow(addF3, Priority.ALWAYS);
                        
                        fieldsBox.getChildren().addAll(screenBox, resolutionBox, sizeBox);
                    break;
                case "Washing Machine":
                        addF1.setPromptText("Enter positive number (e.g., 8)");
                        addF2.setPromptText("Enter letters and spaces only");
                        
                        Label drumLabel = new Label("Drum Size (L):");
                        drumLabel.setMinWidth(120);
                        HBox drumBox = new HBox(10, drumLabel, addF1);
                        HBox.setHgrow(addF1, Priority.ALWAYS);
                        
                        Label typeFieldLabel = new Label("Machine Type:");
                        typeFieldLabel.setMinWidth(120);
                        HBox typeFieldBox = new HBox(10, typeFieldLabel, addF2);
                        HBox.setHgrow(addF2, Priority.ALWAYS);
                        
                        Label dryerLabel = new Label("Has Dryer:");
                        dryerLabel.setMinWidth(120);
                        HBox dryerBox = new HBox(10, dryerLabel, radioBox);
                        
                        fieldsBox.getChildren().addAll(drumBox, typeFieldBox, dryerBox);
                        break;
                    case "SmartPhone":
                        addF1.setPromptText("Enter letters and spaces only");
                        addF2.setPromptText("Enter model name/number");
                        addF3.setPromptText("Enter positive number (e.g., 5000)");
                        
                        Label brandLabel = new Label("Brand:");
                        brandLabel.setMinWidth(120);
                        HBox brandBox = new HBox(10, brandLabel, addF1);
                        HBox.setHgrow(addF1, Priority.ALWAYS);
                        
                        Label modelLabel = new Label("Model:");
                        modelLabel.setMinWidth(120);
                        HBox modelBox = new HBox(10, modelLabel, addF2);
                        HBox.setHgrow(addF2, Priority.ALWAYS);
                        
                        Label batteryLabel = new Label("Battery Capacity (mAh):");
                        batteryLabel.setMinWidth(120);
                        HBox batteryBox = new HBox(10, batteryLabel, addF3);
                        HBox.setHgrow(addF3, Priority.ALWAYS);
                        
                        fieldsBox.getChildren().addAll(brandBox, modelBox, batteryBox);
                    break;
                }
                fieldsBox.setVisible(true);
            } else {
                fieldsBox.setVisible(false);
            }
            // Clear fields when type changes
            addF1.clear();
            addF2.clear();
            addF3.clear();
            noButton.setSelected(true);
        });

        Button submit = new Button("Add Product");
        submit.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        submit.setMaxWidth(Double.MAX_VALUE);
        submit.setOnAction(e -> {
            // Store the radio button selection before handling the product
            if (addTypeBox.getValue() != null && addTypeBox.getValue().equals("Washing Machine")) {
                addF3.setText(yesButton.isSelected() ? "true" : "false");
            }
            handleAddProduct();
        });

        form.getChildren().addAll(
            title,
            typeBox,
            nameBox,
            itemNumberBox,
            priceBox,
            qtyBox,
            fieldsBox,
            submit
        );

        rightPanel.getChildren().add(form);
    }

    private void handleAddProduct() {
        if (stockManager.getProducts().size() >= maxProducts) {
            showAlert("Maximum number of products reached (" + maxProducts + ").");
            return;
        }

        StringBuilder errors = new StringBuilder("Please fix the following errors:\n\n");
        boolean hasErrors = false;

        // Validate Item Number (must be 4 digits)
        int itemNumber = 0;
        try {
            String itemNumberText = addItemNumber.getText().trim();
            if (!itemNumberText.matches("\\d{4}")) {
                errors.append("• Item number must be exactly 4 digits\n");
                hasErrors = true;
            } else {
                itemNumber = Integer.parseInt(itemNumberText);
                if (itemNumber <= 0) {
                    errors.append("• Item number must be a positive number\n");
                    hasErrors = true;
                }
                if (stockManager.isItemNumberUsed(itemNumber)) {
                    errors.append("• Item number is already in use\n");
                    hasErrors = true;
                }
            }
        } catch (NumberFormatException e) {
            errors.append("• Item number must be a positive number\n");
            hasErrors = true;
        }

        // Validate Product Name
        String name = addName.getText();
        if (!name.matches("[A-Za-z ]+")) {
            errors.append("• Product name must contain only letters and spaces\n");
            hasErrors = true;
        }

        // Validate Price
        try {
            double price = Double.parseDouble(addPrice.getText());
            if (price <= 0) {
                errors.append("• Price must be positive value\n");
                hasErrors = true;
            }
        } catch (NumberFormatException e) {
            errors.append("• Price must be a positive number\n");
            hasErrors = true;
        }

        // Validate Quantity
        try {
            int qty = Integer.parseInt(addQty.getText());
            if (qty < 0) {
                errors.append("• Quantity must be a non-negative number\n");
                hasErrors = true;
            }
        } catch (NumberFormatException e) {
            errors.append("• Quantity must be a non-negative number\n");
            hasErrors = true;
        }

        // Validate type-specific fields
        String type = addTypeBox.getValue();
        if (type == null) {
            errors.append("• Please select a product type\n");
            hasErrors = true;
        } else {
            switch (type) {
                  case "Refrigerator":
                    if (!addF1.getText().matches("[A-Za-z ]+")) {
                        errors.append("• Door design must contain only letters and spaces\n");
                        hasErrors = true;
                    }
                    if (!addF2.getText().matches("[A-Za-z ]+")) {
                        errors.append("• Color must contain only letters and spaces\n");
                        hasErrors = true;
                    }
                    try {
                        double capacity = Double.parseDouble(addF3.getText());
                        if (capacity <= 0) {
                            errors.append("• Capacity must be positive\n");
                            hasErrors = true;
                        }
                    } catch (NumberFormatException e) {
                        errors.append("• Capacity must be a positive number\n");
                        hasErrors = true;
                    }
                    break;

                  case "TV":
                    if (!addF1.getText().matches("[A-Za-z ]+")) {
                        errors.append("• Screen type must contain only letters and spaces\n");
                        hasErrors = true;
                    }
                    if (addF2.getText().trim().isEmpty()) {
                        errors.append("• Resolution cannot be empty\n");
                        hasErrors = true;
                    }
                    try {
                        double displaySize = Double.parseDouble(addF3.getText());
                        if (displaySize <= 0) {
                            errors.append("• Display size must be a positive number\n");
                            hasErrors = true;
                        }
                    } catch (NumberFormatException e) {
                        errors.append("• Display size must be a positive number\n");
                        hasErrors = true;
                    }
                    break;

                  case "Washing Machine":
                    try {
                        int drumSize = Integer.parseInt(addF1.getText());
                        if (drumSize <= 0) {
                            errors.append("• Drum size must be positive\n");
                            hasErrors = true;
                        }
                    } catch (NumberFormatException e) {
                        errors.append("• Drum size must be a positive number\n");
                        hasErrors = true;
                    }
                    if (!addF2.getText().matches("[A-Za-z ]+")) {
                        errors.append("• Machine type must contain only letters and spaces\n");
                        hasErrors = true;
                    }
                    if (!addF3.getText().toLowerCase().matches("true|false")) {
                        errors.append("• Has Dryer must be either 'true' or 'false'\n");
                        hasErrors = true;
                    }
                    break;

                case "SmartPhone":
                    if (!addF1.getText().matches("[A-Za-z ]+")) {
                        errors.append("• Brand must contain only letters and spaces\n");
                        hasErrors = true;
                    }
                    if (addF2.getText().trim().isEmpty()) {
                        errors.append("• Model cannot be empty\n");
                        hasErrors = true;
                    }
                    try {
                        int batteryCapacity = Integer.parseInt(addF3.getText());
                        if (batteryCapacity <= 0) {
                            errors.append("• Battery capacity must be a positive number\n");
                            hasErrors = true;
                        }
                    } catch (NumberFormatException e) {
                        errors.append("• Battery capacity must be a positive number\n");
                        hasErrors = true;
                    }
                    break;
            }
        }

        // If there are any errors, show them all at once
        if (hasErrors) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText("Invalid Input");
            alert.setContentText(errors.toString());
            alert.showAndWait();
                return;
            }

        // If no errors, proceed with adding the product
        try {
            itemNumber = Integer.parseInt(addItemNumber.getText());
            String productName = addName.getText();
            int quantity = Integer.parseInt(addQty.getText());
            double price = Double.parseDouble(addPrice.getText());

            switch (type) {
                  case "Refrigerator":
                    stockManager.addRefrigerator(itemNumber, productName, quantity, price,
                        addF1.getText(), addF2.getText(), Double.parseDouble(addF3.getText()));
                    break;
                  case "TV":
                    stockManager.addTV(itemNumber, productName, quantity, price,
                        addF1.getText(), addF2.getText(), Double.parseDouble(addF3.getText()));
                    break;
                  case "Washing Machine":
                    stockManager.addWashingMachine(itemNumber, productName, quantity, price,
                        Integer.parseInt(addF1.getText()), addF2.getText(), Boolean.parseBoolean(addF3.getText()));
                    break;
                case "SmartPhone":
                    stockManager.addSmartPhone(itemNumber, productName, quantity, price,
                        addF1.getText(), addF2.getText(), Integer.parseInt(addF3.getText()));
                    break;
            }
            
            showAlert("Product added successfully!");
            showWelcomePanel();
            updateMaxProductsLabel();
            refreshList();
            
        } catch (Exception e) {
            showAlert("An unexpected error occurred. Please check your input.");
        }
    }

    // --- Product List Cell ---
    private static class ProductListCell extends ListCell<Product> {
        @Override
        protected void updateItem(Product item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                HBox box = new HBox(10);
                box.setAlignment(Pos.CENTER_LEFT);
                
                // Create status indicator as a small circle
                Label status = new Label("•");
                status.setFont(Font.font("System", 14));
                status.setStyle("-fx-cursor: hand;");
                status.setTextFill(item.isProductStatus() ? Color.web("#2ecc71") : Color.web("#e74c3c"));
                
                // Add tooltip to show status
                Tooltip tooltip = new Tooltip(item.isProductStatus() ? "Active" : "Discontinued");
                Tooltip.install(status, tooltip);
                
                // Allow clicking to toggle status
                status.setOnMouseClicked(e -> {
                    item.setProductStatus(!item.isProductStatus());
                    updateItem(item, false);
                });
                
                // Format ID with 4 digits and show it first
                Label id = new Label(String.format("%04d", item.getItemNumber()));
                id.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
                
                Label name = new Label(item.getProductName());
                name.setFont(Font.font("Segoe UI", 13));
                
                Label type = new Label(getTypeString(item));
                type.setFont(Font.font("Segoe UI", 13));
                
                Label qty = new Label("Qty: " + item.getQuantityAvailable());
                qty.setFont(Font.font("Segoe UI", 13));
                
                box.getChildren().addAll(status, id, name, type, qty);
                setGraphic(box);
            }
        }
    }

    private static String getTypeString(Product p) {
        if (p instanceof Refrigerator) return "Refrigerator";
        if (p instanceof TV) return "TV";
        if (p instanceof WashingMachine) return "Washing Machine";
        if (p instanceof SmartPhone) return "SmartPhone";
        return "Unknown";
    }

    private void refreshList() {
        productList.setAll(stockManager.getProducts());
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // Validation utility methods
    private String getValidatedString(String input, Label errorLabel) {
        if (input == null || input.trim().isEmpty()) {
            errorLabel.setText("Input cannot be empty.");
            errorLabel.setVisible(true);
            return "";
        }
        if (!input.matches("[A-Za-z ]+")) {
            errorLabel.setText("Invalid input. Letters and spaces only.");
            errorLabel.setVisible(true);
            return "";
        }
        errorLabel.setVisible(false);
        return input.trim();
    }

    private String getValidatedSurname(String input, Label errorLabel) {
        if (input == null || input.trim().isEmpty()) {
            errorLabel.setText("Input cannot be empty.");
            errorLabel.setVisible(true);
            return "";
        }
        if (!input.matches("[A-Za-z '/]+")) {
            errorLabel.setText("Invalid surname. Letters, spaces, / and ' only.");
            errorLabel.setVisible(true);
            return "";
        }
        errorLabel.setVisible(false);
        return input.trim();
    }

    // --- User Info Dialog ---
    private void showUserInfoDialog() {
        Dialog<UserInfo> dialog = new Dialog<>();
        dialog.setTitle("User Info");
        dialog.setHeaderText("Enter your first name and surname:");
        
        TextField firstName = new TextField();
        firstName.setPromptText("First Name");
        
        TextField surName = new TextField();
        surName.setPromptText("Surname");
        
        Label firstNameError = new Label();
        firstNameError.setTextFill(Color.RED);
        firstNameError.setVisible(false);
        
        Label surNameError = new Label();
        surNameError.setTextFill(Color.RED);
        surNameError.setVisible(false);
        
        VBox vbox = new VBox(5);
        vbox.getChildren().addAll(firstName, firstNameError, surName, surNameError);
        vbox.setPadding(new Insets(10));
        
        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        
        // Add validation on text change
        firstName.textProperty().addListener((observable, oldValue, newValue) -> {
            getValidatedString(newValue, firstNameError);
            updateOkButton(okButton, firstName.getText(), surName.getText(), firstNameError, surNameError);
        });
        
        surName.textProperty().addListener((observable, oldValue, newValue) -> {
            getValidatedSurname(newValue, surNameError);
            updateOkButton(okButton, firstName.getText(), surName.getText(), firstNameError, surNameError);
        });
        
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                String validatedFirstName = getValidatedString(firstName.getText(), firstNameError);
                String validatedSurname = getValidatedSurname(surName.getText(), surNameError);
                
                if (!validatedFirstName.isEmpty() && !validatedSurname.isEmpty()) {
                    return new UserInfo(validatedFirstName, validatedSurname);
                }
            }
            return null;
        });
        
        Optional<UserInfo> result = dialog.showAndWait();
        if (result.isPresent()) {
            currentUser = result.get();
            showMaxProductsDialog();
        } else {
            Platform.exit();
        }
    }
    
    private void updateOkButton(Button okButton, String firstName, String surName, Label firstNameError, Label surNameError) {
        boolean isFirstNameValid = !getValidatedString(firstName, firstNameError).isEmpty();
        boolean isSurNameValid = !getValidatedSurname(surName, surNameError).isEmpty();
        okButton.setDisable(!(isFirstNameValid && isSurNameValid));
    }

    private void showMaxProductsDialog() {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Product Limit");
        dialog.setHeaderText("How many products do you want to add?");
        
        TextField maxProductsField = new TextField();
        maxProductsField.setPromptText("Enter number of products (0 to exit)");
        
        VBox vbox = new VBox(10, maxProductsField);	
        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    int value = Integer.parseInt(maxProductsField.getText());
                    if (value >= 0) return value;
                } catch (NumberFormatException e) {}
            }
            return null;
        });
        
        Optional<Integer> result = dialog.showAndWait();
        if (result.isPresent()) {
            maxProducts = result.get();
            if (maxProducts == 0) {
                Platform.exit();
            }
            showMainWindow();
        } else {
            Platform.exit();
        }
    }

    private void showMainWindow() {
        Stage primaryStage = new Stage();
        primaryStage.setTitle(APP_TITLE);
        
        rootLayout = new BorderPane();
        rootLayout.setTop(createHeader());

        // Create a VBox to hold the main content and exit button
        VBox mainContainer = new VBox();
        mainContainer.setAlignment(Pos.CENTER);
        VBox.setVgrow(mainContainer, Priority.ALWAYS);

        // Add the main content to the container
        BorderPane mainContent = createMainContent();
        VBox.setVgrow(mainContent, Priority.ALWAYS);
        mainContainer.getChildren().add(mainContent);

        // Create exit button with container
        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        exitButton.setPrefWidth(100);
        exitButton.setOnAction(e -> showExitDialog(primaryStage));

        // Create bottom pane for exit button
        HBox bottomPane = new HBox();
        bottomPane.setPadding(new Insets(10, 20, 10, 10));
        bottomPane.setAlignment(Pos.CENTER_RIGHT);
        bottomPane.setStyle("-fx-background-color: #f5f6fa;");
        bottomPane.getChildren().add(exitButton);

        // Add bottom pane to main container
        mainContainer.getChildren().add(bottomPane);

        rootLayout.setCenter(mainContainer);

        Scene scene = new Scene(rootLayout, 1100, 800);
        
        // Set minimum window size to ensure exit button is always visible
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showExitDialog(Stage primaryStage) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Exit Application");
        dialog.setHeaderText("Thank you for using Stock Management System!");
        dialog.initOwner(primaryStage);
        
        // Create custom content
        VBox content = new VBox(15);  // Increased spacing
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);
        content.setMinWidth(400);  // Set minimum width
        content.setStyle("-fx-background-color: white;");
        
        Label messageLabel = new Label("Have a great day!");
        messageLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        messageLabel.setTextFill(Color.web("#2c3e50"));
        
        // Format user info with ID - using %s for both name and ID as they're both strings
        Label userLabel = new Label(String.format("User: %s (ID: %s)", 
            currentUser.getCompleteName(), 
            currentUser.getUserId()));
        userLabel.setFont(Font.font("Segoe UI", 14));
        
        Label timeLabel = new Label("Session End: " + DATE_TIME_FORMATTER.format(LocalDateTime.now()));
        timeLabel.setFont(Font.font("Segoe UI", 14));
        
        content.getChildren().addAll(messageLabel, userLabel, timeLabel);
        
        // Style the dialog pane
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPadding(new Insets(10));
        dialog.getDialogPane().setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-width: 1;");
        
        // Add buttons
        ButtonType exitButtonType = new ButtonType("Exit", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(exitButtonType, cancelButtonType);

        // Style the buttons
        Button exitBtn = (Button) dialog.getDialogPane().lookupButton(exitButtonType);
        exitBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15;");
        
        Button cancelBtn = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelBtn.setStyle("-fx-padding: 8 15; -fx-font-weight: bold;");
        
        // Show dialog and handle result
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == exitButtonType) {
            Platform.exit();
        }
    }
}
