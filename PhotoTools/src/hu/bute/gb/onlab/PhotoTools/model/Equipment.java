package hu.bute.gb.onlab.PhotoTools.model;

public class Equipment implements Comparable<Equipment> {

	public int ID_;
	private String name_;
	private String category_;
	private String notes_;
	private int lentTo_ = 0;
	private boolean isLent_ = false;

	public Equipment(int ID, String name, String category, String notes) {
		ID_ = ID;
		name_ = name;
		category_ = category;
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

	public int getLentTo() {
		return lentTo_;
	}

	public void setLentTo(int id) {
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

}
