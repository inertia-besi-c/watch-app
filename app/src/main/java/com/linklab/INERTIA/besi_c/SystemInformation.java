package com.linklab.INERTIA.besi_c;

// Imports

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("ALL")
class SystemInformation     // Class that acquires the current time from the system and saves it.
{
    private Preferences Preference = new Preferences();     // Gets an instance from the preferences module.

    private String DeviceID = Preference.DeviceID;       // Gets the Device ID from preferences
    private String Accelerometer = Preference.Accelerometer;       // Gets the Accelerometer file from preferences
    private String Battery = Preference.Battery;       // Gets the Battery level file from preferences
    private String Estimote = Preference.Estimote;       // Gets the Estimote file from preferences
    private String Pedometer = Preference.Pedometer;            // Gets the Pedometer file from preferences
    private String Pain_Activity = Preference.Pain_Activity;           // Gets the Pain Activity file from preferences
    private String Pain_Results = Preference.Pain_Results;              // Gets the Pain Results file from preferences
    private String Followup_Activity = Preference.Followup_Activity;           // Gets the Followup Activity file from preferences
    private String Followup_Results = Preference.Followup_Results;           // Gets the Followup Results file from preferences
    private String EndOfDay_Activity = Preference.EndOfDay_Activity;           // Gets the End of Day Activity file from preferences
    private String EndOfDay_Results = Preference.EndOfDay_Results;           // Gets the End of Day Results file from preferences
    private String Sensors = Preference.Sensors;           // Gets the Sensors file from preferences
    private String Steps = Preference.Steps;           // Gets the Steps file from preferences
    private String System = Preference.System;           // Gets the System file from preferences
    private String Heart_Rate = Preference.Heart_Rate;       // Gets the Heart Rate files from preferences

    /* File path for Adding Headers to Individual File Name */
    public String Accelerometer_Path = DeviceID + "_" + Accelerometer;     // This is the Accelerometer File path
    public String Battery_Path = DeviceID + "_" + Battery;        // This is the Battery Information Folder path
    public String Estimote_Path = DeviceID + "_" + Estimote;      // This is the Estimote File path
    public String Pedometer_Path = DeviceID + "_" + Pedometer;        // This is the Pedometer File path
    public String Pain_EMA_Activity_Path = DeviceID + "_" + Pain_Activity;     // This is the Pain EMA Activity File path
    public String Pain_EMA_Results_Path = DeviceID + "_" + Pain_Results;       // This is the Pain EMA Response File path
    public String Followup_EMA_Activity_Path = DeviceID + "_" + Followup_Activity;     // This is the Followup EMA Activity File path
    public String Followup_EMA_Results_Path = DeviceID + "_" + Followup_Results;     // This is the Followup EMA Response File path
    public String EndOfDay_Activity_Path = DeviceID + "_" + EndOfDay_Activity;     // This is the End OF Day EMA Activity File Path
    public String EndOfDay_Results_Path = DeviceID + "_" + EndOfDay_Results;       // This is the End of Day Response File path
    public String Sensors_Path = DeviceID + "_" + Sensors;    // This is the Sensor Activity File path
    public String Steps_Path = DeviceID + "_" + Steps;     // This is the Step Activity File path
    public String System_Path = DeviceID + "_" + System;      // This is the System Activity File path
    public String Heart_Rate_Path = DeviceID + "_" + Heart_Rate;        // This is the Heart Rate path

    String getTime()        // This gets only the current time from the system
    {
        DateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.US);      // The time format is called in US format.
        Date current = new Date();      // The current date and timer is set.
        return timeFormat.format(current);       // The current time is set to show on the time text view.
    }

    String getTimeMilitary()
    {
        DateFormat timeFormat = new SimpleDateFormat("hh:mm:ss", Locale.US);      // The time format is called in US format.
        Date current = new Date();      // The current date and timer is set.
        return timeFormat.format(current);       // The current time is set to show on the time text view.
    }

    String getDate()        // This gets only the current date from the system
    {
        Date current = new Date();      // The current date and timer is set.
        DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.US);     // The date is called in US format.
        return dateFormat.format(current);       // The current date is set to show on the date text view.
    }

    String getTimeStamp()        // Puts the system time acquired into the desired format wanted.
    {
        DateFormat datetimeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS", Locale.US);      // Specified format of the time, in US style.
        Date current = new Date();      // Calls the current date from the system.
        return datetimeFormat.format(current);  // Returns the date and time the system is in.
    }

    String getFolderTimeStamp()        // Puts the system time acquired into the desired format wanted.
    {
        DateFormat datetimeFormat = new SimpleDateFormat("yyMMdd_HHmm", Locale.US);         // Sets the date and time for the file.
        Date current = new Date();      // Calls the current date from the system.
        return datetimeFormat.format(current);  // Returns the date and time the system is in.
    }

    String getBatteryLevel(Context context)     // This returns a string that displays the battery level
    {
        IntentFilter battery = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);     // Starts an intent that calls the battery level service.
        Intent batteryStatus = context.registerReceiver(null, battery);     // This gets the battery status from that service.
        assert batteryStatus != null;       // Asserts that the battery level is not null.
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);      // Initializes an integer value for the battery level
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);      // Scales the battery level to 100 from whatever default value it is.
        int batteryPct = (level*100/scale);     // Sets the battery level as a percentage.
        return String.valueOf(batteryPct);      // This is the battery level string
    }

    boolean isSystemCharging(Context context)       // Returns a boolean that checks if the system is charging or not.
    {
        IntentFilter battery = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);     // Starts an intent that calls the battery level service.
        Intent batteryStatus = context.registerReceiver(null, battery);     // This gets the battery status from that service.
        assert batteryStatus != null;       // Asserts that the battery level is not null.
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);        //  Gets extra data from the battery level service.
        AtomicBoolean isCharging = new AtomicBoolean(status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_PLUGGED_AC);      // If the system is charging.
        return isCharging.get();        // Return true, or false.
    }

    boolean isTimeBetweenTwoTimes (String currentTime, int startHour, int endHour, int startMinute, int endMinute, int startSecond, int endSecond)
    {
        Pattern p = Pattern.compile("([01]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])");
        Matcher m = p.matcher(currentTime);
        if (m.matches())
        {
            String hourString = m.group(1);
            String minuteString = m.group(2);
            String secondString = m.group(3);
            int hour = Integer.parseInt(hourString);
            int minute = Integer.parseInt(minuteString);
            int second = Integer.parseInt(secondString);

            Log.i("Main Activity", "Hour" + String.valueOf(hour));
            Log.i("Main Activity", "Minutes"+ String.valueOf(minute));
            Log.i("Main Activity", String.valueOf(second));

            if ((hour >= startHour && hour <= endHour) && (minute >= startMinute && minute <= endMinute) && (second >= startSecond && second <= endMinute))
            {
                return true;
            }
        }
        return false;
//        }
//        SimpleDateFormat startTime = new SimpleDateFormat('HH:mm');
//        Calendar startTimeCal = Calendar.getInstance();
//        startTimeCal.setTime(startTime);
//
//        int startTimeHour = startTimeCal.get(Calendar.HOUR_OF_DAY);
//        int startTimeMinutes = startTimeCal.get(Calendar.MINUTE);
//        if (startTimeHour == 0)
//        {
//            startTimeHour = 24;
//        }
//
//        Calendar curTimeCal = Calendar.getInstance();
//        curTimeCal.setTime(currentTime);
//
//        int curTimeHour = curTimeCal.get(Calendar.HOUR_OF_DAY);
//        int curTimeMinutes = curTimeCal.get(Calendar.MINUTE);
//
//        Calendar endTimeCal = Calendar.getInstance();
//        endTimeCal.setTime(endTime);
//
//        int endTimeHour = endTimeCal.get(Calendar.HOUR_OF_DAY);
//        int endTimeMinutes = endTimeCal.get(Calendar.MINUTE);
//        if (endTimeHour == 0)
//        {
//            endTimeHour = 24;
//        }
//
//        if (((curTimeHour > startTimeHour) || (curTimeHour == startTimeHour && curTimeMinutes >= startTimeMinutes)) && ((curTimeHour < endTimeHour) || (curTimeHour == endTimeHour && curTimeMinutes <= endTimeHour)))
//        {
//            return true;
//        }
//        else
//        {
//            return false;
//        }
    }
}
