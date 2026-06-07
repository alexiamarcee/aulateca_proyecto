package com.aulateca.view;

import com.aulateca.controller.LoginController;
import com.aulateca.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/** Diálogo de inicio de sesión (solo login, con logo y valores predefinidos). */
public class LoginDialog extends JDialog {

    private static final Dimension TAM_LOGIN = new Dimension(440, 500);

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private User usuarioAutenticado;
    private final LoginController controller = new LoginController();

    public LoginDialog(Frame parent) {
        super(parent, "Aulateca – Iniciar sesión", true);
        initUI();
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setSize(TAM_LOGIN);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(AppColors.BG_APP);
        setContentPane(root);

        // Panel contenedor (Card)
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

        // Panel superior con logo y subtítulo
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

        logoPanel.add(logo);
        logoPanel.add(Box.createVerticalStrut(4));
        logoPanel.add(subtitle);
        logoPanel.add(Box.createVerticalStrut(24));
        
        card.add(logoPanel, BorderLayout.NORTH);
        card.add(panelLogin(), BorderLayout.CENTER);
        
        root.add(card);
    }

    private JPanel panelLogin() {
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        txtEmail = UIFactory.textField(20);
        txtPassword = UIFactory.passwordField(20);
        
        // Valores predefinidos como pediste
        txtEmail.setText("admin@aulateca.es");
        txtPassword.setText("admin123");

        JLabel lblEmail = UIFactory.formLabel("Correo electrónico");
        JLabel lblPass = UIFactory.formLabel("Contraseña");

        // Configuración de etiquetas alineadas a la izquierda y con ancho máximo
        lblEmail.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, lblEmail.getPreferredSize().height));
        lblPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, lblPass.getPreferredSize().height));

        form.add(lblEmail);
        form.add(Box.createVerticalStrut(6));
        form.add(campoAncho(txtEmail));
        form.add(Box.createVerticalStrut(16));
        form.add(lblPass);
        form.add(Box.createVerticalStrut(6));
        form.add(campoAncho(txtPassword));
        form.add(Box.createVerticalStrut(24));

        JButton btnLogin = UIFactory.primaryButton("Iniciar sesión");
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnLogin.addActionListener(e -> autenticar());
        txtPassword.addActionListener(e -> autenticar());
        form.add(btnLogin);

        return form;
    }

    private <T extends JComponent> T campoAncho(T campo) {
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Usar Integer.MAX_VALUE asegura que ocupe todo el ancho del contenedor
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        return campo;
    }

    private void autenticar() {
        LoginController.AuthResult resultado = controller.iniciarSesion(
            txtEmail.getText(), new String(txtPassword.getPassword()));

        if (resultado.error() != null) {
            UIFactory.showError(this, resultado.error());
            return;
        }
        usuarioAutenticado = resultado.usuario();
        dispose();
    }

    public User getUsuarioAutenticado() { return usuarioAutenticado; }
}