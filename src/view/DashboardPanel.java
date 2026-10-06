package view;

import controller.DashboardController;
import util.UIStyle;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;

public class DashboardPanel extends JPanel {

    private final DashboardController controller = new DashboardController();

    // the big numbers on the summary cards
    private final JLabel lblCars = valueLabel();
    private final JLabel lblAvailable = valueLabel();
    private final JLabel lblCustomers = valueLabel();
    private final JLabel lblSales = valueLabel();
    private final JLabel lblRevenue = valueLabel();
    private final JLabel lblProfit = valueLabel();

    private final BarChartPanel monthlyChart =
            new BarChartPanel("Revenue - last 6 months (Rs.)", UIStyle.PRIMARY);
    private final BarChartPanel brandChart =
            new BarChartPanel("Cars sold by brand", UIStyle.SUCCESS);

    public DashboardPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(UIStyle.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("Dashboard");
        title.setFont(UIStyle.FONT_TITLE);
        title.setForeground(UIStyle.TEXT);
        add(title, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(2, 3, 16, 16));
        cards.setOpaque(false);
        cards.add(card("Total cars", lblCars, UIStyle.PRIMARY));
        cards.add(card("Available cars", lblAvailable, UIStyle.SUCCESS));
        cards.add(card("Customers", lblCustomers, UIStyle.NEUTRAL));
        cards.add(card("Total sales", lblSales, UIStyle.PRIMARY));
        cards.add(card("Revenue (Rs.)", lblRevenue, UIStyle.SUCCESS));
        cards.add(card("Profit (Rs.)", lblProfit, UIStyle.DANGER));

        JPanel charts = new JPanel(new GridLayout(1, 2, 16, 0));
        charts.setOpaque(false);
        charts.add(monthlyChart);
        charts.add(brandChart);

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setOpaque(false);
        center.add(cards, BorderLayout.NORTH);
        center.add(charts, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    public void refresh() {
        try {
            lblCars.setText(String.valueOf(controller.getTotalCars()));
            lblAvailable.setText(String.valueOf(controller.getAvailableCars()));
            lblCustomers.setText(String.valueOf(controller.getTotalCustomers()));
            lblSales.setText(String.valueOf(controller.getTotalSales()));
            lblRevenue.setText(UIStyle.money(controller.getTotalRevenue()));
            lblProfit.setText(UIStyle.money(controller.getTotalProfit()));

            monthlyChart.setData(controller.getMonthlyRevenue());
            brandChart.setData(controller.getSalesByBrand());

        } catch (SQLException e) {
            UIStyle.showError(this, "Could not load the dashboard data.\n" + e.getMessage());
        }
    }


    private static JLabel valueLabel() {
        JLabel label = new JLabel("-");
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(UIStyle.TEXT);
        return label;
    }

    private static JPanel card(String caption, JLabel value, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(UIStyle.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyle.BORDER),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 5, 0, 0, accent),
                        BorderFactory.createEmptyBorder(14, 16, 14, 16))));

        JLabel captionLabel = new JLabel(caption);
        captionLabel.setFont(UIStyle.FONT_NORMAL);
        captionLabel.setForeground(UIStyle.MUTED);

        card.add(captionLabel, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        return card;
    }
}