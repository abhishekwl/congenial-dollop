package io.github.abhishekwl.electricsheepprimary.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.github.abhishekwl.electricsheepprimary.R;

public class SignInActivity extends AppCompatActivity {

    @BindView(R.id.signInAppLogoImageView) ImageView appLogoImageView;
    @BindView(R.id.signInEmailEditText) AppCompatEditText emailEditText;
    @BindView(R.id.signInPasswordEditText) AppCompatEditText passwordEditText;
    @BindView(R.id.signInButton) FloatingActionButton signInButton;
    @BindView(R.id.signInTitleTextView) TextView appNameTextView;

    @BindColor(R.color.colorSecondary) int colorSecondary;

    private Unbinder unbinder;
    private Typeface futuraTypeface;
    private FirebaseAuth firebaseAuth;
    private MaterialDialog materialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        setContentView(R.layout.activity_sign_in);

        unbinder = ButterKnife.bind(SignInActivity.this);
        initializeViews();
    }

    private void initializeViews() {
        Glide.with(getApplicationContext()).load(R.drawable.energy_white).into(appLogoImageView);
        futuraTypeface = Typeface.createFromAsset(getAssets(), "fonts/futura.ttf");
        firebaseAuth = FirebaseAuth.getInstance();

        appNameTextView.setTypeface(futuraTypeface);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) notifyMessage("Empty Email Address or Password");
                else signInUser(email, password);
            }
        });
    }

    private void signInUser(String email, String password) {
        materialDialog = new MaterialDialog.Builder(SignInActivity.this)
                .title("Electric Sheep")
                .iconRes(R.drawable.energy_small)
                .content("Signing In...")
                .progress(true, 0)
                .show();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                materialDialog.dismiss();
                startActivity(new Intent(SignInActivity.this, MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                notifyMessage(e.getMessage());
            }
        });
    }

    private void notifyMessage(String message) {
        if (materialDialog.isShowing()) materialDialog.dismiss();

        Snackbar.make(signInButton, message, Snackbar.LENGTH_SHORT)
                .setActionTextColor(colorSecondary)
                .setAction("DISMISS", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {}
                }).show();
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
