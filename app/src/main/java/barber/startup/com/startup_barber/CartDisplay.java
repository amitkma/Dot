package barber.startup.com.startup_barber;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import barber.startup.com.startup_barber.Utility.NetworkCheck;
import barber.startup.com.startup_barber.Utility.UserFavsAndCarts;


public class CartDisplay extends AppCompatActivity {
    int totaltime = 0;
    List<Data> listcart = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private CartActivityAdapter cartActivityAdapter;
    private Toolbar toolbar;
    private TextView retry;
    private TextView empty;
    private Menu menu;
    private TextView checkoutTextView;
    private TextView backTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_display);


        retry = (TextView) findViewById(R.id.retry);
        empty = (TextView) findViewById(R.id.empty);
        checkoutTextView = (TextView) findViewById(R.id.checkoutTextViewId);
        backTextView = (TextView)findViewById(R.id.backTextViewId);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_cart);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Cart");


        checkoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkCheck.checkConnection(getApplicationContext())) {
                    if (totaltime > 120)
                        Snackbar.make(findViewById(R.id.coordinatorlayout), "Total time required exceeds 2 hrs", Snackbar.LENGTH_LONG).show();
                    else startCheckoutActivity();
                } else
                    Snackbar.make(findViewById(R.id.coordinatorlayout), "You are offline", Snackbar.LENGTH_LONG).show();


            }
        });

        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0 ,0);
            }
        });


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_cart);
        mRecyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager linearLayoutManager = new StaggeredGridLayoutManager(2, 1);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        cartActivityAdapter = new CartActivityAdapter(this, listcart, empty);

        ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>(Defaults.INFO_CLASS);
        parseQuery.fromPin("data");
        parseQuery.whereContainedIn("objectId", UserFavsAndCarts.listcart);
        Log.e("Fav", "passed");
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.i("Fav", "passed" + objects.size());
                    if (objects.size() > 0) {
                        for (int i = 0; i < objects.size(); i++) {
                            ParseObject parseObject = objects.get(i);
                            final Data td = new Data();
                            td.title = parseObject.getString("title");
                            td.price = parseObject.getString("price");
                            td.id = parseObject.getObjectId();
                            ParseFile parseFile = parseObject.getParseFile("image");
                            td.url = parseFile.getUrl();
                            if (UserFavsAndCarts.listcart.contains(td.getId()))
                                td.cart = true;
                            listcart.add(td);
                        }
                        Log.d("fav", String.valueOf(listcart));

                        cartActivityAdapter = new CartActivityAdapter(CartDisplay.this, listcart, empty);
                        mRecyclerView.setAdapter(cartActivityAdapter);
                    } else empty.setVisibility(View.VISIBLE);
                } else {
                    Log.i("Favourited", "passed,favcheeck");
                    Log.i("Favourited", "passed,favcheeck" + e.getMessage());
                }
            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fav, menu);
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
        if (id == R.id.action_deleteAll) {

            deleteAll();
            return true;
        }

        if(id == android.R.id.home){
            finish();
            overridePendingTransition(0, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteAll() {
        UserFavsAndCarts.listcart.clear();
        MainActivity.dataUpdated =true;
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < UserFavsAndCarts.listcart.size(); i++) {
            jsonArray.put(UserFavsAndCarts.listcart.get(i));
        }
        final ParseUser parseUser = ParseUser.getCurrentUser();
        parseUser.put("cartLists", jsonArray);
        parseUser.pinInBackground(ParseUser.getCurrentUser().getUsername(), new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    parseUser.saveEventually();
                    cartActivityAdapter.removeAllviews(empty);
                } else e.printStackTrace();
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

        final String[] b = new String[UserFavsAndCarts.listcart.size()];
        for (int i = 0; i < UserFavsAndCarts.listcart.size(); i++) {
            b[i] = UserFavsAndCarts.listcart.get(i);
        }


        Intent i = new Intent(CartDisplay.this, BarberActivity.class);

        Bundle bundle = new Bundle();
        bundle.putStringArray("OBJECTID", b);
        i.putExtra("totalTimeTaken", totaltime);
        i.putExtra("objectIdList", bundle);
        startActivity(i);

        overridePendingTransition(0, 0);

    }


    @Override
    public void onBackPressed() {

        finish();
        overridePendingTransition(0, 0);

    }
}
