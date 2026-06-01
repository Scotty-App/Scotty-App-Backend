package scottyapp.Order;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class MantenimientoOrder {

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

    // devuelve todos los pedidos de la tabla order
    public static ObservableList<Order> consulta(Connection conexion) {
        ObservableList<Order> lista = FXCollections.observableArrayList();
        String query = "SELECT * FROM `ORDER`";
        try {
            Statement stmt = conexion.createStatement();
            ResultSet resultado = stmt.executeQuery(query);
            while (resultado.next()) {
                lista.add(new Order(
                        resultado.getInt("idOrder"),
                        resultado.getString("date"),
                        resultado.getDouble("total"),
                        resultado.getString("status"),
                        resultado.getInt("idUser")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return lista;
    }

    // inserta un pedido nuevo y devuelve el id generado, null si hay error
    public static Integer insertar(Connection conexion, Order pedido) {
        String query = "INSERT INTO `ORDER` (date, total, status, idUser) VALUES ('"
                + pedido.getDate() + "',"
                + pedido.getTotal() + ",'"
                + pedido.getStatus() + "',"
                + pedido.getIdUser() + ")";
        try {
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
            ResultSet idGenerado = stmt.getGeneratedKeys();
            if (idGenerado.next()) {
                return idGenerado.getInt(1);
            }
            return null;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // elimina el pedido y todas sus lineas por cascade manual, devuelve false si hay error
    public static boolean eliminar(Connection conexion, Order pedido) {
        try {
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate("DELETE FROM `ORDER_DETAILS` WHERE idOrder = " + pedido.getIdOrder());
            stmt.executeUpdate("DELETE FROM `ORDER` WHERE idOrder = " + pedido.getIdOrder());
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    // actualiza los datos de un pedido existente
    public static void guardar(Connection conexion, Order pedido) {
        String query = "UPDATE `ORDER` SET date = '" + pedido.getDate() + "', "
                + "total = " + pedido.getTotal() + ", "
                + "status = '" + pedido.getStatus() + "', "
                + "idUser = " + pedido.getIdUser() + " "
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