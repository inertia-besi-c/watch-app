package com.linklab.INERTIA.besi_c;

// Imports

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class ESTimerService extends Service         /* This runs the delay timer, and also calls the heart rate sensor itself, the heart rate sensor kills itself and returns here when complete */
{
    public int delay = 0;       // Starts a delay of 0
    public long period = new Preferences().ESMeasurementInterval;      // This is the duty cycle rate in format (minutes, seconds, milliseconds)
    private String Sensors = new Preferences().Sensors;     // Gets the sensors from preferences.
    private Timer ESTimerService;         // Starts the variable timer.
    private PowerManager.WakeLock wakeLock;     // Starts the wakelock service from the system.
    @SuppressLint("WakelockTimeout")        // Suppresses the wakelock.

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)    /* Establishes the sensor and the ability to collect data at the start of the data collection */
    {
        File sensors = new File(new Preferences().Directory + new SystemInformation().Sensors_Path);     // Gets the path to the Sensors from the system.
        if (sensors.exists())      // If the file exists
        {
            Log.i("End of Day EMA Prompts", "No Header Created");     // Logs to console
        }
        else        // If the file does not exist
        {
            Log.i("End of Day EMA prompts", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Sensors, new Preferences().Sensor_Data_Headers);        /* Logs the Sensors data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }

        Log.i("Estimote", "Starting Estimote Timer Service");     // Logs on Console.

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);     // Starts the power manager service from the system
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ESService: wakeLock");         // Starts a partial wakelock for the heartrate sensor.
        wakeLock.acquire();     // Starts the wakelock without any timeout.
        PeriodicService();     // Makes the periodic service false initially.

        return START_STICKY;    // This allows it to restart if the service is killed
    }

    private void PeriodicService()      // Starts the periodic data sampling.
    {
        final Intent ESService = new Intent(getBaseContext(), EstimoteService.class);       // Starts a ES service intent from the sensor class.
        ESTimerService = new Timer();          // Makes a new timer.
        ESTimerService.schedule( new TimerTask()     // Initializes a timer.
        {
            public void run()       // Runs the imported file based on the timer specified.
            {
                Log.i("Estimote", "Starting Estimote Service");     // Logs on Console.

                String data =  ("Estimote Timer," + "Started Estimote Service at," + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger(Sensors, data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.

                startService(ESService);    // Starts the Estimote service
            }
        }, delay, period);      // Waits for this amount of delay and runs every stated period.
    }

    @Override
    public void onDestroy()     // When the service is destroyed.
    {
        Log.i("Estimote", "Destroying Estimote Timer Service");     // Logs on Console.

        String data =  ("Estimote Timer," + "Stopped Estimote Timer at," + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
        DataLogger datalog = new DataLogger(Sensors, data);      // Logs it into a file called System Activity.
        datalog.LogData();      // Saves the data into the directory.

        ESTimerService.cancel();        //  Cancels the ES Timer Service.
        wakeLock.release();     // Releases the wakelock
    }

    @Override
    /* Unknown but necessary function */
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
