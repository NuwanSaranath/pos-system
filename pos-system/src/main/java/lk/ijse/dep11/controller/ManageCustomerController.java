package lk.ijse.dep11.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
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
import javafx.stage.Stage;
import lk.ijse.dep11.db.CustomerDataAccess;
import lk.ijse.dep11.tm.Customer;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

public class ManageCustomerController {
    public AnchorPane root;
    public Button btnNewCustomer;
    public TextField txtId;
    public TextField txtName;
    public Button btnSave;
    public TextField txtAddress;
    public Button btnDelete;
    public ImageView imgHome;
    public TableView<Customer> tblCustomer;
    public TextField txtPhoneNumber;

    public void imgHomeOnMousePressed(MouseEvent mouseEvent) throws IOException {
        URL resource = this.getClass().getResource("/view/MainForm.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) (this.root.getScene().getWindow());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        Platform.runLater(primaryStage::sizeToScene);
    }

    public void initialize(){
        tblCustomer.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblCustomer.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblCustomer.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));
        tblCustomer.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        txtId.setEditable(false);
        btnDelete.setDisable(true);
        btnNewCustomer.fire();

        try {
            tblCustomer.getItems().addAll(CustomerDataAccess.getAllCustomers());
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,"Failed to load customer");
        }

        tblCustomer.getSelectionModel().selectedItemProperty().addListener((o,prev,cur)->{
            if(cur!=null){
                btnSave.setText("UPDATE");
                btnDelete.setDisable(false);
                txtId.setText(cur.getId());
                txtName.setText(cur.getName());
                txtAddress.setText(cur.getAddress());
            }else{
                btnSave.setText("SAVE");
                btnDelete.setDisable(true);
            }
        });


    }

    public void btnNewCustomerOnAction(ActionEvent actionEvent) {
        for (TextField textField : new TextField[]{txtId, txtName, txtAddress})
            textField.clear();
        tblCustomer.getSelectionModel().clearSelection();
        txtName.requestFocus();
        try {
            String lastCustomerId = CustomerDataAccess.getLastCustomerId();
            if(lastCustomerId==null){
                txtId.setText("C-001");
            }else{
                System.out.println(Integer.parseInt(lastCustomerId.substring(2)));
                int newId = Integer.parseInt(lastCustomerId.substring(2))+1;
                txtId.setText(String.format("C-%03d",newId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,"Failed to establish database connection,try again.");
        }
    }

    public void btnSaveOnAction(ActionEvent actionEvent) {
        if(!isDataValidate()) return;
        Customer customer = new Customer(txtId.getText(),txtName.getText(),txtAddress.getText(),txtPhoneNumber.getText());
        try {
            if(btnSave.getText().equals("SAVE")){
                CustomerDataAccess.saveCustomer(customer);

                tblCustomer.getItems().add(customer);

            }else{
                System.out.println(btnSave.getText());
                CustomerDataAccess.updateCustomer(customer);
                ObservableList<Customer> customerList = tblCustomer.getItems();
                Customer selectedCustomer = tblCustomer.getSelectionModel().getSelectedItem();
                System.out.println(customerList.indexOf(selectedCustomer)+"   index");
                customerList.set(customerList.indexOf(selectedCustomer),customer);

                tblCustomer.refresh();
            }
            btnNewCustomer.fire();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,"Failed to save the customer,try again").show();
        }

    }
    private  boolean isDataValidate(){
        String name = txtName.getText().strip();
        String address = txtAddress.getText().strip();
        if(!name.matches("[A-Za-z]{2,}")){
            txtName.requestFocus();
            txtName.selectAll();
            return false;
        } else if  (address.length()<3){
            txtAddress.requestFocus();
            txtAddress.selectAll();
            return false;
        }
        return true;



    }
    public void btnDeleteOnAction(ActionEvent actionEvent) {


    }
}
