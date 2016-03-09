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
    private int txt_pin;
    private int txt_price=0;
    private String txt_barber;

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
            txt_price=i.getIntExtra("price",0);
            txt_pin = i.getIntExtra("pin",-1);
            txt_barber=i.getStringExtra("barberName");
        }

        username.setText("Username:  " + ParseUser.getCurrentUser().getUsername());
        barber.setText("Barber:  " + txt_barber);
        timeslot.setText("TimeSlot:  " + txt_timeslot);
        cost.setText("Total Price:  Rs " + Integer.toString(txt_price));
        time.setText("Total time:  " + time_taken + " min");
        pin.setText(Integer.toString(txt_pin));
        date.setText("Date:  " + appointmentDate);

    }
}
