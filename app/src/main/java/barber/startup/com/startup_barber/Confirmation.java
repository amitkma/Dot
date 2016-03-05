package barber.startup.com.startup_barber;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.parse.ParseUser;

public class Confirmation extends AppCompatActivity {


    private int time_taken;
    private String appointmentDate;
    private String txt_timeslot;
    private String txt_pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation);
        TextView username = (TextView) findViewById(R.id.confirm_username);
        TextView barber = (TextView) findViewById(R.id.confirm_barber);
        TextView timeslot = (TextView) findViewById(R.id.confirm_appointment_time);
        TextView cost = (TextView) findViewById(R.id.confirm_price);
        TextView time = (TextView) findViewById(R.id.confirm_time);
        TextView pin = (TextView) findViewById(R.id.confirm_pin);
        TextView date = (TextView) findViewById(R.id.confirm_appointment_date);


        Intent i = getIntent();
        if (i != null) {
            time_taken = i.getIntExtra("totalTime", 0);
            appointmentDate = i.getStringExtra("appointmentDate");
            txt_timeslot = i.getStringExtra("timeslot");
            txt_pin = i.getStringExtra("pin");
        }

        username.setText("Username:  " + ParseUser.getCurrentUser().getUsername());
        barber.setText("Barber:  " + "Jawahar Bhawan");
        timeslot.setText("TimeSlot:  " + txt_timeslot);
        cost.setText("Total Price:  " + "N/A");
        time.setText("Total time:  " + time_taken + " min");
        pin.setText(txt_pin);
        date.setText("Date:  " + appointmentDate);

    }
}
