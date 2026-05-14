package scottyapp.Usuario;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.sql.Connection;

public class UsuarioController {

    static Connection db;
    private ObservableList<Usuario> listaUsuarios;

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
    @FXML public Button editarButton;
    @FXML public Button eliminarButton;
    @FXML public Button nuevoButton;

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
        editarButton.setDisable(true);
        eliminarButton.setDisable(true);
        setTextFieldsDisable(true);

        listaUsuarios = MantenimientoUsuario.consulta(db);
        usuarioTableView.setItems(listaUsuarios);

        usuarioTableView.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            if (seleccionado != null) {
                editarButton.setDisable(false);
                eliminarButton.setDisable(false);
            } else {
                editarButton.setDisable(true);
                eliminarButton.setDisable(true);
            }
        });

        for (MenuItem item : roleMenuButton.getItems()) {
            item.setOnAction(e -> roleMenuButton.setText(item.getText()));
        }
    }

    private void setTextFieldsDisable(boolean disabled) {
        nameTextField.setDisable(disabled);
        emailTextField.setDisable(disabled);
        passwordTextField.setDisable(disabled);
        addressTextField.setDisable(disabled);
        phoneTextField.setDisable(disabled);
        roleMenuButton.setDisable(disabled);
    }

    private void limpiarCampos() {
        idTextField.clear();
        nameTextField.clear();
        emailTextField.clear();
        passwordTextField.clear();
        addressTextField.clear();
        phoneTextField.clear();
        roleMenuButton.setText("Role");
    }

    @FXML
    public void nuevoButton() {
        limpiarCampos();
        setTextFieldsDisable(false);
        idTextField.setDisable(true);
        guardarButton.setDisable(false);
        nuevoButton.setDisable(true);
        editarButton.setDisable(true);
        eliminarButton.setDisable(true);
        usuarioTableView.getSelectionModel().clearSelection();
        mensajeLabel.setText("Rellena los campos y pulsa Guardar.");
    }

    @FXML
    public void editarButton() {
        Usuario seleccionado = usuarioTableView.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mensajeLabel.setText("No hay ningún usuario seleccionado.");
        } else {
            setTextFieldsDisable(false);
            idTextField.setText(seleccionado.getIdUser().toString());
            idTextField.setDisable(true);
            nameTextField.setText(seleccionado.getName());
            emailTextField.setText(seleccionado.getEmail());
            passwordTextField.setText(seleccionado.getPassword());
            roleMenuButton.setText(seleccionado.getRole());
            addressTextField.setText(seleccionado.getAddress());
            phoneTextField.setText(seleccionado.getPhone());
            guardarButton.setDisable(false);
            nuevoButton.setDisable(true);
            editarButton.setDisable(true);
            eliminarButton.setDisable(true);
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
            listaUsuarios = MantenimientoUsuario.consulta(db);
            usuarioTableView.setItems(listaUsuarios);
            limpiarCampos();
            setTextFieldsDisable(true);
            guardarButton.setDisable(true);
            nuevoButton.setDisable(false);
        }
    }

    @FXML
    public void guardarButton() {
        String name = nameTextField.getText();
        String email = emailTextField.getText();
        String password = passwordTextField.getText();
        String role = roleMenuButton.getText();
        String address = addressTextField.getText();
        String phone = phoneTextField.getText();

        if (idTextField.getText().isEmpty()) {
            Usuario nuevo = new Usuario(null, name, email, password, role, address, phone);
            if (MantenimientoUsuario.insertar(db, nuevo)) {
                mensajeLabel.setText("Usuario insertado.");
            } else {
                mensajeLabel.setText("Error al insertar. Revisa los datos.");
                return;
            }
        } else {
            Integer id = Integer.parseInt(idTextField.getText());
            Usuario editado = new Usuario(id, name, email, password, role, address, phone);
            MantenimientoUsuario.guardar(db, editado);
            mensajeLabel.setText("Usuario modificado.");
        }

        listaUsuarios = MantenimientoUsuario.consulta(db);
        usuarioTableView.setItems(listaUsuarios);
        limpiarCampos();
        setTextFieldsDisable(true);
        idTextField.setDisable(false);
        guardarButton.setDisable(true);
        nuevoButton.setDisable(false);
    }

    @FXML
    public void buscarButton() {
        String textoBuscar = buscarTextField.getText().toLowerCase();
        ObservableList<Usuario> listaFiltrada = FXCollections.observableArrayList();
        for (Usuario u : listaUsuarios) {
            if (u.getName().toLowerCase().contains(textoBuscar)
                    || u.getEmail().toLowerCase().contains(textoBuscar)) {
                listaFiltrada.add(u);
            }
        }
        if (textoBuscar.isEmpty()) {
            usuarioTableView.setItems(listaUsuarios);
        } else {
            usuarioTableView.setItems(listaFiltrada);
        }
    }

    @FXML
    public void volverButton() throws IOException {
        scottyapp.Main.MainApplication.setRoot("main-view");
    }
}