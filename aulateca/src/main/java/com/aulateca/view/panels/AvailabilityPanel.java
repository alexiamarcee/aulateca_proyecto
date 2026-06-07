package com.aulateca.view.panels;

import com.aulateca.controller.AvailabilityController;
import com.aulateca.service.dto.DisponibilidadResultado;
import com.aulateca.service.dto.DisponibilidadResultado.EstadoCelda;
import com.aulateca.service.dto.DisponibilidadResultado.FilaDisponibilidad;
import com.aulateca.view.*;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/** Consulta de disponibilidad por recurso y franja (capa Vista). */
public class AvailabilityPanel extends JPanel {

    private final AvailabilityController controller = new AvailabilityController();

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

        JPanel filtersRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        filtersRow.setOpaque(false);

        JLabel lblFecha = UIFactory.formLabel("Fecha");
        lblFecha.setAlignmentY(Component.CENTER_ALIGNMENT);
        filtersRow.add(lblFecha);
        dateChooser = UIFactory.dateChooser();
        dateChooser.setDate(new Date());
        filtersRow.add(dateChooser);

        JLabel lblTipo = UIFactory.formLabel("Tipo de recurso");
        lblTipo.setAlignmentY(Component.CENTER_ALIGNMENT);
        filtersRow.add(lblTipo);
        cmbTipo = new JComboBox<>();
        var tiposResult = controller.nombresTiposRecurso();
        if (!tiposResult.esError()) {
            tiposResult.datos().forEach(cmbTipo::addItem);
        }
        cmbTipo.setFont(UIFactory.FONT_BODY);
        cmbTipo.setPrototypeDisplayValue("Todos los tipos");
        filtersRow.add(cmbTipo);

        JButton btnConsultar = UIFactory.primaryButton("Consultar");
        btnConsultar.addActionListener(e -> consultarDisponibilidad());
        filtersRow.add(btnConsultar);

        JPanel legendRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        legendRow.setOpaque(false);
        legendRow.add(legendItem("Libre",          AppColors.AVAILABLE,  AppColors.AVAILABLE_FG));
        legendRow.add(legendItem("Ocupado",         AppColors.RESERVED,   AppColors.RESERVED_FG));
        legendRow.add(legendItem("No reservable",   AppColors.BLOCKED,    AppColors.BLOCKED_FG));

        JPanel filterCard = new JPanel(new BorderLayout(16, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColors.BG_WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        filterCard.setOpaque(false);
        filterCard.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        filterCard.add(filtersRow, BorderLayout.WEST);
        filterCard.add(legendRow, BorderLayout.EAST);

        JPanel topSection = new JPanel(new BorderLayout(0, 12));
        topSection.setOpaque(false);
        topSection.add(header, BorderLayout.NORTH);
        topSection.add(filterCard, BorderLayout.CENTER);
        add(topSection, BorderLayout.NORTH);

        gridPanel = new JPanel(new BorderLayout());
        gridPanel.setOpaque(false);
        JScrollPane scroll = new JScrollPane(gridPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel legendItem(String label, Color bg, Color fg) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);
        JLabel chip = UIFactory.chip(label, bg, fg);
        p.add(chip);
        return p;
    }

    /** Solicita la consulta al controlador y pinta la cuadrícula. */
    private void consultarDisponibilidad() {
        Date d = dateChooser.getDate();
        if (d == null) {
            UIFactory.showError(this, "Selecciona una fecha.");
            return;
        }
        LocalDate fecha = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        String tipoFiltro = (String) cmbTipo.getSelectedItem();

        var resultado = controller.consultar(fecha, tipoFiltro);
        if (resultado.esError()) {
            UIFactory.showError(this, resultado.error());
            return;
        }

        mostrarResultado(resultado.datos());
    }

    private void mostrarResultado(DisponibilidadResultado datos) {
        gridPanel.removeAll();

        if (datos.sinDatos()) {
            JLabel empty = new JLabel("Sin datos para los filtros seleccionados.", SwingConstants.CENTER);
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            empty.setForeground(AppColors.TEXT_HINT);
            gridPanel.add(empty, BorderLayout.CENTER);
            gridPanel.revalidate();
            gridPanel.repaint();
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

        JPanel fechaHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        fechaHeader.setOpaque(false);
        JLabel fechaLbl = new JLabel(datos.fechaFormateada());
        fechaLbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        fechaLbl.setForeground(AppColors.TEXT_PRIMARY);
        fechaHeader.add(fechaLbl);
        card.add(fechaHeader, BorderLayout.NORTH);

        List<String> franjas = datos.nombresFranjas();
        String[] columnas = new String[franjas.size() + 1];
        columnas[0] = "Recurso";
        for (int i = 0; i < franjas.size(); i++) columnas[i + 1] = franjas.get(i);

        Object[][] gridData = new Object[datos.filas().size()][columnas.length];
        for (int ri = 0; ri < datos.filas().size(); ri++) {
            FilaDisponibilidad fila = datos.filas().get(ri);
            gridData[ri][0] = fila.nombreRecurso();
            for (int fi = 0; fi < fila.celdas().size(); fi++) {
                gridData[ri][fi + 1] = fila.celdas().get(fi).name();
            }
        }

        JTable grid = new JTable(gridData, columnas) {
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
                EstadoCelda estado = EstadoCelda.valueOf(val);
                Color bg, fg;
                String display;
                switch (estado) {
                    case OCUPADO   -> { bg = AppColors.RESERVED;  fg = AppColors.RESERVED_FG;  display = "Ocupado"; }
                    case BLOQUEADO -> { bg = AppColors.BLOCKED;   fg = AppColors.BLOCKED_FG;   display = "—"; }
                    default        -> { bg = AppColors.AVAILABLE; fg = AppColors.AVAILABLE_FG; display = "Libre"; }
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
