package scottyapp.OrderDetails;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.Connection;

import scottyapp.Item.Item;
import scottyapp.Item.MantenimientoItem;

public class OrderDetailsController {

    static Connection db;
    private ObservableList<OrderDetails> listaDetalles;
    private ObservableList<Item> listaItems;

    @FXML public TableView<OrderDetails> orderDetailsTableView;
    @FXML public TableColumn<OrderDetails, Integer> idDetailColumn;
    @FXML public TableColumn<OrderDetails, Integer> quantityColumn;
    @FXML public TableColumn<OrderDetails, Double> subtotalColumn;
    @FXML public TableColumn<OrderDetails, Integer> idOrderColumn;
    @FXML public TableColumn<OrderDetails, Integer> idProductColumn;

    @FXML public TableView<Item> productosTableView;
    @FXML public TableColumn<Item, Integer> idProductDisponibleColumn;
    @FXML public TableColumn<Item, String> nameProductColumn;
    @FXML public TableColumn<Item, Double> priceProductColumn;

    @FXML public TextField idDetailTextField;
    @FXML public TextField quantityTextField;
    @FXML public TextField subtotalTextField;

    @FXML public Label pedidoLabel;
    @FXML public Label mensajeLabel;
    @FXML public Button nuevoButton;
    @FXML public Button editarButton;
    @FXML public Button eliminarButton;
    @FXML public Button guardarButton;
    @FXML public Button anyadirProductoButton;

    @FXML
    public void initialize() {
        db = MantenimientoOrderDetails.conexion();

        // Enlazar columnas de la tabla de detalles con los atributos de OrderDetails
        idDetailColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleIntegerProperty(datos.getValue().getIdDetail()).asObject());
        quantityColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleIntegerProperty(datos.getValue().getQuantity()).asObject());
        subtotalColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleDoubleProperty(datos.getValue().getSubtotal()).asObject());
        idOrderColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleIntegerProperty(datos.getValue().getIdOrder()).asObject());
        idProductColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleIntegerProperty(datos.getValue().getIdProduct()).asObject());

        // Enlazar columnas de la tabla de productos disponibles
        idProductDisponibleColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleIntegerProperty(datos.getValue().getIdProduct()).asObject());
        nameProductColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleStringProperty(datos.getValue().getName()));
        priceProductColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleDoubleProperty(datos.getValue().getPrice()).asObject());

        // Botones deshabilitados por defecto
        guardarButton.setDisable(true);
        editarButton.setDisable(true);
        eliminarButton.setDisable(true);
        anyadirProductoButton.setDisable(true);
        setFormDisable(true);

        // Cargar los detalles del pedido seleccionado en la pantalla anterior
        Integer idOrder = scottyapp.Order.OrderController.idOrderSeleccionado;
        pedidoLabel.setText("Detalles del pedido: " + idOrder);
        listaDetalles = MantenimientoOrderDetails.consulta(db, idOrder);
        orderDetailsTableView.setItems(listaDetalles);

        // Cargar productos disponibles para anyadir al pedido
        listaItems = MantenimientoItem.consulta(db);
        productosTableView.setItems(listaItems);

        // Al seleccionar un detalle se habilitan editar y eliminar
        orderDetailsTableView.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            if (seleccionado != null) {
                editarButton.setDisable(false);
                eliminarButton.setDisable(false);
            } else {
                editarButton.setDisable(true);
                eliminarButton.setDisable(true);
            }
        });

        // Al seleccionar un producto disponible se habilita el boton de anyadir
        productosTableView.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            if (seleccionado != null) {
                anyadirProductoButton.setDisable(false);
            }
        });
    }

    // Habilita o deshabilita los campos del formulario
    private void setFormDisable(boolean disabled) {
        quantityTextField.setDisable(disabled);
        subtotalTextField.setDisable(disabled);
        productosTableView.setDisable(disabled);
    }

    // Limpia todos los campos del formulario
    private void limpiarCampos() {
        idDetailTextField.clear();
        quantityTextField.clear();
        subtotalTextField.clear();
    }

    @FXML
    public void nuevoButton() {
        limpiarCampos();
        setFormDisable(false);
        idDetailTextField.setDisable(true);
        quantityTextField.setDisable(false);
        guardarButton.setDisable(false);
        nuevoButton.setDisable(true);
        editarButton.setDisable(true);
        eliminarButton.setDisable(true);
        orderDetailsTableView.getSelectionModel().clearSelection();
        mensajeLabel.setText("Selecciona un producto e indica la cantidad.");
    }

    @FXML
    public void anyadirProductoButton() {
        Item productoSeleccionado = productosTableView.getSelectionModel().getSelectedItem();
        if (productoSeleccionado == null) {
            mensajeLabel.setText("Selecciona un producto.");
            return;
        }
        String cantidadTexto = quantityTextField.getText().trim();
        if (cantidadTexto.isEmpty()) {
            mensajeLabel.setText("Indica la cantidad.");
            return;
        }
        try {
            Integer cantidad = Integer.parseInt(cantidadTexto);
            Double subtotal = productoSeleccionado.getPrice() * cantidad;
            subtotalTextField.setText(String.format("%.2f", subtotal));
            mensajeLabel.setText("Producto: " + productoSeleccionado.getName() + " x" + cantidad + " = " + subtotal);
        } catch (NumberFormatException e) {
            mensajeLabel.setText("La cantidad debe ser un numero entero.");
        }
    }

    @FXML
    public void editarButton() {
        OrderDetails detalleSeleccionado = orderDetailsTableView.getSelectionModel().getSelectedItem();
        if (detalleSeleccionado == null) {
            mensajeLabel.setText("No hay ningun detalle seleccionado.");
        } else {
            setFormDisable(false);
            idDetailTextField.setText(detalleSeleccionado.getIdDetail().toString());
            idDetailTextField.setDisable(true);
            quantityTextField.setText(detalleSeleccionado.getQuantity().toString());
            subtotalTextField.setText(detalleSeleccionado.getSubtotal().toString());
            guardarButton.setDisable(false);
            nuevoButton.setDisable(true);
            editarButton.setDisable(true);
            eliminarButton.setDisable(true);
        }
    }

    @FXML
    public void eliminarButton() {
        OrderDetails detalleSeleccionado = orderDetailsTableView.getSelectionModel().getSelectedItem();
        if (detalleSeleccionado == null) {
            mensajeLabel.setText("No hay ningun detalle seleccionado.");
        } else {
            MantenimientoOrderDetails.eliminar(db, detalleSeleccionado);
            mensajeLabel.setText("Detalle eliminado.");
            listaDetalles = MantenimientoOrderDetails.consulta(db, scottyapp.Order.OrderController.idOrderSeleccionado);
            orderDetailsTableView.setItems(listaDetalles);
            limpiarCampos();
            setFormDisable(true);
            guardarButton.setDisable(true);
            nuevoButton.setDisable(false);
        }
    }

    @FXML
    public void guardarButton() {
        Integer quantity;
        Double subtotal;
        Integer idOrder = scottyapp.Order.OrderController.idOrderSeleccionado;
        Integer idProduct;

        Item productoSeleccionado = productosTableView.getSelectionModel().getSelectedItem();

        try {
            quantity = Integer.parseInt(quantityTextField.getText());
            subtotal = Double.parseDouble(subtotalTextField.getText());
        } catch (NumberFormatException e) {
            mensajeLabel.setText("Cantidad y subtotal deben ser numeros.");
            return;
        }

        if (idDetailTextField.getText().isEmpty()) {
            // Nuevo detalle: necesitamos el producto seleccionado de la tabla
            if (productoSeleccionado == null) {
                mensajeLabel.setText("Selecciona un producto.");
                return;
            }
            idProduct = productoSeleccionado.getIdProduct();
            OrderDetails nuevoDetalle = new OrderDetails(null, quantity, subtotal, idOrder, idProduct);
            if (MantenimientoOrderDetails.insertar(db, nuevoDetalle)) {
                mensajeLabel.setText("Detalle insertado.");
            } else {
                mensajeLabel.setText("Error al insertar. Revisa los datos.");
                return;
            }
        } else {
            // Editar detalle existente
            Integer id = Integer.parseInt(idDetailTextField.getText());
            idProduct = orderDetailsTableView.getSelectionModel().getSelectedItem().getIdProduct();
            OrderDetails detalleEditado = new OrderDetails(id, quantity, subtotal, idOrder, idProduct);
            MantenimientoOrderDetails.guardar(db, detalleEditado);
            mensajeLabel.setText("Detalle modificado.");
        }

        listaDetalles = MantenimientoOrderDetails.consulta(db, idOrder);
        orderDetailsTableView.setItems(listaDetalles);
        limpiarCampos();
        setFormDisable(true);
        idDetailTextField.setDisable(false);
        guardarButton.setDisable(true);
        nuevoButton.setDisable(false);
    }

    @FXML
    public void volverButton() throws IOException {
        scottyapp.Main.MainApplication.setRoot("order-view");
    }
}