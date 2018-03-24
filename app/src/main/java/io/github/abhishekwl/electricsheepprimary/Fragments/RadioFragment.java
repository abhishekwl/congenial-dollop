package io.github.abhishekwl.electricsheepprimary.Fragments;


import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    @BindView(R.id.radioCatalogTextView) TextView catalogTextView;
    @BindView(R.id.radioInternetRadioTextView) TextView internetRadioTextView;
    @BindView(R.id.radioPauseButton) Button pauseButton;

    public RadioFragment() {
        // Required empty public constructor
    }

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
        catalogTextView.setTypeface(futuraTypeface);
        pauseButton.setTypeface(futuraTypeface);

        setupMediaPlayer();

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    pauseButton.setText("Play");
                } else {
                    mediaPlayer.start();
                    pauseButton.setText("Pause");
                }
            }
        });
    }

    private void setupMediaPlayer() {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.reset();
            //mediaPlayer.setDataSource("http://192.168.10.168:8000/stream1");
            mediaPlayer.setDataSource("http://192.168.10.158:8080/Marshmello_ft_Khalid_-_Silence_Talkmuzik.mp3");
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

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
