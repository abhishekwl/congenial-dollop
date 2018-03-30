package io.github.abhishekwl.electricsheepprimary.Fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import java.io.IOException;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Unbinder;
import io.github.abhishekwl.electricsheepprimary.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RadioFragment extends Fragment {

    private View rootView;
    private Unbinder unbinder;
    private Typeface futuraTypeface;
    private MediaPlayer mediaPlayer;

    @BindView(R.id.radioTowerImageView) ImageView radioImageView;
    @BindView(R.id.radioInternetRadioTextView) TextView internetRadioTextView;
    @BindView(R.id.radioPauseButton) Button pauseButton;
    @BindView(R.id.radioLoader) CircularFillableLoaders circularFillableLoaders;

    @BindColor(R.color.colorSecondary) int colorSecondary;

    public RadioFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_radio, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        processViews(rootView);
        return rootView;
    }

    private void processViews(View rootView) {
        Glide.with(rootView.getContext()).load(R.drawable.tower).into(radioImageView);
        futuraTypeface = Typeface.createFromAsset(rootView.getContext().getAssets(), "fonts/futura.ttf");

        internetRadioTextView.setTypeface(futuraTypeface);
        pauseButton.setTypeface(futuraTypeface);

        setupMediaPlayer();
    }

    private void setupMediaPlayer() {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.reset();
            //mediaPlayer.setDataSource("http://192.168.10.168:8000/stream1");
            mediaPlayer.setDataSource(getString(R.string.BASE_DOMAIN_URL)+":8080/Marshmello_ft_Khalid_-_Silence_Talkmuzik.mp3");
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.seekTo(0);
                }
            });
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.v("RADIO", e.getMessage());
        }
    }

    @OnClick(R.id.radioPauseButton)
    public void onPauseButtonClick() {
        toggleMediaPlayer();
    }

    private void toggleMediaPlayer() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            pauseButton.setText("Play");
        } else {
            mediaPlayer.start();
            pauseButton.setText("Pause");
        }
    }

    @OnClick(R.id.radioLoader)
    public void onLoaderClick() {
        toggleMediaPlayer();
    }

    @OnLongClick(R.id.radioLoader)
    public boolean onLoaderLongPress() {
        if (mediaPlayer.isPlaying()) mediaPlayer.stop();
        mediaPlayer.reset();
        Snackbar.make(circularFillableLoaders, "Stream Reset", Snackbar.LENGTH_SHORT).show();
        return true;
    }

    @OnClick(R.id.radioFmButton)
    public void onRadioButtonClick() {
        startApp(rootView.getContext(), "com.miui.fm");
    }

    public void startApp(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + packageName));
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) mediaPlayer.pause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mediaPlayer.isPlaying()) mediaPlayer.stop();
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
