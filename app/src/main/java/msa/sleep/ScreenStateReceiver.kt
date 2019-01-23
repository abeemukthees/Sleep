package msa.sleep

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Created by Abhi Muktheeswarar.
 */

class ScreenStateReceiver : BroadcastReceiver() {

    private val TAG = "ScreenStateReceiver"

    var isScreenOff = false

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_SCREEN_OFF) {
            // DO WHATEVER YOU NEED TO DO HERE
            isScreenOff = true
        } else if (intent.action == Intent.ACTION_SCREEN_ON) {
            // AND DO WHATEVER YOU NEED TO DO HERE
            isScreenOff = false
        }

        Log.d(TAG, "isScreenOff = $isScreenOff")
        val intent = Intent(context, SpyService::class.java)
        intent.putExtra("screen_state", isScreenOff)
        context.startService(intent)

    }
}