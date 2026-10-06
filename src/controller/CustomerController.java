package controller;

import dao.CustomerDAO;
import model.Customer;
import util.ValidationException;
import util.Validator;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public class CustomerController {

    private final CustomerDAO customerDAO = new CustomerDAO();


    public List<Customer> getAllCustomers() throws SQLException {
        return customerDAO.getAll();
    }

    public List<Customer> searchCustomers(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return customerDAO.getAll();
        }
        return customerDAO.search(keyword);
    }


    public int addCustomer(String name, String nic, String phone, String email, String address)
            throws ValidationException, SQLException {

        Customer customer = buildCustomer(0, name, nic, phone, email, address);

        if (customerDAO.nicExists(customer.getNic(), 0)) {
            throw new ValidationException("This NIC is already registered.");
        }
        return customerDAO.add(customer);
    }

    public void updateCustomer(int id, String name, String nic, String phone, String email, String address)
            throws ValidationException, SQLException {

        Customer customer = buildCustomer(id, name, nic, phone, email, address);

        if (customerDAO.nicExists(customer.getNic(), id)) {
            throw new ValidationException("Another customer already uses this NIC.");
        }
        customerDAO.update(customer);
    }

    public void deleteCustomer(int id) throws ValidationException, SQLException {
        try {
            customerDAO.delete(id);
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new ValidationException("This customer cannot be deleted because they have a sale record.");
        }
    }


    private Customer buildCustomer(int id, String name, String nic, String phone,
                                   String email, String address)
            throws ValidationException {

        Validator.requireText(name, "Name");
        Validator.requireMaxLength(name, 100, "Name");

        Validator.validateNic(nic);
        Validator.validatePhone(phone);

        Validator.validateEmail(email);
        Validator.requireMaxLength(email, 100, "Email");

        Validator.requireMaxLength(address, 255, "Address");

        Customer customer = new Customer();
        customer.setId(id);
        customer.setName(name.trim());
        customer.setNic(nic.trim().toUpperCase());
        customer.setPhone(phone.trim());
        customer.setEmail(email == null ? "" : email.trim());
        customer.setAddress(address == null ? "" : address.trim());
        return customer;
    }
}