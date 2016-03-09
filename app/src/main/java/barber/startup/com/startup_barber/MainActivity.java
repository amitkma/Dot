package barber.startup.com.startup_barber;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

public class MainActivity extends BaseActivity {
    protected static float width;
    protected static int height;
    protected static int a;
    private static Menu menu;
    ParseUser parseUser = ParseUser.getCurrentUser();
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private Dialog dialog;
    private String[] categoriesName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

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



int genderCode=ParseUser.getCurrentUser().getInt("genderCode");
        if (genderCode==0)
            this.categoriesName=Defaults.categoriesNameGirls;
        else if(genderCode==1)
            this.categoriesName=Defaults.categoriesNameBoys;
        dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.developer);

        ImageView ayush = (ImageView) dialog.findViewById(R.id.ayush);
        ImageView amit = (ImageView) dialog.findViewById(R.id.amit);

        Glide.with(getApplicationContext()).load((R.drawable.ayush)).into(ayush);
        Glide.with(getApplicationContext()).load((R.drawable.amit)).into(amit);


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(1);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.accent));
        changeTabsFont();


        toolbar = setup_toolbar();
        setup_nav_drawer();
        setup_nav_item_listener();


    }

    private void cartIcon_toolbar() {

        Intent intent = new Intent(MainActivity.this, CartDisplay.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(0, 0);

    }

    private void favIcon_toolbar() {

        Intent intent = new Intent(MainActivity.this, Favourites.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(0, 0);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Application.DEBUG)
            Log.i("MainActivity", "onStart() called");

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Application.DEBUG)
            Log.i("MainActivity", "onStop() called");

        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        Log.d("fraglist", String.valueOf(fragments));


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

                        if (check_connection()) {
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

    public static void makeFavIconRed() {

        MenuItem item = menu.findItem(R.id.action_fav);
        Drawable newIcon = (Drawable) item.getIcon();
        newIcon.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        item.setIcon(newIcon);

    }

    public static void makeCartIconBlue() {
        MenuItem item = menu.findItem(R.id.action_cart);
        Drawable newIcon = (Drawable) item.getIcon();
        newIcon.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
        item.setIcon(newIcon);
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

    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return new Fragment_services_test(position);
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
