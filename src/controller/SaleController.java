package controller;

import dao.SaleDAO;
import model.Car;
import model.Customer;
import model.Sale;
import model.User;
import util.ValidationException;
import util.Validator;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class SaleController {

    private final SaleDAO saleDAO = new SaleDAO();

    public List<Sale> getAllSales() throws SQLException {
        return saleDAO.getAll();
    }


    public int recordSale(Car car, Customer customer, String priceText)
            throws ValidationException, SQLException {

        User user = AuthController.getCurrentUser();
        if (user == null) {
            throw new ValidationException("Please log in before recording a sale.");
        }
        if (car == null) {
            throw new ValidationException("Please select a car.");
        }
        if (customer == null) {
            throw new ValidationException("Please select a customer.");
        }

        double price = Validator.parsePositiveAmount(priceText, "Sale price");

        Sale sale = new Sale();
        sale.setCarId(car.getId());
        sale.setCustomerId(customer.getId());
        sale.setUserId(user.getId());
        sale.setSaleDate(LocalDate.now());
        sale.setSalePrice(price);


        return saleDAO.addSale(sale);
    }
}