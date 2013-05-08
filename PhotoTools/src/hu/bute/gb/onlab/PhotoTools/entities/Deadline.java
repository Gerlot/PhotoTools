package hu.bute.gb.onlab.PhotoTools.entities;

import org.joda.time.DateTime;

import android.os.Parcel;
import android.os.Parcelable;

public class Deadline implements Comparable<Deadline>, Parcelable {

	private long ID_;
	private String name_;
	private DateTime startTime_;
	private DateTime endTime_;
	private boolean isAllDay_ = false;
	private String location_;
	private String notes_;

	public Deadline(long ID, String name, DateTime startTime, DateTime endTime, boolean isAllDay,
			String location, String notes) {
		ID_ = ID;
		name_ = name;
		startTime_ = startTime;
		endTime_ = endTime;
		isAllDay_ = isAllDay;
		location_ = location;
		notes_ = notes;
	}
	
	public Deadline(Parcel in){
		ID_ = in.readLong();
		name_ = in.readString();
		startTime_ = new DateTime(in.readLong());
		endTime_ = new DateTime(in.readLong());
		isAllDay_ = Boolean.parseBoolean(in.readString());
		location_ = in.readString();
		notes_ = in.readString();
	}

	public long getID() {
		return ID_;
	}

	public void setID(long ID) {
		ID_ = ID;
	}

	public String getName() {
		return name_;
	}

	public void setName(String name) {
		name_ = name;
	}

	public DateTime getStartTime() {
		return startTime_;
	}

	public void setStartTime(DateTime startTime) {
		startTime_ = startTime;
	}

	public DateTime getEndTime() {
		return endTime_;
	}

	public void setEndTime(DateTime endTime) {
		endTime_ = endTime;
	}

	public boolean isAllDay() {
		return isAllDay_;
	}

	public void setAllDay(boolean isAllDay) {
		isAllDay_ = isAllDay;
	}

	public String getLocation() {
		return location_;
	}

	public void setLocation(String location) {
		location_ = location;
	}

	public String getNotes() {
		return notes_;
	}

	public void setNotes(String notes) {
		notes_ = notes;
	}

	@Override
	public int compareTo(Deadline anotherDeadline) {

		if (isAllDay_ && anotherDeadline.isAllDay()) {
			return sameDatesCompare(anotherDeadline);
		}
		else if (isAllDay_) {
			return -1;
		}
		else if (anotherDeadline.isAllDay()) {
			return 1;
		}

		int starts = startTime_.compareTo(anotherDeadline.getStartTime());
		if (starts == 0) {
			int ends = 0;
			if (anotherDeadline.getEndTime() != null && endTime_ != null) {
				ends = endTime_.compareTo(anotherDeadline.getEndTime());
			}
			if (ends == 0) {
				return sameDatesCompare(anotherDeadline);
			}
			else {
				return ends;
			}
		}
		return starts;
	}

	private int sameDatesCompare(Deadline anotherDeadline) {
		int names = name_.compareTo(anotherDeadline.getName());
		if (names == 0) {
			int locations = location_.compareTo(anotherDeadline.getLocation());
			if (locations == 0) {
				return notes_.compareTo(anotherDeadline.getNotes());
			}
			else {
				return locations;
			}
		}
		return names;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(ID_);
		dest.writeString(name_);
		dest.writeLong(startTime_.getMillis());
		dest.writeLong(endTime_.getMillis());
		dest.writeString(Boolean.toString(isAllDay_));
		dest.writeString(location_);
		dest.writeString(notes_);
	}
	
	public static final Parcelable.Creator<Deadline> CREATOR = new Parcelable.Creator<Deadline>() {
		public Deadline createFromParcel(Parcel in) {
			return new Deadline(in);
		}

		public Deadline[] newArray(int size) {
			return new Deadline[size];
		}
	};
}
