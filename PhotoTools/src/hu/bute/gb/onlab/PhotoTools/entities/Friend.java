package hu.bute.gb.onlab.PhotoTools.entities;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Friend implements Comparable<Friend>, Parcelable {

	public long ID_;
	public String firstName_;
	public String lastName_;
	public String phoneNumber_;
	public String emailAddress_;
	public String address_;
	public List<Long> lentItems_ = null;

	public Friend(long ID, String firstName, String lastName, String phoneNumber,
			String emailAddress, String address, ArrayList<Long> lentItems) {
		ID_ = ID;
		firstName_ = firstName;
		lastName_ = lastName;
		phoneNumber_ = phoneNumber;
		emailAddress_ = emailAddress;
		address_ = address;
		lentItems_ = lentItems;
	}
	
	public Friend(Parcel in){
		ID_ = in.readLong();
		firstName_ = in.readString();
		lastName_ = in.readString();
		phoneNumber_ = in.readString();
		emailAddress_ = in.readString();
		address_ = in.readString();
		lentItems_ = new ArrayList<Long>();
		long[] lentArray = null;
		in.readLongArray(lentArray);
		for (long l : lentArray) {
			lentItems_.add(Long.valueOf(l));
		}
	}

	public void addLentItem(Long equipmentName) {
		if (lentItems_ == null) {
			lentItems_ = new ArrayList<Long>();
		}

		lentItems_.add(equipmentName);
	}

	public long getID() {
		return ID_;
	}

	public void setID(long ID) {
		ID_ = ID;
	}
	
	public String getFullNameFirstLast(){
		String fullName = firstName_ + " " + lastName_;
		return fullName;
	}
	
	public String getFullNameLastFirst(){
		String fullName = lastName_ + " " + firstName_;
		return fullName;
	}

	public String getFirstName() {
		return firstName_;
	}

	public void setFirstName(String firstName) {
		firstName_ = firstName;
	}

	public String getLastName() {
		return lastName_;
	}

	public void setLastName(String lastName) {
		lastName_ = lastName;
	}

	public String getPhoneNumber() {
		return phoneNumber_;
	}

	public void setPhoneNumber(String phoneNumber) {
		phoneNumber_ = phoneNumber;
	}

	public String getEmailAddress() {
		return emailAddress_;
	}

	public void setEmailAddress(String emailAddress) {
		emailAddress_ = emailAddress;
	}

	public String getAddress() {
		return address_;
	}

	public void setAddress(String address) {
		address_ = address;
	}

	public List<Long> getLentItems() {
		return lentItems_;
	}

	public void setLentItems(List<Long> lentItems) {
		lentItems_ = lentItems;
	}
	
	public void lendItem(long id){
		// Initialize list if this is the first item
		if (lentItems_ == null) {
			lentItems_ = new ArrayList<Long>();
		}
		lentItems_.add(Long.valueOf(id));
	}
	
	public boolean hasLentItems(){
		return (lentItems_ != null);
	}

	@Override
	public int compareTo(Friend anotherFriend) {
		String fullName = firstName_ + " " + lastName_;
		String antoherFullName = anotherFriend.getFirstName() + " " + anotherFriend.getLastName();
		return fullName.compareTo(antoherFullName);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(ID_);
		dest.writeString(firstName_);
		dest.writeString(lastName_);
		dest.writeString(phoneNumber_);
		dest.writeString(emailAddress_);
		dest.writeString(address_);
		long[] lentArray = new long[lentItems_.size()];
		for (int i = 0; i < lentArray.length; i++) {
			lentArray[i] = lentItems_.get(i).longValue();
		}
		dest.writeLongArray(lentArray);
	}
	
	public static final Parcelable.Creator<Friend> CREATOR = new Parcelable.Creator<Friend>() {
		public Friend createFromParcel(Parcel in) {
			return new Friend(in);
		}

		public Friend[] newArray(int size) {
			return new Friend[size];
		}
	};

}
