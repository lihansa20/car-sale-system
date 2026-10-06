package view;

import controller.AuthController;
import util.UIStyle;
import util.ValidationException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;

public class LoginForm extends JFrame {

    private final JTextField txtUsername = UIStyle.textField(20);
    private final JPasswordField txtPassword = UIStyle.passwordField(20);
    private final JButton btnLogin = UIStyle.button("Login", UIStyle.PRIMARY);

    private final AuthController authController = new AuthController();

    public LoginForm() {
        setTitle("Car Sale System - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(460, 480);
        setLocationRelativeTo(null);   // centre of the screen
        setResizable(false);

        buildUI();
        addEvents();
    }

    private void buildUI() {
        // the grey background of the window
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(UIStyle.BACKGROUND);

        // the white card in the middle
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UIStyle.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyle.BORDER),
                BorderFactory.createEmptyBorder(30, 36, 30, 36)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // title
        JLabel title = new JLabel("Car Sale System", SwingConstants.CENTER);
        title.setFont(UIStyle.FONT_TITLE);
        title.setForeground(UIStyle.PRIMARY);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 4, 0);
        card.add(title, gbc);

        JLabel subtitle = new JLabel("Please log in to continue", SwingConstants.CENTER);
        subtitle.setFont(UIStyle.FONT_NORMAL);
        subtitle.setForeground(UIStyle.MUTED);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 24, 0);
        card.add(subtitle, gbc);

        // username
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 4, 0);
        card.add(UIStyle.label("Username"), gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 14, 0);
        card.add(txtUsername, gbc);

        // password
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 4, 0);
        card.add(UIStyle.label("Password"), gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 24, 0);
        card.add(txtPassword, gbc);

        // login button
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(btnLogin, gbc);

        root.add(card);
        setContentPane(root);
    }

    private void addEvents() {
        btnLogin.addActionListener(e -> doLogin());

        // pressing Enter anywhere in the window clicks the Login button
        getRootPane().setDefaultButton(btnLogin);
    }

    private void doLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        try {
            authController.login(username, password);

            // login worked: close this window and open the main window
            dispose();
            new MainFrame().setVisible(true);

        } catch (ValidationException e) {
            UIStyle.showError(this, e.getMessage());
            txtPassword.setText("");
            txtPassword.requestFocus();

        } catch (SQLException | IllegalStateException e) {
            UIStyle.showError(this, "Cannot connect to the database.\n"
                    + "Make sure XAMPP MySQL is running.\n\n" + e.getMessage());
        }
    }
}