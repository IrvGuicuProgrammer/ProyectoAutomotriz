import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.print.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClientesVehiculosPanel extends JPanel {
    private JTable tablaClientes;
    private JTable tablaVehiculos;
    private DefaultTableModel modeloClientes;
    private DefaultTableModel modeloVehiculos;
    private JTextField campoBusquedaClientes;
    private JTextField campoBusquedaVehiculos;

    public ClientesVehiculosPanel() {
        inicializarComponentes();
        cargarDatosClientes();
        cargarDatosVehiculos();
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

        // Panel central con pestañas
        JTabbedPane pestañas = crearPestanas();
        panelPrincipal.add(pestañas, BorderLayout.CENTER);

        // Panel inferior con botones generales
        JPanel panelInferior = crearPanelInferior();
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);

        add(panelPrincipal, BorderLayout.CENTER);
    }

    private JPanel crearBarraTitulo() {
        JPanel barraTitulo = new JPanel(new BorderLayout());
        barraTitulo.setBackground(new Color(30, 60, 114));
        barraTitulo.setBorder(new EmptyBorder(15, 25, 15, 25));
        barraTitulo.setPreferredSize(new Dimension(0, 60));

        JLabel titulo = new JLabel("CLIENTES Y VEHÍCULOS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        titulo.setHorizontalAlignment(SwingConstants.LEFT);

        barraTitulo.add(titulo, BorderLayout.WEST);
        return barraTitulo;
    }

    private JTabbedPane crearPestanas() {
        JTabbedPane pestañas = new JTabbedPane();
        pestañas.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel panelClientes = crearPanelClientes();
        pestañas.addTab("Clientes", panelClientes);

        JPanel panelVehiculos = crearPanelVehiculos();
        pestañas.addTab("Vehículos", panelVehiculos);

        return pestañas;
    }

    private JPanel crearPanelClientes() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.setBackground(Color.WHITE);
        panelBusqueda.setBorder(new EmptyBorder(0, 0, 10, 0));

        campoBusquedaClientes = new JTextField(20);
        campoBusquedaClientes.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campoBusquedaClientes.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(8, 12, 8, 12)));

        JButton botonBuscarClientes = crearBotonEstilizado("Buscar Cliente", new Color(30, 60, 114));
        JButton botonLimpiarClientes = crearBotonEstilizado("Limpiar", new Color(100, 100, 100));

        panelBusqueda.add(new JLabel("Buscar cliente:"));
        panelBusqueda.add(campoBusquedaClientes);
        panelBusqueda.add(botonBuscarClientes);
        panelBusqueda.add(botonLimpiarClientes);

        // Tabla de clientes
        String[] columnasClientes = { "ID", "Nombre", "Teléfono", "Email", "Dirección", "Fecha Registro", "RFC", "Estado" };
        modeloClientes = new DefaultTableModel(columnasClientes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaClientes = new JTable(modeloClientes);
        tablaClientes.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaClientes.setRowHeight(30);
        tablaClientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaClientes.getTableHeader().setBackground(new Color(30, 60, 114));
        tablaClientes.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollClientes = new JScrollPane(tablaClientes);

        // Panel de botones
        JPanel panelBotonesClientes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotonesClientes.setBackground(Color.WHITE);

        JButton botonAgregarCliente = crearBotonEstilizado("Agregar Cliente", new Color(40, 167, 69));
        JButton botonEditarCliente = crearBotonEstilizado("Editar Cliente", new Color(255, 193, 7));
        JButton botonEliminarCliente = crearBotonEstilizado("Eliminar Cliente", new Color(220, 53, 69));

        panelBotonesClientes.add(botonAgregarCliente);
        panelBotonesClientes.add(botonEditarCliente);
        panelBotonesClientes.add(botonEliminarCliente);

        panel.add(panelBusqueda, BorderLayout.NORTH);
        panel.add(scrollClientes, BorderLayout.CENTER);
        panel.add(panelBotonesClientes, BorderLayout.SOUTH);

        // Acciones
        botonBuscarClientes.addActionListener(e -> buscarClientes());
        botonLimpiarClientes.addActionListener(e -> {
            campoBusquedaClientes.setText("");
            cargarDatosClientes();
        });
        botonAgregarCliente.addActionListener(e -> agregarCliente());
        botonEditarCliente.addActionListener(e -> editarCliente());
        botonEliminarCliente.addActionListener(e -> eliminarCliente());

        return panel;
    }

    private JPanel crearPanelVehiculos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.setBackground(Color.WHITE);
        panelBusqueda.setBorder(new EmptyBorder(0, 0, 10, 0));

        campoBusquedaVehiculos = new JTextField(20);
        campoBusquedaVehiculos.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campoBusquedaVehiculos.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(8, 12, 8, 12)));

        JButton botonBuscarVehiculos = crearBotonEstilizado("Buscar Vehículo", new Color(30, 60, 114));
        JButton botonLimpiarVehiculos = crearBotonEstilizado("Limpiar", new Color(100, 100, 100));

        panelBusqueda.add(new JLabel("Buscar vehículo:"));
        panelBusqueda.add(campoBusquedaVehiculos);
        panelBusqueda.add(botonBuscarVehiculos);
        panelBusqueda.add(botonLimpiarVehiculos);

        String[] columnasVehiculos = { "ID", "Placas", "Marca", "Modelo", "Año", "Color", "Cliente", "VIN", "Kilometraje" };
        modeloVehiculos = new DefaultTableModel(columnasVehiculos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaVehiculos = new JTable(modeloVehiculos);
        tablaVehiculos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaVehiculos.setRowHeight(30);
        tablaVehiculos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaVehiculos.getTableHeader().setBackground(new Color(30, 60, 114));
        tablaVehiculos.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollVehiculos = new JScrollPane(tablaVehiculos);

        JPanel panelBotonesVehiculos = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotonesVehiculos.setBackground(Color.WHITE);

        JButton botonAgregarVehiculo = crearBotonEstilizado("Agregar Vehículo", new Color(40, 167, 69));
        JButton botonEditarVehiculo = crearBotonEstilizado("Editar Vehículo", new Color(255, 193, 7));
        JButton botonEliminarVehiculo = crearBotonEstilizado("Eliminar Vehículo", new Color(220, 53, 69));
        JButton botonHistorialVehiculo = crearBotonEstilizado("Historial", new Color(108, 117, 125));

        panelBotonesVehiculos.add(botonAgregarVehiculo);
        panelBotonesVehiculos.add(botonEditarVehiculo);
        panelBotonesVehiculos.add(botonEliminarVehiculo);
        panelBotonesVehiculos.add(botonHistorialVehiculo);

        panel.add(panelBusqueda, BorderLayout.NORTH);
        panel.add(scrollVehiculos, BorderLayout.CENTER);
        panel.add(panelBotonesVehiculos, BorderLayout.SOUTH);

        botonBuscarVehiculos.addActionListener(e -> buscarVehiculos());
        botonLimpiarVehiculos.addActionListener(e -> {
            campoBusquedaVehiculos.setText("");
            cargarDatosVehiculos();
        });
        botonAgregarVehiculo.addActionListener(e -> agregarVehiculo());
        botonEditarVehiculo.addActionListener(e -> editarVehiculo());
        botonEliminarVehiculo.addActionListener(e -> eliminarVehiculo());
        botonHistorialVehiculo.addActionListener(e -> verHistorialVehiculo());

        return panel;
    }

    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(20, 0, 10, 0));

        JButton botonReporteClientes = crearBotonEstilizado("Reporte Clientes", new Color(30, 60, 114));
        JButton botonReporteVehiculos = crearBotonEstilizado("Reporte Vehículos", new Color(30, 60, 114));
        JButton botonExportar = crearBotonEstilizado("Exportar Datos", new Color(40, 167, 69));
        JButton botonImprimirClientes = crearBotonEstilizado("Imprimir Clientes", new Color(0, 123, 255));
        JButton botonImprimirVehiculos = crearBotonEstilizado("Imprimir Vehículos", new Color(0, 123, 255));

        panel.add(botonReporteClientes);
        panel.add(botonReporteVehiculos);
        panel.add(botonExportar);
        panel.add(botonImprimirClientes);
        panel.add(botonImprimirVehiculos);

        // ACCIONES MODIFICADAS
        botonExportar.addActionListener(e -> {
            // Preguntar qué tabla exportar
            String[] opciones = {"Clientes", "Vehículos"};
            int seleccion = JOptionPane.showOptionDialog(this, "¿Qué datos desea exportar?", "Exportar Datos",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

            if (seleccion == 0) {
                ExportarUtils.exportarTablaACSV(tablaClientes, this);
            } else if (seleccion == 1) {
                ExportarUtils.exportarTablaACSV(tablaVehiculos, this);
            }
        });

        // ACCIONES DE REPORTE (PDF)
        botonReporteClientes.addActionListener(e -> ReportePDFUtils.generarReporteTablaPDF(tablaClientes, "REPORTE DE CLIENTES - TALLER CASA DEL MOTOR", "Reporte_Clientes"));
        botonReporteVehiculos.addActionListener(e -> ReportePDFUtils.generarReporteTablaPDF(tablaVehiculos, "REPORTE DE VEHÍCULOS - TALLER DEL MOTOR", "Reporte_Vehiculos"));

        // ACCIONES DE IMPRESIÓN (impresora física, ya implementadas)
        botonImprimirClientes.addActionListener(e -> imprimirTabla(tablaClientes, "REPORTE DE CLIENTES - TALLER DEL MOTOR"));
        botonImprimirVehiculos.addActionListener(e -> imprimirTabla(tablaVehiculos, "REPORTE DE VEHÍCULOS - TALLER DEL MOTOR"));

        return panel;
    }

    private JButton crearBotonEstilizado(String texto, Color color) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(color.brighter());
                } else {
                    g2.setColor(color);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();

                super.paintComponent(g);
            }
        };

        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setForeground(Color.WHITE);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setContentAreaFilled(false);
        boton.setPreferredSize(new Dimension(180, 40));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return boton;
    }

    // ==================== IMPRESIÓN DE TABLAS (MÉTODO EXISTENTE) ====================
    private void imprimirTabla(JTable tabla, String tituloReporte) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName(tituloReporte);

        Printable printable = new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
                if (pageIndex > 0) return Printable.NO_SUCH_PAGE;

                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX() + 20, pageFormat.getImageableY() + 20);

                // Fuentes
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 20));
                g2d.drawString(tituloReporte, 0, 20);

                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2d.drawString("Taller Mecánico Juan", 0, 50);
                g2d.drawString("Fecha: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), 0, 70);

                g2d.drawLine(0, 85, 540, 85);

                // Encabezados
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 10));
                int y = 110;
                int[] anchos = obtenerAnchosColumnas(tabla);

                for (int i = 0; i < tabla.getColumnCount(); i++) {
                    g2d.drawString(tabla.getColumnName(i), i == 0 ? 5 : calcularX(i, anchos), y);
                }

                g2d.drawLine(0, y + 20, 540, y + 20);
                y += 30;

                // Filas
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                int filasPorPagina = 45;
                int inicio = 0;
                int fin = Math.min(tabla.getRowCount(), inicio + filasPorPagina);

                for (int row = inicio; row < fin; row++) {
                    int x = 0;
                    for (int col = 0; col < tabla.getColumnCount(); col++) {
                        Object valor = tabla.getValueAt(row, col);
                        String texto = valor != null ? valor.toString() : "";
                        g2d.drawString(ajustarTexto(texto, anchos[col] - 10), x + 5, y);
                        x += anchos[col];
                    }
                    y += 18;
                    if (y > 750) break;
                }

                // Pie de página
                g2d.setFont(new Font("Segoe UI", Font.ITALIC, 9));
                g2d.setColor(Color.GRAY);
                g2d.drawString("Total de registros: " + tabla.getRowCount() + " | Impreso el " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), 0, 780);

                return Printable.PAGE_EXISTS;
            }
        };

        job.setPrintable(printable);

        if (job.printDialog()) {
            try {
                job.print();
                JOptionPane.showMessageDialog(this, "Reporte enviado a la impresora.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Error al imprimir:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private int[] obtenerAnchosColumnas(JTable tabla) {
        if (tabla == tablaClientes) {
            return new int[]{50, 130, 100, 150, 150, 90, 100, 70};
        } else {
            return new int[]{50, 90, 90, 100, 60, 80, 120, 100, 90};
        }
    }

    private int calcularX(int columna, int[] anchos) {
        int x = 0;
        for (int i = 0; i < columna; i++) x += anchos[i];
        return x + 5;
    }

    private String ajustarTexto(String texto, int max) {
        if (texto.length() > max / 6) {
            return texto.substring(0, Math.min(texto.length(), max / 6 - 3)) + "...";
        }
        return texto;
    }

    // ==================== CLIENTES ====================
    private void cargarDatosClientes() {
        String query = "SELECT id_cliente, nombre, telefono, email, direccion, " +
                "DATE_FORMAT(fecha_registro, '%Y-%m-%d') as fecha_registro, rfc, estado FROM clientes ORDER BY id_cliente";
        DatabaseUtils.llenarTablaDesdeConsulta(tablaClientes, query);
    }

    private void buscarClientes() {
        String busqueda = campoBusquedaClientes.getText().trim();
        if (busqueda.isEmpty()) {
            cargarDatosClientes();
            return;
        }

        String query = "SELECT id_cliente, nombre, telefono, email, direccion, " +
                "DATE_FORMAT(fecha_registro, '%Y-%m-%d') as fecha_registro, rfc, estado FROM clientes " +
                "WHERE nombre LIKE ? OR telefono LIKE ? OR email LIKE ? OR rfc LIKE ? ORDER BY id_cliente";

        String param = "%" + busqueda + "%";
        DatabaseUtils.llenarTablaDesdeConsulta(tablaClientes, query, param, param, param, param);
    }

    private void agregarCliente() {
        JTextField txtNombre = new JTextField();
        JTextField txtTelefono = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtDireccion = new JTextField();
        JTextField txtRFC = new JTextField();
        JComboBox<String> comboEstado = new JComboBox<>(new String[]{"Activo", "Inactivo"});

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Nombre:")); panel.add(txtNombre);
        panel.add(new JLabel("Teléfono:")); panel.add(txtTelefono);
        panel.add(new JLabel("Email:")); panel.add(txtEmail);
        panel.add(new JLabel("Dirección:")); panel.add(txtDireccion);
        panel.add(new JLabel("RFC:")); panel.add(txtRFC);
        panel.add(new JLabel("Estado:")); panel.add(comboEstado);

        int result = JOptionPane.showConfirmDialog(this, panel, "Agregar Cliente", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION && !txtNombre.getText().trim().isEmpty()) {
            String sql = "INSERT INTO clientes (nombre, telefono, email, direccion, rfc, estado, fecha_registro) VALUES (?, ?, ?, ?, ?, ?, CURDATE())";
            int r = DatabaseUtils.ejecutarUpdate(sql, txtNombre.getText().trim(), txtTelefono.getText().trim(),
                    txtEmail.getText().trim(), txtDireccion.getText().trim(), txtRFC.getText().trim(), comboEstado.getSelectedItem());
            if (r > 0) {
                JOptionPane.showMessageDialog(this, "Cliente agregado.");
                cargarDatosClientes();
            }
        }
    }

    private void editarCliente() {
        int fila = tablaClientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente.");
            return;
        }

        int id = (int) modeloClientes.getValueAt(fila, 0);
        JTextField txtNombre = new JTextField((String) modeloClientes.getValueAt(fila, 1));
        JTextField txtTelefono = new JTextField((String) modeloClientes.getValueAt(fila, 2));
        JTextField txtEmail = new JTextField((String) modeloClientes.getValueAt(fila, 3));
        JTextField txtDireccion = new JTextField((String) modeloClientes.getValueAt(fila, 4));
        JTextField txtRFC = new JTextField((String) modeloClientes.getValueAt(fila, 6));
        JComboBox<String> comboEstado = new JComboBox<>(new String[]{"Activo", "Inactivo"});
        comboEstado.setSelectedItem(modeloClientes.getValueAt(fila, 7));

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Nombre:")); panel.add(txtNombre);
        panel.add(new JLabel("Teléfono:")); panel.add(txtTelefono);
        panel.add(new JLabel("Email:")); panel.add(txtEmail);
        panel.add(new JLabel("Dirección:")); panel.add(txtDireccion);
        panel.add(new JLabel("RFC:")); panel.add(txtRFC);
        panel.add(new JLabel("Estado:")); panel.add(comboEstado);

        int result = JOptionPane.showConfirmDialog(this, panel, "Editar Cliente", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String sql = "UPDATE clientes SET nombre=?, telefono=?, email=?, direccion=?, rfc=?, estado=? WHERE id_cliente=?";
            int r = DatabaseUtils.ejecutarUpdate(sql, txtNombre.getText().trim(), txtTelefono.getText().trim(),
                    txtEmail.getText().trim(), txtDireccion.getText().trim(), txtRFC.getText().trim(),
                    comboEstado.getSelectedItem(), id);
            if (r > 0) {
                JOptionPane.showMessageDialog(this, "Cliente actualizado.");
                cargarDatosClientes();
            }
        }
    }

    private void eliminarCliente() {
        int fila = tablaClientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente.");
            return;
        }
        int id = (int) modeloClientes.getValueAt(fila, 0);
        String nombre = (String) modeloClientes.getValueAt(fila, 1);

        if (JOptionPane.showConfirmDialog(this, "¿Eliminar al cliente " + nombre + " y todos sus vehículos?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            int r = DatabaseUtils.ejecutarUpdate("DELETE FROM clientes WHERE id_cliente=?", id);
            if (r > 0) {
                JOptionPane.showMessageDialog(this, "Cliente eliminado.");
                cargarDatosClientes();
                cargarDatosVehiculos();
            }
        }
    }

    // ==================== VEHÍCULOS ====================
    private void cargarDatosVehiculos() {
        String query = "SELECT v.id_vehiculo, v.placas, v.marca, v.modelo, v.año, v.color, " +
                "c.nombre as cliente, v.vin, v.kilometraje_actual " +
                "FROM vehiculos v INNER JOIN clientes c ON v.id_cliente = c.id_cliente ORDER BY v.id_vehiculo";
        DatabaseUtils.llenarTablaDesdeConsulta(tablaVehiculos, query);
    }

    private void buscarVehiculos() {
        String busqueda = campoBusquedaVehiculos.getText().trim();
        if (busqueda.isEmpty()) {
            cargarDatosVehiculos();
            return;
        }
        String query = "SELECT v.id_vehiculo, v.placas, v.marca, v.modelo, v.año, v.color, " +
                "c.nombre as cliente, v.vin, v.kil  " +
                "FROM vehiculos v INNER JOIN clientes c ON v.id_cliente = c.id_cliente " +
                "WHERE v.placas LIKE ? OR v.marca LIKE ? OR v.modelo LIKE ? OR c.nombre LIKE ? OR v.vin LIKE ?";
        String p = "%" + busqueda + "%";
        DatabaseUtils.llenarTablaDesdeConsulta(tablaVehiculos, query, p, p, p, p, p);
    }

    private void agregarVehiculo() {
        List<String> clientes = obtenerListaClientes();
        if (clientes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay clientes activos.");
            return;
        }

        JComboBox<String> combo = new JComboBox<>(clientes.toArray(new String[0]));
        JTextField txtPlacas = new JTextField();
        JTextField txtMarca = new JTextField();
        JTextField txtModelo = new JTextField();
        JTextField txtAnio = new JTextField();
        JTextField txtColor = new JTextField();
        JTextField txtVIN = new JTextField();
        JTextField txtKm = new JTextField();

        JPanel p = new JPanel(new GridLayout(0, 2, 10, 10));
        p.add(new JLabel("Cliente:")); p.add(combo);
        p.add(new JLabel("Placas:")); p.add(txtPlacas);
        p.add(new JLabel("Marca:")); p.add(txtMarca);
        p.add(new JLabel("Modelo:")); p.add(txtModelo);
        p.add(new JLabel("Año:")); p.add(txtAnio);
        p.add(new JLabel("Color:")); p.add(txtColor);
        p.add(new JLabel("VIN:")); p.add(txtVIN);
        p.add(new JLabel("Kilometraje:")); p.add(txtKm);

        if (JOptionPane.showConfirmDialog(this, p, "Agregar Vehículo", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            if (txtPlacas.getText().trim().isEmpty() || txtMarca.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Placas y Marca son obligatorios.");
                return;
            }
            int idCliente = obtenerIdCliente((String) combo.getSelectedItem());
            String sql = "INSERT INTO vehiculos (id_cliente, placas, marca, modelo, año, color, vin, kilometraje_actual) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            Object anio = txtAnio.getText().trim().isEmpty() ? null : Integer.valueOf(txtAnio.getText().trim());
            Object km = txtKm.getText().trim().isEmpty() ? null : Double.valueOf(txtKm.getText().trim());

            int r = DatabaseUtils.ejecutarUpdate(sql, idCliente, txtPlacas.getText().trim().toUpperCase(),
                    txtMarca.getText().trim(), txtModelo.getText().trim(), anio, txtColor.getText().trim(),
                    txtVIN.getText().trim().toUpperCase(), km);
            if (r > 0) {
                JOptionPane.showMessageDialog(this, "Vehículo agregado.");
                cargarDatosVehiculos();
            }
        }
    }

    private void editarVehiculo() {
        int fila = tablaVehiculos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un vehículo.");
            return;
        }

        int id = (int) modeloVehiculos.getValueAt(fila, 0);
        List<String> clientes = obtenerListaClientes();
        JComboBox<String> combo = new JComboBox<>(clientes.toArray(new String[0]));
        combo.setSelectedItem(modeloVehiculos.getValueAt(fila, 6));

        JTextField txtPlacas = new JTextField((String) modeloVehiculos.getValueAt(fila, 1));
        JTextField txtMarca = new JTextField((String) modeloVehiculos.getValueAt(fila, 2));
        JTextField txtModelo = new JTextField((String) modeloVehiculos.getValueAt(fila, 3));
        JTextField txtAnio = new JTextField(modeloVehiculos.getValueAt(fila, 4).toString());
        JTextField txtColor = new JTextField((String) modeloVehiculos.getValueAt(fila, 5));
        JTextField txtVIN = new JTextField((String) modeloVehiculos.getValueAt(fila, 7));
        JTextField txtKm = new JTextField(modeloVehiculos.getValueAt(fila, 8).toString());

        JPanel p = new JPanel(new GridLayout(0, 2, 10, 10));
        p.add(new JLabel("Cliente:")); p.add(combo);
        p.add(new JLabel("Placas:")); p.add(txtPlacas);
        p.add(new JLabel("Marca:")); p.add(txtMarca);
        p.add(new JLabel("Modelo:")); p.add(txtModelo);
        p.add(new JLabel("Año:")); p.add(txtAnio);
        p.add(new JLabel("Color:")); p.add(txtColor);
        p.add(new JLabel("VIN:")); p.add(txtVIN);
        p.add(new JLabel("Kilometraje:")); p.add(txtKm);

        if (JOptionPane.showConfirmDialog(this, p, "Editar Vehículo", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            int idCliente = obtenerIdCliente((String) combo.getSelectedItem());
            String sql = "UPDATE vehiculos SET id_cliente=?, placas=?, marca=?, modelo=?, año=?, color=?, vin=?, kilometraje_actual=? WHERE id_vehiculo=?";
            Object anio = txtAnio.getText().trim().isEmpty() ? null : Integer.valueOf(txtAnio.getText().trim());
            Object km = txtKm.getText().trim().isEmpty() ? null : Double.valueOf(txtKm.getText().trim());

            int r = DatabaseUtils.ejecutarUpdate(sql, idCliente, txtPlacas.getText().trim().toUpperCase(),
                    txtMarca.getText().trim(), txtModelo.getText().trim(), anio, txtColor.getText().trim(),
                    txtVIN.getText().trim().toUpperCase(), km, id);
            if (r > 0) {
                JOptionPane.showMessageDialog(this, "Vehículo actualizado.");
                cargarDatosVehiculos();
            }
        }
    }

    private void eliminarVehiculo() {
        int fila = tablaVehiculos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un vehículo.");
            return;
        }
        int id = (int) modeloVehiculos.getValueAt(fila, 0);
        String desc = modeloVehiculos.getValueAt(fila, 2) + " " + modeloVehiculos.getValueAt(fila, 3) + " (" + modeloVehiculos.getValueAt(fila, 1) + ")";

        if (JOptionPane.showConfirmDialog(this, "¿Eliminar el vehículo " + desc + " y sus servicios?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            int r = DatabaseUtils.ejecutarUpdate("DELETE FROM vehiculos WHERE id_vehiculo=?", id);
            if (r > 0) {
                JOptionPane.showMessageDialog(this, "Vehículo eliminado.");
                cargarDatosVehiculos();
            }
        }
    }

    private void verHistorialVehiculo() {
        int fila = tablaVehiculos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un vehículo.");
            return;
        }
        String desc = modeloVehiculos.getValueAt(fila, 2) + " " + modeloVehiculos.getValueAt(fila, 3) + " - " + modeloVehiculos.getValueAt(fila, 1);
        JOptionPane.showMessageDialog(this, "Historial de servicios para:\n" + desc + "\n\n(Aquí se mostrará el historial completo)", "Historial", JOptionPane.INFORMATION_MESSAGE);
    }

    // ==================== MÉTODOS AUXILIARES ====================
    private List<String> obtenerListaClientes() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT nombre FROM clientes WHERE estado = 'Activo' ORDER BY nombre";
        try (var conn = DatabaseConnection.getConnection();
             var ps = conn.prepareStatement(sql);
             var rs = ps.executeQuery()) {
            while (rs.next()) lista.add(rs.getString(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    private int obtenerIdCliente(String nombre) {
        String sql = "SELECT id_cliente FROM clientes WHERE nombre = ?";
        try (var conn = DatabaseConnection.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            var rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}