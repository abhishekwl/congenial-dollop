package io.github.abhishekwl.electricsheepprimary.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.github.abhishekwl.electricsheepprimary.R;

public class FeedbackActivity extends AppCompatActivity {

    @BindView(R.id.feedbackContinueButton) Button continueButton;
    @BindView(R.id.feedbackRatingBar) AppCompatRatingBar ratingBar;
    @BindView(R.id.feedbackReviewEditText) TextInputEditText feedbackEditText;
    @BindView(R.id.feedbackTitleTextView) TextView titleTextView;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        setContentView(R.layout.activity_feedback);

        unbinder = ButterKnife.bind(this);
        processViews();
    }

    private void processViews() {
        Typeface futuraTypeface = Typeface.createFromAsset(getAssets(), "fonts/futura.ttf");

        titleTextView.setTypeface(futuraTypeface);
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    @OnClick(R.id.feedbackTitleTextView)
    public void onSkipButtonClick() {
        startActivity(new Intent(FeedbackActivity.this, WelcomeActivity.class));
        finish();
    }
}
