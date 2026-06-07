package com.aulateca.view.panels;

import com.aulateca.controller.TimeSlotController;
import com.aulateca.model.TimeSlot;
import com.aulateca.view.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalTime;
import java.util.List;

/** CRUD de franjas horarias (capa Vista). */
public class TimeSlotsPanel extends JPanel {

    private final TimeSlotController controller = new TimeSlotController();
    private JTable tabla;
    private DefaultTableModel modelo;
    private List<TimeSlot> lista;

    public TimeSlotsPanel() {
        setBackground(AppColors.BG_LIGHT);
        setLayout(new BorderLayout(0, 16));
        initUI();
        cargarDatos();
    }

    private void initUI() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(UIFactory.sectionTitle("Franjas horarias"), BorderLayout.WEST);

        JPanel tb = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tb.setOpaque(false);
        JButton btnNuevo    = UIFactory.accentButton("Nueva franja");
        JButton btnEditar   = UIFactory.primaryButton("Editar");
        JButton btnEliminar = UIFactory.dangerButton("Eliminar");
        btnNuevo.addActionListener(e -> abrirForm(null));
        btnEditar.addActionListener(e -> {
            int r = tabla.getSelectedRow();
            if (r >= 0) abrirForm(lista.get(r));
            else UIFactory.showError(this, "Selecciona una franja.");
        });
        btnEliminar.addActionListener(e -> eliminar());
        tb.add(btnEliminar); tb.add(btnEditar); tb.add(btnNuevo);
        top.add(tb, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        modelo = new DefaultTableModel(
                new String[]{"Nombre", "Hora inicio", "Hora fin", "Orden"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        UIFactory.styleTable(tabla);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(160);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(60);

        JScrollPane sp = new JScrollPane(tabla);
        sp.setBorder(BorderFactory.createLineBorder(AppColors.BORDER));
        add(sp, BorderLayout.CENTER);
    }

    private void cargarDatos() {
        modelo.setRowCount(0);
        var resultado = controller.listarFranjas();
        if (resultado.esError()) {
            UIFactory.showError(this, resultado.error());
            lista = List.of();
            return;
        }
        lista = resultado.datos();
        lista.forEach(ts -> modelo.addRow(new Object[]{
            ts.getNombre(),
            ts.getHoraInicio().toString(),
            ts.getHoraFin().toString(),
            ts.getOrden()
        }));
    }

    private void abrirForm(TimeSlot slot) {
        Window w = SwingUtilities.getWindowAncestor(this);
        Frame f  = w instanceof Frame ? (Frame) w : null;
        JDialog dlg = new JDialog(f, slot == null ? "Nueva franja" : "Editar franja", true);

        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));
        root.setBackground(AppColors.BG_WHITE);
        dlg.setContentPane(root);

        root.add(UIFactory.sectionTitle(slot == null ? "Nueva franja horaria" : "Editar franja"), BorderLayout.NORTH);

        JTextField txtNombre  = UIFactory.textField(20);
        JTextField txtHoraIni = UIFactory.textField(8);
        JTextField txtHoraFin = UIFactory.textField(8);
        JSpinner   spinOrden  = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));

        if (slot != null) {
            txtNombre.setText(slot.getNombre());
            txtHoraIni.setText(slot.getHoraInicio().toString());
            txtHoraFin.setText(slot.getHoraFin().toString());
            spinOrden.setValue(slot.getOrden());
        }

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(AppColors.BG_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridx = 0; gbc.weightx = 1.0;

        gbc.gridy = 0; form.add(UIFactory.formLabel("Nombre * (ej: 1ª hora, Recreo)"), gbc);
        gbc.gridy = 1; form.add(txtNombre, gbc);
        gbc.gridy = 2; form.add(UIFactory.formLabel("Hora inicio * (formato HH:mm, ej: 08:00)"), gbc);
        gbc.gridy = 3; form.add(txtHoraIni, gbc);
        gbc.gridy = 4; form.add(UIFactory.formLabel("Hora fin * (formato HH:mm, ej: 09:00)"), gbc);
        gbc.gridy = 5; form.add(txtHoraFin, gbc);
        gbc.gridy = 6; form.add(UIFactory.formLabel("Orden de visualización"), gbc);
        gbc.gridy = 7; form.add(spinOrden, gbc);

        root.add(form, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);
        JButton btnCancel = UIFactory.secondaryButton("Cancelar");
        JButton btnOk     = UIFactory.primaryButton("Guardar");
        btnCancel.addActionListener(e -> dlg.dispose());
        btnOk.addActionListener(e -> {
            var iniResult = controller.parsearHora(txtHoraIni.getText());
            if (iniResult.esError()) { UIFactory.showError(dlg, iniResult.error()); return; }
            var finResult = controller.parsearHora(txtHoraFin.getText());
            if (finResult.esError()) { UIFactory.showError(dlg, finResult.error()); return; }

            LocalTime ini = iniResult.datos();
            LocalTime fin = finResult.datos();
            int orden = (int) spinOrden.getValue();

            var error = (slot == null)
                ? controller.crear(txtNombre.getText(), ini, fin, orden)
                : controller.actualizar(slot, txtNombre.getText(), ini, fin, orden);

            if (error.isPresent()) {
                UIFactory.showError(dlg, error.get());
                return;
            }
            dlg.dispose();
            cargarDatos();
        });
        btns.add(btnCancel); btns.add(btnOk);
        root.add(btns, BorderLayout.SOUTH);

        dlg.pack();
        Dimension tamMin = new Dimension(480, 520);
        if (dlg.getWidth() < tamMin.width || dlg.getHeight() < tamMin.height) {
            dlg.setSize(tamMin);
        }
        dlg.setMinimumSize(tamMin);
        dlg.setLocationRelativeTo(f);
        dlg.setVisible(true);
    }

    private void eliminar() {
        int row = tabla.getSelectedRow();
        if (row < 0) { UIFactory.showError(this, "Selecciona una franja."); return; }
        TimeSlot ts = lista.get(row);
        if (UIFactory.showConfirm(this, "¿Eliminar la franja '" + ts.getNombre() + "'?\n" +
                "Se eliminarán también las reservas asociadas.")) {
            controller.eliminar(ts.getId()).ifPresentOrElse(
                msg -> UIFactory.showError(this, msg),
                this::cargarDatos
            );
        }
    }
}
