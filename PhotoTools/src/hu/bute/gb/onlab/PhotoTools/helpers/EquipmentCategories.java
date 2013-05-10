package hu.bute.gb.onlab.PhotoTools.helpers;

public enum EquipmentCategories {
	CAMERA(1, "Camera"), LENSES(2, "Lenses"), FILTERS(3, "Filter"), FLASHES(4, "Flashes"), MEMORY(
			5, "Memory Cards & Readers"), ACCESSORIES(6, "Accessories");

	private int value_;
	private String label_;

	EquipmentCategories(int value, String label) {
		value_ = value;
		label_ = label;
	}
	
	public static int getSize(){
		return 6;
	}

	public int getValue() {
		return value_;
	}

	@Override
	public String toString() {
		return label_;
	}
}
