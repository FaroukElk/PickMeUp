package com.example.farouk.pickmeup;

import com.google.android.gms.maps.model.Marker;

import java.io.Serializable;

/**
 * Created by Farouk on 4/23/2017.
 */

public class newEvent implements Serializable {
    private String sport, skill_level, location, date, time;
    private Marker marker;

    public newEvent(String sport, String skill_level, String location, String date, String time, Marker marker){
        this.sport = sport;
        this.skill_level = skill_level;
        this.location = location;
        this.date = date;
        this.time = time;
        this.marker = marker;
    }

    public String getSport(){
        return sport;
    }

    public String getSkill(){
        return skill_level;
    }

    public String getLocation(){
        return location;
    }


    public String getDate(){
        return date;
    }

    public String getTime(){
        return time;
    }

    public Marker getMarker(){
        return marker;
    }

    public void setMarker(Marker newMarker) {
        this.marker = newMarker;
    }
}
