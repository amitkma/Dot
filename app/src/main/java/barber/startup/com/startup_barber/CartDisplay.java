package barber.startup.com.startup_barber;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class CartDisplay extends AppCompatActivity {
    ParseUser parseUser = ParseUser.getCurrentUser();
    String[] s;
    List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getCartItemsId();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void getCartItemsId() {


        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(parseUser.getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(final ParseUser object, ParseException e) {
                if (e == null) {
                    list = object.getList("cart");

                    ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Data");

                    //  query.whereEqualTo("objectId", list.get(0));
                    query.whereContainedIn("objectId", list);

                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            Log.d("size", String.valueOf(objects.size()));
                            if (e == null) {
                                s = new String[objects.size()];
                                for (int i = 0; i < objects.size(); i++) {
                                    ParseObject parseObject = objects.get(i);
                                    s[i] = parseObject.getObjectId();
                                    if (i == objects.size() - 1)
                                        trigger();
                                }

                            } else e.printStackTrace();
                        }
                    });
                } else e.printStackTrace();
            }
        });
    }

    private void trigger() {

        Log.d("s", String.valueOf(s.length));
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, s);

        final ListView listView = (ListView) findViewById(R.id.list_item_cart);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                list.remove(position);
                parseUser.put("cart", list);
                parseUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(getApplicationContext(), "removed successfully", Toast.LENGTH_SHORT).show();
                        listView.requestLayout();
                    }
                });
            }
        });
    }
}