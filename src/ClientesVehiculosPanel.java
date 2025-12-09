import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.*;

public class ClientesVehiculosPanel extends JPanel {
    private JTable tablaClientes;
    private JTable tablaVehiculos;
    private DefaultTableModel modeloClientes;
    private DefaultTableModel modeloVehiculos;
    private JTextField campoBusquedaClientes;
    private JTextField campoBusquedaVehiculos;
    private JTabbedPane pestañas;

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
        pestañas = crearPestanas();
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
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel panelClientes = crearPanelClientes();
        tabs.addTab("Clientes", panelClientes);

        JPanel panelVehiculos = crearPanelVehiculos();
        tabs.addTab("Vehículos", panelVehiculos);

        return tabs;
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

        JButton botonBuscarClientes = crearBotonEstilizado("Buscar", new Color(30, 60, 114));
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
        JButton botonReporteClientes = crearBotonEstilizado("Reporte PDF", new Color(23, 162, 184)); 

        panelBotonesClientes.add(botonAgregarCliente);
        panelBotonesClientes.add(botonEditarCliente);
        panelBotonesClientes.add(botonEliminarCliente);
        panelBotonesClientes.add(botonReporteClientes);

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
        botonReporteClientes.addActionListener(e -> ReportePDFUtils.generarReporteTablaPDF(tablaClientes, "REPORTE DE CLIENTES - TALLER CASA DEL MOTOR", "Reporte_Clientes"));

        return panel;
    }

    private JPanel crearPanelVehiculos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel de búsqueda (Diseño Original Restaurado)
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.setBackground(Color.WHITE);
        panelBusqueda.setBorder(new EmptyBorder(0, 0, 10, 0));

        campoBusquedaVehiculos = new JTextField(20);
        campoBusquedaVehiculos.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campoBusquedaVehiculos.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(8, 12, 8, 12)));

        JButton botonBuscarVehiculos = crearBotonEstilizado("Buscar", new Color(30, 60, 114));
        JButton botonLimpiarVehiculos = crearBotonEstilizado("Limpiar", new Color(100, 100, 100));

        panelBusqueda.add(new JLabel("Buscar vehículo:"));
        panelBusqueda.add(campoBusquedaVehiculos);
        panelBusqueda.add(botonBuscarVehiculos);
        panelBusqueda.add(botonLimpiarVehiculos);

        // Tabla de vehículos
        String[] columnasVehiculos = { "ID", "Cliente", "Placas", "Marca", "Modelo", "Año", "Color", "VIN" };
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

        // Panel de botones (Diseño Original Restaurado)
        JPanel panelBotonesVehiculos = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotonesVehiculos.setBackground(Color.WHITE);

        JButton botonAgregarVehiculo = crearBotonEstilizado("Agregar Vehículo", new Color(40, 167, 69));
        JButton botonEditarVehiculo = crearBotonEstilizado("Editar Vehículo", new Color(255, 193, 7));
        JButton botonEliminarVehiculo = crearBotonEstilizado("Eliminar Vehículo", new Color(220, 53, 69));
        JButton botonHistorialVehiculo = crearBotonEstilizado("Historial", new Color(108, 117, 125)); 
        JButton botonReporteVehiculos = crearBotonEstilizado("Reporte PDF", new Color(23, 162, 184));

        panelBotonesVehiculos.add(botonAgregarVehiculo);
        panelBotonesVehiculos.add(botonEditarVehiculo);
        panelBotonesVehiculos.add(botonEliminarVehiculo);
        panelBotonesVehiculos.add(botonHistorialVehiculo);
        panelBotonesVehiculos.add(botonReporteVehiculos);

        panel.add(panelBusqueda, BorderLayout.NORTH);
        panel.add(scrollVehiculos, BorderLayout.CENTER);
        panel.add(panelBotonesVehiculos, BorderLayout.SOUTH);

        // Acciones
        botonBuscarVehiculos.addActionListener(e -> buscarVehiculos());
        botonLimpiarVehiculos.addActionListener(e -> {
            campoBusquedaVehiculos.setText("");
            cargarDatosVehiculos();
        });
        botonAgregarVehiculo.addActionListener(e -> agregarVehiculo());
        botonEditarVehiculo.addActionListener(e -> editarVehiculo());
        botonEliminarVehiculo.addActionListener(e -> eliminarVehiculo());
        botonHistorialVehiculo.addActionListener(e -> verHistorialVehiculo());
        botonReporteVehiculos.addActionListener(e -> ReportePDFUtils.generarReporteTablaPDF(tablaVehiculos, "REPORTE DE VEHÍCULOS - TALLER CASA DEL MOTOR", "Reporte_Vehiculos"));

        return panel;
    }

    private void buscarVehiculos() {
        String textoBusqueda = campoBusquedaVehiculos.getText().trim();

        if (textoBusqueda.isEmpty()) {
            cargarDatosVehiculos();
            return;
        }

        modeloVehiculos.setRowCount(0);

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            // Corrección aplicada: Búsqueda flexible SIN pedir columna estado
            String sql = "SELECT v.id_vehiculo, c.nombre, v.placas, v.marca, v.modelo, v.año, v.color, v.vin " +
                         "FROM vehiculos v JOIN clientes c ON v.id_cliente = c.id_cliente " +
                         "WHERE (v.marca LIKE ? OR v.placas LIKE ? OR v.modelo LIKE ?)";

            stmt = conn.prepareStatement(sql);
            String parametro = "%" + textoBusqueda + "%";
            stmt.setString(1, parametro);
            stmt.setString(2, parametro);
            stmt.setString(3, parametro);

            rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] fila = new Object[] {
                        rs.getInt("id_vehiculo"),
                        rs.getString("nombre"),
                        rs.getString("placas"),
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getInt("año"),
                        rs.getString("color"),
                        rs.getString("vin")
                };
                modeloVehiculos.addRow(fila);
            }

            if (modeloVehiculos.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No se encontraron vehículos que coincidan con: " + textoBusqueda, "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al buscar vehículos: " + e.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
        } finally {
            DatabaseUtils.cerrarRecursos(conn, stmt, rs);
        }
    }

    private void verHistorialVehiculo() {
        int fila = tablaVehiculos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un vehículo para ver su historial.");
            return;
        }
        
        String marca = (String) modeloVehiculos.getValueAt(fila, 3);
        String modelo = (String) modeloVehiculos.getValueAt(fila, 4);
        String placas = (String) modeloVehiculos.getValueAt(fila, 2);
        
        String desc = marca + " " + modelo + " (" + placas + ")";
        JOptionPane.showMessageDialog(this, "Historial de servicios para:\n" + desc + "\n\n(Aquí se mostrará el historial completo de servicios)", "Historial", JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel crearPanelInferior() {
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelInferior.setBackground(new Color(245, 247, 250));

        JButton botonImprimir = crearBotonEstilizado("Imprimir Vista Actual", new Color(108, 117, 125));
        JButton botonExportar = crearBotonEstilizado("Exportar a Excel (CSV)", new Color(25, 135, 84));

        panelInferior.add(botonImprimir);
        panelInferior.add(botonExportar);

        botonImprimir.addActionListener(e -> imprimirVista());
        
        botonExportar.addActionListener(e -> {
            String[] opciones = {"Clientes", "Vehículos"};
            int seleccion = JOptionPane.showOptionDialog(this, "¿Qué datos desea exportar?", "Exportar Datos",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

            if (seleccion == 0) {
                ExportarUtils.exportarTablaACSV(tablaClientes, this);
            } else if (seleccion == 1) {
                ExportarUtils.exportarTablaACSV(tablaVehiculos, this);
            }
        });

        return panelInferior;
    }

    // ===============================================================
    // LÓGICA DE CARGA Y CRUD
    // ===============================================================

    private void cargarDatosClientes() {
        modeloClientes.setRowCount(0);
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT id_cliente, nombre, telefono, email, direccion, fecha_registro, rfc, estado FROM clientes WHERE estado = 'Activo'";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Object[] fila = new Object[] {
                        rs.getInt("id_cliente"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("email"),
                        rs.getString("direccion"),
                        rs.getDate("fecha_registro"),
                        rs.getString("rfc"),
                        rs.getString("estado")
                };
                modeloClientes.addRow(fila);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar clientes: " + e.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
        } finally {
            DatabaseUtils.cerrarRecursos(conn, stmt, rs);
        }
    }

    private void buscarClientes() {
        String textoBusqueda = campoBusquedaClientes.getText().trim();
        modeloClientes.setRowCount(0);

        if (textoBusqueda.isEmpty()) {
            cargarDatosClientes();
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT id_cliente, nombre, telefono, email, direccion, fecha_registro, rfc, estado " +
                         "FROM clientes WHERE estado = 'Activo' AND (nombre LIKE ? OR telefono LIKE ? OR email LIKE ?)";
            
            stmt = conn.prepareStatement(sql);
            String likeText = "%" + textoBusqueda + "%";
            stmt.setString(1, likeText);
            stmt.setString(2, likeText);
            stmt.setString(3, likeText);

            rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] fila = new Object[] {
                        rs.getInt("id_cliente"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("email"),
                        rs.getString("direccion"),
                        rs.getDate("fecha_registro"),
                        rs.getString("rfc"),
                        rs.getString("estado")
                };
                modeloClientes.addRow(fila);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al buscar clientes: " + e.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
        } finally {
            DatabaseUtils.cerrarRecursos(conn, stmt, rs);
        }
    }

    private void cargarDatosVehiculos() {
        modeloVehiculos.setRowCount(0);
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            // Corrección aplicada: Query limpio, sin buscar columna 'estado' en vehículos
            String sql = "SELECT v.id_vehiculo, c.nombre, v.placas, v.marca, v.modelo, v.año, v.color, v.vin " +
                         "FROM vehiculos v JOIN clientes c ON v.id_cliente = c.id_cliente";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Object[] fila = new Object[] {
                        rs.getInt("id_vehiculo"),
                        rs.getString("nombre"),
                        rs.getString("placas"),
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getInt("año"),
                        rs.getString("color"),
                        rs.getString("vin")
                };
                modeloVehiculos.addRow(fila);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar vehículos: " + e.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
        } finally {
            DatabaseUtils.cerrarRecursos(conn, stmt, rs);
        }
    }

    private List<String> obtenerClientes() {
        List<String> clientes = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT id_cliente, nombre FROM clientes WHERE estado = 'Activo' ORDER BY nombre";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                clientes.add(rs.getInt("id_cliente") + " - " + rs.getString("nombre"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.cerrarRecursos(conn, stmt, rs);
        }
        return clientes;
    }

    private void agregarCliente() {
        JTextField txtNombre = new JTextField();
        JTextField txtTelefono = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtDireccion = new JTextField();
        JTextField txtRFC = new JTextField();

        Object[] message = {
                "Nombre:", txtNombre,
                "Teléfono:", txtTelefono,
                "Email:", txtEmail,
                "Dirección:", txtDireccion,
                "RFC:", txtRFC
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Agregar Nuevo Cliente", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String nombre = txtNombre.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String email = txtEmail.getText().trim();
            String direccion = txtDireccion.getText().trim();
            String rfc = txtRFC.getText().trim();

            if (nombre.isEmpty() || telefono.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nombre y Teléfono son obligatorios.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql = "INSERT INTO clientes (nombre, telefono, email, direccion, rfc, estado, fecha_registro) VALUES (?, ?, ?, ?, ?, 'Activo', NOW())";
            
            if (DatabaseUtils.ejecutarUpdate(sql, nombre, telefono, email, direccion, rfc) > 0) {
                JOptionPane.showMessageDialog(this, "Cliente agregado exitosamente.");
                cargarDatosClientes();
            } else {
                JOptionPane.showMessageDialog(this, "Error al agregar cliente.", "Error DB", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarCliente() {
        int fila = tablaClientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente para editar.");
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

        Object[] message = {
                "Nombre:", txtNombre,
                "Teléfono:", txtTelefono,
                "Email:", txtEmail,
                "Dirección:", txtDireccion,
                "RFC:", txtRFC,
                "Estado:", comboEstado
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Editar Cliente", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String nombre = txtNombre.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String email = txtEmail.getText().trim();
            String direccion = txtDireccion.getText().trim();
            String rfc = txtRFC.getText().trim();
            String estado = (String) comboEstado.getSelectedItem();

            if (nombre.isEmpty() || telefono.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nombre y Teléfono son obligatorios.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql = "UPDATE clientes SET nombre=?, telefono=?, email=?, direccion=?, rfc=?, estado=? WHERE id_cliente=?";
            
            if (DatabaseUtils.ejecutarUpdate(sql, nombre, telefono, email, direccion, rfc, estado, id) > 0) {
                JOptionPane.showMessageDialog(this, "Cliente actualizado exitosamente.");
                cargarDatosClientes();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar cliente.", "Error DB", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarCliente() {
        int fila = tablaClientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente para eliminar.");
            return;
        }

        int id = (int) modeloClientes.getValueAt(fila, 0);
        String nombre = (String) modeloClientes.getValueAt(fila, 1);

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que deseas dar de baja (desactivar) al cliente " + nombre + "?",
                "Confirmar Baja de Cliente", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            String sql = "UPDATE clientes SET estado = 'Inactivo' WHERE id_cliente = ?";
            
            if (DatabaseUtils.ejecutarUpdate(sql, id) > 0) {
                JOptionPane.showMessageDialog(this, "Cliente dado de baja exitosamente.");
                cargarDatosClientes();
            } else {
                JOptionPane.showMessageDialog(this, "Error al dar de baja al cliente.", "Error DB", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void agregarVehiculo() {
        List<String> clientes = obtenerClientes();
        if (clientes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe registrar clientes primero.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<String> comboCliente = new JComboBox<>(clientes.toArray(new String[0]));
        JTextField txtPlacas = new JTextField();
        JTextField txtMarca = new JTextField();
        JTextField txtModelo = new JTextField();
        JTextField txtAnio = new JTextField();
        JTextField txtColor = new JTextField();
        JTextField txtVIN = new JTextField();

        Object[] message = {
                "Cliente:", comboCliente,
                "Placas:", txtPlacas,
                "Marca:", txtMarca,
                "Modelo:", txtModelo,
                "Año:", txtAnio,
                "Color:", txtColor,
                "VIN:", txtVIN
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Agregar Nuevo Vehículo", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String clienteSeleccionado = (String) comboCliente.getSelectedItem();
                int idCliente = Integer.parseInt(clienteSeleccionado.split(" - ")[0]);
                String placas = txtPlacas.getText().trim();
                String marca = txtMarca.getText().trim();
                String modelo = txtModelo.getText().trim();
                int anio = Integer.parseInt(txtAnio.getText().trim());
                String color = txtColor.getText().trim();
                String vin = txtVIN.getText().trim();

                if (placas.isEmpty() || marca.isEmpty() || modelo.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Placas, Marca y Modelo son obligatorios.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Corrección aplicada: Eliminado 'estado' del INSERT
                String sql = "INSERT INTO vehiculos (id_cliente, placas, marca, modelo, año, color, vin) VALUES (?, ?, ?, ?, ?, ?, ?)";
                
                if (DatabaseUtils.ejecutarUpdate(sql, idCliente, placas, marca, modelo, anio, color, vin) > 0) {
                    JOptionPane.showMessageDialog(this, "Vehículo agregado exitosamente.");
                    cargarDatosVehiculos();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al agregar vehículo.", "Error DB", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "El campo Año debe ser un número válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarVehiculo() {
        int fila = tablaVehiculos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un vehículo para editar.");
            return;
        }

        int idVehiculo = (int) modeloVehiculos.getValueAt(fila, 0);
        List<String> clientes = obtenerClientes();

        JComboBox<String> comboCliente = new JComboBox<>(clientes.toArray(new String[0]));
        String nombreClienteActual = (String) modeloVehiculos.getValueAt(fila, 1);
        int idClienteActual = getIdClientePorNombre(nombreClienteActual); 
        
        for (int i = 0; i < comboCliente.getItemCount(); i++) {
            String item = comboCliente.getItemAt(i);
            if (item.startsWith(idClienteActual + " - ")) {
                comboCliente.setSelectedIndex(i);
                break;
            }
        }

        JTextField txtPlacas = new JTextField((String) modeloVehiculos.getValueAt(fila, 2));
        JTextField txtMarca = new JTextField((String) modeloVehiculos.getValueAt(fila, 3));
        JTextField txtModelo = new JTextField((String) modeloVehiculos.getValueAt(fila, 4));
        JTextField txtAnio = new JTextField(String.valueOf(modeloVehiculos.getValueAt(fila, 5)));
        JTextField txtColor = new JTextField((String) modeloVehiculos.getValueAt(fila, 6));
        JTextField txtVIN = new JTextField((String) modeloVehiculos.getValueAt(fila, 7));

        Object[] message = {
                "Cliente:", comboCliente,
                "Placas:", txtPlacas,
                "Marca:", txtMarca,
                "Modelo:", txtModelo,
                "Año:", txtAnio,
                "Color:", txtColor,
                "VIN:", txtVIN
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Editar Vehículo", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String clienteSeleccionado = (String) comboCliente.getSelectedItem();
                int nuevoIdCliente = Integer.parseInt(clienteSeleccionado.split(" - ")[0]);
                String placas = txtPlacas.getText().trim();
                String marca = txtMarca.getText().trim();
                String modelo = txtModelo.getText().trim();
                int anio = Integer.parseInt(txtAnio.getText().trim());
                String color = txtColor.getText().trim();
                String vin = txtVIN.getText().trim();

                if (placas.isEmpty() || marca.isEmpty() || modelo.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Placas, Marca y Modelo son obligatorios.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String sql = "UPDATE vehiculos SET id_cliente=?, placas=?, marca=?, modelo=?, año=?, color=?, vin=? WHERE id_vehiculo=?";
                
                if (DatabaseUtils.ejecutarUpdate(sql, nuevoIdCliente, placas, marca, modelo, anio, color, vin, idVehiculo) > 0) {
                    JOptionPane.showMessageDialog(this, "Vehículo actualizado exitosamente.");
                    cargarDatosVehiculos();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar vehículo.", "Error DB", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "El campo Año debe ser un número válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarVehiculo() {
        int fila = tablaVehiculos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un vehículo para eliminar.");
            return;
        }

        int id = (int) modeloVehiculos.getValueAt(fila, 0);
        String placas = (String) modeloVehiculos.getValueAt(fila, 2);

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que deseas eliminar PERMANENTEMENTE el vehículo con placas " + placas + "?",
                "Confirmar Eliminación de Vehículo", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            // Corrección aplicada: Borrado físico (DELETE)
            String sql = "DELETE FROM vehiculos WHERE id_vehiculo = ?";
            
            if (DatabaseUtils.ejecutarUpdate(sql, id) > 0) {
                JOptionPane.showMessageDialog(this, "Vehículo eliminado exitosamente.");
                cargarDatosVehiculos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el vehículo.", "Error DB", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private int getIdClientePorNombre(String nombre) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int id = -1;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT id_cliente FROM clientes WHERE nombre = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nombre);
            rs = stmt.executeQuery();

            if (rs.next()) {
                id = rs.getInt("id_cliente");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.cerrarRecursos(conn, stmt, rs);
        }
        return id;
    }

    private JButton crearBotonEstilizado(String texto, Color color) {
        JButton button = new JButton(texto);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.putClientProperty("JButton.buttonType", "roundRect"); 

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void imprimirVista() {
        JTable tablaAImprimir;
        String tituloReporte;

        if (pestañas.getSelectedIndex() == 0) {
            tablaAImprimir = tablaClientes;
            tituloReporte = "REPORTE DE CLIENTES";
        } else {
            tablaAImprimir = tablaVehiculos;
            tituloReporte = "REPORTE DE VEHÍCULOS";
        }

        try {
            MessageFormat header = new MessageFormat(tituloReporte + " - CASA DEL MOTOR");
            MessageFormat footer = new MessageFormat("Fecha: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()) + " - Página {0}");
            
            if (!tablaAImprimir.print(JTable.PrintMode.FIT_WIDTH, header, footer)) {
                JOptionPane.showMessageDialog(this, "Impresión cancelada por el usuario", "Cancelado", JOptionPane.WARNING_MESSAGE);
            }
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(this, "Error al imprimir: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}