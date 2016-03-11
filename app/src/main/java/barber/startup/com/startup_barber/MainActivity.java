package barber.startup.com.startup_barber;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Date;
import java.util.List;

import barber.startup.com.startup_barber.Utility.NetworkCheck;
import barber.startup.com.startup_barber.Utility.ToggleActionItemColor;

public class MainActivity extends BaseActivity {
    protected static float width;
    protected static int height;
    protected static int a;
    protected static boolean dataUpdated = false;
    ParseUser parseUser = ParseUser.getCurrentUser();

    private final String TAG = "MainActivity";

    private Menu menu;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Dialog dialog;
    public static SharedPreferences prefs;
    public static boolean dataSaved = false;
    private TextView checknet;
    private String[] categoriesName;
    private AppBarLayout appBarLayout;

    public static Date lastUpdatedAt = new Date();
    private ProgressDialog progressDialog;
    private boolean dataChanged;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        appBarLayout = (AppBarLayout)findViewById(R.id.appbarlayout);
        final View v = findViewById(R.id.viewframe);
        if(NetworkCheck.checkConnection(MainActivity.this)) {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("objectId", parseUser.getObjectId());
            query.getFirstInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser object, ParseException e) {
                    if (e == null) {
                        Defaults.mNumberOfServicesLeft = object.getInt("rewardWallet");
                        Log.e("REWARD", String.valueOf(Defaults.mNumberOfServicesLeft));
                        if (Defaults.mNumberOfServicesLeft == 0) {
                            Snackbar.make(v, "You dont have any free service left", Snackbar.LENGTH_LONG).show();
                        } else if (Defaults.mNumberOfServicesLeft > 0) {
                            Snackbar.make(v, "You have " + Defaults.mNumberOfServicesLeft + " free service/s left", Snackbar.LENGTH_LONG).show();
                        }
                    } else
                        Snackbar.make(v, e.getMessage(), Snackbar.LENGTH_SHORT).show();

                }
            });
        }
        else
            Snackbar.make(v, "Error in connection", Snackbar.LENGTH_SHORT).show();

        v.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d("heightv", String.valueOf(v.getHeight()));

                float b = (v.getHeight() / Resources.getSystem().getDisplayMetrics().density);
                a = Math.round(b);
                Log.d("a", String.valueOf(a));
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
                    v.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    v.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.developer);

        ImageView ayush = (ImageView) dialog.findViewById(R.id.ayush);
        ImageView amit = (ImageView) dialog.findViewById(R.id.amit);

        checknet = (TextView) findViewById(R.id.checkconnection);
        Glide.with(getApplicationContext()).load((R.drawable.ayush)).into(ayush);
        Glide.with(getApplicationContext()).load((R.drawable.amit)).into(amit);

        Display displaydp = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        displaydp.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        Float f = outMetrics.heightPixels / density;
        height = Math.round(f);


        Log.d("H", String.valueOf(height));


        width = outMetrics.widthPixels / density;

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(1);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        dataSaved = prefs.getBoolean("dataSaved", false);
        lastUpdatedAt.setTime(prefs.getLong(Defaults.DATE_IN_MILLS_KEY, System.currentTimeMillis()));

        setup_toolbar();

        setup_nav_drawer();
        setup_nav_item_listener();

        Defaults.genderCode = ParseUser.getCurrentUser().getInt("genderCode");
        if (Defaults.genderCode == 0)
            this.categoriesName = Defaults.categoriesNameGirls;
        else if (Defaults.genderCode == 1)
            this.categoriesName = Defaults.categoriesNameBoys;
        checkfordatachange();


    }



   @Override
    protected void onStart() {
        super.onStart();
        if(dataUpdated){
            setupViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
            dataUpdated = false;
        }
    }

    @Override
    protected void onResume(){
        super.onResume();

    }
    @Override
    protected void onStop() {
        super.onStop();
    }

    private void cartIcon_toolbar() {
        Intent intent = new Intent(MainActivity.this, CartDisplay.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void favIcon_toolbar() {
        Intent intent = new Intent(MainActivity.this, Favourites.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

    }

    public void setup_nav_item_listener() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.Favorites:
                        drawerLayout.closeDrawers();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(MainActivity.this, Favourites.class));
                                overridePendingTransition(0, 0);
                            }
                        }, 220);
                        return true;
                    case R.id.About:
                        drawerLayout.closeDrawers();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.show();
                            }
                        }, 220);
                        return true;

                    case R.id.Appointments:
                        drawerLayout.closeDrawers();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(MainActivity.this, Appointments.class));
                                overridePendingTransition(0, 0);
                            }
                        }, 220);
                        return true;

                    case R.id.Cart:
                        drawerLayout.closeDrawers();
                        menuItem.setTitle("Cart");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(MainActivity.this, CartDisplay.class));
                                overridePendingTransition(0, 0);
                            }
                        }, 150);
                        return true;

                    case R.id.logout:

                        if (NetworkCheck.checkConnection(MainActivity.this)) {
                            drawerLayout.closeDrawers();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    logout();
                                }
                            }, 280);
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
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        parseUser = null;
                        Intent i = new Intent(getApplicationContext(), Login.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(i);
                        finish();
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

                    } else if (Application.DEBUG) Log.e("MainActivity", e.getMessage());
                }
            });
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        new ToggleActionItemColor(menu, this).makeIconDefault(R.id.action_fav);
        new ToggleActionItemColor(menu, this).makeIconDefault(R.id.action_cart);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_fav) {

            favIcon_toolbar();

            return true;
        }

        if (id == R.id.action_cart) {

            cartIcon_toolbar();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public Menu getMenu() {
        return menu;
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            Log.e("TAB POSITION", Integer.toString(position));
            return new Fragment_services_test(position, appBarLayout);
        }

        @Override
        public int getCount() {
            return categoriesName.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return categoriesName[position];
        }
    }




    private void Fetch_data() {

        progressDialog.show();
        // Syncing all our data from server
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(Defaults.INFO_CLASS);
        if (ParseUser.getCurrentUser().getInt("genderCode") == 0)
            query.whereEqualTo("gender", 0);
        else
            query.whereEqualTo("gender", 1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        if (Application.DEBUG)
                            Log.d("Objectsize", String.valueOf(objects.size()));
                        for (int i = 0; i < objects.size(); i++) {
                            ParseObject parseObject = objects.get(i);
                            ParseFile parseFile = parseObject.getParseFile("image");
                            Glide.with(getApplicationContext()).load(parseFile.getUrl());
                        }
                        unpinAndRepinData(objects);
                    }
                } else {
                    if (e.getCode() == 100) {
                        Snackbar.make(findViewById(R.id.colayout), "Error in connection", Snackbar.LENGTH_LONG).show();
                    }
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                }
            }
        });


    }

    private void unpinAndRepinData(final List<ParseObject> objects) {
        ParseObject.unpinAllInBackground("data", new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    if (Application.DEBUG)
                        Log.d(TAG, "unPinnedAll");
                    ParseObject.pinAllInBackground("data", objects, new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                if (Application.DEBUG)
                                    Log.d(TAG, "PinnedAll");

                                prefs.edit().putBoolean("dataSaved", true).apply();
                                prefs.edit().putLong(Defaults.DATE_IN_MILLS_KEY, lastUpdatedAt.getTime()).apply();
                                progressDialog.dismiss();
                                progressDialog = null;
                                dataUpdated = true;
                                onStart();

                            } else {
                                e.printStackTrace();
                                if (progressDialog != null) {
                                    progressDialog.dismiss();
                                    progressDialog = null;
                                }
                            }
                        }


                    });

                } else e.printStackTrace();

            }
        });

    }
    private void checkfordatachange() {

        ParseQuery<ParseObject> parseObjectParseQuery = new ParseQuery<ParseObject>("Data");
        parseObjectParseQuery.orderByDescending("updatedAt");
        parseObjectParseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    Date updatedDate = object.getUpdatedAt();
                    if (updatedDate.after(lastUpdatedAt) || !dataSaved ) {
                        Fetch_data();
                    }
                  else  {
                        dataUpdated = true;
                        onStart();
                    }
                } else Log.i("Main", e.getMessage());
            }
        });
    }


}
