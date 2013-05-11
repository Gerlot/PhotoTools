package hu.bute.gb.onlab.PhotoTools.helpers;

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
	
	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || object.getClass() != this.getClass()) {
            return false;
        }

		DeadlineDay other = (DeadlineDay) object;
		return this.dateInDateTime_.equals(other.getDateInDateTime());
	}
	
	@Override
	public int hashCode() {
		return dateInDateTime_.hashCode();
	}

}
