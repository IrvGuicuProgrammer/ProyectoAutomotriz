import java.io.File;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class FacturacionAutomatizadaPanel extends JPanel {
    private JTable tablaFacturas;
    private DefaultTableModel modeloTabla;
    private JTextField campoBusqueda;
    private JComboBox<String> comboFiltroEstado;

    public FacturacionAutomatizadaPanel() {
        inicializarComponentes();
        cargarDatosFacturas();
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

        JLabel titulo = new JLabel("FACTURACIÓN AUTOMATIZADA");
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

        comboFiltroEstado = new JComboBox<>(
                new String[] { "Todas", "Pendiente", "Pagada", "Vencida", "Cancelada" });
        comboFiltroEstado.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton botonBuscar = crearBotonEstilizado("Buscar", new Color(30, 60, 114));
        JButton botonLimpiar = crearBotonEstilizado("Limpiar", new Color(100, 100, 100));
        JButton botonNuevaFactura = crearBotonEstilizado("Nueva Factura", new Color(40, 167, 69));

        panelBusqueda.add(new JLabel("Buscar factura:"));
        panelBusqueda.add(campoBusqueda);
        panelBusqueda.add(new JLabel("Estado:"));
        panelBusqueda.add(comboFiltroEstado);
        panelBusqueda.add(botonBuscar);
        panelBusqueda.add(botonLimpiar);
        panelBusqueda.add(botonNuevaFactura);

        panel.add(panelBusqueda, BorderLayout.CENTER);

        // Acciones de los botones
        botonBuscar.addActionListener(e -> buscarFacturas());
        botonLimpiar.addActionListener(e -> {
            campoBusqueda.setText("");
            comboFiltroEstado.setSelectedIndex(0);
            cargarDatosFacturas();
        });
        botonNuevaFactura.addActionListener(e -> generarNuevaFactura());

        return panel;
    }

    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 10, 10, 10)));

        // Modelo de tabla
        String[] columnas = { "ID", "N° Factura", "Cliente", "Vehículo", "Fecha", "Subtotal", "IVA", "Total", "Estado",
                "Fecha Vencimiento" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 5 || columnIndex == 6 || columnIndex == 7) {
                    return Double.class;
                }
                return String.class;
            }
        };

        tablaFacturas = new JTable(modeloTabla);
        tablaFacturas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaFacturas.setRowHeight(30);
        tablaFacturas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaFacturas.getTableHeader().setBackground(new Color(30, 60, 114));
        tablaFacturas.getTableHeader().setForeground(Color.WHITE);

        // Renderer para estados
        tablaFacturas.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (column == 8) { // Columna Estado
                    String estado = (String) value;
                    switch (estado) {
                        case "Pagada":
                            c.setBackground(new Color(200, 255, 200));
                            break;
                        case "Pendiente":
                            c.setBackground(new Color(255, 255, 200));
                            break;
                        case "Vencida":
                            c.setBackground(new Color(255, 200, 200));
                            break;
                        case "Cancelada":
                            c.setBackground(new Color(220, 220, 220));
                            break;
                        default:
                            c.setBackground(Color.WHITE);
                    }
                } else {
                    c.setBackground(Color.WHITE);
                }

                if (isSelected) {
                    c.setBackground(new Color(200, 220, 255));
                }

                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaFacturas);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton botonVerDetalle = crearBotonEstilizado("Ver Detalle", new Color(30, 60, 114));
        JButton botonGenerarPDF = crearBotonEstilizado("Generar PDF", new Color(40, 167, 69));
        JButton botonEditarFactura = crearBotonEstilizado("Editar Factura", new Color(255, 193, 7)); 
        
        // --- CAMBIO: Botón Cambiar Estado (antes Marcar Pagada) ---
        JButton botonCambiarEstado = crearBotonEstilizado("Cambiar Estado", new Color(23, 162, 184)); // Color cyan/informativo
        
        JButton botonCancelar = crearBotonEstilizado("Cancelar", new Color(220, 53, 69));
        JButton botonReporte = crearBotonEstilizado("Reporte Ventas", new Color(111, 66, 193));

        panel.add(botonVerDetalle);
        panel.add(botonGenerarPDF);
        panel.add(botonEditarFactura);
        panel.add(botonCambiarEstado); // Agregado al panel
        panel.add(botonCancelar);
        panel.add(botonReporte);

        // Acciones de los botones
        botonVerDetalle.addActionListener(e -> verDetalleFactura());
        botonGenerarPDF.addActionListener(e -> generarPDF());
        botonEditarFactura.addActionListener(e -> editarFactura());
        
        // --- CAMBIO: Acción cambiar estado ---
        botonCambiarEstado.addActionListener(e -> cambiarEstadoFactura());
        
        botonCancelar.addActionListener(e -> cancelarFactura());
        botonReporte.addActionListener(e -> generarReporteVentas());

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

    // ==================== FUNCIONES PRINCIPALES ====================

    // --- GENERAR PDF ---
    private void generarPDF() {
        int filaSeleccionada = tablaFacturas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una factura para generar PDF.");
            return;
        }

        int idFactura = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        Factura factura = obtenerFacturaPorId(idFactura);

        if (factura != null) {
            ReportePDFUtils.generarFacturaPDF(
                factura.numeroFactura,
                factura.cliente,
                factura.vehiculo,
                factura.fechaEmision,
                factura.subtotal,
                factura.iva,
                factura.total,
                factura.descripcionServicio != null ? factura.descripcionServicio : "Mantenimiento General"
            );
            
            registrarLogSistema("INFO", "PDF generado para factura: " + factura.numeroFactura);
        } else {
            JOptionPane.showMessageDialog(this, "No se pudieron obtener los datos de la factura.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- EDITAR FACTURA ---
    private void editarFactura() {
        int filaSeleccionada = tablaFacturas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una factura para editar.");
            return;
        }

        int idFactura = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        Factura factura = obtenerFacturaPorId(idFactura);

        if (factura == null) {
            JOptionPane.showMessageDialog(this, "Error al cargar los datos de la factura.");
            return;
        }

        JTextField txtFechaVencimiento = new JTextField(factura.fechaVencimiento);
        JComboBox<String> comboMetodoPago = new JComboBox<>(new String[] { "Efectivo", "Tarjeta", "Transferencia" });
        comboMetodoPago.setSelectedItem(factura.metodoPago != null ? factura.metodoPago : "Efectivo");
        JTextArea txtNotas = new JTextArea(factura.notas != null ? factura.notas : "", 3, 25);
        txtNotas.setLineWrap(true);
        txtNotas.setWrapStyleWord(true);

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        panel.add(new JLabel("Factura N°:"));
        panel.add(new JLabel(factura.numeroFactura));
        panel.add(new JLabel("Fecha Vencimiento (YYYY-MM-DD):"));
        panel.add(txtFechaVencimiento);
        panel.add(new JLabel("Método de Pago:"));
        panel.add(comboMetodoPago);
        panel.add(new JLabel("Notas:"));
        panel.add(new JScrollPane(txtNotas));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(450, 250));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        int result = JOptionPane.showConfirmDialog(this, scrollPane, 
                "Editar Factura " + factura.numeroFactura, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String query = "UPDATE facturas SET fecha_vencimiento = ?, metodo_pago = ?, notas = ? WHERE id_factura = ?";
            
            int filasAfectadas = DatabaseUtils.ejecutarUpdate(query,
                    txtFechaVencimiento.getText().trim(),
                    comboMetodoPago.getSelectedItem().toString(),
                    txtNotas.getText().trim(),
                    idFactura);

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "Factura actualizada correctamente.");
                registrarLogSistema("INFO", "Factura editada: " + factura.numeroFactura);
                cargarDatosFacturas();
            }
        }
    }

    // --- NUEVO MÉTODO: CAMBIAR ESTADO FACTURA ---
    private void cambiarEstadoFactura() {
        int fila = tablaFacturas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una factura para cambiar su estado.");
            return;
        }

        int id = (int) modeloTabla.getValueAt(fila, 0);
        String estadoActual = (String) modeloTabla.getValueAt(fila, 8); // Columna 8 es 'Estado'
        String numeroFactura = (String) modeloTabla.getValueAt(fila, 1);

        if ("Cancelada".equals(estadoActual)) {
            JOptionPane.showMessageDialog(this, "No se puede cambiar el estado de una factura cancelada.", "Acción no permitida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] opciones = {"Pendiente", "Pagada"};
        String nuevoEstado = (String) JOptionPane.showInputDialog(this, 
                "Seleccione el nuevo estado para la factura " + numeroFactura + ":",
                "Cambiar Estado",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                estadoActual);

        if (nuevoEstado != null && !nuevoEstado.equals(estadoActual)) {
            DatabaseUtils.ejecutarUpdate("UPDATE facturas SET estado=? WHERE id_factura=?", nuevoEstado, id);
            
            registrarLogSistema("INFO", "Estado de factura " + numeroFactura + " cambiado a " + nuevoEstado);
            JOptionPane.showMessageDialog(this, "Estado actualizado a: " + nuevoEstado);
            cargarDatosFacturas();
        }
    }

    // --- REPORTE DE VENTAS ---
    private void generarReporteVentas() {
        mostrarDialogoReporteVentas();
        registrarLogSistema("INFO", "Reporte de ventas visualizado.");
    }

    private void mostrarDialogoReporteVentas() {
        JDialog dialogoReporte = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Reporte de Ventas", true);
        dialogoReporte.setSize(600, 400);
        dialogoReporte.setLocationRelativeTo(this);
        dialogoReporte.setLayout(new BorderLayout());

        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelTitulo.setBackground(new Color(30, 60, 114));
        JLabel lblTitulo = new JLabel("Resumen de Ventas por Estado");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        panelTitulo.add(lblTitulo);

        // Tabla de reporte
        String[] columnas = { "Estado", "Cantidad de Facturas", "Monto Total" };
        DefaultTableModel modeloReporte = new DefaultTableModel(columnas, 0);
        JTable tablaReporte = new JTable(modeloReporte);
        tablaReporte.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaReporte.setRowHeight(25);

        // Cargar datos
        String query = "SELECT estado, COUNT(*) as 'Cantidad', SUM(total) as 'Total' " +
                       "FROM facturas GROUP BY estado " +
                       "UNION ALL " +
                       "SELECT 'TOTAL GENERAL', COUNT(*), SUM(total) FROM facturas";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
            
            while (rs.next()) {
                modeloReporte.addRow(new Object[] {
                        rs.getString(1),
                        rs.getInt(2),
                        currency.format(rs.getDouble(3))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Botón para exportar este reporte
        JButton btnExportar = new JButton("Exportar a PDF");
        btnExportar.addActionListener(e -> ReportePDFUtils.generarReporteTablaPDF(tablaReporte, "RESUMEN DE VENTAS", "Resumen_Ventas"));

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dialogoReporte.dispose());
        
        JPanel panelBotones = new JPanel();
        panelBotones.add(btnExportar);
        panelBotones.add(btnCerrar);

        dialogoReporte.add(panelTitulo, BorderLayout.NORTH);
        dialogoReporte.add(new JScrollPane(tablaReporte), BorderLayout.CENTER);
        dialogoReporte.add(panelBotones, BorderLayout.SOUTH);

        dialogoReporte.setVisible(true);
    }

    // ==================== MÉTODOS DE DATOS ====================

    private void cargarDatosFacturas() {
        String query = "SELECT f.id_factura, f.numero_factura, c.nombre as cliente, " +
                "CONCAT(v.marca, ' ', v.modelo, ' - ', v.placas) as vehiculo, " +
                "DATE_FORMAT(f.fecha_emision, '%Y-%m-%d') as fecha, " +
                "f.subtotal, f.iva, f.total, f.estado, " +
                "DATE_FORMAT(f.fecha_vencimiento, '%Y-%m-%d') as fecha_vencimiento " +
                "FROM facturas f " +
                "INNER JOIN servicios s ON f.id_servicio = s.id_servicio " +
                "INNER JOIN vehiculos v ON s.id_vehiculo = v.id_vehiculo " +
                "INNER JOIN clientes c ON v.id_cliente = c.id_cliente " +
                "ORDER BY f.fecha_emision DESC, f.id_factura DESC";
        DatabaseUtils.llenarTablaDesdeConsulta(tablaFacturas, query);
    }

    private void buscarFacturas() {
        String busqueda = campoBusqueda.getText().trim();
        String estadoFiltro = comboFiltroEstado.getSelectedItem().toString();

        StringBuilder query = new StringBuilder(
                "SELECT f.id_factura, f.numero_factura, c.nombre as cliente, " +
                        "CONCAT(v.marca, ' ', v.modelo, ' - ', v.placas) as vehiculo, " +
                        "DATE_FORMAT(f.fecha_emision, '%Y-%m-%d') as fecha, " +
                        "f.subtotal, f.iva, f.total, f.estado, " +
                        "DATE_FORMAT(f.fecha_vencimiento, '%Y-%m-%d') as fecha_vencimiento " +
                        "FROM facturas f " +
                        "INNER JOIN servicios s ON f.id_servicio = s.id_servicio " +
                        "INNER JOIN vehiculos v ON s.id_vehiculo = v.id_vehiculo " +
                        "INNER JOIN clientes c ON v.id_cliente = c.id_cliente " +
                        "WHERE 1=1");

        java.util.List<Object> parametros = new java.util.ArrayList<>();

        if (!busqueda.isEmpty()) {
            query.append(" AND (f.numero_factura LIKE ? OR c.nombre LIKE ? OR v.placas LIKE ?)");
            String parametroBusqueda = "%" + busqueda + "%";
            parametros.add(parametroBusqueda);
            parametros.add(parametroBusqueda);
            parametros.add(parametroBusqueda);
        }

        if (!estadoFiltro.equals("Todas")) {
            query.append(" AND f.estado = ?");
            parametros.add(estadoFiltro);
        }

        query.append(" ORDER BY f.fecha_emision DESC, f.id_factura DESC");
        DatabaseUtils.llenarTablaDesdeConsulta(tablaFacturas, query.toString(), parametros.toArray());
    }

    private void generarNuevaFactura() {
        java.util.List<String> servicios = obtenerServiciosSinFactura();

        if (servicios.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay servicios completados pendientes de facturar.",
                    "Sin Servicios", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JComboBox<String> comboServicio = new JComboBox<>(servicios.toArray(new String[0]));
        JTextField txtFechaVencimiento = new JTextField(java.time.LocalDate.now().plusDays(30).toString());
        JComboBox<String> comboMetodoPago = new JComboBox<>(new String[] { "Efectivo", "Tarjeta", "Transferencia" });
        JTextArea txtNotas = new JTextArea(3, 25);
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("Servicio:*")); panel.add(comboServicio);
        panel.add(new JLabel("Vencimiento (YYYY-MM-DD):")); panel.add(txtFechaVencimiento);
        panel.add(new JLabel("Método de Pago:")); panel.add(comboMetodoPago);
        panel.add(new JLabel("Notas:")); panel.add(new JScrollPane(txtNotas));

        int result = JOptionPane.showConfirmDialog(this, panel, "Generar Nueva Factura", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String servicioSeleccionado = (String) comboServicio.getSelectedItem();
            int idServicio = obtenerIdServicioDesdeDescripcion(servicioSeleccionado);
            
            Servicio servicio = obtenerDatosServicio(idServicio);
            if (servicio == null) return;

            double subtotal = servicio.costoTotal != null ? servicio.costoTotal : 0.0;
            double iva = subtotal * 0.16;
            double total = subtotal + iva;
            String numeroFactura = generarNumeroFactura();

            String query = "INSERT INTO facturas (numero_factura, id_servicio, fecha_emision, fecha_vencimiento, subtotal, iva, total, estado, metodo_pago, notas) VALUES (?, ?, CURDATE(), ?, ?, ?, ?, 'Pendiente', ?, ?)";

            int r = DatabaseUtils.ejecutarUpdate(query, numeroFactura, idServicio, txtFechaVencimiento.getText(), subtotal, iva, total, comboMetodoPago.getSelectedItem(), txtNotas.getText());

            if (r > 0) {
                actualizarEstadoServicio(idServicio, "Facturado");
                JOptionPane.showMessageDialog(this, "Factura generada: " + numeroFactura);
                cargarDatosFacturas();
            }
        }
    }

    private void verDetalleFactura() {
        int fila = tablaFacturas.getSelectedRow();
        if (fila == -1) return;
        int id = (int) modeloTabla.getValueAt(fila, 0);
        Factura f = obtenerFacturaPorId(id);
        if (f != null) {
            String msg = String.format("Factura: %s\nCliente: %s\nTotal: $%.2f\nEstado: %s\nNotas: %s", 
                f.numeroFactura, f.cliente, f.total, f.estado, f.notas);
            JOptionPane.showMessageDialog(this, msg, "Detalle", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void cancelarFactura() {
        int fila = tablaFacturas.getSelectedRow();
        if (fila == -1) return;
        int id = (int) modeloTabla.getValueAt(fila, 0);
        if (JOptionPane.showConfirmDialog(this, "¿Cancelar factura?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            DatabaseUtils.ejecutarUpdate("UPDATE facturas SET estado='Cancelada' WHERE id_factura=?", id);
            cargarDatosFacturas();
        }
    }

    // ==================== MÉTODOS AUXILIARES DB ====================

    private java.util.List<String> obtenerServiciosSinFactura() {
        java.util.List<String> list = new java.util.ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT CONCAT(v.marca, ' ', v.modelo, ' - ', v.placas, ' - ', c.nombre, ' - ', s.descripcion_servicio) FROM servicios s JOIN vehiculos v ON s.id_vehiculo=v.id_vehiculo JOIN clientes c ON v.id_cliente=c.id_cliente LEFT JOIN facturas f ON s.id_servicio=f.id_servicio WHERE s.estado='Completado' AND f.id_factura IS NULL")) {
            while (rs.next()) list.add(rs.getString(1));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    private int obtenerIdServicioDesdeDescripcion(String desc) {
        String placas = desc.split(" - ")[1];
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT s.id_servicio FROM servicios s JOIN vehiculos v ON s.id_vehiculo=v.id_vehiculo WHERE v.placas=? AND s.estado='Completado' LIMIT 1")) {
            ps.setString(1, placas);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }

    private Servicio obtenerDatosServicio(int id) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT costo_total FROM servicios WHERE id_servicio=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) { Servicio s = new Servicio(); s.costoTotal = rs.getDouble(1); return s; }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    private String generarNumeroFactura() {
        return "FAC-" + System.currentTimeMillis() % 10000;
    }

    private void actualizarEstadoServicio(int id, String estado) {
        DatabaseUtils.ejecutarUpdate("UPDATE servicios SET estado=? WHERE id_servicio=?", estado, id);
    }

    private Factura obtenerFacturaPorId(int id) {
        String sql = "SELECT f.*, c.nombre, CONCAT(v.marca, ' ', v.modelo) as vehiculo, s.descripcion_servicio FROM facturas f JOIN servicios s ON f.id_servicio=s.id_servicio JOIN vehiculos v ON s.id_vehiculo=v.id_vehiculo JOIN clientes c ON v.id_cliente=c.id_cliente WHERE f.id_factura=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Factura f = new Factura();
                f.numeroFactura = rs.getString("numero_factura");
                f.cliente = rs.getString("nombre");
                f.vehiculo = rs.getString("vehiculo");
                f.fechaEmision = rs.getString("fecha_emision");
                f.fechaVencimiento = rs.getString("fecha_vencimiento");
                f.metodoPago = rs.getString("metodo_pago");
                f.subtotal = rs.getDouble("subtotal");
                f.iva = rs.getDouble("iva");
                f.total = rs.getDouble("total");
                f.estado = rs.getString("estado");
                f.notas = rs.getString("notas");
                f.descripcionServicio = rs.getString("descripcion_servicio");
                return f;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    private void registrarLogSistema(String tipo, String msg) {
        System.out.println("[LOG " + tipo + "]: " + msg);
    }

    // Clases internas
    private static class Servicio { Double costoTotal; }
    private static class Factura { 
        String numeroFactura, cliente, vehiculo, fechaEmision, fechaVencimiento, metodoPago, estado, notas, descripcionServicio; 
        double subtotal, iva, total; 
    }
}