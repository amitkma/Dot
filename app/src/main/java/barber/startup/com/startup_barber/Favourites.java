package barber.startup.com.startup_barber;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class Favourites extends AppCompatActivity {

    List<String> listcart = new ArrayList<String>();
    List<String> listfav = new ArrayList<String>();
    private RecyclerView recyclerView_fav;
    private StaggeredGridLayoutManager gaggeredGridLayoutManager;
    private FavActivityAdapter favAdapter;
    private Toolbar toolbar;
    private TextView empty;
    private ImageView back_button;
    private int categories[] = {0, 1, 2};
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        toolbar = (Toolbar) findViewById(R.id.toolbar_fav);
        empty = (TextView) findViewById(R.id.empty);

        setSupportActionBar(toolbar);


//        setup_toolbar_actions();
        recyclerView_fav = (RecyclerView) findViewById(R.id.recyclerview_fav);
        recyclerView_fav.setHasFixedSize(true);
        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);

        recyclerView_fav.setLayoutManager(gaggeredGridLayoutManager);
        favAdapter = new FavActivityAdapter(this, empty);


        ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>(Defaults.FavouritesClass);
        parseQuery.fromPin(ParseUser.getCurrentUser().getUsername());
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {


                    if (objects.size() > 0) {


                        String[] a = new String[objects.size()];
                        for (int i = 0; i < objects.size(); i++) {
                            ParseObject parseObject = objects.get(i);
                            listfav.add(parseObject.getString("favourites"));
                        }
                        Log.d("fav", String.valueOf(listfav));

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

    private void deleteAll() {


        ParseObject.unpinAllInBackground(ParseUser.getCurrentUser().getUsername(), new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                    favAdapter.removeAllviews(empty);
                } else e.printStackTrace();
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

        return super.onOptionsItemSelected(item);
    }

    private void getObjects(String[] a) {

        recyclerView_fav.setAdapter(favAdapter);

        final ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>(Defaults.INFO_CLASS);
        parseQuery.whereContainedIn("objectId", listfav);
        parseQuery.fromPin("data");
        parseQuery.orderByDescending("updatedAt");

        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> objects1, ParseException e) {
                if (e == null) {
                    if (Application.DEBUG)
                        Log.d("Favourites", "fetched locally");


                    ParseQuery<ParseObject> parseObjectParseQuery2 = new ParseQuery<ParseObject>("Cart");
                    parseObjectParseQuery2.fromPin("Cart" + ParseUser.getCurrentUser().getUsername());
                    parseObjectParseQuery2.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (objects != null) {
                                for (int i = 0; i < objects.size(); i++) {
                                    final ParseObject parseObject = objects.get(i);
                                    listcart.add(parseObject.getString("cart"));
                                }


                                for (int i = 0; i < objects1.size(); i++) {
                                    final ParseObject parseObject = objects1.get(objects1.size() - i - 1);
                                    final Data td = new Data();
                                    td.title = parseObject.getString("title");
                                    td.price = parseObject.getString("price");
                                    td.id = parseObject.getObjectId();
                                    ParseFile parseFile = parseObject.getParseFile("image");
                                    td.url = parseFile.getUrl();

                                    if (listcart.contains(parseObject.getObjectId())) {
                                        Log.d("Reached", "passed");
                                        td.cart = true;
                                    }
                                    favAdapter.addData(td);


                                }


                                Log.d("listcart", String.valueOf(listcart));
                            }
                        }
                    });





                       /* ParseQuery<ParseObject> parseObjectParseQuery = new ParseQuery<ParseObject>("Cart");
                        parseObjectParseQuery.fromPin("Cart" + ParseUser.getCurrentUser().getUsername());
                        parseObjectParseQuery.whereEqualTo("cart", parseObject.getObjectId());
                        parseObjectParseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                if (e == null && object != null) {
                                    td.cart = true;
                                    favAdapter.addData(td);
                                } else favAdapter.addData(td);


                            }
                        });     */

                }
            }
        });
    }

    @Override
    public void onBackPressed() {


        finish();
        overridePendingTransition(0, 0);

    }

    public Menu getMenu() {
        return menu;
    }
}
