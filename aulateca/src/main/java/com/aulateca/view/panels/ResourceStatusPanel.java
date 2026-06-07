package com.aulateca.view.panels;

import com.aulateca.controller.ResourceStatusController;
import com.aulateca.model.ResourceStatus;
import com.aulateca.view.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/** CRUD de estados de recursos (capa Vista). */
public class ResourceStatusPanel extends JPanel {

    private final ResourceStatusController controller = new ResourceStatusController();
    private JTable tabla;
    private DefaultTableModel modelo;
    private List<ResourceStatus> lista;

    public ResourceStatusPanel() {
        setBackground(AppColors.BG_LIGHT);
        setLayout(new BorderLayout(0, 16));
        initUI();
        cargarDatos();
    }

    private void initUI() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(UIFactory.sectionTitle("🔴  Estados de Recursos"), BorderLayout.WEST);

        JPanel tb = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tb.setOpaque(false);
        JButton btnNuevo   = UIFactory.accentButton("+ Nuevo estado");
        JButton btnEditar  = UIFactory.primaryButton("✏ Editar");
        JButton btnEliminar = UIFactory.dangerButton("🗑 Eliminar");
        btnNuevo.addActionListener(e -> abrirForm(null));
        btnEditar.addActionListener(e -> { int r = tabla.getSelectedRow(); if (r >= 0) abrirForm(lista.get(r)); });
        btnEliminar.addActionListener(e -> eliminar());
        tb.add(btnEliminar); tb.add(btnEditar); tb.add(btnNuevo);
        top.add(tb, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new String[]{"ID", "Nombre", "Descripción", "¿Reservable?"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        UIFactory.styleTable(tabla);
        JScrollPane sp = new JScrollPane(tabla);
        sp.setBorder(BorderFactory.createLineBorder(AppColors.BORDER));
        add(sp, BorderLayout.CENTER);
    }

    private void cargarDatos() {
        modelo.setRowCount(0);
        var resultado = controller.listarEstados();
        if (resultado.esError()) {
            UIFactory.showError(this, resultado.error());
            lista = List.of();
            return;
        }
        lista = resultado.datos();
        lista.forEach(s -> modelo.addRow(new Object[]{
            s.getId(), s.getNombre(),
            s.getDescripcion() != null ? s.getDescripcion() : "—",
            s.isReservable() ? "Sí" : "No"
        }));
    }

    private void abrirForm(ResourceStatus estado) {
        Window w = SwingUtilities.getWindowAncestor(this);
        Frame f  = w instanceof Frame ? (Frame) w : null;
        JDialog dlg = new JDialog(f, estado == null ? "Nuevo estado" : "Editar estado", true);
        dlg.setSize(400, 320);
        dlg.setLocationRelativeTo(f);

        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));
        root.setBackground(AppColors.BG_WHITE);
        dlg.setContentPane(root);

        JTextField txtNombre = UIFactory.textField(20);
        JTextField txtDesc   = UIFactory.textField(20);
        JCheckBox  chkRes    = new JCheckBox("Permite reservar recursos en este estado");
        chkRes.setFont(UIFactory.FONT_BODY);
        chkRes.setOpaque(false);

        if (estado != null) {
            txtNombre.setText(estado.getNombre());
            txtDesc.setText(estado.getDescripcion() != null ? estado.getDescripcion() : "");
            chkRes.setSelected(estado.isReservable());
        } else {
            chkRes.setSelected(true);
        }

        JPanel form = new JPanel(new GridLayout(5, 1, 0, 8));
        form.setOpaque(false);
        form.add(UIFactory.formLabel("Nombre *")); form.add(txtNombre);
        form.add(UIFactory.formLabel("Descripción")); form.add(txtDesc);
        form.add(chkRes);
        root.add(form, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);
        JButton btnCancel = UIFactory.secondaryButton("Cancelar");
        JButton btnOk     = UIFactory.primaryButton("Guardar");
        btnCancel.addActionListener(e -> dlg.dispose());
        btnOk.addActionListener(e -> {
            var error = (estado == null)
                ? controller.crear(txtNombre.getText(), txtDesc.getText(), chkRes.isSelected())
                : controller.actualizar(estado, txtNombre.getText(), txtDesc.getText(), chkRes.isSelected());
            if (error.isPresent()) {
                UIFactory.showError(dlg, error.get());
                return;
            }
            dlg.dispose();
            cargarDatos();
        });
        btns.add(btnCancel); btns.add(btnOk);
        root.add(btns, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void eliminar() {
        int row = tabla.getSelectedRow();
        if (row < 0) { UIFactory.showError(this, "Selecciona un estado."); return; }
        ResourceStatus s = lista.get(row);
        if (UIFactory.showConfirm(this, "¿Eliminar estado '" + s.getNombre() + "'?")) {
            controller.eliminar(s.getId()).ifPresentOrElse(
                msg -> UIFactory.showError(this, msg),
                this::cargarDatos
            );
        }
    }
}
