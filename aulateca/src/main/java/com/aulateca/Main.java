package com.aulateca;

import com.aulateca.model.User;
import com.aulateca.util.DataInitializer;
import com.aulateca.util.HibernateUtil;
import com.aulateca.view.LoginDialog;
import com.aulateca.view.MainFrame;

import javax.swing.*;

/** Punto de entrada de Aulateca. */
public class Main {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        try {
            HibernateUtil.init();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                "<html><b>No se pudo conectar con la base de datos.</b><br><br>" +
                "Comprueba que MySQL/MariaDB está en ejecución<br>" +
                "y que los datos en <b>persistence.xml</b> son correctos.<br><br>" +
                "<small>Error: " + e.getMessage() + "</small></html>",
                "Error de conexión",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        try {
            DataInitializer.inicializar();
        } catch (Exception e) {
            System.err.println("[Aulateca] Advertencia al inicializar datos: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            JFrame dummy = new JFrame();
            LoginDialog login = new LoginDialog(dummy);
            login.setVisible(true);

            User usuario = login.getUsuarioAutenticado();
            if (usuario != null) {
                MainFrame mainFrame = new MainFrame(usuario);
                mainFrame.setVisible(true);
            } else {
                HibernateUtil.close();
                System.exit(0);
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(HibernateUtil::close));
    }
}
