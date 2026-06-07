package com.aulateca.view;

import com.aulateca.model.User;
import com.aulateca.view.panels.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/** Ventana principal de la aplicación. */
public class MainFrame extends JFrame {

    private User    usuarioActual;
    private JPanel  contentArea;
    private JPanel  activeNavItem;

    public MainFrame(User usuario) {
        this.usuarioActual = usuario;
        initUI();
        mostrarPanel(new DashboardPanel(usuarioActual));
    }

    private void initUI() {
        setTitle("Aulateca");
        setSize(1280, 800);
        setMinimumSize(new Dimension(960, 600));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppColors.BG_APP);
        setContentPane(root);

        root.add(buildTopBar(),  BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(AppColors.BG_APP);
        body.add(buildSidebar(), BorderLayout.WEST);

        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(AppColors.BG_APP);
        contentArea.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        body.add(contentArea, BorderLayout.CENTER);

        root.add(body, BorderLayout.CENTER);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout(0, 0));
        bar.setBackground(AppColors.BG_WHITE);
        bar.setPreferredSize(new Dimension(0, 64));
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.BORDER),
            BorderFactory.createEmptyBorder(10, 20, 0, 20)
        ));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);
        JLabel logo = new JLabel("<html>" +
            "<span style='font-family:Segoe UI;font-size:20px;color:#1A73E8'><b>Aula</b></span>" +
            "<span style='font-family:Segoe UI;font-size:20px;color:#0F9D58'><b>teca</b></span>" +
            "</html>");
        left.add(logo);
        bar.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        String iniciales = String.valueOf(usuarioActual.getNombre().charAt(0)).toUpperCase()
            + String.valueOf(usuarioActual.getApellidos().charAt(0)).toUpperCase();
        JLabel avatar = new JLabel(iniciales) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColors.PRIMARY);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        avatar.setForeground(Color.WHITE);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(36, 36));
        avatar.setOpaque(false);

        JLabel nombreLbl = new JLabel(usuarioActual.getNombreCompleto());
        nombreLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nombreLbl.setForeground(AppColors.TEXT_PRIMARY);

        JLabel rolChip = UIFactory.chip(
            usuarioActual.getRol().name(),
            AppColors.PRIMARY_LIGHT,
            AppColors.PRIMARY
        );

        JButton btnSalir = UIFactory.textButton("Cerrar sesión");
        btnSalir.addActionListener(e -> cerrarSesion());

        right.add(avatar);
        right.add(nombreLbl);
        right.add(rolChip);
        right.add(new JSeparator(SwingConstants.VERTICAL) {{
            setPreferredSize(new Dimension(1, 24));
            setForeground(AppColors.BORDER);
        }});
        right.add(btnSalir);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(AppColors.BG_APP);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, AppColors.BORDER),
            BorderFactory.createEmptyBorder(16, 8, 16, 8)
        ));

        JButton btnNueva = new JButton("+ Nueva reserva") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 24, 24));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnNueva.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnNueva.setForeground(AppColors.TEXT_PRIMARY);
        btnNueva.setBackground(AppColors.BG_WHITE);
        btnNueva.setOpaque(false);
        btnNueva.setContentAreaFilled(false);
        btnNueva.setFocusPainted(false);
        btnNueva.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER, 1),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        btnNueva.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnNueva.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnNueva.setMaximumSize(new Dimension(210, 48));
        btnNueva.addActionListener(e -> mostrarPanel(new ReservationsPanel(usuarioActual)));
        sidebar.add(btnNueva);
        sidebar.add(Box.createVerticalStrut(20));

        addSidebarLabel(sidebar, "MENÚ PRINCIPAL");
        addNavItem(sidebar, "🏠", "Inicio",            () -> mostrarPanel(new DashboardPanel(usuarioActual)));
        addNavItem(sidebar, "📅", "Reservas",           () -> mostrarPanel(new ReservationsPanel(usuarioActual)));
        addNavItem(sidebar, "🔍", "Disponibilidad",     () -> mostrarPanel(new AvailabilityPanel()));

        if (usuarioActual.getRol() == User.Rol.ADMIN || usuarioActual.getRol() == User.Rol.PROFESOR) {

            sidebar.add(Box.createVerticalStrut(8));
            addSidebarSeparator(sidebar);
            sidebar.add(Box.createVerticalStrut(8));

            addSidebarLabel(sidebar, "CONFIGURACIÓN");
            addNavItem(sidebar, "🏫", "Recursos",           () -> mostrarPanel(new ResourcesPanel()));
            addNavItem(sidebar, "📁", "Tipos de recurso",   () -> mostrarPanel(new ResourceTypesPanel()));
            addNavItem(sidebar, "🔴", "Estados",            () -> mostrarPanel(new ResourceStatusPanel()));
            addNavItem(sidebar, "⏰", "Franjas horarias",   () -> mostrarPanel(new TimeSlotsPanel()));
        }
        
        if (usuarioActual.getRol() == User.Rol.ADMIN) {
            sidebar.add(Box.createVerticalStrut(8));
            addSidebarSeparator(sidebar);
            sidebar.add(Box.createVerticalStrut(8));
            addSidebarLabel(sidebar, "ADMINISTRACIÓN");
            addNavItem(sidebar, "👥", "Usuarios",       () -> mostrarPanel(new UsersPanel()));
        }

        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private void addSidebarLabel(JPanel sidebar, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(AppColors.TEXT_HINT);
        lbl.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lbl);
        sidebar.add(Box.createVerticalStrut(2));
    }

    private void addSidebarSeparator(JPanel sidebar) {
        JSeparator sep = new JSeparator();
        sep.setForeground(AppColors.DIVIDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebar.add(sep);
    }

    private void addNavItem(JPanel sidebar, String icon, String label, Runnable action) {
        JPanel item = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                if (getBackground() != AppColors.BG_APP) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        item.setLayout(new BoxLayout(item, BoxLayout.X_AXIS));
        item.setOpaque(false);
        item.setMaximumSize(new Dimension(214, 40));
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        item.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 8));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(UIFactory.emojiFont(14));
        iconLbl.setAlignmentY(Component.CENTER_ALIGNMENT);

        JLabel textLbl = new JLabel(label);
        textLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLbl.setForeground(AppColors.TEXT_PRIMARY);
        textLbl.setAlignmentY(Component.CENTER_ALIGNMENT);

        item.add(Box.createHorizontalStrut(4));
        item.add(iconLbl);
        item.add(Box.createHorizontalStrut(8));
        item.add(textLbl);
        item.add(Box.createHorizontalGlue());

        item.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (activeNavItem != null) {
                    activeNavItem.setBackground(AppColors.BG_APP);
                    activeNavItem.setOpaque(false);
                    for (Component c : activeNavItem.getComponents()) {
                        if (c instanceof JLabel l) l.setForeground(AppColors.TEXT_PRIMARY);
                    }
                    activeNavItem.repaint();
                }
                activeNavItem = item;
                item.setBackground(AppColors.NAV_ACTIVE_BG);
                for (Component c : item.getComponents()) {
                    if (c instanceof JLabel l) l.setForeground(AppColors.NAV_ACTIVE_FG);
                }
                item.repaint();
                action.run();
            }
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (item != activeNavItem) {
                    item.setBackground(AppColors.BG_HOVER);
                    item.repaint();
                }
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (item != activeNavItem) {
                    item.setBackground(AppColors.BG_APP);
                    item.repaint();
                }
            }
        });

        sidebar.add(item);
        sidebar.add(Box.createVerticalStrut(2));
    }

    public void mostrarPanel(JPanel panel) {
        contentArea.removeAll();
        contentArea.add(panel, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private void cerrarSesion() {
        if (UIFactory.showConfirm(this, "¿Deseas cerrar sesión?")) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                JFrame dummy = new JFrame();
                LoginDialog login = new LoginDialog(dummy);
                login.setVisible(true);
                User u = login.getUsuarioAutenticado();
                if (u != null) new MainFrame(u).setVisible(true);
            });
        }
    }
}
