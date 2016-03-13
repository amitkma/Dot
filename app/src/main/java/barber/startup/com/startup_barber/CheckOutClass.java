package barber.startup.com.startup_barber;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

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

/**
 * Created by Amit on 12-03-2016.
 */
public class CheckOutClass{

    private Context mContext;

    private Date updateTime;
    private String objectId;

    public CheckOutClass(Context mContext){
        this.mContext = mContext;
    }

    public CheckOutClass(Context mContext, int price, int barberId, int time, String barberName, int i){
        this.mContext = mContext;
        Defaults.time=time;
        Defaults.price = price;
        Defaults.barberId = barberId;
        Defaults.barberName = barberName;
        Defaults.numServices = i;
    }
    public CheckOutClass(Context mContext, int price, int barberId, int time, String barberName){
        this.mContext = mContext;
        Defaults.time=time;
       Defaults.price = price;
        Defaults.barberId = barberId;
        Defaults.barberName = barberName;
    }

    public void init() {
        if (Defaults.time >= 0) {
            DialogFragment dialogFragment = new DatePickerFragment();
            if(mContext instanceof BarberActivity)
                dialogFragment.show(((BarberActivity)mContext).getFragmentManager(), "DatePicker");
            else if(mContext instanceof DetailsActivity)
                dialogFragment.show(((DetailsActivity)mContext).getFragmentManager(), "DatePicker");
        }
    }

    public void setDate(int year, int monthOfYear, int dayOfMonth) {
        Defaults.dateFormat = String.format("%4d%02d%02d", year, monthOfYear + 1, dayOfMonth);
        Defaults.dateformatintent = String.format("%02d/%02d/%4d", dayOfMonth, monthOfYear + 1, year);
        getSlotData(Defaults.dateFormat);
    }

    public void getSlotData(final String dateFormat) {
        Defaults.availableTimeSlots.clear();
        Defaults.availableTimeSlotsString.clear();
        Defaults.bookedTimeSlots = new JSONArray();
        ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("Barber");
        parseQuery.whereEqualTo("barberId", Defaults.barberId);
        parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    if (object != null) {
                        updateTime = object.getUpdatedAt();
                        objectId = object.getObjectId();
                        Defaults.jsonObject = object.getJSONObject("availableTimeJSON");
                        try {

                            JSONArray jsonArray = Defaults.jsonObject.getJSONArray(dateFormat);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject timeSlotObject = jsonArray.getJSONObject(i);
                                TimeSlotFormat timeSlotFormat = new TimeSlotFormat(timeSlotObject.getInt("mStartTime"), timeSlotObject.getInt("mEndTime"));
                                Defaults.availableTimeSlots.add(timeSlotFormat);
                            }
                        } catch (JSONException e1) {
                            if (Application.DEBUG)
                                Log.e("Checkout:JSON object", e1.getMessage());
                            TimeSlotFormat timeSlotFormat = new TimeSlotFormat(900, 2200);
                            Defaults.availableTimeSlots.add(timeSlotFormat);
                        }
                        //progressBar.setVisibility(View.GONE);
                        //mAvailableSlotText.setText(getCurrentSlots(Defaults.SLOT_TYPE_AVAILABLE));
                        Log.e("getSlotData", String.valueOf(Defaults.availableTimeSlots.size()));
                        refineSlots(Defaults.SLOT_TYPE_AVAILABLE);

                    } else if (Application.DEBUG) Log.e("Checkout", "object is null");
                } else if (Application.DEBUG)
                    Log.e("Checkout:getslotdata", e.getMessage());
            }
        });
    }

    // Method for returning slots status as a string.
    public void refineSlots(int slotType) {
            for (int i = 0; i < Defaults.availableTimeSlots.size(); i++) {
                TimeSlotFormat currentTimeData = Defaults.availableTimeSlots.get(i);

                if (currentTimeData.getStartTimeData() != currentTimeData.getEndTimeData()) {

                    int startTimeHour = currentTimeData.getStartTimeData() / 100;
                    int startTimeMinute = currentTimeData.getStartTimeData() % 100;
                    int endTimeHour = currentTimeData.getEndTimeData() / 100;
                    int endTimeMinute = (currentTimeData.getEndTimeData() % 100);

                    String s = String.format("%02d:%02d - %02d:%02d", startTimeHour, startTimeMinute, endTimeHour, endTimeMinute);
                    Defaults.availableTimeSlotsString.add(s);
                } else {
                    Defaults.availableTimeSlots.remove(i);
                    i--;
                }

            }

        Defaults.availableTimeSlotsChars = new CharSequence[Defaults.availableTimeSlotsString.size()];
       Defaults.availableTimeSlotsChars =Defaults.availableTimeSlotsString.toArray(Defaults.availableTimeSlotsChars);
        DialogFragment newFragment = new AvailableSlotsDialog();
        Log.e("DIALOG", "DIALOG IS SHOWN");
        if(mContext instanceof BarberActivity)
            newFragment.show(((BarberActivity) mContext).getFragmentManager(), "Available slots");
        else if(mContext instanceof DetailsActivity)
            newFragment.show(((DetailsActivity) mContext).getFragmentManager(), "Available slots");
    }

    public void showTimePicker(){
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        if(mContext instanceof BarberActivity)
            timePickerFragment.show(((BarberActivity) mContext).getFragmentManager(), "TimePicker");
        else if(mContext instanceof DetailsActivity)
            timePickerFragment.show(((DetailsActivity) mContext).getFragmentManager(), "TimePicker");
    }

    public void settextTime(int hourOfDay, int minute) {
       String.format("%02d:%02d", hourOfDay, minute);
        Defaults.timeslotstart = String.format("%02d:%02d", hourOfDay, minute);
        updateSlots(hourOfDay, minute);
    }

    // Method for checking whether the desired slot is available or not
    public int checkSlots(int startTimeNumberFormat, int endTimeNumberFormat) {

        // Performing binary search to find a particular slot
        int low = 0;
        int high = Defaults.availableTimeSlots.size() - 1;

        Log.e("checkslot", String.valueOf(startTimeNumberFormat) + " " + endTimeNumberFormat + " " + Defaults.availableTimeSlots.size());

        while (high >= low) {

            int mid = (high + low) / 2;

            TimeSlotFormat currentTimeSlot = Defaults.availableTimeSlots.get(mid);

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

        if ((minutes + Defaults.time) >= 60)
            hours++;

        int endTimeSlotFormat = hours * 100 + ((minutes + Defaults.time) % 60); // Convert preferred slot's end time in TimeSlot Format


        Defaults.bookedTimeSlots.put(startTimeSlotFormat);
        Defaults.bookedTimeSlots.put(endTimeSlotFormat);

        Defaults.timeSlot = null;

        try {
            int startTimeHour = Defaults.bookedTimeSlots.getInt(0)/ 100;
            int startTimeMinute = Defaults.bookedTimeSlots.getInt(0)% 100;
            int endTimeHour = Defaults.bookedTimeSlots.getInt(1)/ 100;
            int endTimeMinute = Defaults.bookedTimeSlots.getInt(1)% 100;

            Defaults.timeSlot = new String(String.format("%02d:%02d - %02d:%02d", startTimeHour, startTimeMinute, endTimeHour, endTimeMinute));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        int updateSlotPosition = checkSlots(startTimeSlotFormat, endTimeSlotFormat); // Returns corresponding slot position or -1 if no slot is available

        Log.e("BOOKED SLOT", String.valueOf(updateSlotPosition));
        if (updateSlotPosition > -1) {

            if (!checkDataSetChanged()) {

                // Create new slots and updating current slots in available slots list
                TimeSlotFormat updateCurrentTimeSlot = Defaults.availableTimeSlots.get(updateSlotPosition);
                TimeSlotFormat newSlot = new TimeSlotFormat(updateCurrentTimeSlot.getStartTimeData(), startTimeSlotFormat);
                TimeSlotFormat newSlotDivision = new TimeSlotFormat(endTimeSlotFormat, updateCurrentTimeSlot.getEndTimeData());

                // Update available slot list by adding new slots
                Defaults.availableTimeSlots.remove(updateSlotPosition);
               Defaults. availableTimeSlots.add(updateSlotPosition, newSlot);
                Defaults.availableTimeSlots.add(updateSlotPosition + 1, newSlotDivision);
                confirmBooking();
            } else {
                Toast.makeText(mContext, "Slots updated", Toast.LENGTH_LONG).show();
                getSlotData(Defaults.dateFormat);
            }

        } else{
            Toast.makeText(mContext, "No slots available at specified time", Toast.LENGTH_LONG).show();
            getSlotData(Defaults.dateFormat);

        }

    }

    private void confirmBooking() {
        DialogFragment dialogFragment = new BookingDetailsDialog();
        if(mContext instanceof BarberActivity)
            dialogFragment.show(((BarberActivity) mContext).getFragmentManager(), "confirm");
        if(mContext instanceof DetailsActivity)
            dialogFragment.show(((DetailsActivity) mContext).getFragmentManager(), "confirm");
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

    public void updateParseSlot(final ArrayList<TimeSlotFormat> availableTimeSlots) {
        ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("Barber");
        parseQuery.whereEqualTo("barberId", Defaults.barberId);
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
                        Defaults.jsonObject.put(Defaults.dateFormat, jsonArray);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    object.put("availableTimeJSON", Defaults.jsonObject);
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                if (Application.DEBUG)
                                    Log.e("SAVED", "HO GYA BHAI");
                                saveAppointment();
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

    private void saveAppointment() {

        final ParseObject parseObject = new ParseObject("Appointments");
        if(mContext instanceof BarberActivity)
            parseObject.put("servicesId", UserFavsAndCarts.listcart);
        else if(mContext instanceof DetailsActivity) {
            List<String> detailsActivityCart = new ArrayList<>();
            detailsActivityCart.add(Defaults.defaultObjectId);
            parseObject.put("servicesId", detailsActivityCart);
        }

        parseObject.put("user", ParseUser.getCurrentUser().getUsername());
        parseObject.put("userId", ParseUser.getCurrentUser().getObjectId());
        parseObject.put("url", ParseUser.getCurrentUser().getString("picUri"));
        parseObject.put("barberId", Defaults.barberId);
        parseObject.put("barberName", Defaults.barberName);
        parseObject.put("timeSlot", Defaults.timeSlot);
        parseObject.put("date", Integer.valueOf(Defaults.dateFormat));
        parseObject.put("completed", false);
        parseObject.put("totalPrice", Defaults.price);
        parseObject.put("bookedSlot", Defaults.bookedTimeSlots);

        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                    Intent i = new Intent(mContext, Confirmation.class);
                    Random r = new Random();
                    int pin = r.nextInt(99999 - 9999) + 9999;
                    i.putExtra("pin", pin);
                    i.putExtra("price", Defaults.price);
                    i.putExtra("barberName", Defaults.barberName);
                    i.putExtra("totalTime", Defaults.time);
                    i.putExtra("appointmentDate", Defaults.dateformatintent);
                    i.putExtra("timeslot", Defaults.timeSlot);


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
                        jSonobj.put("alert", "Booking Confirmation\nDate: " + Defaults.dateformatintent + "\nTimeSlot: " + Defaults.timeslotstart + " + " + Defaults.time + "min" + "\nBarber: " + Defaults.barberName);

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


                    mContext.startActivity(i);
                    if(mContext instanceof BarberActivity) {
                        ((BarberActivity) mContext).finish();
                        ((BarberActivity) mContext).overridePendingTransition(0, 0);
                    }
                    else if(mContext instanceof DetailsActivity){
                        ((DetailsActivity) mContext).finish();
                        ((DetailsActivity) mContext).overridePendingTransition(0, 0);
                    }
                } else Log.d("e", e.getMessage());
            }
        });
    }


}


