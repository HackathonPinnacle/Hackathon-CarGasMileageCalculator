package edu.umkc.mobile.cargasmileageestimator.data;

import java.util.Date;

public class MileageCollection {

	private String id; 
	
    private String distance;
    
    private String gasRemaining;
    
    private String range;
    
    private String mileage;
    
    private String date;
    
    private String car_id;

    private String totalDistance;

	private String totalGas;

	private String totalGasCost;


	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getGasRemaining() {
		return gasRemaining;
	}

	public void setGasRemaining(String gasRemaining) {
		this.gasRemaining = gasRemaining;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

	public String getMileage() {
		return mileage;
	}

	public void setMileage(String mileage) {
		this.mileage = mileage;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCar_id() {
		return car_id;
	}

	public void setCar_id(String car_id) {
		this.car_id = car_id;
	}

	public String getTotalDistance() {
		return totalDistance;
	}

	public void setTotalDistance(String totalDistance) {
		this.totalDistance = totalDistance;
	}

	public String getTotalGas() {
		return totalGas;
	}

	public void setTotalGas(String totalGas) {
		this.totalGas = totalGas;
	}

	public String getTotalGasCost() {
		return totalGasCost;
	}

	public void setTotalGasCost(String totalGasCost) {
		this.totalGasCost = totalGasCost;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}