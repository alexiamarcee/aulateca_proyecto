package com.aulateca.view.panels;

import com.aulateca.controller.DashboardController;
import com.aulateca.model.Reservation;
import com.aulateca.model.User;
import com.aulateca.service.dto.DashboardStats;
import com.aulateca.view.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/** Panel de inicio con estadísticas y reservas de hoy (capa Vista). */
public class DashboardPanel extends JPanel {

    private final User                 usuario;
    private final DashboardController  controller = new DashboardController();

    public DashboardPanel(User usuario) {
        this.usuario = usuario;
        setBackground(AppColors.BG_APP);
        setLayout(new BorderLayout(0, 24));
        initUI();
    }

    private void initUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        String fechaStr = LocalDate.now().format(
            DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", new Locale("es", "ES")));
        fechaStr = Character.toUpperCase(fechaStr.charAt(0)) + fechaStr.substring(1);

        JLabel fecha = new JLabel(fechaStr);
        fecha.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        fecha.setForeground(AppColors.TEXT_PRIMARY);

        JLabel saludo = new JLabel("Bienvenido, " + usuario.getNombre());
        saludo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        saludo.setForeground(AppColors.TEXT_SECONDARY);

        JPanel headerText = new JPanel();
        headerText.setOpaque(false);
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.add(saludo);
        headerText.add(Box.createVerticalStrut(4));
        headerText.add(fecha);

        header.add(headerText, BorderLayout.WEST);

        var statsResult = controller.obtenerEstadisticas();
        if (statsResult.esError()) {
            add(header, BorderLayout.NORTH);
            add(new JLabel(statsResult.error(), SwingConstants.CENTER), BorderLayout.CENTER);
            return;
        }

        DashboardStats stats = statsResult.datos();
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 88));
        statsRow.add(statCard("Recursos totales",  String.valueOf(stats.totalRecursos()),    AppColors.PRIMARY));
        statsRow.add(statCard("Reservas hoy",       String.valueOf(stats.reservasHoy()),     AppColors.CHIP_GREEN));
        statsRow.add(statCard("Reservas esta semana", String.valueOf(stats.reservasEstaSemana()), AppColors.CHIP_YELLOW));
        statsRow.add(statCard("Usuarios activos",   String.valueOf(stats.usuariosActivos()),  AppColors.CHIP_PURPLE));

        JPanel topSection = new JPanel();
        topSection.setOpaque(false);
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.add(header);
        topSection.add(Box.createVerticalStrut(24));
        topSection.add(statsRow);
        add(topSection, BorderLayout.NORTH);

        var reservasResult = controller.reservasDeHoy();
        if (!reservasResult.esError()) {
            add(buildTableCard(reservasResult.datos()), BorderLayout.CENTER);
        }
    }

    private JPanel statCard(String label, String value, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColors.BG_WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 88));

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLbl.setForeground(accentColor);

        JLabel labelLbl = new JLabel(label);
        labelLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelLbl.setForeground(AppColors.TEXT_SECONDARY);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(valueLbl);
        content.add(Box.createVerticalStrut(2));
        content.add(labelLbl);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildTableCard(List<Reservation> reservas) {
        JPanel card = new JPanel(new BorderLayout(0, 12)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColors.BG_WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);
        JLabel titulo = new JLabel("Reservas de hoy");
        titulo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titulo.setForeground(AppColors.TEXT_PRIMARY);
        String hoy = LocalDate.now().format(DateTimeFormatter.ofPattern("d MMM yyyy", new Locale("es","ES")));
        JLabel fechaLbl = new JLabel(hoy);
        fechaLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fechaLbl.setForeground(AppColors.TEXT_SECONDARY);
        cardHeader.add(titulo, BorderLayout.WEST);
        cardHeader.add(fechaLbl, BorderLayout.EAST);
        card.add(cardHeader, BorderLayout.NORTH);

        String[] cols = {"Recurso", "Franja horaria", "Usuario", "Motivo", "Estado"};
        Object[][] data = new Object[reservas.size()][5];
        for (int i = 0; i < reservas.size(); i++) {
            Reservation r = reservas.get(i);
            data[i][0] = r.getRecurso().getNombre();
            data[i][1] = r.getFranjaHoraria().getNombre() + "  " + r.getFranjaHoraria().getHorario();
            data[i][2] = r.getUsuario().getNombreCompleto();
            data[i][3] = r.getMotivo() != null ? r.getMotivo() : "—";
            data[i][4] = r.getEstado().name();
        }

        JTable table = new JTable(data, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        UIFactory.styleTable(table);

        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                String val = v != null ? v.toString() : "";
                if ("CONFIRMADA".equals(val)) {
                    setForeground(AppColors.SUCCESS);
                } else if ("CANCELADA".equals(val)) {
                    setForeground(AppColors.ERROR);
                } else {
                    setForeground(AppColors.WARNING);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                setBackground(sel ? AppColors.ROW_SELECTED : AppColors.BG_WHITE);
                setFont(UIFactory.FONT_BOLD);
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppColors.DIVIDER));
        scroll.setBackground(AppColors.BG_WHITE);
        scroll.getViewport().setBackground(AppColors.BG_WHITE);

        if (reservas.isEmpty()) {
            JLabel empty = new JLabel("No hay reservas para hoy", SwingConstants.CENTER);
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            empty.setForeground(AppColors.TEXT_HINT);
            card.add(empty, BorderLayout.CENTER);
        } else {
            card.add(scroll, BorderLayout.CENTER);
        }
        return card;
    }
}
