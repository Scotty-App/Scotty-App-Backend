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

    // Consulta los detalles de un pedido concreto filtrando por idOrder
    public static ObservableList<OrderDetails> consulta(Connection conexion, Integer idOrder) {
        ObservableList<OrderDetails> lista = FXCollections.observableArrayList();
        String query = "SELECT * FROM `ORDER_DETAILS` WHERE idOrder = " + idOrder;
        try {
            Statement stmt = conexion.createStatement();
            ResultSet respuesta = stmt.executeQuery(query);
            while (respuesta.next()) {
                lista.add(new OrderDetails(
                        respuesta.getInt("idDetail"),
                        respuesta.getInt("quantity"),
                        respuesta.getDouble("subtotal"),
                        respuesta.getInt("idOrder"),
                        respuesta.getInt("idProduct")
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

    public static void eliminar(Connection conexion, OrderDetails detalle) {
        String query = "DELETE FROM `ORDER_DETAILS` WHERE idDetail = " + detalle.getIdDetail();
        try {
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void guardar(Connection conexion, OrderDetails detalle) {
        String query = "UPDATE `ORDER_DETAILS` SET quantity = " + detalle.getQuantity() + ", "
                + "subtotal = " + detalle.getSubtotal() + " "
                + "WHERE idDetail = " + detalle.getIdDetail();
        try {
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}