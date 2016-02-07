package barber.startup.com.startup_barber;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.List;

import static barber.startup.com.startup_barber.Cart.getCartItemsId;

public class CartDisplay extends AppCompatActivity {
    static ParseUser parseUser = ParseUser.getCurrentUser();
    static String[] s;
    static List<String> list;
    CountDownTimer waitTimer;
    boolean flag = false;
    ProgressBar progressBar;
    private ListView listView;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private CartActivityAdapter cartActivityAdapter;
    private Toolbar toolbar;
    private ParseQuery<ParseUser> query;
    private TextView retry;

    public static void remove_item(final Context c, int position) {
        list.remove(position);
        parseUser.put("cart", list);
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d("Cart", "Removed");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_display);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        retry = (TextView) findViewById(R.id.retry);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_cart);
        mRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        cartActivityAdapter = new CartActivityAdapter(this);

        startTimer();

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retry.setVisibility(View.INVISIBLE);
                getCartItemsId();
                startTimer();
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void startTimer() {
        getCartItemsId();
        progressBar.setVisibility(View.VISIBLE);

        waitTimer = new CountDownTimer(6000, 300) {

            public void onTick(long millisUntilFinished) {
                //called every 300 milliseconds, which could be used to
                //send messages or some other action
            }

            public void onFinish() {
                Log.d("timer", "query Cancelled by timer");

                query.cancel();
                progressBar.setVisibility(View.INVISIBLE);
                retry.setVisibility(View.VISIBLE);


                //After 60000 milliseconds (60 sec) finish current
                //if you would like to execute something when time finishes
            }
        }.start();

    }

    public void getCartItemsId() {

        mRecyclerView.setAdapter(cartActivityAdapter);
        query = ParseUser.getQuery();
        // query.get(parseUser.getObjectId())
        query.getInBackground(parseUser.getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(final ParseUser object, ParseException e) {
                if (e == null) {
                    list = object.getList("cart");
                    update_text_Items(list.size());
                    ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Data");
                    query.whereContainedIn("objectId", list);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {

                            if (waitTimer != null) {
                                waitTimer.cancel();
                                Log.d("timer", "Cancelled by query");
                                waitTimer = null;
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                            if (e == null) {

                                s = new String[objects.size()];
                                for (int i = 0; i < objects.size(); i++) {
                                    ParseObject parseObject = objects.get(i);
                                    Data data = new Data();
                                    data.price = parseObject.getString("price");
                                    data.title = parseObject.getString("title");
                                    ParseFile parseFile = parseObject.getParseFile("image");
                                    data.url = parseFile.getUrl();
                                    cartActivityAdapter.addData(data);
                                }

                            } else e.printStackTrace();
                        }
                    });
                } else e.printStackTrace();
            }
        });
    }

    private void update_text_Items(int size) {
        TextView items = (TextView) toolbar.findViewById(R.id.items);
        items.setText("Items: " + size);
    }


}
