package com.example.farouk.pickmeup;

import android.content.ClipData;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Farouk on 5/1/2017.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {

    private List<newEvent> eventList;
    private int selectedPos = RecyclerView.NO_POSITION;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView sport, skill_level, location, date, time;
        public ImageView sportIcon;

        public MyViewHolder(View view) {
            super(view);
            view.setClickable(true);
            sportIcon = (ImageView) view.findViewById(R.id.sportIcon);
            sport = (TextView) view.findViewById(R.id.sport);
            skill_level = (TextView) view.findViewById(R.id.skillLevel);
            location = (TextView) view.findViewById(R.id.location);
            date = (TextView) view.findViewById(R.id.date);
            time = (TextView) view.findViewById(R.id.time);
        }
    }

    public EventAdapter(List<newEvent> eventList) {
        this.eventList = eventList;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview, parent, false);
        return new MyViewHolder(itemView);
    }

    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if (selectedPos == position) {
            holder.itemView.setBackgroundColor(Color.BLUE);
            holder.sport.setTextColor(Color.WHITE);
            holder.skill_level.setTextColor(Color.WHITE);
            holder.location.setTextColor(Color.WHITE);
            holder.date.setTextColor(Color.WHITE);
            holder.time.setTextColor(Color.WHITE);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            holder.sport.setTextColor(Color.BLACK);
            holder.skill_level.setTextColor(Color.BLACK);
            holder.location.setTextColor(Color.BLACK);
            holder.date.setTextColor(Color.BLACK);
            holder.time.setTextColor(Color.BLACK);
        }
        final newEvent event = eventList.get(position);
        switch (setImage(event.getSport())) {
            case 1:
                holder.sportIcon.setImageResource(R.drawable.ic_soccer_ball);
                break;
            case 2:
                holder.sportIcon.setImageResource(R.drawable.ic_basketball);
                break;
            case 3:
                holder.sportIcon.setImageResource(R.drawable.ic_hockey);
                break;
            case 4:
                holder.sportIcon.setImageResource(R.drawable.ic_tennis);
                break;
            case 5:
                holder.sportIcon.setImageResource(R.drawable.ic_baseball);
                break;
            case 6:
                holder.sportIcon.setImageResource(R.drawable.ic_lacrosse);
                break;
            case 7:
                holder.sportIcon.setImageResource(R.drawable.ic_volleyball_game);
        }
        holder.sport.setText("Sport: " + event.getSport());
        holder.skill_level.setText("Skill Level: " + event.getSkill());
        holder.location.setText("Location: " + event.getLocation());
        holder.date.setText("Date: " + event.getDate());
        holder.time.setText("Time: " + event.getTime());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                notifyItemChanged(selectedPos);
                selectedPos = position;
                LatLng latLng = event.getMarker().getPosition();
                event.getMarker().showInfoWindow();
                MainActivity.mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                MainActivity.mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
                notifyItemChanged(selectedPos);
            }
        });
    }

    public int getItemCount() {
        return eventList.size();
    }

    public int setImage(String sport) {
        if (sport.equals("Soccer")) {
            return 1;
        } else if (sport.equals("Basketball")) {
            return 2;
        } else if (sport.equals("Hockey")) {
            return 3;
        } else if (sport.equals("Tennis")) {
            return 4;
        } else if (sport.equals("Baseball")) {
            return 5;
        } else if (sport.equals("Softball")) {
            return 5;
        } else if (sport.equals("Lacrosse")) {
            return 6;
        } else {
            return 7;
        }
    }
}