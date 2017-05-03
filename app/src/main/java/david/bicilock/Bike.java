package david.bicilock;

import java.io.Serializable;

public class Bike implements Serializable {

    String serialNumber, brand, model, color, year, details;
    int stolen;
    Long id;

    public Bike(Long id, String serialNumber, String brand, String model, String color, String year, int stolen, String details) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.year = year;
        this.stolen = stolen;
        this.details = details;
    }

    public Bike(){

    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getStolen() {
        return stolen;
    }

    public void setStolen(int stolen) {
        this.stolen = stolen;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
