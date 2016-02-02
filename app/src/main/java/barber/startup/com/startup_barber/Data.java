package barber.startup.com.startup_barber;

import com.parse.ParseObject;

/**
 * Created by ayush on 29/1/16.
 */
public class Data {
    String id;

    String title;
    String price;
    String url;
    int cardItems;

    public int getCardItems() {
        return cardItems;
    }

    public String getPrice() {
        return price;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
}
