package io.github.abhishekwl.electricsheepprimary.Fragments;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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

import org.json.JSONObject;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.github.abhishekwl.electricsheepprimary.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TravelFragment extends Fragment {

    @BindView(R.id.travelTitleTextView) TextView titleTextView;
    @BindView(R.id.travelSubtitleTextView) TextView subtitleTextView;
    @BindView(R.id.travelBackgroundImageView) ImageView backgroundImageView;
    @BindView(R.id.travelFromTextView) TextView fromTextView;
    @BindView(R.id.travelToTextView) TextView toTextView;
    @BindView(R.id.travelDepartureTextView) TextView departureTextView;
    @BindView(R.id.travelArrivalTextView) TextView arrivalTextView;
    @BindView(R.id.travelPnrTitleTextView) TextView pnrTitleTextView;
    @BindView(R.id.travelPnrTextView) TextView pnrTextView;
    @BindView(R.id.travelTicketNumberTitleTextView) TextView ticketTitleTextView;
    @BindView(R.id.travelTicketNumberTextView) TextView ticketTextView;
    @BindView(R.id.travelCostTitleTextView) TextView costTitleTextView;
    @BindView(R.id.travelCostTextView) TextView costTextView;
    @BindView(R.id.travelAdultsImageView) ImageView adultImageView;
    @BindView(R.id.travelChildrenImageView) ImageView childImageView;
    @BindView(R.id.travelAdultsTextView) TextView adultTextView;
    @BindView(R.id.travelChildrenTextView) TextView childrenTextView;
    @BindView(R.id.travelTrainNumberTitleTextView) TextView trainNumberTitleTextView;
    @BindView(R.id.travelTrainNumberTextView) TextView trainNumberTextView;
    @BindView(R.id.travelQrCodeImageView) ImageView qrCodeImageView;
    @BindView(R.id.travelClassTextView) TextView classTextView;
    @BindView(R.id.travelSeatsTextView) TextView seatsTextView;
    @BindView(R.id.travelClassTitleTextView) TextView classTitleTextView;
    @BindView(R.id.travelSeatsTitleTextView) TextView seatsTitleTextView;
    @BindView(R.id.travelClientNameTitleTextView) TextView clientNameTitleTextView;
    @BindView(R.id.travelClientNameTextView) TextView clientNameTextView;
    @BindColor(R.color.colorAccent) int colorAccent;

    private View rootView;
    private Unbinder unbinder;
    private Typeface futuraTypeface;
    private FirebaseAuth firebaseAuth;
    private RequestQueue requestQueue;

    public TravelFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_travel, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        processViews(rootView);
        return rootView;
    }

    private void processViews(View rootView) {
        Glide.with(rootView.getContext()).load(R.drawable.train_one).into(backgroundImageView);
        Glide.with(rootView.getContext()).load(R.drawable.adult).into(adultImageView);
        Glide.with(rootView.getContext()).load(R.drawable.child).into(childImageView);
        futuraTypeface = Typeface.createFromAsset(rootView.getContext().getAssets(), "fonts/futura.ttf");

        titleTextView.setTypeface(futuraTypeface);
        departureTextView.setTypeface(futuraTypeface);
        arrivalTextView.setTypeface(futuraTypeface);
        pnrTitleTextView.setTypeface(futuraTypeface);
        ticketTitleTextView.setTypeface(futuraTypeface);
        costTitleTextView.setTypeface(futuraTypeface);
        adultTextView.setTypeface(futuraTypeface);
        childrenTextView.setTypeface(futuraTypeface);
        trainNumberTitleTextView.setTypeface(futuraTypeface);
        classTitleTextView.setTypeface(futuraTypeface);
        seatsTitleTextView.setTypeface(futuraTypeface);
        clientNameTitleTextView.setTypeface(futuraTypeface);

        requestQueue = Volley.newRequestQueue(rootView.getContext());
        firebaseAuth = FirebaseAuth.getInstance();
        performHttpRequest(rootView);
    }

    private void performHttpRequest(View rootView) {
        String requestUrl = getString(R.string.BASE_DOMAIN_URL)+getString(R.string.BASE_SERVER_URL)+"?uid="+firebaseAuth.getCurrentUser().getUid();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, requestUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                renderJsonResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void renderJsonResponse(JSONObject response) {
        try {
            String fromPlace = response.getString("from");
            String toPlace = response.getString("to");
            String departureTime = response.getString("departure");
            String arrivalTime = response.getString("arrival");
            int cost = response.getInt("cost");
            String pnrNumber = response.getString("pnr");
            String ticketNumber = response.getString("ticketNumber");
            String trainNumber = response.getString("trainNumber");
            int adults = response.getInt("adults");
            int children = response.getInt("children");
            String className = response.getString("class");
            String seats = response.getString("seats");
            String clientName = response.getString("clientName");

            seats = seats.replace("\"","");
            seats = seats.substring(1, seats.length()-1).trim().replace(",", ", ");

            fromTextView.setText(fromPlace);
            toTextView.setText(toPlace);
            departureTextView.setText(departureTime);
            arrivalTextView.setText(arrivalTime);
            costTextView.setText(Integer.toString(cost));
            pnrTextView.setText(pnrNumber);
            ticketTextView.setText(ticketNumber);
            trainNumberTextView.setText(trainNumber);
            adultTextView.setText("Adults: "+adults);
            childrenTextView.setText("Children: "+children);
            classTextView.setText(className);
            seatsTextView.setText(seats);
            clientNameTextView.setText(clientName);

            QRGEncoder qrgEncoder = new QRGEncoder(response.toString(), null, QRGContents.Type.TEXT, 256);
            Glide.with(rootView.getContext()).load(qrgEncoder.encodeAsBitmap()).into(qrCodeImageView);
        } catch (Exception e) {
            notifyMessage(e.getMessage());
        }
    }

    private void notifyMessage(String message) {
        Snackbar.make(qrCodeImageView, message, Snackbar.LENGTH_SHORT)
                .setActionTextColor(colorAccent)
                .setAction("DISMISS", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {}
                }).show();
    }

}
