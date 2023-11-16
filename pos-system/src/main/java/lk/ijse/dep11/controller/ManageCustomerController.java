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

    public void navigateToHome(MouseEvent mouseEvent) throws IOException {
    }

    public void initialize(){


    }
    public void btnNewCustomerOnAction(ActionEvent actionEvent) {

    }

    public void btnSaveOnAction(ActionEvent actionEvent) {

    }
    public void btnDeleteOnAction(ActionEvent actionEvent) {

    }
}
