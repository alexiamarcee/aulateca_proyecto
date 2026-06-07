package com.aulateca.view.panels;

import com.aulateca.dao.*;
import com.aulateca.model.*;
import com.aulateca.view.*;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** Consulta de disponibilidad por recurso y franja. */
public class AvailabilityPanel extends JPanel {

    private final ResourceDAO     resourceDAO     = new ResourceDAO();
    private final TimeSlotDAO     timeSlotDAO     = new TimeSlotDAO();
    private final ReservationDAO  reservationDAO  = new ReservationDAO();
    private final ResourceTypeDAO resourceTypeDAO = new ResourceTypeDAO();

    private JDateChooser      dateChooser;
    private JComboBox<String> cmbTipo;
    private JPanel            gridPanel;

    public AvailabilityPanel() {
        setBackground(AppColors.BG_APP);
        setLayout(new BorderLayout(0, 20));
        initUI();
        consultarDisponibilidad();
    }

    private void initUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel titulo = new JLabel("Disponibilidad");
        titulo.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        titulo.setForeground(AppColors.TEXT_PRIMARY);
        header.add(titulo, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel filterCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColors.BG_WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        filterCard.setOpaque(false);
        filterCard.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        filterCard.add(UIFactory.formLabel("Fecha"));
        dateChooser = new JDateChooser();
        dateChooser.setDate(new Date());
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setFont(UIFactory.FONT_BODY);
        dateChooser.setPreferredSize(new Dimension(150, 36));
        estilizarCalendario(dateChooser);
        filterCard.add(dateChooser);

        filterCard.add(Box.createHorizontalStrut(8));

        filterCard.add(UIFactory.formLabel("Tipo de recurso"));
        cmbTipo = new JComboBox<>();
        cmbTipo.addItem("Todos los tipos");
        resourceTypeDAO.buscarTodos().forEach(t -> cmbTipo.addItem(t.getNombre()));
        cmbTipo.setFont(UIFactory.FONT_BODY);
        cmbTipo.setPreferredSize(new Dimension(180, 36));
        filterCard.add(cmbTipo);

        filterCard.add(Box.createHorizontalStrut(8));
        JButton btnConsultar = UIFactory.primaryButton("Consultar");
        btnConsultar.addActionListener(e -> consultarDisponibilidad());
        filterCard.add(btnConsultar);

        filterCard.add(Box.createHorizontalStrut(16));
        filterCard.add(legendItem("Libre",          AppColors.AVAILABLE,  AppColors.AVAILABLE_FG));
        filterCard.add(legendItem("Ocupado",         AppColors.RESERVED,   AppColors.RESERVED_FG));
        filterCard.add(legendItem("No reservable",   AppColors.BLOCKED,    AppColors.BLOCKED_FG));

        add(filterCard, BorderLayout.CENTER);

        gridPanel = new JPanel(new BorderLayout());
        gridPanel.setOpaque(false);
        JScrollPane scroll = new JScrollPane(gridPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setPreferredSize(new Dimension(0, 420));
        add(scroll, BorderLayout.SOUTH);
    }

    private void estilizarCalendario(JDateChooser dc) {
        for (Component c : dc.getComponents()) {
            if (c instanceof JButton btn) {
                btn.setBackground(AppColors.PRIMARY);
                btn.setForeground(Color.WHITE);
                btn.setFocusPainted(false);
                btn.setBorderPainted(false);
                btn.setOpaque(true);
                btn.setText("📅");
                btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
            }
        }
    }

    private JPanel legendItem(String label, Color bg, Color fg) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);
        JLabel chip = UIFactory.chip(label, bg, fg);
        p.add(chip);
        return p;
    }

    private void consultarDisponibilidad() {
        Date d = dateChooser.getDate();
        if (d == null) {
            UIFactory.showError(this, "Selecciona una fecha.");
            return;
        }
        LocalDate fecha = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        List<TimeSlot> franjas  = timeSlotDAO.buscarTodos();
        List<Resource> recursos = resourceDAO.buscarTodos();

        String tipoFiltro = (String) cmbTipo.getSelectedItem();
        if (!"Todos los tipos".equals(tipoFiltro)) {
            recursos = recursos.stream()
                .filter(r -> r.getTipo().getNombre().equals(tipoFiltro))
                .toList();
        }

        gridPanel.removeAll();

        if (recursos.isEmpty() || franjas.isEmpty()) {
            JLabel empty = new JLabel("Sin datos para los filtros seleccionados.", SwingConstants.CENTER);
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            empty.setForeground(AppColors.TEXT_HINT);
            gridPanel.add(empty, BorderLayout.CENTER);
            gridPanel.revalidate(); gridPanel.repaint();
            return;
        }

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
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        String fechaStr = fecha.format(DateTimeFormatter.ofPattern(
            "EEEE, d 'de' MMMM 'de' yyyy", new Locale("es", "ES")));
        fechaStr = Character.toUpperCase(fechaStr.charAt(0)) + fechaStr.substring(1);
        JLabel fechaLbl = new JLabel(fechaStr);
        fechaLbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        fechaLbl.setForeground(AppColors.TEXT_PRIMARY);
        card.add(fechaLbl, BorderLayout.NORTH);

        String[] columnas = new String[franjas.size() + 1];
        columnas[0] = "Recurso";
        for (int i = 0; i < franjas.size(); i++) columnas[i + 1] = franjas.get(i).getNombre();

        Object[][] datos = new Object[recursos.size()][columnas.length];
        for (int ri = 0; ri < recursos.size(); ri++) {
            Resource recurso = recursos.get(ri);
            datos[ri][0] = recurso.getNombre();
            List<TimeSlot> ocupadas = reservationDAO.buscarFranjasOcupadas(recurso, fecha);
            for (int fi = 0; fi < franjas.size(); fi++) {
                if (!recurso.getEstado().isReservable())      datos[ri][fi+1] = "BLOQUEADO";
                else if (ocupadas.contains(franjas.get(fi)))  datos[ri][fi+1] = "OCUPADO";
                else                                           datos[ri][fi+1] = "LIBRE";
            }
        }

        JTable grid = new JTable(datos, columnas) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        grid.setFont(UIFactory.FONT_BODY);
        grid.setRowHeight(40);
        grid.setShowGrid(false);
        grid.setIntercellSpacing(new Dimension(2, 2));
        grid.setBackground(AppColors.BG_WHITE);
        grid.getColumnModel().getColumn(0).setPreferredWidth(200);

        JTableHeader th = grid.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 11));
        th.setBackground(AppColors.BG_WHITE);
        th.setForeground(AppColors.TEXT_SECONDARY);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.BORDER));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                String val = v != null ? v.toString() : "";
                if (col == 0) {
                    JLabel lbl = new JLabel(val);
                    lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    lbl.setForeground(AppColors.TEXT_PRIMARY);
                    lbl.setBackground(sel ? AppColors.ROW_SELECTED : AppColors.BG_WHITE);
                    lbl.setOpaque(true);
                    lbl.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                    return lbl;
                }
                Color bg, fg;
                String display;
                switch (val) {
                    case "OCUPADO"   -> { bg = AppColors.RESERVED;  fg = AppColors.RESERVED_FG;  display = "Ocupado"; }
                    case "BLOQUEADO" -> { bg = AppColors.BLOCKED;   fg = AppColors.BLOCKED_FG;   display = "—"; }
                    default          -> { bg = AppColors.AVAILABLE; fg = AppColors.AVAILABLE_FG; display = "Libre"; }
                }
                JLabel chip = new JLabel(display, SwingConstants.CENTER) {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(getBackground());
                        g2.fillRoundRect(2, 4, getWidth()-4, getHeight()-8, 10, 10);
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };
                chip.setFont(new Font("Segoe UI", Font.BOLD, 11));
                chip.setForeground(fg);
                chip.setBackground(sel ? AppColors.ROW_SELECTED : bg);
                chip.setOpaque(false);
                return chip;
            }
        };
        for (int c = 0; c < grid.getColumnCount(); c++)
            grid.getColumnModel().getColumn(c).setCellRenderer(renderer);

        JScrollPane tableScroll = new JScrollPane(grid);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        tableScroll.getViewport().setBackground(AppColors.BG_WHITE);
        card.add(tableScroll, BorderLayout.CENTER);

        gridPanel.add(card, BorderLayout.CENTER);
        gridPanel.revalidate();
        gridPanel.repaint();
    }
}
