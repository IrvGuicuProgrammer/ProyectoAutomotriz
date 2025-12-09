import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import org.mindrot.jbcrypt.BCrypt;

public class GestionUsuariosPanel extends JPanel {
    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;
    private JTextField campoBusqueda;

    public GestionUsuariosPanel() {
        inicializarComponentes();
        cargarDatosUsuarios();
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

        JLabel titulo = new JLabel("GESTIÓN DE USUARIOS");
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

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.setBackground(new Color(245, 247, 250));

        campoBusqueda = new JTextField(20);
        campoBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campoBusqueda.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(8, 12, 8, 12)));

        JButton botonBuscar = crearBotonEstilizado("Buscar", new Color(30, 60, 114));
        JButton botonLimpiar = crearBotonEstilizado("Limpiar", new Color(100, 100, 100));

        panelBusqueda.add(new JLabel("Buscar usuario:"));
        panelBusqueda.add(campoBusqueda);
        panelBusqueda.add(botonBuscar);
        panelBusqueda.add(botonLimpiar);

        panel.add(panelBusqueda, BorderLayout.CENTER);

        // Acciones de los botones
        botonBuscar.addActionListener(e -> buscarUsuarios());
        botonLimpiar.addActionListener(e -> {
            campoBusqueda.setText("");
            cargarDatosUsuarios();
        });

        return panel;
    }

    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 10, 10, 10)));

        String[] columnas = { "ID", "Usuario", "Nombre Completo", "Rol", "Email", "Estado", "Fecha Creación" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaUsuarios = new JTable(modeloTabla);
        tablaUsuarios.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaUsuarios.setRowHeight(30);
        tablaUsuarios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaUsuarios.getTableHeader().setBackground(new Color(30, 60, 114));
        tablaUsuarios.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton botonAgregar = crearBotonEstilizado("Agregar Usuario", new Color(40, 167, 69));
        JButton botonEditar = crearBotonEstilizado("Editar Usuario", new Color(255, 193, 7));
        JButton botonEliminar = crearBotonEstilizado("Eliminar Usuario", new Color(220, 53, 69));
        JButton botonResetPass = crearBotonEstilizado("Reset Contraseña", new Color(108, 117, 125));

        panel.add(botonAgregar);
        panel.add(botonEditar);
        panel.add(botonEliminar);
        panel.add(botonResetPass);

        // Acciones de los botones
        botonAgregar.addActionListener(e -> agregarUsuario());
        botonEditar.addActionListener(e -> editarUsuario());
        botonEliminar.addActionListener(e -> eliminarUsuario());
        botonResetPass.addActionListener(e -> resetContrasena());

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

    private void cargarDatosUsuarios() {
        String query = "SELECT id_usuario, usuario, nombre_completo, rol, email, estado, " +
                "DATE_FORMAT(fecha_creacion, '%Y-%m-%d') as fecha_creacion " +
                "FROM usuarios ORDER BY id_usuario";
        DatabaseUtils.llenarTablaDesdeConsulta(tablaUsuarios, query);
    }

    private void buscarUsuarios() {
        String busqueda = campoBusqueda.getText().trim();
        if (busqueda.isEmpty()) {
            cargarDatosUsuarios();
            return;
        }

        String query = "SELECT id_usuario, usuario, nombre_completo, rol, email, estado, " +
                "DATE_FORMAT(fecha_creacion, '%Y-%m-%d') as fecha_creacion " +
                "FROM usuarios WHERE usuario LIKE ? OR nombre_completo LIKE ? OR email LIKE ? " +
                "ORDER BY id_usuario";

        String parametroBusqueda = "%" + busqueda + "%";
        DatabaseUtils.llenarTablaDesdeConsulta(tablaUsuarios, query,
                parametroBusqueda, parametroBusqueda, parametroBusqueda);
    }

    private void agregarUsuario() {
        JTextField txtUsuario = new JTextField();
        JTextField txtNombre = new JTextField();
        JPasswordField txtPassword = new JPasswordField();
        
        // --- SOLO 2 ROLES PERMITIDOS ---
        JComboBox<String> comboRol = new JComboBox<>(new String[] { "Administrador", "Empleado" });
        
        JTextField txtEmail = new JTextField();
        JComboBox<String> comboEstado = new JComboBox<>(new String[] { "Activo", "Inactivo" });

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Usuario:"));
        panel.add(txtUsuario);
        panel.add(new JLabel("Nombre Completo:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Contraseña:"));
        panel.add(txtPassword);
        panel.add(new JLabel("Rol:"));
        panel.add(comboRol);
        panel.add(new JLabel("Email:"));
        panel.add(txtEmail);
        panel.add(new JLabel("Estado:"));
        panel.add(comboEstado);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Agregar Nuevo Usuario", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            if (txtUsuario.getText().trim().isEmpty() || txtNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Usuario y Nombre son campos obligatorios.");
                return;
            }

            String plainPassword = new String(txtPassword.getPassword());
            if (plainPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "La contraseña no puede estar vacía.");
                return;
            }

            String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

            String query = "INSERT INTO usuarios (usuario, nombre_completo, contrasena, rol, email, estado) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            int filasAfectadas = DatabaseUtils.ejecutarUpdate(query,
                    txtUsuario.getText().trim(),
                    txtNombre.getText().trim(),
                    hashedPassword,
                    comboRol.getSelectedItem().toString(),
                    txtEmail.getText().trim(),
                    comboEstado.getSelectedItem().toString());

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "Usuario agregado correctamente.");
                cargarDatosUsuarios();
            }
        }
    }

    private void editarUsuario() {
        int filaSeleccionada = tablaUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para editar.");
            return;
        }

        int idUsuario = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        String usuarioActual = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
        String nombreActual = (String) modeloTabla.getValueAt(filaSeleccionada, 2);
        String rolActual = (String) modeloTabla.getValueAt(filaSeleccionada, 3);
        String emailActual = (String) modeloTabla.getValueAt(filaSeleccionada, 4);
        String estadoActual = (String) modeloTabla.getValueAt(filaSeleccionada, 5);

        JTextField txtUsuario = new JTextField(usuarioActual);
        JTextField txtNombre = new JTextField(nombreActual);
        
        // --- SOLO 2 ROLES PERMITIDOS ---
        JComboBox<String> comboRol = new JComboBox<>(new String[] { "Administrador", "Empleado" });
        comboRol.setSelectedItem(rolActual);
        
        JTextField txtEmail = new JTextField(emailActual);
        JComboBox<String> comboEstado = new JComboBox<>(new String[] { "Activo", "Inactivo" });
        comboEstado.setSelectedItem(estadoActual);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Usuario:"));
        panel.add(txtUsuario);
        panel.add(new JLabel("Nombre Completo:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Rol:"));
        panel.add(comboRol);
        panel.add(new JLabel("Email:"));
        panel.add(txtEmail);
        panel.add(new JLabel("Estado:"));
        panel.add(comboEstado);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Editar Usuario", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String query = "UPDATE usuarios SET usuario = ?, nombre_completo = ?, rol = ?, email = ?, estado = ? " +
                    "WHERE id_usuario = ?";

            int filasAfectadas = DatabaseUtils.ejecutarUpdate(query,
                    txtUsuario.getText().trim(),
                    txtNombre.getText().trim(),
                    comboRol.getSelectedItem().toString(),
                    txtEmail.getText().trim(),
                    comboEstado.getSelectedItem().toString(),
                    idUsuario);

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "Usuario actualizado correctamente.");
                cargarDatosUsuarios();
            }
        }
    }

    private void eliminarUsuario() {
        int filaSeleccionada = tablaUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para eliminar.");
            return;
        }

        int idUsuario = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        String nombreUsuario = (String) modeloTabla.getValueAt(filaSeleccionada, 2);

        // Validación para no eliminar al Admin principal
        if (idUsuario == 1) {
            JOptionPane.showMessageDialog(this,
                    "No se puede eliminar al usuario 'admin' (ID 1).",
                    "Acción No Permitida",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de ELIMINAR PERMANENTEMENTE al usuario: " + nombreUsuario + "?\n\n" +
                        "ADVERTENCIA: Esta acción no se puede deshacer.\n",
                "Confirmar Eliminación Permanente", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            String query = "DELETE FROM usuarios WHERE id_usuario = ?";
            int resultado = DatabaseUtils.ejecutarUpdate(query, idUsuario);

            if (resultado > 0) {
                JOptionPane.showMessageDialog(this, "Usuario eliminado permanentemente.");
                cargarDatosUsuarios();
            }
        }
    }

    private void resetContrasena() {
        int filaSeleccionada = tablaUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para resetear contraseña.");
            return;
        }

        int idUsuario = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        String nombreUsuario = (String) modeloTabla.getValueAt(filaSeleccionada, 2);

        JPasswordField txtNuevaPassword = new JPasswordField();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Nueva contraseña para " + nombreUsuario + ":"), BorderLayout.NORTH);
        panel.add(txtNuevaPassword, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Resetear Contraseña", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {

            String nuevaPassword = new String(txtNuevaPassword.getPassword());
            if (nuevaPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "La contraseña no puede estar vacía.");
                return;
            }

            String hashedPassword = BCrypt.hashpw(nuevaPassword, BCrypt.gensalt());

            String query = "UPDATE usuarios SET contrasena = ? WHERE id_usuario = ?";

            int filasAfectadas = DatabaseUtils.ejecutarUpdate(query, hashedPassword, idUsuario);

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "Contraseña actualizada correctamente.");
            }
        }
    }
}