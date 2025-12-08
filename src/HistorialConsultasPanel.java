import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Panel de búsqueda y filtros
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.setBackground(new Color(245, 247, 250));

        campoBusqueda = new JTextField(20);
        campoBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campoBusqueda.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(8, 12, 8, 12)));

        comboTipoConsulta = new JComboBox<>(
                new String[] { "Todos", "Clientes", "Vehículos", "Servicios", "Facturas", "Inventario" });
        comboTipoConsulta.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        comboRangoFechas = new JComboBox<>(
                new String[] { "Todo el tiempo", "Hoy", "Última semana", "Último mes", "Último trimestre" });
        comboRangoFechas.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton botonBuscar = crearBotonEstilizado("Buscar", new Color(30, 60, 114));
        JButton botonLimpiar = crearBotonEstilizado("Limpiar", new Color(100, 100, 100));
        
        // Se eliminó el botón de Consulta Avanzada

        panelBusqueda.add(new JLabel("Buscar:"));
        panelBusqueda.add(campoBusqueda);
        panelBusqueda.add(new JLabel("Tipo:"));
        panelBusqueda.add(comboTipoConsulta);
        panelBusqueda.add(new JLabel("Rango:"));
        panelBusqueda.add(comboRangoFechas);
        panelBusqueda.add(botonBuscar);
        panelBusqueda.add(botonLimpiar);

        panel.add(panelBusqueda, BorderLayout.CENTER);

        // Acciones de los botones
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

        // Modelo de tabla
        String[] columnas = { "ID", "Tipo", "Descripción", "Cliente/Vehículo", "Fecha", "Usuario", "Detalles",
                "Resultado" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaHistorial = new JTable(modeloTabla);
        tablaHistorial.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaHistorial.setRowHeight(30);
        tablaHistorial.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaHistorial.getTableHeader().setBackground(new Color(30, 60, 114));
        tablaHistorial.getTableHeader().setForeground(Color.WHITE);

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

        // Acciones de los botones
        botonVerDetalles.addActionListener(e -> verDetallesHistorial());
        
        // --- IMPLEMENTACIÓN REPORTES ---
        botonGenerarReporte.addActionListener(e -> generarReporteHistorial());
        
        // --- IMPLEMENTACIÓN EXPORTAR ---
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

                if (getModel().isPressed()) {
                    g2.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(color.brighter());
                } else {
                    g2.setColor(color);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };

        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setForeground(Color.WHITE);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setContentAreaFilled(false);
        boton.setPreferredSize(new Dimension(160, 35));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return boton;
    }

    // ==================== MÉTODOS LÓGICOS ====================

    private void cargarDatosHistorial() {
        String query = "SELECT hc.id_consulta, " +
                "hc.tipo_consulta, " +
                "hc.descripcion, " +
                "hc.entidad_consultada, " +
                "DATE_FORMAT(hc.fecha_consulta, '%Y-%m-%d %H:%i') as fecha, " +
                "u.nombre_completo as usuario, " +
                "hc.detalles, " +
                "hc.resultado " +
                "FROM historial_consultas hc " +
                "INNER JOIN usuarios u ON hc.id_usuario = u.id_usuario " +
                "ORDER BY hc.fecha_consulta DESC " +
                "LIMIT 100";

        DatabaseUtils.llenarTablaDesdeConsulta(tablaHistorial, query);
    }

    private void buscarHistorial() {
        String busqueda = campoBusqueda.getText().trim();
        String tipoFiltro = comboTipoConsulta.getSelectedItem().toString();
        String rangoFiltro = comboRangoFechas.getSelectedItem().toString();

        StringBuilder query = new StringBuilder(
                "SELECT hc.id_consulta, " +
                        "hc.tipo_consulta, " +
                        "hc.descripcion, " +
                        "hc.entidad_consultada, " +
                        "DATE_FORMAT(hc.fecha_consulta, '%Y-%m-%d %H:%i') as fecha, " +
                        "u.nombre_completo as usuario, " +
                        "hc.detalles, " +
                        "hc.resultado " +
                        "FROM historial_consultas hc " +
                        "INNER JOIN usuarios u ON hc.id_usuario = u.id_usuario " +
                        "WHERE 1=1");

        List<Object> parametros = new ArrayList<>();

        if (!busqueda.isEmpty()) {
            query.append(" AND (hc.descripcion LIKE ? OR hc.entidad_consultada LIKE ? OR u.nombre_completo LIKE ?)");
            String parametroBusqueda = "%" + busqueda + "%";
            parametros.add(parametroBusqueda);
            parametros.add(parametroBusqueda);
            parametros.add(parametroBusqueda);
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

        // Registrar consulta solo si hay filtros activos
        if (!busqueda.isEmpty() || !tipoFiltro.equals("Todos") || !rangoFiltro.equals("Todo el tiempo")) {
            registrarConsulta("Busqueda Historial", 
                "Filtro: " + tipoFiltro + ", Rango: " + rangoFiltro + ", Busq: " + busqueda, 
                "Historial", "Éxito");
        }

        DatabaseUtils.llenarTablaDesdeConsulta(tablaHistorial, query.toString(), parametros.toArray());
    }

    private String obtenerCondicionFecha(String rango) {
        switch (rango) {
            case "Hoy":
                return "DATE(hc.fecha_consulta) = CURDATE()";
            case "Última semana":
                return "hc.fecha_consulta >= DATE_SUB(NOW(), INTERVAL 7 DAY)";
            case "Último mes":
                return "hc.fecha_consulta >= DATE_SUB(NOW(), INTERVAL 1 MONTH)";
            case "Último trimestre":
                return "hc.fecha_consulta >= DATE_SUB(NOW(), INTERVAL 3 MONTH)";
            default:
                return null;
        }
    }

    private void verDetallesHistorial() {
        int filaSeleccionada = tablaHistorial.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione un registro del historial para ver detalles.",
                    "Ver Detalles", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder detalles = new StringBuilder();
        detalles.append("ID Consulta: ").append(modeloTabla.getValueAt(filaSeleccionada, 0)).append("\n");
        detalles.append("Tipo: ").append(modeloTabla.getValueAt(filaSeleccionada, 1)).append("\n");
        detalles.append("Descripción: ").append(modeloTabla.getValueAt(filaSeleccionada, 2)).append("\n");
        detalles.append("Entidad: ").append(modeloTabla.getValueAt(filaSeleccionada, 3)).append("\n");
        detalles.append("Fecha: ").append(modeloTabla.getValueAt(filaSeleccionada, 4)).append("\n");
        detalles.append("Usuario: ").append(modeloTabla.getValueAt(filaSeleccionada, 5)).append("\n");
        detalles.append("Detalles: ").append(modeloTabla.getValueAt(filaSeleccionada, 6)).append("\n");
        detalles.append("Resultado: ").append(modeloTabla.getValueAt(filaSeleccionada, 7)).append("\n");

        JTextArea textArea = new JTextArea(detalles.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 200));

        JOptionPane.showMessageDialog(this, scrollPane, "Detalles del Registro", JOptionPane.INFORMATION_MESSAGE);
    }

    // --- GENERAR REPORTE (PDF) ---
    private void generarReporteHistorial() {
        ReportePDFUtils.generarReporteTablaPDF(tablaHistorial, "REPORTE DE HISTORIAL DE CONSULTAS", "Reporte_Historial");
        registrarLogSistema("INFO", "Reporte de historial generado en PDF.");
    }

    // --- EXPORTAR DATOS (CSV) ---
    private void exportarHistorial() {
        ExportarUtils.exportarTablaACSV(tablaHistorial, this);
        registrarLogSistema("INFO", "Datos de historial exportados a CSV.");
    }

    private void limpiarHistorial() {
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea limpiar TODO el historial?\n\n" +
                        "ADVERTENCIA: Esta acción eliminará TODOS los registros.\n" +
                        "Se recomienda exportar los datos antes.",
                "Limpiar Historial Completo", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            String query = "DELETE FROM historial_consultas";
            int filasEliminadas = DatabaseUtils.ejecutarUpdate(query);

            if (filasEliminadas > 0) {
                JOptionPane.showMessageDialog(this,
                        "Historial limpiado completamente.\nRegistros eliminados: " + filasEliminadas);
                registrarLogSistema("WARNING", "Limpieza completa de historial. Registros: " + filasEliminadas);
                cargarDatosHistorial();
            }
        }
    }

    private void mostrarEstadisticas() {
        String query = "SELECT 'Total Consultas' as estadistica, COUNT(*) as valor FROM historial_consultas " +
                "UNION ALL " +
                "SELECT 'Consultas Hoy', COUNT(*) FROM historial_consultas WHERE DATE(fecha_consulta) = CURDATE() " +
                "UNION ALL " +
                "SELECT 'Consultas Última Semana', COUNT(*) FROM historial_consultas WHERE fecha_consulta >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
                "UNION ALL " +
                "SELECT 'Tipo Más Consultado', (SELECT tipo_consulta FROM historial_consultas GROUP BY tipo_consulta ORDER BY COUNT(*) DESC LIMIT 1) " +
                "UNION ALL " +
                "SELECT 'Usuario Más Activo', (SELECT u.nombre_completo FROM historial_consultas hc INNER JOIN usuarios u ON hc.id_usuario = u.id_usuario GROUP BY hc.id_usuario ORDER BY COUNT(*) DESC LIMIT 1)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            StringBuilder estadisticas = new StringBuilder();
            estadisticas.append("ESTADÍSTICAS DEL HISTORIAL\n\n");

            while (rs.next()) {
                estadisticas.append(rs.getString("estadistica"))
                        .append(": ")
                        .append(rs.getString("valor"))
                        .append("\n");
            }

            JOptionPane.showMessageDialog(this, estadisticas.toString(), "Estadísticas", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- MÉTODOS AUXILIARES ---

    private void registrarLogSistema(String tipo, String descripcion) {
        int idUsuario = 1; // ID Admin por defecto
        String query = "INSERT INTO logs_sistema (tipo, modulo, descripcion, id_usuario) VALUES (?, ?, ?, ?)";
        DatabaseUtils.ejecutarUpdate(query, tipo, "Historial", descripcion, idUsuario);
    }

    private void registrarConsulta(String tipo, String descripcion, String entidad, String resultado) {
        int idUsuario = 1; // ID Admin por defecto
        String query = "INSERT INTO historial_consultas (tipo_consulta, descripcion, entidad_consultada, id_usuario, detalles, resultado) VALUES (?, ?, ?, ?, ?, ?)";
        DatabaseUtils.ejecutarUpdate(query, tipo, descripcion, entidad, idUsuario, "Consulta desde sistema", resultado);
    }
}