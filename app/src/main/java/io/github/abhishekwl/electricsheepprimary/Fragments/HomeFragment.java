package io.github.abhishekwl.electricsheepprimary.Fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.gaurav.gesto.OnGestureListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.github.abhishekwl.electricsheepprimary.Adapters.DashboardRecyclerViewAdapter;
import io.github.abhishekwl.electricsheepprimary.Models.DashboardNotification;
import io.github.abhishekwl.electricsheepprimary.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    @BindView(R.id.homeDashboardRecyclerView) RecyclerView dashboardRecylcerView;
    @BindView(R.id.homeAppLogoImageView) ImageView appLogoImageView;
    @BindView(R.id.homeWelcomeTextView) TextView welcomeTextView;
    @BindView(R.id.homeViewTravelPlanButton) Button travelPlanButton;
    @BindView(R.id.homeDashboardTextView) TextView dashboardTextView;
    @BindColor(R.color.colorAccent) int colorAccent;

    private PlaceDetectionClient placeDetectionClient;
    private View rootView;
    private Unbinder unbinder;
    private Typeface futuraTypeface;
    private BottomNavigationView bottomNavigationView;
    private Location deviceLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseAuth firebaseAuth;
    private RequestQueue requestQueue;
    private JSONObject userObject;
    private DashboardRecyclerViewAdapter dashboardRecyclerViewAdapter;
    private ArrayList<DashboardNotification> dashboardNotifications = new ArrayList<>();
    private MaterialDialog materialDialog;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        processViews(rootView);
        return rootView;
    }

    private void processViews(View rootView) {
        setupHeader(rootView);
        setupListeners();
        new SetupRecyclerView().execute();
    }

    private class SetupRecyclerView extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dashboardRecylcerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
            dashboardRecylcerView.setHasFixedSize(true);
            dashboardRecylcerView.setItemAnimator(new DefaultItemAnimator());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            dashboardNotifications.clear();
            for (int i=0; i<10; i++) {
                dashboardNotifications.add(new DashboardNotification(new Date(), "Notification "+i));
            }
            dashboardRecyclerViewAdapter = new DashboardRecyclerViewAdapter(dashboardNotifications);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dashboardRecylcerView.setAdapter(dashboardRecyclerViewAdapter);
            dashboardRecylcerView.scrollToPosition(0);
        }
    }

    @SuppressLint("MissingPermission")
    private void setupListeners() {
        firebaseAuth = FirebaseAuth.getInstance();
        requestQueue = Volley.newRequestQueue(rootView.getContext());
        performHttpRequest(rootView);

        travelPlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainFrameLayout, new TravelFragment()).commit();
                bottomNavigationView.setSelectedItemId(R.id.menuItemTravelPlan);
            }
        });
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                deviceLocation = location;
                Log.v("LAT: "+deviceLocation.getLatitude(), "\tLONG: "+deviceLocation.getLongitude());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("ERR", e.getMessage());
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupHeader(View rootView) {
        Glide.with(rootView.getContext()).load(R.drawable.energy_white).into(appLogoImageView);
        futuraTypeface = Typeface.createFromAsset(rootView.getContext().getAssets(), "fonts/futura.ttf");

        welcomeTextView.setTypeface(futuraTypeface);
        travelPlanButton.setTypeface(futuraTypeface);
        dashboardTextView.setTypeface(futuraTypeface);

        bottomNavigationView = getActivity().findViewById(R.id.mainBottomNavigationView);

        placeDetectionClient = Places.getPlaceDetectionClient(getActivity());
        @SuppressLint("MissingPermission") Task<PlaceLikelihoodBufferResponse> placeResult = placeDetectionClient.getCurrentPlace(null);
        placeResult.addOnSuccessListener(new OnSuccessListener<PlaceLikelihoodBufferResponse>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onSuccess(PlaceLikelihoodBufferResponse placeLikelihoods) {
                if (placeLikelihoods.getCount()>0) {
                    String placeName = placeLikelihoods.get(0).getPlace().getName().toString();
                    welcomeTextView.setText("Welcome to "+placeName+".");
                }
                placeLikelihoods.release();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                welcomeTextView.setText("Welcome.");
                Log.v("HOME_FRAG", e.getMessage());
            }
        });

        appLogoImageView.setOnTouchListener(new OnGestureListener(getActivity()){
            @Override
            public void onSwipeTop() {
                super.onSwipeTop();
                displayDialog();
            }
        });
    }

    private void displayDialog() {
        materialDialog = new MaterialDialog.Builder(getActivity())
                .title("Electric Sheep")
                .iconRes(R.drawable.energy_small)
                .content("Are you sure you want us to put you through Emergency Services?")
                .positiveText("OKAY")
                .positiveColor(colorAccent)
                .negativeColor(colorAccent)
                .negativeText("CANCEL")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        if (userObject!=null) {
                            materialDialog = new MaterialDialog.Builder(getActivity())
                                    .title("Electric Sheep")
                                    .iconRes(R.drawable.energy_small)
                                    .content(userObject.toString())
                                    .show();
                            onCall();
                        }
                    }
                }).show();
    }

    private void performHttpRequest(View rootView) {
        String requestUrl = getString(R.string.BASE_SERVER_URL)+"?uid="+firebaseAuth.getCurrentUser().getUid();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, requestUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                useData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void useData(JSONObject response) {
        try {
            userObject = response;
            userObject.put("sos_now", new Date().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onCall() {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},123);
        else startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:9663265931")));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 123:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) onCall();
                else Log.v("PERMISSION", "Call Permission Not Granted");
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        unbinder = ButterKnife.bind(this, rootView);
    }

    @Override
    public void onStart() {
        super.onStart();
        unbinder = ButterKnife.bind(this, rootView);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
