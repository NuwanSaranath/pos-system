package lk.ijse.dep11.controller;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
public class MainFormController {
    public AnchorPane root;
    public ImageView imgItem;
    public ImageView imgOrder;
    public ImageView imgSearch;
    public ImageView imgCustomer;
    public Label lblDescription;
    public Label lblGreeting;

    public void playMouseEnterAnimation(MouseEvent mouseEvent) {
        if(mouseEvent.getSource() instanceof ImageView){
            ImageView image = (ImageView)mouseEvent.getSource();
            String imageId = image.getId();
            if(imageId.equals("imgItem")){
                lblGreeting.setText("Manege Items");
                lblDescription.setText("Click to add, edit, delete, search or view items");
            }else if(imageId.equals("imgOrder")){
                lblGreeting.setText("Place Orders");
                lblDescription.setText("Click here if you want to place a new order");
            }else if(imageId.equals("imgSearch")){
                lblGreeting.setText("Search Orders");
                lblDescription.setText("Click if you want to search orders");
            }else{
                lblGreeting.setText("Manage Customers ");
                lblDescription.setText("Click to add, edit, delete, search or view customers");
            }
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), image);
            scaleTransition.setToY(1.3);
            scaleTransition.setToX(1.3);
            scaleTransition.play();
            DropShadow dropShadow = new DropShadow();
            dropShadow.setColor(Color.rgb(47,131,240));
            dropShadow.setRadius(30);
            image.setEffect(dropShadow);
        }
    }
    public void playMouseExitAnimation(MouseEvent mouseEvent) {
        if(mouseEvent.getSource() instanceof ImageView){
            lblGreeting.setText("Welcome");
            lblDescription.setText("Please select one of above main operations to proceed");
            ImageView image = (ImageView)mouseEvent.getSource();
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), image);
            scaleTransition.setToY(1);
            scaleTransition.setToX(1);
            scaleTransition.play();
            image.setEffect(null);
        }
    }

    public void imageOnMouseClick(MouseEvent event) throws IOException {
        if (event.getSource() instanceof ImageView) {
            AnchorPane root=null;
            ImageView image = (ImageView) event.getSource();
            String imageId = image.getId();
            if (imageId.equals("imgItem")) {
                root= FXMLLoader.load(getClass().getResource("/view/ManageItem.fxml"));
            } else if (imageId.equals("imgOrder")) {
                root= FXMLLoader.load(getClass().getResource("/view/PlaceOrder.fxml"));
            } else if (imageId.equals("imgSearch")) {
                root= FXMLLoader.load(getClass().getResource("/view/SearchOrder.fxml"));
            } else if (imageId.equals("imgCustomer")) {
                root= FXMLLoader.load(getClass().getResource("/view/ManageCustomer.fxml"));
            }
            if(root!=null){
                Scene subScene = new Scene(root);
                Stage stage = (Stage) this.root.getScene().getWindow();
                stage.setScene(subScene);
                String title = null;
                switch (imageId){
                    case "imgItem":
                        title="Manage Item";
                        break;
                    case "imgOrder":
                        title="Place Order";
                        break;
                    case "imgCustomer":
                        title="Manage Customer";
                        break;
                    case "imgSearch":
                        title="Search Order";
                        break;

                }
                stage.setTitle(title);
                stage.centerOnScreen();
                stage.show();
            }

        }
    }
}
