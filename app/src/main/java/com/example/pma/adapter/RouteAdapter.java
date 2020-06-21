package com.example.pma.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pma.R;
import com.example.pma.RouteDetail;
import com.example.pma.model.Route;

import java.util.ArrayList;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.ViewHolder> {

    private ArrayList<Route> routesData;
    private Context context;

    public RouteAdapter(Context context, ArrayList<Route> routes) {
        this.routesData = routes;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.route_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RouteAdapter.ViewHolder holder, int position) {

        Route currentRoute = routesData.get(position);
        holder.bindTo(currentRoute);
    }

    @Override
    public int getItemCount() {
        return routesData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mIdText;
        private TextView mDistanceText;
        private TextView mCaloriesText;
        private ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);

            mCaloriesText = itemView.findViewById(R.id.route_calories);
            mDistanceText = itemView.findViewById(R.id.route_distance);
            mIdText = itemView.findViewById(R.id.route_id);
            imageView = itemView.findViewById(R.id.circle_img);

            itemView.setOnClickListener(this);
        }

        void bindTo(Route currentRoute) {
            mIdText.setText("Route #"+currentRoute.getId());
            if(currentRoute.getCalories() < 500) {
                mIdText.setTextColor(context.getResources().getColor(R.color.low));
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.low_circle));
            }
            else if(currentRoute.getCalories() < 1000) {
                mIdText.setTextColor(context.getResources().getColor(R.color.middle));
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.medium_circle));
            }
            else {
                mIdText.setTextColor(context.getResources().getColor(R.color.intesive));
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.intense_circle));
            }
            mDistanceText.setText("Distance: " + Math.round(currentRoute.getDistance()*100)/100.0 + " m" );
            mCaloriesText.setText("Calories: " +  Math.round(currentRoute.getCalories()*100)/100.0 + " cal");
        }

        @Override
        public void onClick(View v) {
            Route currentRoute = routesData.get(getAdapterPosition());
            Intent intent = new Intent(context, RouteDetail.class);
            intent.putExtra("route", currentRoute.getId());
            context.startActivity(intent);
        }
    }

}
