package view;

import controller.CustomerController;
import model.Customer;
import util.UIStyle;
import util.ValidationException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
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

public class CustomerPanel extends JPanel {

    private final CustomerController controller = new CustomerController();

    // ---------- table ----------
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Name", "NIC", "Phone", "Email", "Address"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;   // the table is read-only, editing is done in the form
        }
    };
    private final JTable table = new JTable(tableModel);

    // the customers currently shown in the table (row number = position in this list)
    private List<Customer> currentCustomers = new ArrayList<>();

    // ---------- search ----------
    private final JTextField txtSearch = UIStyle.textField(18);
    private final JButton btnSearch = UIStyle.button("Search", UIStyle.PRIMARY);
    private final JButton btnShowAll = UIStyle.button("Show All", UIStyle.NEUTRAL);

    // ---------- form ----------
    private final JTextField txtName = UIStyle.textField(16);
    private final JTextField txtNic = UIStyle.textField(16);
    private final JTextField txtPhone = UIStyle.textField(16);
    private final JTextField txtEmail = UIStyle.textField(16);
    private final JTextField txtAddress = UIStyle.textField(16);

    private final JButton btnAdd = UIStyle.button("Add", UIStyle.PRIMARY);
    private final JButton btnUpdate = UIStyle.button("Update", UIStyle.SUCCESS);
    private final JButton btnDelete = UIStyle.button("Delete", UIStyle.DANGER);
    private final JButton btnClear = UIStyle.button("Clear", UIStyle.NEUTRAL);

    public CustomerPanel() {
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
        loadAllCustomers();
    }

    // ---------- building the screen ----------

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Manage Customers");
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
        int[] widths = {45, 150, 120, 100, 170, 180};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

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

        JLabel heading = new JLabel("Customer details");
        heading.setFont(UIStyle.FONT_HEADING);
        heading.setForeground(UIStyle.TEXT);
        form.add(heading, gbc);

        addField(form, gbc, "Full name", txtName);
        addField(form, gbc, "NIC", txtNic);
        addField(form, gbc, "Phone (10 digits)", txtPhone);
        addField(form, gbc, "Email (optional)", txtEmail);
        addField(form, gbc, "Address", txtAddress);

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

    // ---------- events ----------

    private void addEvents() {
        // clicking a row fills the form
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedCustomer();
            }
        });

        btnSearch.addActionListener(e -> doSearch());
        txtSearch.addActionListener(e -> doSearch());      // Enter key in the search box
        btnShowAll.addActionListener(e -> {
            txtSearch.setText("");
            loadAllCustomers();
        });

        btnAdd.addActionListener(e -> addCustomer());
        btnUpdate.addActionListener(e -> updateCustomer());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnClear.addActionListener(e -> clearForm());
    }

    // ---------- loading ----------

    private void loadAllCustomers() {
        try {
            showCustomers(controller.getAllCustomers());
        } catch (SQLException e) {
            UIStyle.showError(this, "Could not load the customers.\n" + e.getMessage());
        }
    }

    private void doSearch() {
        try {
            showCustomers(controller.searchCustomers(txtSearch.getText()));
        } catch (SQLException e) {
            UIStyle.showError(this, "Search failed.\n" + e.getMessage());
        }
    }

    // puts a list of customers into the table
    private void showCustomers(List<Customer> customers) {
        currentCustomers = customers;
        tableModel.setRowCount(0);

        for (Customer c : customers) {
            tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getName(),
                    c.getNic(),
                    c.getPhone(),
                    c.getEmail(),
                    c.getAddress()
            });
        }
    }

    // ---------- actions ----------

    private void addCustomer() {
        try {
            controller.addCustomer(txtName.getText(), txtNic.getText(), txtPhone.getText(),
                    txtEmail.getText(), txtAddress.getText());

            UIStyle.showInfo(this, "Customer added successfully.");
            clearForm();
            loadAllCustomers();

        } catch (ValidationException e) {
            UIStyle.showError(this, e.getMessage());
        } catch (SQLException e) {
            UIStyle.showError(this, "Database error: " + e.getMessage());
        }
    }

    private void updateCustomer() {
        Customer selected = selectedCustomer();
        if (selected == null) {
            UIStyle.showError(this, "Please select a customer from the table first.");
            return;
        }

        try {
            controller.updateCustomer(selected.getId(), txtName.getText(), txtNic.getText(),
                    txtPhone.getText(), txtEmail.getText(), txtAddress.getText());

            UIStyle.showInfo(this, "Customer updated successfully.");
            clearForm();
            loadAllCustomers();

        } catch (ValidationException e) {
            UIStyle.showError(this, e.getMessage());
        } catch (SQLException e) {
            UIStyle.showError(this, "Database error: " + e.getMessage());
        }
    }

    private void deleteCustomer() {
        Customer selected = selectedCustomer();
        if (selected == null) {
            UIStyle.showError(this, "Please select a customer from the table first.");
            return;
        }
        if (!UIStyle.confirm(this, "Delete " + selected.getName() + "?")) {
            return;
        }

        try {
            controller.deleteCustomer(selected.getId());

            UIStyle.showInfo(this, "Customer deleted.");
            clearForm();
            loadAllCustomers();

        } catch (ValidationException e) {
            UIStyle.showError(this, e.getMessage());
        } catch (SQLException e) {
            UIStyle.showError(this, "Database error: " + e.getMessage());
        }
    }

    // ---------- form helpers ----------

    // the customer of the selected table row, or null if nothing is selected
    private Customer selectedCustomer() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentCustomers.size()) {
            return null;
        }
        return currentCustomers.get(row);
    }

    private void showSelectedCustomer() {
        Customer c = selectedCustomer();
        if (c == null) {
            return;
        }
        txtName.setText(c.getName());
        txtNic.setText(c.getNic());
        txtPhone.setText(c.getPhone());
        txtEmail.setText(c.getEmail());
        txtAddress.setText(c.getAddress());
    }

    private void clearForm() {
        txtName.setText("");
        txtNic.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtAddress.setText("");
        table.clearSelection();
        txtName.requestFocus();
    }
}