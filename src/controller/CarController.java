package controller;

import dao.CarDAO;
import model.Car;
import util.ValidationException;
import util.Validator;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.List;

public class CarController {

    private final CarDAO carDAO = new CarDAO();


    public List<Car> getAllCars() throws SQLException {
        return carDAO.getAll();
    }

    public List<Car> getAvailableCars() throws SQLException {
        return carDAO.getAvailable();
    }

    public List<Car> searchCars(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return carDAO.getAll();
        }
        return carDAO.search(keyword);
    }


    public int addCar(String brand, String model, String yearText, String color,
                      String fuelType, String costText, String sellingText)
            throws ValidationException, SQLException {

        Car car = buildCar(0, brand, model, yearText, color, fuelType, costText, sellingText);
        car.setStatus("Available");
        car.setAddedDate(LocalDate.now());
        return carDAO.add(car);
    }

    public void updateCar(int id, String brand, String model, String yearText, String color,
                          String fuelType, String costText, String sellingText)
            throws ValidationException, SQLException {

        Car car = buildCar(id, brand, model, yearText, color, fuelType, costText, sellingText);
        carDAO.update(car);
    }

    public void deleteCar(int id) throws ValidationException, SQLException {
        try {
            carDAO.delete(id);
        } catch (SQLIntegrityConstraintViolationException e) {
            // the car is linked to a sale record, so the database refuses to delete it
            throw new ValidationException("This car cannot be deleted because it has a sale record.");
        }
    }

    private Car buildCar(int id, String brand, String model, String yearText, String color,
                         String fuelType, String costText, String sellingText)
            throws ValidationException {

        Validator.requireText(brand, "Brand");
        Validator.requireMaxLength(brand, 50, "Brand");

        Validator.requireText(model, "Model");
        Validator.requireMaxLength(model, 50, "Model");

        int year = Validator.parseYear(yearText);

        Validator.requireMaxLength(color, 30, "Color");

        Validator.requireText(fuelType, "Fuel type");

        double cost = Validator.parsePositiveAmount(costText, "Cost price");
        double selling = Validator.parsePositiveAmount(sellingText, "Selling price");
        Validator.validateSellingPrice(cost, selling);

        Car car = new Car();
        car.setId(id);
        car.setBrand(brand.trim());
        car.setModel(model.trim());
        car.setYear(year);
        car.setColor(color == null ? "" : color.trim());
        car.setFuelType(fuelType.trim());
        car.setCostPrice(cost);
        car.setSellingPrice(selling);
        return car;
    }
}