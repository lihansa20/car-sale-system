package view;

import controller.CarController;
import controller.CustomerController;
import controller.SaleController;
import model.Car;
import model.Customer;
import model.Sale;
import util.UIStyle;
import util.ValidationException;
import util.Validator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.List;

public class SalePanel extends JPanel {

    private final CarController carController = new CarController();
    private final CustomerController customerController = new CustomerController();
    private final SaleController saleController = new SaleController();

    //sales history table
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Date", "Car", "Customer", "Price (Rs.)", "Profit (Rs.)"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    //form
    private final JComboBox<Car> cmbCar = new JComboBox<>();
    private final JComboBox<Customer> cmbCustomer = new JComboBox<>();
    private final JLabel lblCost = infoValue();
    private final JLabel lblListed = infoValue();
    private final JTextField txtPrice = UIStyle.textField(16);
    private final JLabel lblProfit = infoValue();

    private final JButton btnRecord = UIStyle.button("Record Sale", UIStyle.SUCCESS);
    private final JButton btnClear = UIStyle.button("Clear", UIStyle.NEUTRAL);

    // true while the dropdowns are being reloaded (so their events are ignored)
    private boolean loading = false;

    public SalePanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(UIStyle.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildForm(), BorderLayout.EAST);

        addEvents();
    }

    // reloads the dropdowns and the history (called every time this page is shown)
    public void refresh() {
        loading = true;
        try {
            cmbCar.removeAllItems();
            for (Car car : carController.getAvailableCars()) {
                cmbCar.addItem(car);
            }

            cmbCustomer.removeAllItems();
            for (Customer customer : customerController.getAllCustomers()) {
                cmbCustomer.addItem(customer);
            }

            showSales(saleController.getAllSales());

        } catch (SQLException e) {
            UIStyle.showError(this, "Could not load the sale data.\n" + e.getMessage());
        } finally {
            loading = false;
        }
        clearForm();
    }

    //building the screen

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("New Sale");
        title.setFont(UIStyle.FONT_TITLE);
        title.setForeground(UIStyle.TEXT);
        header.add(title, BorderLayout.WEST);

        JLabel subtitle = new JLabel("Sales history");
        subtitle.setFont(UIStyle.FONT_HEADING);
        subtitle.setForeground(UIStyle.MUTED);
        header.add(subtitle, BorderLayout.EAST);

        return header;
    }

    private JScrollPane buildTable() {
        UIStyle.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        int[] widths = {45, 90, 220, 170, 130, 130};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // money columns aligned to the right
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(4).setCellRenderer(right);
        table.getColumnModel().getColumn(5).setCellRenderer(right);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UIStyle.BORDER));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    private JScrollPane buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIStyle.CARD);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyle.BORDER),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel heading = new JLabel("Sale details");
        heading.setFont(UIStyle.FONT_HEADING);
        heading.setForeground(UIStyle.TEXT);
        form.add(heading, gbc);

        cmbCar.setFont(UIStyle.FONT_NORMAL);
        cmbCar.setBackground(Color.WHITE);
        cmbCustomer.setFont(UIStyle.FONT_NORMAL);
        cmbCustomer.setBackground(Color.WHITE);

        addField(form, gbc, "Car (available only)", cmbCar);
        addField(form, gbc, "Customer", cmbCustomer);

        // read-only information about the selected car
        gbc.gridy++;
        gbc.insets = new Insets(14, 0, 0, 0);
        form.add(infoRow("Cost price (Rs.)", lblCost), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(4, 0, 0, 0);
        form.add(infoRow("Listed price (Rs.)", lblListed), gbc);

        addField(form, gbc, "Sale price (Rs.)", txtPrice);

        gbc.gridy++;
        gbc.insets = new Insets(14, 0, 0, 0);
        form.add(infoRow("Profit", lblProfit), gbc);

        // buttons
        JPanel buttons = new JPanel(new GridLayout(1, 2, 8, 0));
        buttons.setOpaque(false);
        buttons.add(btnRecord);
        buttons.add(btnClear);

        gbc.gridy++;
        gbc.insets = new Insets(18, 0, 0, 0);
        form.add(buttons, gbc);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setPreferredSize(new Dimension(350, 0));
        return scroll;
    }

    // a label above a field
    private void addField(JPanel form, GridBagConstraints gbc, String label, Component field) {
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 3, 0);
        form.add(UIStyle.label(label), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        form.add(field, gbc);
    }

    // caption on the left, value on the right
    private JPanel infoRow(String caption, JLabel value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JLabel captionLabel = new JLabel(caption);
        captionLabel.setFont(UIStyle.FONT_NORMAL);
        captionLabel.setForeground(UIStyle.MUTED);

        row.add(captionLabel, BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);
        return row;
    }

    private static JLabel infoValue() {
        JLabel label = new JLabel("-");
        label.setFont(UIStyle.FONT_BOLD);
        label.setForeground(UIStyle.TEXT);
        return label;
    }

    //events

    private void addEvents() {
        // choosing a car fills the prices
        cmbCar.addActionListener(e -> {
            if (!loading) {
                onCarSelected();
            }
        });

        // typing a price updates the profit preview
        txtPrice.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateProfit();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateProfit();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateProfit();
            }
        });

        btnRecord.addActionListener(e -> recordSale());
        btnClear.addActionListener(e -> clearForm());
    }

    //table

    private void showSales(List<Sale> sales) {
        tableModel.setRowCount(0);

        for (Sale s : sales) {
            tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getSaleDate(),
                    s.getCarDescription(),
                    s.getCustomerName(),
                    UIStyle.money(s.getSalePrice()),
                    UIStyle.money(s.getProfit())
            });
        }
    }

    //form logic

    private void onCarSelected() {
        Car car = (Car) cmbCar.getSelectedItem();

        if (car == null) {
            lblCost.setText("-");
            lblListed.setText("-");
            txtPrice.setText("");
            return;
        }

        lblCost.setText(UIStyle.money(car.getCostPrice()));
        lblListed.setText(UIStyle.money(car.getSellingPrice()));
        txtPrice.setText(String.format("%.2f", car.getSellingPrice()));
        updateProfit();
    }

    private void updateProfit() {
        Car car = (Car) cmbCar.getSelectedItem();

        if (car == null) {
            lblProfit.setText("-");
            lblProfit.setForeground(UIStyle.TEXT);
            return;
        }

        try {
            double price = Double.parseDouble(txtPrice.getText().trim().replace(",", ""));
            double profit = price - car.getCostPrice();

            lblProfit.setText(UIStyle.money(profit));
            lblProfit.setForeground(profit >= 0 ? UIStyle.SUCCESS : UIStyle.DANGER);

        } catch (NumberFormatException e) {
            lblProfit.setText("-");
            lblProfit.setForeground(UIStyle.TEXT);
        }
    }

    private void recordSale() {
        Car car = (Car) cmbCar.getSelectedItem();
        Customer customer = (Customer) cmbCustomer.getSelectedItem();

        try {
            if (car == null) {
                throw new ValidationException("Please select a car.");
            }
            if (customer == null) {
                throw new ValidationException("Please select a customer.");
            }
            double price = Validator.parsePositiveAmount(txtPrice.getText(), "Sale price");
            double profit = price - car.getCostPrice();

            String message = "Record this sale?\n\n"
                    + "Car: " + car + "\n"
                    + "Customer: " + customer.getName() + "\n"
                    + "Price: Rs. " + UIStyle.money(price) + "\n"
                    + "Profit: Rs. " + UIStyle.money(profit);
            if (profit < 0) {
                message += "\n\nWarning: this price is below the cost price (a loss).";
            }
            if (!UIStyle.confirm(this, message)) {
                return;
            }

            int saleId = saleController.recordSale(car, customer, txtPrice.getText());

            UIStyle.showInfo(this, "Sale #" + saleId + " recorded successfully.\n"
                    + "Profit: Rs. " + UIStyle.money(profit));
            refresh();

        } catch (ValidationException e) {
            UIStyle.showError(this, e.getMessage());
        } catch (SQLException e) {
            UIStyle.showError(this, "Database error: " + e.getMessage());
        }
    }

    private void clearForm() {
        cmbCar.setSelectedIndex(-1);
        cmbCustomer.setSelectedIndex(-1);
        lblCost.setText("-");
        lblListed.setText("-");
        txtPrice.setText("");
        lblProfit.setText("-");
        lblProfit.setForeground(UIStyle.TEXT);
    }
}