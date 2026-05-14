package scottyapp.Item;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.*;

public class ItemController {

    static Connection db;
    private ObservableList<Item> listaItem;

    @FXML public TableView<Item> itemTableView;
    @FXML public TableColumn<Item, String> nameColumn;
    @FXML public TableColumn<Item, String> descriptionlColumn;
    @FXML public TableColumn<Item, Double> priceColumn;
    @FXML public TableColumn<Item, Integer> stockColumn;
    @FXML public TableColumn<Item, String> categoryColumn;

    @FXML public TextField buscarTextField;
    @FXML public TextField nameTextField;
    @FXML public TextField descriptionTextField;
    @FXML public TextField priceTextField;
    @FXML public TextField stockTextField;
    @FXML public TextField categoryTextField;

    @FXML public Button buscarButton;
    @FXML public Button nuevoButton;
    @FXML public Button editarButton;
    @FXML public Button eliminarButton;
    @FXML public Button guardarButton;
    @FXML public Label mensajeLabel;

    @FXML
    public void initialize() {
        db = MantenimientoItem.conexion();

        nameColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleStringProperty(datos.getValue().getName()));
        descriptionlColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleStringProperty(datos.getValue().getDescription()));
        priceColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleDoubleProperty(datos.getValue().getPrice()).asObject());
        stockColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleIntegerProperty(datos.getValue().getStock()).asObject());
        categoryColumn.setCellValueFactory(datos -> new javafx.beans.property.SimpleStringProperty(datos.getValue().getDescription()));

        guardarButton.setDisable(true);
        editarButton.setDisable(true);
        eliminarButton.setDisable(true);
        setTextFieldsDisable(true);

        listaItem = MantenimientoItem.consulta(db);
        itemTableView.setItems(listaItem);

        itemTableView.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            if (seleccionado != null) {
                editarButton.setDisable(false);
                eliminarButton.setDisable(false);
            } else {
                editarButton.setDisable(true);
                eliminarButton.setDisable(true);
            }
        });

    }

    private void setTextFieldsDisable(boolean disabled) {
        nameTextField.setDisable(disabled);
        descriptionTextField.setDisable(disabled);
        priceTextField.setDisable(disabled);
        stockTextField.setDisable(disabled);
        categoryTextField.setDisable(disabled);
    }

    private void limpiarCampos() {
        nameTextField.clear();
        descriptionTextField.clear();
        priceTextField.clear();
        stockTextField.clear();
        categoryTextField.clear();
    }

    @FXML
    public void nuevoButton() {
        limpiarCampos();
        setTextFieldsDisable(false);
        nameTextField.setDisable(true);
        guardarButton.setDisable(false);
        nuevoButton.setDisable(true);
        editarButton.setDisable(true);
        eliminarButton.setDisable(true);
        itemTableView.getSelectionModel().clearSelection();
        mensajeLabel.setText("Rellena los campos y pulsa Guardar.");
    }

    @FXML
    public void editarButton() {
        Item seleccionado = itemTableView.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mensajeLabel.setText("No hay ningún usuario seleccionado.");
        } else {
            setTextFieldsDisable(false);
            nameTextField.setText(seleccionado.getName());
            nameTextField.setDisable(true);
            descriptionTextField.setText(seleccionado.getDescription());
            priceTextField.setText(seleccionado.getPrice().toString());
            stockTextField.setText(seleccionado.getStock().toString());
            categoryTextField.setText(seleccionado.getCategory());
            guardarButton.setDisable(false);
            nuevoButton.setDisable(true);
            editarButton.setDisable(true);
            eliminarButton.setDisable(true);
        }
    }

    @FXML
    public void eliminarButton() {
        Item seleccionado = itemTableView.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mensajeLabel.setText("No hay ningún item seleccionado.");
        } else {
            MantenimientoItem.eliminar(db, seleccionado);
            mensajeLabel.setText("Item eliminado.");
            listaItem = MantenimientoItem.consulta(db);
            itemTableView.setItems(listaItem);
            limpiarCampos();
            setTextFieldsDisable(true);
            guardarButton.setDisable(true);
            nuevoButton.setDisable(false);
        }
    }

    @FXML
    public void guardarButton() {
        String name = nameTextField.getText();
        String description = descriptionTextField.getText();
        Double price = Double.valueOf(priceTextField.getText());
        Integer stock = Integer.valueOf(stockTextField.getText());
        String category = categoryTextField.getText();

        Item nuevo = new Item(name, description, price, stock, category);
        MantenimientoItem.insertar(db, nuevo);

        listaItem = MantenimientoItem.consulta(db);
        itemTableView.setItems(listaItem);
        limpiarCampos();
        setTextFieldsDisable(true);
        nameTextField.setDisable(false);
        guardarButton.setDisable(true);
        nuevoButton.setDisable(false);
    }

    @FXML
    public void buscarButton() {
        String textoBuscar = buscarTextField.getText().toLowerCase();
        ObservableList<Item> listaFiltrada = FXCollections.observableArrayList();
        for (Item item : listaItem) {
            if (item.getName().toLowerCase().contains(textoBuscar)
                    || item.getDescription().toLowerCase().contains(textoBuscar)) {
                listaFiltrada.add(item);
            }
        }
        if (textoBuscar.isEmpty()) {
            itemTableView.setItems(listaItem);
        } else {
            itemTableView.setItems(listaFiltrada);
        }
    }

    @FXML
    public void volverButton() throws IOException {
        scottyapp.Main.MainApplication.setRoot("main-view");
    }
}
