package com.aulateca.view.panels;

import com.aulateca.model.*;
import com.aulateca.service.ReservationService;
import com.aulateca.view.*;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/** Formulario de nueva o edición de reserva. */
public class ReservationFormDialog extends JDialog {

    private final Reservation      reserva;
    private final User             usuarioActual;
    private final List<Resource>   recursos;
    private final List<User>       usuarios;
    private final List<TimeSlot>   franjas;
    private final ReservationService service;

    private JComboBox<User>     cmbUsuario;
    private JComboBox<Resource> cmbRecurso;
    private JDateChooser        dateChooser;
    private JComboBox<TimeSlot> cmbFranja;
    private JTextArea           txtMotivo;
    private JLabel              lblDisponibilidad;
    private boolean             saved = false;

    public ReservationFormDialog(Frame parent, Reservation reserva, User usuarioActual,
                                  List<Resource> recursos, List<User> usuarios,
                                  List<TimeSlot> franjas, ReservationService service) {
        super(parent, reserva == null ? "Nueva reserva" : "Editar reserva", true);
        this.reserva       = reserva;
        this.usuarioActual = usuarioActual;
        this.recursos      = recursos;
        this.usuarios      = usuarios;
        this.franjas       = franjas;
        this.service       = service;
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

        dateChooser = new JDateChooser();
        dateChooser.setDate(new Date());
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setFont(UIFactory.FONT_BODY);
        dateChooser.setMinSelectableDate(new Date());
        estilizarCalendario(dateChooser);

        cmbFranja = UIFactory.comboBox();
        franjas.forEach(cmbFranja::addItem);

        txtMotivo = new JTextArea(3, 20);

        int row = 0;
        addFormRow(form, gbc, row++, "Usuario", cmbUsuario);
        addFormRow(form, gbc, row++, "Recurso", cmbRecurso);
        addFormRow(form, gbc, row++, "Fecha",   dateChooser);
        addFormRow(form, gbc, row++, "Franja horaria", cmbFranja);
        addFormRow(form, gbc, row++, "Motivo (opcional)", UIFactory.textArea(txtMotivo));

        gbc.gridy  = row++;
        gbc.insets = new Insets(16, 0, 4, 0);
        JButton btnComprobar = UIFactory.secondaryButton("Comprobar disponibilidad");
        btnComprobar.addActionListener(e -> comprobarDisponibilidad());
        form.add(btnComprobar, gbc);

        gbc.gridy  = row;
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

    private void estilizarCalendario(JDateChooser dc) {
        for (Component c : dc.getComponents()) {
            if (c instanceof JButton btn) {
                btn.setBackground(AppColors.PRIMARY);
                btn.setForeground(Color.WHITE);
                btn.setFocusPainted(false);
                btn.setBorderPainted(false);
                btn.setOpaque(true);
                btn.setText("📅");
                btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
            }
        }
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

    private void comprobarDisponibilidad() {
        Resource  recurso = (Resource)  cmbRecurso.getSelectedItem();
        TimeSlot  franja  = (TimeSlot)  cmbFranja.getSelectedItem();
        LocalDate fecha   = getFecha();

        if (fecha == null)   { lblDisponibilidad.setText("Selecciona una fecha."); lblDisponibilidad.setForeground(AppColors.WARNING); return; }
        if (recurso == null) { lblDisponibilidad.setText("Selecciona un recurso."); lblDisponibilidad.setForeground(AppColors.WARNING); return; }

        if (!recurso.getEstado().isReservable()) {
            lblDisponibilidad.setText("✕  El recurso no está disponible (" + recurso.getEstado().getNombre() + ")");
            lblDisponibilidad.setForeground(AppColors.ERROR);
            return;
        }
        Long excludeId = (reserva != null) ? reserva.getId() : null;
        boolean conflicto = new com.aulateca.dao.ReservationDAO()
            .existeConflicto(recurso, fecha, franja, excludeId);

        if (conflicto) {
            lblDisponibilidad.setText("✕  Ya existe una reserva para ese recurso, fecha y franja.");
            lblDisponibilidad.setForeground(AppColors.ERROR);
        } else {
            lblDisponibilidad.setText("✓  Disponible — puedes guardar.");
            lblDisponibilidad.setForeground(AppColors.SUCCESS);
        }
    }

    private void guardar() {
        User      usuario = (User)      cmbUsuario.getSelectedItem();
        Resource  recurso = (Resource)  cmbRecurso.getSelectedItem();
        TimeSlot  franja  = (TimeSlot)  cmbFranja.getSelectedItem();
        LocalDate fecha   = getFecha();
        String    motivo  = txtMotivo.getText().trim();

        if (fecha == null) { UIFactory.showError(this, "Selecciona una fecha."); return; }

        try {
            if (reserva == null) {
                service.crearReserva(usuario, recurso, fecha, franja, motivo.isEmpty() ? null : motivo);
                UIFactory.showSuccess(this, "Reserva creada correctamente.");
            } else {
                service.modificarReserva(reserva.getId(), usuario, recurso, fecha, franja,
                    motivo.isEmpty() ? null : motivo);
                UIFactory.showSuccess(this, "Reserva actualizada correctamente.");
            }
            saved = true;
            dispose();
        } catch (IllegalArgumentException ex) {
            UIFactory.showError(this, ex.getMessage());
        } catch (Exception ex) {
            UIFactory.showError(this, "Error inesperado: " + ex.getMessage());
        }
    }

    public boolean isSaved() { return saved; }
}
