package view;

import controller.AuthController;
import model.User;
import util.UIStyle;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainFrame extends JFrame {

    // page names used by the CardLayout
    private static final String DASHBOARD = "dashboard";
    private static final String CARS = "cars";
    private static final String CUSTOMERS = "customers";
    private static final String SALE = "sale";
    private static final String REPORTS = "reports";

    private static final Color SIDEBAR = new Color(33, 43, 54);

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel content = new JPanel(cardLayout);

    private final DashboardPanel dashboardPanel = new DashboardPanel();
    private final CarPanel carPanel = new CarPanel();
    private final CustomerPanel customerPanel = new CustomerPanel();
    private final SalePanel salePanel = new SalePanel();
    private final ReportPanel reportPanel = new ReportPanel();

    private final Map<String, JButton> menuButtons = new LinkedHashMap<>();

    public MainFrame() {
        setTitle("Car Sale System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 720);
        setMinimumSize(new Dimension(1000, 640));
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(buildSidebar(), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);

        showPage(DASHBOARD);
    }


    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(24, 0, 20, 0));

        JLabel logo = new JLabel("Car Sale System");
        logo.setFont(UIStyle.FONT_HEADING);
        logo.setForeground(Color.WHITE);
        logo.setBorder(BorderFactory.createEmptyBorder(0, 20, 24, 20));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(logo);

        sidebar.add(menuButton("Dashboard", DASHBOARD));
        sidebar.add(menuButton("Manage Cars", CARS));
        sidebar.add(menuButton("Manage Customers", CUSTOMERS));
        sidebar.add(menuButton("New Sale", SALE));
        sidebar.add(menuButton("Sales & Reports", REPORTS));

        sidebar.add(Box.createVerticalGlue());

        // who is logged in
        User user = AuthController.getCurrentUser();
        String who = (user == null) ? "" : user.getFullName() + " (" + user.getRole() + ")";
        JLabel lblUser = new JLabel(who);
        lblUser.setFont(UIStyle.FONT_NORMAL);
        lblUser.setForeground(new Color(176, 190, 197));
        lblUser.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lblUser);

        JButton btnLogout = UIStyle.button("Logout", UIStyle.DANGER);
        btnLogout.addActionListener(e -> logout());
        JPanel logoutWrap = new JPanel(new BorderLayout());
        logoutWrap.setOpaque(false);
        logoutWrap.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        logoutWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        logoutWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutWrap.add(btnLogout, BorderLayout.CENTER);
        sidebar.add(logoutWrap);

        return sidebar;
    }

    private JButton menuButton(String text, String page) {
        JButton button = new JButton(text);
        button.setFont(UIStyle.FONT_BOLD);
        button.setForeground(Color.WHITE);
        button.setBackground(SIDEBAR);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> showPage(page));

        menuButtons.put(page, button);
        return button;
    }


    private JPanel buildContent() {
        content.setBackground(UIStyle.BACKGROUND);

        content.add(dashboardPanel, DASHBOARD);
        content.add(carPanel, CARS);
        content.add(customerPanel, CUSTOMERS);
        content.add(salePanel, SALE);
        content.add(reportPanel, REPORTS);

        return content;
    }

    // shows one page and highlights its menu button
    private void showPage(String page) {
        cardLayout.show(content, page);

        for (Map.Entry<String, JButton> entry : menuButtons.entrySet()) {
            boolean active = entry.getKey().equals(page);
            entry.getValue().setBackground(active ? UIStyle.PRIMARY : SIDEBAR);
        }

        // these pages reload their data every time they are shown
        if (DASHBOARD.equals(page)) {
            dashboardPanel.refresh();
        } else if (CARS.equals(page)) {
            carPanel.refresh();
        } else if (CUSTOMERS.equals(page)) {
            customerPanel.refresh();
        } else if (SALE.equals(page)) {
            salePanel.refresh();
        }
    }

    private void logout() {
        if (UIStyle.confirm(this, "Do you want to log out?")) {
            AuthController.logout();
            dispose();
            new LoginForm().setVisible(true);
        }
    }
}