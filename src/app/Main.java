package app;

import view.LoginForm;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        // Swing screens must be created on the Swing thread
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}