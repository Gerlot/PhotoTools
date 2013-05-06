package hu.bute.gb.onlab.PhotoTools.model;


public class Location {
	
	public int ID_;
	private String name_;
	private String address_;
	private Coordinate coordinate_;
	private boolean carEntry_;
	private boolean powerSource_;
	private String notes_;
	
	public Location(int ID, String name, String address, Coordinate coordinate, boolean carEntry,
			boolean powerSource, String notes) {
		ID_ = ID;
		name_ = name;
		address_ = address;
		coordinate_ = coordinate;
		carEntry_ = carEntry;
		powerSource_ = powerSource;
		notes_ = notes;
	}
	
	public int getID(){
		return ID_;
	}
	
	public void setID(int ID){
		ID_ = ID;
	}

	public String getName() {
		return name_;
	}
	
	public void setName(String name) {
		name_ = name;
	}
	
	public String getAddress() {
		return address_;
	}
	
	public void setAddress(String address) {
		address_ = address;
	}
	
	public Coordinate getCoordinate() {
		return coordinate_;
	}
	
	public void setCoordinate(Coordinate coordinate) {
		coordinate_ = coordinate;
	}
	
	public boolean hasCarEntry() {
		return carEntry_;
	}
	
	public void setCarEntry(boolean carEntry) {
		carEntry_ = carEntry;
	}
	
	public boolean hasPowerSource() {
		return powerSource_;
	}
	
	public void setPowerSource(boolean powerSource) {
		powerSource_ = powerSource;
	}
	
	public String getNotes() {
		return notes_;
	}
	
	public void setNotes(String notes) {
		notes_ = notes;
	}

}
