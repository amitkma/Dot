package barber.startup.com.startup_barber;

import java.io.Serializable;

/**
 * Created by ayush on 29/1/16.
 */
public class Data  implements Serializable{
    String id = "default";
    String title = "title";
    String price = "0";
    String url = null;
    boolean fav = false;
    boolean cart = false;

    int cost = 0;
    int time = 0;

    public int getTime() {
        return time;
    }

    public int getCost() {
        return cost;
    }

    public boolean isFav() {
        return fav;
    }

    public boolean isCart() {
        return cart;
    }

    public String getId() {
        return id;
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
