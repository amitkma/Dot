package barber.startup.com.startup_barber;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import barber.startup.com.startup_barber.Utility.UserFavsAndCarts;

public class Checkout extends AppCompatActivity {

    private int time;
    private int price;
    private int barberId;


    private TextView totalprice;
    private TextView totaltime;
    private TextView mAvailableSlotText;


    // Declaring arraylist variables for available time slots and already booked slots
    private ArrayList<TimeSlotFormat> availableTimeSlots = new ArrayList<>();
    private JSONArray bookedTimeSlots = new JSONArray();
    private Date updateTime;
    private String objectId;
    private ProgressBar progressBar;
    private String[] objectsId;
    private Toolbar toolbar;
    private String dateFormat;
    private Button button_time;
    private FloatingActionButton fab;
    private String timeslotstart;
    private String dateformatintent;
    private JSONObject jsonObject;
    private String barberName;
    private TextView checkoutbutton;
    private String timeSlot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbarTitle();
        backArrow_toolbar();


        //fab = (FloatingActionButton) findViewById(R.id.fab);
        checkoutbutton = (TextView) findViewById(R.id.checkout);
        checkoutbutton.setVisibility(View.INVISIBLE);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        Intent i = getIntent();
        if (i != null) {
            time = i.getIntExtra("totalTime", 0);
            price = i.getIntExtra("totalPrice", 0);
            barberId = i.getIntExtra("barberId", -1);
            barberName = i.getStringExtra("barberName");
        }

        Button select_date = (Button) findViewById(R.id.button_date);
        select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (time >= 0) {
                    DialogFragment dialogFragment = new DatePickerFragment();
                    dialogFragment.show(getFragmentManager(), "DatePicker");
                }
            }
        });


        button_time = (Button) findViewById(R.id.time);
        button_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getFragmentManager(), "TimePicker");
            }
        });


        totalprice = (TextView) findViewById(R.id.price);
        totaltime = (TextView) findViewById(R.id.times);
        mAvailableSlotText = (TextView) findViewById(R.id.availableSlots);

        totaltime.setText("Total Time : " + time + " min");
        totalprice.setText("Total Price: " + "Rs. " + price);


    }

    private void finalcheckoutFAB(final int hourOfDay, final int minute) {
        checkoutbutton.setVisibility(View.VISIBLE);
        checkoutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSlots(hourOfDay, minute);
            }
        });
    }

    private void backArrow_toolbar() {
        ImageView back_button = (ImageView) toolbar.findViewById(R.id.button_arrow_back);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void settextTime(int hourOfDay, int minute) {
        TextView text_time = (TextView) findViewById(R.id.text_time);
        text_time.setText(String.format("%02d:%02d", hourOfDay, minute));
        timeslotstart = String.format("%02d:%02d", hourOfDay, minute);

        finalcheckoutFAB(hourOfDay, minute);

    }

    ;


    private void toolbarTitle() {
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.title_toolbar);
        toolbar_title.setText("Checkout");
        Typeface tfe = Typeface.createFromAsset(getAssets(), "fonts/CaveatBrush-Regular.ttf");
        toolbar_title.setTypeface(tfe);
        toolbar_title.setSelected(true);
        toolbar_title.setSingleLine(true);
    }

    // Method for returning slots status as a string.
    public String getCurrentSlots(int slotType) {

        StringBuilder availableTimeSlotsString = new StringBuilder("Available Slots \n"); // Header text for available slots
        StringBuilder bookedTimeSlotsString = new StringBuilder(""); // Empty text for booked slots

        if (slotType == Defaults.SLOT_TYPE_AVAILABLE) {  // Check whether returning slots status is of AVAILABLE TYPE

            for (int i = 0; i < availableTimeSlots.size(); i++) {
                TimeSlotFormat currentTimeData = availableTimeSlots.get(i);

                if (currentTimeData.getStartTimeData() != currentTimeData.getEndTimeData()) {

                    int startTimeHour = currentTimeData.getStartTimeData() / 100;
                    int startTimeMinute = currentTimeData.getStartTimeData() % 100;
                    int endTimeHour = currentTimeData.getEndTimeData() / 100;
                    int endTimeMinute = (currentTimeData.getEndTimeData() % 100);

                    availableTimeSlotsString.append(String.format("%02d:%02d - %02d:%02d \n", startTimeHour, startTimeMinute, endTimeHour, endTimeMinute));
                } else {
                    availableTimeSlots.remove(i);
                    i--;
                }

            }

            return availableTimeSlotsString.toString();

        }
        return "";   // returns empty string in default case( slots are of none type )
    }


    public void getSlotData(final String dateFormat) {
        availableTimeSlots.clear();


        ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("Barber");
        parseQuery.whereEqualTo("barberId", barberId);
        parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    if (object != null) {
                        updateTime = object.getUpdatedAt();
                        objectId = object.getObjectId();
                        jsonObject = object.getJSONObject("availableTimeJSON");
                        try {

                            JSONArray jsonArray = jsonObject.getJSONArray(dateFormat);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject timeSlotObject = jsonArray.getJSONObject(i);
                                TimeSlotFormat timeSlotFormat = new TimeSlotFormat(timeSlotObject.getInt("mStartTime"), timeSlotObject.getInt("mEndTime"));
                                availableTimeSlots.add(timeSlotFormat);
                            }
                        } catch (JSONException e1) {
                            if (Application.DEBUG)
                                Log.e("Checkout:JSON object", e1.getMessage());
                            TimeSlotFormat timeSlotFormat = new TimeSlotFormat(900, 2200);
                            availableTimeSlots.add(timeSlotFormat);


                        }
                        progressBar.setVisibility(View.GONE);
                        mAvailableSlotText.setText(getCurrentSlots(Defaults.SLOT_TYPE_AVAILABLE));
                    } else if (Application.DEBUG) Log.e("Checkout", "object is null");
                } else if (Application.DEBUG)
                    Log.e("Checkout:getslotdata", e.getMessage());
            }
        });
    }

    //Method called after date is selected
    public void setDate(int year, int monthOfYear, int dayOfMonth) {
        TextView text_date = (TextView) findViewById(R.id.text_date);

        text_date.setText(String.format("%4d/%02d/%02d", year, monthOfYear + 1, dayOfMonth));
        dateFormat = String.format("%4d%02d%02d", year, monthOfYear + 1, dayOfMonth);
        dateformatintent = String.format("%02d/%02d/%4d", dayOfMonth, monthOfYear + 1, year);
        button_time.setVisibility(View.VISIBLE);
        if (Application.DEBUG)
            Log.e("DATE", dateFormat);
        progressBar.setVisibility(View.VISIBLE);

        getSlotData(dateFormat);


    }

    // Method for checking whether the desired slot is available or not
    public int checkSlots(int startTimeNumberFormat, int endTimeNumberFormat) {

        // Performing binary search to find a particular slot
        int low = 0;
        int high = availableTimeSlots.size() - 1;

        while (high >= low) {

            int mid = (high + low) / 2;

            TimeSlotFormat currentTimeSlot = availableTimeSlots.get(mid);

            if (startTimeNumberFormat >= currentTimeSlot.getStartTimeData()) {

                if (endTimeNumberFormat <= currentTimeSlot.getEndTimeData()) {
                    return mid;
                } else if (endTimeNumberFormat > currentTimeSlot.getEndTimeData() && startTimeNumberFormat >= currentTimeSlot.getEndTimeData()) {
                    low = mid + 1;
                } else
                    return -low - 1;
            } else
                high = mid - 1;
        }

        return -low - 1;
    }


    // Method for updating slots status in available slots list and booked slots list
    public void updateSlots(int hours, int minutes) {

        int startTimeSlotFormat = hours * 100 + ((minutes) % 60); // Convert preferred slot's start time in TimeSlot Format

        // Check whether total minutes are greater than 60 or not to increase no. of hours

        if ((minutes + time) >= 60)
            hours++;

        int endTimeSlotFormat = hours * 100 + ((minutes + time) % 60); // Convert preferred slot's end time in TimeSlot Format


        bookedTimeSlots.put(startTimeSlotFormat);
        bookedTimeSlots.put(endTimeSlotFormat);

        int updateSlotPosition = checkSlots(startTimeSlotFormat, endTimeSlotFormat); // Returns corresponding slot position or -1 if no slot is available

        if (updateSlotPosition > -1) {

            if (!checkDataSetChanged()) {

                // Create new slots and updating current slots in available slots list
                TimeSlotFormat updateCurrentTimeSlot = availableTimeSlots.get(updateSlotPosition);
                TimeSlotFormat newSlot = new TimeSlotFormat(updateCurrentTimeSlot.getStartTimeData(), startTimeSlotFormat);
                TimeSlotFormat newSlotDivision = new TimeSlotFormat(endTimeSlotFormat, updateCurrentTimeSlot.getEndTimeData());

                // Update available slot list by adding new slots
                availableTimeSlots.remove(updateSlotPosition);
                availableTimeSlots.add(updateSlotPosition, newSlot);
                availableTimeSlots.add(updateSlotPosition + 1, newSlotDivision);

                // Update available slots text view
                mAvailableSlotText.setText(getCurrentSlots(Defaults.SLOT_TYPE_AVAILABLE));

                updateParseSlot(availableTimeSlots, hours, minutes);
            } else {
                Snackbar.make(findViewById(R.id.coordinatorlayout), "Slots data changed. Conflict!!!", Snackbar.LENGTH_INDEFINITE).show();
                getSlotData(dateFormat);
            }

        } else
            Snackbar.make(findViewById(R.id.coordinatorlayout), "No available slot at that time.", Snackbar.LENGTH_INDEFINITE).show(); // No slots available toast

    }

    private boolean checkDataSetChanged() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Barber");
        ParseObject parseObject = null;
        try {
            parseObject = query.get(objectId);
            Date newUpdated = parseObject.getUpdatedAt();
            if (newUpdated.after(updateTime)) {
                return true;
            } else
                return false;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;

    }

    private void updateParseSlot(final ArrayList<TimeSlotFormat> availableTimeSlots, final int hours, final int minutes) {
        ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("Barber");
        parseQuery.whereEqualTo("barberId", barberId);
        parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null && object != null) {
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < availableTimeSlots.size(); i++) {
                        int mStartTime = availableTimeSlots.get(i).getStartTimeData();
                        int mEndTime = availableTimeSlots.get(i).getEndTimeData();
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("mStartTime", mStartTime);
                            jsonObject.put("mEndTime", mEndTime);

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        jsonArray.put(jsonObject);
                    }

                    try {
                        jsonObject.put(dateFormat, jsonArray);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    object.put("availableTimeJSON", jsonObject);
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                if (Application.DEBUG)
                                    Log.e("SAVED", "HO GYA BHAI");
                                saveAppointment(hours, minutes);
                            } else {
                                if (Application.DEBUG)
                                    Log.e("SAVED", e.getMessage());

                            }

                        }
                    });

                }


            }
        });
    }

    private void saveAppointment(int hours, int minutes) {

         timeSlot = null;

        try {
            int startTimeHour = bookedTimeSlots.getInt(0)/ 100;
            int startTimeMinute = bookedTimeSlots.getInt(0)% 100;
            int endTimeHour = bookedTimeSlots.getInt(1)/ 100;
            int endTimeMinute = bookedTimeSlots.getInt(1)% 100;

            timeSlot = new String(String.format("%02d:%02d - %02d:%02d \n", startTimeHour, startTimeMinute, endTimeHour, endTimeMinute));
        } catch (JSONException e) {
            e.printStackTrace();
        }



        final ParseObject parseObject = new ParseObject("Appointments");

        parseObject.put("servicesId", UserFavsAndCarts.listcart);
        parseObject.put("user", ParseUser.getCurrentUser().getUsername());
        parseObject.put("userId", ParseUser.getCurrentUser().getObjectId());
        parseObject.put("url", ParseUser.getCurrentUser().getString("picUri"));
        parseObject.put("barberId", barberId);
        parseObject.put("barberName", barberName);
        parseObject.put("timeSlot", timeSlot);
        parseObject.put("date", Integer.valueOf(dateFormat));
        parseObject.put("completed", false);
        parseObject.put("bookedSlot", bookedTimeSlots);

        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                    Intent i = new Intent(Checkout.this, Confirmation.class);
                    Random r = new Random();
                    int pin = r.nextInt(99999 - 9999) + 9999;
                    i.putExtra("pin", pin);
                    i.putExtra("price", price);
                    i.putExtra("barberName", barberName);
                    i.putExtra("totalTime", time);
                    i.putExtra("appointmentDate", dateformatintent);
                    i.putExtra("timeslot", timeSlot);


                    //Send Push to user and barber
                    ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
                    query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());  //Send to this user only
                    String a[] = {"oEhBK7XN5Q", ParseUser.getCurrentUser().getObjectId()};
                    query.whereContainedIn("userId", Arrays.asList(a));
                    JSONObject jSonobj;
                    try {
                        jSonobj = new JSONObject();
                        jSonobj.put("action", "com.thoughtrix.introduce.UPDATE_STATUS");
                        jSonobj.put("from", "Barber");
                        jSonobj.put("title", "Startup");
                        jSonobj.put("alert", "Booking Confirmation\nDate: " + dateformatintent + "\nTimeSlot: " + timeslotstart + " + " + time + "min" + "\nBarber: " + barberName);


                        sendPushtoUser();
                        ParsePush push = new ParsePush();
                        push.setQuery(query);
                        push.setData(jSonobj);
                        push.sendInBackground(new SendCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null)
                                    if (Application.DEBUG)
                                        Log.d("Pushing", e.getMessage());
                            }
                        });

                    } catch (JSONException e3) {
                        if (Application.DEBUG)
                            Log.d("push", e3.getMessage());
                    }


                    startActivity(i);
                    finish();
                    overridePendingTransition(0, 0);
                } else if ((Application.DEBUG)) Log.d("e", e.getMessage());
            }
        });
    }

    private void sendPushtoUser() {

    }

    @Override
    public void onBackPressed() {


        finish();
        overridePendingTransition(0, 0);
    }


}
