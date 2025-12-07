import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
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
                new String[] { "Todo el tiempo", "Hoy", "Última semana", "Último mes", "Último trimestre",
                        "Personalizado" });
        comboRangoFechas.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton botonBuscar = crearBotonEstilizado("🔍 Buscar", new Color(30, 60, 114));
        JButton botonLimpiar = crearBotonEstilizado("🔄 Limpiar", new Color(100, 100, 100));
        JButton botonConsultaAvanzada = crearBotonEstilizado("📊 Consulta Avanzada", new Color(40, 167, 69));

        panelBusqueda.add(new JLabel("Buscar:"));
        panelBusqueda.add(campoBusqueda);
        panelBusqueda.add(new JLabel("Tipo:"));
        panelBusqueda.add(comboTipoConsulta);
        panelBusqueda.add(new JLabel("Rango:"));
        panelBusqueda.add(comboRangoFechas);
        panelBusqueda.add(botonBuscar);
        panelBusqueda.add(botonLimpiar);
        panelBusqueda.add(botonConsultaAvanzada);

        panel.add(panelBusqueda, BorderLayout.CENTER);

        // Acciones de los botones
        botonBuscar.addActionListener(e -> buscarHistorial());
        botonLimpiar.addActionListener(e -> {
            campoBusqueda.setText("");
            comboTipoConsulta.setSelectedIndex(0);
            comboRangoFechas.setSelectedIndex(0);
            cargarDatosHistorial();
        });
        botonConsultaAvanzada.addActionListener(e -> mostrarConsultaAvanzada());

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

        JButton botonVerDetalles = crearBotonEstilizado("📋 Ver Detalles", new Color(30, 60, 114));
        JButton botonGenerarReporte = crearBotonEstilizado("📊 Generar Reporte", new Color(40, 167, 69));
        JButton botonExportar = crearBotonEstilizado("💾 Exportar Datos", new Color(255, 193, 7));
        JButton botonLimpiarHistorial = crearBotonEstilizado("🗑️ Limpiar Historial", new Color(220, 53, 69));
        JButton botonEstadisticas = crearBotonEstilizado("📈 Estadísticas", new Color(111, 66, 193));

        panel.add(botonVerDetalles);
        panel.add(botonGenerarReporte);
        panel.add(botonExportar);
        panel.add(botonLimpiarHistorial);
        panel.add(botonEstadisticas);

        // Acciones de los botones
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

    // MÉTODOS PARA HISTORIAL Y CONSULTAS
    private void cargarDatosHistorial() {
        // Consulta que combina datos del historial_consultas con información de otras
        // tablas
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

        // Filtro por rango de fechas
        if (!rangoFiltro.equals("Todo el tiempo")) {
            String condicionFecha = obtenerCondicionFecha(rangoFiltro);
            if (condicionFecha != null) {
                query.append(" AND ").append(condicionFecha);
            }
        }

        query.append(" ORDER BY hc.fecha_consulta DESC LIMIT 100");

        // --- INICIO DE CORRECCIÓN ---
        // Registrar la búsqueda en el historial (si no es una búsqueda vacía)
        if (!busqueda.isEmpty() || !tipoFiltro.equals("Todos") || !rangoFiltro.equals("Todo el tiempo")) {
            String tipoLog = tipoFiltro;
            if (tipoLog.equals("Todos")) {
                tipoLog = "Servicios"; // Usar un valor por defecto válido para el ENUM
            }
            String descripcionLog = "Búsqueda: '" + busqueda + "', Tipo: '" + tipoFiltro + "', Rango: '" + rangoFiltro
                    + "'";
            registrarConsulta(tipoLog, descripcionLog, "Filtro: " + tipoFiltro, "Búsqueda realizada");
        }
        // --- FIN DE CORRECCIÓN ---

        DatabaseUtils.llenarTablaDesdeConsulta(tablaHistorial, query.toString(), parametros.toArray());
    }

    private String obtenerCondicionFecha(String rango) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        switch (rango) {
            case "Hoy":
                return "DATE(hc.fecha_consulta) = CURDATE()";
            case "Última semana":
                return "hc.fecha_consulta >= DATE_SUB(NOW(), INTERVAL 7 DAY)";
            case "Último mes":
                return "hc.fecha_consulta >= DATE_SUB(NOW(), INTERVAL 1 MONTH)";
            case "Último trimestre":
                return "hc.fecha_consulta >= DATE_SUB(NOW(), INTERVAL 3 MONTH)";
            case "Personalizado":
                // Para personalizado, podrías agregar un diálogo para fechas
                // Por ahora, retornamos null o implementamos simple
                return null;
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

        JOptionPane.showMessageDialog(this, scrollPane,
                "Detalles del Registro", JOptionPane.INFORMATION_MESSAGE);
    }

    private void generarReporteHistorial() {
        // Implementación básica: Generar un reporte simple en texto
        StringBuilder reporte = new StringBuilder();
        reporte.append("REPORTE DE HISTORIAL DE CONSULTAS\n\n");
        reporte.append("Fecha de generación: ")
                .append(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append("\n\n");

        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            for (int j = 0; j < modeloTabla.getColumnCount(); j++) {
                reporte.append(modeloTabla.getColumnName(j)).append(": ").append(modeloTabla.getValueAt(i, j))
                        .append("\n");
            }
            reporte.append("----------------------------------------\n");
        }

        JTextArea textArea = new JTextArea(reporte.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(this, scrollPane,
                "Reporte de Historial", JOptionPane.INFORMATION_MESSAGE);

        // --- INICIO DE CORRECCIÓN ---
        // Registrar la generación del reporte en logs_sistema
        registrarLogSistema("INFO",
                "Generación de reporte de historial. Registros incluidos: " + modeloTabla.getRowCount());
        // --- FIN DE CORRECCIÓN ---
    }

    private void exportarHistorial() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exportar Historial");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        javax.swing.filechooser.FileNameExtensionFilter csvFilter = new javax.swing.filechooser.FileNameExtensionFilter(
                "Archivos CSV (*.csv)", "csv");

        fileChooser.addChoosableFileFilter(csvFilter);
        fileChooser.setFileFilter(csvFilter);

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.endsWith(".csv")) {
                filePath += ".csv";
                fileToSave = new File(filePath);
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                // Escribir encabezados
                for (int i = 0; i < modeloTabla.getColumnCount(); i++) {
                    writer.write("\"" + modeloTabla.getColumnName(i) + "\"");
                    if (i < modeloTabla.getColumnCount() - 1) {
                        writer.write(",");
                    }
                }
                writer.newLine();

                // Escribir datos
                for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                    for (int j = 0; j < modeloTabla.getColumnCount(); j++) {
                        String value = "NULL";
                        if (modeloTabla.getValueAt(i, j) != null) {
                            value = modeloTabla.getValueAt(i, j).toString().replace("\"", "\"\"");
                        }
                        writer.write("\"" + value + "\"");
                        if (j < modeloTabla.getColumnCount() - 1) {
                            writer.write(",");
                        }
                    }
                    writer.newLine();
                }

                JOptionPane.showMessageDialog(this,
                        "Historial exportado correctamente a: " + filePath,
                        "Exportar Historial", JOptionPane.INFORMATION_MESSAGE);

                // --- INICIO DE CORRECCIÓN ---
                // Registrar la exportación en logs_sistema
                registrarLogSistema("INFO", "Exportación de historial a CSV. Archivo: " + fileToSave.getName());
                // --- FIN DE CORRECCIÓN ---

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Error al exportar el historial: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                registrarLogSistema("ERROR", "Error al exportar historial: " + e.getMessage());
            }
        }
    }

    private void limpiarHistorial() {
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea limpiar TODO el historial?\n\n" +
                        "⚠️  ADVERTENCIA: Esta acción eliminará TODOS los registros del historial de consultas.\n" +
                        "Esta operación NO se puede deshacer.\n\n" +
                        "Se recomienda hacer un respaldo antes de continuar.",
                "Limpiar Historial Completo", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                // MODIFICACIÓN: Eliminar TODOS los registros sin filtro de fecha
                String query = "DELETE FROM historial_consultas";
                int filasEliminadas = DatabaseUtils.ejecutarUpdate(query);

                if (filasEliminadas > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Historial limpiado completamente.\n" +
                                    "Total de registros eliminados: " + filasEliminadas,
                            "Limpiar Historial", JOptionPane.INFORMATION_MESSAGE);

                    // --- INICIO DE CORRECCIÓN ---
                    // Registrar la limpieza completa en logs_sistema
                    registrarLogSistema("WARNING",
                            "Limpieza COMPLETA de historial (historial_consultas). Registros eliminados: "
                                    + filasEliminadas);
                    // --- FIN DE CORRECCIÓN ---

                    cargarDatosHistorial();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se encontraron registros para eliminar.",
                            "Limpiar Historial", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error al limpiar el historial: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                registrarLogSistema("ERROR", "Error al limpiar historial: " + e.getMessage());
            }
        }
    }

    private void mostrarEstadisticas() {
        // Consulta para obtener estadísticas del historial
        String query = "SELECT 'Total Consultas' as estadistica, COUNT(*) as valor FROM historial_consultas " +
                "UNION ALL " +
                "SELECT 'Consultas Hoy', COUNT(*) FROM historial_consultas WHERE DATE(fecha_consulta) = CURDATE() " +
                "UNION ALL " +
                "SELECT 'Consultas Última Semana', COUNT(*) FROM historial_consultas WHERE fecha_consulta >= DATE_SUB(NOW(), INTERVAL 7 DAY) "
                +
                "UNION ALL " +
                "SELECT 'Tipo Más Consultado', (SELECT tipo_consulta FROM historial_consultas GROUP BY tipo_consulta ORDER BY COUNT(*) DESC LIMIT 1) "
                +
                "UNION ALL " +
                "SELECT 'Usuario Más Activo', (SELECT u.nombre_completo FROM historial_consultas hc INNER JOIN usuarios u ON hc.id_usuario = u.id_usuario GROUP BY hc.id_usuario ORDER BY COUNT(*) DESC LIMIT 1)";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            StringBuilder estadisticas = new StringBuilder();
            estadisticas.append("ESTADÍSTICAS DEL HISTORIAL\n\n");

            while (rs.next()) {
                estadisticas.append(rs.getString("estadistica"))
                        .append(": ")
                        .append(rs.getString("valor"))
                        .append("\n");
            }

            DatabaseUtils.cerrarRecursos(conn, stmt, rs);

            // --- INICIO DE CORRECCIÓN ---
            // Registrar la consulta de estadísticas en logs_sistema
            registrarLogSistema("INFO", "Consulta de estadísticas del historial.");
            // --- FIN DE CORRECCIÓN ---

            JOptionPane.showMessageDialog(this, estadisticas.toString(),
                    "Estadísticas del Historial", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al obtener estadísticas: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            registrarLogSistema("ERROR", "Error al obtener estadísticas: " + e.getMessage());
        }
    }

    private void mostrarConsultaAvanzada() {
        JTextArea txtConsulta = new JTextArea(5, 50);
        txtConsulta.setFont(new Font("Consolas", Font.PLAIN, 12));
        JTextArea txtResultado = new JTextArea(10, 50);
        txtResultado.setEditable(false);
        txtResultado.setFont(new Font("Consolas", Font.PLAIN, 12));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Ingrese su consulta SQL (Solo personal autorizado):"), BorderLayout.NORTH);
        panel.add(new JScrollPane(txtConsulta), BorderLayout.CENTER);
        panel.add(new JLabel("Resultado:"), BorderLayout.SOUTH);
        panel.add(new JScrollPane(txtResultado), BorderLayout.SOUTH);

        int option = JOptionPane.showConfirmDialog(this, panel,
                "Consulta Avanzada", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION && !txtConsulta.getText().trim().isEmpty()) {
            String consultaSQL = txtConsulta.getText().trim();

            try {
                // Ejecutar consulta y mostrar resultados
                Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();

                // Verificar si es una consulta SELECT
                if (consultaSQL.trim().toUpperCase().startsWith("SELECT")) {
                    ResultSet rs = stmt.executeQuery(consultaSQL);

                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    StringBuilder resultado = new StringBuilder();

                    // Encabezados
                    for (int i = 1; i <= columnCount; i++) {
                        resultado.append(String.format("%-25s", metaData.getColumnName(i)));
                    }
                    resultado.append("\n");
                    resultado.append("-".repeat(columnCount * 25)).append("\n");

                    // Datos
                    int rowCount = 0;
                    while (rs.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            String valor = rs.getString(i);
                            resultado.append(String.format("%-25s", valor != null ? valor : "NULL"));
                        }
                        resultado.append("\n");
                        rowCount++;
                    }

                    resultado.append("\nTotal de registros: ").append(rowCount);
                    txtResultado.setText(resultado.toString());

                    // --- INICIO DE CORRECCIÓN ---
                    // Registrar consulta avanzada exitosa en logs_sistema
                    registrarLogSistema("INFO", "Consulta Avanzada (SELECT) ejecutada. Query: " + consultaSQL
                            + ". Registros devueltos: " + rowCount);
                    // --- FIN DE CORRECCIÓN ---

                    rs.close();
                } else {
                    // Por motivos de seguridad, SOLO permitimos SELECT desde la interfaz.
                    txtResultado
                            .setText("Operación no permitida: solo se permiten consultas SELECT en Consulta Avanzada.");
                    registrarLogSistema("WARNING",
                            "Intento de ejecutar consulta no-SELECT en Consulta Avanzada: " + consultaSQL);
                }

                stmt.close();
                conn.close();

            } catch (SQLException e) {
                txtResultado.setText("Error en la consulta: " + e.getMessage());

                // --- INICIO DE CORRECCIÓN ---
                // Registrar error en consulta avanzada en logs_sistema
                registrarLogSistema("ERROR",
                        "Error en Consulta Avanzada. Query: " + consultaSQL + ". Error: " + e.getMessage());
                // --- FIN DE CORRECCIÓN ---
            }
        }
    }

    // --- INICIO DE NUEVO MÉTODO ---
    /**
     * Registra una acción en la tabla logs_sistema.
     * 
     * @param tipoLog     Tipo de log (INFO, ERROR, WARNING, DEBUG)
     * @param descripcion Descripción de la acción realizada
     */
    private void registrarLogSistema(String tipoLog, String descripcion) {
        // FIXME: El ID de usuario está hardcodeado.
        // En una implementación ideal, se obtendría de un objeto Sesion
        // o se pasaría al constructor del panel.
        int idUsuario = 1; // Usamos 1 (admin) por defecto, como en el código original.

        String query = "INSERT INTO logs_sistema (tipo, modulo, descripcion, id_usuario) " +
                "VALUES (?, ?, ?, ?)";

        // Asegurarse de que el tipoLog esté en el ENUM: ('INFO', 'ERROR', 'WARNING',
        // 'DEBUG')
        String tipoValido = tipoLog.toUpperCase();
        if (!java.util.Arrays.asList("INFO", "ERROR", "WARNING", "DEBUG").contains(tipoValido)) {
            tipoValido = "INFO"; // Default a INFO si no es válido
        }

        DatabaseUtils.ejecutarUpdate(query,
                tipoValido,
                "HistorialConsultas", // El módulo es este panel
                descripcion,
                idUsuario);
    }
    // --- FIN DE NUEVO MÉTODO ---

    // Método auxiliar para registrar consultas en el historial
    private void registrarConsulta(String tipo, String descripcion, String entidad, String resultado) {
        // Obtener el ID del usuario actual (hardcoded por ahora, en implementación
        // real, de sesión)
        // FIXME: El ID de usuario está hardcodeado.
        int idUsuario = 1; // Por defecto, admin

        String query = "INSERT INTO historial_consultas (tipo_consulta, descripcion, entidad_consultada, id_usuario, detalles, resultado) "
                +
                "VALUES (?, ?, ?, ?, ?, ?)";

        DatabaseUtils.ejecutarUpdate(query, tipo, descripcion, entidad, idUsuario, "Consulta desde sistema",
                resultado);
    }

    // Método para realizar consultas específicas por tipo
    public void realizarConsultaClientes(String criterio) {
        String query = "SELECT * FROM clientes WHERE nombre LIKE ? OR email LIKE ?";
        String[] parametros = { "%" + criterio + "%", "%" + criterio + "%" };
        DatabaseUtils.llenarTablaDesdeConsulta(tablaHistorial, query, (Object[]) parametros);
        registrarConsulta("Clientes", "Búsqueda de clientes: " + criterio, "Tabla: clientes", "Éxito");
    }

    public void realizarConsultaVehiculos(String criterio) {
        String query = "SELECT v.*, c.nombre as cliente FROM vehiculos v " +
                "INNER JOIN clientes c ON v.id_cliente = c.id_cliente " +
                "WHERE v.placas LIKE ? OR v.marca LIKE ? OR v.modelo LIKE ?";
        String[] parametros = { "%" + criterio + "%", "%" + criterio + "%", "%" + criterio + "%" };
        DatabaseUtils.llenarTablaDesdeConsulta(tablaHistorial, query, (Object[]) parametros);
        registrarConsulta("Vehículos", "Búsqueda de vehículos: " + criterio, "Tabla: vehiculos", "Éxito");
    }

    public void realizarConsultaServicios(String criterio) {
        String query = "SELECT s.*, v.placas, c.nombre as cliente FROM servicios s " +
                "INNER JOIN vehiculos v ON s.id_vehiculo = v.id_vehiculo " +
                "INNER JOIN clientes c ON v.id_cliente = c.id_cliente " +
                "WHERE s.descripcion_servicio LIKE ? OR s.estado LIKE ?";
        String[] parametros = { "%" + criterio + "%", "%" + criterio + "%" };
        DatabaseUtils.llenarTablaDesdeConsulta(tablaHistorial, query, (Object[]) parametros);
        registrarConsulta("Servicios", "Búsqueda de servicios: " + criterio, "Tabla: servicios", "Éxito");
    }
}