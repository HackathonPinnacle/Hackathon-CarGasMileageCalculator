package edu.umkc.mobile.cargasmileageestimator.data;

/**
 * Created by Esha Mayuri on 11/12/2017.
 */

public class CarDetailsCollection {

    private String id;
    private String emailId;
    private String make;
    private String model;
    private String year;
    private String odometer;
    private String tankCapacity;
    private String mileage;
    private String fuel;
    private String totalGasCost;
    private String totalGas;

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getOdometer() {
        return odometer;
    }

    public void setOdometer(String odometer) {
        this.odometer = odometer;
    }

    public String getTankCapacity() {
        return tankCapacity;
    }

    public void setTankCapacity(String tankCapacity) {
        this.tankCapacity = tankCapacity;
    }


    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }



    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTotalGasCost() {
        return totalGasCost;
    }

    public void setTotalGasCost(String totalGasCost) {
        this.totalGasCost = totalGasCost;
    }

    public String getTotalGas() {
        return totalGas;
    }

    public void setTotalGas(String totalGas) {
        this.totalGas = totalGas;
    }
}
