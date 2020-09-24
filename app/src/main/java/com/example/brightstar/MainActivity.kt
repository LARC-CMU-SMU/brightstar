package com.example.brightstar

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
//    private lateinit var sensorManager: SensorManager
//    private var light: Sensor? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get an instance of the sensor service, and use that to get an instance of
        // a particular sensor.
//        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        Intent(this, RecordService::class.java).also { intent ->
            startService(intent)
        }
    }





}