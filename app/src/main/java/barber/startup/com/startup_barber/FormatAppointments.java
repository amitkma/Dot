package barber.startup.com.startup_barber;

/**
 * Created by ayush on 5/3/16.
 */
public class FormatAppointments {
    private String date;
    private String timeslot;
    private String barber;
    private String objectId;
    private int numberOfServices;
    private int totalPrice;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBarber() {
        return barber;
    }

    public void setBarber(String barber) {
        this.barber = barber;
    }

    public void setObjectId(String objectId){
        this.objectId = objectId;
    }

    public String getObjectId(){
        return objectId;
    }
    public String getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(String timeslot) {
        this.timeslot = timeslot;
    }

    public void setNumberOfServices(int numberOfServices) {
        this.numberOfServices = numberOfServices;
    }

    public int getNumberOfServices() {
        return numberOfServices;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getTotalPrice() {
        return totalPrice;
    }
}

