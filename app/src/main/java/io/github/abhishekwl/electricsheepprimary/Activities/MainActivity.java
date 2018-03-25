package io.github.abhishekwl.electricsheepprimary.Activities;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;

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

    @BindView(R.id.mainBottomNavigationView) BottomNavigationView bottomNavigationView;
    @BindView(R.id.mainFrameLayout) FrameLayout frameLayout;
    @BindColor(R.color.colorAccent) int colorAccent;

    private Fragment fragment;
    private int previousId;
    private PendingIntent pi;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        unbinder = ButterKnife.bind(MainActivity.this);
        firebaseAuth = FirebaseAuth.getInstance();
        initializeViews();
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
