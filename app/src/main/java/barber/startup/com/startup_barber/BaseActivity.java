package barber.startup.com.startup_barber;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

/**
 * Created by ayush on 23/1/16.
 */
public class BaseActivity extends AppCompatActivity {

    static ParseUser currentUser = ParseUser.getCurrentUser();
    static int cart_items = 0;
    private static Toolbar toolbar;
    ListView listview;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    int Temp;

    public static void updatecart() {
        if (currentUser.get("cartItems") != null)
            cart_items = (int) currentUser.get("cartItems");
        TextView cartText = (TextView) toolbar.findViewById(R.id.cardtext);
        if (cart_items != 0)
            cartText.setText("(" + cart_items + ")");
    }

    public static void updatecartbyONE(final Context c) {

        cart_items++;
        TextView cartText = (TextView) toolbar.findViewById(R.id.cardtext);
        if (cart_items != 0)
            cartText.setText("(" + cart_items + ")");
        currentUser.increment("cartItems");
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(c, "savedin Server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Toolbar setup_toolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView img = (ImageView) toolbar.findViewById(R.id.cart_image);
        img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(BaseActivity.this, CartDisplay.class));
                Cart.getCartItemsId();
            }
        });
        return toolbar;

    }

    public void setup_nav_drawer() {
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View view = navigationView.inflateHeaderView(R.layout.nav_header);
        TextView textView = (TextView) view.findViewById(R.id.nav_header_text_welcome);
        if (currentUser != null) {
            if (currentUser.getUsername() != null)
                textView.setText("Welcome " + currentUser.getUsername() + " !");
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

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                //super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

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
        if (!check_connection())
            query.fromLocalDatastore();
        query.getInBackground(currentUser.getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                updatecart();
            }
        });


    }

}
