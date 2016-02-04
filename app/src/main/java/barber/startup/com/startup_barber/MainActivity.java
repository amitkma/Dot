package barber.startup.com.startup_barber;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class MainActivity extends BaseActivity {
    ParseUser parseUser = ParseUser.getCurrentUser();

    boolean track = false;
    View vi;
    RelativeLayout notifCount;
    TextView cartText;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MainActivityAdapter currentTrendsAdapter;
    private StaggeredGridLayoutManager gaggeredGridLayoutManager;
    private RecyclerView mRecyclerView_hairStyles;
    private StaggeredGridLayoutManager gaggeredGridLayoutManager_hairStyles;
    private MainActivityAdapter hairStyleAdaper;
    private FrameLayout frame_beards;
    private FrameLayout frame_hairStyles;
    private boolean temp = false;
    private FrameLayout frame_mustache;
    private RecyclerView mRecyclerView_moustache;
    private StaggeredGridLayoutManager gaggeredGridLayoutManager_moustache;
    private MainActivityAdapter moustacheAdaper;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vi = findViewById(R.id.fade_view);
        frame_beards = (FrameLayout) findViewById(R.id.frame_beards);
        frame_hairStyles = (FrameLayout) findViewById(R.id.frame_hairStyles);
        frame_mustache = (FrameLayout) findViewById(R.id.frame_mustache);

        final FloatingActionMenu menu1 = (FloatingActionMenu) findViewById(R.id.menu);
        FloatingActionButton fab_hairStyles = (FloatingActionButton) findViewById(R.id.menu_item1);
        FloatingActionButton fab_mustache = (FloatingActionButton) findViewById(R.id.menu_item2);
        FloatingActionButton fab_beards = (FloatingActionButton) findViewById(R.id.menu_item3);

        AnimatorSet set = new AnimatorSet();
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

                menu1.getMenuIconView().setImageResource(menu1.isOpened()
                        ? R.drawable.ic_tune_white_24dp : R.drawable.ic_add_white_24dp);
                menu1.getMenuIconView().setRotation(menu1.isOpened() ? -45 : 0);


            }

            @Override
            public void onAnimationEnd(Animator animation) {
                menu1.getMenuIconView().setRotation(menu1.isOpened() ? 0 : 45);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        menu1.setIconToggleAnimatorSet(set);


        fab_hairStyles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (frame_mustache.getVisibility() == View.VISIBLE)
                    frame_mustache.setVisibility(View.INVISIBLE);

                else if (frame_beards.getVisibility() == View.VISIBLE)
                    frame_beards.setVisibility(View.INVISIBLE);
                toolbar.setTitle("HairCuts");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        frame_hairStyles.setVisibility(View.VISIBLE);
                    }
                }, 800);

                menu1.close(true);

            }


        });

        fab_mustache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (frame_beards.getVisibility() == View.VISIBLE)
                    frame_beards.setVisibility(View.INVISIBLE);

                else if (frame_hairStyles.getVisibility() == View.VISIBLE)
                    frame_hairStyles.setVisibility(View.INVISIBLE);
                toolbar.setTitle("Mustache");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        frame_mustache.setVisibility(View.VISIBLE);
                    }
                }, 800);
                menu1.close(true);

            }


        });
        fab_beards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (frame_mustache.getVisibility() == View.VISIBLE)
                    frame_mustache.setVisibility(View.INVISIBLE);

                else if (frame_hairStyles.getVisibility() == View.VISIBLE)
                    frame_hairStyles.setVisibility(View.INVISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        frame_beards.setVisibility(View.VISIBLE);
                    }
                }, 800);
                toolbar.setTitle("Beards");

                menu1.close(true);
            }


        });


        toolbar = setup_toolbar();
        setup_nav_drawer();
        setup_nav_item_listener();
        update_cart_text();


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        mRecyclerView.setLayoutManager(gaggeredGridLayoutManager);
        currentTrendsAdapter = new MainActivityAdapter(this);

        setUpRecyclerView();


        mRecyclerView_hairStyles = (RecyclerView) findViewById(R.id.recyclerview_hairStyles);
        mRecyclerView_hairStyles.setHasFixedSize(true);
        gaggeredGridLayoutManager_hairStyles = new StaggeredGridLayoutManager(2, 1);
        mRecyclerView_hairStyles.setLayoutManager(gaggeredGridLayoutManager_hairStyles);
        hairStyleAdaper = new MainActivityAdapter(this);

        show_hairstyles();

        mRecyclerView_moustache = (RecyclerView) findViewById(R.id.recyclerview_moustache);

        mRecyclerView_moustache.setHasFixedSize(true);
        gaggeredGridLayoutManager_moustache = new StaggeredGridLayoutManager(2, 1);
        mRecyclerView_moustache.setLayoutManager(gaggeredGridLayoutManager_moustache);
        moustacheAdaper = new MainActivityAdapter(this);

        show_moustache();

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add Filter", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    private void show_moustache() {

        mRecyclerView_moustache.setAdapter(moustacheAdaper);
        ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("DataMoustache");
        if (!check_connection())
            parseQuery.fromLocalDatastore();
        parseQuery.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (check_connection()) {
                    ParseObject.pinAllInBackground(objects);
                }
                for (int i = 0; i < objects.size(); i++) {
                    final ParseObject parseObject = objects.get(objects.size() - i - 1);
                    final Data td = new Data();
                    td.title = parseObject.getString("title");
                    td.price = parseObject.getString("price");
                    ParseFile parseFile = parseObject.getParseFile("image");
                    td.url = parseFile.getUrl();
                    moustacheAdaper.addData(td);
                }

            }

        });
    }

    private void show_hairstyles() {

        mRecyclerView_hairStyles.setAdapter(hairStyleAdaper);
        final ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("DataHairStyles");
        if (!check_connection())
            parseQuery.fromLocalDatastore();
        parseQuery.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                ParseObject.pinAllInBackground(objects);

                for (int i = 0; i < objects.size(); i++) {
                    final ParseObject parseObject = objects.get(objects.size() - i - 1);
                    final Data td = new Data();
                    td.title = parseObject.getString("title");
                    td.price = parseObject.getString("price");
                    ParseFile parseFile = parseObject.getParseFile("image");
                    td.url = parseFile.getUrl();

                    hairStyleAdaper.addData(td);
                }

            }

        });
    }

    private void setUpRecyclerView() {
        mRecyclerView.setAdapter(currentTrendsAdapter);
        final ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("Data");
        if (!check_connection())
            parseQuery.fromLocalDatastore();
        parseQuery.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                ParseObject.pinAllInBackground(objects);

                for (int i = 0; i < objects.size(); i++) {
                    final ParseObject parseObject = objects.get(objects.size() - i - 1);

                    final Data td = new Data();
                    td.title = parseObject.getString("title");
                    td.price = parseObject.getString("price");
                    td.id = parseObject.getObjectId();

                    ParseFile parseFile = parseObject.getParseFile("image");
                    td.url = parseFile.getUrl();

                    currentTrendsAdapter.addData(td);
                }

            }

        });
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

                            }
                        }, 150);
                        return true;

                    case R.id.Cart:
                        drawerLayout.closeDrawers();
                        menuItem.setTitle("Cart(0)");
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
                            }, 250);
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


    /*   ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(currentUser.getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                updatecart();
            }

            private void updatecart() {
                if (v.getId() == R.id.cardtext) {
                    int cart_items = 0;
                    if (parseUser.get("cartItems") != null)
                        cart_items = (int) parseUser.get("cartItems");
                    cartText = (TextView) v.findViewById(R.id.cardtext);
                    if (cart_items != 0)
                        cartText.setText("(" + cart_items + ")");
                }
            }
        });*/
    private void logout() {
        currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            ParseUser.logOutInBackground();
        }

        startActivity(new Intent(this, Choose_Login.class));
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);


    }

}
