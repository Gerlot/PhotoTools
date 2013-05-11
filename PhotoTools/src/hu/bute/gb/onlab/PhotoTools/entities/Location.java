package hu.bute.gb.onlab.PhotoTools.entities;

import hu.bute.gb.onlab.PhotoTools.helpers.Coordinate;
import android.os.Parcel;
import android.os.Parcelable;


public class Location implements Parcelable {
	
	private long ID_;
	private String name_;
	private String address_;
	private Coordinate coordinate_;
	private boolean carEntry_;
	private boolean powerSource_;
	private String notes_;
	
	public Location(long ID, String name, String address, Coordinate coordinate, boolean carEntry,
			boolean powerSource, String notes) {
		ID_ = ID;
		name_ = name;
		address_ = address;
		coordinate_ = coordinate;
		carEntry_ = carEntry;
		powerSource_ = powerSource;
		notes_ = notes;
	}
	
	public Location(Parcel in){
		ID_ = in.readLong();
		name_ = in.readString();
		address_ = in.readString();
		String latitude = in.readString();
		String longitude = in.readString();
		coordinate_ = new Coordinate(Double.parseDouble(latitude), Double.parseDouble(longitude));
		carEntry_ = Boolean.parseBoolean(in.readString());
		powerSource_ = Boolean.parseBoolean(in.readString());
		notes_ = in.readString();
	}
	
	public long getID(){
		return ID_;
	}
	
	public void setID(long ID){
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(ID_);
		dest.writeString(name_);
		dest.writeString(address_);
		dest.writeString(Double.toString(coordinate_.getLatitude()));
		dest.writeString(Double.toString(coordinate_.getLongitude()));
		dest.writeString(Boolean.toString(carEntry_));
		dest.writeString(Boolean.toString(powerSource_));
		dest.writeString(notes_);
	}
	
	public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
		public Location createFromParcel(Parcel in) {
			return new Location(in);
		}

		public Location[] newArray(int size) {
			return new Location[size];
		}
	};

}
