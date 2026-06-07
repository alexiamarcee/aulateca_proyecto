package com.aulateca.view;

import com.aulateca.controller.LoginController;
import com.aulateca.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/** Diálogo de inicio de sesión (capa Vista). */
public class LoginDialog extends JDialog {

    private JTextField     txtEmail;
    private JPasswordField txtPassword;
    private User           usuarioAutenticado;
    private final LoginController controller = new LoginController();

    public LoginDialog(Frame parent) {
        super(parent, "Aulateca – Iniciar sesión", true);
        initUI();
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setSize(440, 500);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setUndecorated(false);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(AppColors.BG_APP);
        setContentPane(root);

        JPanel card = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColors.BG_WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(380, 440));
        card.setBorder(BorderFactory.createEmptyBorder(40, 40, 32, 40));

        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("<html>" +
            "<span style='color:" + AppColors.toHex(AppColors.PRIMARY_DARK) + ";font-size:26px;font-family:Segoe UI'><b>Aula</b></span>" +
            "<span style='color:" + AppColors.toHex(AppColors.PRIMARY) + ";font-size:26px;font-family:Segoe UI'><b>teca</b></span>" +
            "</html>");
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Gestor de Reservas del Centro");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(AppColors.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel bienvenida = new JLabel("Iniciar sesión");
        bienvenida.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        bienvenida.setForeground(AppColors.TEXT_PRIMARY);
        bienvenida.setAlignmentX(Component.CENTER_ALIGNMENT);

        logoPanel.add(logo);
        logoPanel.add(Box.createVerticalStrut(4));
        logoPanel.add(subtitle);
        logoPanel.add(Box.createVerticalStrut(24));
        logoPanel.add(bienvenida);
        card.add(logoPanel, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(24, 0, 0, 0));

        txtEmail    = UIFactory.textField(20);
        txtPassword = UIFactory.passwordField(20);
        txtEmail.setText("admin@aulateca.es");
        txtPassword.setText("admin123");

        JLabel lblEmail = UIFactory.formLabel("Correo electrónico");
        JLabel lblPass  = UIFactory.formLabel("Contraseña");
        lblEmail.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtEmail.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        form.add(lblEmail);
        form.add(Box.createVerticalStrut(6));
        form.add(txtEmail);
        form.add(Box.createVerticalStrut(16));
        form.add(lblPass);
        form.add(Box.createVerticalStrut(6));
        form.add(txtPassword);
        form.add(Box.createVerticalStrut(8));

        JLabel hint = new JLabel("Por defecto: admin@aulateca.es / admin123");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hint.setForeground(AppColors.TEXT_HINT);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(hint);
        form.add(Box.createVerticalStrut(28));

        JButton btnLogin = UIFactory.primaryButton("Iniciar sesión");
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnLogin.addActionListener(e -> autenticar());
        txtPassword.addActionListener(e -> autenticar());
        form.add(btnLogin);

        card.add(form, BorderLayout.CENTER);
        root.add(card);
    }

    /** Delega la autenticación al controlador. */
    private void autenticar() {
        LoginController.AuthResult resultado = controller.iniciarSesion(
            txtEmail.getText(), new String(txtPassword.getPassword()));

        if (resultado.error() != null) {
            UIFactory.showError(this, resultado.error());
            if (resultado.usuario() == null && !txtEmail.getText().isBlank()) {
                txtPassword.selectAll();
                txtPassword.requestFocus();
            }
            return;
        }
        usuarioAutenticado = resultado.usuario();
        dispose();
    }

    public User getUsuarioAutenticado() { return usuarioAutenticado; }
}
