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

    // columnas de la tabla de usuarios
    @FXML public TableView<Usuario> usuarioTableView;
    @FXML public TableColumn<Usuario, Integer> idUserColumn;
    @FXML public TableColumn<Usuario, String> nameColumn;
    @FXML public TableColumn<Usuario, String> emailColumn;
    @FXML public TableColumn<Usuario, String> passwordColumn;
    @FXML public TableColumn<Usuario, String> roleColumn;
    @FXML public TableColumn<Usuario, String> addressColumn;
    @FXML public TableColumn<Usuario, String> phoneColumn;

    // campos del formulario
    @FXML public TextField idTextField;
    @FXML public TextField nameTextField;
    @FXML public TextField emailTextField;
    @FXML public TextField passwordTextField;
    @FXML public TextField addressTextField;
    @FXML public TextField phoneTextField;
    @FXML public TextField buscarTextField;

    // selector de rol y botones
    @FXML public SplitMenuButton roleMenuButton;
    @FXML public Label mensajeLabel;
    @FXML public Button guardarButton;
    @FXML public Button editarButton;
    @FXML public Button eliminarButton;
    @FXML public Button nuevoButton;

    @FXML
    public void initialize() {
        db = MantenimientoUsuario.conexion();

        // enlazar columnas con los atributos del objeto usuario
        idUserColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleIntegerProperty(datos.getValue().getIdUser()).asObject());
        nameColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleStringProperty(datos.getValue().getName()));
        emailColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleStringProperty(datos.getValue().getEmail()));
        passwordColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleStringProperty(datos.getValue().getPassword()));
        roleColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleStringProperty(datos.getValue().getRole()));
        addressColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleStringProperty(datos.getValue().getAddress()));
        phoneColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleStringProperty(datos.getValue().getPhone()));

        // estado inicial de botones
        guardarButton.setDisable(true);
        editarButton.setDisable(true);
        eliminarButton.setDisable(true);
        setTextFieldsDisable(true);

        // cargar todos los usuarios en la tabla
        listaUsuarios = MantenimientoUsuario.consulta(db);
        usuarioTableView.setItems(listaUsuarios);

        // habilitar editar y eliminar solo cuando hay una fila seleccionada
        usuarioTableView.getSelectionModel().selectedItemProperty().addListener((observable, usuarioAnterior, usuarioSeleccionado) -> {
            if (usuarioSeleccionado != null) {
                editarButton.setDisable(false);
                eliminarButton.setDisable(false);
            } else {
                editarButton.setDisable(true);
                eliminarButton.setDisable(true);
            }
        });

        // asignar cada opcion del desplegable de rol al texto del boton
        for (MenuItem opcionRol : roleMenuButton.getItems()) {
            opcionRol.setOnAction(evento -> roleMenuButton.setText(opcionRol.getText()));
        }
    }

    // habilita o deshabilita todos los campos del formulario
    private void setTextFieldsDisable(boolean deshabilitado) {
        nameTextField.setDisable(deshabilitado);
        emailTextField.setDisable(deshabilitado);
        passwordTextField.setDisable(deshabilitado);
        addressTextField.setDisable(deshabilitado);
        phoneTextField.setDisable(deshabilitado);
        roleMenuButton.setDisable(deshabilitado);
    }

    // limpia todos los campos del formulario
    private void limpiarCampos() {
        idTextField.clear();
        nameTextField.clear();
        emailTextField.clear();
        passwordTextField.clear();
        addressTextField.clear();
        phoneTextField.clear();
        roleMenuButton.setText("Role");
    }

    // valida los campos antes de insertar o guardar
    private boolean validarCampos(String nombre, String email, String password, String rol, String telefono) {
        if (nombre.isEmpty()) {
            mensajeLabel.setText("El nombre no puede estar vacio.");
            return false;
        }
        if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
            mensajeLabel.setText("El email no tiene un formato valido.");
            return false;
        }
        if (password.isEmpty()) {
            mensajeLabel.setText("La contrasena no puede estar vacia.");
            return false;
        }
        if (!rol.equals("ALUMNI") && !rol.equals("ADMINISTRATOR")) {
            mensajeLabel.setText("Selecciona un rol valido: ALUMNI o ADMINISTRATOR.");
            return false;
        }
        if (!telefono.isEmpty() && !telefono.matches("^[0-9]{9}$")) {
            mensajeLabel.setText("El telefono debe tener exactamente 9 digitos numericos.");
            return false;
        }
        return true;
    }

    // prepara el formulario para crear un usuario nuevo
    @FXML
    public void nuevoButton() {
        mensajeLabel.setText("");
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

    // carga los datos del usuario seleccionado en el formulario para editarlo
    @FXML
    public void editarButton() {
        mensajeLabel.setText("");
        Usuario usuarioSeleccionado = usuarioTableView.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado == null) {
            mensajeLabel.setText("No hay ningun usuario seleccionado.");
            return;
        }
        setTextFieldsDisable(false);
        idTextField.setText(usuarioSeleccionado.getIdUser().toString());
        idTextField.setDisable(true);
        nameTextField.setText(usuarioSeleccionado.getName());
        emailTextField.setText(usuarioSeleccionado.getEmail());
        passwordTextField.setText(usuarioSeleccionado.getPassword());
        roleMenuButton.setText(usuarioSeleccionado.getRole());
        addressTextField.setText(usuarioSeleccionado.getAddress() != null ? usuarioSeleccionado.getAddress() : "");
        phoneTextField.setText(usuarioSeleccionado.getPhone() != null ? usuarioSeleccionado.getPhone() : "");
        guardarButton.setDisable(false);
        nuevoButton.setDisable(true);
        editarButton.setDisable(true);
        eliminarButton.setDisable(true);
    }

    // elimina el usuario seleccionado de la base de datos
    @FXML
    public void eliminarButton() {
        mensajeLabel.setText("");
        Usuario usuarioSeleccionado = usuarioTableView.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado == null) {
            mensajeLabel.setText("No hay ningun usuario seleccionado.");
            return;
        }
        if (!MantenimientoUsuario.eliminar(db, usuarioSeleccionado)) {
            mensajeLabel.setText("Error al eliminar. El usuario puede tener pedidos asociados.");
            return;
        }
        mensajeLabel.setText("Usuario eliminado correctamente.");
        listaUsuarios = MantenimientoUsuario.consulta(db);
        usuarioTableView.setItems(listaUsuarios);
        limpiarCampos();
        setTextFieldsDisable(true);
        guardarButton.setDisable(true);
        nuevoButton.setDisable(false);
    }

    // guarda el usuario nuevo o los cambios del usuario editado
    @FXML
    public void guardarButton() {
        mensajeLabel.setText("");
        String nombre = nameTextField.getText().trim();
        String email = emailTextField.getText().trim();
        String password = passwordTextField.getText().trim();
        String rol = roleMenuButton.getText();
        String direccion = addressTextField.getText().trim();
        String telefono = phoneTextField.getText().trim();

        if (!validarCampos(nombre, email, password, rol, telefono)) {
            return;
        }

        if (idTextField.getText().isEmpty()) {
            // insertar usuario nuevo
            Usuario nuevoUsuario = new Usuario(null, nombre, email, password, rol, direccion, telefono.isEmpty() ? null : telefono);
            if (MantenimientoUsuario.insertar(db, nuevoUsuario)) {
                mensajeLabel.setText("Usuario insertado correctamente.");
            } else {
                mensajeLabel.setText("Error al insertar. El email ya existe o los datos son incorrectos.");
                return;
            }
        } else {
            // actualizar usuario existente
            Integer id = Integer.parseInt(idTextField.getText());
            Usuario usuarioEditado = new Usuario(id, nombre, email, password, rol, direccion, telefono.isEmpty() ? null : telefono);
            if (MantenimientoUsuario.guardar(db, usuarioEditado)) {
                mensajeLabel.setText("Usuario modificado correctamente.");
            } else {
                mensajeLabel.setText("Error al guardar. El email ya existe o los datos son incorrectos.");
                return;
            }
        }

        listaUsuarios = MantenimientoUsuario.consulta(db);
        usuarioTableView.setItems(listaUsuarios);
        limpiarCampos();
        setTextFieldsDisable(true);
        idTextField.setDisable(false);
        guardarButton.setDisable(true);
        nuevoButton.setDisable(false);
    }

    // filtra la tabla por nombre o email
    @FXML
    public void buscarButton() {
        mensajeLabel.setText("");
        String textoBuscar = buscarTextField.getText().toLowerCase().trim();
        if (textoBuscar.isEmpty()) {
            usuarioTableView.setItems(listaUsuarios);
            return;
        }
        ObservableList<Usuario> listaFiltrada = FXCollections.observableArrayList();
        for (Usuario usuario : listaUsuarios) {
            if (usuario.getName().toLowerCase().contains(textoBuscar)
                    || usuario.getEmail().toLowerCase().contains(textoBuscar)) {
                listaFiltrada.add(usuario);
            }
        }
        usuarioTableView.setItems(listaFiltrada);
    }

    @FXML
    public void volverButton() throws IOException {
        scottyapp.Main.MainApplication.setRoot("main-view");
    }
}