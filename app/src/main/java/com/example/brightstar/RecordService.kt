package com.example.brightstar

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class RecordService : Service(), SensorEventListener {

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null
    private val ONGOING_NOTIFICATION_ID = 1
    private val CHANNEL_DEFAULT_IMPORTANCE = "one"
    private var currentIlluminance = -1.0
    private lateinit var sensorManager: SensorManager
    private var light: Sensor? = null

    // Handler that receives messages from the thread
    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                Thread.sleep(5000)
            } catch (e: InterruptedException) {
                // Restore interrupt status.
                Thread.currentThread().interrupt()
            }

            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1)
        }
    }

    override fun onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
//        HandlerThread("ServiceStartArguments", 10).apply {
////            start()
//            startForegroundActivity()
//            // Get the HandlerThread's Looper and use it for our Handler
////            startForegroundActivity()
//            doDeviousStuff()
//            serviceLooper = looper
//            serviceHandler = ServiceHandler(looper)
//        }
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL)

        startForegroundActivity()
        doDeviousStuff()

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        serviceHandler?.obtainMessage()?.also { msg ->
            msg.arg1 = startId
            serviceHandler?.sendMessage(msg)
        }

        // If we get killed, after returning from here, restart
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show()
    }

    fun startForegroundActivity(){
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }

        val notification: Notification =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Notification.Builder(this, CHANNEL_DEFAULT_IMPORTANCE)
                    .setContentTitle(getText(R.string.notification_title))
                    .setContentText(getText(R.string.notification_message))
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .setTicker(getText(R.string.ticker_text))
                    .build()
            } else {
                Notification.Builder(this)
                    .setContentTitle(getText(R.string.notification_title))
                    .setContentText(getText(R.string.notification_message))
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .setTicker(getText(R.string.ticker_text))
                    .build()
            }


// Notification ID cannot be 0.
        startForeground(ONGOING_NOTIFICATION_ID, notification)

    }

//    val contentResolver = applicationContext.contentResolver

    private fun alterDocument(text: String) {
        val fileName = "test.txt"
        val state = Environment.getExternalStorageState()
        if (!Environment.MEDIA_MOUNTED.equals(state)) {

            //If it isn't mounted - we can't write into it.
            return
        }

        //Create a new file that points to the root directory, with the given name:

        //Create a new file that points to the root directory, with the given name:
        val file = File(getExternalFilesDir(null), fileName)
        val uri = Uri.fromFile(file)
        try {
            contentResolver.openFileDescriptor(uri, "wa")?.use {
                FileOutputStream(it.fileDescriptor).use {
                    it.write(
                        ("%s,${System.currentTimeMillis()/1000}\n".format(text))
                            .toByteArray()
                    )
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    fun doDeviousStuff(){

        Thread(Runnable {
            // a potentially time consuming task; hah you don't say
            while (true) {
                var brightness = getBrightensSetting()
                Log.d("LOG_TAG", "screen brightness %d".format(getBrightensSetting()))
                val illuminance = currentIlluminance
                Log.d("LOG_TAG", "illuminance %f".format(currentIlluminance))
                val text = "%d,%f".format(brightness, illuminance)
                alterDocument(text)
                Thread.sleep(5000)
            }
        }).start()


    }

    fun getBrightensSetting(): Int {
        var curBrightnessValue = -1
        try {
            curBrightnessValue = Settings.System.getInt(
                contentResolver, Settings.System.SCREEN_BRIGHTNESS
            )
        } catch (e: Settings.SettingNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return curBrightnessValue
    }

    override fun onSensorChanged(event: SensorEvent) {
        currentIlluminance = event.values[0].toDouble()
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
//        TODO("Not yet implemented")
    }



}
