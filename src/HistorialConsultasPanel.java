import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistorialConsultasPanel extends JPanel {
    private JTable tablaHistorial;
    private DefaultTableModel modeloTabla;
    private JTextField campoBusqueda;
    private JComboBox<String> comboTipoConsulta;
    private JComboBox<String> comboRangoFechas;

    public HistorialConsultasPanel() {
        inicializarComponentes();
        cargarDatosHistorial();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        // Barra de título
        JPanel barraTitulo = crearBarraTitulo();
        add(barraTitulo, BorderLayout.NORTH);

        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(245, 247, 250));
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel superior con búsqueda
        JPanel panelSuperior = crearPanelSuperior();
        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);

        // Panel central con tabla
        JPanel panelCentral = crearPanelCentral();
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);

        // Panel inferior con botones de acción
        JPanel panelInferior = crearPanelInferior();
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);

        add(panelPrincipal, BorderLayout.CENTER);
    }

    private JPanel crearBarraTitulo() {
        JPanel barraTitulo = new JPanel(new BorderLayout());
        barraTitulo.setBackground(new Color(30, 60, 114));
        barraTitulo.setBorder(new EmptyBorder(15, 25, 15, 25));
        barraTitulo.setPreferredSize(new Dimension(0, 60));

        JLabel titulo = new JLabel("HISTORIAL Y CONSULTAS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        titulo.setHorizontalAlignment(SwingConstants.LEFT);

        barraTitulo.add(titulo, BorderLayout.WEST);
        return barraTitulo;
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));

        campoBusqueda = new JTextField(20);
        campoBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campoBusqueda.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(8, 12, 8, 12)));

        comboTipoConsulta = new JComboBox<>(
                new String[] { "Todos", "Sistema", "Clientes", "Vehículos", "Servicios", "Facturas", "Inventario" });
        comboTipoConsulta.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        comboRangoFechas = new JComboBox<>(
                new String[] { "Todo el tiempo", "Hoy", "Última semana", "Último mes" });
        comboRangoFechas.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton botonBuscar = crearBotonEstilizado("Buscar", new Color(30, 60, 114));
        JButton botonLimpiar = crearBotonEstilizado("Limpiar", new Color(100, 100, 100));

        panel.add(new JLabel("Buscar:"));
        panel.add(campoBusqueda);
        panel.add(new JLabel("Tipo:"));
        panel.add(comboTipoConsulta);
        panel.add(new JLabel("Rango:"));
        panel.add(comboRangoFechas);
        panel.add(botonBuscar);
        panel.add(botonLimpiar);

        // Acciones
        botonBuscar.addActionListener(e -> buscarHistorial());
        botonLimpiar.addActionListener(e -> {
            campoBusqueda.setText("");
            comboTipoConsulta.setSelectedIndex(0);
            comboRangoFechas.setSelectedIndex(0);
            cargarDatosHistorial();
        });

        return panel;
    }

    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 10, 10, 10)));

        String[] columnas = { "ID", "Tipo", "Descripción", "Entidad", "Fecha", "Usuario", "Detalles", "Resultado" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaHistorial = new JTable(modeloTabla);
        tablaHistorial.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaHistorial.setRowHeight(25);
        tablaHistorial.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaHistorial.getTableHeader().setBackground(new Color(30, 60, 114));
        tablaHistorial.getTableHeader().setForeground(Color.WHITE);

        // Anchos de columna personalizados
        tablaHistorial.getColumnModel().getColumn(0).setPreferredWidth(40); // ID
        tablaHistorial.getColumnModel().getColumn(1).setPreferredWidth(80); // Tipo
        tablaHistorial.getColumnModel().getColumn(2).setPreferredWidth(150); // Desc
        tablaHistorial.getColumnModel().getColumn(4).setPreferredWidth(120); // Fecha

        JScrollPane scrollPane = new JScrollPane(tablaHistorial);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton botonVerDetalles = crearBotonEstilizado("Ver Detalles", new Color(30, 60, 114));
        JButton botonGenerarReporte = crearBotonEstilizado("Generar Reporte", new Color(40, 167, 69));
        JButton botonExportar = crearBotonEstilizado("Exportar Datos", new Color(255, 193, 7));
        JButton botonLimpiarHistorial = crearBotonEstilizado("Limpiar Historial", new Color(220, 53, 69));
        JButton botonEstadisticas = crearBotonEstilizado("Estadísticas", new Color(111, 66, 193));

        panel.add(botonVerDetalles);
        panel.add(botonGenerarReporte);
        panel.add(botonExportar);
        panel.add(botonLimpiarHistorial);
        panel.add(botonEstadisticas);

        botonVerDetalles.addActionListener(e -> verDetallesHistorial());
        botonGenerarReporte.addActionListener(e -> generarReporteHistorial());
        botonExportar.addActionListener(e -> exportarHistorial());
        botonLimpiarHistorial.addActionListener(e -> limpiarHistorial());
        botonEstadisticas.addActionListener(e -> mostrarEstadisticas());

        return panel;
    }

    private JButton crearBotonEstilizado(String texto, Color color) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) g2.setColor(color.darker());
                else if (getModel().isRollover()) g2.setColor(color.brighter());
                else g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setForeground(Color.WHITE);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setContentAreaFilled(false);
        boton.setPreferredSize(new Dimension(140, 35));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }

    private void cargarDatosHistorial() {
        // CORRECCIÓN: Usar LEFT JOIN para mostrar historial aunque el usuario se haya borrado
        // Usar IFNULL para mostrar 'Sistema' o 'Desconocido' si no hay usuario
        String query = "SELECT hc.id_consulta, hc.tipo_consulta, hc.descripcion, hc.entidad_consultada, " +
                "DATE_FORMAT(hc.fecha_consulta, '%Y-%m-%d %H:%i') as fecha, " +
                "IFNULL(u.nombre_completo, 'Sistema/Eliminado') as usuario, " +
                "hc.detalles, hc.resultado " +
                "FROM historial_consultas hc " +
                "LEFT JOIN usuarios u ON hc.id_usuario = u.id_usuario " +
                "ORDER BY hc.fecha_consulta DESC LIMIT 100";

        DatabaseUtils.llenarTablaDesdeConsulta(tablaHistorial, query);
    }

    private void buscarHistorial() {
        String busqueda = campoBusqueda.getText().trim();
        String tipoFiltro = comboTipoConsulta.getSelectedItem().toString();
        String rangoFiltro = comboRangoFechas.getSelectedItem().toString();

        StringBuilder query = new StringBuilder(
                "SELECT hc.id_consulta, hc.tipo_consulta, hc.descripcion, hc.entidad_consultada, " +
                        "DATE_FORMAT(hc.fecha_consulta, '%Y-%m-%d %H:%i') as fecha, " +
                        "IFNULL(u.nombre_completo, 'Sistema/Eliminado') as usuario, " +
                        "hc.detalles, hc.resultado " +
                        "FROM historial_consultas hc " +
                        "LEFT JOIN usuarios u ON hc.id_usuario = u.id_usuario " +
                        "WHERE 1=1");

        List<Object> parametros = new ArrayList<>();

        if (!busqueda.isEmpty()) {
            query.append(" AND (hc.descripcion LIKE ? OR hc.entidad_consultada LIKE ? OR IFNULL(u.nombre_completo, '') LIKE ?)");
            String p = "%" + busqueda + "%";
            parametros.add(p);
            parametros.add(p);
            parametros.add(p);
        }

        if (!tipoFiltro.equals("Todos")) {
            query.append(" AND hc.tipo_consulta = ?");
            parametros.add(tipoFiltro);
        }

        if (!rangoFiltro.equals("Todo el tiempo")) {
            String condicionFecha = obtenerCondicionFecha(rangoFiltro);
            if (condicionFecha != null) {
                query.append(" AND ").append(condicionFecha);
            }
        }

        query.append(" ORDER BY hc.fecha_consulta DESC LIMIT 100");

        // Registrar que se hizo una búsqueda (¡Esto hace que el historial "genere" datos!)
        if (!busqueda.isEmpty() || !tipoFiltro.equals("Todos")) {
            registrarConsulta("Sistema", "Búsqueda en historial", "Panel Historial", "Filtros aplicados");
        }

        DatabaseUtils.llenarTablaDesdeConsulta(tablaHistorial, query.toString(), parametros.toArray());
    }

    private String obtenerCondicionFecha(String rango) {
        switch (rango) {
            case "Hoy": return "DATE(hc.fecha_consulta) = CURDATE()";
            case "Última semana": return "hc.fecha_consulta >= DATE_SUB(NOW(), INTERVAL 7 DAY)";
            case "Último mes": return "hc.fecha_consulta >= DATE_SUB(NOW(), INTERVAL 1 MONTH)";
            default: return null;
        }
    }

    // Método para insertar en la base de datos (Soluciona el "no genera")
    private void registrarConsulta(String tipo, String descripcion, String entidad, String resultado) {
        // Usamos ID 1 (Admin) por defecto si no hay sesión global implementada
        int idUsuario = 1; 
        String sql = "INSERT INTO historial_consultas (tipo_consulta, descripcion, entidad_consultada, id_usuario, detalles, resultado, fecha_consulta) " +
                     "VALUES (?, ?, ?, ?, ?, ?, NOW())";
        
        // Ejecutamos silenciosamente (sin mostrar popup de éxito cada vez)
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tipo);
            ps.setString(2, descripcion);
            ps.setString(3, entidad);
            ps.setInt(4, idUsuario);
            ps.setString(5, "Acción registrada desde el panel");
            ps.setString(6, resultado);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al registrar historial: " + e.getMessage());
        }
    }

    private void verDetallesHistorial() {
        int fila = tablaHistorial.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tablaHistorial.getColumnCount(); i++) {
            sb.append(tablaHistorial.getColumnName(i)).append(": ")
              .append(tablaHistorial.getValueAt(fila, i)).append("\n\n");
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Detalle Completo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void generarReporteHistorial() {
        ReportePDFUtils.generarReporteTablaPDF(tablaHistorial, "REPORTE DE HISTORIAL", "Reporte_Historial");
        registrarConsulta("Reportes", "Generación de PDF", "Historial", "Exitoso");
    }

    private void exportarHistorial() {
        ExportarUtils.exportarTablaACSV(tablaHistorial, this);
        registrarConsulta("Reportes", "Exportación CSV", "Historial", "Exitoso");
    }

    private void limpiarHistorial() {
        if (JOptionPane.showConfirmDialog(this, "¿Borrar TODO el historial?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            DatabaseUtils.ejecutarUpdate("DELETE FROM historial_consultas");
            cargarDatosHistorial();
        }
    }

    private void mostrarEstadisticas() {
        // Consulta corregida para evitar errores si la tabla está vacía
        String sql = "SELECT 'Total Registros', COUNT(*) FROM historial_consultas UNION ALL " +
                     "SELECT 'Eventos de Hoy', COUNT(*) FROM historial_consultas WHERE DATE(fecha_consulta) = CURDATE()";
        
        StringBuilder stats = new StringBuilder("=== ESTADÍSTICAS ===\n\n");
        try (Connection c = DatabaseConnection.getConnection(); 
             Statement s = c.createStatement(); 
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                stats.append(rs.getString(1)).append(": ").append(rs.getString(2)).append("\n");
            }
            JOptionPane.showMessageDialog(this, stats.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}