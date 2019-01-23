package msa.sleep

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.*
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import msa.domain.entities.EventRecord
import msa.domain.statemachine.AppStateMachine
import msa.domain.statemachine.EventAction
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by Abhi Muktheeswarar.
 */

const val NOTIFICATION_CHANNEL_ID = "notification_channel_id"
const val NOTIFICATION_Service_CHANNEL_ID = "service_channel"

class SpyService : Service(), SensorEventListener {

    private lateinit var screenStateReceiver: BroadcastReceiver

    private lateinit var filter: IntentFilter

    private lateinit var sensorManager: SensorManager

    private lateinit var sensorAccelerometer: Sensor
    private var significantMotionAccelerometer: Sensor? = null
    private val triggerEventListener = object : TriggerEventListener() {
        override fun onTrigger(event: TriggerEvent) {
            Timber.d("Motion detected, onTrigger = $event")
            endTime = System.currentTimeMillis()
            insertEvent()
        }
    }

    private var startTime: Long = System.currentTimeMillis()
    private var endTime: Long = System.currentTimeMillis()

    private val appStateMachine by inject<AppStateMachine>()


    private var mGravity: FloatArray = floatArrayOf()
    private var mAccelLast: Float = 0.0f
    private var mAccelCurrent: Float = 0.0f
    private var delta: Float = 0.0f
    private var mAccel: Float = 0.0f


    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(1, createNotification())

        screenStateReceiver = ScreenStateReceiver()
        filter = IntentFilter(Intent.ACTION_SCREEN_ON).apply { addAction(Intent.ACTION_SCREEN_OFF) }
        registerReceiver(screenStateReceiver, filter)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        significantMotionAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION)
        Timber.d("Sensors A = ${sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER)}")
        Timber.d("Sensors B = ${sensorManager.getSensorList(Sensor.TYPE_SIGNIFICANT_MOTION)}")

    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val isScreenOff = intent.getBooleanExtra("screen_state", false)
        Timber.d("isScreenOff = $isScreenOff")
        if (isScreenOff) {
            startTime = System.currentTimeMillis()
            Timber.d("isScreenOff TRUE = $isScreenOff")

            sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
            significantMotionAccelerometer?.let {
                sensorManager.requestTriggerSensor(
                    triggerEventListener,
                    it
                )
            }

        } else {
            endTime = System.currentTimeMillis()
            insertEvent()

            // YOUR CODE
            Timber.d("isScreenOff FALSE = $isScreenOff")
            sensorManager.unregisterListener(this)
            significantMotionAccelerometer?.let {
                sensorManager.cancelTriggerSensor(
                    triggerEventListener,
                    it
                )
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        unregisterReceiver(screenStateReceiver)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent) {

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {

            //Timber.d( "onSensorChanged of type -> Sensor.TYPE_ACCELEROMETER")

            mGravity = event.values.clone()

            val x = mGravity[0]
            val y = mGravity[1]
            val z = mGravity[2]


            mAccelLast = mAccelCurrent
            mAccelCurrent = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            delta = mAccelCurrent - mAccelLast
            mAccel = mAccel * 0.9f + delta

            //Timber.d("mAccel = $mAccel")

            if (mAccel > 3) {
                //Timber.d( "Motion detected")
            }
        }
    }

    private fun createNotification(): Notification {

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        return if (Build.VERSION.SDK_INT >= 26) {

            val channel = NotificationChannel(
                NOTIFICATION_Service_CHANNEL_ID,
                "Sync Service",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Service Name"
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            Notification.Builder(this, NOTIFICATION_Service_CHANNEL_ID)
                .setContentTitle("Spy")
                .setContentText("Measuring your phone usage pattern")
                .setSmallIcon(R.drawable.ic_schedule)
                .setContentIntent(pendingIntent)
                .build()

        } else {

            NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_schedule)
                .setContentIntent(pendingIntent)
                .setContentTitle("Spy")
                .setContentText("Measuring your phone usage pattern")
                .build()
        }

    }

    private fun insertEvent() {

        val diffInMs = endTime - startTime
        val diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs)
        Timber.d("Duration of sleep = $diffInSec")

        val eventRecord =
            EventRecord(startDate = Date(startTime), endDate = Date(endTime), durationInMilliSeconds = diffInMs)

        appStateMachine.input.accept(EventAction.InsertEventRecordAction(eventRecord))
    }
}