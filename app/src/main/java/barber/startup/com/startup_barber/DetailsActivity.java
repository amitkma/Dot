package barber.startup.com.startup_barber;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

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

import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {

    private String id;
    private RecyclerView recyclerView;
    private Data currentData;
    private ImageView imageView;

    private ArrayList<ServiceDescriptionFormat> barberList = new ArrayList<>();
    private Menu menu;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private int height;


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
            height = intent.getIntExtra("height", 10);
        }
        if (currentData.getUrl() != null) {
            int newHeight = height + dpToPx(72);
            Glide.with(DetailsActivity.this)
                    .load(currentData.getUrl())
                    .override(height, newHeight)

                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            Glide.with(DetailsActivity.this).load(currentData.getUrl()).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(resource).dontAnimate()
                                    .into(imageView);
                            return false;
                        }
                    })
                    .into(imageView);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.transparentToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Defaults.defaultObjectId = currentData.getId();
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
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_share:
                Toast.makeText(this, "We will add some action to it soon", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_add_to_cart:

                break;
            case R.id.action_go_cart:
                startActivity(new Intent(this, CartDisplay.class));
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    public Menu getMenu() {

        return menu;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}
