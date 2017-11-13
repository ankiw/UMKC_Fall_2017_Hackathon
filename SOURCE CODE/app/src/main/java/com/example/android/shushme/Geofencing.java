package com.example.android.shushme;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by darip on 10-11-2017.
 */

public class Geofencing implements ResultCallback{

    private static final String TAG = Geofencing.class.getSimpleName();
    private static final long GEOFENCE_TIMEOUT = 24*60*60*1000;
    private static final float GEOFENCE_RADIUS = 50;

    private GoogleApiClient mClient;
    private Context mContext;
    private List<Geofence> mGeoFenceList;
    private PendingIntent mGeoIntent;

    public Geofencing(Context context, GoogleApiClient client){
        mContext = context;
        mClient = client;
        mGeoIntent =null;
        mGeoFenceList = new ArrayList<>();
    }

    public void updateGeofenceList(PlaceBuffer places){
        mGeoFenceList = new ArrayList<>();
        if(places == null || places.getCount() == 0){
            return;
        }
        for(Place place : places){
            String placeUid = place.getId();
            double lat = place.getLatLng().latitude;
            double lng = place.getLatLng().longitude;
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(placeUid)
                    .setExpirationDuration(GEOFENCE_TIMEOUT)
                    .setCircularRegion(lat, lng, GEOFENCE_RADIUS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
            mGeoFenceList.add(geofence);
        }
    }

    private GeofencingRequest geofencingRequest(){
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeoFenceList);
        return  builder.build();
    }

    private PendingIntent getGeofencePendingIntent(){
        if(mGeoIntent != null)
            return mGeoIntent;
        Intent intent = new Intent(mContext, GeofenceBroadcastReceiver.class);
        mGeoIntent = PendingIntent.getBroadcast(mContext, 0 , intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeoIntent;
    }

    public void registerAllGeofences(){
        if(mClient == null || !mClient.isConnected() || mGeoFenceList == null || mGeoFenceList.size() == 0){
            return;
        }
        try {
            LocationServices.GeofencingApi.addGeofences(mClient, geofencingRequest(), getGeofencePendingIntent()).setResultCallback(this);
        }catch(SecurityException s){
            Log.e(TAG, s.getMessage());
        }
    }

    public void unregisterGeofences(){
        if(mClient == null || !mClient.isConnected()){
            return;
        }
        try {
            LocationServices.GeofencingApi.removeGeofences(mClient, getGeofencePendingIntent()).setResultCallback(this);
        }catch(SecurityException s){
            Log.e(TAG, s.getMessage());
        }
    }

    @Override
    public void onResult(@NonNull Result result) {
        Log.e(TAG, String.format("Error adding/removing geofences: %s",result.getStatus().toString()));
    }
}
