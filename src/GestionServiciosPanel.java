import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.List; // Importar List para las listas desplegables

public class GestionServiciosPanel extends JPanel {
    private JTable tablaServicios;
    private DefaultTableModel modeloTabla;
    private JTextField campoBusqueda;
    private JComboBox<String> comboFiltroEstado;

    public GestionServiciosPanel() {
        inicializarComponentes();
        cargarDatosServicios();
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

        JLabel titulo = new JLabel("GESTIÓN DE SERVICIOS");
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
                new String[] { "Todos", "Pendiente", "En Proceso", "Completado", "Facturado", "Cancelado" });
        comboFiltroEstado.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton botonBuscar = crearBotonEstilizado("Buscar", new Color(30, 60, 114));
        JButton botonLimpiar = crearBotonEstilizado("Limpiar", new Color(100, 100, 100));
        JButton botonNuevoServicio = crearBotonEstilizado("Nuevo Servicio", new Color(40, 167, 69));

        panelBusqueda.add(new JLabel("Buscar servicio:"));
        panelBusqueda.add(campoBusqueda);
        panelBusqueda.add(new JLabel("Estado:"));
        panelBusqueda.add(comboFiltroEstado);
        panelBusqueda.add(botonBuscar);
        panelBusqueda.add(botonLimpiar);
        panelBusqueda.add(botonNuevoServicio);

        panel.add(panelBusqueda, BorderLayout.CENTER);

        // Acciones de los botones
        botonBuscar.addActionListener(e -> buscarServicios());
        botonLimpiar.addActionListener(e -> {
            campoBusqueda.setText("");
            comboFiltroEstado.setSelectedIndex(0);
            cargarDatosServicios();
        });
        botonNuevoServicio.addActionListener(e -> agregarServicio());

        return panel;
    }

    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 10, 10, 10)));

        // Modelo de tabla
        String[] columnas = { "ID", "Vehículo", "Cliente", "Servicio", "Fecha Inicio", "Fecha Fin", "Estado",
                "Costo Total", "Mecánico" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            // Definir tipos de columnas para asegurar que los números se traten como números
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 7) { // Costo Total
                    return Double.class;
                }
                return String.class;
            }
        };

        tablaServicios = new JTable(modeloTabla);
        tablaServicios.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaServicios.setRowHeight(30);
        tablaServicios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaServicios.getTableHeader().setBackground(new Color(30, 60, 114));
        tablaServicios.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(tablaServicios);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton botonDetalles = crearBotonEstilizado("Ver Detalles", new Color(30, 60, 114));
        JButton botonEditar = crearBotonEstilizado("Editar Servicio", new Color(255, 193, 7));
        JButton botonCambiarEstado = crearBotonEstilizado("Cambiar Estado", new Color(108, 117, 125));
        JButton botonGenerarFactura = crearBotonEstilizado("Generar Factura", new Color(40, 167, 69));
        JButton botonEliminar = crearBotonEstilizado("Eliminar", new Color(220, 53, 69));

        panel.add(botonDetalles);
        panel.add(botonEditar);
        panel.add(botonCambiarEstado);
        panel.add(botonGenerarFactura);
        panel.add(botonEliminar);

        // Acciones de los botones
        botonDetalles.addActionListener(e -> verDetallesServicio());
        botonEditar.addActionListener(e -> editarServicio());
        botonCambiarEstado.addActionListener(e -> cambiarEstadoServicio());
        
        // --- AQUÍ ESTÁ LA FUNCIÓN MODIFICADA ---
        botonGenerarFactura.addActionListener(e -> generarFactura());
        
        botonEliminar.addActionListener(e -> eliminarServicio());

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

    // =========================================================================
    // MÉTODOS DE LÓGICA
    // =========================================================================

    private void cargarDatosServicios() {
        String query = "SELECT s.id_servicio, " +
                "CONCAT(v.marca, ' ', v.modelo, ' - ', v.placas) as vehiculo, " +
                "c.nombre as cliente, " +
                "s.descripcion_servicio, " +
                "DATE_FORMAT(s.fecha_inicio, '%Y-%m-%d') as fecha_inicio, " +
                "DATE_FORMAT(s.fecha_fin, '%Y-%m-%d') as fecha_fin, " +
                "s.estado, " +
                "s.costo_total, " +
                "u.nombre_completo as mecanico " +
                "FROM servicios s " +
                "INNER JOIN vehiculos v ON s.id_vehiculo = v.id_vehiculo " +
                "INNER JOIN clientes c ON v.id_cliente = c.id_cliente " +
                "INNER JOIN usuarios u ON s.id_mecanico = u.id_usuario " +
                "ORDER BY s.fecha_inicio DESC, s.id_servicio DESC";
        DatabaseUtils.llenarTablaDesdeConsulta(tablaServicios, query);
    }

    private void buscarServicios() {
        String busqueda = campoBusqueda.getText().trim();
        String estadoFiltro = comboFiltroEstado.getSelectedItem().toString();

        StringBuilder query = new StringBuilder(
                "SELECT s.id_servicio, " +
                        "CONCAT(v.marca, ' ', v.modelo, ' - ', v.placas) as vehiculo, " +
                        "c.nombre as cliente, " +
                        "s.descripcion_servicio, " +
                        "DATE_FORMAT(s.fecha_inicio, '%Y-%m-%d') as fecha_inicio, " +
                        "DATE_FORMAT(s.fecha_fin, '%Y-%m-%d') as fecha_fin, " +
                        "s.estado, " +
                        "s.costo_total, " +
                        "u.nombre_completo as mecanico " +
                        "FROM servicios s " +
                        "INNER JOIN vehiculos v ON s.id_vehiculo = v.id_vehiculo " +
                        "INNER JOIN clientes c ON v.id_cliente = c.id_cliente " +
                        "INNER JOIN usuarios u ON s.id_mecanico = u.id_usuario " +
                        "WHERE 1=1");

        java.util.List<Object> parametros = new java.util.ArrayList<>();

        if (!busqueda.isEmpty()) {
            query.append(
                    " AND (v.placas LIKE ? OR c.nombre LIKE ? OR s.descripcion_servicio LIKE ? OR u.nombre_completo LIKE ?)");
            String parametroBusqueda = "%" + busqueda + "%";
            parametros.add(parametroBusqueda);
            parametros.add(parametroBusqueda);
            parametros.add(parametroBusqueda);
            parametros.add(parametroBusqueda);
        }

        if (!estadoFiltro.equals("Todos")) {
            query.append(" AND s.estado = ?");
            parametros.add(estadoFiltro);
        }

        query.append(" ORDER BY s.fecha_inicio DESC, s.id_servicio DESC");

        DatabaseUtils.llenarTablaDesdeConsulta(tablaServicios, query.toString(), parametros.toArray());
    }

    private void agregarServicio() {
        java.util.List<String> vehiculos = obtenerListaVehiculos();
        java.util.List<String> mecanicos = obtenerListaMecanicos();
        java.util.List<String> categorias = obtenerListaCategoriasServicios();

        if (vehiculos.isEmpty() || mecanicos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay vehículos o mecánicos registrados. Debe agregarlos primero.");
            return;
        }

        JComboBox<String> comboVehiculo = new JComboBox<>(vehiculos.toArray(new String[0]));
        JComboBox<String> comboMecanico = new JComboBox<>(mecanicos.toArray(new String[0]));
        JComboBox<String> comboCategoria = new JComboBox<>(categorias.toArray(new String[0]));
        JTextArea txtDescripcion = new JTextArea(3, 25);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JTextField txtFechaInicio = new JTextField(java.time.LocalDate.now().toString());
        JTextField txtFechaFin = new JTextField();
        JTextField txtCostoManoObra = new JTextField();
        JTextField txtCostoTotal = new JTextField();
        JTextArea txtObservaciones = new JTextArea(2, 25);
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        JComboBox<String> comboEstado = new JComboBox<>(
                new String[] { "Pendiente", "En Proceso", "Completado", "Facturado", "Cancelado" });

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Vehículo:")); panel.add(comboVehiculo);
        panel.add(new JLabel("Mecánico:")); panel.add(comboMecanico);
        panel.add(new JLabel("Categoría:")); panel.add(comboCategoria);
        panel.add(new JLabel("Descripción:")); panel.add(new JScrollPane(txtDescripcion));
        panel.add(new JLabel("Fecha Inicio:")); panel.add(txtFechaInicio);
        panel.add(new JLabel("Fecha Fin:")); panel.add(txtFechaFin);
        panel.add(new JLabel("Costo Mano Obra:")); panel.add(txtCostoManoObra);
        panel.add(new JLabel("Costo Total:")); panel.add(txtCostoTotal);
        panel.add(new JLabel("Estado:")); panel.add(comboEstado);
        panel.add(new JLabel("Observaciones:")); panel.add(new JScrollPane(txtObservaciones));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        int result = JOptionPane.showConfirmDialog(this, scrollPane,
                "Agregar Nuevo Servicio", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            if (txtDescripcion.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "La descripción del servicio es obligatoria.");
                return;
            }

            String vehiculoSeleccionado = (String) comboVehiculo.getSelectedItem();
            int idVehiculo = obtenerIdVehiculo(vehiculoSeleccionado);
            String mecanicoSeleccionado = (String) comboMecanico.getSelectedItem();
            int idMecanico = obtenerIdUsuario(mecanicoSeleccionado);
            String categoriaSeleccionada = (String) comboCategoria.getSelectedItem();
            int idCategoria = obtenerIdCategoriaServicio(categoriaSeleccionada);

            String query = "INSERT INTO servicios (id_vehiculo, id_mecanico, id_categoria, descripcion_servicio, " +
                    "fecha_inicio, fecha_fin, estado, costo_mano_obra, costo_total, observaciones) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            int filasAfectadas = DatabaseUtils.ejecutarUpdate(query,
                    idVehiculo, idMecanico, idCategoria,
                    txtDescripcion.getText().trim(),
                    txtFechaInicio.getText().trim(),
                    txtFechaFin.getText().trim().isEmpty() ? null : txtFechaFin.getText().trim(),
                    comboEstado.getSelectedItem().toString(),
                    txtCostoManoObra.getText().trim().isEmpty() ? null : Double.parseDouble(txtCostoManoObra.getText().trim()),
                    txtCostoTotal.getText().trim().isEmpty() ? null : Double.parseDouble(txtCostoTotal.getText().trim()),
                    txtObservaciones.getText().trim());

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "Servicio agregado correctamente.");
                cargarDatosServicios();
            }
        }
    }

    private void editarServicio() {
        int filaSeleccionada = tablaServicios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un servicio para editar.");
            return;
        }

        int idServicio = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        Servicio servicio = obtenerServicioPorId(idServicio);
        if (servicio == null) {
            JOptionPane.showMessageDialog(this, "Error al cargar los datos del servicio.");
            return;
        }

        java.util.List<String> vehiculos = obtenerListaVehiculos();
        java.util.List<String> mecanicos = obtenerListaMecanicos();
        java.util.List<String> categorias = obtenerListaCategoriasServicios();

        JComboBox<String> comboVehiculo = new JComboBox<>(vehiculos.toArray(new String[0]));
        comboVehiculo.setSelectedItem(servicio.vehiculo);
        JComboBox<String> comboMecanico = new JComboBox<>(mecanicos.toArray(new String[0]));
        comboMecanico.setSelectedItem(servicio.mecanico);
        JComboBox<String> comboCategoria = new JComboBox<>(categorias.toArray(new String[0]));
        comboCategoria.setSelectedItem(servicio.categoria);
        JTextArea txtDescripcion = new JTextArea(servicio.descripcion, 4, 30);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JTextField txtFechaInicio = new JTextField(servicio.fechaInicio);
        JTextField txtFechaFin = new JTextField(servicio.fechaFin != null ? servicio.fechaFin : "");
        JTextField txtCostoManoObra = new JTextField(servicio.costoManoObra != null ? servicio.costoManoObra.toString() : "");
        JTextField txtCostoTotal = new JTextField(servicio.costoTotal != null ? servicio.costoTotal.toString() : "");
        JTextArea txtObservaciones = new JTextArea(servicio.observaciones != null ? servicio.observaciones : "", 3, 30);
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        JComboBox<String> comboEstado = new JComboBox<>(new String[] { "Pendiente", "En Proceso", "Completado", "Facturado", "Cancelado" });
        comboEstado.setSelectedItem(servicio.estado);

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Vehículo:")); panel.add(comboVehiculo);
        panel.add(new JLabel("Mecánico:")); panel.add(comboMecanico);
        panel.add(new JLabel("Categoría:")); panel.add(comboCategoria);
        panel.add(new JLabel("Descripción:")); panel.add(new JScrollPane(txtDescripcion));
        panel.add(new JLabel("Fecha Inicio (YYYY-MM-DD):")); panel.add(txtFechaInicio);
        panel.add(new JLabel("Fecha Fin (YYYY-MM-DD):")); panel.add(txtFechaFin);
        panel.add(new JLabel("Costo Mano de Obra:")); panel.add(txtCostoManoObra);
        panel.add(new JLabel("Costo Total:")); panel.add(txtCostoTotal);
        panel.add(new JLabel("Estado:")); panel.add(comboEstado);
        panel.add(new JLabel("Observaciones:")); panel.add(new JScrollPane(txtObservaciones));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        int result = JOptionPane.showConfirmDialog(this, scrollPane, "Editar Servicio", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String vehiculoSeleccionado = (String) comboVehiculo.getSelectedItem();
            int idVehiculo = obtenerIdVehiculo(vehiculoSeleccionado);
            String mecanicoSeleccionado = (String) comboMecanico.getSelectedItem();
            int idMecanico = obtenerIdUsuario(mecanicoSeleccionado);
            String categoriaSeleccionada = (String) comboCategoria.getSelectedItem();
            int idCategoria = obtenerIdCategoriaServicio(categoriaSeleccionada);

            String query = "UPDATE servicios SET id_vehiculo = ?, id_mecanico = ?, id_categoria = ?, " +
                    "descripcion_servicio = ?, fecha_inicio = ?, fecha_fin = ?, estado = ?, " +
                    "costo_mano_obra = ?, costo_total = ?, observaciones = ? WHERE id_servicio = ?";

            int filasAfectadas = DatabaseUtils.ejecutarUpdate(query,
                    idVehiculo, idMecanico, idCategoria,
                    txtDescripcion.getText().trim(),
                    txtFechaInicio.getText().trim(),
                    txtFechaFin.getText().trim().isEmpty() ? null : txtFechaFin.getText().trim(),
                    comboEstado.getSelectedItem().toString(),
                    txtCostoManoObra.getText().trim().isEmpty() ? null : Double.parseDouble(txtCostoManoObra.getText().trim()),
                    txtCostoTotal.getText().trim().isEmpty() ? null : Double.parseDouble(txtCostoTotal.getText().trim()),
                    txtObservaciones.getText().trim(),
                    idServicio);

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "Servicio actualizado correctamente.");
                cargarDatosServicios();
            }
        }
    }

    private void eliminarServicio() {
        int filaSeleccionada = tablaServicios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un servicio para eliminar.");
            return;
        }

        int idServicio = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        String descripcionServicio = (String) modeloTabla.getValueAt(filaSeleccionada, 3);

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el servicio: " + descripcionServicio + "?\n\n" +
                        "NOTA: También se eliminarán los detalles de servicio asociados.",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            String query = "DELETE FROM servicios WHERE id_servicio = ?";
            int resultado = DatabaseUtils.ejecutarUpdate(query, idServicio);
            if (resultado > 0) {
                JOptionPane.showMessageDialog(this, "Servicio eliminado correctamente.");
                cargarDatosServicios();
            }
        }
    }

    private void verDetallesServicio() {
        int filaSeleccionada = tablaServicios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un servicio para ver los detalles.");
            return;
        }

        int idServicio = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        Servicio servicio = obtenerServicioPorId(idServicio);

        if (servicio != null) {
            String detalles = String.format(
                    "ID Servicio: %d\nVehículo: %s\nCliente: %s\nMecánico: %s\nCategoría: %s\nDescripción: %s\n" +
                    "Fecha Inicio: %s\nFecha Fin: %s\nEstado: %s\nCosto Mano de Obra: $%.2f\nCosto Total: $%.2f\nObservaciones: %s",
                    servicio.idServicio, servicio.vehiculo, servicio.cliente, servicio.mecanico,
                    servicio.categoria, servicio.descripcion, servicio.fechaInicio,
                    servicio.fechaFin != null ? servicio.fechaFin : "No completado",
                    servicio.estado, servicio.costoManoObra != null ? servicio.costoManoObra : 0.0,
                    servicio.costoTotal != null ? servicio.costoTotal : 0.0,
                    servicio.observaciones != null ? servicio.observaciones : "Ninguna");

            JOptionPane.showMessageDialog(this, detalles, "Detalles del Servicio", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void cambiarEstadoServicio() {
        int filaSeleccionada = tablaServicios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un servicio para cambiar su estado.");
            return;
        }

        int idServicio = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        String estadoActual = (String) modeloTabla.getValueAt(filaSeleccionada, 6);
        String descripcionServicio = (String) modeloTabla.getValueAt(filaSeleccionada, 3);

        JComboBox<String> comboNuevoEstado = new JComboBox<>(
                new String[] { "Pendiente", "En Proceso", "Completado", "Facturado", "Cancelado" });
        comboNuevoEstado.setSelectedItem(estadoActual);

        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.add(new JLabel("Servicio: " + descripcionServicio));
        panel.add(new JLabel("Estado actual: " + estadoActual));
        panel.add(new JLabel("Nuevo estado:"));
        panel.add(comboNuevoEstado);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Cambiar Estado del Servicio", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String nuevoEstado = (String) comboNuevoEstado.getSelectedItem();
            String query = "UPDATE servicios SET estado = ? WHERE id_servicio = ?";

            int filasAfectadas = DatabaseUtils.ejecutarUpdate(query, nuevoEstado, idServicio);
            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "Estado del servicio actualizado correctamente.");
                cargarDatosServicios();
            }
        }
    }

    // ==================== GENERAR FACTURA (MÉTODO MODIFICADO) ====================
    private void generarFactura() {
        int filaSeleccionada = tablaServicios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un servicio para generar factura.");
            return;
        }

        // Obtener ID y Estado
        int idServicio = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        String estadoActual = (String) modeloTabla.getValueAt(filaSeleccionada, 6);

        // Validación: Solo permitir facturar servicios completados
        if (!"Completado".equals(estadoActual)) {
            JOptionPane.showMessageDialog(this,
                    "Solo se pueden generar facturas para servicios completados.\n" +
                            "El estado actual del servicio es: " + estadoActual,
                    "Estado Incorrecto", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirmación para generar PDF
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Desea generar un reporte PDF preliminar de este servicio?",
                "Generar Reporte PDF", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Recoger datos de la tabla para el reporte
            String vehiculo = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
            String cliente = (String) modeloTabla.getValueAt(filaSeleccionada, 2);
            String descripcion = (String) modeloTabla.getValueAt(filaSeleccionada, 3);
            
            // Manejo seguro del costo total (puede ser Double o String según la carga)
            Double total = 0.0;
            Object totalObj = modeloTabla.getValueAt(filaSeleccionada, 7);
            if (totalObj instanceof Double) {
                total = (Double) totalObj;
            } else if (totalObj instanceof Number) {
                total = ((Number) totalObj).doubleValue();
            } else if (totalObj instanceof String) {
                try {
                    total = Double.parseDouble((String) totalObj);
                } catch (NumberFormatException e) {
                    total = 0.0;
                }
            }

            // Cálculos aproximados para el reporte
            double subtotal = total / 1.16;
            double iva = total - subtotal;

            // Llamada a la utilidad de PDF
            ReportePDFUtils.generarFacturaPDF(
                    "SERV-" + idServicio, // ID Temporal para el reporte
                    cliente,
                    vehiculo,
                    java.time.LocalDate.now().toString(),
                    subtotal,
                    iva,
                    total,
                    descripcion
            );
        }
    }

    // ==================== MÉTODOS DE BASE DE DATOS AUXILIARES ====================
    
    private java.util.List<String> obtenerListaVehiculos() {
        java.util.List<String> vehiculos = new java.util.ArrayList<>();
        String query = "SELECT CONCAT(marca, ' ', modelo, ' - ', placas) as vehiculo FROM vehiculos ORDER BY marca, modelo";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) vehiculos.add(rs.getString("vehiculo"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehiculos;
    }

    private java.util.List<String> obtenerListaMecanicos() {
        java.util.List<String> mecanicos = new java.util.ArrayList<>();
        String query = "SELECT nombre_completo FROM usuarios WHERE rol IN ('Técnico', 'Supervisor') AND estado = 'Activo' ORDER BY nombre_completo";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) mecanicos.add(rs.getString("nombre_completo"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mecanicos;
    }

    private java.util.List<String> obtenerListaCategoriasServicios() {
        java.util.List<String> categorias = new java.util.ArrayList<>();
        String query = "SELECT nombre FROM categorias_servicios WHERE estado = 'Activa' ORDER BY nombre";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) categorias.add(rs.getString("nombre"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categorias;
    }

    private int obtenerIdVehiculo(String vehiculoInfo) {
        String placas = vehiculoInfo.substring(vehiculoInfo.lastIndexOf("-") + 1).trim();
        String query = "SELECT id_vehiculo FROM vehiculos WHERE placas = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, placas);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id_vehiculo");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private int obtenerIdUsuario(String nombreCompleto) {
        String query = "SELECT id_usuario FROM usuarios WHERE nombre_completo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nombreCompleto);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id_usuario");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private int obtenerIdCategoriaServicio(String nombreCategoria) {
        String query = "SELECT id_categoria FROM categorias_servicios WHERE nombre = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nombreCategoria);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id_categoria");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private Servicio obtenerServicioPorId(int idServicio) {
        String query = "SELECT s.*, CONCAT(v.marca, ' ', v.modelo, ' - ', v.placas) as vehiculo, c.nombre as cliente, u.nombre_completo as mecanico, cs.nombre as categoria " +
                "FROM servicios s " +
                "INNER JOIN vehiculos v ON s.id_vehiculo = v.id_vehiculo " +
                "INNER JOIN clientes c ON v.id_cliente = c.id_cliente " +
                "INNER JOIN usuarios u ON s.id_mecanico = u.id_usuario " +
                "LEFT JOIN categorias_servicios cs ON s.id_categoria = cs.id_categoria " +
                "WHERE s.id_servicio = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idServicio);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Servicio s = new Servicio();
                s.idServicio = rs.getInt("id_servicio");
                s.vehiculo = rs.getString("vehiculo");
                s.cliente = rs.getString("cliente");
                s.mecanico = rs.getString("mecanico");
                s.categoria = rs.getString("categoria");
                s.descripcion = rs.getString("descripcion_servicio");
                s.fechaInicio = rs.getString("fecha_inicio");
                s.fechaFin = rs.getString("fecha_fin");
                s.estado = rs.getString("estado");
                s.costoManoObra = rs.getObject("costo_mano_obra") != null ? rs.getDouble("costo_mano_obra") : null;
                s.costoTotal = rs.getObject("costo_total") != null ? rs.getDouble("costo_total") : null;
                s.observaciones = rs.getString("observaciones");
                return s;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class Servicio {
        int idServicio;
        String vehiculo, cliente, mecanico, categoria, descripcion, fechaInicio, fechaFin, estado, observaciones;
        Double costoManoObra, costoTotal;
    }
}