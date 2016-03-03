package barber.startup.com.startup_barber;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import java.util.Arrays;
import java.util.List;

public class Favourites extends AppCompatActivity {

    private RecyclerView recyclerView_fav;
    private StaggeredGridLayoutManager gaggeredGridLayoutManager;
    private FavActivityAdapter favAdapter;
    private Toolbar toolbar;
    private TextView empty;
    private ImageView back_button;
    private int categories[] = {0, 1, 2};

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


        ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("Fav");
        parseQuery.fromPin(ParseUser.getCurrentUser().getUsername());
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {


                    if (objects.size() > 0) {


                        String[] a = new String[objects.size()];
                        for (int i = 0; i < objects.size(); i++) {
                            ParseObject parseObject = objects.get(i);
                            a[i] = parseObject.getString("favourites");
                        }


                        getObjects(a);
                    } else empty.setVisibility(View.VISIBLE);
                } else e.printStackTrace();
            }
        });


    }

    private void setup_toolbar_actions() {

        back_button = (ImageView) toolbar.findViewById(R.id.button_arrow_back);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView title = (TextView) toolbar.findViewById(R.id.title_toolbar);
        title.setText("Favourites");
        Typeface tfe = Typeface.createFromAsset(getAssets(), "fonts/CaveatBrush-Regular.ttf");
        title.setTypeface(tfe);
        title.setSelected(true);
        title.setSingleLine(true);

        final ImageView delete = (ImageView) toolbar.findViewById(R.id.fav_deleteAll);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ParseObject.unpinAllInBackground(ParseUser.getCurrentUser().getUsername(), new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {

                            favAdapter.removeAllviews(empty);
                        } else e.printStackTrace();
                    }
                });
            }
        });
    }

    private void getObjects(String[] a) {

        recyclerView_fav.setAdapter(favAdapter);

        final ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("Data");
        parseQuery.whereContainedIn("objectId", Arrays.asList(a));
        parseQuery.fromPin("data");
        parseQuery.orderByDescending("updatedAt");

        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects1, ParseException e) {
                if (e == null) {
                    if (Application.APPDEBUG)
                        Log.d("Favourites", "fetched locally");

                    for (int i = 0; i < objects1.size(); i++) {
                        final ParseObject parseObject = objects1.get(objects1.size() - i - 1);
                        final Data td = new Data();
                        td.title = parseObject.getString("title");
                        td.price = parseObject.getString("price");
                        td.id = parseObject.getObjectId();
                        ParseFile parseFile = parseObject.getParseFile("image");
                        td.url = parseFile.getUrl();
                        favAdapter.addData(td);
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(Favourites.this, MainActivity.class));
        finish();
        overridePendingTransition(0, 0);

    }
}
