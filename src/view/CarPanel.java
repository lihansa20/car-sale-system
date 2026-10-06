package view;

import controller.CarController;
import model.Car;
import util.UIStyle;
import util.ValidationException;

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
import java.util.ArrayList;
import java.util.List;

public class CarPanel extends JPanel {

    private final CarController controller = new CarController();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Brand", "Model", "Year", "Color", "Fuel",
                    "Cost (Rs.)", "Selling (Rs.)", "Status"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;   // the table is read-only, editing is done in the form
        }
    };
    private final JTable table = new JTable(tableModel);

    // the cars currently shown in the table (row number = position in this list)
    private List<Car> currentCars = new ArrayList<>();

    private final JTextField txtSearch = UIStyle.textField(18);
    private final JButton btnSearch = UIStyle.button("Search", UIStyle.PRIMARY);
    private final JButton btnShowAll = UIStyle.button("Show All", UIStyle.NEUTRAL);

    private final JTextField txtBrand = UIStyle.textField(16);
    private final JTextField txtModel = UIStyle.textField(16);
    private final JTextField txtYear = UIStyle.textField(16);
    private final JTextField txtColor = UIStyle.textField(16);
    private final JComboBox<String> cmbFuel =
            new JComboBox<>(new String[]{"Petrol", "Diesel", "Hybrid", "Electric"});
    private final JTextField txtCost = UIStyle.textField(16);
    private final JTextField txtSelling = UIStyle.textField(16);

    private final JButton btnAdd = UIStyle.button("Add", UIStyle.PRIMARY);
    private final JButton btnUpdate = UIStyle.button("Update", UIStyle.SUCCESS);
    private final JButton btnDelete = UIStyle.button("Delete", UIStyle.DANGER);
    private final JButton btnClear = UIStyle.button("Clear", UIStyle.NEUTRAL);

    public CarPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(UIStyle.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildForm(), BorderLayout.EAST);

        addEvents();
    }

    // reloads the table from the database (called every time this page is shown)
    public void refresh() {
        loadAllCars();
    }


    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Manage Cars");
        title.setFont(UIStyle.FONT_TITLE);
        title.setForeground(UIStyle.TEXT);
        header.add(title, BorderLayout.WEST);

        JPanel searchBar = new JPanel(new GridLayout(1, 3, 8, 0));
        searchBar.setOpaque(false);
        searchBar.add(txtSearch);
        searchBar.add(btnSearch);
        searchBar.add(btnShowAll);
        header.add(searchBar, BorderLayout.EAST);

        return header;
    }

    private JScrollPane buildTable() {
        UIStyle.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // column widths
        int[] widths = {45, 100, 110, 60, 80, 80, 120, 120, 90};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // prices aligned to the right
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(6).setCellRenderer(right);
        table.getColumnModel().getColumn(7).setCellRenderer(right);

        // status in color: green = Available, red = Sold
        table.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean selected, boolean focus, int row, int column) {
                super.getTableCellRendererComponent(t, value, selected, focus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(UIStyle.FONT_BOLD);
                if (selected) {
                    setForeground(UIStyle.TEXT);
                } else {
                    setForeground("Sold".equals(value) ? UIStyle.DANGER : UIStyle.SUCCESS);
                }
                return this;
            }
        });

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

        JLabel heading = new JLabel("Car details");
        heading.setFont(UIStyle.FONT_HEADING);
        heading.setForeground(UIStyle.TEXT);
        form.add(heading, gbc);

        cmbFuel.setFont(UIStyle.FONT_NORMAL);
        cmbFuel.setBackground(Color.WHITE);

        addField(form, gbc, "Brand", txtBrand);
        addField(form, gbc, "Model", txtModel);
        addField(form, gbc, "Year", txtYear);
        addField(form, gbc, "Color", txtColor);
        addField(form, gbc, "Fuel type", cmbFuel);
        addField(form, gbc, "Cost price (Rs.)", txtCost);
        addField(form, gbc, "Selling price (Rs.)", txtSelling);

        // the four buttons in a 2 x 2 grid
        JPanel buttons = new JPanel(new GridLayout(2, 2, 8, 8));
        buttons.setOpaque(false);
        buttons.add(btnAdd);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnClear);

        gbc.gridy++;
        gbc.insets = new Insets(18, 0, 0, 0);
        form.add(buttons, gbc);

        // scroll bar appears only if the window is too small for the form
        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setPreferredSize(new Dimension(350, 0));
        return scroll;
    }

    // a label above a field
    private void addField(JPanel form, GridBagConstraints gbc, String label, Component field) {
        gbc.gridy++;
        gbc.insets = new Insets(8, 0, 3, 0);
        form.add(UIStyle.label(label), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        form.add(field, gbc);
    }


    private void addEvents() {
        // clicking a row fills the form
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedCar();
            }
        });

        btnSearch.addActionListener(e -> doSearch());
        txtSearch.addActionListener(e -> doSearch());      // Enter key in the search box
        btnShowAll.addActionListener(e -> {
            txtSearch.setText("");
            loadAllCars();
        });

        btnAdd.addActionListener(e -> addCar());
        btnUpdate.addActionListener(e -> updateCar());
        btnDelete.addActionListener(e -> deleteCar());
        btnClear.addActionListener(e -> clearForm());
    }


    private void loadAllCars() {
        try {
            showCars(controller.getAllCars());
        } catch (SQLException e) {
            UIStyle.showError(this, "Could not load the cars.\n" + e.getMessage());
        }
    }

    private void doSearch() {
        try {
            showCars(controller.searchCars(txtSearch.getText()));
        } catch (SQLException e) {
            UIStyle.showError(this, "Search failed.\n" + e.getMessage());
        }
    }

    // puts a list of cars into the table
    private void showCars(List<Car> cars) {
        currentCars = cars;
        tableModel.setRowCount(0);

        for (Car car : cars) {
            tableModel.addRow(new Object[]{
                    car.getId(),
                    car.getBrand(),
                    car.getModel(),
                    car.getYear(),
                    car.getColor(),
                    car.getFuelType(),
                    UIStyle.money(car.getCostPrice()),
                    UIStyle.money(car.getSellingPrice()),
                    car.getStatus()
            });
        }
    }


    private void addCar() {
        try {
            controller.addCar(txtBrand.getText(), txtModel.getText(), txtYear.getText(),
                    txtColor.getText(), (String) cmbFuel.getSelectedItem(),
                    txtCost.getText(), txtSelling.getText());

            UIStyle.showInfo(this, "Car added successfully.");
            clearForm();
            loadAllCars();

        } catch (ValidationException e) {
            UIStyle.showError(this, e.getMessage());
        } catch (SQLException e) {
            UIStyle.showError(this, "Database error: " + e.getMessage());
        }
    }

    private void updateCar() {
        Car selected = selectedCar();
        if (selected == null) {
            UIStyle.showError(this, "Please select a car from the table first.");
            return;
        }
        if ("Sold".equals(selected.getStatus())) {
            UIStyle.showError(this, "A sold car cannot be edited.");
            return;
        }

        try {
            controller.updateCar(selected.getId(), txtBrand.getText(), txtModel.getText(),
                    txtYear.getText(), txtColor.getText(), (String) cmbFuel.getSelectedItem(),
                    txtCost.getText(), txtSelling.getText());

            UIStyle.showInfo(this, "Car updated successfully.");
            clearForm();
            loadAllCars();

        } catch (ValidationException e) {
            UIStyle.showError(this, e.getMessage());
        } catch (SQLException e) {
            UIStyle.showError(this, "Database error: " + e.getMessage());
        }
    }

    private void deleteCar() {
        Car selected = selectedCar();
        if (selected == null) {
            UIStyle.showError(this, "Please select a car from the table first.");
            return;
        }
        if (!UIStyle.confirm(this, "Delete " + selected + "?")) {
            return;
        }

        try {
            controller.deleteCar(selected.getId());

            UIStyle.showInfo(this, "Car deleted.");
            clearForm();
            loadAllCars();

        } catch (ValidationException e) {
            UIStyle.showError(this, e.getMessage());
        } catch (SQLException e) {
            UIStyle.showError(this, "Database error: " + e.getMessage());
        }
    }



    // the car of the selected table row, or null if nothing is selected
    private Car selectedCar() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentCars.size()) {
            return null;
        }
        return currentCars.get(row);
    }

    private void showSelectedCar() {
        Car car = selectedCar();
        if (car == null) {
            return;
        }
        txtBrand.setText(car.getBrand());
        txtModel.setText(car.getModel());
        txtYear.setText(String.valueOf(car.getYear()));
        txtColor.setText(car.getColor());
        cmbFuel.setSelectedItem(car.getFuelType());
        txtCost.setText(String.format("%.2f", car.getCostPrice()));
        txtSelling.setText(String.format("%.2f", car.getSellingPrice()));
    }

    private void clearForm() {
        txtBrand.setText("");
        txtModel.setText("");
        txtYear.setText("");
        txtColor.setText("");
        cmbFuel.setSelectedIndex(0);
        txtCost.setText("");
        txtSelling.setText("");
        table.clearSelection();
        txtBrand.requestFocus();
    }
}