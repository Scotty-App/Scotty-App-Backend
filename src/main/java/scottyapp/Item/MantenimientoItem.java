package scottyapp.Item;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class MantenimientoItem {

    public static Connection conexion() {
        Connection conexion;
        String host = "jdbc:mariadb://localhost:3307/";
        String user = "root";
        String psw = "";
        String bd = "bd_scottyapp";
        System.out.println("Conectando...");
        try {
            conexion = DriverManager.getConnection(host + bd, user, psw);
            System.out.println("Conexión realizada con éxito.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return conexion;
    }

    public static ObservableList<Item> consulta(Connection conexion) {
        ObservableList<Item> lista = FXCollections.observableArrayList();
        String query = "SELECT * FROM `PRODUCT`";
        try {
            Statement stmt = conexion.createStatement();
            ResultSet respuesta = stmt.executeQuery(query);
            while (respuesta.next()) {
                lista.add(new Item(
                        respuesta.getInt("idProduct"),
                        respuesta.getString("name"),
                        respuesta.getString("description"),
                        respuesta.getDouble("price"),
                        respuesta.getInt("stock"),
                        respuesta.getString("category")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return lista;
    }

    public static boolean insertar(Connection conexion, Item item) {
        String query = "INSERT INTO `PRODUCT` (name, description, price, stock, category) VALUES ('"
                + item.getName() + "','"
                + item.getDescription() + "',"
                + item.getPrice() + ","
                + item.getStock() + ",'"
                + item.getCategory() + "')";
        try {
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static void eliminar(Connection conexion, Item item) {
        String query = "DELETE FROM `PRODUCT` WHERE idProduct = " + item.getIdProduct();
        try {
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void guardar(Connection conexion, Item item) {
        String query = "UPDATE `PRODUCT` SET name = '" + item.getName() + "', "
                + "description = '" + item.getDescription() + "', "
                + "price = " + item.getPrice() + ", "
                + "stock = " + item.getStock() + ", "
                + "category = '" + item.getCategory() + "' "
                + "WHERE idProduct = " + item.getIdProduct();
        try {
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
