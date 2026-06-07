package com.aulateca.view.panels;

import com.aulateca.controller.ReservationController;
import com.aulateca.model.*;
import com.aulateca.service.dto.DisponibilidadCheckResult;
import com.aulateca.view.*;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/** Formulario de nueva o edición de reserva (capa Vista). */
public class ReservationFormDialog extends JDialog {

    private final Reservation           reserva;
    private final User                  usuarioActual;
    private final List<Resource>        recursos;
    private final List<User>            usuarios;
    private final List<TimeSlot>        franjas;
    private final ReservationController controller;

    private JComboBox<User>     cmbUsuario;
    private JComboBox<Resource> cmbRecurso;
    private JDateChooser        dateChooser;
    private JComboBox<TimeSlot> cmbFranja;
    private JTextArea           txtMotivo;
    private JLabel              lblDisponibilidad;
    private boolean             saved = false;

    public ReservationFormDialog(Frame parent, Reservation reserva, User usuarioActual,
                                  List<Resource> recursos, List<User> usuarios,
                                  List<TimeSlot> franjas, ReservationController controller) {
        super(parent, reserva == null ? "Nueva reserva" : "Editar reserva", true);
        this.reserva       = reserva;
        this.usuarioActual = usuarioActual;
        this.recursos      = recursos;
        this.usuarios      = usuarios;
        this.franjas       = franjas;
        this.controller    = controller;
        initUI();
        if (reserva != null) rellenarDatos();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setMinimumSize(new Dimension(500, 580));
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppColors.BG_WHITE);
        setContentPane(root);

        JPanel topStrip = new JPanel(new BorderLayout());
        topStrip.setBackground(AppColors.PRIMARY);
        topStrip.setPreferredSize(new Dimension(0, 56));
        topStrip.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        JLabel titleLbl = new JLabel(reserva == null ? "Nueva reserva" : "Editar reserva");
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        titleLbl.setForeground(Color.WHITE);
        topStrip.add(titleLbl, BorderLayout.CENTER);

        JButton btnX = new JButton("✕");
        btnX.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btnX.setForeground(Color.WHITE);
        btnX.setBackground(AppColors.PRIMARY);
        btnX.setBorderPainted(false);
        btnX.setFocusPainted(false);
        btnX.setOpaque(true);
        btnX.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnX.addActionListener(e -> dispose());
        topStrip.add(btnX, BorderLayout.EAST);
        root.add(topStrip, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(AppColors.BG_WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(24, 28, 16, 28));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.gridx   = 0;
        gbc.weightx = 1.0;

        cmbUsuario = UIFactory.comboBox();
        usuarios.forEach(cmbUsuario::addItem);
        cmbUsuario.setSelectedItem(usuarioActual);

        cmbRecurso = UIFactory.comboBox();
        recursos.forEach(cmbRecurso::addItem);

        dateChooser = UIFactory.dateChooser();
        dateChooser.setDate(new Date());
        dateChooser.setMinSelectableDate(new Date());

        cmbFranja = UIFactory.comboBox();
        franjas.forEach(cmbFranja::addItem);

        txtMotivo = new JTextArea(3, 20);

        int row = 0;
        addFormRow(form, gbc, row++, "Usuario", cmbUsuario);
        addFormRow(form, gbc, row++, "Recurso", cmbRecurso);
        addFormRow(form, gbc, row++, "Fecha",   dateChooser);
        addFormRow(form, gbc, row++, "Franja horaria", cmbFranja);
        addFormRow(form, gbc, row++, "Motivo (opcional)", UIFactory.textArea(txtMotivo));

        gbc.gridy  = row * 2;
        gbc.insets = new Insets(16, 0, 4, 0);
        JButton btnComprobar = UIFactory.secondaryButton("Comprobar disponibilidad");
        btnComprobar.addActionListener(e -> comprobarDisponibilidad());
        form.add(btnComprobar, gbc);

        gbc.gridy  = row * 2 + 1;
        gbc.insets = new Insets(4, 0, 0, 0);
        lblDisponibilidad = new JLabel(" ");
        lblDisponibilidad.setFont(new Font("Segoe UI", Font.BOLD, 12));
        form.add(lblDisponibilidad, gbc);

        root.add(form, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        footer.setBackground(AppColors.BG_APP);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppColors.BORDER));

        JButton btnCancel  = UIFactory.textButton("Cancelar");
        JButton btnGuardar = UIFactory.primaryButton(
            reserva == null ? "Guardar" : "Actualizar");

        btnCancel.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> guardar());
        footer.add(btnCancel);
        footer.add(btnGuardar);
        root.add(footer, BorderLayout.SOUTH);
    }

    private void addFormRow(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridy  = row * 2;
        gbc.insets = new Insets(row == 0 ? 0 : 14, 0, 4, 0);
        form.add(UIFactory.formLabel(label), gbc);
        gbc.gridy  = row * 2 + 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        form.add(field, gbc);
    }

    private void rellenarDatos() {
        cmbUsuario.setSelectedItem(reserva.getUsuario());
        cmbRecurso.setSelectedItem(reserva.getRecurso());
        dateChooser.setDate(Date.from(
            reserva.getFecha().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        cmbFranja.setSelectedItem(reserva.getFranjaHoraria());
        txtMotivo.setText(reserva.getMotivo() != null ? reserva.getMotivo() : "");
    }

    private LocalDate getFecha() {
        Date d = dateChooser.getDate();
        return d == null ? null : d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /** Delega la comprobación de disponibilidad al controlador. */
    private void comprobarDisponibilidad() {
        Long excludeId = (reserva != null) ? reserva.getId() : null;
        DisponibilidadCheckResult resultado = controller.verificarDisponibilidad(
            (Resource) cmbRecurso.getSelectedItem(),
            getFecha(),
            (TimeSlot) cmbFranja.getSelectedItem(),
            excludeId);

        String prefijo = switch (resultado.tipo()) {
            case OK    -> "✓  ";
            case AVISO -> "";
            case ERROR -> "✕  ";
        };
        lblDisponibilidad.setText(prefijo + resultado.mensaje());
        lblDisponibilidad.setForeground(switch (resultado.tipo()) {
            case OK    -> AppColors.SUCCESS;
            case AVISO -> AppColors.WARNING;
            case ERROR -> AppColors.ERROR;
        });
    }

    /** Delega el guardado al controlador. */
    private void guardar() {
        User      usuario = (User)     cmbUsuario.getSelectedItem();
        Resource  recurso = (Resource) cmbRecurso.getSelectedItem();
        TimeSlot  franja  = (TimeSlot) cmbFranja.getSelectedItem();
        LocalDate fecha   = getFecha();
        String    motivo  = txtMotivo.getText().trim();
        String motivoFinal = motivo.isEmpty() ? null : motivo;

        var error = (reserva == null)
            ? controller.crearReserva(usuario, recurso, fecha, franja, motivoFinal)
            : controller.modificarReserva(reserva.getId(), usuario, recurso, fecha, franja, motivoFinal);

        if (error.isPresent()) {
            UIFactory.showError(this, error.get());
            return;
        }
        UIFactory.showSuccess(this, reserva == null ?
            "Reserva creada correctamente." : "Reserva actualizada correctamente.");
        saved = true;
        dispose();
    }

    public boolean isSaved() { return saved; }
}
