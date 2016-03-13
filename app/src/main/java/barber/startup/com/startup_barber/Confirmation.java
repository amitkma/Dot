package barber.startup.com.startup_barber;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import barber.startup.com.startup_barber.Utility.UserFavsAndCarts;

public class Confirmation extends AppCompatActivity {


    private int time_taken;
    private String appointmentDate;
    private String txt_timeslot;
    private int txt_pin;
    private int txt_price=0;
    private int txt_numberOfServices = 0;
    private String txt_barber;

    private RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_reciept);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity();

            }
        });

        relativeLayout = (RelativeLayout)findViewById(R.id.relative);
        Button btn = (Button)findViewById(R.id.close_receipt);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity();
            }
        });
        TextView username = (TextView) findViewById(R.id.confirm_username);
        TextView barber = (TextView) findViewById(R.id.confirm_barber);
        TextView cost = (TextView) findViewById(R.id.confirm_price);
        TextView duration = (TextView)findViewById(R.id.duration);
        TextView pin = (TextView) findViewById(R.id.confirm_pin);
        TextView numberOfServices = (TextView) findViewById(R.id.number_services1);
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

        numberOfServices.setText(Integer.toString(UserFavsAndCarts.listcart.size()));
        username.setText(ParseUser.getCurrentUser().getUsername());
        barber.setText("Barber:  " + txt_barber);
        cost.setText("Rs " + Integer.toString(txt_price));
        pin.setText(Integer.toString(txt_pin));
        date.setText(appointmentDate+" at "+txt_timeslot);
        duration.setText(Integer.toString(time_taken)+" mins");

        final ParseUser parseUser = ParseUser.getCurrentUser();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", parseUser.getObjectId());
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    Defaults.mNumberOfServicesLeft = object.getInt("rewardWallet");
                    parseUser.put("rewardWallet", Defaults.mNumberOfServicesLeft-1);
                    parseUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){
                                Snackbar.make(relativeLayout, "You have " + (Defaults.mNumberOfServicesLeft - 1) + " service(s) left in your free wallet.", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                }

                }
        });


    }

    private void startMainActivity() {
        Intent i = new Intent(Confirmation.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        overridePendingTransition(0, 0);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startMainActivity();
    }
}
