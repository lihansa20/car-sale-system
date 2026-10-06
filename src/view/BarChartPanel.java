package view;

import util.UIStyle;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.LinkedHashMap;
import java.util.Map;


public class BarChartPanel extends JPanel {

    private final String title;
    private final Color barColor;
    private Map<String, Double> data = new LinkedHashMap<>();

    public BarChartPanel(String title, Color barColor) {
        this.title = title;
        this.barColor = barColor;

        setBackground(UIStyle.CARD);
        setBorder(BorderFactory.createLineBorder(UIStyle.BORDER));
        setPreferredSize(new Dimension(420, 280));
    }

    public void setData(Map<String, ? extends Number> newData) {
        Map<String, Double> copy = new LinkedHashMap<>();
        for (Map.Entry<String, ? extends Number> entry : newData.entrySet()) {
            copy.put(entry.getKey(), entry.getValue().doubleValue());
        }
        data = copy;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        g2.setFont(UIStyle.FONT_HEADING);
        g2.setColor(UIStyle.TEXT);
        g2.drawString(title, 16, 28);

        if (data.isEmpty()) {
            g2.setFont(UIStyle.FONT_NORMAL);
            g2.setColor(UIStyle.MUTED);
            FontMetrics fm = g2.getFontMetrics();
            String message = "No data yet";
            g2.drawString(message, (width - fm.stringWidth(message)) / 2, height / 2);
            g2.dispose();
            return;
        }

        int left = 24;
        int right = width - 24;
        int top = 60;
        int bottom = height - 36;
        int chartHeight = bottom - top;

        double max = 0;
        for (double value : data.values()) {
            max = Math.max(max, value);
        }
        if (max <= 0) {
            max = 1;
        }

        double slot = (right - left) / (double) data.size();
        int barWidth = (int) Math.min(70, slot * 0.6);

        g2.setColor(UIStyle.BORDER);
        g2.drawLine(left, bottom, right, bottom);

        g2.setFont(UIStyle.FONT_NORMAL);
        FontMetrics fm = g2.getFontMetrics();

        int index = 0;
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            double value = entry.getValue();

            int barHeight = (int) (chartHeight * value / max);
            int x = (int) (left + slot * index + (slot - barWidth) / 2);
            int y = bottom - barHeight;

            g2.setColor(barColor);
            g2.fillRect(x, y, barWidth, barHeight);

            if (value > 0) {
                String valueText = shortNumber(value);
                g2.setColor(UIStyle.TEXT);
                g2.drawString(valueText, x + (barWidth - fm.stringWidth(valueText)) / 2, y - 6);
            }

            String label = entry.getKey();
            g2.setColor(UIStyle.MUTED);
            g2.drawString(label, x + (barWidth - fm.stringWidth(label)) / 2, bottom + 22);

            index++;
        }

        g2.dispose();
    }

    private String shortNumber(double value) {
        if (value >= 1_000_000) {
            return String.format("%.1fM", value / 1_000_000);
        }
        if (value >= 1_000) {
            return String.format("%.0fK", value / 1_000);
        }
        return String.format("%.0f", value);
    }
}