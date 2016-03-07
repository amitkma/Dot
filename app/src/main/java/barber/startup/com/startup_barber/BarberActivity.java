package barber.startup.com.startup_barber;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BarberActivity extends AppCompatActivity {

    private String[] b;
    private TextView tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_barber);

        tv = (TextView) findViewById(R.id.textView);
        tv.setText("Barber Details \n");

        Intent i = getIntent();
        if (i != null) {

            Bundle bundle = i.getBundleExtra("objectIdList");
            b = bundle.getStringArray("OBJECTID");
        }
        ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("Data");
        parseQuery.whereContainedIn("objectId", Arrays.asList(b));
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects != null) {
                    ArrayList<ServiceDescriptionFormat> arrayList = new ArrayList<ServiceDescriptionFormat>();
                    for (int i = 0; i < objects.size(); i++) {

                        JSONArray jsonArray = objects.get(i).getJSONArray("serviceDescription");
                        arrayList.clear();
                        for (int j = 0; j < jsonArray.length(); j++) {
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                ServiceDescriptionFormat serviceDescriptionFormat = new ServiceDescriptionFormat();
                                serviceDescriptionFormat.setBarberObjectId(jsonObject.getString("barberObjectId"));
                                serviceDescriptionFormat.setBarberId(jsonObject.getString("barberId"));
                                serviceDescriptionFormat.setBarberName(jsonObject.getString("barberName"));
                                Log.e("Bol", jsonObject.getString("barberName"));
                                serviceDescriptionFormat.setServicePrice(jsonObject.getInt("servicePrice"));
                                serviceDescriptionFormat.setServiceTime(jsonObject.getInt("serviceTime"));

                                arrayList.add(serviceDescriptionFormat);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }

                        }
                    }

                    updateTextView(arrayList);
                } else Log.e("BarberActivity", e.getMessage());
            }
        });

    }

    private void updateTextView(ArrayList<ServiceDescriptionFormat> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            ServiceDescriptionFormat serviceDescriptionFormat = arrayList.get(i);
            tv.append(serviceDescriptionFormat.getBarberName() + " Price:" + serviceDescriptionFormat.getServicePrice() + " Time:" + serviceDescriptionFormat.getServiceTime() + "\n");
        }
    }
}
