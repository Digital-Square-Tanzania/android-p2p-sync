package org.smartregister.p2p.util;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 26/03/2019
 */

public class Device {

    /**
     * This method returns the device's WLAN MAC Address. We expect any device using the P2P sync module to
     * either have a bluetooth or Wifi module. We are going to assume that they atleast have  Wifi module
     *
     * @return
     */
    @Nullable @WorkerThread
    public static final String generateUniqueDeviceId(Context context) {
        String uniqueId = null;
        if(context != null) {
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                boolean wasWifiEnabled = wifiManager.isWifiEnabled();

                //TODO: Test if hotspot on and wifi enabled are the same thing
                if (!wifiManager.isWifiEnabled()) {
                    // ENABLE THE WIFI FIRST
                    wifiManager.setWifiEnabled(true);
                }

                try {
                    // Let's give the device some-time to start the wifi
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Timber.e(e);
                }

                uniqueId = getMacAddress();

                if (!wasWifiEnabled) {
                    wifiManager.setWifiEnabled(false);
                }
        }

        return uniqueId;
    }

    /**
     * This method returns WLAN0's MAC Address
     *
     * @return  WLAN0 MAC address or NULL if unable to get the mac address
     */
    public static String getMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return null;
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (SocketException ex) {
            Timber.e(ex);
        }
        return null;
    }

}
