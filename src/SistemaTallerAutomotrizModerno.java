import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.awt.image.BufferedImage;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class SistemaTallerAutomotrizModerno {
    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private String usuarioActual;
    private boolean isMaximized = false;
    private Point initialClick;
    private Rectangle originalBounds;

    public static void main(String[] args) {
        // Probar conexión primero
        if (!DatabaseConnection.testConnection()) {
            JOptionPane.showMessageDialog(null,
                    "No se pudo conectar a la base de datos.\nVerifica que MySQL esté ejecutándose.",
                    "Error de Conexión", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(() -> {
            new SistemaTallerAutomotrizModerno().crearYMostrarGUI();
        });
    }

    public void crearYMostrarGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Configuración de la ventana principal
        frame = new JFrame();
        frame.setTitle("La Casa del Motor - Sistema de Gestión Integral");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.setUndecorated(true);
        frame.setShape(new RoundRectangle2D.Double(0, 0, 1200, 800, 20, 20));

        // Guardar tamaño original
        originalBounds = new Rectangle(1200, 800);

        // Configuración del layout de tarjetas
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(new Color(245, 247, 250));

        // Crear los paneles
        JPanel loginPanel = crearPanelLoginModerno();
        JPanel mainMenuPanel = crearPanelMenuPrincipalModerno();
        mainPanel.add(loginPanel, "Login");
        mainPanel.add(mainMenuPanel, "MainMenu");

        cardLayout.show(mainPanel, "Login");
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JPanel crearPanelLoginModerno() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));

        // Panel izquierdo con color sólido
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(36, 71, 133)); // #244785
        leftPanel.setPreferredSize(new Dimension(400, 0));

        try {
            String logoPath = "C:\\Users\\irvin\\Documents\\VisualStudioCode\\Automotriz\\logo.png";
            File logoFile = new File(logoPath);

            if (logoFile.exists()) {
                ImageIcon originalIcon = new ImageIcon(logoPath);

                // Escalar a tamaño razonable
                Image imagenEscalada = originalIcon.getImage().getScaledInstance(450, 300, Image.SCALE_SMOOTH);
                ImageIcon iconoEscalado = new ImageIcon(imagenEscalada);

                JLabel logoLabel = new JLabel(iconoEscalado);
                logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
                logoLabel.setVerticalAlignment(SwingConstants.CENTER);
                logoLabel.setBorder(new EmptyBorder(40, 20, 40, 20));

                leftPanel.add(logoLabel, BorderLayout.CENTER);

            } else {
                JLabel titulo = new JLabel("LA CASA DEL MOTOR");
                titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
                titulo.setForeground(Color.WHITE);
                titulo.setHorizontalAlignment(SwingConstants.CENTER);
                titulo.setBorder(new EmptyBorder(100, 50, 0, 50));
                leftPanel.add(titulo, BorderLayout.NORTH);

                JPanel iconPanel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        g2d.setColor(Color.WHITE);
                        g2d.fillRoundRect(50, 50, 200, 100, 20, 20);
                        g2d.setColor(new Color(36, 71, 133));
                        g2d.fillOval(70, 100, 40, 40);
                        g2d.fillOval(190, 100, 40, 40);
                    }
                };
                iconPanel.setOpaque(false);
                iconPanel.setPreferredSize(new Dimension(300, 200));
                leftPanel.add(iconPanel, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JLabel titulo = new JLabel("LA CASA DEL MOTOR");
            titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
            titulo.setForeground(Color.WHITE);
            titulo.setHorizontalAlignment(SwingConstants.CENTER);
            leftPanel.add(titulo, BorderLayout.CENTER);
        }

        // Panel derecho con formulario
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(230, 230, 230)),
                new EmptyBorder(60, 60, 60, 60)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 0, 15, 0);

        JLabel welcomeLabel = new JLabel("Bienvenido");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(30, 60, 114));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        rightPanel.add(welcomeLabel, gbc);

        JLabel subTitleLabel = new JLabel("Sistema de Gestión Automotriz");
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subTitleLabel.setForeground(new Color(150, 150, 150));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 30, 0);
        rightPanel.add(subTitleLabel, gbc);

        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridwidth = 1;

        JLabel userLabel = new JLabel("Usuario");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userLabel.setForeground(new Color(80, 80, 80));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        rightPanel.add(userLabel, gbc);

        JTextField userField = new JTextField(20);
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(12, 15, 12, 15)));
        gbc.gridy = 3;
        rightPanel.add(userField, gbc);

        JLabel passLabel = new JLabel("Contraseña");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLabel.setForeground(new Color(80, 80, 80));
        gbc.gridy = 4;
        rightPanel.add(passLabel, gbc);

        // Panel para contraseña con botón de mostrar/ocultar
        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setBackground(Color.WHITE);
        
        JPasswordField passField = new JPasswordField(20);
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(12, 15, 12, 15)));
        
        // Botón para mostrar/ocultar contraseña con imágenes
        JButton togglePasswordBtn = new JButton();
        togglePasswordBtn.setPreferredSize(new Dimension(40, 40));
        togglePasswordBtn.setBackground(Color.WHITE);
        togglePasswordBtn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        togglePasswordBtn.setFocusPainted(false);
        togglePasswordBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Cargar imágenes para el botón (reemplaza estas rutas con las tuyas)
        ImageIcon iconoOjoAbierto = cargarYEscalarImagen("C:\\Users\\irvin\\Documents\\VisualStudioCode\\Automotriz\\ojo_abierto.png", 20, 20);
        ImageIcon iconoOjoCerrado = cargarYEscalarImagen("C:\\Users\\irvin\\Documents\\VisualStudioCode\\Automotriz\\ojo_cerrado.png", 20, 20);
        
        // Si no se cargan las imágenes, usar texto como fallback
        if (iconoOjoAbierto == null || iconoOjoCerrado == null) {
            togglePasswordBtn.setText("👁");
        } else {
            togglePasswordBtn.setIcon(iconoOjoCerrado);
        }
        
        // Agregar componentes al panel de contraseña
        passwordPanel.add(passField, BorderLayout.CENTER);
        passwordPanel.add(togglePasswordBtn, BorderLayout.EAST);
        
        gbc.gridy = 5;
        rightPanel.add(passwordPanel, gbc);

        JButton loginButton = new JButton("Iniciar Sesión") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(30, 60, 114), 0, getHeight(),
                        new Color(42, 82, 152));
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setPreferredSize(new Dimension(0, 45));
        gbc.gridy = 6;
        gbc.insets = new Insets(20, 0, 10, 0);
        rightPanel.add(loginButton, gbc);

        JButton exitButton = new JButton("Salir del Sistema");
        exitButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        exitButton.setForeground(new Color(150, 150, 150));
        exitButton.setBorderPainted(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setFocusPainted(false);
        gbc.gridy = 7;
        gbc.insets = new Insets(10, 0, 0, 0);
        rightPanel.add(exitButton, gbc);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.CENTER);

        // Acción para mostrar/ocultar contraseña
        togglePasswordBtn.addActionListener(e -> {
            if (passField.getEchoChar() == '\u2022') { // Si está oculta
                passField.setEchoChar((char) 0); // Mostrar texto
                if (iconoOjoAbierto != null) {
                    togglePasswordBtn.setIcon(iconoOjoAbierto);
                    togglePasswordBtn.setText("");
                } else {
                    togglePasswordBtn.setText("🙈");
                }
            } else {
                passField.setEchoChar('\u2022'); // Ocultar texto
                if (iconoOjoCerrado != null) {
                    togglePasswordBtn.setIcon(iconoOjoCerrado);
                    togglePasswordBtn.setText("");
                } else {
                    togglePasswordBtn.setText("👁");
                }
            }
        });

        // Acciones de los botones
        loginButton.addActionListener(e -> {
            String usuario = userField.getText();
            String contrasena = new String(passField.getPassword());
            if (validarCredenciales(usuario, contrasena)) {
                usuarioActual = usuario;
                cardLayout.show(mainPanel, "MainMenu");
            } else {
                JOptionPane.showMessageDialog(frame, "Credenciales incorrectas. Por favor verifique sus datos.",
                        "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
            }
        });

        exitButton.addActionListener(e -> System.exit(0));

        return panel;
    }

    // Método para cargar y escalar imágenes
    private ImageIcon cargarYEscalarImagen(String ruta, int ancho, int alto) {
        try {
            File archivoImagen = new File(ruta);
            if (archivoImagen.exists()) {
                ImageIcon iconoOriginal = new ImageIcon(ruta);
                Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
                return new ImageIcon(imagenEscalada);
            } else {
                System.out.println("No se encontró la imagen: " + ruta);
                return null;
            }
        } catch (Exception e) {
            System.out.println("Error al cargar la imagen: " + ruta + " - " + e.getMessage());
            return null;
        }
    }

    private JPanel crearPanelMenuPrincipalModerno() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(36, 71, 133)); // Cambiado al mismo azul del inicio (#244785)

        // Barra superior
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                new EmptyBorder(15, 25, 15, 25)));

        topBar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });

        topBar.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int thisX = frame.getLocation().x;
                int thisY = frame.getLocation().y;

                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;

                int X = thisX + xMoved;
                int Y = thisY + yMoved;
                frame.setLocation(X, Y);
            }
        });

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("LA CASA DEL MOTOR");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(30, 60, 114));
        titlePanel.add(titleLabel);

        JLabel subLabel = new JLabel("Sistema de Gestión Integral");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(new Color(150, 150, 150));
        subLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        titlePanel.add(subLabel);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setOpaque(false);
        JLabel userLabel = new JLabel("Usuario: " + (usuarioActual != null ? usuarioActual : "Administrador"));
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userLabel.setForeground(new Color(100, 100, 100));

        JButton logoutBtn = new JButton("Cerrar Sesión");
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoutBtn.setForeground(new Color(30, 60, 114));
        logoutBtn.setBackground(new Color(240, 240, 240));
        logoutBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(5, 10, 5, 10)));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        logoutBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutBtn.setBackground(new Color(230, 230, 230));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutBtn.setBackground(new Color(240, 240, 240));
            }
        });

        JButton minimizeBtn = new JButton("—");
        JButton maximizeBtn = new JButton("□");
        JButton closeBtn = new JButton("×");

        estiloBotonControl(minimizeBtn);
        estiloBotonControl(maximizeBtn);
        estiloBotonControl(closeBtn);

        if (isMaximized) {
            maximizeBtn.setBackground(new Color(200, 200, 200));
        }

        controlPanel.add(userLabel);
        controlPanel.add(logoutBtn);
        controlPanel.add(minimizeBtn);
        controlPanel.add(maximizeBtn);
        controlPanel.add(closeBtn);

        topBar.add(titlePanel, BorderLayout.WEST);
        topBar.add(controlPanel, BorderLayout.EAST);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(36, 71, 133)); // Fondo azul
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel welcomeTitle = new JLabel("Panel de Control Principal");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeTitle.setForeground(Color.WHITE); // Cambiado a blanco
        welcomeTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        contentPanel.add(welcomeTitle, BorderLayout.NORTH);

        // MODIFICACIÓN: Grid 2x3 en lugar de 3x3 (quitamos 3 módulos)
        JPanel modulesPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        modulesPanel.setBackground(new Color(36, 71, 133)); // Fondo azul

        // Solo los 6 módulos esenciales
        String[][] modulos = {
                {"👥", "Gestión de Usuarios", "Administrar usuarios y permisos del sistema"},
                {"🚗", "Clientes y Vehículos", "Registro y gestión de clientes y vehículos"},
                {"🔧", "Gestión de Servicios", "Control de servicios automotrices realizados"},
                {"📦", "Control de Inventario", "Gestión de refacciones y stock disponible"},
                {"🧾", "Facturación Automatizada", "Generación de facturas en PDF"},
                {"📊", "Historial y Consultas", "Consultas avanzadas y reportes históricos"}
                // Se quitaron: Generar Reportes, Respaldos y Mantenimiento, Configuración
        };

        for (String[] modulo : modulos) {
            modulesPanel.add(crearTarjetaModulo(modulo[0], modulo[1], modulo[2]));
        }

        contentPanel.add(modulesPanel, BorderLayout.CENTER);

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        minimizeBtn.addActionListener(e -> frame.setState(Frame.ICONIFIED));
        maximizeBtn.addActionListener(e -> toggleMaximize());
        closeBtn.addActionListener(e -> System.exit(0));

        // MODIFICACIÓN: Limpiar campos al cerrar sesión
        logoutBtn.addActionListener(e -> {
            int confirmacion = JOptionPane.showConfirmDialog(
                    frame,
                    "¿Está seguro que desea cerrar sesión?",
                    "Cerrar Sesión",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (confirmacion == JOptionPane.YES_OPTION) {
                usuarioActual = null;
                
                // Limpiar los campos del login
                limpiarCamposLogin();
                
                cardLayout.show(mainPanel, "Login");
            }
        });

        return panel;
    }

    private void toggleMaximize() {
        if (isMaximized) {
            frame.setSize(originalBounds.width, originalBounds.height);
            frame.setLocationRelativeTo(null);
            frame.setShape(new RoundRectangle2D.Double(0, 0, originalBounds.width, originalBounds.height, 20, 20));
            isMaximized = false;
        } else {
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            Rectangle bounds = gd.getDefaultConfiguration().getBounds();
            frame.setSize(bounds.width, bounds.height);
            frame.setLocation(bounds.x, bounds.y);
            frame.setShape(new RoundRectangle2D.Double(0, 0, bounds.width, bounds.height, 0, 0));
            isMaximized = true;
        }
        frame.revalidate();
        frame.repaint();
    }

    private JPanel crearTarjetaModulo(String icono, String titulo, String descripcion) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(240, 240, 240)),
                new EmptyBorder(20, 20, 20, 20)));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // RESTAURADO: Efecto de borde al pasar el mouse
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(250, 250, 250));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(30, 60, 114), 2), // Borde azul de 2px
                        new EmptyBorder(20, 20, 20, 20)));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(240, 240, 240)), // Borde gris claro
                        new EmptyBorder(20, 20, 20, 20)));
            }
        });

        JLabel iconLabel = new JLabel(icono);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel titleLabel = new JLabel(titulo);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(30, 60, 114));

        JLabel descLabel = new JLabel("<html><div style='text-align: justify;'>" + descripcion + "</div></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        descLabel.setForeground(new Color(120, 120, 120));
        descLabel.setBorder(new EmptyBorder(5, 0, 0, 0));

        card.add(iconLabel, BorderLayout.NORTH);
        card.add(titleLabel, BorderLayout.CENTER);
        card.add(descLabel, BorderLayout.SOUTH);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                switch (titulo) {
                    case "Gestión de Usuarios":
                        abrirModuloUsuarios();
                        break;
                    case "Clientes y Vehículos":
                        abrirModuloClientesVehiculos();
                        break;
                    case "Gestión de Servicios":
                        abrirModuloServicios();
                        break;
                    case "Control de Inventario":
                        abrirModuloInventario();
                        break;
                    case "Facturación Automatizada":
                        abrirModuloFacturacion();
                        break;
                    case "Historial y Consultas":
                        abrirModuloHistorial();
                        break;
                    default:
                        JOptionPane.showMessageDialog(frame,
                                "Accediendo al módulo: " + titulo + "\n\n" + descripcion,
                                "Módulo del Sistema",
                                JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        return card;
    }

    private void estiloBotonControl(JButton boton) {
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setPreferredSize(new Dimension(30, 25));
        boton.setBackground(new Color(240, 240, 240));
        boton.setForeground(new Color(100, 100, 100));
        boton.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        boton.setFocusPainted(false);
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(new Color(230, 230, 230));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(new Color(240, 240, 240));
            }
        });
    }

    // NUEVO MÉTODO: Limpiar campos de login
    private void limpiarCamposLogin() {
        // Buscar y limpiar los campos del panel de login
        Component[] components = mainPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                limpiarCamposEnPanel(panel);
            }
        }
    }

    private void limpiarCamposEnPanel(JPanel panel) {
        Component[] components = panel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JTextField) {
                // Limpiar campo de usuario
                ((JTextField) comp).setText("");
            } else if (comp instanceof JPasswordField) {
                // Limpiar campo de contraseña
                ((JPasswordField) comp).setText("");
            } else if (comp instanceof JPanel) {
                // Buscar recursivamente en sub-paneles
                limpiarCamposEnPanel((JPanel) comp);
            }
        }
    }

    // MÉTODOS PARA ABRIR MÓDULOS (solo los esenciales)
    private void abrirModuloUsuarios() {
        JPanel panelContenedor = crearPanelContenedorConVolver();
        GestionUsuariosPanel panelUsuarios = new GestionUsuariosPanel();
        panelContenedor.add(panelUsuarios, BorderLayout.CENTER);

        mainPanel.add(panelContenedor, "GestionUsuarios");
        cardLayout.show(mainPanel, "GestionUsuarios");
    }

    private void abrirModuloClientesVehiculos() {
        JPanel panelContenedor = crearPanelContenedorConVolver();
        ClientesVehiculosPanel panelClientesVehiculos = new ClientesVehiculosPanel();
        panelContenedor.add(panelClientesVehiculos, BorderLayout.CENTER);

        mainPanel.add(panelContenedor, "ClientesVehiculos");
        cardLayout.show(mainPanel, "ClientesVehiculos");
    }

    private void abrirModuloServicios() {
        JPanel panelContenedor = crearPanelContenedorConVolver();
        GestionServiciosPanel panelServicios = new GestionServiciosPanel();
        panelContenedor.add(panelServicios, BorderLayout.CENTER);

        mainPanel.add(panelContenedor, "GestionServicios");
        cardLayout.show(mainPanel, "GestionServicios");
    }

    private void abrirModuloInventario() {
        JPanel panelContenedor = crearPanelContenedorConVolver();
        ControlInventarioPanel panelInventario = new ControlInventarioPanel();
        panelContenedor.add(panelInventario, BorderLayout.CENTER);

        mainPanel.add(panelContenedor, "ControlInventario");
        cardLayout.show(mainPanel, "ControlInventario");
    }

    private void abrirModuloFacturacion() {
        JPanel panelContenedor = crearPanelContenedorConVolver();
        FacturacionAutomatizadaPanel panelFacturacion = new FacturacionAutomatizadaPanel();
        panelContenedor.add(panelFacturacion, BorderLayout.CENTER);

        mainPanel.add(panelContenedor, "FacturacionAutomatizada");
        cardLayout.show(mainPanel, "FacturacionAutomatizada");
    }

    private void abrirModuloHistorial() {
        JPanel panelContenedor = crearPanelContenedorConVolver();
        HistorialConsultasPanel panelHistorial = new HistorialConsultasPanel();
        panelContenedor.add(panelHistorial, BorderLayout.CENTER);

        mainPanel.add(panelContenedor, "HistorialConsultas");
        cardLayout.show(mainPanel, "HistorialConsultas");
    }

    private JPanel crearPanelContenedorConVolver() {
        JPanel panelContenedor = new JPanel(new BorderLayout());
        panelContenedor.setBackground(new Color(245, 247, 250));

        JPanel panelVolver = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelVolver.setBackground(new Color(245, 247, 250));
        panelVolver.setBorder(new EmptyBorder(10, 20, 10, 20));

        JButton botonVolver = new JButton("← Volver al Menú Principal");
        botonVolver.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        botonVolver.setForeground(new Color(30, 60, 114));
        botonVolver.setBorderPainted(false);
        botonVolver.setContentAreaFilled(false);
        botonVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));

        botonVolver.addActionListener(e -> {
            cardLayout.show(mainPanel, "MainMenu");
        });

        panelVolver.add(botonVolver);
        panelContenedor.add(panelVolver, BorderLayout.NORTH);

        return panelContenedor;
    }

    private boolean validarCredenciales(String usuario, String contrasena) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            // Obtener el hash de la contraseña para el usuario activo
            String sql = "SELECT id_usuario, contrasena FROM usuarios WHERE usuario = ? AND estado = 'Activo'";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, usuario);

            rs = stmt.executeQuery();

            if (rs.next()) {
                String hash = rs.getString("contrasena");
                int idUsuario = rs.getInt("id_usuario");

                // Verificar con bcrypt cuando el hash está en formato bcrypt
                try {
                    if (hash != null
                            && (hash.startsWith("$2a$") || hash.startsWith("$2y$") || hash.startsWith("$2b$"))) {
                        if (BCrypt.checkpw(contrasena, hash)) {
                            actualizarUltimoAcceso(idUsuario);
                            return true;
                        }
                    } else {
                        // Fallback: si la contraseña en BD no está en formato bcrypt,
                        // comparar texto plano (soporte para datos legacy).
                        if (hash != null && hash.equals(contrasena)) {
                            actualizarUltimoAcceso(idUsuario);
                            return true;
                        }
                    }
                } catch (IllegalArgumentException iae) {
                    // Si el hash tiene un formato inválido para jBCrypt, intentar fallback a
                    // comparación directa
                    if (hash != null && hash.equals(contrasena)) {
                        actualizarUltimoAcceso(idUsuario);
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                    "Error al validar credenciales: " + e.getMessage(),
                    "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                    "Error inesperado al validar credenciales: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DatabaseUtils.cerrarRecursos(conn, stmt, rs);
        }
        return false;
    }

    private void actualizarUltimoAcceso(int idUsuario) {
        String query = "UPDATE usuarios SET ultimo_acceso = NOW() WHERE id_usuario = ?";
        DatabaseUtils.ejecutarUpdate(query, idUsuario);
    }
}