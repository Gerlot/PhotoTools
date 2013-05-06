package hu.bute.gb.onlab.PhotoTools.model;

import java.util.ArrayList;
import java.util.List;

public class Friend implements Comparable<Friend> {

	public int ID_;
	public String firstName_;
	public String lastName_;
	public String phoneNumber_;
	public String emailAddress_;
	public String address_;
	public List<Integer> lentItems_ = null;

	public Friend(int ID, String firstName, String lastName, String phoneNumber,
			String emailAddress, String address, ArrayList<Integer> lentItems) {
		ID_ = ID;
		firstName_ = firstName;
		lastName_ = lastName;
		phoneNumber_ = phoneNumber;
		emailAddress_ = emailAddress;
		address_ = address;
		lentItems_ = lentItems;
	}

	public void addLentItem(Integer equipmentName) {
		if (lentItems_ == null) {
			lentItems_ = new ArrayList<Integer>();
		}

		lentItems_.add(equipmentName);
	}

	public int getID() {
		return ID_;
	}

	public void setID(int ID) {
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

	public List<Integer> getLentItems() {
		return lentItems_;
	}

	public void setLentItems(List<Integer> lentItems) {
		lentItems_ = lentItems;
	}
	
	public void lendItem(int id){
		// Initialize lsit if this is the first item
		if (lentItems_ == null) {
			lentItems_ = new ArrayList<Integer>();
		}
		lentItems_.add(Integer.valueOf(id));
	}

	@Override
	public int compareTo(Friend anotherFriend) {
		String fullName = firstName_ + " " + lastName_;
		String antoherFullName = anotherFriend.getFirstName() + " " + anotherFriend.getLastName();
		return fullName.compareTo(antoherFullName);
	}

}
