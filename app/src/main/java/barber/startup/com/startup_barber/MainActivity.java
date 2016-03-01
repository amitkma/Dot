package barber.startup.com.startup_barber;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    ParseUser parseUser = ParseUser.getCurrentUser();


    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        changeTabsFont();


        toolbar = setup_toolbar();
        setup_nav_drawer();
        setup_nav_item_listener();
        update_cart_text();

        setup_favIcon_toolbar();
        setup_cartIcon_toolbar();





    }


    private void setup_cartIcon_toolbar() {
        ImageView cart = (ImageView) toolbar.findViewById(R.id.cart_image);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CartDisplay.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }

    private void setup_favIcon_toolbar() {
        ImageView fav = (ImageView) toolbar.findViewById(R.id.fav_image);
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Favourites.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Fragment_services(0), "Beards");
        adapter.addFragment(new Fragment_services(1), "Moustache");
        adapter.addFragment(new Fragment_services(2), "Hairstyle");


        viewPager.setAdapter(adapter);

    }

    public void setup_nav_item_listener() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                switch (menuItem.getItemId()) {

                    case R.id.About:
                        drawerLayout.closeDrawers();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                final Dialog dialog = new Dialog(MainActivity.this);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.about_developer);
                                dialog.show();
                            }
                        }, 150);
                        return true;

                    case R.id.Cart:
                        drawerLayout.closeDrawers();
                        menuItem.setTitle("Cart");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(MainActivity.this, CartDisplay.class));

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
                        Log.d("REACHED", "ECHED");
                        parseUser = null;
                        Intent i = new Intent(getApplicationContext(), Login.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(i);
                        finish();
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

                    } else
                        e.printStackTrace();
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





    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
