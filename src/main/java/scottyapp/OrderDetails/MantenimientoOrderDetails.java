package scottyapp.OrderDetails;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class MantenimientoOrderDetails {

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

    // Devuelve las lineas de un pedido con el nombre del producto incluido via JOIN
    public static ObservableList<OrderDetails> consulta(Connection conexion, Integer idOrder) {
        ObservableList<OrderDetails> lista = FXCollections.observableArrayList();
        String query = "SELECT od.idDetail, od.quantity, od.subtotal, od.idOrder, od.idProduct, p.name AS nombreProducto "
                + "FROM `ORDER_DETAILS` od "
                + "JOIN `PRODUCT` p ON od.idProduct = p.idProduct "
                + "WHERE od.idOrder = " + idOrder;
        try {
            Statement stmt = conexion.createStatement();
            ResultSet resultado = stmt.executeQuery(query);
            while (resultado.next()) {
                lista.add(new OrderDetails(
                        resultado.getInt("idDetail"),
                        resultado.getInt("quantity"),
                        resultado.getDouble("subtotal"),
                        resultado.getInt("idOrder"),
                        resultado.getInt("idProduct"),
                        resultado.getString("nombreProducto")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return lista;
    }

    public static boolean insertar(Connection conexion, OrderDetails detalle) {
        String query = "INSERT INTO `ORDER_DETAILS` (quantity, subtotal, idOrder, idProduct) VALUES ("
                + detalle.getQuantity() + ","
                + detalle.getSubtotal() + ","
                + detalle.getIdOrder() + ","
                + detalle.getIdProduct() + ")";
        try {
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static boolean eliminar(Connection conexion, OrderDetails detalle) {
        String query = "DELETE FROM `ORDER_DETAILS` WHERE idDetail = " + detalle.getIdDetail();
        try {
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static boolean guardar(Connection conexion, OrderDetails detalle) {
        String query = "UPDATE `ORDER_DETAILS` SET quantity = " + detalle.getQuantity() + ", "
                + "subtotal = " + detalle.getSubtotal() + " "
                + "WHERE idDetail = " + detalle.getIdDetail();
        try {
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    // Recalcula el total del pedido sumando todos sus subtotales
    public static void recalcularTotal(Connection conexion, Integer idOrder) {
        String query = "UPDATE `ORDER` SET total = ("
                + "SELECT COALESCE(SUM(subtotal), 0) FROM `ORDER_DETAILS` WHERE idOrder = " + idOrder
                + ") WHERE idOrder = " + idOrder;
        try {
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}