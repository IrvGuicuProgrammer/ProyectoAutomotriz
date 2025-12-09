import java.sql.*;
import javax.swing.JOptionPane;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/taller_automotriz?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "irving"; 

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Conexión exitosa con MySQL"); 
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