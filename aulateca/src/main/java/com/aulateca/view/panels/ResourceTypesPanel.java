package com.aulateca.view.panels;

import com.aulateca.dao.ResourceTypeDAO;
import com.aulateca.model.ResourceType;
import com.aulateca.view.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/** CRUD de tipos de recursos. */
public class ResourceTypesPanel extends JPanel {

    private final ResourceTypeDAO dao = new ResourceTypeDAO();
    private JTable tabla;
    private DefaultTableModel modelo;
    private List<ResourceType> lista;

    public ResourceTypesPanel() {
        setBackground(AppColors.BG_LIGHT);
        setLayout(new BorderLayout(0, 16));
        initUI();
        cargarDatos();
    }

    private void initUI() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(UIFactory.sectionTitle("📁  Tipos de Recurso"), BorderLayout.WEST);

        JPanel tb = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tb.setOpaque(false);
        JButton btnNuevo   = UIFactory.accentButton("+ Nuevo tipo");
        JButton btnEditar  = UIFactory.primaryButton("✏ Editar");
        JButton btnEliminar = UIFactory.dangerButton("🗑 Eliminar");
        btnNuevo.addActionListener(e -> abrirForm(null));
        btnEditar.addActionListener(e -> { int r = tabla.getSelectedRow(); if (r >= 0) abrirForm(lista.get(r)); });
        btnEliminar.addActionListener(e -> eliminar());
        tb.add(btnEliminar); tb.add(btnEditar); tb.add(btnNuevo);
        top.add(tb, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new String[]{"ID", "Nombre", "Descripción"}, 0) {
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
        lista = dao.buscarTodos();
        lista.forEach(t -> modelo.addRow(new Object[]{
            t.getId(), t.getNombre(),
            t.getDescripcion() != null ? t.getDescripcion() : "—"
        }));
    }

    private void abrirForm(ResourceType tipo) {
        Window w = SwingUtilities.getWindowAncestor(this);
        Frame f = w instanceof Frame ? (Frame) w : null;
        JDialog dlg = new JDialog(f, tipo == null ? "Nuevo tipo" : "Editar tipo", true);
        dlg.setSize(400, 260);
        dlg.setLocationRelativeTo(f);

        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));
        root.setBackground(AppColors.BG_WHITE);
        dlg.setContentPane(root);

        JTextField txtNombre = UIFactory.textField(20);
        JTextField txtDesc   = UIFactory.textField(20);
        if (tipo != null) {
            txtNombre.setText(tipo.getNombre());
            txtDesc.setText(tipo.getDescripcion() != null ? tipo.getDescripcion() : "");
        }

        JPanel form = new JPanel(new GridLayout(4, 1, 0, 8));
        form.setOpaque(false);
        form.add(UIFactory.formLabel("Nombre *")); form.add(txtNombre);
        form.add(UIFactory.formLabel("Descripción")); form.add(txtDesc);
        root.add(form, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);
        JButton btnCancel = UIFactory.secondaryButton("Cancelar");
        JButton btnOk     = UIFactory.primaryButton("Guardar");
        btnCancel.addActionListener(e -> dlg.dispose());
        btnOk.addActionListener(e -> {
            String n = txtNombre.getText().trim();
            if (n.isEmpty()) { UIFactory.showError(dlg, "El nombre es obligatorio."); return; }
            try {
                if (tipo == null) {
                    dao.guardar(new ResourceType(n, txtDesc.getText().trim().isEmpty() ? null : txtDesc.getText().trim()));
                } else {
                    tipo.setNombre(n);
                    tipo.setDescripcion(txtDesc.getText().trim().isEmpty() ? null : txtDesc.getText().trim());
                    dao.actualizar(tipo);
                }
                dlg.dispose(); cargarDatos();
            } catch (Exception ex) { UIFactory.showError(dlg, ex.getMessage()); }
        });
        btns.add(btnCancel); btns.add(btnOk);
        root.add(btns, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void eliminar() {
        int row = tabla.getSelectedRow();
        if (row < 0) { UIFactory.showError(this, "Selecciona un tipo."); return; }
        ResourceType t = lista.get(row);
        if (UIFactory.showConfirm(this, "¿Eliminar tipo '" + t.getNombre() + "'?")) {
            try { dao.eliminar(t.getId()); cargarDatos(); }
            catch (Exception ex) { UIFactory.showError(this, "No se puede eliminar: " + ex.getMessage()); }
        }
    }
}
