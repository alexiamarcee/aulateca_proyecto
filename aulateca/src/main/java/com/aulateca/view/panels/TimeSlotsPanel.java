package com.aulateca.view.panels;

import com.aulateca.dao.TimeSlotDAO;
import com.aulateca.model.TimeSlot;
import com.aulateca.view.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalTime;
import java.util.List;

/** CRUD de franjas horarias. */
public class TimeSlotsPanel extends JPanel {

    private final TimeSlotDAO dao = new TimeSlotDAO();
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
        top.add(UIFactory.sectionTitle("⏰  Franjas Horarias"), BorderLayout.WEST);

        JPanel tb = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tb.setOpaque(false);
        JButton btnNuevo    = UIFactory.accentButton("+ Nueva franja");
        JButton btnEditar   = UIFactory.primaryButton("✏ Editar");
        JButton btnEliminar = UIFactory.dangerButton("🗑 Eliminar");
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
                new String[]{"ID", "Nombre", "Hora inicio", "Hora fin", "Orden"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        UIFactory.styleTable(tabla);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(160);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(60);

        JScrollPane sp = new JScrollPane(tabla);
        sp.setBorder(BorderFactory.createLineBorder(AppColors.BORDER));
        add(sp, BorderLayout.CENTER);

        JLabel hint = new JLabel("  El orden determina la secuencia en la que se muestran las franjas (menor número = primera).");
        hint.setFont(UIFactory.FONT_SMALL);
        hint.setForeground(AppColors.TEXT_GRAY);
        add(hint, BorderLayout.SOUTH);
    }

    private void cargarDatos() {
        modelo.setRowCount(0);
        lista = dao.buscarTodos();
        lista.forEach(ts -> modelo.addRow(new Object[]{
            ts.getId(), ts.getNombre(),
            ts.getHoraInicio().toString(),
            ts.getHoraFin().toString(),
            ts.getOrden()
        }));
    }

    private void abrirForm(TimeSlot slot) {
        Window w = SwingUtilities.getWindowAncestor(this);
        Frame f  = w instanceof Frame ? (Frame) w : null;
        JDialog dlg = new JDialog(f, slot == null ? "Nueva franja" : "Editar franja", true);
        dlg.setSize(420, 360);
        dlg.setLocationRelativeTo(f);

        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));
        root.setBackground(AppColors.BG_WHITE);
        dlg.setContentPane(root);

        root.add(UIFactory.sectionTitle(slot == null ? "Nueva franja horaria" : "Editar franja"), BorderLayout.NORTH);

        JTextField txtNombre    = UIFactory.textField(20);
        JTextField txtHoraIni   = UIFactory.textField(8);
        JTextField txtHoraFin   = UIFactory.textField(8);
        JSpinner   spinOrden    = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));

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
            String nombre = txtNombre.getText().trim();
            String iniStr = txtHoraIni.getText().trim();
            String finStr = txtHoraFin.getText().trim();
            if (nombre.isEmpty() || iniStr.isEmpty() || finStr.isEmpty()) {
                UIFactory.showError(dlg, "Nombre, hora de inicio y hora de fin son obligatorios.");
                return;
            }
            LocalTime ini, fin;
            try {
                ini = LocalTime.parse(iniStr);
                fin = LocalTime.parse(finStr);
            } catch (Exception ex) {
                UIFactory.showError(dlg, "Formato de hora incorrecto. Usa HH:mm (ej: 08:30).");
                return;
            }
            if (!fin.isAfter(ini)) {
                UIFactory.showError(dlg, "La hora de fin debe ser posterior a la hora de inicio.");
                return;
            }
            try {
                int orden = (int) spinOrden.getValue();
                if (slot == null) {
                    dao.guardar(new TimeSlot(nombre, ini, fin, orden));
                } else {
                    slot.setNombre(nombre);
                    slot.setHoraInicio(ini);
                    slot.setHoraFin(fin);
                    slot.setOrden(orden);
                    dao.actualizar(slot);
                }
                dlg.dispose();
                cargarDatos();
            } catch (Exception ex) {
                UIFactory.showError(dlg, "Error: " + ex.getMessage());
            }
        });
        btns.add(btnCancel); btns.add(btnOk);
        root.add(btns, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void eliminar() {
        int row = tabla.getSelectedRow();
        if (row < 0) { UIFactory.showError(this, "Selecciona una franja."); return; }
        TimeSlot ts = lista.get(row);
        if (UIFactory.showConfirm(this, "¿Eliminar la franja '" + ts.getNombre() + "'?\n" +
                "Se eliminarán también las reservas asociadas.")) {
            try {
                dao.eliminar(ts.getId());
                cargarDatos();
            } catch (Exception ex) {
                UIFactory.showError(this, "No se puede eliminar: " + ex.getMessage());
            }
        }
    }
}
