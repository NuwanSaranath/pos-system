package lk.ijse.dep11.controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.dep11.db.CustomerDataAccess;
import lk.ijse.dep11.db.ItemDataAccess;
import lk.ijse.dep11.db.OrderDataAccess;
import lk.ijse.dep11.tm.Customer;
import lk.ijse.dep11.tm.Item;
import lk.ijse.dep11.tm.OrderItem;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PlaceOrderController {
    public AnchorPane root;
    public ImageView imgHome;
    public Button btnAdd;
    public TableView<OrderItem> tblOrder;
    public TextField txtItemDescription;
    public TextField txtCustomerName;
    public TextField qty;
    public TextField unitPrice;
    public TextField txtQtyOnHand;
    public Label lbtTotal;
    public Button btnPlaceOrder;
    public Label lblDate;
    public Label lblOrderId;
    public ComboBox<Customer> cmbCustomerId;
    public ComboBox<Item> cmbItemCode;
    public Label lblTotal;

    public void initialize() throws IOException {

        String[] cols = {"code", "description", "qty", "unitPrice", "total", "btnDelete"};
        for (int i = 0; i < cols.length; i++) {
            tblOrder.getColumns().get(i).setCellValueFactory(new PropertyValueFactory<>(cols[i]));
        }

        lblDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        newOrder();
        cmbCustomerId.getSelectionModel().selectedItemProperty().addListener((ov, prev, cur) -> {
            enablePlaceOrderButton();
            if (cur != null) {
                txtCustomerName.setText(cur.getName());
                txtCustomerName.setDisable(false);
                txtCustomerName.setEditable(false);
            } else {
                txtCustomerName.clear();
                txtCustomerName.setDisable(true);
            }
        });
        cmbItemCode.getSelectionModel().selectedItemProperty().addListener((ov, prev, cur) -> {
            if (cur != null) {
                txtItemDescription.setText(cur.getDescription());
                txtQtyOnHand.setText(cur.getQty() + "");
                unitPrice.setText(cur.getUnitPrice().toString());

                for (TextField txt : new TextField[]{txtItemDescription, txtQtyOnHand, unitPrice}) {
                    txt.setDisable(false);
                    txt.setEditable(false);
                }
                qty.setDisable(cur.getQty() == 0);
            } else {
                for (TextField txt : new TextField[]{txtItemDescription, txtQtyOnHand, unitPrice, qty}) {
                    txt.setDisable(true);
                    txt.clear();
                }
            }
        });
        qty.textProperty().addListener((ov, prevQty, curQty) -> {
            Item selectedItem = cmbItemCode.getSelectionModel().getSelectedItem();
//            btnSave.setDisable(true);
//            if (cur.matches("\\d+")){
//                if (Integer.parseInt(cur) <= selectedItem.getQty() && Integer.parseInt(cur) > 0){
//                    btnSave.setDisable(false);
//                }
//            }
            btnAdd.setDisable(!(curQty.matches("\\d+") && Integer.parseInt(curQty) <= selectedItem.getQty()
                    && Integer.parseInt(curQty) > 0));
        });
    }
    public void imgHomeOnMousePressed(MouseEvent mouseEvent) throws IOException {
        URL resource = this.getClass().getResource("/view/MainForm.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) (this.root.getScene().getWindow());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        Platform.runLater(primaryStage::sizeToScene);
    }

    public void btnPlaceOrderOnAction(ActionEvent actionEvent) {
        try {
            OrderDataAccess.saveOrder(lblOrderId.getText().replace("Order ID: ", "").strip(),
                    Date.valueOf(lblDate.getText()),
                    cmbCustomerId.getValue().getId(),
                    tblOrder.getItems());
            printBill();
            newOrder();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to save the order, try again").show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void newOrder() throws IOException {
        for (TextField txt : new TextField[]{txtCustomerName, txtItemDescription, qty, txtQtyOnHand, unitPrice}) {
            txt.clear();
            txt.setDisable(true);
        }
        tblOrder.getItems().clear();
        lblTotal.setText("TOTAL: Rs. 0.00");
        btnAdd.setDisable(true);
        btnPlaceOrder.setDisable(true);
        cmbCustomerId.getSelectionModel().clearSelection();
        cmbItemCode.getSelectionModel().clearSelection();
        try {
            cmbCustomerId.getItems().clear();
            cmbCustomerId.getItems().addAll(CustomerDataAccess.getAllCustomers());
            cmbItemCode.getItems().clear();
            cmbItemCode.getItems().addAll(ItemDataAccess.getAllItems());
            String lastOrderId = OrderDataAccess.getLastOrderId();
            if (lastOrderId == null){
                lblOrderId.setText("Order ID: OD001");
            }else{
                int newOrderId = Integer.parseInt(lastOrderId.substring(2)) + 1;
                lblOrderId.setText(String.format("Order ID: OD%03d", newOrderId));
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to establish database connection, try later").show();
            e.printStackTrace();

        }
        Platform.runLater(cmbCustomerId::requestFocus);
    }


    private void calculateOrderTotal() {
        Optional<BigDecimal> orderTotal = tblOrder.getItems().stream()
                .map(oi -> oi.getTotal())
                .reduce((prev, cur) -> prev.add(cur));
        lblTotal.setText("Total: Rs. " + orderTotal.orElseGet(()->BigDecimal.ZERO).setScale(2));
    }
    private void enablePlaceOrderButton(){
        Customer selectedCustomer = cmbCustomerId.getSelectionModel().getSelectedItem();
        btnPlaceOrder.setDisable(!(selectedCustomer != null && !tblOrder.getItems().isEmpty()));
    }

    public void btnAddOnAction(ActionEvent actionEvent) {
        Item selectedItem = cmbItemCode.getSelectionModel().getSelectedItem();
        Optional<OrderItem> optOrderItem = tblOrder.getItems().stream()
                .filter(item -> selectedItem.getCode().equals(item.getCode())).findFirst();

        if (optOrderItem.isEmpty()) {
            JFXButton btnDelete = new JFXButton("Delete");
            OrderItem newOrderItem = new OrderItem(selectedItem.getCode(), selectedItem.getDescription(),
                    Integer.parseInt(qty.getText()), selectedItem.getUnitPrice(),btnDelete);
            tblOrder.getItems().add(newOrderItem);
            btnDelete.setOnAction(e -> {
                tblOrder.getItems().remove(newOrderItem);
                selectedItem.setQty(selectedItem.getQty() + newOrderItem.getQty());
                calculateOrderTotal();
                enablePlaceOrderButton();
            });
            selectedItem.setQty(selectedItem.getQty() - newOrderItem.getQty());
        } else {
            OrderItem orderItem = optOrderItem.get();
            orderItem.setQty(orderItem.getQty() + Integer.parseInt(qty.getText()));
            tblOrder.refresh();
            selectedItem.setQty(selectedItem.getQty() - Integer.parseInt(qty.getText()));
        }
        cmbItemCode.getSelectionModel().clearSelection();
        cmbItemCode.requestFocus();
        calculateOrderTotal();
        enablePlaceOrderButton();
    }
    private void printBill(){
        try {
            JasperDesign jasperDesign = JRXmlLoader
                    .load(getClass().getResourceAsStream("/print/pos-bill.jrxml"));

            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

            Map<String, Object> reportParams = new HashMap<>();
            reportParams.put("id", lblOrderId.getText().replace("Order ID: ", "").strip());
            reportParams.put("date", lblDate.getText());
            reportParams.put("customer-id", cmbCustomerId.getValue().getId());
            reportParams.put("customer-name", cmbCustomerId.getValue().getName());
            reportParams.put("total", lblTotal.getText());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, reportParams,
                    new JRBeanCollectionDataSource(tblOrder.getItems()));

            JasperViewer.viewReport(jasperPrint, false);
            // JasperPrintManager.printReport(jasperPrint, false);
        } catch (JRException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to print the bill").show();
        }
    }
}
