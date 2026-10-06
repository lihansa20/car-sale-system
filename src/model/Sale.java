package model;

import java.time.LocalDate;

public class Sale {

    private int id;
    private int carId;
    private int customerId;
    private int userId;
    private LocalDate saleDate;
    private double salePrice;
    private double profit;

    // display-only fields, filled when a sale is loaded with a JOIN
    private String carDescription;
    private String customerName;
    private String salespersonName;

    public Sale() {
    }

    public Sale(int id, int carId, int customerId, int userId,
                LocalDate saleDate, double salePrice, double profit) {
        this.id = id;
        this.carId = carId;
        this.customerId = customerId;
        this.userId = userId;
        this.saleDate = saleDate;
        this.salePrice = salePrice;
        this.profit = profit;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCarId() { return carId; }
    public void setCarId(int carId) { this.carId = carId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public LocalDate getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDate saleDate) { this.saleDate = saleDate; }

    public double getSalePrice() { return salePrice; }
    public void setSalePrice(double salePrice) { this.salePrice = salePrice; }

    public double getProfit() { return profit; }
    public void setProfit(double profit) { this.profit = profit; }

    public String getCarDescription() { return carDescription; }
    public void setCarDescription(String carDescription) { this.carDescription = carDescription; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getSalespersonName() { return salespersonName; }
    public void setSalespersonName(String salespersonName) { this.salespersonName = salespersonName; }
}