package barber.startup.com.startup_barber;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.Calendar;

/**
 * Created by ayush on 2/3/16.
 */
public class CustomDatePickerDialog extends DatePickerDialog {
    private DateFormat dateFormat;
    private int currentDate = 12;
    private int currentYear = 2016;
    private int currentMonth = 2;
    private int minMonth;
    private int maxMonth;

    public CustomDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
        currentDate = dayOfMonth;
        currentMonth = monthOfYear;
        currentYear = year;
        dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);

        try {
            Class<?> superclass = getClass().getSuperclass();
            Field mTimePickerField = superclass.getDeclaredField("DatePicker");
            mTimePickerField.setAccessible(true);
            DatePicker mDatePicker = (DatePicker) mTimePickerField.get(this);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }
    }

    public CustomDatePickerDialog(Context context, int theme, OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth) {
        super(context, theme, listener, year, monthOfYear, dayOfMonth);
    }


    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        Log.e("CUSTOM", "THIS IS PASSED");
        if (month > getMaxMonth() && month < getMinMonth()) {
            //Toast.makeText(getOwnerActivity(), "Not available", Toast.LENGTH_SHORT).show();
        }

    }

    public int getMinMonth() {
        final Calendar c = Calendar.getInstance();
        minMonth = c.get(Calendar.MONTH);
        return minMonth;
    }

    public int getMaxMonth() {
        final Calendar c = Calendar.getInstance();
        minMonth = c.get(Calendar.MONTH);
        maxMonth = minMonth + 1;
        return maxMonth;

    }
}
