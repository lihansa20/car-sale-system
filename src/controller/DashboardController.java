package controller;

import dao.DashboardDAO;

import java.sql.SQLException;
import java.util.Map;

public class DashboardController {

    private final DashboardDAO dashboardDAO = new DashboardDAO();

    public int getTotalCars() throws SQLException {
        return dashboardDAO.countCars();
    }

    public int getAvailableCars() throws SQLException {
        return dashboardDAO.countAvailableCars();
    }

    public int getTotalCustomers() throws SQLException {
        return dashboardDAO.countCustomers();
    }

    public int getTotalSales() throws SQLException {
        return dashboardDAO.countSales();
    }

    public double getTotalRevenue() throws SQLException {
        return dashboardDAO.totalRevenue();
    }

    public double getTotalProfit() throws SQLException {
        return dashboardDAO.totalProfit();
    }

    public Map<String, Double> getMonthlyRevenue() throws SQLException {
        return dashboardDAO.monthlyRevenue(6);
    }

    public Map<String, Integer> getSalesByBrand() throws SQLException {
        return dashboardDAO.salesByBrand();
    }
}