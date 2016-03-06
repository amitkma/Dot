package barber.startup.com.startup_barber;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ayush on 2/3/16.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private final static long DEFAULT_MILLS = 5184000000l;
    private int minMonth;
    private int maxMonth;
    private DatePickerDialog datePickerDialog;
    private int mDay;
    private int mMonth;
    private int mYear;
    private DatePicker datePicker;
    private boolean flag = false;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(getActivity(), this, mYear, mMonth, mDay) {

            public void onDateChanged(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                Date dateMin = getMinDate();
                Calendar calMin = Calendar.getInstance();
                calMin.setTime(dateMin);

                Date dateMax = getMaxDate();
                Calendar calMax = Calendar.getInstance();
                calMax.setTime(dateMax);

                Log.e("THIS", Integer.toString(calMin.get(Calendar.YEAR)) + "  " + Integer.toString(calMax.get(Calendar.YEAR)) + " " + Integer.toString(mYear));
                Log.e("THIS", Integer.toString(mDay) + "  " + Integer.toString(mMonth) + " " + Integer.toString(mYear));

                if (year < calMin.get(Calendar.YEAR) || year > calMax.get(Calendar.YEAR)) {
                    Toast.makeText(getActivity(), "No date available", Toast.LENGTH_SHORT).show();
                    Log.e("ERROR", Integer.toString(mYear));
                    datePickerDialog.updateDate(mYear, mMonth, mDay);
                } else if (monthOfYear < calMin.get(Calendar.MONTH) || monthOfYear > calMax.get(Calendar.MONTH)) {
                    Toast.makeText(getActivity(), "No date available", Toast.LENGTH_SHORT).show();
                    datePickerDialog.updateDate(mYear, mMonth, mDay);
                } else if (monthOfYear == calMax.get(Calendar.MONTH) && dayOfMonth > mDay) {
                    Toast.makeText(getActivity(), "No date available", Toast.LENGTH_SHORT).show();
                    datePickerDialog.updateDate(mYear, mMonth, mDay);
                } else if (monthOfYear == mMonth && dayOfMonth < mDay) {
                    Toast.makeText(getActivity(), "No date available", Toast.LENGTH_SHORT).show();
                    datePickerDialog.updateDate(mYear, mMonth, mDay);
                } else if (flag = false) {
                    datePickerDialog.updateDate(year, monthOfYear, dayOfMonth);
                    flag = true;
                    int month = monthOfYear + 1;
                    //sendDate(year, month, dayOfMonth);


                }

            }
        };

        return datePickerDialog;


    }


    public Date getMinDate() {
        Date dateMin = new Date();
        dateMin.setTime(System.currentTimeMillis());
        return dateMin;
    }

    public Date getMaxDate() {
        Date dateMax = new Date();
        dateMax.setTime(System.currentTimeMillis() + DEFAULT_MILLS);
        return dateMax;

    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        ((Checkout) getActivity()).setDate(year, monthOfYear, dayOfMonth);
    }


}