package barber.startup.com.startup_barber;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

public class MainActivity extends BaseActivity {
    protected static float width;
    protected static int height;
    protected static int a;
    ParseUser parseUser = ParseUser.getCurrentUser();
    ProgressDialog progressDialog = null;
    private Menu menu;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Dialog dialog;
    private SharedPreferences prefs;
    private boolean dataSaved = false;
    private TextView checknet;
    private String[] categoriesName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);


        final View v = findViewById(R.id.viewframe);

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
        //   setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        //  tabLayout.setupWithViewPager(viewPager);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        dataSaved = prefs.getBoolean("dataSaved", false);

        changeTabsFont();

        Toolbar toolbar = setup_toolbar();
        setup_nav_drawer();
        setup_nav_item_listener();


        int genderCode = ParseUser.getCurrentUser().getInt("genderCode");
        if (genderCode == 0)
            this.categoriesName = Defaults.categoriesNameGirls;
        else if (genderCode == 1)
            this.categoriesName = Defaults.categoriesNameBoys;


        ParseQuery<ParseObject> parseObjectParseQuery = new ParseQuery<ParseObject>("Data");
        parseObjectParseQuery.fromPin("data");
        parseObjectParseQuery.orderByDescending("updatedAt");
        parseObjectParseQuery.getFirstInBackground(new GetCallback<ParseObject>() {

            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    Log.d("of", "reached");
                    if (object != null) {
                        Log.d("of", "notnull");
                        Date date = new Date();
                        date = object.getUpdatedAt();

                        ParseQuery<ParseObject> parseObjectParseQuery = new ParseQuery<ParseObject>("Data");
                        parseObjectParseQuery.orderByDescending("updatedAt");
                        final Date finalDate = date;
                        parseObjectParseQuery.getFirstInBackground(new GetCallback<ParseObject>() {


                            @Override
                            public void done(ParseObject object, ParseException e) {
                                if (e == null) {
                                    Date dateserver = object.getUpdatedAt();
                                    if (dateserver.after(finalDate)) {
                                        Log.i("Main", "Reached here");
                                        startfetch();
                                    } else
                                        Log.i("Main", "Reached here else");
                                } else Log.i("Main", e.getMessage());
                            }
                        });


                    }
                } else if (e.getCode() == 101)
                    startfetch();

            }
        });


    }

    private void startfetch() {
        if (NetworkCheck.checkConnection(getApplicationContext())) {
            if (dataSaved == false) {

                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Loading styles and barbers");
                progressDialog.setCancelable(false);
                Fetch_data();

            } else if (dataSaved == true) {
                onStart();
            }

        } else {
            Snackbar.make(findViewById(R.id.colayout), "Error in connection", Snackbar.LENGTH_INDEFINITE).show();
            checknet.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();


        Log.i("main", "onstart called");
        //setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);


    }


    @Override
    protected void onStop() {
        super.onStop();

        Log.i("called", "passed");
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            for (Fragment f : fragments) {
                //You can perform additional check to remove some (not all) fragments:
                if (f instanceof Fragment_services_test) {
                    ft.remove(f);
                }
            }
            ft.commitAllowingStateLoss();
        }
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

  /*  private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
    /*    if (ParseUser.getCurrentUser().getInt("genderCode") == 1) {
            adapter.addFragment(new Fragment_services_test(1), "hairStyle");
            adapter.addFragment(new Fragment_services_test(2), "Beards");
            adapter.addFragment(new Fragment_services_test(0), "HairRemoval");
            adapter.addFragment(new Fragment_services_test(0), "Facial");
            adapter.addFragment(new Fragment_services_test(0), "Massage");
            adapter.addFragment(new Fragment_services_test(0), "HairWash");
            adapter.addFragment(new Fragment_services_test(0), "hairColor");
        } else {
            adapter.addFragment(new Fragment_services_test(10), "Short");
            adapter.addFragment(new Fragment_services_test(11), "Medium");
            adapter.addFragment(new Fragment_services_test(12), "Long");
        }*/

        viewPager.setAdapter(adapter);

    }
*/
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

                        if (NetworkCheck.checkConnection(getApplicationContext())) {
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

    private void changeTabsFont() {

        //Typeface tf=Typeface.createFromAsset(getAssets(),"fonts/C");
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                    ((TextView) tabViewChild).setAllCaps(true);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
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
                        Log.d("MainActivityPin", "unPinnedAll");
                    ParseObject.pinAllInBackground("data", objects, new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                if (Application.DEBUG)
                                    Log.d("Login", "PinnedAll");

                                prefs.edit().putBoolean("dataSaved", true).commit();
                                progressDialog.dismiss();
                                progressDialog = null;

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

    public Menu getMenu() {
        return menu;
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {

            int genderposition;
            if (ParseUser.getCurrentUser().getInt("genderCode") == 0)
                genderposition = position + 10;
            else
                genderposition = position;

            return new Fragment_services_test(genderposition);
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


}
