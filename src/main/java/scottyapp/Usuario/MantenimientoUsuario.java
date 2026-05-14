package scottyapp.Usuario;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class MantenimientoUsuario {

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

    public static ObservableList<Usuario> consulta(Connection conexion) {
        ObservableList<Usuario> lista = FXCollections.observableArrayList();
        String query = "SELECT * FROM `user`";
        try {
            Statement stmt = conexion.createStatement();
            ResultSet respuesta = stmt.executeQuery(query);
            while (respuesta.next()) {
                lista.add(new Usuario(
                        respuesta.getInt("idUser"),
                        respuesta.getString("name"),
                        respuesta.getString("email"),
                        respuesta.getString("password"),
                        respuesta.getString("role"),
                        respuesta.getString("address"),
                        respuesta.getString("phone")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return lista;
    }

    public static boolean insertar(Connection conexion, Usuario usuario) {
        String query = "INSERT INTO `user` (name, email, password, role, address, phone) VALUES ('"
                + usuario.getName() + "','"
                + usuario.getEmail() + "','"
                + usuario.getPassword() + "','"
                + usuario.getRole() + "','"
                + usuario.getAddress() + "','"
                + usuario.getPhone() + "')";
        try {
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static void eliminar(Connection conexion, Usuario usuario) {
        String query = "DELETE FROM `user` WHERE idUser = " + usuario.getIdUser();
        try {
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void guardar(Connection conexion, Usuario usuario) {
        String query = "UPDATE `user` SET name = '" + usuario.getName() + "', "
                + "email = '" + usuario.getEmail() + "', "
                + "password = '" + usuario.getPassword() + "', "
                + "role = '" + usuario.getRole() + "', "
                + "address = '" + usuario.getAddress() + "', "
                + "phone = '" + usuario.getPhone() + "' "
                + "WHERE idUser = " + usuario.getIdUser();
        try {
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}