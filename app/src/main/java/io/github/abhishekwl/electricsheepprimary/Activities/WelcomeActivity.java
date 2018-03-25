package io.github.abhishekwl.electricsheepprimary.Activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.github.abhishekwl.electricsheepprimary.R;

public class WelcomeActivity extends AppCompatActivity {

    private Unbinder unbinder;
    private Typeface futuraTypeface;
    private FirebaseAuth firebaseAuth;

    @BindView(R.id.welcomeAppLogoImageView) ImageView appLogoImageView;
    @BindView(R.id.welcomeTitleTextView) TextView welcomeTextView;
    @BindView(R.id.welcomeSignInButton) Button signInButton;
    @BindView(R.id.welcomeSignUpButton) Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSecondary));
        setContentView(R.layout.activity_welcome);

        unbinder = ButterKnife.bind(WelcomeActivity.this);
        initializeViews();
    }

    private void initializeViews() {
        Glide.with(getApplicationContext()).load(R.drawable.energy_white).into(appLogoImageView);
        futuraTypeface = Typeface.createFromAsset(getAssets(), "fonts/futura.ttf");
        firebaseAuth = FirebaseAuth.getInstance();

        welcomeTextView.setTypeface(futuraTypeface);
        signInButton.setTypeface(futuraTypeface);
        signUpButton.setTypeface(futuraTypeface);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, SignInActivity.class));
                finish();
            }
        });

        getPermissions();
    }

    private void getPermissions() {
        Dexter.withActivity(WelcomeActivity.this)
                .withPermissions(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.BLUETOOTH,
                        android.Manifest.permission.BLUETOOTH_ADMIN,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CALL_PHONE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        }).check();
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
