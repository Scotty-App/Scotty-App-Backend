package scottyapp.OrderDetails;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.Connection;
import java.util.Locale;

import scottyapp.Item.Item;
import scottyapp.Item.MantenimientoItem;
import scottyapp.Order.MantenimientoOrder;
import scottyapp.Order.Order;
import scottyapp.Usuario.Usuario;
import scottyapp.Usuario.MantenimientoUsuario;

public class OrderDetailsController {

    static Connection db;
    private ObservableList<OrderDetails> listaDetalles;
    private ObservableList<Item> listaProductos;
    private ObservableList<Item> listaProductosCompleta;
    private Integer idOrderActual;

    // columnas de la tabla de lineas del pedido
    @FXML public TableView<OrderDetails> orderDetailsTableView;
    @FXML public TableColumn<OrderDetails, Integer> idDetailColumn;
    @FXML public TableColumn<OrderDetails, String> nombreProductoColumn;
    @FXML public TableColumn<OrderDetails, Integer> quantityColumn;
    @FXML public TableColumn<OrderDetails, Double> subtotalColumn;
    @FXML public TableColumn<OrderDetails, Integer> idProductColumn;

    // columnas de la tabla de productos disponibles
    @FXML public TableView<Item> productosTableView;
    @FXML public TableColumn<Item, Integer> idProductDisponibleColumn;
    @FXML public TableColumn<Item, String> nombreProductoDisponibleColumn;
    @FXML public TableColumn<Item, Double> priceProductColumn;
    @FXML public TableColumn<Item, Integer> stockDisponibleColumn;

    // campos del formulario de edicion de linea
    @FXML public TextField idDetailTextField;
    @FXML public TextField quantityTextField;
    @FXML public TextField subtotalTextField;

    // campo de cantidad y buscador para añadir producto
    @FXML public TextField cantidadTextField;
    @FXML public TextField buscarTextField;

    // labels de cabecera del pedido
    @FXML public Label pedidoIdLabel;
    @FXML public Label pedidoFechaLabel;
    @FXML public Label pedidoStatusLabel;
    @FXML public Label pedidoTotalLabel;
    @FXML public Label clienteNombreLabel;
    @FXML public Label clienteEmailLabel;
    @FXML public Label clienteDireccionLabel;
    @FXML public Label clienteTelefonoLabel;
    @FXML public Label mensajeLabel;

    // botones
    @FXML public Button editarButton;
    @FXML public Button eliminarButton;
    @FXML public Button guardarDetalleButton;
    @FXML public Button anyadirProductoButton;

    @FXML
    public void initialize() {
        db = MantenimientoOrderDetails.conexion();
        idOrderActual = scottyapp.Order.OrderController.idOrderSeleccionado;

        if (idOrderActual == null) {
            mensajeLabel.setText("Error: no hay ningun pedido seleccionado. Vuelve a pedidos.");
            return;
        }

        // enlazar columnas de lineas del pedido
        idDetailColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleIntegerProperty(datos.getValue().getIdDetail()).asObject());
        nombreProductoColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleStringProperty(datos.getValue().getNombreProducto()));
        quantityColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleIntegerProperty(datos.getValue().getQuantity()).asObject());
        subtotalColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleDoubleProperty(datos.getValue().getSubtotal()).asObject());
        idProductColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleIntegerProperty(datos.getValue().getIdProduct()).asObject());

        // enlazar columnas de productos disponibles
        idProductDisponibleColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleIntegerProperty(datos.getValue().getIdProduct()).asObject());
        nombreProductoDisponibleColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleStringProperty(datos.getValue().getName()));
        priceProductColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleDoubleProperty(datos.getValue().getPrice()).asObject());
        stockDisponibleColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleIntegerProperty(datos.getValue().getStock()).asObject());

        // botones deshabilitados por defecto
        editarButton.setDisable(true);
        eliminarButton.setDisable(true);
        guardarDetalleButton.setDisable(true);
        anyadirProductoButton.setDisable(true);

        // cargar datos
        cargarCabecera();
        listaDetalles = MantenimientoOrderDetails.consulta(db, idOrderActual);
        orderDetailsTableView.setItems(listaDetalles);
        listaProductosCompleta = MantenimientoItem.consulta(db);
        listaProductos = FXCollections.observableArrayList(listaProductosCompleta);
        productosTableView.setItems(listaProductos);

        // habilitar editar y eliminar al seleccionar una linea
        orderDetailsTableView.getSelectionModel().selectedItemProperty().addListener((observable, lineaAnterior, lineaSeleccionada) -> {
            if (lineaSeleccionada != null) {
                editarButton.setDisable(false);
                eliminarButton.setDisable(false);
            } else {
                editarButton.setDisable(true);
                eliminarButton.setDisable(true);
            }
        });

        // habilitar añadir al seleccionar un producto disponible
        productosTableView.getSelectionModel().selectedItemProperty().addListener((observable, productoAnterior, productoSeleccionado) -> {
            if (productoSeleccionado != null) {
                anyadirProductoButton.setDisable(false);
            } else {
                anyadirProductoButton.setDisable(true);
            }
        });
    }

    // carga la cabecera con los datos del pedido y del cliente
    private void cargarCabecera() {
        ObservableList<Order> todosLosPedidos = MantenimientoOrder.consulta(db);
        Order pedido = null;
        for (Order orden : todosLosPedidos) {
            if (orden.getIdOrder().equals(idOrderActual)) {
                pedido = orden;
                break;
            }
        }
        if (pedido == null) return;

        pedidoIdLabel.setText(String.valueOf(idOrderActual));
        pedidoFechaLabel.setText(pedido.getDate());
        pedidoTotalLabel.setText(String.format(Locale.US, "%.2f EUR", pedido.getTotal()));

        String estado = pedido.getStatus();
        pedidoStatusLabel.setText(estado);
        switch (estado) {
            case "PENDING":
                pedidoStatusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #d97706;");
                break;
            case "PROCESSED":
                pedidoStatusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #16a34a;");
                break;
            case "CANCELLED":
                pedidoStatusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #6b7280;");
                break;
        }

        ObservableList<Usuario> todosLosUsuarios = MantenimientoUsuario.consulta(db);
        Usuario cliente = null;
        for (Usuario usuario : todosLosUsuarios) {
            if (usuario.getIdUser().equals(pedido.getIdUser())) {
                cliente = usuario;
                break;
            }
        }
        if (cliente == null) return;

        clienteNombreLabel.setText(cliente.getName());
        clienteEmailLabel.setText(cliente.getEmail());
        clienteDireccionLabel.setText(cliente.getAddress() != null ? cliente.getAddress() : "—");
        clienteTelefonoLabel.setText(cliente.getPhone() != null ? cliente.getPhone() : "—");
    }

    private void limpiarFormulario() {
        idDetailTextField.clear();
        quantityTextField.clear();
        subtotalTextField.clear();
        quantityTextField.setDisable(true);
    }

    // filtra la tabla de productos disponibles por nombre
    @FXML
    public void buscarButton() {
        String textoBuscar = buscarTextField.getText().toLowerCase().trim();
        if (textoBuscar.isEmpty()) {
            productosTableView.setItems(listaProductosCompleta);
            return;
        }
        ObservableList<Item> listaFiltrada = FXCollections.observableArrayList();
        for (Item producto : listaProductosCompleta) {
            if (producto.getName().toLowerCase().contains(textoBuscar)) {
                listaFiltrada.add(producto);
            }
        }
        productosTableView.setItems(listaFiltrada);
    }

    // añade un producto al pedido o suma cantidad si ya existe como linea
    @FXML
    public void anyadirProductoButton() {
        Item productoSeleccionado = productosTableView.getSelectionModel().getSelectedItem();
        if (productoSeleccionado == null) {
            mensajeLabel.setText("Selecciona un producto de la lista.");
            return;
        }
        String cantidadTexto = cantidadTextField.getText().trim();
        if (cantidadTexto.isEmpty()) {
            mensajeLabel.setText("Indica la cantidad.");
            return;
        }
        Integer cantidad;
        try {
            cantidad = Integer.parseInt(cantidadTexto);
        } catch (NumberFormatException excepcion) {
            mensajeLabel.setText("La cantidad debe ser un numero entero.");
            return;
        }
        if (cantidad <= 0) {
            mensajeLabel.setText("La cantidad debe ser mayor que cero.");
            return;
        }
        if (cantidad > productoSeleccionado.getStock()) {
            mensajeLabel.setText("Stock insuficiente. Disponible: " + productoSeleccionado.getStock());
            return;
        }

        // comprobar si el producto ya existe como linea en este pedido
        OrderDetails lineaExistente = null;
        for (OrderDetails linea : listaDetalles) {
            if (linea.getIdProduct().equals(productoSeleccionado.getIdProduct())) {
                lineaExistente = linea;
                break;
            }
        }

        if (lineaExistente != null) {
            // el producto ya esta en el pedido: sumar cantidad y recalcular subtotal
            Integer nuevaCantidad = lineaExistente.getQuantity() + cantidad;
            Double nuevoSubtotal = productoSeleccionado.getPrice() * nuevaCantidad;
            OrderDetails lineaActualizada = new OrderDetails(
                    lineaExistente.getIdDetail(), nuevaCantidad, nuevoSubtotal,
                    idOrderActual, lineaExistente.getIdProduct(), lineaExistente.getNombreProducto()
            );
            if (MantenimientoOrderDetails.guardar(db, lineaActualizada)) {
                mensajeLabel.setText("Cantidad actualizada: " + productoSeleccionado.getName()
                        + " ahora tiene " + nuevaCantidad + " unidades.");
            } else {
                mensajeLabel.setText("Error al actualizar la cantidad.");
                return;
            }
        } else {
            // el producto no estaba: crear linea nueva
            Double subtotal = productoSeleccionado.getPrice() * cantidad;
            OrderDetails nuevaLinea = new OrderDetails(
                    null, cantidad, subtotal, idOrderActual,
                    productoSeleccionado.getIdProduct(), productoSeleccionado.getName()
            );
            if (MantenimientoOrderDetails.insertar(db, nuevaLinea)) {
                mensajeLabel.setText("Añadido: " + productoSeleccionado.getName()
                        + " x" + cantidad + " = " + String.format(Locale.US, "%.2f EUR", productoSeleccionado.getPrice() * cantidad));
            } else {
                mensajeLabel.setText("Error al añadir el producto.");
                return;
            }
        }

        MantenimientoOrderDetails.recalcularTotal(db, idOrderActual);
        listaDetalles = MantenimientoOrderDetails.consulta(db, idOrderActual);
        orderDetailsTableView.setItems(listaDetalles);
        cantidadTextField.clear();
        productosTableView.getSelectionModel().clearSelection();
        cargarCabecera();
    }

    // carga los datos de la linea seleccionada en el formulario de edicion
    @FXML
    public void editarButton() {
        OrderDetails lineaSeleccionada = orderDetailsTableView.getSelectionModel().getSelectedItem();
        if (lineaSeleccionada == null) {
            mensajeLabel.setText("No hay ninguna linea seleccionada.");
            return;
        }
        idDetailTextField.setText(lineaSeleccionada.getIdDetail().toString());
        quantityTextField.setText(lineaSeleccionada.getQuantity().toString());
        subtotalTextField.setText(String.format(Locale.US, "%.2f", lineaSeleccionada.getSubtotal()));
        quantityTextField.setDisable(false);
        guardarDetalleButton.setDisable(false);
        mensajeLabel.setText("Modifica la cantidad y pulsa Guardar cambios.");
    }

    // guarda los cambios de cantidad y recalcula subtotal y total del pedido
    @FXML
    public void guardarDetalleButton() {
        if (idDetailTextField.getText().isEmpty()) {
            mensajeLabel.setText("No hay ninguna linea en edicion.");
            return;
        }
        Integer idLinea = Integer.parseInt(idDetailTextField.getText());
        Integer nuevaCantidad;
        try {
            nuevaCantidad = Integer.parseInt(quantityTextField.getText().trim());
        } catch (NumberFormatException excepcion) {
            mensajeLabel.setText("La cantidad debe ser un numero entero.");
            return;
        }
        if (nuevaCantidad <= 0) {
            mensajeLabel.setText("La cantidad debe ser mayor que cero.");
            return;
        }

        // buscar la linea original para obtener el producto
        OrderDetails lineaOriginal = null;
        for (OrderDetails linea : listaDetalles) {
            if (linea.getIdDetail().equals(idLinea)) {
                lineaOriginal = linea;
                break;
            }
        }
        if (lineaOriginal == null) {
            mensajeLabel.setText("Error: linea no encontrada.");
            return;
        }

        // buscar el precio unitario del producto para recalcular el subtotal
        Item productoDeLinea = null;
        for (Item producto : listaProductos) {
            if (producto.getIdProduct().equals(lineaOriginal.getIdProduct())) {
                productoDeLinea = producto;
                break;
            }
        }
        Double precioUnitario = productoDeLinea != null
                ? productoDeLinea.getPrice()
                : lineaOriginal.getSubtotal() / lineaOriginal.getQuantity();

        Double nuevoSubtotal = precioUnitario * nuevaCantidad;
        OrderDetails lineaActualizada = new OrderDetails(
                idLinea, nuevaCantidad, nuevoSubtotal,
                idOrderActual, lineaOriginal.getIdProduct(), lineaOriginal.getNombreProducto()
        );

        if (MantenimientoOrderDetails.guardar(db, lineaActualizada)) {
            MantenimientoOrderDetails.recalcularTotal(db, idOrderActual);
            listaDetalles = MantenimientoOrderDetails.consulta(db, idOrderActual);
            orderDetailsTableView.setItems(listaDetalles);
            cargarCabecera();
            limpiarFormulario();
            guardarDetalleButton.setDisable(true);
            mensajeLabel.setText("Linea actualizada correctamente.");
        } else {
            mensajeLabel.setText("Error al guardar los cambios.");
        }
    }

    // elimina la linea seleccionada y recalcula el total del pedido
    @FXML
    public void eliminarButton() {
        OrderDetails lineaSeleccionada = orderDetailsTableView.getSelectionModel().getSelectedItem();
        if (lineaSeleccionada == null) {
            mensajeLabel.setText("No hay ninguna linea seleccionada.");
            return;
        }
        if (MantenimientoOrderDetails.eliminar(db, lineaSeleccionada)) {
            MantenimientoOrderDetails.recalcularTotal(db, idOrderActual);
            listaDetalles = MantenimientoOrderDetails.consulta(db, idOrderActual);
            orderDetailsTableView.setItems(listaDetalles);
            cargarCabecera();
            limpiarFormulario();
            guardarDetalleButton.setDisable(true);
            mensajeLabel.setText("Linea eliminada. Total recalculado.");
        } else {
            mensajeLabel.setText("Error al eliminar la linea.");
        }
    }

    @FXML
    public void volverButton() throws IOException {
        scottyapp.Main.MainApplication.setRoot("order-view");
    }
}