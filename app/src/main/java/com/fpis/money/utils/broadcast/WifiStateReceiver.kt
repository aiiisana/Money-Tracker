package com.fpis.money.utils.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import com.fpis.money.utils.ToastType
import com.fpis.money.utils.showCustomToast

class WifiStateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == WifiManager.WIFI_STATE_CHANGED_ACTION) {
            val wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)
            when (wifiState) {
                WifiManager.WIFI_STATE_ENABLED -> {
                    showCustomToast(context, "WI-FI turned on", ToastType.INFO)
                }
                WifiManager.WIFI_STATE_DISABLED -> {
                    showCustomToast(context, "WI-FI turned off", ToastType.INFO)
                }
            }
        }
    }
}