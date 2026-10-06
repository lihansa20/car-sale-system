package view;

import controller.ReportController;
import net.sf.jasperreports.engine.JRException;
import util.UIStyle;
import util.ValidationException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class ReportPanel extends JPanel {

    // one report action:create this report and save it into this file
    private interface ReportAction {
        void run(File file) throws ValidationException, SQLException, JRException;
    }

    private final ReportController controller = new ReportController();

    // sales report
    private final JTextField txtFrom = UIStyle.textField(12);
    private final JTextField txtTo = UIStyle.textField(12);

    private final JButton btnThisMonth = UIStyle.button("This month", UIStyle.NEUTRAL);
    private final JButton btnLast30 = UIStyle.button("Last 30 days", UIStyle.NEUTRAL);
    private final JButton btnThisYear = UIStyle.button("This year", UIStyle.NEUTRAL);
    private final JButton btnAllTime = UIStyle.button("All time", UIStyle.NEUTRAL);

    private final JButton btnSalesPreview = UIStyle.button("Preview Report", UIStyle.PRIMARY);
    private final JButton btnSalesSave = UIStyle.button("Save as PDF", UIStyle.SUCCESS);

    // stock report
    private static final String[] STOCK_CHOICES = {"All cars", "Available only", "Sold only"};
    private static final String[] STOCK_STATUS = {"All", "Available", "Sold"};

    private final JComboBox<String> cmbStock = new JComboBox<>(STOCK_CHOICES);

    private final JButton btnStockPreview = UIStyle.button("Preview Report", UIStyle.PRIMARY);
    private final JButton btnStockSave = UIStyle.button("Save as PDF", UIStyle.SUCCESS);

    public ReportPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(UIStyle.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("Sales & Reports");
        title.setFont(UIStyle.FONT_TITLE);
        title.setForeground(UIStyle.TEXT);
        add(title, BorderLayout.NORTH);

        // the two report cards one under the other, with a scroll bar if the window is short
        JPanel cards = new JPanel();
        cards.setLayout(new BoxLayout(cards, BoxLayout.Y_AXIS));
        cards.setOpaque(false);
        cards.add(leftAligned(buildSalesCard()));
        cards.add(Box.createVerticalStrut(16));
        cards.add(leftAligned(buildStockCard()));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(cards, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(top);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        addEvents();
        setRange(LocalDate.now().withDayOfYear(1), LocalDate.now());   // default: this year
    }

    // building the screen

    private JPanel buildSalesCard() {
        JPanel card = newCard();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        card.add(heading("Sales Report"), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(6, 0, 18, 0);
        card.add(description("Lists every sale in the chosen period with the car and customer, "
                + "and shows the total revenue and profit at the end."), gbc);

        // the two date fields side by side
        JPanel dates = new JPanel(new GridLayout(2, 2, 12, 4));
        dates.setOpaque(false);
        dates.add(UIStyle.label("From (yyyy-MM-dd)"));
        dates.add(UIStyle.label("To (yyyy-MM-dd)"));
        dates.add(txtFrom);
        dates.add(txtTo);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 14, 0);
        card.add(dates, gbc);

        // quick ranges
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 4, 0);
        card.add(UIStyle.label("Quick ranges"), gbc);

        JPanel quick = new JPanel(new GridLayout(1, 4, 8, 0));
        quick.setOpaque(false);
        quick.add(btnThisMonth);
        quick.add(btnLast30);
        quick.add(btnThisYear);
        quick.add(btnAllTime);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 22, 0);
        card.add(quick, gbc);

        JPanel actions = new JPanel(new GridLayout(1, 2, 12, 0));
        actions.setOpaque(false);
        actions.add(btnSalesPreview);
        actions.add(btnSalesSave);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(actions, gbc);

        return card;
    }

    private JPanel buildStockCard() {
        JPanel card = newCard();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        card.add(heading("Car Stock Report"), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(6, 0, 18, 0);
        card.add(description("Lists the cars with their cost and listed price, and shows "
                + "the total stock value and the potential profit at the end."), gbc);

        cmbStock.setFont(UIStyle.FONT_NORMAL);
        cmbStock.setBackground(Color.WHITE);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 4, 0);
        card.add(UIStyle.label("Cars to include"), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 22, 0);
        card.add(cmbStock, gbc);

        JPanel actions = new JPanel(new GridLayout(1, 2, 12, 0));
        actions.setOpaque(false);
        actions.add(btnStockPreview);
        actions.add(btnStockSave);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(actions, gbc);

        return card;
    }

    // a white card with a border
    private JPanel newCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UIStyle.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyle.BORDER),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)));
        return card;
    }

    private JLabel heading(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIStyle.FONT_HEADING);
        label.setForeground(UIStyle.TEXT);
        return label;
    }

    private JLabel description(String text) {
        JLabel label = new JLabel("<html><body style='width:460px'>" + text + "</body></html>");
        label.setFont(UIStyle.FONT_NORMAL);
        label.setForeground(UIStyle.MUTED);
        return label;
    }

    // keeps a card at its normal width, at the left side of the page
    private JPanel leftAligned(JPanel card) {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.add(card);
        return wrapper;
    }

    //events

    private void addEvents() {
        LocalDate today = LocalDate.now();

        btnThisMonth.addActionListener(e -> setRange(today.withDayOfMonth(1), today));
        btnLast30.addActionListener(e -> setRange(today.minusDays(30), today));
        btnThisYear.addActionListener(e -> setRange(today.withDayOfYear(1), today));
        btnAllTime.addActionListener(e -> setRange(LocalDate.of(2000, 1, 1), today));

        // sales report buttons
        ReportAction salesReport = file ->
                controller.exportSalesReport(txtFrom.getText(), txtTo.getText(), file);
        btnSalesPreview.addActionListener(e -> preview(salesReport));
        btnSalesSave.addActionListener(e -> saveAsPdf("sales_report", salesReport));

        // stock report buttons
        ReportAction stockReport = file ->
                controller.exportStockReport(STOCK_STATUS[cmbStock.getSelectedIndex()], file);
        btnStockPreview.addActionListener(e -> preview(stockReport));
        btnStockSave.addActionListener(e -> saveAsPdf("stock_report", stockReport));
    }

    private void setRange(LocalDate from, LocalDate to) {
        txtFrom.setText(from.toString());
        txtTo.setText(to.toString());
    }

    //actions

    // creates the PDF in a temporary file and opens it in the PDF viewer
    private void preview(ReportAction action) {
        try {
            File temp = File.createTempFile("report_", ".pdf");
            temp.deleteOnExit();

            if (generate(action, temp)) {
                openFile(temp);
            }
        } catch (IOException e) {
            UIStyle.showError(this, "Could not create the temporary report file.\n" + e.getMessage());
        }
    }

    // lets the user choose where to save the PDF
    private void saveAsPdf(String namePrefix, ReportAction action) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save report");
        chooser.setFileFilter(new FileNameExtensionFilter("PDF files (*.pdf)", "pdf"));
        chooser.setSelectedFile(new File(namePrefix + "_" + LocalDate.now() + ".pdf"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".pdf")) {
            file = new File(file.getAbsolutePath() + ".pdf");
        }
        if (file.exists() && !UIStyle.confirm(this, file.getName() + " already exists. Replace it?")) {
            return;
        }

        if (generate(action, file)) {
            UIStyle.showInfo(this, "Report saved:\n" + file.getAbsolutePath());
            openFile(file);
        }
    }

    // runs the report action; returns true if it worked
    private boolean generate(ReportAction action, File file) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            action.run(file);
            return true;

        } catch (ValidationException e) {
            UIStyle.showError(this, e.getMessage());
        } catch (SQLException e) {
            UIStyle.showError(this, "Database error: " + e.getMessage());
        } catch (JRException e) {
            UIStyle.showError(this, "Could not create the report.\n" + e.getMessage());
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
        return false;
    }

    private void openFile(File file) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            } else {
                UIStyle.showInfo(this, "The report was created here:\n" + file.getAbsolutePath());
            }
        } catch (IOException e) {
            UIStyle.showError(this, "The report was created but could not be opened.\n"
                    + "You can find it here:\n" + file.getAbsolutePath());
        }
    }
}