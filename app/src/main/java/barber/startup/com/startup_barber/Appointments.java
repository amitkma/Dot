package barber.startup.com.startup_barber;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.List;

public class Appointments extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Adapter_Appointments adapter_appointments;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);

            }
        });

        tv = (TextView)findViewById(R.id.no_appointments);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter_appointments = new Adapter_Appointments();
        recyclerView.setAdapter(adapter_appointments);
        ParseQuery<ParseObject> parseObjectParseQuery = new ParseQuery<ParseObject>(Defaults.AppointmentClass);
        parseObjectParseQuery.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
        parseObjectParseQuery.orderByDescending("createdAt");
        parseObjectParseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if(objects!=null) {
                        if (objects.size() > 0) {
                            tv.setVisibility(View.GONE);
                            for (int i = 0; i < objects.size(); i++) {
                                ParseObject parseObject = objects.get(i);

                                FormatAppointments data = new FormatAppointments();
                                data.setBarber(parseObject.getString("barberName"));
                                data.setTotalPrice(parseObject.getInt("totalPrice"));
                                JSONArray jsonArray = parseObject.getJSONArray("servicesId");
                                int date = parseObject.getInt("date");
                                int year = date / 10000;
                                date = date % 10000;
                                int month = date / 100;
                                int day = date % 100;
                                data.setDate(String.format("%02d/%02d/%4d", day, month, year));
                                data.setObjectId(parseObject.getObjectId());
                                if (jsonArray != null)
                                    data.setNumberOfServices(jsonArray.length());
                                data.setTimeslot(parseObject.getString("timeSlot"));
                                adapter_appointments.addData(data);
                            }
                        } else
                            tv.setVisibility(View.VISIBLE);
                    }
                    else
                        tv.setVisibility(View.VISIBLE);
                } else if (Application.DEBUG) {Log.e("Appointments", e.getMessage());}
            }
        });


    }

}
