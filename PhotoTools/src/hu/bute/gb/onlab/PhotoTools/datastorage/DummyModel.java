package hu.bute.gb.onlab.PhotoTools.datastorage;

import hu.bute.gb.onlab.PhotoTools.entities.Deadline;
import hu.bute.gb.onlab.PhotoTools.entities.Equipment;
import hu.bute.gb.onlab.PhotoTools.entities.Friend;
import hu.bute.gb.onlab.PhotoTools.entities.Location;
import hu.bute.gb.onlab.PhotoTools.helpers.Coordinate;
import hu.bute.gb.onlab.PhotoTools.helpers.DeadlineDay;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections15.bag.TreeBag;
import org.joda.time.DateTime;

import android.util.Log;

public class DummyModel {

	// Locations database
	public List<Location> locations = new ArrayList<Location>();
	private static String DUMMYLOCATIONS[] = { "Heroes' Square", "Schönherz Dormitory",
			"The Tomb of Gül Baba", "BME Building K", "Turul Bird" };
	public AtomicInteger locationId = new AtomicInteger(1);

	// Deadlines database
	public Map<DeadlineDay, TreeBag<Deadline>> deadlines = new TreeMap<DeadlineDay, TreeBag<Deadline>>();
	public AtomicInteger deadlineId = new AtomicInteger(1);

	// Equipment database
	public Map<String, TreeSet<Equipment>> equipment = new LinkedHashMap<String, TreeSet<Equipment>>();
	public String equipmentCategories[] = new String[] { "Camera", "Lenses", "Filters", "Flashes",
			"Memory Cards & Readers", "Accessories" };
	public AtomicInteger equipmentId = new AtomicInteger(1);

	// Friends database
	public Map<String, TreeSet<Friend>> friends = new TreeMap<String, TreeSet<Friend>>();
	public AtomicInteger friendId = new AtomicInteger(1);

	// Instance
	private final static DummyModel instance_ = new DummyModel();

	private DummyModel() {

		// Populate Locations
		boolean jitter = false;
		for (int i = 0; i < 5; i++) {
			String note = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam justo dui, elementum quis sollicitudin sit ametxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
			Location location = new Location(locationId.getAndIncrement(), DUMMYLOCATIONS[i],
					"1117 Budapest, Irinyi József St 42", new Coordinate(0.01, 0.02), jitter,
					!jitter, note);
			jitter = !jitter;
			addLocation(location);
		}

		// Populate Deadlines
		DateTime start = new DateTime(2013, 4, 26, 15, 0);
		DateTime end = new DateTime(2013, 4, 26, 19, 0);
		Deadline deadline = new Deadline(
				deadlineId.getAndIncrement(),
				"Model Shoot",
				start,
				end,
				false,
				"SPOT Schönherz Photo Studio",
				"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam justo dui, elementum quis sollicitudin sit amet");
		addDeadline(deadline);

		start = new DateTime(2013, 4, 26, 15, 0);
		end = null;
		deadline = new Deadline(deadlineId.getAndIncrement(), "Finish the Model Shoot Retouch",
				start, end, true, "", "");
		addDeadline(deadline);

		start = new DateTime(2013, 4, 25, 18, 0);
		end = null;
		deadline = new Deadline(deadlineId.getAndIncrement(), "Finish the Photo Tour Retouch",
				start, end, true, "", "You promised the photos to your friend.");
		addDeadline(deadline);

		start = new DateTime(2013, 4, 28, 12, 0);
		end = null;
		deadline = new Deadline(deadlineId.getAndIncrement(), "Photo Tour in the Mountains", start,
				end, false, "", "With Dave.");
		// addDeadline(deadline);

		start = new DateTime(2013, 4, 27, 18, 0);
		end = new DateTime(2013, 4, 27, 23, 0);
		deadline = new Deadline(deadlineId.getAndIncrement(), "Nigth city photography", start, end,
				false, "", "With Joe.");
		// addDeadline(deadline);

		start = new DateTime(2013, 4, 27, 12, 0);
		end = null;
		deadline = new Deadline(deadlineId.getAndIncrement(), "Nature photography", start, end,
				false, "", "With Dave.");
		// addDeadline(deadline);

		// Populate Equipments
		Equipment anequipment = null;

		for (int i = 0; i < equipmentCategories.length; i++) {
			equipment.put(equipmentCategories[i], new TreeSet<Equipment>());
		}
		anequipment = new Equipment(equipmentId.getAndIncrement(), "Crumpler Cupcake Backpack",
				equipmentCategories[5], "Present from my Girlfriend", 0);
		addEquipment(anequipment);

		anequipment = new Equipment(equipmentId.getAndIncrement(), "Metz mecablitz 50 AF-1",
				equipmentCategories[3], "Powerful flash for my Nikon", 0);
		addEquipment(anequipment);

		anequipment = new Equipment(equipmentId.getAndIncrement(), "Nikon D5000",
				equipmentCategories[0], "My first DSLR", 0);
		addEquipment(anequipment);

		anequipment = new Equipment(equipmentId.getAndIncrement(),
				"AF-S DX VR Nikkor 18-55mm f/3.5-5.6G", equipmentCategories[1],
				"Kit lens came with my D5000.", 0);
		addEquipment(anequipment);

		anequipment = new Equipment(equipmentId.getAndIncrement(),
				"AF-S DX VR Nikkor 55-200mm f/4-5.6G", equipmentCategories[1],
				"A light and nimble Nikon zoom lens.", 0);
		addEquipment(anequipment);

		anequipment = new Equipment(equipmentId.getAndIncrement(), "KingMax SDHC 8GB",
				equipmentCategories[4], "My primary memory card with a capacity of 8 Gigs.", 0);
		addEquipment(anequipment);

		anequipment = new Equipment(equipmentId.getAndIncrement(), "Maxell SD 2GB",
				equipmentCategories[4],
				"My secondary, older memory card with a capacity of 2 Gigs.", 0);
		anequipment.setLentTo(8);
		addEquipment(anequipment);

		anequipment = new Equipment(equipmentId.getAndIncrement(), "Remote Controller For Nikon",
				equipmentCategories[5],
				"A simple and cheap chinese-made remote controller for Nikons", 0);
		addEquipment(anequipment);

		// Populate Friends
		for (int i = 0; i < 3; i++) {
			Friend friend = new Friend(friendId.getAndIncrement(), "Béla" + i, "Kovács",
					"+36207654321", "bela" + i + "@gmail.com",
					"1117 Budapest, Irinyi József St 42 ", null, false);
			addFriend(friend);
		}
		for (int i = 0; i < 4; i++) {
			Friend friend = new Friend(friendId.getAndIncrement(), "Céla" + i, "Kovács",
					"+36207654321", "cela" + i + "@gmail.com",
					"1117 Budapest, Irinyi József St 42 ", null, false);
			addFriend(friend);
		}
		ArrayList<Long> aladarHas = new ArrayList<Long>();
		aladarHas.add(Long.valueOf(7));
		Friend friend = new Friend(friendId.getAndIncrement(), "Aladár", "Kovács", "+36207654321",
				"aladar@outlook.com", "1117 Budapest, Irinyi József St 42 ", aladarHas, true);
		addFriend(friend);

		// Testing database for debugging
		// testDatabase();

	}

	public static DummyModel getInstance() {
		return instance_;
	}

	public Location getLocationById(long id) {
		for (Location location : locations) {
			if (location.getID() == id) {
				return location;
			}
		}
		return null;
	}

	public void addLocation(Location newlocation) {
		locations.add(newlocation);
	}

	public void editLocation(Location editedLocation) {
		for (Location location : locations) {
			if (location.getID() == editedLocation.getID()) {
				location.setName(editedLocation.getName());
				location.setAddress(editedLocation.getAddress());
				location.setCoordinate(editedLocation.getCoordinate());
				location.setCarEntry(editedLocation.hasCarEntry());
				location.setPowerSource(editedLocation.hasPowerSource());
				location.setNotes(editedLocation.getNotes());
				break;
			}
		}
	}

	public void removeLocation(Location locationToRemove) {
		locations.remove(locationToRemove);
	}

	public boolean removeLocationById(long id) {
		for (Location location : locations) {
			if (location.getID() == id) {
				locations.remove(location);
				return true;
			}
		}
		return false;
	}

	public Deadline getDeadlineById(long id) {
		for (Map.Entry<DeadlineDay, TreeBag<Deadline>> alphabet : deadlines.entrySet()) {
			TreeBag<Deadline> current = alphabet.getValue();
			for (Deadline deadline : current) {
				if (deadline.getID() == id) {
					return deadline;
				}
			}
		}
		return null;
	}

	public void addDeadline(Deadline newDeadline) {
		DateTime start = newDeadline.getStartTime();
		DeadlineDay day = new DeadlineDay(start);
		if (!deadlines.containsKey(day)) {
			deadlines.put(day, new TreeBag<Deadline>());
		}
		deadlines.get(day).add(newDeadline);
	}

	public void editDeadline(Deadline editedDeadline, Deadline oldDeadline) {
		DateTime start = editedDeadline.getStartTime();
		DeadlineDay day = new DeadlineDay(start);

		// If the start time changed, remove deadline from it's old category,
		// and add in it's new category
		if (!editedDeadline.getStartTime().toDateMidnight()
				.equals(oldDeadline.getStartTime().toDateMidnight())) {

			DeadlineDay oldDay = new DeadlineDay(oldDeadline.getStartTime());

			TreeBag<Deadline> oldCategory = deadlines.get(oldDay);
			if (oldCategory != null) {
				oldCategory.remove(oldDeadline);
			}
			// If new category doesn't exist, create it
			if (!deadlines.containsKey(day)) {
				deadlines.put(day, new TreeBag<Deadline>());
			}
			deadlines.get(day).add(editedDeadline);
		}
		// If the start time is the same, modify deadline
		else {
			TreeBag<Deadline> category = deadlines.get(day);
			for (Deadline deadline : category) {
				if (deadline.getID() == editedDeadline.getID()) {
					deadline.setName(editedDeadline.getName());
					deadline.setStartTime(editedDeadline.getStartTime());
					deadline.setEndTime(editedDeadline.getEndTime());
					deadline.setAllDay(editedDeadline.isAllDay());
					deadline.setLocation(editedDeadline.getLocation());
					deadline.setNotes(editedDeadline.getNotes());
					break;
				}
			}
		}

	}

	public void removeDeadline(Deadline deadlineToRemove) {
		deadlines.remove(deadlineToRemove);
	}

	public boolean removeDeadlineById(long id) {
		Deadline deadlineToRemove = null;
		for (Map.Entry<DeadlineDay, TreeBag<Deadline>> alphabet : deadlines.entrySet()) {
			TreeBag<Deadline> current = alphabet.getValue();
			for (Deadline deadline : current) {
				if (deadline.getID() == id) {
					deadlineToRemove = deadline;
					break;
				}
			}
			if (deadlineToRemove != null) {
				current.remove(deadlineToRemove);
				return true;
			}
		}
		return false;
	}

	public Equipment getEquipmentById(long id) {
		for (Map.Entry<String, TreeSet<Equipment>> alphabet : equipment.entrySet()) {
			TreeSet<Equipment> current = alphabet.getValue();
			for (Equipment equipment : current) {
				if (equipment.getID() == id) {
					return equipment;
				}
			}
		}
		return null;
	}

	public void addEquipment(Equipment newEquipment) {
		int categoryId = convertCategoryNameToId(newEquipment.getCategory());
		equipment.get(equipmentCategories[categoryId]).add(newEquipment);
	}

	public void editEquipment(Equipment editedEquipment, Equipment oldEquipment) {
		int oldCategoryId = convertCategoryNameToId(oldEquipment.getCategory());
		int categoryId = convertCategoryNameToId(editedEquipment.getCategory());

		// If category changed, remove the equipment from it's old category and
		// add it to it's new category
		if (categoryId != oldCategoryId) {
			TreeSet<Equipment> oldCategory = equipment.get(equipmentCategories[oldCategoryId]);
			oldCategory.remove(oldEquipment);
			equipment.get(equipmentCategories[categoryId]).add(editedEquipment);
		}
		else {
			TreeSet<Equipment> category = equipment.get(equipmentCategories[categoryId]);
			for (Equipment equipment : category) {
				if (equipment.getID() == editedEquipment.getID()) {
					equipment.setName(editedEquipment.getName());
					equipment.setCategory(editedEquipment.getCategory());
					equipment.setNotes(editedEquipment.getNotes());
					if (editedEquipment.isLent()) {
						equipment.setLentTo(editedEquipment.getLentTo());
					}
					break;
				}
			}
		}
	}

	public void removeEquipment(Equipment equipmentToRemove) {
		equipment.remove(equipmentToRemove);
	}

	public boolean removeEquipmentById(long id) {
		Equipment equipmentToRemove = null;
		for (Map.Entry<String, TreeSet<Equipment>> alphabet : equipment.entrySet()) {
			TreeSet<Equipment> current = alphabet.getValue();
			for (Equipment equipment : current) {
				if (equipment.getID() == id) {
					equipmentToRemove = equipment;
					break;
				}
			}
			if (equipmentToRemove != null) {
				current.remove(equipmentToRemove);
				return true;
			}
		}
		return false;
	}

	public boolean lendEquipment(long equipmentId, long friendId) {
		Equipment equipment = getEquipmentById(equipmentId);
		if (!equipment.isLent()) {
			equipment.setLentTo(friendId);

			Friend friend = getFriendById(friendId);
			friend.lendItem(equipmentId);
			return true;
		}
		return false;
	}

	public Friend getFriendById(long id) {
		for (Map.Entry<String, TreeSet<Friend>> alphabet : friends.entrySet()) {
			TreeSet<Friend> current = alphabet.getValue();
			for (Friend friend : current) {
				if (friend.getID() == id) {
					return friend;
				}
			}
		}
		return null;
	}

	public void addFriend(Friend newFriend) {
		String firstChar = newFriend.getFirstName().substring(0, 1);

		if (!(friends.containsKey(firstChar))) {
			friends.put(firstChar, new TreeSet<Friend>());
		}
		friends.get(firstChar).add(newFriend);
	}

	private void testDatabase() {
		// Testing locations database
		for (Location location : locations) {
			Log.d("location", Long.toString(location.getID()));
			Log.d("location", location.getName());
		}

		// Testing deadline database
		for (Map.Entry<DeadlineDay, TreeBag<Deadline>> d : deadlines.entrySet()) {
			Log.d("deadline", "" + d.getKey());
			TreeBag<Deadline> db = d.getValue();
			for (Deadline de : db) {
				Log.d("deadline", Long.toString(de.getID()));
				Log.d("deadline", de.getName());
				Log.d("deadline", de.getStartTime().toString());
			}
		}

		// Testing equipment database
		for (Map.Entry<String, TreeSet<Equipment>> e : equipment.entrySet()) {
			Log.d("equipment", e.getKey());
			TreeSet<Equipment> db = e.getValue();
			for (Equipment eq : db) {
				Log.d("equipment", Long.toString(eq.getID()));
				Log.d("equipment", eq.getName());
			}
		}

		// Testing friend database
		for (Map.Entry<String, TreeSet<Friend>> f : friends.entrySet()) {
			Log.d("friend", f.getKey());
			TreeSet<Friend> db = f.getValue();
			for (Friend fr : db) {
				Log.d("friend", Long.toString(fr.getID()));
				Log.d("friend", fr.getFirstName() + " " + fr.getLastName());
			}
		}
	}

	private int convertCategoryNameToId(String categoryName) {
		if (categoryName.equals("Lenses")) {
			return 1;
		}
		else if (categoryName.equals("Filters")) {
			return 2;
		}
		else if (categoryName.equals("Flashes")) {
			return 3;
		}
		else if (categoryName.equals("Memory Cards & Readers")) {
			return 4;
		}
		else if (categoryName.equals("Accessories")) {
			return 5;
		}
		return 0;
	}

}
