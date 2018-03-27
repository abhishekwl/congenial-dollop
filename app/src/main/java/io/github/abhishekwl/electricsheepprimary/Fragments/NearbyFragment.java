package io.github.abhishekwl.electricsheepprimary.Fragments;


import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.github.abhishekwl.electricsheepprimary.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NearbyFragment extends Fragment {


    private View rootView;
    private Unbinder unbinder;
    private MessageListener messageListener;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;
    private Location deviceLocation;

    @BindView(R.id.nearbyDistanceTextView) TextView distanceTextView;

    public NearbyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_nearby, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        processViews();
        return rootView;
    }

    private void processViews() {
        initializeMessageListener();
        intializeLocation();
    }

    @SuppressLint({"MissingPermission", "RestrictedApi"})
    private void intializeLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                deviceLocation = location;
                Log.v("LOCATION", "DEVICE LOCATION INITIALIZED");
            }
        });
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult!=null) {
                    for (Location location: locationResult.getLocations()) {
                        String lat = Double.toString(location.getLatitude());
                        String lon = Double.toString(location.getLongitude());
                        byte[] bytes = (lat+","+lon).getBytes();
                        Nearby.getMessagesClient(rootView.getContext()).publish(new Message(bytes));
                    }
                }
            }
        };
        locationRequest = new LocationRequest();
        locationRequest.setInterval(0);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        startLocationUpdates();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.v("CONNECTION", "SUSPENDED");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.v("CONNECTION", "FAILED");
                    }
                })
                .build();
    }

    private void initializeMessageListener() {
        messageListener = new MessageListener(){
            @Override
            public void onFound(Message message) {
                super.onFound(message);
                Log.v("onFound", new String(message.getContent()));

                String locationString = new String(message.getContent());
                String[] latLonArr = locationString.split(",");
                double foreginLat = Double.parseDouble(latLonArr[0]);
                double foreignLon = Double.parseDouble(latLonArr[1]);

                if (deviceLocation!=null) {
                    double devLat = deviceLocation.getLatitude();
                    double devLon = deviceLocation.getLongitude();

                    double distanceBetweenCoords = distance(foreginLat, devLat, foreignLon, devLon);
                    DecimalFormat df = new DecimalFormat("#.####");
                    distanceTextView.setText(""+df.format(distanceBetweenCoords/1000));
                    Log.v("DSB ", Double.toString(distanceBetweenCoords));
                }

            }

            @Override
            public void onLost(Message message) {
                super.onLost(message);
                Log.v("onLost", new String(message.getContent()));
            }
        };
    }

    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, new com.google.android.gms.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String lat = Double.toString(location.getLatitude());
                String lon = Double.toString(location.getLongitude());
                byte[] bytes = (lat+","+lon).getBytes();
                Nearby.getMessagesClient(rootView.getContext()).publish(new Message(bytes));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        Nearby.getMessagesClient(rootView.getContext()).subscribe(messageListener);
        unbinder = ButterKnife.bind(this, rootView);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(
            mGoogleApiClient, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    String lat = Double.toString(location.getLatitude());
                    String lon = Double.toString(location.getLongitude());
                    String ele = Double.toString(location.getAltitude());
                    byte[] bytes = (lat+","+lon+","+ele).getBytes();
                    Nearby.getMessagesClient(rootView.getContext()).publish(new Message(bytes));
                }
            });
    }

    public double distance(double lat1, double lat2, double lon1, double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }


    @Override
    public void onResume() {
        super.onResume();
        unbinder = ButterKnife.bind(this, rootView);
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        Nearby.getMessagesClient(rootView.getContext()).unsubscribe(messageListener);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
