package barber.startup.com.startup_barber;

import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

public class BaseActivity extends AppCompatActivity {

    static ParseUser currentUser = ParseUser.getCurrentUser();

    NavigationView navigationView;
    DrawerLayout drawerLayout;
    int Temp;

    private ActionBarDrawerToggle mDrawerToggle;

    public Toolbar setup_toolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open_content_desc, R.string.drawer_close_content_desc){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.addDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();
        return toolbar;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
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


}
