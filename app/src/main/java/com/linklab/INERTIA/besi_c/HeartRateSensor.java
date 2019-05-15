package com.linklab.INERTIA.besi_c;

// Imports

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class HeartRateSensor extends Service implements SensorEventListener     // This is the file heading, it listens to the physical Heart Rate Senor
{
    private Preferences Preference = new Preferences();     // Gets an instance from the preferences module.
    private SystemInformation SystemInformation = new SystemInformation();  // Gets an instance from the system information module
    public long Duration = Preference.HRSampleDuration;        // This is the sampling rate in milliseconds gotten from preferences.
    private SensorManager mSensorManager;       // Creates the sensor manager that looks into the sensor
    private String Sensors = Preference.Sensors;     // Gets the sensors from preferences.
    private String Heart_Rate = Preference.Heart_Rate;     // This is the file name set from preferences.
    int Time_zero;      // Time at start of measurement (milliseconds)
    final Timer HRSensorTimer = new Timer();          // Makes a new timer for HRSensorTimer.

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)    /* Establishes the sensor and the ability to collect data at the start of the data collection */
    {
        File sensors = new File(Preference.Directory + SystemInformation.Sensors_Path);     // Gets the path to the Sensors from the system.
        if (sensors.exists())      // If the file exists
        {
            Log.i("Heart Rate Sensor", "No Header Created");     // Logs to console
        }
        else        // If the file does not exist
        {
            Log.i("Heart Rate Sensor", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Sensors, Preference.Sensor_Data_Headers);        /* Logs the Sensors data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }

        File HRSensors = new File(Preference.Directory + SystemInformation.Heart_Rate_Path);     // Gets the path to the Sensors from the system.
        if (HRSensors.exists())      // If the file exists
        {
            Log.i("Heart Rate Sensor", "No Header Created");     // Logs to console
        }
        else        // If the file does not exist
        {
            Log.i("Heart Rate Sensor", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Heart_Rate, Preference.Heart_Rate_Data_Headers);        /* Logs the Sensors data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);       // Starts the sensor service for any sensor in the system.
        Sensor mHeartRate = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);        // Makes sure it is for the Heart Rate sensor.
        mSensorManager.registerListener(this, mHeartRate, SensorManager.SENSOR_DELAY_FASTEST);      // Registers the listener for the HR sensor in the system.
        Time_zero = getTime();      // Gets the current system time.

        HRSensorTimer.schedule( new TimerTask()     // Initializes a timer.
        {
            public void run()       // Runs the imported file based on the timer specified.
            {
                Log.i("Heart Rate Sensor", "Stopping Sensor");     // Logs on Console.

                String data =  ("Heart Rate Sensor," + "Killed Sensor at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger(Sensors, data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.

                stopSelf();    // Stops the Heart Rate Sensor
            }
        }, Duration);       // Waits for this amount of duration.

        return START_NOT_STICKY;        // Makes sure the timer is started again if it is killed.
    }

    @Override
    public void onSensorChanged(SensorEvent event)      // This is where the data collected by the sensor is saved into a csv file which can be accessed.
    {
        String HeartRateMonitor = String.valueOf(event.values[0]);      // This changes the value of the sensor data to a string.
        final String logstring = SystemInformation.getTimeStamp() + "," + String.valueOf(event.timestamp) + "," + HeartRateMonitor + "," + event.accuracy;     // Appends the Heart Rate value onto the string

        new Thread(new Runnable()       // Starts a new runnable file.
        {
            public void run()       // Runs when the runnable is called
            {
                DataLogger dataLogger = new DataLogger(Heart_Rate, logstring);       // Logs the data into a file that can be retrieved.
                dataLogger.LogData();   // Logs the data to the computer.
            }
        }).start();     // Starts the runnable.
    }

    private int getTime()   // Gets the time from the system
    {
        return (int)System.currentTimeMillis();     // Returns the system time.
    }

    @Override
    public void onDestroy()     // A destroy all activity switch (kill switch)
    {
        Log.i("Heart Rate Sensor", "Destroying Sensor Service");     // Logs on Console.

        mSensorManager.unregisterListener(this);        // Kills the listener
        HRSensorTimer.cancel();     // Kills the timer.
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy)     // Changes the accuracy of the sensor data collected.
    {
        // Please do not remove this, the code needs this to function properly. Thank you :-)
    }

    @Override
    public IBinder onBind(Intent intent)    /* Unknown but necessary function */
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
