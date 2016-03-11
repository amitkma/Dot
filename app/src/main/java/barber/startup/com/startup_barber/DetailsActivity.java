package barber.startup.com.startup_barber;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import barber.startup.com.startup_barber.Utility.NetworkCheck;

public class DetailsActivity extends AppCompatActivity {

    private String id;
    private RecyclerView recyclerView;
    private Data currentData;
    private ImageView imageView;

    private ArrayList<ServiceDescriptionFormat> barberList = new ArrayList<>();
    private Menu menu;
    private ProgressBar progressBar;
    private CollapsingToolbarLayout collapsingToolbarLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        recyclerView = (RecyclerView) findViewById(R.id.detailsRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        imageView = (ImageView) findViewById(R.id.detailView);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setContentScrimResource(R.color.primary);

        Intent intent = getIntent();
        if (intent != null) {
            currentData = (Data) intent.getSerializableExtra("objectData");
        }

        Glide.with(DetailsActivity.this)
                .load(currentData.getUrl())
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.transparentToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (NetworkCheck.checkConnection(this)) {
            ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("Data");
            parseQuery.whereEqualTo("objectId", currentData.getId());
            parseQuery.fromPin("data");
            parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        JSONArray array = object.getJSONArray("serviceDescription");
                        Log.e("arrya", Integer.toString(array.length()));
                        for (int j = 0; j < array.length(); j++) {
                            try {
                                JSONObject jsonObject = array.getJSONObject(j);
                                ServiceDescriptionFormat serviceDescriptionFormat = new ServiceDescriptionFormat();
                                serviceDescriptionFormat.setBarberObjectId(jsonObject.getString("barberObjectId"));
                                serviceDescriptionFormat.setBarberName(jsonObject.getString("barberName"));
                                serviceDescriptionFormat.setBarberId(jsonObject.getInt("barberId"));
                                serviceDescriptionFormat.setServicePrice(jsonObject.getInt("servicePrice"));
                                serviceDescriptionFormat.setServiceTime(jsonObject.getInt("serviceTime"));
                                barberList.add(serviceDescriptionFormat);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }

                        }
                    } else
                        Log.e("DetailsActivity", e.getMessage());

                    DetailsActivityAdapter detailsActivityAdapter = new DetailsActivityAdapter(DetailsActivity.this, barberList, recyclerView);
                    recyclerView.setAdapter(detailsActivityAdapter);

                }
            });

        }
        else
            Snackbar.make(recyclerView, "Error in connection", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_activity, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
