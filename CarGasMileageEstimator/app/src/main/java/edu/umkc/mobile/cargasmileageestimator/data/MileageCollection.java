package edu.umkc.mobile.cargasmileageestimator.data;

import java.util.ArrayList;
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

	protected String unit;


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

	protected ArrayList<Double> originsLatitudes;
	protected ArrayList<Double> originsLongitudes;

	public MileageCollection() {
		originsLatitudes = new ArrayList<Double>();
		originsLongitudes = new ArrayList<Double>();
	}
	/**
	 * Add a coordinate to the list
	 *
	 * @param latitude
	 * @param longitude
	 */
	public void addCordinate(double latitude, double longitude) {
		originsLatitudes.add(latitude);
		originsLongitudes.add(longitude);
	}

	/**
	 * Get the starting latitude
	 *
	 * @return
	 */
	public double getStartingLatitude() {
		return originsLatitudes.get(0);
	}

	/**
	 * Get the starting longitude coordinate
	 *
	 * @return
	 */
	public double getStartingLongitude() {
		return originsLongitudes.get(0);
	}

	public double getPreviousLatitude() {
		return originsLatitudes.size() > 0 ? originsLatitudes.get(originsLatitudes.size()-1) : 0;
	}

	public double getPreviousLongitude() {
		return originsLongitudes.size() > 0 ? originsLongitudes.get(originsLongitudes.size()-1) : 0;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
}