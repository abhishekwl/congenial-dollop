package io.github.abhishekwl.electricsheepprimary.Fragments;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.github.abhishekwl.electricsheepprimary.Models.Author;
import io.github.abhishekwl.electricsheepprimary.Models.Message;
import io.github.abhishekwl.electricsheepprimary.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BotFragment extends Fragment {

    private Unbinder unbinder;
    private View rootView;
    private Typeface futuraTypeface;
    private static final String BOT_ID = "abcde";
    private Author botAuthor;
    private Author user;
    private FirebaseAuth firebaseAuth;
    private MessagesListAdapter<Message> messagesListAdapter;

    @BindView(R.id.botTitleTextView) TextView botTitleTextView;
    @BindView(R.id.botMessagesList) MessagesList messagesList;
    @BindView(R.id.botMessageInput) MessageInput messageInput;
    private RequestQueue requestQueue;

    public BotFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bot, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        processViews(rootView);
        return rootView;
    }

    private void processViews(View rootView) {
        initializeViews(rootView);
    }

    private void initializeViews(View rootView) {
        firebaseAuth = FirebaseAuth.getInstance();
        requestQueue = Volley.newRequestQueue(rootView.getContext());
        futuraTypeface = Typeface.createFromAsset(rootView.getContext().getAssets(), "fonts/futura.ttf");

        setupBots();
    }

    private void setupBots() {
        botAuthor = new Author(BOT_ID, "Electric Sheep", "https://cps-static.rovicorp.com/3/JPG_500/MI0001/755/MI0001755052.jpg");
        user = new Author(firebaseAuth.getCurrentUser().getUid(), firebaseAuth.getCurrentUser().getEmail(), "https://img00.deviantart.net/e8a1/i/2015/015/e/5/warlock_by_abelvera-d8e1fja.jpg");
        messagesListAdapter = new MessagesListAdapter<Message>(firebaseAuth.getCurrentUser().getUid(), new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                Glide.with(rootView.getContext()).load(url).into(imageView);
            }
        });
        messagesList.setAdapter(messagesListAdapter);

        messageInput.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                messagesListAdapter.addToStart(new Message(firebaseAuth.getCurrentUser().getUid(), input.toString(), user, new Date()), true);
                performHttpRequest(input.toString());

                return true;
            }
        });
    }

    private void performHttpRequest(String message) {
        String requestUrl = getString(R.string.BASE_DOMAIN_URL)+getString(R.string.BASE_SERVER_URL)+"?uid="+firebaseAuth.getCurrentUser().getUid()+"&message="+message;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, requestUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String clientName = response.getString("clientName");
                    String from = response.getString("from");
                    String to = response.getString("to");
                    String departure = response.getString("departure");
                    String arrival = response.getString("arrival");
                    String pnr = response.getString("pnr");
                    String trainNumber = response.getString("trainNumber");
                    String ticketNumber = response.getString("ticketNumber");
                    int adults = response.getInt("adults");
                    int children = response.getInt("children");
                    int cost = response.getInt("cost");

                    String resp = "Hey "+clientName+"!\nYour travel plan is as follow:\n";
                    resp+="\nFrom "+from.toUpperCase()+" to "+to.toUpperCase();
                    resp+="\nDeparture is at "+departure+" and arrival is at "+arrival;
                    resp+="\nPNR: "+pnr+"\nTrain Number: "+trainNumber+"\nTicket Number: "+ticketNumber+"\n";
                    resp+="Adult(s): "+adults+"\nChildren: "+children;
                    resp+="\nGrand Total: "+cost;

                    messagesListAdapter.addToStart(new Message(firebaseAuth.getCurrentUser().getUid(), resp, botAuthor, new Date()), true);

                } catch (JSONException e) {
                    Log.v("EXCEPTION", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(jsonObjectRequest);

    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
