package io.paus.tools.lucas.kmlviewer;

public class LucasPoint {
	
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public Float getLatitude() {
		return latitude;
	}
	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}
	public Float getLongitude() {
		return longitude;
	}
	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}

	public LucasPoint(String iD, Float latitude, Float longitude, String country, String altitude) {
		super();
		ID = iD;
		this.latitude = latitude;
		this.longitude = longitude;
		this.country = country;
		this.altitude = altitude;
	}

	String ID;
	Float latitude;
	Float longitude;
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getAltitude() {
		return altitude;
	}
	public void setAltitude(String altitude) {
		this.altitude = altitude;
	}

	String country;
	String altitude;

}
