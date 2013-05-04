package hu.bute.gb.onlab.PhotoToolsProto.model;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

public class DeadlineDay implements Comparable<DeadlineDay>{
	
	private DateMidnight dateInDateTime_;
	
	public DeadlineDay(DateTime dateInDateTime){
		dateInDateTime_ = dateInDateTime.toDateMidnight();
	}

	public DateMidnight getDateInDateTime() {
		return dateInDateTime_;
	}

	public void setDateInDateTime_(DateTime dateInDateTime) {
		dateInDateTime_ = dateInDateTime.toDateMidnight();
	}
	
	@Override
	public String toString() {
		return dateInDateTime_.toLocalDate().toString("yyyy. MMM dd., EEEE");
	}

	@Override
	public int compareTo(DeadlineDay anotherDeadlineDay) {
		return dateInDateTime_.compareTo(anotherDeadlineDay.getDateInDateTime());
	}

}
