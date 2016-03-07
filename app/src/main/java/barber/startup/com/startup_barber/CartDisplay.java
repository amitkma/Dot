package barber.startup.com.startup_barber;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;


public class CartDisplay extends AppCompatActivity {
    int totaltime = 0;
    private RecyclerView mRecyclerView;
    private CartActivityAdapter cartActivityAdapter;
    private Toolbar toolbar;
    private TextView retry;
    private TextView empty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_display);


        retry = (TextView) findViewById(R.id.retry);
        empty = (TextView) findViewById(R.id.empty);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_cart);

        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check_connection()) {
                    if (totaltime > 120)
                        Snackbar.make(findViewById(R.id.coordinatorlayout), "Total time required exceeds 2 hrs", Snackbar.LENGTH_LONG).show();
                    else startCheckoutActivity();
                } else
                    Snackbar.make(findViewById(R.id.coordinatorlayout), "You are offline", Snackbar.LENGTH_LONG).show();


            }
        });


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_cart);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        cartActivityAdapter = new CartActivityAdapter(this, empty);
        ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("Cart");
        parseQuery.fromPin("Cart" + ParseUser.getCurrentUser().getUsername());
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        String[] a = new String[objects.size()];
                        for (int i = 0; i < objects.size(); i++) {
                            ParseObject parseObject = objects.get(i);
                            a[i] = parseObject.getString("cart");
                        }


                        getObjects(a);
                    } else empty.setVisibility(View.VISIBLE);
                } else e.printStackTrace();
            }
        });

        backArrow();


    }

    private void backArrow() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_deleteAll) {

            deleteAll();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteAll() {
        ParseObject.unpinAllInBackground(Defaults.CartClass+ ParseUser.getCurrentUser().getUsername(), new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                    cartActivityAdapter.removeAllviews(empty);
                } else e.printStackTrace();
            }
        });
    }





    private void getObjects(String[] a) {

        mRecyclerView.setAdapter(cartActivityAdapter);

        final ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>(Defaults.DataClass);
        parseQuery.whereContainedIn("objectId", Arrays.asList(a));
        parseQuery.fromPin("data");
        parseQuery.orderByDescending("updatedAt");

        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects1, ParseException e) {
                if (e == null) {
                    if (Application.DEBUG)
                        Log.d("Cart", "fetched locally");

                    for (int i = 0; i < objects1.size(); i++) {
                        final ParseObject parseObject = objects1.get(i);
                        final Data td = new Data();
                        td.title = parseObject.getString("title");
                        td.price = parseObject.getString("price");
                        td.id = parseObject.getObjectId();
                        td.time = parseObject.getInt("time");

                        Log.d("td.time", String.valueOf(td.getTime()));
                        Log.d("td.total", String.valueOf(totaltime));
                        totaltime = parseObject.getInt("time") + totaltime;
                        ParseFile parseFile = parseObject.getParseFile("image");
                        td.url = parseFile.getUrl();
                        cartActivityAdapter.addData(td);
                    }
                }
            }
        });
    }


    private void toolbarTitle() {
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.title_toolbar);
        toolbar_title.setText("Cart");
        Typeface tfe = Typeface.createFromAsset(getAssets(), "fonts/CaveatBrush-Regular.ttf");
        toolbar_title.setTypeface(tfe);
        toolbar_title.setSelected(true);
        toolbar_title.setSingleLine(true);
    }


    private void startCheckoutActivity() {


        ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>(Defaults.CartClass);
        parseQuery.fromPin("Cart" + ParseUser.getCurrentUser().getUsername());
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        final String[] b = new String[objects.size()];
                        for (int i = 0; i < objects.size(); i++) {
                            ParseObject parseObject = objects.get(i);
                            b[i] = parseObject.getString("cart");
                        }

                        ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>(Defaults.DataClass);
                        parseQuery.whereContainedIn("objectId", Arrays.asList(b));
                        parseQuery.fromPin("data");

                        parseQuery.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects1, ParseException e) {
                                if (e == null) {
                                    if (objects1.size() > 0) {
                                        for (int i = 0; i < objects1.size(); i++) {
                                            final ParseObject parseObject = objects1.get(i);
                                            parseObject.getInt("time");
                                            // totaltime = parseObject.getInt("time") + totaltime;
                                        }
                                        Intent i = new Intent(CartDisplay.this, BarberActivity.class);

                                        Bundle bundle = new Bundle();
                                        bundle.putStringArray("OBJECTID", b);
                                        i.putExtra("totalTimeTaken", totaltime);
                                        i.putExtra("objectIdList", bundle);
                                        startActivity(i);

                                        overridePendingTransition(0, 0);

                                    }
                                }
                            }
                        });
                    }

                }
            }

        });


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


    @Override
    public void onBackPressed() {

        startActivity(new Intent(CartDisplay.this, MainActivity.class));
        finish();
        overridePendingTransition(0, 0);

    }

    public void updateTotalTime(int lesstime) {
        Log.d("CartDisplay", Integer.toString(totaltime));
        totaltime = totaltime - lesstime;
        Log.d("CartDisplay", Integer.toString(totaltime));
    }

}
