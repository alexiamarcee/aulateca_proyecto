package com.aulateca.view;

import com.aulateca.controller.LoginController;
import com.aulateca.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/** Diálogo de inicio de sesión y registro (capa Vista). */
public class LoginDialog extends JDialog {

    private static final Dimension TAM_LOGIN     = new Dimension(440, 500);
    private static final Dimension TAM_REGISTRO  = new Dimension(440, 640);
    private static final Dimension TAM_CARD_LOGIN    = new Dimension(380, 440);
    private static final Dimension TAM_CARD_REGISTRO = new Dimension(380, 580);

    private JTextField     txtEmail;
    private JPasswordField txtPassword;
    private JTextField     txtRegNombre;
    private JTextField     txtRegApellidos;
    private JTextField     txtRegEmail;
    private JPasswordField txtRegPassword;
    private JPasswordField txtRegConfirmar;
    private JLabel         lblBienvenida;
    private JPanel         card;
    private JPanel         panelContenido;
    private CardLayout     cardLayout;
    private User           usuarioAutenticado;
    private final LoginController controller = new LoginController();

    public LoginDialog(Frame parent) {
        super(parent, "Aulateca – Iniciar sesión", true);
        initUI();
        mostrarLogin();
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(AppColors.BG_APP);
        setContentPane(root);

        card = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColors.BG_WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        card.setOpaque(false);
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

        lblBienvenida = new JLabel("Iniciar sesión");
        lblBienvenida.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        lblBienvenida.setForeground(AppColors.TEXT_PRIMARY);
        lblBienvenida.setAlignmentX(Component.CENTER_ALIGNMENT);

        logoPanel.add(logo);
        logoPanel.add(Box.createVerticalStrut(4));
        logoPanel.add(subtitle);
        logoPanel.add(Box.createVerticalStrut(24));
        logoPanel.add(lblBienvenida);
        card.add(logoPanel, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);
        panelContenido.setOpaque(false);
        panelContenido.setBorder(BorderFactory.createEmptyBorder(24, 0, 0, 0));
        panelContenido.add(panelLogin(), "login");
        panelContenido.add(panelRegistro(), "registro");
        card.add(panelContenido, BorderLayout.CENTER);

        root.add(card);
    }

    private JPanel panelLogin() {
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        txtEmail    = UIFactory.textField(20);
        txtPassword = UIFactory.passwordField(20);
        txtEmail.setText("admin@aulateca.es");
        txtPassword.setText("admin123");

        JLabel lblEmail = UIFactory.formLabel("Correo electrónico");
        JLabel lblPass  = UIFactory.formLabel("Contraseña");
        lblEmail.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, lblEmail.getPreferredSize().height));
        lblPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, lblPass.getPreferredSize().height));
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

        JButton btnLogin = UIFactory.primaryButton("Iniciar sesión");
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnLogin.addActionListener(e -> autenticar());
        txtPassword.addActionListener(e -> autenticar());
        form.add(btnLogin);

        JButton linkRegistro = UIFactory.textButton("¿No tienes cuenta? Crear una");
        linkRegistro.setAlignmentX(Component.CENTER_ALIGNMENT);
        linkRegistro.addActionListener(e -> mostrarRegistro());
        form.add(Box.createVerticalStrut(14));
        form.add(linkRegistro);

        return form;
    }

    private JPanel panelRegistro() {
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        txtRegNombre     = campoAncho(UIFactory.textField(20));
        txtRegApellidos  = campoAncho(UIFactory.textField(20));
        txtRegEmail      = campoAncho(UIFactory.textField(20));
        txtRegPassword   = campoAncho(UIFactory.passwordField(20));
        txtRegConfirmar  = campoAncho(UIFactory.passwordField(20));

        form.add(campoFormulario("Nombre *", txtRegNombre));
        form.add(Box.createVerticalStrut(10));
        form.add(campoFormulario("Apellidos *", txtRegApellidos));
        form.add(Box.createVerticalStrut(10));
        form.add(campoFormulario("Correo electrónico *", txtRegEmail));
        form.add(Box.createVerticalStrut(10));
        form.add(campoFormulario("Contraseña *", txtRegPassword));
        form.add(Box.createVerticalStrut(10));
        form.add(campoFormulario("Confirmar contraseña *", txtRegConfirmar));
        form.add(Box.createVerticalStrut(12));

        JLabel aviso = new JLabel("La cuenta se creará con rol de alumno.");
        aviso.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        aviso.setForeground(AppColors.TEXT_SECONDARY);
        aviso.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(aviso);
        form.add(Box.createVerticalStrut(16));

        JButton btnRegistrar = botonAncho(UIFactory.accentButton("Crear cuenta"));
        btnRegistrar.addActionListener(e -> registrar());
        form.add(btnRegistrar);

        JButton linkLogin = UIFactory.textButton("¿Ya tienes cuenta? Iniciar sesión");
        linkLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        linkLogin.addActionListener(e -> mostrarLogin());
        form.add(Box.createVerticalStrut(14));
        form.add(linkLogin);

        return form;
    }

    private void mostrarLogin() {
        lblBienvenida.setText("Iniciar sesión");
        card.setPreferredSize(TAM_CARD_LOGIN);
        cardLayout.show(panelContenido, "login");
        setSize(TAM_LOGIN);
        centrarVentana();
    }

    private void mostrarRegistro() {
        lblBienvenida.setText("Crear cuenta");
        card.setPreferredSize(TAM_CARD_REGISTRO);
        cardLayout.show(panelContenido, "registro");
        setSize(TAM_REGISTRO);
        centrarVentana();
    }

    private void centrarVentana() {
        revalidate();
        repaint();
        setLocationRelativeTo(getOwner());
    }

    private JPanel campoFormulario(String etiqueta, JComponent campo) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        JLabel lbl = UIFactory.formLabel(etiqueta);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(6));
        panel.add(campo);
        return panel;
    }

    private <T extends JComponent> T campoAncho(T campo) {
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        return campo;
    }

    private JButton botonAncho(JButton btn) {
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        return btn;
    }

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

    private void registrar() {
        LoginController.AuthResult resultado = controller.registrar(
            txtRegNombre.getText(),
            txtRegApellidos.getText(),
            txtRegEmail.getText(),
            new String(txtRegPassword.getPassword()),
            new String(txtRegConfirmar.getPassword()));

        if (resultado.error() != null) {
            UIFactory.showError(this, resultado.error());
            return;
        }
        usuarioAutenticado = resultado.usuario();
        dispose();
    }

    public User getUsuarioAutenticado() { return usuarioAutenticado; }
}
