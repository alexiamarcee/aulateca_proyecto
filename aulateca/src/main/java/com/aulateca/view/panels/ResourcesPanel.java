package com.aulateca.view.panels;

import com.aulateca.controller.ResourceController;
import com.aulateca.model.*;
import com.aulateca.util.ResourceCatalog;
import com.aulateca.view.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        top.add(UIFactory.sectionTitle("Gestión de recursos"), BorderLayout.WEST);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        toolbar.setOpaque(false);
        JButton btnNuevo  = UIFactory.accentButton("Nuevo recurso");
        JButton btnEditar = UIFactory.primaryButton("Editar");
        JButton btnEliminar = UIFactory.dangerButton("Eliminar");
        JButton btnActualizar = UIFactory.secondaryButton("↻");

        btnNuevo.addActionListener(e -> {
            if (!hayRecursosDisponiblesEnCatalogo()) {
                UIFactory.showError(this, "Todos los recursos del catálogo ya están registrados.");
                return;
            }
            abrirFormulario(null);
        });
        btnEditar.addActionListener(e -> editarSeleccionado());
        btnEliminar.addActionListener(e -> eliminarSeleccionado());
        btnActualizar.addActionListener(e -> cargarDatos());

        toolbar.add(btnActualizar);
        toolbar.add(btnEliminar);
        toolbar.add(btnEditar);
        toolbar.add(btnNuevo);
        top.add(toolbar, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        String[] cols = {"Nombre", "Tipo", "Estado", "Comentarios"};
        modelo = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        UIFactory.styleTable(tabla);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(220);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(280);

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
                r.getNombre(),
                r.getTipo().getNombre(),
                r.getEstado().getNombre(),
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

        List<ResourceType> tiposCatalogo = tiposResult.datos().stream()
            .filter(t -> ResourceCatalog.tieneCatalogo(t.getNombre()))
            .collect(Collectors.toList());
        if (tiposCatalogo.isEmpty()) {
            UIFactory.showError(this, "No hay tipos de recurso configurados en el catálogo.");
            return;
        }
        List<ResourceStatus> estados = estadosResult.datos();

        Window w = SwingUtilities.getWindowAncestor(this);
        Frame frame = w instanceof Frame ? (Frame) w : null;

        JDialog dlg = new JDialog(frame,
            recurso == null ? "Nuevo recurso" : "Editar recurso", true);

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

        JComboBox<ResourceType>   cmbTipo       = UIFactory.comboBox();
        JComboBox<String>         cmbNombre     = UIFactory.comboBox();
        JComboBox<ResourceStatus> cmbEstado     = UIFactory.comboBox();
        JTextArea                 txtComentarios = new JTextArea(3, 20);

        tiposCatalogo.forEach(cmbTipo::addItem);
        estados.forEach(cmbEstado::addItem);

        Runnable actualizarNombres = () -> {
            cmbNombre.removeAllItems();
            ResourceType tipo = (ResourceType) cmbTipo.getSelectedItem();
            if (tipo == null) return;

            Set<String> nombresUsados = nombresOcupados(recurso);
            for (ResourceCatalog.Opcion opcion : ResourceCatalog.opcionesPorTipo(tipo.getNombre())) {
                boolean esActual = recurso != null && opcion.nombre().equals(recurso.getNombre());
                if (esActual || !nombresUsados.contains(opcion.nombre())) {
                    cmbNombre.addItem(opcion.nombre());
                }
            }

            if (recurso != null && cmbNombre.getItemCount() == 0) {
                cmbNombre.addItem(recurso.getNombre());
            } else if (recurso != null) {
                cmbNombre.setSelectedItem(recurso.getNombre());
            }
        };

        cmbTipo.addActionListener(e -> actualizarNombres.run());

        if (recurso != null) {
            cmbTipo.setSelectedItem(recurso.getTipo());
            cmbEstado.setSelectedItem(recurso.getEstado());
            txtComentarios.setText(recurso.getDescripcion() != null ? recurso.getDescripcion() : "");
        }
        actualizarNombres.run();

        gbc.gridy = 0; form.add(UIFactory.formLabel("Tipo *"), gbc);
        gbc.gridy = 1; form.add(cmbTipo, gbc);
        gbc.gridy = 2; form.add(UIFactory.formLabel("Nombre *"), gbc);
        gbc.gridy = 3; form.add(cmbNombre, gbc);
        gbc.gridy = 4; form.add(UIFactory.formLabel("Estado *"), gbc);
        gbc.gridy = 5; form.add(cmbEstado, gbc);
        gbc.gridy = 6; form.add(UIFactory.formLabel("Comentarios"), gbc);
        gbc.gridy = 7; form.add(UIFactory.textArea(txtComentarios), gbc);

        root.add(form, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);
        JButton btnCancel = UIFactory.secondaryButton("Cancelar");
        JButton btnOk     = UIFactory.primaryButton("Guardar");
        btnCancel.addActionListener(e -> dlg.dispose());
        btnOk.addActionListener(e -> {
            ResourceType tipo = (ResourceType) cmbTipo.getSelectedItem();
            String nombre = (String) cmbNombre.getSelectedItem();
            if (tipo == null || nombre == null || nombre.isBlank()) {
                UIFactory.showError(dlg, "Selecciona un tipo y un nombre de recurso.");
                return;
            }

            String comentarios = txtComentarios.getText().trim();
            String ubicacion = ResourceCatalog.buscarUbicacion(tipo.getNombre(), nombre)
                .orElse(recurso != null ? recurso.getUbicacion() : null);

            var error = (recurso == null)
                ? controller.crear(nombre, comentarios, tipo,
                    (ResourceStatus) cmbEstado.getSelectedItem(), ubicacion)
                : controller.actualizar(recurso, nombre, comentarios, tipo,
                    (ResourceStatus) cmbEstado.getSelectedItem(), ubicacion);

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

        dlg.pack();
        Dimension tamMin = new Dimension(500, 480);
        if (dlg.getWidth() < tamMin.width || dlg.getHeight() < tamMin.height) {
            dlg.setSize(tamMin);
        }
        dlg.setMinimumSize(tamMin);
        dlg.setLocationRelativeTo(frame);
        dlg.setVisible(true);
    }

    private boolean hayRecursosDisponiblesEnCatalogo() {
        return ResourceCatalog.quedanOpcionesDisponibles(nombresOcupados(null));
    }

    private Set<String> nombresOcupados(Resource recursoEnEdicion) {
        Set<String> usados = new HashSet<>();
        for (Resource r : listaActual) {
            if (recursoEnEdicion == null || !r.getId().equals(recursoEnEdicion.getId())) {
                usados.add(r.getNombre());
            }
        }
        return usados;
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
