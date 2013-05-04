package hu.bute.gb.onlab.PhotoToolsProto.model;

import org.joda.time.DateTime;

import android.util.Log;

public class Deadline implements Comparable<Deadline> {

	private int ID_;
	private String name_;
	private DateTime startTime_;
	private DateTime endTime_;
	private boolean isAllDay_ = false;
	private String location_;
	private String notes_;

	public Deadline(int ID, String name, DateTime startTime, DateTime endTime, boolean isAllDay,
			String location, String notes) {
		ID_ = ID;
		name_ = name;
		startTime_ = startTime;
		endTime_ = endTime;
		isAllDay_ = isAllDay;
		location_ = location;
		notes_ = notes;
	}

	public int getID() {
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
}
