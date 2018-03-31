package io.github.abhishekwl.electricsheepprimary.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import br.com.goncalves.pugnotification.notification.PugNotification;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.github.abhishekwl.electricsheepprimary.Fragments.BotFragment;
import io.github.abhishekwl.electricsheepprimary.Fragments.HomeFragment;
import io.github.abhishekwl.electricsheepprimary.Fragments.NearbyFragment;
import io.github.abhishekwl.electricsheepprimary.Fragments.RadioFragment;
import io.github.abhishekwl.electricsheepprimary.Fragments.TravelFragment;
import io.github.abhishekwl.electricsheepprimary.R;

public class MainActivity extends AppCompatActivity {

    private Unbinder unbinder;
    private FragmentManager fragmentManager;
    private FirebaseAuth firebaseAuth;
    private int sosCount=0;

    @BindView(R.id.mainBottomNavigationView) BottomNavigationView bottomNavigationView;
    @BindView(R.id.mainFrameLayout) FrameLayout frameLayout;
    @BindColor(R.color.colorAccent) int colorAccent;

    private Fragment fragment;
    private int previousId;
    private PendingIntent pi;
    private Uri uri;
    private MaterialDialog materialDialog;
    private RequestQueue requestQueue;
    private JSONObject userObject;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location deviceLocation;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        unbinder = ButterKnife.bind(MainActivity.this);
        firebaseAuth = FirebaseAuth.getInstance();
        initializeViews();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        performHttpRequest();
        try {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
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
        } catch (Exception ex) {
            materialDialog = new MaterialDialog.Builder(MainActivity.this)
                    .title("Electric Sheep")
                    .iconRes(R.drawable.energy_grey)
                    .content("Please turn on your GPS")
                    .show();
            Log.v("MainActivity", ex.getMessage());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_VOLUME_DOWN && sosCount>=3) {
            displayDialog();
            sosCount=0;
        } else if (keyCode==KeyEvent.KEYCODE_VOLUME_DOWN) {
            sosCount++;
            Log.v("SOS_COUNT", Integer.toString(sosCount));
        }

        return super.onKeyDown(keyCode, event);
    }

    private void displayDialog() {
        materialDialog = new MaterialDialog.Builder(MainActivity.this)
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
                            materialDialog = new MaterialDialog.Builder(MainActivity.this)
                                    .title("Electric Sheep")
                                    .iconRes(R.drawable.energy_small)
                                    .content(userObject.toString())
                                    .show();
                            onCall();
                        }
                    }
                }).show();
    }


    private void performHttpRequest() {
        String requestUrl = getString(R.string.BASE_DOMAIN_URL)+getString(R.string.BASE_SERVER_URL)+"?uid="+firebaseAuth.getCurrentUser().getUid();
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
            if (deviceLocation!=null) {
                userObject.put("latitude", deviceLocation.getLatitude());
                userObject.put("longitude", deviceLocation.getLongitude());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void onCall() {
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE},123);
        else startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:9663265931")));
    }

    private void initializeViews() {
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.mainFrameLayout, new HomeFragment()).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId()==previousId) notifyUser();

                switch (item.getItemId()) {
                    case R.id.menuItemHome:
                        fragment = new HomeFragment();
                        break;
                    case R.id.menuItemRadio:
                        fragment = new RadioFragment();
                        break;
                    case R.id.menuItemNearby:
                        fragment = new NearbyFragment();
                        break;
                    case R.id.menuItemTravelPlan:
                        fragment = new TravelFragment();
                        break;
                    default:
                        fragment = new BotFragment();
                        break;
                }
                previousId = item.getItemId();
                fragmentManager.beginTransaction().replace(R.id.mainFrameLayout, fragment).commit();
                return true;
            }
        });

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        pi = PendingIntent.getActivity(this, 0, intent, 0);
        uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null) {
                    startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
                    finish();
                }
            }
        });
    }

    private void notifyUser() {

        PugNotification.with(getApplicationContext())
                .load()
                .identifier(1)
                .title("Electric Sheep")
                .message("Just a heads up. Your train to Mangalore departs in 10 minutes")
                .click(pi)
                .smallIcon(R.drawable.ic_near_me_black_24dp)
                .largeIcon(R.drawable.energy_small)
                .flags(Notification.DEFAULT_ALL)
                .sound(uri)
                .simple()
                .build();
    }


    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
