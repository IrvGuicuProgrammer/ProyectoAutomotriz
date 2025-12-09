// Archivo: DatabaseUtils.java
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DatabaseUtils {

    // CAMBIO REALIZADO: Se cambió 'PreparedStatement stmt' por 'Statement stmt'
    // Esto permite cerrar tanto Statements normales como PreparedStatements.
    public static void cerrarRecursos(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void llenarTablaDesdeConsulta(JTable tabla, String query, Object... parametros) {
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0); // Limpiar tabla

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(query);

            // Establecer parámetros
            for (int i = 0; i < parametros.length; i++) {
                stmt.setObject(i + 1, parametros[i]);
            }

            rs = stmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Object[] fila = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    fila[i] = rs.getObject(i + 1);
                }
                modelo.addRow(fila);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error al cargar datos: " + e.getMessage(),
                    "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    public static int ejecutarUpdate(String query, Object... parametros) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(query);

            for (int i = 0; i < parametros.length; i++) {
                stmt.setObject(i + 1, parametros[i]);
            }

            return stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error en operación: " + e.getMessage(),
                    "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            return -1;
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
    }
}