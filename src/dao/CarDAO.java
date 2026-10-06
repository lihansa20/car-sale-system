package dao;

import database.DBConnection;
import model.Car;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CarDAO {

    private static final String SELECT_ALL =
            "SELECT id, brand, model, year, color, fuel_type, cost_price, "
                    + "selling_price, status, added_date FROM cars";


    public List<Car> getAll() throws SQLException {
        return query(SELECT_ALL + " ORDER BY id DESC", null);
    }

    public List<Car> getAvailable() throws SQLException {
        return query(SELECT_ALL + " WHERE status = 'Available' ORDER BY brand, model", null);
    }

    public List<Car> search(String keyword) throws SQLException {
        String sql = SELECT_ALL + " WHERE brand LIKE ? OR model LIKE ? ORDER BY id DESC";
        return query(sql, "%" + keyword.trim() + "%");
    }

    public Car getById(int id) throws SQLException {
        List<Car> list = query(SELECT_ALL + " WHERE id = ?", id);
        return list.isEmpty() ? null : list.get(0);
    }


    public int add(Car car) throws SQLException {
        String sql = "INSERT INTO cars (brand, model, year, color, fuel_type, "
                + "cost_price, selling_price, status, added_date) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection con = DBConnection.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, car.getBrand());
            ps.setString(2, car.getModel());
            ps.setInt(3, car.getYear());
            ps.setString(4, car.getColor());
            ps.setString(5, car.getFuelType());
            ps.setDouble(6, car.getCostPrice());
            ps.setDouble(7, car.getSellingPrice());
            ps.setString(8, car.getStatus());
            ps.setDate(9, Date.valueOf(car.getAddedDate()));
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    public void update(Car car) throws SQLException {
        String sql = "UPDATE cars SET brand = ?, model = ?, year = ?, color = ?, "
                + "fuel_type = ?, cost_price = ?, selling_price = ? WHERE id = ?";

        Connection con = DBConnection.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, car.getBrand());
            ps.setString(2, car.getModel());
            ps.setInt(3, car.getYear());
            ps.setString(4, car.getColor());
            ps.setString(5, car.getFuelType());
            ps.setDouble(6, car.getCostPrice());
            ps.setDouble(7, car.getSellingPrice());
            ps.setInt(8, car.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM cars WHERE id = ?";

        Connection con = DBConnection.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }


    private List<Car> query(String sql, Object param) throws SQLException {
        List<Car> cars = new ArrayList<>();
        Connection con = DBConnection.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            int placeholders = sql.length() - sql.replace("?", "").length();
            for (int i = 1; i <= placeholders; i++) {
                if (param instanceof Integer) {
                    ps.setInt(i, (Integer) param);
                } else {
                    ps.setString(i, (String) param);
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cars.add(mapRow(rs));
                }
            }
        }
        return cars;
    }

    private Car mapRow(ResultSet rs) throws SQLException {
        return new Car(
                rs.getInt("id"),
                rs.getString("brand"),
                rs.getString("model"),
                rs.getInt("year"),
                rs.getString("color"),
                rs.getString("fuel_type"),
                rs.getDouble("cost_price"),
                rs.getDouble("selling_price"),
                rs.getString("status"),
                rs.getDate("added_date").toLocalDate());
    }
}