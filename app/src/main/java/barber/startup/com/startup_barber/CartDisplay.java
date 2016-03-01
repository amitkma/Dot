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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.List;


public class CartDisplay extends AppCompatActivity {
    static ParseUser parseUser = ParseUser.getCurrentUser();
    static List<String> list;
    int totaltime = 0;
    private RecyclerView mRecyclerView;
    private CartActivityAdapter cartActivityAdapter;
    private Toolbar toolbar;
    private TextView retry;
    private TextView empty;

    public static void remove_item(int position) {
        list.remove(position);
        parseUser.put("cart", list);
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d("Cart", "Removed");
                //  BaseActivity.updatecart();

            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_display);


        retry = (TextView) findViewById(R.id.retry);
        empty = (TextView) findViewById(R.id.empty);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_cart);

        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        toolbarTitle();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCheckoutActivity();
            }
        });


        final ImageView delete = (ImageView) toolbar.findViewById(R.id.imageview_deleteAll);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ParseObject.unpinAllInBackground("Cart" + ParseUser.getCurrentUser().getUsername(), new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {

                            cartActivityAdapter.removeAllviews(empty);
                        } else e.printStackTrace();
                    }
                });
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

        backArrow_toolbar();


    }

    private void startCheckoutActivity() {
        Intent i = new Intent(CartDisplay.this, Checkout.class);
        i.putExtra("totalTimeTaken", totaltime);
        startActivity(i);
    }


    private void getObjects(String[] a) {

        mRecyclerView.setAdapter(cartActivityAdapter);

        final ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("Data");
        parseQuery.whereContainedIn("objectId", Arrays.asList(a));
        parseQuery.fromPin("data");
        parseQuery.orderByDescending("updatedAt");

        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects1, ParseException e) {
                if (e == null) {
                    if (Application.APPDEBUG)
                        Log.d("Cart", "fetched locally");

                    for (int i = 0; i < objects1.size(); i++) {
                        final ParseObject parseObject = objects1.get(i);
                        final Data td = new Data();
                        td.title = parseObject.getString("title");
                        td.price = parseObject.getString("price");
                        td.id = parseObject.getObjectId();
                        td.time = parseObject.getInt("time");

                        totaltime = td.time + totaltime;
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

    private void backArrow_toolbar() {
        ImageView back_button = (ImageView) toolbar.findViewById(R.id.button_arrow_back);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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

}
