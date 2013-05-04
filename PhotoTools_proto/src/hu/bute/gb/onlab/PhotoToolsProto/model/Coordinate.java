package hu.bute.gb.onlab.PhotoToolsProto.model;

public class Coordinate {
	
	private double latitude_;
	private double longitude_;
	
	public Coordinate (double latitude, double longitude){
		latitude_ = latitude;
		longitude_ = longitude;
	}
	
	public double getLatitude() {
		return latitude_;
	}
	
	public double getLatitudeE6() {
		return (latitude_ * 1E6);
	}
	
	public void setLatitude(double latitude) {
		latitude_ = latitude;
	}
	
	public double getLongitude() {
		return longitude_;
	}
	
	public double getLongitudeE6() {
		return (longitude_ * 1E6);
	}
	
	public void setLongitude(double longitude) {
		longitude_ = longitude;
	}
	
	

}
