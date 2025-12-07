// Archivo: DatabaseConnection.java

import java.sql.*;
import javax.swing.JOptionPane;

public class DatabaseConnection {
    // ---- INICIO DE CAMBIOS ----

    // 1. CAMBIA "mariadb" POR "mysql" EN LA URL
    private static final String URL = "jdbc:mysql://localhost:3306/taller_automotriz?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    // ---- FIN DE CAMBIOS ----

    private static final String USER = "root";
    private static final String PASSWORD = "irving"; // Asegúrate que esta sea tu contraseña

    public static Connection getConnection() {
        try {
            // ---- INICIO DE CAMBIOS ----

            // 2. USA EL DRIVER DE MYSQL (el que corresponde a tu librería)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // ---- FIN DE CAMBIOS ----

            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Conexión exitosa con MySQL"); // Mensaje actualizado
            return conn;
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                    "Error: Driver MySQL no encontrado (Revisa que 'mysql-connector-java.jar' esté en el proyecto).\n"
                            + e.getMessage(),
                    "Error de Conexión", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Error al conectar con MySQL.\n" + e.getMessage(),
                    "Error de Conexión", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return null;
    }

    // ... (el resto del archivo queda igual)
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Prueba de conexión exitosa");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("❌ Error en prueba de conexión: " + e.getMessage());
        }
        return false;
    }
}