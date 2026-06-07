package com.aulateca.view.panels;

import com.aulateca.controller.UserController;
import com.aulateca.model.User;
import com.aulateca.view.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/** CRUD de usuarios (capa Vista, solo admin). */
public class UsersPanel extends JPanel {

    private final UserController controller = new UserController();
    private JTable tabla;
    private DefaultTableModel modelo;
    private List<User> lista;

    public UsersPanel() {
        setBackground(AppColors.BG_LIGHT);
        setLayout(new BorderLayout(0, 16));
        initUI();
        cargarDatos();
    }

    private void initUI() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(UIFactory.sectionTitle("Gestión de usuarios"), BorderLayout.WEST);

        JPanel tb = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tb.setOpaque(false);
        JButton btnNuevo    = UIFactory.accentButton("Nuevo usuario");
        JButton btnEditar   = UIFactory.primaryButton("Editar");
        JButton btnDesactivar = UIFactory.dangerButton("Desactivar");
        JButton btnRefrescar  = UIFactory.secondaryButton("↻");

        btnNuevo.addActionListener(e -> abrirForm(null));
        btnEditar.addActionListener(e -> {
            int r = tabla.getSelectedRow();
            if (r >= 0) abrirForm(lista.get(r));
            else UIFactory.showError(this, "Selecciona un usuario.");
        });
        btnDesactivar.addActionListener(e -> desactivar());
        btnRefrescar.addActionListener(e -> cargarDatos());

        tb.add(btnRefrescar); tb.add(btnDesactivar); tb.add(btnEditar); tb.add(btnNuevo);
        top.add(tb, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        modelo = new DefaultTableModel(
                new String[]{"Nombre completo", "Email", "Rol", "Activo"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        UIFactory.styleTable(tabla);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(220);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(60);

        JScrollPane sp = new JScrollPane(tabla);
        sp.setBorder(BorderFactory.createLineBorder(AppColors.BORDER));
        add(sp, BorderLayout.CENTER);
    }

    private void cargarDatos() {
        modelo.setRowCount(0);
        var resultado = controller.listarUsuarios();
        if (resultado.esError()) {
            UIFactory.showError(this, resultado.error());
            lista = List.of();
            return;
        }
        lista = resultado.datos();
        lista.forEach(u -> modelo.addRow(new Object[]{
            u.getNombreCompleto(), u.getEmail(),
            u.getRol().name(), u.isActivo() ? "Sí" : "No"
        }));
    }

    private void abrirForm(User usuario) {
        Window w = SwingUtilities.getWindowAncestor(this);
        Frame f  = w instanceof Frame ? (Frame) w : null;
        boolean esNuevo = usuario == null;

        JDialog dlg = new JDialog(f, esNuevo ? "Nuevo usuario" : "Editar usuario", true);

        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));
        root.setBackground(AppColors.BG_WHITE);
        dlg.setContentPane(root);

        root.add(UIFactory.sectionTitle(esNuevo ? "Nuevo usuario" : "Editar usuario"), BorderLayout.NORTH);

        JTextField      txtNombre    = UIFactory.textField(20);
        JTextField      txtApellidos = UIFactory.textField(20);
        JTextField      txtEmail     = UIFactory.textField(20);
        JPasswordField  txtPassword  = UIFactory.passwordField(20);
        JComboBox<User.Rol> cmbRol   = new JComboBox<>(User.Rol.values());
        JCheckBox       chkActivo    = new JCheckBox("Usuario activo");
        chkActivo.setFont(UIFactory.FONT_BODY);
        chkActivo.setOpaque(false);
        chkActivo.setSelected(true);

        if (!esNuevo) {
            txtNombre.setText(usuario.getNombre());
            txtApellidos.setText(usuario.getApellidos());
            txtEmail.setText(usuario.getEmail());
            cmbRol.setSelectedItem(usuario.getRol());
            chkActivo.setSelected(usuario.isActivo());
        }

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(AppColors.BG_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridx = 0; gbc.weightx = 1.0;

        gbc.gridy = 0;  form.add(UIFactory.formLabel("Nombre *"), gbc);
        gbc.gridy = 1;  form.add(txtNombre, gbc);
        gbc.gridy = 2;  form.add(UIFactory.formLabel("Apellidos *"), gbc);
        gbc.gridy = 3;  form.add(txtApellidos, gbc);
        gbc.gridy = 4;  form.add(UIFactory.formLabel("Email *"), gbc);
        gbc.gridy = 5;  form.add(txtEmail, gbc);
        gbc.gridy = 6;  form.add(UIFactory.formLabel(
            esNuevo ? "Contraseña *" : "Nueva contraseña (opcional)"), gbc);
        gbc.gridy = 7;  form.add(txtPassword, gbc);
        gbc.gridy = 8;  form.add(UIFactory.formLabel("Rol *"), gbc);
        gbc.gridy = 9;  form.add(cmbRol, gbc);
        gbc.gridy = 10; gbc.insets = new Insets(10, 0, 0, 0);
        form.add(chkActivo, gbc);

        root.add(form, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);
        JButton btnCancel = UIFactory.secondaryButton("Cancelar");
        JButton btnOk     = UIFactory.primaryButton("Guardar");

        btnCancel.addActionListener(e -> dlg.dispose());
        btnOk.addActionListener(e -> {
            String password = new String(txtPassword.getPassword()).trim();
            var error = esNuevo
                ? controller.crear(txtNombre.getText(), txtApellidos.getText(),
                    txtEmail.getText(), password, (User.Rol) cmbRol.getSelectedItem())
                : controller.actualizar(usuario, txtNombre.getText(), txtApellidos.getText(),
                    txtEmail.getText(), password, (User.Rol) cmbRol.getSelectedItem(),
                    chkActivo.isSelected());

            if (error.isPresent()) {
                UIFactory.showError(dlg, error.get());
                return;
            }
            UIFactory.showSuccess(dlg, "Usuario guardado correctamente.");
            dlg.dispose();
            cargarDatos();
        });

        btns.add(btnCancel); btns.add(btnOk);
        root.add(btns, BorderLayout.SOUTH);

        dlg.pack();
        Dimension tamMin = new Dimension(480, 580);
        if (dlg.getWidth() < tamMin.width || dlg.getHeight() < tamMin.height) {
            dlg.setSize(tamMin);
        }
        dlg.setMinimumSize(tamMin);
        dlg.setLocationRelativeTo(f);
        dlg.setVisible(true);
    }

    private void desactivar() {
        int row = tabla.getSelectedRow();
        if (row < 0) { UIFactory.showError(this, "Selecciona un usuario."); return; }
        User u = lista.get(row);
        String accion = u.isActivo() ? "desactivar" : "activar";
        if (UIFactory.showConfirm(this, "¿Deseas " + accion + " al usuario '" + u.getNombreCompleto() + "'?")) {
            controller.cambiarEstadoActivo(u).ifPresentOrElse(
                msg -> UIFactory.showError(this, msg),
                this::cargarDatos
            );
        }
    }
}
