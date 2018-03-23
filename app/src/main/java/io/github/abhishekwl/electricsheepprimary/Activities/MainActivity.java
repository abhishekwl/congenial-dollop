package io.github.abhishekwl.electricsheepprimary.Activities;

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

    @BindView(R.id.mainBottomNavigationView) BottomNavigationView bottomNavigationView;
    @BindView(R.id.mainFrameLayout) FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        unbinder = ButterKnife.bind(MainActivity.this);
        initializeViews();
    }

    private void initializeViews() {
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.mainFrameLayout, new HomeFragment()).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
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
                fragmentManager.beginTransaction().replace(R.id.mainFrameLayout, fragment).commit();
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
