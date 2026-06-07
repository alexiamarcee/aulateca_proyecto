package com.aulateca.view;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/** Componentes Swing reutilizables. */
public class UIFactory {

    public static final Font FONT_TITLE  = new Font("Google Sans", Font.BOLD,   22);
    public static final Font FONT_H2     = new Font("Google Sans", Font.PLAIN,  18);
    public static final Font FONT_BODY   = new Font("Roboto",      Font.PLAIN,  13);
    public static final Font FONT_SMALL  = new Font("Roboto",      Font.PLAIN,  11);
    public static final Font FONT_BOLD   = new Font("Roboto",      Font.BOLD,   13);
    public static final Font FONT_BUTTON = new Font("Google Sans", Font.PLAIN,  13);

    static {
        if (!isFontAvailable("Google Sans")) {
            replaceFont("Google Sans", "Segoe UI");
        }
        if (!isFontAvailable("Roboto")) {
            replaceFont("Roboto", "Segoe UI");
        }
    }

    private static boolean isFontAvailable(String name) {
        for (String f : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            if (f.equals(name)) return true;
        }
        return false;
    }

    private static void replaceFont(String from, String to) {
    }

    public static JButton primaryButton(String text) {
        return makeButton(text, AppColors.PRIMARY, Color.WHITE, true);
    }

    public static JButton accentButton(String text) {
        return makeButton(text, AppColors.CHIP_GREEN, Color.WHITE, true);
    }

    public static JButton dangerButton(String text) {
        return makeButton(text, AppColors.ERROR, Color.WHITE, true);
    }

    public static JButton secondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setForeground(AppColors.PRIMARY);
        btn.setBackground(AppColors.BG_WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER, 1, true),
            BorderFactory.createEmptyBorder(7, 18, 7, 18)
        ));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(AppColors.PRIMARY_LIGHT); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(AppColors.BG_WHITE); }
        });
        return btn;
    }

    public static JButton textButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setForeground(AppColors.PRIMARY);
        btn.setBackground(new Color(0, 0, 0, 0));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(AppColors.PRIMARY_DARK); }
            public void mouseExited(MouseEvent e)  { btn.setForeground(AppColors.PRIMARY); }
        });
        return btn;
    }

    private static JButton makeButton(String text, Color bg, Color fg, boolean filled) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BUTTON);
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        Color hoverColor = bg.darker();
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hoverColor); btn.repaint(); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(bg);         btn.repaint(); }
        });
        return btn;
    }

    public static JTextField textField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(FONT_BODY);
        tf.setForeground(AppColors.TEXT_PRIMARY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        tf.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppColors.PRIMARY, 2),
                    BorderFactory.createEmptyBorder(7, 11, 7, 11)));
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppColors.BORDER, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)));
            }
        });
        return tf;
    }

    public static JPasswordField passwordField(int columns) {
        JPasswordField pf = new JPasswordField(columns);
        pf.setFont(FONT_BODY);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        pf.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                pf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppColors.PRIMARY, 2),
                    BorderFactory.createEmptyBorder(7, 11, 7, 11)));
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                pf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppColors.BORDER, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)));
            }
        });
        return pf;
    }

    public static JScrollPane textArea(JTextArea ta) {
        ta.setFont(FONT_BODY);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        ta.setForeground(AppColors.TEXT_PRIMARY);
        JScrollPane sp = new JScrollPane(ta);
        sp.setBorder(BorderFactory.createLineBorder(AppColors.BORDER));
        return sp;
    }

    public static <T> JComboBox<T> comboBox() {
        JComboBox<T> cb = new JComboBox<>();
        cb.setFont(FONT_BODY);
        cb.setBackground(AppColors.BG_WHITE);
        cb.setForeground(AppColors.TEXT_PRIMARY);
        cb.setBorder(BorderFactory.createLineBorder(AppColors.BORDER));
        return cb;
    }

    private static final Border DATE_FIELD_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(AppColors.BORDER, 1),
        BorderFactory.createEmptyBorder(8, 12, 8, 12)
    );
    private static final Border DATE_FIELD_FOCUS_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(AppColors.PRIMARY, 2),
        BorderFactory.createEmptyBorder(7, 11, 7, 11)
    );

    /** Selector de fecha alineado con el resto de campos del formulario. */
    public static JDateChooser dateChooser() {
        JDateChooser dc = new JDateChooser();
        dc.setDateFormatString("dd/MM/yyyy");
        dc.setFont(FONT_BODY);
        dc.setBackground(AppColors.BG_WHITE);
        dc.setOpaque(true);
        dc.setPreferredSize(new Dimension(0, 38));

        JComponent editor = (JComponent) dc.getDateEditor().getUiComponent();
        editor.setFont(FONT_BODY);
        editor.setForeground(AppColors.TEXT_PRIMARY);
        editor.setBackground(AppColors.BG_WHITE);
        editor.setBorder(DATE_FIELD_BORDER);
        editor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                editor.setBorder(DATE_FIELD_FOCUS_BORDER);
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                editor.setBorder(DATE_FIELD_BORDER);
            }
        });

        for (Component c : dc.getComponents()) {
            if (c instanceof JButton btn) {
                btn.setBackground(AppColors.PRIMARY);
                btn.setForeground(Color.WHITE);
                btn.setFocusPainted(false);
                btn.setBorderPainted(false);
                btn.setOpaque(true);
                btn.setText("📅");
                btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
                btn.setPreferredSize(new Dimension(38, 38));
                btn.setMinimumSize(new Dimension(38, 38));
            }
        }
        return dc;
    }

    public static JLabel sectionTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        lbl.setForeground(AppColors.TEXT_PRIMARY);
        return lbl;
    }

    public static JLabel formLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(AppColors.TEXT_SECONDARY);
        return lbl;
    }

    public static JLabel chip(String text, Color bg, Color fg) {
        JLabel lbl = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(fg);
        lbl.setBackground(bg);
        lbl.setOpaque(false);
        lbl.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        return lbl;
    }

    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(40);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(AppColors.DIVIDER);
        table.setBackground(AppColors.BG_WHITE);
        table.setSelectionBackground(AppColors.ROW_SELECTED);
        table.setSelectionForeground(AppColors.TEXT_PRIMARY);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(AppColors.BG_WHITE);
        header.setForeground(AppColors.TEXT_SECONDARY);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.BORDER));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (sel) {
                    setBackground(AppColors.ROW_SELECTED);
                    setForeground(AppColors.TEXT_PRIMARY);
                } else {
                    setBackground(AppColors.BG_WHITE);
                    setForeground(AppColors.TEXT_PRIMARY);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                setFont(FONT_BODY);
                return this;
            }
        });
    }

    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Correcto",
            JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error",
            JOptionPane.ERROR_MESSAGE);
    }

    public static boolean showConfirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Confirmar",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public static JPanel headerPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(AppColors.BG_WHITE);
        p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.BORDER));
        return p;
    }
}
