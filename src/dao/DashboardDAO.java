package dao;

import database.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardDAO {


    public int countCars() throws SQLException {
        return (int) scalar("SELECT COUNT(*) FROM cars");
    }

    public int countAvailableCars() throws SQLException {
        return (int) scalar("SELECT COUNT(*) FROM cars WHERE status = 'Available'");
    }

    public int countCustomers() throws SQLException {
        return (int) scalar("SELECT COUNT(*) FROM customers");
    }

    public int countSales() throws SQLException {
        return (int) scalar("SELECT COUNT(*) FROM sales");
    }

    public double totalRevenue() throws SQLException {
        return scalar("SELECT COALESCE(SUM(sale_price), 0) FROM sales");
    }

    public double totalProfit() throws SQLException {
        return scalar("SELECT COALESCE(SUM(profit), 0) FROM sales");
    }


    public Map<String, Double> monthlyRevenue(int months) throws SQLException {

        // start with every month at 0, oldest first, so the chart has no gaps
        Map<String, Double> result = new LinkedHashMap<>();
        YearMonth start = YearMonth.now().minusMonths(months - 1);
        for (int i = 0; i < months; i++) {
            result.put(start.plusMonths(i).toString(), 0.0);
        }

        String sql = "SELECT DATE_FORMAT(sale_date, '%Y-%m') AS ym, SUM(sale_price) AS total "
                + "FROM sales WHERE sale_date >= ? "
                + "GROUP BY ym ORDER BY ym";

        Connection con = DBConnection.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(start.atDay(1)));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString("ym"), rs.getDouble("total"));
                }
            }
        }
        return result;
    }

    public Map<String, Integer> salesByBrand() throws SQLException {
        String sql = "SELECT c.brand, COUNT(*) AS total "
                + "FROM sales s JOIN cars c ON s.car_id = c.id "
                + "GROUP BY c.brand ORDER BY total DESC, c.brand";

        Map<String, Integer> result = new LinkedHashMap<>();
        Connection con = DBConnection.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.put(rs.getString("brand"), rs.getInt("total"));
            }
        }
        return result;
    }


    private double scalar(String sql) throws SQLException {
        Connection con = DBConnection.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0;
        }
    }
}