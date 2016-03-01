package barber.startup.com.startup_barber;

/**
 * Created by Amit on 28-02-2016.
 * Format for a time slot of a service.
 */
public class TimeSlotFormat {

    // Declaring integers for start and end time for a slot
    public int mStartTime;
    public int mEndTime;

    // Constructor for creating a time slot with start and end time
    public TimeSlotFormat(int startTime, int endTime) {
        mStartTime = startTime;
        mEndTime = endTime;
    }

    // public method for returning start time of a slot.
    public int getStartTimeData() {
        return mStartTime;
    }

    // public method for returning end time for a slot.
    public int getEndTimeData() {
        return mEndTime;
    }
}
