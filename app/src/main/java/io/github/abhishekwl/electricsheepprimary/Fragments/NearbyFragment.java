package io.github.abhishekwl.electricsheepprimary.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.github.abhishekwl.electricsheepprimary.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NearbyFragment extends Fragment {


    private View rootView;
    private Unbinder unbinder;
    private MessageListener messageListener;

    @BindView(R.id.nearbyMessageEditText) TextInputEditText messageEditText;

    public NearbyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_nearby, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        processViews();
        return rootView;
    }

    private void processViews() {
        initializeMessageListener();
    }

    private void initializeMessageListener() {
        messageListener = new MessageListener(){
            @Override
            public void onFound(Message message) {
                super.onFound(message);
                Toast.makeText(getContext(), new String(message.getContent()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLost(Message message) {
                super.onLost(message);
                Toast.makeText(getContext(), new String(message.getContent()), Toast.LENGTH_SHORT).show();
            }
        };
    }

    @OnClick(R.id.nearbySendButton)
    public void onSendButtonPress() {
        Nearby.getMessagesClient(rootView.getContext()).publish(new Message(messageEditText.getText().toString().getBytes()));
    }

    @Override
    public void onStart() {
        super.onStart();
        Nearby.getMessagesClient(rootView.getContext()).subscribe(messageListener);
    }

    @Override
    public void onStop() {
        Nearby.getMessagesClient(rootView.getContext()).unsubscribe(messageListener);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
