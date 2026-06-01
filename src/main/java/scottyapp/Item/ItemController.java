package scottyapp.Item;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

public class ItemController {

    static Connection db;
    private ObservableList<Item> listaProductos;

    // columnas de la tabla de productos
    @FXML public TableView<Item> itemTableView;
    @FXML public TableColumn<Item, Integer> idProductColumn;
    @FXML public TableColumn<Item, String> nameColumn;
    @FXML public TableColumn<Item, String> descriptionColumn;
    @FXML public TableColumn<Item, Double> priceColumn;
    @FXML public TableColumn<Item, Integer> stockColumn;
    @FXML public TableColumn<Item, String> categoryColumn;

    // campos del formulario
    @FXML public TextField idTextField;
    @FXML public TextField buscarTextField;
    @FXML public TextField nameTextField;
    @FXML public TextField descriptionTextField;
    @FXML public TextField priceTextField;
    @FXML public TextField stockTextField;

    // selector de categoria y botones
    @FXML public SplitMenuButton categoryMenuButton;
    @FXML public Button nuevoButton;
    @FXML public Button editarButton;
    @FXML public Button eliminarButton;
    @FXML public Button guardarButton;
    @FXML public Label mensajeLabel;

    @FXML
    public void initialize() {
        db = MantenimientoItem.conexion();

        // enlazar columnas con los atributos del objeto producto
        idProductColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleIntegerProperty(datos.getValue().getIdProduct()).asObject());
        nameColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleStringProperty(datos.getValue().getName()));
        descriptionColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleStringProperty(datos.getValue().getDescription()));
        priceColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleDoubleProperty(datos.getValue().getPrice()).asObject());
        stockColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleIntegerProperty(datos.getValue().getStock()).asObject());
        categoryColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleStringProperty(datos.getValue().getCategory()));

        // estado inicial de botones
        guardarButton.setDisable(true);
        editarButton.setDisable(true);
        eliminarButton.setDisable(true);
        setTextFieldsDisable(true);

        // cargar todos los productos en la tabla
        listaProductos = MantenimientoItem.consulta(db);
        itemTableView.setItems(listaProductos);

        // habilitar editar y eliminar solo cuando hay una fila seleccionada
        itemTableView.getSelectionModel().selectedItemProperty().addListener((observable, productoAnterior, productoSeleccionado) -> {
            if (productoSeleccionado != null) {
                editarButton.setDisable(false);
                eliminarButton.setDisable(false);
            } else {
                editarButton.setDisable(true);
                eliminarButton.setDisable(true);
            }
        });

        // asignar cada opcion del desplegable de categoria al texto del boton
        for (MenuItem opcionCategoria : categoryMenuButton.getItems()) {
            opcionCategoria.setOnAction(evento -> categoryMenuButton.setText(opcionCategoria.getText()));
        }
    }

    // habilita o deshabilita todos los campos del formulario
    private void setTextFieldsDisable(boolean deshabilitado) {
        nameTextField.setDisable(deshabilitado);
        descriptionTextField.setDisable(deshabilitado);
        priceTextField.setDisable(deshabilitado);
        stockTextField.setDisable(deshabilitado);
        categoryMenuButton.setDisable(deshabilitado);
    }

    // limpia todos los campos del formulario
    private void limpiarCampos() {
        idTextField.clear();
        nameTextField.clear();
        descriptionTextField.clear();
        priceTextField.clear();
        stockTextField.clear();
        categoryMenuButton.setText("Category");
    }

    // prepara el formulario para crear un producto nuevo
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
        itemTableView.getSelectionModel().clearSelection();
        mensajeLabel.setText("Rellena los campos y pulsa Guardar.");
    }

    // carga los datos del producto seleccionado en el formulario para editarlo
    @FXML
    public void editarButton() {
        mensajeLabel.setText("");
        Item productoSeleccionado = itemTableView.getSelectionModel().getSelectedItem();
        if (productoSeleccionado == null) {
            mensajeLabel.setText("No hay ningun producto seleccionado.");
            return;
        }
        setTextFieldsDisable(false);
        idTextField.setText(productoSeleccionado.getIdProduct().toString());
        idTextField.setDisable(true);
        nameTextField.setText(productoSeleccionado.getName());
        descriptionTextField.setText(productoSeleccionado.getDescription());
        priceTextField.setText(productoSeleccionado.getPrice().toString());
        stockTextField.setText(productoSeleccionado.getStock().toString());
        categoryMenuButton.setText(productoSeleccionado.getCategory());
        guardarButton.setDisable(false);
        nuevoButton.setDisable(true);
        editarButton.setDisable(true);
        eliminarButton.setDisable(true);
    }

    // elimina el producto seleccionado de la base de datos
    @FXML
    public void eliminarButton() {
        mensajeLabel.setText("");
        Item productoSeleccionado = itemTableView.getSelectionModel().getSelectedItem();
        if (productoSeleccionado == null) {
            mensajeLabel.setText("No hay ningun producto seleccionado.");
            return;
        }
        if (!MantenimientoItem.eliminar(db, productoSeleccionado)) {
            mensajeLabel.setText("Error al eliminar. El producto puede estar en pedidos existentes.");
            return;
        }
        mensajeLabel.setText("Producto eliminado correctamente.");
        listaProductos = MantenimientoItem.consulta(db);
        itemTableView.setItems(listaProductos);
        limpiarCampos();
        setTextFieldsDisable(true);
        guardarButton.setDisable(true);
        nuevoButton.setDisable(false);
    }

    // valida y guarda el producto nuevo o los cambios del producto editado
    @FXML
    public void guardarButton() {
        mensajeLabel.setText("");
        String nombre = nameTextField.getText().trim();
        String descripcion = descriptionTextField.getText().trim();
        String categoria = categoryMenuButton.getText();
        Double precio;
        Integer stock;

        if (nombre.isEmpty()) {
            mensajeLabel.setText("El nombre del producto no puede estar vacio.");
            return;
        }

        // comprobar que la categoria es una de las opciones validas del check constraint
        List<String> categoriasValidas = Arrays.asList(
                "Hardware", "Peripherals", "Gaming", "Storage", "Accessories", "Merchandising"
        );
        if (!categoriasValidas.contains(categoria)) {
            mensajeLabel.setText("Selecciona una categoria valida del desplegable.");
            return;
        }

        try {
            precio = Double.parseDouble(priceTextField.getText());
            if (precio <= 0) {
                mensajeLabel.setText("El precio debe ser mayor que 0.");
                return;
            }
            stock = Integer.parseInt(stockTextField.getText());
            if (stock < 0) {
                mensajeLabel.setText("El stock no puede ser negativo.");
                return;
            }
        } catch (NumberFormatException excepcion) {
            mensajeLabel.setText("Precio y stock deben ser numeros validos.");
            return;
        }

        if (idTextField.getText().isEmpty()) {
            // insertar producto nuevo
            Item nuevoProducto = new Item(null, nombre, descripcion, precio, stock, categoria);
            if (MantenimientoItem.insertar(db, nuevoProducto)) {
                mensajeLabel.setText("Producto insertado correctamente.");
            } else {
                mensajeLabel.setText("Error al insertar. Revisa los datos.");
                return;
            }
        } else {
            // actualizar producto existente
            Integer id = Integer.parseInt(idTextField.getText());
            Item productoEditado = new Item(id, nombre, descripcion, precio, stock, categoria);
            if (MantenimientoItem.guardar(db, productoEditado)) {
                mensajeLabel.setText("Producto modificado correctamente.");
            } else {
                mensajeLabel.setText("Error al guardar. Revisa los datos.");
                return;
            }
        }

        listaProductos = MantenimientoItem.consulta(db);
        itemTableView.setItems(listaProductos);
        limpiarCampos();
        setTextFieldsDisable(true);
        idTextField.setDisable(false);
        guardarButton.setDisable(true);
        nuevoButton.setDisable(false);
    }

    // filtra la tabla por nombre o descripcion del producto
    @FXML
    public void buscarButton() {
        mensajeLabel.setText("");
        String textoBuscar = buscarTextField.getText().toLowerCase().trim();
        if (textoBuscar.isEmpty()) {
            itemTableView.setItems(listaProductos);
            return;
        }
        ObservableList<Item> listaFiltrada = FXCollections.observableArrayList();
        for (Item producto : listaProductos) {
            if (producto.getName().toLowerCase().contains(textoBuscar)
                    || producto.getDescription().toLowerCase().contains(textoBuscar)) {
                listaFiltrada.add(producto);
            }
        }
        itemTableView.setItems(listaFiltrada);
    }

    @FXML
    public void volverButton() throws IOException {
        scottyapp.Main.MainApplication.setRoot("main-view");
    }
}