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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class Appointments extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Adapter_Appointments adapter_appointments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter_appointments = new Adapter_Appointments();
        recyclerView.setAdapter(adapter_appointments);
        ParseQuery<ParseObject> parseObjectParseQuery = new ParseQuery<ParseObject>("Appointments");
        parseObjectParseQuery.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
        parseObjectParseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                    if (objects.size() > 0) {
                        for (int i = 0; i < objects.size(); i++) {
                            ParseObject parseObject = objects.get(i);

                            FormatAppointments data = new FormatAppointments();

                            data.setBarber(parseObject.getString("barberName"));
                            data.setDate(parseObject.getString("date"));
                            data.setTimeslot(parseObject.getString("timeSlot"));

                            adapter_appointments.addData(data);
                        }
                    }
                } else if (Application.APPDEBUG) Log.e("Appointments", e.getMessage());
            }
        });


    }

}
