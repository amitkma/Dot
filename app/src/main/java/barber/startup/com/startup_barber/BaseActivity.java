package barber.startup.com.startup_barber;

import android.net.ConnectivityManager;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.parse.ParseUser;

/**
 * Created by ayush on 23/1/16.
 */
public class BaseActivity extends AppCompatActivity {

    ListView listview;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ParseUser currentUser;
    int Temp;
    private Toolbar toolbar;


    public void setup_toolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    public void setup_nav_drawer() {
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View view = navigationView.inflateHeaderView(R.layout.nav_header);
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
}
