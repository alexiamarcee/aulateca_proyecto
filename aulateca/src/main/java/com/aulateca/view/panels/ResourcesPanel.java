package com.aulateca.view.panels;

import com.aulateca.controller.ResourceController;
import com.aulateca.model.*;
import com.aulateca.view.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/** CRUD de recursos (capa Vista). */
public class ResourcesPanel extends JPanel {

    private final ResourceController controller = new ResourceController();

    private JTable tabla;
    private DefaultTableModel modelo;
    private List<Resource> listaActual;

    public ResourcesPanel() {
        setBackground(AppColors.BG_LIGHT);
        setLayout(new BorderLayout(0, 16));
        initUI();
        cargarDatos();
    }

    private void initUI() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(UIFactory.sectionTitle("🏫  Gestión de Recursos"), BorderLayout.WEST);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        toolbar.setOpaque(false);
        JButton btnNuevo  = UIFactory.accentButton("+ Nuevo recurso");
        JButton btnEditar = UIFactory.primaryButton("✏ Editar");
        JButton btnEliminar = UIFactory.dangerButton("🗑 Eliminar");
        JButton btnActualizar = UIFactory.secondaryButton("↻");

        btnNuevo.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> editarSeleccionado());
        btnEliminar.addActionListener(e -> eliminarSeleccionado());
        btnActualizar.addActionListener(e -> cargarDatos());

        toolbar.add(btnActualizar);
        toolbar.add(btnEliminar);
        toolbar.add(btnEditar);
        toolbar.add(btnNuevo);
        top.add(toolbar, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        String[] cols = {"ID", "Nombre", "Tipo", "Estado", "Ubicación", "Descripción"};
        modelo = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        UIFactory.styleTable(tabla);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(120);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(AppColors.BORDER));
        add(scroll, BorderLayout.CENTER);
    }

    private void cargarDatos() {
        modelo.setRowCount(0);
        var resultado = controller.listarRecursos();
        if (resultado.esError()) {
            UIFactory.showError(this, resultado.error());
            listaActual = List.of();
            return;
        }
        listaActual = resultado.datos();
        for (Resource r : listaActual) {
            modelo.addRow(new Object[]{
                r.getId(), r.getNombre(),
                r.getTipo().getNombre(),
                r.getEstado().getNombre(),
                r.getUbicacion() != null ? r.getUbicacion() : "—",
                r.getDescripcion() != null ? r.getDescripcion() : "—"
            });
        }
    }

    private void abrirFormulario(Resource recurso) {
        var tiposResult   = controller.listarTipos();
        var estadosResult = controller.listarEstados();
        if (tiposResult.esError() || estadosResult.esError()) {
            UIFactory.showError(this, "No se pudieron cargar tipos y estados.");
            return;
        }
        List<ResourceType>   tipos   = tiposResult.datos();
        List<ResourceStatus> estados = estadosResult.datos();

        Window w = SwingUtilities.getWindowAncestor(this);
        Frame frame = w instanceof Frame ? (Frame) w : null;

        JDialog dlg = new JDialog(frame,
            recurso == null ? "Nuevo recurso" : "Editar recurso", true);
        dlg.setSize(480, 520);
        dlg.setLocationRelativeTo(frame);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppColors.BG_WHITE);
        root.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));
        dlg.setContentPane(root);

        root.add(UIFactory.sectionTitle(recurso == null ?
            "Nuevo recurso" : "Editar: " + recurso.getNombre()), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(AppColors.BG_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridx = 0; gbc.weightx = 1.0;

        JTextField txtNombre      = UIFactory.textField(20);
        JTextArea  txtDescripcion = new JTextArea(3, 20);
        JTextField txtUbicacion   = UIFactory.textField(20);
        JComboBox<ResourceType>   cmbTipo   = UIFactory.comboBox();
        JComboBox<ResourceStatus> cmbEstado = UIFactory.comboBox();
        tipos.forEach(cmbTipo::addItem);
        estados.forEach(cmbEstado::addItem);

        if (recurso != null) {
            txtNombre.setText(recurso.getNombre());
            txtDescripcion.setText(recurso.getDescripcion() != null ? recurso.getDescripcion() : "");
            txtUbicacion.setText(recurso.getUbicacion() != null ? recurso.getUbicacion() : "");
            cmbTipo.setSelectedItem(recurso.getTipo());
            cmbEstado.setSelectedItem(recurso.getEstado());
        }

        gbc.gridy = 0; form.add(UIFactory.formLabel("Nombre *"), gbc);
        gbc.gridy = 1; form.add(txtNombre, gbc);
        gbc.gridy = 2; form.add(UIFactory.formLabel("Tipo *"), gbc);
        gbc.gridy = 3; form.add(cmbTipo, gbc);
        gbc.gridy = 4; form.add(UIFactory.formLabel("Estado *"), gbc);
        gbc.gridy = 5; form.add(cmbEstado, gbc);
        gbc.gridy = 6; form.add(UIFactory.formLabel("Ubicación"), gbc);
        gbc.gridy = 7; form.add(txtUbicacion, gbc);
        gbc.gridy = 8; form.add(UIFactory.formLabel("Descripción"), gbc);
        gbc.gridy = 9; form.add(UIFactory.textArea(txtDescripcion), gbc);

        root.add(form, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);
        JButton btnCancel = UIFactory.secondaryButton("Cancelar");
        JButton btnOk     = UIFactory.primaryButton("Guardar");
        btnCancel.addActionListener(e -> dlg.dispose());
        btnOk.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String desc   = txtDescripcion.getText().trim();
            String ubi    = txtUbicacion.getText().trim();
            var error = (recurso == null)
                ? controller.crear(nombre, desc,
                    (ResourceType) cmbTipo.getSelectedItem(),
                    (ResourceStatus) cmbEstado.getSelectedItem(), ubi)
                : controller.actualizar(recurso, nombre, desc,
                    (ResourceType) cmbTipo.getSelectedItem(),
                    (ResourceStatus) cmbEstado.getSelectedItem(), ubi);

            if (error.isPresent()) {
                UIFactory.showError(dlg, error.get());
                return;
            }
            UIFactory.showSuccess(dlg, "Recurso guardado correctamente.");
            dlg.dispose();
            cargarDatos();
        });
        btns.add(btnCancel);
        btns.add(btnOk);
        root.add(btns, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void editarSeleccionado() {
        int row = tabla.getSelectedRow();
        if (row < 0) { UIFactory.showError(this, "Selecciona un recurso."); return; }
        abrirFormulario(listaActual.get(row));
    }

    private void eliminarSeleccionado() {
        int row = tabla.getSelectedRow();
        if (row < 0) { UIFactory.showError(this, "Selecciona un recurso."); return; }
        Resource r = listaActual.get(row);
        if (UIFactory.showConfirm(this, "¿Eliminar el recurso '" + r.getNombre() + "'?\n" +
            "Se eliminarán también todas sus reservas.")) {
            controller.eliminar(r.getId()).ifPresentOrElse(
                msg -> UIFactory.showError(this, msg),
                this::cargarDatos
            );
        }
    }
}
