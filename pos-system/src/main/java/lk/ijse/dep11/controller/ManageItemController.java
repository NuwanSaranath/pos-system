package lk.ijse.dep11.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.dep11.db.OrderDataAccess;
import lk.ijse.dep11.tm.Item;
import javafx.stage.Stage;
import lk.ijse.dep11.db.ItemDataAccess;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ManageItemController {
    public AnchorPane root;
    public Button btnNewItem;
    public TextField txtItemCode;
    public TextField txtItemDescription;
    public TextField txtQty;
    public Button btnSave;
    public Button btnDelete;
    public ImageView imgHome;
    public TableView<Item> tblItems;
    public TextField txtUnitPrice;

    public void initialize(){
        tblItems.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("code"));
        tblItems.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblItems.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("qty"));
        tblItems.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        btnDelete.setDisable(true);
        btnSave.setDefaultButton(true);
        try {
            tblItems.getItems().addAll(ItemDataAccess.getAllItems());
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load items, try later").show();
            e.printStackTrace();
        }
        Platform.runLater(txtItemCode::requestFocus);
        tblItems.getSelectionModel().selectedItemProperty().addListener((ov, prev, cur)->{
            if (cur == null){
                btnSave.setText("SAVE");
                btnDelete.setDisable(true);
                txtItemCode.setDisable(false);
            }else{
                btnSave.setText("UPDATE");
                btnDelete.setDisable(false);
                txtItemCode.setText(cur.getCode());
                txtItemCode.setDisable(true);
                txtItemDescription.setText(cur.getDescription());
                txtQty.setText(cur.getQty()  + "");
                txtUnitPrice.setText(cur.getUnitPrice() + "");
            }
        });
    }

    public void btnSaveOnAction(ActionEvent actionEvent) {
        if (!isDataValid()) return;

        try {
            Item item = new Item(txtItemCode.getText().strip(), txtItemDescription.getText().strip(),
                    Integer.parseInt(txtQty.getText()), new BigDecimal(txtUnitPrice.getText()).setScale(2));

            if (btnSave.getText().equals("SAVE")) {
                if (ItemDataAccess.existsItem(item.getCode())) {
                    new Alert(Alert.AlertType.ERROR, "Item code already exists").show();
                    txtItemCode.requestFocus();
                    txtItemCode.selectAll();
                    return;
                }
                ItemDataAccess.saveItem(item);
                tblItems.getItems().add(item);
            }else{
                ItemDataAccess.updateItem(item);
                ObservableList<Item> itemList = tblItems.getItems();
                Item selectedItem = tblItems.getSelectionModel().getSelectedItem();
                itemList.set(itemList.indexOf(selectedItem), item);
                tblItems.refresh();
            }
            btnNewItem.fire();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to save the item, try again").show();
        }
    }
    private boolean isDataValid(){
        String code = txtItemCode.getText().strip();
        String description = txtItemDescription.getText().strip();
        String qty = txtQty.getText().strip();
        String unitPrice = txtUnitPrice.getText().strip();

        if (!code.matches("\\d{4,}")){
            txtItemCode.requestFocus();
            txtItemCode.selectAll();
            return false;
        }else if (!description.matches("[A-Za-z0-9 ]{4,}")){
            txtItemDescription.requestFocus();
            txtItemDescription.selectAll();
            return false;
        }else if(!qty.matches("\\d+") || Integer.parseInt(qty) <= 0){
            txtQty.requestFocus();
            txtQty.selectAll();
            return false;
        }else if(!isPrice(unitPrice)){
            txtUnitPrice.requestFocus();
            txtUnitPrice.selectAll();
            return false;
        }

        return true;
    }
    private boolean isPrice(String input){
        try {
            double price = Double.parseDouble(input);
            return price > 0;
        } catch (NumberFormatException e) {
            return false;
        }
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

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        Item selectedItem = tblItems.getSelectionModel().getSelectedItem();
        try {
            if (OrderDataAccess.existsOrderByItemCode(selectedItem.getCode())){
                new Alert(Alert.AlertType.ERROR, "Failed to delete, the item already associated with an order").show();
            }else{
                ItemDataAccess.deleteItem(selectedItem.getCode());
                tblItems.getItems().remove(selectedItem);
                if (tblItems.getItems().isEmpty()) btnNewItem.fire();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to delete the item, try again").show();
        }
    }


    public void btnNewItemOnAction(ActionEvent actionEvent) {
        for (TextField textField : new TextField[]{txtItemCode, txtItemDescription, txtQty, txtUnitPrice})
            textField.clear();
        txtItemCode.requestFocus();
        tblItems.getSelectionModel().clearSelection();
    }
}
