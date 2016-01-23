package barber.startup.com.startup_barber;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.view.MenuItem;
import android.view.View;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.parse.ParseUser;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setup_toolbar();
        setup_nav_drawer();
        setup_nav_item_listener();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void setup_nav_item_listener() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Checking if the item is in checked state or not, if not make it in checked state
              /*  if (menuItem.isChecked())
                    menuItem.setChecked(false);
                else
                    menuItem.setChecked(true);*/


                switch (menuItem.getItemId()) {

                    case R.id.About:
                        drawerLayout.closeDrawers();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                            }
                        }, 150);
                        return true;

                    case R.id.logout:

                        if (check_connection()) {
                            drawerLayout.closeDrawers();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    logout();
                                }
                            }, 220);
                        }
                        return true;

                    case R.id.Feedback:
                        drawerLayout.closeDrawers();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                            }
                        }, 500);
                        return true;


                    default:

                        return true;

                }
            }
        });
    }


    private void logout() {
        currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            ParseUser.logOutInBackground();
        } else {
            Profile profile = Profile.getCurrentProfile();
            if (profile != null) {
                LoginManager.getInstance().logOut();
            }
        }


        startActivity(new Intent(this, Choose_Login.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY));
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);


    }
}
