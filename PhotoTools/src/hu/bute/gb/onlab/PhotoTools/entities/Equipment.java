package hu.bute.gb.onlab.PhotoTools.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Equipment implements Comparable<Equipment>, Parcelable {

	private long ID_;
	private String name_;
	private String category_;
	private String notes_;
	private long lentTo_ = 0;
	private boolean isLent_ = false;

	public Equipment(long ID, String name, String category, String notes, long lentTo) {
		ID_ = ID;
		name_ = name;
		category_ = category;
		notes_ = notes;
		lentTo_ = lentTo;
		if (lentTo_ != 0) {
			isLent_ = true;
		}
	}
	
	public Equipment(Parcel in){
		ID_ = in.readLong();
		name_ = in.readString();
		category_ = in.readString();
		notes_ = in.readString();
		lentTo_ = in.readLong();
		if (lentTo_ != 0) {
			isLent_ = true;
		}
	}

	public long getID() {
		return ID_;
	}

	public void setID(int ID) {
		ID_ = ID;
	}

	public String getName() {
		return name_;
	}

	public void setName(String name) {
		name_ = name;
	}

	public String getCategory() {
		return category_;
	}

	public void setCategory(String category) {
		category_ = category;
	}

	public String getNotes() {
		return notes_;
	}

	public void setNotes(String notes) {
		notes_ = notes;
	}

	public long getLentTo() {
		return lentTo_;
	}

	public void setLentTo(long id) {
		lentTo_ = id;
		if (id == 0) {
			isLent_ = false;
		}
		else {
			isLent_ = true;
		}
	}
	
	public boolean isLent(){
		return isLent_;
	}

	@Override
	public int compareTo(Equipment anotherEquipment) {
		return name_.compareTo(anotherEquipment.getName());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(ID_);
		dest.writeString(name_);
		dest.writeString(category_);
		dest.writeString(notes_);
		dest.writeLong(lentTo_);
	}
	
	public static final Parcelable.Creator<Equipment> CREATOR = new Parcelable.Creator<Equipment>() {
		public Equipment createFromParcel(Parcel in) {
			return new Equipment(in);
		}

		public Equipment[] newArray(int size) {
			return new Equipment[size];
		}
	};
}
