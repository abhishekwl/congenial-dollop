package io.github.abhishekwl.electricsheepprimary.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.github.abhishekwl.electricsheepprimary.R;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.splashIrctcImageView) ImageView appLogoImageView;
    @BindView(R.id.splashAppLogoImageView) ImageView appLogo2ImageView;
    @BindView(R.id.splashAppLogoTextView) TextView appNameTextView;

    private Unbinder unbinder;
    private static final int SPLASH_DELAY = 2500;
    private FirebaseAuth firebaseAuth;
    private Typeface futuraTypeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        setContentView(R.layout.activity_splash);

        unbinder = ButterKnife.bind(SplashActivity.this);
        initializeViews();
    }

    private void initializeViews() {
        Glide.with(getApplicationContext()).load(R.drawable.irctc).into(appLogoImageView);
        Glide.with(getApplicationContext()).load(R.drawable.energy_grey).into(appLogo2ImageView);
        futuraTypeface = Typeface.createFromAsset(getAssets(), "fonts/futura.ttf");

        appNameTextView.setTypeface(futuraTypeface);
        firebaseAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (firebaseAuth.getCurrentUser()==null) startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
                else startActivity(new Intent(SplashActivity.this, FeedbackActivity.class));
                finish();
            }
        }, SPLASH_DELAY);
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
