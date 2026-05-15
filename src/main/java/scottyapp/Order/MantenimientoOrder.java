package scottyapp.Order;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class MantenimientoOrder {

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

    // Consulta todos los pedidos de la base de datos
    public static ObservableList<Order> consulta(Connection conexion) {
        ObservableList<Order> lista = FXCollections.observableArrayList();
        String query = "SELECT * FROM `ORDER`";
        try {
            Statement stmt = conexion.createStatement();
            ResultSet respuesta = stmt.executeQuery(query);
            while (respuesta.next()) {
                lista.add(new Order(
                        respuesta.getInt("idOrder"),
                        respuesta.getString("date"),
                        respuesta.getDouble("total"),
                        respuesta.getString("status"),
                        respuesta.getInt("idUser")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return lista;
    }

    public static boolean insertar(Connection conexion, Order pedido) {
        String query = "INSERT INTO `ORDER` (date, total, status, idUser) VALUES ('"
                + pedido.getDate() + "',"
                + pedido.getTotal() + ",'"
                + pedido.getStatus() + "',"
                + pedido.getIdUser() + ")";
        try {
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static void eliminar(Connection conexion, Order pedido) {
        String query = "DELETE FROM `ORDER` WHERE idOrder = " + pedido.getIdOrder();
        try {
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void guardar(Connection conexion, Order pedido) {
        String query = "UPDATE `ORDER` SET status = '" + pedido.getStatus() + "', "
                + "total = " + pedido.getTotal() + " "
                + "WHERE idOrder = " + pedido.getIdOrder();
        try {
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}