package com.linklab.INERTIA.besi_c;

// Imports.

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class EODTimerService extends Application        // Starts the EOD EMA Timer Service when called.
{
    private Preferences Preference = new Preferences();     // Gets an instance from the preferences module.
    private SystemInformation SystemInformation = new SystemInformation();  // Gets an instance from the system information module
    private String Sensors = Preference.Sensors;     // Gets the sensors from preferences.

    @Override
    public void onCreate()      // Creates the instance when it is started.
    {
        File sensors = new File(Preference.Directory + SystemInformation.Sensors_Path);     // Gets the path to the Sensors from the system.
        if (sensors.exists())      // If the file exists
        {
            Log.i("End of Day EMA Prompts", "No Header Created");     // Logs to console
        }
        else        // If the file does not exist
        {
            Log.i("End of Day EMA prompts", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Sensors, Preference.Sensor_Data_Headers);        /* Logs the Sensors data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }

        Log.i("End of Day EMA", "End of Day EMA Timer is starting");     // Logs on Console.

        super.onCreate();       // Starts the creation.
        ScheduleEndOfDayEMA(this);      // Links the schedule EOD EMA to this.
    }

    private void ScheduleEndOfDayEMA(Context context)       // When the timer is called, the schedule is activated.
    {
        final Context thisContext = context;        // Gets a context for the file name.
        Calendar calendar = Calendar.getInstance();     // Gets the calendar.
        calendar.set(Calendar.HOUR_OF_DAY, Preference.EoDEMA_Time_Hour);     // Gets the hour of the day from the preference.
        calendar.set(Calendar.MINUTE, Preference.EoDEMA_Time_Minute);     // Gets the minute of the day from the preference.
        calendar.set(Calendar.SECOND, Preference.EoDEMA_Time_Second);     // Gets the seconds of the day from the preference.

        try     // Try the scheduled task.
        {
            long delay = calendar.getTimeInMillis() - System.currentTimeMillis();       // Starts a long delay variable.

            Timer EODTimerService = new Timer();      // When called the timer is started.
            EODTimerService.schedule(new TimerTask()        // Starts the EODTimerService
            {
                @Override
                public void run()       // Runs when it is called.
                {
                    Log.i("End of Day EMA", "End of Day EMA Timer is starting First EMA Prompt");     // Logs on Console.

                    String data =  ("End of Day Timer Service," + "Started Prompt 1 at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                    DataLogger datalog = new DataLogger(Sensors, data);      // Logs it into a file called System Activity.
                    datalog.LogData();      // Saves the data into the directory.

                    Intent StartEMAActivity = new Intent(thisContext, EndOfDayPrompt1.class);     // Starts the first EOD EMA prompt.
                    startActivity(StartEMAActivity);      // Starts the StartEMAActivity.
                }
            }, delay, Preference.EoDEMA_Period);     // Gets the preferences setting from the preference system.
        }
        catch(Exception ex)     // If it fails manually start the service.
        {
            long delay = calendar.getTimeInMillis() - System.currentTimeMillis() + 24*60*60*1000;       // If it failed, start it manually.

            Timer EODTimerService = new Timer();      // When called the timer is started.
            EODTimerService.schedule(new TimerTask()        // Starts the EODTimerService
            {
                @Override
                public void run()       // Runs when it is called.
                {
                    Log.i("End of Day EMA", "End of Day EMA Timer is starting First EMA Prompt");     // Logs on Console.

                    String data =  ("End of Day Timer Service," + "Started Prompt 1 at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                    DataLogger datalog = new DataLogger(Sensors, data);      // Logs it into a file called System Activity.
                    datalog.LogData();      // Saves the data into the directory.

                    Intent intent = new Intent(thisContext, EndOfDayPrompt1.class);     // Starts the first EOD EMA prompt.
                    startActivity(intent);      // Starts the intent.
                }
            }, delay, Preference.EoDEMA_Period);     // Gets the preferences setting from the preference system.
        }
    }
}

