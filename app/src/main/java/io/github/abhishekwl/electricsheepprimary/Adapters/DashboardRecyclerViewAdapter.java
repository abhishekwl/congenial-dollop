package io.github.abhishekwl.electricsheepprimary.Adapters;

import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.abhishekwl.electricsheepprimary.Models.DashboardNotification;
import io.github.abhishekwl.electricsheepprimary.R;

/**
 * Created by ghost on 3/24/18.
 */

public class DashboardRecyclerViewAdapter extends RecyclerView.Adapter<DashboardRecyclerViewAdapter.DashboardNotificationViewHolder> {
    private ArrayList<DashboardNotification> dashboardNotifications;

    public DashboardRecyclerViewAdapter(ArrayList<DashboardNotification> dashboardNotifications) {
        this.dashboardNotifications = dashboardNotifications;
    }

    @NonNull
    @Override
    public DashboardRecyclerViewAdapter.DashboardNotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_notification, parent, false);
        return new DashboardNotificationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardRecyclerViewAdapter.DashboardNotificationViewHolder holder, int position) {
        DashboardNotification dashboardNotification = dashboardNotifications.get(position);

        String currentDate = dashboardNotification.getCurrentDateAndTime().toString();
        currentDate = currentDate.substring(0, currentDate.indexOf("GMT")).trim();
        holder.notificationDateTextView.setText(currentDate);
        holder.notificationContentTextView.setText(dashboardNotification.getNotificationContent());
        holder.notificationImageView.setColorFilter(holder.colorAccent, PorterDuff.Mode.SRC_IN);
    }

    @Override
    public int getItemCount() {
        return dashboardNotifications.size();
    }

    class DashboardNotificationViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.dashboardNotificationCardView) CardView notificationCardView;
        @BindView(R.id.dashboardNotificationDate) TextView notificationDateTextView;
        @BindView(R.id.dashboardNotificationContent) TextView notificationContentTextView;
        @BindView(R.id.dashboardNotificationImageView) ImageView notificationImageView;
        @BindColor(R.color.colorAccent) int colorAccent;

        DashboardNotificationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
