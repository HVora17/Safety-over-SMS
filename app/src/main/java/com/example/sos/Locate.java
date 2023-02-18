package com.example.sos;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class Locate extends Service {

    private LocationManager locManager;
    private LocationListener locListener = new MyLocationListener();

    private boolean network_enabled = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try {
            network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        catch (Exception ex) {
        }

        if (network_enabled)
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);

        stopSelf();
        return START_STICKY;
    }



    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                // This needs to stop getting the location data and save the battery power.
                locManager.removeUpdates(locListener);

                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());
                String url = "https://maps.google.com/?q="+latitude+","+longitude;

                ReceiveSMS.sendLocation(url);
            }
        }


        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    }
}

