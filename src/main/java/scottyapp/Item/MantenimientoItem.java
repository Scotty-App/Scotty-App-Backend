package scottyapp.Item;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class MantenimientoItem {

    // abre conexion con la base de datos
    public static Connection conexion() {
        Connection conexion;
        String host = "jdbc:mariadb://localhost:3307/";
        String user = "root";
        String psw = "";
        String bd = "bd_scottyapp";
        System.out.println("Conectando...");
        try {
            conexion = DriverManager.getConnection(host + bd, user, psw);
            System.out.println("Conexion realizada con exito.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return conexion;
    }

    // devuelve todos los productos de la tabla product
    public static ObservableList<Item> consulta(Connection conexion) {
        ObservableList<Item> lista = FXCollections.observableArrayList();
        String query = "SELECT * FROM `PRODUCT`";
        try {
            Statement stmt = conexion.createStatement();
            ResultSet resultado = stmt.executeQuery(query);
            while (resultado.next()) {
                lista.add(new Item(
                        resultado.getInt("idProduct"),
                        resultado.getString("name"),
                        resultado.getString("description"),
                        resultado.getDouble("price"),
                        resultado.getInt("stock"),
                        resultado.getString("category")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return lista;
    }

    // inserta un producto nuevo, devuelve false si hay error
    public static boolean insertar(Connection conexion, Item producto) {
        String query = "INSERT INTO `PRODUCT` (name, description, price, stock, category) VALUES ('"
                + producto.getName() + "','"
                + producto.getDescription() + "',"
                + producto.getPrice() + ","
                + producto.getStock() + ",'"
                + producto.getCategory() + "')";
        try {
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    // elimina un producto por su id, devuelve false si hay error
    public static boolean eliminar(Connection conexion, Item producto) {
        String query = "DELETE FROM `PRODUCT` WHERE idProduct = " + producto.getIdProduct();
        try {
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    // actualiza los datos de un producto existente, devuelve false si hay error
    public static boolean guardar(Connection conexion, Item producto) {
        String query = "UPDATE `PRODUCT` SET name = '" + producto.getName() + "', "
                + "description = '" + producto.getDescription() + "', "
                + "price = " + producto.getPrice() + ", "
                + "stock = " + producto.getStock() + ", "
                + "category = '" + producto.getCategory() + "' "
                + "WHERE idProduct = " + producto.getIdProduct();
        try {
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}