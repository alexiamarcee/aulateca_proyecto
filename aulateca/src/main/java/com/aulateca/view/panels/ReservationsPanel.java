package com.aulateca.view.panels;

import com.aulateca.controller.ReservationController;
import com.aulateca.model.*;
import com.aulateca.view.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Gestión de reservas (capa Vista). */
public class ReservationsPanel extends JPanel {

    private final User                  usuarioActual;
    private final ReservationController controller = new ReservationController();

    private JTable            tabla;
    private DefaultTableModel modelo;
    private List<Reservation> reservasActuales;
    private JComboBox<String> cmbFiltro;

    public ReservationsPanel(User usuario) {
        this.usuarioActual = usuario;
        setBackground(AppColors.BG_APP);
        setLayout(new BorderLayout(0, 20));
        initUI();
        cargarReservas();
    }

    private void initUI() {
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);

        JLabel titulo = new JLabel("Reservas");
        titulo.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        titulo.setForeground(AppColors.TEXT_PRIMARY);
        header.add(titulo, BorderLayout.WEST);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        toolbar.setOpaque(false);

        cmbFiltro = new JComboBox<>(new String[]{
            "Todas", "Hoy", "Esta semana", "Este mes"});
        cmbFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbFiltro.setPreferredSize(new Dimension(150, 36));
        cmbFiltro.addActionListener(e -> cargarReservas());

        JButton btnRefrescar  = UIFactory.secondaryButton("↻");
        JButton btnCancelar   = UIFactory.secondaryButton("Cancelar reserva");
        JButton btnNueva      = UIFactory.primaryButton("Nueva reserva");

        btnRefrescar.addActionListener(e -> cargarReservas());
        btnCancelar.addActionListener(e -> cancelarSeleccionada());
        btnNueva.addActionListener(e -> abrirFormulario(null));

        toolbar.add(cmbFiltro);
        toolbar.add(btnRefrescar);
        toolbar.add(btnCancelar);
        toolbar.add(btnNueva);
        header.add(toolbar, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColors.BG_WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);

        String[] columnas = {"", "Recurso", "Fecha", "Franja", "Usuario", "Motivo", "Estado"};
        modelo = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        UIFactory.styleTable(tabla);

        tabla.getColumnModel().getColumn(0).setPreferredWidth(8);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(160);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(180);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(160);
        tabla.getColumnModel().getColumn(6).setPreferredWidth(110);

        tabla.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                JPanel p = new JPanel();
                p.setOpaque(true);
                p.setBackground(sel ? AppColors.ROW_SELECTED : AppColors.BG_WHITE);
                if (row < reservasActuales.size()) {
                    Reservation r = reservasActuales.get(row);
                    Color barColor = switch (r.getEstado()) {
                        case CONFIRMADA -> AppColors.PRIMARY;
                        case CANCELADA  -> AppColors.ERROR;
                        default         -> AppColors.WARNING;
                    };
                    p.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, barColor));
                }
                return p;
            }
        });

        tabla.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                String val = v != null ? v.toString() : "";
                Color bg = switch (val) {
                    case "CONFIRMADA" -> AppColors.AVAILABLE;
                    case "CANCELADA"  -> AppColors.RESERVED;
                    default           -> new Color(0xFFF8E1);
                };
                Color fg = switch (val) {
                    case "CONFIRMADA" -> AppColors.AVAILABLE_FG;
                    case "CANCELADA"  -> AppColors.RESERVED_FG;
                    default           -> AppColors.WARNING;
                };
                JLabel chip = UIFactory.chip(val, bg, fg);
                chip.setOpaque(true);
                chip.setBackground(sel ? AppColors.ROW_SELECTED : AppColors.BG_WHITE);
                return chip;
            }
        });

        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) verDetalle();
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(AppColors.BG_WHITE);
        card.add(scroll, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);
    }

    private void cargarReservas() {
        modelo.setRowCount(0);
        var resultado = controller.listarReservas(usuarioActual, cmbFiltro.getSelectedIndex());
        if (resultado.esError()) {
            UIFactory.showError(this, resultado.error());
            reservasActuales = List.of();
            return;
        }
        reservasActuales = resultado.datos();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Reservation r : reservasActuales) {
            modelo.addRow(new Object[]{
                "",
                r.getRecurso().getNombre(),
                r.getFecha().format(fmt),
                r.getFranjaHoraria().getNombre(),
                r.getUsuario().getNombreCompleto(),
                r.getMotivo() != null ? r.getMotivo() : "—",
                r.getEstado().name()
            });
        }
    }

    private void abrirFormulario(Reservation reserva) {
        abrirFormulario(this, usuarioActual, reserva, this::cargarReservas);
    }

    public static void abrirFormulario(Component parent, User usuarioActual,
                                       Reservation reserva, Runnable alGuardar) {
        ReservationController ctrl = new ReservationController();
        var recursos = ctrl.listarRecursos();
        var usuarios = ctrl.listarUsuariosActivos();
        var franjas  = ctrl.listarFranjas();
        if (recursos.esError() || usuarios.esError() || franjas.esError()) {
            UIFactory.showError(parent, "No se pudieron cargar los datos del formulario.");
            return;
        }

        List<User> usuariosForm = usuarioActual.puedeGestionarReservasAjena()
            ? usuarios.datos()
            : List.of(usuarioActual);

        Window w = SwingUtilities.getWindowAncestor(parent);
        Frame frame = w instanceof Frame f ? f : null;
        ReservationFormDialog dlg = new ReservationFormDialog(
            frame, reserva, usuarioActual,
            recursos.datos(), usuariosForm, franjas.datos(), ctrl);
        dlg.setVisible(true);
        if (dlg.isSaved() && alGuardar != null) alGuardar.run();
    }

    private void cancelarSeleccionada() {
        int row = tabla.getSelectedRow();
        if (row < 0) { UIFactory.showError(this, "Selecciona una reserva primero."); return; }
        Reservation r = reservasActuales.get(row);
        if (r.getEstado() == Reservation.Estado.CANCELADA) {
            UIFactory.showError(this, "Esta reserva ya está cancelada.");
            return;
        }
        if (UIFactory.showConfirm(this, "¿Cancelar la reserva de «" + r.getRecurso().getNombre()
                + "» el " + r.getFecha() + "?")) {
            controller.cancelarReserva(usuarioActual, r.getId()).ifPresentOrElse(
                msg -> UIFactory.showError(this, msg),
                () -> { UIFactory.showSuccess(this, "Reserva cancelada."); cargarReservas(); }
            );
        }
    }

    private void verDetalle() {
        int row = tabla.getSelectedRow();
        if (row < 0) return;
        Reservation r = reservasActuales.get(row);
        String msg = "<html><b>Detalle de la reserva</b><br><br>"
            + "<b>Recurso:</b> " + r.getRecurso().getNombre() + "<br>"
            + "<b>Tipo:</b> "    + r.getRecurso().getTipo().getNombre() + "<br>"
            + "<b>Fecha:</b> "   + r.getFecha() + "<br>"
            + "<b>Franja:</b> "  + r.getFranjaHoraria() + "<br>"
            + "<b>Usuario:</b> " + r.getUsuario().getNombreCompleto() + "<br>"
            + "<b>Motivo:</b> "  + (r.getMotivo() != null ? r.getMotivo() : "—") + "<br>"
            + "<b>Estado:</b> "  + r.getEstado() + "</html>";
        JOptionPane.showMessageDialog(this, msg, "Detalle de reserva", JOptionPane.PLAIN_MESSAGE);
    }
}
