import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class ControlInventarioPanel extends JPanel {
    private JTable tablaInventario;
    private DefaultTableModel modeloTabla;
    private JTextField campoBusqueda;
    private JComboBox<String> comboFiltroCategoria;

    public ControlInventarioPanel() {
        inicializarComponentes();
        cargarDatosInventario();
        cargarCategorias();
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

        JLabel titulo = new JLabel("CONTROL DE INVENTARIO");
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

        comboFiltroCategoria = new JComboBox<>();
        comboFiltroCategoria.addItem("Todas");
        comboFiltroCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton botonBuscar = crearBotonEstilizado("Buscar", new Color(30, 60, 114));
        JButton botonLimpiar = crearBotonEstilizado("Limpiar", new Color(100, 100, 100));
        JButton botonAgregar = crearBotonEstilizado("Agregar Producto", new Color(40, 167, 69));

        panelBusqueda.add(new JLabel("Buscar producto:"));
        panelBusqueda.add(campoBusqueda);
        panelBusqueda.add(new JLabel("Categoría:"));
        panelBusqueda.add(comboFiltroCategoria);
        panelBusqueda.add(botonBuscar);
        panelBusqueda.add(botonLimpiar);
        panelBusqueda.add(botonAgregar);

        panel.add(panelBusqueda, BorderLayout.CENTER);

        // Acciones de los botones
        botonBuscar.addActionListener(e -> buscarProductos());
        botonLimpiar.addActionListener(e -> {
            campoBusqueda.setText("");
            comboFiltroCategoria.setSelectedIndex(0);
            cargarDatosInventario();
        });
        botonAgregar.addActionListener(e -> agregarProducto());

        return panel;
    }

    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 10, 10, 10)));

        // Modelo de tabla
        String[] columnas = { "ID", "Código", "Producto", "Categoría", "Stock Actual", "Stock Mínimo",
                "Precio Compra", "Precio Venta", "Proveedor", "Ubicación", "Estado" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4 || columnIndex == 5) {
                    return Integer.class;
                }
                if (columnIndex == 6 || columnIndex == 7) {
                    return Double.class;
                }
                return String.class;
            }
        };

        tablaInventario = new JTable(modeloTabla);
        tablaInventario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaInventario.setRowHeight(30);
        tablaInventario.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaInventario.getTableHeader().setBackground(new Color(30, 60, 114));
        tablaInventario.getTableHeader().setForeground(Color.WHITE);

        // Renderer para resaltar stock bajo
        tablaInventario.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (column == 4) { // Columna Stock Actual
                    try {
                        int stockActual = (Integer) table.getValueAt(row, 4);
                        int stockMinimo = (Integer) table.getValueAt(row, 5);

                        if (stockActual <= stockMinimo) {
                            c.setBackground(new Color(255, 200, 200)); // Rojo claro para stock bajo
                        } else if (stockActual <= stockMinimo + 5) {
                            c.setBackground(new Color(255, 255, 200)); // Amarillo para stock crítico
                        } else {
                            c.setBackground(Color.WHITE);
                        }
                    } catch (Exception e) {
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

        JScrollPane scrollPane = new JScrollPane(tablaInventario);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton botonAgregarStock = crearBotonEstilizado("Entrada Stock", new Color(40, 167, 69));
        JButton botonRetirarStock = crearBotonEstilizado("Salida Stock", new Color(255, 193, 7));
        JButton botonEditar = crearBotonEstilizado("Editar Producto", new Color(30, 60, 114));
        JButton botonEliminar = crearBotonEstilizado("Eliminar", new Color(220, 53, 69));
        
        // Botón modificado para usar el PDF
        JButton botonReporte = crearBotonEstilizado("Reporte Stock (PDF)", new Color(108, 117, 125));

        panel.add(botonAgregarStock);
        panel.add(botonRetirarStock);
        panel.add(botonEditar);
        panel.add(botonEliminar);
        panel.add(botonReporte);

        // Acciones de los botones
        botonAgregarStock.addActionListener(e -> entradaStock());
        botonRetirarStock.addActionListener(e -> salidaStock());
        botonEditar.addActionListener(e -> editarProducto());
        botonEliminar.addActionListener(e -> eliminarProducto());
        botonReporte.addActionListener(e -> generarReporteStock());

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

    // MÉTODOS PARA INVENTARIO
    private void cargarDatosInventario() {
        String query = "SELECT p.id_producto, p.codigo_producto, p.nombre, " +
                "cp.nombre as categoria, p.stock_actual, p.stock_minimo, " +
                "p.precio_compra, p.precio_venta, " +
                "pr.nombre as proveedor, p.ubicacion, p.estado " +
                "FROM productos p " +
                "LEFT JOIN categorias_productos cp ON p.id_categoria = cp.id_categoria " +
                "LEFT JOIN proveedores pr ON p.id_proveedor = pr.id_proveedor " +
                "ORDER BY p.nombre";
        DatabaseUtils.llenarTablaDesdeConsulta(tablaInventario, query);
    }

    private void cargarCategorias() {
        String query = "SELECT nombre FROM categorias_productos WHERE estado = 'Activa' ORDER BY nombre";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                comboFiltroCategoria.addItem(rs.getString("nombre"));
            }

            DatabaseUtils.cerrarRecursos(conn, stmt, rs);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar categorías: " + e.getMessage());
        }
    }

    private void buscarProductos() {
        String busqueda = campoBusqueda.getText().trim();
        String categoriaFiltro = comboFiltroCategoria.getSelectedItem().toString();

        StringBuilder query = new StringBuilder(
                "SELECT p.id_producto, p.codigo_producto, p.nombre, " +
                        "cp.nombre as categoria, p.stock_actual, p.stock_minimo, " +
                        "p.precio_compra, p.precio_venta, " +
                        "pr.nombre as proveedor, p.ubicacion, p.estado " +
                        "FROM productos p " +
                        "LEFT JOIN categorias_productos cp ON p.id_categoria = cp.id_categoria " +
                        "LEFT JOIN proveedores pr ON p.id_proveedor = pr.id_proveedor " +
                        "WHERE 1=1");

        java.util.List<Object> parametros = new java.util.ArrayList<>();

        if (!busqueda.isEmpty()) {
            query.append(" AND (p.codigo_producto LIKE ? OR p.nombre LIKE ? OR p.ubicacion LIKE ?)");
            String parametroBusqueda = "%" + busqueda + "%";
            parametros.add(parametroBusqueda);
            parametros.add(parametroBusqueda);
            parametros.add(parametroBusqueda);
        }

        if (!categoriaFiltro.equals("Todas")) {
            query.append(" AND cp.nombre = ?");
            parametros.add(categoriaFiltro);
        }

        query.append(" ORDER BY p.nombre");

        DatabaseUtils.llenarTablaDesdeConsulta(tablaInventario, query.toString(), parametros.toArray());
    }

    private void agregarProducto() {
        java.util.List<String> categorias = obtenerListaCategorias();
        java.util.List<String> proveedores = obtenerListaProveedores();

        if (categorias.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay categorías registradas. Debe agregar categorías primero.");
            return;
        }

        JTextField txtCodigo = new JTextField();
        JTextField txtNombre = new JTextField();
        JComboBox<String> comboCategoria = new JComboBox<>(categorias.toArray(new String[0]));
        JTextField txtStockActual = new JTextField("0");
        JTextField txtStockMinimo = new JTextField("5");
        JTextField txtPrecioCompra = new JTextField();
        JTextField txtPrecioVenta = new JTextField();
        JComboBox<String> comboProveedor = new JComboBox<>(proveedores.toArray(new String[0]));
        comboProveedor.insertItemAt("Ninguno", 0);
        comboProveedor.setSelectedIndex(0);
        JTextField txtUbicacion = new JTextField();
        JComboBox<String> comboEstado = new JComboBox<>(new String[] { "Activo", "Inactivo" });
        JTextArea txtDescripcion = new JTextArea(2, 25);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Código Producto:"));
        panel.add(txtCodigo);
        panel.add(new JLabel("Nombre:*"));
        panel.add(txtNombre);
        panel.add(new JLabel("Categoría:*"));
        panel.add(comboCategoria);
        panel.add(new JLabel("Stock Actual:"));
        panel.add(txtStockActual);
        panel.add(new JLabel("Stock Mínimo:"));
        panel.add(txtStockMinimo);
        panel.add(new JLabel("Precio Compra:"));
        panel.add(txtPrecioCompra);
        panel.add(new JLabel("Precio Venta:"));
        panel.add(txtPrecioVenta);
        panel.add(new JLabel("Proveedor:"));
        panel.add(comboProveedor);
        panel.add(new JLabel("Ubicación:"));
        panel.add(txtUbicacion);
        panel.add(new JLabel("Estado:"));
        panel.add(comboEstado);
        panel.add(new JLabel("Descripción:"));
        panel.add(new JScrollPane(txtDescripcion));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        int result = JOptionPane.showConfirmDialog(this, scrollPane,
                "Agregar Nuevo Producto", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            if (txtNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre del producto es obligatorio.");
                return;
            }

            String categoriaSeleccionada = (String) comboCategoria.getSelectedItem();
            int idCategoria = obtenerIdCategoriaProducto(categoriaSeleccionada);
            String proveedorSeleccionado = (String) comboProveedor.getSelectedItem();
            int idProveedor = proveedorSeleccionado.equals("Ninguno") ? -1 : obtenerIdProveedor(proveedorSeleccionado);

            String query = "INSERT INTO productos (codigo_producto, nombre, id_categoria, stock_actual, stock_minimo, "
                    +
                    "precio_compra, precio_venta, id_proveedor, ubicacion, estado, descripcion) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            int filasAfectadas = DatabaseUtils.ejecutarUpdate(query,
                    txtCodigo.getText().trim().isEmpty() ? null : txtCodigo.getText().trim(),
                    txtNombre.getText().trim(),
                    idCategoria,
                    Integer.parseInt(txtStockActual.getText().trim()),
                    Integer.parseInt(txtStockMinimo.getText().trim()),
                    txtPrecioCompra.getText().trim().isEmpty() ? null
                            : Double.parseDouble(txtPrecioCompra.getText().trim()),
                    txtPrecioVenta.getText().trim().isEmpty() ? null
                            : Double.parseDouble(txtPrecioVenta.getText().trim()),
                    idProveedor == -1 ? null : idProveedor,
                    txtUbicacion.getText().trim(),
                    comboEstado.getSelectedItem().toString(),
                    txtDescripcion.getText().trim());

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "Producto agregado correctamente.");
                cargarDatosInventario();
            }
        }
    }

    private void editarProducto() {
        int filaSeleccionada = tablaInventario.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para editar.");
            return;
        }

        int idProducto = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        Producto producto = obtenerProductoPorId(idProducto);
        if (producto == null) {
            JOptionPane.showMessageDialog(this, "Error al cargar los datos del producto.");
            return;
        }

        java.util.List<String> categorias = obtenerListaCategorias();
        java.util.List<String> proveedores = obtenerListaProveedores();

        JTextField txtCodigo = new JTextField(producto.codigo != null ? producto.codigo : "");
        JTextField txtNombre = new JTextField(producto.nombre);
        JComboBox<String> comboCategoria = new JComboBox<>(categorias.toArray(new String[0]));
        comboCategoria.setSelectedItem(producto.categoria);
        JTextField txtStockActual = new JTextField(String.valueOf(producto.stockActual));
        JTextField txtStockMinimo = new JTextField(String.valueOf(producto.stockMinimo));
        JTextField txtPrecioCompra = new JTextField(
                producto.precioCompra != null ? String.valueOf(producto.precioCompra) : "");
        JTextField txtPrecioVenta = new JTextField(
                producto.precioVenta != null ? String.valueOf(producto.precioVenta) : "");
        JComboBox<String> comboProveedor = new JComboBox<>(proveedores.toArray(new String[0]));
        comboProveedor.insertItemAt("Ninguno", 0);
        comboProveedor.setSelectedItem(producto.proveedor != null ? producto.proveedor : "Ninguno");
        JTextField txtUbicacion = new JTextField(producto.ubicacion != null ? producto.ubicacion : "");
        JComboBox<String> comboEstado = new JComboBox<>(new String[] { "Activo", "Inactivo" });
        comboEstado.setSelectedItem(producto.estado);
        JTextArea txtDescripcion = new JTextArea(producto.descripcion != null ? producto.descripcion : "", 3, 30);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Código Producto:"));
        panel.add(txtCodigo);
        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Categoría:"));
        panel.add(comboCategoria);
        panel.add(new JLabel("Stock Actual:"));
        panel.add(txtStockActual);
        panel.add(new JLabel("Stock Mínimo:"));
        panel.add(txtStockMinimo);
        panel.add(new JLabel("Precio Compra:"));
        panel.add(txtPrecioCompra);
        panel.add(new JLabel("Precio Venta:"));
        panel.add(txtPrecioVenta);
        panel.add(new JLabel("Proveedor:"));
        panel.add(comboProveedor);
        panel.add(new JLabel("Ubicación:"));
        panel.add(txtUbicacion);
        panel.add(new JLabel("Estado:"));
        panel.add(comboEstado);
        panel.add(new JLabel("Descripción:"));
        panel.add(new JScrollPane(txtDescripcion));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        int result = JOptionPane.showConfirmDialog(this, scrollPane,
                "Editar Producto", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String categoriaSeleccionada = (String) comboCategoria.getSelectedItem();
            int idCategoria = obtenerIdCategoriaProducto(categoriaSeleccionada);
            String proveedorSeleccionado = (String) comboProveedor.getSelectedItem();
            int idProveedor = proveedorSeleccionado.equals("Ninguno") ? -1 : obtenerIdProveedor(proveedorSeleccionado);

            String query = "UPDATE productos SET codigo_producto = ?, nombre = ?, id_categoria = ?, " +
                    "stock_actual = ?, stock_minimo = ?, precio_compra = ?, precio_venta = ?, " +
                    "id_proveedor = ?, ubicacion = ?, estado = ?, descripcion = ? " +
                    "WHERE id_producto = ?";

            int filasAfectadas = DatabaseUtils.ejecutarUpdate(query,
                    txtCodigo.getText().trim().isEmpty() ? null : txtCodigo.getText().trim(),
                    txtNombre.getText().trim(),
                    idCategoria,
                    Integer.parseInt(txtStockActual.getText().trim()),
                    Integer.parseInt(txtStockMinimo.getText().trim()),
                    txtPrecioCompra.getText().trim().isEmpty() ? null
                            : Double.parseDouble(txtPrecioCompra.getText().trim()),
                    txtPrecioVenta.getText().trim().isEmpty() ? null
                            : Double.parseDouble(txtPrecioVenta.getText().trim()),
                    idProveedor == -1 ? null : idProveedor,
                    txtUbicacion.getText().trim(),
                    comboEstado.getSelectedItem().toString(),
                    txtDescripcion.getText().trim(),
                    idProducto);

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "Producto actualizado correctamente.");
                cargarDatosInventario();
            }
        }
    }

    private void eliminarProducto() {
        int filaSeleccionada = tablaInventario.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar.");
            return;
        }

        int idProducto = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        String nombreProducto = (String) modeloTabla.getValueAt(filaSeleccionada, 2);

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el producto: " + nombreProducto + "?\n\n" +
                        "NOTA: Esta acción no se puede deshacer.",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            String query = "DELETE FROM productos WHERE id_producto = ?";
            int resultado = DatabaseUtils.ejecutarUpdate(query, idProducto);
            if (resultado > 0) {
                JOptionPane.showMessageDialog(this, "Producto eliminado correctamente.");
                cargarDatosInventario();
            }
        }
    }

    private void entradaStock() {
        int filaSeleccionada = tablaInventario.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para registrar entrada de stock.");
            return;
        }

        int idProducto = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        String nombreProducto = (String) modeloTabla.getValueAt(filaSeleccionada, 2);
        int stockActual = (int) modeloTabla.getValueAt(filaSeleccionada, 4);

        JTextField txtCantidad = new JTextField();
        JTextField txtMotivo = new JTextField("Compra/Reabastecimiento");
        JTextField txtReferencia = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Producto:"));
        panel.add(new JLabel(nombreProducto));
        panel.add(new JLabel("Stock Actual:"));
        panel.add(new JLabel(String.valueOf(stockActual)));
        panel.add(new JLabel("Cantidad a agregar:"));
        panel.add(txtCantidad);
        panel.add(new JLabel("Motivo:"));
        panel.add(txtMotivo);
        panel.add(new JLabel("Referencia:"));
        panel.add(txtReferencia);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Entrada de Stock", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int cantidad = Integer.parseInt(txtCantidad.getText().trim());
                if (cantidad <= 0) {
                    JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a cero.");
                    return;
                }

                String updateQuery = "UPDATE productos SET stock_actual = stock_actual + ? WHERE id_producto = ?";
                int filasActualizadas = DatabaseUtils.ejecutarUpdate(updateQuery, cantidad, idProducto);

                if (filasActualizadas > 0) {
                    registrarMovimientoInventario(idProducto, "Entrada", cantidad,
                            txtMotivo.getText().trim(), txtReferencia.getText().trim());

                    JOptionPane.showMessageDialog(this,
                            "Entrada de stock registrada correctamente.\n" +
                                    "Nuevo stock: " + (stockActual + cantidad));
                    cargarDatosInventario();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser un número válido.");
            }
        }
    }

    private void salidaStock() {
        int filaSeleccionada = tablaInventario.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para registrar salida de stock.");
            return;
        }

        int idProducto = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        String nombreProducto = (String) modeloTabla.getValueAt(filaSeleccionada, 2);
        int stockActual = (int) modeloTabla.getValueAt(filaSeleccionada, 4);

        JTextField txtCantidad = new JTextField();
        JTextField txtMotivo = new JTextField("Venta/Uso en servicio");
        JTextField txtReferencia = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Producto:"));
        panel.add(new JLabel(nombreProducto));
        panel.add(new JLabel("Stock Actual:"));
        panel.add(new JLabel(String.valueOf(stockActual)));
        panel.add(new JLabel("Cantidad a retirar:"));
        panel.add(txtCantidad);
        panel.add(new JLabel("Motivo:"));
        panel.add(txtMotivo);
        panel.add(new JLabel("Referencia:"));
        panel.add(txtReferencia);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Salida de Stock", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int cantidad = Integer.parseInt(txtCantidad.getText().trim());
                if (cantidad <= 0) {
                    JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a cero.");
                    return;
                }

                if (cantidad > stockActual) {
                    JOptionPane.showMessageDialog(this,
                            "No hay suficiente stock disponible.\n" +
                                    "Stock actual: " + stockActual + "\n" +
                                    "Cantidad solicitada: " + cantidad);
                    return;
                }

                String updateQuery = "UPDATE productos SET stock_actual = stock_actual - ? WHERE id_producto = ?";
                int filasActualizadas = DatabaseUtils.ejecutarUpdate(updateQuery, cantidad, idProducto);

                if (filasActualizadas > 0) {
                    registrarMovimientoInventario(idProducto, "Salida", cantidad,
                            txtMotivo.getText().trim(), txtReferencia.getText().trim());

                    JOptionPane.showMessageDialog(this,
                            "Salida de stock registrada correctamente.\n" +
                                    "Nuevo stock: " + (stockActual - cantidad));
                    cargarDatosInventario();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser un número válido.");
            }
        }
    }

    // ==================== GENERAR REPORTE DE STOCK (PDF) ====================
    private void generarReporteStock() {
        // Generar PDF usando la utilidad ReportePDFUtils
        ReportePDFUtils.generarReporteTablaPDF(tablaInventario, "REPORTE DE INVENTARIO Y STOCK - TALLER CASA DEL MOTOR", "Reporte_Inventario");
    }

    // ==================== MÉTODOS AUXILIARES DE BASE DE DATOS ====================
    private java.util.List<String> obtenerListaCategorias() {
        java.util.List<String> categorias = new java.util.ArrayList<>();
        String query = "SELECT nombre FROM categorias_productos WHERE estado = 'Activa' ORDER BY nombre";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) categorias.add(rs.getString("nombre"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categorias;
    }

    private java.util.List<String> obtenerListaProveedores() {
        java.util.List<String> proveedores = new java.util.ArrayList<>();
        String query = "SELECT nombre FROM proveedores WHERE estado = 'Activo' ORDER BY nombre";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) proveedores.add(rs.getString("nombre"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return proveedores;
    }

    private int obtenerIdCategoriaProducto(String nombreCategoria) {
        String query = "SELECT id_categoria FROM categorias_productos WHERE nombre = ?";
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

    private int obtenerIdProveedor(String nombreProveedor) {
        String query = "SELECT id_proveedor FROM proveedores WHERE nombre = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nombreProveedor);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id_proveedor");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private Producto obtenerProductoPorId(int idProducto) {
        String query = "SELECT p.*, cp.nombre as categoria_nombre, pr.nombre as proveedor_nombre " +
                "FROM productos p " +
                "LEFT JOIN categorias_productos cp ON p.id_categoria = cp.id_categoria " +
                "LEFT JOIN proveedores pr ON p.id_proveedor = pr.id_proveedor " +
                "WHERE p.id_producto = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idProducto);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Producto producto = new Producto();
                producto.idProducto = rs.getInt("id_producto");
                producto.codigo = rs.getString("codigo_producto");
                producto.nombre = rs.getString("nombre");
                producto.categoria = rs.getString("categoria_nombre");
                producto.stockActual = rs.getInt("stock_actual");
                producto.stockMinimo = rs.getInt("stock_minimo");
                producto.precioCompra = rs.getObject("precio_compra") != null ? rs.getDouble("precio_compra") : null;
                producto.precioVenta = rs.getObject("precio_venta") != null ? rs.getDouble("precio_venta") : null;
                producto.proveedor = rs.getString("proveedor_nombre");
                producto.ubicacion = rs.getString("ubicacion");
                producto.estado = rs.getString("estado");
                producto.descripcion = rs.getString("descripcion");
                return producto;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void registrarMovimientoInventario(int idProducto, String tipo, int cantidad, String motivo, String referencia) {
        int idUsuario = 1; // Por defecto
        String query = "INSERT INTO movimientos_inventario (id_producto, tipo, cantidad, motivo, referencia, id_usuario) VALUES (?, ?, ?, ?, ?, ?)";
        DatabaseUtils.ejecutarUpdate(query, idProducto, tipo, cantidad, motivo, referencia, idUsuario);
    }

    private static class Producto {
        int idProducto;
        String codigo, nombre, categoria, proveedor, ubicacion, estado, descripcion;
        int stockActual, stockMinimo;
        Double precioCompra, precioVenta;
    }
}