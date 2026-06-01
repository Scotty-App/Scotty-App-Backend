package scottyapp.Main;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.io.IOException;

public class MainController {

    @FXML private ComboBox<String> menuOpcionComboBox;
    @FXML private Label seleccionLabel;

    // carga las opciones del menu principal
    @FXML
    public void initialize() {
        menuOpcionComboBox.getItems().addAll("Usuario", "Productos", "Pedido");
    }

    // navega a la pantalla seleccionada en el combo
    @FXML
    public void irButton() {
        String opcionElegida = menuOpcionComboBox.getSelectionModel().getSelectedItem();
        if (opcionElegida == null) {
            seleccionLabel.setVisible(true);
            return;
        }
        seleccionLabel.setVisible(false);
        try {
            switch (opcionElegida) {
                case "Usuario":
                    MainApplication.setRoot("usuario-view");
                    break;
                case "Productos":
                    MainApplication.setRoot("item-view");
                    break;
                case "Pedido":
                    MainApplication.setRoot("order-view");
                    break;
            }
        } catch (IOException excepcion) {
            System.out.println("Error al cargar la vista: " + excepcion.getMessage());
            excepcion.printStackTrace();
        }
    }
}