package scottyapp.Order;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDate;

import scottyapp.Usuario.Usuario;
import scottyapp.Usuario.MantenimientoUsuario;
import scottyapp.Item.Item;
import scottyapp.Item.MantenimientoItem;

public class OrderController {

    static Connection db;
    private ObservableList<Order> listaOrders;
    private ObservableList<Usuario> listaUsuarios;
    private ObservableList<Item> listaItems;

    @FXML public TableView<Order> orderTableView;
    @FXML public TableColumn<Order, Integer> idOrderColumn;
    @FXML public TableColumn<Order, String> dateColumn;
    @FXML public TableColumn<Order, Double> totalColumn;
    @FXML public TableColumn<Order, String> statusColumn;
    @FXML public TableColumn<Order, Integer> idUserColumn;

    @FXML public TableView<Item> productosTableView;
    @FXML public TableColumn<Item, Integer> idProductColumn;
    @FXML public TableColumn<Item, String> nameProductColumn;
    @FXML public TableColumn<Item, Double> priceProductColumn;

    @FXML public TextField cantidadTextField;
    @FXML public TextField idOrderTextField;
    @FXML public TextField dateTextField;
    @FXML public TextField totalTextField;
    @FXML public TextField buscarTextField;

    @FXML public ComboBox<String> usuarioComboBox;
    @FXML public SplitMenuButton statusMenuButton;
    @FXML public Button nuevoButton;
    @FXML public Button editarButton;
    @FXML public Button eliminarButton;
    @FXML public Button guardarButton;
    @FXML public Button verDetallesButton;
    @FXML public Button anyadirProductoButton;
    @FXML public Label mensajeLabel;

    public static Integer idOrderSeleccionado;

    @FXML
    public void initialize() {
        db = MantenimientoOrder.conexion();

        // Enlazar columnas de la tabla de pedidos con los atributos de Order
        idOrderColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleIntegerProperty(datos.getValue().getIdOrder()).asObject());
        dateColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleStringProperty(datos.getValue().getDate()));
        totalColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleDoubleProperty(datos.getValue().getTotal()).asObject());
        statusColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleStringProperty(datos.getValue().getStatus()));
        idUserColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleIntegerProperty(datos.getValue().getIdUser()).asObject());

        // Enlazar columnas de la tabla de productos disponibles con los atributos de Item
        idProductColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleIntegerProperty(datos.getValue().getIdProduct()).asObject());
        nameProductColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleStringProperty(datos.getValue().getName()));
        priceProductColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleDoubleProperty(datos.getValue().getPrice()).asObject());

        // Botones deshabilitados por defecto hasta que se seleccione algo
        guardarButton.setDisable(true);
        editarButton.setDisable(true);
        eliminarButton.setDisable(true);
        verDetallesButton.setDisable(true);
        anyadirProductoButton.setDisable(true);
        setFormDisable(true);

        // Cargar pedidos en la tabla principal
        listaOrders = MantenimientoOrder.consulta(db);
        orderTableView.setItems(listaOrders);

        // Cargar usuarios en el ComboBox mostrando id y nombre
        listaUsuarios = MantenimientoUsuario.consulta(db);
        for (Usuario usuario : listaUsuarios) {
            usuarioComboBox.getItems().add(usuario.getIdUser() + " - " + usuario.getName());
        }

        // Cargar productos disponibles en la tabla de seleccion
        listaItems = MantenimientoItem.consulta(db);
        productosTableView.setItems(listaItems);

        // Al seleccionar un pedido se habilitan editar, eliminar y ver detalles
        orderTableView.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            if (seleccionado != null) {
                editarButton.setDisable(false);
                eliminarButton.setDisable(false);
                verDetallesButton.setDisable(false);
            } else {
                editarButton.setDisable(true);
                eliminarButton.setDisable(true);
                verDetallesButton.setDisable(true);
            }
        });

        // Al seleccionar un producto se habilita el boton de anyadir
        productosTableView.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            if (seleccionado != null) {
                anyadirProductoButton.setDisable(false);
            }
        });

        // Las opciones del SplitMenuButton muestran la opcion elegida
        for (MenuItem item : statusMenuButton.getItems()) {
            item.setOnAction(e -> statusMenuButton.setText(item.getText()));
        }
    }

    // Habilita o deshabilita los campos del formulario
    private void setFormDisable(boolean disabled) {
        dateTextField.setDisable(disabled);
        totalTextField.setDisable(disabled);
        usuarioComboBox.setDisable(disabled);
        statusMenuButton.setDisable(disabled);
        productosTableView.setDisable(disabled);
    }

    // Limpia todos los campos del formulario
    private void limpiarCampos() {
        idOrderTextField.clear();
        dateTextField.clear();
        totalTextField.clear();
        cantidadTextField.clear();
        usuarioComboBox.getSelectionModel().clearSelection();
        usuarioComboBox.setPromptText("Selecciona usuario");
        statusMenuButton.setText("Status");
    }

    @FXML
    public void nuevoButton() {
        limpiarCampos();
        setFormDisable(false);
        idOrderTextField.setDisable(true);
        // La fecha se pone automaticamente con la fecha de hoy
        dateTextField.setText(LocalDate.now().toString());
        guardarButton.setDisable(false);
        nuevoButton.setDisable(true);
        editarButton.setDisable(true);
        eliminarButton.setDisable(true);
        verDetallesButton.setDisable(true);
        cantidadTextField.setDisable(false);
        cantidadTextField.clear();
        orderTableView.getSelectionModel().clearSelection();
        mensajeLabel.setText("Selecciona usuario, productos y pulsa Guardar.");
    }

    @FXML
    public void anyadirProductoButton() {
        Item productoSeleccionado = productosTableView.getSelectionModel().getSelectedItem();
        if (productoSeleccionado == null) {
            mensajeLabel.setText("Selecciona un producto.");
            return;
        }
        String cantidadTexto = cantidadTextField.getText().trim();
        if (cantidadTexto.isEmpty()) {
            mensajeLabel.setText("Indica la cantidad.");
            return;
        }
        try {
            Integer cantidad = Integer.parseInt(cantidadTexto);
            Double subtotal = productoSeleccionado.getPrice() * cantidad;
            Double totalActual = totalTextField.getText().isEmpty() ? 0.0 : Double.parseDouble(totalTextField.getText());
            totalTextField.setText(String.format("%.2f", totalActual + subtotal));
            mensajeLabel.setText("Producto anyadido: " + productoSeleccionado.getName() + " x" + cantidad);
            // Limpiar cantidad para el siguiente producto sin bloquear el campo
            cantidadTextField.clear();
            cantidadTextField.setDisable(false);
        } catch (NumberFormatException e) {
            mensajeLabel.setText("La cantidad debe ser un numero entero.");
        }
    }

    @FXML
    public void editarButton() {
        Order pedidoSeleccionado = orderTableView.getSelectionModel().getSelectedItem();
        if (pedidoSeleccionado == null) {
            mensajeLabel.setText("No hay ningun pedido seleccionado.");
        } else {
            setFormDisable(false);
            idOrderTextField.setText(pedidoSeleccionado.getIdOrder().toString());
            idOrderTextField.setDisable(true);
            dateTextField.setText(pedidoSeleccionado.getDate());
            totalTextField.setText(pedidoSeleccionado.getTotal().toString());
            statusMenuButton.setText(pedidoSeleccionado.getStatus());
            // Seleccionar el usuario correspondiente en el ComboBox
            for (String opcion : usuarioComboBox.getItems()) {
                if (opcion.startsWith(pedidoSeleccionado.getIdUser().toString())) {
                    usuarioComboBox.getSelectionModel().select(opcion);
                    break;
                }
            }
            guardarButton.setDisable(false);
            nuevoButton.setDisable(true);
            editarButton.setDisable(true);
            eliminarButton.setDisable(true);
            verDetallesButton.setDisable(true);
        }
    }

    @FXML
    public void eliminarButton() {
        Order pedidoSeleccionado = orderTableView.getSelectionModel().getSelectedItem();
        if (pedidoSeleccionado == null) {
            mensajeLabel.setText("No hay ningun pedido seleccionado.");
        } else {
            MantenimientoOrder.eliminar(db, pedidoSeleccionado);
            mensajeLabel.setText("Pedido eliminado.");
            listaOrders = MantenimientoOrder.consulta(db);
            orderTableView.setItems(listaOrders);
            limpiarCampos();
            setFormDisable(true);
            guardarButton.setDisable(true);
            nuevoButton.setDisable(false);
        }
    }

    @FXML
    public void guardarButton() {
        String date = dateTextField.getText();
        String status = statusMenuButton.getText();
        String usuarioElegido = usuarioComboBox.getSelectionModel().getSelectedItem();
        Double total;
        Integer idUser;

        if (usuarioElegido == null) {
            mensajeLabel.setText("Selecciona un usuario.");
            return;
        }

        try {
            total = Double.parseDouble(totalTextField.getText());
            idUser = Integer.parseInt(usuarioElegido.split(" - ")[0]);
        } catch (NumberFormatException e) {
            mensajeLabel.setText("Total debe ser un numero.");
            return;
        }

        if (idOrderTextField.getText().isEmpty()) {
            Order nuevoPedido = new Order(null, date, total, status, idUser);
            if (MantenimientoOrder.insertar(db, nuevoPedido)) {
                mensajeLabel.setText("Pedido insertado.");
            } else {
                mensajeLabel.setText("Error al insertar. Revisa los datos.");
                return;
            }
        } else {
            Integer id = Integer.parseInt(idOrderTextField.getText());
            Order pedidoEditado = new Order(id, date, total, status, idUser);
            MantenimientoOrder.guardar(db, pedidoEditado);
            mensajeLabel.setText("Pedido modificado.");
        }

        listaOrders = MantenimientoOrder.consulta(db);
        orderTableView.setItems(listaOrders);
        limpiarCampos();
        setFormDisable(true);
        cantidadTextField.setDisable(true);
        idOrderTextField.setDisable(false);
        guardarButton.setDisable(true);
        nuevoButton.setDisable(false);
    }

    @FXML
    public void buscarButton() {
        String textoBuscar = buscarTextField.getText().toLowerCase();
        ObservableList<Order> listaFiltrada = FXCollections.observableArrayList();
        for (Order pedido : listaOrders) {
            if (pedido.getStatus().toLowerCase().contains(textoBuscar)
                    || pedido.getIdUser().toString().contains(textoBuscar)) {
                listaFiltrada.add(pedido);
            }
        }
        if (textoBuscar.isEmpty()) {
            orderTableView.setItems(listaOrders);
        } else {
            orderTableView.setItems(listaFiltrada);
        }
    }

    @FXML
    public void verDetallesButton() throws IOException {
        Order pedidoSeleccionado = orderTableView.getSelectionModel().getSelectedItem();
        if (pedidoSeleccionado != null) {
            idOrderSeleccionado = pedidoSeleccionado.getIdOrder();
            scottyapp.Main.MainApplication.setRoot("orderDetails-view");
        }
    }

    @FXML
    public void volverButton() throws IOException {
        scottyapp.Main.MainApplication.setRoot("main-view");
    }
}