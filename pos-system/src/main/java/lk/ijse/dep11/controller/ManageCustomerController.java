package lk.ijse.dep11.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class ManageCustomerController {
    public AnchorPane root;
    public Button btnNewCustomer;
    public TextField txtId;
    public TextField txtName;
    public Button btnSave;
    public TextField txtAddress;
    public Button btnDelete;
    public ImageView imgHome;
    public TableView tblCustomer;

    public void initialize(){
        btnSave.setDisable(true);
        btnDelete.setDisable(true);
        btnNewCustomer.fire();



    }

    public void btnNewCustomerOnAction(ActionEvent actionEvent) {
    }

    public void btnSaveOnAction(ActionEvent actionEvent) {
    }
    public void btnDeleteOnAction(ActionEvent actionEvent) {
    }
}
