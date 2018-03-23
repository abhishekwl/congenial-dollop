package io.github.abhishekwl.electricsheepprimary.Activities;

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
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
