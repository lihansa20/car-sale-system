package util;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class UIStyle {

    public static final Color PRIMARY    = new Color(25, 118, 210);
    public static final Color DANGER     = new Color(211, 47, 47);
    public static final Color SUCCESS    = new Color(46, 125, 50);
    public static final Color NEUTRAL    = new Color(96, 125, 139);
    public static final Color BACKGROUND = new Color(245, 247, 250);
    public static final Color CARD       = Color.WHITE;
    public static final Color BORDER     = new Color(218, 222, 228);
    public static final Color TEXT       = new Color(33, 37, 41);
    public static final Color MUTED      = new Color(108, 117, 125);

    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_NORMAL  = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD    = new Font("Segoe UI", Font.BOLD, 14);

    private UIStyle() {
    }


    public static JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_NORMAL);
        label.setForeground(TEXT);
        return label;
    }

    public static JTextField textField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(FONT_NORMAL);
        field.setBorder(fieldBorder());
        return field;
    }

    public static JPasswordField passwordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(FONT_NORMAL);
        field.setBorder(fieldBorder());
        return field;
    }

    public static JButton button(String text, Color background) {
        JButton button = new JButton(text);
        button.setFont(FONT_BOLD);
        button.setForeground(Color.WHITE);
        button.setBackground(background);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(background.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(background);
            }
        });
        return button;
    }

    public static void styleTable(JTable table) {
        table.setFont(FONT_NORMAL);
        table.setRowHeight(30);
        table.setGridColor(BORDER);
        table.setSelectionBackground(new Color(187, 222, 251));
        table.setSelectionForeground(TEXT);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(PRIMARY);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
    }


    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(Component parent, String message) {
        int choice = JOptionPane.showConfirmDialog(parent, message, "Please confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return choice == JOptionPane.YES_OPTION;
    }


    public static String money(double amount) {
        return String.format("%,.2f", amount);
    }

    private static javax.swing.border.Border fieldBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(7, 10, 7, 10));
    }
}