package scottyapp.Main;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.io.IOException;

public class MainController {

    @FXML private ComboBox<String> menuOpcionComboBox;
    @FXML private Label seleccionLabel;

    @FXML
    public void initialize() {
        menuOpcionComboBox.getItems().addAll("Usuario", "Productos", "Pedido", "DetallesPedido");
    }

    @FXML
    public void irButton() {
        String select = menuOpcionComboBox.getSelectionModel().getSelectedItem();
        if (select != null) {
            seleccionLabel.setVisible(false);
            try {
                switch (select) {
                    case "Usuario":
                        MainApplication.setRoot("usuario-view");
                        break;
                    case "Productos":
                        MainApplication.setRoot("item-view");
                        break;
                    case "Pedido":
                        MainApplication.setRoot("order-view");
                        break;
                    case "DetallesPedido":
                        MainApplication.setRoot("orderDetails-view");
                        break;
                }
            } catch (IOException e) {
                System.out.println("Error al cargar la vista: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            seleccionLabel.setVisible(true);
        }
    }
}