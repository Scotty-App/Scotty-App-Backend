package scottyapp.Usuario;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class MantenimientoUsuario {

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

    // devuelve todos los usuarios de la tabla user
    public static ObservableList<Usuario> consulta(Connection conexion) {
        ObservableList<Usuario> lista = FXCollections.observableArrayList();
        String query = "SELECT * FROM `USER`";
        try {
            Statement stmt = conexion.createStatement();
            ResultSet resultado = stmt.executeQuery(query);
            while (resultado.next()) {
                lista.add(new Usuario(
                        resultado.getInt("idUser"),
                        resultado.getString("name"),
                        resultado.getString("email"),
                        resultado.getString("password"),
                        resultado.getString("role"),
                        resultado.getString("address"),
                        resultado.getString("phone")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return lista;
    }

    // inserta un usuario nuevo, devuelve false si el email ya existe
    public static boolean insertar(Connection conexion, Usuario usuario) {
        String query = "INSERT INTO `USER` (name, email, password, role, address, phone) VALUES ('"
                + usuario.getName() + "','"
                + usuario.getEmail() + "','"
                + usuario.getPassword() + "','"
                + usuario.getRole() + "','"
                + usuario.getAddress() + "',"
                + (usuario.getPhone() == null ? "NULL" : "'" + usuario.getPhone() + "'") + ")";
        try {
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    // elimina un usuario por su id, devuelve false si hay error
    public static boolean eliminar(Connection conexion, Usuario usuario) {
        String query = "DELETE FROM `USER` WHERE idUser = " + usuario.getIdUser();
        try {
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    // actualiza los datos de un usuario existente, devuelve false si hay error
    public static boolean guardar(Connection conexion, Usuario usuario) {
        String query = "UPDATE `USER` SET name = '" + usuario.getName() + "', "
                + "email = '" + usuario.getEmail() + "', "
                + "password = '" + usuario.getPassword() + "', "
                + "role = '" + usuario.getRole() + "', "
                + "address = '" + usuario.getAddress() + "', "
                + "phone = " + (usuario.getPhone() == null ? "NULL" : "'" + usuario.getPhone() + "'") + " "
                + "WHERE idUser = " + usuario.getIdUser();
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