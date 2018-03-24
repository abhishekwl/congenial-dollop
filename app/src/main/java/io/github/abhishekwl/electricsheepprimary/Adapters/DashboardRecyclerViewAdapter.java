package io.github.abhishekwl.electricsheepprimary.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

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

        holder.notificationDateTextView.setText(dashboardNotification.getCurrentDateAndTime().toString());
        holder.notificationContentTextView.setText(dashboardNotification.getNotificationContent());
    }

    @Override
    public int getItemCount() {
        return dashboardNotifications.size();
    }

    class DashboardNotificationViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.dashboardNotificationCardView) CardView notificationCardView;
        @BindView(R.id.dashboardNotificationDate) TextView notificationDateTextView;
        @BindView(R.id.dashboardNotificationContent) TextView notificationContentTextView;

        DashboardNotificationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
