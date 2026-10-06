package dao;

import database.DBConnection;
import model.Sale;
import util.ValidationException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SaleDAO {


    public int addSale(Sale sale) throws SQLException, ValidationException {
        Connection con = DBConnection.getInstance().getConnection();

        try {
            con.setAutoCommit(false);   // start the transaction

            double costPrice;
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT cost_price, status FROM cars WHERE id = ? FOR UPDATE")) {
                ps.setInt(1, sale.getCarId());

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new ValidationException("The selected car does not exist.");
                    }
                    if (!"Available".equals(rs.getString("status"))) {
                        throw new ValidationException("This car has already been sold.");
                    }
                    costPrice = rs.getDouble("cost_price");
                }
            }

            double profit = sale.getSalePrice() - costPrice;
            sale.setProfit(profit);

            int newId;
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO sales (car_id, customer_id, user_id, sale_date, sale_price, profit) "
                            + "VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, sale.getCarId());
                ps.setInt(2, sale.getCustomerId());
                ps.setInt(3, sale.getUserId());
                ps.setDate(4, Date.valueOf(sale.getSaleDate()));
                ps.setDouble(5, sale.getSalePrice());
                ps.setDouble(6, profit);
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    newId = keys.next() ? keys.getInt(1) : -1;
                }
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE cars SET status = 'Sold' WHERE id = ?")) {
                ps.setInt(1, sale.getCarId());
                ps.executeUpdate();
            }

            con.commit();
            return newId;

        } catch (SQLException | ValidationException e) {
            con.rollback();
            throw e;

        } finally {
            con.setAutoCommit(true);
        }
    }

    public List<Sale> getAll() throws SQLException {
        String sql = "SELECT s.id, s.car_id, s.customer_id, s.user_id, s.sale_date, "
                + "s.sale_price, s.profit, "
                + "CONCAT(c.brand, ' ', c.model, ' (', c.year, ')') AS car_desc, "
                + "cu.name AS customer_name, u.full_name AS salesperson "
                + "FROM sales s "
                + "JOIN cars c ON s.car_id = c.id "
                + "JOIN customers cu ON s.customer_id = cu.id "
                + "JOIN users u ON s.user_id = u.id "
                + "ORDER BY s.sale_date DESC, s.id DESC";

        List<Sale> sales = new ArrayList<>();
        Connection con = DBConnection.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Sale s = new Sale(
                        rs.getInt("id"),
                        rs.getInt("car_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("user_id"),
                        rs.getDate("sale_date").toLocalDate(),
                        rs.getDouble("sale_price"),
                        rs.getDouble("profit"));
                s.setCarDescription(rs.getString("car_desc"));
                s.setCustomerName(rs.getString("customer_name"));
                s.setSalespersonName(rs.getString("salesperson"));
                sales.add(s);
            }
        }
        return sales;
    }
}