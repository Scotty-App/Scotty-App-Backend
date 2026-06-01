package scottyapp.Order;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.Locale;

import scottyapp.Usuario.Usuario;
import scottyapp.Usuario.MantenimientoUsuario;

public class OrderController {

    static Connection db;
    private ObservableList<Order> listaOrders;
    private ObservableList<Order> listaCompleta;
    private ObservableList<Usuario> listaUsuarios;

    // columnas de la tabla de pedidos
    @FXML public TableView<Order> orderTableView;
    @FXML public TableColumn<Order, Integer> idOrderColumn;
    @FXML public TableColumn<Order, String> dateColumn;
    @FXML public TableColumn<Order, Double> totalColumn;
    @FXML public TableColumn<Order, String> statusColumn;
    @FXML public TableColumn<Order, Integer> idUserColumn;
    @FXML public TableColumn<Order, String> userNameColumn;

    // campos del formulario
    @FXML public TextField idOrderTextField;
    @FXML public TextField dateTextField;
    @FXML public TextField totalTextField;
    @FXML public TextField buscarTextField;

    // selectores y botones
    @FXML public ComboBox<String> usuarioComboBox;
    @FXML public ComboBox<String> filtroStatusComboBox;
    @FXML public SplitMenuButton statusMenuButton;
    @FXML public Button nuevoButton;
    @FXML public Button editarButton;
    @FXML public Button eliminarButton;
    @FXML public Button guardarButton;
    @FXML public Button verDetallesButton;

    // labels de informacion y contadores
    @FXML public Label mensajeLabel;
    @FXML public Label modoLabel;
    @FXML public Label contadorTotalLabel;
    @FXML public Label contadorPendingLabel;
    @FXML public Label contadorProcessedLabel;
    @FXML public Label contadorCancelledLabel;

    // id del pedido seleccionado, compartido con orderdetailscontroller
    public static Integer idOrderSeleccionado;

    @FXML
    public void initialize() {
        db = MantenimientoOrder.conexion();

        // enlazar columnas con los atributos del objeto pedido
        idOrderColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleIntegerProperty(datos.getValue().getIdOrder()).asObject());
        dateColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleStringProperty(datos.getValue().getDate()));
        totalColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleDoubleProperty(datos.getValue().getTotal()).asObject());
        idUserColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleIntegerProperty(datos.getValue().getIdUser()).asObject());

        // columna estado con color segun el valor
        statusColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleStringProperty(datos.getValue().getStatus()));
        statusColumn.setCellFactory(columna -> new TableCell<Order, String>() {
            @Override
            protected void updateItem(String estado, boolean vacio) {
                super.updateItem(estado, vacio);
                if (vacio || estado == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(estado);
                    switch (estado) {
                        case "PENDING":
                            setStyle("-fx-text-fill: #d97706; -fx-font-weight: bold;");
                            break;
                        case "PROCESSED":
                            setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
                            break;
                        case "CANCELLED":
                            setStyle("-fx-text-fill: #6b7280; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });

        // columna cliente muestra el nombre del usuario en lugar del id
        userNameColumn.setCellValueFactory(datos -> {
            Integer idUsuario = datos.getValue().getIdUser();
            String nombreCliente = "Desconocido";
            for (Usuario usuario : listaUsuarios) {
                if (usuario.getIdUser().equals(idUsuario)) {
                    nombreCliente = usuario.getName();
                    break;
                }
            }
            return new javafx.beans.property.SimpleStringProperty(nombreCliente);
        });

        // estado inicial de botones
        guardarButton.setDisable(true);
        editarButton.setDisable(true);
        eliminarButton.setDisable(true);
        verDetallesButton.setDisable(true);
        setFormDisable(true);

        // cargar usuarios en el combo de seleccion de cliente
        listaUsuarios = MantenimientoUsuario.consulta(db);
        for (Usuario usuario : listaUsuarios) {
            usuarioComboBox.getItems().add(usuario.getIdUser() + " - " + usuario.getName());
        }

        // opciones del filtro rapido por estado
        filtroStatusComboBox.getItems().addAll("Todos", "PENDING", "PROCESSED", "CANCELLED");
        filtroStatusComboBox.getSelectionModel().selectFirst();

        // cargar pedidos y ordenar por fecha descendente
        listaCompleta = MantenimientoOrder.consulta(db);
        listaOrders = FXCollections.observableArrayList(listaCompleta);
        orderTableView.setItems(listaOrders);
        actualizarContadores();
        orderTableView.getSortOrder().add(dateColumn);
        dateColumn.setSortType(TableColumn.SortType.DESCENDING);
        orderTableView.sort();

        // habilitar editar, eliminar y ver detalles solo con fila seleccionada
        orderTableView.getSelectionModel().selectedItemProperty().addListener((observable, pedidoAnterior, pedidoSeleccionado) -> {
            if (pedidoSeleccionado != null) {
                editarButton.setDisable(false);
                eliminarButton.setDisable(false);
                verDetallesButton.setDisable(false);
            } else {
                editarButton.setDisable(true);
                eliminarButton.setDisable(true);
                verDetallesButton.setDisable(true);
            }
        });

        // asignar cada opcion del desplegable de estado al texto del boton
        for (MenuItem opcionEstado : statusMenuButton.getItems()) {
            opcionEstado.setOnAction(evento -> statusMenuButton.setText(opcionEstado.getText()));
        }
    }

    // actualiza los contadores de la barra superior
    private void actualizarContadores() {
        long total = listaCompleta.size();
        long pendientes = listaCompleta.stream().filter(pedido -> "PENDING".equals(pedido.getStatus())).count();
        long procesados = listaCompleta.stream().filter(pedido -> "PROCESSED".equals(pedido.getStatus())).count();
        long cancelados = listaCompleta.stream().filter(pedido -> "CANCELLED".equals(pedido.getStatus())).count();
        contadorTotalLabel.setText("Total: " + total);
        contadorPendingLabel.setText("Pendientes: " + pendientes);
        contadorProcessedLabel.setText("Procesados: " + procesados);
        contadorCancelledLabel.setText("Cancelados: " + cancelados);
    }

    // habilita o deshabilita los campos del formulario
    private void setFormDisable(boolean deshabilitado) {
        dateTextField.setDisable(deshabilitado);
        usuarioComboBox.setDisable(deshabilitado);
        statusMenuButton.setDisable(deshabilitado);
    }

    // limpia todos los campos del formulario
    private void limpiarCampos() {
        idOrderTextField.clear();
        dateTextField.clear();
        totalTextField.clear();
        usuarioComboBox.getSelectionModel().clearSelection();
        usuarioComboBox.setPromptText("Selecciona cliente");
        statusMenuButton.setText("Estado");
        modoLabel.setText("");
    }

    // filtra la tabla por el estado elegido en el combo rapido
    @FXML
    public void filtrarPorStatus() {
        String filtro = filtroStatusComboBox.getSelectionModel().getSelectedItem();
        if (filtro == null || filtro.equals("Todos")) {
            listaOrders = FXCollections.observableArrayList(listaCompleta);
        } else {
            ObservableList<Order> listaFiltrada = FXCollections.observableArrayList();
            for (Order pedido : listaCompleta) {
                if (pedido.getStatus().equals(filtro)) {
                    listaFiltrada.add(pedido);
                }
            }
            listaOrders = listaFiltrada;
        }
        orderTableView.setItems(listaOrders);
        buscarTextField.clear();
    }

    // prepara el formulario para crear un pedido nuevo
    @FXML
    public void nuevoButton() {
        mensajeLabel.setText("");
        limpiarCampos();
        setFormDisable(false);
        idOrderTextField.setDisable(true);
        totalTextField.setText("0.00");
        dateTextField.setText(LocalDate.now().toString());
        guardarButton.setDisable(false);
        nuevoButton.setDisable(true);
        editarButton.setDisable(true);
        eliminarButton.setDisable(true);
        verDetallesButton.setDisable(true);
        orderTableView.getSelectionModel().clearSelection();
        modoLabel.setText("Nuevo pedido: selecciona cliente y pulsa Guardar. Añade productos desde Ver detalles.");
    }

    // carga el estado del pedido seleccionado en el formulario para editarlo
    @FXML
    public void editarButton() {
        mensajeLabel.setText("");
        Order pedidoSeleccionado = orderTableView.getSelectionModel().getSelectedItem();
        if (pedidoSeleccionado == null) {
            mensajeLabel.setText("No hay ningun pedido seleccionado.");
            return;
        }
        setFormDisable(false);
        idOrderTextField.setText(pedidoSeleccionado.getIdOrder().toString());
        idOrderTextField.setDisable(true);
        dateTextField.setText(pedidoSeleccionado.getDate());
        dateTextField.setDisable(true);
        totalTextField.setText(String.format(Locale.US, "%.2f", pedidoSeleccionado.getTotal()));
        statusMenuButton.setText(pedidoSeleccionado.getStatus());
        for (String opcion : usuarioComboBox.getItems()) {
            if (opcion.startsWith(pedidoSeleccionado.getIdUser().toString())) {
                usuarioComboBox.getSelectionModel().select(opcion);
                break;
            }
        }
        usuarioComboBox.setDisable(true);
        guardarButton.setDisable(false);
        nuevoButton.setDisable(true);
        editarButton.setDisable(true);
        eliminarButton.setDisable(true);
        verDetallesButton.setDisable(true);
        modoLabel.setText("Editar estado: solo puedes cambiar el estado del pedido.");
    }

    // elimina el pedido seleccionado y todas sus lineas
    @FXML
    public void eliminarButton() {
        mensajeLabel.setText("");
        Order pedidoSeleccionado = orderTableView.getSelectionModel().getSelectedItem();
        if (pedidoSeleccionado == null) {
            mensajeLabel.setText("No hay ningun pedido seleccionado.");
            return;
        }
        if (!MantenimientoOrder.eliminar(db, pedidoSeleccionado)) {
            mensajeLabel.setText("Error al eliminar el pedido.");
            return;
        }
        mensajeLabel.setText("Pedido #" + pedidoSeleccionado.getIdOrder() + " eliminado correctamente.");
        listaCompleta = MantenimientoOrder.consulta(db);
        listaOrders = FXCollections.observableArrayList(listaCompleta);
        orderTableView.setItems(listaOrders);
        actualizarContadores();
        limpiarCampos();
        setFormDisable(true);
        guardarButton.setDisable(true);
        nuevoButton.setDisable(false);
    }

    // guarda el pedido nuevo o actualiza el estado del pedido editado
    // si es nuevo, navega directamente a ver detalles para añadir productos
    @FXML
    public void guardarButton() throws IOException {
        mensajeLabel.setText("");
        String fecha = dateTextField.getText().trim();
        String estado = statusMenuButton.getText();
        String clienteElegido = usuarioComboBox.getSelectionModel().getSelectedItem();

        if (!estado.equals("PENDING") && !estado.equals("PROCESSED") && !estado.equals("CANCELLED")) {
            mensajeLabel.setText("Selecciona un estado valido.");
            return;
        }

        if (idOrderTextField.getText().isEmpty()) {
            // insertar pedido nuevo
            if (clienteElegido == null) {
                mensajeLabel.setText("Selecciona un cliente.");
                return;
            }
            if (fecha.isEmpty()) {
                mensajeLabel.setText("La fecha no puede estar vacia.");
                return;
            }
            Integer idUsuario = Integer.parseInt(clienteElegido.split(" - ")[0]);
            Order nuevoPedido = new Order(null, fecha, 0.0, estado, idUsuario);
            Integer idGenerado = MantenimientoOrder.insertar(db, nuevoPedido);
            if (idGenerado == null) {
                mensajeLabel.setText("Error al crear el pedido. Revisa los datos.");
                return;
            }
            // navegar directamente a detalles del pedido recien creado
            idOrderSeleccionado = idGenerado;
            scottyapp.Main.MainApplication.setRoot("orderDetails-view");
            return;
        } else {
            // actualizar solo el estado del pedido existente
            Integer id = Integer.parseInt(idOrderTextField.getText());
            Double total;
            try {
                total = Double.parseDouble(totalTextField.getText());
            } catch (NumberFormatException excepcion) {
                total = 0.0;
            }
            Integer idUsuario = Integer.parseInt(
                    usuarioComboBox.getSelectionModel().getSelectedItem().split(" - ")[0]);
            Order pedidoEditado = new Order(id, fecha, total, estado, idUsuario);
            MantenimientoOrder.guardar(db, pedidoEditado);
            mensajeLabel.setText("Estado del pedido #" + id + " actualizado a " + estado + ".");
        }

        listaCompleta = MantenimientoOrder.consulta(db);
        listaOrders = FXCollections.observableArrayList(listaCompleta);
        orderTableView.setItems(listaOrders);
        actualizarContadores();
        limpiarCampos();
        setFormDisable(true);
        idOrderTextField.setDisable(false);
        dateTextField.setDisable(false);
        guardarButton.setDisable(true);
        nuevoButton.setDisable(false);
    }

    // filtra la tabla por numero de pedido, nombre de cliente o estado
    @FXML
    public void buscarButton() {
        mensajeLabel.setText("");
        String textoBuscar = buscarTextField.getText().toLowerCase().trim();
        if (textoBuscar.isEmpty()) {
            orderTableView.setItems(listaOrders);
            return;
        }
        ObservableList<Order> listaFiltrada = FXCollections.observableArrayList();
        for (Order pedido : listaOrders) {
            String nombreCliente = "";
            for (Usuario usuario : listaUsuarios) {
                if (usuario.getIdUser().equals(pedido.getIdUser())) {
                    nombreCliente = usuario.getName().toLowerCase();
                    break;
                }
            }
            if (pedido.getIdOrder().toString().contains(textoBuscar)
                    || pedido.getStatus().toLowerCase().contains(textoBuscar)
                    || nombreCliente.contains(textoBuscar)) {
                listaFiltrada.add(pedido);
            }
        }
        orderTableView.setItems(listaFiltrada);
    }

    // navega a los detalles del pedido seleccionado
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