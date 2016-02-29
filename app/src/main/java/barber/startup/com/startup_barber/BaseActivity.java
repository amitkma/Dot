package barber.startup.com.startup_barber;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ayush on 23/1/16.
 */
public class BaseActivity extends AppCompatActivity {

    static ParseUser currentUser = ParseUser.getCurrentUser();
    static int cart_items = 0;
    private static Toolbar toolbar;
    private static List<Object> list;
    private static ImageView fav;
    private static ImageView cart;
    ListView listview;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    int Temp;
    boolean flag_drawerLeft = false;

    public static void updatecart() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(currentUser.getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(final ParseUser object, ParseException e) {
                if (e == null) {
                    if (object.getList("cart") != null) {
                        list = object.getList("cart");
                        update_text_Items(list.size());
                    }

                } else e.printStackTrace();
            }
        });
    }

    private static void update_text_Items(int size) {
        TextView cartText = (TextView) toolbar.findViewById(R.id.cardtext);
        cartText.setText("(" + size + ")");

    }

    public static void make_favIcon_red() {

        fav.setColorFilter(Color.RED);
    }

    public static void make_cartIcon_blue() {

        cart.setColorFilter(Color.BLUE);
    }

    public Toolbar setup_toolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView title = (TextView) toolbar.findViewById(R.id.title_toolbar);
        Typeface tfe = Typeface.createFromAsset(getAssets(), "fonts/CaveatBrush-Regular.ttf");
        title.setTypeface(tfe);
        title.setSelected(true);
        title.setSingleLine(true);

        fav = (ImageView) toolbar.findViewById(R.id.fav_image);
        cart = (ImageView) toolbar.findViewById(R.id.cart_image);

        ParseQuery<ParseObject> parseObjectParseQuery = new ParseQuery<ParseObject>("fav");
        parseObjectParseQuery.fromPin(ParseUser.getCurrentUser().getUsername());
        parseObjectParseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0)
                        fav.setColorFilter(Color.RED);
                } else e.printStackTrace();
            }
        });


        ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("Cart");
        parseQuery.fromPin("Cart" + ParseUser.getCurrentUser().getUsername());
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0)
                        cart.setColorFilter(Color.BLUE);
                } else e.printStackTrace();
            }
        });


        return toolbar;


    }

    public void setup_nav_drawer() {

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View view = navigationView.inflateHeaderView(R.layout.nav_header);
        TextView textView = (TextView) view.findViewById(R.id.nav_header_text_welcome);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        textView.setTypeface(tf);
        if (currentUser != null) {
            if (currentUser.getUsername() != null)
                textView.setText(currentUser.getUsername());
            if (currentUser.getUsername() != null)
                Log.d("name", currentUser.getUsername());
        }
        navigationView.inflateMenu(R.menu.nav_menu);
        Temp = 0;
        ImageView imageView_editprof = (ImageView) view.findViewById(R.id.nav_header_imageView_editprofile);
        imageView_editprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Temp == 0) {
                    navigationView.getMenu().clear();
                    //navigationView.getMenu().findItem(R.menu.nav_menu).setVisible(false);
                    navigationView.inflateMenu(R.menu.nav_menu_profile_settings);
                    Temp = 1;

                } else {
                    Temp = 0;
                    navigationView.getMenu().clear();
                    navigationView.inflateMenu(R.menu.nav_menu);
                }
            }
        });


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);


        ImageView button_drawer_left = (ImageView) toolbar.findViewById(R.id.button_drawer_left);
        button_drawer_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawerLayout.openDrawer(GravityCompat.START);


            }
        });


        if (currentUser != null)
            setup_nav_header_profile_pic(view);
    }

    private void setup_nav_header_profile_pic(View view) {

        String uri = (String) currentUser.get("picUri");

        if (uri != null) {
            ImageView img = (ImageView) view.findViewById(R.id.profile_image);
            Picasso.with(this)
                    .load(uri)
                    .into(img);
        }
    }

    public boolean check_connection() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

//For 3G check
        boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .isConnectedOrConnecting();
//For WiFi Check
        boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting();

        System.out.println(is3g + " net " + isWifi);

        if (!is3g && !isWifi) {

            return false;
        } else {

            return true;
        }


    }

    public void update_cart_text() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(currentUser.getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                updatecart();
            }
        });


    }

}
