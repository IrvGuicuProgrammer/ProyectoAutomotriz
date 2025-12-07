import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.NumberFormat; // Importado para formatear moneda
import java.util.Locale; // Importado para formatear moneda

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
        JButton botonEnviarEmail = crearBotonEstilizado("Enviar Email", new Color(255, 193, 7));
        JButton botonMarcarPagada = crearBotonEstilizado("Marcar Pagada", new Color(108, 117, 125));
        JButton botonCancelar = crearBotonEstilizado("Cancelar", new Color(220, 53, 69));
        JButton botonReporte = crearBotonEstilizado("Reporte Ventas", new Color(111, 66, 193));

        panel.add(botonVerDetalle);
        panel.add(botonGenerarPDF);
        panel.add(botonEnviarEmail);
        panel.add(botonMarcarPagada);
        panel.add(botonCancelar);
        panel.add(botonReporte);

        // Acciones de los botones
        botonVerDetalle.addActionListener(e -> verDetalleFactura());
        botonGenerarPDF.addActionListener(e -> generarPDF());
        botonEnviarEmail.addActionListener(e -> enviarEmail());
        botonMarcarPagada.addActionListener(e -> marcarPagada());
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

    // MÉTODOS PARA FACTURACIÓN
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

        // --- INICIO DE CORRECCIÓN ---
        // Registrar la búsqueda en el historial
        String descripcionLog = "Búsqueda: '" + busqueda + "', Estado: '" + estadoFiltro + "'";
        registrarConsulta(descripcionLog, "Filtro: " + estadoFiltro, "Búsqueda realizada");
        // --- FIN DE CORRECCIÓN ---

        DatabaseUtils.llenarTablaDesdeConsulta(tablaFacturas, query.toString(), parametros.toArray());
    }

    private void generarNuevaFactura() {
        // Obtener lista de servicios completados sin factura
        java.util.List<String> servicios = obtenerServiciosSinFactura();

        if (servicios.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay servicios completados pendientes de facturar.\n" +
                            "Todos los servicios completados ya tienen factura asociada.",
                    "Sin Servicios", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JComboBox<String> comboServicio = new JComboBox<>(servicios.toArray(new String[0]));
        JTextField txtFechaVencimiento = new JTextField(java.time.LocalDate.now().plusDays(30).toString());
        JComboBox<String> comboMetodoPago = new JComboBox<>(new String[] { "Efectivo", "Tarjeta", "Transferencia" });
        JTextArea txtNotas = new JTextArea(3, 25);
        txtNotas.setLineWrap(true);
        txtNotas.setWrapStyleWord(true);

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Servicio:*"));
        panel.add(comboServicio);
        panel.add(new JLabel("Fecha Vencimiento (YYYY-MM-DD):"));
        panel.add(txtFechaVencimiento);
        panel.add(new JLabel("Método de Pago:"));
        panel.add(comboMetodoPago);
        panel.add(new JLabel("Notas:"));
        panel.add(new JScrollPane(txtNotas));

        // Crear un panel contenedor con scroll
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(500, 200));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        int result = JOptionPane.showConfirmDialog(this, scrollPane,
                "Generar Nueva Factura", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String servicioSeleccionado = (String) comboServicio.getSelectedItem();
            int idServicio = obtenerIdServicioDesdeDescripcion(servicioSeleccionado);

            if (idServicio == -1) {
                JOptionPane.showMessageDialog(this, "Error al obtener el servicio seleccionado.");
                return;
            }

            // Obtener datos del servicio para calcular totales
            Servicio servicio = obtenerDatosServicio(idServicio);
            if (servicio == null) {
                JOptionPane.showMessageDialog(this, "Error al obtener los datos del servicio.");
                return;
            }

            // Calcular totales
            double subtotal = servicio.costoTotal != null ? servicio.costoTotal : 0.0;
            double iva = subtotal * 0.16; // 16% IVA
            double total = subtotal + iva;

            // Generar número de factura
            String numeroFactura = generarNumeroFactura();

            String query = "INSERT INTO facturas (numero_factura, id_servicio, fecha_emision, " +
                    "fecha_vencimiento, subtotal, iva, total, estado, metodo_pago, notas) " +
                    "VALUES (?, ?, CURDATE(), ?, ?, ?, ?, 'Pendiente', ?, ?)";

            int filasAfectadas = DatabaseUtils.ejecutarUpdate(query,
                    numeroFactura,
                    idServicio,
                    txtFechaVencimiento.getText().trim(),
                    subtotal,
                    iva,
                    total,
                    comboMetodoPago.getSelectedItem().toString(),
                    txtNotas.getText().trim());

            if (filasAfectadas > 0) {
                // Actualizar estado del servicio a "Facturado"
                actualizarEstadoServicio(idServicio, "Facturado");

                // --- INICIO DE CORRECCIÓN ---
                registrarLogSistema("INFO", "Factura generada: " + numeroFactura + " para servicio ID: " + idServicio
                        + ". Total: " + total);
                // --- FIN DE CORRECCIÓN ---

                JOptionPane.showMessageDialog(this,
                        "Factura generada correctamente.\n" +
                                "Número de Factura: " + numeroFactura + "\n" +
                                "Subtotal: $" + String.format("%.2f", subtotal) + "\n" +
                                "IVA: $" + String.format("%.2f", iva) + "\n" +
                                "Total: $" + String.format("%.2f", total));
                cargarDatosFacturas();
            } else {
                registrarLogSistema("ERROR", "Error al generar factura para servicio ID: " + idServicio);
            }
        }
    }

    private void verDetalleFactura() {
        int filaSeleccionada = tablaFacturas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una factura para ver el detalle.");
            return;
        }

        int idFactura = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        Factura factura = obtenerFacturaPorId(idFactura);

        if (factura != null) {
            String detalles = String.format(
                    "ID Factura: %d\n" +
                            "Número: %s\n" +
                            "Cliente: %s\n" +
                            "Vehículo: %s\n" +
                            "Fecha Emisión: %s\n" +
                            "Fecha Vencimiento: %s\n" +
                            "Estado: %s\n" +
                            "Método Pago: %s\n" +
                            "Subtotal: $%.2f\n" +
                            "IVA: $%.2f\n" +
                            "Total: $%.2f\n" +
                            "Notas: %s\n\n" +
                            "Servicio Asociado:\n%s",
                    factura.idFactura, factura.numeroFactura, factura.cliente, factura.vehiculo,
                    factura.fechaEmision, factura.fechaVencimiento, factura.estado,
                    factura.metodoPago != null ? factura.metodoPago : "No especificado",
                    factura.subtotal, factura.iva, factura.total,
                    factura.notas != null ? factura.notas : "Ninguna",
                    factura.descripcionServicio);

            JOptionPane.showMessageDialog(this, detalles, "Detalles de Factura", JOptionPane.INFORMATION_MESSAGE);

            // --- INICIO DE CORRECCIÓN ---
            registrarLogSistema("INFO",
                    "Consulta detalle factura ID: " + idFactura + " (N°: " + factura.numeroFactura + ")");
            // --- FIN DE CORRECCIÓN ---
        }
    }

    private void generarPDF() {
        int filaSeleccionada = tablaFacturas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una factura para generar PDF.");
            return;
        }

        int idFactura = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        String numeroFactura = (String) modeloTabla.getValueAt(filaSeleccionada, 1);

        registrarLogSistema("INFO",
                "Generación de PDF solicitada para factura ID: " + idFactura + " (N°: " + numeroFactura + ")");

        // TODO: Implementar la generación real de PDF aquí.
        // 1. Añadir una librería de PDF a tu proyecto (ej. iText 7 o Apache PDFBox).
        // Maven: <dependency>
        // <groupId>com.itextpdf</groupId>
        // <artifactId>itext7-core</artifactId>
        // <version>7.1.15</version> // </dependency>
        // 2. Obtener los datos completos de la factura (usando
        // obtenerFacturaPorId(idFactura)).
        // 3. Obtener los datos del cliente (nombre, dirección, RFC) de la tabla
        // 'clientes'.
        // 4. Obtener los detalles del servicio (productos, mano de obra) de
        // 'detalles_servicio' y 'servicios'.
        // 5. Usar la librería para crear un nuevo documento PDF.
        // 6. Diseñar el layout: Añadir logo, datos del taller, datos del cliente, tabla
        // de conceptos, totales (subtotal, IVA, total).
        // 7. Guardar el archivo PDF en el disco (ej. "C:/Facturas/FAC-2024-001.pdf").
        // 8. Opcional: Abrir el PDF automáticamente.

        JOptionPane.showMessageDialog(this,
                "Generando PDF para factura: " + numeroFactura + "\n\n" +
                        "Esta funcionalidad aún no está implementada.\n" +
                        "Se requiere la librería iText o PDFBox.",
                "Generar PDF", JOptionPane.INFORMATION_MESSAGE);
    }

    private void enviarEmail() {
        int filaSeleccionada = tablaFacturas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una factura para enviar por email.");
            return;
        }

        int idFactura = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        String numeroFactura = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
        String cliente = (String) modeloTabla.getValueAt(filaSeleccionada, 2);

        // Obtener email del cliente
        String emailCliente = obtenerEmailCliente(cliente);

        if (emailCliente == null || emailCliente.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El cliente " + cliente + " no tiene un email registrado.\n" +
                            "No se puede enviar la factura por email.",
                    "Email No Disponible", JOptionPane.WARNING_MESSAGE);
            return;
        }

        registrarLogSistema("INFO", "Envío de email solicitado para factura ID: " + idFactura + " (N°: " + numeroFactura
                + ") a: " + emailCliente);

        // TODO: Implementar el envío real de email aquí.
        // 1. Añadir la API JavaMail a tu proyecto.
        // Maven: <dependency>
        // <groupId>com.sun.mail</groupId>
        // <artifactId>javax.mail</artifactId>
        // <version>1.6.2</version>
        // </dependency>
        // 2. Configurar las propiedades (Properties) con tu servidor SMTP (ej.
        // "smtp.gmail.com"), puerto (587), y autenticación (TLS).
        // 3. Crear una Sesión (Session) con un autenticador (Authenticator) usando tu
        // email y contraseña.
        // 4. (Primero, debes generar el PDF usando la lógica de generarPDF()).
        // 5. Crear un MimeMessage.
        // 6. Crear un MimeBodyPart para el texto del email.
        // 7. Crear un MimeBodyPart para el archivo adjunto (el PDF).
        // 8. Agregar ambas partes a un MimeMultipart.
        // 9. Establecer el contenido del mensaje (setMessage.setContent(multipart)).
        // 10. Enviar el mensaje (Transport.send(message)).
        // NOTA: Es crucial manejar la contraseña de tu email de forma segura (no
        // hardcodearla).

        JOptionPane.showMessageDialog(this,
                "Enviando factura " + numeroFactura + " a: " + emailCliente + "\n\n" +
                        "Esta funcionalidad aún no está implementada.\n" +
                        "Se requiere la API JavaMail y un PDF generado.",
                "Enviar Email", JOptionPane.INFORMATION_MESSAGE);
    }

    private void marcarPagada() {
        int filaSeleccionada = tablaFacturas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una factura para marcar como pagada.");
            return;
        }

        int idFactura = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        String numeroFactura = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
        String estadoActual = (String) modeloTabla.getValueAt(filaSeleccionada, 8);

        if ("Pagada".equals(estadoActual)) {
            JOptionPane.showMessageDialog(this, "La factura ya está marcada como pagada.");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de marcar la factura " + numeroFactura + " como PAGADA?",
                "Confirmar Pago", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            String query = "UPDATE facturas SET estado = 'Pagada' WHERE id_factura = ?";
            int resultado = DatabaseUtils.ejecutarUpdate(query, idFactura);
            if (resultado > 0) {
                JOptionPane.showMessageDialog(this, "Factura marcada como pagada correctamente.");

                // --- INICIO DE CORRECCIÓN ---
                registrarLogSistema("INFO",
                        "Factura marcada como PAGADA. ID: " + idFactura + " (N°: " + numeroFactura + ")");
                // --- FIN DE CORRECCIÓN ---

                cargarDatosFacturas();
            } else {
                registrarLogSistema("ERROR", "Error al marcar como PAGADA factura ID: " + idFactura);
            }
        }
    }

    private void cancelarFactura() {
        int filaSeleccionada = tablaFacturas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una factura para cancelar.");
            return;
        }

        int idFactura = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        String numeroFactura = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
        String estadoActual = (String) modeloTabla.getValueAt(filaSeleccionada, 8);

        if ("Cancelada".equals(estadoActual)) {
            JOptionPane.showMessageDialog(this, "La factura ya está cancelada.");
            return;
        }

        if ("Pagada".equals(estadoActual)) {
            JOptionPane.showMessageDialog(this,
                    "No se puede cancelar una factura que ya ha sido pagada.",
                    "Factura Pagada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextArea txtMotivo = new JTextArea(3, 25);
        txtMotivo.setLineWrap(true);
        txtMotivo.setWrapStyleWord(true);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Motivo de cancelación:"), BorderLayout.NORTH);
        panel.add(new JScrollPane(txtMotivo), BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Cancelar Factura " + numeroFactura, JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String motivo = txtMotivo.getText().trim();
            if (motivo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe especificar un motivo para la cancelación.");
                return;
            }

            String query = "UPDATE facturas SET estado = 'Cancelada', notas = CONCAT(IFNULL(notas, ''), '\nCANCELADA - Motivo: ', ?) WHERE id_factura = ?";
            int resultado = DatabaseUtils.ejecutarUpdate(query, motivo, idFactura);
            if (resultado > 0) {
                JOptionPane.showMessageDialog(this, "Factura cancelada correctamente.");

                // --- INICIO DE CORRECCIÓN ---
                registrarLogSistema("WARNING",
                        "Factura CANCELADA. ID: " + idFactura + " (N°: " + numeroFactura + "). Motivo: " + motivo);
                // --- FIN DE CORRECCIÓN ---

                cargarDatosFacturas();
            } else {
                registrarLogSistema("ERROR", "Error al CANCELAR factura ID: " + idFactura);
            }
        }
    }

    private void generarReporteVentas() {
        // --- INICIO DE CORRECCIÓN ---
        registrarLogSistema("INFO", "Generación de reporte de ventas solicitada.");
        // Se llama al nuevo método que muestra el diálogo.
        mostrarDialogoReporteVentas();
        // --- FIN DE CORRECCIÓN ---
    }

    /**
     * --- INICIO DE NUEVO MÉTODO ---
     * Muestra un diálogo modal con un resumen de las ventas por estado.
     */
    private void mostrarDialogoReporteVentas() {
        // 1. Crear el diálogo
        JDialog dialogoReporte = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Reporte de Ventas", true);
        dialogoReporte.setSize(500, 350);
        dialogoReporte.setLocationRelativeTo(this);
        dialogoReporte.setLayout(new BorderLayout());

        // 2. Crear el panel de título
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelTitulo.setBackground(new Color(30, 60, 114));
        JLabel lblTitulo = new JLabel("Resumen de Ventas por Estado");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        panelTitulo.add(lblTitulo);
        panelTitulo.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 3. Crear la tabla para los datos
        String[] columnas = { "Estado", "Cantidad de Facturas", "Monto Total" };
        DefaultTableModel modeloReporte = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tablaReporte = new JTable(modeloReporte);
        tablaReporte.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaReporte.setRowHeight(25);

        // 4. Llenar la tabla con datos reales
        String query = "SELECT estado, " +
                "COUNT(*) as 'Cantidad', " +
                "SUM(total) as 'Total' " +
                "FROM facturas GROUP BY estado " +
                "UNION ALL " +
                "SELECT 'TOTAL (Pagado+Pendiente)', COUNT(*), SUM(total) " +
                "FROM facturas WHERE estado IN ('Pagada', 'Pendiente')";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String estado = rs.getString("estado");
                int cantidad = rs.getInt("Cantidad");
                double total = rs.getDouble("Total");

                modeloReporte.addRow(new Object[] {
                        estado,
                        cantidad,
                        formatter.format(total) // Formatear como moneda
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al generar reporte: " + e.getMessage());
        } finally {
            DatabaseUtils.cerrarRecursos(conn, stmt, rs);
        }

        // 5. Botón de cerrar
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dialogoReporte.dispose());
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBoton.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelBoton.add(btnCerrar);

        // 6. Añadir componentes al diálogo
        dialogoReporte.add(panelTitulo, BorderLayout.NORTH);
        dialogoReporte.add(new JScrollPane(tablaReporte), BorderLayout.CENTER);
        dialogoReporte.add(panelBoton, BorderLayout.SOUTH);

        // 7. Mostrar diálogo
        dialogoReporte.setVisible(true);
    }
    /**
     * --- FIN DE NUEVO MÉTODO ---
     */

    // --- INICIO DE MÉTODOS DE LOGGING ---

    /**
     * Registra una acción en la tabla logs_sistema.
     * 
     * @param tipoLog     Tipo de log (INFO, ERROR, WARNING, DEBUG)
     * @param descripcion Descripción de la acción realizada
     */
    private void registrarLogSistema(String tipoLog, String descripcion) {
        // FIXME: El ID de usuario está hardcodeado.
        // En una implementación ideal, se obtendría de un objeto Sesion.
        int idUsuario = 1; // Usamos 1 (admin) por defecto.

        String query = "INSERT INTO logs_sistema (tipo, modulo, descripcion, id_usuario) " +
                "VALUES (?, ?, ?, ?)";

        String tipoValido = tipoLog.toUpperCase();
        if (!java.util.Arrays.asList("INFO", "ERROR", "WARNING", "DEBUG").contains(tipoValido)) {
            tipoValido = "INFO"; // Default a INFO si no es válido
        }

        DatabaseUtils.ejecutarUpdate(query,
                tipoValido,
                "Facturacion", // Módulo de Facturación
                descripcion,
                idUsuario);
    }

    /**
     * Registra una búsqueda en la tabla historial_consultas.
     * 
     * @param descripcion Descripción de la búsqueda
     * @param entidad     Entidad consultada (e.j., Filtro: Facturas)
     * @param resultado   Resultado de la búsqueda
     */
    private void registrarConsulta(String descripcion, String entidad, String resultado) {
        // FIXME: El ID de usuario está hardcodeado.
        int idUsuario = 1; // Usamos 1 (admin) por defecto.

        String query = "INSERT INTO historial_consultas (tipo_consulta, descripcion, entidad_consultada, id_usuario, detalles, resultado) "
                +
                "VALUES ('Facturas', ?, ?, ?, 'Consulta desde sistema', ?)";

        DatabaseUtils.ejecutarUpdate(query, descripcion, entidad, idUsuario, resultado);
    }

    // --- FIN DE MÉTODOS DE LOGGING ---

    // MÉTODOS AUXILIARES
    private java.util.List<String> obtenerServiciosSinFactura() {
        java.util.List<String> servicios = new java.util.ArrayList<>();
        String query = "SELECT s.id_servicio, CONCAT(v.marca, ' ', v.modelo, ' - ', v.placas, ' - ', c.nombre, ' - ', s.descripcion_servicio) as descripcion "
                +
                "FROM servicios s " +
                "INNER JOIN vehiculos v ON s.id_vehiculo = v.id_vehiculo " +
                "INNER JOIN clientes c ON v.id_cliente = c.id_cliente " +
                "LEFT JOIN facturas f ON s.id_servicio = f.id_servicio " +
                "WHERE s.estado = 'Completado' AND f.id_factura IS NULL " +
                "ORDER BY s.fecha_inicio DESC";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                servicios.add(rs.getString("descripcion"));
            }

            DatabaseUtils.cerrarRecursos(conn, stmt, rs);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar servicios: " + e.getMessage());
        }

        return servicios;
    }

    private int obtenerIdServicioDesdeDescripcion(String descripcion) {
        // La descripción tiene formato: "Marca Modelo - Placas - Cliente - Descripción
        // Servicio"
        String[] partes = descripcion.split(" - ");
        if (partes.length < 4)
            return -1;

        String placas = partes[1];
        String query = "SELECT s.id_servicio FROM servicios s " +
                "INNER JOIN vehiculos v ON s.id_vehiculo = v.id_vehiculo " +
                "WHERE v.placas = ? AND s.estado = 'Completado' " +
                "ORDER BY s.fecha_inicio DESC LIMIT 1";
        int idServicio = -1;

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, placas);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                idServicio = rs.getInt("id_servicio");
            }

            DatabaseUtils.cerrarRecursos(conn, stmt, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return idServicio;
    }

    private Servicio obtenerDatosServicio(int idServicio) {
        String query = "SELECT costo_total FROM servicios WHERE id_servicio = ?";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, idServicio);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Servicio servicio = new Servicio();
                servicio.costoTotal = rs.getObject("costo_total") != null ? rs.getDouble("costo_total") : null;
                DatabaseUtils.cerrarRecursos(conn, stmt, rs);
                return servicio;
            }

            DatabaseUtils.cerrarRecursos(conn, stmt, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String generarNumeroFactura() {
        // Obtener la serie desde la configuración
        String serie = "FAC-2024-"; // Por defecto
        String queryConfig = "SELECT serie_facturas FROM configuracion_sistema LIMIT 1";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(queryConfig);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                serie = rs.getString("serie_facturas");
            }

            DatabaseUtils.cerrarRecursos(conn, stmt, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Obtener el último número de factura
        String queryUltimo = "SELECT numero_factura FROM facturas WHERE numero_factura LIKE ? ORDER BY id_factura DESC LIMIT 1";
        int siguienteNumero = 1;

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(queryUltimo);
            stmt.setString(1, serie + "%");
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String ultimoNumero = rs.getString("numero_factura");
                String numeroStr = ultimoNumero.substring(serie.length());
                siguienteNumero = Integer.parseInt(numeroStr) + 1;
            }

            DatabaseUtils.cerrarRecursos(conn, stmt, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return serie + String.format("%03d", siguienteNumero);
    }

    private void actualizarEstadoServicio(int idServicio, String estado) {
        String query = "UPDATE servicios SET estado = ? WHERE id_servicio = ?";
        DatabaseUtils.ejecutarUpdate(query, estado, idServicio);
    }

    private Factura obtenerFacturaPorId(int idFactura) {
        String query = "SELECT f.*, c.nombre as cliente, " +
                "CONCAT(v.marca, ' ', v.modelo, ' - ', v.placas) as vehiculo, " +
                "s.descripcion_servicio " +
                "FROM facturas f " +
                "INNER JOIN servicios s ON f.id_servicio = s.id_servicio " +
                "INNER JOIN vehiculos v ON s.id_vehiculo = v.id_vehiculo " +
                "INNER JOIN clientes c ON v.id_cliente = c.id_cliente " +
                "WHERE f.id_factura = ?";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, idFactura);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Factura factura = new Factura();
                factura.idFactura = rs.getInt("id_factura");
                factura.numeroFactura = rs.getString("numero_factura");
                factura.cliente = rs.getString("cliente");
                factura.vehiculo = rs.getString("vehiculo");
                factura.fechaEmision = rs.getString("fecha_emision");
                factura.fechaVencimiento = rs.getString("fecha_vencimiento");
                factura.estado = rs.getString("estado");
                factura.metodoPago = rs.getString("metodo_pago");
                factura.subtotal = rs.getDouble("subtotal");
                factura.iva = rs.getDouble("iva");
                factura.total = rs.getDouble("total");
                factura.notas = rs.getString("notas");
                factura.descripcionServicio = rs.getString("descripcion_servicio");

                DatabaseUtils.cerrarRecursos(conn, stmt, rs);
                return factura;
            }

            DatabaseUtils.cerrarRecursos(conn, stmt, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String obtenerEmailCliente(String nombreCliente) {
        String query = "SELECT email FROM clientes WHERE nombre = ?";
        String email = null;

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, nombreCliente);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                email = rs.getString("email");
            }

            DatabaseUtils.cerrarRecursos(conn, stmt, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return email;
    }

    // Clases auxiliares
    private static class Servicio {
        Double costoTotal;
    }

    private static class Factura {
        int idFactura;
        String numeroFactura;
        String cliente;
        String vehiculo;
        String fechaEmision;
        String fechaVencimiento;
        String estado;
        String metodoPago;
        double subtotal;
        double iva;
        double total;
        String notas;
        String descripcionServicio;
    }
}