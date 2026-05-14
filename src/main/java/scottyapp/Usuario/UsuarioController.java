package scottyapp.Usuario;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.sql.Connection;

public class UsuarioController {

    static Connection db;

    @FXML public TableView<Usuario> usuarioTableView;
    @FXML public TableColumn<Usuario, Integer> idUserColumn;
    @FXML public TableColumn<Usuario, String> nameColumn;
    @FXML public TableColumn<Usuario, String> emailColumn;
    @FXML public TableColumn<Usuario, String> passwordColumn;
    @FXML public TableColumn<Usuario, String> roleColumn;
    @FXML public TableColumn<Usuario, String> addressColumn;
    @FXML public TableColumn<Usuario, String> phoneColumn;

    @FXML public TextField idTextField;
    @FXML public TextField nameTextField;
    @FXML public TextField emailTextField;
    @FXML public TextField passwordTextField;
    @FXML public TextField addressTextField;
    @FXML public TextField phoneTextField;
    @FXML public TextField buscarTextField;

    @FXML public SplitMenuButton roleMenuButton;
    @FXML public Label mensajeLabel;
    @FXML public Button guardarButton;

    @FXML
    public void initialize() {
        db = MantenimientoUsuario.conexion();

        idUserColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleIntegerProperty(datos.getValue().getIdUser()).asObject());
        nameColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleStringProperty(datos.getValue().getName()));
        emailColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleStringProperty(datos.getValue().getEmail()));
        passwordColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleStringProperty(datos.getValue().getPassword()));
        roleColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleStringProperty(datos.getValue().getRole()));
        addressColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleStringProperty(datos.getValue().getAddress()));
        phoneColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleStringProperty(datos.getValue().getPhone()));

        guardarButton.setDisable(true);
        usuarioTableView.setItems(MantenimientoUsuario.consulta(db));
    }

    @FXML
    public void nuevoButton() {
        idTextField.clear();
        nameTextField.clear();
        emailTextField.clear();
        passwordTextField.clear();
        addressTextField.clear();
        phoneTextField.clear();
        roleMenuButton.setText("Role");
    }

    @FXML
    public void editarButton() {
        Usuario seleccionado = usuarioTableView.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mensajeLabel.setText("No hay ningún usuario seleccionado.");
        } else {
            guardarButton.setDisable(false);
            idTextField.setText(seleccionado.getIdUser().toString());
            idTextField.setDisable(true);
            nameTextField.setText(seleccionado.getName());
            emailTextField.setText(seleccionado.getEmail());
            passwordTextField.setText(seleccionado.getPassword());
            roleMenuButton.setText(seleccionado.getRole());
            addressTextField.setText(seleccionado.getAddress());
            phoneTextField.setText(seleccionado.getPhone());
        }
    }

    @FXML
    public void eliminarButton() {
        Usuario seleccionado = usuarioTableView.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mensajeLabel.setText("No hay ningún usuario seleccionado.");
        } else {
            MantenimientoUsuario.eliminar(db, seleccionado);
            mensajeLabel.setText("Usuario eliminado.");
            usuarioTableView.setItems(MantenimientoUsuario.consulta(db));
        }
    }

    @FXML
    public void guardarButton() {
        Integer id = Integer.parseInt(idTextField.getText());
        String name = nameTextField.getText();
        String email = emailTextField.getText();
        String password = passwordTextField.getText();
        String role = roleMenuButton.getText();
        String address = addressTextField.getText();
        String phone = phoneTextField.getText();

        MantenimientoUsuario.guardar(db, new Usuario(id, name, email, password, role, address, phone));
        guardarButton.setDisable(true);
        idTextField.setDisable(false);
        mensajeLabel.setText("Usuario modificado.");
        nuevoButton();
        usuarioTableView.setItems(MantenimientoUsuario.consulta(db));
    }

    @FXML
    public void buscarButton() {
        String textoBuscar = buscarTextField.getText();
        usuarioTableView.setItems(MantenimientoUsuario.buscar(db, textoBuscar));
    }

    @FXML
    public void volverButton() throws IOException {
        scottyapp.Main.MainApplication.setRoot("/scottyapp/main-view");
    }
}