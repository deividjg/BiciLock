package david.bicilock;

/**
 * Created by DJG on 24/04/2017.
 */

public class Bike {

    String serialNumber, brand, model, color, year, stolen, details;

    public Bike(String serialNumber, String brand, String model, String color, String year, String stolen, String details) {
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

    public String getStolen() {
        return stolen;
    }

    public void setStolen(String stolen) {
        this.stolen = stolen;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
