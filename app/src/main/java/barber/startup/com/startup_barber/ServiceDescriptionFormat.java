package barber.startup.com.startup_barber;

/**
 * Created by ayush on 7/3/16.
 */
public class ServiceDescriptionFormat {

    private String barberObjectId;
    private String barberId;
    private String barberName;
    private int servicePrice;
    private int serviceTime;

    public String getBarberId() {
        return barberId;
    }

    public void setBarberId(String barberId) {
        this.barberId = barberId;
    }

    public String getBarberObjectId() {
        return barberObjectId;
    }

    public void setBarberObjectId(String barberObjectId) {
        this.barberObjectId = barberObjectId;
    }

    public String getBarberName() {
        return barberName;
    }

    public void setBarberName(String barberName) {
        this.barberName = barberName;
    }

    public int getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(int servicePrice) {
        this.servicePrice = servicePrice;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(int serviceTime) {
        this.serviceTime = serviceTime;
    }
}
