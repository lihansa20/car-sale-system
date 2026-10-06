package model;

import java.time.LocalDate;

public class Car {

    private int id;
    private String brand;
    private String model;
    private int year;
    private String color;
    private String fuelType;
    private double costPrice;
    private double sellingPrice;
    private String status;
    private LocalDate addedDate;

    public Car() {
    }

    public Car(int id, String brand, String model, int year, String color,
               String fuelType, double costPrice, double sellingPrice,
               String status, LocalDate addedDate) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.color = color;
        this.fuelType = fuelType;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
        this.status = status;
        this.addedDate = addedDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }

    public double getCostPrice() { return costPrice; }
    public void setCostPrice(double costPrice) { this.costPrice = costPrice; }

    public double getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(double sellingPrice) { this.sellingPrice = sellingPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getAddedDate() { return addedDate; }
    public void setAddedDate(LocalDate addedDate) { this.addedDate = addedDate; }

    @Override
    public String toString() {
        return brand + " " + model + " (" + year + ")";
    }
}