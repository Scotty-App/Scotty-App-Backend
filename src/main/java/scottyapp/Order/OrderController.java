package scottyapp.Order;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDate;

import scottyapp.Usuario.Usuario;
import scottyapp.Usuario.MantenimientoUsuario;

public class OrderController {

    static Connection db;
    private ObservableList<Order> listaOrders;
    private ObservableList<Order> listaCompleta;
    private ObservableList<Usuario> listaUsuarios;

    @FXML public TableView<Order> orderTableView;
    @FXML public TableColumn<Order, Integer> idOrderColumn;
    @FXML public TableColumn<Order, String> dateColumn;
    @FXML public TableColumn<Order, Double> totalColumn;
    @FXML public TableColumn<Order, String> statusColumn;
    @FXML public TableColumn<Order, Integer> idUserColumn;
    @FXML public TableColumn<Order, String> userNameColumn;

    @FXML public TextField idOrderTextField;
    @FXML public TextField dateTextField;
    @FXML public TextField totalTextField;
    @FXML public TextField buscarTextField;

    @FXML public ComboBox<String> usuarioComboBox;
    @FXML public ComboBox<String> filtroStatusComboBox;
    @FXML public SplitMenuButton statusMenuButton;
    @FXML public Button nuevoButton;
    @FXML public Button editarButton;
    @FXML public Button eliminarButton;
    @FXML public Button guardarButton;
    @FXML public Button verDetallesButton;

    @FXML public Label mensajeLabel;
    @FXML public Label modoLabel;
    @FXML public Label contadorTotalLabel;
    @FXML public Label contadorPendingLabel;
    @FXML public Label contadorProcessedLabel;
    @FXML public Label contadorCancelledLabel;

    public static Integer idOrderSeleccionado;

    @FXML
    public void initialize() {
        db = MantenimientoOrder.conexion();

        idOrderColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleIntegerProperty(datos.getValue().getIdOrder()).asObject());
        dateColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleStringProperty(datos.getValue().getDate()));
        totalColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleDoubleProperty(datos.getValue().getTotal()).asObject());
        idUserColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleIntegerProperty(datos.getValue().getIdUser()).asObject());

        statusColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleStringProperty(datos.getValue().getStatus()));
        statusColumn.setCellFactory(col -> new TableCell<Order, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status) {
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

        userNameColumn.setCellValueFactory(datos -> {
            Integer idUser = datos.getValue().getIdUser();
            String nombre = listaUsuarios.stream()
                    .filter(u -> u.getIdUser().equals(idUser))
                    .map(Usuario::getName)
                    .findFirst()
                    .orElse("Desconocido");
            return new javafx.beans.property.SimpleStringProperty(nombre);
        });

        guardarButton.setDisable(true);
        editarButton.setDisable(true);
        eliminarButton.setDisable(true);
        verDetallesButton.setDisable(true);
        setFormDisable(true);

        listaUsuarios = MantenimientoUsuario.consulta(db);
        for (Usuario usuario : listaUsuarios) {
            usuarioComboBox.getItems().add(usuario.getIdUser() + " - " + usuario.getName());
        }

        filtroStatusComboBox.getItems().addAll("Todos", "PENDING", "PROCESSED", "CANCELLED");
        filtroStatusComboBox.getSelectionModel().selectFirst();

        listaCompleta = MantenimientoOrder.consulta(db);
        listaOrders = FXCollections.observableArrayList(listaCompleta);
        orderTableView.setItems(listaOrders);
        actualizarContadores();

        orderTableView.getSortOrder().add(dateColumn);
        dateColumn.setSortType(TableColumn.SortType.DESCENDING);
        orderTableView.sort();

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

        for (MenuItem item : statusMenuButton.getItems()) {
            item.setOnAction(e -> statusMenuButton.setText(item.getText()));
        }
    }

    private void actualizarContadores() {
        long total = listaCompleta.size();
        long pending = listaCompleta.stream().filter(o -> "PENDING".equals(o.getStatus())).count();
        long processed = listaCompleta.stream().filter(o -> "PROCESSED".equals(o.getStatus())).count();
        long cancelled = listaCompleta.stream().filter(o -> "CANCELLED".equals(o.getStatus())).count();
        contadorTotalLabel.setText("Total: " + total);
        contadorPendingLabel.setText("Pendientes: " + pending);
        contadorProcessedLabel.setText("Procesados: " + processed);
        contadorCancelledLabel.setText("Cancelados: " + cancelled);
    }

    private void setFormDisable(boolean disabled) {
        dateTextField.setDisable(disabled);
        usuarioComboBox.setDisable(disabled);
        statusMenuButton.setDisable(disabled);
    }

    private void limpiarCampos() {
        idOrderTextField.clear();
        dateTextField.clear();
        totalTextField.clear();
        usuarioComboBox.getSelectionModel().clearSelection();
        usuarioComboBox.setPromptText("Selecciona cliente");
        statusMenuButton.setText("Estado");
        modoLabel.setText("");
    }

    @FXML
    public void filtrarPorStatus() {
        String filtro = filtroStatusComboBox.getSelectionModel().getSelectedItem();
        if (filtro == null || filtro.equals("Todos")) {
            listaOrders = FXCollections.observableArrayList(listaCompleta);
        } else {
            ObservableList<Order> filtrada = FXCollections.observableArrayList();
            for (Order o : listaCompleta) {
                if (o.getStatus().equals(filtro)) {
                    filtrada.add(o);
                }
            }
            listaOrders = filtrada;
        }
        orderTableView.setItems(listaOrders);
        buscarTextField.clear();
    }

    @FXML
    public void nuevoButton() {
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
        modoLabel.setText("Modo: Nuevo pedido — rellena cliente y pulsa Guardar. Podrás añadir productos desde Ver detalles.");
    }

    @FXML
    public void editarButton() {
        Order seleccionado = orderTableView.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mensajeLabel.setText("No hay ningún pedido seleccionado.");
            return;
        }
        setFormDisable(false);
        idOrderTextField.setText(seleccionado.getIdOrder().toString());
        idOrderTextField.setDisable(true);
        dateTextField.setText(seleccionado.getDate());
        dateTextField.setDisable(true);
        totalTextField.setText(seleccionado.getTotal().toString());
        statusMenuButton.setText(seleccionado.getStatus());
        for (String opcion : usuarioComboBox.getItems()) {
            if (opcion.startsWith(seleccionado.getIdUser().toString())) {
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
        modoLabel.setText("Modo: Editar estado — solo puedes cambiar el estado del pedido.");
    }

    @FXML
    public void eliminarButton() {
        Order seleccionado = orderTableView.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mensajeLabel.setText("No hay ningún pedido seleccionado.");
            return;
        }
        if (!MantenimientoOrder.eliminar(db, seleccionado)) {
            mensajeLabel.setText("Error al eliminar el pedido.");
            return;
        }
        mensajeLabel.setText("Pedido #" + seleccionado.getIdOrder() + " eliminado.");
        listaCompleta = MantenimientoOrder.consulta(db);
        listaOrders = FXCollections.observableArrayList(listaCompleta);
        orderTableView.setItems(listaOrders);
        actualizarContadores();
        limpiarCampos();
        setFormDisable(true);
        guardarButton.setDisable(true);
        nuevoButton.setDisable(false);
    }

    @FXML
    public void guardarButton() {
        String date = dateTextField.getText().trim();
        String status = statusMenuButton.getText();
        String usuarioElegido = usuarioComboBox.getSelectionModel().getSelectedItem();
        Double total;
        Integer idUser;

        if (!status.equals("PENDING") && !status.equals("PROCESSED") && !status.equals("CANCELLED")) {
            mensajeLabel.setText("Selecciona un estado válido.");
            return;
        }

        if (idOrderTextField.getText().isEmpty()) {
            if (usuarioElegido == null) {
                mensajeLabel.setText("Selecciona un cliente.");
                return;
            }
            if (date.isEmpty()) {
                mensajeLabel.setText("La fecha no puede estar vacía.");
                return;
            }
            idUser = Integer.parseInt(usuarioElegido.split(" - ")[0]);
            Order nuevoPedido = new Order(null, date, 0.0, status, idUser);
            Integer idGenerado = MantenimientoOrder.insertar(db, nuevoPedido);
            if (idGenerado != null) {
                mensajeLabel.setText("Pedido #" + idGenerado + " creado. Accede a Ver detalles para añadir productos.");
                idOrderSeleccionado = idGenerado;
            } else {
                mensajeLabel.setText("Error al crear el pedido. Revisa los datos.");
                return;
            }
        } else {
            Integer id = Integer.parseInt(idOrderTextField.getText());
            try {
                total = Double.parseDouble(totalTextField.getText());
            } catch (NumberFormatException e) {
                total = 0.0;
            }
            Order pedidoEditado = new Order(id, date, total, status, Integer.parseInt(
                    usuarioComboBox.getSelectionModel().getSelectedItem().split(" - ")[0]));
            MantenimientoOrder.guardar(db, pedidoEditado);
            mensajeLabel.setText("Estado del pedido #" + id + " actualizado a " + status + ".");
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

    @FXML
    public void buscarButton() {
        String texto = buscarTextField.getText().toLowerCase().trim();
        if (texto.isEmpty()) {
            orderTableView.setItems(listaOrders);
            return;
        }
        ObservableList<Order> filtrada = FXCollections.observableArrayList();
        for (Order o : listaOrders) {
            String nombre = listaUsuarios.stream()
                    .filter(u -> u.getIdUser().equals(o.getIdUser()))
                    .map(Usuario::getName)
                    .findFirst()
                    .orElse("").toLowerCase();
            if (o.getIdOrder().toString().contains(texto)
                    || o.getStatus().toLowerCase().contains(texto)
                    || nombre.contains(texto)) {
                filtrada.add(o);
            }
        }
        orderTableView.setItems(filtrada);
    }

    @FXML
    public void verDetallesButton() throws IOException {
        Order seleccionado = orderTableView.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            idOrderSeleccionado = seleccionado.getIdOrder();
            scottyapp.Main.MainApplication.setRoot("orderDetails-view");
        }
    }

    @FXML
    public void volverButton() throws IOException {
        scottyapp.Main.MainApplication.setRoot("main-view");
    }
}