package barber.startup.com.startup_barber;

/**
 * Created by ayush on 29/1/16.
 */
public class Data {
    String id = "default";
    String title = "title";
    String price = "0";
    String url = "url";
    boolean fav = false;
    boolean cart = false;

    int cost = 0;
    int time;

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
