package com.aulateca.view.panels;

import com.aulateca.controller.ResourceController;
import com.aulateca.model.*;
import com.aulateca.view.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;

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
        var tiposResult = controller.listarTipos();
        var estadosResult = controller.listarEstados();
        if (tiposResult.esError() || estadosResult.esError()) {
            UIFactory.showError(this, "No se pudieron cargar tipos y estados.");
            return;
        }
    
        List<ResourceType> tipos = tiposResult.datos();
        List<ResourceStatus> estados = estadosResult.datos();
        if (tipos.isEmpty()) {
            UIFactory.showError(this, "No hay tipos de recurso configurados.");
            return;
        }
    
        boolean esNuevo = recurso == null;
        Window w = SwingUtilities.getWindowAncestor(this);
        Frame frame = w instanceof Frame ? (Frame) w : null;
    
        JDialog dlg = new JDialog(frame, esNuevo ? "Nuevo recurso" : "Editar recurso", true);
        dlg.setResizable(true);
    
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(AppColors.BG_WHITE);
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 20, 28));
        dlg.setContentPane(root);
    
        root.add(UIFactory.sectionTitle(esNuevo ? "Nuevo recurso" : "Editar recurso"), BorderLayout.NORTH);
    
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(AppColors.BG_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 0, 5, 0);
    
        JComboBox<ResourceType> cmbTipo = comboFormulario();
        JTextField txtNombre = UIFactory.textField(32);
        JComboBox<ResourceStatus> cmbEstado = comboFormulario();
    
        JTextArea txtComentarios = new JTextArea();
        txtComentarios.setLineWrap(true);
        txtComentarios.setWrapStyleWord(true);
        
        JScrollPane panelComentarios = new JScrollPane(txtComentarios);
        panelComentarios.setPreferredSize(new Dimension(0, 250));
        panelComentarios.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panelComentarios.setBorder(BorderFactory.createLineBorder(AppColors.BORDER));
    
        tipos.forEach(cmbTipo::addItem);
        estados.forEach(cmbEstado::addItem);
    
        int row = 0;
        if (esNuevo) {
            agregarFilaConPeso(form, gbc, row++, "Tipo *", cmbTipo, 0);
            agregarFilaConPeso(form, gbc, row++, "Nombre *", txtNombre, 0);
        } else {
            agregarFilaConPeso(form, gbc, row++, "Tipo", campoSoloLectura(recurso.getTipo().getNombre()), 0);
            agregarFilaConPeso(form, gbc, row++, "Nombre", campoSoloLectura(recurso.getNombre()), 0);
            cmbEstado.setSelectedItem(recurso.getEstado());
            txtComentarios.setText(recurso.getDescripcion() != null ? recurso.getDescripcion() : "");
        }
    
        agregarFilaConPeso(form, gbc, row++, "Estado *", cmbEstado, 0);
        agregarFilaConPeso(form, gbc, row, "Comentarios", panelComentarios, 1.0);
    
        root.add(form, BorderLayout.CENTER);
    
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);
        btns.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        JButton btnCancel = UIFactory.secondaryButton("Cancelar");
        JButton btnOk = UIFactory.primaryButton("Guardar");
        btnCancel.addActionListener(e -> dlg.dispose());
        btnOk.addActionListener(e -> {
            String comentarios = txtComentarios.getText().trim();
            ResourceStatus estado = (ResourceStatus) cmbEstado.getSelectedItem();
            if (estado == null) {
                UIFactory.showError(dlg, "Selecciona un estado.");
                return;
            }
    
            Optional<String> error;
            if (esNuevo) {
                ResourceType tipo = (ResourceType) cmbTipo.getSelectedItem();
                String nombre = txtNombre.getText().trim();
                if (tipo == null || nombre.isBlank()) {
                    UIFactory.showError(dlg, "Indica el tipo y el nombre del recurso.");
                    return;
                }
                error = controller.crear(nombre, comentarios, tipo, estado, null);
            } else {
                error = controller.actualizar(recurso, recurso.getNombre(), comentarios,
                    recurso.getTipo(), estado, recurso.getUbicacion());
            }
    
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
        Dimension tamMin = new Dimension(600, 600);
        dlg.setSize(tamMin);
        dlg.setMinimumSize(tamMin);
        dlg.setLocationRelativeTo(frame);
        dlg.setVisible(true);
    }
    
    private void agregarFilaConPeso(JPanel form, GridBagConstraints gbc, int row, String etiqueta, Component campo, double weighty) {
        gbc.gridy = row * 2;
        gbc.weighty = 0;
        form.add(UIFactory.formLabel(etiqueta), gbc);
        gbc.gridy = row * 2 + 1;
        gbc.weighty = weighty;
        form.add(campo, gbc);
    }

    private <T> JComboBox<T> comboFormulario() {
        JComboBox<T> cb = UIFactory.comboBox();
        cb.setPreferredSize(new Dimension(0, 40));
        return cb;
    }

    private JTextField campoSoloLectura(String valor) {
        JTextField tf = UIFactory.textField(32);
        tf.setText(valor);
        tf.setEditable(false);
        tf.setFocusable(false);
        tf.setBackground(AppColors.BG_APP);
        tf.setCaretPosition(0);
        return tf;
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
